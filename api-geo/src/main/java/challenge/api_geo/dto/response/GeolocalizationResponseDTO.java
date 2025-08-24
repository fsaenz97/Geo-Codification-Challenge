package challenge.api_geo.dto.response;

import challenge.api_geo.entity.GeolocalizationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeolocalizationResponseDTO {
    private UUID id;

    private String street;
    private String number;
    private String city;
    private String postalCode;
    private String province;
    private String country;

    private Double latitude;
    private Double longitude;

    private GeolocalizationEntity.Status status;

    private String errorCode;      // ej: "INVALID_ADDRESS"
    private String errorMessage;   // ej: "Dirección no encontrada"
}
