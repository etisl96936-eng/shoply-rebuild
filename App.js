// App.js
import { useEffect } from 'react';
import { I18nManager, Platform, StatusBar } from 'react-native';
import 'react-native-gesture-handler';
import { AuthProvider } from './src/context/AuthContext';
import AppNavigator from './src/navigation/AppNavigator';

export default function App() {
  useEffect(() => {
    // Force RTL for Hebrew support
    if (!I18nManager.isRTL) {
      I18nManager.allowRTL(true);
      I18nManager.forceRTL(true);
      if (Platform.OS !== 'web') {
        // On native, we need to reload
        // Updates.reloadAsync();
      }
    }
  }, []);

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <AuthProvider>
        <AppNavigator />
      </AuthProvider>
    </>
  );
}