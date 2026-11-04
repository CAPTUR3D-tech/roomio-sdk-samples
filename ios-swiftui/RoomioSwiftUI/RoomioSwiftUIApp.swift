import RoomioSDK
import SwiftUI

@main
struct RoomioSwiftUIApp: App {
    init() {
        Roomio.configure(publishableKey: "pk_test_replace_me")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
