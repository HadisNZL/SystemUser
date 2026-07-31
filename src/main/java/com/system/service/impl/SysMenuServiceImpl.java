package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.system.common.BusinessException;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import com.system.convert.MenuConvert;
import com.system.dto.MenuAddDTO;
import com.system.dto.MenuUpdateDTO;
import com.system.entity.SysPermission;
import com.system.mapper.SysPermissionMapper;
import com.system.service.PermissionCacheService;
import com.system.service.SysMenuService;
import com.system.util.SecurityUtil;
import com.system.vo.MenuTreeVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    private static final Long ROOT_PARENT_ID = 0L;
    private static final Integer TYPE_BUTTON = 3;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private MenuConvert menuConvert;

    @Resource
    private PermissionCacheService permissionCacheService;

    @Override
    public List<MenuTreeVO> getMenuTree() {
        List<SysPermission> permissions = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .orderByAsc(SysPermission::getParentId)
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId));
        List<MenuTreeVO> menuList = permissions.stream()
                .map(menuConvert::convertMenuTreeVO)
                .collect(Collectors.toList());
        return buildTree(menuList);
    }

    @Override
    public List<MenuTreeVO> getCurrentUserMenuTree() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<MenuTreeVO> menuList = sysPermissionMapper.selectCurrentUserMenus(userId).stream()
                .map(menuConvert::convertMenuTreeVO)
                .collect(Collectors.toList());
        return buildTree(menuList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(MenuAddDTO menuAddDTO) {
        checkParentExists(menuAddDTO.getParentId(), null);
        checkPermissionKeyRequired(menuAddDTO.getType(), menuAddDTO.getPermissionKey());
        checkPermissionKeyUnique(menuAddDTO.getPermissionKey(), null);
        SysPermission permission = menuConvert.convertMenuAddDTO(menuAddDTO);
        fillDefaultValue(permission);
        int rows = sysPermissionMapper.insert(permission);
        if (rows <= 0) {
            throw new BusinessException("新增菜单失败");
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editMenu(MenuUpdateDTO menuUpdateDTO) {
        SysPermission dbPermission = sysPermissionMapper.selectById(menuUpdateDTO.getId());
        if (dbPermission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "菜单不存在");
        }
        checkParentExists(menuUpdateDTO.getParentId(), menuUpdateDTO.getId());
        Integer finalType = menuUpdateDTO.getType() == null ? dbPermission.getType() : menuUpdateDTO.getType();
        String finalPermissionKey = menuUpdateDTO.getPermissionKey() == null ? dbPermission.getPermissionKey() : menuUpdateDTO.getPermissionKey();
        checkPermissionKeyRequired(finalType, finalPermissionKey);
        checkPermissionKeyUnique(menuUpdateDTO.getPermissionKey(), menuUpdateDTO.getId());
        menuConvert.updateEntityFromDTO(menuUpdateDTO, dbPermission);
        fillDefaultValue(dbPermission);
        int rows = sysPermissionMapper.updateById(dbPermission);
        if (rows <= 0) {
            throw new BusinessException("修改菜单失败");
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        Long childCount = sysPermissionMapper.selectCount(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("当前菜单存在子菜单，不能删除");
        }
        Long roleBindCount = sysPermissionMapper.countRolePermissionByPermissionId(id);
        if (roleBindCount != null && roleBindCount > 0) {
            throw new BusinessException("当前菜单已分配给角色，不能删除");
        }
        int rows = sysPermissionMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "菜单不存在");
        }
        permissionCacheService.clearAllUserPermissionCache();
    }

    private List<MenuTreeVO> buildTree(List<MenuTreeVO> menuList) {
        Map<Long, MenuTreeVO> menuMap = menuList.stream()
                .collect(Collectors.toMap(MenuTreeVO::getId, item -> item));
        return menuList.stream()
                .filter(item -> {
                    Long parentId = defaultParentId(item.getParentId());
                    if (ROOT_PARENT_ID.equals(parentId) || !menuMap.containsKey(parentId)) {
                        return true;
                    }
                    menuMap.get(parentId).getChildren().add(item);
                    return false;
                })
                .collect(Collectors.toList());
    }

    private void checkParentExists(Long parentId, Long currentId) {
        Long realParentId = defaultParentId(parentId);
        if (ROOT_PARENT_ID.equals(realParentId)) {
            return;
        }
        if (Objects.equals(realParentId, currentId)) {
            throw new BusinessException("父菜单不能选择自己");
        }
        SysPermission parent = sysPermissionMapper.selectById(realParentId);
        if (parent == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "父菜单不存在");
        }
        if (TYPE_BUTTON.equals(parent.getType())) {
            throw new BusinessException("按钮权限不能作为父菜单");
        }
        checkParentCycle(realParentId, currentId);
    }

    private void checkParentCycle(Long parentId, Long currentId) {
        if (currentId == null) {
            return;
        }
        Long nextParentId = parentId;
        while (!ROOT_PARENT_ID.equals(nextParentId)) {
            SysPermission parent = sysPermissionMapper.selectById(nextParentId);
            if (parent == null) {
                return;
            }
            if (Objects.equals(parent.getId(), currentId)) {
                throw new BusinessException("父菜单不能选择自己或自己的子菜单");
            }
            nextParentId = defaultParentId(parent.getParentId());
        }
    }

    private void checkPermissionKeyRequired(Integer type, String permissionKey) {
        if (TYPE_BUTTON.equals(type) && (permissionKey == null || permissionKey.isBlank())) {
            throw new BusinessException("按钮权限标识不能为空");
        }
    }

    private void checkPermissionKeyUnique(String permissionKey, Long excludeId) {
        if (permissionKey == null || permissionKey.isBlank()) {
            return;
        }
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermissionKey, permissionKey);
        wrapper.ne(excludeId != null, SysPermission::getId, excludeId);
        Long count = sysPermissionMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("权限标识已存在");
        }
    }

    private void fillDefaultValue(SysPermission permission) {
        permission.setParentId(defaultParentId(permission.getParentId()));
        if (permission.getPermissionKey() == null) {
            permission.setPermissionKey("");
        }
        if (permission.getMenuPath() == null) {
            permission.setMenuPath("");
        }
        if (permission.getSort() == null) {
            permission.setSort(0);
        }
        if (permission.getIcon() == null) {
            permission.setIcon("");
        }
        if (permission.getComponent() == null) {
            permission.setComponent("");
        }
        if (permission.getVisible() == null) {
            permission.setVisible(SystemConstants.USER_NORMAL);
        }
        if (permission.getStatus() == null) {
            permission.setStatus(SystemConstants.USER_NORMAL);
        }
        if (permission.getRedirect() == null) {
            permission.setRedirect("");
        }
        if (permission.getIsCache() == null) {
            permission.setIsCache(SystemConstants.USER_NORMAL);
        }
        if (permission.getIsFrame() == null) {
            permission.setIsFrame(SystemConstants.USER_DISABLE);
        }
        if (permission.getRemark() == null) {
            permission.setRemark("");
        }
    }

    private Long defaultParentId(Long parentId) {
        return parentId == null ? ROOT_PARENT_ID : parentId;
    }
}
