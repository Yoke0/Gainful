import SwiftUI
import WidgetKit
import Shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    private var observer: NSObjectProtocol?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        registerReloadObserver()
        return true
    }

    private func registerReloadObserver() {
        observer = NotificationCenter.default.addObserver(
            forName: NSNotification.Name("com.yoke.gainful.widgetReload"),
            object: nil,
            queue: .main
        ) { _ in
            WidgetCenter.shared.reloadAllTimelines()
        }
    }
}
