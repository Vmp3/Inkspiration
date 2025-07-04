import React, { useEffect, useState } from 'react';
import { View, TextInput, StyleSheet, Platform } from 'react-native';
import toastHelper from '../utils/toastHelper';

export const TimeInput = ({ value, onChange, disabled, period, type, startTime }) => {
  const [isValid, setIsValid] = useState(true);

  useEffect(() => {
    if (value && value.length === 5) {
      const validationResult = validateTime(value);
      setIsValid(validationResult);
    } else if (value && value.length > 0 && value.length < 5) {
      setIsValid(true);
    } else {
      setIsValid(true);
    }
  }, [value, startTime]);
  
  const validateTime = (timeStr) => {
    if (!timeStr || timeStr.length < 5) {
      return timeStr.length === 0;
    }
    
    const [hours, minutes] = timeStr.split(':').map(num => parseInt(num, 10));
    const time = hours * 60 + minutes;

    if (period === 'morning') {
      if (time > 11 * 60 + 59) {
        return false;
      }
    } else if (period === 'afternoon') {
      if (time < 12 * 60) {
        return false;
      }
    }
    
    if (type === 'end' && startTime && startTime.length === 5) {
      const [startHours, startMinutes] = startTime.split(':').map(num => parseInt(num, 10));
      const startInMinutes = startHours * 60 + startMinutes;
      
      if (time <= startInMinutes) {
        return false;
      }
    }
    
    return true;
  };

  const handleChange = (text) => {
    let formattedText = text.replace(/[^0-9]/g, '');
    
    if (formattedText.length >= 1) {
      
      if (formattedText.length >= 2) {
        const hourStr = formattedText.slice(0, 2);
        if (parseInt(hourStr, 10) > 23) {
          formattedText = '23' + formattedText.slice(2);
        }
      }
      
      if (formattedText.length >= 4) {
        const minStr = formattedText.slice(2, 4);
        if (parseInt(minStr, 10) > 59) {
          formattedText = formattedText.slice(0, 2) + '59';
        }
      }
    }
    
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
          Platform.OS === 'web' && styles.webInput,
          value && value.length === 5 && !isValid && styles.invalidInput
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
    paddingHorizontal: Platform.OS === 'web' ? 10 : 6,
    paddingVertical: Platform.OS === 'web' ? 8 : 6,
    borderWidth: 1,
    borderColor: '#e1e1e1',
    borderRadius: 4,
    minWidth: Platform.OS === 'web' ? 70 : 60,
    width: Platform.OS === 'web' ? 70 : 60,
    textAlign: 'center',
    backgroundColor: '#fff',
    fontSize: Platform.OS === 'web' ? 14 : 12,
  },
  disabledInput: {
    backgroundColor: '#f0f0f0',
    color: '#999',
  },
  invalidInput: {
    borderColor: '#ff3333',
    backgroundColor: '#fff0f0',
    color: '#ff0000',
  },
  webInput: {
    outlineStyle: 'none',
  }
});