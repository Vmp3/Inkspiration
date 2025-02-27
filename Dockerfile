# Imagem base com JDK 17
FROM eclipse-temurin:17-jdk-focal

# Diretório de trabalho
WORKDIR /app

# Instalar OpenSSL e Maven com cache de camadas
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    openssl \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Copiar apenas o pom.xml primeiro para aproveitar o cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar o código fonte e recursos
COPY src ./src

# Criar script para gerar chaves com melhor tratamento de erros
RUN echo '#!/bin/bash \n\
set -e \n\
\n\
KEY_DIR="/app/src/main/resources" \n\
HOST_KEY_DIR="/keys" \n\
\n\
echo "Verificando diretórios de chaves..." \n\
mkdir -p $KEY_DIR \n\
mkdir -p $HOST_KEY_DIR \n\
\n\
if [ ! -s "$KEY_DIR/app.key" ] || [ ! -s "$HOST_KEY_DIR/app.key" ]; then \n\
    echo "Gerando novas chaves RSA..." \n\
    # Gerar chaves com tratamento de erros \n\
    if ! openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out "$KEY_DIR/app.key"; then \n\
        echo "ERRO: Falha ao gerar chave privada" >&2 \n\
        exit 1 \n\
    fi \n\
    \n\
    if ! openssl rsa -pubout -in "$KEY_DIR/app.key" -out "$KEY_DIR/app.pub"; then \n\
        echo "ERRO: Falha ao extrair chave pública" >&2 \n\
        exit 1 \n\
    fi \n\
    \n\
    echo "Copiando chaves para o volume montado..." \n\
    cp "$KEY_DIR/app.key" "$HOST_KEY_DIR/app.key" \n\
    cp "$KEY_DIR/app.pub" "$HOST_KEY_DIR/app.pub" \n\
    \n\
    echo "Definindo permissões..." \n\
    chmod 600 "$KEY_DIR/app.key" "$HOST_KEY_DIR/app.key" \n\
    chmod 644 "$KEY_DIR/app.pub" "$HOST_KEY_DIR/app.pub" \n\
    \n\
    echo "✅ Chaves RSA geradas com sucesso!" \n\
else \n\
    echo "✅ Chaves RSA existentes encontradas. Mantendo as chaves atuais." \n\
    # Garantir que as chaves do host sejam usadas no container \n\
    if [ -s "$HOST_KEY_DIR/app.key" ] && [ ! -s "$KEY_DIR/app.key" ]; then \n\
        echo "Copiando chaves do host para o container..." \n\
        cp "$HOST_KEY_DIR/app.key" "$KEY_DIR/app.key" \n\
        cp "$HOST_KEY_DIR/app.pub" "$KEY_DIR/app.pub" \n\
        chmod 600 "$KEY_DIR/app.key" \n\
        chmod 644 "$KEY_DIR/app.pub" \n\
    fi \n\
fi \n\
\n\
echo "Iniciando build do projeto..." \n\
mvn clean package -DskipTests \n\
\n\
echo "Iniciando a aplicação..." \n\
exec java -jar target/schoolface-0.0.1-SNAPSHOT.jar \n\
' > /app/entrypoint.sh

# Tornar o script executável
RUN chmod +x /app/entrypoint.sh

# Verificar a sintaxe do script
RUN bash -n /app/entrypoint.sh

# Expor a porta 8080
EXPOSE 8080

# Usar o script como ponto de entrada
ENTRYPOINT ["/app/entrypoint.sh"] 