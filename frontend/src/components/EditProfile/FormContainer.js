import React from 'react';
import { View, StyleSheet } from 'react-native';

import TabHeader from '../ui/TabHeader';

const FormContainer = ({ tabs, activeTab, setActiveTab, children }) => {
  return (
    <View style={styles.cardWrapper}>
      <View style={styles.formCard}>
        <View style={styles.tabHeaderWrapper}>
          <TabHeader 
            tabs={tabs}
            activeTab={activeTab}
            setActiveTab={setActiveTab}
          />
        </View>
        
        <View style={styles.formContainer}>
          {children}
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  cardWrapper: {
    width: '100%',
    maxWidth: 800,
    alignSelf: 'center',
    backgroundColor: '#fff',
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
    marginBottom: 24,
  },
  formCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    overflow: 'hidden',
  },
  tabHeaderWrapper: {
    borderBottomWidth: 1,
    borderBottomColor: '#e2e2e2',
  },
  formContainer: {
    padding: 24,
  },
});

export default FormContainer; 