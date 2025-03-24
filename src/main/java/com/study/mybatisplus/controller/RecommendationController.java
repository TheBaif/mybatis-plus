package com.study.mybatisplus.controller;

import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.dto.LearningProgressSummary;
import com.study.mybatisplus.service.LearningRecommendationService;
import com.study.mybatisplus.utils.JwtUtil;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/learning")
public class RecommendationController {

    @Autowired
    private LearningRecommendationService recommendationService;

    @GetMapping("/recommendations")
    public Result<List<Sign>> getRecommendations(@RequestParam(defaultValue = "5") Integer limit) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        // 这里需要从用户名获取用户ID的实现
        Integer userId = getUserIdFromUsername(username);

        List<Sign> recommendations = recommendationService.getRecommendedSigns(userId, limit);
        return Result.success(recommendations);
    }

    @PostMapping("/record")
    public Result recordLearning(@RequestParam Integer signId,
                                 @RequestParam(required = false) Boolean isCorrect) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        // 从用户名获取用户ID
        Integer userId = getUserIdFromUsername(username);

        recommendationService.updateLearningRecord(userId, signId, isCorrect);
        return Result.success();
    }


    @GetMapping("/progress")
    public Result<LearningProgressSummary> getUserProgress() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        // 从用户名获取用户ID
        Integer userId = getUserIdFromUsername(username);

        LearningProgressSummary summary = recommendationService.getUserProgressSummary(userId);
        return Result.success(summary);
    }

    // 辅助方法：从用户名获取用户ID
    private Integer getUserIdFromUsername(String username) {
        // 这里需要实现从用户名查询用户ID的逻辑
        // 可能需要注入UserMapper或者UserService
        return 1; // 示例返回值
    }
}