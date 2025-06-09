import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const StepHeader = ({ icon, title, description }) => {
  return (
    <View style={styles.container}>
      <View style={styles.iconContainer}>
        <Text style={styles.stepIcon}>{icon}</Text>
      </View>
      
      <Text style={styles.stepTitle}>{title}</Text>
      
      <Text style={styles.stepDescription}>{description}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
  },
  iconContainer: {
    marginBottom: 16,
  },
  stepIcon: {
    fontSize: 36,
  },
  stepTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 10,
    color: '#111',
  },
  stepDescription: {
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 24,
    color: '#666',
    lineHeight: 20,
  },
});

export default StepHeader; 