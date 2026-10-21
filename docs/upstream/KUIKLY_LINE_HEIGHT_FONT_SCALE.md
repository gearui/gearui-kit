# KuiklyUI Android：行高不随系统字号缩放（issue 草稿）

> 可直接提交的 issue 草稿，提交与否由维护者决定。上游仓库：`Tencent-TDS/KuiklyUI`。
> GearUI 已在 kit 内绕过（36bde64），上游修复后可删掉 `rendererLineHeight`。

## 标题

[Android] Text 的 lineHeight 按 dp 换算、fontSize 按 sp 换算，系统大字号下多行文字重叠

## 现象

Compose DSL 的 `Text` 同时设置 `fontSize` 和 `lineHeight`（例如 17.sp / 24.sp）。把系统字号调到最大
（`adb shell settings put system font_scale 2.0`）后，字号放大了，行高没变，折行文字的各行互相压在一起。
1.0 倍下一切正常。

## 原因

`core-render-android` 的 `KRRichTextView`（`KRTextProps.setProp`）：

```kotlin
PROP_KEY_FONT_SIZE -> fontSize = propValue.toNumberFloat()            // 之后经 spToPxF 换算，跟随 fontScale
PROP_KEY_LINE_HEIGHT -> lineHeight = kuiklyContext.toPxF(propValue.toNumberFloat())  // 按 dp 换算，不跟随
```

同一仓库里的 `KRTextFieldView.setLineHeight` 在 `useDpFontSizeDim == false` 时用 `spToPxI`，与字号一致，
所以输入框没有这个问题——只有 `KRRichTextView` 不一致。

## 建议修复

`KRRichTextView` 的行高按与字号相同的规则换算：`useDpFontSizeDim` 为 false 时用 `spToPxF`。这样
Android 14 的非线性字号缩放也会同样作用于行高，与平台 `TextView` 的行为一致。

## 复现环境

KuiklyUI 2.28.0（Kotlin 2.1.21），Android 16 真机（Xiaomi 2201122G），`font_scale` 2.0。
