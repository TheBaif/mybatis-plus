package com.study.mybatisplus.service;

import com.study.mybatisplus.domain.TestQuestion;
import com.study.mybatisplus.domain.UserTestRecord;
import com.study.mybatisplus.dto.TestResultDTO;
import com.study.mybatisplus.dto.TestSubmitDTO;

import java.util.List;

public interface TestService {

    /**
     * 获取测验题目
     * @param limit 题目数量限制
     * @param difficulty 难度等级
     * @param categoryId 分类ID
     * @param userId 用户ID
     * @return 测验题目列表
     */
    List<TestQuestion> getTestQuestions(Integer limit, String difficulty, Integer categoryId, Integer userId);

    /**
     * 提交测验结果
     * @param userId 用户ID
     * @param submitDTO 提交的答案数据
     * @return 测验结果
     */
    TestResultDTO submitTest(Integer userId, TestSubmitDTO submitDTO);

    /**
     * 获取用户测验历史
     * @param userId 用户ID
     * @param page 页码
     * @param limit 每页数量
     * @return 测验历史记录列表
     */
    List<UserTestRecord> getTestHistory(Integer userId, Integer page, Integer limit);

    /**
     * 添加测验题目
     * @param question 题目信息
     * @return 添加的题目ID
     */
    Integer addTestQuestion(TestQuestion question);

    /**
     * 添加测验选项
     * @param questionId 题目ID
     * @param options 选项列表
     */
    void addTestOptions(Integer questionId, List<com.study.mybatisplus.dto.TestOptionDTO> options);
}