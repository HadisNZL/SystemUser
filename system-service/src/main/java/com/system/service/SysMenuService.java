package com.system.service;

import com.system.dto.MenuAddDTO;
import com.system.dto.MenuUpdateDTO;
import com.system.vo.MenuTreeVO;

import java.util.List;

/**
 * 菜单权限业务接口。
 */
public interface SysMenuService {

    List<MenuTreeVO> getMenuTree();

    List<MenuTreeVO> getCurrentUserMenuTree();

    void saveMenu(MenuAddDTO menuAddDTO);

    void editMenu(MenuUpdateDTO menuUpdateDTO);

    void deleteMenu(Long id);
}
