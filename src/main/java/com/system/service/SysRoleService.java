package com.system.service;

import com.system.common.PageResult;
import com.system.dto.RoleAddDTO;
import com.system.dto.RoleSearchDTO;
import com.system.dto.RoleStatusDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.vo.RolePageVO;

public interface SysRoleService {

    PageResult<RolePageVO> getRolePage(RoleSearchDTO dto, Integer pageNum, Integer pageSize);

    void saveRole(RoleAddDTO roleAddDTO);

    void editRole(RoleUpdateDTO roleUpdateDTO);

    void updateRoleStatus(RoleStatusDTO roleStatusDTO);

    void deleteRole(Long id);
}
