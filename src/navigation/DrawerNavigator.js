// src/navigation/DrawerNavigator.js
import { createDrawerNavigator } from '@react-navigation/drawer';
import HomeScreen from '../screens/Home/HomeScreen';
import MyListsScreen from '../screens/Lists/MyListsScreen';
import ProfileScreen from '../screens/Profile/ProfileScreen';
import BudgetReportsScreen from '../screens/Reports/BudgetReportsScreen';

const Drawer = createDrawerNavigator();

const DrawerNavigator = () => {
  return (
    <Drawer.Navigator
      screenOptions={{
        headerShown: true,
        drawerPosition: 'right', // מגירה מימין כי זה בעברית
        headerTitleAlign: 'center',
      }}
    >
      <Drawer.Screen 
        name="Home" 
        component={HomeScreen}
        options={{ title: 'בית' }}
      />
      <Drawer.Screen 
        name="MyLists" 
        component={MyListsScreen}
        options={{ title: 'הרשימות שלי' }}
      />
      <Drawer.Screen 
        name="BudgetReports" 
        component={BudgetReportsScreen}
        options={{ title: 'דוחות תקציב' }}
      />
      <Drawer.Screen 
        name="Profile" 
        component={ProfileScreen}
        options={{ title: 'פרופיל' }}
      />
    </Drawer.Navigator>
  );
};

export default DrawerNavigator;