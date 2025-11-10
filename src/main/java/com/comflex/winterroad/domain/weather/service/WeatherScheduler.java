package com.comflex.winterroad.domain.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherService weatherService;

    // 1시간마다 실행
    @Scheduled(fixedDelay = 3600000, initialDelay = 10000) // 앱 시작 10초 후 첫 실행, 이후 1시간 간격
    public void runWeatherJob() {
        weatherService.updateWeatherData();
    }

}
