package com.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mybatisplus.domain.TestQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TestQuestionMapper extends BaseMapper<TestQuestion> {

    @Select("SELECT q.*, s.imageSrc FROM test_question q " +
            "LEFT JOIN sign s ON q.signId = s.id " +
            "WHERE q.difficulty = #{difficulty} " +
            "ORDER BY RAND() LIMIT #{limit}")
    List<TestQuestion> selectRandomQuestionsByDifficulty(@Param("difficulty") String difficulty,
                                                         @Param("limit") Integer limit);

    @Select("SELECT q.*, s.imageSrc FROM test_question q " +
            "LEFT JOIN sign s ON q.signId = s.id " +
            "WHERE s.parentId = #{categoryId} OR s.childId = #{categoryId} " +
            "ORDER BY RAND() LIMIT #{limit}")
    List<TestQuestion> selectRandomQuestionsByCategory(@Param("categoryId") Integer categoryId,
                                                       @Param("limit") Integer limit);

    @Select("SELECT q.*, s.imageSrc FROM test_question q " +
            "LEFT JOIN sign s ON q.signId = s.id " +
            "INNER JOIN user_learning_record r ON s.id = r.sign_id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.proficiency_score ASC, RAND() LIMIT #{limit}")
    List<TestQuestion> selectRandomQuestionsByUserProgress(@Param("userId") Integer userId,
                                                           @Param("limit") Integer limit);
}