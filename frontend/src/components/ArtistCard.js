import React from 'react';
import { View, Text, Image, StyleSheet, Platform } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

const ArtistCard = ({ artist }) => {
  const { name, rating, specialties, location, coverImage } = artist;

  return (
    <View style={styles.card}>
      <View style={styles.imageContainer}>
        <Image 
          source={coverImage ? { uri: coverImage } : { uri: 'https://via.placeholder.com/400x200/e0e0e0/8c8c8c?text=No+Image' }}
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
            <MaterialIcons name="star" size={16} color="#FFD700" style={styles.starIcon} />
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
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 8,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
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