package com.study.mybatisplus.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestSubmitDTO {
    private List<AnswerDTO> answers;

    @Data
    public static class AnswerDTO {
        private Integer questionId;
        private Integer answerId;
        private Boolean isCorrect;
        private Integer timeSpent;
    }
}