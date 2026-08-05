package com.system.controller;

import com.system.annotation.OperationLog;
import com.system.common.Result;
import com.system.dto.MenuAddDTO;
import com.system.dto.MenuUpdateDTO;
import com.system.service.SysMenuService;
import com.system.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单权限管理接口。
 */
@RestController
@RequestMapping("/system/menu")
@Validated
@Tag(name = "菜单权限模块", description = "提供目录、菜单和按钮权限的树形查询及维护接口")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    @Operation(summary = "获取完整菜单树", description = "获取包含目录、菜单和按钮权限的完整树形结构")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result<List<MenuTreeVO>> getMenuTree() {
        List<MenuTreeVO> menuTree = sysMenuService.getMenuTree();
        return Result.success(menuTree);
    }

    @Operation(summary = "获取当前用户菜单树", description = "获取当前登录用户可见的目录和菜单，不包含按钮权限")
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public Result<List<MenuTreeVO>> getCurrentUserMenuTree() {
        List<MenuTreeVO> menuTree = sysMenuService.getCurrentUserMenuTree();
        return Result.success(menuTree);
    }

    @Operation(summary = "新增菜单权限", description = "新增目录、菜单或按钮权限节点")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    @OperationLog(module = "菜单管理", operation = "新增菜单")
    public Result<String> addMenu(@Valid @RequestBody MenuAddDTO menuAddDTO) {
        sysMenuService.saveMenu(menuAddDTO);
        return Result.success("新增菜单成功");
    }

    @Operation(summary = "修改菜单权限", description = "根据权限ID修改目录、菜单或按钮权限节点")
    @PostMapping("/modify")
    @PreAuthorize("hasAuthority('sys:menu:edit')")
    @OperationLog(module = "菜单管理", operation = "修改菜单")
    public Result<Boolean> editMenu(@Valid @RequestBody MenuUpdateDTO menuUpdateDTO) {
        sysMenuService.editMenu(menuUpdateDTO);
        return Result.success(true);
    }

    @Operation(summary = "删除菜单权限", description = "删除没有子节点且未被角色引用的菜单权限")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:remove')")
    @OperationLog(module = "菜单管理", operation = "删除菜单")
    public Result<String> deleteMenu(@PathVariable @Min(value = 1, message = "菜单ID必须大于等于1") Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success("删除菜单成功");
    }
}
