package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.domain.UserLearningRecord;
import com.study.mybatisplus.dto.LearningProgressSummary;
import com.study.mybatisplus.mapper.SignMapper;
import com.study.mybatisplus.mapper.UserLearningRecordMapper;
import com.study.mybatisplus.service.LearningRecommendationService;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Get user's learned signs
        List<UserLearningRecord> learningRecords = learningRecordMapper.selectByUserId(userId);

        // Extract sign IDs and proficiency
        Map<Integer, Integer> signProficiencyMap = new HashMap<>();
        for (UserLearningRecord record : learningRecords) {
            signProficiencyMap.put(record.getSignId(), record.getProficiencyScore());
        }

        // Calculate average proficiency
        int averageProficiency = 0;
        if (!signProficiencyMap.isEmpty()) {
            averageProficiency = signProficiencyMap.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum() / signProficiencyMap.size();
        }

        // Recommend signs based on proficiency
        List<Sign> recommendedSigns = new ArrayList<>();

        if (learningRecords.isEmpty() || averageProficiency < 30) {
            // New user or beginner: recommend basic content
            recommendedSigns = signMapper.selectBasicSigns(limit);
        } else if (averageProficiency >= 30 && averageProficiency < 70) {
            // Intermediate learner: recommend next level content
            recommendedSigns = signMapper.selectIntermediateSigns(limit);
        } else {
            // Advanced learner: recommend advanced content
            recommendedSigns = signMapper.selectAdvancedSigns(limit);
        }

        // Exclude already mastered signs (proficiency > 90)
        recommendedSigns = recommendedSigns.stream()
                .filter(sign -> !signProficiencyMap.containsKey(sign.getId()) ||
                        signProficiencyMap.get(sign.getId()) < 90)
                .limit(limit)
                .collect(Collectors.toList());

        return recommendedSigns;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLearningRecord(Integer userId, Integer signId, Boolean isCorrect) {
        if (userId == null || signId == null) {
            throw new IllegalArgumentException("用户ID和手语ID不能为空");
        }

        try {
            LambdaQueryWrapper<UserLearningRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserLearningRecord::getUserId, userId)
                    .eq(UserLearningRecord::getSignId, signId);

            UserLearningRecord record = learningRecordMapper.selectOne(wrapper);

            if (record == null) {
                // 创建新记录
                record = new UserLearningRecord();
                record.setUserId(userId);
                record.setSignId(signId);
                // 设置其他字段...
                learningRecordMapper.insert(record);
            } else {
                // 更新已有记录
                // 更新字段...
                learningRecordMapper.updateById(record);
            }
        } catch (DuplicateKeyException e) {
            // 处理并发情况下的唯一键冲突
            // 可以尝试再次查询并更新
            LambdaQueryWrapper<UserLearningRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserLearningRecord::getUserId, userId)
                    .eq(UserLearningRecord::getSignId, signId);

            UserLearningRecord record = learningRecordMapper.selectOne(wrapper);
            if (record != null) {
                // 更新已有记录
                // 更新字段...
                learningRecordMapper.updateById(record);
            }
        }
    }

    @Override
    public LearningProgressSummary getUserProgressSummary(Integer userId) {
        // Get user's learning records
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        // Calculate total signs learned
        int totalSigns = records.size();

        // Calculate mastered signs (proficiency > 80)
        int masteredSigns = (int) records.stream()
                .filter(r -> r.getProficiencyScore() >= 80)
                .count();

        // Calculate average proficiency
        double averageProficiency = records.stream()
                .mapToInt(UserLearningRecord::getProficiencyScore)
                .average()
                .orElse(0);

        // Calculate total learning time (assume 5 minutes per view)
        int totalLearningTime = records.stream()
                .mapToInt(UserLearningRecord::getViewCount)
                .sum() * 5;

        // Create and return summary
        LearningProgressSummary summary = new LearningProgressSummary();
        summary.setTotalSigns(totalSigns);
        summary.setMasteredSigns(masteredSigns);
        summary.setAverageProficiency(averageProficiency);
        summary.setTotalLearningTimeMinutes(totalLearningTime);

        // Set recommended next signs
        List<Sign> recommendedNext = getRecommendedSigns(userId, 3);
        summary.setRecommendedNextSigns(recommendedNext);

        return summary;
    }
}