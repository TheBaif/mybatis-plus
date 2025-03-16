package com.study.mybatisplus.mapper;

import com.study.mybatisplus.domain.Sign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author 14530
* @description 针对表【sign】的数据库操作Mapper
* @createDate 2025-02-14 19:33:24
* @Entity com.study.mybatisplus.domain.Sign
*/
@Mapper
public interface SignMapper extends BaseMapper<Sign> {
    @Update("update sign set name=#{name},pinyin=#{pinyin},gesture=#{gesture} where id=#{id}")
    void update(Sign sign);
    @Delete("delete from sign where id=#{id}")
    void delete(Sign sign);
    List<Sign> list(Integer parentId, Integer childId);
    /**
     * 选择基础级别手语
     * @param limit 限制数量
     * @return 基础手语列表
     */
    @Select("SELECT * FROM sign WHERE difficulty = 'BEGINNER' LIMIT #{limit}")
    List<Sign> selectBasicSigns(@Param("limit") Integer limit);

    /**
     * 选择中级手语
     * @param limit 限制数量
     * @return 中级手语列表
     */
    @Select("SELECT * FROM sign WHERE difficulty = 'INTERMEDIATE' LIMIT #{limit}")
    List<Sign> selectIntermediateSigns(@Param("limit") Integer limit);

    /**
     * 选择高级手语
     * @param limit 限制数量
     * @return 高级手语列表
     */
    @Select("SELECT * FROM sign WHERE difficulty = 'ADVANCED' LIMIT #{limit}")
    List<Sign> selectAdvancedSigns(@Param("limit") Integer limit);
}




