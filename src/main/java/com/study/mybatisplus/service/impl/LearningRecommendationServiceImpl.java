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

    @Override
    public void updateLearningRecord(Integer userId, Integer signId, Boolean isCorrect) {
        // Check for required parameters
        if (userId == null || signId == null) {
            throw new RuntimeException("用户ID和手语ID不能为空");
        }

        LambdaQueryWrapper<UserLearningRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLearningRecord::getUserId, userId)
                .eq(UserLearningRecord::getSignId, signId);

        UserLearningRecord record = learningRecordMapper.selectOne(wrapper);

        if (record == null) {
            // Create new record
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
            // Update existing record
            record.setViewCount(record.getViewCount() + 1);
            record.setLastViewTime(LocalDateTime.now());

            // Update proficiency score
            int proficiencyDelta = 0;
            if (isCorrect != null) {
                // If quiz result provided, adjust score accordingly
                proficiencyDelta = isCorrect ? 5 : -2;

                // Update quiz accuracy
                double currentAccuracy = record.getQuizAccuracy() != null ? record.getQuizAccuracy() : 0;
                int quizCount = record.getViewCount(); // Assuming each view has one quiz
                double newAccuracy = (currentAccuracy * (quizCount - 1) + (isCorrect ? 1 : 0)) / quizCount;
                record.setQuizAccuracy(newAccuracy);
            } else {
                // Just viewing, small increase in proficiency
                proficiencyDelta = 1;
            }

            // Ensure proficiency stays within 0-100 range
            int newProficiency = Math.min(100, Math.max(0, record.getProficiencyScore() + proficiencyDelta));
            record.setProficiencyScore(newProficiency);

            record.setUpdateTime(LocalDateTime.now());

            learningRecordMapper.updateById(record);
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