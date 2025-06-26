import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import ImageWithAlt from './ImageWithAlt';

const Avatar = ({ source, fallback, size = 48, style, alt, ...props }) => {
  const avatarStyles = [
    styles.avatar,
    { width: size, height: size, borderRadius: size / 2 },
    style
  ];

  const fallbackStyles = [
    styles.fallback,
    { fontSize: size * 0.4 }
  ];

  return (
    <View style={avatarStyles} {...props}>
      {source ? (
        <ImageWithAlt 
          source={{ uri: source }} 
          alt={alt || "Foto de perfil do usuário"}
          style={[styles.image, { width: size, height: size, borderRadius: size / 2 }]}
          accessibilityLabel={alt || "Foto de perfil do usuário"}
          fallbackIconName="person"
          fallbackIconSize={size * 0.6}
          fallbackStyle={[styles.fallback, { fontSize: size * 0.25 }]}
        />
      ) : (
        <Text style={fallbackStyles}>{fallback}</Text>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  avatar: {
    backgroundColor: '#F3F4F6',
    justifyContent: 'center',
    alignItems: 'center',
    overflow: 'hidden',
  },
  image: {
    resizeMode: 'cover',
  },
  fallback: {
    color: '#6B7280',
    fontWeight: '600',
    textAlign: 'center',
  },
});

export default Avatar; 