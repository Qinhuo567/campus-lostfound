package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.service.AdminService;
import com.campus.lostfound.service.AuthService;
import com.campus.lostfound.vo.PostVO;
import com.campus.lostfound.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        authService.requireAdmin();
        return Result.ok(adminService.stats());
    }

    @GetMapping("/pending")
    public Result<List<PostVO>> pending() {
        authService.requireAdmin();
        return Result.ok(adminService.pendingPosts());
    }

    @PostMapping("/posts/{id}/approve")
    public Result<PostVO> approve(@PathVariable Long id) {
        authService.requireAdmin();
        return Result.ok(adminService.approvePost(id));
    }

    @PostMapping("/posts/{id}/reject")
    public Result<PostVO> reject(@PathVariable Long id) {
        authService.requireAdmin();
        return Result.ok(adminService.rejectPost(id));
    }

    @GetMapping("/posts")
    public Result<List<PostVO>> posts(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String modStatus,
            @RequestParam(required = false) String keyword) {
        authService.requireAdmin();
        return Result.ok(adminService.allPosts(type, modStatus, keyword));
    }

    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        authService.requireAdmin();
        adminService.deletePost(id);
        return Result.ok();
    }

    @GetMapping("/users")
    public Result<Page<UserVO>> users(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        authService.requireAdmin();
        return Result.ok(adminService.listUsers(keyword, page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<UserVO> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        authService.requireAdmin();
        return Result.ok(adminService.updateUserStatus(id, body.get("status")));
    }
}
