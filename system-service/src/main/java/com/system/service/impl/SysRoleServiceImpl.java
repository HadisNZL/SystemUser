package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.BusinessException;
import com.system.common.PageResult;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import com.system.dto.RoleAssignPermissionDTO;
import com.system.dto.RoleAddDTO;
import com.system.dto.RoleSearchDTO;
import com.system.dto.RoleStatusDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.entity.SysPermission;
import com.system.entity.SysRole;
import com.system.entity.SysRolePermission;
import com.system.mapper.SysPermissionMapper;
import com.system.mapper.SysRoleMapper;
import com.system.mapper.SysRolePermissionMapper;
import com.system.service.PermissionCacheService;
import com.system.service.SysRoleService;
import com.system.vo.MenuTreeVO;
import com.system.vo.RolePageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色管理业务实现。
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    private static final Long ROOT_PARENT_ID = 0L;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Resource
    private PermissionCacheService permissionCacheService;

    @Override
    public PageResult<RolePageVO> getRolePage(RoleSearchDTO dto, Integer pageNum, Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        Page<SysRole> rolePage = sysRoleMapper.selectPage(page, buildRoleSearchWrapper(dto));
        List<RolePageVO> voList = rolePage.getRecords().stream()
                .map(this::toRolePageVO)
                .collect(Collectors.toList());
        return PageResult.build(rolePage.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleAddDTO roleAddDTO) {
        checkRoleCodeUnique(roleAddDTO.getRoleCode(), null);
        SysRole role = toRole(roleAddDTO);
        if (role.getSort() == null) {
            role.setSort(0);
        }
        if (role.getStatus() == null) {
            role.setStatus(SystemConstants.USER_NORMAL);
        }
        int rows = sysRoleMapper.insert(role);
        if (rows <= 0) {
            throw new BusinessException("新增角色失败");
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(RoleStatusDTO roleStatusDTO) {
        SysRole dbRole = sysRoleMapper.selectById(roleStatusDTO.getId());
        if (dbRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
        dbRole.setStatus(roleStatusDTO.getStatus());
        int rows = sysRoleMapper.updateById(dbRole);
        if (rows <= 0) {
            throw new BusinessException("修改角色状态失败");
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    @Override
    public List<MenuTreeVO> getRolePermissions(Long id) {
        checkRoleExists(id);
        List<MenuTreeVO> permissions = sysPermissionMapper.selectPermissionsByRoleId(id).stream()
                .map(this::toMenuTreeVO)
                .collect(Collectors.toList());
        return buildTree(permissions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolePermissions(Long id, RoleAssignPermissionDTO roleAssignPermissionDTO) {
        checkRoleExists(id);
        List<Long> permissionIds = roleAssignPermissionDTO.getPermissionIds().stream()
                .distinct()
                .collect(Collectors.toList());
        checkPermissionsValid(permissionIds);

        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, id));

        for (Long permissionId : permissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(id);
            rolePermission.setPermissionId(permissionId);
            int rows = sysRolePermissionMapper.insert(rolePermission);
            if (rows <= 0) {
                throw new BusinessException("分配权限失败");
            }
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editRole(RoleUpdateDTO roleUpdateDTO) {
        SysRole dbRole = sysRoleMapper.selectById(roleUpdateDTO.getId());
        if (dbRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
        if (roleUpdateDTO.getRoleCode() != null && !roleUpdateDTO.getRoleCode().equals(dbRole.getRoleCode())) {
            checkRoleCodeUnique(roleUpdateDTO.getRoleCode(), roleUpdateDTO.getId());
        }
        updateRole(roleUpdateDTO, dbRole);
        int rows = sysRoleMapper.updateById(dbRole);
        if (rows <= 0) {
            throw new BusinessException("修改角色失败");
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        int rows = sysRoleMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    private List<MenuTreeVO> buildTree(List<MenuTreeVO> permissionList) {
        Map<Long, MenuTreeVO> permissionMap = permissionList.stream()
                .collect(Collectors.toMap(MenuTreeVO::getId, item -> item));
        return permissionList.stream()
                .filter(item -> {
                    Long parentId = item.getParentId() == null ? ROOT_PARENT_ID : item.getParentId();
                    if (ROOT_PARENT_ID.equals(parentId) || !permissionMap.containsKey(parentId)) {
                        return true;
                    }
                    permissionMap.get(parentId).getChildren().add(item);
                    return false;
                })
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<SysRole> buildRoleSearchWrapper(RoleSearchDTO dto) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            wrapper.like(dto.getRoleName() != null && !dto.getRoleName().isEmpty(), SysRole::getRoleName, dto.getRoleName());
            wrapper.like(dto.getRoleCode() != null && !dto.getRoleCode().isEmpty(), SysRole::getRoleCode, dto.getRoleCode());
        }
        return wrapper.orderByAsc(SysRole::getSort).orderByDesc(SysRole::getCreateTime);
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

    private SysRole toRole(RoleAddDTO dto) {
        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setSort(dto.getSort());
        role.setStatus(dto.getStatus());
        return role;
    }

    private void updateRole(RoleUpdateDTO dto, SysRole role) {
        if (dto.getRoleName() != null) {
            role.setRoleName(dto.getRoleName());
        }
        if (dto.getRoleCode() != null) {
            role.setRoleCode(dto.getRoleCode());
        }
        if (dto.getSort() != null) {
            role.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
    }

    private MenuTreeVO toMenuTreeVO(SysPermission permission) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setId(permission.getId());
        vo.setParentId(permission.getParentId());
        vo.setName(permission.getName());
        vo.setMenuPath(permission.getMenuPath());
        vo.setPermissionKey(permission.getPermissionKey());
        vo.setType(permission.getType());
        vo.setSort(permission.getSort());
        vo.setIcon(permission.getIcon());
        vo.setComponent(permission.getComponent());
        vo.setVisible(permission.getVisible());
        vo.setStatus(permission.getStatus());
        vo.setRedirect(permission.getRedirect());
        vo.setIsCache(permission.getIsCache());
        vo.setIsFrame(permission.getIsFrame());
        vo.setRemark(permission.getRemark());
        vo.setCreateTime(permission.getCreateTime());
        vo.setUpdateTime(permission.getUpdateTime());
        return vo;
    }

    private void checkRoleExists(Long id) {
        SysRole dbRole = sysRoleMapper.selectById(id);
        if (dbRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
    }

    private void checkPermissionsValid(List<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            return;
        }
        Long count = sysPermissionMapper.selectCount(new LambdaQueryWrapper<SysPermission>()
                .in(SysPermission::getId, permissionIds)
                .eq(SysPermission::getStatus, SystemConstants.USER_NORMAL));
        if (count == null || count != permissionIds.size()) {
            throw new BusinessException("存在无效或已禁用权限");
        }
    }

    private void checkRoleCodeUnique(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        wrapper.ne(excludeId != null, SysRole::getId, excludeId);
        Long count = sysRoleMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("角色标识已存在");
        }
    }
}
