package com.comflex.winterroad.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "road_info")
@Getter
@Setter
@NoArgsConstructor
public class RoadInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "road_name")
    private String roadName;

    private Double latitude;
    private Double longitude;
    private String description;

    @Column(name = "region_code")
    private String regionCode;
}

