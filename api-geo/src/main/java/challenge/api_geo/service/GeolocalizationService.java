package challenge.api_geo.service;

import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.repository.GeolocalizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeolocalizationService {

    private final GeolocalizationRepository geolocalizationRepository;
    private final RabbitTemplate rabbitTemplate;

    public UUID processGeolocalizationRequest(GeolocalizationRequestDTO request) {
        // 1. Mapea el DTO a la entidad
        GeolocalizationEntity entity = GeolocalizationEntity.builder()
                .street(request.getStreet())
                .number(request.getNumber())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .province(request.getProvince())
                .country(request.getCountry())
                .status("PROCESANDO")
                .build();

        // 2. Guarda la entidad en la base de datos
        GeolocalizationEntity savedEntity = geolocalizationRepository.save(entity);

        // 3. Envía el mensaje a RabbitMQ
        // Aquí debes definir el nombre del exchange y la cola para el Geocodificador
        String geocodingQueue = "geocoding_requests_queue";
        rabbitTemplate.convertAndSend(geocodingQueue, savedEntity);

        return savedEntity.getId();
    }
}