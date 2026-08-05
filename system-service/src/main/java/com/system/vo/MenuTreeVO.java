package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限树返回对象。
 */
@Data
public class MenuTreeVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String name;

    private String menuPath;

    private String permissionKey;

    private Integer type;

    private Integer sort;

    private String icon;

    private String component;

    private Integer visible;

    private Integer status;

    private String redirect;

    private Integer isCache;

    private Integer isFrame;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<MenuTreeVO> children = new ArrayList<>();
}
