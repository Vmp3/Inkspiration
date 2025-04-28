import React from 'react';
import { View, StyleSheet } from 'react-native';
import Button from './Button';

const FormNavigation = ({ 
  onPrev, 
  onNext, 
  showPrev = true, 
  showNext = true,
  prevText = "Voltar",
  nextText = "Próximo",
  isLoading = false,
  nextDisabled = false
}) => {
  return (
    <View style={styles.buttonContainer}>
      {showPrev && (
        <Button 
          onPress={onPrev}
          variant="outline"
          style={styles.button}
        >
          {prevText}
        </Button>
      )}
      {showNext && (
        <Button 
          onPress={onNext}
          style={[
            styles.button, 
            styles.nextButton,
            nextDisabled && styles.disabledButton
          ]}
          disabled={isLoading || nextDisabled}
        >
          {isLoading ? 'Criando...' : nextText}
        </Button>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginTop: 24,
    gap: 12,
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 6,
    minWidth: 120,
  },
  nextButton: {
    backgroundColor: '#000',
  },
  disabledButton: {
    backgroundColor: '#ccc',
    opacity: 0.7,
  }
});

export default FormNavigation; 