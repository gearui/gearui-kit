package com.gearui.sample.examples.timeline

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.timeline.Timeline
import com.gearui.components.timeline.TimelineColor
import com.gearui.components.timeline.TimelineCustom
import com.gearui.components.timeline.TimelineItem
import com.gearui.components.timeline.TimelineMode
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Timeline 组件示例
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
        // 基础时间轴
        ExampleSection(
            title = "基础用法",
            description = "展示时间流程的基础时间轴"
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

        // 不同颜色
        ExampleSection(
            title = "颜色主题",
            description = "支持多种颜色表示不同状态"
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

        // 右侧模式
        ExampleSection(
            title = "右侧模式",
            description = "内容显示在右侧"
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

        // 交替模式
        ExampleSection(
            title = "交替模式",
            description = "内容左右交替显示"
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

        // 倒序显示
        ExampleSection(
            title = "倒序显示",
            description = "时间轴倒序排列"
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

        // 自定义内容
        ExampleSection(
            title = "自定义内容",
            description = "使用 TimelineCustom 自定义内容"
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
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "任务完成",
                                style = Typography.TitleMedium,
                                color = colors.success
                            )
                            Text(
                                text = "所有测试用例通过",
                                style = Typography.BodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                    1 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "代码审查中",
                                style = Typography.TitleMedium,
                                color = colors.primary
                            )
                            Text(
                                text = "等待团队成员审核",
                                style = Typography.BodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                    else -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "待处理",
                                style = Typography.TitleMedium,
                                color = colors.warning
                            )
                            Text(
                                text = "需要进一步优化",
                                style = Typography.BodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // 实际应用：物流追踪
        ExampleSection(
            title = "物流追踪",
            description = "实际应用场景示例"
        ) {
            Timeline(
                items = listOf(
                    TimelineItem(
                        content = "快件已签收，签收人：本人",
                        timestamp = "01-17 15:20",
                        color = TimelineColor.SUCCESS
                    ),
                    TimelineItem(
                        content = "派送中，快递员：张师傅 138****1234",
                        timestamp = "01-17 09:30",
                        color = TimelineColor.PRIMARY
                    ),
                    TimelineItem(
                        content = "快件已到达【北京朝阳营业部】",
                        timestamp = "01-17 06:00"
                    ),
                    TimelineItem(
                        content = "快件已发出，下一站【北京转运中心】",
                        timestamp = "01-16 18:00"
                    ),
                    TimelineItem(
                        content = "快件已发出，下一站【上海转运中心】",
                        timestamp = "01-15 20:00"
                    ),
                    TimelineItem(
                        content = "商家已发货",
                        timestamp = "01-15 14:30"
                    )
                )
            )
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Timeline 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. 支持三种模式: LEFT, RIGHT, ALTERNATE",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. 支持五种颜色: DEFAULT, PRIMARY, SUCCESS, WARNING, ERROR",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. 支持倒序显示 (reverse)",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. TimelineCustom 支持完全自定义内容",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
