package com.system.controller;

import com.system.annotation.OperationLog;
import com.system.common.PageResult;
import com.system.common.Result;
import com.system.common.SystemConstants;
import com.system.dto.UserAddDTO;
import com.system.dto.UserAssignRoleDTO;
import com.system.dto.UserChangePasswordDTO;
import com.system.dto.UserResetPasswordDTO;
import com.system.dto.UserSearchDTO;
import com.system.dto.UserStatusDTO;
import com.system.dto.UserUpdateDTO;
import com.system.service.SysUserService;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import com.system.vo.UserImportResultVO;
import com.system.vo.UserPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 系统服务用户管理接口。
 */
@RestController
@RequestMapping("/system/user")
@Validated
@Tag(name = "用户管理模块", description = "提供用户查询、维护、状态控制、角色分配和导入导出接口")
public class SystemUserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "获取用户详情", description = "通过用户ID获取用户基本信息及已分配角色")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:detail')")
    public Result<UserDetailVO> getUserDetail(@PathVariable @Min(value = 1, message = "用户ID必须大于等于1") Long id) {
        return Result.success(sysUserService.getUserDetail(id));
    }

    @Operation(summary = "分页查询用户", description = "根据用户名、昵称和状态等条件分页查询用户")
    @GetMapping("/search_list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<PageResult<UserPageVO>> getUserList(@Valid UserSearchDTO dto,
                                                      @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_NUM) @Min(value = 1, message = "页码必须大于等于1") Integer pageNum,
                                                      @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_SIZE) @Min(value = 1, message = "每页条数必须大于等于1") @Max(value = 100, message = "每页条数不能超过100") Integer pageSize) {
        return Result.success(sysUserService.getUserPage(dto, pageNum, pageSize));
    }

    @Operation(summary = "导出用户列表", description = "根据查询条件导出用户Excel文件")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('sys:user:export')")
    public ResponseEntity<byte[]> exportUserExcel(@Valid UserSearchDTO dto) {
        return buildExcelResponse(sysUserService.exportUserExcel(dto), "用户列表.xlsx");
    }

    @Operation(summary = "下载用户导入模板", description = "下载批量导入用户所需的Excel模板")
    @GetMapping("/import-template")
    @PreAuthorize("hasAuthority('sys:user:import')")
    public ResponseEntity<byte[]> getUserImportTemplate() {
        return buildExcelResponse(sysUserService.getUserImportTemplate(), "用户导入模板.xlsx");
    }

    @Operation(summary = "导入用户", description = "上传Excel文件并批量导入用户，返回成功和失败明细")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('sys:user:import')")
    @OperationLog(module = "用户管理", operation = "导入用户")
    public Result<UserImportResultVO> importUserExcel(@RequestParam("file") MultipartFile file) {
        return Result.success(sysUserService.importUserExcel(file));
    }

    @Operation(summary = "新增用户", description = "创建用户并使用BCrypt加密保存初始密码")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:user:add')")
    @OperationLog(module = "用户管理", operation = "新增用户")
    public Result<String> addUser(@Valid @RequestBody UserAddDTO userAddDTO) {
        sysUserService.saveUser(userAddDTO);
        return Result.success("新增用户成功");
    }

    @Operation(summary = "修改用户", description = "根据用户ID修改可编辑的用户基本信息")
    @PostMapping("/modify")
    @PreAuthorize("hasAuthority('sys:user:edit')")
    @OperationLog(module = "用户管理", operation = "修改用户")
    public Result<Boolean> editUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        sysUserService.editUser(userUpdateDTO);
        return Result.success(true);
    }

    @Operation(summary = "修改用户状态", description = "启用或禁用指定用户账号")
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('sys:user:status')")
    @OperationLog(module = "用户管理", operation = "修改用户状态")
    public Result<Boolean> updateUserStatus(@Valid @RequestBody UserStatusDTO userStatusDTO) {
        sysUserService.updateUserStatus(userStatusDTO);
        return Result.success(true);
    }

    @Operation(summary = "重置用户密码", description = "由管理员重置指定用户的登录密码")
    @PutMapping("/reset-password")
    @PreAuthorize("hasAuthority('sys:user:resetPwd')")
    @OperationLog(module = "用户管理", operation = "重置用户密码")
    public Result<Boolean> resetPassword(@Valid @RequestBody UserResetPasswordDTO resetPasswordDTO) {
        sysUserService.resetPassword(resetPasswordDTO);
        return Result.success(true);
    }

    @Operation(summary = "修改当前用户密码", description = "当前登录用户校验旧密码后修改自己的密码")
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @OperationLog(module = "个人中心", operation = "修改当前用户密码")
    public Result<Boolean> changePassword(@Valid @RequestBody UserChangePasswordDTO changePasswordDTO) {
        sysUserService.changePassword(changePasswordDTO);
        return Result.success(true);
    }

    @Operation(summary = "查询用户角色", description = "通过用户ID查询已分配的角色列表")
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('sys:user:assignRole')")
    public Result<List<RolePageVO>> getUserRoles(@PathVariable @Min(value = 1, message = "用户ID必须大于等于1") Long id) {
        return Result.success(sysUserService.getUserRoles(id));
    }

    @Operation(summary = "给用户分配角色", description = "使用角色ID列表覆盖用户现有角色，空数组表示清空")
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('sys:user:assignRole')")
    @OperationLog(module = "用户管理", operation = "分配用户角色")
    public Result<Boolean> assignUserRoles(@PathVariable @Min(value = 1, message = "用户ID必须大于等于1") Long id,
                                           @Valid @RequestBody UserAssignRoleDTO userAssignRoleDTO) {
        sysUserService.assignUserRoles(id, userAssignRoleDTO);
        return Result.success(true);
    }

    @Operation(summary = "逻辑删除用户", description = "通过用户ID逻辑删除用户，保留数据库记录")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:user:remove')")
    @OperationLog(module = "用户管理", operation = "删除用户")
    public Result<String> deleteUser(@PathVariable @Min(value = 1, message = "用户ID必须大于等于1") Long id) {
        sysUserService.deleteUser(id);
        return Result.success("删除用户成功");
    }

    @Operation(summary = "物理删除用户", description = "管理员通过用户ID永久删除已逻辑删除的用户")
    @DeleteMapping("/delete_admin/{id}")
    @PreAuthorize("hasAuthority('sys:user:physicalDel')")
    @OperationLog(module = "用户管理", operation = "物理删除用户")
    public Result<Boolean> adminPhysicalDeleteUser(@PathVariable @Min(value = 1, message = "用户ID必须大于等于1") Long id) {
        sysUserService.adminPhysicalDeleteUser(id);
        return Result.success(true);
    }

    private ResponseEntity<byte[]> buildExcelResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(filename, StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(data);
    }
}
