# Build stage
FROM eclipse-temurin:17-jdk-focal as builder
WORKDIR /app

# Instala dependências e copia arquivos
COPY . .
RUN apt-get update && apt-get install -y maven openssl

# Gera chaves RSA (apenas se não existirem)
RUN mkdir -p src/main/resources && \
    if [ ! -f src/main/resources/app.key ]; then \
        openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out src/main/resources/app.key && \
        openssl rsa -pubout -in src/main/resources/app.key -out src/main/resources/app.pub; \
    fi

# Build da aplicação
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Copia o JAR e chaves
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/src/main/resources src/main/resources

# Configurações de segurança
RUN chmod -R 400 src/main/resources/app.key && \
    chown -R 1000:1000 /app

# Porta e usuário não-root
USER 1000
EXPOSE 8080

# Comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]