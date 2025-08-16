package challenge.api_geo.service;

import challenge.api_geo.config.RabbitMQConfig;
import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.messaging.GeocodingRequestMessage;
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
        // 1) Mapear y guardar en estado PROCESANDO
        GeolocalizationEntity entity = GeolocalizationEntity.builder()
                .street(request.getStreet())
                .number(request.getNumber())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .province(request.getProvince())
                .country(request.getCountry())
                .status("PROCESANDO")
                .build();

        GeolocalizationEntity saved = geolocalizationRepository.save(entity);

        // 2) Armar el mensaje para el geocodificador
        GeocodingRequestMessage msg = new GeocodingRequestMessage(
                saved.getId(),
                saved.getStreet(),
                saved.getNumber(),
                saved.getCity(),
                saved.getPostalCode(),
                saved.getProvince(),
                saved.getCountry()
        );

        // 3) Enviar a la cola (default exchange → routingKey = nombre de la cola)
        rabbitTemplate.convertAndSend(RabbitMQConfig.GEO_REQUESTS_QUEUE, msg);

        return saved.getId();
    }
}