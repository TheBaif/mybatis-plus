package com.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mybatisplus.domain.TestOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TestOptionMapper extends BaseMapper<TestOption> {

    @Select("SELECT * FROM test_option WHERE questionId = #{questionId}")
    List<TestOption> selectByQuestionId(@Param("questionId") Integer questionId);

    @Select("SELECT * FROM test_option WHERE questionId = #{questionId} AND isCorrect = 1 LIMIT 1")
    TestOption selectCorrectOptionByQuestionId(@Param("questionId") Integer questionId);
}