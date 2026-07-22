package com.system.controller;

import com.system.common.PageResult;
import com.system.common.Result;
import com.system.entity.SysUser;
import com.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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
    public Result<PageResult<SysUser>> userPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        PageResult<SysUser> page = sysUserService.getUserPage(pageNum, pageSize);
        return Result.success(page);
    }

    // post http://localhost:8080/sys/user/add
    //{
    //    "username": "佟麟",
    //    "nickname": "tonglin",
    //    "phone": "13800138000",
    //    "email": "tonglin@qq.com"
    //}
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody SysUser user) {
        boolean success = sysUserService.saveUser(user);
        if (success) {
            return Result.success("新增用户成功");
        } else {
            return Result.fail("新增用户失败");
        }
    }

    // 前端通过 DELETE 方式请求，例如：/sys/user/delete/1
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        boolean success = sysUserService.deleteUserById(id);
        if (success) {
            return Result.success("删除用户成功");
        } else {
            return Result.fail("删除用户失败，ID可能不存在");
        }
    }
}
