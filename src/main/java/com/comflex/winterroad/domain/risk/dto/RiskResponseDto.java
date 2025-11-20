package com.comflex.winterroad.domain.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskResponseDto {//범용 Dto
    private Integer roadId;
    private String roadName;
    private String regionCode;
    private Double riskScore;
    private String riskLevel;
    private LocalDateTime updatedAt;

}
