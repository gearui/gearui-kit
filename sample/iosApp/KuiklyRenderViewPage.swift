import SwiftUI

/**
 * SwiftUI 包装器，用于显示 KuiklyUI 页面
 */
struct KuiklyRenderViewPage: UIViewControllerRepresentable {
    var pageName: String
    var data: Dictionary<String, Any> = [:]

    func makeUIViewController(context: Context) -> UINavigationController {
        let renderVC = KuiklyRenderViewController(pageName: pageName, pageData: data)
        let navController = UINavigationController(rootViewController: renderVC)
        navController.setNavigationBarHidden(true, animated: false)
        return navController
    }

    func updateUIViewController(_ uiViewController: UINavigationController, context: Context) {
        // 不需要更新
    }
}
