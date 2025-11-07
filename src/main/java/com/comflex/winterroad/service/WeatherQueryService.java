package com.comflex.winterroad.service;

import com.comflex.winterroad.domain.RoadInfo;
import com.comflex.winterroad.domain.WeatherLog;
import com.comflex.winterroad.dto.WeatherResponseDto;
import com.comflex.winterroad.repository.RoadInfoRepository;
import com.comflex.winterroad.repository.WeatherLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherQueryService {

    private final RoadInfoRepository roadRepo;
    private final WeatherLogRepository weatherRepo;

    public WeatherResponseDto getNearestWeather(double lat, double lon) {
        RoadInfo nearest = roadRepo.findNearest(lat, lon)
                .orElseThrow(() -> new RuntimeException("❌ 가까운 도로를 찾을 수 없습니다."));

        WeatherLog latest = weatherRepo.findLatestByRoadId(nearest.getId())
                .orElseThrow(() -> new RuntimeException("❌ 날씨 데이터가 존재하지 않습니다."));

        return new WeatherResponseDto(
                nearest.getRoadName(),
                latest.getTemp(),
                latest.getHumidity(),
                latest.getSnow(),
                latest.getRainType(),
                latest.getWindSpeed(),
                latest.getTimestamp(),
                latest.getBaseDate(),  // ✅ 추가
                latest.getBaseTime()   // ✅ 추가
        );

    }
}
