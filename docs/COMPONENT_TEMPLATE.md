# GearUI Kit - 组件开发模板

> 标准的组件开发模板和最佳实践

---

## 📝 基础组件模板

```kotlin
package com.gearui.components.mycomponent

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.GearTheme

/**
 * MyComponent - 100% GearTheme 驱动
 *
 * ✅ 规则：第一行永远是 val colors = GearTheme.colors
 * ❌ 禁止：Color(0x...) / 硬编码颜色
 *
 * @param text 显示的文本
 * @param modifier Modifier
 * @param enabled 是否启用
 * @param onClick 点击回调
 */
@Composable
fun MyComponent(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    // ⭐ Framework Rule #1: 第一行永远是这个
    val colors = GearTheme.colors
    
    // 组件实现
    Box(
        modifier = modifier
            .background(colors.surface)
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable { onClick() }
                } else Modifier
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Typography.BodyMedium,
            color = if (enabled) colors.textPrimary else colors.textDisabled
        )
    }
}
```

---

## 🎨 带主题变体的组件模板

```kotlin
package com.gearui.components.mycomponent

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.GearTheme

/**
 * MyComponent - 支持多种主题
 */
@Composable
fun MyComponent(
    text: String,
    modifier: Modifier = Modifier,
    theme: MyComponentTheme = MyComponentTheme.PRIMARY,
    variant: MyComponentVariant = MyComponentVariant.FILLED,
    disabled: Boolean = false
) {
    // ⭐ Framework Rule #1
    val colors = GearTheme.colors
    
    // ⭐ 颜色映射：GearTheme 语义 → 组件视觉
    val (themeColor, themeLightColor) = when (theme) {
        MyComponentTheme.PRIMARY -> colors.primary to colors.primaryLight
        MyComponentTheme.SUCCESS -> colors.success to colors.successLight
        MyComponentTheme.WARNING -> colors.warning to colors.warningLight
        MyComponentTheme.DANGER -> colors.danger to colors.dangerLight
    }
    
    val (backgroundColor, textColor) = when (variant) {
        MyComponentVariant.FILLED -> themeColor to colors.onPrimary
        MyComponentVariant.LIGHT -> themeLightColor to themeColor
        MyComponentVariant.OUTLINE -> colors.surface to themeColor
    }
    
    val finalBackgroundColor = if (disabled) colors.disabledContainer else backgroundColor
    val finalTextColor = if (disabled) colors.disabled else textColor
    
    Box(
        modifier = modifier
            .background(finalBackgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            style = Typography.BodyMedium,
            color = finalTextColor
        )
    }
}

enum class MyComponentTheme { PRIMARY, SUCCESS, WARNING, DANGER }
enum class MyComponentVariant { FILLED, LIGHT, OUTLINE }
```

---

## 📦 完整组件示例（带 Showcase）

### MyComponent.kt

```kotlin
package com.gearui.components.mycomponent

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.GearTheme

/**
 * MyComponent - 完整示例
 *
 * Features:
 * - 3 种主题 (PRIMARY, SUCCESS, DANGER)
 * - 3 种尺寸 (LARGE, MEDIUM, SMALL)
 * - 支持禁用状态
 * - 支持图标
 */
@Composable
fun MyComponent(
    text: String,
    modifier: Modifier = Modifier,
    theme: MyComponentTheme = MyComponentTheme.PRIMARY,
    size: MyComponentSize = MyComponentSize.MEDIUM,
    disabled: Boolean = false,
    onClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null
) {
    val colors = GearTheme.colors
    
    // 尺寸参数
    val (height, horizontalPadding, textStyle) = when (size) {
        MyComponentSize.LARGE -> Triple(48.dp, 24.dp, Typography.BodyLarge)
        MyComponentSize.MEDIUM -> Triple(40.dp, 16.dp, Typography.BodyMedium)
        MyComponentSize.SMALL -> Triple(32.dp, 12.dp, Typography.BodySmall)
    }
    
    // 主题颜色
    val themeColor = when (theme) {
        MyComponentTheme.PRIMARY -> colors.primary
        MyComponentTheme.SUCCESS -> colors.success
        MyComponentTheme.DANGER -> colors.danger
    }
    
    val backgroundColor = if (disabled) colors.disabledContainer else themeColor
    val textColor = if (disabled) colors.disabled else colors.onPrimary
    
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (onClick != null && !disabled) {
                    Modifier.clickable { onClick() }
                } else Modifier
            )
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon?.invoke()
            
            Text(
                text = text,
                style = textStyle,
                color = textColor
            )
        }
    }
}

enum class MyComponentTheme { PRIMARY, SUCCESS, DANGER }
enum class MyComponentSize { LARGE, MEDIUM, SMALL }
```

