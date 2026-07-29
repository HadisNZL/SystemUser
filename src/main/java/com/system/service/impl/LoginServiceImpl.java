package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.system.common.BusinessException;
import com.system.common.SystemConstants;
import com.system.dto.LoginDTO;
import com.system.entity.SysUser;
import com.system.mapper.SysUserMapper;
import com.system.service.LoginService;
import com.system.util.JwtUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录实现
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public String login(LoginDTO loginDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("账号不存在");
        }
        if (SystemConstants.USER_DISABLE.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        // 签发令牌
        return jwtUtil.generateToken(user.getId());
    }

}
