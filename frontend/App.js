import React from 'react';
import { StatusBar, View, Platform, StyleSheet } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import 'react-native-gesture-handler';
import Toast from 'react-native-toast-message';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

import HomeScreen from './src/screens/HomeScreen';
import LoginScreen from './src/screens/LoginScreen';
import RegisterScreen from './src/screens/RegisterScreen';
import ExploreScreen from './src/screens/ExploreScreen';
import AboutScreen from './src/screens/AboutScreen';
import Header from './src/components/Header';
import toastConfig from './src/config/toastConfig';

const Stack = createNativeStackNavigator();

const linking = {
  prefixes: [],
  config: {
    screens: {
      Home: 'home',
      Login: 'login',
      Register: 'register',
      Main: 'main',
      Explore: 'explore',
      About: 'about',
    },
  },
};

export default function App() {
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <NavigationContainer linking={linking}>
          <StatusBar 
            backgroundColor="white" 
            barStyle="dark-content" 
            translucent={Platform.OS === 'android'}
          />
          
          <View style={{ flex: 1 }}>
            <SafeAreaView style={{ zIndex: 1000 }}>
              <Header />
            </SafeAreaView>
            
            <Stack.Navigator 
              initialRouteName="Home"
              screenOptions={{
                headerShown: false,
              }}
            >
              <Stack.Screen name="Home" component={HomeScreen} />
              <Stack.Screen name="Login" component={LoginScreen} />
              <Stack.Screen name="Register" component={RegisterScreen} />
              <Stack.Screen name="Explore" component={ExploreScreen} />
              <Stack.Screen name="About" component={AboutScreen} />
            </Stack.Navigator>
          </View>
        </NavigationContainer>
        <View style={styles.toastContainer}>
          <Toast config={toastConfig} />
        </View>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  toastContainer: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    zIndex: 10000,
    elevation: 25,
  }
});
