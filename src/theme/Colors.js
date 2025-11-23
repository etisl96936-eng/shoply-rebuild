// src/theme/Colors.js

const Colors = {
  // Primary Colors - צבעים ראשיים
  primary: {
    main: '#4CAF50',      // ירוק עיקרי - לכפתורים ראשיים
    light: '#81C784',     // ירוק בהיר - למצבי hover
    dark: '#388E3C',      // ירוק כהה - לדגשים
    contrast: '#FFFFFF',  // לבן - טקסט על רקע ירוק
  },

  // Secondary Colors - צבעים משניים
  secondary: {
    main: '#2196F3',      // כחול עיקרי
    light: '#64B5F6',     // כחול בהיר
    dark: '#1976D2',      // כחול כהה
    contrast: '#FFFFFF',  // לבן - טקסט על רקע כחול
  },

  // Background Colors - צבעי רקע
  background: {
    primary: '#FFFFFF',   // רקע לבן ראשי
    secondary: '#F5F5F5', // רקע אפור בהיר
    card: '#FFFFFF',      // רקע כרטיסים
    drawer: '#FAFAFA',    // רקע תפריט צד
  },

  // Text Colors - צבעי טקסט
  text: {
    primary: '#212121',   // טקסט ראשי - שחור כהה
    secondary: '#757575', // טקסט משני - אפור
    disabled: '#BDBDBD',  // טקסט מושבת
    hint: '#9E9E9E',      // רמזים
    white: '#FFFFFF',     // טקסט לבן
  },

  // Status Colors - צבעי סטטוס
  status: {
    success: '#4CAF50',   // הצלחה - ירוק
    error: '#F44336',     // שגיאה - אדום
    warning: '#FF9800',   // אזהרה - כתום
    info: '#2196F3',      // מידע - כחול
  },

  // Border Colors - צבעי מסגרת
  border: {
    light: '#E0E0E0',     // מסגרת בהירה
    main: '#BDBDBD',      // מסגרת רגילה
    dark: '#9E9E9E',      // מסגרת כהה
  },

  // Semantic Colors - צבעים סמנטיים לפיצ'רים ספציפיים
  list: {
    shared: '#E3F2FD',    // רקע לרשימה משותפת
    archived: '#F5F5F5',  // רקע לרשימה בארכיון
    active: '#FFFFFF',    // רקע לרשימה פעילה
  },

  // Chart Colors - צבעים לגרפים
  chart: {
    primary: '#4CAF50',
    secondary: '#2196F3',
    tertiary: '#FF9800',
    quaternary: '#9C27B0',
    quinary: '#F44336',
  },

  // Overlay - שכבות
  overlay: {
    light: 'rgba(0, 0, 0, 0.1)',
    medium: 'rgba(0, 0, 0, 0.3)',
    dark: 'rgba(0, 0, 0, 0.5)',
  },
};

export default Colors;