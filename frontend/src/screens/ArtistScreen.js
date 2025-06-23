import React, { useState, useEffect, useMemo } from 'react';
import { 
  View, 
  Text, 
  ScrollView, 
  StyleSheet, 
  Image, 
  TouchableOpacity, 
  FlatList, 
  Dimensions,
  Linking,
  Modal,
  ActivityIndicator
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { MaterialIcons, Feather, FontAwesome, Entypo, AntDesign } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';
import ProfessionalService from '../services/ProfessionalService';
import AgendamentoService from '../services/AgendamentoService';
import toastHelper from '../utils/toastHelper';
import textUtils from '../utils/textUtils';
import { formatCurrency } from '../utils/formatters';
import { artistMessages } from '../components/common/messages';
import { mockReviews } from '../data/reviews';
import DefaultUser from '../../assets/default_user.png'
import AvaliacaoService from '../services/AvaliacaoService';
import { formatDate } from '../utils/formatters';
import Avatar from '../components/ui/Avatar';

const Tabs = ({ tabs, activeTab, onTabChange }) => {
  return (
    <View style={styles.tabsContainer}>
      {tabs.map((tab) => (
        <TouchableOpacity
          key={tab.value}
          style={[
            styles.tabButton,
            activeTab === tab.value && styles.activeTabButton
          ]}
          onPress={() => onTabChange(tab.value)}
        >
          <Text 
            style={[
              styles.tabText,
              activeTab === tab.value && styles.activeTabText
            ]}
          >
            {tab.label}
          </Text>
        </TouchableOpacity>
      ))}
    </View>
  );
};

const Badge = ({ children }) => {
  return (
    <View style={styles.badge}>
      <Text style={styles.badgeText}>{children}</Text>
    </View>
  );
};

const Card = ({ children, style }) => {
  return (
    <View style={[styles.card, style]}>
      {children}
    </View>
  );
};

const PortfolioItem = ({ image, onPress }) => {
  // Função para processar a imagem base64
  const processBase64Image = (base64String) => {    
    // Se já tem o prefixo data:image, usar diretamente
    if (base64String.startsWith('data:image/')) {
      return base64String;
    }
    
    // Se não tem o prefixo, adicionar
    return `data:image/jpeg;base64,${base64String}`;
  };

  const imageUri = processBase64Image(image);

  return (
    <TouchableOpacity style={styles.portfolioItem} onPress={() => onPress(imageUri)} activeOpacity={0.8}>
      <Image
        source={{ uri: imageUri }}
        style={styles.portfolioImage}
        resizeMode="cover"
      />
      <View style={styles.portfolioPlaceholder}>
        <Feather name="image" size={24} color="#D1D5DB" />
      </View>
    </TouchableOpacity>
  );
};

const TikTokIcon = ({ size = 16, color = "#6B7280" }) => {
  return (
    <FontAwesome name="music" size={size} color={color} />
  );
};

const SocialMediaItem = ({ platform, username, onPress }) => {
  const getIcon = () => {
    switch (platform) {
      case 'instagram':
        return <AntDesign name="instagram" size={16} color="#6B7280" style={styles.socialIcon} />;
      case 'tiktok':
        return <FontAwesome name="music" size={16} color="#6B7280" style={styles.socialIcon} />;
      case 'facebook':
        return <AntDesign name="facebook-square" size={16} color="#6B7280" style={styles.socialIcon} />;
      case 'twitter':
        return <AntDesign name="twitter" size={16} color="#6B7280" style={styles.socialIcon} />;
      case 'website':
        return <Feather name="globe" size={16} color="#6B7280" style={styles.socialIcon} />;
      default:
        return <Feather name="link" size={16} color="#6B7280" style={styles.socialIcon} />;
    }
  };

  return (
    <TouchableOpacity 
      style={styles.socialItem} 
      onPress={onPress}
      activeOpacity={0.7}
    >
      {getIcon()}
      <Text style={styles.socialText} numberOfLines={1} ellipsizeMode="tail">
        {textUtils.truncateText(username, 20)}
      </Text>
    </TouchableOpacity>
  );
};

const ArtistScreen = ({ route }) => {
  const navigation = useNavigation();
  const { userData } = useAuth();
  const { artistId } = route.params || {};
  
  const [activeTab, setActiveTab] = useState('portfolio');
  const [isAdmin, setIsAdmin] = useState(false);
  const [deleteReviewId, setDeleteReviewId] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [artist, setArtist] = useState(null);
  const [portfolioImages, setPortfolioImages] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [screenData, setScreenData] = useState(Dimensions.get('window'));
  const [isSamePerson, setIsSamePerson] = useState(false);
  const [isLoadingReviews, setIsLoadingReviews] = useState(false);
  const [currentReviewPage, setCurrentReviewPage] = useState(0);
  const [hasMoreReviews, setHasMoreReviews] = useState(true);
  const [selectedImage, setSelectedImage] = useState(null);
  const [showImageModal, setShowImageModal] = useState(false);
  const [reviewCount, setReviewCount] = useState(0);
  const [avaliacoesStats, setAvaliacoesStats] = useState({
    totalAvaliacoes: 0,
    mediaAvaliacoes: 0,
    avaliacoesComComentario: 0,
    avaliacoesSemComentario: 0
  });
  
  const isMobile = screenData.width < 768;

  const mapServiceType = (serviceType) => {
    const serviceTypeMap = {
      'TATUAGEM_PEQUENA': 'Tatuagem Pequena',
      'TATUAGEM_MEDIA': 'Tatuagem Média', 
      'TATUAGEM_GRANDE': 'Tatuagem Grande',
      'SESSAO': 'Sessão'
    };
    
    return serviceTypeMap[serviceType] || serviceType;
  };

  useEffect(() => {
    const onChange = (result) => {
      setScreenData(result.window);
    };

    const subscription = Dimensions.addEventListener('change', onChange);
    
    return () => subscription?.remove();
  }, []);

  useEffect(() => {
    // Verifica se o usuário tem permissão de admin
    if (userData?.role === 'ROLE_ADMIN') {
      setIsAdmin(true);
    }

    if (!artistId) {
      toastHelper.showError(artistMessages.errors.noArtistId);
      navigation.navigate('Home');
      return;
    }

    // Carregar dados do profissional
    loadArtistData();
  }, [artistId, userData, navigation]);

  // useEffect para redirecionamento quando não encontra artista
  useEffect(() => {
    if (!artist && !isLoading) {
      const timeoutId = setTimeout(() => {
        navigation.navigate('Home');
      }, 1000);
      
      return () => clearTimeout(timeoutId);
    }
  }, [artist, isLoading, navigation]);

  const loadArtistData = async () => {
    try {
      setIsLoading(true);
      
      // Verificar se artistId está definido
      if (!artistId) {
        toastHelper.showError(artistMessages.errors.professionalIdNotFound);
        navigation.navigate('Home');
        return;
      }
      
      // Buscar dados completos do profissional
      const professionalData = await ProfessionalService.getProfessionalCompleteById(artistId);
      
      if (userData?.idUsuario === professionalData.profissional.idUsuario) {
        setIsSamePerson(true);
      }
      
      const transformedData = ProfessionalService.transformCompleteProfessionalData(professionalData);
      
      // Buscar imagens do portfólio
      const images = await ProfessionalService.getProfessionalImages(artistId);
      
      // Processar imagens base64
      const processedImages = images.map((img, index) => {
        return {
          id: index.toString(),
          imagemBase64: img.imagemBase64,
          idImagem: img.idImagem,
          idPortfolio: img.idPortfolio
        };
      });
      
      // Buscar preços dos serviços
      const servicesWithPrices = await AgendamentoService.buscarTiposServicoPorProfissional(artistId);
      
      const mappedServices = servicesWithPrices.length > 0 
        ? servicesWithPrices.map(service => ({
            name: mapServiceType(service.tipo),
            price: service.preco || 0
          }))
        : professionalData.profissional.tiposServico 
          ? professionalData.profissional.tiposServico.map(serviceType => ({
              name: mapServiceType(serviceType),
              price: 0
            }))
          : [];

      setArtist({
        ...transformedData,
        idProfissional: artistId,
        title: "Tatuador",
        bio: transformedData.description,
        reviewCount: reviewCount,
        profileImage: transformedData.coverImage,
        coverImage: transformedData.coverImage,
        portfolio: processedImages,
        services: mappedServices,
        rating: professionalData.profissional.nota || 0,
        social: {
          instagram: transformedData.instagram,
          facebook: transformedData.facebook,
          twitter: transformedData.twitter,
          tiktok: transformedData.tiktok,
          website: transformedData.website,
        },
      });
      
      setPortfolioImages(processedImages);
    } catch (error) {
      toastHelper.showError(artistMessages.errors.loadProfile);
      navigation.navigate('Home');
    } finally {
      setIsLoading(false);
    }
  };

  const loadReviews = async (page = 0, shouldRefresh = false) => {
    try {
      setIsLoadingReviews(true);
      if (!artist || !artist.idProfissional) {
        setReviews([]);
        setAvaliacoesStats({
          totalAvaliacoes: 0,
          mediaAvaliacoes: 0,
          avaliacoesComComentario: 0,
          avaliacoesSemComentario: 0
        });
        setReviewCount(0);
        return;
      }
      // Chamada correta
      const apiResponse = await AvaliacaoService.listarPorProfissional(artist.idProfissional, page);

      let newReviews = [];
      let totalAvaliacoes = 0;
      let avaliacoesComComentario = 0;
      let avaliacoesSemComentario = 0;

      if (apiResponse && apiResponse.content && Array.isArray(apiResponse.content)) {
        newReviews = apiResponse.content.map(review => ({
          id: review.idAvaliacao,
          userName: review.agendamento?.usuario?.nome || 'Usuário',
          userImage: review.agendamento?.usuario?.fotoPerfil || DefaultUser,
          rating: review.rating,
          comment: review.descricao,
          date: formatDate(new Date(review.agendamento?.dataHora)),
          tattooType: review.agendamento?.tipoTatuagem
        }));

        totalAvaliacoes = apiResponse.totalElements || 0;
        avaliacoesComComentario = newReviews.filter(r => r.comment && r.comment.trim() !== "").length;
        avaliacoesSemComentario = totalAvaliacoes - avaliacoesComComentario;

        setHasMoreReviews(apiResponse.hasNext || false);

        setAvaliacoesStats({
          totalAvaliacoes,
          mediaAvaliacoes: 0,
          avaliacoesComComentario,
          avaliacoesSemComentario
        });
      } else if (Array.isArray(apiResponse)) {
        // Fallback para estrutura antiga
        newReviews = apiResponse.map(review => ({
          id: review.idAvaliacao,
          userName: review.agendamento?.usuario?.nome || 'Usuário',
          userImage: review.agendamento?.usuario?.fotoPerfil || DefaultUser,
          rating: review.rating,
          comment: review.descricao,
          date: formatDate(new Date(review.agendamento?.dataHora)),
          tattooType: review.agendamento?.tipoTatuagem
        }));
        totalAvaliacoes = page === 0 ? apiResponse.length : reviewCount;
        avaliacoesComComentario = newReviews.filter(r => r.comment && r.comment.trim() !== "").length;
        avaliacoesSemComentario = totalAvaliacoes - avaliacoesComComentario;
        setHasMoreReviews(newReviews.length === 10);
        setAvaliacoesStats({
          totalAvaliacoes,
          mediaAvaliacoes: 0,
          avaliacoesComComentario,
          avaliacoesSemComentario
        });
      } else {
        newReviews = [];
        totalAvaliacoes = 0;
        avaliacoesComComentario = 0;
        avaliacoesSemComentario = 0;
        setHasMoreReviews(false);
        setAvaliacoesStats({
          totalAvaliacoes: 0,
          mediaAvaliacoes: 0,
          avaliacoesComComentario: 0,
          avaliacoesSemComentario: 0
        });
      }

      if (shouldRefresh || page === 0) {
        setReviews(newReviews);
      } else {
        setReviews(prev => [...prev, ...newReviews]);
      }

      setReviewCount(totalAvaliacoes);
      setCurrentReviewPage(page);
    } catch (error) {
      console.error('Erro ao carregar avaliações:', error);
      toastHelper.showError('Erro ao carregar avaliações');
      setReviews([]);
      setReviewCount(0);
      setHasMoreReviews(false);
      setAvaliacoesStats({
        totalAvaliacoes: 0,
        mediaAvaliacoes: 0,
        avaliacoesComComentario: 0,
        avaliacoesSemComentario: 0
      });
    } finally {
      setIsLoadingReviews(false);
    }
  };

  const handleLoadMoreReviews = () => {
    if (!isLoadingReviews && hasMoreReviews) {
      loadReviews(currentReviewPage + 1);
    }
  };

  const handleDeleteReview = async (reviewId) => {
    try {
      await AvaliacaoService.excluirAvaliacao(reviewId);
      toastHelper.showSuccess('Avaliação excluída com sucesso');
      loadReviews(0, true);
    } catch (error) {
      console.error('Erro ao excluir avaliação:', error);
      toastHelper.showError('Erro ao excluir avaliação');
    }
  };

  useEffect(() => {
    if (artist && artist.idProfissional) {
      loadReviews(0, true);
    }
  }, [artist?.idProfissional]);

  const openSocialLink = async (url) => {
    try {
      // Para website, garantir que tenha protocolo
      let finalUrl = url;
      if (url && !url.startsWith('http://') && !url.startsWith('https://')) {
        finalUrl = `https://${url}`;
      }
      
      const supported = await Linking.canOpenURL(finalUrl);
      if (supported) {
        await Linking.openURL(finalUrl);
      } else {
        toastHelper.showError(artistMessages.errors.openLink);
      }
    } catch (error) {
      toastHelper.showError(artistMessages.errors.linkError);
    }
  };

  const handleImagePress = (imageUri) => {
    setSelectedImage(imageUri);
    setShowImageModal(true);
  };

  const closeImageModal = () => {
    setShowImageModal(false);
    setSelectedImage(null);
  };

  // Renderizar o componente de carregamento
  const renderLoading = useMemo(() => (
    <View style={styles.loadingContainer}>
      <MaterialIcons name="hourglass-top" size={32} color="#6B7280" />
      <Text style={styles.loadingText}>Carregando perfil do profissional...</Text>
      <Text style={styles.loadingSubtext}>Aguarde enquanto buscamos as informações do artista</Text>
    </View>
  ), []);

  // Mostrar loading enquanto carrega os dados
  if (isLoading) {
    return renderLoading;
  }

  // Se não encontrou o artista
  if (!artist) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>Redirecionando...</Text>
      </View>
    );
  }

  const renderStars = (rating) => {
    // Arredondar para a meia estrela mais próxima
    const rounded = Math.round(rating * 2) / 2;
    const fullStars = Math.floor(rounded);
    const hasHalfStar = rounded - fullStars === 0.5;
    return (
      <View style={styles.starContainer}>
        {[...Array(5)].map((_, i) => {
          if (i < fullStars) {
            return (
              <MaterialIcons key={i} name="star" size={16} color="#FACC15" style={styles.filledStar} />
            );
          } else if (i === fullStars && hasHalfStar) {
            return (
              <MaterialIcons key={i} name="star-half" size={16} color="#FACC15" style={styles.filledStar} />
            );
          } else {
            return (
              <MaterialIcons key={i} name="star-border" size={16} color="#FACC15" />
            );
          }
        })}
      </View>
    );
  };

  const renderPortfolio = () => {
    return (
      <View style={styles.tabContent}>
        <View style={styles.portfolioGrid}>
          {portfolioImages.length === 0 ? (
            <View style={styles.noImagesContainer}>
              <Text style={styles.noImagesText}>{artistMessages.info.noImages}</Text>
            </View>
          ) : (
            portfolioImages.map((image, index) => (
              <PortfolioItem 
                key={index} 
                image={image.imagemBase64 || DefaultUser} 
                onPress={handleImagePress}
              />
            ))
          )}
        </View>
      </View>
    );
  };

  const renderReviews = () => {
    // Filtrar apenas avaliações com comentários
    const reviewsWithComments = reviews.filter(
      review => review.comment && review.comment.trim() !== ""
    );
    
    return (
      <View style={styles.tabContent}>
        <View style={styles.reviewsContainer}>
          <View style={styles.reviewHeader}>
            <Text style={styles.reviewsTitle}>Avaliações dos Clientes</Text>
            <View style={styles.ratingContainer}>
              <View style={styles.starsWrapper}>
                {renderStars(artist.rating)}
              </View>
              <Text style={styles.ratingValue}>{artist.rating} ({reviewCount} avaliações)</Text>
            </View>
          </View>

          {/* Estatísticas das avaliações */}
          <View style={isMobile ? styles.statsContainerMobile : styles.statsContainer}>
            <View style={isMobile ? styles.statItemMobile : styles.statItem}>
              <Text style={isMobile ? styles.statNumberMobile : styles.statNumber}>
                {avaliacoesStats?.totalAvaliacoes || 0}
              </Text>
              <Text style={isMobile ? styles.statLabelMobile : styles.statLabel}>Total</Text>
            </View>
            <View style={isMobile ? styles.statItemMobile : styles.statItem}>
              <Text style={isMobile ? styles.statNumberMobile : styles.statNumber}>
                {avaliacoesStats?.avaliacoesComComentario || 0}
              </Text>
              <Text style={isMobile ? styles.statLabelMobile : styles.statLabel}>Com Comentário</Text>
            </View>
            <View style={isMobile ? styles.statItemMobile : styles.statItem}>
              <Text style={isMobile ? styles.statNumberMobile : styles.statNumber}>
                {avaliacoesStats?.avaliacoesSemComentario || 0}
              </Text>
              <Text style={isMobile ? styles.statLabelMobile : styles.statLabel}>Sem Comentário</Text>
            </View>
          </View>

          {isLoadingReviews ? (
            <View style={{ alignItems: 'center', marginTop: 32 }}>
              <ActivityIndicator size="small" color="#111" />
              <Text style={{ marginTop: 12, color: '#6B7280' }}>Carregando avaliações...</Text>
            </View>
          ) : reviewsWithComments.length === 0 ? (
            <View style={{ alignItems: 'center', marginTop: 32 }}>
              <Text style={{ color: '#6B7280', fontSize: 16, textAlign: 'center' }}>
                {reviews.length === 0 
                  ? 'Ainda não há avaliações para este artista.'
                  : 'Não há avaliações com comentários para exibir.'
                }
              </Text>
            </View>
          ) : (
            <FlatList
              data={reviewsWithComments}
              keyExtractor={(item) => item.id.toString()}
              renderItem={({ item }) => (
                <View style={styles.reviewCard}>
                  <View style={styles.reviewHeader}>
                    <View style={styles.reviewerInfo}>
                      <Avatar
                        source={item.userImage && item.userImage !== DefaultUser ? item.userImage : null}
                        fallback={textUtils.getInitials(item.userName)}
                        size={40}
                        style={{ marginRight: 12 }}
                      />
                      <View>
                        <Text style={styles.reviewerName}>{item.userName}</Text>
                        <Text style={styles.reviewDate}>{item.date}</Text>
                      </View>
                    </View>
                    <View style={styles.reviewActions}>
                      {renderStars(item.rating)}
                      {isAdmin && (
                        <TouchableOpacity
                          style={styles.deleteButton}
                          onPress={() => handleDeleteReview(item.id)}
                        >
                          <Feather name="trash-2" size={16} color="#EF4444" />
                        </TouchableOpacity>
                      )}
                    </View>
                  </View>

                  <Text style={styles.reviewComment}>{item.comment}</Text>

                  <View style={styles.reviewFooter}>
                    <Text style={styles.serviceLabel}>Serviço:</Text>
                    <Text style={styles.serviceType}>{mapServiceType(item.tattooType)}</Text>
                  </View>
                </View>
              )}
              onEndReached={handleLoadMoreReviews}
              onEndReachedThreshold={0.1}
              ListFooterComponent={() => (
                isLoadingReviews ? (
                  <View style={styles.loadingMoreContainer}>
                    <ActivityIndicator size="small" color="#111" />
                    <Text style={styles.loadingMoreText}>Carregando mais avaliações...</Text>
                  </View>
                ) : null
              )}
              style={styles.reviewsList}
            />
          )}
        </View>
      </View>
    );
  };

  const renderAbout = () => {
    return (
      <View style={styles.tabContent}>
        <View style={styles.aboutContainer}>
          <View style={styles.aboutSection}>
            <Text style={styles.aboutTitle}>Sobre Mim</Text>
            <Text style={styles.aboutText}>{artist.bio}</Text>
          </View>
          <View style={styles.aboutSection}>
            <Text style={styles.aboutTitle}>Experiência</Text>
            <Text style={styles.aboutText}>{artist.experience}</Text>
          </View>
          <View style={styles.aboutSection}>
            <Text style={styles.aboutTitle}>Especialidades</Text>
            <View style={styles.specialtiesContainer}>
              {artist.specialties.map((specialty, index) => (
                <Badge key={index}>{specialty}</Badge>
              ))}
            </View>
          </View>
        </View>
      </View>
    );
  };

  const renderActiveTab = () => {
    switch (activeTab) {
      case 'portfolio':
        return renderPortfolio();
      case 'reviews':
        return renderReviews();
      case 'about':
        return renderAbout();
      default:
        return renderPortfolio();
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={[styles.pageWrapper, isMobile && styles.pageWrapperMobile]}>
        <View style={[styles.leftColumn, isMobile && styles.leftColumnMobile]}>
          <View style={styles.profileHeader}>
            <Image 
              source={{ uri: artist.profileImage }} 
              style={styles.profileImage}
            />
            
            <View style={styles.profileInfo}>
              <Text style={styles.artistName} numberOfLines={2} ellipsizeMode="tail">
                {textUtils.truncateName(artist.name, 15)}
              </Text>
              <Text style={styles.artistTitle}>{artist.title}</Text>
              <View style={styles.ratingRow}>
                {renderStars(artist.rating)}
                <Text style={styles.ratingText}>{artist.rating} ({reviewCount} avaliações)</Text>
              </View>
              <View style={styles.locationRow}>
                <Feather name="map-pin" size={16} color="#6B7280" />
                <Text style={styles.locationText} numberOfLines={1} ellipsizeMode="tail">
                  {textUtils.truncateText(artist.location, 30)}
                </Text>
              </View>
            </View>
          </View>

          {/* Especialidades em formato de tags */}
          <View style={styles.specialtiesRow}>
            {artist.specialties.map((specialty, index) => (
              <TouchableOpacity key={index} style={styles.specialtyTag}>
                <Text style={styles.specialtyText}>{specialty}</Text>
              </TouchableOpacity>
            ))}
          </View>

          {!isSamePerson && (
            <TouchableOpacity 
              style={styles.scheduleButton}
              onPress={() => {
                if (!userData) {
                  toastHelper.showError(artistMessages.errors.loginRequired);
                  navigation.navigate('Login');
                } else {
                  navigation.navigate('Booking', { professionalId: artist.idProfissional });
                }
              }}
            >
              <Feather name="calendar" size={20} color="#FFFFFF" style={styles.scheduleIcon} />
              <Text style={styles.scheduleText}>Agendar</Text>
            </TouchableOpacity>
          )}

          {/* Serviços */}
          <Card style={styles.servicesCard}>
            <Text style={styles.sectionTitle}>Serviços</Text>
            {artist.services.length === 0 ? (
              <Text style={styles.noServicesText}>Nenhum serviço cadastrado</Text>
            ) : (
              artist.services.map((service, index) => (
                <View key={index} style={styles.serviceItemContainer}>
                  <Text style={styles.serviceItem}>{service.name}</Text>
                  <Text style={styles.servicePrice}>
                    {formatCurrency(service.price || 0)}
                  </Text>
                </View>
              ))
            )}
          </Card>

          {(artist.social.instagram || artist.social.tiktok || artist.social.facebook || artist.social.twitter || artist.social.website) && (
            <Card style={styles.socialCard}>
              <Text style={styles.sectionTitle}>Redes Sociais</Text>
              <View style={styles.socialLinks}>
                {artist.social.instagram && (
                  <SocialMediaItem platform="instagram" username={artist.social.instagram} onPress={() => openSocialLink(`https://instagram.com/${artist.social.instagram}`)} />
                )}
                {artist.social.tiktok && (
                  <SocialMediaItem platform="tiktok" username={artist.social.tiktok} onPress={() => openSocialLink(`https://tiktok.com/@${artist.social.tiktok}`)} />
                )}
                {artist.social.facebook && (
                  <SocialMediaItem platform="facebook" username={artist.social.facebook} onPress={() => openSocialLink(`https://facebook.com/${artist.social.facebook}`)} />
                )}
                {artist.social.twitter && (
                  <SocialMediaItem platform="twitter" username={artist.social.twitter} onPress={() => openSocialLink(`https://twitter.com/${artist.social.twitter}`)} />
                )}
                {artist.social.website && (
                  <SocialMediaItem platform="website" username={artist.social.website} onPress={() => openSocialLink(artist.social.website)} />
                )}
              </View>
            </Card>
          )}
        </View>

        <View style={[styles.rightColumn, isMobile && styles.rightColumnMobile]}>
          <Tabs
            tabs={[
              { value: 'portfolio', label: 'Portfólio' },
              { value: 'reviews', label: 'Avaliações' },
              { value: 'about', label: 'Sobre' },
            ]}
            activeTab={activeTab}
            onTabChange={setActiveTab}
          />

          {/* Conteúdo da tab ativa */}
          {renderActiveTab()}
        </View>
      </View>

      {/* Modal de Imagem Ampliada */}
      <Modal
        visible={showImageModal}
        transparent={true}
        animationType="fade"
        onRequestClose={closeImageModal}
      >
        <View style={styles.imageModalContainer}>
          <TouchableOpacity 
            style={styles.imageModalBackdrop} 
            onPress={closeImageModal}
            activeOpacity={1}
          >
            <View style={styles.imageModalContent}>
              <TouchableOpacity 
                style={styles.closeButton} 
                onPress={closeImageModal}
                activeOpacity={0.7}
              >
                <Feather name="x" size={24} color="#FFFFFF" />
              </TouchableOpacity>
              
              {selectedImage && (
                <Image
                  source={{ uri: selectedImage }}
                  style={styles.expandedImage}
                  resizeMode="contain"
                />
              )}
            </View>
          </TouchableOpacity>
        </View>
      </Modal>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F9FAFB',
  },
  pageWrapper: {
    padding: 20,
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  leftColumn: {
    width: '100%',
    maxWidth: 320,
    marginRight: 20,
  },
  rightColumn: {
    flex: 1,
    minWidth: 300,
  },
  profileHeader: {
    marginBottom: 16,
    alignItems: 'center',
  },
  profileImage: {
    width: 140,
    height: 140,
    borderRadius: 70,
    marginBottom: 16,
  },
  profileInfo: {
    alignItems: 'center',
  },
  artistName: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 4,
  },
  artistTitle: {
    fontSize: 18,
    color: '#6B7280',
    marginBottom: 8,
  },
  ratingRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 8,
  },
  ratingText: {
    fontSize: 16,
    color: '#111827',
    marginLeft: 4,
  },
  locationRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  locationText: {
    fontSize: 14,
    color: '#6B7280',
    marginLeft: 4,
  },
  specialtiesRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 20,
    justifyContent: 'center',
  },
  specialtyTag: {
    backgroundColor: '#F3F4F6',
    borderRadius: 16,
    paddingVertical: 6,
    paddingHorizontal: 12,
    marginRight: 8,
    marginBottom: 8,
  },
  specialtyText: {
    fontSize: 14,
    color: '#4B5563',
  },
  scheduleButton: {
    backgroundColor: '#111827',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 20,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 24,
  },
  scheduleIcon: {
    marginRight: 8,
  },
  scheduleText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    padding: 20,
    marginBottom: 20,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 16,
  },
  serviceItemContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  serviceItem: {
    fontSize: 14,
    color: '#6B7280',
    flex: 1,
  },
  servicePrice: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
  },
  socialLinks: {
    marginTop: 4,
  },
  socialItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
    paddingVertical: 4,
    paddingHorizontal: 8,
    borderRadius: 6,
    backgroundColor: 'transparent',
  },
  socialIcon: {
    marginRight: 8,
  },
  socialText: {
    fontSize: 14,
    color: '#6B7280',
    marginLeft: 8,
    textDecorationLine: 'underline',
  },
  tabsContainer: {
    flexDirection: 'row',
    borderBottomWidth: 1,
    borderBottomColor: '#E5E7EB',
    marginBottom: 24,
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  tabButton: {
    flex: 1,
    paddingVertical: 12,
    alignItems: 'center',
  },
  activeTabButton: {
    borderBottomWidth: 2,
    borderBottomColor: '#111827',
  },
  tabText: {
    fontSize: 16,
    color: '#6B7280',
  },
  activeTabText: {
    color: '#111827',
    fontWeight: '500',
  },
  tabContent: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E5E7EB',
    padding: 0,
    overflow: 'hidden',
  },
  portfolioGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    padding: 4,
  },
  portfolioItem: {
    width: '20%',
    aspectRatio: 1,
    position: 'relative',
    backgroundColor: '#F3F4F6',
    padding: 3,
  },
  portfolioImage: {
    width: '100%',
    height: '100%',
    position: 'absolute',
    top: 2,
    left: 2,
    right: 2,
    bottom: 2,
    zIndex: 2,
    borderRadius: 4,
  },
  portfolioPlaceholder: {
    position: 'absolute',
    top: 2,
    left: 2,
    right: 2,
    bottom: 2,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F3F4F6',
    zIndex: 1,
    borderRadius: 4,
  },
  badge: {
    backgroundColor: '#F3F4F6',
    borderRadius: 16,
    paddingVertical: 6,
    paddingHorizontal: 12,
    marginRight: 8,
    marginBottom: 8,
  },
  badgeText: {
    fontSize: 14,
    color: '#4B5563',
  },
  reviewsContainer: {
    flex: 1,
    padding: 16,
  },
  reviewHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  reviewsTitle: {
    fontSize: 18,
    fontWeight: '600',
  },
  starContainer: {
    flexDirection: 'row',
  },
  filledStar: {
    color: '#FACC15',
  },
  starsWrapper: {
    flexDirection: 'row',
    marginRight: 8,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  ratingValue: {
    fontSize: 16,
    fontWeight: '600',
    marginRight: 4,
  },
  reviewsList: {
    flex: 1,
  },
  reviewCard: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    padding: 16,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  reviewerInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  reviewerImage: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 12,
  },
  reviewerName: {
    fontSize: 14,
    fontWeight: '500',
  },
  reviewDate: {
    fontSize: 12,
    color: '#6B7280',
  },
  reviewActions: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  deleteButton: {
    marginLeft: 8,
    padding: 4,
  },
  reviewComment: {
    marginVertical: 12,
    fontSize: 14,
    color: '#6B7280',
    lineHeight: 20,
  },
  reviewFooter: {
    flexDirection: 'row',
    borderTopWidth: 1,
    borderTopColor: '#E5E7EB',
    paddingTop: 12,
  },
  serviceLabel: {
    fontSize: 12,
    fontWeight: '500',
  },
  serviceType: {
    fontSize: 12,
    color: '#6B7280',
    marginLeft: 4,
  },
  aboutContainer: {
    flex: 1,
    padding: 16,
  },
  aboutSection: {
    marginBottom: 24,
  },
  aboutTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 8,
  },
  aboutText: {
    fontSize: 14,
    color: '#6B7280',
    lineHeight: 20,
  },
  specialtiesContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    fontSize: 16,
    color: '#6B7280',
    textAlign: 'center',
    marginTop: 16,
    marginBottom: 8,
  },
  loadingSubtext: {
    fontSize: 14,
    color: '#6B7280',
    textAlign: 'center',
    opacity: 0.8,
  },
  noImagesContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  noImagesText: {
    fontSize: 16,
    color: '#6B7280',
    textAlign: 'center',
  },
  noServicesText: {
    fontSize: 14,
    color: '#6B7280',
    fontStyle: 'italic',
  },
  pageWrapperMobile: {
    flexDirection: 'column',
    alignItems: 'center',
    padding: 16,
  },
  leftColumnMobile: {
    width: '100%',
    maxWidth: 320,
    marginRight: 0,
    marginBottom: 24,
    alignSelf: 'center',
  },
  rightColumnMobile: {
    flex: 1,
    minWidth: 300,
    width: '100%',
  },
  loadingMoreContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 16,
  },
  loadingMoreText: {
    fontSize: 14,
    color: '#6B7280',
    marginLeft: 8,
  },
  // Estilos do Modal de Imagem
  imageModalContainer: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.9)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  imageModalBackdrop: {
    flex: 1,
    width: '100%',
    justifyContent: 'center',
    alignItems: 'center',
  },
  imageModalContent: {
    width: '90%',
    height: '80%',
    position: 'relative',
  },
  closeButton: {
    position: 'absolute',
    top: 10,
    right: 10,
    zIndex: 3,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    borderRadius: 20,
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  expandedImage: {
    width: '100%',
    height: '100%',
    borderRadius: 8,
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
    paddingHorizontal: 8,
  },
  statItem: {
    flexDirection: 'column',
    alignItems: 'center',
    flex: 1,
    paddingVertical: 12,
    paddingHorizontal: 8,
    backgroundColor: '#F9FAFB',
    borderRadius: 8,
    marginHorizontal: 4,
  },
  statNumber: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#111827',
    marginBottom: 4,
  },
  statLabel: {
    fontSize: 12,
    color: '#6B7280',
    textAlign: 'center',
    fontWeight: '500',
  },
  statsContainerMobile: {
    flexDirection: 'column',
    gap: 8,
    marginBottom: 16,
  },
  statItemMobile: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    paddingHorizontal: 12,
    backgroundColor: '#F9FAFB',
    borderRadius: 8,
  },
  statNumberMobile: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#111827',
  },
  statLabelMobile: {
    fontSize: 14,
    color: '#6B7280',
    fontWeight: '500',
  },
});

export default ArtistScreen; 