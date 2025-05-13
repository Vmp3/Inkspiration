import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Button from '../ui/Button';

/**
 * Hero section component for the About page
 * @param {Object} props Component properties
 * @param {function} props.onExplorePress Function to call when Explore button is pressed
 */
const HeroSection = ({ onExplorePress }) => {
  return (
    <View style={styles.heroSection}>
      <View style={styles.heroContent}>
        <Text style={styles.heroTitle}>Sobre o Inkspiration</Text>
        <Text style={styles.heroSubtitle}>
          Conectando entusiastas de tatuagem com os melhores artistas e estúdios do Brasil
        </Text>
        <View style={styles.buttonContainer}>
          <Button 
            variant="primary"
            label="Explorar Artistas"
            onPress={onExplorePress}
            size="md"
            style={styles.exploreButton}
            fullWidth={true}
          />
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  heroSection: {
    paddingVertical: 64,
    backgroundColor: '#f8fafc',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  heroContent: {
    paddingHorizontal: 16,
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
    alignItems: 'center',
  },
  heroTitle: {
    fontSize: 32,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 16,
    color: '#111827',
  },
  heroSubtitle: {
    fontSize: 18,
    textAlign: 'center',
    marginBottom: 24,
    color: '#6b7280',
    maxWidth: 600,
  },
  buttonContainer: {
    width: 200,
  },
  exploreButton: {
    paddingHorizontal: 24,
  }
});

export default HeroSection; 