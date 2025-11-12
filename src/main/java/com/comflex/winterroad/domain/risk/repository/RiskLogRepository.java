package com.comflex.winterroad.domain.risk.repository;

import com.comflex.winterroad.domain.risk.dto.RiskResponseDto;
import com.comflex.winterroad.domain.risk.entity.RiskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RiskLogRepository extends JpaRepository<RiskLog, Long> {

    // 도로별 최신 위험도 조회
    @Query("""
        SELECT r FROM RiskLog r
        WHERE r.road.id = :roadId
        ORDER BY r.updatedAt DESC
        LIMIT 1
    """)
    Optional<RiskLog> findLatestByRoadId(Integer roadId);

    // 위험도 순으로 상위 10개 도로 (예: 지도 표시용)
    @Query("""
        SELECT r FROM RiskLog r
        ORDER BY r.riskScore DESC
        LIMIT 10
    """)
    List<RiskLog> findTop10ByRiskScore();

    @Query("""
    SELECT new com.comflex.winterroad.domain.risk.dto.RiskResponseDto(
        r.road.id,
        r.road.roadName,
        r.road.regionCode,
        r.riskScore,
        r.riskColor,
        r.updatedAt
    )
    FROM RiskLog r
""")
    List<RiskResponseDto> findAllWithRoad();



}
