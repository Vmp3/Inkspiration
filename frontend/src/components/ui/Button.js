import React from 'react';
import { TouchableOpacity, Text, StyleSheet, View } from 'react-native';

/**
 * Reusable Button component with primary (black) and secondary (white) variants
 * @param {Object} props Component properties
 * @param {string} props.variant 'primary' (black) or 'secondary' (white)
 * @param {function} props.onPress Function to call when button is pressed
 * @param {string} props.label Text to display in the button
 * @param {Object} props.style Additional styles to apply to the button
 * @param {Object} props.labelStyle Additional styles to apply to the label
 * @param {boolean} props.fullWidth Whether the button should take full width
 * @param {string} props.size 'sm', 'md', 'lg', or 'search' for different button sizes
 */
const Button = ({ 
  variant = 'primary', 
  onPress, 
  label, 
  style, 
  labelStyle,
  fullWidth = false,
  size = 'md',
  leftIcon,
  ...props 
}) => {
  // Determine button style based on variant and size
  const buttonStyles = [
    styles.button,
    variant === 'primary' ? styles.primaryButton : styles.secondaryButton,
    size === 'sm' ? styles.smallButton : 
    size === 'lg' ? styles.largeButton : 
    size === 'search' ? styles.searchButton : styles.mediumButton,
    fullWidth && styles.fullWidth,
    style
  ];

  // Determine label style based on variant and size
  const textStyles = [
    styles.buttonText,
    variant === 'primary' ? styles.primaryText : styles.secondaryText,
    size === 'sm' ? styles.smallText : 
    size === 'lg' ? styles.largeText : 
    size === 'search' ? styles.searchText : styles.mediumText,
    labelStyle
  ];

  return (
    <TouchableOpacity 
      style={buttonStyles} 
      onPress={onPress}
      activeOpacity={0.8}
      {...props}
    >
      <View style={styles.contentContainer}>
        {leftIcon && <View style={styles.iconContainer}>{leftIcon}</View>}
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
    backgroundColor: '#111827', // Black
  },
  secondaryButton: {
    backgroundColor: '#FFFFFF', // White
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
    height: 40, // Match the SearchInput height
    paddingVertical: 0, // Remove vertical padding to maintain the exact height
    paddingHorizontal: 16, // Smaller horizontal padding
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
    color: '#111827', // Changed from #6B7280 (gray) to #111827 (black)
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
    fontSize: 14, // Smaller text for search buttons
  }
});

export default Button; 