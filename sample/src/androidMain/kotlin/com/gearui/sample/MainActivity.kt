package com.gearui.sample

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.gearui.sample.adapter.SampleImageAdapter
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

        // 创建容器
        container = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        setContentView(container)

        // 注册状态栏控制器
        StatusBarControllerImpl.register(this)

        if (KuiklyRenderAdapterManager.krImageAdapter == null) {
            KuiklyRenderAdapterManager.krImageAdapter = SampleImageAdapter(applicationContext)
        }

        // 初始化 KuiklyUI 委托
        kuiklyDelegator = KuiklyRenderViewBaseDelegator(this)

        // 默认打开 MainDemo 页面
        kuiklyDelegator.onAttach(
            container,
            "",
            "MainDemo",  // 对应 @Page("MainDemo") 注解
            emptyMap()
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
}
