package challenge.api_geo.controller;

import challenge.api_geo.dto.response.GeocodingStatusResponseDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.repository.GeolocalizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GeocodingQueryController {

    private final GeolocalizationRepository repository;

    @GetMapping("/geocodificar")
    public ResponseEntity<GeocodingStatusResponseDTO> get(@RequestParam("id") UUID id) {
        return repository.findById(id)
                .map(entity -> ResponseEntity.ok(
                        new GeocodingStatusResponseDTO(
                                entity.getId().toString(),
                                entity.getLatitude(),
                                entity.getLongitude(),
                                entity.getStatus()
                        )
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new GeocodingStatusResponseDTO(
                                id.toString(),
                                null,
                                null,
                                "No existe la operación con id " + id
                        ))
                );
    }


    private GeocodingStatusResponseDTO toResponse(GeolocalizationEntity e) {
        return new GeocodingStatusResponseDTO(
                e.getId().toString(),
                e.getLatitude(),
                e.getLongitude(),
                e.getStatus()
        );
    }
}