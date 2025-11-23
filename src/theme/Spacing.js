// src/theme/Spacing.js

const Spacing = {
  // Base Spacing Unit - יחידת מרווח בסיסית
  unit: 4, // כל המרווחים יהיו כפולות של 4

  // Spacing Values - ערכי מרווחים
  xs: 4,    // קטן מאוד - מרווח מינימלי
  sm: 8,    // קטן - מרווח בין אלמנטים קרובים
  md: 12,   // בינוני - מרווח סטנדרטי
  base: 16, // בסיסי - מרווח ברירת מחדל
  lg: 20,   // גדול - מרווח בין קבוצות
  xl: 24,   // גדול מאוד - מרווח בין סקציות
  xxl: 32,  // ענק - מרווח גדול בין סקציות
  xxxl: 40, // ענק מאוד - מרווחים מיוחדים

  // Screen Padding - ריפוד מסכים
  screenPadding: {
    horizontal: 16, // ריפוד אופקי למסכים
    vertical: 20,   // ריפוד אנכי למסכים
    top: 20,        // ריפוד עליון
    bottom: 20,     // ריפוד תחתון
  },

  // Card Spacing - מרווחים לכרטיסים
  card: {
    padding: 16,        // ריפוד פנימי לכרטיס
    margin: 12,         // מרווח חיצוני לכרטיס
    borderRadius: 12,   // עיגול פינות
  },

  // Button Spacing - מרווחים לכפתורים
  button: {
    paddingVertical: 12,   // ריפוד אנכי
    paddingHorizontal: 24, // ריפוד אופקי
    borderRadius: 8,       // עיגול פינות
    marginVertical: 8,     // מרווח אנכי בין כפתורים
  },

  // Input Spacing - מרווחים לשדות קלט
  input: {
    paddingVertical: 12,   // ריפוד אנכי
    paddingHorizontal: 16, // ריפוד אופקי
    borderRadius: 8,       // עיגול פינות
    marginVertical: 8,     // מרווח בין שדות
  },

  // List Item Spacing - מרווחים לפריטים ברשימה
  listItem: {
    paddingVertical: 12,   // ריפוד אנכי
    paddingHorizontal: 16, // ריפוד אופקי
    marginVertical: 4,     // מרווח בין פריטים
    borderRadius: 8,       // עיגול פינות
  },

  // Border Radius - עיגולי פינות
  borderRadius: {
    none: 0,
    sm: 4,
    base: 8,
    md: 12,
    lg: 16,
    xl: 20,
    round: 999, // עיגול מלא - לכפתורים עגולים
  },

  // Icon Sizes - גדלי אייקונים
  icon: {
    xs: 16,
    sm: 20,
    base: 24,
    md: 28,
    lg: 32,
    xl: 40,
    xxl: 48,
  },

  // Common Layouts - פריסות נפוצות
  layout: {
    headerHeight: 60,      // גובה Header
    tabBarHeight: 56,      // גובה Tab Bar
    drawerWidth: 280,      // רוחב Drawer
    maxContentWidth: 600,  // רוחב מקסימלי לתוכן (לטאבלטים)
  },
};

export default Spacing;