package challenge.api_geo.controller;

import challenge.api_geo.dto.response.GeocodingStatusResponseDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.service.GeolocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GeocodingQueryController {

    private final GeolocalizationService geolocalizationService;

    @GetMapping("/geocodificar")
    public ResponseEntity<GeocodingStatusResponseDTO> get(@RequestParam("id") UUID id) {
        return geolocalizationService.findById(id)
                .map(entity -> {
                    GeocodingStatusResponseDTO response = new GeocodingStatusResponseDTO(
                            entity.getId().toString(),
                            entity.getLatitude(),
                            entity.getLongitude(),
                            entity.getStatus().name(), // PENDING, COMPLETED, ERROR
                            buildStatusMessage(entity)
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GeocodingStatusResponseDTO(
                                id.toString(),
                                null,
                                null,
                                "NOT_FOUND",
                                "No existe ninguna operación con el id " + id
                        )));
    }

    private String buildStatusMessage(GeolocalizationEntity entity) {
        return switch (entity.getStatus()) {
            case PENDING -> "La geocodificación sigue en proceso.";
            case COMPLETED -> "La geocodificación se completó correctamente.";
            case ERROR -> entity.getErrorMessage() != null
                    ? "Hubo un error: " + entity.getErrorMessage()
                    : "La geocodificación falló por un error desconocido.";
        };
    }
}