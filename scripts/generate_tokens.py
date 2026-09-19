"""Compile GearUI's declared DTCG dimensions, colors and typography profile."""
import argparse
import json
import math
import re
import sys
import os
import tempfile
from decimal import Decimal
from pathlib import Path

# Support both direct CLI execution and importlib-based build tests.
sys.path.insert(0, str(Path(__file__).resolve().parent))
from dtcg_format import resolve_document, load_json, parse_json, validate_value, WEIGHTS
from dtcg_resolver import Resolver
from dtcg_color import to_srgb

ROOT = Path(__file__).resolve().parents[1]
ROOT_FONT_PX = Decimal('16')  # Explicit build-time rem policy, not device font scaling.
COLOR_GAMUT = 'error'
COLOR_MISSING = 'error'
SOURCE = ROOT / "tokens/controls.tokens.json"
OUTPUT = ROOT / "gearui-kit/src/commonMain/kotlin/com/gearui/foundation/control/ControlGeometry.kt"
COLOR_SOURCE = ROOT / "tokens/colors.tokens.json"
COLOR_OUTPUT = ROOT / "gearui-kit/src/commonMain/kotlin/com/gearui/theme/DefaultPalette.kt"
TYPE_SOURCE = ROOT / "tokens/typography.tokens.json"
TYPE_OUTPUT = ROOT / "gearui-kit/src/commonMain/kotlin/com/gearui/theme/TypographyProfiles.kt"
MATERIAL_SOURCE = ROOT / "tokens/materials.tokens.json"
MATERIAL_OUTPUT = ROOT / "gearui-kit/src/commonMain/kotlin/com/gearui/foundation/material/MaterialDefaults.kt"


def numeric(value, name):
    if type(value) not in (int, float) or not math.isfinite(value):
        raise ValueError(f'{name}: expected finite number')
    return Decimal(str(value))


def dimension(value, name, nonnegative=False):
    if not isinstance(value, dict) or set(value) != {'value', 'unit'} or value['unit'] not in ('px', 'rem'):
        raise ValueError(f'{name}: adapter requires px or rem dimensions')
    number = numeric(value['value'], name) * (ROOT_FONT_PX if value['unit'] == 'rem' else 1)
    if nonnegative and number < 0:
        raise ValueError(f'{name}: negative size')
    return format(number, 'f') + '.dp'


def duration_ms(value, name):
    if not isinstance(value, dict) or set(value) != {'value', 'unit'} or value['unit'] not in ('ms', 's'):
        raise ValueError(f'{name}: expected ms or s duration')
    number = numeric(value['value'], name) * (1000 if value['unit'] == 's' else 1)
    if number < 0 or number > 2147483647 or number != int(number):
        raise ValueError(f'{name}: adapter requires nonnegative Int milliseconds')
    return str(int(number))


def bezier(value, name):
    if not isinstance(value, list) or len(value) != 4:
        raise ValueError(f'{name}: expected four bezier coordinates')
    coords = [numeric(v, name) for v in value]
    if not all(0 <= coords[i] <= 1 for i in (0, 2)):
        raise ValueError(f'{name}: bezier x must be in 0..1')
    return 'CubicBezierEasing(' + ', '.join(format(v, 'f') + 'f' for v in coords) + ')'


