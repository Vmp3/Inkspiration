import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Button from '../ui/Button';

/**
 * Call to Action section component for the About page
 * @param {Object} props Component properties
 * @param {function} props.onCreateAccountPress Function to call when Create Account button is pressed
 * @param {function} props.onExplorePress Function to call when Explore button is pressed
 * @param {boolean} props.isMobile Whether the screen is in mobile view
 */
const CTASection = ({ onCreateAccountPress, onExplorePress, isMobile }) => {
  return (
    <View style={styles.section}>
      <View style={styles.content}>
        <Text style={styles.title}>Pronto para Encontrar seu Artista Ideal?</Text>
        <Text style={styles.subtitle}>
          Junte-se a milhares de pessoas que já encontraram o artista perfeito para suas tatuagens
        </Text>
        <View style={styles.buttonsContainer}>
          <View style={styles.buttonWrapper}>
            <Button
              variant="primary"
              label="Criar Conta"
              onPress={onCreateAccountPress}
              size="md"
              style={styles.button}
              fullWidth={true}
            />
          </View>
          <View style={styles.buttonWrapper}>
            <Button
              variant="secondary"
              label="Explorar"
              onPress={onExplorePress}
              size="md"
              style={styles.button}
              fullWidth={true}
            />
          </View>
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  section: {
    backgroundColor: '#f3f4f6',
    paddingVertical: 48,
  },
  content: {
    paddingHorizontal: 16,
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#111827',
    textAlign: 'center',
  },
  subtitle: {
    fontSize: 16,
    color: '#6b7280',
    marginBottom: 24,
    textAlign: 'center',
    maxWidth: 600,
  },
  buttonsContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    width: '100%',
    maxWidth: 480,
    gap: 16,
  },
  buttonWrapper: {
    flex: 1,
    maxWidth: 200,
  },
  button: {
    paddingHorizontal: 24,
  }
});

export default CTASection; 