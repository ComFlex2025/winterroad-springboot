package com.comflex.winterroad.domain.risk.controller;

import com.comflex.winterroad.domain.risk.dto.RiskTopResponseDto;
import com.comflex.winterroad.domain.risk.entity.RiskLog;
import com.comflex.winterroad.domain.risk.repository.RiskLogRepository;
import com.comflex.winterroad.domain.risk.service.RiskCalculatorService;
import com.comflex.winterroad.domain.road.entity.RoadInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskLogController {

    private final RiskLogRepository riskLogRepository;
    private final RiskCalculatorService riskCalculatorService;

    /**
     * ✅ 전체 도로별 최신 위험도 조회
     */
    @GetMapping
    public ResponseEntity<List<RiskLog>> getAllRiskLogs() {
        List<RiskLog> list = riskLogRepository.findAll();
        return ResponseEntity.ok(list);
    }

    /**
     * ✅ 특정 도로의 최신 위험도 조회
     * /api/risk/road/12 → 도로 ID 12의 최신 위험도 반환
     */
    @GetMapping("/road/{roadId}")
    public ResponseEntity<RiskTopResponseDto> getLatestRisk(@PathVariable Integer roadId) {

        Pageable pageable = PageRequest.of(0, 1);

        List<RiskTopResponseDto> result =
                riskLogRepository.findLatestByRoadId(roadId, pageable);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result.get(0));
    }

    /**
     * ✅ 상위 위험도 10개 구간 조회
     * 프론트 “가장 위험한 도로 TOP10” 표시에 활용
     */
    @GetMapping("/top")
    public ResponseEntity<List<RiskTopResponseDto>> getTopRiskRoads() {
        List<RiskTopResponseDto> top = riskCalculatorService.getTopRiskRoads();
        return ResponseEntity.ok(top);
    }




}
