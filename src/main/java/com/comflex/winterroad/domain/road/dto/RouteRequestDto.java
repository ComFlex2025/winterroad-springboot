package com.comflex.winterroad.domain.road.dto;

import lombok.Data;

@Data
public class RouteRequestDto {
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String endAddress;
}
