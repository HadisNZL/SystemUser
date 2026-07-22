package com.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.SysUser;
import com.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<List<SysUser>> userList() {
        List<SysUser> list = sysUserService.findUserList();
        return Result.success(list);
    }
    // MP分页接口
    @GetMapping("/page")
    public Result<Page<SysUser>> userPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        Page<SysUser> page = sysUserService.getUserPage(pageNum, pageSize);
        return Result.success(page);
    }
}
