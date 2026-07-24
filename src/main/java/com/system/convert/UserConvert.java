package com.system.convert;

import com.system.entity.SysUser;
import com.system.vo.UserPageVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring") // 指定让 Spring 容器接管
public interface UserConvert {

    // 如果源对象和目标对象的属性名完全一致，会自动映射
    // 如果名字不一致，可以用 @Mapping(source = "源字段", target = "目标字段") 手动指定
    UserPageVO convert(SysUser sysUser);

    // UserPageVO 转 SysUser (新增的反向方法)
    SysUser convert(UserPageVO userVO);

    /**
     * 3. 新增的：局部更新方法（专门用于编辑）
     * 核心注解：NullValuePropertyMappingStrategy.IGNORE
     * 作用：当 UserPageVO 中的属性为 null 时，不覆盖 dbUser 原本的值
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromVO(UserPageVO userVO, @MappingTarget SysUser dbUser);
}
