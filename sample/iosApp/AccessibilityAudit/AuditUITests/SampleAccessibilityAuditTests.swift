import XCTest

/// Runs Apple's accessibility audit on every sample page, light and dark.
///
/// Pages come from `TEST_RUNNER_GEARUI_ROUTES` as comma-separated `route=Title|Heading`
/// entries (the NavBar's English name, or the Chinese heading of a page without one),
/// which scripts/acceptance/ios_accessibility_audit.sh fills from the component
/// registry. A page is audited only once its NavBar title is on screen; otherwise it
/// is reported as `AUDITPAGE|route|theme|NOT_READY` and the run fails.
/// Every issue is printed as one `AUDIT|route|theme|type|element|description` line
/// and the test fails at the end if any page had one, so a single run reports all.
/// Dynamic Type is audited separately (gate D3) and excluded here.
final class SampleAccessibilityAuditTests: XCTestCase {
    func testEveryPage() throws {
        let routes: [(String, String)] = (ProcessInfo.processInfo.environment["GEARUI_ROUTES"] ?? "")
            .split(separator: ",").compactMap { pair in
                let parts = pair.split(separator: "=", maxSplits: 1).map(String.init)
                return parts.count == 2 ? (parts[0], parts[1]) : nil
            }
        XCTAssertFalse(routes.isEmpty, "no routes given")
        let themes = (ProcessInfo.processInfo.environment["GEARUI_THEMES"] ?? "light,dark")
            .split(separator: ",").map(String.init)
        var failures = 0
        var notReady = 0
        for (route, title) in routes {
            for theme in themes {
                let app = XCUIApplication(bundleIdentifier: "com.gearui.kit.sample")
                app.launchArguments = ["-route", route, "-theme", theme]
                app.launch()
                guard app.wait(for: .runningForeground, timeout: 10),
                      waitForAny(app, title.split(separator: "|").map(String.init), timeout: 20) else {
                    print("AUDITPAGE|\(route)|\(theme)|NOT_READY")
                    notReady += 1
                    app.terminate()
                    continue
                }
                sleep(1) // entry animations
                var issues = 0
                try app.performAccessibilityAudit(for: XCUIAccessibilityAuditType.all.subtracting(.dynamicType)) { issue in
                    let element = issue.element.map { "\($0.elementType.rawValue):\($0.label)@y\(Int($0.frame.minY))" } ?? "-"
                    print("AUDIT|\(route)|\(theme)|\(issue.auditType.rawValue)|\(element)|\(issue.compactDescription)")
                    issues += 1
                    return true // collect, do not fail per issue
                }
                print("AUDITPAGE|\(route)|\(theme)|\(issues)")
                failures += issues
                app.terminate()
            }
        }
        XCTAssertEqual(notReady, 0, "pages that never rendered; see NOT_READY lines")
        XCTAssertEqual(failures, 0, "accessibility issues found; see AUDIT lines")
    }

    private func waitForAny(_ app: XCUIApplication, _ titles: [String], timeout: TimeInterval) -> Bool {
        let end = Date().addingTimeInterval(timeout)
        while Date() < end {
            if titles.contains(where: { app.staticTexts[$0].firstMatch.exists }) { return true }
            usleep(300_000)
        }
        return false
    }
}
