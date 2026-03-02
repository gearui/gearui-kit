package com.gearui.components.switch

import androidx.compose.runtime.*
import com.gearui.components.icon.Icons
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Switch 尺寸枚举
 */
enum class SwitchSize {
    LARGE,   // 大尺寸 52x32
    MEDIUM,  // 中尺寸 45x28
    SMALL    // 小尺寸 39x24
}

/**
 * Switch 类型枚举
 */
enum class SwitchType {
    FILL,    // 填充型（默认）
    TEXT,    // 带文字
    LOADING, // 加载中
    ICON     // 带图标
}

/**
 * Switch 尺寸数据类
 */
private data class SwitchDimensions(
    val trackWidth: Dp,
    val trackHeight: Dp,
    val thumbSize: Dp,
    val thumbPadding: Dp
)

/**
 * Switch - 开关组件
 *
 * 特性：
 * - 开/关状态切换
 * - 禁用状态（整体透明度 0.4）
 * - 加载状态（显示加载指示器，不可点击）
 * - 文字类型（滑块内显示开/关文字）
 * - 图标类型（滑块内显示勾/叉图标）
 * - 3种尺寸（大、中、小）
 * - 自定义轨道颜色
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: SwitchType = SwitchType.FILL,
    size: SwitchSize = SwitchSize.MEDIUM,
    trackOnColor: Color? = null,
    trackOffColor: Color? = null,
    openText: String = "开",
    closeText: String = "关"
) {
    val colors = Theme.colors

    val dimensions = when (size) {
        SwitchSize.LARGE -> SwitchDimensions(52.dp, 32.dp, 28.dp, 2.dp)
        SwitchSize.MEDIUM -> SwitchDimensions(45.dp, 28.dp, 24.dp, 2.dp)
        SwitchSize.SMALL -> SwitchDimensions(39.dp, 24.dp, 20.dp, 2.dp)
    }

    // 加载状态时不可点击
    val switchEnabled = enabled && type != SwitchType.LOADING

    // 轨道颜色
    val activeTrackColor = trackOnColor ?: colors.primary
    val inactiveTrackColor = trackOffColor ?: colors.textDisabled

    val trackColor = if (checked) activeTrackColor else inactiveTrackColor

    // 滑块内容颜色
    val thumbContentColor = if (checked) colors.primary else colors.textDisabled

    // 滑块位置
    val thumbOffset = if (checked) {
        dimensions.trackWidth - dimensions.thumbSize - dimensions.thumbPadding
    } else {
        dimensions.thumbPadding
    }

    // 整体透明度：禁用时 0.4
    val alpha = if (enabled) 1f else 0.4f

    Box(
        modifier = modifier
            .width(dimensions.trackWidth)
            .height(dimensions.trackHeight)
            .alpha(alpha)
            .clip(RoundedCornerShape(dimensions.trackHeight / 2))
            .background(trackColor)
            .then(
                if (switchEnabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // 滑块
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(dimensions.thumbSize)
                .clip(RoundedCornerShape(dimensions.thumbSize / 2))
                .background(colors.surface),
            contentAlignment = Alignment.Center
        ) {
            // 滑块内容
            when (type) {
                SwitchType.TEXT -> {
                    // 文字类型：显示开/关
                    Text(
                        text = if (checked) openText else closeText,
                        style = Typography.BodySmall,
                        color = thumbContentColor,
                        maxLines = 1
                    )
                }
                SwitchType.ICON -> {
                    // 图标类型：显示勾/叉
                    Icon(
                        name = if (checked) Icons.check else Icons.close,
                        size = 12.dp,
                        tint = thumbContentColor
                    )
                }
                SwitchType.LOADING -> {
                    // 加载类型：显示加载指示器
                    Icon(
                        name = Icons.autorenew,
                        size = 12.dp,
                        tint = thumbContentColor
                    )
                }
                SwitchType.FILL -> {
                    // 填充类型：无内容
                }
            }
        }
    }
}

/**
 * SwitchWithLabel - 带标签的开关
 */
@Composable
fun SwitchWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: SwitchType = SwitchType.FILL,
    size: SwitchSize = SwitchSize.MEDIUM
) {
    val colors = Theme.colors
    val typography = Theme.typography

    // 加载状态时不可点击
    val switchEnabled = enabled && type != SwitchType.LOADING

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (switchEnabled) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else Modifier
            )
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = Typography.BodyLarge,
            color = if (enabled) colors.textPrimary else colors.textDisabled
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            type = type,
            size = size
        )
    }
}
