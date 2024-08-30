package ru.practicum;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class StatsClient {
    private final RestTemplate rest;

    public StatsClient(RestTemplate rest) {
        this.rest = rest;
    }

    public ResponseEntity<Object> getRequest(String uri) {
        return prepareRequest(uri, null, HttpMethod.GET);
    }

    public <T> ResponseEntity<Object> postRequest(String uri, T body) {
        return prepareRequest(uri, body, HttpMethod.POST);
    }

    private <T> ResponseEntity<Object> prepareRequest(String uri, T body, HttpMethod method) {
        HttpEntity<Object> entity = new HttpEntity<>(body, this.defaultHeaders());
        return rest.exchange(uri, method, entity, Object.class);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}