package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.common.ResultCode;
import com.system.entity.SysRole;
import com.system.entity.SysUser;
import com.system.mapper.SysPermissionMapper;
import com.system.mapper.SysRoleMapper;
import com.system.mapper.SysUserMapper;
import com.system.service.SysProfileService;
import com.system.util.SecurityUtil;
import com.system.vo.ProfileVO;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 当前登录用户资料查询实现。
 */
@Service
public class SysProfileServiceImpl implements SysProfileService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public ProfileVO getCurrentProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }

        ProfileVO profile = new ProfileVO();
        profile.setUser(toUserDetailVO(user));
        profile.setRoles(sysRoleMapper.selectRolesByUserId(userId).stream().map(this::toRolePageVO).toList());
        profile.setPermissions(sysPermissionMapper.selectUserPermissionKeys(userId));
        return profile;
    }

    private UserDetailVO toUserDetailVO(SysUser user) {
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
