import { Platform } from 'react-native';
import { PROD_IP, ENVIRONMENT, WEB_API_URL, MOBILE_API_URL } from '@env';

let BASE_URL = '';

if (ENVIRONMENT === 'prod') {
  BASE_URL = PROD_IP;
} else {
  if (Platform.OS === 'web') {
    BASE_URL = WEB_API_URL;
  } else if (Platform.OS === 'android') {
    BASE_URL = MOBILE_API_URL;
  } else {
    BASE_URL = WEB_API_URL;
  }
}

export const API_CONFIG = {
  BASE_URL,
  TIMEOUT: 30000,
};
