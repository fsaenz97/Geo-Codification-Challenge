package challenge.api_geo.controller;

import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.service.GeolocalizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GeocodingQueryController.class)
class GeocodingQueryControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;
    @MockBean private GeolocalizationService service;

    private GeolocalizationEntity base(UUID id) {
        return GeolocalizationEntity.builder()
                .id(id)
                .street("X").number("1").city("BA")
                .postalCode("C1").province("CABA").country("AR")
                .status(GeolocalizationEntity.Status.PENDING)
                .build();
    }

    @Test
    void get_completed_returnsDtoWithCoords() throws Exception {
        UUID id = UUID.randomUUID();
        GeolocalizationEntity e = base(id);
        e.setStatus(GeolocalizationEntity.Status.COMPLETED);
        e.setLatitude(-34.6); e.setLongitude(-58.4);

        when(service.findById(id)).thenReturn(Optional.of(e));

        mvc.perform(get("/geocodificar").param("id", id.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.estado").value("COMPLETED"))
                .andExpect(jsonPath("$.latitud").value(-34.6))
                .andExpect(jsonPath("$.longitud").value(-58.4))
                .andExpect(jsonPath("$.mensaje").value("La geocodificacion se completo correctamente."));
    }

    @Test
    void get_pending_returnsPendingMessage() throws Exception {
        UUID id = UUID.randomUUID();
        GeolocalizationEntity e = base(id);
        e.setStatus(GeolocalizationEntity.Status.PENDING);

        when(service.findById(id)).thenReturn(Optional.of(e));

        mvc.perform(get("/geocodificar").param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDING"))
                .andExpect(jsonPath("$.mensaje").value("La geocodificacion sigue en proceso."));
    }

    @Test
    void get_error_returnsErrorMessage() throws Exception {
        UUID id = UUID.randomUUID();
        GeolocalizationEntity e = base(id);
        e.setStatus(GeolocalizationEntity.Status.ERROR);
        e.setErrorMessage("El geocodificador devolvio status='ERROR'");

        when(service.findById(id)).thenReturn(Optional.of(e));

        mvc.perform(get("/geocodificar").param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ERROR"))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.startsWith("Hubo un error:")));
    }

    @Test
    void get_notFound_returns404Body() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        mvc.perform(get("/geocodificar").param("id", id.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value("NOT_FOUND"));
    }
}