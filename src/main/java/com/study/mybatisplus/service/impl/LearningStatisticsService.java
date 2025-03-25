package com.study.mybatisplus.service;

import com.study.mybatisplus.dto.LearningStatisticsDTO;
import java.util.Map;

public interface LearningStatisticsService {

    /**
     * 获取用户的详细学习统计数据
     * @param userId 用户ID
     * @return 用户的详细学习统计数据
     */
    LearningStatisticsDTO getUserLearningStatistics(Integer userId);

    /**
     * 获取用户的掌握等级分布数据
     * @param userId 用户ID
     * @return 掌握等级分布数据，key为掌握等级(未学习/未复习/已掌握)，value为数量
     */
    Map<String, Integer> getMasteryDistribution(Integer userId);
}