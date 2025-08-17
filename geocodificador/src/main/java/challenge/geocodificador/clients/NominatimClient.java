package challenge.geocodificador.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NominatimClient {

    private final RestClient.Builder builder;

    @Value("${nominatim.base-url}")
    private String baseUrl;

    @Value("${nominatim.email:}")
    private String contactEmail;

    public Optional<Coordinates> geocode(String query) {
        RestClient client = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, "geo-challenge/1.0 (" + contactEmail + ")")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        List<NominatimResponseItem> items = client.get()
                .uri(uri -> uri.path("/search")
                        .queryParam("q", query)
                        .queryParam("format", "json")
                        .queryParam("limit", "1")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<NominatimResponseItem>>() {});

        if (items == null || items.isEmpty()) return Optional.empty();

        NominatimResponseItem it = items.get(0);
        try {
            double lat = Double.parseDouble(it.lat());
            double lon = Double.parseDouble(it.lon());
            return Optional.of(new Coordinates(lat, lon));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public record Coordinates(double latitude, double longitude) {}
    public record NominatimResponseItem(String lat, String lon) {}
}
