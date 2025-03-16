package com.study.mybatisplus.mapper;

import com.study.mybatisplus.domain.ChildSign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
* @author 14530
* @description 针对表【childsign】的数据库操作Mapper
* @createDate 2025-02-14 15:59:49
* @Entity com.study.mybatisplus.domain.Childsign
*/
@Mapper
public interface ChildSignMapper extends BaseMapper<ChildSign> {
    @Update("update parentsign set name=#{name},where id=#{id}")
    void update(ChildSign childSign);
    @Delete("delete from childsign where id=#{id}")
    void delete(ChildSign childSign);
}




