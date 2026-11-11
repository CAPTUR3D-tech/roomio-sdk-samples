import { useEffect, useState } from 'react';
import { BackHandler, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import {
  RoomioScanView,
  useRoomioScanSession,
  type RoomioBundle,
  type RoomioScanSession,
} from '@roomio/react-native-sdk';

type Props = {
  session: RoomioScanSession;
  onDone: (bundle: RoomioBundle | null) => void;
};

export function ScanScreen({ session, onDone }: Props) {
  const { state, isShowingOnboarding } = useRoomioScanSession(session);
  const [message, setMessage] = useState<string | null>(null);

  function close(bundle: RoomioBundle | null) {
    session.close();
    onDone(bundle);
  }

  useEffect(() => {
    session.start();
  }, [session]);

  useEffect(() => {
    const subscription = BackHandler.addEventListener('hardwareBackPress', () => {
      close(null);
      return true;
    });
    return () => subscription.remove();
  }, [session]);

  useEffect(() => {
    if (state.type === 'cancelled') close(null);
    if (state.type !== 'finished') return;
    session.export().then(close, (error) => setMessage(error.message));
  }, [state.type, session]);

  const failureMessage = message ?? (state.type === 'failed' ? state.error.message : null);

  return (
    <View style={styles.screen}>
      <RoomioScanView session={session} style={StyleSheet.absoluteFill} />
      {!isShowingOnboarding && (
        <SafeAreaView style={styles.overlay} pointerEvents="box-none">
          <View style={styles.middle} pointerEvents="box-none">
            {failureMessage && <Text style={styles.panel}>{failureMessage}</Text>}
          </View>
        </SafeAreaView>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: 'black' },
  overlay: { flex: 1, padding: 16 },
  middle: { flex: 1, justifyContent: 'center' },
  panel: { backgroundColor: 'rgba(255, 255, 255, 0.85)', borderRadius: 12, padding: 12 },
});
