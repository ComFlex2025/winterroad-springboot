package com.comflex.winterroad.domain.weather.entity;

import com.comflex.winterroad.domain.road.entity.RoadInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "weather_log",
        uniqueConstraints = @UniqueConstraint(columnNames = {"road_id", "base_date", "base_time"})
)
@Getter
@Setter
@NoArgsConstructor
public class WeatherLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_id", nullable = false)
    private RoadInfo roadInfo;

    private LocalDateTime timestamp;

    private Double temp;
    private Double snow;
    private Double humidity;

    @Column(name = "rain_type")
    private String rainType;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "source_type")
    private String sourceType = "KMA";

    @Column(name = "base_date", length = 8)
    private String baseDate;   // 예: "20251107"

    @Column(name = "base_time", length = 4)
    private String baseTime;   // 예: "2000"
}
