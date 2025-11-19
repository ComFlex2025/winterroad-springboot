package com.comflex.winterroad.domain.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RiskTopResponseDto { //JPQL 최적화 DTO
    private Integer roadId;
    private String roadName;
    private String regionCode;
    private Double riskScore;
    private String riskColor;
    private LocalDateTime updatedAt;
}
