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
          variant="secondary"
          label={prevText}
          style={styles.button}
        />
      )}
      {showNext && (
        <Button 
          onPress={onNext}
          variant="primary"
          label={isLoading ? 'Criando...' : nextText}
          style={styles.button}
          disabled={isLoading || nextDisabled}
        />
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
    minWidth: 120,
  }
});

export default FormNavigation; 