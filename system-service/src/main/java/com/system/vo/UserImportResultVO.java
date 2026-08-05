package com.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户导入结果。
 */
@Data
public class UserImportResultVO {

    private Integer successCount = 0;

    private Integer failureCount = 0;

    private List<UserImportFailureVO> failures = new ArrayList<>();
}
