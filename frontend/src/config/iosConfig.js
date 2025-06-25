import { Platform, Dimensions } from 'react-native';

export const iosKeyboardConfig = {
  // Configurações específicas para iOS
  keyboardDismissMode: 'on-drag',
  keyboardShouldPersistTaps: 'handled',
  automaticallyAdjustContentInsets: false,
  bounces: false,
  bouncesZoom: false,
  alwaysBounceVertical: false,
  alwaysBounceHorizontal: false,
  maximumZoomScale: 1.0,
  minimumZoomScale: 1.0,
  showsVerticalScrollIndicator: true,
  showsHorizontalScrollIndicator: false,
  scrollEnabled: true,
  directionalLockEnabled: true,
  maintainVisibleContentPosition: {
    minHeight: 44,
    autoscrollToTopThreshold: 100,
  },
};

export const getKeyboardVerticalOffset = () => {
  if (Platform.OS === 'ios') {
    const { height } = Dimensions.get('window');
    return height < 700 ? 40 : 64; // Ajuste para diferentes tamanhos de tela
  }
  return 0;
};

export const getScrollViewProps = () => {
  if (Platform.OS === 'ios') {
    return {
      ...iosKeyboardConfig,
      contentInsetAdjustmentBehavior: 'never',
      automaticallyAdjustContentInsets: false,
      contentContainerStyle: {
        flexGrow: 1,
        paddingBottom: 20,
      },
    };
  }
  return {};
}; 