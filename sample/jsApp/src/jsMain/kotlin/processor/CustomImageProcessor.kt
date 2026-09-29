package com.tencent.kuikly.h5app.processor

import com.tencent.kuikly.core.render.web.processor.IImageProcessor
import com.tencent.kuikly.core.render.web.runtime.web.expand.processor.ImageProcessor as BaseImageProcessor
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.url.URL
import kotlinx.browser.document

/**
 * Resolve sample assets relative to the host page, including nested Pages paths.
 */
object CustomImageProcessor : IImageProcessor {
    // Assets image resource prefix, identifies assets resource images
    private const val ASSETS_IMAGE_PREFIX = "assets://"
    override fun getImageAssetsSource(src: String): String {
        return if (src.startsWith(ASSETS_IMAGE_PREFIX)) {
            URL("assets/${src.removePrefix(ASSETS_IMAGE_PREFIX)}", document.baseURI).href
        } else {
            BaseImageProcessor.getImageAssetsSource(src)
        }
    }

    override fun isSVGFilterSupported(): Boolean {
        return BaseImageProcessor.isSVGFilterSupported()
    }

    override fun applyTintColor(imageElement: HTMLImageElement, tintColorValue: String, frameHeight: Double) {
        BaseImageProcessor.applyTintColor(imageElement, tintColorValue, frameHeight)
    }
}
