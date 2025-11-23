// src/theme/Typography.js
import { Platform } from 'react-native';

const Typography = {
  // Font Families - משפחות פונטים
  fontFamily: {
    regular: Platform.select({
      ios: 'System',
      android: 'Roboto',
      default: 'System',
    }),
    medium: Platform.select({
      ios: 'System',
      android: 'Roboto-Medium',
      default: 'System',
    }),
    bold: Platform.select({
      ios: 'System',
      android: 'Roboto-Bold',
      default: 'System',
    }),
    light: Platform.select({
      ios: 'System',
      android: 'Roboto-Light',
      default: 'System',
    }),
  },

  // Font Sizes - גדלי פונטים
  fontSize: {
    xs: 10,    // קטן מאוד - תוויות זעירות
    sm: 12,    // קטן - תתי כותרות, הערות
    base: 14,  // בסיסי - טקסט רגיל
    md: 16,    // בינוני - טקסט חשוב
    lg: 18,    // גדול - כותרות משניות
    xl: 20,    // גדול מאוד - כותרות ראשיות
    xxl: 24,   // ענק - כותרות מסכים
    xxxl: 28,  // ענק מאוד - כותרות דף הבית
  },

  // Font Weights - משקלי פונטים
  fontWeight: {
    light: '300',
    regular: '400',
    medium: '500',
    semibold: '600',
    bold: '700',
  },

  // Line Heights - גובה שורה
  lineHeight: {
    tight: 1.2,   // צפוף - לכותרות
    normal: 1.5,  // רגיל - לטקסט רגיל
    relaxed: 1.8, // מרווח - לפסקאות ארוכות
  },

  // Text Styles - סגנונות טקסט מוכנים לשימוש
  heading1: {
    fontSize: 28,
    fontWeight: '700',
    lineHeight: 1.2,
  },
  heading2: {
    fontSize: 24,
    fontWeight: '700',
    lineHeight: 1.3,
  },
  heading3: {
    fontSize: 20,
    fontWeight: '600',
    lineHeight: 1.4,
  },
  subtitle1: {
    fontSize: 18,
    fontWeight: '500',
    lineHeight: 1.5,
  },
  subtitle2: {
    fontSize: 16,
    fontWeight: '500',
    lineHeight: 1.5,
  },
  body1: {
    fontSize: 16,
    fontWeight: '400',
    lineHeight: 1.5,
  },
  body2: {
    fontSize: 14,
    fontWeight: '400',
    lineHeight: 1.5,
  },
  button: {
    fontSize: 16,
    fontWeight: '600',
    lineHeight: 1.2,
    textTransform: 'uppercase',
  },
  caption: {
    fontSize: 12,
    fontWeight: '400',
    lineHeight: 1.4,
  },
  overline: {
    fontSize: 10,
    fontWeight: '500',
    lineHeight: 1.2,
    textTransform: 'uppercase',
    letterSpacing: 1.5,
  },
};

export default Typography;