package com.gearui.sample.examples.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.gearui.components.button.Button
import com.gearui.components.button.ButtonSize
import com.gearui.components.button.ButtonTheme
import com.gearui.components.button.ButtonType
import com.gearui.components.icon.Icons
import com.gearui.components.toast.Toast
import com.gearui.foundation.primitives.Icon
import com.gearui.foundation.primitives.Text
import com.gearui.foundation.typography.Typography
import com.gearui.sample.config.ComponentInfo
import com.gearui.sample.pages.ExamplePage
import com.gearui.sample.pages.ExampleSection
import com.gearui.theme.Theme
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp

private enum class UploadState { NORMAL, LOADING, RETRY, ERROR }

private data class UploadFileDemo(
    val id: Int,
    val name: String,
    val state: UploadState
)

@Composable
fun UploadExample(
    component: ComponentInfo,
    onBack: () -> Unit
) {
    val files = remember {
        mutableStateListOf(
            UploadFileDemo(1, "example-1.png", UploadState.NORMAL),
            UploadFileDemo(2, "example-2.png", UploadState.NORMAL)
        )
    }
    val loadingFiles = remember {
        mutableStateListOf(
            UploadFileDemo(3, "uploading-a.png", UploadState.LOADING),
            UploadFileDemo(4, "uploading-b.png", UploadState.LOADING)
        )
    }
    val retryFiles = remember {
        mutableStateListOf(
            UploadFileDemo(5, "retry.png", UploadState.RETRY)
        )
    }
    val errorFiles = remember {
        mutableStateListOf(
            UploadFileDemo(6, "error.png", UploadState.ERROR)
        )
    }

    ExamplePage(component = component, onBack = onBack) {
        ExampleSection(title = "组件类型", description = "单选上传、替换上传、多选上传、自定义上传按钮") {
            Button(
                text = "单选上传（演示）",
                type = ButtonType.OUTLINE,
                block = true,
                onClick = {
                    files.clear()
                    files.add(UploadFileDemo(7, "single-picked.png", UploadState.NORMAL))
                    Toast.show("已选择 1 张图片")
                }
            )
            Button(
                text = "单选上传（替换）",
                type = ButtonType.OUTLINE,
                block = true,
                onClick = {
                    if (files.isEmpty()) files.add(UploadFileDemo(8, "replace.png", UploadState.NORMAL))
                    else files[0] = files[0].copy(name = "replace-${files[0].id}.png")
                    Toast.show("已替换首张图片")
                }
            )
            Button(
                text = "多选上传（追加）",
                type = ButtonType.OUTLINE,
                block = true,
                onClick = {
                    val nextId = (files.maxOfOrNull { it.id } ?: 0) + 1
                    files.add(UploadFileDemo(nextId, "multi-$nextId.png", UploadState.NORMAL))
                }
            )
            Button(
                text = "自定义上传按钮事件",
                type = ButtonType.OUTLINE,
                block = true,
                onClick = {
                    val nextId = (files.maxOfOrNull { it.id } ?: 0) + 1
                    files.add(UploadFileDemo(nextId, "custom-$nextId.png", UploadState.NORMAL))
                    Toast.show("触发自定义上传逻辑")
                }
            )
            UploadList(files = files)
        }

        ExampleSection(title = "组件状态", description = "加载状态、重新上传、上传失败") {
            UploadList(files = loadingFiles)
            UploadList(files = retryFiles)
            UploadList(files = errorFiles)
        }
    }
}

@Composable
private fun UploadList(files: List<UploadFileDemo>) {
    if (files.isEmpty()) return
    val colors = Theme.colors

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        files.forEach { file ->
            val statusText = when (file.state) {
                UploadState.NORMAL -> "已上传"
                UploadState.LOADING -> "上传中..."
                UploadState.RETRY -> "重新上传"
                UploadState.ERROR -> "上传失败"
            }
            val statusColor = when (file.state) {
                UploadState.NORMAL -> colors.success
                UploadState.LOADING -> colors.warning
                UploadState.RETRY -> colors.primary
                UploadState.ERROR -> colors.danger
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(8.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(name = Icons.image, size = 16.dp, tint = colors.textSecondary)
                Text(text = file.name, style = Typography.BodySmall, color = colors.textPrimary, modifier = Modifier.weight(1f))
                Text(text = statusText, style = Typography.BodySmall, color = statusColor)
            }
        }
    }
}
