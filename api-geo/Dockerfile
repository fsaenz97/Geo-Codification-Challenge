FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copiamos el JAR ya compilado desde el host al contenedor
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]