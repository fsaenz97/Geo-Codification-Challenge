### Geo-Codification Challenge ###

Este proyecto es una solución de geocodificación de direcciones implementada con Java + Spring Boot, siguiendo una arquitectura basada en microservicios y ejecutándose completamente sobre contenedores Docker.

El objetivo es transformar direcciones en coordenadas geográficas (latitud/longitud) utilizando el servicio Nominatim (OpenStreetMap), con procesamiento asíncrono mediante RabbitMQ.

### Tecnologías utilizadas

- Java 17

- Spring Boot 3

- RabbitMQ

- Nominatim API (OpenStreetMap)

- Docker & Docker Compose

- Maven
 

### Arquitectura

La solución está organizada en módulos independientes, facilitando la escalabilidad y mantenibilidad:

API Gateway → expone los endpoints REST.

Geocoding Service → procesa las solicitudes y consulta la API de Nominatim.

Messaging (RabbitMQ) → maneja la comunicación asíncrona entre servicios.

Shared DTOs → para unificar contratos de datos entre módulos.

### Cómo ejecutar el proyecto 

1) Clonar el repositorio:
- git clone https://github.com/fsaenz97/Geo-Codification-Challenge.git
- cd Geo-Codification-Challenge

2) Levantar los contenedores con Docker Compose

- docker-compose up --build


Esto levantará:

- RabbitMQ

- Servicios de geocodificación

3) Acceder a la aplicación

📡 Endpoints principales
1) Enviar una solicitud de geocodificación
   POST http://localhost:8080/geolocalizar


Body (JSON):

{
"street": "Calle o Av",
"number": "1234",
"city": "Ciudad",
"country": "Argentina"
}

2. Consultar el estado del procesamiento
   GET http://localhost:8080/geocodificar?id={queryId}


Posibles respuestas:

PENDING → la solicitud sigue en proceso

COMPLETED → devuelve latitud/longitud

ERROR → ocurrió un problema

NOT_FOUND → dirección no encontrada


### Autor

👤 Federico Sáenz
📌 Desarrollador Backend (Java - Spring Boot)