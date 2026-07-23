package com.system.convert;

import com.system.entity.SysUser;
import com.system.vo.UserPageVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // 指定让 Spring 容器接管
public interface UserConvert {

    // 如果源对象和目标对象的属性名完全一致，会自动映射
    // 如果名字不一致，可以用 @Mapping(source = "源字段", target = "目标字段") 手动指定
    UserPageVO convert(SysUser sysUser);
}
