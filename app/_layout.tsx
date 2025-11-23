// app/_layout.tsx
import React from 'react';
import 'react-native-gesture-handler';
import { AuthProvider } from '../src/context/AuthContext';
import AppNavigator from '../src/navigation/AppNavigator';

export default function RootLayout() {
  return (
    <AuthProvider>
      <AppNavigator />
    </AuthProvider>
  );
}