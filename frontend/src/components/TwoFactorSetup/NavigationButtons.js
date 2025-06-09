import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ActivityIndicator } from 'react-native';

const NavigationButtons = ({ 
  onPrev, 
  onNext, 
  showPrev = true, 
  showNext = true,
  prevText = "Voltar", 
  nextText = "Próximo",
  isLoading = false,
  disabled = false,
  isDanger = false
}) => {
  return (
    <View style={styles.navigationButtons}>
      {showPrev && (
        <TouchableOpacity 
          style={styles.secondaryButton} 
          onPress={onPrev}
        >
          <Text style={styles.secondaryButtonText}>{prevText}</Text>
        </TouchableOpacity>
      )}

      {showNext && (
        <TouchableOpacity 
          style={[
            styles.primaryButton, 
            styles.nextButton,
            isDanger && styles.dangerButton
          ]} 
          onPress={onNext}
          disabled={disabled || isLoading}
        >
          {isLoading ? (
            <ActivityIndicator size="small" color="#fff" />
          ) : (
            <Text style={styles.primaryButtonText}>{nextText}</Text>
          )}
        </TouchableOpacity>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  navigationButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    width: '100%',
    marginTop: 24,
    gap: 12,
  },
  primaryButton: {
    backgroundColor: '#111',
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 6,
    minWidth: 100,
    alignItems: 'center',
  },
  nextButton: {
    flex: 1,
  },
  dangerButton: {
    backgroundColor: '#ef5350',
  },
  primaryButtonText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: 'bold',
  },
  secondaryButton: {
    backgroundColor: 'transparent',
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: '#e2e2e2',
    minWidth: 100,
    alignItems: 'center',
  },
  secondaryButtonText: {
    color: '#111',
    fontSize: 14,
    fontWeight: '500',
  },
});

export default NavigationButtons; 