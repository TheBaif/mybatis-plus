package com.study.mybatisplus.service;

import com.study.mybatisplus.domain.ChildSign;
import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mybatisplus.domain.ParentSign;
import com.study.mybatisplus.dto.ChildSignUpdateRequest;

import java.util.List;

/**
* @author 14530
* @description 针对表【childsign】的数据库操作Service
* @createDate 2025-02-14 15:59:49
*/
public interface ChildsignService extends IService<ChildSign> {
    void addChildSign(ChildSign childSign);
    void update(ChildSign childSign);
    void delete(ChildSign childSign);
}
