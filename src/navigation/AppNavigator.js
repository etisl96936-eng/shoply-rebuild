// src/navigation/AppNavigator.js
import { NavigationContainer } from '@react-navigation/native';
import React from 'react';
import { useAuth } from '../context/AuthContext';
import AuthNavigator from './AuthNavigator';
import DrawerNavigator from './DrawerNavigator';

const AppNavigator = () => {
  const { isAuthenticated } = useAuth();

  return (
    <NavigationContainer>
      {isAuthenticated ? <DrawerNavigator /> : <AuthNavigator />}
    </NavigationContainer>
  );
};

export default AppNavigator;