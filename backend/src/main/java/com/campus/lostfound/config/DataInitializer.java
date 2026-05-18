package com.campus.lostfound.config;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
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
