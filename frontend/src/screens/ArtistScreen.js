import React, { useState, useEffect } from 'react';
import { 
  View, 
  Text, 
  ScrollView, 
  StyleSheet, 
  Image, 
  TouchableOpacity, 
  FlatList, 
  Dimensions 
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { MaterialIcons, Feather, FontAwesome, Entypo } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';

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

const PortfolioItem = ({ image }) => {
  return (
    <View style={styles.portfolioItem}>
      <Image
        source={{ uri: image }}
        style={styles.portfolioImage}
        resizeMode="cover"
      />
      <View style={styles.portfolioPlaceholder}>
        <Feather name="image" size={24} color="#D1D5DB" />
      </View>
    </View>
  );
};

const TikTokIcon = ({ size = 16, color = "#6B7280" }) => {
  return (
    <FontAwesome name="music" size={size} color={color} />
  );
};

const ArtistScreen = ({ route }) => {
  const navigation = useNavigation();
  const { userData } = useAuth();
  const { id } = route.params;
  const [activeTab, setActiveTab] = useState('portfolio');
  const [isAdmin, setIsAdmin] = useState(false);
  const [deleteReviewId, setDeleteReviewId] = useState(null);
  const [reviews, setReviews] = useState([]);
  const screenWidth = Dimensions.get('window').width;
  const isMobile = screenWidth < 768;

  useEffect(() => {
    // Verifica se o usuário tem permissão de admin
    if (userData?.role === 'ROLE_ADMIN') {
      setIsAdmin(true);
    }

    // Mock de avaliações
    setReviews([
      {
        id: "1",
        userName: "Carlos Mendes",
        userImage: "https://via.placeholder.com/50",
        rating: 5,
        comment:
          "Trabalho incrível! Alex é muito talentoso e cuidadoso. A tatuagem ficou exatamente como eu queria. Recomendo muito!",
        date: "2 semanas atrás",
        tattooType: "Tatuagem pequena",
      },
      {
        id: "2",
        userName: "Ana Oliveira",
        userImage: "https://via.placeholder.com/50",
        rating: 5,
        comment:
          "Experiência maravilhosa! O Alex é super profissional, o estúdio é muito limpo e o resultado final superou minhas expectativas.",
        date: "1 mês atrás",
        tattooType: "Tatuagem média",
      },
      {
        id: "3",
        userName: "Roberto Almeida",
        userImage: "https://via.placeholder.com/50",
        rating: 4,
        comment:
          "Muito bom! O Alex entendeu perfeitamente o que eu queria. O processo foi tranquilo e o resultado ficou ótimo.",
        date: "2 meses atrás",
        tattooType: "Tatuagem grande",
      },
      {
        id: "4",
        userName: "Fernanda Costa",
        userImage: "https://via.placeholder.com/50",
        rating: 5,
        comment:
          "Simplesmente perfeito! O Alex é um artista incrível, muito atencioso e detalhista. Já estou planejando minha próxima tatuagem com ele!",
        date: "3 meses atrás",
        tattooType: "Sessão dia inteiro",
      },
      {
        id: "5",
        userName: "Lucas Santos",
        userImage: "https://via.placeholder.com/50",
        rating: 5,
        comment:
          "Melhor tatuador da cidade! Trabalho impecável, ambiente super confortável e higienizado. Recomendo de olhos fechados.",
        date: "3 meses atrás",
        tattooType: "Tatuagem grande",
      },
      {
        id: "6",
        userName: "Juliana Lima",
        userImage: "https://via.placeholder.com/50",
        rating: 4,
        comment:
          "Ótima experiência! O Alex é muito profissional e talentoso. A tatuagem ficou linda, exatamente como eu imaginava.",
        date: "4 meses atrás",
        tattooType: "Tatuagem média",
      },
    ]);
  }, [userData]);

  const handleDeleteReview = (reviewId) => {
    const updatedReviews = reviews.filter((review) => review.id !== reviewId);
    setReviews(updatedReviews);
    alert('Avaliação excluída com sucesso');
  };

  // Mock de artista
  const artist = {
    id: id,
    name: "Alex Rivera",
    title: "Tatuador",
    bio: "Especializado em estilos tradicional e japonês com mais de 8 anos de experiência. Foco na criação de designs personalizados que contam uma história e refletem a personalidade dos meus clientes.",
    rating: 4.9,
    reviewCount: 6,
    specialties: ["Tradicional", "Japonês", "Neo-Tradicional", "Tatuagem"],
    location: "São Paulo, SP",
    experience: "8+ anos",
    profileImage: "https://via.placeholder.com/300",
    coverImage: "https://via.placeholder.com/1200x400",
    portfolio: [
      { id: "1", image: "https://via.placeholder.com/300" },
      { id: "2", image: "https://via.placeholder.com/300" },
      { id: "3", image: "https://via.placeholder.com/300" },
      { id: "4", image: "https://via.placeholder.com/300" },
      { id: "5", image: "https://via.placeholder.com/300" },
      { id: "6", image: "https://via.placeholder.com/300" },
    ],
    services: [
      { name: "Tatuagem Pequena (5-8 cm)" },
      { name: "Tatuagem Média (10-15 cm)" },
      { name: "Tatuagem Grande (18+ cm)" },
      { name: "Sessão Dia Inteiro (6-8 horas)" },
    ],
    social: {
      instagram: "@alexrivera_tattoo",
      facebook: "alexriveratattoo",
      twitter: "@alexrivera_ink",
      tiktok: "@alexrivera_tattoo",
      website: "alexrivera.com",
    },
  };

  const renderStars = (rating) => {
    return (
      <View style={styles.starContainer}>
        {[1, 2, 3, 4, 5].map((star) => (
          <MaterialIcons
            key={star}
            name="star"
            size={16}
            color={star <= rating ? "#FACC15" : "#D1D5DB"}
            style={star <= rating && styles.filledStar}
          />
        ))}
      </View>
    );
  };

  const renderPortfolio = () => {
    return (
      <View style={styles.tabContent}>
        <View style={styles.portfolioGrid}>
          {Array.from({ length: 6 }).map((_, index) => (
            <PortfolioItem key={index} image={"https://via.placeholder.com/300"} />
          ))}
        </View>
      </View>
    );
  };

  const renderReviews = () => {
    return (
      <View style={styles.tabContent}>
        <View style={styles.reviewsContainer}>
          <View style={styles.reviewHeader}>
            <Text style={styles.reviewsTitle}>Avaliações dos Clientes</Text>
            <View style={styles.ratingContainer}>
              <View style={styles.starsWrapper}>
                {[1, 2, 3, 4, 5].map((star) => (
                  <MaterialIcons
                    key={star}
                    name="star"
                    size={20}
                    color="#FACC15"
                    style={styles.filledStar}
                  />
                ))}
              </View>
              <Text style={styles.ratingValue}>{artist.rating}</Text>
              <Text style={styles.reviewCount}>({artist.reviewCount})</Text>
            </View>
          </View>

          <FlatList
            data={reviews}
            keyExtractor={(item) => item.id}
            renderItem={({ item }) => (
              <View style={styles.reviewCard}>
                <View style={styles.reviewHeader}>
                  <View style={styles.reviewerInfo}>
                    <Image
                      source={{ uri: item.userImage }}
                      style={styles.reviewerImage}
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
                  <Text style={styles.serviceType}>{item.tattooType}</Text>
                </View>
              </View>
            )}
            style={styles.reviewsList}
          />
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
      {/* Layout principal semelhante ao site - dividido em coluna esquerda e direita */}
      <View style={styles.pageWrapper}>
        {/* Coluna Esquerda - Informações do artista */}
        <View style={styles.leftColumn}>
          {/* Foto e informações básicas */}
          <View style={styles.profileHeader}>
            <Image 
              source={{ uri: artist.profileImage }} 
              style={styles.profileImage}
            />
            
            <View style={styles.profileInfo}>
              <Text style={styles.artistName}>{artist.name}</Text>
              <Text style={styles.artistTitle}>{artist.title}</Text>
              <View style={styles.ratingRow}>
                <MaterialIcons name="star" size={16} color="#FACC15" />
                <Text style={styles.ratingText}>{artist.rating} ({artist.reviewCount} reviews)</Text>
              </View>
              <View style={styles.locationRow}>
                <Feather name="map-pin" size={16} color="#6B7280" />
                <Text style={styles.locationText}>{artist.location}</Text>
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

          {/* Botão de agendar */}
          <TouchableOpacity 
            style={styles.scheduleButton}
            onPress={() => navigation.navigate('Booking', { id: artist.id, type: 'artist' })}
          >
            <Feather name="calendar" size={20} color="#FFFFFF" style={styles.scheduleIcon} />
            <Text style={styles.scheduleText}>Agendar</Text>
          </TouchableOpacity>

          {/* Serviços */}
          <Card style={styles.servicesCard}>
            <Text style={styles.sectionTitle}>Serviços</Text>
            {artist.services.map((service, index) => (
              <Text key={index} style={styles.serviceItem}>{service.name}</Text>
            ))}
          </Card>

          {/* Redes Sociais */}
          <Card style={styles.socialCard}>
            <Text style={styles.sectionTitle}>Redes Sociais</Text>
            <View style={styles.socialLinks}>
              {artist.social.instagram && (
                <View style={styles.socialItem}>
                  <Feather name="instagram" size={16} color="#6B7280" style={styles.socialIcon} />
                  <Text style={styles.socialText}>{artist.social.instagram}</Text>
                </View>
              )}
              {artist.social.tiktok && (
                <View style={styles.socialItem}>
                  <TikTokIcon />
                  <Text style={styles.socialText}>{artist.social.tiktok}</Text>
                </View>
              )}
              {artist.social.facebook && (
                <View style={styles.socialItem}>
                  <Feather name="facebook" size={16} color="#6B7280" style={styles.socialIcon} />
                  <Text style={styles.socialText}>{artist.social.facebook}</Text>
                </View>
              )}
              {artist.social.twitter && (
                <View style={styles.socialItem}>
                  <Feather name="twitter" size={16} color="#6B7280" style={styles.socialIcon} />
                  <Text style={styles.socialText}>{artist.social.twitter}</Text>
                </View>
              )}
              {artist.social.website && (
                <View style={styles.socialItem}>
                  <Feather name="globe" size={16} color="#6B7280" style={styles.socialIcon} />
                  <Text style={styles.socialText}>{artist.social.website}</Text>
                </View>
              )}
            </View>
          </Card>
        </View>

        {/* Coluna Direita - Tabs e conteúdo */}
        <View style={styles.rightColumn}>
          {/* Tabs */}
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
  serviceItem: {
    fontSize: 14,
    color: '#6B7280',
    marginBottom: 8,
  },
  socialLinks: {
    marginTop: 4,
  },
  socialItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  socialIcon: {
    marginRight: 8,
  },
  socialText: {
    fontSize: 14,
    color: '#6B7280',
    marginLeft: 8,
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
  },
  portfolioItem: {
    width: '33.33%',
    aspectRatio: 1,
    position: 'relative',
  },
  portfolioImage: {
    width: '100%',
    height: '100%',
  },
  portfolioPlaceholder: {
    ...StyleSheet.absoluteFillObject,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F3F4F6',
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
});

export default ArtistScreen; 