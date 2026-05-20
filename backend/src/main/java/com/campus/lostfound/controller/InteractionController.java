package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.dto.CommentRequest;
import com.campus.lostfound.service.InteractionService;
import com.campus.lostfound.vo.CommentVO;
import com.campus.lostfound.vo.PostVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/posts/{id}/comments")
    public Result<CommentVO> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequest req) {
        return Result.ok(interactionService.addComment(id, req));
    }

    @GetMapping("/posts/{id}/comments")
    public Result<List<CommentVO>> listComments(@PathVariable Long id) {
        return Result.ok(interactionService.listComments(id));
    }

    @PostMapping("/posts/{id}/favorite")
    public Result<Map<String, Object>> toggleFavorite(@PathVariable Long id) {
        return Result.ok(interactionService.toggleFavorite(id));
    }

    @GetMapping("/favorites")
    public Result<List<PostVO>> myFavorites() {
        return Result.ok(interactionService.myFavorites());
    }
}
