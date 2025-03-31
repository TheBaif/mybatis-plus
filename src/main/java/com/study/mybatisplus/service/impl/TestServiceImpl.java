package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.*;
import com.study.mybatisplus.dto.TestOptionDTO;
import com.study.mybatisplus.dto.TestResultDTO;
import com.study.mybatisplus.dto.TestSubmitDTO;
import com.study.mybatisplus.mapper.*;
import com.study.mybatisplus.service.LearningRecommendationService;
import com.study.mybatisplus.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestQuestionMapper testQuestionMapper;

    @Autowired
    private TestOptionMapper testOptionMapper;

    @Autowired
    private UserTestRecordMapper userTestRecordMapper;

    @Autowired
    private UserTestAnswerMapper userTestAnswerMapper;

    @Autowired
    private SignMapper signMapper;

    @Autowired
    private LearningRecommendationService learningRecommendationService;

    @Override
    public List<TestQuestion> getTestQuestions(Integer limit, String difficulty, Integer categoryId, Integer userId) {
        // 设置默认值
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        List<TestQuestion> questions;

        // 根据条件获取题目
        if (difficulty != null && !difficulty.isEmpty()) {
            questions = testQuestionMapper.selectRandomQuestionsByDifficulty(difficulty, limit);
        } else if (categoryId != null) {
            questions = testQuestionMapper.selectRandomQuestionsByCategory(categoryId, limit);
        } else {
            // 基于用户学习进度推荐题目
            questions = testQuestionMapper.selectRandomQuestionsByUserProgress(userId, limit);

            // 如果没有足够的题目，补充随机题目
            if (questions.size() < limit) {
                int remainingCount = limit - questions.size();
                List<TestQuestion> additionalQuestions = testQuestionMapper.selectRandomQuestionsByDifficulty("BEGINNER", remainingCount);
                questions.addAll(additionalQuestions);
            }
        }

        // 为每个题目加载选项
        for (TestQuestion question : questions) {
            List<TestOption> options = testOptionMapper.selectByQuestionId(question.getId());
            question.setOptions(options);
        }

        return questions;
    }

    @Override
    @Transactional
    public TestResultDTO submitTest(Integer userId, TestSubmitDTO submitDTO) {
        LocalDateTime now = LocalDateTime.now();

        // 创建测验记录
        UserTestRecord testRecord = new UserTestRecord();
        testRecord.setUserId(userId);
        testRecord.setTotalQuestions(submitDTO.getAnswers().size());

        // 计算得分和正确数
        int correctCount = 0;
        int totalTimeSpent = 0;

        for (TestSubmitDTO.AnswerDTO answer : submitDTO.getAnswers()) {
            if (answer.getIsCorrect()) {
                correctCount++;
            }

            if (answer.getTimeSpent() != null) {
                totalTimeSpent += answer.getTimeSpent();
            }
        }

        // 计算得分(满分100)
        int score = (int) (((double) correctCount / submitDTO.getAnswers().size()) * 100);

        // 设置测验记录属性
        testRecord.setScore(score);
        testRecord.setAccuracy((double) correctCount / submitDTO.getAnswers().size() * 100);
        testRecord.setTimeSpent(totalTimeSpent);
        testRecord.setDifficulty("MIXED"); // 可根据实际题目难度计算
        testRecord.setTestTime(now);
        testRecord.setCreateTime(now);
        testRecord.setUpdateTime(now);

        // 保存测验记录
        userTestRecordMapper.insert(testRecord);

        // 构建测验结果
        TestResultDTO resultDTO = new TestResultDTO();
        resultDTO.setScore(score);
        resultDTO.setTotalQuestions(submitDTO.getAnswers().size());
        resultDTO.setCorrectCount(correctCount);
        resultDTO.setAccuracy((double) correctCount / submitDTO.getAnswers().size() * 100);
        resultDTO.setTimeSpent(totalTimeSpent);

        // 保存答题详情并构建结果详情
        List<TestResultDTO.TestResultDetailDTO> details = new ArrayList<>();

        for (TestSubmitDTO.AnswerDTO answer : submitDTO.getAnswers()) {
            // 保存答题记录
            UserTestAnswer userAnswer = new UserTestAnswer();
            userAnswer.setTestRecordId(testRecord.getId());
            userAnswer.setQuestionId(answer.getQuestionId());
            userAnswer.setUserAnswerId(answer.getAnswerId());
            userAnswer.setIsCorrect(answer.getIsCorrect());
            userAnswer.setTimeSpent(answer.getTimeSpent());
            userAnswer.setCreateTime(now);
            userAnswer.setUpdateTime(now);
            userTestAnswerMapper.insert(userAnswer);

            // 获取正确答案
            TestOption correctOption = testOptionMapper.selectCorrectOptionByQuestionId(answer.getQuestionId());

            // 获取题目对应的手语信息
            TestQuestion question = testQuestionMapper.selectById(answer.getQuestionId());
            Sign sign = signMapper.selectById(question.getSignId());

            // 构建结果详情
            TestResultDTO.TestResultDetailDTO detail = new TestResultDTO.TestResultDetailDTO();
            detail.setQuestionId(answer.getQuestionId());
            detail.setIsCorrect(answer.getIsCorrect());
            detail.setUserAnswerId(answer.getAnswerId());
            detail.setCorrectAnswerId(correctOption != null ? correctOption.getId() : null);
            detail.setSignId(question.getSignId());
            detail.setSignName(sign != null ? sign.getName() : "");

            details.add(detail);

            // 更新用户学习记录
            if (sign != null) {
                learningRecommendationService.updateLearningRecord(userId, sign.getId(), answer.getIsCorrect());
            }
        }

        resultDTO.setDetails(details);

        return resultDTO;
    }

    @Override
    public List<UserTestRecord> getTestHistory(Integer userId, Integer page, Integer limit) {
        // 设置默认值
        if (page == null || page <= 0) {
            page = 1;
        }
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 计算偏移量
        int offset = (page - 1) * limit;

        // 查询历史记录
        return userTestRecordMapper.selectByUserIdWithPagination(userId, offset, limit);
    }

    @Override
    @Transactional
    public Integer addTestQuestion(TestQuestion question) {
        // 验证引用的手语是否存在
        if (question.getSignId() != null && !validateSignExists(question.getSignId())) {
            throw new RuntimeException("引用的手语ID不存在: " + question.getSignId());
        }

        // 设置时间字段
        LocalDateTime now = LocalDateTime.now();
        question.setCreateTime(now);
        question.setUpdateTime(now);

        // 插入数据并返回ID
        testQuestionMapper.insert(question);
        return question.getId();
    }

    @Override
    @Transactional
    public void addTestOptions(Integer questionId, List<TestOptionDTO> options) {
        // 验证题目是否存在
        TestQuestion question = testQuestionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在: " + questionId);
        }

        // 删除该题目的旧选项
        LambdaQueryWrapper<TestOption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TestOption::getQuestionId, questionId);
        testOptionMapper.delete(wrapper);

        // 插入新选项
        LocalDateTime now = LocalDateTime.now();
        for (TestOptionDTO optionDTO : options) {
            TestOption option = new TestOption();
            option.setQuestionId(questionId);
            option.setText(optionDTO.getText());
            option.setIsCorrect(optionDTO.getIsCorrect());
            option.setCreateTime(now);
            option.setUpdateTime(now);
            testOptionMapper.insert(option);
        }
    }

    // 验证手语是否存在的辅助方法
    private boolean validateSignExists(Integer signId) {
        Sign sign = signMapper.selectById(signId);
        return sign != null;
    }
}