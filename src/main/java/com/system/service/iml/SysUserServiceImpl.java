package com.system.service.iml;

import com.system.entity.SysUser;
import com.system.mapper.SysUserMapper;
import com.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysUser> findUserList() {
        // 直接使用 MyBatis-Plus 自带的 selectList(null) 查询全部数据
        return sysUserMapper.selectList(null);
    }
}
