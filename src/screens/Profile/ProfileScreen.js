// src/screens/Profile/ProfileScreen.js
import React from 'react';
import { Alert, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useAuth } from '../../context/AuthContext';
import Theme from '../../theme';

const ProfileScreen = () => {
  const { user, logout } = useAuth();

  const handleLogout = () => {
    Alert.alert(
      'התנתקות',
      'האם אתה בטוח שברצונך להתנתק?',
      [
        {
          text: 'ביטול',
          style: 'cancel',
        },
        {
          text: 'התנתק',
          onPress: logout,
          style: 'destructive',
        },
      ]
    );
  };

  // פרטי פרופיל לדוגמה - יתמלאו מהשרת בעתיד
  const profileSections = [
    {
      title: 'פרטים אישיים',
      items: [
        { label: 'שם מלא', value: user?.name || 'לא הוגדר' },
        { label: 'אימייל', value: user?.email || 'לא הוגדר' },
        { label: 'טלפון', value: 'לא הוגדר' },
      ],
    },
    {
      title: 'העדפות',
      items: [
        { label: 'רשתות מועדפות', value: 'לא נבחרו' },
        { label: 'אנשי קשר מועדפים', value: 'לא נבחרו' },
      ],
    },
  ];

  return (
    <ScrollView style={styles.container}>
      <View style={styles.content}>
        {/* Header with Avatar */}
        <View style={styles.header}>
          <View style={styles.avatarContainer}>
            <Text style={styles.avatarText}>
              {user?.name ? user.name.charAt(0).toUpperCase() : '?'}
            </Text>
          </View>
          <Text style={styles.userName}>{user?.name || 'משתמש'}</Text>
          <Text style={styles.userEmail}>{user?.email || ''}</Text>
        </View>

        {/* Profile Sections */}
        {profileSections.map((section, index) => (
          <View key={index} style={styles.section}>
            <Text style={styles.sectionTitle}>{section.title}</Text>
            <View style={styles.card}>
              {section.items.map((item, itemIndex) => (
                <View key={itemIndex}>
                  <View style={styles.infoRow}>
                    <Text style={styles.infoLabel}>{item.label}</Text>
                    <Text style={styles.infoValue}>{item.value}</Text>
                  </View>
                  {itemIndex < section.items.length - 1 && <View style={styles.divider} />}
                </View>
              ))}
            </View>
          </View>
        ))}

        {/* Action Buttons */}
        <View style={styles.actionsSection}>
          <TouchableOpacity style={styles.editButton}>
            <Text style={styles.editButtonText}>עריכת פרופיל</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
            <Text style={styles.logoutButtonText}>התנתק מהמערכת</Text>
          </TouchableOpacity>
        </View>

        {/* App Info */}
        <View style={styles.appInfo}>
          <Text style={styles.appInfoText}>Shoply v1.0.0</Text>
          <Text style={styles.appInfoText}>Smart Shopping Manager</Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    ...Theme.commonStyles.container,
  },
  content: {
    ...Theme.commonStyles.screenPadding,
  },
  header: {
    alignItems: 'center',
    paddingVertical: Theme.spacing.xxl,
  },
  avatarContainer: {
    width: 80,
    height: 80,
    borderRadius: Theme.spacing.borderRadius.round,
    backgroundColor: Theme.colors.primary.main,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: Theme.spacing.md,
    ...Theme.shadows.md,
  },
  avatarText: {
    ...Theme.typography.heading1,
    color: Theme.colors.text.white,
  },
  userName: {
    ...Theme.typography.heading2,
    color: Theme.colors.text.primary,
    marginBottom: Theme.spacing.xs,
  },
  userEmail: {
    ...Theme.typography.body1,
    color: Theme.colors.text.secondary,
  },
  section: {
    marginBottom: Theme.spacing.xl,
  },
  sectionTitle: {
    ...Theme.typography.subtitle1,
    color: Theme.colors.text.primary,
    marginBottom: Theme.spacing.md,
    fontWeight: Theme.typography.fontWeight.semibold,
  },
  card: {
    ...Theme.commonStyles.card,
  },
  infoRow: {
    ...Theme.commonStyles.rowSpaceBetween,
    paddingVertical: Theme.spacing.sm,
  },
  infoLabel: {
    ...Theme.typography.body1,
    color: Theme.colors.text.secondary,
  },
  infoValue: {
    ...Theme.typography.body1,
    color: Theme.colors.text.primary,
    fontWeight: Theme.typography.fontWeight.medium,
  },
  divider: {
    height: 1,
    backgroundColor: Theme.colors.border.light,
    marginVertical: Theme.spacing.xs,
  },
  actionsSection: {
    marginTop: Theme.spacing.lg,
    marginBottom: Theme.spacing.xxl,
  },
  editButton: {
    ...Theme.commonStyles.buttonPrimary,
    marginBottom: Theme.spacing.md,
  },
  editButtonText: {
    ...Theme.commonStyles.buttonText,
  },
  logoutButton: {
    ...Theme.commonStyles.buttonOutline,
    borderColor: Theme.colors.status.error,
  },
  logoutButtonText: {
    ...Theme.commonStyles.buttonTextOutline,
    color: Theme.colors.status.error,
  },
  appInfo: {
    alignItems: 'center',
    paddingVertical: Theme.spacing.xl,
    marginTop: Theme.spacing.lg,
  },
  appInfoText: {
    ...Theme.typography.caption,
    color: Theme.colors.text.hint,
  },
});

export default ProfileScreen;