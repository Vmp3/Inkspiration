import { Platform } from 'react-native';
import { PROD_IP, ENVIRONMENT, WEB_API_URL, MOBILE_API_URL } from '@env';

export const API_CONFIG = {
  get BASE_URL() {
    if (ENVIRONMENT === 'prod') return PROD_IP;
    if (Platform.OS === 'web') return WEB_API_URL;
    if (Platform.OS === 'android') return MOBILE_API_URL;
    return WEB_API_URL;
  },
  TIMEOUT: 30000,
};
