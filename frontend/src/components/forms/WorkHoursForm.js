import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Feather } from '@expo/vector-icons';
import { TimeInput } from '../TimeInput';
import toastHelper from '../../utils/toastHelper';

const WorkHoursForm = ({ workHours, handleWorkHourChange, handlePrevTab, handleNextTab }) => {
  const validateEndTime = (startTime, endTime, period) => {
    if (!startTime || !endTime || startTime.length < 5 || endTime.length < 5) return true;
    
    const [startHours, startMinutes] = startTime.split(':').map(num => parseInt(num, 10));
    const [endHours, endMinutes] = endTime.split(':').map(num => parseInt(num, 10));
    
    const startInMinutes = startHours * 60 + startMinutes;
    const endInMinutes = endHours * 60 + endMinutes;
    
    if (endInMinutes <= startInMinutes) {
      toastHelper.showError(`O horário de fim deve ser maior que o horário de início no período da ${period === 'morning' ? 'manhã' : 'tarde'}`);
      return false;
    }
    
    return true;
  };

  const handleTimeChange = (index, period, field, value) => {
    const dayPeriod = workHours[index][period];
    
    if (field === 'end' && dayPeriod.start) {
      if (!validateEndTime(dayPeriod.start, value, period)) {
        return;
      }
    }
    
    handleWorkHourChange(index, period, field, value);
  };

  return (
    <View style={styles.tabContent}>
      <Text style={styles.workHoursTitle}>Horário de Trabalho</Text>
      <Text style={styles.workHoursSubtitle}>Defina seus horários de disponibilidade para agendamentos.</Text>
      
      <View style={styles.daysContainer}>
        {workHours.map((day, index) => (
          <View key={index} style={styles.dayCard}>
            <View style={styles.dayHeader}>
              <Text style={styles.dayName}>{day.day}</Text>
              <View style={styles.availableCheckbox}>
                <TouchableOpacity 
                  style={[styles.checkbox, day.available && styles.checkboxChecked]}
                  onPress={() => handleWorkHourChange(index, null, 'available', !day.available)}
                >
                  {day.available && <Feather name="check" size={16} color="#fff" />}
                </TouchableOpacity>
                <Text style={styles.checkboxLabel}>Disponível</Text>
              </View>
            </View>
            
            {day.available && (
              <View style={styles.dayHours}>
                <View style={styles.periodRow}>
                  <View style={styles.periodCheckbox}>
                    <TouchableOpacity 
                      style={[styles.checkbox, day.morning.enabled && styles.checkboxChecked]}
                      onPress={() => handleWorkHourChange(index, 'morning', 'enabled', !day.morning.enabled)}
                    >
                      {day.morning.enabled && <Feather name="check" size={16} color="#fff" />}
                    </TouchableOpacity>
                    <Text style={styles.checkboxLabel}>Manhã:</Text>
                  </View>
                  
                  <View style={styles.timeInputContainer}>
                    <TimeInput
                      value={day.morning.start}
                      onChange={(value) => handleTimeChange(index, 'morning', 'start', value)}
                      disabled={!day.morning.enabled}
                      period="morning"
                      type="start"
                    />
                    <Text style={styles.timeInputSeparator}>às</Text>
                    <TimeInput
                      value={day.morning.end}
                      onChange={(value) => handleTimeChange(index, 'morning', 'end', value)}
                      disabled={!day.morning.enabled}
                      period="morning"
                      type="end"
                    />
                  </View>
                </View>
                
                <View style={styles.periodRow}>
                  <View style={styles.periodCheckbox}>
                    <TouchableOpacity 
                      style={[styles.checkbox, day.afternoon.enabled && styles.checkboxChecked]}
                      onPress={() => handleWorkHourChange(index, 'afternoon', 'enabled', !day.afternoon.enabled)}
                    >
                      {day.afternoon.enabled && <Feather name="check" size={16} color="#fff" />}
                    </TouchableOpacity>
                    <Text style={styles.checkboxLabel}>Tarde:</Text>
                  </View>
                  
                  <View style={styles.timeInputContainer}>
                    <TimeInput
                      value={day.afternoon.start}
                      onChange={(value) => handleTimeChange(index, 'afternoon', 'start', value)}
                      disabled={!day.afternoon.enabled}
                      period="afternoon"
                      type="start"
                    />
                    <Text style={styles.timeInputSeparator}>às</Text>
                    <TimeInput
                      value={day.afternoon.end}
                      onChange={(value) => handleTimeChange(index, 'afternoon', 'end', value)}
                      disabled={!day.afternoon.enabled}
                      period="afternoon"
                      type="end"
                    />
                  </View>
                </View>
              </View>
            )}
          </View>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  tabContent: {
    padding: 16,
  },
  workHoursTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 6,
  },
  workHoursSubtitle: {
    fontSize: 14,
    color: '#666',
    marginBottom: 16,
  },
  daysContainer: {
    marginBottom: 20,
  },
  dayCard: {
    borderWidth: 1,
    borderColor: '#eaeaea',
    borderRadius: 4,
    padding: 12,
    marginBottom: 12,
  },
  dayHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  dayName: {
    fontSize: 16,
    fontWeight: '500',
  },
  availableCheckbox: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  dayHours: {
    marginTop: 12,
  },
  periodRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  periodCheckbox: {
    flexDirection: 'row',
    alignItems: 'center',
    width: 80,
  },
  timeInputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  timeInputSeparator: {
    marginHorizontal: 8,
  },
  checkbox: {
    width: 20,
    height: 20,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 4,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 8,
  },
  checkboxChecked: {
    backgroundColor: '#000',
    borderColor: '#000',
  },
  checkboxLabel: {
    fontSize: 14,
  },
});

export default WorkHoursForm; 