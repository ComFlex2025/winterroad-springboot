package com.comflex.winterroad.domain.road.service;

import com.comflex.winterroad.domain.road.dto.GpsRiskResponseDto;
import com.comflex.winterroad.domain.road.RoadInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GpsRiskService {

    private final RoadInfoRepository roadInfoRepository;

    public List<GpsRiskResponseDto> findNearbyWithRisk(double lat, double lon, double rangeKm) {
        double degree = rangeKm / 111.0; // km → 위도/경도 근사 변환
        double minLat = lat - degree;
        double maxLat = lat + degree;
        double minLon = lon - degree;
        double maxLon = lon + degree;

        // ✅ Repository 메서드 정의에 맞게 4개 인자만 전달
        List<Object[]> results = roadInfoRepository.findNearbyWithRisk(minLat, maxLat, minLon, maxLon);

        return results.stream()
                .map(obj -> new GpsRiskResponseDto(
                        (Integer) obj[0],  // r.id
                        (String) obj[1],   // r.roadName
                        (Double) obj[2],   // r.latitude
                        (Double) obj[3],   // r.longitude
                        (Double) obj[6],   // rl.riskScore
                        (String) obj[7]    // rl.riskColor
                ))
                .collect(Collectors.toList());
    }
}