def composite_literal(kind, value, name):
    """Lower resolved composites to data, not to a claim of renderer support."""
    def fields(obj, required, optional=()):
        if not isinstance(obj, dict) or not set(required) <= set(obj) or set(obj) - set(required) - set(optional):
            raise ValueError(f'{name}: invalid {kind} properties')

    def color(obj):
        if not isinstance(obj, dict):
            raise ValueError(f'{name}: expected color object')
        return 'Color(' + ', '.join(format(Decimal(str(v)), 'f') + 'f' for v in color_channels(obj, name)) + ')'

    if kind == 'gradient':
        validate_value(kind, value, name)
        stops = []
        for stop in value:
            position = min(Decimal(1), max(Decimal(0), numeric(stop['position'], name)))
            stops.append(f"TokenGradientStop({format(position, 'f')}f, {color(stop['color'])})")
        return 'listOf<TokenGradientStop>(' + ', '.join(stops) + ')'
    if kind == 'transition':
        fields(value, ('duration', 'delay', 'timingFunction'))
        return f"TokenTransition({duration_ms(value['duration'], name)}, {duration_ms(value['delay'], name)}, {bezier(value['timingFunction'], name)})"
    if kind == 'border':
        fields(value, ('color', 'width', 'style'))
        style = composite_literal('strokeStyle', value['style'], name)
        return f"TokenBorder({color(value['color'])}, {dimension(value['width'], name, True)}, {style})"
    if kind == 'strokeStyle':
        validate_value(kind, value, name)
        if isinstance(value, str):
            return f'SurfaceStroke(BorderLineStyle.{value.title()})'
        dash = ', '.join(dimension(item, name, True) for item in value['dashArray'])
        return f"SurfaceStroke(BorderLineStyle.Custom, listOf({dash}), StrokeCap.{value['lineCap'].title()})"
    if kind == 'shadow':
        layers = value if isinstance(value, list) else [value]
        result = []
        for layer in layers:
            fields(layer, ('color', 'offsetX', 'offsetY', 'blur', 'spread'), ('inset',))
            inset = layer.get('inset', False)
            if type(inset) is not bool:
                raise ValueError(f'{name}: inset must be boolean')
            dims = [dimension(layer[key], name, key == 'blur') for key in ('offsetX', 'offsetY', 'blur', 'spread')]
            result.append('TokenShadow(' + ', '.join([color(layer['color']), *dims, str(inset).lower()]) + ')')
        return 'listOf<TokenShadow>(' + ', '.join(result) + ')'
    raise ValueError(f'{name}: unsupported composite type {kind}')


def generate_materials(document):
    group = resolve_document(document)['materials']
    lines = ['// Generated by scripts/generate_tokens.py. Edit tokens/materials.tokens.json.',
             'package com.gearui.foundation.material', '',
             'import com.tencent.kuikly.compose.ui.graphics.Color',
             'import com.tencent.kuikly.compose.ui.unit.Dp',
             'import com.tencent.kuikly.compose.ui.unit.dp',
             'import com.tencent.kuikly.compose.animation.core.CubicBezierEasing', '',
             'import com.tencent.kuikly.compose.ui.graphics.StrokeCap', '',
             'internal typealias TokenShadow = SurfaceShadow',
             'internal typealias TokenBorder = SurfaceBorder',
             'internal data class TokenTransition(val durationMillis: Int, val delayMillis: Int, val easing: CubicBezierEasing)', '',
             'internal data class TokenGradientStop(val position: Float, val color: Color)', '',
             'internal object MaterialDefaults {']
    for name, token in sorted(group.items()):
        if name.startswith('$'):
            continue
        if not re.fullmatch(r'[a-z][a-zA-Z0-9]*', name) or name in {'class', 'when', 'object', 'val', 'fun', 'is', 'in', 'as', 'if', 'else', 'return', 'true', 'false', 'null', 'super', 'this', 'throw', 'try', 'while', 'for', 'do', 'break', 'continue', 'package', 'interface', 'typeof', 'typealias'}:
            raise ValueError(f'Invalid material name: {name}')
        lines.append(f"    val {name} = {composite_literal(token['$type'], token['$value'], name)}")
    return '\n'.join([*lines, '}', ''])


