import React from 'react';
import { View, StyleSheet } from 'react-native';
import SectionTitle from './SectionTitle';
import BenefitItem from './BenefitItem';

/**
 * Why Choose section component for the About page
 * @param {Object} props Component properties
 * @param {boolean} props.isMobile Whether the screen is in mobile view
 */
const WhyChooseSection = ({ isMobile }) => {
  const benefitsContainerStyle = [
    styles.benefitsContainer,
    !isMobile && { flexDirection: 'row' }
  ];
  
  const benefitItemStyle = [
    styles.benefitItem,
    !isMobile && { width: '25%' }
  ];

  const benefits = [
    {
      icon: "people",
      title: "Comunidade",
      description: "Uma comunidade vibrante de artistas e entusiastas de tatuagem.",
    },
    {
      icon: "verified",
      title: "Qualidade",
      description: "Artistas verificados e avaliados pela comunidade.",
    },
    {
      icon: "access-time",
      title: "Conveniência",
      description: "Agendamento online fácil e rápido.",
    }
  ];

  return (
    <View style={styles.section}>
      <SectionTitle centered>Por Que Escolher o Inkspiration</SectionTitle>
      
      <View style={benefitsContainerStyle}>
        {benefits.map((benefit, index) => (
          <BenefitItem
            key={index}
            icon={benefit.icon}
            title={benefit.title}
            description={benefit.description}
            style={benefitItemStyle}
          />
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  section: {
    padding: 32,
    backgroundColor: '#fff',
  },
  benefitsContainer: {
    flexDirection: 'column',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
    marginTop: 32,
  },
  benefitItem: {
    width: '100%'
  }
});

export default WhyChooseSection; 