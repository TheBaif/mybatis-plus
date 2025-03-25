package com.study.mybatisplus.dto;

import lombok.Data;

@Data
public class LearningCategoryStats {
    // 分类名称
    private String category;

    // 该分类的手语总数
    private Integer totalSigns;

    // 平均熟练度
    private Double averageProficiency;

    // 未学习的数量
    private Integer notLearnedCount;

    // 未复习的数量
    private Integer notReviewedCount;

    // 已掌握的数量
    private Integer masteredCount;
}