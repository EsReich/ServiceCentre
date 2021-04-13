package com.jm.students.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.students.model.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class GeocodingServiceHereImpl implements GeocodingService {

    private String hereMapsApiKey = "vLzuZVgjeNRt1lGlVutRAQWCli2hPzcHenxs9gcxdtk";

    private String hereMapsApiUrl = "https://geocode.search.hereapi.com/v1/geocode";

    public void setHereMapsApiUrl(String hereMapsApiUrl) {
        this.hereMapsApiUrl = hereMapsApiUrl;
    }

    public void setHereMapsApiKey(String hereMapsApiKey) {
        this.hereMapsApiKey = hereMapsApiKey;
    }

    @Override
    public Optional<Location> getLocation(String address) {
        Optional<Location> result = Optional.empty();
        RestTemplate restTemplate = new RestTemplate();
        String url = hereMapsApiUrl +
                "?q=" +
                address.replaceAll(" ", "+") +
                "&apiKey=" +
                hereMapsApiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode position = objectMapper.readTree(response.getBody()).get("items").get(0).get("position");
                double lat = position.get("lat").asDouble();
                double lng = position.get("lng").asDouble();
                return Optional.of(new Location(lat, lng));
            } catch (JsonProcessingException ignored) {
            }
        }
        return result;

    }
}
