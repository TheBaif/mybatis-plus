package com.study.mybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName sign
 */
@TableName(value ="sign")
@Data
public class Sign implements Serializable {
    private Integer id;

    private String name;
    @TableField("pinyin")
    private String pinyin;

    private String gesture;
    @TableField("parentId")
    private Integer parentId;
    @TableField("parentName")
    private String parentName;
    @TableField("childId")
    private Integer childId;
    @TableField("childName")
    private String childName;
    @TableField("imageSrc")
    private String imageSrc;
    @TableField("wordVideoSrc")
    private String wordVideoSrc;

    // 掌握度/难度字段
    private String difficulty;

    // 额外添加虚拟字段，不映射到数据库
    @TableField(exist = false)
    private String masteryLevel;

    // 学习记录统计相关
    @TableField(exist = false)
    private Integer proficiencyScore;
    @TableField(exist = false)
    private Integer viewCount;
}