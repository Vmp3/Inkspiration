import React from 'react';
import { View, TouchableOpacity } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

/**
 *
 * Props:
 * - value: número (pode ser decimal para visualização, ex: 4.5)
 * - editable: boolean (se pode clicar para selecionar)
 * - onChange: função (callback ao clicar em uma estrela)
 * - size: número (tamanho das estrelas)
 * - color: cor das estrelas preenchidas
 * - emptyColor: cor das estrelas vazias
 * - halfColor: cor das meias estrelas (opcional)
 */
const StarRating = ({
  value = 0,
  editable = false,
  onChange = () => {},
  size = 32,
  color = '#FFD700',
  emptyColor = '#E5E7EB',
  halfColor = '#FFD700',
  style = {},
}) => {
  // Para visualização: pode ter meia estrela
  // Para edição: só permite seleção de inteiros
  const renderStar = (index) => {
    if (value >= index + 1) {
      // Estrela cheia
      return <MaterialIcons key={index} name="star" size={size} color={color} />;
    } else if (value >= index + 0.5) {
      // Meia estrela (visualização)
      return <MaterialIcons key={index} name="star-half" size={size} color={halfColor} />;
    } else {
      // Estrela vazia
      return <MaterialIcons key={index} name="star-border" size={size} color={emptyColor} />;
    }
  };

  return (
    <View style={[{ flexDirection: 'row', alignItems: 'center' }, style]}>
      {[0, 1, 2, 3, 4].map((index) => (
        editable ? (
          <TouchableOpacity
            key={index}
            onPress={() => onChange(index + 1)}
            activeOpacity={0.7}
          >
            <MaterialIcons
              name={value >= index + 1 ? 'star' : 'star-border'}
              size={size}
              color={value >= index + 1 ? color : emptyColor}
            />
          </TouchableOpacity>
        ) : (
          renderStar(index)
        )
      ))}
    </View>
  );
};

export default StarRating; 