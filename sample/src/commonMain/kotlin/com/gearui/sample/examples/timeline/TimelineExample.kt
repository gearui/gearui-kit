package com.gearui.sample.examples.timeline

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.gearui.components.timeline.Timeline
import com.gearui.components.timeline.TimelineColor
import com.gearui.components.timeline.TimelineCustom
import com.gearui.components.timeline.TimelineItem
import com.gearui.components.timeline.TimelineMode
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

/**
 * Timeline component examples
 */
@Composable
fun TimelineExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // Basic timeline
        ExampleSection(
            title = "基础用法",
            description = "按时间先后列出事件与时间戳"
        ) {
            Timeline(
                items = listOf(
                    TimelineItem(
                        content = "创建订单",
                        timestamp = "2024-01-15 10:00"
                    ),
                    TimelineItem(
                        content = "支付成功",
                        timestamp = "2024-01-15 10:05"
                    ),
                    TimelineItem(
                        content = "商家发货",
                        timestamp = "2024-01-15 14:30"
                    ),
                    TimelineItem(
                        content = "快递运输中",
                        timestamp = "2024-01-16 09:00"
                    ),
                    TimelineItem(
                        content = "已签收",
                        timestamp = "2024-01-17 15:20"
                    )
                )
            )
        }

        // Colours
        ExampleSection(
            title = "节点颜色",
            description = "color 区分默认、主色、成功、警告、错误"
        ) {
            Timeline(
                items = listOf(
                    TimelineItem(
                        content = "系统初始化",
                        timestamp = "09:00",
                        color = TimelineColor.DEFAULT
                    ),
                    TimelineItem(
                        content = "数据加载完成",
                        timestamp = "09:05",
                        color = TimelineColor.SUCCESS
                    ),
                    TimelineItem(
                        content = "检测到警告",
                        timestamp = "09:10",
                        color = TimelineColor.WARNING
                    ),
                    TimelineItem(
                        content = "正在处理中",
                        timestamp = "09:15",
                        color = TimelineColor.PRIMARY
                    ),
                    TimelineItem(
                        content = "发生错误",
                        timestamp = "09:20",
                        color = TimelineColor.ERROR
                    )
                )
            )
        }

        // Right-hand mode
        ExampleSection(
            title = "右侧模式",
            description = "mode = RIGHT，内容排在轴的右侧"
        ) {
            Timeline(
                mode = TimelineMode.RIGHT,
                items = listOf(
                    TimelineItem(
                        content = "项目启动",
                        timestamp = "第1周"
                    ),
                    TimelineItem(
                        content = "需求分析",
                        timestamp = "第2-3周"
                    ),
                    TimelineItem(
                        content = "设计阶段",
                        timestamp = "第4-5周"
                    ),
                    TimelineItem(
                        content = "开发阶段",
                        timestamp = "第6-10周"
                    )
                )
            )
        }

        // Alternating mode
        ExampleSection(
            title = "交替模式",
            description = "mode = ALTERNATE，内容左右交替"
        ) {
            Timeline(
                mode = TimelineMode.ALTERNATE,
                items = listOf(
                    TimelineItem(
                        content = "2020年 - 公司成立",
                        timestamp = "里程碑",
                        color = TimelineColor.PRIMARY
                    ),
                    TimelineItem(
                        content = "2021年 - 首个产品发布",
                        timestamp = "里程碑",
                        color = TimelineColor.SUCCESS
                    ),
                    TimelineItem(
                        content = "2022年 - 用户突破100万",
                        timestamp = "里程碑",
                        color = TimelineColor.SUCCESS
                    ),
                    TimelineItem(
                        content = "2023年 - 获得A轮融资",
                        timestamp = "里程碑",
                        color = TimelineColor.PRIMARY
                    ),
                    TimelineItem(
                        content = "2024年 - 国际化布局",
                        timestamp = "里程碑",
                        color = TimelineColor.WARNING
                    )
                )
            )
        }

        // Reverse order
        ExampleSection(
            title = "倒序显示",
            description = "reverse 让最新的事件排在最前"
        ) {
            Timeline(
                reverse = true,
                items = listOf(
                    TimelineItem(
                        content = "最早的事件",
                        timestamp = "08:00"
                    ),
                    TimelineItem(
                        content = "中间的事件",
                        timestamp = "12:00"
                    ),
                    TimelineItem(
                        content = "最新的事件",
                        timestamp = "18:00",
                        color = TimelineColor.PRIMARY
                    )
                )
            )
        }

        // Custom content
        ExampleSection(
            title = "自定义内容",
            description = "TimelineCustom 自定义每个节点的内容与圆点颜色"
        ) {
            TimelineCustom(
                itemCount = 3,
                mode = TimelineMode.LEFT,
                dotColor = { index ->
                    when (index) {
                        0 -> colors.success
                        1 -> colors.primary
                        else -> colors.warning
                    }
                }
            ) { index ->
                when (index) {
                    0 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Text(
                                text = "任务完成",
                                style = Theme.typography.titleMedium,
                                color = colors.success
                            )
                            Text(
                                text = "所有测试用例通过",
                                style = Theme.typography.bodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Text(
                                text = "代码审查中",
                                style = Theme.typography.titleMedium,
                                color = colors.primary
                            )
                            Text(
                                text = "等待团队成员审核",
                                style = Theme.typography.bodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Text(
                                text = "待处理",
                                style = Theme.typography.titleMedium,
                                color = colors.warning
                            )
                            Text(
                                text = "需要进一步优化",
                                style = Theme.typography.bodySmall,
                                color = colors.mutedForeground
                            )
                        }
                    }
                }
            }
        }
    }
}
