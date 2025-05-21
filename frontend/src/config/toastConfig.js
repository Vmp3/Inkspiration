import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const toastConfig = {
  success: (props) => (
    <View style={[styles.toast, styles.successToast]}>
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </View>
  ),
  error: (props) => (
    <View style={[styles.toast, styles.errorToast]}>
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </View>
  ),
  any_custom_type: (props) => (
    <View style={[styles.toast, styles.defaultToast]}>
      <Text style={styles.text1}>{props.text1}</Text>
      <Text style={styles.text2}>{props.text2}</Text>
    </View>
  ),
  config: {
    position: 'bottom',
    visibilityTime: 4000,
    bottomOffset: 16,
  }
};

const styles = StyleSheet.create({
  toast: {
    padding: 15,
    borderRadius: 8,
    marginHorizontal: 16,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
    alignSelf: 'flex-end',
    maxWidth: '80%',
  },
  successToast: {
    backgroundColor: '#4CAF50',
  },
  errorToast: {
    backgroundColor: '#F44336',
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