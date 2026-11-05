module.exports = {
  name: 'Roomio Expo Sample',
  slug: 'roomio-expo-sample',
  version: '1.0.0',
  orientation: 'portrait',
  icon: './assets/icon.png',
  ios: {
    bundleIdentifier: 'io.captur3d.roomio.sample.expo',
    deploymentTarget: '17.0',
  },
  android: {
    package: 'io.captur3d.roomio.sample.expo',
    adaptiveIcon: {
      backgroundColor: '#E6F4FE',
      foregroundImage: './assets/android-icon-foreground.png',
      backgroundImage: './assets/android-icon-background.png',
      monochromeImage: './assets/android-icon-monochrome.png',
    },
  },
  plugins: [
    '@roomio/react-native-sdk',
    'expo-sharing',
    [
      'expo-build-properties',
      {
        android: {
          buildArchs: ['arm64-v8a'],
        },
      },
    ],
  ],
};
