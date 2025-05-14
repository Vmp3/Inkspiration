import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

/**
 * Feature card component for displaying features with icon, title, and description
 * @param {Object} props Component properties
 * @param {string} props.icon Icon name from MaterialIcons
 * @param {string} props.title Card title
 * @param {string} props.description Card description
 * @param {Object} props.style Additional styles for the card container
 */
const FeatureCard = ({ icon, title, description, style }) => {
  return (
    <View style={[styles.card, style]}>
      <View style={styles.iconContainer}>
        <MaterialIcons name={icon} size={24} color="#111827" />
      </View>
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.description}>{description}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 24,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
    alignItems: 'center',
  },
  iconContainer: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#f3f4f6',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#111827',
    textAlign: 'center',
  },
  description: {
    fontSize: 14,
    color: '#6b7280',
    textAlign: 'center',
    lineHeight: 20,
  }
});

export default FeatureCard; 