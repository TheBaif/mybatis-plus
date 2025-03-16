package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mybatisplus.domain.ChildSign;
import com.study.mybatisplus.domain.ParentSign;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.dto.ChildSignUpdateRequest;
import com.study.mybatisplus.dto.SignUpdateRequest;
import com.study.mybatisplus.service.ChildsignService;
import com.study.mybatisplus.mapper.ChildSignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
* @author 14530
* @description 针对表【childsign】的数据库操作Service实现
* @createDate 2025-02-14 15:59:49
*/
@Service
public class ChildsignServiceImpl extends ServiceImpl<ChildSignMapper, ChildSign>
    implements ChildsignService{
    @Autowired
    private ChildSignMapper childsignMapper;
    @Override
    public void addChildSign(ChildSign childSign) {
        childsignMapper.insert(childSign);
    }

    @Override
    public void update(ChildSign childSign) {
        childsignMapper.update(childSign);
    }

    @Override
    public void delete(ChildSign childSign) {
        childsignMapper.delete(childSign);
    }
}




