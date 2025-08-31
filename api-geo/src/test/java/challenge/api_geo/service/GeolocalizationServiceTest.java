package challenge.api_geo.service;

import challenge.api_geo.config.RabbitMQConfig;
import challenge.api_geo.dto.request.GeocodingRequestMessageDTO;
import challenge.api_geo.dto.request.GeolocalizationRequestDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.repository.GeolocalizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeolocalizationServiceTest {

    @Mock private GeolocalizationRepository repository;
    @Mock private RabbitTemplate rabbitTemplate;
    @InjectMocks private GeolocalizationService service;

    @Test
    void processGeolocalizationRequest_savesPendingAndSendsMessage() {
        // given: request válida
        GeolocalizationRequestDTO req = new GeolocalizationRequestDTO();
        req.setStreet("Corrientes");
        req.setNumber("1234");
        req.setCity("Buenos Aires");
        req.setPostalCode("C1043");
        req.setProvince("CABA");
        req.setCountry("Argentina");

        // mock: cuando guarda, le inyecto un id
        when(repository.save(any())).thenAnswer(inv -> {
            GeolocalizationEntity e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        // when
        UUID id = service.processGeolocalizationRequest(req);

        // then: id no nulo
        assertNotNull(id);

        // se guardó con estado PENDING
        ArgumentCaptor<GeolocalizationEntity> entityCap = ArgumentCaptor.forClass(GeolocalizationEntity.class);
        verify(repository).save(entityCap.capture());
        assertEquals(GeolocalizationEntity.Status.PENDING, entityCap.getValue().getStatus());

        // se envió el mensaje a la cola correcta con el id correcto
        ArgumentCaptor<GeocodingRequestMessageDTO> msgCap = ArgumentCaptor.forClass(GeocodingRequestMessageDTO.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.REQUESTS_QUEUE), msgCap.capture());
        assertEquals(id, msgCap.getValue().getId());
        assertEquals("Corrientes", msgCap.getValue().getStreet());
    }
}