# Roomio SDK — React Native sample

A React Native app that scans a room with the Roomio SDK on its own scanning screen and shares the resulting bundle.

## Requirements

- Node 22.11 or later
- iOS: Xcode 26 or later, CocoaPods, and an iPhone running iOS 17 or later
- Android: an ARCore-supported phone running Android 7.0 (API 24) or later, with a 64-bit (arm64) processor
- Scanning needs a real device. The Simulator and emulator cannot scan.

## Setup

The app depends on `@roomio/react-native-sdk` 0.0.7 from `https://cdn.roomio.io/sdk/react-native/0.0.7/roomio-react-native-sdk-0.0.7.tgz`, declared in `package.json`. Its Android SDK comes from `https://cdn.roomio.io/sdk/android/maven`.

1. Install this app's packages, from this folder:

   ```
   npm install
   ```

2. Replace `pk_test_replace_me` in `App.tsx` with your publishable key.

### iOS

```
cd ios && bundle install && bundle exec pod install && cd ..
npx react-native run-ios --device
```

Select your team under **Signing & Capabilities** in `ios/RoomioExample.xcworkspace` if Xcode asks.

### Android

```
npx react-native run-android
```

## What it shows

| Step | API |
| --- | --- |
| Configure the SDK | `Roomio.configure(publishableKey)` |
| Check the device | `Roomio.isSupported()` |
| Start a session | `Roomio.startScan()` |
| Show the camera | `<RoomioScanView session={session} />` |
| Show onboarding | Shown by the scan view before the scan; controls hide while `isShowingOnboarding` is true |
| Drive the scan | `startPreview()`, `start()`; the scan view's own buttons complete or exit the scan |
| Follow progress | `useRoomioScanSession(session)` for the state (`finished` or `cancelled` when the scan ends) |
| Get the result | `export()` resolves with a `RoomioBundle` |
| Send diagnostics to Roomio support | `Roomio.exportDiagnostics()` resolves with an encrypted file to share |
| Clean up | `close()` |

`App.tsx` starts a scan and shares the results. `ScanScreen.tsx` is the scanning screen.

## Native setup

These are the changes this app makes to the React Native template, which your app needs too:

| Setting | Where |
| --- | --- |
| Camera usage text (`NSCameraUsageDescription`) | `ios/RoomioExample/Info.plist` |
| iOS 17 deployment target | `ios/Podfile` and the Xcode project |
| arm64 only (`reactNativeArchitectures=arm64-v8a`) | `android/gradle.properties` |
| Maven repository for the Android SDK (`https://cdn.roomio.io/sdk/android/maven`) | `android/build.gradle` |
