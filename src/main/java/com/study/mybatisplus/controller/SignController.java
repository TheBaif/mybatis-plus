package com.study.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.study.mybatisplus.domain.*;
import com.study.mybatisplus.dto.SignUpdateRequest;
import com.study.mybatisplus.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sign")
public class SignController {
    @Autowired
    private SignService signService;

    @RequestMapping("/add")
    public Result addSign(@RequestBody Sign sign) {
        signService.addSign(sign);
        return Result.success();
    }
    @GetMapping("/getById")
    public Result getById(@RequestParam int id) {
        Sign sign = signService.getById(id);
        return Result.success(sign);
    }
    @PutMapping("/update")
    public Result update(@RequestBody @Validated Sign sign) {
        signService.update(sign);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestBody Sign sign) {
        signService.delete(sign);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer parentId,
            @RequestParam(required = false) Integer childId) {

        // 获取手语列表
        List<Sign> signList = signService.list(pageNum, pageSize, parentId, childId);

        // 使用PageInfo获取分页信息
        PageInfo<Sign> pageInfo = new PageInfo<>(signList);

        // 构建包含数据和分页信息的响应
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("records", signList);
        resultMap.put("total", pageInfo.getTotal());
        resultMap.put("pages", pageInfo.getPages());
        resultMap.put("current", pageInfo.getPageNum());
        resultMap.put("size", pageInfo.getPageSize());

        return Result.success(resultMap);
    }
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<Sign> results = signService.searchSigns(keyword);
        return Result.success(results);
    }
    @GetMapping("/detail/{id}")
    public Result getSignDetail(@PathVariable Integer id) {
        if (id == null) {
            return Result.error("ID参数不能为空");
        }

        Sign sign = signService.getById(id);
        if (sign == null) {
            return Result.error("未找到相关手语信息");
        }

        return Result.success(sign);
    }
}

