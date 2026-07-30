package com.system.service;

import com.system.common.PageResult;
import com.system.dto.RoleAssignPermissionDTO;
import com.system.dto.RoleAddDTO;
import com.system.dto.RoleSearchDTO;
import com.system.dto.RoleStatusDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.vo.MenuTreeVO;
import com.system.vo.RolePageVO;

import java.util.List;

public interface SysRoleService {

    PageResult<RolePageVO> getRolePage(RoleSearchDTO dto, Integer pageNum, Integer pageSize);

    void saveRole(RoleAddDTO roleAddDTO);

    void editRole(RoleUpdateDTO roleUpdateDTO);

    void updateRoleStatus(RoleStatusDTO roleStatusDTO);

    List<MenuTreeVO> getRolePermissions(Long id);

    void assignRolePermissions(Long id, RoleAssignPermissionDTO roleAssignPermissionDTO);

    void deleteRole(Long id);
}
