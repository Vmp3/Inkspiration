import { Platform } from 'react-native';

let BASE_URL = '';

if (Platform.OS === 'web') {
  BASE_URL = process.env.WEB_API_URL || 'http://localhost:8080';
} else if (Platform.OS === 'android') {
  BASE_URL = process.env.MOBILE_API_URL || 'http://10.0.2.2:8080';
} else {
  BASE_URL = process.env.WEB_API_URL || 'http://localhost:8080';
}

export const API_CONFIG = {
  BASE_URL,
  TIMEOUT: 30000,
};
