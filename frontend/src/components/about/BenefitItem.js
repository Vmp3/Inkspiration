import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

/**
 * Benefit item component to display an icon with title and description
 * @param {Object} props Component properties
 * @param {string} props.icon Icon name from MaterialIcons
 * @param {string} props.title Benefit title
 * @param {string} props.description Benefit description
 * @param {Object} props.style Additional styles for container
 */
const BenefitItem = ({ icon, title, description, style }) => {
  return (
    <View style={[styles.container, style]}>
      <MaterialIcons name={icon} size={32} color="#111827" style={styles.icon} />
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.description}>{description}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    marginBottom: 24,
    alignItems: 'center',
  },
  icon: {
    marginBottom: 12,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#111827',
    textAlign: 'center',
  },
  description: {
    fontSize: 14,
    color: '#6b7280',
    textAlign: 'center',
  }
});

export default BenefitItem; 