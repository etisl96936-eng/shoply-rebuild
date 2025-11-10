import React from "react";
import { StyleSheet, Text, TouchableOpacity } from "react-native";

type Props = {
  title: string;
  onPress: () => void;
  color?: string;
};

export default function CustomButton({ title, onPress, color = "#4A90E2" }: Props) {
  return (
    <TouchableOpacity style={[styles.button, { backgroundColor: color }]} onPress={onPress}>
      <Text style={styles.text}>{title}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  button: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 12,
    alignItems: "center",
    marginVertical: 10,
  },
  text: {
    color: "#0b1803ff",
    fontSize: 18,
    fontWeight: "600",
  },
});
