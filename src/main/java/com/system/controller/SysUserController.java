package com.system.controller;

import com.system.common.PageResult;
import com.system.common.Result;
import com.system.dto.UserSearchDTO;
import com.system.entity.SysUser;
import com.system.service.SysUserService;
import com.system.vo.UserPageVO;
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

    // MP分页接口 关联LambdaQueryWrapper条件查询
    //e.g.带dto任何字段都可以 GET http://localhost:8080/page?username=test&status=1&pageNum=2&pageSize=10
    @GetMapping("/search_list")
    public Result<PageResult<UserPageVO>> getUser(UserSearchDTO dto, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
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
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody UserPageVO user) {
        boolean success = sysUserService.saveUser(user);
        if (success) {
            return Result.success("新增用户成功");
        } else {
            return Result.fail("新增用户失败");
        }
    }

    //修改
    @PostMapping("/modify")
    public Result<Boolean> editUser(@RequestBody UserPageVO userPageVO) {
        boolean success = sysUserService.editUser(userPageVO);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("数据已被其他人修改或并发冲突，请刷新后重试");
        }
    }

    /**
     * 删除，根据有没有注解@TableLogic标识 ，MyBatis-Plus底层判断是逻辑删除还是物理删除
     * 请求示例：DELETE /sys/user/delete/1
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        boolean success = sysUserService.deleteUser(id);
        return success ? Result.success("删除用户成功") : Result.fail("删除用户失败，ID可能不存在");
    }

    /**
     * 管理员临时 物理删除，不要外泄
     * 请求示例：DELETE /sys/user/delete_admin/1
     */
    @DeleteMapping("/delete_admin/{id}")
    public Result<Boolean> adminPhysicalDeleteUser(@PathVariable Long id) {
        boolean success = sysUserService.adminPhysicalDeleteUser(id);
        return success ? Result.success(true) : Result.fail("管理员临时 物理删除失败");
    }
}
