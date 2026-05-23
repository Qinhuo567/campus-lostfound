package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.service.SeedService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SeedController {

    private final SeedService seedService;

    public SeedController(SeedService seedService) {
        this.seedService = seedService;
    }

    @PostMapping("/seed")
    public Result<Map<String, Object>> seed(@RequestParam(defaultValue = "false") boolean force) {
        return Result.ok(seedService.seed(force));
    }
}
