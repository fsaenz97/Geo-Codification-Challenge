package challenge.geocodificador.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class GeocodingResultMessageDTO {
    private UUID id;
    private Double latitude;
    private Double longitude;
    private String status;
}
