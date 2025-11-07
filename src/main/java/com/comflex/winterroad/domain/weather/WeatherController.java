package com.comflex.winterroad.domain.weather;

import com.comflex.winterroad.domain.weather.service.WeatherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//test컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherQueryService queryService;

    @GetMapping("/nearest")
    public WeatherResponseDto getNearestWeather(@RequestParam double lat, @RequestParam double lon) {
        return queryService.getNearestWeather(lat, lon);
    }
}
