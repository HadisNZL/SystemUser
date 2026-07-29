package com.system.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改用户入参")
public class UserUpdateDTO {
    @TableId(type = IdType.ASSIGN_ID)// 改为雪花算法分布式 ID
    private Long id;// 类型保持 Long（雪花 ID 是长整数，不能用 Integer）
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
}
