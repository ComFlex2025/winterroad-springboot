package com.comflex.winterroad.domain.weather;

import com.comflex.winterroad.domain.weather.entity.WeatherLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WeatherLogRepository extends JpaRepository<WeatherLog, Integer> {

    // 최신 날씨 (인덱스 활용)
    @Query(value = """
    SELECT * FROM weather_log
    WHERE road_id = :roadId
    ORDER BY timestamp DESC
    LIMIT 1
""", nativeQuery = true)
    Optional<WeatherLog> findLatestByRoadId(Integer roadId);

}
