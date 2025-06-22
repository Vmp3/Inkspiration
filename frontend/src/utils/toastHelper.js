import Toast from 'react-native-toast-message';
import toastConfig from '../config/toastConfig';

export const showToast = (type, title, message) => {
  Toast.show({
    type,
    text1: title,
    text2: message,
    ...toastConfig.config,
    position: 'bottom',
    bottomOffset: 50,
    visibilityTime: 5000
  });
};

const toastHelper = {
  showSuccess: (message) => showToast('success', 'Sucesso', message),
  showError: (message) => showToast('error', 'Erro', message),
  showInfo: (message) => showToast('info', 'Informação', message),
  showWarning: (message) => showToast('warning', 'Atenção', message)
};

export default toastHelper; 