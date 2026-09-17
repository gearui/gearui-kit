"""DTCG 2025.10 data validation and resolution, independent of renderers."""
import copy
import json
import math
import re
from urllib.parse import unquote

TYPES = frozenset(('color', 'dimension', 'fontFamily', 'fontWeight', 'duration',
                  'cubicBezier', 'number', 'strokeStyle', 'border', 'transition',
                  'shadow', 'gradient', 'typography'))
COLOR_SPACES = frozenset(('srgb', 'srgb-linear', 'hsl', 'hwb', 'lab', 'lch',
                         'oklab', 'oklch', 'display-p3', 'a98-rgb', 'prophoto-rgb',
                         'rec2020', 'xyz-d50', 'xyz-d65'))
WEIGHTS = {name: weight for weight, names in (
    (100, 'thin hairline'), (200, 'extra-light ultra-light'), (300, 'light'),
    (400, 'normal regular book'), (500, 'medium'), (600, 'semi-bold demi-bold'),
    (700, 'bold'), (800, 'extra-bold ultra-bold'), (900, 'black heavy'),
    (950, 'extra-black ultra-black')) for name in names.split()}
MEMBERS = {
    'border': {'color': 'color', 'width': 'dimension', 'style': 'strokeStyle'},
    'transition': {'duration': 'duration', 'delay': 'duration', 'timingFunction': 'cubicBezier'},
    'shadow': {'color': 'color', 'offsetX': 'dimension', 'offsetY': 'dimension',
               'blur': 'dimension', 'spread': 'dimension'},
    'typography': {'fontFamily': 'fontFamily', 'fontSize': 'dimension',
                   'fontWeight': 'fontWeight', 'letterSpacing': 'dimension', 'lineHeight': 'number'},
    'stop': {'color': 'color', 'position': 'number'},
}


def fail(path, message):
    raise ValueError(f'{path}: {message}')


def number(value, path):
    try:
        valid = type(value) in (int, float) and math.isfinite(value)
    except OverflowError:
        valid = False
    if not valid:
        fail(path, 'expected finite number')
    return value


def fields(value, required, optional, path):
    if not isinstance(value, dict) or not set(required) <= value.keys() or value.keys() - set(required) - set(optional):
        fail(path, 'invalid object properties')


def parse_json(text, source='JSON input'):
    def pairs(items):
        result = {}
        for key, value in items:
            if key in result:
                fail(source, f'duplicate JSON key {key}')
            result[key] = value
        return result
    def floating(value):
        result = float(value)
        number(result, source)
        return result
    return json.loads(text, object_pairs_hook=pairs, parse_float=floating,
                      parse_constant=lambda value: fail(source, f'invalid JSON number {value}'))


def load_json(path):
    return parse_json(path.read_text(encoding='utf-8'), str(path))


def pointer(ref):
    if not isinstance(ref, str) or not ref.startswith('#'):
        fail(ref, 'expected local JSON Pointer')
    if re.search(r'%(?![0-9a-fA-F]{2})', ref):
        fail(ref, 'invalid URI escape')
    fragment = unquote(ref[1:])
    if fragment == '':
        return ()
    if not fragment.startswith('/'):
        fail(ref, 'invalid JSON Pointer')
    parts = fragment[1:].split('/')
    if any(re.search(r'~(?![01])', part) for part in parts):
        fail(ref, 'invalid JSON Pointer escape')
    return tuple(part.replace('~1', '/').replace('~0', '~') for part in parts)


def at(document, path):
    node = document
    try:
        for part in path:
            if isinstance(node, list):
                if not re.fullmatch(r'0|[1-9][0-9]*', part):
                    fail(path, 'invalid array index')
                node = node[int(part)]
            else:
                node = node[part]
        return node
    except (KeyError, IndexError, TypeError) as exc:
        raise ValueError(f'{path}: unresolved reference') from exc


def alias(value):
    if isinstance(value, str) and re.fullmatch(r'\{[^{}]+\}', value):
        return tuple(value[1:-1].split('.'))
    return None


def merge(base, override):
    """Merge groups recursively, replacing tokens and metadata atomically."""
    if not isinstance(base, dict) or not isinstance(override, dict) or '$value' in base or '$value' in override:
        return copy.deepcopy(override)
    result = copy.deepcopy(base)
    for key, value in override.items():
        result[key] = merge(result[key], value) if key in result and not key.startswith('$') else copy.deepcopy(value)
    return result


