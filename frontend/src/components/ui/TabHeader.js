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
    <View style={styles.container}>
      <ScrollView 
        horizontal 
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={[
          styles.tabsScrollContainer,
          isMobile && styles.tabsScrollContainerMobile
        ]}
      >
        <View style={[
          styles.tabsContainer,
          isMobile && styles.tabsContainerMobile
        ]}>
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
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#f8f8f8',
    borderBottomWidth: 1,
    borderBottomColor: '#e2e2e2',
  },
  tabsScrollContainer: {
    flexGrow: 1,
    paddingHorizontal: 4,
  },
  tabsScrollContainerMobile: {
    paddingHorizontal: 2,
  },
  tabsContainer: {
    flexDirection: 'row',
    backgroundColor: '#f8f8f8',
    minWidth: '100%',
  },
  tabsContainerMobile: {
    paddingVertical: 4,
  },
  tabItem: {
    paddingVertical: 16,
    paddingHorizontal: 12,
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    minHeight: 50,
  },
  tabItemMobile: {
    paddingHorizontal: 8,
    paddingVertical: 12,
    minHeight: 60,
    marginHorizontal: 2,
  },
  activeTabItem: {
    backgroundColor: '#fff',
    borderBottomWidth: 3,
    borderBottomColor: '#000000',
  },
  disabledTabItem: {
    backgroundColor: '#f8f8f8',
    opacity: 0.5,
  },
  tabText: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    fontWeight: '500',
  },
  tabTextMobile: {
    fontSize: 13,
    lineHeight: 16,
    fontWeight: '500',
  },
  activeTabText: {
    fontWeight: '600',
    color: '#000000',
  },
  disabledTabText: {
    color: '#ccc',
    fontWeight: '400',
  },
});

export default TabHeader; 