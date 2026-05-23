package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.service.AuthService;
import com.campus.lostfound.service.MatchService;
import com.campus.lostfound.vo.MatchVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final AuthService authService;

    public MatchController(MatchService matchService, AuthService authService) {
        this.matchService = matchService;
        this.authService = authService;
    }

    @GetMapping("/for-post/{postId}")
    public Result<List<MatchVO>> forPost(@PathVariable Long postId) {
        return Result.ok(matchService.forPost(postId));
    }

    @GetMapping("/my")
    public Result<List<MatchVO>> myMatches() {
        return Result.ok(matchService.myMatches());
    }

    @PostMapping("/{id}/accept")
    public Result<MatchVO> accept(@PathVariable Long id) {
        return Result.ok(matchService.acceptMatch(id));
    }

    @PostMapping("/{id}/dismiss")
    public Result<MatchVO> dismiss(@PathVariable Long id) {
        return Result.ok(matchService.dismissMatch(id));
    }

    @PostMapping("/run")
    public Result<Map<String, Integer>> run() {
        authService.requireAdmin();
        int count = matchService.runFullMatch();
        return Result.ok(Map.of("matched", count));
    }
}
