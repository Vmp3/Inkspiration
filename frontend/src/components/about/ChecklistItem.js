import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

/**
 * Checklist item with a checkmark icon and text
 * @param {Object} props Component properties
 * @param {string} props.text The text to display
 */
const ChecklistItem = ({ text }) => {
  return (
    <View style={styles.container}>
      <MaterialIcons 
        name="check-circle" 
        size={20} 
        color="#111827" 
        style={styles.icon} 
      />
      <Text style={styles.text}>{text}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 12,
  },
  icon: {
    marginRight: 8,
    marginTop: 2,
  },
  text: {
    flex: 1,
    fontSize: 16,
    color: '#374151',
  }
});

export default ChecklistItem; 