package com.study.mybatisplus.service;

import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.dto.LearningProgressSummary;

import java.util.List;

public interface LearningRecommendationService {
    /**
     * 获取用户的推荐学习内容
     * @param userId 用户ID
     * @param limit 返回结果数量
     * @return 推荐的手语列表
     */
    List<Sign> getRecommendedSigns(Integer userId, Integer limit);

    /**
     * 更新用户的学习记录
     * @param userId 用户ID
     * @param signId 手语ID
     * @param isCorrect 测验是否正确（可选）
     */
    void updateLearningRecord(Integer userId, Integer signId, Boolean isCorrect);

    /**
     * 获取用户的学习进度概览
     * @param userId 用户ID
     * @return 包含各项统计数据的对象
     */
    LearningProgressSummary getUserProgressSummary(Integer userId);
}