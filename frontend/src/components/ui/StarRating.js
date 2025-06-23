import React, { useState } from 'react';
import { View, TouchableOpacity, Animated } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

/**
 * Componente de estrelas reutilizável
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
  highlightColor = '#FFB300', // cor de destaque ao passar o mouse/tocar
}) => {
  const [hovered, setHovered] = useState(null); // null ou índice da estrela

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
      {[0, 1, 2, 3, 4].map((index) => {
        if (editable) {
          const isHighlighted = hovered !== null ? index <= hovered : index < value;
          return (
            <TouchableOpacity
              key={index}
              onPress={() => onChange(index + 1)}
              activeOpacity={0.7}
              onPressIn={() => setHovered(index)}
              onPressOut={() => setHovered(null)}
              onMouseEnter={() => setHovered(index)}
              onMouseLeave={() => setHovered(null)}
            >
              <Animated.View style={{
                transform: [{ scale: isHighlighted ? 1.15 : 1 }],
              }}>
                <MaterialIcons
                  name={isHighlighted ? 'star' : 'star-border'}
                  size={size}
                  color={isHighlighted ? highlightColor : emptyColor}
                />
              </Animated.View>
            </TouchableOpacity>
          );
        } else {
          return renderStar(index);
        }
      })}
    </View>
  );
};

export default StarRating; 