import UIKit

/// VoiceOver for GearUI's layers: the host half of the kit's `gearuiLayer` view property
/// (BEHAVIOR_CONTRACT §6).
///
/// Kuikly renders the page under a dialog as ordinary views, so without this VoiceOver
/// swipes straight past the dialog into the page behind it. While a `modal` layer is open
/// everything outside it is hidden from accessibility, VoiceOver moves into it, and when it
/// closes focus goes back to what had it. `hidden` (a page under the top one, an unselected
/// tab) is hidden. `popup` needs nothing here: an anchored panel shuts nothing out.
///
/// Kuikly sets a view property `key` by calling `setCss_<key>:` when the view responds to
/// it, so declaring the setter is the whole registration.
extension UIView {
    @objc func setCss_gearuiLayer(_ value: Any?) {
        AccessibilityLayers.shared.apply(self, kind: (value as? String) ?? "")
    }
}

final class AccessibilityLayers {
    static let shared = AccessibilityLayers()

    private final class Layer {
        weak var view: UIView?
        let kind: String
        weak var opener: NSObject?
        /// Views this layer hid, with whether they were hidden before.
        var hid: [(view: UIView, wasHidden: Bool)] = []
        var opened = false

        init(view: UIView, kind: String, opener: NSObject?) {
            self.view = view
            self.kind = kind
            self.opener = opener
        }
    }

    private var layers: [Layer] = []

    func apply(_ view: UIView, kind: String) {
        if let old = layers.first(where: { $0.view === view }) {
            if old.kind == kind { return }
            close(old)
        }
        guard kind == "modal" || kind == "hidden" else { return }
        let opener = kind == "modal"
            ? UIAccessibility.focusedElement(using: .notificationVoiceOver) as? NSObject
            : nil
        let layer = Layer(view: view, kind: kind, opener: opener)
        layers.append(layer)
        // The property is set before the view is inserted: there is nothing above it to
        // hide yet, and its content is not built.
        DispatchQueue.main.async { [weak self] in self?.open(layer) }
    }

    private func open(_ layer: Layer) {
        pruneRemoved()
        guard !layer.opened, layers.contains(where: { $0 === layer }), let view = layer.view else { return }
        layer.opened = true
        if layer.kind == "hidden" {
            hide(view, for: layer)
            return
        }
        // Everything outside the modal: the siblings of each view from it up to the window.
        var node = view
        while let parent = node.superview {
            for sibling in parent.subviews where sibling !== node {
                hide(sibling, for: layer)
            }
            node = parent
        }
        if UIAccessibility.isVoiceOverRunning {
            DispatchQueue.main.asyncAfter(deadline: .now() + Self.focusDelay) {
                UIAccessibility.post(notification: .screenChanged, argument: view)
            }
        }
    }

    private func hide(_ view: UIView, for layer: Layer) {
        let mine = layer.hid.contains { $0.view === view }
        let others = layers.contains { $0 !== layer && $0.hid.contains { $0.view === view } }
        if mine || others { return }
        layer.hid.append((view, view.accessibilityElementsHidden))
        view.accessibilityElementsHidden = true
    }

    private func close(_ layer: Layer) {
        guard let index = layers.firstIndex(where: { $0 === layer }) else { return }
        layers.remove(at: index)
        for entry in layer.hid.reversed() {
            entry.view.accessibilityElementsHidden = entry.wasHidden
        }
        layer.hid.removeAll()
        guard layer.kind == "modal", UIAccessibility.isVoiceOverRunning else { return }
        let opener = layer.opener
        DispatchQueue.main.asyncAfter(deadline: .now() + Self.focusDelay) {
            // Back to the trigger if it is still on screen; otherwise let VoiceOver choose.
            let target = (opener as? UIView).flatMap { $0.window == nil ? nil : $0 } ?? opener
            UIAccessibility.post(notification: .screenChanged, argument: target)
        }
    }

    /// A layer usually leaves by being removed, which sets no property: release those.
    private func pruneRemoved() {
        for layer in layers where layer.opened && layer.view?.window == nil {
            close(layer)
        }
    }

    private static let focusDelay: TimeInterval = 0.15
}
