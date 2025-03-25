// Updated RecommendationController.java to handle extended parameters
package com.study.mybatisplus.controller;

import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.domain.User;
import com.study.mybatisplus.dto.LearningProgressSummary;
import com.study.mybatisplus.mapper.UserMapper;
import com.study.mybatisplus.service.LearningRecommendationService;
import com.study.mybatisplus.service.impl.LearningRecommendationServiceImpl;
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

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/recommendations")
    public Result<List<Sign>> getRecommendations(@RequestParam(defaultValue = "5") Integer limit) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        Integer userId = getUserIdFromUsername(username);
        if (userId == null) {
            return Result.error("无法获取用户ID");
        }

        List<Sign> recommendations = recommendationService.getRecommendedSigns(userId, limit);
        return Result.success(recommendations);
    }

    @PostMapping("/record")
    public Result recordLearning(
            @RequestParam Integer signId,
            @RequestParam(required = false) Boolean isCorrect,
            @RequestParam(required = false) Boolean extendedView) {

        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        Integer userId = getUserIdFromUsername(username);
        if (userId == null) {
            return Result.error("无法获取用户ID");
        }

        // If extended view parameter is provided, use the extended method
        if (extendedView != null) {
            ((LearningRecommendationServiceImpl)recommendationService)
                    .updateLearningRecordExtended(userId, signId, isCorrect, extendedView);
        } else {
            recommendationService.updateLearningRecord(userId, signId, isCorrect);
        }

        return Result.success();
    }

    @GetMapping("/progress")
    public Result<LearningProgressSummary> getUserProgress() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        Integer userId = getUserIdFromUsername(username);
        if (userId == null) {
            return Result.error("无法获取用户ID");
        }

        LearningProgressSummary summary = recommendationService.getUserProgressSummary(userId);
        return Result.success(summary);
    }

    // Helper method to get userId from username
    private Integer getUserIdFromUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        // Use the UserMapper to find the user by username
        User user = userMapper.findByUserName(username);
        return user != null ? user.getId() : null;
    }
}