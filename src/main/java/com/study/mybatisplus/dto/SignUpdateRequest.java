package com.study.mybatisplus.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class SignUpdateRequest {
    @NotBlank(message = "旧名称不能为空")
    private String oldName;    // 当前名称（作为条件）
    private String newName;    // 要更新的新名称
    private String gesture;
    private String pinyin;     // 其他可选更新字段
    private Integer parentId;
    private String parentName;
    private Integer childId;
    private String  childName;
}