def validate_value(kind, value, path='token'):
    if kind == 'number':
        number(value, path)
    elif kind in ('dimension', 'duration'):
        fields(value, ('value', 'unit'), (), path)
        number(value['value'], path)
        if value['unit'] not in (('px', 'rem') if kind == 'dimension' else ('ms', 's')):
            fail(path, f'invalid {kind} unit')
    elif kind == 'fontFamily':
        if not isinstance(value, str) and not (isinstance(value, list) and all(isinstance(x, str) for x in value)):
            fail(path, 'expected font name or ordered font names')
    elif kind == 'fontWeight':
        if isinstance(value, str):
            if value not in WEIGHTS:
                fail(path, 'unknown font weight')
        elif not 1 <= number(value, path) <= 1000:
            fail(path, 'font weight outside 1..1000')
    elif kind == 'cubicBezier':
        if not isinstance(value, list) or len(value) != 4:
            fail(path, 'expected four bezier coordinates')
        for item in value:
            number(item, path)
        if not all(0 <= value[i] <= 1 for i in (0, 2)):
            fail(path, 'bezier x outside 0..1')
    elif kind == 'color':
        fields(value, ('colorSpace', 'components'), ('alpha', 'hex'), path)
        space, components = value['colorSpace'], value['components']
        if not isinstance(space, str) or space not in COLOR_SPACES:
            fail(path, 'unknown color space')
        if not isinstance(components, list) or len(components) != 3:
            fail(path, 'expected three color components')
        bounds = [(0, 1)] * 3
        if space in ('hsl', 'hwb'):
            bounds = [(0, 360), (0, 100), (0, 100)]
        elif space in ('lab', 'oklab'):
            bounds = [(0, 100 if space == 'lab' else 1), (None, None), (None, None)]
        elif space in ('lch', 'oklch'):
            bounds = [(0, 100 if space == 'lch' else 1), (0, None), (0, 360)]
        for index, (component, (low, high)) in enumerate(zip(components, bounds)):
            if component == 'none':
                continue
            number(component, path)
            if (low is not None and component < low) or (high is not None and component > high):
                fail(path, 'color component outside range')
            if high == 360 and component == 360:
                fail(path, 'hue must be less than 360')
        if not 0 <= number(value.get('alpha', 1), path) <= 1:
            fail(path, 'alpha outside 0..1')
        if 'hex' in value and (not isinstance(value['hex'], str) or not re.fullmatch(r'#[0-9a-fA-F]{6}', value['hex'])):
            fail(path, 'invalid six-digit hex fallback')
    elif kind == 'strokeStyle':
        if isinstance(value, str):
            if value not in ('solid', 'dashed', 'dotted', 'double', 'groove', 'ridge', 'outset', 'inset'):
                fail(path, 'unknown stroke style')
        else:
            fields(value, ('dashArray', 'lineCap'), (), path)
            if not isinstance(value['dashArray'], list) or value['lineCap'] not in ('round', 'butt', 'square'):
                fail(path, 'invalid custom stroke')
            for item in value['dashArray']:
                validate_value('dimension', item, path)
    elif kind in ('shadow', 'gradient') and isinstance(value, list):
        for index, item in enumerate(value):
            # An alias to an array is not an implicit request to flatten it.
            if not isinstance(item, dict):
                fail(path, 'expected a single composite per array element')
            validate_value('stop' if kind == 'gradient' else 'shadow', item, f'{path}/{index}')
    elif kind in MEMBERS:
        fields(value, MEMBERS[kind], ('inset',) if kind == 'shadow' else (), path)
        for key, member_type in MEMBERS[kind].items():
            validate_value(member_type, value[key], f'{path}/{key}')
        if kind == 'shadow' and 'inset' in value and type(value['inset']) is not bool:
            fail(path, 'inset must be boolean')
    else:
        fail(path, f'invalid value for type {kind}')


