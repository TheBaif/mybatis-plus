package com.study.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.mybatisplus.domain.ParentSign;
import com.study.mybatisplus.domain.Result;
import com.study.mybatisplus.dto.ParentSignUpdateRequest;
import com.study.mybatisplus.service.ParentSignService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parentSign")
public class ParentSignController {
    @Autowired
    private ParentSignService parentSignService;

    @RequestMapping("/add")
    public Result addParentSign(@RequestBody ParentSign parentSign) {
        parentSignService.addParentSign(parentSign);
        return Result.success();
    }

    @GetMapping("/getById")
    public Result getById(@RequestParam int id) {
        ParentSign parentSign = parentSignService.getById(id);
        return Result.success(parentSign);
    }

    @GetMapping("/list")
    public Result <List<ParentSign>> list() {
        List<ParentSign> parentSigns=parentSignService.list();
        return Result.success(parentSigns);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated ParentSign parentSign) {
        parentSignService.update(parentSign);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestBody ParentSign parentSign) {
        parentSignService.delete(parentSign);
        return Result.success();
    }
}


