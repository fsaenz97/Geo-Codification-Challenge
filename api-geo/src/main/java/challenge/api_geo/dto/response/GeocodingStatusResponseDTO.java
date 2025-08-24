package challenge.api_geo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingStatusResponseDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("latitud")
    private Double latitude;

    @JsonProperty("longitud")
    private Double longitude;

    @JsonProperty("estado")
    private String status;

    @JsonProperty("mensaje")
    private String message;

    public GeocodingStatusResponseDTO(String id, Double latitude, Double longitude, String status) {
        this(id, latitude, longitude, status, null);
    }
}