def resolve_document(document):
    """Resolve a copy; source references and vendor extensions remain untouched."""
    if not isinstance(document, dict) or '$value' in document:
        fail('root', 'expected a token document')
    original = copy.deepcopy(document)
    cache = {}

    def lookup(path, stack):
        try:
            return at(original, path)
        except ValueError:
            # A path can exist only after its nearest declared ancestor extends
            # another group. Resolve that ancestor before rejecting the path.
            for length in range(len(path) - 1, 0, -1):
                try:
                    at(original, path[:length])
                except ValueError:
                    continue
                return at(expand(path[:length], stack), path[length:])
            fail(path, 'unresolved reference')

    def expand(path, stack=()):
        if path in stack:
            fail(path, 'Circular group extension')
        if path in cache:
            return copy.deepcopy(cache[path])
        node = lookup(path, stack)
        if not isinstance(node, dict):
            fail(path, 'expected group or token')
        if '$value' in node:
            return copy.deepcopy(node)
        node = copy.deepcopy(node)
        if any(key in node and node[key] is None for key in ('$extends', '$ref')):
            fail(path, 'group reference must not be null')
        ref = node.pop('$extends', None)
        pointer_ref = node.pop('$ref', None)
        if ref is not None and pointer_ref is not None:
            fail(path, 'use one group extension syntax')
        base = {}
        if ref is not None or pointer_ref is not None:
            if isinstance(ref, dict) and set(ref) == {'$ref'}:
                target = pointer(ref['$ref'])
            elif isinstance(ref, str) and ref.startswith('#'):
                target = pointer(ref)
            else:
                target = alias(ref) if ref is not None else pointer(pointer_ref)
            if target is None:
                fail(path, 'invalid group extension')
            base = expand(target, (*stack, path))
            if '$value' in base:
                if ref is not None:
                    fail(path, 'extension must target a group')
                # A token-level JSON reference aliases its value as well as its
                # metadata. Keeping the value reference preserves type inference.
                value_ref = '#/' + '/'.join(part.replace('~', '~0').replace('/', '~1') for part in (*target, '$value'))
                cache[path] = {**base, '$value': {'$ref': value_ref}, **node}
                return copy.deepcopy(cache[path])
        for name in list(node):
            if not name.startswith('$') or name == '$root':
                node[name] = expand((*path, name), (*stack, path))
        cache[path] = merge(base, node)
        return copy.deepcopy(cache[path])

    result = expand(())
    tokens, inherited_types = {}, {}

    def collect(node, path=(), inherited_type=None, deprecated=None):
        if not isinstance(node, dict):
            fail(path, 'expected group or token')
        is_token = '$value' in node
        allowed = {'$value', '$type', '$description', '$extensions', '$deprecated'} if is_token else {'$type', '$description', '$extensions', '$deprecated', '$root'}
        if any(key.startswith('$') and key not in allowed for key in node):
            fail(path, 'unknown reserved property')
        for key in ('$description', '$type'):
            if key in node and not isinstance(node[key], str):
                fail(path, f'{key} must be a string')
        if '$type' in node and node['$type'] not in TYPES:
            fail(path, 'unknown token type')
        if '$extensions' in node and not isinstance(node['$extensions'], dict):
            fail(path, '$extensions must be an object')
        if '$deprecated' in node and type(node['$deprecated']) not in (bool, str):
            fail(path, '$deprecated must be boolean or string')
        deprecated = node.get('$deprecated', deprecated)
        if is_token:
            if any(not key.startswith('$') for key in node):
                fail(path, 'token cannot have children')
            tokens[path], inherited_types[path] = node, inherited_type
            if '$deprecated' not in node and deprecated is not None:
                node['$deprecated'] = deprecated
            return
        for name, child in node.items():
            if not name.startswith('$') or name == '$root':
                if any(char in name for char in '.{}'):
                    fail(path, f'invalid name {name}')
                if name == '$root' and (not isinstance(child, dict) or '$value' not in child):
                    fail(path, '$root must be a token')
                collect(child, (*path, name), node.get('$type', inherited_type), deprecated)

    collect(result)
    types = {}

    def token_type(path, stack=()):
        if path in stack:
            fail(path, 'Circular type reference')
        if path not in tokens:
            fail(path, 'alias must target a token')
        if path in types:
            return types[path]
        node = tokens[path]
        target = alias(node['$value'])
        if isinstance(node['$value'], dict) and set(node['$value']) == {'$ref'}:
            ref_path = pointer(node['$value']['$ref'])
            if ref_path and ref_path[-1] == '$value' and ref_path[:-1] in tokens:
                target = ref_path[:-1]
        inferred = token_type(target, (*stack, path)) if target is not None else None
        kind = node.get('$type', inferred or inherited_types[path])
        if kind is None:
            fail(path, 'missing token type')
        if inferred is not None and kind != inferred:
            fail(path, 'alias type mismatch')
        types[path] = kind
        return kind

    def resolve(value, stack, expected=None):
        target = alias(value)
        if target is not None:
            kind = token_type(target)
            if expected is not None and kind != expected:
                fail(target, f'alias type mismatch: expected {expected}, got {kind}')
            return dereference((*target, '$value'), stack, expected)
        if isinstance(value, dict):
            if '$ref' in value:
                if set(value) != {'$ref'}:
                    fail(stack, 'Format value reference cannot have sibling properties')
                target = pointer(value['$ref'])
                if target and target[-1] == '$value' and target[:-1] in tokens:
                    if expected and token_type(target[:-1]) != expected:
                        fail(target, 'pointer type mismatch')
                return dereference(target, stack, expected)
            members = MEMBERS.get(expected, {})
            if expected in ('dimension', 'duration'):
                members = {'value': 'number'}
            elif expected == 'color':
                members = {'alpha': 'number'}
            elif expected == 'strokeStyle':
                members = {'dashArray': 'dimensions'}
            return {key: resolve(item, stack, members.get(key)) for key, item in value.items()}
        if isinstance(value, list):
            element_type = {'shadow': 'shadow', 'gradient': 'stop', 'cubicBezier': 'number',
                            'dimensions': 'dimension'}.get(expected)
            return [resolve(item, stack, element_type) for item in value]
        return value

    def dereference(path, stack, expected=None):
        if path in stack:
            fail(path, 'Circular value reference')
        return resolve(at(result, path), (*stack, path), expected)

    resolved = {}
    for path in tokens:
        kind = token_type(path)
        value = dereference((*path, '$value'), (), kind)
        validate_value(kind, value, '.'.join(path))
        if kind == 'gradient':
            for stop in value:
                stop['position'] = min(1, max(0, stop['position']))
        resolved[path] = (kind, value)
    for path, (kind, value) in resolved.items():
        tokens[path]['$type'], tokens[path]['$value'] = kind, value
    return result