### MyComponentShowcase.kt

```kotlin
package com.gearui.components.mycomponent

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.GearTheme

/**
 * MyComponentShowcase - 组件展示页
 */
@Composable
fun MyComponentShowcase() {
    val colors = GearTheme.colors
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // 标题
        Text(
            text = "MyComponent 组件",
            style = Typography.HeadlineMedium,
            color = colors.textPrimary
        )
        
        // 基础示例
        SectionTitle("基础用法")
        MyComponent(
            text = "默认按钮",
            onClick = { /* TODO */ }
        )
        
        // 主题示例
        SectionTitle("不同主题")
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MyComponent(
                text = "Primary",
                theme = MyComponentTheme.PRIMARY,
                onClick = {}
            )
            MyComponent(
                text = "Success",
                theme = MyComponentTheme.SUCCESS,
                onClick = {}
            )
            MyComponent(
                text = "Danger",
                theme = MyComponentTheme.DANGER,
                onClick = {}
            )
        }
        
        // 尺寸示例
        SectionTitle("不同尺寸")
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MyComponent(text = "Large", size = MyComponentSize.LARGE, onClick = {})
            MyComponent(text = "Medium", size = MyComponentSize.MEDIUM, onClick = {})
            MyComponent(text = "Small", size = MyComponentSize.SMALL, onClick = {})
        }
        
        // 禁用状态
        SectionTitle("禁用状态")
        MyComponent(
            text = "Disabled",
            disabled = true,
            onClick = {}
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    val colors = GearTheme.colors
    
    Text(
        text = text,
        style = Typography.TitleMedium,
        color = colors.textPrimary,
        modifier = Modifier.padding(top = 8.dp)
    )
}
```

---

## ✅ 检查清单

开发组件时，确保满足以下所有条件：

### Import 规范
- [ ] `androidx.compose.runtime.*` ✅
- [ ] `com.tencent.kuikly.compose.foundation.*` ✅
- [ ] `com.tencent.kuikly.compose.ui.*` ✅
- [ ] `com.gearui.foundation.primitives.Text` ✅
- [ ] `com.gearui.foundation.typography.Typography` ✅
- [ ] `com.gearui.theme.GearTheme` ✅
- [ ] ❌ NO `androidx.compose.foundation.*`
- [ ] ❌ NO `androidx.compose.ui.*`
- [ ] ❌ NO `com.tencent.kuikly.compose.material3.Text`

### 代码规范
- [ ] 第一行是 `val colors = GearTheme.colors`
- [ ] 使用 `Text` from `com.gearui.foundation.primitives`
- [ ] Typography 首字母大写 (`Typography.BodyMedium`)
- [ ] 所有颜色来自 `colors.*`
- [ ] ❌ 没有 `Color(0x...)`
- [ ] ❌ 没有 `GearText`, `GearCheckbox` 等
- [ ] ❌ 没有 `ButtonColorTokens`, `BorderTokens` 等

### 文档规范
- [ ] 组件有完整的 KDoc 注释
- [ ] 说明 ✅ 规则和 ❌ 禁止
- [ ] 列出所有参数说明
- [ ] 有使用示例

### Showcase 规范
- [ ] 创建对应的 Showcase 文件
- [ ] 展示所有主要变体
- [ ] 展示不同尺寸
- [ ] 展示禁用状态
- [ ] 使用 SectionTitle 分组

---

## 🚀 快速创建新组件

```bash
# 1. 创建组件目录
mkdir -p gearui-kit/src/commonMain/kotlin/com/gearui/components/mycomponent

# 2. 复制模板
cp docs/COMPONENT_TEMPLATE.md \
   gearui-kit/src/commonMain/kotlin/com/gearui/components/mycomponent/MyComponent.kt

# 3. 修改组件名和实现
# 编辑 MyComponent.kt

# 4. 创建 Showcase
# 编辑 MyComponentShowcase.kt

# 5. 运行规范检查
./scripts/CHECK_STANDARDS.sh

# 6. 编译测试
./gradlew :gearui-kit:compileDebugKotlinAndroid
```

---

## 📚 参考示例

### 简单组件参考
- **Tag.kt** - 简单的展示组件，多种主题和变体
- **Badge.kt** - 小型装饰组件
- **Divider.kt** - 最简单的布局组件

### 复杂组件参考
- **Button.kt** - 完整的交互组件，支持多种状态
- **Checkbox.kt** - 表单输入组件
- **Input.kt** - 复杂的表单组件

### 最佳实践组件
- **Button.kt** - ✅ 完美遵循所有规范
- **Tag.kt** - ✅ 优秀的颜色映射示例
- **Checkbox.kt** - ✅ 清晰的状态管理

---

*最后更新: 2026-02-03*
