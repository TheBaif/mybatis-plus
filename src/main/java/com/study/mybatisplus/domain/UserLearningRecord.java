package com.study.mybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_learning_record")
public class UserLearningRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer signId;

    // 熟练度评分：0-100
    private Integer proficiencyScore;

    // 学习次数
    private Integer viewCount;

    // 最近学习时间
    private LocalDateTime lastViewTime;

    // 测验正确率
    private Double quizAccuracy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}