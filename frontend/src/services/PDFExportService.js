import { Platform, Alert } from 'react-native';
import * as FileSystem from 'expo-file-system';
import * as Sharing from 'expo-sharing';
import toastHelper from '../utils/toastHelper';
import { pdfExportMessages } from './messages';

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
    
    let blob;
    
    if (response.data instanceof Blob) {
      blob = response.data;
    } 
    else if (typeof response.data === 'string') {
      try {
        const binaryString = atob(response.data);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
          bytes[i] = binaryString.charCodeAt(i);
        }
        blob = new Blob([bytes], { type: 'application/pdf' });
      } catch (error) {
        throw new Error("Erro ao decodificar dados base64 do PDF");
      }
    } 
    else {
      blob = new Blob([response.data], { type: 'application/pdf' });
    }
    
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
    
    toastHelper.showSuccess(pdfExportMessages.success.pdfGenerated);
  }

  async handleMobileDownload(response, filename) {
    const fileUri = FileSystem.documentDirectory + filename;
    let base64Data = response.data;
    
    if (typeof response.data === 'object' && response.data instanceof Blob) {
      const arrayBuffer = await response.data.arrayBuffer();
      const bytes = new Uint8Array(arrayBuffer);
      base64Data = btoa(String.fromCharCode(...bytes));
    }
    
    if (typeof response.data === 'string') {
      base64Data = response.data;
    }
    
    if (!base64Data || typeof base64Data !== 'string') {
      throw new Error('Dados PDF inválidos recebidos');
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
        
        toastHelper.showSuccess(pdfExportMessages.success.pdfSaved);
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
      toastHelper.showSuccess(pdfExportMessages.success.pdfGeneratedUseShare);
    } else {
      Alert.alert(
        'PDF Gerado',
        `O arquivo foi salvo em: ${fileUri}\n\nVocê pode acessá-lo através do gerenciador de arquivos do dispositivo.`,
        [
          { 
            text: 'OK', 
            onPress: () => toastHelper.showSuccess(pdfExportMessages.success.pdfSavedToDevice) 
          }
        ]
      );
    }
  }
}

export default new PDFExportService(); 