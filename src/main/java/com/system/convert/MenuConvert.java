package com.system.convert;

import com.system.dto.MenuAddDTO;
import com.system.dto.MenuUpdateDTO;
import com.system.entity.SysPermission;
import com.system.vo.MenuTreeVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MenuConvert {

    @Mapping(target = "children", ignore = true)
    MenuTreeVO convertMenuTreeVO(SysPermission sysPermission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    SysPermission convertMenuAddDTO(MenuAddDTO menuAddDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    void updateEntityFromDTO(MenuUpdateDTO menuUpdateDTO, @MappingTarget SysPermission dbPermission);
}
