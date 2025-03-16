package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mybatisplus.domain.ParentSign;
import com.study.mybatisplus.mapper.ParentSignMapper;
import com.study.mybatisplus.service.ParentSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentSignServiceImpl extends ServiceImpl<ParentSignMapper, ParentSign> implements ParentSignService {
    @Autowired
    ParentSignMapper parentSignMapper;
    @Override
    public void addParentSign(ParentSign parentSign) {
        parentSignMapper.insert(parentSign);
    }
    @Override
    public void update(ParentSign parentSign) {
        parentSignMapper.update(parentSign);
    }

    @Override
    public void delete(ParentSign parentSign) {
        parentSignMapper.delete(parentSign);
    }


}
