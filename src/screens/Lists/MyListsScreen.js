// src/screens/Lists/MyListsScreen.js
import { StyleSheet, Text, View } from 'react-native';

const MyListsScreen = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>הרשימות שלי</Text>
      <Text style={styles.subtitle}>כאן יוצגו כל הרשימות שלך</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
});

export default MyListsScreen;