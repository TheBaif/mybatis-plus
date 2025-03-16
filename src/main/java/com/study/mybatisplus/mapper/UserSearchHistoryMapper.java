package com.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mybatisplus.domain.UserSearchHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserSearchHistoryMapper extends BaseMapper<UserSearchHistory> {

    @Select("SELECT keyword FROM user_search_history " +
            "WHERE user_id = #{userId} " +
            "ORDER BY update_time DESC LIMIT #{limit}")
    List<String> getRecentHistory(@Param("userId") Integer userId, @Param("limit") Integer limit);

    @Delete("DELETE FROM user_search_history WHERE user_id = #{userId}")
    int clearHistory(@Param("userId") Integer userId);
}
