import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const StepIndicator = ({ currentStep, totalSteps }) => {
  return (
    <View style={styles.progressContainer}>
      <View style={styles.progressBar}>
        <View style={[
          styles.progressFill, 
          { width: `${(currentStep / totalSteps) * 100}%` }
        ]} />
      </View>
      <Text style={styles.progressText}>Passo {currentStep} de {totalSteps}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  progressContainer: {
    marginBottom: 28,
  },
  progressBar: {
    width: '100%',
    height: 2,
    backgroundColor: '#e2e2e2',
    borderRadius: 1,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#111',
    borderRadius: 1,
  },
  progressText: {
    textAlign: 'center',
    marginTop: 4,
    fontSize: 10,
    color: '#666',
  },
});

export default StepIndicator; 