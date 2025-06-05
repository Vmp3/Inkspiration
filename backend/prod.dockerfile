# Multi-stage build for production
FROM eclipse-temurin:17-jdk-focal AS build

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    openssl \
    maven \
    && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN echo '#!/bin/bash \n\
set -e \n\
\n\
KEY_DIR="/app/src/main/resources" \n\
\n\
echo "Verificando diretório de chaves..." \n\
mkdir -p $KEY_DIR \n\
\n\
if [ ! -s "$KEY_DIR/app.key" ]; then \n\
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
    echo "Definindo permissões..." \n\
    chmod 600 "$KEY_DIR/app.key" \n\
    chmod 644 "$KEY_DIR/app.pub" \n\
    \n\
    echo "✅ Chaves RSA geradas com sucesso!" \n\
else \n\
    echo "✅ Chaves RSA existentes encontradas. Mantendo as chaves atuais." \n\
fi \n\
' > /app/setup-keys.sh

RUN chmod +x /app/setup-keys.sh && /app/setup-keys.sh

RUN mvn clean package -DskipTests

# Production runtime stage
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r spring && useradd -r -g spring spring

COPY --from=build /app/target/inkspiration-*.jar app.jar
COPY --from=build /app/src/main/resources/app.key /app/app.key
COPY --from=build /app/src/main/resources/app.pub /app/app.pub

RUN chown spring:spring /app/app.jar /app/app.key /app/app.pub && \
    chmod 600 /app/app.key && \
    chmod 644 /app/app.pub

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/app.jar"] 