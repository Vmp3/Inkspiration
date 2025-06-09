import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Dimensions, Platform } from 'react-native';
import ArtistCard from '../ArtistCard';

const ArtistsGrid = ({ artists, numColumns, navigation }) => {
  if (artists.length === 0) {
    return (
      <View style={styles.noResultsContainer}>
        <Text style={styles.noResultsTitle}>Nenhum resultado encontrado</Text>
        <Text style={styles.noResultsSubtitle}>
          Tente ajustar seus filtros ou termos de busca para encontrar mais resultados.
        </Text>
      </View>
    );
  }

  const chunkArray = (array, size) => {
    const result = [];
    for (let i = 0; i < array.length; i += size) {
      result.push(array.slice(i, i + size));
    }
    return result;
  };

  const rows = chunkArray(artists, numColumns);

  const cardWidthPercentage = numColumns === 1 ? 100 : (numColumns === 2 ? 49 : 32);
  const cardMargin = numColumns === 1 ? 0 : 0.5;

  return (
    <View style={styles.artistsGrid}>
      {artists.map((artist) => (
        <TouchableOpacity 
          key={artist.id}
          style={[
            styles.artistCard,
            numColumns === 3 ? { width: '32%', marginHorizontal: '0.66%' } :
            numColumns === 2 ? { width: '48%', marginHorizontal: '1%' } :
            { width: '100%' }
          ]}
          onPress={() => navigation.navigate('Artist', { artistId: artist.id })}
        >
          <ArtistCard artist={artist} />
        </TouchableOpacity>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    marginBottom: 16,
    width: '100%',
  },
  noResultsContainer: {
    padding: 48,
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  noResultsTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 8,
    textAlign: 'center',
    color: '#111827',
  },
  noResultsSubtitle: {
    fontSize: 16,
    color: '#6B7280',
    textAlign: 'center',
  },
});

export default ArtistsGrid; 