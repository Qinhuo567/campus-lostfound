package com.campus.lostfound.service;

import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.entity.Post;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.MatchRecordRepository;
import com.campus.lostfound.repository.PostRepository;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.vo.PostVO;
import com.campus.lostfound.vo.UserVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final @Lazy PostService postService;

    public AdminService(UserRepository userRepository, PostRepository postRepository,
                        MatchRecordRepository matchRecordRepository,
                        @Lazy PostService postService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.postService = postService;
    }

    public Map<String, Object> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepository.count());
        stats.put("postCount", postRepository.count());
        stats.put("pendingCount", postRepository.countByModStatus(Constants.MOD_PENDING));
        stats.put("matchCount", matchRecordRepository.countByStatus(Constants.MATCH_ACCEPTED));
        return stats;
    }

    public List<PostVO> pendingPosts() {
        return postRepository.findByModStatusOrderByCreatedAtAsc(Constants.MOD_PENDING)
                .stream()
                .map(post -> postService.toVO(post))
                .collect(Collectors.toList());
    }

    public PostVO approvePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        post.setModStatus(Constants.MOD_APPROVED);
        postRepository.save(post);
        return postService.toVO(post);
    }

    public PostVO rejectPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BusinessException("帖子不存在"));
        post.setModStatus(Constants.MOD_REJECTED);
        postRepository.save(post);
        return postService.toVO(post);
    }

    public List<PostVO> allPosts(String type, String modStatus, String keyword) {
        Specification<Post> spec = Specification.where(null);

        if (type != null && !type.isBlank() && !"all".equals(type)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        if (modStatus != null && !modStatus.isBlank() && !"all".equals(modStatus)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("modStatus"), modStatus));
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(root.get("title"), like),
                    cb.like(root.get("location"), like)
            ));
        }

        return postRepository.findAll(spec, PageRequest.of(0, 200)).getContent()
                .stream()
                .map(post -> postService.toVO(post))
                .collect(Collectors.toList());
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new BusinessException("帖子不存在");
        }
        postRepository.deleteById(id);
    }

    public Page<UserVO> listUsers(String keyword, int page, int size) {
        Specification<User> spec = Specification.where(null);
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(root.get("username"), like),
                    cb.like(root.get("nickname"), like),
                    cb.like(root.get("studentNo"), like)
            ));
        }
        return userRepository.findAll(spec, PageRequest.of(page, size))
                .map(UserVO::from);
    }

    public UserVO updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        if (!Constants.USER_ACTIVE.equals(status) && !Constants.USER_DISABLED.equals(status)) {
            throw new BusinessException("无效的状态");
        }
        user.setStatus(status);
        userRepository.save(user);
        return UserVO.from(user);
    }
}
