# Roomio SDK — Android Views sample

An Android app built with Activities and XML layouts that scans a space with the Roomio SDK and shares the resulting bundle.

The code is in [`android-views`](https://github.com/CAPTUR3D-tech/roomio-sdk-samples/tree/main/android-views) in the samples repository.

## Requirements

- Android Studio Koala or later
- An ARCore-supported Android phone running Android 7.0 (API 24) or later, with a 64-bit (arm64) processor. The emulator cannot scan.

## Setup

The app depends on `io.captur3d:roomio-sdk:0.0.15`, declared in `app/build.gradle.kts`. It comes from the Maven repository at `https://cdn.roomio.io/sdk/android/maven`, declared in `settings.gradle.kts`.

1. Open this folder in Android Studio.
2. Replace `pk_test_replace_me` in `SampleApplication.kt` with your publishable key.
3. Run on your phone.

## What it shows

| Step | API |
| --- | --- |
| Configure the SDK | `Roomio.configure(context, publishableKey)` |
| Check the device | `Roomio.isSupported(context)` |
| Start a session | `Roomio.startScan(activity)` |
| Show the camera | `RoomioScanView` in `activity_scan.xml`, with `bind(session)` |
| Show onboarding | Shown by the scan view before the scan; controls hide while `session.isShowingOnboarding` is true |
| Show sheets and dialogs | Shown by the scan view before and during the scan; the sample's message hides while `session.isShowingDialog` is true |
| Drive the scan | `start()`; the scan view's own buttons complete or exit the scan |
| Follow progress | `session.state` (`finished` or `cancelled` when the scan ends), collected with `repeatOnLifecycle` |
| Get the result | `export()` returns a `RoomioBundle` |
| Send diagnostics to Roomio support | `Roomio.exportDiagnostics(context)` returns an encrypted file to share |
| Clean up | `unbind(session)`, `close()` |

`HomeActivity.kt` starts a scan; the SDK asks for camera access on the scan screen. `ScanActivity.kt` is the scanning screen.
