package com.study.mybatisplus.dto;

import com.study.mybatisplus.dto.WeeklyLearningData;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UserLearningStatistics {
    // 总学习天数
    private Integer totalLearningDays;

    // 连续学习天数
    private Integer consecutiveLearningDays;

    // 最早学习日期
    private LocalDateTime firstLearningDate;

    // 最近学习日期
    private LocalDateTime lastLearningDate;

    // 本周学习数据（每天的学习情况）
    private List<WeeklyLearningData> weeklyLearningData;

    // 各分类掌握程度，key为分类名称，value为掌握程度百分比
    private Map<String, Double> categoryMasteryMap;
}
