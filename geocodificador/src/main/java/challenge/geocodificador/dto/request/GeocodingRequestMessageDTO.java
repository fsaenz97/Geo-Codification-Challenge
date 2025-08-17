package challenge.geocodificador.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class GeocodingRequestMessageDTO {
    private UUID id;
    private String street;
    private String number;
    private String city;
    private String postalCode;
    private String province;
    private String country;
}
