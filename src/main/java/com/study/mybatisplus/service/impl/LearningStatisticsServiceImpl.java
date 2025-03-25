package com.study.mybatisplus.service.impl;

import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.domain.UserLearningRecord;
import com.study.mybatisplus.dto.LearningCategoryStats;
import com.study.mybatisplus.dto.LearningStatisticsDTO;
import com.study.mybatisplus.dto.WeeklyLearningData;
import com.study.mybatisplus.mapper.SignMapper;
import com.study.mybatisplus.mapper.UserLearningRecordMapper;
import com.study.mybatisplus.service.ChildsignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningStatisticsServiceImpl implements ChildsignService.LearningStatisticsService {

    @Autowired
    private UserLearningRecordMapper learningRecordMapper;

    @Autowired
    private SignMapper signMapper;

    // 定义掌握等级的分数阈值
    private static final int NOT_LEARNED_THRESHOLD = 0;
    private static final int NOT_REVIEWED_THRESHOLD = 30;
    private static final int MASTERED_THRESHOLD = 70;

    @Override
    public LearningStatisticsDTO getUserLearningStatistics(Integer userId) {
        // 创建返回的统计数据对象
        LearningStatisticsDTO statistics = new LearningStatisticsDTO();

        // 获取用户的所有学习记录
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        // 如果没有记录，返回默认值
        if (records == null || records.isEmpty()) {
            statistics.setTotalLearningDays(0);
            statistics.setConsecutiveLearningDays(0);
            statistics.setFirstLearningDate(null);
            statistics.setLastLearningDate(null);
            statistics.setDailyActivityData(Collections.emptyList());
            statistics.setCategoryStats(Collections.emptyList());
            return statistics;
        }

        // 1. 计算学习天数相关统计
        calculateLearningDays(statistics, records);

        // 2. 获取每周学习数据
        statistics.setDailyActivityData(getWeeklyLearningData(userId));

        // 3. 计算分类掌握统计
        calculateCategoryStats(statistics, records);

        return statistics;
    }

    private void calculateLearningDays(LearningStatisticsDTO statistics, List<UserLearningRecord> records) {
        // 获取所有学习日期
        Set<LocalDate> learningDates = records.stream()
                .map(record -> record.getLastViewTime().toLocalDate())
                .collect(Collectors.toSet());

        // 设置总学习天数
        statistics.setTotalLearningDays(learningDates.size());

        // 设置最早学习日期
        LocalDateTime firstLearningDate = records.stream()
                .min(Comparator.comparing(UserLearningRecord::getCreateTime))
                .map(UserLearningRecord::getCreateTime)
                .orElse(null);
        statistics.setFirstLearningDate(firstLearningDate);

        // 设置最近学习日期
        LocalDateTime lastLearningDate = records.stream()
                .max(Comparator.comparing(UserLearningRecord::getLastViewTime))
                .map(UserLearningRecord::getLastViewTime)
                .orElse(null);
        statistics.setLastLearningDate(lastLearningDate);

        // 计算连续学习天数
        int consecutiveDays = calculateConsecutiveLearningDays(learningDates);
        statistics.setConsecutiveLearningDays(consecutiveDays);
    }

    private int calculateConsecutiveLearningDays(Set<LocalDate> learningDates) {
        if (learningDates.isEmpty()) {
            return 0;
        }

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

    private void calculateCategoryStats(LearningStatisticsDTO statistics, List<UserLearningRecord> records) {
        // 获取所有已学习手语的ID
        List<Integer> signIds = records.stream()
                .map(UserLearningRecord::getSignId)
                .collect(Collectors.toList());

        if (signIds.isEmpty()) {
            statistics.setCategoryStats(Collections.emptyList());
            return;
        }

        // 查询所有相关手语
        List<Sign> signs = signMapper.selectBatchIds(signIds);

        // 按分类分组统计
        Map<String, List<UserLearningRecord>> recordsByCategory = new HashMap<>();

        // 关联记录和手语信息
        for (UserLearningRecord record : records) {
            Sign sign = signs.stream()
                    .filter(s -> s.getId().equals(record.getSignId()))
                    .findFirst()
                    .orElse(null);

            if (sign != null && sign.getParentName() != null) {
                String category = sign.getParentName();
                recordsByCategory.computeIfAbsent(category, k -> new ArrayList<>())
                        .add(record);
            }
        }

        // 计算每个分类的统计数据
        List<LearningCategoryStats> categoryStats = new ArrayList<>();

        for (Map.Entry<String, List<UserLearningRecord>> entry : recordsByCategory.entrySet()) {
            String category = entry.getKey();
            List<UserLearningRecord> categoryRecords = entry.getValue();

            // 计算该分类的平均熟练度
            double avgProficiency = categoryRecords.stream()
                    .mapToInt(UserLearningRecord::getProficiencyScore)
                    .average()
                    .orElse(0);

            // 计算该分类的掌握等级分布
            long notLearnedCount = 0; // 通常为0，因为都是已学习的
            long notReviewedCount = categoryRecords.stream()
                    .filter(r -> r.getProficiencyScore() < MASTERED_THRESHOLD && r.getProficiencyScore() >= NOT_REVIEWED_THRESHOLD)
                    .count();
            long masteredCount = categoryRecords.stream()
                    .filter(r -> r.getProficiencyScore() >= MASTERED_THRESHOLD)
                    .count();

            // 创建分类统计对象
            LearningCategoryStats categoryStat = new LearningCategoryStats();
            categoryStat.setCategory(category);
            categoryStat.setTotalSigns(categoryRecords.size());
            categoryStat.setAverageProficiency(avgProficiency);
            categoryStat.setNotLearnedCount((int) notLearnedCount);
            categoryStat.setNotReviewedCount((int) notReviewedCount);
            categoryStat.setMasteredCount((int) masteredCount);

            categoryStats.add(categoryStat);
        }

        // 按平均熟练度降序排序
        categoryStats.sort(Comparator.comparing(LearningCategoryStats::getAverageProficiency).reversed());

        statistics.setCategoryStats(categoryStats);
    }

    @Override
    public Map<String, Integer> getMasteryDistribution(Integer userId) {
        // 获取用户的所有学习记录
        List<UserLearningRecord> records = learningRecordMapper.selectByUserId(userId);

        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("未学习", 0); // 这个数量需要与总的手语数量比较计算

        if (records == null || records.isEmpty()) {
            distribution.put("未复习", 0);
            distribution.put("已掌握", 0);
            return distribution;
        }

        // 计算未复习和已掌握的数量
        int notReviewedCount = (int) records.stream()
                .filter(r -> r.getProficiencyScore() < MASTERED_THRESHOLD && r.getProficiencyScore() >= NOT_REVIEWED_THRESHOLD)
                .count();

        int masteredCount = (int) records.stream()
                .filter(r -> r.getProficiencyScore() >= MASTERED_THRESHOLD)
                .count();

        // 更新分布统计
        distribution.put("未复习", notReviewedCount);
        distribution.put("已掌握", masteredCount);

        // 计算未学习数量（总数减去已学习的）
        int totalSignsCount = Math.toIntExact(signMapper.selectCount(null)); // 获取总的手语数量
        int learnedCount = records.size();
        int notLearnedCount = totalSignsCount - learnedCount;
        distribution.put("未学习", Math.max(0, notLearnedCount)); // 防止出现负数

        return distribution;
    }
}