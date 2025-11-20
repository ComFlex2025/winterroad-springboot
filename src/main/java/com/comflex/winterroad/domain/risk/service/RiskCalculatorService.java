package com.comflex.winterroad.domain.risk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskCalculatorService {

    private final JdbcTemplate jdbcTemplate;

    // 매시 정각마다 실행 (매시간 위험도 갱신)
    @Scheduled(cron = "0 0 * * * *")
    public void updateRiskLog() {
        log.info("🚨 위험도 계산 및 risk_log 테이블 갱신 시작");

        String sql = """
INSERT INTO risk_log (road_id, risk_score, risk_level, updated_at)
SELECT road_id,
       risk_score,
       CASE
         WHEN risk_score >= 80 THEN '매우 위험'
         WHEN risk_score >= 60 THEN '위험'
         WHEN risk_score >= 40 THEN '보통'
         WHEN risk_score >= 20 THEN '일반'
         ELSE '안전'
       END AS risk_level,
       NOW() AT TIME ZONE 'Asia/Seoul'
FROM vw_risk_computed
ON CONFLICT (road_id)
DO UPDATE SET
  risk_score = EXCLUDED.risk_score,
  risk_level = EXCLUDED.risk_level,
  updated_at = NOW() AT TIME ZONE 'Asia/Seoul';
""";



        try {
            int updated = jdbcTemplate.update(sql);
            log.info("✅ 위험도 갱신 완료 — {}개 도로 반영됨", updated);
        } catch (Exception e) {
            log.error("❌ 위험도 갱신 실패: {}", e.getMessage(), e);
        }

    }
    public void calculateAndInsertRisk() {
        updateRiskLog();
    }

}
