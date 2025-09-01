package challenge.api_geo.controller;

import challenge.api_geo.dto.response.GeocodingStatusResponseDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.service.GeolocalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Consulta de geocodificacion", description = "Permite consultar el estado de una solicitud de geolocalizacion")
public class GeocodingQueryController {

    private final GeolocalizationService geolocalizationService;

    @GetMapping("/geocodificar")
    @Operation(
            summary = "Consultar estado de geocodificacion",
            description = "Obtiene el estado actual de una solicitud de geocodificacion mediante su identificador unico (UUID)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada, devuelve su estado"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna operacion con ese ID")
    })
    public ResponseEntity<GeocodingStatusResponseDTO> get(
            @Parameter(description = "Identificador único de la solicitud", required = true)
            @RequestParam("id") UUID id) {

        return geolocalizationService.findById(id)
                .map(entity -> {
                    GeocodingStatusResponseDTO response = new GeocodingStatusResponseDTO(
                            entity.getId().toString(),
                            entity.getLatitude(),
                            entity.getLongitude(),
                            entity.getStatus().name(),
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
                                "No existe ninguna operacion con el id " + id
                        )));
    }

    private String buildStatusMessage(GeolocalizationEntity entity) {
        return switch (entity.getStatus()) {
            case PENDING -> "La geocodificacion sigue en proceso.";
            case COMPLETED -> "La geocodificacion se completo correctamente.";
            case ERROR -> entity.getErrorMessage() != null
                    ? "Hubo un error: " + entity.getErrorMessage()
                    : "La geocodificacion fallo por un error desconocido.";
        };
    }
}