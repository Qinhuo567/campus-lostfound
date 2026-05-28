package com.campus.lostfound.config;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.PostRepository;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.service.SeedService;
import com.campus.lostfound.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SeedService seedService;

    public DataInitializer(UserRepository userRepository, PostRepository postRepository,
                           SeedService seedService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.seedService = seedService;
    }

    @Override
    public void run(String... args) {
        ensureAdmin();
        if (postRepository.count() == 0) {
            log.info("数据库为空，正在初始化示例数据...");
            var result = seedService.seed(false);
            log.info("示例数据初始化完成：帖子 {} 条，匹配 {} 条", result.get("posts"), result.get("matched"));
        }
    }

    private void ensureAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setNickname("系统管理员");
            admin.setPasswordHash(PasswordUtil.hash("admin123"));
            admin.setRole(Constants.ROLE_ADMIN);
            admin.setStatus(Constants.USER_ACTIVE);
            userRepository.save(admin);
        }
    }
}
