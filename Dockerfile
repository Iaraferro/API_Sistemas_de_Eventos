# ============================================================
# STAGE 1 — Build (Maven + Java 17)
# Compila o projeto inteiro dentro do container.
# Nenhum Java ou Maven local é necessário.
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia somente o pom.xml primeiro para cachear as dependências.
# Rebuild das deps só acontece se o pom.xml mudar.
COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress -q

# Copia o restante do código e compila (sem rodar testes)
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress -q

# ============================================================
# STAGE 2 — Runtime (JRE 17, imagem enxuta)
# Apenas o JAR compilado é copiado para a imagem final.
# ============================================================
FROM eclipse-temurin:17-jre

WORKDIR /deployments

# Quarkus Fast-jar: 4 camadas para melhor reuso de cache Docker
COPY --from=build /app/target/quarkus-app/lib/      ./lib/
COPY --from=build /app/target/quarkus-app/*.jar     ./
COPY --from=build /app/target/quarkus-app/app/      ./app/
COPY --from=build /app/target/quarkus-app/quarkus/  ./quarkus/

EXPOSE 8080

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 \
               -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

CMD ["sh", "-c", "java $JAVA_OPTS -jar quarkus-run.jar"]
