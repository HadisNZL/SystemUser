package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.BusinessException;
import com.system.common.PageResult;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import com.system.convert.RoleConvert;
import com.system.convert.UserConvert;
import com.system.dto.UserAddDTO;
import com.system.dto.UserAssignRoleDTO;
import com.system.dto.UserChangePasswordDTO;
import com.system.dto.UserExcelDTO;
import com.system.dto.UserResetPasswordDTO;
import com.system.dto.UserSearchDTO;
import com.system.dto.UserStatusDTO;
import com.system.dto.UserUpdateDTO;
import com.system.entity.SysRole;
import com.system.entity.SysUser;
import com.system.entity.SysUserRole;
import com.system.mapper.SysRoleMapper;
import com.system.mapper.SysUserMapper;
import com.system.mapper.SysUserRoleMapper;
import com.system.service.SysUserService;
import com.system.util.SecurityUtil;
import com.system.util.UserExcelUtil;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import com.system.vo.UserImportFailureVO;
import com.system.vo.UserImportResultVO;
import com.system.vo.UserPageVO;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private UserConvert userConvert;

    @Resource
    private RoleConvert roleConvert;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public UserDetailVO getUserDetail(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        return userConvert.convertUserDetailVO(user);
    }

    /**
     * LambdaQueryWrapper用法
     */
    @Override
    public PageResult<UserPageVO> getUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = buildUserSearchWrapper(dto);
        Page<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);

        // 实体转VO
        // 使用 Stream 流配合 MapStruct 一行代码完成转换
        List<UserPageVO> voList = userPage.getRecords().stream().map(userConvert::convertUserPageVO).collect(Collectors.toList());

