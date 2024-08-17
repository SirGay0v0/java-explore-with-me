package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

public class StatsClient {

    private final RestTemplate rest;

    @Value("${shareit-server.url}")
    private String uri;

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    public EndpointHitRequestDto saveHit(String app, String uri, String ip) {
        EndpointHitRequestDto endpoint = new EndpointHitRequestDto()
                .setApp(app)
                .setUri(uri)
                .setIp(ip)
                .setTimestamp(LocalDateTime.now().toString());
        ResponseEntity<EndpointHitRequestDto> response = rest.postForEntity(
                uri + "/hit", endpoint, EndpointHitRequestDto.class);
        return response.getBody();
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique);

        ResponseEntity<List<ViewStats>> response = rest.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStats>>() {
                }
        );
        return response.getBody();
    }
}