package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.UserSearchHistory;
import com.study.mybatisplus.mapper.UserSearchHistoryMapper;
import com.study.mybatisplus.service.UserSearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSearchHistoryServiceImpl implements UserSearchHistoryService {

    @Autowired
    private UserSearchHistoryMapper historyMapper;

    private static final int MAX_HISTORY_COUNT = 10;

    @Override
    @Transactional
    public void saveHistory(Integer userId, String keyword) {
        if (userId == null || keyword == null || keyword.trim().isEmpty()) {
            throw new RuntimeException("用户ID和关键词不能为空");
        }

        // 检查是否已存在
        LambdaQueryWrapper<UserSearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSearchHistory::getUserId, userId)
                .eq(UserSearchHistory::getKeyword, keyword.trim());

        UserSearchHistory existingHistory = historyMapper.selectOne(wrapper);

        if (existingHistory != null) {
            // 更新时间
            existingHistory.setUpdateTime(LocalDateTime.now());
            historyMapper.updateById(existingHistory);
            return;
        }

        // 检查历史记录数量
        LambdaQueryWrapper<UserSearchHistory> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(UserSearchHistory::getUserId, userId);
        Integer count = Math.toIntExact(historyMapper.selectCount(countWrapper));

        // 超过限制则删除最旧的记录
        if (count >= MAX_HISTORY_COUNT) {
            LambdaQueryWrapper<UserSearchHistory> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(UserSearchHistory::getUserId, userId)
                    .orderByAsc(UserSearchHistory::getUpdateTime)
                    .last("LIMIT 1");
            historyMapper.delete(deleteWrapper);
        }

        // 保存新记录
        UserSearchHistory history = new UserSearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword.trim());
        history.setCreateTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());
        historyMapper.insert(history);
    }

    @Override
    @Transactional
    public void batchSaveHistory(Integer userId, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }

        // 清空原有历史
        clearHistory(userId);

        // 批量保存新历史
        keywords.stream()
                .limit(MAX_HISTORY_COUNT)
                .forEach(keyword -> saveHistory(userId, keyword));
    }

    @Override
    public List<String> getUserHistory(Integer userId) {
        return historyMapper.getRecentHistory(userId, MAX_HISTORY_COUNT);
    }

    @Override
    @Transactional
    public void clearHistory(Integer userId) {
        LambdaQueryWrapper<UserSearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSearchHistory::getUserId, userId);
        historyMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void deleteHistory(Integer userId, String keyword) {
        LambdaQueryWrapper<UserSearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSearchHistory::getUserId, userId)
                .eq(UserSearchHistory::getKeyword, keyword);
        historyMapper.delete(wrapper);
    }
}
