// src/screens/Home/HomeScreen.js
import React from 'react';
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useAuth } from '../../context/AuthContext';
import Theme from '../../theme';

const HomeScreen = () => {
  const { user } = useAuth();
  const hour = new Date().getHours();
  const greeting = hour < 12 ? 'בוקר טוב' : hour < 18 ? 'צהריים טובים' : 'ערב טוב';

  // רשימת הפיצ'רים
  const features = [
    {
      id: 1,
      title: 'ניהול רשימות חכם',
      description: 'צור והשתף רשימות קניות עם בני משפחה וחברים',
      icon: '📝',
    },
    {
      id: 2,
      title: 'השוואת מחירים',
      description: 'מצא את המחירים הכי זולים בין רשתות השיווק',
      icon: '💰',
    },
    {
      id: 3,
      title: 'דוחות תקציב',
      description: 'עקוב אחר ההוצאות שלך וחסוך כסף',
      icon: '📊',
    },
    {
      id: 4,
      title: 'קטגוריות מוצרים',
      description: 'גלוש בקטגוריות והוסף מוצרים בקלות',
      icon: '🛒',
    },
  ];

  return (
    <ScrollView style={styles.container}>
      <View style={styles.content}>
        {/* Greeting Section */}
        <View style={styles.greetingSection}>
          <Text style={styles.greeting}>{greeting},</Text>
          <Text style={styles.userName}>{user?.name || 'אורח'}!</Text>
          <Text style={styles.welcomeText}>ברוך הבא ל-Shoply - מנהל הקניות החכם שלך</Text>
        </View>

        {/* Features Section */}
        <View style={styles.featuresSection}>
          <Text style={styles.sectionTitle}>מה תוכל לעשות?</Text>
          
          {features.map((feature) => (
            <TouchableOpacity key={feature.id} style={styles.featureCard}>
              <View style={styles.featureIcon}>
                <Text style={styles.iconText}>{feature.icon}</Text>
              </View>
              <View style={styles.featureContent}>
                <Text style={styles.featureTitle}>{feature.title}</Text>
                <Text style={styles.featureDescription}>{feature.description}</Text>
              </View>
            </TouchableOpacity>
          ))}
        </View>

        {/* Quick Actions */}
        <View style={styles.quickActionsSection}>
          <Text style={styles.sectionTitle}>פעולות מהירות</Text>
          
          <TouchableOpacity style={styles.actionButton}>
            <Text style={styles.actionButtonText}>צור רשימה חדשה</Text>
          </TouchableOpacity>
          
          <TouchableOpacity style={[styles.actionButton, styles.actionButtonSecondary]}>
            <Text style={[styles.actionButtonText, styles.actionButtonTextSecondary]}>
              הצג דוחות
            </Text>
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
  content: {
    ...Theme.commonStyles.centeredContent,
    paddingVertical: Theme.spacing.screenPadding.vertical,
  },
  greetingSection: {
    marginBottom: Theme.spacing.xxl,
    paddingVertical: Theme.spacing.lg,
    alignItems: 'flex-end',
  },
  greeting: {
    ...Theme.typography.heading2,
    color: Theme.colors.text.primary,
    textAlign: 'right',
  },
  userName: {
    ...Theme.typography.heading1,
    color: Theme.colors.primary.main,
    marginBottom: Theme.spacing.sm,
    textAlign: 'right',
  },
  welcomeText: {
    ...Theme.typography.body1,
    color: Theme.colors.text.secondary,
    marginTop: Theme.spacing.xs,
    textAlign: 'right',
  },
  featuresSection: {
    marginBottom: Theme.spacing.xxl,
  },
  sectionTitle: {
    ...Theme.typography.heading3,
    color: Theme.colors.text.primary,
    marginBottom: Theme.spacing.lg,
    textAlign: 'right',
  },
  featureCard: {
    ...Theme.commonStyles.card,
    flexDirection: 'row-reverse',
    alignItems: 'center',
  },
  featureIcon: {
    width: 60,
    height: 60,
    borderRadius: Theme.spacing.borderRadius.md,
    backgroundColor: Theme.colors.primary.light + '20', // 20% opacity
    alignItems: 'center',
    justifyContent: 'center',
    marginLeft: Theme.spacing.md,
  },
  iconText: {
    fontSize: Theme.spacing.icon.lg,
  },
  featureContent: {
    flex: 1,
  },
  featureTitle: {
    ...Theme.typography.subtitle1,
    color: Theme.colors.text.primary,
    marginBottom: Theme.spacing.xs,
    textAlign: 'right',
  },
  featureDescription: {
    ...Theme.typography.body2,
    color: Theme.colors.text.secondary,
    textAlign: 'right',
  },
  quickActionsSection: {
    marginBottom: Theme.spacing.xxl,
  },
  actionButton: {
    ...Theme.commonStyles.buttonPrimary,
    marginBottom: Theme.spacing.md,
  },
  actionButtonSecondary: {
    ...Theme.commonStyles.buttonOutline,
  },
  actionButtonText: {
    ...Theme.commonStyles.buttonText,
  },
  actionButtonTextSecondary: {
    ...Theme.commonStyles.buttonTextOutline,
  },
});

export default HomeScreen;