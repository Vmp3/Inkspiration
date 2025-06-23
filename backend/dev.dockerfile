FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

RUN apt-get update && apt-get install -y maven openssl

COPY pom.xml .
RUN mvn dependency:go-offline

# Gera chaves apenas se não existirem (evita sobrescrita)
RUN mkdir -p src/main/resources && \
    [ ! -f src/main/resources/app.key ] && \
    openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out src/main/resources/app.key && \
    openssl rsa -pubout -in src/main/resources/app.key -out src/main/resources/app.pub || true

# Configuração essencial para hot reload
ENV MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000"
ENV SPRING_DEVTOOLS_RESTART_ENABLED=true
ENV SPRING_DEVTOOLS_LIVERELOAD_ENABLED=true

EXPOSE 8080 8000 35729

# Comando otimizado para desenvolvimento
CMD while true; do \
    mvn compile; \
    mvn spring-boot:run -Dspring-boot.run.fork=false; \
    echo "Reiniciando devido a mudanças..."; \
    sleep 2; \
done