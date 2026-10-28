package com.tencent.kuikly.h5app.module

import com.tencent.kuikly.core.render.web.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.web.ktx.KuiklyRenderCallback

/**
 * Receives the page's answer to Back: `backHandle` says whether the page consumed it.
 *
 * Kuikly's Web renderer ships no such module, so the first Back sent to a page
 * (Escape, see KeyboardAccess) failed with "module not found". On a phone an
 * unconsumed Back leaves the app; Escape in a browser has nothing to fall back to,
 * so the answer is taken and ignored.
 */
class KRBackPressModule : KuiklyRenderBaseModule() {
    override fun call(method: String, params: String?, callback: KuiklyRenderCallback?): Any? =
        when (method) {
            BACK_HANDLE -> null
            else -> super.call(method, params, callback)
        }

    companion object {
        const val MODULE_NAME = "KRBackPressModule"
        private const val BACK_HANDLE = "backHandle"
    }
}
