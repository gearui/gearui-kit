"""Explicit sRGB lowering for DTCG colors; never modifies interchange data.

Matrices/transfer functions follow CSS Color 4 color-conversion-code:
https://www.w3.org/TR/css-color-4/#color-conversion-code
OKLab uses the published inverse (https://bottosson.github.io/posts/oklab/).
"""
import colorsys
import math

from dtcg_format import fail, validate_value

XYZ_TO_RGB = ((12831/3959, -329/214, -1974/3959),
              (-851781/878810, 1648619/878810, 36519/878810),
              (705/12673, -2585/12673, 705/667))
TO_XYZ = {
    'display-p3': ((608311/1250200, 189793/714400, 198249/1000160),
                   (35783/156275, 247089/357200, 198249/2500400),
                   (0, 32229/714400, 5220557/5000800)),
    'a98-rgb': ((573536/994567, 263643/1420810, 187206/994567),
                (591459/1989134, 6239551/9945670, 374412/4972835),
                (53769/1989134, 351524/4972835, 4929758/4972835)),
    'prophoto-rgb': ((.7977666449006423, .13518129740053308, .0313477341283922),
                     (.2880748288194013, .711835234241873, .00008993693872564),
                     (0, 0, .8251046025104602)),
    'rec2020': ((63426534/99577255, 20160776/139408157, 47086771/278816314),
                (26158966/99577255, 472592308/697040785, 8267143/139408157),
                (0, 19567812/697040785, 295819943/278816314)),
}
D50_TO_D65 = ((.955473421488075, -.02309845494876471, .06325924320057072),
              (-.0283697093338637, 1.0099953980813041, .021041441191917323),
              (.012314014864481998, -.020507649298898964, 1.330365926242124))


def multiply(matrix, vector):
    return [sum(a * b for a, b in zip(row, vector)) for row in matrix]


def decode(value):
    return value / 12.92 if value <= .04045 else ((value + .055) / 1.055) ** 2.4


def to_srgb(value, name='color', *, gamut='error', missing='error'):
    """gamut=clip is opt-in channel clipping, not perceptual gamut mapping.

    Missing components are preserved by the Format processor. A static renderer
    may explicitly choose zero; that policy must not be used for interpolation.
    """
    validate_value('color', value, name)
    if gamut not in ('error', 'clip') or missing not in ('error', 'zero'):
        fail(name, 'unknown color lowering policy')
    components = value['components']
    if 'none' in components:
        if missing == 'error':
            fail(name, 'missing components require an explicit static-render policy')
        components = [0 if x == 'none' else x for x in components]
    space, alpha = value['colorSpace'], value.get('alpha', 1)
    if space == 'srgb':
        return [*components, alpha]
    if space == 'hsl':
        h, s, l = components
        return [*colorsys.hls_to_rgb(h / 360, l / 100, s / 100), alpha]
    if space == 'hwb':
        h, w, b = components
        w, b = w / 100, b / 100
        rgb = [w / (w + b)] * 3 if w + b >= 1 else [c * (1 - w - b) + w for c in colorsys.hsv_to_rgb(h / 360, 1, 1)]
        return [*rgb, alpha]
    try:
        linear = linear_srgb(space, components)
    except OverflowError as exc:
        raise ValueError(f'{name}: color conversion overflow') from exc
    if any(not math.isfinite(c) for c in linear):
        fail(name, 'non-finite color conversion')
    if gamut == 'error' and any(c < -1e-6 or c > 1 + 1e-6 for c in linear):
        fail(name, 'outside sRGB gamut; explicit mapping policy required')
    rgb = []
    for c in linear:
        c = min(1, max(0, c))
        rgb.append(round(12.92 * c if c <= .0031308 else 1.055 * c ** (1 / 2.4) - .055, 9))
    return [*rgb, alpha]


def linear_srgb(space, components):
    if space == 'srgb-linear':
        return components
    if space in ('oklab', 'oklch'):
        lightness, a, b = components
        if space == 'oklch':
            a, b = a * math.cos(math.radians(b)), a * math.sin(math.radians(b))
        l = (lightness + .3963377774 * a + .2158037573 * b) ** 3
        m = (lightness - .1055613458 * a - .0638541728 * b) ** 3
        s = (lightness - .0894841775 * a - 1.2914855480 * b) ** 3
        return [4.0767416621*l - 3.3077115913*m + .2309699292*s,
                -1.2684380046*l + 2.6097574011*m - .3413193965*s,
                -.0041960863*l - .7034186147*m + 1.7076147010*s]
    if space in TO_XYZ:
        if space == 'display-p3':
            rgb = [decode(c) for c in components]
        elif space == 'a98-rgb':
            rgb = [c ** (563 / 256) for c in components]
        elif space == 'prophoto-rgb':
            rgb = [c / 16 if c <= 16 / 512 else c ** 1.8 for c in components]
        else:
            rgb = [c ** 2.4 for c in components]
        xyz = multiply(TO_XYZ[space], rgb)
    elif space in ('lab', 'lch'):
        lightness, a, b = components
        if space == 'lch':
            a, b = a * math.cos(math.radians(b)), a * math.sin(math.radians(b))
        fy = (lightness + 16) / 116
        f = (fy + a / 500, fy, fy - b / 200)
        white = (.3457 / .3585, 1, (1 - .3457 - .3585) / .3585)
        xyz = [(x ** 3 if x ** 3 > 216 / 24389 else (116 * x - 16) / (24389 / 27)) * w for x, w in zip(f, white)]
    else:
        xyz = components
    if space in ('lab', 'lch', 'xyz-d50', 'prophoto-rgb'):
        xyz = multiply(D50_TO_D65, xyz)
    return multiply(XYZ_TO_RGB, xyz)
