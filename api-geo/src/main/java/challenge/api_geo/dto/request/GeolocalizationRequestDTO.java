package challenge.api_geo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeolocalizationRequestDTO {

    @NotBlank(message = "La calle no puede estar vacía")
    private String street;

    @NotBlank(message = "El número no puede estar vacío")
    private String number;

    @NotBlank(message = "La ciudad no puede estar vacía")
    private String city;

    @NotBlank(message = "El código postal no puede estar vacío")
    private String postalCode;

    @NotBlank(message = "La provincia no puede estar vacía")
    private String province;

    @NotBlank(message = "El país no puede estar vacío")
    private String country;
}
