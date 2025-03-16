package com.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mybatisplus.domain.UserLearningRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserLearningRecordMapper extends BaseMapper<UserLearningRecord> {

    /**
     * 获取用户的所有学习记录
     * @param userId 用户ID
     * @return 学习记录列表
     */
    @Select("SELECT * FROM user_learning_record WHERE user_id = #{userId}")
    List<UserLearningRecord> selectByUserId(@Param("userId") Integer userId);

    /**
     * 获取用户特定手语的学习记录
     * @param userId 用户ID
     * @param signId 手语ID
     * @return 学习记录
     */
    @Select("SELECT * FROM user_learning_record WHERE user_id = #{userId} AND sign_id = #{signId}")
    UserLearningRecord selectByUserIdAndSignId(@Param("userId") Integer userId, @Param("signId") Integer signId);
}