package com.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mybatisplus.domain.UserTestAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserTestAnswerMapper extends BaseMapper<UserTestAnswer> {

    @Select("SELECT * FROM user_test_answer WHERE test_record_id = #{testRecordId}")
    List<UserTestAnswer> selectByTestRecordId(@Param("testRecordId") Integer testRecordId);
}