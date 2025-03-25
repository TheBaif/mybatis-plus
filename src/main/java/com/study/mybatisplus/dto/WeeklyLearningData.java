package com.study.mybatisplus.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WeeklyLearningData {
    // 日期
    private LocalDate date;

    // 星期几 (1-7, 周一为1)
    private Integer dayOfWeek;

    // 学习次数
    private Integer learningCount;

    // 学习时长（分钟）
    private Integer learningMinutes;
}
