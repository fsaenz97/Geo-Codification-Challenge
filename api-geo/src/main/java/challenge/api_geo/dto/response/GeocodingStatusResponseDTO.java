package challenge.api_geo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class GeocodingStatusResponseDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("latitud")
    private Double latitude;

    @JsonProperty("longitud")
    private Double longitude;

    @JsonProperty("estado")
    private String status;
}
