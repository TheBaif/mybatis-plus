package com.study.mybatisplus.service.impl;

import com.study.mybatisplus.domain.User;
import com.study.mybatisplus.mapper.UserMapper;
import com.study.mybatisplus.service.UserService;
import com.study.mybatisplus.utils.Md5Util;
import com.study.mybatisplus.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByUserName(String username) {
        User u=userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username, String password) {
        //加密
        String md5String = Md5Util.getMD5String(password);
        //添加
        userMapper.add(username,md5String);
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map=ThreadLocalUtil.get();
        String username=(String) map.get("username");
        userMapper.updateAvatar(avatarUrl,username);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String,Object> map=ThreadLocalUtil.get();
        String username=(String) map.get("username");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),username);
    }
}
