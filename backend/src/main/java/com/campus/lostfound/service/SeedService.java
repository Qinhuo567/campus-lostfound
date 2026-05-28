package com.campus.lostfound.service;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.entity.Claim;
import com.campus.lostfound.entity.Comment;
import com.campus.lostfound.entity.Post;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.*;
import com.campus.lostfound.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SeedService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ClaimRepository claimRepository;
    private final FavoriteRepository favoriteRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final MatchService matchService;

    public SeedService(UserRepository userRepository, PostRepository postRepository,
                       CommentRepository commentRepository, ClaimRepository claimRepository,
                       FavoriteRepository favoriteRepository,
                       MatchRecordRepository matchRecordRepository,
                       MatchService matchService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.claimRepository = claimRepository;
        this.favoriteRepository = favoriteRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.matchService = matchService;
    }

    @Transactional
    public Map<String, Object> seed(boolean force) {
        if (postRepository.count() > 0 && !force) {
            Map<String, Object> result = new HashMap<>();
            result.put("skipped", true);
            result.put("message", "已有数据，如需重新生成请传 force=true");
            return result;
        }

        if (force) {
            matchRecordRepository.deleteAll();
            favoriteRepository.deleteAll();
            commentRepository.deleteAll();
            claimRepository.deleteAll();
            postRepository.deleteAll();
        }

        User demo = ensureUser("demo", "校园用户", "20240001", "demo123");
        User xiaoming = ensureUser("xiaoming", "小明同学", "20240002", "demo123");
        User lili = ensureUser("lili", "丽丽同学", "20240003", "demo123");

        LocalDateTime now = LocalDateTime.now();

        Post lost1 = createPost(demo.getId(), Constants.POST_TYPE_LOST,
                "丢失白色 AirPods 耳机", "电子设备", "图书馆三楼 A 区",
                now.minusDays(2), "充电盒背面有小刮痕，左耳有星星贴纸，于周日下午丢失",
                "wx_demo", Constants.MOD_APPROVED,
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=400&fit=crop");

        Post found1 = createPost(lili.getId(), Constants.POST_TYPE_FOUND,
                "图书馆拾到白色 AirPods", "电子设备", "图书馆三楼阅览区",
                now.minusDays(2).plusHours(3), "白色耳机盒，左耳贴有星星贴纸，请失主联系认领",
                "wx_lili", Constants.MOD_APPROVED,
                "https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=600&h=400&fit=crop");

        Post lost2 = createPost(xiaoming.getId(), Constants.POST_TYPE_LOST,
                "丢失校园一卡通", "证件卡片", "东操场看台",
                now.minusDays(1), "透明磨砂卡套，背面夹着小纸条，姓名：小明",
                "qq_xiaoming", Constants.MOD_APPROVED,
                "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=600&h=400&fit=crop");

        Post found2 = createPost(demo.getId(), Constants.POST_TYPE_FOUND,
                "看台捡到校园卡", "证件卡片", "东操场看台第二排",
                now.minusDays(1).plusHours(2), "透明卡套，卡面姓名已遮挡，请失主描述卡套特征",
                "wx_demo", Constants.MOD_APPROVED,
                "https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=600&h=400&fit=crop");

        Post found3 = createPost(lili.getId(), Constants.POST_TYPE_FOUND,
                "食堂捡到黑色雨伞", "其他", "第一食堂门口",
                now.minusHours(5), "长柄黑伞，伞柄有红色挂绳，伞面有小熊图案",
                "phone_lili", Constants.MOD_APPROVED,
                "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?w=600&h=400&fit=crop");

        createPost(demo.getId(), Constants.POST_TYPE_LOST,
                "丢失宿舍钥匙一串", "钥匙", "6号宿舍楼门口",
                now.minusHours(8), "三把钥匙，含一个小熊钥匙扣",
                "wx_demo", Constants.MOD_PENDING, null);

        createPost(xiaoming.getId(), Constants.POST_TYPE_FOUND,
                "教学楼捡到 U 盘", "电子设备", "教三楼 302 教室",
                now.minusHours(3), "黑色金士顿 U 盘，32G，贴有姓名贴",
                "qq_xiaoming", Constants.MOD_PENDING, null);

        createPost(lili.getId(), Constants.POST_TYPE_LOST,
                "丢失高等数学教材", "书籍文具", "教五楼自习区",
                now.minusDays(3), "封面有名字标签，内页有笔记",
                "wx_lili", Constants.MOD_APPROVED,
                "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=600&h=400&fit=crop");

        addComment(lost1.getId(), xiaoming.getId(), "我也在那附近丢过，帮你留意！");
        addComment(found1.getId(), demo.getId(), "好像是我的，左耳有星星贴纸");
        addComment(found2.getId(), xiaoming.getId(), "卡套是透明的，背面有小纸条");

        Claim pendingClaim = new Claim();
        pendingClaim.setPostId(found1.getId());
        pendingClaim.setUserId(demo.getId());
        pendingClaim.setProof("充电盒背面有小刮痕，左耳耳机贴有星星贴纸，购买于2024年");
        pendingClaim.setStatus(Constants.CLAIM_PENDING);
        claimRepository.save(pendingClaim);
        found1.setPostStatus(Constants.POST_STATUS_PENDING_CLAIM);
        found1.setPendingClaimId(pendingClaim.getId());
        postRepository.save(found1);

        int matched = matchService.runFullMatch();

        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);
        result.put("posts", postRepository.count());
        result.put("matched", matched);
        return result;
    }

    private User ensureUser(String username, String nickname, String studentNo, String password) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setNickname(nickname);
            u.setStudentNo(studentNo);
            u.setPasswordHash(PasswordUtil.hash(password));
            u.setRole(Constants.ROLE_USER);
            u.setStatus(Constants.USER_ACTIVE);
            return userRepository.save(u);
        });
    }

    private Post createPost(Long userId, String type, String title, String category,
                            String location, LocalDateTime eventTime, String description,
                            String contact, String modStatus, String imageUrl) {
        Post post = new Post();
        post.setUserId(userId);
        post.setType(type);
        post.setTitle(title);
        post.setCategory(category);
        post.setLocation(location);
        post.setEventTime(eventTime);
        post.setDescription(description);
        post.setContact(contact);
        post.setModStatus(modStatus);
        post.setPostStatus(Constants.POST_STATUS_OPEN);
        post.setImageUrl(imageUrl);
        return postRepository.save(post);
    }

    private void addComment(Long postId, Long userId, String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentRepository.save(comment);
    }
}
