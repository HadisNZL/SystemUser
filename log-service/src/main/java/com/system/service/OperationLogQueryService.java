package com.system.service;

import com.system.common.PageResult;
import com.system.dto.OperationLogSearchDTO;
import com.system.vo.OperationLogVO;

/**
 * 操作日志查询服务。
 */
public interface OperationLogQueryService {

    PageResult<OperationLogVO> getOperationLogPage(OperationLogSearchDTO dto, Integer pageNum, Integer pageSize);
}
