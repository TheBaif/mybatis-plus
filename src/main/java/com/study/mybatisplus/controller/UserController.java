package com.study.mybatisplus.controller;

import ch.qos.logback.core.util.StringUtil;
import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.domain.User;
import com.study.mybatisplus.service.UserService;
import com.study.mybatisplus.utils.JwtUtil;
import com.study.mybatisplus.utils.Md5Util;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
            User u = userService.findByUserName(username);
            if (u == null) {
                //没有占用
                //注册
                userService.register(username, password);
                return Result.success();
            } else {
                return Result.error("用户名已被占用");
            }
        }
    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        // Log login attempt for debugging
        System.out.println("Login attempt - Username: " + username);

        // Check if user exists
        User loginUser = userService.findByUserName(username);
        if (loginUser == null) {
            System.out.println("User not found: " + username);
            return Result.error("用户名不存在");
        }

        // Check password
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            // Create claims for JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", loginUser.getUsername());
            claims.put("userId", loginUser.getId());

            // Generate token
            String token = JwtUtil.genToken(claims);
            System.out.println("Login successful, token generated: " + token);

            // Store token in Redis with expiration time (12 hours)
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token, token, 12, TimeUnit.HOURS);
            System.out.println("Token stored in Redis");

            return Result.success(token);
        }

        System.out.println("Password incorrect for user: " + username);
        return Result.error("密码错误");
    }
    @GetMapping ("/userInfo")
    public Result<User> userInfo() {
        Map<String,Object> map= ThreadLocalUtil.get();
        String username=(String) map.get("username");
        User user=userService.findByUserName(username);
        return Result.success(user);
    }

    @PutMapping ("/update")
    public Result update(@RequestBody @Validated User user) {
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
        //1、校验参数
        String oldPwd=params.get("old_pwd");
        String newPwd=params.get("new_pwd");
        String rePwd=params.get("re_pwd");

        if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }
        //原密码是否正确
        //调用userService根据用户名拿到密码，再和old_pwd比对
        Map<String,Object> map=ThreadLocalUtil.get();
        String username=(String) map.get("username");
        User loginUser=userService.findByUserName(username);
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写不正确");
        }
        //newPwd和rePwd校验
        if(!newPwd.equals(rePwd)){
            return Result.error("两次输入的密码不一样");
        }
        //2、调用service完成密码更新
        userService.updatePwd(newPwd);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

}
