package com.study.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.domain.User;
import com.study.mybatisplus.dto.SearchHistoryDTO;
import com.study.mybatisplus.mapper.UserMapper;
import com.study.mybatisplus.service.UserSearchHistoryService;
import com.study.mybatisplus.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserSearchHistoryController {

    @Autowired
    private UserSearchHistoryService historyService;

    @Autowired
    private UserMapper userMapper;

    private Integer getUserIdFromToken(String token) {
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            System.out.println("Claims: " + claims);

            String username = (String) claims.get("username");
            if (username == null) {
                return null;
            }

            // 使用自定义的查询方法
            User user = userMapper.findByUserName(username);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/search-history")
    public Result saveSearchHistory(@RequestHeader("Authorization") String token,
                                    @RequestBody SearchHistoryDTO dto) {
        try {
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无法获取用户ID");
            }

            historyService.batchSaveHistory(userId, dto.getHistory());
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存搜索历史失败: " + e.getMessage());
        }
    }

    @GetMapping("/search-history")
    public Result getSearchHistory(@RequestHeader("Authorization") String token) {
        try {
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无法获取用户ID");
            }

            List<String> history = historyService.getUserHistory(userId);
            return Result.success(history);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取搜索历史失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/search-history")
    public Result clearHistory(@RequestHeader("Authorization") String token) {
        try {
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无法获取用户ID");
            }

            historyService.clearHistory(userId);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("清空搜索历史失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/search-history/{keyword}")
    public Result deleteHistory(@RequestHeader("Authorization") String token,
                                @PathVariable String keyword) {
        try {
            Integer userId = getUserIdFromToken(token);
            if (userId == null) {
                return Result.error("无法获取用户ID");
            }

            historyService.deleteHistory(userId, keyword);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除搜索历史失败: " + e.getMessage());
        }
    }
}
