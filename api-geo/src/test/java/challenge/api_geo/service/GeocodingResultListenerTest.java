package challenge.api_geo.service;

import challenge.api_geo.dto.response.GeocodingResultMessageDTO;
import challenge.api_geo.entity.GeolocalizationEntity;
import challenge.api_geo.repository.GeolocalizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeocodingResultListenerTest {

    @Mock private GeolocalizationRepository repository;
    @InjectMocks private GeocodingResultListener listener;

    private GeolocalizationEntity newPendingEntity(UUID id) {
        return GeolocalizationEntity.builder()
                .id(id)
                .street("X")
                .number("1")
                .city("BA")
                .postalCode("C1")
                .province("CABA")
                .country("AR")
                .status(GeolocalizationEntity.Status.PENDING)
                .build();
    }

    @Test
    void whenSuccessStatusAndCoords_thenCompleted() {
        UUID id = UUID.randomUUID();
        GeolocalizationEntity e = newPendingEntity(id);

        when(repository.findById(id)).thenReturn(Optional.of(e));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        listener.onMessage(new GeocodingResultMessageDTO(id, -34.6, -58.4, "TERMINADO"));

        ArgumentCaptor<GeolocalizationEntity> cap = ArgumentCaptor.forClass(GeolocalizationEntity.class);
        verify(repository).save(cap.capture());
        GeolocalizationEntity saved = cap.getValue();

        assertEquals(GeolocalizationEntity.Status.COMPLETED, saved.getStatus());
        assertEquals(-34.6, saved.getLatitude());
        assertEquals(-58.4, saved.getLongitude());
        assertNull(saved.getErrorCode());
        assertNull(saved.getErrorMessage());
    }

    @Test
    void whenSuccessStatusButNullCoords_thenError() {
        UUID id = UUID.randomUUID();
        GeolocalizationEntity e = newPendingEntity(id);

        when(repository.findById(id)).thenReturn(Optional.of(e));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        listener.onMessage(new GeocodingResultMessageDTO(id, null, null, "TERMINADO"));

        ArgumentCaptor<GeolocalizationEntity> cap = ArgumentCaptor.forClass(GeolocalizationEntity.class);
        verify(repository).save(cap.capture());
        GeolocalizationEntity saved = cap.getValue();

        assertEquals(GeolocalizationEntity.Status.ERROR, saved.getStatus());
        assertNull(saved.getLatitude());
        assertNull(saved.getLongitude());
        assertEquals("GEOCODER_NO_COORDS", saved.getErrorCode());
        assertNotNull(saved.getErrorMessage());
    }

    @Test
    void whenInvalidStatus_thenError() {
        UUID id = UUID.randomUUID();
        GeolocalizationEntity e = newPendingEntity(id);

        when(repository.findById(id)).thenReturn(Optional.of(e));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        listener.onMessage(new GeocodingResultMessageDTO(id, null, null, "ERROR"));

        ArgumentCaptor<GeolocalizationEntity> cap = ArgumentCaptor.forClass(GeolocalizationEntity.class);
        verify(repository).save(cap.capture());
        GeolocalizationEntity saved = cap.getValue();

        assertEquals(GeolocalizationEntity.Status.ERROR, saved.getStatus());
        assertEquals("GEOCODER_STATUS_INVALID", saved.getErrorCode());
    }

    @Test
    void whenIdNotFound_thenDoNothing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        listener.onMessage(new GeocodingResultMessageDTO(id, -34.0, -58.0, "OK"));

        verify(repository, never()).save(any());
    }
}