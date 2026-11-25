# Etapa 1: Compilação com Maven e Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia arquivos do projeto e compila o JAR
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Execução leve com JRE 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/*.jar app.jar

# Render define a porta pela variável $PORT
ENV PORT=8080
EXPOSE 8080

# Limita a memória a 350MB para caber com folga no plano Free (512MB) do Render
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -Xmx350m -jar app.jar"]
