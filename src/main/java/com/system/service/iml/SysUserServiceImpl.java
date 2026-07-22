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
        return PageResult.build(userPage.getTotal(), userPage.getRecords());
    }

    @Override
    public boolean saveUser(SysUser user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 执行插入，并判断受影响行数是否大于 0
        int rows = sysUserMapper.insert(user);
        return rows > 0;
    }

    //    // “逻辑删除”（推荐的企业级做法）
//    @Override
//    public boolean deleteUserById(Long id) {
//        // 构造一个用户对象，把 id 传进去，并把 delete_flag 设为 1（代表已删除）
//        SysUser user = new SysUser();
//        user.setId(id);
//        user.setDeleteFlag(1);
//
//        // 调用 updateById，根据 ID 修改这一条数据的 delete_flag
//        int rows = sysUserMapper.updateById(user);
//        return rows > 0;
//    }
    //物理删除
    @Override
    public boolean deleteUserById(Long id) {
        // 直接调用 sysUserMapper 的物理删除方法，传入主键 ID
        int rows = sysUserMapper.deleteById(id);
        return rows > 0; // 影响行数大于 0 则代表删除成功
    }
}
