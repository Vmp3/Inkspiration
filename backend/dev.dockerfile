FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

RUN apt-get update && apt-get install -y maven openssl

COPY pom.xml .
RUN mvn dependency:go-offline

# Script para gerar chaves RSA apenas se necessário
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
while true; do \n\
    mvn compile; \n\
    mvn spring-boot:run -Dspring-boot.run.fork=false; \n\
    echo "Reiniciando devido a mudanças..."; \n\
    sleep 2; \n\
done \n\
' > /usr/local/bin/entrypoint-dev.sh

RUN chmod +x /usr/local/bin/entrypoint-dev.sh

# Configuração essencial para hot reload
ENV MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000"
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true
ENV SPRING_DEVTOOLS_LIVERELOAD_ENABLED=true

EXPOSE 8080 8000 35729

# Comando otimizado para desenvolvimento com geração de chaves RSA
ENTRYPOINT ["/usr/local/bin/entrypoint-dev.sh"]