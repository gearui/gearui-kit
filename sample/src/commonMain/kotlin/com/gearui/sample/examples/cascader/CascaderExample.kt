package com.gearui.sample.examples.cascader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.cascader.Cascader
import com.gearui.components.cascader.CascaderOption
import com.gearui.foundation.field.FieldVariant
import com.gearui.foundation.primitives.Text
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import kotlinx.coroutines.delay

/**
 * An address tree keyed by administrative division codes, the way an app ships it: values
 * are codes (unique), labels are names (not: several cities have a district of the same name).
 */
private val Regions = listOf(
    CascaderOption("330000", "浙江省", children = listOf(
        CascaderOption("330100", "杭州市", children = listOf(
            CascaderOption("330106", "西湖区"),
            CascaderOption("330108", "滨江区"),
            CascaderOption("330109", "萧山区"),
        )),
        CascaderOption("330200", "宁波市", children = listOf(
            CascaderOption("330203", "海曙区"),
            CascaderOption("330205", "江北区"),
        )),
    )),
    CascaderOption("320000", "江苏省", children = listOf(
        CascaderOption("320100", "南京市", children = listOf(
            CascaderOption("320102", "玄武区"),
            CascaderOption("320104", "秦淮区"),
        )),
        CascaderOption("320500", "苏州市", children = listOf(
            CascaderOption("320508", "姑苏区"),
            CascaderOption("320506", "吴中区"),
        )),
    )),
    // A region with no level below it: choosing it completes the path at once.
    CascaderOption("810000", "香港特别行政区"),
)

/** Provinces whose cities and districts are fetched level by level. */
private val LazyRegions = listOf(
    CascaderOption("440000", "广东省", isLeaf = false),
    CascaderOption("510000", "四川省", isLeaf = false),
)

private val LazyChildren = mapOf(
    "440000" to listOf(
        CascaderOption("440100", "广州市", isLeaf = false),
        CascaderOption("440300", "深圳市", isLeaf = false),
    ),
    "510000" to listOf(CascaderOption("510100", "成都市", isLeaf = false)),
    "440100" to listOf(CascaderOption("440106", "天河区"), CascaderOption("440104", "越秀区")),
    "440300" to listOf(CascaderOption("440305", "南山区"), CascaderOption("440304", "福田区")),
    "510100" to listOf(CascaderOption("510104", "锦江区"), CascaderOption("510107", "武侯区")),
)

/**
 * Cascader examples: an address picked province → city → district in a bottom sheet.
 */
@Composable
fun CascaderExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val colors = Theme.colors

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            title = "地址选择",
            description = "底部面板逐级选择，选到最后一级自动完成；中途关闭不改变已选值"
        ) {
            var path by remember { mutableStateOf<List<String>>(emptyList()) }
            Cascader(
                options = Regions,
                selectedPath = path,
                onSelect = { path = it },
                placeholder = "请选择所在地区",
                variant = FieldVariant.SECONDARY,
            )
            Text(
                text = "value：${path.joinToString(" / ").ifEmpty { "无" }}",
                style = Theme.typography.bodySmall,
                color = colors.mutedForeground,
            )
        }

        ExampleSection(
            title = "默认值",
            description = "打开时停在已选的最后一级"
        ) {
            var path by remember { mutableStateOf(listOf("330000", "330100", "330106")) }
            Cascader(
                options = Regions,
                selectedPath = path,
                onSelect = { path = it },
                separator = " ",
                variant = FieldVariant.SECONDARY,
            )
        }

        ExampleSection(
            title = "异步加载",
            description = "loadChildren 按需拉取下一级；首次打开广东省会失败一次，点重试"
        ) {
            var path by remember { mutableStateOf<List<String>>(emptyList()) }
            var failedOnce by remember { mutableStateOf(false) }
            Cascader(
                options = LazyRegions,
                selectedPath = path,
                onSelect = { path = it },
                placeholder = "请选择所在地区",
                variant = FieldVariant.SECONDARY,
                loadChildren = { node ->
                    delay(700)
                    if (node.value == "440000" && !failedOnce) {
                        failedOnce = true
                        error("network")
                    }
                    LazyChildren[node.value].orEmpty()
                },
            )
        }

        ExampleSection(
            title = "禁用选项",
            description = "禁用的节点不可选择"
        ) {
            var path by remember { mutableStateOf<List<String>>(emptyList()) }
            Cascader(
                options = listOf(
                    CascaderOption("tech", "技术部", children = listOf(
                        CascaderOption("frontend", "前端组"),
                        CascaderOption("backend", "后端组"),
                        CascaderOption("qa", "测试组", disabled = true),
                    )),
                    CascaderOption("product", "产品部", disabled = true, children = listOf(
                        CascaderOption("pm", "产品经理"),
                    )),
                    CascaderOption("ops", "运营部", children = listOf(
                        CascaderOption("content", "内容运营"),
                        CascaderOption("growth", "用户运营"),
                    )),
                ),
                selectedPath = path,
                onSelect = { path = it },
                placeholder = "请选择部门",
                variant = FieldVariant.SECONDARY,
            )
        }
    }
}
