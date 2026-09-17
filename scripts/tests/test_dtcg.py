"""Normative data cases and renderer-policy cases must not be conflated."""
import copy
import itertools
import json
import os
import subprocess
import sys
import tempfile
import unittest
from unittest import mock
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from dtcg_format import COLOR_SPACES, TYPES, load_json, parse_json, resolve_document, validate_value
from dtcg_resolver import Resolver
from dtcg_color import to_srgb
import generate_tokens as generator

PX = {'value': 2, 'unit': 'px'}
RED = {'colorSpace': 'srgb', 'components': [1, 0, 0]}
SHADOW = dict(color=RED, offsetX=PX, offsetY=PX, blur=PX, spread=PX)


def token(kind, value, **metadata):
    return {'$type': kind, '$value': copy.deepcopy(value), **metadata}


class FormatTest(unittest.TestCase):
    def test_every_standard_type(self):
        values = {
            'color': RED, 'dimension': {'value': -.25, 'unit': 'rem'},
            'number': -.25, 'fontFamily': ['Inter', 'system-ui'], 'fontWeight': 'semi-bold',
            'duration': {'value': .0001, 'unit': 's'}, 'cubicBezier': [0, -1, 1, 2],
            'strokeStyle': {'dashArray': [PX], 'lineCap': 'round'},
            'border': {'color': RED, 'width': PX, 'style': 'dotted'},
            'transition': {'duration': {'value': 1, 'unit': 's'}, 'delay': {'value': -1, 'unit': 'ms'}, 'timingFunction': [0, 0, 1, 1]},
            'shadow': [SHADOW], 'gradient': [{'color': RED, 'position': .5}],
            'typography': {'fontFamily': ['Inter', 'sans-serif'], 'fontSize': PX,
                           'fontWeight': 350.5, 'letterSpacing': PX, 'lineHeight': 1.15},
        }
        self.assertEqual(TYPES, set(values))
        doc = {kind: token(kind, value) for kind, value in values.items()}
        self.assertEqual(doc, resolve_document(doc))

    def test_alias_type_inferred_before_group_type(self):
        doc = {'n': token('number', 2), 'group': {'$type': 'color', 'a': {'$value': '{n}'}}}
        result = resolve_document(doc)
        self.assertEqual('number', result['group']['a']['$type'])

    def test_alias_chain_without_explicit_types(self):
        doc = {'a': {'$value': '{b}'}, 'b': {'$value': '{c}'}, 'c': token('number', 2)}
        self.assertEqual(2, resolve_document(doc)['a']['$value'])

    def test_extension_nested_merge_and_atomic_token_override(self):
        doc = {'base': {'$type': 'number', '$description': 'base',
                        'a': token('number', 1, **{'$description': 'old'}),
                        'nested': {'b': {'$value': 2}}},
               'derived': {'$extends': '{base}', 'a': {'$value': 3},
                           'nested': {'c': {'$value': 4}}}}
        original = copy.deepcopy(doc)
        result = resolve_document(doc)['derived']
        self.assertEqual({'$value': 3, '$type': 'number'}, result['a'])
        self.assertEqual({'b', 'c'}, set(result['nested']))
        self.assertEqual('base', result['$description'])
        self.assertEqual(original, doc)

    def test_pointer_extension_and_root(self):
        doc = {'base': {'$root': token('number', 1)}, 'derived': {'$ref': '#/base'},
               'value': {'$value': '{derived.$root}'}}
        self.assertEqual(1, resolve_document(doc)['value']['$value'])

    def test_extension_chain(self):
        doc = {'base': {'n': token('number', 1)}, 'b': {'$extends': '{base}'}, 'c': {'$extends': '{b}'}}
        self.assertEqual(1, resolve_document(doc)['c']['n']['$value'])

    def test_extension_can_target_an_inherited_group(self):
        doc = {'base': {'nested': {'n': token('number', 3)}},
               'derived': {'$extends': '{base}'},
               'consumer': {'$extends': '{derived.nested}'}}
        for ordered in (doc, dict(reversed(list(doc.items())))):
            self.assertEqual(3, resolve_document(ordered)['consumer']['n']['$value'])

    def test_token_reference_can_target_an_inherited_token(self):
        doc = {'consumer': {'$ref': '#/derived/n'},
               'derived': {'$extends': '{base}'}, 'base': {'n': token('number', 7)}}
        self.assertEqual(token('number', 7), resolve_document(doc)['consumer'])

    def test_inherited_reference_cycles_still_fail(self):
        doc = {'base': {'nested': {'$extends': '{derived.nested}'}},
               'derived': {'$extends': '{base}'}}
        with self.assertRaisesRegex(ValueError, 'Circular'):
            resolve_document(doc)

    def test_missing_child_of_inherited_group_fails(self):
        doc = {'base': {'n': token('number', 7)}, 'derived': {'$extends': '{base}'},
               'consumer': {'$ref': '#/derived/missing'}}
        with self.assertRaisesRegex(ValueError, 'unresolved'):
            resolve_document(doc)

    def test_extension_cycles_and_invalid_targets(self):
        cases = [{'a': {'$extends': '{b}'}, 'b': {'$extends': '{a}'}},
                 {'a': {'b': {'$extends': '{a}'}}},
                 {'a': token('number', 1), 'b': {'$extends': '{a}'}},
                 {'a': {'$extends': '{missing}'}}]
        for doc in cases:
            with self.subTest(doc=doc), self.assertRaises(ValueError):
                resolve_document(doc)

    def test_metadata_preserved_and_deprecation_inherited(self):
        extension = {'org.example': {'$ref': 'not-a-reference', 'nested': [1, 2]}}
        doc = {'group': {'$deprecated': 'use newer', '$extensions': extension,
                          'a': token('number', 1), 'b': token('number', 2, **{'$deprecated': False})}}
        result = resolve_document(doc)['group']
        self.assertEqual(extension, result['$extensions'])
        self.assertEqual('use newer', result['a']['$deprecated'])
        self.assertIs(False, result['b']['$deprecated'])

    def test_invalid_metadata_names_and_structures(self):
        cases = [{'$surprise': {}}, {'a.b': token('number', 1)},
                 {'a': token('unknown', 1)}, {'a': {'$value': 1}},
                 {'a': token('number', 1, **{'$description': 3})},
                 {'a': token('number', 1, **{'$deprecated': 3})},
                 {'a': token('number', 1, **{'$extensions': []})},
                 {'a': token('number', 1, child={})}, {'a': {'$root': {}}}]
        cases += [{'a': {'$extends': None}}, {'a': {'$ref': None}}]
        for doc in cases:
            with self.subTest(doc=doc), self.assertRaises(ValueError):
                resolve_document(doc)

    def test_empty_groups_allowed(self):
        self.assertEqual({'a': {}}, resolve_document({'a': {}}))

    def test_composite_member_alias_type_not_just_shape(self):
        doc = {'weight': token('number', 400), 'text': token('typography', {
            'fontFamily': 'system-ui', 'fontWeight': '{weight}', 'fontSize': PX,
            'letterSpacing': PX, 'lineHeight': 1.2})}
        with self.assertRaisesRegex(ValueError, 'type mismatch'):
            resolve_document(doc)
        doc['weight']['$type'] = 'fontWeight'
        self.assertEqual(400, resolve_document(doc)['text']['$value']['fontWeight'])

    def test_pointer_array_and_escaped_names(self):
        doc = {'a/b~c': token('cubicBezier', [0, .4, 1, 1]),
               'n': token('number', {'$ref': '#/a~1b~0c/$value/1'})}
        self.assertEqual(.4, resolve_document(doc)['n']['$value'])
        for ref in ('#/a~2b', '#/a~1b~0c/$value/01', '#/a~1b~0c/$value/4'):
            doc['n']['$value']['$ref'] = ref
            with self.subTest(ref=ref), self.assertRaises(ValueError):
                resolve_document(doc)

    def test_pointer_cycle_through_composite(self):
        doc = {'a': token('dimension', {'value': {'$ref': '#/b/$value'}, 'unit': 'px'}),
               'b': token('number', {'$ref': '#/a/$value/value'})}
        with self.assertRaisesRegex(ValueError, 'Circular'):
            resolve_document(doc)

    def test_pointer_wrong_whole_token_type(self):
        doc = {'a': token('fontWeight', 400), 'b': token('number', {'$ref': '#/a/$value'})}
        with self.assertRaisesRegex(ValueError, 'type mismatch'):
            resolve_document(doc)

    def test_pointer_whole_token_infers_type(self):
        doc = {'a': token('number', 4), 'b': {'$value': {'$ref': '#/a/$value'}}}
        self.assertEqual(token('number', 4), resolve_document(doc)['b'])

    def test_token_level_json_reference(self):
        doc = {'base': {'$type': 'number', 'n': {'$value': 4, '$description': 'source'}},
               'a': {'$ref': '#/base/n'}, 'b': {'$ref': '#/a', '$description': 'alias'}}
        result = resolve_document(doc)
        self.assertEqual(token('number', 4, **{'$description': 'source'}), result['a'])
        self.assertEqual(token('number', 4, **{'$description': 'alias'}), result['b'])

    def test_token_level_reference_cycle(self):
        with self.assertRaisesRegex(ValueError, 'Circular'):
            resolve_document({'a': {'$ref': '#/b'}, 'b': {'$ref': '#/a'}})

    def test_gradient_clamps_positions_without_mutating_source(self):
        doc = {'a': token('gradient', [{'color': RED, 'position': -2}, {'color': RED, 'position': 42}])}
        self.assertEqual([0, 1], [stop['position'] for stop in resolve_document(doc)['a']['$value']])
        self.assertEqual(-2, doc['a']['$value'][0]['position'])

    def test_custom_dash_alias_type(self):
        doc = {'width': token('dimension', PX), 'stroke': token('strokeStyle', {'dashArray': ['{width}'], 'lineCap': 'butt'})}
        self.assertEqual([PX], resolve_document(doc)['stroke']['$value']['dashArray'])
        doc['width'] = token('number', 2)
        with self.assertRaises(ValueError):
            resolve_document(doc)

    def test_no_array_flattening(self):
        doc = {'a': token('shadow', [SHADOW]), 'b': token('shadow', ['{a}'])}
        with self.assertRaises(ValueError):
            resolve_document(doc)

    def test_strict_json_duplicate_and_nonfinite(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / 'tokens.json'
            for text in ('{"a":1,"a":2}', '{"a":NaN}', '{"a":Infinity}', '{"a":1e999}'):
                path.write_text(text)
                with self.subTest(text=text), self.assertRaises(ValueError):
                    load_json(path)

    def test_legal_values_need_not_fit_renderer(self):
        value = token('border', {'color': RED, 'width': {'value': -1, 'unit': 'px'}, 'style': 'dashed'})
        self.assertEqual(value, resolve_document({'a': value})['a'])
        with self.assertRaises(ValueError):
            generator.composite_literal('border', value['$value'], 'a')


class ColorTest(unittest.TestCase):
    def test_independent_csswg_conversion_vectors(self):
        fixture = load_json(Path(__file__).parent / 'fixtures/css-color4-vectors.json')
        self.assertEqual(20, len(fixture['vectors']))
        for vector in fixture['vectors']:
            with self.subTest(space=vector['space'], components=vector['components']):
                actual = to_srgb({'colorSpace': vector['space'], 'components': vector['components']})
                for expected, channel in zip(vector['srgb'], actual):
                    self.assertAlmostEqual(expected, channel, places=8)

    def test_all_spaces_and_missing_components_preserved(self):
        for space in COLOR_SPACES:
            value = {'colorSpace': space, 'components': ['none', 0, 0], 'alpha': .4, 'hex': '#000000'}
            with self.subTest(space=space):
                self.assertEqual(value, resolve_document({'a': token('color', value)})['a']['$value'])
                with self.assertRaisesRegex(ValueError, 'missing components'):
                    to_srgb(value)
                self.assertEqual([1, 0, 0, .4] if space == 'hwb' else [0, 0, 0, .4], to_srgb(value, missing='zero'))

    def test_all_spaces_black_vector(self):
        for space in COLOR_SPACES:
            with self.subTest(space=space):
                self.assertEqual([0, 0, 0, 1], to_srgb({'colorSpace': space, 'components': [0, 0, 100] if space == 'hwb' else [0, 0, 0]}))

    def test_reference_white_vectors(self):
        vectors = {'display-p3': [1, 1, 1], 'a98-rgb': [1, 1, 1],
                   'prophoto-rgb': [1, 1, 1], 'rec2020': [1, 1, 1],
                   'xyz-d50': [.3457/.3585, 1, (1-.3457-.3585)/.3585],
                   'lab': [100, 0, 0], 'lch': [100, 0, 0]}
        for space, components in vectors.items():
            with self.subTest(space=space):
                actual = to_srgb({'colorSpace': space, 'components': components})
                for channel in actual:
                    self.assertAlmostEqual(1, channel, places=6)

    def test_hue_and_white_black_mix(self):
        self.assertEqual([1, 0, 0, 1], to_srgb({'colorSpace': 'hsl', 'components': [0, 100, 50]}))
        self.assertEqual([.5, .5, .5, 1], to_srgb({'colorSpace': 'hwb', 'components': [120, 100, 100]}))

    def test_xyz_d65_grey_vector(self):
        # D65 full white has Z > 1, outside the DTCG 2025.10 input table.
        xyz = [.3127 / .3290 / 2, .5, (1 - .3127 - .3290) / .3290 / 2]
        actual = to_srgb({'colorSpace': 'xyz-d65', 'components': xyz})
        for channel in actual[:3]:
            self.assertAlmostEqual(.735356983, channel, places=7)

    def test_primary_conversion_vectors(self):
        # CSS Color 4 sRGB red represented in CIE D65 and D50 XYZ.
        for space, values in [('xyz-d65', [.4123907992659595, .21263900587151036, .01933081871559185]),
                              ('xyz-d50', [.436065742824811, .222493191756237, .013923904500943])]:
            actual = to_srgb({'colorSpace': space, 'components': values})
            for expected, channel in zip([1, 0, 0, 1], actual):
                self.assertAlmostEqual(expected, channel, places=5)

    def test_explicit_clipping(self):
        p3 = {'colorSpace': 'display-p3', 'components': [1, 0, 0], 'alpha': .7}
        with self.assertRaisesRegex(ValueError, 'outside sRGB gamut'):
            to_srgb(p3)
        self.assertEqual([1, 0, 0, .7], to_srgb(p3, gamut='clip'))
        self.assertEqual([1, 0, 0], p3['components'])

    def test_invalid_colors(self):
        for value in ({**RED, 'hex': '#000000ff'}, {**RED, 'alpha': 'none'},
                      {**RED, 'components': [True, 0, 0]}, {**RED, 'colorSpace': 'sRGB'}):
            with self.subTest(value=value), self.assertRaises(ValueError):
                validate_value('color', value)


class ResolverTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.doc = {'version': '2025.10', 'sets': {'base': {'sources': [
            {'primitive': token('number', 1), 'semantic': {'$value': '{primitive}'}}]}},
            'modifiers': {'brand': {'contexts': {
                'blue': [], 'red': [{'primitive': token('number', 2)}]}, 'default': 'blue'},
                'shape': {'contexts': {'round': [{'radius': token('dimension', PX)}],
                    'square': [{'radius': token('dimension', {'value': 0, 'unit': 'px'})}]}, 'default': 'round'}},
            'resolutionOrder': [{'$ref': '#/sets/base'}, {'$ref': '#/modifiers/brand'}, {'$ref': '#/modifiers/shape'}]}

    def build(self, inputs=None):
        return Resolver(self.doc, self.root).resolve(inputs)

    def test_contexts_orthogonal_and_aliases_after_merge(self):
        for brand, shape in itertools.product(('blue', 'red'), ('round', 'square')):
            result = self.build({'brand': brand, 'shape': shape})
            self.assertEqual(1 if brand == 'blue' else 2, result['semantic']['$value'])
            self.assertEqual(2 if shape == 'round' else 0, result['radius']['$value']['value'])

    def test_defaults_and_source_unchanged(self):
        original = copy.deepcopy(self.doc)
        self.assertEqual(1, self.build()['primitive']['$value'])
        self.assertEqual(original, self.doc)

    def test_unknown_and_missing_contexts(self):
        for inputs in ({'brand': 'typo'}, {'typo': 'blue'}, {'brand': 3}):
            with self.subTest(inputs=inputs), self.assertRaises(ValueError):
                self.build(inputs)
        del self.doc['modifiers']['brand']['default']
        with self.assertRaises(ValueError):
            self.build()

    def test_invalid_unselected_context_file_fails(self):
        self.doc['modifiers']['brand']['contexts']['red'] = [{'$ref': 'missing.json'}]
        with self.assertRaises(OSError):
            self.build({'brand': 'blue'})

    def test_all_context_validation_finds_unselected_bad_alias(self):
        self.doc['modifiers']['brand']['contexts']['red'] = [{'primitive': {'$value': '{missing}'}}]
        resolver = Resolver(self.doc, self.root)
        self.assertEqual(1, resolver.resolve()['primitive']['$value'])
        with self.assertRaisesRegex(ValueError, 'contexts.*red'):
            resolver.validate_all_contexts()

    def test_combination_limit_fails_instead_of_partial_validation(self):
        with self.assertRaisesRegex(ValueError, 'limit'):
            Resolver(self.doc, self.root).validate_all_contexts(limit=3)
        self.assertEqual(4, Resolver(self.doc, self.root).validate_all_contexts())

    def test_inline_set_modifier(self):
        self.doc['resolutionOrder'] = [
            {'name': 'base', 'type': 'set', 'sources': [{'$ref': '#/sets/base'}]},
            {'name': 'mode', 'type': 'modifier', 'contexts': {'light': [], 'dark': [{'primitive': token('number', 3)}]}, 'default': 'dark'}]
        self.assertEqual(3, self.build()['semantic']['$value'])

    def test_duplicate_order_names(self):
        self.doc['resolutionOrder'].append({'name': 'base', 'type': 'set', 'sources': []})
        with self.assertRaisesRegex(ValueError, 'duplicate'):
            self.build()

    def test_inline_requires_name_and_type(self):
        for entry in ({'sources': []}, {'name': 'a', 'sources': []}, {'type': 'set', 'sources': []}):
            self.doc['resolutionOrder'] = [entry]
            with self.subTest(entry=entry), self.assertRaises(ValueError):
                self.build()

    def test_no_reference_to_inline_or_modifier_context(self):
        for ref in ('#/resolutionOrder/0', '#/modifiers/brand', '#/modifiers/brand/contexts/blue'):
            self.doc['sets']['extra'] = {'sources': [{'$ref': ref}]}
            with self.subTest(ref=ref), self.assertRaises(ValueError):
                self.build()

    def test_set_cycle(self):
        self.doc['sets']['a'] = {'sources': [{'$ref': '#/sets/b'}]}
        self.doc['sets']['b'] = {'sources': [{'$ref': '#/sets/a'}]}
        with self.assertRaisesRegex(ValueError, 'Circular'):
            self.build()

    def test_local_files_and_nested_relative_refs(self):
        (self.root / 'nested').mkdir()
        (self.root / 'nested/token.json').write_text(json.dumps({'n': token('number', 7)}))
        (self.root / 'nested/set.json').write_text(json.dumps({'sources': [{'$ref': 'token.json'}]}))
        self.doc['sets']['base']['sources'] = [{'$ref': 'nested/set.json'}]
        self.assertEqual(7, self.build()['n']['$value'])

    def test_external_cycle(self):
        (self.root / 'a.json').write_text('{"sources":[{"$ref":"b.json"}]}')
        (self.root / 'b.json').write_text('{"sources":[{"$ref":"a.json"}]}')
        self.doc['sets']['base']['sources'] = [{'$ref': 'a.json'}]
        with self.assertRaisesRegex(ValueError, 'Circular'):
            self.build()

    def test_remote_and_path_escape_rejected(self):
        for ref in ('https://example.test/a.json', '../outside.json', '%2e%2e/outside.json'):
            self.doc['sets']['base']['sources'] = [{'$ref': ref}]
            with self.subTest(ref=ref), self.assertRaises(ValueError):
                self.build()

    def test_symlink_cannot_escape_local_root(self):
        with tempfile.TemporaryDirectory() as outside:
            path = Path(outside) / 'tokens.json'
            path.write_text('{}')
            (self.root / 'link.json').symlink_to(path)
            self.doc['sets']['base']['sources'] = [{'$ref': 'link.json'}]
            with self.assertRaisesRegex(ValueError, 'escapes'):
                self.build()

    def test_sources_is_legal_token_group_name(self):
        (self.root / 'tokens.json').write_text(json.dumps({'sources': {'a': token('number', 1)}}))
        self.doc['sets']['base']['sources'] = [{'$ref': 'tokens.json'}]
        self.assertEqual(1, self.build()['sources']['a']['$value'])

    def test_reference_siblings_override_shallowly(self):
        self.doc['resolutionOrder'][0]['sources'] = [{'primitive': token('number', 8)}]
        self.assertNotIn('semantic', self.build())
        self.assertEqual(8, self.build()['primitive']['$value'])

    def test_override_replaces_entire_token(self):
        self.doc['sets']['base']['sources'] = [{'a': token('number', 1, **{'$description': 'old'})}, {'a': token('number', 2)}]
        self.assertEqual(token('number', 2), self.build()['a'])

    def test_defs_are_accepted_and_metadata_not_interpreted(self):
        self.doc['$defs'] = {'vendor': {'$ref': 'unknown-protocol:ignored'}}
        self.doc['sets']['base']['$extensions'] = {'org.example': {'$ref': 'ignored'}}
        self.assertEqual(1, self.build()['semantic']['$value'])

    def test_real_manifest_reaches_all_generators(self):
        root = Path(__file__).resolve().parents[2]
        result = Resolver(load_json(root / 'tokens/gearui.resolver.json'), root / 'tokens').resolve()
        for generate, output in ((generator.generate, generator.OUTPUT),
                                 (generator.generate_colors, generator.COLOR_OUTPUT),
                                 (generator.generate_typography, generator.TYPE_OUTPUT),
                                 (generator.generate_materials, generator.MATERIAL_OUTPUT)):
            self.assertEqual(output.read_text(), generate(result))

    def test_shipped_orthogonal_theme_example(self):
        path = Path(__file__).resolve().parents[2] / 'tokens/examples/theme.resolver.json'
        resolver = Resolver(load_json(path), path.parent)
        self.assertEqual(4, resolver.validate_all_contexts())
        square = resolver.resolve({'shape': 'square'})
        rounded = resolver.resolve({'shape': 'rounded'})
        self.assertEqual(square['button']['fill'], rounded['button']['fill'])
        green = resolver.resolve({'brand': 'green'})
        self.assertEqual(rounded['button']['radius'], green['button']['radius'])


class OutputPublicationTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.first, self.second = self.root / 'one.kt', self.root / 'two.kt'
        self.first.write_text('old one')
        self.second.write_text('old two')

    def test_late_adapter_failure_leaves_every_output_unchanged(self):
        doc = Resolver(load_json(generator.ROOT / 'tokens/gearui.resolver.json'), generator.ROOT / 'tokens').resolve()
        doc['geometry']['controlMedium']['$value']['value'] = 49
        doc['materials']['unsupported'] = token('border', {'width': {'value': -1, 'unit': 'px'}, 'color': RED, 'style': 'dashed'})
        with self.assertRaises(ValueError):
            generator.publish_outputs(doc, [(self.first, generator.generate), (self.second, generator.generate_materials)])
        self.assertEqual('old one', self.first.read_text())
        self.assertEqual('old two', self.second.read_text())
        self.assertEqual(2, len(list(self.root.iterdir())))

    def test_check_reports_all_stale_outputs_without_writing(self):
        with self.assertRaisesRegex(ValueError, 'one.kt, two.kt'):
            generator.publish_outputs({}, [(self.first, lambda _: 'new'), (self.second, lambda _: 'new')], check=True)
        self.assertEqual('old one', self.first.read_text())
        self.assertEqual('old two', self.second.read_text())

    def test_unchanged_output_keeps_mtime(self):
        os.utime(self.first, ns=(1_000_000_000, 1_000_000_000))
        before = self.first.stat().st_mtime_ns
        generator.publish_outputs({}, [(self.first, lambda _: 'old one')])
        self.assertEqual(before, self.first.stat().st_mtime_ns)

    def test_success_preserves_permissions_and_cleans_staging(self):
        self.first.chmod(0o640)
        generator.publish_outputs({}, [(self.first, lambda _: 'new one'), (self.second, lambda _: 'new two')])
        self.assertEqual('new one', self.first.read_text())
        self.assertEqual('new two', self.second.read_text())
        self.assertEqual(0o640, self.first.stat().st_mode & 0o777)
        self.assertEqual(2, len(list(self.root.iterdir())))

    def test_staging_failure_cleans_temporary_files_without_replacing_outputs(self):
        real_mkstemp = tempfile.mkstemp
        calls = 0

        def stage(*args, **kwargs):
            nonlocal calls
            calls += 1
            if calls == 2:
                raise OSError('simulated full disk')
            return real_mkstemp(*args, **kwargs)

        with mock.patch.object(generator.tempfile, 'mkstemp', side_effect=stage):
            with self.assertRaisesRegex(OSError, 'full disk'):
                generator.publish_outputs({}, [(self.first, lambda _: 'new'), (self.second, lambda _: 'new')])
        self.assertEqual('old one', self.first.read_text())
        self.assertEqual('old two', self.second.read_text())
        self.assertEqual(2, len(list(self.root.iterdir())))

    def test_duplicate_output_is_rejected(self):
        with self.assertRaisesRegex(ValueError, 'Duplicate'):
            generator.publish_outputs({}, [(self.first, lambda _: 'one'), (self.first, lambda _: 'two')])
        self.assertEqual('old one', self.first.read_text())


class CommandLineTest(unittest.TestCase):
    def run_command(self, script, *arguments):
        return subprocess.run([sys.executable, str(generator.ROOT / 'scripts' / script), *arguments],
                              cwd=generator.ROOT, text=True, capture_output=True, timeout=20)

    def test_duplicate_context_input_is_not_silently_overridden(self):
        result = self.run_command('dtcg_resolver.py', 'tokens/examples/theme.resolver.json',
                                  '--input', '{"brand":"blue","brand":"green"}')
        self.assertNotEqual(0, result.returncode)
        self.assertIn('duplicate JSON key', result.stderr)
        self.assertNotIn('Traceback', result.stderr)

    def test_generator_rejects_duplicate_inputs_before_publishing(self):
        result = self.run_command('generate_tokens.py', '--check', '--input', '{"brand":"a","brand":"b"}')
        self.assertNotEqual(0, result.returncode)
        self.assertIn('duplicate JSON key', result.stderr)
        self.assertNotIn('Traceback', result.stderr)

    def test_invalid_root_font_has_actionable_error(self):
        for value in ('typo', 'NaN', 'Infinity', '0', '-2'):
            with self.subTest(value=value):
                result = self.run_command('generate_tokens.py', '--check', '--root-font-px', value)
                self.assertNotEqual(0, result.returncode)
                self.assertIn('root font', result.stderr)
                self.assertNotIn('Traceback', result.stderr)

    def test_resolver_export_is_valid_standard_data(self):
        result = self.run_command('dtcg_resolver.py', 'tokens/examples/theme.resolver.json',
                                  '--all-contexts', '--input', '{"brand":"green","shape":"square"}')
        self.assertEqual(0, result.returncode, result.stderr)
        exported = parse_json(result.stdout)
        self.assertEqual(exported, resolve_document(exported))
        self.assertEqual(0, exported['button']['radius']['$value']['value'])


class AdapterTest(unittest.TestCase):
    def test_typography_family_list_and_tracking_reach_kotlin(self):
        doc = load_json(generator.TYPE_SOURCE)
        value = doc['profiles']['native']['bodyMedium']['$value']
        value['fontFamily'] = ['Brand$Font', 'serif']
        value['letterSpacing'] = {'value': -.025, 'unit': 'rem'}
        result = generator.generate_typography(doc)
        self.assertIn(r'fontFamily = listOf("Brand\$Font", "serif")', result)
        self.assertIn('letterSpacing = -0.400.sp', result)

    def test_custom_border_preserves_odd_dash_list_and_caps(self):
        value = {'color': RED, 'width': PX, 'style': {'dashArray': [PX, PX, PX], 'lineCap': 'square'}}
        literal = generator.composite_literal('border', value, 'custom')
        self.assertIn('BorderLineStyle.Custom', literal)
        self.assertIn('StrokeCap.Square', literal)
        self.assertEqual(4, literal.count('2.dp'))

    def test_stroke_style_token_lowers_independently(self):
        self.assertEqual('SurfaceStroke(BorderLineStyle.Ridge)', generator.composite_literal('strokeStyle', 'ridge', 'ridge'))

    def test_gradient_preserves_order_alpha_and_hard_edges(self):
        stops = [{'color': {**RED, 'alpha': .25}, 'position': .3},
                 {'color': RED, 'position': .3}, {'color': RED, 'position': 1}]
        literal = generator.composite_literal('gradient', stops, 'gradient')
        self.assertEqual(3, literal.count('TokenGradientStop('))
        self.assertEqual(2, literal.count('TokenGradientStop(0.3f,'))
        self.assertIn('0.25f)', literal)

    def test_gradient_aliases_and_positions_reach_kotlin(self):
        doc = {'color': token('color', RED), 'end': token('number', 2),
               'materials': {'fade': token('gradient', [
                   {'color': '{color}', 'position': -1}, {'color': '{color}', 'position': '{end}'}])}}
        output = generator.generate_materials(doc)
        self.assertIn('TokenGradientStop(0f, Color(1f, 0f, 0f, 1f))', output)
        self.assertIn('TokenGradientStop(1f, Color(1f, 0f, 0f, 1f))', output)

    def test_gradient_rejects_invalid_stop(self):
        for stops in ([{'color': RED}], [{'color': RED, 'position': True}], [[{'color': RED, 'position': 0}]]):
            with self.subTest(stops=stops), self.assertRaises(ValueError):
                generator.composite_literal('gradient', stops, 'gradient')

    def test_picker_mask_source_is_used_by_generation(self):
        doc = load_json(generator.MATERIAL_SOURCE)
        original = generator.generate_materials(doc)
        doc['materials']['pickerTopMask']['$value'][0]['color']['alpha'] = .75
        changed = generator.generate_materials(doc)
        self.assertNotEqual(original, changed)
        self.assertIn('pickerTopMask = listOf<TokenGradientStop>(TokenGradientStop(0f, Color(1f, 1f, 1f, 0.75f))', changed)

    def test_rem_uses_explicit_build_root(self):
        self.assertEqual('24.0.dp', generator.dimension({'value': 1.5, 'unit': 'rem'}, 'test'))
        doc = load_json(generator.SOURCE)
        doc['geometry']['controlMedium']['$value'] = {'value': 3, 'unit': 'rem'}
        self.assertIn('controlMedium = 48.dp', generator.generate(doc))

    def test_fractional_line_height_not_rounded_to_whole_pixels(self):
        doc = load_json(generator.TYPE_SOURCE)
        value = doc['profiles']['native']['bodyMedium']['$value']
        value['fontSize']['value'], value['lineHeight'] = 17, 1.25
        self.assertIn('TextStyle(17.sp, 21.25.sp', generator.generate_typography(doc))

    def test_rem_typography_and_feedback_reach_output(self):
        doc = load_json(generator.TYPE_SOURCE)
        value = doc['profiles']['native']['bodyMedium']['$value']
        value['fontSize'] = {'value': 1.25, 'unit': 'rem'}
        self.assertIn('bodyMedium = TextStyle(20.00.sp', generator.generate_typography(doc))
        feedback = load_json(generator.ROOT / 'tokens/feedback.tokens.json')
        feedback['feedback']['referenceWidth']['$value'] = {'value': 2, 'unit': 'rem'}
        self.assertIn('referenceWidth = 32f', generator.generate_feedback(feedback))

    def test_named_and_non_hundred_font_weights(self):
        doc = load_json(generator.TYPE_SOURCE)
        value = doc['profiles']['native']['bodyMedium']['$value']
        for weight, expected in [('semi-bold', 600), (350, 350)]:
            value['fontWeight'] = weight
            self.assertIn(f'FontWeight({expected})', generator.generate_typography(doc))


if __name__ == '__main__':
    unittest.main()
