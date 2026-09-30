package com.gearui.sample

import android.os.Bundle
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.gearui.runtime.RuntimeInsetsBridge
import com.tencent.kuikly.compose.ui.unit.dp
import com.gearui.sample.adapter.SampleImageAdapter
import com.gearui.sample.adapter.SampleThreadAdapter
import com.tencent.kuikly.core.render.android.adapter.KuiklyRenderAdapterManager
import com.tencent.kuikly.core.render.android.expand.KuiklyRenderViewBaseDelegator
import com.tencent.kuikly.core.render.android.expand.KuiklyRenderViewBaseDelegatorDelegate

/**
 * GearUI-KuiklyUI Sample MainActivity
 */
class MainActivity : AppCompatActivity(), KuiklyRenderViewBaseDelegatorDelegate {

    private lateinit var container: FrameLayout
    private lateinit var kuiklyDelegator: KuiklyRenderViewBaseDelegator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SPEC 4.6 Fullscreen Container Contract: root must be edge-to-edge
        // so KuiklyUI's safeAreaInsets is the single source of top padding.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Create the container
        container = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        setContentView(container)

        // The keyboard inset comes from the host (RuntimeInsetsBridge), as in a real app:
        // Kuikly does not report it, and without it dropdowns open under the keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(container) { _, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            RuntimeInsetsBridge.updateKeyboardHeight((ime / resources.displayMetrics.density).dp)
            insets
        }

        // Register the status bar controller
        StatusBarControllerImpl.register(this)

        if (KuiklyRenderAdapterManager.krImageAdapter == null) {
            KuiklyRenderAdapterManager.krImageAdapter = SampleImageAdapter(applicationContext)
        }
        // Before the first page: Kuikly creates its context thread once, lazily.
        if (KuiklyRenderAdapterManager.krThreadAdapter == null) {
            KuiklyRenderAdapterManager.krThreadAdapter = SampleThreadAdapter()
        }

        // Initialise the KuiklyUI delegate
        kuiklyDelegator = KuiklyRenderViewBaseDelegator(this)

        // Open the MainDemo page by default
        kuiklyDelegator.onAttach(
            container,
            "",
            "MainDemo",  // matches @Page("MainDemo")
            // Automation: `adb shell am start -n … --es route switch --es theme dark`
            listOf("route", "theme", "lang")
                .mapNotNull { key -> intent.getStringExtra(key)?.let { key to it } }
                .toMap()
        )
    }

    override fun onResume() {
        super.onResume()
        kuiklyDelegator.onResume()
    }

    override fun onPause() {
        super.onPause()
        kuiklyDelegator.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        kuiklyDelegator.onDetach()
        StatusBarControllerImpl.unregister()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (kuiklyDelegator.onBackPressed()) {
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
