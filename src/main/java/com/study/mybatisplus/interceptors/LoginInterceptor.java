package com.study.mybatisplus.interceptors;

import com.study.mybatisplus.utils.JwtUtil;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Log the request path for debugging
        System.out.println("Intercepting request: " + request.getRequestURI());

        // Get the Authorization header
        String token = request.getHeader("Authorization");
        System.out.println("Token from header: " + token);

        // Check if token is null or empty
        if (!StringUtils.hasLength(token)) {
            System.out.println("Token is missing or empty");
            response.setStatus(401);
            return false;
        }

        try {
            // Get token from Redis
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            System.out.println("Token from Redis: " + redisToken);

            if (redisToken == null) {
                System.out.println("Token not found in Redis");
                throw new RuntimeException("Token not found in Redis");
            }

            // Parse the token
            Map<String, Object> claims = JwtUtil.parseToken(token);
            System.out.println("Token parsed successfully: " + claims);

            // Store claims in ThreadLocal
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            System.out.println("Token validation error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(401);
            return false;
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Clean up ThreadLocal
        ThreadLocalUtil.remove();
    }
}