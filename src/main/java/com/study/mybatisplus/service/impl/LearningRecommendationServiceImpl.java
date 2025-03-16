package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.domain.UserLearningRecord;
import com.study.mybatisplus.dto.LearningProgressSummary;
import com.study.mybatisplus.mapper.SignMapper;
import com.study.mybatisplus.mapper.UserLearningRecordMapper;
import com.study.mybatisplus.service.LearningRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LearningRecommendationServiceImpl implements LearningRecommendationService {

    @Autowired
    private UserLearningRecordMapper learningRecordMapper;

    @Autowired
    private SignMapper signMapper;

    @Override
    public List<Sign> getRecommendedSigns(Integer userId, Integer limit) {
        // 获取用户已学习的手语
        List<UserLearningRecord> learningRecords = learningRecordMapper.selectByUserId(userId);

        // 从学习记录中提取手语ID和熟练度
        Map<Integer, Integer> signProficiencyMap = new HashMap<>();
        for (UserLearningRecord record : learningRecords) {
            signProficiencyMap.put(record.getSignId(), record.getProficiencyScore());
        }

        // 计算用户的平均熟练度
        int averageProficiency = 0;
        if (!signProficiencyMap.isEmpty()) {
            averageProficiency = signProficiencyMap.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum() / signProficiencyMap.size();
        }

        // 根据用户熟练度决定推荐策略
        List<Sign> recommendedSigns = new ArrayList<>();

        if (learningRecords.isEmpty() || averageProficiency < 30) {
            // 新用户或初学者：推荐基础内容
            recommendedSigns = signMapper.selectBasicSigns(limit);
        } else if (averageProficiency >= 30 && averageProficiency < 70) {
            // 中级学习者：推荐下一难度级别的内容
            recommendedSigns = signMapper.selectIntermediateSigns(limit);
        } else {
            // 熟练学习者：推荐高级内容
            recommendedSigns = signMapper.selectAdvancedSigns(limit);
        }

        // 排除已高度熟练的内容（得分大于90）
        recommendedSigns = recommendedSigns.stream()
                .filter(sign -> !signProficiencyMap.containsKey(sign.getId()) ||
                        signProficiencyMap.get(sign.getId()) < 90)
                .limit(limit)
                .collect(Collectors.toList());

        return recommendedSigns;
    }

    @Override
    public void updateLearningRecord(Integer userId, Integer signId, Boolean isCorrect) {
        LambdaQueryWrapper<UserLearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLearningRecord::getUserId, userId)
                .eq(UserLearningRecord::getSignId, signId);

        UserLearningRecord record = learningRecordMapper.selectOne(wrapper);

        if (record == null) {
            // 创建新记录
            record = new UserLearningRecord();
            record.setUserId(userId);
            record.setSignId(signId);
            record.setProficiencyScore(isCorrect != null && isCorrect ? 30 : 10);
            record.setViewCount(1);
            record.setQuizAccuracy(isCorrect != null ? (isCorrect ? 1.0 : 0.0) : null);
            record.setLastViewTime(LocalDateTime.now());
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());

            learningRecordMapper.insert(record);
        } else {
            // 更新现有记录
            record.setViewCount(record.getViewCount() + 1);
            record.setLastViewTime(LocalDateTime.now());

            // 更新熟练度分数
            int proficiencyDelta = 0;
            if (isCorrect != null) {
                // 如果提供了测验结果，根据结果调整分数
                proficiencyDelta = isCorrect ? 5 : -2;

                // 更新测验准确率
                double currentAccuracy = record.getQuizAccuracy() != null ? record.getQuizAccuracy() : 0;
                int quizCount = record.getViewCount(); // 假设每次查看都有一次测验
                double newAccuracy = (currentAccuracy * (quizCount - 1) + (isCorrect ? 1 : 0)) / quizCount;
                record.setQuizAccuracy(newAccuracy);
            } else {
                // 仅查看，小幅增加熟练度
                proficiencyDelta = 1;
            }

            // 确保熟练度在0-100范围内
            int newProficiency = Math.min(100, Math.max(0, record.getProficiencyScore() + proficiencyDelta));
            record.setProficiencyScore(newProficiency);

            record.setUpdateTime(LocalDateTime.now());

            learningRecordMapper.updateById(record);
        }
    }

    @Override
    public LearningProgressSummary getUserProgressSummary(Integer userId) {
        // 获取用户的所有学习记录
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        // 计算总学习手语数量
        int totalSigns = records.size();

        // 计算熟练掌握的手语数量（熟练度大于80）
        int masteredSigns = (int) records.stream()
                .filter(r -> r.getProficiencyScore() >= 80)
                .count();

        // 计算平均熟练度
        double averageProficiency = records.stream()
                .mapToInt(UserLearningRecord::getProficiencyScore)
                .average()
                .orElse(0);

        // 计算总学习时间（假设每次学习5分钟）
        int totalLearningTime = records.stream()
                .mapToInt(UserLearningRecord::getViewCount)
                .sum() * 5;

        // 创建并返回进度摘要
        LearningProgressSummary summary = new LearningProgressSummary();
        summary.setTotalSigns(totalSigns);
        summary.setMasteredSigns(masteredSigns);
        summary.setAverageProficiency(averageProficiency);
        summary.setTotalLearningTimeMinutes(totalLearningTime);

        // 设置推荐的下一步学习内容
        List<Sign> recommendedNext = getRecommendedSigns(userId, 3);
        summary.setRecommendedNextSigns(recommendedNext);

        return summary;
    }
}