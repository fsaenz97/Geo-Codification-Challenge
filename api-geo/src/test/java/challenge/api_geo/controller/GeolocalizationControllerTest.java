package challenge.api_geo.controller;

import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
import challenge.api_geo.service.GeolocalizationService;
import challenge.api_geo.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GeolocalizationController.class)
@Import(GlobalExceptionHandler.class)
class GeolocalizationControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;
    @MockBean private GeolocalizationService service;

    @Test
    void post_ok_returns202AndLocation() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.processGeolocalizationRequest(any())).thenReturn(id);

        GeolocalizationRequestDTO dto = new GeolocalizationRequestDTO();
        dto.setStreet("Corrientes"); dto.setNumber("1234");
        dto.setCity("Buenos Aires"); dto.setPostalCode("C1043");
        dto.setProvince("CABA"); dto.setCountry("Argentina");

        mvc.perform(post("/geolocalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", containsString("/geolocalizar/" + id)))
                .andExpect(jsonPath("$.Id").value(id.toString()));
    }

    @Test
    void post_invalid_returns400() throws Exception {
        // omito "street"
        String body = """
        {
          "number":"123",
          "city":"BA",
          "postalCode":"C1",
          "province":"CABA",
          "country":"AR"
        }
        """;

        mvc.perform(post("/geolocalizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}