import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const Badge = ({ variant = 'default', children, style, ...props }) => {
  const badgeStyles = [
    styles.badge,
    variant === 'admin' ? styles.adminBadge :
    variant === 'professional' ? styles.professionalBadge :
    variant === 'client' ? styles.clientBadge :
    variant === 'inactive' ? styles.inactiveBadge :
    styles.defaultBadge,
    style
  ];

  const textStyles = [
    styles.badgeText,
    variant === 'admin' ? styles.adminText :
    variant === 'professional' ? styles.professionalText :
    variant === 'client' ? styles.clientText :
    variant === 'inactive' ? styles.inactiveText :
    styles.defaultText,
  ];

  return (
    <View style={badgeStyles} {...props}>
      <Text style={textStyles}>{children}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  badge: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
    alignSelf: 'flex-start',
  },
  defaultBadge: {
    backgroundColor: '#F3F4F6',
  },
  adminBadge: {
    backgroundColor: '#7C3AED',
  },
  professionalBadge: {
    backgroundColor: '#3B82F6',
  },
  clientBadge: {
    backgroundColor: '#10B981',
  },
  inactiveBadge: {
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: '#EF4444',
  },
  badgeText: {
    fontSize: 12,
    fontWeight: '500',
  },
  defaultText: {
    color: '#374151',
  },
  adminText: {
    color: '#FFFFFF',
  },
  professionalText: {
    color: '#FFFFFF',
  },
  clientText: {
    color: '#FFFFFF',
  },
  inactiveText: {
    color: '#EF4444',
  },
});

export default Badge; 