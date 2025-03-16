package com.study.mybatisplus.dto;

import lombok.Data;

@Data
public class ParentSignUpdateRequest {
    private String oldName;
    private String newName;
}
