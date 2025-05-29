import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Dimensions } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { useAuth } from '../context/AuthContext';

const Footer = () => {
  const navigation = useNavigation();
  const { isAuthenticated, userData } = useAuth();
  const windowWidth = Dimensions.get('window').width;
  const isMobile = windowWidth < 768;

  const handleArtistRegistration = () => {
    if (!isAuthenticated) {
      navigation.navigate('Login');
      return;
    }

    navigation.navigate('ProfessionalRegister');
  };

  const handleLinkPress = (route) => {
    navigation.navigate(route);
  };

  const shouldShowArtistButton = !isAuthenticated || (userData?.role !== 'ROLE_PROF');

  return (
    <View style={styles.footer}>
      <View style={styles.container}>
        <View style={[styles.columnsContainer, !isMobile && styles.desktopColumns]}>
          <View style={[styles.column, !isMobile && styles.desktopColumn]}>
            <Text style={styles.title}>Inkspiration</Text>
            <Text style={styles.description}>
              Conectando entusiastas de tatuagem com artistas talentosos desde 2025.
            </Text>
          </View>

          <View style={[styles.column, !isMobile && styles.desktopColumn]}>
            <Text style={styles.subtitle}>Links Rápidos</Text>
            <View style={styles.linksList}>
              <TouchableOpacity onPress={() => handleLinkPress('Home')}>
                <Text style={styles.link}>Início</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => handleLinkPress('Explore')}>
                <Text style={styles.link}>Explorar Artistas</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => handleLinkPress('About')}>
                <Text style={styles.link}>Sobre Nós</Text>
              </TouchableOpacity>
            </View>
          </View>

          {shouldShowArtistButton && (
            <View style={[styles.column, !isMobile && styles.desktopColumn]}>
              <Text style={styles.subtitle}>Para Artistas</Text>
              <View style={styles.linksList}>
                <TouchableOpacity onPress={handleArtistRegistration}>
                  <Text style={styles.link}>Cadastre-se como Artista</Text>
                </TouchableOpacity>
              </View>
            </View>
          )}
        </View>

        <View style={styles.divider} />
        <Text style={styles.copyright}>
          © 2025 Inkspiration. Todos os direitos reservados.
        </Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  footer: {
    backgroundColor: '#f3f4f6',
    paddingTop: 24,
    paddingBottom: 16,
    width: '100%',
  },
  container: {
    paddingHorizontal: 16,
    maxWidth: 1200,
    marginHorizontal: 'auto',
    width: '100%',
  },
  columnsContainer: {
    flexDirection: 'column',
  },
  desktopColumns: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  column: {
    width: '100%',
    marginBottom: 32,
    paddingHorizontal: 8,
  },
  desktopColumn: {
    width: '30%',
  },
  title: {
    fontSize: 18,
    fontWeight: '700',
    marginBottom: 16,
    color: '#111827',
  },
  description: {
    fontSize: 14,
    color: '#6b7280',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    fontWeight: '500',
    marginBottom: 16,
    color: '#111827',
  },
  linksList: {
    marginTop: 8,
  },
  link: {
    fontSize: 14,
    color: '#6b7280',
    marginBottom: 12,
  },
  divider: {
    borderTopWidth: 1,
    borderTopColor: '#e5e7eb',
    marginTop: 16,
    paddingTop: 16,
  },
  copyright: {
    fontSize: 14,
    color: '#6b7280',
    textAlign: 'center',
  },
});

// Adicione media query para tablets e desktops usando responsividade
// Para tamanhos de tela maiores que 768px
const mediaQuery = {
  tablet: {
    columnsContainer: {
      flexDirection: 'row',
      justifyContent: 'space-between',
    },
    column: {
      width: '33.33%',
    },
  },
};

// Em uma implementação real, você precisaria de uma biblioteca como react-native-responsive-screen
// para aplicar estilos baseados no tamanho da tela

export default Footer; 