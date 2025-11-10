package com.comflex.winterroad.domain.road.dto;

import lombok.Data;

@Data
public class RouteRequestDto {
    private double startLat;
    private double startLon;
    private String endAddress;
}
