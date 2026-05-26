package com.campus.lostfound.service;

import com.campus.lostfound.common.BusinessException;
import com.campus.lostfound.common.Constants;
import com.campus.lostfound.config.JwtUtil;
import com.campus.lostfound.config.UserContext;
import com.campus.lostfound.dto.LoginRequest;
import com.campus.lostfound.dto.RegisterRequest;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.util.PasswordUtil;
import com.campus.lostfound.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (req.getStudentNo() != null && !req.getStudentNo().isBlank()
                && userRepository.existsByStudentNo(req.getStudentNo())) {
            throw new BusinessException("学号已被注册");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setStudentNo(req.getStudentNo());
        user.setPasswordHash(PasswordUtil.hash(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setPhone(req.getPhone());
        user.setRole(Constants.ROLE_USER);
        user.setStatus(Constants.USER_ACTIVE);
        userRepository.save(user);

        return buildLoginResponse(user);
    }

    public Map<String, Object> login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (!PasswordUtil.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (Constants.USER_DISABLED.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        return buildLoginResponse(user);
    }

    public UserVO me() {
        Long userId = UserContext.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(401, "用户不存在"));
        return UserVO.from(user);
    }

    public User getCurrentUser() {
        return userRepository.findById(UserContext.getUserId())
                .orElseThrow(() -> new BusinessException(401, "用户不存在"));
    }

    public void requireAdmin() {
        if (!Constants.ROLE_ADMIN.equals(UserContext.getRole())) {
            throw new BusinessException(403, "需要管理员权限");
        }
    }

    private Map<String, Object> buildLoginResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", UserVO.from(user));
        return result;
    }
}
