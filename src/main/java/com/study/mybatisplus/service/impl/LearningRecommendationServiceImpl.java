package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.domain.UserLearningRecord;
import com.study.mybatisplus.dto.LearningProgressSummary;
import com.study.mybatisplus.dto.UserLearningStatistics;
import com.study.mybatisplus.dto.WeeklyLearningData;
import com.study.mybatisplus.mapper.SignMapper;
import com.study.mybatisplus.mapper.UserLearningRecordMapper;
import com.study.mybatisplus.service.LearningRecommendationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningRecommendationServiceImpl implements LearningRecommendationService {

    @Autowired
    private UserLearningRecordMapper learningRecordMapper;

    @Autowired
    private SignMapper signMapper;

    // 定义掌握等级的分数阈值
    private static final int NOT_LEARNED_THRESHOLD = 0;
    private static final int NOT_REVIEWED_THRESHOLD = 30;
    private static final int MASTERED_THRESHOLD = 70;

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

        // 获取不同掌握等级的手语
        List<Sign> recommendedSigns = new ArrayList<>();

        // 基于用户熟练度决定推荐策略
        if (learningRecords.isEmpty() || averageProficiency < NOT_REVIEWED_THRESHOLD) {
            // 新用户或未复习：推荐基础内容
            recommendedSigns = signMapper.selectBasicSigns(limit);
        } else if (averageProficiency >= NOT_REVIEWED_THRESHOLD && averageProficiency < MASTERED_THRESHOLD) {
            // 未复习状态：推荐中级内容和需要复习的内容
            recommendedSigns = signMapper.selectIntermediateSigns(limit);

            // 补充需要复习的内容
            List<Integer> needReviewSignIds = learningRecords.stream()
                    .filter(r -> r.getProficiencyScore() >= NOT_REVIEWED_THRESHOLD
                            && r.getProficiencyScore() < MASTERED_THRESHOLD)
                    .sorted(Comparator.comparing(UserLearningRecord::getLastViewTime))
                    .limit(limit / 2)
                    .map(UserLearningRecord::getSignId)
                    .collect(Collectors.toList());

            if (!needReviewSignIds.isEmpty()) {
                List<Sign> reviewSigns = signMapper.selectBatchIds(needReviewSignIds);
                // 确保不重复添加
                for (Sign sign : reviewSigns) {
                    if (recommendedSigns.stream().noneMatch(s -> s.getId().equals(sign.getId()))) {
                        recommendedSigns.add(sign);
                        if (recommendedSigns.size() >= limit) break;
                    }
                }
            }
        } else {
            // 已掌握状态：推荐高级内容和需要巩固的内容
            recommendedSigns = signMapper.selectAdvancedSigns(limit / 2);

            // 补充需要巩固的内容（最近学习但可能遗忘的内容）
            LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
            List<Integer> consolidateSignIds = learningRecords.stream()
                    .filter(r -> r.getProficiencyScore() >= MASTERED_THRESHOLD
                            && r.getLastViewTime().isBefore(oneWeekAgo))
                    .sorted(Comparator.comparing(UserLearningRecord::getLastViewTime))
                    .limit(limit / 2)
                    .map(UserLearningRecord::getSignId)
                    .collect(Collectors.toList());

            if (!consolidateSignIds.isEmpty()) {
                List<Sign> consolidateSigns = signMapper.selectBatchIds(consolidateSignIds);
                // 确保不重复添加
                for (Sign sign : consolidateSigns) {
                    if (recommendedSigns.stream().noneMatch(s -> s.getId().equals(sign.getId()))) {
                        recommendedSigns.add(sign);
                        if (recommendedSigns.size() >= limit) break;
                    }
                }
            }
        }

        // 排除已高度熟练的内容（得分大于90）
        recommendedSigns = recommendedSigns.stream()
                .filter(sign -> !signProficiencyMap.containsKey(sign.getId()) ||
                        signProficiencyMap.get(sign.getId()) < 90)
                .limit(limit)
                .collect(Collectors.toList());

        // 为每个推荐的手语添加掌握等级
        for (Sign sign : recommendedSigns) {
            if (signProficiencyMap.containsKey(sign.getId())) {
                int proficiency = signProficiencyMap.get(sign.getId());
                sign.setMasteryLevel(getMasteryLevel(proficiency));
                // 将掌握度分数添加到Sign对象中，方便前端使用
                sign.setProficiencyScore(proficiency);
            } else {
                sign.setMasteryLevel("未学习");
                sign.setProficiencyScore(0);
            }
        }

        return recommendedSigns;
    }

    // 根据熟练度确定掌握等级
    private String getMasteryLevel(int proficiency) {
        if (proficiency < NOT_REVIEWED_THRESHOLD) {
            return "未学习";
        } else if (proficiency < MASTERED_THRESHOLD) {
            return "未复习";
        } else {
            return "已掌握";
        }
    }

    @Override
    public void updateLearningRecord(Integer userId, Integer signId, Boolean isCorrect) {
        // Check parameters
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
                // If quiz result is provided, adjust score accordingly
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
        // 获取用户的所有学习记录
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        // 计算总学习手语数量
        int totalSigns = records.size();

        // 三种掌握等级的数量统计
        int notLearnedCount = 0; // 这里通常为0，因为只有学习过的才有记录
        int notReviewedCount = (int) records.stream()
                .filter(r -> r.getProficiencyScore() >= NOT_REVIEWED_THRESHOLD && r.getProficiencyScore() < MASTERED_THRESHOLD)
                .count();
        int masteredSigns = (int) records.stream()
                .filter(r -> r.getProficiencyScore() >= MASTERED_THRESHOLD)
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
        summary.setNotReviewedCount(notReviewedCount);
        summary.setNotLearnedCount(notLearnedCount);
        summary.setAverageProficiency(averageProficiency);
        summary.setTotalLearningTimeMinutes(totalLearningTime);

        // 设置推荐的下一步学习内容
        List<Sign> recommendedNext = getRecommendedSigns(userId, 3);
        summary.setRecommendedNextSigns(recommendedNext);

        return summary;
    }

    @Resource
    public UserLearningStatistics getUserLearningStatistics(Integer userId) {
        // 获取用户的所有学习记录
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        UserLearningStatistics statistics = new UserLearningStatistics();

        // 设置学习时间和日期统计
        if (!records.isEmpty()) {
            // 设置学习天数
            long days = records.stream()
                    .map(r -> r.getLastViewTime().toLocalDate())
                    .distinct()
                    .count();
            statistics.setTotalLearningDays((int) days);

            // 设置最早学习日期
            LocalDateTime firstLearningDate = records.stream()
                    .min(Comparator.comparing(UserLearningRecord::getCreateTime))
                    .map(UserLearningRecord::getCreateTime)
                    .orElse(LocalDateTime.now());
            statistics.setFirstLearningDate(firstLearningDate);

            // 设置最近学习日期
            LocalDateTime lastLearningDate = records.stream()
                    .max(Comparator.comparing(UserLearningRecord::getLastViewTime))
                    .map(UserLearningRecord::getLastViewTime)
                    .orElse(LocalDateTime.now());
            statistics.setLastLearningDate(lastLearningDate);

            // 设置连续学习天数
            int consecutiveDays = calculateConsecutiveLearningDays(userId);
            statistics.setConsecutiveLearningDays(consecutiveDays);

            // 设置本周学习数据
            List<WeeklyLearningData> weeklyData = getWeeklyLearningData(userId);
            statistics.setWeeklyLearningData(weeklyData);

            // 根据记录计算分类掌握情况
            Map<String, Double> categoryMasteryMap = new HashMap<>();
            // 这里需要查询每个sign的分类信息，示例逻辑
            List<Integer> signIds = records.stream()
                    .map(UserLearningRecord::getSignId)
                    .collect(Collectors.toList());

            if (!signIds.isEmpty()) {
                List<Sign> signs = signMapper.selectBatchIds(signIds);

                // 根据parentName分组计算掌握度
                Map<String, List<UserLearningRecord>> recordsByCategoryMap = new HashMap<>();

                for (UserLearningRecord record : records) {
                    Sign sign = signs.stream()
                            .filter(s -> s.getId().equals(record.getSignId()))
                            .findFirst()
                            .orElse(null);

                    if (sign != null && sign.getParentName() != null) {
                        recordsByCategoryMap.computeIfAbsent(sign.getParentName(), k -> new ArrayList<>())
                                .add(record);
                    }
                }

                // 计算每个分类的平均掌握度
                for (Map.Entry<String, List<UserLearningRecord>> entry : recordsByCategoryMap.entrySet()) {
                    double avgProficiency = entry.getValue().stream()
                            .mapToInt(UserLearningRecord::getProficiencyScore)
                            .average()
                            .orElse(0);

                    categoryMasteryMap.put(entry.getKey(), avgProficiency);
                }
            }

            statistics.setCategoryMasteryMap(categoryMasteryMap);
        }

        return statistics;
    }

    private int calculateConsecutiveLearningDays(Integer userId) {
        // 获取用户的学习记录，按日期分组
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        if (records.isEmpty()) {
            return 0;
        }

        // 获取所有学习日期
        Set<LocalDate> learningDates = records.stream()
                .map(r -> r.getLastViewTime().toLocalDate())
                .collect(Collectors.toSet());

        // 检查今天是否学习了
        boolean learnedToday = learningDates.contains(LocalDate.now());

        // 计算连续学习天数
        int consecutiveDays = learnedToday ? 1 : 0;
        LocalDate currentDate = learnedToday ? LocalDate.now().minusDays(1) : LocalDate.now().minusDays(1);

        while (learningDates.contains(currentDate)) {
            consecutiveDays++;
            currentDate = currentDate.minusDays(1);
        }

        return consecutiveDays;
    }

    private List<WeeklyLearningData> getWeeklyLearningData(Integer userId) {
        // 准备周数据结构
        List<WeeklyLearningData> weeklyData = new ArrayList<>();

        // 本周的开始日期（周一）
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 获取用户的学习记录
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        // 过滤本周的学习记录并按日期分组
        Map<LocalDate, List<UserLearningRecord>> recordsByDate = records.stream()
                .filter(r -> {
                    LocalDate recordDate = r.getLastViewTime().toLocalDate();
                    return !recordDate.isBefore(startOfWeek) && !recordDate.isAfter(LocalDate.now());
                })
                .collect(Collectors.groupingBy(r -> r.getLastViewTime().toLocalDate()));

        // 遍历周一到今天，生成每天的学习数据
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);

            // 如果日期超过今天，则不再处理
            if (date.isAfter(LocalDate.now())) {
                break;
            }

            // 获取这一天的学习记录
            List<UserLearningRecord> dayRecords = recordsByDate.getOrDefault(date, Collections.emptyList());

            // 计算学习次数
            int learningCount = dayRecords.size();

            // 计算学习时长（假设每次5分钟）
            int learningMinutes = dayRecords.stream()
                    .mapToInt(UserLearningRecord::getViewCount)
                    .sum() * 5;

            // 创建日数据并添加到周数据
            WeeklyLearningData dayData = new WeeklyLearningData();
            dayData.setDate(date);
            dayData.setDayOfWeek(date.getDayOfWeek().getValue());
            dayData.setLearningCount(learningCount);
            dayData.setLearningMinutes(learningMinutes);

            weeklyData.add(dayData);
        }

        return weeklyData;
    }
    public void updateLearningRecordExtended(Integer userId, Integer signId, Boolean isCorrect, Boolean extendedView) {
        // Check parameters
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

            // Extended view gives higher initial proficiency
            int initialScore = 10; // default
            if (isCorrect != null && isCorrect) initialScore = 30;
            if (extendedView != null && extendedView) initialScore += 5;

            record.setProficiencyScore(initialScore);
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

            // Calculate proficiency increase
            int proficiencyDelta = 0;

            if (isCorrect != null) {
                // Quiz result provided
                proficiencyDelta = isCorrect ? 5 : -2;

                // Update quiz accuracy
                double currentAccuracy = record.getQuizAccuracy() != null ? record.getQuizAccuracy() : 0;
                int quizCount = record.getViewCount();
                double newAccuracy = (currentAccuracy * (quizCount - 1) + (isCorrect ? 1 : 0)) / quizCount;
                record.setQuizAccuracy(newAccuracy);
            } else if (extendedView != null && extendedView) {
                // Extended viewing gives higher proficiency increase
                proficiencyDelta = 2;
            } else {
                // Just regular viewing
                proficiencyDelta = 1;
            }

            // Ensure proficiency stays within 0-100 range
            int newProficiency = Math.min(100, Math.max(0, record.getProficiencyScore() + proficiencyDelta));
            record.setProficiencyScore(newProficiency);
            record.setUpdateTime(LocalDateTime.now());

            learningRecordMapper.updateById(record);
        }
    }
}