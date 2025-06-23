import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView } from 'react-native';
import { isMobileView } from '../../utils/responsive';

const TabHeader = ({ tabs, activeTab, setActiveTab, onTabPress, availableTabs }) => {
  const isMobile = isMobileView();
  
  const handleTabPress = (tabId) => {
    if (availableTabs && !availableTabs.includes(tabId)) {
      return;
    }
    
    if (onTabPress) {
      onTabPress(tabId);
    } else {
      setActiveTab(tabId);
    }
  };

  return (
    <ScrollView 
      horizontal 
      showsHorizontalScrollIndicator={false}
      contentContainerStyle={[
        styles.tabsScrollContainer,
        isMobile && styles.tabsScrollContainerMobile
      ]}
    >
      <View style={styles.tabsContainer}>
        {tabs.map((tab) => {
          const isAvailable = !availableTabs || availableTabs.includes(tab.id);
          const isActive = activeTab === tab.id;
          
          return (
            <TouchableOpacity
              key={tab.id}
              style={[
                styles.tabItem,
                isMobile && styles.tabItemMobile,
                isActive && styles.activeTabItem,
                !isAvailable && styles.disabledTabItem,
              ]}
              onPress={() => handleTabPress(tab.id)}
              disabled={!isAvailable}
            >
              <Text
                style={[
                  styles.tabText,
                  isMobile && styles.tabTextMobile,
                  isActive && styles.activeTabText,
                  !isAvailable && styles.disabledTabText
                ]}
                numberOfLines={isMobile ? 2 : 1}
                ellipsizeMode="tail"
              >
                {tab.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  tabsScrollContainer: {
    flexGrow: 1,
    width: '100%',
  },
  tabsScrollContainerMobile: {
    minWidth: '100%',
  },
  tabsContainer: {
    flexDirection: 'row',
    backgroundColor: '#f8f8f8',
    borderBottomWidth: 0,
    width: '100%',
  },
  tabItem: {
    paddingVertical: 14,
    paddingHorizontal: 10,
    alignItems: 'center',
    justifyContent: 'center',
    minWidth: 80,
    flex: 1,
  },
  tabItemMobile: {
    paddingHorizontal: 6,
    paddingVertical: 12,
    minWidth: 0,
    flex: 1,
  },
  activeTabItem: {
    backgroundColor: '#fff',
    borderBottomWidth: 3,
    borderBottomColor: '#eaeaea',
  },
  disabledTabItem: {
    backgroundColor: '#f8f8f8',
    opacity: 0.5,
  },
  tabText: {
    fontSize: 13.5,
    color: '#666',
    textAlign: 'center',
    fontWeight: '400',
  },
  tabTextMobile: {
    fontSize: 12,
    lineHeight: 14,
  },
  activeTabText: {
    fontWeight: '600',
    color: '#111',
  },
  disabledTabText: {
    color: '#ccc',
    fontWeight: '400',
  },
});

export default TabHeader; 