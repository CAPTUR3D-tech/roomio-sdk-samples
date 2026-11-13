# Roomio SDK — UIKit sample

A UIKit app that scans a space with the Roomio SDK and shares the resulting bundle.

The code is in [`ios-uikit`](https://github.com/CAPTUR3D-tech/roomio-sdk-samples/tree/main/ios-uikit) in the samples repository.

## Requirements

- Xcode 26 or later
- An iPhone running iOS 17 or later. Scanning needs a real device; the Simulator can build the app but cannot scan.

## Setup

The app depends on the `RoomioSDK` Swift package from `https://github.com/CAPTUR3D-tech/roomio-sdk-ios` at version 0.0.15.

1. Open `RoomioUIKit.xcodeproj`. Xcode downloads the package.
2. Select your team under **Signing & Capabilities**.
3. Replace `pk_test_replace_me` in `AppDelegate.swift` with your publishable key.
4. Run on your iPhone.

## What it shows

| Step | API |
| --- | --- |
| Configure the SDK | `Roomio.configure(publishableKey:)` |
| Check the device | `Roomio.isSupported()` |
| Start a session | `Roomio.startScan()` |
| Show the camera | `RoomioScanView(session:)` in a `UIHostingController` |
| Show onboarding | Shown by the scan view before the scan; controls hide while `session.isShowingOnboarding` is true |
| Show sheets and dialogs | Shown by the scan view before and during the scan; the sample's message hides while `session.isShowingDialog` is true |
| Drive the scan | `start()` when the scan screen appears; the scan view's own buttons complete or exit the scan |
| Follow progress | `session.state` with `withObservationTracking` (`finished` or `cancelled` when the scan ends) |
| Get the result | `export()` returns a `RoomioBundle` |
| Send diagnostics to Roomio support | `Roomio.exportDiagnostics()` returns an encrypted file to share |
| Clean up | `close()` |

`HomeViewController.swift` starts a scan; the SDK asks for camera access on the scan screen. `ScanViewController.swift` is the scanning screen.
