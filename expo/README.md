# Roomio SDK — Expo sample

An Expo app that scans a space with the Roomio SDK on its own scanning screen and shares the resulting bundle.

The code is in [`expo`](https://github.com/CAPTUR3D-tech/roomio-sdk-samples/tree/main/expo) in the samples repository.

The SDK has native code, so the app runs as a development build. Expo Go cannot run it.

## Requirements

- Node 22.13 or later
- iOS: Xcode 26.4 or later, CocoaPods, and an iPhone running iOS 17 or later
- Android: an ARCore-supported phone running Android 7.0 (API 24) or later, with a 64-bit (arm64) processor
- Scanning needs a real device. The Simulator and emulator cannot scan.

## Setup

The app depends on `@roomio/react-native-sdk` 0.0.15 from `https://cdn.roomio.io/sdk/react-native/0.0.15/roomio-react-native-sdk-0.0.15.tgz`, declared in `package.json`. Its Android SDK comes from `https://cdn.roomio.io/sdk/android/maven`.

1. Install this app's packages at the versions it was tested with, from this folder:

   ```
   npm ci
   ```

2. Replace `pk_test_replace_me` in `App.tsx` with your publishable key.
3. Run on your phone:

   ```
   npx expo run:ios --device
   npx expo run:android --device
   ```

## What it shows

| Step | API |
| --- | --- |
| Configure the SDK | `Roomio.configure(publishableKey)` |
| Check the device | `Roomio.isSupported()` |
| Start a session | `Roomio.startScan()` |
| Show the camera | `<RoomioScanView session={session} />` |
| Show onboarding | Shown by the scan view before the scan; controls hide while `isShowingOnboarding` is true |
| Show sheets and dialogs | Shown by the scan view before and during the scan; the sample's message hides while `isShowingDialog` is true |
| Drive the scan | `start()`; the scan view's own buttons complete or exit the scan |
| Follow progress | `useRoomioScanSession(session)` for the state (`finished` or `cancelled` when the scan ends) |
| Get the result | `export()` resolves with a `RoomioBundle` |
| Send diagnostics to Roomio support | `Roomio.exportDiagnostics()` resolves with an encrypted file to share |
| Clean up | `close()` |

`App.tsx` starts a scan and shares the results. `ScanScreen.tsx` is the scanning screen.

## App config

`app.config.js` sets up the SDK:

| Setting | Why |
| --- | --- |
| `ios.deploymentTarget: "17.0"` | The SDK needs iOS 17 |
| `@roomio/react-native-sdk` plugin | Adds the camera usage text and the Maven repository on cdn.roomio.io that hosts the Android SDK. Pass `{ "cameraPermission": "…" }` to change the text |
| `expo-build-properties` `buildArchs: ["arm64-v8a"]` | The Android SDK is arm64 only |
