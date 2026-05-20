package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.dto.ClaimRequest;
import com.campus.lostfound.dto.PostRequest;
import com.campus.lostfound.service.PostService;
import com.campus.lostfound.vo.ClaimVO;
import com.campus.lostfound.vo.PostVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Result<List<PostVO>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String postStatus) {
        return Result.ok(postService.listPosts(type, category, keyword, postStatus));
    }

    @GetMapping("/{id}")
    public Result<PostVO> detail(@PathVariable Long id) {
        return Result.ok(postService.getPost(id));
    }

    @PostMapping
    public Result<PostVO> create(@Valid @RequestBody PostRequest req) {
        return Result.ok(postService.createPost(req));
    }

    @GetMapping("/my")
    public Result<List<PostVO>> myPosts() {
        return Result.ok(postService.myPosts());
    }

    @PutMapping("/{id}")
    public Result<PostVO> update(@PathVariable Long id, @Valid @RequestBody PostRequest req) {
        return Result.ok(postService.updatePost(id, req));
    }

    @PostMapping("/{id}/claims")
    public Result<ClaimVO> claim(@PathVariable Long id, @Valid @RequestBody ClaimRequest req) {
        return Result.ok(postService.createClaim(id, req));
    }

    @PostMapping("/{postId}/claims/{claimId}/approve")
    public Result<PostVO> approveClaim(@PathVariable Long postId, @PathVariable Long claimId) {
        return Result.ok(postService.approveClaim(postId, claimId));
    }

    @PostMapping("/{postId}/claims/{claimId}/reject")
    public Result<PostVO> rejectClaim(@PathVariable Long postId, @PathVariable Long claimId) {
        return Result.ok(postService.rejectClaim(postId, claimId));
    }

    @GetMapping("/my/claims")
    public Result<List<PostVO>> myClaims() {
        return Result.ok(postService.myClaims());
    }

    @GetMapping("/{id}/claims")
    public Result<List<ClaimVO>> postClaims(@PathVariable Long id) {
        return Result.ok(postService.getPostClaims(id));
    }
}
