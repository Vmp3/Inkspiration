import Toast from 'react-native-toast-message';
import toastConfig from '../config/toastConfig';

export const showToast = (type, title, message) => {
  Toast.show({
    type,
    text1: title,
    text2: message,
    ...toastConfig.config
  });
};

export default {
  showSuccess: (message) => showToast('success', 'Sucesso', message),
  showError: (message) => showToast('error', 'Erro', message)
}; 