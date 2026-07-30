package com.system.convert;

import com.system.dto.RoleAddDTO;
import com.system.dto.RoleUpdateDTO;
import com.system.entity.SysRole;
import com.system.vo.RolePageVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoleConvert {

    RolePageVO convertRolePageVO(SysRole sysRole);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    SysRole convertRoleAddDTO(RoleAddDTO roleAddDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleteFlag", ignore = true)
    void updateEntityFromDTO(RoleUpdateDTO roleUpdateDTO, @MappingTarget SysRole dbRole);
}
