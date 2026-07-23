package com.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user") // 对应数据库中的表名
public class SysUser {
    @TableId(type = IdType.ASSIGN_ID)// 改为雪花算法分布式 ID
    private Long id;// 类型保持 Long（雪花 ID 是长整数，不能用 Integer）
    // 普通字段映射
    @TableField("username")
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
    // 自动填充：创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    // 自动填充：更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic//逻辑删除标记，MP 自动拼接删除条件，查询自动过滤已删数据
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}