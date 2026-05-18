package com.campus.lostfound.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (isPublic(request)) {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                try {
                    String token = auth.substring(7);
                    UserContext.set(jwtUtil.getUserId(token), jwtUtil.getRole(token));
                } catch (Exception ignored) {
                }
            }
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new com.campus.lostfound.common.BusinessException(401, "请先登录");
        }

        try {
            String token = auth.substring(7);
            UserContext.set(jwtUtil.getUserId(token), jwtUtil.getRole(token));
            return true;
        } catch (Exception e) {
            throw new com.campus.lostfound.common.BusinessException(401, "登录已过期，请重新登录");
        }
    }

    private boolean isPublic(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        if ("/api/posts".equals(path)) {
            return true;
        }
        return path.matches("/api/posts/\\d+")
                || path.matches("/api/posts/\\d+/comments")
                || path.matches("/api/matches/for-post/\\d+");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
