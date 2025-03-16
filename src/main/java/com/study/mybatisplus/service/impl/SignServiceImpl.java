package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.study.mybatisplus.domain.ChildSign;
import com.study.mybatisplus.domain.PageBean;
import com.study.mybatisplus.domain.Sign;
import com.study.mybatisplus.dto.SignUpdateRequest;
import com.study.mybatisplus.service.SignService;
import com.study.mybatisplus.mapper.SignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
* @author 14530
* @description 针对表【sign】的数据库操作Service实现
* @createDate 2025-02-14 19:33:24
*/
@Service
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign>
    implements SignService{

    @Autowired
    private SignMapper signMapper;
    @Override
    public void addSign(Sign sign) {
        signMapper.insert(sign);
    }

    @Override
    public void update(Sign sign) {
        signMapper.update(sign);
    }

    @Override
    public void delete(Sign sign) {
        signMapper.delete(sign);
    }

    @Override
    public List<Sign> list(Integer pageNum, Integer pageSize, Integer parentId, Integer childId) {
        // 参数校验
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);

        // 调用mapper获取数据
        List<Sign> signList = signMapper.list(parentId, childId);

        return signList;
    }

    @Override
    public List<Sign> searchSigns(String keyword) {
        QueryWrapper<Sign> query = new QueryWrapper<>();

        // 中文名称查询
        query.like("name", keyword);

        // 拼音查询（需要数据库字段）
        query.or().like("pinyin", keyword.toLowerCase());

        // 支持分词搜索的改进版
        /*String[] keywords = keyword.split("\\s+");
        for(String kw : keywords){
            query.and(q -> q
                .like("name", kw)
                .or()
                .like("pinyin", kw)
            );
        }*/

        return signMapper.selectList(query);
    }


}




