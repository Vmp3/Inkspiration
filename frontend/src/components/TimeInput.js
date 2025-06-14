import React from 'react';
import { View, TextInput, StyleSheet, Platform } from 'react-native';
import toastHelper from '../utils/toastHelper';

export const TimeInput = ({ value, onChange, disabled, period, type }) => {
  const validateTime = (timeStr) => {
    if (!timeStr || timeStr.length < 5) return true;
    
    const [hours, minutes] = timeStr.split(':').map(num => parseInt(num, 10));
    const time = hours * 60 + minutes;

    if (period === 'morning') {
      if (time > 11 * 60 + 59) {
        toastHelper.showError('Horário da manhã deve ser entre 00:00 e 11:59');
        return false;
      }
    } else if (period === 'afternoon') {
      if (time < 12 * 60) {
        toastHelper.showError('Horário da tarde deve ser entre 12:00 e 23:59');
        return false;
      }
    }
    
    return true;
  };

  const handleChange = (text) => {
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
    
    if (formattedText.length === 5) {
      if (validateTime(formattedText)) {
        onChange(formattedText);
      }
    } else {
      onChange(formattedText);
    }
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