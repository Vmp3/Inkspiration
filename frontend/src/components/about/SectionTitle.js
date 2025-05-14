import React from 'react';
import { Text, StyleSheet } from 'react-native';

/**
 * Section title component for headings
 * @param {Object} props Component properties
 * @param {string} props.children Text to display
 * @param {boolean} props.centered Whether to center the text
 * @param {Object} props.style Additional styles to apply
 */
const SectionTitle = ({ children, centered = false, style }) => {
  return (
    <Text style={[
      styles.title, 
      centered && styles.centered,
      style
    ]}>
      {children}
    </Text>
  );
};

const styles = StyleSheet.create({
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#111827',
  },
  centered: {
    textAlign: 'center',
  }
});

export default SectionTitle; 