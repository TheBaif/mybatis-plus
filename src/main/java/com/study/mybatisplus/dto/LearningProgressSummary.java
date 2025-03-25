package com.study.mybatisplus.dto;

import com.study.mybatisplus.domain.Sign;
import lombok.Data;

import java.util.List;

@Data
public class LearningProgressSummary {
    // 已学习的手语总数
    private Integer totalSigns;

    // 未学习的手语数量（只有通过计算得到，不在记录中）
    private Integer notLearnedCount;

    // 未复习的手语数量（熟练度 >= 30 && < 70）
    private Integer notReviewedCount;

    // 已掌握的手语数量（熟练度 >= 70）
    private Integer masteredSigns;

    // 平均熟练度
    private Double averageProficiency;

    // 累计学习时间（分钟）
    private Integer totalLearningTimeMinutes;

    // 推荐下一步学习的手语
    private List<Sign> recommendedNextSigns;
}