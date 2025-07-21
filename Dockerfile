# Etapa 1: Build y test
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copiamos los descriptor de dependencias
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Resolvemos dependencias primero (mejora cache)
RUN ./mvnw dependency:go-offline

# Copiamos el resto del código
COPY src ./src

# Ejecutamos los tests (si fallan, se corta aquí)
RUN ./mvnw clean verify -DskipITs=true

# Etapa 2: Imagen de ejecución ligera
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiamos el JAR generado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto de exposición (opcional pero recomendable)
EXPOSE 8080

# Perfil Docker y ejecución
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
