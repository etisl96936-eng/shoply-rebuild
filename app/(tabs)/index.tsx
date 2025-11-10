import { Alert, StyleSheet, Text, View } from "react-native";
import CustomButton from "../../components/CustomButton";

export default function HomeScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>שלום Eti 👋</Text>

      <CustomButton
        title="לחצי כאן"
        color="#1c9703ff"
        onPress={() => Alert.alert("לחצת על הכפתור!")}
      />

      <CustomButton
        title="כפתור נוסף"
        color="#1c9703ff"
        onPress={() => Alert.alert("לחצת על השני!")}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#e5f9ddff",
  },
  text: {
    fontSize: 28,
    fontWeight: "bold",
    color: "#333",
  },
});
