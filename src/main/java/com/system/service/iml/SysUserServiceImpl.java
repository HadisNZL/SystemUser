package com.system.service.iml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.PageResult;
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
        // 只查询未删除数据
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleteFlag, 0);
        return sysUserMapper.selectList(wrapper);
    }

    @Override
    public PageResult<SysUser> getUserPage(Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleteFlag, 0);
        Page<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);
        // 自动分页、自动统计总条数
        return PageResult.build(userPage.getTotal(),userPage.getRecords());
    }
}
