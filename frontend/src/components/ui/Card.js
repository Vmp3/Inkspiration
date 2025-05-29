import React from 'react';
import { View, StyleSheet } from 'react-native';

const Card = ({ children, style, ...props }) => {
  return (
    <View style={[styles.card, style]} {...props}>
      {children}
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    padding: 16,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    boxShadow: '0px 2px 2px rgba(0, 0, 0, 0.05)',
    elevation: 1,
  },
});

export default Card; 