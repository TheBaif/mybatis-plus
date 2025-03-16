package com.study.mybatisplus.interceptors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.ParentSign;
import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.utils.JwtUtil;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception{
        //令牌验证
        String token=request.getHeader("Authorization");
        try {
            // 解析 Token 验证登录状态
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String RedisToken=operations.get(token);
            if(RedisToken==null){
                throw new RuntimeException();
            }
            Map<String, Object> claims = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            response.setStatus(401); // 401
            return false;
        }
    }
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        ThreadLocalUtil.remove();
    }
}
