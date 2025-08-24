package challenge.api_geo.service;

import challenge.api_geo.config.RabbitMQConfig;
import challenge.api_geo.dto.request.GeocodingRequestMessageDTO;
import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
import challenge.api_geo.dto.response.GeolocalizationResponseDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.repository.GeolocalizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeolocalizationService {

    private final GeolocalizationRepository repository;
    private final RabbitTemplate rabbitTemplate;

     // Crea la solicitud en estado PROCESANDO y publica el mensaje en RabbitMQ.
    public UUID processGeolocalizationRequest(GeolocalizationRequestDTO request) {
        GeolocalizationEntity entity = GeolocalizationEntity.builder()
                .street(request.getStreet())
                .number(request.getNumber())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .province(request.getProvince())
                .country(request.getCountry())
                .status(GeolocalizationEntity.Status.PENDING)
                .build();

        GeolocalizationEntity saved = repository.save(entity);

        GeocodingRequestMessageDTO msg = new GeocodingRequestMessageDTO(
                saved.getId(),
                saved.getStreet(),
                saved.getNumber(),
                saved.getCity(),
                saved.getPostalCode(),
                saved.getProvince(),
                saved.getCountry()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.REQUESTS_QUEUE, msg);
        return saved.getId(); // UUID
    }

     // Devuelve la entidad cruda (por si la necesita tu controller actual).
    public Optional<GeolocalizationEntity> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<GeolocalizationResponseDTO> getGeolocalizationResult(UUID id) {
        return repository.findById(id).map(e ->
                GeolocalizationResponseDTO.builder()
                        .id(e.getId())
                        .street(e.getStreet())
                        .number(e.getNumber())
                        .city(e.getCity())
                        .postalCode(e.getPostalCode())
                        .province(e.getProvince())
                        .country(e.getCountry())
                        .latitude(e.getLatitude())
                        .longitude(e.getLongitude())
                        .status(e.getStatus())
                        .errorCode(e.getErrorCode())
                        .errorMessage(e.getErrorMessage())
                        .build()
        );
    }
}