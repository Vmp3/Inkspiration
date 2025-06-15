import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TouchableOpacity,
  ActivityIndicator
} from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';
import { Feather } from '@expo/vector-icons';
import ApiService from '../services/ApiService';
import toastHelper from '../utils/toastHelper';
import textUtils from '../utils/textUtils';
import { professionalMessages } from '../components/professional/messages';

const ProfessionalProfileScreen = () => {
  const navigation = useNavigation();
  const route = useRoute();
  const { profissionalId } = route.params || {};
  
  const [loading, setLoading] = useState(true);
  const [profissional, setProfissional] = useState(null);
  const [portfolio, setPortfolio] = useState(null);
  const [imagens, setImagens] = useState([]);
  
  useEffect(() => {
    if (!profissionalId) {
      toastHelper.showError(professionalMessages.profileErrors.noId);
      navigation.goBack();
      return;
    }
    
    loadProfissionalData();
  }, [profissionalId]);
  
  const loadProfissionalData = async () => {
    try {
      setLoading(true);
      
      // Usar ApiService em vez de fetch
      const completeData = await ApiService.get(`/profissional/completo/${profissionalId}`);
      
      // Extrair dados organizados
      const { profissional: profData, portfolio, usuario, endereco, imagens } = completeData;
      
      setProfissional({
        ...profData,
        usuario,
        portifolio: portfolio,
        endereco
      });
      
      // Definir as imagens diretamente
      setImagens(imagens || []);
      
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
      toastHelper.showError(professionalMessages.profileErrors.loadProfile);
    } finally {
      setLoading(false);
    }
  };
  
  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#000" />
        <Text style={styles.loadingText}>{professionalMessages.loading.profile}</Text>
      </View>
    );
  }
  
  if (!profissional) {
    return (
      <View style={styles.errorContainer}>
        <Feather name="alert-circle" size={64} color="#ff4444" />
        <Text style={styles.errorText}>{professionalMessages.profileErrors.notFound}</Text>
        <TouchableOpacity
          style={styles.backButton}
          onPress={() => navigation.goBack()}
        >
          <Text style={styles.backButtonText}>Voltar</Text>
        </TouchableOpacity>
      </View>
    );
  }
  
  const renderEspecialidades = () => {
    if (!profissional.portifolio?.especialidade) return null;
    
    const especialidades = profissional.portifolio.especialidade.split(',').map(e => e.trim());
    
    return (
      <View style={styles.especialidadesContainer}>
        <Text style={styles.sectionTitle}>Especialidades</Text>
        <View style={styles.tagContainer}>
          {especialidades.map((esp, index) => (
            <View key={index} style={styles.tag}>
              <Text style={styles.tagText}>{esp}</Text>
            </View>
          ))}
        </View>
      </View>
    );
  };
  
  const renderPortfolio = () => {
    if (imagens.length === 0) {
      return (
        <View style={styles.emptyPortfolio}>
          <Feather name="image" size={32} color="#999" />
          <Text style={styles.emptyText}>{professionalMessages.info.noImages}</Text>
        </View>
      );
    }
    
    return (
      <View style={styles.portfolioGrid}>
        {imagens.map((imagem, index) => (
          <TouchableOpacity 
            key={index} 
            style={styles.portfolioItem}
            onPress={() => {
              // Implementar uma visualização ampliada da imagem
              console.log('Imagem clicada:', index);
            }}
          >
            <Image 
              source={{ uri: imagem.imagemBase64 }}
              style={styles.portfolioImage}
              resizeMode="cover"
            />
          </TouchableOpacity>
        ))}
      </View>
    );
  };
  
  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.contentContainer}>
      <View style={styles.header}>
        <TouchableOpacity 
          style={styles.backIcon} 
          onPress={() => navigation.goBack()}
        >
          <Feather name="arrow-left" size={24} color="#000" />
        </TouchableOpacity>
        
        <Text style={styles.title}>Perfil do Profissional</Text>
      </View>
      
      <View style={styles.profileHeader}>
        <View style={styles.profileImageContainer}>
          <Image 
            source={{ uri: profissional.usuario?.imagemPerfil || 'https://via.placeholder.com/150' }}
            style={styles.profileImage}
            resizeMode="cover"
          />
        </View>
        
        <View style={styles.profileInfo}>
          <Text style={styles.profileName} numberOfLines={2} ellipsizeMode="tail">
            {textUtils.truncateName(profissional.usuario?.nome || 'Nome não disponível', 30)}
          </Text>
          <Text style={styles.profileExperience}>Experiência: {profissional.portifolio?.experiencia || 'Não informada'}</Text>
          <View style={styles.ratingContainer}>
            <Feather name="star" size={16} color="#FFD700" />
            <Text style={styles.ratingText}>{profissional.nota?.toFixed(1) || '0.0'}</Text>
          </View>
        </View>
      </View>
      
      {renderEspecialidades()}
      
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Sobre</Text>
        <Text style={styles.bio}>{profissional.portifolio?.descricao || 'Nenhuma descrição disponível'}</Text>
      </View>
      
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Portfólio</Text>
        {renderPortfolio()}
      </View>
      
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Redes Sociais</Text>
        <View style={styles.socialContainer}>
          {profissional.portifolio?.instagram && (
            <TouchableOpacity style={styles.socialItem}>
              <Feather name="instagram" size={20} color="#000" />
              <Text style={styles.socialText} numberOfLines={1} ellipsizeMode="tail">
                {textUtils.truncateText(profissional.portifolio.instagram, 25)}
              </Text>
            </TouchableOpacity>
          )}
          
          {profissional.portifolio?.tiktok && (
            <TouchableOpacity style={styles.socialItem}>
              <Feather name="music" size={20} color="#000" />
              <Text style={styles.socialText} numberOfLines={1} ellipsizeMode="tail">
                {textUtils.truncateText(profissional.portifolio.tiktok, 25)}
              </Text>
            </TouchableOpacity>
          )}
          
          {profissional.portifolio?.facebook && (
            <TouchableOpacity style={styles.socialItem}>
              <Feather name="facebook" size={20} color="#000" />
              <Text style={styles.socialText} numberOfLines={1} ellipsizeMode="tail">
                {textUtils.truncateText(profissional.portifolio.facebook, 25)}
              </Text>
            </TouchableOpacity>
          )}
          
          {profissional.portifolio?.twitter && (
            <TouchableOpacity style={styles.socialItem}>
              <Feather name="twitter" size={20} color="#000" />
              <Text style={styles.socialText} numberOfLines={1} ellipsizeMode="tail">
                {textUtils.truncateText(profissional.portifolio.twitter, 25)}
              </Text>
            </TouchableOpacity>
          )}
          
          {profissional.portifolio?.website && (
            <TouchableOpacity style={styles.socialItem}>
              <Feather name="globe" size={20} color="#000" />
              <Text style={styles.socialText} numberOfLines={1} ellipsizeMode="tail">
                {textUtils.truncateText(profissional.portifolio.website, 25)}
              </Text>
            </TouchableOpacity>
          )}
          
          {!profissional.portifolio?.instagram && 
           !profissional.portifolio?.tiktok && 
           !profissional.portifolio?.facebook && 
           !profissional.portifolio?.twitter && 
           !profissional.portifolio?.website && (
            <Text style={styles.emptyText}>{professionalMessages.info.noSocialMedia}</Text>
          )}
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  contentContainer: {
    padding: 16,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: '#666',
  },
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#fff',
  },
  errorText: {
    fontSize: 18,
    color: '#333',
    marginTop: 12,
    marginBottom: 24,
    textAlign: 'center',
  },
  backButton: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    backgroundColor: '#000',
    borderRadius: 4,
  },
  backButtonText: {
    color: '#fff',
    fontWeight: '600',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 20,
  },
  backIcon: {
    padding: 8,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    marginLeft: 16,
  },
  profileHeader: {
    flexDirection: 'row',
    marginBottom: 24,
  },
  profileImageContainer: {
    width: 100,
    height: 100,
    borderRadius: 50,
    overflow: 'hidden',
    marginRight: 16,
  },
  profileImage: {
    width: '100%',
    height: '100%',
  },
  profileInfo: {
    flex: 1,
    justifyContent: 'center',
  },
  profileName: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  profileExperience: {
    fontSize: 14,
    color: '#666',
    marginBottom: 8,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  ratingText: {
    marginLeft: 4,
    fontSize: 16,
    fontWeight: '600',
  },
  especialidadesContainer: {
    marginBottom: 24,
  },
  tagContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: 8,
  },
  tag: {
    backgroundColor: '#f0f0f0',
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 20,
    marginRight: 8,
    marginBottom: 8,
  },
  tagText: {
    fontSize: 14,
    color: '#333',
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  bio: {
    fontSize: 16,
    lineHeight: 24,
    color: '#333',
  },
  portfolioGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -8,
  },
  portfolioItem: {
    width: '50%',
    padding: 8,
  },
  portfolioImage: {
    width: '100%',
    aspectRatio: 1,
    borderRadius: 8,
  },
  emptyPortfolio: {
    padding: 40,
    alignItems: 'center',
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    justifyContent: 'center',
  },
  emptyText: {
    fontSize: 16,
    color: '#999',
    textAlign: 'center',
    marginTop: 12,
  },
  socialContainer: {
    marginTop: 8,
  },
  socialItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  socialText: {
    marginLeft: 12,
    fontSize: 16,
    color: '#333',
  },
});

export default ProfessionalProfileScreen; 