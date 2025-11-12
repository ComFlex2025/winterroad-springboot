package com.comflex.winterroad.domain.road.controller;

import com.comflex.winterroad.domain.road.dto.GpsRiskResponseDto;
import com.comflex.winterroad.domain.road.service.GpsRiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
public class GpsRiskController {

    private final GpsRiskService gpsRiskService;

    /**
     * ✅ 현재 위치(lat, lon)를 기준으로 근처 도로 + 위험도 조회
     *
     * 예시 요청:
     * GET /api/gps/risk?lat=37.3921&lon=127.1212&rangeKm=2
     */
    @GetMapping("/risk")
    public ResponseEntity<List<GpsRiskResponseDto>> getNearbyRoadsWithRisk(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "2.0") double rangeKm
    ) {
        List<GpsRiskResponseDto> roads = gpsRiskService.findNearbyWithRisk(lat, lon, rangeKm);
        return ResponseEntity.ok(roads);
    }
}
