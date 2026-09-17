import copy
import importlib.util
import json
import unittest
from pathlib import Path

SCRIPT = Path(__file__).resolve().parents[1] / "generate_tokens.py"
SPEC = importlib.util.spec_from_file_location("generate_tokens", SCRIPT)
generator = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(generator)


class TokenGenerationTest(unittest.TestCase):
    def setUp(self):
        self.document = json.loads(generator.SOURCE.read_text())

    def test_checked_in_output_matches(self):
        self.assertEqual(generator.generate(self.document), generator.OUTPUT.read_text())

    def test_control_reference_values(self):
        values = self.document["geometry"]
        for key, expected in {"controlMedium": 48, "radiusDefault": 14,
                              "buttonPaddingMedium": 16, "fieldPaddingMedium": 12}.items():
            self.assertEqual(expected, values[key]["$value"]["value"])

    def test_order_does_not_change_output(self):
        reordered = copy.deepcopy(self.document)
        reordered["geometry"] = dict(reversed(list(reordered["geometry"].items())))
        self.assertEqual(generator.generate(self.document), generator.generate(reordered))

    def test_source_change_invalidates_generated_output(self):
        self.document["geometry"]["controlMedium"]["$value"]["value"] = 45
        self.assertNotEqual(generator.generate(self.document), generator.OUTPUT.read_text())

    def test_small_fraction_is_not_written_as_scientific_notation(self):
        self.document["geometry"]["controlMedium"]["$value"]["value"] = 0.00001
        self.assertIn("val controlMedium = 0.00001.dp", generator.generate(self.document))

    def test_invalid_values_fail(self):
        for value in [True, -1, float("nan"), float("inf"), "44"]:
            with self.subTest(value=value):
                self.document["geometry"]["controlMedium"]["$value"]["value"] = value
                with self.assertRaises(ValueError):
                    generator.generate(self.document)

    def test_unsupported_unit_and_missing_alias_fail(self):
        for value in [{"value": 1, "unit": "em"}, "{geometry.missing}"]:
            with self.subTest(value=value):
                self.document["geometry"]["controlMedium"]["$value"] = value
                with self.assertRaises(ValueError):
                    generator.generate(self.document)

    def test_color_output_is_current(self):
        document = json.loads(generator.COLOR_SOURCE.read_text())
        self.assertEqual(generator.generate_colors(document), generator.COLOR_OUTPUT.read_text())

    def test_color_rejects_unsupported_space_and_channels(self):
        source = json.loads(generator.COLOR_SOURCE.read_text())
        for invalid in [{"colorSpace": "unknown", "components": [0, 0, 0]},
                        {"colorSpace": "srgb", "components": [0, 0]},
                        {"colorSpace": "srgb", "components": [0, 0, 2]},
                        {"colorSpace": "srgb", "components": [True, 0, 0]},
                        {"colorSpace": "srgb", "components": [0, 0, 0], "alpha": -1}]:
            with self.subTest(invalid=invalid):
                source["colors"]["lightBackground"]["$value"] = invalid
                with self.assertRaises(ValueError):
                    generator.generate_colors(source)

    def test_typography_output_is_current(self):
        document = json.loads(generator.TYPE_SOURCE.read_text())
        self.assertEqual(generator.generate_typography(document), generator.TYPE_OUTPUT.read_text())

    def test_typography_line_height_is_a_multiplier(self):
        document = json.loads(generator.TYPE_SOURCE.read_text())
        value = document["profiles"]["native"]["bodyMedium"]["$value"]
        value["fontSize"]["value"] = 20
        value["lineHeight"] = 1.5
        self.assertIn("bodyMedium = TextStyle(20.sp, 30.sp", generator.generate_typography(document))

    def test_typography_rejects_missing_alias_and_wrong_type(self):
        for token in [{"$value": "{profiles.native.missing}"},
                      {"$type": "dimension", "$value": {}}]:
            with self.subTest(token=token):
                document = json.loads(generator.TYPE_SOURCE.read_text())
                document["profiles"]["native"]["bodyMedium"] = token
                with self.assertRaises(ValueError):
                    generator.generate_typography(document)

    def test_alias_reaches_kotlin_generator(self):
        self.document['geometry']['controlMedium']['$value'] = '{geometry.controlSmall}'
        self.assertIn('controlMedium = 40.dp', generator.generate(self.document))

    def test_typography_alias_reaches_generator(self):
        document = json.loads(generator.TYPE_SOURCE.read_text())
        document['profiles']['native']['bodyMedium']['$value'] = '{profiles.native.bodySmall}'
        self.assertIn('bodyMedium = TextStyle', generator.generate_typography(document))

    def test_chained_alias_and_root_token(self):
        doc = {'base': {'$type': 'number', '$root': {'$value': 3}},
               'a': {'$type': 'number', '$value': '{base.$root}'},
               'b': {'$type': 'number', '$value': '{a}'}}
        self.assertEqual(3, generator.resolve_document(doc)['b']['$value'])

    def test_pointer_composite_and_array(self):
        doc = {'base': {'$type': 'color', '$value': {'colorSpace': 'srgb', 'components': [0.2, 0.3, 0.4]}},
               'n': {'$type': 'number', '$value': {'$ref': '#/base/$value/components/1'}}}
        self.assertEqual(0.3, generator.resolve_document(doc)['n']['$value'])

    def test_pointer_escaping(self):
        doc = {'a/b~c': {'$type': 'number', '$value': 7},
               'n': {'$type': 'number', '$value': {'$ref': '#/a~1b~0c/$value'}}}
        self.assertEqual(7, generator.resolve_document(doc)['n']['$value'])

    def test_cycles_fail_for_both_syntaxes(self):
        for value in ['{a}', {'$ref': '#/a/$value'}]:
            with self.subTest(value=value), self.assertRaisesRegex(ValueError, 'Circular'):
                generator.resolve_document({'a': {'$type': 'number', '$value': value}})

    def test_alias_type_mismatch_fails(self):
        doc = {'a': {'$type': 'number', '$value': 1},
               'b': {'$type': 'dimension', '$value': '{a}'}}
        with self.assertRaisesRegex(ValueError, 'type mismatch'):
            generator.resolve_document(doc)

    def test_group_alias_and_external_reference_fail(self):
        for value in ['{geometry}', {'$ref': 'other.json#/a'}]:
            self.document['geometry']['controlMedium']['$value'] = value
            with self.subTest(value=value), self.assertRaises(ValueError):
                generator.generate(self.document)

    def test_resolution_does_not_mutate_source(self):
        original = copy.deepcopy(self.document)
        generator.resolve_document(self.document)
        self.assertEqual(original, self.document)

    def test_oklch_neutrals_and_alpha(self):
        for lightness, expected in [(0, 0), (1, 1), (0.5, 0.388572859)]:
            actual = generator.color_channels({'colorSpace': 'oklch',
                'components': [lightness, 0, 0], 'alpha': 0.2}, 'neutral')
            for channel in actual[:3]:
                self.assertAlmostEqual(expected, channel, places=7)
            self.assertEqual(0.2, actual[3])

    def test_oklab_primary_reference_vectors(self):
        # Independent published sRGB primary coordinates, rounded to 10 digits.
        vectors = [([0.6279553606, 0.2248630611, 0.1258462985], [1, 0, 0]),
                   ([0.8664396115, -0.2338875742, 0.1794984799], [0, 1, 0]),
                   ([0.4520137184, -0.0324569842, -0.3115281477], [0, 0, 1])]
        for lab, rgb in vectors:
            actual = generator.color_channels({'colorSpace': 'oklab', 'components': lab}, 'primary')
            for expected, channel in zip(rgb, actual):
                self.assertAlmostEqual(expected, channel, places=5)

    def test_oklch_polar_conversion(self):
        lab = generator.color_channels({'colorSpace': 'oklab', 'components': [0.6, 0, 0.05]}, 'lab')
        lch = generator.color_channels({'colorSpace': 'oklch', 'components': [0.6, 0.05, 90]}, 'lch')
        self.assertEqual(lab, lch)

    def test_out_of_gamut_is_not_silently_clipped(self):
        with self.assertRaisesRegex(ValueError, 'outside sRGB gamut'):
            generator.color_channels({'colorSpace': 'oklch', 'components': [0.7, 0.4, 30]}, 'vivid')

    def test_invalid_oklch_fails(self):
        for components in [[-0.1, 0, 0], [0.5, -0.1, 0], [0.5, 0, 360],
                           [0.5, 0, True], [0.5, float('nan'), 0], [0.5, 0, 'none']]:
            with self.subTest(components=components), self.assertRaises(ValueError):
                generator.color_channels({'colorSpace': 'oklch', 'components': components}, 'invalid')

    def test_oklch_alias_reaches_kotlin(self):
        doc = {'primitives': {'neutral': {'$type': 'color', '$value': {
            'colorSpace': 'oklch', 'components': [0.5, 0, 0]}}},
            'colors': {'$type': 'color', 'lightSurface': {'$value': '{primitives.neutral}'}}}
        self.assertIn('Color(0.388572859f, 0.388572859f, 0.388572859f, 1f)',
                      generator.generate_colors(doc))

    def test_feedback_generation_and_invalid_input(self):
        doc = json.loads((generator.ROOT / 'tokens/feedback.tokens.json').read_text())
        self.assertIn('pressDuration = 300', generator.generate_feedback(doc))
        self.assertIn('selectIndicatorMass = 4f', generator.generate_feedback(doc))
        for name, value in [('pressDuration', {'value': -1, 'unit': 'ms'}),
                            ('pressScale', 2), ('pressEasing', [2, 0, 1, 1]),
                            ('referenceWidth', {'value': 0, 'unit': 'px'}),
                            ('selectIndicatorMass', 0), ('selectIndicatorStiffness', -1),
                            ('selectIndicatorDamping', float('inf')),
                            ('switchMass', 0), ('switchStiffness', -1),
                            ('sliderMass', 0), ('sliderStiffness', -1),
                            ('sliderDamping', float('inf')), ('sliderDragScale', 1.1),
                            ('accordionMass', 0), ('accordionLayoutStiffness', -1),
                            ('accordionFadeDuration', {'value': -1, 'unit': 'ms'}),
                            ('accordionEnterEasing', [2, 0, 1, 1]),
                            ('selectionPressScale', 1.2), ('switchEasing', [2, 0, 1, 1])]:
            bad = copy.deepcopy(doc)
            bad['feedback'][name]['$value'] = value
            with self.subTest(name=name), self.assertRaises(ValueError):
                generator.generate_feedback(bad)

    def test_reference_palette_semantic_mapping(self):
        doc = generator.resolve_document(json.loads(generator.COLOR_SOURCE.read_text()))
        self.assertEqual([0.6204, 0.195, 253.83], doc['colors']['lightPrimary']['$value']['components'])
        self.assertEqual(0, doc['reference']['dark']['field-border']['$value']['alpha'])
        self.assertEqual(doc['reference']['dark']['border']['$value'], doc['colors']['darkInputBorder']['$value'])
        self.assertEqual(doc['reference']['light']['surface']['$value'], doc['colors']['lightSurface']['$value'])


