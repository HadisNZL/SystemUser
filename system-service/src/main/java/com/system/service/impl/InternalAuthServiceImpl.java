package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.system.common.BusinessException;
import com.system.common.ResultCode;
import com.system.entity.SysUser;
import com.system.mapper.SysUserMapper;
import com.system.service.InternalAuthService;
import com.system.vo.InternalLoginUserVO;
import org.springframework.stereotype.Service;

/**
 * 从系统用户表读取登录校验信息。
 */
@Service
public class InternalAuthServiceImpl implements InternalAuthService {

    private final SysUserMapper sysUserMapper;

    public InternalAuthServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public InternalLoginUserVO getLoginUser(String username) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_FAIL);
        }
        InternalLoginUserVO vo = new InternalLoginUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPassword(user.getPassword());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
