import { Platform, Alert } from 'react-native';
import * as FileSystem from 'expo-file-system';
import * as Sharing from 'expo-sharing';
import toastHelper from '../utils/toastHelper';

class PDFExportService {
  async exportToPDF(response, filename) {
    if (Platform.OS === 'web') {
      return this.handleWebDownload(response, filename);
    } else {
      return this.handleMobileDownload(response, filename);
    }
  }

  handleWebDownload(response, filename) {
    if (!response.data) {
      throw new Error("Dados do PDF não recebidos");
    }
    
    const blob = new Blob([response.data], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    
    link.click();
    
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
      document.body.removeChild(link);
    }, 100);
    
    toastHelper.showSuccess('PDF gerado com sucesso!');
  }

  async handleMobileDownload(response, filename) {
    const fileUri = FileSystem.documentDirectory + filename;
    let base64Data = response.data;
    
    if (typeof response.data === 'object' && response.data instanceof Blob) {
      const arrayBuffer = await response.data.arrayBuffer();
      const bytes = new Uint8Array(arrayBuffer);
      base64Data = btoa(String.fromCharCode(...bytes));
    }
    
    await FileSystem.writeAsStringAsync(fileUri, base64Data, {
      encoding: FileSystem.EncodingType.Base64,
    });

    if (Platform.OS === 'android') {
      const saved = await this.tryAndroidSAF(base64Data, filename);
      if (saved) return;
    }
    
    await this.shareFile(fileUri, filename);
  }

  async tryAndroidSAF(base64Data, filename) {
    try {
      const permissions = await FileSystem.StorageAccessFramework.requestDirectoryPermissionsAsync();
      
      if (permissions.granted) {
        const safUri = await FileSystem.StorageAccessFramework.createFileAsync(
          permissions.directoryUri,
          filename,
          'application/pdf'
        );
        
        await FileSystem.writeAsStringAsync(safUri, base64Data, { 
          encoding: FileSystem.EncodingType.Base64 
        });
        
        toastHelper.showSuccess('PDF salvo com sucesso na pasta selecionada!');
        return true;
      }
    } catch (error) {
      return false;
    }
    return false;
  }

  async shareFile(fileUri, filename) {
    const shareAvailable = await Sharing.isAvailableAsync();
    
    if (shareAvailable) {
      await Sharing.shareAsync(fileUri, {
        mimeType: 'application/pdf',
        dialogTitle: `Salvar ${filename}`,
        UTI: 'com.adobe.pdf'
      });
      toastHelper.showSuccess('PDF gerado com sucesso! Use o menu de compartilhamento para salvar.');
    } else {
      Alert.alert(
        'PDF Gerado',
        `O arquivo foi salvo em: ${fileUri}\n\nVocê pode acessá-lo através do gerenciador de arquivos do dispositivo.`,
        [
          { 
            text: 'OK', 
            onPress: () => toastHelper.showSuccess('PDF salvo no dispositivo!') 
          }
        ]
      );
    }
  }
}

export default new PDFExportService(); 