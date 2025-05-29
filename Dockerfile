# Usamos una imagen oficial de Maven para construir el proyecto
FROM maven:3.9.0-eclipse-temurin-17 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos los archivos pom.xml y src
COPY pom.xml .
COPY src ./src

# Construimos el proyecto (package crea el jar)
RUN mvn clean package

# Segunda etapa: imagen más liviana para correr el jar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el jar generado en la etapa de build
COPY --from=build /app/target/telegram-bot-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Definimos la variable de entorno para el token (podés setearla en Render también)
ENV TELEGRAM_BOT_TOKEN=7336576655:AAFub-EA7jcI07YYNN5Yb2hmzFvB4uYc_4o
# Comando para ejecutar el jar
CMD ["java", "-jar", "app.jar"]
