package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.common.SystemConstants;
import com.system.entity.SysUser;
import com.system.mapper.SysPermissionMapper;
import com.system.mapper.SysUserMapper;
import com.system.service.PermissionCacheService;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统服务根据用户ID加载账号状态与权限。
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysPermissionMapper sysPermissionMapper;
    @Resource
    private PermissionCacheService permissionCacheService;

    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        Long userId = Long.valueOf(userIdStr);
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (SystemConstants.USER_DISABLE.equals(user.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        List<GrantedAuthority> authorities = permissionCacheService.getUserPermissionKeys(userId,
                        () -> sysPermissionMapper.selectUserPermissionKeys(userId))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
        return new User(userId.toString(), user.getPassword(), authorities);
    }
}
