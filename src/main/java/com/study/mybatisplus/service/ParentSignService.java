package com.study.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mybatisplus.domain.ParentSign;

public interface ParentSignService extends IService<ParentSign>{
    void addParentSign(ParentSign parentSign);
    void update(ParentSign parentSign);
    void delete(ParentSign parentSign);
}
