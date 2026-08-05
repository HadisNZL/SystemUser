package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.common.ResultCode;
import com.system.entity.SysUser;
import com.system.mapper.SysPermissionMapper;
import com.system.mapper.SysUserMapper;
import com.system.service.InternalAuthorizationService;
import com.system.service.PermissionCacheService;
import com.system.vo.InternalUserAuthorizationVO;
import org.springframework.stereotype.Service;

/**
 * 从用户表和权限缓存加载内部授权信息。
 */
@Service
public class InternalAuthorizationServiceImpl implements InternalAuthorizationService {

    private final SysUserMapper sysUserMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final PermissionCacheService permissionCacheService;

    public InternalAuthorizationServiceImpl(SysUserMapper sysUserMapper,
                                            SysPermissionMapper sysPermissionMapper,
                                            PermissionCacheService permissionCacheService) {
        this.sysUserMapper = sysUserMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.permissionCacheService = permissionCacheService;
    }

    @Override
    public InternalUserAuthorizationVO getUserAuthorization(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        InternalUserAuthorizationVO vo = new InternalUserAuthorizationVO();
        vo.setUserId(user.getId());
        vo.setStatus(user.getStatus());
        vo.setPermissionKeys(permissionCacheService.getUserPermissionKeys(userId,
                () -> sysPermissionMapper.selectUserPermissionKeys(userId)));
        return vo;
    }
}
