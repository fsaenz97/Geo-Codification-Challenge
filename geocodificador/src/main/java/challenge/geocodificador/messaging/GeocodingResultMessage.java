package challenge.geocodificador.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class GeocodingResultMessage {
    private UUID id;
    private Double latitude;
    private Double longitude;
    private String status;
}
