package com.system.dto;

import lombok.Data;

/**
 * 用户 Excel 行数据。
 */
@Data
public class UserExcelDTO {

    private Integer rowNum;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private String status;
}
