package com.study.mybatisplus.service;

import com.study.mybatisplus.domain.ChildSign;
import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mybatisplus.dto.LearningStatisticsDTO;

import java.util.Map;

/**
* @author 14530
* @description 针对表【childsign】的数据库操作Service
* @createDate 2025-02-14 15:59:49
*/
public interface ChildsignService extends IService<ChildSign> {
    void addChildSign(ChildSign childSign);
    void update(ChildSign childSign);
    void delete(ChildSign childSign);

    interface LearningStatisticsService {

        /**
         * 获取用户的详细学习统计数据
         * @param userId 用户ID
         * @return 用户的详细学习统计数据
         */
        LearningStatisticsDTO getUserLearningStatistics(Integer userId);

        /**
         * 获取用户的掌握等级分布数据
         * @param userId 用户ID
         * @return 掌握等级分布数据，key为掌握等级(未学习/未复习/已掌握)，value为数量
         */
        Map<String, Integer> getMasteryDistribution(Integer userId);
    }
}
