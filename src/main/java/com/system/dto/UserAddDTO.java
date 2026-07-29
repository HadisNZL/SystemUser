package com.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "添加用户入参")
public class UserAddDTO {
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private Integer status;
}
