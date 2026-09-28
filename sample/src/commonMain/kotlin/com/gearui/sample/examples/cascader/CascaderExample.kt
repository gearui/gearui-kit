package com.gearui.sample.examples.cascader

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.components.cascader.Cascader
import com.gearui.components.cascader.CascaderOption
import com.gearui.foundation.layout.Spacing
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.sample.pages.SectionSurface
import com.gearui.foundation.primitives.Text
import com.gearui.theme.Theme

/** Height of the option panel, sized to the demo data. */
private val DropdownHeight = 240.dp

/**
 * Cascader component examples
 *
 * A cascading select, for choosing through several linked levels
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
        // Basic cascading select
        ExampleSection(
            surface = SectionSurface.Plain,
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

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Cascader(
                    options = options,
                    selectedPath = selectedPath,
                    onSelect = { selectedPath = it },
                    placeholder = "请选择地区",
                    dropdownHeight = DropdownHeight
                )

                Text(
                    text = "已选择路径: ${selectedPath.joinToString(" > ").ifEmpty { "无" }}",
                    style = Theme.typography.bodySmall,
                    color = colors.mutedForeground
                )
            }
        }

        // Default value
        ExampleSection(
            surface = SectionSurface.Plain,
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
                dropdownHeight = DropdownHeight
            )
        }

        // Custom separator
        ExampleSection(
            surface = SectionSurface.Plain,
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
                dropdownHeight = DropdownHeight
            )
        }

        // Disabled options
        ExampleSection(
            surface = SectionSurface.Plain,
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
                dropdownHeight = DropdownHeight
            )
        }
    }
}
