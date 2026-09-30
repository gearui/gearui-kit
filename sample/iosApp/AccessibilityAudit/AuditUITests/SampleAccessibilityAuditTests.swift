import XCTest

/// Runs Apple's accessibility audit on every sample page, light and dark.
///
/// Routes come from `TEST_RUNNER_GEARUI_ROUTES` (comma separated), which
/// scripts/acceptance/ios_accessibility_audit.sh fills from the component registry.
/// Every issue is printed as one `AUDIT|route|theme|type|element|description` line
/// and the test fails at the end if any page had one, so a single run reports all.
/// Dynamic Type is audited separately (gate D3) and excluded here.
final class SampleAccessibilityAuditTests: XCTestCase {
    func testEveryPage() throws {
        let routes = (ProcessInfo.processInfo.environment["GEARUI_ROUTES"] ?? "")
            .split(separator: ",").map(String.init).filter { !$0.isEmpty }
        XCTAssertFalse(routes.isEmpty, "no routes given")
        let themes = (ProcessInfo.processInfo.environment["GEARUI_THEMES"] ?? "light,dark")
            .split(separator: ",").map(String.init)
        var failures = 0
        for route in routes {
            for theme in themes {
                let app = XCUIApplication(bundleIdentifier: "com.gearui.kit.sample")
                app.launchArguments = ["-route", route, "-theme", theme]
                app.launch()
                _ = app.wait(for: .runningForeground, timeout: 10)
                sleep(2)
                var issues = 0
                try app.performAccessibilityAudit(for: XCUIAccessibilityAuditType.all.subtracting(.dynamicType)) { issue in
                    let element = issue.element.map { "\($0.elementType.rawValue):\($0.label)" } ?? "-"
                    print("AUDIT|\(route)|\(theme)|\(issue.auditType.rawValue)|\(element)|\(issue.compactDescription)")
                    issues += 1
                    return true // collect, do not fail per issue
                }
                print("AUDITPAGE|\(route)|\(theme)|\(issues)")
                failures += issues
                app.terminate()
            }
        }
        XCTAssertEqual(failures, 0, "accessibility issues found; see AUDIT lines")
    }
}
