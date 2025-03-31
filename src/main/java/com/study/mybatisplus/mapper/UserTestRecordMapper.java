package com.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mybatisplus.domain.UserTestRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserTestRecordMapper extends BaseMapper<UserTestRecord> {

    @Select("SELECT * FROM user_test_record WHERE user_id = #{userId} " +
            "ORDER BY test_time DESC LIMIT #{offset}, #{limit}")
    List<UserTestRecord> selectByUserIdWithPagination(@Param("userId") Integer userId,
                                                      @Param("offset") Integer offset,
                                                      @Param("limit") Integer limit);
}