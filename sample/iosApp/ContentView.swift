import SwiftUI

struct ContentView: View {
    var body: some View {
        KuiklyRenderViewPage(pageName: "MainDemo", data: launchParams())
            .edgesIgnoringSafeArea(.all)
    }
}

/// Automation: `xcrun simctl launch <udid> <bundle> -route switch -theme dark -lang en-US`
/// opens a page directly. Launch arguments in `-key value` form land in UserDefaults.
private func launchParams() -> [String: Any] {
    var params: [String: Any] = [:]
    for key in ["route", "theme", "lang"] {
        if let value = UserDefaults.standard.string(forKey: key) { params[key] = value }
    }
    return params
}
