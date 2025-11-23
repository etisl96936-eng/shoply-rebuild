// src/theme/index.js

import Colors from './Colors';
import Spacing from './Spacing';
import Typography from './Typography';

// Theme Object - אובייקט מרכזי שמכיל את כל ה-Design System
const Theme = {
  colors: Colors,
  typography: Typography,
  spacing: Spacing,

  // Common Shadows - צללים נפוצים
  shadows: {
    none: {
      shadowColor: 'transparent',
      shadowOffset: { width: 0, height: 0 },
      shadowOpacity: 0,
      shadowRadius: 0,
      elevation: 0,
    },
    sm: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.18,
      shadowRadius: 1.0,
      elevation: 1,
    },
    md: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.23,
      shadowRadius: 2.62,
      elevation: 4,
    },
    lg: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.30,
      shadowRadius: 4.65,
      elevation: 8,
    },
    xl: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 6 },
      shadowOpacity: 0.37,
      shadowRadius: 7.49,
      elevation: 12,
    },
  },

  // Common Styles - סגנונות נפוצים מוכנים לשימוש
  commonStyles: {
    // Container Styles
    container: {
      flex: 1,
      backgroundColor: Colors.background.primary,
    },
    screenPadding: {
      paddingHorizontal: Spacing.screenPadding.horizontal,
      paddingVertical: Spacing.screenPadding.vertical,
    },
    centeredContent: {
      width: '100%',
      maxWidth: 600,
      alignSelf: 'center',
      paddingHorizontal: Spacing.screenPadding.horizontal,
    },
    
    // Card Styles
    card: {
      backgroundColor: Colors.background.card,
      borderRadius: Spacing.card.borderRadius,
      padding: Spacing.card.padding,
      marginVertical: Spacing.card.margin,
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.23,
      shadowRadius: 2.62,
      elevation: 4,
    },

    // Button Styles
    buttonPrimary: {
      backgroundColor: Colors.primary.main,
      paddingVertical: Spacing.button.paddingVertical,
      paddingHorizontal: Spacing.button.paddingHorizontal,
      borderRadius: Spacing.button.borderRadius,
      alignItems: 'center',
      justifyContent: 'center',
    },
    buttonSecondary: {
      backgroundColor: Colors.secondary.main,
      paddingVertical: Spacing.button.paddingVertical,
      paddingHorizontal: Spacing.button.paddingHorizontal,
      borderRadius: Spacing.button.borderRadius,
      alignItems: 'center',
      justifyContent: 'center',
    },
    buttonOutline: {
      backgroundColor: 'transparent',
      borderWidth: 1,
      borderColor: Colors.primary.main,
      paddingVertical: Spacing.button.paddingVertical,
      paddingHorizontal: Spacing.button.paddingHorizontal,
      borderRadius: Spacing.button.borderRadius,
      alignItems: 'center',
      justifyContent: 'center',
    },
    buttonText: {
      ...Typography.button,
      color: Colors.text.white,
    },
    buttonTextOutline: {
      ...Typography.button,
      color: Colors.primary.main,
    },

    // Input Styles
    input: {
      backgroundColor: Colors.background.primary,
      borderWidth: 1,
      borderColor: Colors.border.main,
      borderRadius: Spacing.input.borderRadius,
      paddingVertical: Spacing.input.paddingVertical,
      paddingHorizontal: Spacing.input.paddingHorizontal,
      fontSize: Typography.fontSize.base,
      color: Colors.text.primary,
    },
    inputFocused: {
      borderColor: Colors.primary.main,
      borderWidth: 2,
    },
    inputError: {
      borderColor: Colors.status.error,
    },

    // List Item Styles
    listItem: {
      backgroundColor: Colors.background.card,
      paddingVertical: Spacing.listItem.paddingVertical,
      paddingHorizontal: Spacing.listItem.paddingHorizontal,
      marginVertical: Spacing.listItem.marginVertical,
      borderRadius: Spacing.listItem.borderRadius,
      flexDirection: 'row',
      alignItems: 'center',
    },

    // Text Styles
    title: {
      ...Typography.heading1,
      color: Colors.text.primary,
    },
    subtitle: {
      ...Typography.subtitle1,
      color: Colors.text.secondary,
    },
    body: {
      ...Typography.body1,
      color: Colors.text.primary,
    },
    caption: {
      ...Typography.caption,
      color: Colors.text.secondary,
    },

    // Centered Content
    centered: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },

    // Row Layout
    row: {
      flexDirection: 'row',
      alignItems: 'center',
    },
    rowSpaceBetween: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
    },
  },
};

// Export individual modules
export { Colors, Spacing, Typography };

// Export complete theme as default
export default Theme;