def generate_typography(document):
    document = resolve_document(document)
    profiles = document["profiles"]
    if set(profiles) != {"native", "web"}:
        raise ValueError("Expected native and web typography profiles")
    lines = [
        "// Generated by scripts/generate_tokens.py. Edit tokens/typography.tokens.json.",
        "package com.gearui.theme", "",
        "import com.gearui.foundation.typography.TextStyle",
        "import com.tencent.kuikly.compose.ui.text.font.FontWeight",
        "import com.tencent.kuikly.compose.ui.unit.sp", "",
        "internal object TypographyProfiles {",
    ]
    for platform, group in sorted(profiles.items()):
        if group.get("$type") != "typography":
            raise ValueError("Expected typography type")
        lines.append(f"    val {platform.title()} = Typography(")
        for name, token in sorted(group.items()):
            if name.startswith("$"):
                continue
            if not re.fullmatch(r"[a-z]+(?:[A-Z][a-zA-Z0-9]*)*", name) or name in {"class", "when", "object", "val", "fun"}:
                raise ValueError(f"Invalid typography role: {name}")
            value = token["$value"]
            if token.get("$type", "typography") != "typography" or not isinstance(value, dict):
                raise ValueError(f"{name}: expected literal typography")
            font_size = value["fontSize"]
            spacing = value["letterSpacing"]
            families = value['fontFamily']
            families = [families] if isinstance(families, str) else families
            if any(not family.strip() for family in families):
                raise ValueError(f'{name}: empty font family cannot be rendered')
            spacing_value = numeric(spacing['value'], name) * (ROOT_FONT_PX if spacing['unit'] == 'rem' else 1)
            size = numeric(font_size['value'], name) * (ROOT_FONT_PX if font_size['unit'] == 'rem' else 1)
            height, weight = value["lineHeight"], value["fontWeight"]
            if size <= 0 or numeric(height, name) <= 0:
                raise ValueError(f"{name}: invalid type dimensions")
            weight = WEIGHTS.get(weight, weight) if isinstance(weight, str) else weight
            if type(weight) is not int or not 1 <= weight <= 1000:
                raise ValueError(f"{name}: unsupported weight")
            # DTCG expresses lineHeight as a multiplier; Kotlin expects a size.
            line_height = format((Decimal(str(size)) * Decimal(str(height))).quantize(Decimal('0.000001')).normalize(), 'f')
            size_literal = format(Decimal(str(size)), "f")
            extra = ''
            if families != ['system-ui'] or spacing_value != 0:
                # Kotlin string literals also interpolate dollars, unlike JSON.
                quoted = ', '.join(json.dumps(f, ensure_ascii=False).replace('$', r'\$') for f in families)
                extra = f", fontFamily = listOf({quoted}), letterSpacing = {format(spacing_value, 'f')}.sp"
            lines.append(f"        {name} = TextStyle({size_literal}.sp, {line_height}.sp, FontWeight({weight}){extra}),")
        lines.extend(["    )", ""])
    return "\n".join([*lines, "}", ""])


def color_channels(value, name):
    return to_srgb(value, name, gamut=COLOR_GAMUT, missing=COLOR_MISSING)


def generate_colors(document):
    document = resolve_document(document)
    group = document["colors"]
    if group.get("$type") != "color":
        raise ValueError("colors must declare color")
    lines = [
        "// Generated by scripts/generate_tokens.py. Edit tokens/colors.tokens.json.",
        "package com.gearui.theme",
        "",
        "import com.tencent.kuikly.compose.ui.graphics.Color",
        "",
        "internal object DefaultPalette {",
    ]
    for name, token in sorted(group.items()):
        if name.startswith("$"):
            continue
        if not re.fullmatch(r"[a-z]+(?:[A-Z][a-zA-Z0-9]*)+", name):
            raise ValueError(f"Invalid Kotlin color name: {name}")
        value = token["$value"]
        if token.get("$type", "color") != "color" or not isinstance(value, dict):
            raise ValueError(f"{name}: expected a literal color")
        channels = color_channels(value, name)
        literals = [format(Decimal(str(x)), "f") + "f" for x in channels]
        lines.append(f"    val {name} = Color({', '.join(literals)})")
    return "\n".join([*lines, "}", ""])


def generate(document):
    document = resolve_document(document)
    group = document["geometry"]
    if group.get("$type") != "dimension":
        raise ValueError("geometry must declare dimension")
    lines = [
        "// Generated by scripts/generate_tokens.py. Edit tokens/controls.tokens.json.",
        "package com.gearui.foundation.control",
        "",
        "import com.tencent.kuikly.compose.ui.unit.dp",
        "",
        "internal object ControlGeometry {",
    ]
    for name, token in sorted(group.items()):
        if name.startswith("$"):
            continue
        if not re.fullmatch(r"[a-z]+(?:[A-Z][a-zA-Z0-9]*)+", name):
            raise ValueError(f"Invalid Kotlin token name: {name}")
        if token.get("$type", "dimension") != "dimension":
            raise ValueError(f"{name}: unsupported type")
        value = token["$value"]
        lines.append(f"    val {name} = {dimension(value, name, True)}")
    return "\n".join([*lines, "}", ""])


