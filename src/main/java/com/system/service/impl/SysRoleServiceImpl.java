package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.BusinessException;
import com.system.common.PageResult;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import com.system.convert.RoleConvert;
import com.system.dto.RoleAddDTO;
import com.system.dto.RoleSearchDTO;
import com.system.dto.RoleStatusDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.entity.SysRole;
import com.system.mapper.SysRoleMapper;
import com.system.service.SysRoleService;
import com.system.vo.RolePageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private RoleConvert roleConvert;

    @Override
    public PageResult<RolePageVO> getRolePage(RoleSearchDTO dto, Integer pageNum, Integer pageSize) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(dto.getRoleName() != null && !dto.getRoleName().isEmpty(), SysRole::getRoleName, dto.getRoleName());
        wrapper.like(dto.getRoleCode() != null && !dto.getRoleCode().isEmpty(), SysRole::getRoleCode, dto.getRoleCode());
        wrapper.orderByAsc(SysRole::getSort).orderByDesc(SysRole::getCreateTime);
        Page<SysRole> rolePage = sysRoleMapper.selectPage(page, wrapper);
        List<RolePageVO> voList = rolePage.getRecords().stream()
                .map(roleConvert::convertRolePageVO)
                .collect(Collectors.toList());
        return PageResult.build(rolePage.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRole(RoleAddDTO roleAddDTO) {
        checkRoleCodeUnique(roleAddDTO.getRoleCode(), null);
        SysRole role = roleConvert.convertRoleAddDTO(roleAddDTO);
        if (role.getSort() == null) {
            role.setSort(0);
        }
        if (role.getStatus() == null) {
            role.setStatus(SystemConstants.USER_NORMAL);
        }
        int rows = sysRoleMapper.insert(role);
        if (rows <= 0) {
            throw new BusinessException("新增角色失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(RoleStatusDTO roleStatusDTO) {
        SysRole dbRole = sysRoleMapper.selectById(roleStatusDTO.getId());
        if (dbRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
        dbRole.setStatus(roleStatusDTO.getStatus());
        int rows = sysRoleMapper.updateById(dbRole);
        if (rows <= 0) {
            throw new BusinessException("修改角色状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editRole(RoleUpdateDTO roleUpdateDTO) {
        SysRole dbRole = sysRoleMapper.selectById(roleUpdateDTO.getId());
        if (dbRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
        if (roleUpdateDTO.getRoleCode() != null && !roleUpdateDTO.getRoleCode().equals(dbRole.getRoleCode())) {
            checkRoleCodeUnique(roleUpdateDTO.getRoleCode(), roleUpdateDTO.getId());
        }
        roleConvert.updateEntityFromDTO(roleUpdateDTO, dbRole);
        int rows = sysRoleMapper.updateById(dbRole);
        if (rows <= 0) {
            throw new BusinessException("修改角色失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        int rows = sysRoleMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "角色不存在");
        }
    }

    private void checkRoleCodeUnique(String roleCode, Long excludeId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        wrapper.ne(excludeId != null, SysRole::getId, excludeId);
        Long count = sysRoleMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("角色标识已存在");
        }
    }
}
