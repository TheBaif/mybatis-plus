package com.study.mybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_search_history")
public class UserSearchHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String keyword;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
