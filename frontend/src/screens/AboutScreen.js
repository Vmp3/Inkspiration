import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Image,
  Dimensions,
  SafeAreaView
} from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { MaterialIcons } from '@expo/vector-icons';
import Footer from '../components/Footer';
import HeroSection from '../components/about/HeroSection';
import MissionSection from '../components/about/MissionSection';
import HowItWorksSection from '../components/about/HowItWorksSection';
import WhyChooseSection from '../components/about/WhyChooseSection';
import CTASection from '../components/about/CTASection';

const AboutScreen = ({ navigation }) => {
  const [screenWidth, setScreenWidth] = useState(Dimensions.get('window').width);
  
  // Detectar tamanho da tela para responsividade
  useEffect(() => {
    const updateLayout = () => {
      const { width } = Dimensions.get('window');
      setScreenWidth(width);
    };
    
    updateLayout();
    const dimensionsHandler = Dimensions.addEventListener('change', updateLayout);
    
    return () => {
      if (dimensionsHandler?.remove) {
        dimensionsHandler.remove();
      }
    };
  }, []);
  
  // Valores derivados baseados na largura da tela
  const isMobile = screenWidth < 768;
  const isTablet = screenWidth >= 768 && screenWidth < 1024;
  const isDesktop = screenWidth >= 1024;

  // Navigation handlers
  const handleExplorePress = () => {
    navigation.navigate('Explore');
  };
  
  const handleCreateAccountPress = () => {
    navigation.navigate('Register');
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />
      
      <View style={styles.pageWrapper}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <HeroSection onExplorePress={handleExplorePress} />
          <MissionSection isMobile={isMobile} />
          <HowItWorksSection isMobile={isMobile} />
          <WhyChooseSection isMobile={isMobile} />
          <CTASection 
            isMobile={isMobile}
            onCreateAccountPress={handleCreateAccountPress}
            onExplorePress={handleExplorePress}
          />
          <View style={styles.footerSpacer} />
          <Footer />
        </ScrollView>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  pageWrapper: {
    flex: 1,
  },
  scrollContent: {
    flexGrow: 1,
    flexDirection: 'column',
  },
  footerSpacer: {
    flex: 1,
    minHeight: 20,
  },
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
  section: {
    padding: 32,
    backgroundColor: '#fff',
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#111827',
  },
  centeredTitle: {
    textAlign: 'center',
  },
  sectionText: {
    fontSize: 16,
    color: '#6b7280',
    marginBottom: 16,
    lineHeight: 24,
  },
  missionContainer: {
    flexDirection: 'column',
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
    backgroundColor: '#fff',
  },
  missionTextContainer: {
    flex: 1,
    paddingHorizontal: 16,
  },
  missionImageContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 16,
  },
  imagePlaceholder: {
    width: '100%',
    aspectRatio: 4/3,
    backgroundColor: '#f3f4f6',
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },
  checklistContainer: {
    marginTop: 16,
  },
  checklistItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 12,
  },
  checkIcon: {
    marginRight: 8,
    marginTop: 2,
  },
  checklistText: {
    flex: 1,
    fontSize: 16,
    color: '#374151',
  },
  howItWorksSection: {
    backgroundColor: '#f3f4f6',
  },
  cardsContainer: {
    flexDirection: 'column',
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
    marginTop: 32,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 24,
    marginBottom: 16,
    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
    elevation: 2,
    alignItems: 'center',
  },
  cardIconContainer: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: '#eff6ff',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 16,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#111827',
    textAlign: 'center',
  },
  cardText: {
    fontSize: 14,
    color: '#6b7280',
    textAlign: 'center',
    lineHeight: 20,
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
    width: '100%',
    marginBottom: 24,
    alignItems: 'center',
  },
  benefitIcon: {
    marginBottom: 12,
  },
  benefitTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#111827',
    textAlign: 'center',
  },
  benefitText: {
    fontSize: 14,
    color: '#6b7280',
    textAlign: 'center',
  },
  ctaSection: {
    backgroundColor: '#f3f4f6',
    paddingVertical: 48,
  },
  ctaContent: {
    paddingHorizontal: 16,
    maxWidth: 1200,
    width: '100%',
    alignSelf: 'center',
    alignItems: 'center',
  },
  ctaTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#111827',
    textAlign: 'center',
  },
  ctaSubtitle: {
    fontSize: 16,
    color: '#6b7280',
    marginBottom: 24,
    textAlign: 'center',
    maxWidth: 600,
  },
  ctaButtons: {
    flexDirection: 'column',
    alignItems: 'center',
    width: '100%',
    maxWidth: 400,
  },
  primaryButton: {
    backgroundColor: '#6366f1',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 6,
    marginBottom: 12,
    width: '100%',
    alignItems: 'center',
  },
  primaryButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  secondaryButton: {
    backgroundColor: '#fff',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: '#e5e7eb',
    width: '100%',
    alignItems: 'center',
  },
  secondaryButtonText: {
    color: '#6b7280',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default AboutScreen; 