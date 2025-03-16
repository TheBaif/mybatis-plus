package com.study.mybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @TableName parentsign
 */
@TableName(value ="parentsign")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentSign {
    @NotNull
    private Integer id;
    @NotEmpty
    private String name;
}