# Build stage
FROM eclipse-temurin:17-jdk-focal as builder
WORKDIR /app

# Instala dependências e copia arquivos
COPY . .
RUN apt-get update && apt-get install -y maven openssl

# Build da aplicação
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Instala openssl para geração de chaves
RUN apt-get update && \
    apt-get install -y --no-install-recommends openssl && \
    rm -rf /var/lib/apt/lists/*

# Copia o JAR
COPY --from=builder /app/target/*.jar app.jar

# Script para gerar chaves RSA e executar aplicação
RUN echo '#!/bin/bash \n\
set -e \n\
\n\
KEY_DIR="/app/src/main/resources" \n\
mkdir -p $KEY_DIR \n\
\n\
if [ ! -s "$KEY_DIR/app.key" ]; then \n\
    echo "Gerando novas chaves RSA..." \n\
    openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out "$KEY_DIR/app.key" \n\
    openssl rsa -pubout -in "$KEY_DIR/app.key" -out "$KEY_DIR/app.pub" \n\
    chmod 600 "$KEY_DIR/app.key" \n\
    chmod 644 "$KEY_DIR/app.pub" \n\
    echo "✅ Chaves RSA geradas com sucesso!" \n\
else \n\
    echo "✅ Chaves RSA existentes encontradas." \n\
fi \n\
\n\
echo "Iniciando aplicação..." \n\
exec java -jar app.jar \n\
' > /usr/local/bin/entrypoint-prod.sh

RUN chmod +x /usr/local/bin/entrypoint-prod.sh

# Configurações de segurança
RUN chown -R 1000:1000 /app

# Porta e usuário não-root
USER 1000
EXPOSE 8080

# Comando de execução
ENTRYPOINT ["/usr/local/bin/entrypoint-prod.sh"]