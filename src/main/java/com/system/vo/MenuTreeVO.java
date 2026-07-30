package com.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "菜单权限树视图对象")
public class MenuTreeVO {

    private Long id;

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
