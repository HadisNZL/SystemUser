package com.system.service.impl;

import com.system.common.BusinessException;
import com.system.common.ResultCode;
import com.system.convert.RoleConvert;
import com.system.convert.UserConvert;
import com.system.entity.SysUser;
import com.system.mapper.SysPermissionMapper;
import com.system.mapper.SysRoleMapper;
import com.system.mapper.SysUserMapper;
import com.system.service.SysProfileService;
import com.system.util.SecurityUtil;
import com.system.vo.ProfileVO;
import com.system.vo.RolePageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysProfileServiceImpl implements SysProfileService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private UserConvert userConvert;

    @Resource
    private RoleConvert roleConvert;

    @Override
    public ProfileVO getCurrentProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        SysUser dbUser = sysUserMapper.selectById(userId);
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        List<RolePageVO> roles = sysRoleMapper.selectRolesByUserId(userId).stream()
                .map(roleConvert::convertRolePageVO)
                .collect(Collectors.toList());
        List<String> permissions = sysPermissionMapper.selectUserPermissionKeys(userId);

        ProfileVO profile = new ProfileVO();
        profile.setUser(userConvert.convertUserDetailVO(dbUser));
        profile.setRoles(roles);
        profile.setPermissions(permissions);
        return profile;
    }
}
