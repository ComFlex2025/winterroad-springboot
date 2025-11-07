package com.comflex.winterroad.domain.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WeatherResponseDto {
    private String roadName;
    private Double temp;
    private Double humidity;
    private Double snow;
    private String rainType;
    private Double windSpeed;
    private LocalDateTime timestamp; // 저장 시각
    private String baseDate;         // 기상청 기준 날짜
    private String baseTime;         // 기상청 기준 시각
}
