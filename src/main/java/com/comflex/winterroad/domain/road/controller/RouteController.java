package com.comflex.winterroad.domain.road.controller;

import com.comflex.winterroad.domain.road.dto.RoadInfoResponseDto;
import com.comflex.winterroad.domain.road.dto.RouteRequestDto;
import com.comflex.winterroad.domain.road.entity.RoadInfo;
import com.comflex.winterroad.domain.road.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<List<RoadInfoResponseDto>> getRoute(@RequestBody RouteRequestDto request) {
        List<RoadInfoResponseDto> roads = routeService.findRoadsAlongRoute(request);
        return ResponseEntity.ok(roads);
    }
}
