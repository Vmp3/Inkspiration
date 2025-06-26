import React, { useState } from 'react';
import { View, Text, Image, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

/**
 * Componente de imagem com texto alternativo
 * Mostra uma descrição quando a imagem não pode ser carregada
 * @param {Object} props - Propriedades do componente
 * @param {Object} props.source - Fonte da imagem
 * @param {string} props.alt - Texto alternativo para quando a imagem não carrega
 * @param {Object} props.style - Estilos para a imagem
 * @param {string} props.resizeMode - Modo de redimensionamento da imagem
 * @param {string} props.accessibilityLabel - Label de acessibilidade
 * @param {boolean} props.accessible - Se a imagem é acessível
 * @param {Function} props.onError - Callback para erro de carregamento
 * @param {Function} props.onLoad - Callback para carregamento bem-sucedido
 * @param {Object} props.fallbackStyle - Estilos para o fallback
 * @param {string} props.fallbackIconName - Nome do ícone para o fallback
 * @param {number} props.fallbackIconSize - Tamanho do ícone para o fallback
 * @param {string} props.fallbackIconColor - Cor do ícone para o fallback
 */
const ImageWithAlt = ({ 
  source, 
  alt, 
  style, 
  resizeMode = 'cover',
  accessibilityLabel,
  accessible = true,
  onError,
  onLoad,
  fallbackStyle,
  fallbackIconName = 'broken-image',
  fallbackIconSize = 24,
  fallbackIconColor = '#9CA3AF',
  ...props 
}) => {
  const [hasError, setHasError] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const handleError = (error) => {
    setHasError(true);
    setIsLoading(false);
    if (onError) {
      onError(error);
    }
  };

  const handleLoad = () => {
    setIsLoading(false);
    if (onLoad) {
      onLoad();
    }
  };

  const containerStyle = [
    styles.container,
    style,
    hasError && styles.fallbackContainer
  ];

  const fallbackTextStyle = [
    styles.fallbackText,
    fallbackStyle
  ];

  if (hasError) {
    return (
      <View style={containerStyle} {...props}>
        <MaterialIcons 
          name={fallbackIconName} 
          size={fallbackIconSize} 
          color={fallbackIconColor} 
          style={styles.fallbackIcon}
        />
        {alt && (
          <Text style={fallbackTextStyle} numberOfLines={3} ellipsizeMode="tail">
            {alt}
          </Text>
        )}
      </View>
    );
  }

  return (
    <Image
      source={source}
      style={style}
      resizeMode={resizeMode}
      accessible={accessible}
      accessibilityLabel={accessibilityLabel || alt}
      onError={handleError}
      onLoad={handleLoad}
      {...props}
    />
  );
};

const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F9FAFB',
    borderWidth: 1,
    borderColor: '#E5E7EB',
    borderStyle: 'dashed',
  },
  fallbackContainer: {
    padding: 12,
  },
  fallbackIcon: {
    marginBottom: 8,
  },
  fallbackText: {
    fontSize: 12,
    color: '#6B7280',
    textAlign: 'center',
    lineHeight: 16,
  },
});

export default ImageWithAlt; 