package com.comflex.winterroad.domain.risk.entity;

import com.comflex.winterroad.domain.road.entity.RoadInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiskLog {
//sql뷰에서 처리
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //조회 시 도로 이름을 함께 자주 갖고오기 떄문에 즉시로딩
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "road_id")
    private RoadInfo road;


    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "risk_level")
    private String riskLevel;




    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
