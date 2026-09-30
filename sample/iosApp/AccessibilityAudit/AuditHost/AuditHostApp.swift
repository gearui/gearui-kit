import SwiftUI

/// Required host for the UI test bundle; the tests drive the GearUI sample instead.
@main
struct AuditHostApp: App {
    var body: some Scene { WindowGroup { Text("GearUI accessibility audit host") } }
}
