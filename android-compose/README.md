# Roomio SDK — Jetpack Compose sample

A Compose app that scans a room with the Roomio SDK and shares the resulting bundle.

## Requirements

- Android Studio Koala or later
- An ARCore-supported Android phone running Android 7.0 (API 24) or later, with a 64-bit (arm64) processor. The emulator cannot scan.

## Setup

The app depends on `io.captur3d:roomio-sdk:0.0.7`, declared in `app/build.gradle.kts`. It comes from the Maven repository at `https://cdn.roomio.io/sdk/android/maven`, declared in `settings.gradle.kts`.

1. Open this folder in Android Studio.
2. Replace `pk_test_replace_me` in `MainActivity.kt` with your publishable key.
3. Run on your phone.

## What it shows

| Step | API |
| --- | --- |
| Configure the SDK | `Roomio.configure(publishableKey)` |
| Check the device | `Roomio.isSupported(context)` |
| Start a session | `Roomio.startScan(activity)` |
| Show the camera | `RoomioScanView` in an `AndroidView`, with `bind(session)` |
| Show onboarding | Shown by the scan view before the scan; controls hide while `session.isShowingOnboarding` is true |
| Drive the scan | `start()`, and `pause()` / `resume()` from the lifecycle; the scan view's own buttons complete or exit the scan |
| Follow progress | `session.state` (`finished` or `cancelled` when the scan ends) |
| Get the result | `export()` returns a `RoomioBundle` |
| Send diagnostics to Roomio support | `Roomio.exportDiagnostics(context)` returns an encrypted file to share |
| Clean up | `close()` |

`ContentScreen.kt` starts a scan; the SDK asks for camera access on the scan screen. `ScanScreen.kt` is the scanning screen.
