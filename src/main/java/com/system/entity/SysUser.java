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
    /**
     * 乐观锁版本号
     * 多人同时编辑同一条数据，防止数据覆盖丢失，电商库存、订单、用户信息高频使用
     * 数据库先执行创建属性
     * ALTER TABLE sys_user ADD COLUMN version INT DEFAULT 1 COMMENT '乐观锁版本号';
     * 再去MyMetaObjectHandler新增this.strictInsertFill(metaObject, "version", Integer::valueOf, 1);
     * <p>
     * 执行原理
     * 查询数据时带出 version 版本号
     * 更新时自动拼接条件 where version = 旧版本
     * 更新成功自动 version+1
     * 版本不一致更新行数为 0，判定数据已被他人修改
     * <p>
     * 业务使用
     */
    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;
}