package com.study.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mybatisplus.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper {
    //根据用户名查询用户
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);
    //添加
    @Insert("insert into user(username,password,create_time,update_time)"+
            "values(#{username},#{password},now(),now())")
    void add(String username,String password);
    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);
    @Update("update user set user_pic=#{avatarUrl},update_time=now() where username=#{username}")
    void updateAvatar(String avatarUrl,String username);
    @Update("update user set password=#{md5String},update_time=now() where username=#{username}")
    void updatePwd(String md5String, String username);
}
