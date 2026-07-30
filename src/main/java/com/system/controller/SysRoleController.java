package com.system.controller;

import com.system.common.PageResult;
import com.system.common.Result;
import com.system.common.SystemConstants;
import com.system.annotation.OperationLog;
import com.system.dto.RoleAssignPermissionDTO;
import com.system.dto.RoleAddDTO;
import com.system.dto.RoleSearchDTO;
import com.system.dto.RoleStatusDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.service.SysRoleService;
import com.system.vo.MenuTreeVO;
import com.system.vo.RolePageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "角色管理模块", description = "提供角色的分页查询、新增、修改、删除接口")
@RestController
@RequestMapping("/sys/role")
@Validated
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    @Operation(summary = "获取角色分页列表", description = "角色分页列表的获取")
    @GetMapping("/search_list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result<PageResult<RolePageVO>> getRoleList(@Valid RoleSearchDTO dto,
                                                      @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_NUM) @Min(value = 1, message = "页码必须大于等于1") Integer pageNum,
                                                      @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_SIZE) @Min(value = 1, message = "每页条数必须大于等于1") @Max(value = 100, message = "每页条数不能超过100") Integer pageSize) {
        PageResult<RolePageVO> page = sysRoleService.getRolePage(dto, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "新增角色", description = "新增一个角色")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:role:add')")
    @OperationLog(module = "角色管理", operation = "新增角色")
    public Result<String> addRole(@Valid @RequestBody RoleAddDTO roleAddDTO) {
        sysRoleService.saveRole(roleAddDTO);
        return Result.success("新增角色成功");
    }

    @Operation(summary = "修改角色", description = "修改角色基础信息")
    @PostMapping("/modify")
    @PreAuthorize("hasAuthority('sys:role:edit')")
    @OperationLog(module = "角色管理", operation = "修改角色")
    public Result<Boolean> editRole(@Valid @RequestBody RoleUpdateDTO roleUpdateDTO) {
        sysRoleService.editRole(roleUpdateDTO);
        return Result.success(true);
    }

    @Operation(summary = "修改角色状态", description = "启用或禁用角色")
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('sys:role:status')")
    @OperationLog(module = "角色管理", operation = "修改角色状态")
    public Result<Boolean> updateRoleStatus(@Valid @RequestBody RoleStatusDTO roleStatusDTO) {
        sysRoleService.updateRoleStatus(roleStatusDTO);
        return Result.success(true);
    }

    @Operation(summary = "查询角色权限", description = "查询角色已绑定的菜单权限树")
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('sys:role:assignPermission')")
    public Result<List<MenuTreeVO>> getRolePermissions(@PathVariable @Min(value = 1, message = "角色ID必须大于等于1") Long id) {
        List<MenuTreeVO> permissions = sysRoleService.getRolePermissions(id);
        return Result.success(permissions);
    }

    @Operation(summary = "给角色分配权限", description = "传入权限ID列表，空数组表示清空角色权限")
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('sys:role:assignPermission')")
    @OperationLog(module = "角色管理", operation = "分配角色权限")
    public Result<Boolean> assignRolePermissions(@PathVariable @Min(value = 1, message = "角色ID必须大于等于1") Long id,
                                                 @Valid @RequestBody RoleAssignPermissionDTO roleAssignPermissionDTO) {
        sysRoleService.assignRolePermissions(id, roleAssignPermissionDTO);
        return Result.success(true);
    }

    @Operation(summary = "删除角色", description = "逻辑删除角色")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:role:remove')")
    @OperationLog(module = "角色管理", operation = "删除角色")
    public Result<String> deleteRole(@PathVariable @Min(value = 1, message = "角色ID必须大于等于1") Long id) {
        sysRoleService.deleteRole(id);
        return Result.success("删除角色成功");
    }
}
