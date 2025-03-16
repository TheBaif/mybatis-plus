package com.study.mybatisplus.mapper;

import com.study.mybatisplus.domain.ParentSign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
* @author 14530
* @description 针对表【parentsign】的数据库操作Mapper
* @createDate 2025-02-13 22:23:19
* @Entity com.study.mybatisplus.domain.Parentsign
*/
@Mapper
public interface ParentSignMapper extends BaseMapper<ParentSign> {

    @Update("update parentsign set name=#{name} where id=#{id}")
    void update(ParentSign parentSign);
    @Delete("delete from parentsign where id=#{id}")
    void delete(ParentSign parentSign);
}




