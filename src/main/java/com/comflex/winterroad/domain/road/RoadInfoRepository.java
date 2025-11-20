package com.comflex.winterroad.domain.road;

import com.comflex.winterroad.domain.road.entity.RoadInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoadInfoRepository extends JpaRepository<RoadInfo, Integer> {
    //특정 좌표 근처 가장 가까운 도로 1개 (날씨 조회할 때 이용)
    @Query("""
        SELECT r FROM RoadInfo r
        ORDER BY ((r.latitude - :lat)*(r.latitude - :lat)
                 + (r.longitude - :lon)*(r.longitude - :lon))
    """)
    Optional<RoadInfo> findNearest(double lat, double lon);


    //경로 전체 기준 근처 도로 후보 조회
    @Query("""
    SELECT r FROM RoadInfo r
    WHERE r.latitude BETWEEN :minLat AND :maxLat
      AND r.longitude BETWEEN :minLon AND :maxLon
""")
    List<RoadInfo> findNearby(double minLat, double maxLat, double minLon, double maxLon);

    // ✅ 위험도 JOIN 조회용 (최신 risk_log 포함)
    @Query("""
    SELECT r.id AS id, r.roadName AS roadName, r.latitude AS latitude, 
           r.longitude AS longitude, r.description AS description, 
           r.regionCode AS regionCode, rl.riskScore AS riskScore, rl.riskLevel AS riskLevel
    FROM RoadInfo r
    LEFT JOIN RiskLog rl ON rl.road.id = r.id
    WHERE r.latitude BETWEEN :minLat AND :maxLat
      AND r.longitude BETWEEN :minLon AND :maxLon
""")
    List<Object[]> findNearbyWithRisk(double minLat, double maxLat, double minLon, double maxLon);

}
