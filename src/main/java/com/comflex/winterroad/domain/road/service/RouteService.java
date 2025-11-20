package com.comflex.winterroad.domain.road.service;

import com.comflex.winterroad.domain.road.RoadInfoRepository;
import com.comflex.winterroad.domain.road.dto.RoadInfoResponseDto;
import com.comflex.winterroad.domain.road.dto.RouteRequestDto;
import com.comflex.winterroad.infra.Coordinate;
import com.comflex.winterroad.infra.NaverMapClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RoadInfoRepository roadInfoRepository;
    private final NaverMapClient naverMapClient;

    public List<RoadInfoResponseDto> findRoadsAlongRoute(RouteRequestDto request) {

        // ✅ 1️⃣ 경로 API 호출 (좌표 기반 우선, 주소 기반 fallback)
        List<Coordinate> routePoints;
        boolean hasEndCoords = request.getEndLat() != 0.0 && request.getEndLon() != 0.0;

        if (hasEndCoords) {
            // ✅ endLat / endLon 이 있으면 → 좌표 기반 호출
            routePoints = naverMapClient.getRouteByCoords(
                    request.getStartLat(), request.getStartLon(),
                    request.getEndLat(), request.getEndLon()
            );
        } else {
            // ✅ 없으면 → 주소 기반 호출
            routePoints = naverMapClient.getRoute(
                    request.getStartLat(), request.getStartLon(),
                    request.getEndAddress()
            );
        }

        // 2️⃣ 범위 계산
        double buffer = 0.01;
        double minLat = routePoints.stream().mapToDouble(Coordinate::getLat).min().orElse(0) - buffer;
        double maxLat = routePoints.stream().mapToDouble(Coordinate::getLat).max().orElse(0) + buffer;
        double minLon = routePoints.stream().mapToDouble(Coordinate::getLon).min().orElse(0) - buffer;
        double maxLon = routePoints.stream().mapToDouble(Coordinate::getLon).max().orElse(0) + buffer;

        // 3️⃣ 도로 + 위험도 조회
        List<Object[]> rawList = roadInfoRepository.findNearbyWithRisk(minLat, maxLat, minLon, maxLon);

        // 4️⃣ DTO 변환 및 거리 필터링
        double thresholdKm = 0.2; // 허용 오차 (약 200m)
        return rawList.stream()
                .map(r -> RoadInfoResponseDto.builder()
                        .id((Integer) r[0])
                        .roadName((String) r[1])
                        .latitude((Double) r[2])
                        .longitude((Double) r[3])
                        .description((String) r[4])
                        .regionCode((String) r[5])
                        .riskScore((Double) r[6])
                        .riskLevel( (String) r[7])
                        .build())
                .filter(dto -> routePoints.stream().anyMatch(p ->
                        haversineDistance(dto.getLatitude(), dto.getLongitude(), p.getLat(), p.getLon()) < thresholdKm))
                .distinct()
                .collect(Collectors.toList());
    }

    /** 🌍 두 지점 간 거리 계산 (단위: km) */
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
