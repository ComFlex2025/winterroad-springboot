package com.comflex.winterroad.infra;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NaverMapClient {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Coordinate> getRoute(double startLat, double startLon, String endAddress) {
        String geoUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="
                + URLEncoder.encode(endAddress, StandardCharsets.UTF_8);

        HttpHeaders headers = createHeaders();
        ResponseEntity<JsonNode> geoRes = restTemplate.exchange(
                geoUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

        JsonNode addrNode = geoRes.getBody().path("addresses").get(0);
        double endLon = addrNode.path("x").asDouble();
        double endLat = addrNode.path("y").asDouble();

        String dirUrl = String.format(
                "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=%f,%f&goal=%f,%f",
                startLon, startLat, endLon, endLat
        );

        ResponseEntity<JsonNode> dirRes = restTemplate.exchange(
                dirUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

        List<Coordinate> coords = new ArrayList<>();
        JsonNode path = dirRes.getBody().path("route").path("traoptimal").get(0).path("path");
        for (JsonNode node : path) {
            coords.add(new Coordinate(node.get(1).asDouble(), node.get(0).asDouble())); // lat, lon
        }

        return coords;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);
        return headers;
    }
}
