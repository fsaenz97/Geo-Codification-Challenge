package challenge.geocodificador.messaging;

import challenge.geocodificador.clients.NominatimClient;
import challenge.geocodificador.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeocodingRequestListenerTest {

    private NominatimClient nominatimClient;
    private RabbitTemplate rabbitTemplate;
    private GeocodingRequestListener listener;

    @BeforeEach
    void setUp() {
        nominatimClient = mock(NominatimClient.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        listener = new GeocodingRequestListener(nominatimClient, rabbitTemplate);
    }

    @Test
    void shouldSendOkResultWhenGeocodingSucceeds() {
        // Arrange
        GeocodingRequestMessage msg = new GeocodingRequestMessage(
                UUID.randomUUID(), "Calle Falsa", "123", "Springfield", "1234", "Provincia", "Argentina"
        );

        when(nominatimClient.geocode(anyString()))
                .thenReturn(Optional.of(new NominatimClient.Coordinates(10.0, 20.0)));

        // Act
        listener.onMessage(msg);

        // Assert
        ArgumentCaptor<GeocodingResultMessage> captor = ArgumentCaptor.forClass(GeocodingResultMessage.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.RESPONSES_QUEUE), captor.capture());

        GeocodingResultMessage result = captor.getValue();
        assertEquals(msg.getId(), result.getId());
        assertEquals(10.0, result.getLatitude());
        assertEquals(20.0, result.getLongitude());
        assertEquals("OK", result.getStatus());
    }

    @Test
    void shouldSendErrorWhenGeocodeReturnsEmpty() {
        // Arrange
        GeocodingRequestMessage msg = new GeocodingRequestMessage(
                UUID.randomUUID(), "Calle Inventada", "999", "Ciudad", "0000", "Provincia", "Argentina"
        );

        when(nominatimClient.geocode(anyString())).thenReturn(Optional.empty());

        // Act
        listener.onMessage(msg);

        // Assert
        ArgumentCaptor<GeocodingResultMessage> captor = ArgumentCaptor.forClass(GeocodingResultMessage.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.RESPONSES_QUEUE), captor.capture());

        GeocodingResultMessage result = captor.getValue();
        assertEquals(msg.getId(), result.getId());
        assertNull(result.getLatitude());
        assertNull(result.getLongitude());
        assertEquals("ERROR", result.getStatus());
    }

    @Test
    void shouldSendErrorWhenExceptionOccurs() {
        // Arrange
        GeocodingRequestMessage msg = new GeocodingRequestMessage(
                UUID.randomUUID(), "Calle Bug", "404", "ErrorCity", "9999", "Provincia", "Argentina"
        );

        when(nominatimClient.geocode(anyString())).thenThrow(new RuntimeException("Simulated error"));

        // Act
        listener.onMessage(msg);

        // Assert
        ArgumentCaptor<GeocodingResultMessage> captor = ArgumentCaptor.forClass(GeocodingResultMessage.class);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.RESPONSES_QUEUE), captor.capture());

        GeocodingResultMessage result = captor.getValue();
        assertEquals(msg.getId(), result.getId());
        assertNull(result.getLatitude());
        assertNull(result.getLongitude());
        assertEquals("ERROR", result.getStatus());
    }
}