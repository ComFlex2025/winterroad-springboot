package com.comflex.winterroad.controller;

import com.comflex.winterroad.domain.RoadInfo;
import com.comflex.winterroad.repository.RoadInfoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roads")
public class RoadInfoController {

    private final RoadInfoRepository roadInfoRepository;

    public RoadInfoController(RoadInfoRepository roadInfoRepository) {
        this.roadInfoRepository = roadInfoRepository;
    }

    @GetMapping
    public List<RoadInfo> getAll() {
        return roadInfoRepository.findAll();
    }

    @PostMapping
    public RoadInfo create(@RequestBody RoadInfo roadInfo) {
        return roadInfoRepository.save(roadInfo);
    }
}
