import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import Toast from 'react-native-toast-message';

const toastConfig = {
  success: (props) => (
    <TouchableOpacity 
      style={[styles.toast, styles.successToast]}
      onPress={() => Toast.hide()}
      activeOpacity={0.8}
    >
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </TouchableOpacity>
  ),
  error: (props) => (
    <TouchableOpacity 
      style={[styles.toast, styles.errorToast]}
      onPress={() => Toast.hide()}
      activeOpacity={0.8}
    >
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </TouchableOpacity>
  ),
  info: (props) => (
    <TouchableOpacity 
      style={[styles.toast, styles.infoToast]}
      onPress={() => Toast.hide()}
      activeOpacity={0.8}
    >
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </TouchableOpacity>
  ),
  warning: (props) => (
    <TouchableOpacity 
      style={[styles.toast, styles.warningToast]}
      onPress={() => Toast.hide()}
      activeOpacity={0.8}
    >
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </TouchableOpacity>
  ),
  any_custom_type: (props) => (
    <TouchableOpacity 
      style={[styles.toast, styles.defaultToast]}
      onPress={() => Toast.hide()}
      activeOpacity={0.8}
    >
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </TouchableOpacity>
  ),
  config: {
    position: 'bottom',
    visibilityTime: 4000,
    bottomOffset: 16,
    zIndex: 999999,
  }
};

const styles = StyleSheet.create({
  toast: {
    padding: 15,
    borderRadius: 8,
    marginHorizontal: 16,
    marginBottom: 16,
    boxShadow: '0px 2px 3.84px rgba(0, 0, 0, 0.25)',
    elevation: 999999,
    zIndex: 999999,
    alignSelf: 'flex-end',
    maxWidth: '80%',
  },
  successToast: {
    backgroundColor: '#4CAF50',
  },
  errorToast: {
    backgroundColor: '#F44336',
  },
  infoToast: {
    backgroundColor: '#2196F3',
  },
  warningToast: {
    backgroundColor: '#FF9800',
  },
  defaultToast: {
    backgroundColor: '#333333',
  },
  text1: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  text2: {
    color: 'white',
    fontSize: 14,
  },
});

export default toastConfig; 