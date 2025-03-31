package com.study.mybatisplus.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestResultDTO {
    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;
    private Double accuracy;
    private Integer timeSpent;
    private List<TestResultDetailDTO> details;

    @Data
    public static class TestResultDetailDTO {
        private Integer questionId;
        private Boolean isCorrect;
        private Integer userAnswerId;
        private Integer correctAnswerId;
        private Integer signId;
        private String signName;
    }
}