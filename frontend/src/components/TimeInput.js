import React from 'react';
import { View, TextInput, StyleSheet, Platform } from 'react-native';

export const TimeInput = ({ value, onChange, disabled }) => {
  const handleChange = (text) => {
    // Remove caracteres não numéricos
    let formattedText = text.replace(/[^0-9]/g, '');
    
    // Limita as horas a 23 e os minutos a 59
    if (formattedText.length >= 1) {
      const hour = parseInt(formattedText.slice(0, 2), 10);
      
      // Se o primeiro dígito for maior que 2, ajusta para 0
      if (formattedText.length === 1 && parseInt(formattedText[0], 10) > 2) {
        formattedText = '0' + formattedText[0];
      }
      
      // Se as horas forem maiores que 23, ajusta para 23
      if (formattedText.length >= 2) {
        const hourStr = formattedText.slice(0, 2);
        if (parseInt(hourStr, 10) > 23) {
          formattedText = '23' + formattedText.slice(2);
        }
      }
      
      // Se os minutos forem maiores que 59, ajusta para 59
      if (formattedText.length >= 4) {
        const minStr = formattedText.slice(2, 4);
        if (parseInt(minStr, 10) > 59) {
          formattedText = formattedText.slice(0, 2) + '59';
        }
      }
    }
    
    // Formata para hh:mm
    if (formattedText.length > 2) {
      formattedText = `${formattedText.slice(0, 2)}:${formattedText.slice(2, 4)}`;
    }
    
    onChange(formattedText);
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={[
          styles.input,
          disabled && styles.disabledInput,
          Platform.OS === 'web' && styles.webInput
        ]}
        value={value}
        onChangeText={handleChange}
        placeholder="HH:MM"
        keyboardType="numeric"
        maxLength={5}
        editable={!disabled}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  input: {
    paddingHorizontal: 10,
    paddingVertical: 8,
    borderWidth: 1,
    borderColor: '#e1e1e1',
    borderRadius: 4,
    minWidth: 70,
    textAlign: 'center',
    backgroundColor: '#fff',
  },
  disabledInput: {
    backgroundColor: '#f0f0f0',
    color: '#999',
  },
  webInput: {
    outlineStyle: 'none',
  }
}); 