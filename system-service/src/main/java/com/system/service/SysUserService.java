package com.system.service;

import com.system.common.PageResult;
import com.system.dto.UserAddDTO;
import com.system.dto.UserAssignRoleDTO;
import com.system.dto.UserChangePasswordDTO;
import com.system.dto.UserResetPasswordDTO;
import com.system.dto.UserSearchDTO;
import com.system.dto.UserStatusDTO;
import com.system.dto.UserUpdateDTO;
import com.system.vo.RolePageVO;
import com.system.vo.UserDetailVO;
import com.system.vo.UserImportResultVO;
import com.system.vo.UserPageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户服务。
 */
public interface SysUserService {

    UserDetailVO getUserDetail(Long id);

    PageResult<UserPageVO> getUserPage(UserSearchDTO dto, Integer pageNum, Integer pageSize);

    byte[] exportUserExcel(UserSearchDTO dto);

    byte[] getUserImportTemplate();

    UserImportResultVO importUserExcel(MultipartFile file);

    void saveUser(UserAddDTO user);

    void editUser(UserUpdateDTO user);

    void updateUserStatus(UserStatusDTO userStatusDTO);

    void resetPassword(UserResetPasswordDTO resetPasswordDTO);

    void changePassword(UserChangePasswordDTO changePasswordDTO);

    List<RolePageVO> getUserRoles(Long id);

    void assignUserRoles(Long id, UserAssignRoleDTO userAssignRoleDTO);

    void deleteUser(Long id);

    void adminPhysicalDeleteUser(Long id);
}
