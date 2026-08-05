package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.BusinessException;
import com.system.common.PageResult;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import com.system.dto.UserAddDTO;
import com.system.dto.UserAssignRoleDTO;
import com.system.dto.UserChangePasswordDTO;
import com.system.dto.UserExcelDTO;
import com.system.dto.UserResetPasswordDTO;
import com.system.dto.UserSearchDTO;
import com.system.dto.UserStatusDTO;
import com.system.dto.UserUpdateDTO;
import com.system.entity.SysRole;
import com.system.entity.SysUser;
import com.system.entity.SysUserRole;
import com.system.mapper.SysRoleMapper;
import com.system.mapper.SysUserMapper;
import com.system.mapper.SysUserRoleMapper;
import com.system.service.PermissionCacheService;
import com.system.service.SysUserService;
import com.system.util.SecurityUtil;
import com.system.util.UserExcelUtil;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import com.system.vo.UserImportFailureVO;
import com.system.vo.UserImportResultVO;
import com.system.vo.UserPageVO;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户管理业务实现。
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private PermissionCacheService permissionCacheService;

    @Override
    public UserDetailVO getUserDetail(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        return toDetailVO(user);
    }

    @Override
    public PageResult<UserPageVO> getUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        Page<SysUser> userPage = sysUserMapper.selectPage(page, buildUserSearchWrapper(dto));
        return PageResult.build(userPage.getTotal(), userPage.getRecords().stream().map(this::toPageVO).toList());
    }

    @Override
    public byte[] exportUserExcel(UserSearchDTO dto) {
        List<UserPageVO> users = sysUserMapper.selectList(buildUserSearchWrapper(dto)).stream()
                .map(this::toPageVO)
                .toList();
        return UserExcelUtil.writeUsers(users);
    }

    @Override
    public byte[] getUserImportTemplate() {
        return UserExcelUtil.buildTemplate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportResultVO importUserExcel(MultipartFile file) {
        List<UserExcelDTO> rows = UserExcelUtil.readUsers(file);
        UserImportResultVO result = new UserImportResultVO();
        Set<String> usernamesInExcel = new HashSet<>();

        for (UserExcelDTO row : rows) {
            List<String> errors = validateImportRow(row, usernamesInExcel);
            if (!errors.isEmpty()) {
                addImportFailure(result, row, String.join("；", errors));
                continue;
            }
            SysUser user = buildImportUser(row);
            if (sysUserMapper.insert(user) <= 0) {
                addImportFailure(result, row, "新增用户失败");
                continue;
            }
            result.setSuccessCount(result.getSuccessCount() + 1);
        }
        result.setFailureCount(result.getFailures().size());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(UserAddDTO dto) {
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus() == null ? SystemConstants.USER_NORMAL : dto.getStatus());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (sysUserMapper.insert(user) <= 0) {
            throw new BusinessException("新增用户失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editUser(UserUpdateDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        SysUser dbUser = sysUserMapper.selectById(dto.getId());
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        mergeUserUpdate(dto, dbUser);
        updateByIdWithOptimisticLock(dbUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(UserStatusDTO dto) {
        SysUser dbUser = sysUserMapper.selectById(dto.getId());
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        dbUser.setStatus(dto.getStatus());
        updateByIdWithOptimisticLock(dbUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(UserResetPasswordDTO dto) {
        SysUser dbUser = sysUserMapper.selectById(dto.getId());
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        dbUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        updateByIdWithOptimisticLock(dbUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserChangePasswordDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        SysUser dbUser = sysUserMapper.selectById(userId);
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), dbUser.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), dbUser.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        dbUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        updateByIdWithOptimisticLock(dbUser);
    }

    @Override
    public List<RolePageVO> getUserRoles(Long id) {
        checkUserExists(id);
        return sysRoleMapper.selectRolesByUserId(id).stream().map(this::toRolePageVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(Long id, UserAssignRoleDTO dto) {
        checkUserExists(id);
        List<Long> roleIds = dto.getRoleIds().stream().distinct().toList();
        checkRolesValid(roleIds);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(id);
            userRole.setRoleId(roleId);
            if (sysUserRoleMapper.insert(userRole) <= 0) {
                throw new BusinessException("分配角色失败");
            }
        }
        permissionCacheService.clearUserPermissionCache(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (sysUserMapper.deleteById(id) <= 0) {
            throw new BusinessException("删除用户失败，ID不存在");
        }
        permissionCacheService.clearUserPermissionCache(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminPhysicalDeleteUser(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (sysUserMapper.physicalDeleteById(id) <= 0) {
            throw new BusinessException("物理删除失败，ID不存在");
        }
        permissionCacheService.clearUserPermissionCache(id);
    }

    private void updateByIdWithOptimisticLock(SysUser dbUser) {
        if (sysUserMapper.updateById(dbUser) <= 0) {
            throw new BusinessException(ResultCode.OPTIMISTIC_LOCK_ERROR, SystemConstants.OPTIMISTIC_LOCK_MSG);
        }
    }

    private void checkUserExists(Long id) {
        if (sysUserMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
    }

    private void checkRolesValid(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        Long count = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, SystemConstants.USER_NORMAL));
        if (count == null || count != roleIds.size()) {
            throw new BusinessException("存在无效或已禁用角色");
        }
    }

    private LambdaQueryWrapper<SysUser> buildUserSearchWrapper(UserSearchDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (dto == null) {
            return wrapper.orderByDesc(SysUser::getCreateTime);
        }
        wrapper.like(dto.getUsername() != null && !dto.getUsername().isEmpty(), SysUser::getUsername, dto.getUsername());
        wrapper.eq(dto.getStatus() != null, SysUser::getStatus, dto.getStatus());
        wrapper.ge(dto.getStartTime() != null, SysUser::getCreateTime, dto.getStartTime());
        wrapper.le(dto.getEndTime() != null, SysUser::getCreateTime, dto.getEndTime());
        wrapper.orderByDesc(SysUser::getCreateTime);
        return wrapper;
    }

    private void mergeUserUpdate(UserUpdateDTO dto, SysUser dbUser) {
        if (dto.getNickname() != null) {
            dbUser.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            dbUser.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            dbUser.setEmail(dto.getEmail());
        }
        if (dto.getStatus() != null) {
            dbUser.setStatus(dto.getStatus());
        }
    }

    private List<String> validateImportRow(UserExcelDTO row, Set<String> usernamesInExcel) {
        List<String> errors = new ArrayList<>();
        if (row.getUsername() == null || row.getUsername().isBlank()) {
            errors.add("账号不能为空");
        } else {
            if (row.getUsername().length() > 30) {
                errors.add("账号长度不能超过30个字符");
            }
            if (!usernamesInExcel.add(row.getUsername())) {
                errors.add("Excel内账号重复");
            }
            Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, row.getUsername()));
            if (count != null && count > 0) {
                errors.add("账号已存在");
            }
        }
        if (row.getPassword() == null || row.getPassword().isBlank()) {
            errors.add("密码不能为空");
        } else if (row.getPassword().length() < 6 || row.getPassword().length() > 20) {
            errors.add("密码长度必须在6到20个字符之间");
        }
        if (row.getNickname() == null || row.getNickname().isBlank()) {
            errors.add("昵称不能为空");
        } else if (row.getNickname().length() > 50) {
            errors.add("昵称长度不能超过50个字符");
        }
        if (row.getPhone() != null && !row.getPhone().isBlank() && !row.getPhone().matches(PHONE_REGEX)) {
            errors.add("手机号格式不正确");
        }
        if (row.getEmail() != null && !row.getEmail().isBlank() && !row.getEmail().matches(EMAIL_REGEX)) {
            errors.add("邮箱格式不正确");
        }
        if (parseImportStatus(row.getStatus()) == null) {
            errors.add("状态只能是0或1");
        }
        return errors;
    }

    private SysUser buildImportUser(UserExcelDTO row) {
        SysUser user = new SysUser();
        user.setUsername(row.getUsername());
        user.setPassword(passwordEncoder.encode(row.getPassword()));
        user.setNickname(row.getNickname());
        user.setPhone(row.getPhone());
        user.setEmail(row.getEmail());
        user.setStatus(parseImportStatus(row.getStatus()));
        return user;
    }

    private Integer parseImportStatus(String status) {
        if (status == null || status.isBlank()) {
            return SystemConstants.USER_NORMAL;
        }
        if ("1".equals(status) || "正常".equals(status)) {
            return SystemConstants.USER_NORMAL;
        }
        if ("0".equals(status) || "禁用".equals(status)) {
            return SystemConstants.USER_DISABLE;
        }
        return null;
    }

    private void addImportFailure(UserImportResultVO result, UserExcelDTO row, String reason) {
        UserImportFailureVO failure = new UserImportFailureVO();
        failure.setRowNum(row.getRowNum());
        failure.setUsername(row.getUsername());
        failure.setReason(reason);
        result.getFailures().add(failure);
    }

    private UserDetailVO toDetailVO(SysUser user) {
        UserDetailVO vo = new UserDetailVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }

    private UserPageVO toPageVO(SysUser user) {
        UserPageVO vo = new UserPageVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    private RolePageVO toRolePageVO(SysRole role) {
        RolePageVO vo = new RolePageVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setSort(role.getSort());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }
}
