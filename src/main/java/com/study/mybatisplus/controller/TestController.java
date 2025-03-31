package com.study.mybatisplus.controller;

import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.domain.TestQuestion;
import com.study.mybatisplus.domain.User;
import com.study.mybatisplus.domain.UserTestRecord;
import com.study.mybatisplus.dto.TestOptionDTO;
import com.study.mybatisplus.dto.TestQuestionDTO;
import com.study.mybatisplus.dto.TestResultDTO;
import com.study.mybatisplus.dto.TestSubmitDTO;
import com.study.mybatisplus.mapper.UserMapper;
import com.study.mybatisplus.service.TestService;
import com.study.mybatisplus.utils.JwtUtil;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/questions")
    public Result<List<TestQuestion>> getTestQuestions(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer category) {
        try {
            // 从ThreadLocal中获取用户信息
            Map<String, Object> claims = ThreadLocalUtil.get();
            String username = (String) claims.get("username");
            // 获取用户ID
            Integer userId = getUserIdFromUsername(username);

            if (userId == null) {
                return Result.error("无法获取用户ID");
            }

            List<TestQuestion> questions = testService.getTestQuestions(limit, difficulty, category, userId);
            return Result.success(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测验题目失败: " + e.getMessage());
        }
    }

    @PostMapping("/submit")
    public Result<TestResultDTO> submitTest(@RequestBody TestSubmitDTO submitDTO) {
        try {
            // 从ThreadLocal中获取用户信息
            Map<String, Object> claims = ThreadLocalUtil.get();
            String username = (String) claims.get("username");
            // 获取用户ID
            Integer userId = getUserIdFromUsername(username);

            if (userId == null) {
                return Result.error("无法获取用户ID");
            }

            TestResultDTO result = testService.submitTest(userId, submitDTO);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交测验结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public Result<List<UserTestRecord>> getTestHistory(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            // 从ThreadLocal中获取用户信息
            Map<String, Object> claims = ThreadLocalUtil.get();
            String username = (String) claims.get("username");
            // 获取用户ID
            Integer userId = getUserIdFromUsername(username);

            if (userId == null) {
                return Result.error("无法获取用户ID");
            }

            List<UserTestRecord> history = testService.getTestHistory(userId, page, limit);
            return Result.success(history);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取测验历史失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Integer> addTestQuestion(@RequestBody TestQuestionDTO questionDTO) {
        try {
            TestQuestion question = new TestQuestion();
            question.setQuestion(questionDTO.getQuestion());
            question.setSignId(questionDTO.getSignId());
            question.setDifficulty(questionDTO.getDifficulty());

            // 添加题目
            Integer questionId = testService.addTestQuestion(question);

            // 添加选项
            if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
                testService.addTestOptions(questionId, questionDTO.getOptions());
            }

            return Result.success(questionId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加测验题目失败: " + e.getMessage());
        }
    }

    // 从用户名获取用户ID的辅助方法
    private Integer getUserIdFromUsername(String username) {
        try {
            User user = userMapper.findByUserName(username);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/mock")
    public Result<Void> generateMockQuestions(@RequestParam int count) {
        testService.generateMockQuestions(count);
        return Result.success();
    }
}