package challenge.api_geo.controller;

import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
import challenge.api_geo.dto.response.IdResponseDTO;
import challenge.api_geo.service.GeolocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/geolocalizar")
@RequiredArgsConstructor
public class GeolocalizationController {

    private final GeolocalizationService geolocalizationService;

    @PostMapping
    public ResponseEntity<IdResponseDTO> geolocalizar(@Valid @RequestBody GeolocalizationRequestDTO request) {
        UUID requestId = geolocalizationService.processGeolocalizationRequest(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(requestId)
                .toUri();

        return ResponseEntity
                .accepted()
                .location(location)
                .body(new IdResponseDTO(requestId.toString()));
    }

    // dejamos el GET provisorio, luego lo adaptamos al formato solicitado por el enunciado
}
