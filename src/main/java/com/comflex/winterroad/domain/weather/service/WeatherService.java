package com.comflex.winterroad.domain.weather.service;

import com.comflex.winterroad.domain.road.entity.RoadInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${external.weather.base-url}")
    private String baseUrl;

    @Value("${external.weather.service-key}")
    private String serviceKey;

    @Value("${external.weather.data-type}")
    private String dataType;

    @Value("${external.weather.num-of-rows}")
    private int numOfRows;

    @Value("${external.weather.page-no}")
    private int pageNo;

    /** 1️⃣ 도로별 기상 데이터 수집 */
    public void updateWeatherData() {
        String sql = "SELECT id, latitude, longitude FROM road_info_selected_mv";
        List<RoadInfo> roads = jdbcTemplate.query(sql, (rs, rowNum) -> {
            RoadInfo r = new RoadInfo();
            r.setId(rs.getInt("id"));
            r.setLatitude(rs.getDouble("latitude"));
            r.setLongitude(rs.getDouble("longitude"));
            return r;
        });

        log.info("🚗 총 {}개 도로 구간 날씨 데이터 수집 시작", roads.size());

        for (RoadInfo road : roads) {
            try {
                Map<String, Object> weather = callWeatherApi(road.getLatitude(), road.getLongitude());
                if (weather == null || weather.isEmpty()) {
                    log.warn("⚠️ 도로 {}: 수신된 날씨 데이터 없음", road.getId());
                    continue;
                }
                saveWeatherData(road, weather);
                Thread.sleep(1000); // API rate limit 대응 (초당 5회)
            } catch (Exception e) {
                log.error("❌ 도로 {} 날씨 수집 실패: {}", road.getId(), e.getMessage());
            }
        }
    }

    /** 2️⃣ 기상청 API 호출 및 파싱 */
    @SuppressWarnings("unchecked")
    private Map<String, Object> callWeatherApi(double lat, double lon) {
        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = LocalTime.now().minusHours(1)
                .withMinute(0).withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("HH00"));
        int nx = convertToGridX(lat, lon);
        int ny = convertToGridY(lat, lon);

        try {
            String url = String.format(
                    "%s?serviceKey=%s&pageNo=%d&numOfRows=%d&dataType=%s&base_date=%s&base_time=%s&nx=%d&ny=%d",
                    baseUrl, serviceKey, pageNo, numOfRows, dataType, baseDate, baseTime, nx, ny
            );

            URI uri = new URI(url);
            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

            if (response == null) {
                log.warn("⚠️ API 응답 null (nx={}, ny={})", nx, ny);
                return Map.of();
            }

            // ✅ JSON 파싱
            Map<String, Object> responseMap = (Map<String, Object>) response.get("response");
            if (responseMap == null) return Map.of();

            Map<String, Object> body = (Map<String, Object>) responseMap.get("body");
            if (body == null) return Map.of();

            Map<String, Object> items = (Map<String, Object>) body.get("items");
            if (items == null) return Map.of();

            List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
            if (itemList == null || itemList.isEmpty()) return Map.of();

            double temp = 0, humidity = 0, windSpeed = 0, snow = 0, rainType = 0;

            for (Map<String, Object> item : itemList) {
                String category = (String) item.get("category");
                double value = Double.parseDouble(item.get("obsrValue").toString());
                switch (category) {
                    case "T1H" -> temp = value;
                    case "REH" -> humidity = value;
                    case "WSD" -> windSpeed = value;
                    case "RNS", "SN1" -> snow = value;
                    case "PTY" -> rainType = value;
                }
            }

            log.debug("✅ 좌표({}, {}) → T={}℃, H={}%, WSD={}, PTY={}, SN={}",
                    nx, ny, temp, humidity, windSpeed, rainType, snow);

            return Map.of(
                    "temp", temp,
                    "humidity", humidity,
                    "snow", snow,
                    "windSpeed", windSpeed,
                    "rainType", rainType
            );

        } catch (Exception e) {
            log.error("❌ API 호출/파싱 실패 (lat={}, lon={}): {}", lat, lon, e.getMessage());
            return Map.of();
        }
    }

    /** 3️⃣ DB 저장 (UPSERT) */
    private void saveWeatherData(RoadInfo road, Map<String, Object> data) {
        try {
            String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = LocalTime.now().minusHours(1)
                    .withMinute(0).withSecond(0).withNano(0)
                    .format(DateTimeFormatter.ofPattern("HH00"));

            jdbcTemplate.update("""
    INSERT INTO weather_log (road_id, timestamp, base_date, base_time, temp, humidity, snow, wind_speed, rain_type)
    VALUES (?, now(), ?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT (road_id, base_date, base_time)
    DO UPDATE
    SET temp = EXCLUDED.temp,
        humidity = EXCLUDED.humidity,
        snow = EXCLUDED.snow,
        wind_speed = EXCLUDED.wind_speed,
        rain_type = EXCLUDED.rain_type
""",
                    road.getId(),
                    baseDate,
                    baseTime,
                    data.get("temp"),
                    data.get("humidity"),
                    data.get("snow"),
                    data.get("windSpeed"),
                    data.get("rainType")
            );

            log.info("🌤️ 도로 {} 저장 완료 ({} {})", road.getId(), baseDate, baseTime);
        } catch (Exception e) {
            log.error("💥 도로 {} DB 저장 실패: {}", road.getId(), e.getMessage());
        }
    }


    /** 🔹 위경도 → 기상청 격자 변환 */
    private int convertToGridX(double lat, double lon) {
        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0, SLAT2 = 60.0, OLON = 126.0, OLAT = 38.0;
        double XO = 43, YO = 136;
        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) /
                Math.log(Math.tan(Math.PI * 0.25 + slat2 * 0.5) /
                        Math.tan(Math.PI * 0.25 + slat1 * 0.5));
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        return (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
    }

    private int convertToGridY(double lat, double lon) {
        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0, SLAT2 = 60.0, OLON = 126.0, OLAT = 38.0;
        double XO = 43, YO = 136;
        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) /
                Math.log(Math.tan(Math.PI * 0.25 + slat2 * 0.5) /
                        Math.tan(Math.PI * 0.25 + slat1 * 0.5));
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        return (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
    }
}
