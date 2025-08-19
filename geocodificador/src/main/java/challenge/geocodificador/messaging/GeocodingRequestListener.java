package challenge.geocodificador.messaging;

import challenge.geocodificador.clients.NominatimClient;
import challenge.geocodificador.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingRequestListener {

    private final NominatimClient nominatim;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.REQUESTS_QUEUE)
    public void onMessage(GeocodingRequestMessage msg) {
        String address = String.format("%s %s, %s, %s, %s, %s",
                msg.getStreet(), msg.getNumber(), msg.getCity(),
                msg.getPostalCode(), msg.getProvince(), msg.getCountry());

        log.info("Geocodificando: {} (id={})", address, msg.getId());

        try {

            var opt = nominatim.geocode(address);
            GeocodingResultMessage result = opt
                    .map(c -> new GeocodingResultMessage(msg.getId(), c.latitude(), c.longitude(), "OK"))
                    .orElseGet(() -> new GeocodingResultMessage(msg.getId(), null, null, "ERROR"));

            rabbitTemplate.convertAndSend(RabbitMQConfig.RESPONSES_QUEUE, result);
            log.info("Resultado publicado: {}", result);
        } catch (Exception e) {
            log.error("Error geocodificando", e);
            GeocodingResultMessage error = new GeocodingResultMessage(msg.getId(), null, null, "ERROR");
            rabbitTemplate.convertAndSend(RabbitMQConfig.RESPONSES_QUEUE, error);
        }
    }
}
