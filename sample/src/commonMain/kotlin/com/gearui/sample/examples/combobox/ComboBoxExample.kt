package com.gearui.sample.examples.combobox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.combobox.ComboBox
import com.gearui.components.select.SelectOption
import com.gearui.foundation.field.FieldDescription
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.ui.unit.dp

private val CITIES = listOf(
    "北京", "上海", "广州", "深圳", "杭州", "成都", "南京", "武汉", "西安", "重庆", "苏州", "长沙",
).map { SelectOption(value = it, label = it) }

private val CURRENCIES = listOf(
    "CNY" to "人民币 CNY",
    "USD" to "美元 USD",
    "EUR" to "欧元 EUR",
    "JPY" to "日元 JPY",
    "HKD" to "港元 HKD",
).map { SelectOption(value = it.first, label = it.second) }

@Composable
fun ComboBoxExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    var city by remember { mutableStateOf("") }
    var picked by remember { mutableStateOf<String?>(null) }
    var currency by remember { mutableStateOf("") }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            useCardContainer = false,
            title = "输入筛选",
            description = "聚焦即展示候选，输入实时过滤；无匹配时不弹面板"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ComboBox(
                    query = city,
                    onQueryChange = { city = it },
                    options = CITIES,
                    onSelect = { option ->
                        city = option.label
                        picked = option.value
                    },
                    label = "城市",
                    placeholder = "输入城市名",
                    autoFocus = true,
                )
                FieldDescription(if (picked != null) "已选择:$picked" else "尚未选择")
            }
        }

        ExampleSection(
            useCardContainer = false,
            title = "自定义过滤",
            description = "按代码或名称匹配，例如输入 usd"
        ) {
            ComboBox(
                query = currency,
                onQueryChange = { currency = it },
                options = CURRENCIES,
                onSelect = { currency = it.label },
                label = "币种",
                placeholder = "输入币种或代码",
                filter = { option, text ->
                    text.isBlank() ||
                        option.label.contains(text, ignoreCase = true) ||
                        option.value.contains(text, ignoreCase = true)
                },
            )
        }

        ExampleSection(
            useCardContainer = false,
            title = "禁用",
            description = "enabled = false"
        ) {
            ComboBox(
                query = "北京",
                onQueryChange = {},
                options = CITIES,
                onSelect = {},
                label = "城市",
                enabled = false,
            )
        }
    }
}
