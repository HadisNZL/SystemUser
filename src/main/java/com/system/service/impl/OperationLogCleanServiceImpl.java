package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import com.system.service.OperationLogCleanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志清理服务实现。
 */
@Service
public class OperationLogCleanServiceImpl implements OperationLogCleanService {

    @Resource
    private SysOperationLogMapper sysOperationLogMapper;

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
