package com.gearui.sample.examples.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.gearui.components.toast.Toast
import com.gearui.components.upload.Upload
import com.gearui.components.upload.UploadItem
import com.gearui.components.upload.UploadStatus
import com.gearui.foundation.field.FieldDescription
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun UploadExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    // The host owns picking and transfer; the demo fakes both.
    var items by remember {
        mutableStateOf(
            listOf(
                UploadItem(id = "1", name = "合同.pdf"),
                UploadItem(id = "2", name = "身份证正面.jpg"),
            )
        )
    }
    var next by remember { mutableStateOf(3) }

    LaunchedEffect(items) {
        val uploading = items.filter { it.status == UploadStatus.UPLOADING }
        if (uploading.isEmpty()) return@LaunchedEffect
        delay(300)
        items = items.map { item ->
            if (item.status != UploadStatus.UPLOADING) return@map item
            val progress = item.progress + 0.25f
            when {
                progress >= 1f && item.id.toIntOrNull()?.rem(4) == 0 ->
                    item.copy(status = UploadStatus.FAILED, progress = 1f)
                progress >= 1f -> item.copy(status = UploadStatus.DONE, progress = 1f)
                else -> item.copy(progress = progress)
            }
        }
    }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(
            useCardContainer = false,
            title = "附件列表",
            description = "点 + 添加，右上角移除；每 4 个会失败一次，点失败的瓦片重试"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Upload(
                    items = items,
                    onAdd = {
                        items = items + UploadItem(
                            id = next.toString(),
                            name = "文件-$next",
                            status = UploadStatus.UPLOADING,
                            progress = 0f,
                        )
                        next += 1
                    },
                    onRemove = { item -> items = items.filterNot { it.id == item.id } },
                    onRetry = { item ->
                        items = items.map { if (it.id == item.id) it.copy(status = UploadStatus.UPLOADING, progress = 0f) else it }
                    },
                    onPreview = { Toast.show("预览 ${it.name}") },
                    maxCount = 6,
                )
                FieldDescription("已选 ${items.size} / 6")
            }
        }

        ExampleSection(
            useCardContainer = false,
            title = "状态",
            description = "等待、上传中、失败、完成"
        ) {
            Upload(
                items = listOf(
                    UploadItem(id = "p", name = "排队中", status = UploadStatus.PENDING),
                    UploadItem(id = "u", name = "上传中", status = UploadStatus.UPLOADING, progress = 0.6f),
                    UploadItem(id = "f", name = "失败", status = UploadStatus.FAILED),
                    UploadItem(id = "d", name = "已完成"),
                ),
                onAdd = {},
                maxCount = 4,
            )
        }

        ExampleSection(
            useCardContainer = false,
            title = "只读",
            description = "enabled = false，不显示移除按钮"
        ) {
            Upload(
                items = listOf(UploadItem(id = "r1", name = "回执.png"), UploadItem(id = "r2", name = "发票.pdf")),
                onAdd = {},
                onRemove = {},
                enabled = false,
                maxCount = 2,
            )
        }
    }
}
