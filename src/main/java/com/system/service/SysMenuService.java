package com.system.service;

import com.system.dto.MenuAddDTO;
import com.system.dto.MenuUpdateDTO;
import com.system.vo.MenuTreeVO;

import java.util.List;

public interface SysMenuService {

    List<MenuTreeVO> getMenuTree();

    void saveMenu(MenuAddDTO menuAddDTO);

    void editMenu(MenuUpdateDTO menuUpdateDTO);

    void deleteMenu(Long id);
}
