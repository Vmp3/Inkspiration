# Inkspiration

> **A plataforma que conecta pessoas, arte e tatuadores!**

---

## Sobre o Inkspiration
O **Inkspiration** é um ecossistema digital pensado para quem ama tatuagem e para quem vive da arte de tatuar. Aqui, clientes e tatuadores se encontram, compartilham experiências e transformam ideias em arte na pele, com segurança, praticidade e inspiração.

---

## 👤 Para Clientes
- **Descubra artistas incríveis:** Explore perfis de tatuadores, veja portfólios reais, estilos, especialidades e avaliações de outros clientes.
- **Agende sua tattoo online:** Escolha o profissional, veja horários disponíveis e marque sua sessão sem burocracia.
- **Avalie sua experiência:** Após o atendimento, deixe sua avaliação e ajude outros apaixonados por tatuagem a escolherem o melhor artista.
- **Acompanhe seu histórico:** Veja todos os seus agendamentos, avaliações e mantenha seu perfil atualizado.

---

## 🧑 Para Tatuadores
- **Divulgue seu portfólio:** Mostre seus melhores trabalhos, estilos e especialidades para atrair novos clientes.
- **Gerencie sua agenda:** Controle seus horários, confirme ou recuse agendamentos e evite conflitos de agenda.
- **Receba avaliações reais:** Construa sua reputação com base em feedbacks autênticos de clientes.
- **Organize seu perfil profissional:** Atualize informações, redes sociais, endereço do estúdio e mantenha tudo centralizado.

---

## 🌟 Experiências e Diferenciais
- **Busca inteligente:** Encontre tatuadores por localização, estilo, nota, preço e disponibilidade.
- **Portfólio visual:** Cada artista tem uma galeria de fotos para inspirar e encantar.
- **Segurança e transparência:** Autenticação em dois fatores e histórico de avaliações públicas.
- **Comunidade:** Espaço avaliações.
- **Acesso fácil:** Plataforma responsiva, disponível para web e mobile.
- **Administração eficiente:** Painel para gestão de usuários, profissionais e agendamentos.

---

## 💡 Nossa Missão
Conectar pessoas e artistas, tornando a experiência de tatuar mais acessível, transparente e inspiradora para todos. Queremos valorizar a arte, o profissionalismo e a confiança em cada etapa do processo.

---

## 🚀 Junte-se à comunidade Inkspiration!
Se você ama tatuagem, quer encontrar o artista perfeito ou deseja profissionalizar seu estúdio, o Inkspiration é o seu lugar. Venha fazer parte dessa rede que valoriza a arte na pele e a conexão entre pessoas!

---

**Inkspiration** — Conectando pessoas e arte na pele! 🎉

## Funcionalidades principais

- Cadastro e autenticação de usuários (clientes e profissionais)
- Agendamento online de sessões de tatuagem
- Gerenciamento de agenda para profissionais
- Portfólio de trabalhos para artistas
- Avaliação de atendimentos
- Recuperação de senha e autenticação em dois fatores
- Busca de profissionais por especialidade, localização e avaliações
- Upload de fotos de perfil e portfólio
- Validação de endereço via CEP (integração com ViaCEP)
- Painel administrativo para gestão de usuários

## Tecnologias utilizadas

### Backend
- Java 17
- Spring Boot
- Spring Security (JWT)
- JPA/Hibernate
- PostgreSQL (ou outro banco relacional)
- Integração ViaCEP
- Testes com JUnit

### Frontend
- React Native (mobile e web)
- Expo
- React Navigation
- Axios

## Como rodar o projeto

### Pré-requisitos
- Java 17+
- Node.js 16+
- PostgreSQL
- Docker

### Backend
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/Inkspiration.git
   cd Inkspiration/backend
   ```
2. Configure o banco de dados, verifique o  `.env-example`.
3. Rode o backend:
   ```bash
   ./mvnw spring-boot:run
   # ou usando Docker Compose
   docker-compose -f dev.docker-compose.yml up
   ```

### Frontend
1. No diretório raiz:
   ```bash
   cd ../frontend
   npm install
   npm start
   # ou para mobile
   npx expo start
   ```

## Contribuição
1. Faça um fork do projeto
2. Crie uma branch: `git checkout -b minha-feature`
3. Commit suas alterações: `git commit -m 'Minha feature'`
4. Push para o fork: `git push origin minha-feature`
5. Abra um Pull Request
---

**Inkspiration** — Conectando pessoas e arte na pele 