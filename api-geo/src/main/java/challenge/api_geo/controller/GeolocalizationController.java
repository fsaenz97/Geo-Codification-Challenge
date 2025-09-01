package challenge.api_geo.controller;

import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
import challenge.api_geo.dto.response.IdResponseDTO;
import challenge.api_geo.service.GeolocalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Geolocalización", description = "Operaciones relacionadas con la creación de solicitudes de geolocalización")
public class GeolocalizationController {

    private final GeolocalizationService geolocalizationService;

    @PostMapping
    @Operation(
            summary = "Crear una solicitud de geolocalización",
            description = "Crea una nueva solicitud para geolocalizar una dirección y devuelve un ID para consultar el estado posteriormente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Solicitud aceptada y en proceso"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (datos incompletos o mal formateados)")
    })
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
}