package challenge.api_geo.service;

import challenge.api_geo.dto.response.GeocodingResultMessageDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.entity.GeolocalizationEntity.Status;
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
        log.info("📩 Respuesta recibida (raw): id={} status={} lat={} lon={}",
                message.getId(), message.getStatus(), message.getLatitude(), message.getLongitude());

        Optional<GeolocalizationEntity> opt = repository.findById(message.getId());
        if (opt.isEmpty()) {
            log.warn("⚠️ No se encontró la entidad con id {}", message.getId());
            return;
        }

        GeolocalizationEntity entity = opt.get();
        String statusMsg = message.getStatus() == null ? "" : message.getStatus().trim();

        boolean statusIndicatesSuccess = "OK".equalsIgnoreCase(statusMsg)
                || "TERMINADO".equalsIgnoreCase(statusMsg)
                || "COMPLETADO".equalsIgnoreCase(statusMsg);

        boolean hasCoordinates = message.getLatitude() != null && message.getLongitude() != null;

        Status newStatus;
        String errorCode = null;
        String errorMessage = null;

        if (statusIndicatesSuccess && hasCoordinates) {
            newStatus = Status.COMPLETED;
            entity.setLatitude(message.getLatitude());
            entity.setLongitude(message.getLongitude());
        } else if (!statusIndicatesSuccess) {
            newStatus = Status.ERROR;
            errorCode = "GEOCODER_STATUS_INVALID";
            errorMessage = "El geocodificador devolvio status='" + statusMsg + "'";
            entity.setLatitude(null);
            entity.setLongitude(null);
        } else {
            newStatus = Status.ERROR;
            errorCode = "GEOCODER_NO_COORDS";
            errorMessage = "El geocodificador funciona pero devolvio coordenadas nulas.";
            entity.setLatitude(null);
            entity.setLongitude(null);
        }

        entity.setStatus(newStatus);
        entity.setErrorCode(errorCode);
        entity.setErrorMessage(errorMessage);

        repository.save(entity);

        log.info("🔁 Entidad actualizada: id={} newStatus={} lat={} lon={} errorCode={} errorMsg={}",
                entity.getId(), entity.getStatus(), entity.getLatitude(), entity.getLongitude(),
                entity.getErrorCode(), entity.getErrorMessage());
    }
}