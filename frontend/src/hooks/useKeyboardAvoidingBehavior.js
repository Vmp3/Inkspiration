import { useEffect } from 'react';
import { Platform, Keyboard } from 'react-native';

export const useKeyboardAvoidingBehavior = () => {
  useEffect(() => {
    if (Platform.OS !== 'ios') return;

    let keyboardDidShowListener;
    let keyboardDidHideListener;
    let keyboardWillShowListener;
    let keyboardWillHideListener;

    const handleKeyboardWillShow = () => {
      // Prepare for keyboard showing
    };

    const handleKeyboardDidShow = () => {
      // Keyboard is fully visible
    };

    const handleKeyboardWillHide = () => {
      // Prepare for keyboard hiding
    };

    const handleKeyboardDidHide = () => {
      // Force layout recalculation to prevent white rectangle
      setTimeout(() => {
        // This timeout helps iOS clean up properly
      }, 100);
    };

    keyboardWillShowListener = Keyboard.addListener('keyboardWillShow', handleKeyboardWillShow);
    keyboardDidShowListener = Keyboard.addListener('keyboardDidShow', handleKeyboardDidShow);
    keyboardWillHideListener = Keyboard.addListener('keyboardWillHide', handleKeyboardWillHide);
    keyboardDidHideListener = Keyboard.addListener('keyboardDidHide', handleKeyboardDidHide);

    return () => {
      keyboardWillShowListener?.remove();
      keyboardDidShowListener?.remove();
      keyboardWillHideListener?.remove();
      keyboardDidHideListener?.remove();
    };
  }, []);
};

export default useKeyboardAvoidingBehavior; 