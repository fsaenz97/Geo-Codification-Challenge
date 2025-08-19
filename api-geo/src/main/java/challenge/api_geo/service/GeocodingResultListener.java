package challenge.api_geo.service;

import challenge.api_geo.dto.response.GeocodingResultMessageDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.repository.GeolocalizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static challenge.api_geo.config.RabbitMQConfig.RESPONSES_QUEUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingResultListener {

    private final GeolocalizationRepository repository;

    @RabbitListener(queues = RESPONSES_QUEUE)
    public void onMessage(GeocodingResultMessageDTO message) {
        log.info("Respuesta recibida: {}", message);

        Optional<GeolocalizationEntity> opt = repository.findById(message.getId());
        if (opt.isEmpty()) {
            log.warn("No se encontró la entidad con id {}", message.getId());
            return;
        }

        GeolocalizationEntity e = opt.get();
        String status = (message.getStatus() != null && !message.getStatus().isBlank())
                ? message.getStatus()
                : ((message.getLatitude() != null && message.getLongitude() != null) ? "OK" : "ERROR");

        e.setLatitude(message.getLatitude());
        e.setLongitude(message.getLongitude());
        e.setStatus(status);

        repository.save(e);

        log.info("Entidad {} actualizada -> lat={}, lon={}, estado={}",
                e.getId(), e.getLatitude(), e.getLongitude(), e.getStatus());
    }
}
