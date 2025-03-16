package com.study.mybatisplus.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName childsign
 */
@TableName(value ="childsign")
@Data
public class ChildSign implements Serializable {
    private Integer id;

    private String name;
    @TableField("parentId")
    private Integer parentId;
    @TableField("parentName")
    private String parentName;

}