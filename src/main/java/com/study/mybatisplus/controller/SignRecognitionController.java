package com.study.mybatisplus.controller;

import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.service.LearningRecommendationService;
import com.study.mybatisplus.service.SignService;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/sign-recognition")
public class SignRecognitionController {

    @Autowired
    private SignService signService;

    @Autowired
    private LearningRecommendationService recommendationService;

    /**
     * 获取用于练习的手语列表
     * @param count 要获取的手语数量
     * @return 手语列表
     */
    @GetMapping("/practice-list")
    public Result<List<Sign>> getPracticeList(@RequestParam(defaultValue = "5") Integer count) {
        // 从用户的学习记录中获取推荐的练习内容
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        // 这里需要通过用户名获取用户ID的方法
        Integer userId = getUserIdFromUsername(username);

        List<Sign> recommendedSigns = recommendationService.getRecommendedSigns(userId, count);
        return Result.success(recommendedSigns);
    }

    /**
     * 验证用户的手语动作
     * @param signId 手语ID
     * @param videoFile 用户录制的视频
     * @return 验证结果
     */
    @PostMapping("/verify")
    public Result<Map<String, Object>> verifySignPerformance(
            @RequestParam Integer signId,
            @RequestParam(required = false) MultipartFile videoFile) {

        // 模拟验证过程
        // 实际实现应该将视频发送给手语识别模型进行分析
        // 这里为了演示，直接返回模拟结果

        // 获取目标手语信息
        Sign targetSign = signService.getById(signId);
        if (targetSign == null) {
            return Result.error("找不到指定的手语");
        }

        // 模拟评分 (70-100之间的随机数)
        double score = 70 + Math.random() * 30;
        double accuracy = score / 100.0;

        // 记录学习结果
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");
        Integer userId = getUserIdFromUsername(username);

        // 更新学习记录
        boolean isCorrect = score >= 70; // 大于70分视为正确
        recommendationService.updateLearningRecord(userId, signId, isCorrect);

        // 组装结果
        Map<String, Object> result = new HashMap<>();
        result.put("signId", signId);
        result.put("signName", targetSign.getName());
        result.put("score", Math.round(score));
        result.put("accuracy", accuracy);
        result.put("isCorrect", isCorrect);

        // 根据分数提供反馈
        String feedback;
        if (score >= 90) {
            feedback = "太棒了！你的手语表达非常准确。";
        } else if (score >= 80) {
            feedback = "很好！你已经掌握了这个手语的基本要领。";
        } else if (score >= 70) {
            feedback = "不错！还有一些小细节可以改进。";
        } else if (score >= 60) {
            feedback = "基本掌握了，多加练习可以更流畅。";
        } else {
            feedback = "建议多看视频示范，注意手指姿势和方向。";
        }
        result.put("feedback", feedback);

        return Result.success(result);
    }

    /**
     * 从用户名获取用户ID的辅助方法
     * 实际实现需要查询数据库
     */
    private Integer getUserIdFromUsername(String username) {
        // 模拟实现，实际应该查询用户表
        return 1; // 假设ID为1
    }
}