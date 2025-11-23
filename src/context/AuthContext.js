// src/context/AuthContext.js
import { createContext, useContext, useState } from 'react';

// יצירת ה-Context
const AuthContext = createContext();

// Provider שיעטוף את כל האפליקציה
export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);

  // פונקציית התחברות
  const login = (userData) => {
    setUser(userData);
    setIsAuthenticated(true);
    console.log('User logged in:', userData);
  };

  // פונקציית התנתקות
  const logout = () => {
    setUser(null);
    setIsAuthenticated(false);
    console.log('User logged out');
  };

  // פונקציית הרשמה
  const register = (userData) => {
    // כרגע פשוט נכניס את המשתמש, בעתיד נוסיף קריאה לשרת
    setUser(userData);
    setIsAuthenticated(true);
    console.log('User registered:', userData);
  };

  // הערכים שנרצה לשתף עם כל האפליקציה
  const value = {
    isAuthenticated,
    user,
    login,
    logout,
    register,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Hook מותאם אישית לשימוש קל ב-Context
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext;