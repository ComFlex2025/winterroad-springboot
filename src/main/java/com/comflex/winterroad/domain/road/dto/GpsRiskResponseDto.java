package com.comflex.winterroad.domain.road.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GpsRiskResponseDto {
    private Integer roadId;
    private String roadName;
    private double latitude;
    private double longitude;
    private Double riskScore;
    private String riskLevel;
}
