import React from 'react';
import { View, Text, Image, StyleSheet } from 'react-native';

const Avatar = ({ source, fallback, size = 48, style, ...props }) => {
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
        <Image 
          source={{ uri: source }} 
          style={[styles.image, { width: size, height: size, borderRadius: size / 2 }]}
          onError={() => {
          }}
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