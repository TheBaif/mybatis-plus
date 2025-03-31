package com.study.mybatisplus.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestQuestionDTO {
    private String question;
    private Integer signId;
    private String difficulty;
    private List<TestOptionDTO> options;
}