# Roomio SDK samples

Complete apps that scan a space with the Roomio SDK, one per platform. Each folder's `README.md` lists every SDK call it uses; its scanning screen is the reference implementation the docs point to.

To add Roomio to another app, follow https://roomio-sdk.apidocumentation.com/get-started/install-with-ai/index.md. Copy structure from these samples; don't copy their placeholder key.

| Folder | Scanning screen |
| --- | --- |
| `android-compose` | `app/src/main/kotlin/…/ScanScreen.kt` |
| `android-views` | `app/src/main/kotlin/…/ScanActivity.kt`, `app/src/main/res/layout/activity_scan.xml` |
| `ios-swiftui` | `RoomioSwiftUI/ScanView.swift` |
| `ios-uikit` | `RoomioUIKit/ScanViewController.swift` |
| `react-native` | `ScanScreen.tsx` |
| `expo` | `ScanScreen.tsx` |

To run one: replace `pk_test_replace_me` (in the file that calls `configure`) with the user's `roomio_pub_live_` key, and run it on a real phone. Emulators and simulators can't scan. The SDK version in each sample is pinned by Roomio's release pipeline; don't change it.
