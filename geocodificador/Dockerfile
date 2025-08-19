FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copiamos el JAR ya compilado desde el host al contenedor
COPY target/*.jar app.jar

# (Opcional) Exponer un puerto si tu geocodificador tiene Actuator/health u otro endpoint HTTP.
# EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
