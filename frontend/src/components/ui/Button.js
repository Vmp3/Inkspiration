import React from 'react';
import { TouchableOpacity, Text, StyleSheet, View, ActivityIndicator } from 'react-native';

const Button = ({ 
  variant = 'primary', 
  onPress, 
  label, 
  style, 
  labelStyle,
  fullWidth = false,
  size = 'md',
  leftIcon,
  disabled = false,
  loading = false,
  ...props 
}) => {
  const isDisabled = disabled || loading;
  
  const buttonStyles = [
    styles.button,
    variant === 'primary' ? styles.primaryButton : styles.secondaryButton,
    size === 'sm' ? styles.smallButton : 
    size === 'lg' ? styles.largeButton : 
    size === 'search' ? styles.searchButton : styles.mediumButton,
    fullWidth && styles.fullWidth,
    isDisabled && (variant === 'primary' ? styles.primaryDisabled : styles.secondaryDisabled),
    style
  ];

  const textStyles = [
    styles.buttonText,
    variant === 'primary' ? styles.primaryText : styles.secondaryText,
    size === 'sm' ? styles.smallText : 
    size === 'lg' ? styles.largeText : 
    size === 'search' ? styles.searchText : styles.mediumText,
    isDisabled && (variant === 'primary' ? styles.primaryTextDisabled : styles.secondaryTextDisabled),
    labelStyle
  ];

  return (
    <TouchableOpacity 
      style={buttonStyles} 
      onPress={isDisabled ? undefined : onPress}
      activeOpacity={isDisabled ? 1 : 0.8}
      disabled={isDisabled}
      {...props}
    >
      <View style={styles.contentContainer}>
        {loading && (
          <ActivityIndicator 
            size="small" 
            color={variant === 'primary' ? '#FFFFFF' : '#111827'}
            style={styles.loadingIcon}
          />
        )}
        {!loading && leftIcon && <View style={styles.iconContainer}>{leftIcon}</View>}
        <Text style={textStyles}>{label}</Text>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    borderRadius: 6,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 24,
  },
  primaryButton: {
    backgroundColor: '#111827',
  },
  secondaryButton: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  fullWidth: {
    width: '100%',
  },
  contentContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  iconContainer: {
    marginRight: 8,
  },
  // Size variations
  smallButton: {
    paddingVertical: 8,
  },
  mediumButton: {
    paddingVertical: 12,
  },
  largeButton: {
    paddingVertical: 16,
  },
  searchButton: {
    height: 40,
    paddingVertical: 0,
    paddingHorizontal: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  // Text styles
  buttonText: {
    fontWeight: '600',
    textAlign: 'center',
  },
  primaryText: {
    color: '#FFFFFF',
  },
  secondaryText: {
    color: '#111827',
  },
  smallText: {
    fontSize: 14,
  },
  mediumText: {
    fontSize: 16,
  },
  largeText: {
    fontSize: 18,
  },
  searchText: {
    fontSize: 14,
  },
  primaryDisabled: {
    backgroundColor: '#9CA3AF', 
  },
  secondaryDisabled: {
    backgroundColor: '#F3F4F6', 
    borderWidth: 1,
    borderColor: '#D1D5DB',
  },
  primaryTextDisabled: {
    color: '#E5E7EB',
  },
  secondaryTextDisabled: {
    color: '#9CA3AF', 
  },
  loadingIcon: {
    marginRight: 8,
  },
});

export default Button; 