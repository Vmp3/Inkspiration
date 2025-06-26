import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';

const TermsAndPolicies = ({ type }) => {
  const termsOfUseText = `
Última atualização: 16 de junho de 2025
<br>
Este Termo de Uso regula o acesso e a utilização do aplicativo Inkspiration, de propriedade da Inkspiration Ltda., inscrita no CNPJ sob nº [CNPJ], sediada em [endereço completo].
<br>
Ao acessar ou utilizar nossos serviços, você declara que leu, compreendeu e concorda com este Termo.
<br>
<strong>1. Objeto</strong>
O Inkspiration é uma plataforma digital que conecta tatuadores e clientes, permitindo a criação de perfis profissionais, agendamento de sessões, divulgação de portfólios e avaliações.
<br>
<strong>2. Cadastro e Acesso</strong>
    ● Para utilizar os serviços, é necessário realizar cadastro com informações verídicas e atualizadas.
    ● Você é responsável por manter a confidencialidade de suas credenciais de acesso.
    ● O uso da conta é pessoal e intransferível.
<br>
<strong>3. Regras de Uso</strong>
    É proibido:
        ● Inserir informações falsas ou enganosas.
        ● Utilizar linguagem ofensiva, discriminatória ou difamatória.
        ● Utilizar o aplicativo para fins ilícitos.
        ● Publicar conteúdo que viole direitos autorais, marcas ou imagem de terceiros.
    A violação destas regras poderá acarretar a exclusão do perfil e responsabilização 
    civil e penal.
<br>
<strong>4. Responsabilidades</strong>
<br>
<strong>Inkspiration:</strong>
        ● Disponibilizar a plataforma de forma funcional e segura.
        ● Zelar pela proteção de dados conforme LGPD.
<br>
<strong>Usuário:</strong>
        ● Fornecer informações corretas.
        ● Agir com boa-fé nas interações com outros usuários.
        ● Assumir responsabilidade sobre o conteúdo que publica.

    A Inkspiration <strong>não</strong> se responsabiliza por:

        ● Qualidade dos serviços prestados por tatuadores.
        ● Prejuízos oriundos de agendamentos não realizados.
        ● Conteúdo publicado por terceiros.
<br>
<strong>5. Propriedade Intelectual</strong>
    Todos os direitos sobre a marca "Inkspiration", layout do aplicativo, textos, imagens e software são de titularidade da Inkspiration ou licenciados. É vedada qualquer reprodução sem autorização expressa.
<br>
<strong>6. Rescisão e Cancelamento</strong>
    A Inkspiration poderá, a seu critério, suspender ou cancelar o acesso do usuário em caso de violação aos termos. O usuário também poderá solicitar o cancelamento a qualquer momento.
<br>
<strong>7. Modificações no Termo</strong>
    Este Termo poderá ser alterado a qualquer momento, sendo recomendável a leitura periódica. As alterações entrarão em vigor na data de sua publicação no aplicativo.
<br>
<strong>8. Foro e Legislação Aplicável</strong>
    Este Termo é regido pelas leis da República Federativa do Brasil. Quaisquer disputas serão solucionadas no foro da comarca do domicílio do usuário, salvo disposição legal em contrário.
<br>
<strong>Contato Inkspiration</strong>
    ● E-mail de suporte: [inserir]
`;

  const privacyPolicyText = `
Última atualização: 04 de junho de 2025
<br>
A sua privacidade é prioridade para a Inkspiration Ltda. Esta Política de Privacidade explica, de forma clara e transparente, como coletamos, usamos, armazenamos, tratamos e protegemos seus dados pessoais ao utilizar nosso aplicativo e serviços.
<br>
Ao utilizar a Inkspiration, você declara estar ciente e concorda com os termos descritos nesta Política, conforme previsto na Constituição Federal de 1988 (art. 5º, LXXIX e art. 22º, XXX – EC 115/2022), na Lei Geral de Proteção de Dados Pessoais (Lei nº 13.709/2018 – LGPD), no Código de Defesa do Consumidor (Lei nº 8.078/1990), entre outras legislações aplicáveis.
<br>
<strong>1. Dados que coletamos e finalidades</strong>
<br>
<strong>1.1. Dados fornecidos por você</strong>
<br>
    <strong>Tatuadores:</strong>
        ● Nome, e-mail, telefone, CPF, endereço, foto, portfólio (imagens), descrição profissional, horários disponíveis.
        ● Finalidade: Criar perfis públicos, divulgar trabalhos, permitir agendamentos, facilitar comunicação com clientes.
<br>
<strong>Clientes:</strong>
        ● Nome, CPF, e-mail, telefone, data de nascimento, endereço.
        ● Finalidade: Cadastro, agendamento de sessões e controle dos agendamentos por parte dos tatuadores.
<br>
<strong>Avaliações:</strong>
        ● Notas e comentários sobre atendimentos realizados.
        ● Finalidade: Apoiar decisões de outros usuários e aperfeiçoar serviços dos tatuadores.
<br>
<strong>1.2. Dados coletados automaticamente</strong>
        ● Dispositivo: Modelo, sistema operacional, identificadores únicos.
        ● Uso do app: Páginas visitadas, tempo de uso, funcionalidades utilizadas.
        ● Localização: (somente mediante consentimento) localização aproximada.
        ● Finalidade geral: Otimizar a experiência do usuário, identificar erros, sugerir profissionais próximos.
<br>
<strong>2. Forma de coleta dos dados</strong>
        ● Preenchimento de formulários e cadastros.
        ● Upload de portfólio e avaliações.
        ● Navegação e uso do app.
        ● Consentimento prévio, específico e informado, respeitando a LGPD.
    Você pode revogar o consentimento a qualquer momento, mas isso pode limitar o uso de certas funcionalidades.
<br>
<strong>3. Direitos dos titulares</strong>
    De acordo com o art. 18 da LGPD, você pode:
        ● Confirmar a existência de tratamento.
        ● Acessar, corrigir ou atualizar seus dados.
        ● Solicitar anonimização, bloqueio ou exclusão.
        ● Solicitar portabilidade.
        ● Revogar consentimento e solicitar exclusão de dados tratados com base nele.
<br>
<strong>4. Como exercer seus direitos</strong>
    Entre em contato conosco por:
        ● E-mail: [preencher]
    Poderemos solicitar documentos para confirmar sua identidade.
<br>
<strong>5. Armazenamento e retenção de dados</strong>
    Os dados serão armazenados pelo tempo necessário ao cumprimento das finalidades descritas ou exigências legais.
    <strong>Exceções legais para retenção:</strong> conforme art. 16 da LGPD (ex: obrigação legal, pesquisa, defesa judicial).
    Utilizamos servidores seguros e criptografia, com acesso restrito e controlado.
<br>
<strong>6. Segurança dos dados</strong>
    Adotamos medidas técnicas e organizacionais para proteger seus dados:
        ● Acesso restrito e autorizado.
        ● Compromisso de confidencialidade.
        ● Monitoramento de segurança.
        ● Comunicação de incidentes à ANPD e aos titulares, quando necessário.
<br>
<strong>7. Compartilhamento de dados</strong>
    Seus dados podem ser compartilhados com:
        ● Provedores de hospedagem e infraestrutura.
        ● Autoridades legais ou judiciais, se exigido.
        ● Em caso de fusão, aquisição ou venda da Inkspiration.
        ● Terceiros localizados no exterior, observando os princípios da LGPD.
<br>
<strong>8. Tecnologias de rastreamento (cookies e similares)</strong>
    Utilizamos tecnologias como armazenamento local e identificadores de dispositivo:
        ● Manter sessão ativa.
        ● Salvar preferências.
        ● Coletar dados analíticos.
    Você pode desativar o uso nas configurações do seu dispositivo.
<br>
<strong>9. Alterações nesta Política</strong>
    Reservamo-nos o direito de atualizar esta Política periodicamente. Notificaremos os usuários em caso de alterações relevantes. O uso contínuo dos serviços após mudanças será interpretado como aceitação.
<br>
<strong>10. Responsabilidade e isenções</strong>
    A Inkspiration é responsável pelo correto tratamento dos dados. Contudo, não se responsabiliza por:
        ● Uso indevido ou negligência do usuário.
        ● Ataques de terceiros (ex: hackers), exceto em caso de falha comprovada da empresa.
        ● Informações falsas inseridas pelo usuário.
<br>
<strong>11. Encarregado de Dados (DPO)</strong>
    ● Nome: [Inserir]
    ● CPF: [Inserir]
    ● E-mail: [Inserir]
`;

  // Função para processar o texto e manter a formatação adequada
  const processText = (text) => {
    // Adiciona título específico para cada tipo
    const titleText = type === 'terms' 
      ? "<strong>TERMOS DE USO – INKSPIRATION LTDA.</strong><br><br>"
      : "<strong>POLÍTICA DE PRIVACIDADE – INKSPIRATION LTDA.</strong><br><br>";
    
    // Texto completo com título
    const fullText = titleText + text;
    
    // Divide o texto em partes baseadas na tag <br>
    const paragraphs = fullText.split('<br>');
    
    return paragraphs.map((paragraph, paragraphIndex) => {
      // Se o parágrafo estiver vazio, renderiza um espaço em branco
      if (!paragraph.trim()) {
        return <View key={`space-${paragraphIndex}`} style={styles.emptySpace} />;
      }
      
      // Processa o parágrafo
      // Verifica se o parágrafo contém tags <strong>
      if (paragraph.includes('<strong>') || paragraph.includes('</strong>')) {
        // Divide o parágrafo em partes: texto normal e texto em negrito
        const parts = paragraph.split(/<\/?strong>/);
        
        return (
          <View key={`para-${paragraphIndex}`} style={styles.paragraph}>
            <Text style={styles.lineContainer}>
              {parts.map((part, partIndex) => {
                // Alternando entre texto normal e texto em negrito
                const isStrong = partIndex % 2 === 1;
                
                if (part.trim() === '') return null;
                
                return (
                  <Text 
                    key={`part-${paragraphIndex}-${partIndex}`} 
                    style={[
                      styles.text,
                      isStrong && styles.boldText
                    ]}
                  >
                    {part}
                  </Text>
                );
              })}
            </Text>
          </View>
        );
      } 
      // Verifica se o parágrafo contém linhas com marcadores
      else if (paragraph.includes('●')) {
        // Divide em linhas
        const lines = paragraph.split('\n');
        
        return (
          <View key={`para-${paragraphIndex}`} style={styles.paragraph}>
            {lines.map((line, lineIndex) => {
              // Se é uma linha com marcador ●
              if (line.trim().startsWith('●')) {
                // Extrai o texto após o marcador
                const bulletText = line.trim().substring(1).trim();
                
                // Processa o texto do marcador para identificar quebras de linha
                const processedBulletText = bulletText
                  .replace(/\s{2,}/g, ' ') // Remove espaços extras
                  .trim();
                
                return (
                  <View key={`bullet-${paragraphIndex}-${lineIndex}`} style={styles.bulletItem}>
                    <Text style={styles.bulletPoint}>●</Text>
                    <Text style={styles.bulletText}>{processedBulletText}</Text>
                  </View>
                );
              } 
              // Se é uma linha com indentação
              else if (line.trim() && (line.startsWith('    ') || line.startsWith('\t'))) {
                return (
                  <Text key={`indent-${paragraphIndex}-${lineIndex}`} style={styles.indentedText}>
                    {line.trim()}
                  </Text>
                );
              } 
              // Linha normal
              else if (line.trim()) {
                return (
                  <Text key={`normal-${paragraphIndex}-${lineIndex}`} style={styles.text}>
                    {line.trim()}
                  </Text>
                );
              }
              
              return null;
            })}
          </View>
        );
      } 
      // Parágrafo normal
      else {
        return (
          <View key={`para-${paragraphIndex}`} style={styles.paragraph}>
            <Text style={styles.text}>{paragraph.trim()}</Text>
          </View>
        );
      }
    });
  };

  return (
    <View style={styles.container}>
      {processText(type === 'terms' ? termsOfUseText : privacyPolicyText)}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'transparent',
  },
  paragraph: {
    marginBottom: 14,
  },
  emptySpace: {
    height: 8,
  },
  lineContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  text: {
    fontSize: 14,
    lineHeight: 20,
    color: '#333',
    textAlign: 'left',
  },
  boldText: {
    fontWeight: 'bold',
    color: '#111',
  },
  bulletItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: 6,
    paddingLeft: 16,
  },
  bulletPoint: {
    fontSize: 14,
    lineHeight: 20,
    color: '#333',
    width: 15,
    marginRight: 4,
  },
  bulletText: {
    flex: 1,
    fontSize: 14,
    lineHeight: 20,
    color: '#333',
  },
  indentedText: {
    fontSize: 14,
    lineHeight: 20,
    color: '#333',
    paddingLeft: 16,
    marginBottom: 6,
  }
});

export default TermsAndPolicies; 