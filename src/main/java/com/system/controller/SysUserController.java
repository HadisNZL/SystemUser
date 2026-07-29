package com.system.controller;

import com.system.common.PageResult;
import com.system.common.Result;
import com.system.common.SystemConstants;
import com.system.dto.UserAddDTO;
import com.system.dto.UserSearchDTO;
import com.system.dto.UserUpdateDTO;
import com.system.entity.SysUser;
import com.system.service.SysUserService;
import com.system.vo.UserPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * sys:user:list        获取用户列表
 * sys:user:search      获取用户分页列表
 * sys:user:add         新增用户
 * sys:user:edit        修改用户
 * sys:user:remove      逻辑删除用户
 * sys:user:physicalDel 管理员物理删除用户
 */

@Tag(name = "用户管理模块", description = "提供用户的增删改查及并发控制接口")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "获取用户列表", description = "用户列表的查询")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<List<SysUser>> userList() {
        List<SysUser> list = sysUserService.findUserList();
        return Result.success(list);
    }


    // MP分页接口 关联LambdaQueryWrapper条件查询
    //e.g.带dto任何字段都可以 GET http://localhost:8080/page?username=test&status=1&pageNum=2&pageSize=10
    @Operation(summary = "获取用户分页式列表", description = "分页式列表的获取")
    @GetMapping("/search_list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<PageResult<UserPageVO>> getUserList(UserSearchDTO dto, @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_NUM) Integer pageNum, @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_SIZE) Integer pageSize) {
        PageResult<UserPageVO> page = sysUserService.getUserPage(dto, pageNum, pageSize);
        return Result.success(page);
    }

    // post http://localhost:8080/sys/user/add
    //{
    //    "username": "佟麟",
    //    "nickname": "tonglin",
    //    "phone": "13800138000",
    //    "email": "tonglin@qq.com"
    //}
    @Operation(summary = "增加一个用户", description = "传入VO，增加一个对象")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:user:add')")
    public Result<String> addUser(@RequestBody UserAddDTO userAddDTO) {
        sysUserService.saveUser(userAddDTO);
        //失败都在serviceImpl中拦截，这里只处理成功
        return Result.success("新增用户成功");
    }

    //修改
    @Operation(summary = "修改用户信息", description = "通过传入VO修改，VO的属性选择性传入就可以，Id必传")
    @PostMapping("/modify")
    @PreAuthorize("hasAuthority('sys:user:edit')")
    public Result<Boolean> editUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        sysUserService.editUser(userUpdateDTO);
        return Result.success(true);
    }

    /**
     * 删除，根据有没有注解@TableLogic标识 ，MyBatis-Plus底层判断是逻辑删除还是物理删除
     * 请求示例：DELETE /sys/user/delete/1
     */
    @Operation(summary = "删除一个用户(逻辑删除)", description = "逻辑删除")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:user:remove')")
    public Result<String> deleteUser(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success("删除用户成功");
    }

    /**
     * 管理员临时 物理删除，不要外泄
     * 请求示例：DELETE /sys/user/delete_admin/1
     */
    @Operation(summary = "物理删除用户(管理员)", description = "物理删除")
    @DeleteMapping("/delete_admin/{id}")
    @PreAuthorize("hasAuthority('sys:user:physicalDel')")
    public Result<Boolean> adminPhysicalDeleteUser(@PathVariable Long id) {
        sysUserService.adminPhysicalDeleteUser(id);
        return Result.success(true);
    }
}
