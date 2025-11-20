package com.comflex.winterroad.domain.risk.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class RiskTopResponseDto {

    private Integer roadId;
    private String roadName;
    private String regionCode;
    private Double riskScore;
    private String riskLevel;
    private String updatedAt;

    private Integer idx;

    public RiskTopResponseDto(Integer roadId,
                              String roadName,
                              String regionCode,
                              Double riskScore,
                              String riskLevel,
                              LocalDateTime updatedAt) {

        this.roadId = roadId;
        this.roadName = roadName;
        this.regionCode = regionCode;

        // riskScore 소수점 2자리 반올림
        this.riskScore = riskScore != null
                ? Math.round(riskScore * 100) / 100.0
                : null;

        // ⭐ riskLevel 저장
        this.riskLevel = riskLevel;

        // 날짜 포맷 변환
        this.updatedAt = updatedAt != null
                ? updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : null;
    }
    public void setIdx(Integer idx) {
        this.idx = idx;
    }


}
