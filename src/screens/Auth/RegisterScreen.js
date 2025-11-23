// src/screens/Auth/RegisterScreen.js
import React, { useState } from 'react';
import { Alert, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { useAuth } from '../../context/AuthContext';
import Theme from '../../theme';

const RegisterScreen = ({ navigation }) => {
  const { register } = useAuth();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleRegister = () => {
    // בדיקות תקינות
    if (!name.trim()) {
      Alert.alert('שגיאה', 'נא להזין שם');
      return;
    }
    if (!email.trim()) {
      Alert.alert('שגיאה', 'נא להזין אימייל');
      return;
    }
    if (!password.trim()) {
      Alert.alert('שגיאה', 'נא להזין סיסמה');
      return;
    }
    if (password.length < 6) {
      Alert.alert('שגיאה', 'הסיסמה חייבת להכיל לפחות 6 תווים');
      return;
    }
    if (password !== confirmPassword) {
      Alert.alert('שגיאה', 'הסיסמאות אינן תואמות');
      return;
    }

    // הרשמה
    register(name, email, password);
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.content}>
        {/* כותרת */}
        <Text style={styles.title}>הרשמה ל-Shoply</Text>
        <Text style={styles.subtitle}>צור חשבון חדש ותתחיל לחסוך בקניות</Text>

        {/* שדות קלט */}
        <View style={styles.inputContainer}>
          <Text style={styles.label}>שם מלא</Text>
          <TextInput
            style={styles.input}
            placeholder="הזן שם מלא"
            value={name}
            onChangeText={setName}
            placeholderTextColor={Theme.colors.text.hint}
          />
        </View>

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
            placeholder="הזן סיסמה (לפחות 6 תווים)"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            placeholderTextColor={Theme.colors.text.hint}
          />
        </View>

        <View style={styles.inputContainer}>
          <Text style={styles.label}>אימות סיסמה</Text>
          <TextInput
            style={styles.input}
            placeholder="הזן סיסמה שוב"
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            secureTextEntry
            placeholderTextColor={Theme.colors.text.hint}
          />
        </View>

        {/* כפתור הרשמה */}
        <TouchableOpacity style={styles.registerButton} onPress={handleRegister}>
          <Text style={styles.registerButtonText}>הירשם</Text>
        </TouchableOpacity>

        {/* קישור להתחברות */}
        <View style={styles.loginContainer}>
          <Text style={styles.loginText}>כבר יש לך חשבון? </Text>
          <TouchableOpacity onPress={() => navigation.navigate('Login')}>
            <Text style={styles.loginLink}>התחבר כאן</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    ...Theme.commonStyles.container,
  },
  contentContainer: {
    flexGrow: 1,
    justifyContent: 'center',
  },
  content: {
    width: '100%',
    paddingHorizontal: Theme.spacing.screenPadding.horizontal,
    paddingVertical: Theme.spacing.screenPadding.vertical,
  },
  title: {
    ...Theme.typography.heading1,
    color: Theme.colors.text.primary,
    textAlign: 'center',
    marginBottom: Theme.spacing.sm,
  },
  subtitle: {
    ...Theme.typography.body1,
    color: Theme.colors.text.secondary,
    textAlign: 'center',
    marginBottom: Theme.spacing.xl,
  },
  inputContainer: {
    marginBottom: Theme.spacing.lg,
  },
  label: {
    ...Theme.typography.body2,
    color: Theme.colors.text.primary,
    marginBottom: Theme.spacing.xs,
    fontWeight: Theme.typography.fontWeight.medium,
  },
  input: {
    ...Theme.commonStyles.input,
  },
  registerButton: {
    ...Theme.commonStyles.buttonPrimary,
    marginTop: Theme.spacing.lg,
  },
  registerButtonText: {
    ...Theme.commonStyles.buttonText,
  },
  loginContainer: {
    ...Theme.commonStyles.row,
    justifyContent: 'center',
    marginTop: Theme.spacing.xl,
  },
  loginText: {
    ...Theme.typography.body2,
    color: Theme.colors.text.secondary,
  },
  loginLink: {
    ...Theme.typography.body2,
    color: Theme.colors.primary.main,
    fontWeight: Theme.typography.fontWeight.semibold,
  },
});

export default RegisterScreen;