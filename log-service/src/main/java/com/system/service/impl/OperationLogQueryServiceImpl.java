package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.BusinessException;
import com.system.common.PageResult;
import com.system.common.ResultCode;
import com.system.dto.OperationLogSearchDTO;
import com.system.entity.SysOperationLog;
import com.system.mapper.SysOperationLogMapper;
import com.system.service.OperationLogQueryService;
import com.system.vo.OperationLogVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 操作日志分页查询实现。
 */
@Service
public class OperationLogQueryServiceImpl implements OperationLogQueryService {

    private final SysOperationLogMapper sysOperationLogMapper;

    public OperationLogQueryServiceImpl(SysOperationLogMapper sysOperationLogMapper) {
        this.sysOperationLogMapper = sysOperationLogMapper;
    }

    @Override
    public PageResult<OperationLogVO> getOperationLogPage(OperationLogSearchDTO dto, Integer pageNum, Integer pageSize) {
        checkTimeRange(dto);
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getModule()), SysOperationLog::getModule, dto.getModule())
                .like(StringUtils.hasText(dto.getOperation()), SysOperationLog::getOperation, dto.getOperation())
                .eq(dto.getOperatorId() != null, SysOperationLog::getOperatorId, dto.getOperatorId())
                .eq(dto.getStatus() != null, SysOperationLog::getStatus, dto.getStatus())
                .ge(dto.getStartTime() != null, SysOperationLog::getCreateTime, dto.getStartTime())
                .le(dto.getEndTime() != null, SysOperationLog::getCreateTime, dto.getEndTime())
                .orderByDesc(SysOperationLog::getCreateTime, SysOperationLog::getId);
        Page<SysOperationLog> page = sysOperationLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<OperationLogVO> list = page.getRecords().stream().map(this::toVO).toList();
        return PageResult.build(page.getTotal(), list);
    }

    private void checkTimeRange(OperationLogSearchDTO dto) {
        if (dto.getStartTime() != null && dto.getEndTime() != null && dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "开始时间不能晚于结束时间");
        }
    }

    private OperationLogVO toVO(SysOperationLog entity) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(entity.getId());
        vo.setModule(entity.getModule());
        vo.setOperation(entity.getOperation());
        vo.setRequestMethod(entity.getRequestMethod());
        vo.setRequestUri(entity.getRequestUri());
        vo.setStatus(entity.getStatus());
        vo.setErrorMsg(entity.getErrorMsg());
        vo.setOperatorId(entity.getOperatorId());
        vo.setIp(entity.getIp());
        vo.setCostTime(entity.getCostTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
