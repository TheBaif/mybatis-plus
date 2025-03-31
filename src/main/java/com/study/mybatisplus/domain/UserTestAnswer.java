package com.study.mybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_test_answer")
public class UserTestAnswer {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("test_record_id")
    private Integer testRecordId;

    @TableField("question_id")
    private Integer questionId;

    @TableField("user_answer_id")
    private Integer userAnswerId;

    @TableField("is_correct")
    private Boolean isCorrect;

    @TableField("time_spent")
    private Integer timeSpent;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}