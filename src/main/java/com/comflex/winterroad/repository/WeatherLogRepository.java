package com.comflex.winterroad.repository;

import com.comflex.winterroad.domain.WeatherLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WeatherLogRepository extends JpaRepository<WeatherLog, Integer> {

    // 최신 날씨 (인덱스 활용)
    @Query("""
        SELECT w FROM WeatherLog w
        WHERE w.roadInfo.id = :roadId
        ORDER BY w.timestamp DESC
    """)
    Optional<WeatherLog> findLatestByRoadId(Integer roadId);
}
