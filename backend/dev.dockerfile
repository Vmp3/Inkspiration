FROM eclipse-temurin:17-jdk-focal

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
\n\
echo "Iniciando aplicação em modo desenvolvimento com hot reload..." \n\
exec mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true" \n\
' > /app/entrypoint-dev.sh

RUN chmod +x /app/entrypoint-dev.sh

RUN bash -n /app/entrypoint-dev.sh

EXPOSE 8080
EXPOSE 35729

ENTRYPOINT ["/app/entrypoint-dev.sh"] 