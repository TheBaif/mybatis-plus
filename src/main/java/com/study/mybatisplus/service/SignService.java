package com.study.mybatisplus.service;

import com.study.mybatisplus.domain.PageBean;
import com.study.mybatisplus.domain.Sign;
import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mybatisplus.dto.SignUpdateRequest;

import java.util.List;

/**
* @author 14530
* @description 针对表【sign】的数据库操作Service
* @createDate 2025-02-14 19:33:24
*/
public interface SignService extends IService<Sign> {
    void addSign(Sign sign);
    void update(Sign sign);
    void delete(Sign sign);
    List<Sign> list(Integer pageNum, Integer pageSize, Integer parentId, Integer childId);

    List<Sign> searchSigns(String keyword);
}
