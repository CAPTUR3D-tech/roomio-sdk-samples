# Roomio SDK — UIKit sample

A UIKit app that scans a room with the Roomio SDK and shares the resulting bundle.

## Requirements

- Xcode 26 or later
- An iPhone running iOS 17 or later. Scanning needs a real device; the Simulator can build the app but cannot scan.

## Setup

The app depends on the `RoomioSDK` Swift package from `https://github.com/CAPTUR3D-tech/roomio-sdk-ios-test` at version 0.0.7. The repository is private, so add a GitHub account with access to it under **Xcode → Settings → Accounts**.

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
| Drive the scan | `startPreview()`, `start()`; the scan view's own buttons complete or exit the scan |
| Follow progress | `session.state` with `withObservationTracking` (`finished` or `cancelled` when the scan ends) |
| Get the result | `export()` returns a `RoomioBundle` |
| Send diagnostics to Roomio support | `Roomio.exportDiagnostics()` returns an encrypted file to share |
| Clean up | `close()` |

`HomeViewController.swift` starts a scan; the SDK asks for camera access on the scan screen. `ScanViewController.swift` is the scanning screen.
