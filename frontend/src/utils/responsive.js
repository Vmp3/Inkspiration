import { Dimensions, Platform } from 'react-native';

// Breakpoints comuns para responsividade
export const BREAKPOINTS = {
  MOBILE: 480,
  TABLET: 768,
  DESKTOP: 1024,
  LARGE_DESKTOP: 1200
};

/**
 * Verifica se o dispositivo está em modo de visualização mobile
 * @returns {boolean} true se a largura da tela for menor que o breakpoint de tablet
 */
export const isMobileView = () => {
  const { width } = Dimensions.get('window');
  return width < BREAKPOINTS.TABLET;
};

/**
 * Verifica se o dispositivo está em modo de visualização tablet
 * @returns {boolean} true se a largura da tela estiver entre os breakpoints de tablet e desktop
 */
export const isTabletView = () => {
  const { width } = Dimensions.get('window');
  return width >= BREAKPOINTS.TABLET && width < BREAKPOINTS.DESKTOP;
};

/**
 * Verifica se o dispositivo está em modo de visualização desktop
 * @returns {boolean} true se a largura da tela for maior que o breakpoint de desktop
 */
export const isDesktopView = () => {
  const { width } = Dimensions.get('window');
  return width >= BREAKPOINTS.DESKTOP;
};

/**
 * Hook para adicionar um listener para mudanças no tamanho da tela
 * @param {Function} callback - Função a ser chamada quando o tamanho da tela mudar
 * @returns {Function} Função para remover o listener
 */
export const addDimensionsListener = (callback) => {
  const listener = Dimensions.addEventListener('change', callback);
  
  return () => {
    if (listener?.remove) {
      listener.remove();
    }
  };
};

/**
 * Retorna estilos condicionais baseados no tamanho da tela atual
 * @param {Object} mobileStyles - Estilos para mobile
 * @param {Object} tabletStyles - Estilos para tablet
 * @param {Object} desktopStyles - Estilos para desktop
 * @returns {Object} Objeto de estilos baseado no tamanho da tela atual
 */
export const getResponsiveStyles = (mobileStyles = {}, tabletStyles = {}, desktopStyles = {}) => {
  if (isDesktopView()) {
    return { ...mobileStyles, ...tabletStyles, ...desktopStyles };
  } else if (isTabletView()) {
    return { ...mobileStyles, ...tabletStyles };
  }
  return mobileStyles;
};

/**
 * Verifica se o dispositivo é um dispositivo iOS
 * @returns {boolean}
 */
export const isIOS = Platform.OS === 'ios';

/**
 * Verifica se o dispositivo é um dispositivo Android
 * @returns {boolean}
 */
export const isAndroid = Platform.OS === 'android';

export default {
  BREAKPOINTS,
  isMobileView,
  isTabletView,
  isDesktopView,
  addDimensionsListener,
  getResponsiveStyles,
  isIOS,
  isAndroid
}; 