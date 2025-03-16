package com.study.mybatisplus.service;

import java.util.List;

public interface UserSearchHistoryService {
    void saveHistory(Integer userId, String keyword);
    void batchSaveHistory(Integer userId, List<String> keywords);
    List<String> getUserHistory(Integer userId);
    void clearHistory(Integer userId);
    void deleteHistory(Integer userId, String keyword);
}
