import { useNavigation } from '@react-navigation/native';

export const useNavigationHelper = () => {
  const navigation = useNavigation();

  /**
   * Função para voltar de forma segura
   * @param {string} fallbackRoute - Rota para navegar se não puder voltar (padrão: 'Home')
   */
  const safeGoBack = (fallbackRoute = 'Home') => {
    if (navigation.canGoBack()) {
      navigation.goBack();
    } else {
      navigation.navigate(fallbackRoute);
    }
  };

  const safeGoBackToProfile = () => {
    safeGoBack('EditProfile');
  };

  return {
    navigation,
    safeGoBack,
    safeGoBackToProfile,
  };
};

export default useNavigationHelper; 