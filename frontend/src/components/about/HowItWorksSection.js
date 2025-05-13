import React from 'react';
import { View, StyleSheet } from 'react-native';
import SectionTitle from './SectionTitle';
import FeatureCard from './FeatureCard';

/**
 * How It Works section component for the About page
 * @param {Object} props Component properties
 * @param {boolean} props.isMobile Whether the screen is in mobile view
 */
const HowItWorksSection = ({ isMobile }) => {
  const cardsContainerStyle = [
    styles.cardsContainer,
    !isMobile && { flexDirection: 'row' }
  ];

  const cardStyle = [
    !isMobile && { flex: 1, margin: 8 }
  ];

  return (
    <View style={styles.section}>
      <SectionTitle centered>Como Funciona</SectionTitle>
      
      <View style={cardsContainerStyle}>
        <FeatureCard
          style={cardStyle}
          icon="search"
          title="Busque"
          description="Encontre artistas e estúdios por localização, estilo ou avaliações. Filtre por especialidades para encontrar o profissional ideal para sua ideia."
        />
        
        <FeatureCard
          style={cardStyle}
          icon="palette"
          title="Explore"
          description="Navegue por portfólios, veja trabalhos anteriores e leia avaliações de outros clientes para tomar a melhor decisão."
        />
        
        <FeatureCard
          style={cardStyle}
          icon="event"
          title="Agende"
          description="Marque sua sessão diretamente pela plataforma, converse com o artista sobre seu projeto e prepare-se para sua nova tatuagem."
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  section: {
    padding: 32,
    backgroundColor: '#f3f4f6',
  },
  cardsContainer: {
    flexDirection: 'column',
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
    marginTop: 32,
  }
});

export default HowItWorksSection; 