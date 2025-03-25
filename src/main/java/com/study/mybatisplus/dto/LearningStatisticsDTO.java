package com.study.mybatisplus.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LearningStatisticsDTO {
    // 总学习天数
    private Integer totalLearningDays;

    // 连续学习天数
    private Integer consecutiveLearningDays;

    // 最早学习日期
    private LocalDateTime firstLearningDate;

    // 最近学习日期
    private LocalDateTime lastLearningDate;

    // 每日学习活动数据（最近一周）
    private List<WeeklyLearningData> dailyActivityData;

    // 分类学习统计
    private List<LearningCategoryStats> categoryStats;
}
