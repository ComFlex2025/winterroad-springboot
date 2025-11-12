package com.comflex.winterroad.infra;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverMapClient {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /** ✅ 기존: 주소 기반 경로 탐색 */
    public List<Coordinate> getRoute(double startLat, double startLon, String endAddress) {
        String geoUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="
                + URLEncoder.encode(endAddress, StandardCharsets.UTF_8);

        HttpHeaders headers = createHeaders();
        ResponseEntity<JsonNode> geoRes = restTemplate.exchange(
                geoUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

        JsonNode addrNode = geoRes.getBody().path("addresses").get(0);
        double endLon = addrNode.path("x").asDouble();
        double endLat = addrNode.path("y").asDouble();

        return requestDirection(startLat, startLon, endLat, endLon, headers);
    }

    /** ✅ 추가: 좌표 기반 경로 탐색 */
    public List<Coordinate> getRouteByCoords(double startLat, double startLon, double endLat, double endLon) {
        HttpHeaders headers = createHeaders();
        return requestDirection(startLat, startLon, endLat, endLon, headers);
    }

    /** ✅ 공통 경로 요청 로직 */
    private List<Coordinate> requestDirection(double startLat, double startLon,
                                              double endLat, double endLon,
                                              HttpHeaders headers) {
        String dirUrl = String.format(
                "https://maps.apigw.ntruss.com/map-direction/v1/driving?start=%f,%f&goal=%f,%f&option=trafast",
                startLon, startLat, endLon, endLat
        );

        ResponseEntity<JsonNode> dirRes = restTemplate.exchange(
                dirUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

        JsonNode body = dirRes.getBody();
        if (body == null) {
            log.error("❌ Naver API 응답이 null입니다. URL: {}", dirUrl);
            return List.of();
        }

        JsonNode routeNode = body.path("route");
        if (routeNode.isMissingNode()) {
            log.error("❌ route 노드가 존재하지 않습니다: {}", body.toPrettyString());
            return List.of();
        }

        // ✅ traoptimal / trafast 자동 감지
        JsonNode routeArray;
        if (routeNode.has("trafast")) {
            routeArray = routeNode.path("trafast");
        } else if (routeNode.has("traoptimal")) {
            routeArray = routeNode.path("traoptimal");
        } else {
            log.error("🚫 route 내부에 traoptimal 또는 trafast 경로가 없습니다: {}", routeNode.toPrettyString());
            return List.of();
        }

        if (!routeArray.isArray() || routeArray.isEmpty()) {
            log.error("🚫 경로 배열이 비어 있습니다: {}", routeArray.toPrettyString());
            return List.of();
        }

        JsonNode path = routeArray.get(0).path("path");
        if (!path.isArray()) {
            log.error("🚫 path 데이터가 배열 형식이 아닙니다: {}", routeArray.toPrettyString());
            return List.of();
        }

        List<Coordinate> coords = new ArrayList<>();
        for (JsonNode node : path) {
            // [lon, lat] 형태이므로 반대로 주의!
            if (node.size() >= 2) {
                coords.add(new Coordinate(node.get(1).asDouble(), node.get(0).asDouble())); // lat, lon
            }
        }

        log.info("✅ 총 {}개의 좌표를 수신했습니다.", coords.size());
        return coords;
    }


    private HttpHeaders createHeaders() {
        System.out.println("🟢 NAVER API KEYS → ID: " + clientId + ", SECRET: " + clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);
        return headers;
    }

}
