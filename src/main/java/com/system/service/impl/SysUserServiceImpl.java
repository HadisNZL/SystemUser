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

    /**
     * LambdaQueryWrapper用法
     */
    @Override
    public PageResult<UserPageVO> getUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize) {
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
        List<UserPageVO> voList = userPage.getRecords().stream().map(userConvert::convert).collect(Collectors.toList());

//        Page<UserPageVO> voPage = new Page<>();
//        voPage.setTotal(userPage.getTotal());
//        voPage.setCurrent(userPage.getCurrent());
//        voPage.setSize(userPage.getSize());
//        voPage.setRecords(voList);

        return PageResult.build(userPage.getTotal(), voList);
    }

    @Override
    public boolean saveUser(UserPageVO userVO) {
        // 1. 将前端的 VO/DTO 转换成数据库实体类 SysUser
        SysUser user = userConvert.convert(userVO);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 执行插入，并判断受影响行数是否大于 0
        //新增则version、createTime、updateTime、deleteFlag 全部自动填充，无需手动set
        //也包括乐观锁
        int rows = sysUserMapper.insert(user);
        return rows > 0;
    }

    @Override
    public boolean editUser(UserPageVO userPageVO) {
        if (userPageVO.getId() == null) {
            return false;
        }
        // 1. 先查库，拿到当前最新version
        SysUser dbUser = sysUserMapper.selectById(userPageVO.getId());
        if (dbUser == null) {
            return false;
        }
        // 2. 利用 MapStruct 直接合并：
        // 会自动把 userPageVO 里不为 null 的字段覆盖到 dbUser 上，
        // 同时完美保留了 dbUser 的 id、createTime 以及最重要的 version！
        userConvert.updateEntityFromVO(userPageVO, dbUser);
        // 3. 执行更新，MP 会自动带上 id 和 version 条件
        int rows = sysUserMapper.updateById(dbUser);
        // 更新行数为0 = 版本已变更，被别人抢先修改
        return rows > 0;
    }

    /**
     * 删除一条数据
     * MyBatis-Plus操作 deleteById(id)
     * 分两种情况
     * 1.表里面增加了@TableLogic标识
     * 则 执行逻辑删除
     * MyBatis-Plus 在框架底层做了全局拦截。当你调用 deleteById(id) 时，
     * 它会瞬间“变脸”，把原本要执行的 DELETE 语句，强行篡改并转换成一条 UPDATE 更新语句
     * 2.表里面没有@TableLogic标识
     * 则 执行物理删除
     * MyBatis-Plus 认准了这就是个普通字段，它在底层生成的 SQL 就是硬碰硬的物理删除
     */
    @Override
    public boolean deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        //这里是执行的逻辑删除 看上面注释
        int rows = sysUserMapper.deleteById(id);
        return rows > 0; // 影响行数大于 0 则代表删除成功
    }

    /**
     * 管理员临时 物理删除，不要外泄
     */
    @Override
    public boolean adminPhysicalDeleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        // 使用 Wrappers 构建条件，调用 delete(Wrapper) 方法
        // 这并不会直接执行真正的 DELETE FROM sys_user WHERE id = ?
        //  而是按照逻辑删除来了，所以只能手写删除
//        int rows = sysUserMapper.delete(new LambdaQueryWrapper<SysUser>()
//                .eq(SysUser::getId, id));
        int rows = sysUserMapper.physicalDeleteById(id);
        return rows > 0;// 真正从数据库物理删除，返回受影响行数
    }

}
