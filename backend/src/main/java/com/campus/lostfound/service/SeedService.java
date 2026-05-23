package com.campus.lostfound.service;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.entity.Post;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.PostRepository;
import com.campus.lostfound.repository.UserRepository;
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
    private final MatchService matchService;

    public SeedService(UserRepository userRepository, PostRepository postRepository,
                       MatchService matchService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
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

        User demo = userRepository.findByUsername("demo")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("demo");
                    u.setNickname("演示用户");
                    u.setStudentNo("20240001");
                    u.setPasswordHash(PasswordUtil.hash("demo123"));
                    u.setRole(Constants.ROLE_USER);
                    u.setStatus(Constants.USER_ACTIVE);
                    return userRepository.save(u);
                });

        LocalDateTime now = LocalDateTime.now();

        Post lost1 = createPost(demo.getId(), Constants.POST_TYPE_LOST,
                "丢失白色 AirPods 耳机", "电子设备", "图书馆三楼 A 区",
                now.minusDays(2), "充电盒背面有小刮痕，左耳有星星贴纸",
                "wx_demo", Constants.MOD_APPROVED);

        Post found1 = createPost(demo.getId(), Constants.POST_TYPE_FOUND,
                "图书馆拾到白色 AirPods", "电子设备", "图书馆三楼阅览区",
                now.minusDays(2).plusHours(3), "白色耳机盒，左耳贴有星星贴纸",
                "wx_demo", Constants.MOD_APPROVED);

        Post lost2 = createPost(demo.getId(), Constants.POST_TYPE_LOST,
                "丢失校园一卡通", "证件卡片", "东操场看台",
                now.minusDays(1), "透明磨砂卡套，背面有小纸条",
                "qq_demo", Constants.MOD_APPROVED);

        Post found2 = createPost(demo.getId(), Constants.POST_TYPE_FOUND,
                "看台捡到校园卡", "证件卡片", "东操场看台第二排",
                now.minusDays(1).plusHours(2), "透明卡套，卡面姓名已遮挡",
                "qq_demo", Constants.MOD_APPROVED);

        createPost(demo.getId(), Constants.POST_TYPE_FOUND,
                "食堂捡到黑色雨伞", "其他", "第一食堂门口",
                now.minusHours(5), "长柄黑伞，伞柄有红色挂绳",
                "phone_demo", Constants.MOD_PENDING);

        int matched = matchService.runFullMatch();

        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);
        result.put("posts", 5);
        result.put("matched", matched);
        return result;
    }

    private Post createPost(Long userId, String type, String title, String category,
                            String location, LocalDateTime eventTime, String description,
                            String contact, String modStatus) {
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
        return postRepository.save(post);
    }
}
