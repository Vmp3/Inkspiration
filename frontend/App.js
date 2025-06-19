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
import EditProfileScreen from './src/screens/EditProfileScreen';
import toastConfig from './src/config/toastConfig';
import { AuthProvider } from './src/context/AuthContext';
import ProfessionalRegisterScreen from './src/screens/ProfessionalRegisterScreen';
import AdminUsersScreen from './src/screens/AdminUsersScreen';
import ArtistScreen from './src/screens/ArtistScreen';
import BookingScreen from './src/screens/BookingScreen';
import MyAppointmentsScreen from './src/screens/MyAppointmentsScreen';
import MyAttendancesScreen from './src/screens/MyAttendancesScreen';
import ForgotPasswordScreen from './src/screens/ForgotPasswordScreen';
import ResetPasswordScreen from './src/screens/ResetPasswordScreen';
import TwoFactorSetupScreen from './src/screens/TwoFactorSetupScreen';
import ProtectedRoute from './src/components/ProtectedRoute';

const Stack = createNativeStackNavigator();

const linking = {
  prefixes: [],
  config: {
    screens: {
      Home: 'home',
      Login: 'login',
      Register: 'register',
      ForgotPassword: 'forgot-password',
      ResetPassword: 'reset-password',
      AdminUsers: 'admin/usuarios',
      Artist: 'artist/:artistId',
      Booking: 'booking/:professionalId',
      Main: 'main',
      Explore: 'explore',
      About: 'about',
      Profile: 'profile',
      EditProfile: 'edit-profile',
      ProfessionalRegister: 'professional-register',
      MyAppointments: 'my-appointments',
      MyAttendances: 'my-attendances',
      TwoFactorSetup: 'two-factor-setup',
    },
  },
};

const publicRoutes = [
  'Home',
  'Login',
  'Register',
  'Explore',
  'Artist',
  'About',
  'ForgotPassword',
  'ResetPassword'
];

export default function App() {
  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <AuthProvider>
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

              <ProtectedRoute publicRoutes={publicRoutes}>
                <Stack.Navigator
                  initialRouteName="Home"
                  screenOptions={{
                    headerShown: false,
                  }}
                >
                  <Stack.Screen name="Home" component={HomeScreen} />
                  <Stack.Screen name="Login" component={LoginScreen} />
                  <Stack.Screen name="Register" component={RegisterScreen} />
                  <Stack.Screen name="ForgotPassword" component={ForgotPasswordScreen} />
                  <Stack.Screen name="ResetPassword" component={ResetPasswordScreen} />
                  <Stack.Screen name="Explore" component={ExploreScreen} />
                  <Stack.Screen name="About" component={AboutScreen} />
                  <Stack.Screen name="Profile" component={EditProfileScreen} />
                  <Stack.Screen name="Artist" component={ArtistScreen} />
                  <Stack.Screen name="Booking" component={BookingScreen} />
                  <Stack.Screen name="AdminUsers" component={AdminUsersScreen} />
                  <Stack.Screen name="ProfessionalRegister" component={ProfessionalRegisterScreen} />
                  <Stack.Screen name="MyAppointments" component={MyAppointmentsScreen} />
                  <Stack.Screen name="MyAttendances" component={MyAttendancesScreen} />
                  <Stack.Screen name="TwoFactorSetup" component={TwoFactorSetupScreen} />
                </Stack.Navigator>
              </ProtectedRoute>
            </View>

            <Toast config={toastConfig} />
          </NavigationContainer>
        </AuthProvider>
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