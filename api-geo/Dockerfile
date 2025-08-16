# Usamos una imagen base de Java 17 con Maven
FROM eclipse-temurin:17-jdk-focal as builder

# Instala Maven
RUN apt-get update && apt-get install -y maven

# Configuramos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo pom.xml y lo descargamos en el contenedor para que Maven descargue las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el resto del código fuente
COPY src ./src

# Empaquetamos la aplicación en un archivo JAR
RUN mvn package -DskipTests

# Usamos una imagen base más ligera para la ejecución de la aplicación (menos espacio)
FROM eclipse-temurin:17-jre-focal

# Configuramos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo JAR del paso anterior
COPY --from=builder /app/target/*.jar ./app.jar

# Exponemos el puerto que usa la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]