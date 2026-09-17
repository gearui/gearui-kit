"""Deterministic DTCG 2025.10 sets/modifiers resolver with local-file policy."""
import argparse
import copy
import json
import itertools
import math
from pathlib import Path
from urllib.parse import unquote, urlsplit

from dtcg_format import at, fail, load_json, parse_json, merge, pointer, resolve_document


class Resolver:
    """Case-sensitive contexts; references cannot escape the declared file root.

    Remote URLs are deliberately not fetched. Build inputs must be reproducible,
    local, and reviewed. Caller-owned documents are never mutated.
    """

    def __init__(self, document, base_dir, root_dir=None):
        self.document = copy.deepcopy(document)
        self.base_dir = Path(base_dir).resolve()
        self.root_dir = Path(root_dir or base_dir).resolve()
        self.files = {}
        self.entries = []
        self._validate_root()

    def _validate_root(self):
        doc = self.document
        if not isinstance(doc, dict) or doc.get('version') != '2025.10':
            fail('resolver', 'version must be 2025.10')
        allowed = {'version', 'name', 'description', 'sets', 'modifiers',
                   'resolutionOrder', '$schema', '$defs', '$extensions'}
        if doc.keys() - allowed:
            fail('resolver', 'unknown root property')
        for name in ('name', 'description', '$schema'):
            if name in doc and not isinstance(doc[name], str):
                fail(name, 'expected string')
        for name in ('sets', 'modifiers'):
            if not isinstance(doc.get(name, {}), dict):
                fail(name, 'expected object')
        if '$extensions' in doc and not isinstance(doc['$extensions'], dict):
            fail('resolver', '$extensions must be an object')
        if not isinstance(doc.get('resolutionOrder'), list):
            fail('resolutionOrder', 'expected array')
        # Validate even definitions/contexts that are not selected by this build.
        for plural, kind in (('sets', 'set'), ('modifiers', 'modifier')):
            for name, entry in doc.get(plural, {}).items():
                self._entry(entry, kind, self.document, self.base_dir, ((id(doc), plural, name),))
        names = set()
        for entry in doc['resolutionOrder']:
            if not isinstance(entry, dict):
                fail('resolutionOrder', 'expected object')
            if '$ref' in entry:
                ref = entry['$ref']
                path = pointer(ref)
                if len(path) != 2 or path[0] not in ('sets', 'modifiers'):
                    fail(ref, 'resolution order must reference a named set or modifier')
                name, kind = path[1], 'set' if path[0] == 'sets' else 'modifier'
                target = at(doc, path)
                if not isinstance(target, dict):
                    fail(ref, 'expected set or modifier')
                value = {**target, **{k: v for k, v in entry.items() if k != '$ref'}}
            else:
                name, kind = entry.get('name'), entry.get('type')
                if not isinstance(name, str) or kind not in ('set', 'modifier'):
                    fail('resolutionOrder', 'inline entry requires name and type')
                value = {k: v for k, v in entry.items() if k not in ('name', 'type')}
            if name in names:
                fail(name, 'duplicate resolution order name')
            names.add(name)
            self.entries.append((name, kind, self._entry(value, kind, doc, self.base_dir, ())))

    def _entry(self, value, kind, doc, base, stack):
        required = 'sources' if kind == 'set' else 'contexts'
        allowed = {required, 'description', '$extensions'} | ({'default'} if kind == 'modifier' else set())
        if not isinstance(value, dict) or required not in value or value.keys() - allowed:
            fail(kind, 'invalid properties')
        if 'description' in value and not isinstance(value['description'], str):
            fail(kind, 'description must be string')
        if '$extensions' in value and not isinstance(value['$extensions'], dict):
            fail(kind, '$extensions must be object')
        result = copy.deepcopy(value)
        if kind == 'set':
            result['sources'] = self._sources(value['sources'], doc, base, stack)
        else:
            contexts = value['contexts']
            if not isinstance(contexts, dict) or not contexts:
                fail(kind, 'contexts must be a nonempty object')
            if 'default' in value and (not isinstance(value['default'], str) or value['default'] not in contexts):
                fail(kind, 'default must name a context')
            result['contexts'] = {name: self._sources(sources, doc, base, stack)
                                  for name, sources in contexts.items()}
        return result

    def _sources(self, sources, doc, base, stack):
        if not isinstance(sources, list):
            fail('sources', 'expected array')
        result = []
        for source in sources:
            if not isinstance(source, dict):
                fail('sources', 'expected token document or reference')
            if '$ref' not in source:
                result.append(copy.deepcopy(source))
                continue
            ref = source['$ref']
            if not isinstance(ref, str):
                fail('source', 'reference must be a string')
            uri = urlsplit(ref)
            if uri.scheme or uri.netloc or uri.query:
                fail(ref, 'only local file references are enabled')
            target_doc, target_base = doc, base
            if uri.path:
                path = (base / unquote(uri.path)).resolve()
                if not path.is_relative_to(self.root_dir):
                    fail(ref, 'reference escapes token root')
                if path not in self.files:
                    self.files[path] = load_json(path)
                target_doc, target_base = self.files[path], path.parent
            path = pointer('#' + uri.fragment)
            if path and path[0] in ('modifiers', 'resolutionOrder'):
                fail(ref, 'sources cannot reference modifiers or inline entries')
            key = (id(target_doc), *path)
            if key in stack:
                fail(ref, 'Circular resolver reference')
            target = at(target_doc, path)
            if not isinstance(target, dict):
                fail(ref, 'source reference must resolve to an object')
            # Resolver reference siblings override whole properties, not deep merge.
            target = {**target, **{k: copy.deepcopy(v) for k, v in source.items() if k != '$ref'}}
            if isinstance(target.get('sources'), list):
                resolved = self._entry(target, 'set', target_doc, target_base, (*stack, key))
                result.extend(resolved['sources'])
            elif '$ref' in target:
                result.extend(self._sources([target], target_doc, target_base, (*stack, key)))
            elif (isinstance(target.get('contexts'), dict)
                  and target.keys() <= {'contexts', 'default', 'description', '$extensions'}
                  and all(isinstance(v, list) for v in target['contexts'].values())):
                fail(ref, 'modifier cannot be used as a source')
            else:
                result.append(copy.deepcopy(target))
        return result

    def resolve(self, inputs=None):
        inputs = {} if inputs is None else inputs
        if not isinstance(inputs, dict) or any(not isinstance(v, str) for v in inputs.values()):
            fail('inputs', 'expected an object of context strings')
        modifiers = {name for name, kind, _ in self.entries if kind == 'modifier'}
        if inputs.keys() - modifiers:
            fail('inputs', 'unknown modifier')
        result = {}
        for name, kind, value in self.entries:
            if kind == 'set':
                sources = value['sources']
            else:
                context = inputs.get(name, value.get('default'))
                if context not in value['contexts']:
                    fail(name, 'missing or unknown context')
                sources = value['contexts'][context]
            for source in sources:
                result = merge(result, source)
        return resolve_document(result)

    def validate_all_contexts(self, limit=256):
        """Validate final alias graphs, including combinations not built by default."""
        modifiers = [(name, tuple(value['contexts'])) for name, kind, value in self.entries if kind == 'modifier']
        count = math.prod(len(contexts) for _, contexts in modifiers)
        if count > limit:
            fail('contexts', f'{count} combinations exceed explicit validation limit {limit}')
        for choices in itertools.product(*(contexts for _, contexts in modifiers)):
            inputs = dict(zip((name for name, _ in modifiers), choices))
            try:
                self.resolve(inputs)
            except ValueError as exc:
                raise ValueError(f'contexts {inputs}: {exc}') from exc
        return count


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('manifest', type=Path)
    parser.add_argument('--input', default='{}', help='JSON object of modifier/context selections')
    parser.add_argument('--output', type=Path)
    parser.add_argument('--all-contexts', action='store_true', help='Validate every context combination before export')
    args = parser.parse_args()
    try:
        resolver = Resolver(load_json(args.manifest), args.manifest.resolve().parent)
        if args.all_contexts:
            resolver.validate_all_contexts()
        output = json.dumps(resolver.resolve(parse_json(args.input, '--input')), indent=2, ensure_ascii=False, allow_nan=False) + '\n'
        if args.output:
            args.output.write_text(output, encoding='utf-8')
        else:
            print(output, end='')
    except (ValueError, OSError) as exc:
        parser.exit(1, f'{exc}\n')


if __name__ == '__main__':
    main()
