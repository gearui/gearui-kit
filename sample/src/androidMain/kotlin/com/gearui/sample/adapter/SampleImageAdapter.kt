package com.gearui.sample.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.tencent.kuikly.core.render.android.adapter.HRImageLoadOption
import com.tencent.kuikly.core.render.android.adapter.IKRImageAdapter
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Minimal image adapter for sample app.
 * Supports assets/file/http(s)/base64 for validation pages.
 */
class SampleImageAdapter(
    private val context: Context
) : IKRImageAdapter {

    override fun fetchDrawable(
        imageLoadOption: HRImageLoadOption,
        callback: (drawable: Drawable?) -> Unit
    ) {
        thread(name = "sample-image-loader") {
            callback(loadDrawable(imageLoadOption))
        }
    }

    private fun loadDrawable(option: HRImageLoadOption): Drawable? {
        val bitmap = when {
            option.isBase64() -> decodeBase64(option.src)
            option.isAssets() -> decodeAssets(option.src)
            option.isFile() -> decodeFile(option.src)
            option.isWebUrl() -> decodeHttp(option.src)
            else -> null
        } ?: return null
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun decodeAssets(src: String) =
        runCatching {
            val path = src.removePrefix(HRImageLoadOption.SCHEME_ASSETS)
            context.assets.open(path).use { BitmapFactory.decodeStream(it) }
        }.getOrNull()

    private fun decodeFile(src: String) =
        runCatching {
            val path = src.removePrefix(HRImageLoadOption.SCHEME_FILE)
            BitmapFactory.decodeFile(File(path).absolutePath)
        }.getOrNull()

    private fun decodeHttp(src: String) =
        runCatching {
            val connection = (URL(src).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                doInput = true
                requestMethod = "GET"
                connect()
            }
            connection.inputStream.use { BitmapFactory.decodeStream(it) }.also {
                connection.disconnect()
            }
        }.getOrNull()

    private fun decodeBase64(src: String) =
        runCatching {
            val payload = src.substringAfter("base64,", "")
            val bytes = android.util.Base64.decode(payload, android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
}

