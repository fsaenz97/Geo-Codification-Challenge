package challenge.api_geo.controller;

import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
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
    public ResponseEntity<?> geolocalizar(@Valid @RequestBody GeolocalizationRequestDTO request) {
        // 1 Llama al servicio para procesar la solicitud
        UUID requestId = geolocalizationService.processGeolocalizationRequest(request);

        // 2 Construye la URL para el endpoint de consulta
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(requestId)
                .toUri();

        // 3 Devuelve la respuesta HTTP 202 Accepted
        return ResponseEntity.accepted().location(location).body(requestId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getGeolocalizacion(@PathVariable UUID id) {
        // Solo devuelve una respuesta de prueba.
        String response = "Consulta para el ID: " + id;
        return ResponseEntity.ok(response);
    }
}
