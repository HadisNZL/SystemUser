package com.system.vo;

import lombok.Data;

/**
 * 用户导入失败行信息。
 */
@Data
public class UserImportFailureVO {

    private Integer rowNum;

    private String username;

    private String reason;
}