class CompositeTokenTest(unittest.TestCase):
    def setUp(self):
        self.document = json.loads(generator.MATERIAL_SOURCE.read_text())
        self.shadow = copy.deepcopy(self.document['materials']['darkOverlay']['$value'])
        self.transition = {'duration': {'value': .2, 'unit': 's'},
                           'delay': {'value': 25, 'unit': 'ms'},
                           'timingFunction': [0, -.2, 1, 1.2]}

    def test_generated_materials_are_current(self):
        self.assertEqual(generator.generate_materials(self.document), generator.MATERIAL_OUTPUT.read_text())

    def test_shadow_order_and_signed_offsets_are_preserved(self):
        resolved = generator.resolve_document(self.document)
        layers = resolved['materials']['lightOverlay']['$value']
        self.assertEqual([2, -6, 14], [x['offsetY']['value'] for x in layers])
        self.assertIn('-6.dp', generator.generate_materials(self.document))

    def test_inset_and_alpha_are_preserved(self):
        literal = generator.composite_literal('shadow', self.shadow, 'test')
        self.assertIn('0.2f', literal)
        self.assertIn('true)', literal)

    def test_spread_can_be_negative_and_inset_defaults_false(self):
        self.shadow['spread']['value'] = -2
        del self.shadow['inset']
        self.assertIn('-2.dp, false)', generator.composite_literal('shadow', self.shadow, 'test'))

    def test_shadow_array_alias_is_not_flattened(self):
        self.document['materials']['invalid'] = {'$type': 'shadow', '$value': ['{materials.lightSurface}']}
        with self.assertRaises(ValueError):
            generator.generate_materials(self.document)

    def test_single_shadow_alias_in_array(self):
        self.document['materials']['mixed'] = {'$type': 'shadow', '$value': ['{materials.darkOverlay}', self.shadow]}
        literal = generator.generate_materials(self.document).split('val mixed = ')[1].split('\n')[0]
        self.assertEqual(2, literal.count('TokenShadow('))

    def test_invalid_shadow_members_fail(self):
        for key, value in [('inset', 1), ('blur', {'value': -1, 'unit': 'px'}),
                           ('spread', {'value': float('nan'), 'unit': 'px'}),
                           ('offsetX', {'value': 1, 'unit': 'em'}), ('color', 'black')]:
            with self.subTest(key=key):
                bad = {**self.shadow, key: value}
                with self.assertRaises(ValueError):
                    generator.composite_literal('shadow', bad, 'test')

    def test_transition_converts_seconds_and_keeps_bezier_overshoot(self):
        literal = generator.composite_literal('transition', self.transition, 'test')
        self.assertIn('TokenTransition(200, 25,', literal)
        self.assertIn('-0.2f', literal)
        self.assertIn('1.2f', literal)

    def test_fractional_milliseconds_are_rejected_not_rounded(self):
        for value in [.0001, -1, True, float('inf')]:
            with self.subTest(value=value), self.assertRaises(ValueError):
                generator.duration_ms({'value': value, 'unit': 's'}, 'test')

    def test_transition_requires_all_properties(self):
        for key in self.transition:
            bad = {k: v for k, v in self.transition.items() if k != key}
            with self.subTest(key=key), self.assertRaises(ValueError):
                generator.composite_literal('transition', bad, 'test')

    def test_border_preserves_standard_styles(self):
        value = {'color': self.shadow['color'], 'width': {'value': .5, 'unit': 'px'}, 'style': 'solid'}
        self.assertIn('0.5.dp', generator.composite_literal('border', value, 'test'))
        for style in ['solid', 'dashed', 'dotted', 'double', 'groove', 'ridge', 'inset', 'outset']:
            self.assertIn('BorderLineStyle.' + style.title(), generator.composite_literal('border', {**value, 'style': style}, 'test'))
        self.assertIn('StrokeCap.Round', generator.composite_literal('border', {**value, 'style': {'dashArray': [], 'lineCap': 'round'}}, 'test'))
        for style in ['typo', {'dashArray': [{'value': -1, 'unit': 'px'}], 'lineCap': 'round'}]:
            with self.subTest(style=style), self.assertRaises(ValueError):
                generator.composite_literal('border', {**value, 'style': style}, 'test')

    def test_transition_source_changes_reach_feedback_output(self):
        doc = json.loads((generator.ROOT / 'tokens/feedback.tokens.json').read_text())
        doc['transitions']['accordionEnter']['$value']['delay'] = {'value': .075, 'unit': 's'}
        self.assertIn('TokenTransition(200, 75,', generator.generate_feedback(doc))

    def test_wrong_composite_alias_type_fails(self):
        self.document['materials']['invalid'] = {'$type': 'border', '$value': '{materials.darkOverlay}'}
        with self.assertRaises(ValueError):
            generator.generate_materials(self.document)


if __name__ == "__main__":
    unittest.main()
