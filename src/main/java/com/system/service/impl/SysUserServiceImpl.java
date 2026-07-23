package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.PageResult;
import com.system.convert.UserConvert;
import com.system.dto.UserSearchDTO;
import com.system.entity.SysUser;
import com.system.mapper.SysUserMapper;
import com.system.service.SysUserService;
import com.system.vo.UserPageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LambdaQueryWrapper用法
 * 业务需求	Lambda 写法
 * 等于	eq (实体：：字段，值)
 * 不等于	ne()
 * 大于	gt()
 * 大于等于	ge()
 * 小于	lt()
 * 小于等于	le()
 * 模糊查询	like()
 * 左模糊	likeLeft()
 * 右模糊	likeRight()
 * 包含范围	in()
 * 不在范围	notIn()
 * 为空	isNull()
 * 不为空	isNotNull()
 * 排序	orderByAsc / orderByDesc
 */

@Service
public class SysUserServiceImpl implements SysUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private UserConvert userConvert;

    @Override
    public List<SysUser> findUserList() {
        // 只查询未删除数据
//        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        //因为写了TableLogic字段，和配置了全局application.yaml就不需要下面这一行了
//        wrapper.eq(SysUser::getDeleteFlag, 0);
        return sysUserMapper.selectList(null);
    }

    @Override
    public PageResult<SysUser> getUserPage(Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        //因为写了TableLogic字段，就不需要下面这一行了
//        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(SysUser::getDeleteFlag, 0);
//        Page<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);
        Page<SysUser> userPage = sysUserMapper.selectPage(page, null);
        // 自动分页、自动统计总条数
        return PageResult.build(userPage.getTotal(), userPage.getRecords());
    }

    /**
     * LambdaQueryWrapper用法
     */
    @Override
    public PageResult<UserPageVO> searchUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 1.账号模糊查询 不为空才拼接条件
        wrapper.like(dto.getUsername() != null && !dto.getUsername().isEmpty(), SysUser::getUsername, dto.getUsername());
        // 2.状态精准查询
        wrapper.eq(dto.getStatus() != null, SysUser::getStatus, dto.getStatus());
        // 3.创建时间区间查询
        wrapper.ge(dto.getStartTime() != null, SysUser::getCreateTime, dto.getStartTime());
        wrapper.le(dto.getEndTime() != null, SysUser::getCreateTime, dto.getEndTime());
        // 4.默认按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);

        // 实体转VO
        // 使用 Stream 流配合 MapStruct 一行代码完成转换
        List<UserPageVO> voList = userPage.getRecords().stream()
                .map(userConvert::convert)
                .collect(Collectors.toList());

//        Page<UserPageVO> voPage = new Page<>();
//        voPage.setTotal(userPage.getTotal());
//        voPage.setCurrent(userPage.getCurrent());
//        voPage.setSize(userPage.getSize());
//        voPage.setRecords(voList);

        return PageResult.build(userPage.getTotal(), voList);
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
