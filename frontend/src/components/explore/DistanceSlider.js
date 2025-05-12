import React, { useRef, useState } from 'react';
import { View, Text, StyleSheet, PanResponder } from 'react-native';
import FilterSection from './FilterSection';

const DistanceSlider = ({ maxDistance, setMaxDistance, onSliderRelease }) => {
  const sliderRef = useRef(null);
  const [isSliderActive, setIsSliderActive] = useState(false);

  // Configuração do Pan Responder para o slider
  const panResponder = PanResponder.create({
    onStartShouldSetPanResponder: () => true,
    onStartShouldSetPanResponderCapture: () => true,
    onMoveShouldSetPanResponder: () => true,
    onMoveShouldSetPanResponderCapture: () => true,
    onPanResponderGrant: () => {
      setIsSliderActive(true);
    },
    onPanResponderMove: (evt, gestureState) => {
      if (sliderRef.current) {
        sliderRef.current.measure((fx, fy, width, height, px, py) => {
          // Calculamos a porcentagem baseada na posição do dedo
          let percentage = Math.max(0, Math.min(100, ((gestureState.moveX - px) / width) * 100));
          
          // Convertemos a porcentagem para o valor de distância (0-50km)
          const newDistance = Math.round((percentage / 100) * 50);
          
          // Atualizamos o valor do estado
          setMaxDistance(newDistance);
        });
      }
    },
    onPanResponderRelease: () => {
      setIsSliderActive(false);
      if (onSliderRelease) onSliderRelease();
    },
    onPanResponderTerminate: () => {
      setIsSliderActive(false);
    },
  });

  return (
    <FilterSection title={null}>
      <View style={styles.distanceHeader}>
        <Text style={styles.filterSectionTitle}>Distância</Text>
        <Text style={styles.distanceValue}>{maxDistance} km</Text>
      </View>
      <View 
        ref={sliderRef}
        style={styles.slider}
        {...panResponder.panHandlers}
      >
        <View style={styles.sliderTrack} />
        <View 
          style={[
            styles.sliderFill, 
            { width: `${(maxDistance/50)*100}%` }
          ]} 
        />
        <View 
          style={[
            styles.sliderThumb, 
            { left: `${(maxDistance/50)*100}%` },
            isSliderActive && styles.sliderThumbActive
          ]} 
        />
        <View style={styles.sliderLabels}>
          <Text style={styles.sliderLabelText}>0 km</Text>
          <Text style={styles.sliderLabelText}>50 km</Text>
        </View>
      </View>
    </FilterSection>
  );
};

const styles = StyleSheet.create({
  distanceHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  filterSectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 0,
  },
  distanceValue: {
    fontSize: 14,
    fontWeight: '600',
    color: '#111827',
  },
  slider: {
    height: 4,
    backgroundColor: '#E5E7EB',
    borderRadius: 2,
    marginVertical: 12,
    position: 'relative',
    marginBottom: 24,
  },
  sliderTrack: {
    height: 4,
    width: '100%',
    backgroundColor: '#E5E7EB',
    borderRadius: 2,
    position: 'absolute',
  },
  sliderFill: {
    height: 4,
    backgroundColor: '#000000',
    borderRadius: 2,
    position: 'absolute',
  },
  sliderThumb: {
    width: 16,
    height: 16,
    backgroundColor: '#000000',
    borderRadius: 8,
    position: 'absolute',
    top: -6,
    marginLeft: -8,
  },
  sliderThumbActive: {
    transform: [{ scale: 1.2 }],
  },
  sliderLabels: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    position: 'absolute',
    width: '100%',
    top: 10,
  },
  sliderLabelText: {
    fontSize: 12,
    color: '#6B7280',
  },
});

export default DistanceSlider; 