package com.study.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.ChildSign;
import com.study.mybatisplus.domain.ParentSign;
import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.dto.ChildSignUpdateRequest;
import com.study.mybatisplus.service.ChildsignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/childSign")
public class ChildSignController {
    @Autowired
    private ChildsignService childsignService;

    @RequestMapping("/add")
    public Result addChildSign(@RequestBody ChildSign childSign) {
        childsignService.addChildSign(childSign);
        return Result.success();
    }

    @GetMapping("/getById")
    public Result getById(@RequestParam int id) {
        ChildSign childSign = childsignService.getById(id);
        return Result.success(childSign);
    }

    @GetMapping("/list")
    public Result<List<ChildSign>> list() {
        List<ChildSign> childSigns = childsignService.list();
        return Result.success(childSigns);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated ChildSign childSign) {
        childsignService.update(childSign);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestBody ChildSign childSign) {
        childsignService.delete(childSign);
        return Result.success();
    }
}