def generate_feedback(document):
    group = resolve_document(document)['feedback']
    expected = {'pressDuration': 'duration', 'highlightDuration': 'duration',
                'pressScale': 'number', 'referenceWidth': 'dimension',
                'highlightOpacity': 'number', 'disabledOpacity': 'number', 'pressEasing': 'cubicBezier',
                'neutralMix': 'number', 'accentMix': 'number', 'transparentHighlightOpacity': 'number',
                'selectIndicatorDamping': 'number', 'selectIndicatorStiffness': 'number',
                'selectIndicatorMass': 'number', 'selectIndicatorRotation': 'number',
                'selectForegroundMix': 'number',
                'tagSoftOpacity': 'number', 'tagAccentForegroundMix': 'number',
                'tagSuccessForegroundMix': 'number', 'tagWarningForegroundMix': 'number',
                'selectionPressScale': 'number', 'selectionPressDuration': 'duration',
                'selectionRevealDuration': 'duration', 'selectionRevealScale': 'number',
                'radioExitScale': 'number', 'radioRevealDuration': 'duration',
                'switchColorDuration': 'duration', 'switchStiffness': 'number',
                'switchDamping': 'number', 'switchMass': 'number', 'switchEasing': 'cubicBezier',
                'sliderDragScale': 'number', 'sliderStiffness': 'number',
                'sliderDamping': 'number', 'sliderMass': 'number',
                'accordionIndicatorStiffness': 'number', 'accordionIndicatorDamping': 'number',
                'accordionMass': 'number', 'accordionLayoutStiffness': 'number',
                'accordionLayoutDamping': 'number', 'accordionRotation': 'number',
                'accordionFadeDuration': 'duration', 'accordionEnterEasing': 'cubicBezier',
                'accordionExitEasing': 'cubicBezier',
                'accordionEnterTransition': 'transition', 'accordionExitTransition': 'transition',
                'menuItemPressScale': 'number', 'menuItemPressDuration': 'duration',
                'menuItemDangerPressOpacity': 'number',
                'overlayEnterDuration': 'duration', 'overlayExitDuration': 'duration',
                'dialogEnterScale': 'number', 'anchoredEnterScale': 'number',
                'anchoredEnterTranslate': 'dimension',
                'overlayEaseOut': 'cubicBezier', 'overlayEaseIn': 'cubicBezier'}
    if set(group) != set(expected):
        raise ValueError('Unexpected feedback token roles')
    lines = ['// Generated by scripts/generate_tokens.py. Edit tokens/feedback.tokens.json.',
             'package com.gearui.foundation.motion', '',
             'import com.tencent.kuikly.compose.animation.core.CubicBezierEasing',
             'import com.gearui.foundation.material.TokenTransition', '',
             'internal object FeedbackDefaults {']
    for name, kind in expected.items():
        token = group[name]
        if token['$type'] != kind:
            raise ValueError(f'{name}: incorrect feedback type')
        value = token['$value']
        if kind == 'transition':
            literal = composite_literal(kind, value, name)
        elif kind == 'cubicBezier':
            literal = bezier(value, name)
        elif kind == 'duration':
            literal = duration_ms(value, name)
        else:
            if kind == 'dimension':
                value = float(numeric(value['value'], name) * (ROOT_FONT_PX if value['unit'] == 'rem' else 1))
                if value.is_integer():
                    value = int(value)
            if type(value) not in (int, float) or not math.isfinite(value) or value < 0:
                raise ValueError(f'{name}: invalid value')
            spring_role = name in {'selectIndicatorDamping', 'selectIndicatorStiffness',
                                  'selectIndicatorMass', 'selectIndicatorRotation',
                                  'switchStiffness', 'switchDamping', 'switchMass', 'radioExitScale',
                                  'sliderStiffness', 'sliderDamping', 'sliderMass',
                                  'accordionIndicatorStiffness', 'accordionIndicatorDamping',
                                  'accordionMass', 'accordionLayoutStiffness',
                                  'accordionLayoutDamping', 'accordionRotation'}
            if (kind == 'number' and not spring_role and value > 1
                    or (name == 'referenceWidth' or spring_role) and value == 0
                    or spring_role and value > 1000000):
                raise ValueError(f'{name}: out of range')
            if kind == 'duration' and (int(value) != value or value > 2147483647):
                raise ValueError(f'{name}: expected Int milliseconds')
            literal = str(int(value)) if kind == 'duration' else f'{value}f'
        lines.append(f'    val {name} = {literal}')
    return '\n'.join([*lines, '}', ''])


