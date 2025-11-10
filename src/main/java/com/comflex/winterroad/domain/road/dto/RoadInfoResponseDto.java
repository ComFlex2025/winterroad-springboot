package com.comflex.winterroad.domain.road.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoadInfoResponseDto {
    private Integer id;
    private String roadName;
    private String description;
    private String regionCode;
    private double latitude;
    private double longitude;
    private Double  riskScore;
}