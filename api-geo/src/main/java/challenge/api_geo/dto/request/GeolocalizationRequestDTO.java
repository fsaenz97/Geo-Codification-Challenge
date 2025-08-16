package challenge.api_geo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeolocalizationRequestDTO {
    @NotBlank(message = "La calle no puede estar vacia")
    private String street;

    @NotBlank(message = "El numero no puede estar vacio")
    private String number;

    @NotBlank(message = "La ciudad no puede esta vacia")
    private String city;

    @NotBlank(message = "El codigo postal no puede estar vacio")
    private String postalCode;

    @NotBlank(message = "La provincia no puede estar vacia")
    private String province;

    @NotBlank(message = "El pais no puede estar vacio")
    private String country;
}
