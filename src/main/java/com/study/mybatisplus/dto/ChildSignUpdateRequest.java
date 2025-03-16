package com.study.mybatisplus.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class ChildSignUpdateRequest {
    @NotBlank(message = "旧名称不能为空")
    private String oldName;
    @NotBlank(message = "新名称不能为空")
    private String newName;
    private Integer parentId;
    private String parentName;

}
