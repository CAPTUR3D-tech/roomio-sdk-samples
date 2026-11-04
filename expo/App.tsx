import { useEffect, useState } from 'react';
import { Button, StyleSheet, Text, View } from 'react-native';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import { File, Paths } from 'expo-file-system';
import { shareAsync } from 'expo-sharing';
import { Roomio, type RoomioBundle, type RoomioScanSession } from '@roomio/react-native-sdk';
import { ScanScreen } from './ScanScreen';

Roomio.configure('pk_test_replace_me');

async function share(path: string) {
  const copy = new File(Paths.cache, path.split('/').pop()!);
  await new File(`file://${path}`).copy(copy, { overwrite: true });
  await shareAsync(copy.uri);
}

export default function App() {
  const [session, setSession] = useState<RoomioScanSession | null>(null);
  const [bundle, setBundle] = useState<RoomioBundle | null>(null);
  const [supported, setSupported] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    Roomio.isSupported().then((support) => setSupported(support !== 'unsupported'));
  }, []);

  async function run(action: () => Promise<void>) {
    setErrorMessage(null);
    try {
      await action();
    } catch (error) {
      setErrorMessage((error as Error).message);
    }
  }

  if (session) {
    return (
      <SafeAreaProvider>
        <ScanScreen
          session={session}
          onDone={(result) => {
            setBundle((last) => result ?? last);
            setSession(null);
          }}
        />
      </SafeAreaProvider>
    );
  }

  return (
    <SafeAreaProvider>
      <SafeAreaView style={styles.home}>
        <View style={styles.content}>
          <Text style={styles.title}>Roomio Sample</Text>
          {bundle && (
            <>
              <Text>Last scan: {(bundle.sizeBytes / 1e6).toFixed(1)} MB</Text>
              <Button title="Share scan" onPress={() => run(() => share(bundle.path))} />
            </>
          )}
          <Button title="Send diagnostics" onPress={() => run(async () => share(await Roomio.exportDiagnostics()))} />
          {errorMessage && <Text style={styles.error}>{errorMessage}</Text>}
        </View>
        {supported ? (
          <Button title="Start scan" onPress={() => run(async () => setSession(await Roomio.startScan()))} />
        ) : (
          <Text>This device can't scan.</Text>
        )}
      </SafeAreaView>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  home: { flex: 1, padding: 24 },
  content: { flex: 1, justifyContent: 'center', alignItems: 'center', gap: 20 },
  title: { fontSize: 32, fontWeight: 'bold' },
  error: { color: 'red', textAlign: 'center' },
});
