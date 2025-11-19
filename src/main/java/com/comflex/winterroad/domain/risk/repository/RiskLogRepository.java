package com.comflex.winterroad.domain.risk.repository;
import org.springframework.data.domain.Pageable;

import com.comflex.winterroad.domain.risk.dto.RiskResponseDto;
import com.comflex.winterroad.domain.risk.dto.RiskTopResponseDto;
import com.comflex.winterroad.domain.risk.entity.RiskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RiskLogRepository extends JpaRepository<RiskLog, Long> {

    // 도로별 최신 위험도 조회
    @Query("""
    SELECT new com.comflex.winterroad.domain.risk.dto.RiskTopResponseDto(
        r.road.id,
        r.road.roadName,
        r.road.regionCode,
        r.riskScore,
        r.riskColor,
        r.updatedAt
    )
    FROM RiskLog r
    WHERE r.road.id = :roadId
    ORDER BY r.updatedAt DESC
""")
    List<RiskTopResponseDto> findLatestByRoadId(Integer roadId, Pageable pageable);

    // 위험도 순으로 상위 10개 도로
    @Query("""
    SELECT new com.comflex.winterroad.domain.risk.dto.RiskTopResponseDto(
        r.road.id,
        r.road.roadName,
        r.road.regionCode,
        r.riskScore,
        r.riskColor,
        r.updatedAt
    )
    FROM RiskLog r
    ORDER BY r.riskScore DESC
""")
    List<RiskTopResponseDto> findTopRisk(Pageable pageable);

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
