// src/screens/Auth/LoginScreen.js
import React, { useState } from 'react';
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View
} from 'react-native';
import { useAuth } from '../../context/AuthContext';
import Theme from '../../theme';

const LoginScreen = ({ navigation }) => {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = () => {
    // בדיקות תקינות
    if (!email.trim()) {
      Alert.alert('שגיאה', 'נא להזין אימייל');
      return;
    }
    if (!password.trim()) {
      Alert.alert('שגיאה', 'נא להזין סיסמה');
      return;
    }

    // התחברות
    login(email, password);
  };

  return (
    <KeyboardAvoidingView 
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <ScrollView 
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
      >
        <View style={styles.content}>
          {/* כותרת */}
          <View style={styles.header}>
            <Text style={styles.title}>ברוכים הבאים ל-Shoply</Text>
            <Text style={styles.subtitle}>התחבר כדי להתחיל לנהל את הקניות שלך</Text>
          </View>

          {/* שדות קלט */}
          <View style={styles.form}>
            <View style={styles.inputContainer}>
              <Text style={styles.label}>אימייל</Text>
              <TextInput
                style={styles.input}
                placeholder="הזן אימייל"
                value={email}
                onChangeText={setEmail}
                keyboardType="email-address"
                autoCapitalize="none"
                placeholderTextColor={Theme.colors.text.hint}
              />
            </View>

            <View style={styles.inputContainer}>
              <Text style={styles.label}>סיסמה</Text>
              <TextInput
                style={styles.input}
                placeholder="הזן סיסמה"
                value={password}
                onChangeText={setPassword}
                secureTextEntry
                placeholderTextColor={Theme.colors.text.hint}
              />
            </View>

            {/* כפתור התחברות */}
            <TouchableOpacity style={styles.loginButton} onPress={handleLogin}>
              <Text style={styles.loginButtonText}>התחבר</Text>
            </TouchableOpacity>

            {/* קישור להרשמה */}
            <View style={styles.registerContainer}>
              <Text style={styles.registerText}>עדיין אין לך חשבון? </Text>
              <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                <Text style={styles.registerLink}>הירשם כאן</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Theme.colors.background.primary,
  },
  scrollContent: {
    flexGrow: 1,
    justifyContent: 'center',
    minHeight: '100%',
  },
  content: {
    width: '100%',
    maxWidth: 400,
    alignSelf: 'center',
    paddingHorizontal: Theme.spacing.xl,
    paddingVertical: Theme.spacing.xxl,
  },
  header: {
    marginBottom: Theme.spacing.xxxl,
    alignItems: 'center',
  },
  title: {
    ...Theme.typography.heading1,
    color: Theme.colors.text.primary,
    textAlign: 'center',
    marginBottom: Theme.spacing.md,
  },
  subtitle: {
    ...Theme.typography.body1,
    color: Theme.colors.text.secondary,
    textAlign: 'center',
    lineHeight: 24,
  },
  form: {
    width: '100%',
  },
  inputContainer: {
    marginBottom: Theme.spacing.lg,
  },
  label: {
    ...Theme.typography.body2,
    color: Theme.colors.text.primary,
    marginBottom: Theme.spacing.sm,
    fontWeight: Theme.typography.fontWeight.medium,
    textAlign: 'right',
  },
  input: {
    ...Theme.commonStyles.input,
    textAlign: 'right',
    fontSize: 16,
    paddingVertical: 14,
    paddingHorizontal: 16,
  },
  loginButton: {
    ...Theme.commonStyles.buttonPrimary,
    marginTop: Theme.spacing.xl,
    paddingVertical: 16,
  },
  loginButtonText: {
    ...Theme.commonStyles.buttonText,
    fontSize: 18,
  },
  registerContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: Theme.spacing.xl,
  },
  registerText: {
    ...Theme.typography.body2,
    color: Theme.colors.text.secondary,
  },
  registerLink: {
    ...Theme.typography.body2,
    color: Theme.colors.primary.main,
    fontWeight: Theme.typography.fontWeight.semibold,
  },
});

export default LoginScreen;