//        Page<UserPageVO> voPage = new Page<>();
//        voPage.setTotal(userPage.getTotal());
//        voPage.setCurrent(userPage.getCurrent());
//        voPage.setSize(userPage.getSize());
//        voPage.setRecords(voList);

        return PageResult.build(userPage.getTotal(), voList);
    }

    @Override
    public byte[] exportUserExcel(UserSearchDTO dto) {
        List<UserPageVO> users = sysUserMapper.selectList(buildUserSearchWrapper(dto)).stream()
                .map(userConvert::convertUserPageVO)
                .collect(Collectors.toList());
        return UserExcelUtil.writeUsers(users);
    }

    @Override
    public byte[] getUserImportTemplate() {
        return UserExcelUtil.buildTemplate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportResultVO importUserExcel(MultipartFile file) {
        List<UserExcelDTO> rows = UserExcelUtil.readUsers(file);
        UserImportResultVO result = new UserImportResultVO();
        Set<String> usernamesInExcel = new HashSet<>();

        for (UserExcelDTO row : rows) {
            List<String> errors = validateImportRow(row, usernamesInExcel);
            if (!errors.isEmpty()) {
                addImportFailure(result, row, String.join("；", errors));
                continue;
            }
            SysUser user = buildImportUser(row);
            int insertRows = sysUserMapper.insert(user);
            if (insertRows <= 0) {
                addImportFailure(result, row, "新增用户失败");
                continue;
            }
            result.setSuccessCount(result.getSuccessCount() + 1);
        }
        result.setFailureCount(result.getFailures().size());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)//事务保障原子性
    public void saveUser(UserAddDTO dto) {
        // 1. 将前端的 VO/DTO 转换成数据库实体类 SysUser
        SysUser user = userConvert.convertUserAddDTO(dto);
        // 建议：前端不传密码时业务抛出提示，不推荐后端硬编码默认密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (user.getStatus() == null) {
            user.setStatus(SystemConstants.USER_NORMAL);
        }
        // Bcrypt加密存入数据库
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 执行插入，并判断受影响行数是否大于 0
        //新增则version、createTime、updateTime、deleteFlag 全部自动填充，无需手动set
        //也包括乐观锁
        int rows = sysUserMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("新增用户失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)//事务保障原子性
    public void editUser(UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        // 1. 先查库，拿到当前最新version
        SysUser dbUser = sysUserMapper.selectById(userUpdateDTO.getId());
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        // 2. 利用 MapStruct 直接合并：
        // 会自动把 userUpdateDTO 里不为 null 的字段覆盖到 dbUser 上，
        // 同时完美保留了 dbUser 的 id、createTime 以及最重要的 version！
        userConvert.updateEntityFromVO(userUpdateDTO, dbUser);
        // 3. 执行更新，MP 会自动带上 id 和 version 条件
        int rows = sysUserMapper.updateById(dbUser);
        // 更新行数为0 = 版本已变更，被别人抢先修改
        if (rows <= 0) {
            // 乐观锁冲突提示
            throw new BusinessException(ResultCode.OPTIMISTIC_LOCK_ERROR, SystemConstants.OPTIMISTIC_LOCK_MSG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(UserStatusDTO userStatusDTO) {
        SysUser dbUser = sysUserMapper.selectById(userStatusDTO.getId());
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        dbUser.setStatus(userStatusDTO.getStatus());
        int rows = sysUserMapper.updateById(dbUser);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.OPTIMISTIC_LOCK_ERROR, SystemConstants.OPTIMISTIC_LOCK_MSG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(UserResetPasswordDTO resetPasswordDTO) {
        SysUser dbUser = sysUserMapper.selectById(resetPasswordDTO.getId());
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        dbUser.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        int rows = sysUserMapper.updateById(dbUser);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.OPTIMISTIC_LOCK_ERROR, SystemConstants.OPTIMISTIC_LOCK_MSG);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserChangePasswordDTO changePasswordDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        SysUser dbUser = sysUserMapper.selectById(userId);
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), dbUser.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), dbUser.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        dbUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        int rows = sysUserMapper.updateById(dbUser);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.OPTIMISTIC_LOCK_ERROR, SystemConstants.OPTIMISTIC_LOCK_MSG);
        }
    }

    @Override
    public List<RolePageVO> getUserRoles(Long id) {
        checkUserExists(id);
        return sysRoleMapper.selectRolesByUserId(id).stream()
                .map(roleConvert::convertRolePageVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(Long id, UserAssignRoleDTO userAssignRoleDTO) {
        checkUserExists(id);
        List<Long> roleIds = userAssignRoleDTO.getRoleIds().stream()
                .distinct()
                .collect(Collectors.toList());
        checkRolesValid(roleIds);

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));

        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(id);
            userRole.setRoleId(roleId);
            int rows = sysUserRoleMapper.insert(userRole);
            if (rows <= 0) {
                throw new BusinessException("分配角色失败");
            }
        }
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        //这里是执行的逻辑删除 看上面注释
        int rows = sysUserMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException("删除用户失败，ID不存在");
        }
    }

    /**
     * 管理员临时 物理删除，不要外泄
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminPhysicalDeleteUser(Long id) {
        if (id == null) {
            throw new BusinessException("用户ID不能为空");
        }
        // 使用 Wrappers 构建条件，调用 delete(Wrapper) 方法
        // 这并不会直接执行真正的 DELETE FROM sys_user WHERE id = ?
        //  而是按照逻辑删除来了，所以只能手写删除
//        int rows = sysUserMapper.delete(new LambdaQueryWrapper<SysUser>()
//                .eq(SysUser::getId, id));
        int rows = sysUserMapper.physicalDeleteById(id);
        if (rows <= 0) {
            throw new BusinessException("物理删除失败，ID不存在");
        }
    }

    private void checkUserExists(Long id) {
        SysUser dbUser = sysUserMapper.selectById(id);
        if (dbUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "用户不存在");
        }
    }

    private void checkRolesValid(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        Long count = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, SystemConstants.USER_NORMAL));
        if (count == null || count != roleIds.size()) {
            throw new BusinessException("存在无效或已禁用角色");
        }
    }

    private LambdaQueryWrapper<SysUser> buildUserSearchWrapper(UserSearchDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (dto == null) {
            return wrapper.orderByDesc(SysUser::getCreateTime);
        }
        wrapper.like(dto.getUsername() != null && !dto.getUsername().isEmpty(), SysUser::getUsername, dto.getUsername());
        wrapper.eq(dto.getStatus() != null, SysUser::getStatus, dto.getStatus());
        wrapper.ge(dto.getStartTime() != null, SysUser::getCreateTime, dto.getStartTime());
        wrapper.le(dto.getEndTime() != null, SysUser::getCreateTime, dto.getEndTime());
        wrapper.orderByDesc(SysUser::getCreateTime);
        return wrapper;
    }

    private List<String> validateImportRow(UserExcelDTO row, Set<String> usernamesInExcel) {
        List<String> errors = new ArrayList<>();
        if (row.getUsername() == null || row.getUsername().isBlank()) {
            errors.add("账号不能为空");
        } else {
            if (row.getUsername().length() > 30) {
                errors.add("账号长度不能超过30个字符");
            }
            if (!usernamesInExcel.add(row.getUsername())) {
                errors.add("Excel内账号重复");
            }
            Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, row.getUsername()));
            if (count != null && count > 0) {
                errors.add("账号已存在");
            }
        }
        if (row.getPassword() == null || row.getPassword().isBlank()) {
            errors.add("密码不能为空");
        } else if (row.getPassword().length() < 6 || row.getPassword().length() > 20) {
            errors.add("密码长度必须在6到20个字符之间");
        }
        if (row.getNickname() == null || row.getNickname().isBlank()) {
            errors.add("昵称不能为空");
        } else if (row.getNickname().length() > 50) {
            errors.add("昵称长度不能超过50个字符");
        }
        if (row.getPhone() != null && !row.getPhone().isBlank() && !row.getPhone().matches(PHONE_REGEX)) {
            errors.add("手机号格式不正确");
        }
        if (row.getEmail() != null && !row.getEmail().isBlank() && !row.getEmail().matches(EMAIL_REGEX)) {
            errors.add("邮箱格式不正确");
        }
        if (parseImportStatus(row.getStatus()) == null) {
            errors.add("状态只能是0或1");
        }
        return errors;
    }

    private SysUser buildImportUser(UserExcelDTO row) {
        SysUser user = new SysUser();
        user.setUsername(row.getUsername());
        user.setPassword(passwordEncoder.encode(row.getPassword()));
        user.setNickname(row.getNickname());
        user.setPhone(row.getPhone());
        user.setEmail(row.getEmail());
        user.setStatus(parseImportStatus(row.getStatus()));
        return user;
    }

    private Integer parseImportStatus(String status) {
        if (status == null || status.isBlank()) {
            return SystemConstants.USER_NORMAL;
        }
        if ("1".equals(status) || "正常".equals(status)) {
            return SystemConstants.USER_NORMAL;
        }
        if ("0".equals(status) || "禁用".equals(status)) {
            return SystemConstants.USER_DISABLE;
        }
        return null;
    }

    private void addImportFailure(UserImportResultVO result, UserExcelDTO row, String reason) {
        UserImportFailureVO failure = new UserImportFailureVO();
        failure.setRowNum(row.getRowNum());
        failure.setUsername(row.getUsername());
        failure.setReason(reason);
        result.getFailures().add(failure);
    }

}
