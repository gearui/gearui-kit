package com.gearui.sample.examples.cascader

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.cascader.Cascader
import com.gearui.components.cascader.CascaderOption
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.theme.Theme

/**
 * Cascader 组件示例
 *
 * 级联选择器，用于多级关联选择
 */
@Composable
fun CascaderExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(
        component = component,
        onBack = onBack
    ) {
        // 基础级联选择
        ExampleSection(
            title = "基础用法",
            description = "点击展开下一级选项"
        ) {
            var selectedPath by remember { mutableStateOf<List<String>>(emptyList()) }

            val options = remember {
                listOf(
                    CascaderOption(
                        value = "zhejiang",
                        label = "浙江省",
                        children = listOf(
                            CascaderOption(
                                value = "hangzhou",
                                label = "杭州市",
                                children = listOf(
                                    CascaderOption(value = "xihu", label = "西湖区"),
                                    CascaderOption(value = "binjiang", label = "滨江区"),
                                    CascaderOption(value = "xiaoshan", label = "萧山区")
                                )
                            ),
                            CascaderOption(
                                value = "ningbo",
                                label = "宁波市",
                                children = listOf(
                                    CascaderOption(value = "haishu", label = "海曙区"),
                                    CascaderOption(value = "jiangbei", label = "江北区")
                                )
                            )
                        )
                    ),
                    CascaderOption(
                        value = "jiangsu",
                        label = "江苏省",
                        children = listOf(
                            CascaderOption(
                                value = "nanjing",
                                label = "南京市",
                                children = listOf(
                                    CascaderOption(value = "xuanwu", label = "玄武区"),
                                    CascaderOption(value = "qinhuai", label = "秦淮区")
                                )
                            ),
                            CascaderOption(
                                value = "suzhou",
                                label = "苏州市",
                                children = listOf(
                                    CascaderOption(value = "gusu", label = "姑苏区"),
                                    CascaderOption(value = "wuzhong", label = "吴中区")
                                )
                            )
                        )
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Cascader(
                    options = options,
                    selectedPath = selectedPath,
                    onSelect = { selectedPath = it },
                    placeholder = "请选择地区",
                    dropdownHeight = 250.dp
                )

                Text(
                    text = "已选择路径: ${selectedPath.joinToString(" > ").ifEmpty { "无" }}",
                    style = Typography.BodySmall,
                    color = colors.textSecondary
                )
            }
        }

        // 默认值
        ExampleSection(
            title = "默认值",
            description = "设置初始选中值"
        ) {
            var selectedPath by remember { mutableStateOf(listOf("zhejiang", "hangzhou", "xihu")) }

            val options = remember {
                listOf(
                    CascaderOption(
                        value = "zhejiang",
                        label = "浙江省",
                        children = listOf(
                            CascaderOption(
                                value = "hangzhou",
                                label = "杭州市",
                                children = listOf(
                                    CascaderOption(value = "xihu", label = "西湖区"),
                                    CascaderOption(value = "binjiang", label = "滨江区")
                                )
                            )
                        )
                    ),
                    CascaderOption(
                        value = "jiangsu",
                        label = "江苏省",
                        children = listOf(
                            CascaderOption(
                                value = "nanjing",
                                label = "南京市",
                                children = listOf(
                                    CascaderOption(value = "xuanwu", label = "玄武区")
                                )
                            )
                        )
                    )
                )
            }

            Cascader(
                options = options,
                selectedPath = selectedPath,
                onSelect = { selectedPath = it },
                placeholder = "请选择地区",
                dropdownHeight = 200.dp
            )
        }

        // 自定义分隔符
        ExampleSection(
            title = "自定义分隔符",
            description = "使用自定义分隔符显示选中值"
        ) {
            var selectedPath by remember { mutableStateOf<List<String>>(emptyList()) }

            val options = remember {
                listOf(
                    CascaderOption(
                        value = "food",
                        label = "食品",
                        children = listOf(
                            CascaderOption(
                                value = "fruit",
                                label = "水果",
                                children = listOf(
                                    CascaderOption(value = "apple", label = "苹果"),
                                    CascaderOption(value = "banana", label = "香蕉"),
                                    CascaderOption(value = "orange", label = "橙子")
                                )
                            ),
                            CascaderOption(
                                value = "vegetable",
                                label = "蔬菜",
                                children = listOf(
                                    CascaderOption(value = "tomato", label = "番茄"),
                                    CascaderOption(value = "cucumber", label = "黄瓜")
                                )
                            )
                        )
                    ),
                    CascaderOption(
                        value = "electronics",
                        label = "电子产品",
                        children = listOf(
                            CascaderOption(
                                value = "phone",
                                label = "手机",
                                children = listOf(
                                    CascaderOption(value = "iphone", label = "iPhone"),
                                    CascaderOption(value = "android", label = "Android")
                                )
                            )
                        )
                    )
                )
            }

            Cascader(
                options = options,
                selectedPath = selectedPath,
                onSelect = { selectedPath = it },
                placeholder = "请选择分类",
                separator = " - ",
                dropdownHeight = 200.dp
            )
        }

        // 禁用选项
        ExampleSection(
            title = "禁用选项",
            description = "部分选项可设置为禁用状态"
        ) {
            var selectedPath by remember { mutableStateOf<List<String>>(emptyList()) }

            val options = remember {
                listOf(
                    CascaderOption(
                        value = "dept1",
                        label = "技术部",
                        children = listOf(
                            CascaderOption(value = "frontend", label = "前端组"),
                            CascaderOption(value = "backend", label = "后端组"),
                            CascaderOption(value = "qa", label = "测试组", disabled = true)
                        )
                    ),
                    CascaderOption(
                        value = "dept2",
                        label = "产品部",
                        disabled = true,
                        children = listOf(
                            CascaderOption(value = "pm", label = "产品经理"),
                            CascaderOption(value = "designer", label = "设计师")
                        )
                    ),
                    CascaderOption(
                        value = "dept3",
                        label = "运营部",
                        children = listOf(
                            CascaderOption(value = "content", label = "内容运营"),
                            CascaderOption(value = "user", label = "用户运营")
                        )
                    )
                )
            }

            Cascader(
                options = options,
                selectedPath = selectedPath,
                onSelect = { selectedPath = it },
                placeholder = "请选择部门",
                dropdownHeight = 200.dp
            )
        }

        // 使用说明
        ExampleSection(
            title = "使用说明",
            description = "Cascader 组件特性"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. options: 级联选项数据，支持多级嵌套",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "2. selectedPath: 当前选中路径 (value 列表)",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "3. separator: 显示文本的分隔符",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "4. disabled: 禁用某些选项",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = "5. 基于 GearOverlay 实现真正的浮层",
                    style = Typography.BodyMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
