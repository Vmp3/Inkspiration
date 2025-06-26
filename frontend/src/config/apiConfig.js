import { Platform } from 'react-native';
import { PROD_IP, ENVIRONMENT, WEB_API_URL, MOBILE_API_URL } from '@env';

let BASE_URL = '';

if (ENVIRONMENT === 'prod') {
  BASE_URL = PROD_IP;
} else if (ENVIRONMENT === 'dev') {
  if (Platform.OS === 'web') {
    BASE_URL = WEB_API_URL || 'http://localhost:8080';
  } else if (Platform.OS === 'android') {
    BASE_URL = MOBILE_API_URL || 'http://10.0.2.2:8000';
  } else {
    BASE_URL = WEB_API_URL || 'http://localhost:8080';
  }
} else {
  BASE_URL = 'http://localhost:8080';
}

export const API_CONFIG = {
  BASE_URL,
  TIMEOUT: 30000,
};
