package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import com.system.service.OperationLogCleanService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 根据保留天数删除历史操作日志。
 */
@Service
public class OperationLogCleanServiceImpl implements OperationLogCleanService {

    private final SysOperationLogMapper sysOperationLogMapper;

    public OperationLogCleanServiceImpl(SysOperationLogMapper sysOperationLogMapper) {
        this.sysOperationLogMapper = sysOperationLogMapper;
    }

    @Override
    public int cleanExpiredLogs(int retentionDays) {
        if (retentionDays <= 0) {
            return 0;
        }
        LocalDateTime expireTime = LocalDateTime.now().minusDays(retentionDays);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysOperationLog::getCreateTime, expireTime);
        return sysOperationLogMapper.delete(wrapper);
    }
}
