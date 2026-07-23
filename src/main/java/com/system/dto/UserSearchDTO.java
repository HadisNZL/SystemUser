package com.system.dto;

import lombok.Data;

@Data
public class UserSearchDTO {
    // 账号模糊查询
    private String username;
    // 状态精准查询
    private Integer status;
    // 开始时间
    private String startTime;
    // 结束时间
    private String endTime;
}