package com.comflex.winterroad.repository;

import com.comflex.winterroad.domain.RoadInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoadInfoRepository extends JpaRepository<RoadInfo, Integer> {

    @Query("""
        SELECT r FROM RoadInfo r
        ORDER BY ((r.latitude - :lat)*(r.latitude - :lat)
                 + (r.longitude - :lon)*(r.longitude - :lon))
    """)
    Optional<RoadInfo> findNearest(double lat, double lon);
}
