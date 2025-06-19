import React, { useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigation } from '@react-navigation/native';
import toastHelper from '../utils/toastHelper';
import { protectedRouteMessages } from './protectedRoute/messages';

const ProtectedRoute = ({ children, publicRoutes = [] }) => {
  const { isAuthenticated, loading } = useAuth();
  const navigation = useNavigation();
  
  const currentRoute = navigation.getCurrentRoute();
  const currentRouteName = currentRoute?.name;

  const publicRoutesSet = new Set([
    'Login',
    'Register',
    'Home',
    'Explore',
    'Artist',
    'About',
    'ForgotPassword',
    'ResetPassword',
    ...publicRoutes
  ]);

  useEffect(() => {
    if (loading) {
      return;
    }
    
    if (!isAuthenticated && currentRouteName && !publicRoutesSet.has(currentRouteName)) {
      toastHelper.showError(protectedRouteMessages.errors.loginRequired);
      navigation.navigate('Login');
    }
  }, [isAuthenticated, loading, currentRouteName, navigation, publicRoutesSet]);

  return children;
};

export default ProtectedRoute; 