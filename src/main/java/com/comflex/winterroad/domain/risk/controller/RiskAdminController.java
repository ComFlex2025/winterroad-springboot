package com.comflex.winterroad.domain.risk.controller;

import com.comflex.winterroad.domain.risk.service.RiskCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskAdminController {

    private final RiskCalculatorService riskCalculator;

    @PostMapping("/refresh")
    public ResponseEntity<String> manualRefresh() {
        riskCalculator.calculateAndInsertRisk();
        return ResponseEntity.ok("✅ 위험도 수동 갱신 완료");
    }
}
