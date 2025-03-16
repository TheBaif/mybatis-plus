package com.study.mybatisplus.dto;

import com.study.mybatisplus.domain.Sign;
import lombok.Data;

import java.util.List;

@Data
public class LearningProgressSummary {
    // 已学习的手语总数
    private Integer totalSigns;

    // 熟练掌握的手语数量
    private Integer masteredSigns;

    // 平均熟练度
    private Double averageProficiency;

    // 累计学习时间（分钟）
    private Integer totalLearningTimeMinutes;

    // 推荐下一步学习的手语
    private List<Sign> recommendedNextSigns;
}