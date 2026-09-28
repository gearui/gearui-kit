import sys
import unittest
from collections import OrderedDict
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import component_spec as spec  # noqa: E402


def token(value, source=None):
    t = {"$value": {"value": value, "unit": "px"}}
    if source is not None:
        t["$extensions"] = {spec.EXT: source}
    return t


class ProvenanceRuleTest(unittest.TestCase):
    def test_basis_must_match_the_shipped_value(self):
        src = {"heroui": {"value": 48, "ref": "a"}, "basis": "heroui"}
        self.assertEqual([], spec.validate_source("x", 48, src))
        self.assertTrue(spec.validate_source("x", 40, src))

    def test_ios_basis_needs_an_ios_reference(self):
        self.assertTrue(spec.validate_source("x", 28, {"basis": "ios"}))

    def test_gearui_value_needs_a_why(self):
        self.assertTrue(spec.validate_source("x", 7, {"basis": "gearui"}))
        self.assertEqual([], spec.validate_source("x", 7, {"basis": "gearui", "why": "because"}))

    def test_disagreeing_references_need_a_why(self):
        src = {"heroui": {"value": 48, "ref": "a"}, "ios": {"value": 63, "ref": "b"}, "basis": "ios"}
        self.assertTrue(spec.validate_source("x", 63, src))
        src["why"] = "platform signature control"
        self.assertEqual([], spec.validate_source("x", 63, src))

    def test_references_need_value_and_ref(self):
        src = {"heroui": {"value": 48}, "basis": "heroui"}
        self.assertTrue(spec.validate_source("x", 48, src))

    def test_unknown_basis_is_rejected(self):
        self.assertTrue(spec.validate_source("x", 1, {"basis": "material"}))


class BaselineRatchetTest(unittest.TestCase):
    def setUp(self):
        self.tokens = OrderedDict([
            ("aSourced", token(1, {"heroui": {"value": 1, "ref": "r"}, "basis": "heroui"})),
            ("bPending", token(2)),
        ])

    def test_pending_token_in_baseline_passes(self):
        self.assertEqual([], spec.check(self.tokens, ["bPending"]))

    def test_new_token_without_provenance_fails(self):
        self.assertTrue(any("new token" in e for e in spec.check(self.tokens, [])))

    def test_sourced_token_must_leave_the_baseline(self):
        errors = spec.check(self.tokens, ["aSourced", "bPending"])
        self.assertTrue(any("remove it" in e for e in errors))

    def test_stale_baseline_entry_fails(self):
        errors = spec.check(self.tokens, ["bPending", "cGone"])
        self.assertTrue(any("no such token" in e for e in errors))


class RenderTest(unittest.TestCase):
    def test_table_groups_by_component_and_marks_pending(self):
        tokens = OrderedDict([
            ("switchWidth", token(63, {"heroui": {"value": 48, "ref": "h"},
                                       "ios": {"value": 63, "ref": "i"},
                                       "basis": "ios", "why": "signature"})),
            ("switchHeight", token(28)),
        ])
        text = spec.render(tokens, "en")
        self.assertIn("## switch", text)
        self.assertIn("| `switchWidth` | 63 | 48 | 63 | iOS | signature |", text)
        self.assertIn("_not yet sourced_", text)
        self.assertIn("1 of 2", text)


class RepositoryTest(unittest.TestCase):
    def test_repository_tokens_pass_the_check(self):
        self.assertEqual([], spec.check(spec.load_tokens(), spec.read_baseline()))


if __name__ == "__main__":
    unittest.main()
