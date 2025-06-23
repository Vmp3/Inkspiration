import React from 'react';
import { View, Text, Image, StyleSheet, TouchableOpacity } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { useNavigation } from '@react-navigation/native';
import { Platform } from 'react-native';
import DefaultUser from '../../assets/default_user.png'
import StarRating from './ui/StarRating';

const ArtistCard = ({ artist }) => {
  const { id, name, rating, specialties, location, coverImage } = artist;
  const navigation = useNavigation();

  const handlePress = () => {
    navigation.navigate('Artist', { artistId: id });
  };

  return (
    <TouchableOpacity style={styles.card} onPress={handlePress} activeOpacity={0.9}>
      <View style={styles.imageContainer}>
        <Image 
          source={coverImage ? { uri: coverImage } : { uri: DefaultUser }}
          style={styles.image}
          resizeMode="cover"
        />
      </View>
      <View style={styles.content}>
        <View style={styles.header}>
          <View style={styles.nameContainer}>
            <Text style={styles.name} numberOfLines={1} ellipsizeMode="tail">{name}</Text>
          </View>
          <View style={styles.ratingContainer}>
            <StarRating value={rating} size={16} editable={false} />
            <Text style={styles.rating}>{rating}</Text>
          </View>
        </View>
        <View style={styles.specialtiesContainer}>
          {specialties.slice(0, 3).map((specialty, index) => (
            <View key={index} style={styles.badge}>
              <Text style={styles.badgeText}>{specialty}</Text>
            </View>
          ))}
        </View>
        <View style={styles.locationContainer}>
          <MaterialIcons name="location-on" size={14} color="#6B7280" />
          <Text style={styles.locationText} numberOfLines={1} ellipsizeMode="tail">{location}</Text>
        </View>
      </View>
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    overflow: 'hidden',
    boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.1)',
    elevation: 2,
    width: '100%',
    ...Platform.select({
      android: {
        borderWidth: 1,
        borderColor: '#E5E7EB',
      }
    })
  },
  imageContainer: {
    aspectRatio: 16/9,
    overflow: 'hidden',
    backgroundColor: '#f3f4f6',
  },
  image: {
    width: '100%',
    height: '100%',
  },
  content: {
    padding: 12,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
    width: '100%',
  },
  nameContainer: {
    flex: 1,
    paddingRight: 8,
  },
  name: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#111827',
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  starIcon: {
    marginRight: 4,
  },
  rating: {
    fontWeight: '600',
    fontSize: 14,
    color: '#111827',
  },
  specialtiesContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 8,
  },
  badge: {
    backgroundColor: '#F3F4F6',
    borderRadius: 4,
    paddingHorizontal: 8,
    paddingVertical: 2,
    marginRight: 4,
    marginBottom: 4,
  },
  badgeText: {
    fontSize: 12,
    color: '#4B5563',
  },
  locationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '100%',
  },
  locationText: {
    marginLeft: 4,
    fontSize: 12,
    color: '#6B7280',
    flex: 1,
  },
});

export default ArtistCard; 