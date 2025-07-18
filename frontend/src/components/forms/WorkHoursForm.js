import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Platform, Dimensions } from 'react-native';
import { Feather } from '@expo/vector-icons';
import { TimeInput } from '../TimeInput';
import toastHelper from '../../utils/toastHelper';
import { isMobileView, isTabletView, isDesktopView, addDimensionsListener } from '../../utils/responsive';

const WorkHoursForm = ({ workHours, handleWorkHourChange, handlePrevTab, handleNextTab }) => {
  const [windowWidth, setWindowWidth] = useState(Dimensions.get('window').width);

  useEffect(() => {
    const cleanup = addDimensionsListener(({ window }) => {
      setWindowWidth(window.width);
    });

    return cleanup;
  }, []);

  const isTimeComplete = (time) => {
    return time && time.length === 5;
  };
  
  const validateTimeFormat = (time, period) => {
    if (!isTimeComplete(time)) {
      return false;
    }
    
    const [hours, minutes] = time.split(':').map(num => parseInt(num, 10));
    const timeInMinutes = hours * 60 + minutes;
    
    if (period === 'morning' && timeInMinutes > 11 * 60 + 59) {
      return false;
    } else if (period === 'afternoon' && timeInMinutes < 12 * 60) {
      return false;
    }
    
    return true;
  };

  const validateEndTime = (startTime, endTime, period) => {
    if (!isTimeComplete(startTime) || !isTimeComplete(endTime)) {
      return false;
    }
    
    if (!validateTimeFormat(startTime, period) || !validateTimeFormat(endTime, period)) {
      return false;
    }
    
    const [startHours, startMinutes] = startTime.split(':').map(num => parseInt(num, 10));
    const [endHours, endMinutes] = endTime.split(':').map(num => parseInt(num, 10));
    
    const startInMinutes = startHours * 60 + startMinutes;
    const endInMinutes = endHours * 60 + endMinutes;
    
    if (endInMinutes <= startInMinutes) {
      return false;
    }
    
    return true;
  };

  const handleTimeChange = (index, period, field, value) => {
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
                <View style={[
                  styles.periodsContainer,
                  isDesktopView() && styles.periodsContainerDesktop,
                  isTabletView() && styles.periodsContainerTablet
                ]}>
                  {/* Período da Manhã */}
                  <View style={[
                    styles.periodRow,
                    (isDesktopView() || isTabletView()) && styles.periodRowDesktop
                  ]}>
                    <View style={[
                      styles.periodCheckbox,
                      (isDesktopView() || isTabletView()) && styles.periodCheckboxDesktop
                    ]}>
                      <TouchableOpacity 
                        style={[styles.checkbox, day.morning.enabled && styles.checkboxChecked]}
                        onPress={() => handleWorkHourChange(index, 'morning', 'enabled', !day.morning.enabled)}
                      >
                        {day.morning.enabled && <Feather name="check" size={16} color="#fff" />}
                      </TouchableOpacity>
                      <Text style={styles.checkboxLabel}>Manhã</Text>
                    </View>
                    
                    <View style={[
                      styles.timeInputContainer,
                      (isDesktopView() || isTabletView()) && styles.timeInputContainerDesktop
                    ]}>
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
                        startTime={day.morning.start}
                      />
                    </View>
                  </View>
                  
                  {/* Período da Tarde */}
                  <View style={[
                    styles.periodRow,
                    (isDesktopView() || isTabletView()) && styles.periodRowDesktop
                  ]}>
                    <View style={[
                      styles.periodCheckbox,
                      (isDesktopView() || isTabletView()) && styles.periodCheckboxDesktop
                    ]}>
                      <TouchableOpacity 
                        style={[styles.checkbox, day.afternoon.enabled && styles.checkboxChecked]}
                        onPress={() => handleWorkHourChange(index, 'afternoon', 'enabled', !day.afternoon.enabled)}
                      >
                        {day.afternoon.enabled && <Feather name="check" size={16} color="#fff" />}
                      </TouchableOpacity>
                      <Text style={styles.checkboxLabel}>Tarde</Text>
                    </View>
                    
                    <View style={[
                      styles.timeInputContainer,
                      (isDesktopView() || isTabletView()) && styles.timeInputContainerDesktop
                    ]}>
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
                        startTime={day.afternoon.start}
                      />
                    </View>
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
    padding: isMobileView() ? 8 : 16,
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
    padding: isMobileView() ? 8 : 12,
    marginBottom: 12,
  },
  dayHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
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
    marginTop: 8,
  },
  periodsContainer: {
    flexDirection: 'column',
  },
  periodsContainerDesktop: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
  },
  periodsContainerTablet: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
  },
  periodRow: {
    marginBottom: 12,
    width: '100%',
  },
  periodRowDesktop: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
    marginRight: 16,
    minWidth: '45%',
    maxWidth: '48%',
  },
  periodCheckbox: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  periodCheckboxDesktop: {
    width: 100,
    marginRight: 8,
    marginBottom: 0,
  },
  timeInputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingLeft: 28,
    flexWrap: 'wrap',
  },
  timeInputContainerDesktop: {
    flex: 1,
    paddingLeft: 0,
    flexWrap: 'nowrap',
  },
  timeInputSeparator: {
    marginHorizontal: isMobileView() ? 4 : 8,
    minWidth: isMobileView() ? 20 : 24,
    textAlign: 'center',
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