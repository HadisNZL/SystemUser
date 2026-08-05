package com.system.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色分页返回对象。
 */
@Data
public class RolePageVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String roleName;

    private String roleCode;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;
}
