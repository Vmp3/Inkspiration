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

  // Helper function to chunk artists into rows
  const chunkArray = (array, size) => {
    const result = [];
    for (let i = 0; i < array.length; i += size) {
      result.push(array.slice(i, i + size));
    }
    return result;
  };

  // Divide artists into rows based on numColumns
  const rows = chunkArray(artists, numColumns);

  // Calculate card width based on numColumns
  const cardWidthPercentage = numColumns === 1 ? 100 : (numColumns === 2 ? 49 : 32);
  const cardMargin = numColumns === 1 ? 0 : 0.5;

  return (
    <View style={styles.container}>
      {rows.map((row, rowIndex) => (
        <View key={`row-${rowIndex}`} style={styles.row}>
          {row.map((artist) => (
            <TouchableOpacity 
              key={artist.id}
              style={[
                styles.artistCard,
                { 
                  width: `${cardWidthPercentage}%`,
                  marginHorizontal: `${cardMargin}%` 
                }
              ]}
              onPress={() => navigation.navigate('ArtistDetail', { artistId: artist.id })}
            >
              <ArtistCard artist={artist} />
            </TouchableOpacity>
          ))}
          
          {/* Add placeholder cards to fill the row if needed */}
          {row.length < numColumns && numColumns > 1 && Array(numColumns - row.length).fill().map((_, index) => (
            <View 
              key={`placeholder-${index}`} 
              style={{ 
                width: `${cardWidthPercentage}%`, 
                marginHorizontal: `${cardMargin}%` 
              }} 
            />
          ))}
        </View>
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
  artistCard: {
    // Base styles for the card container
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