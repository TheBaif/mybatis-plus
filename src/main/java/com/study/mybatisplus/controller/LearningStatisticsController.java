package com.study.mybatisplus.controller;

import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.domain.User;
import com.study.mybatisplus.dto.LearningStatisticsDTO;
import com.study.mybatisplus.mapper.UserMapper;
import com.study.mybatisplus.service.ChildsignService;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/learning/statistics")
public class LearningStatisticsController {

    @Autowired
    private ChildsignService.LearningStatisticsService learningStatisticsService;

    @Autowired
    private UserMapper userMapper;

    private Integer getUserIdFromToken() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");

        if (username == null) {
            return null;
        }

        // 通过用户名查询用户ID
        User user = userMapper.findByUserName(username);
        return user != null ? user.getId() : null;
    }

    /**
     * 获取详细的学习统计数据
     * @return 学习统计数据
     */
    @GetMapping("/detailed")
    public Result<LearningStatisticsDTO> getDetailedStatistics() {
        Integer userId = getUserIdFromToken();
        if (userId == null) {
            return Result.error("无法获取用户ID");
        }

        LearningStatisticsDTO statistics = learningStatisticsService.getUserLearningStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 获取掌握等级分布数据
     * @return 掌握等级分布数据
     */
    @GetMapping("/mastery-distribution")
    public Result<Map<String, Integer>> getMasteryDistribution() {
        Integer userId = getUserIdFromToken();
        if (userId == null) {
            return Result.error("无法获取用户ID");
        }

        Map<String, Integer> distribution = learningStatisticsService.getMasteryDistribution(userId);
        return Result.success(distribution);
    }
}