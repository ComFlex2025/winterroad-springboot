package com.comflex.winterroad.domain.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherService weatherService;

    // 1시간마다 실행
    @Scheduled(fixedRate = 3600000)
    public void runWeatherJob() {
        weatherService.updateWeatherData();
    }
}
