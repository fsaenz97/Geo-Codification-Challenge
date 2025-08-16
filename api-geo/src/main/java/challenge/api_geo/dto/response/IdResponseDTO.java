package challenge.api_geo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class IdResponseDTO {
    @JsonProperty("Id")
    private String id;
}