def publish_outputs(document, jobs, check=False):
    """Validate every conversion before touching any destination.

    Stage complete UTF-8 files before replacing them. Each replacement is atomic;
    this is not a cross-file filesystem transaction on power loss or I/O failure.
    """
    compiled = [(output, compiler(document)) for output, compiler in jobs]
    if len({path.resolve() for path, _ in compiled}) != len(compiled):
        raise ValueError('Duplicate token output destination')
    stale = [(path, content) for path, content in compiled
             if not path.exists() or path.read_text(encoding='utf-8') != content]
    if check:
        if stale:
            raise ValueError('Stale token outputs: ' + ', '.join(path.name for path, _ in stale))
        for path, _ in compiled:
            print(f'{path.name} matches its source')
        return
    staged = []
    try:
        for path, content in stale:
            path.parent.mkdir(parents=True, exist_ok=True)
            fd, temporary = tempfile.mkstemp(prefix='.' + path.name + '.', dir=path.parent)
            temporary = Path(temporary)
            staged.append((path, temporary))
            with os.fdopen(fd, 'w', encoding='utf-8', newline='\n') as stream:
                stream.write(content)
            temporary.chmod(path.stat().st_mode & 0o777 if path.exists() else 0o644)
        for path, temporary in staged:
            os.replace(temporary, path)
    finally:
        for _, temporary in staged:
            temporary.unlink(missing_ok=True)


def root_font_argument(value):
    try:
        result = Decimal(value)
    except ArithmeticError as exc:
        raise argparse.ArgumentTypeError('root font must be a finite positive number') from exc
    if not result.is_finite() or result <= 0:
        raise argparse.ArgumentTypeError('root font must be a finite positive number')
    return result


def main():
    global ROOT_FONT_PX, COLOR_GAMUT, COLOR_MISSING
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    parser.add_argument('--resolver', type=Path, default=ROOT / 'tokens/gearui.resolver.json')
    parser.add_argument('--input', default='{}', help='JSON modifier/context selections')
    parser.add_argument('--root-font-px', type=root_font_argument, default=ROOT_FONT_PX, help='Explicit fixed rem conversion (default 16 logical px)')
    parser.add_argument('--color-gamut', choices=('error', 'clip'), default='error')
    parser.add_argument('--missing-color', choices=('error', 'zero'), default='error')
    args = parser.parse_args()
    if not args.root_font_px.is_finite() or args.root_font_px <= 0:
        parser.error('--root-font-px must be finite and positive')
    ROOT_FONT_PX, COLOR_GAMUT, COLOR_MISSING = args.root_font_px, args.color_gamut, args.missing_color
    resolver = Resolver(load_json(args.resolver), args.resolver.resolve().parent)
    resolver.validate_all_contexts()
    document = resolver.resolve(parse_json(args.input, '--input'))
    publish_outputs(document, [(OUTPUT, generate), (COLOR_OUTPUT, generate_colors),
                    (TYPE_OUTPUT, generate_typography), (MATERIAL_OUTPUT, generate_materials),
                    (ROOT / 'gearui-kit/src/commonMain/kotlin/com/gearui/foundation/motion/FeedbackDefaults.kt', generate_feedback)], args.check)


if __name__ == "__main__":
    try:
        main()
    except (ValueError, OSError) as exc:
        raise SystemExit(str(exc)) from None
