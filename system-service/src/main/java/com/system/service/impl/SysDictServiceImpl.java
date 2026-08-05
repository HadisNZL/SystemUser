package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.BusinessException;
import com.system.common.PageResult;
import com.system.common.ResultCode;
import com.system.common.SystemConstants;
import com.system.dto.DictDataAddDTO;
import com.system.dto.DictDataSearchDTO;
import com.system.dto.DictDataUpdateDTO;
import com.system.dto.DictTypeAddDTO;
import com.system.dto.DictTypeSearchDTO;
import com.system.dto.DictTypeUpdateDTO;
import com.system.entity.SysDictData;
import com.system.entity.SysDictType;
import com.system.mapper.SysDictDataMapper;
import com.system.mapper.SysDictTypeMapper;
import com.system.service.SysDictService;
import com.system.vo.DictDataVO;
import com.system.vo.DictTypeVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典配置服务实现。
 */
@Service
public class SysDictServiceImpl implements SysDictService {

    @Resource
    private SysDictTypeMapper sysDictTypeMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Override
    public PageResult<DictTypeVO> getDictTypePage(DictTypeSearchDTO dto, Integer pageNum, Integer pageSize) {
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        Page<SysDictType> dictTypePage = sysDictTypeMapper.selectPage(page, buildDictTypeWrapper(dto));
        List<DictTypeVO> list = dictTypePage.getRecords().stream()
                .map(this::convertDictTypeVO)
                .collect(Collectors.toList());
        return PageResult.build(dictTypePage.getTotal(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictType(DictTypeAddDTO dto) {
        checkDictTypeUnique(dto.getDictType(), null);
        SysDictType dictType = new SysDictType();
        dictType.setDictName(dto.getDictName());
        dictType.setDictType(dto.getDictType());
        dictType.setStatus(dto.getStatus() == null ? SystemConstants.USER_NORMAL : dto.getStatus());
        dictType.setRemark(dto.getRemark());
        int rows = sysDictTypeMapper.insert(dictType);
        if (rows <= 0) {
            throw new BusinessException("新增字典类型失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(DictTypeUpdateDTO dto) {
        SysDictType dbDictType = getDictTypeById(dto.getId());
        if (dto.getDictType() != null && !dto.getDictType().isBlank() && !dto.getDictType().equals(dbDictType.getDictType())) {
            checkDictTypeUnique(dto.getDictType(), dto.getId());
            updateDictDataType(dbDictType.getDictType(), dto.getDictType());
            dbDictType.setDictType(dto.getDictType());
        }
        if (dto.getDictName() != null) {
            dbDictType.setDictName(dto.getDictName());
        }
        if (dto.getStatus() != null) {
            dbDictType.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            dbDictType.setRemark(dto.getRemark());
        }
        int rows = sysDictTypeMapper.updateById(dbDictType);
        if (rows <= 0) {
            throw new BusinessException("修改字典类型失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        SysDictType dbDictType = getDictTypeById(id);
        Long dataCount = sysDictDataMapper.selectCount(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dbDictType.getDictType()));
        if (dataCount != null && dataCount > 0) {
            throw new BusinessException("字典类型下存在字典数据，不能删除");
        }
        int rows = sysDictTypeMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException("删除字典类型失败");
        }
    }

    @Override
    public PageResult<DictDataVO> getDictDataPage(DictDataSearchDTO dto, Integer pageNum, Integer pageSize) {
        Page<SysDictData> page = new Page<>(pageNum, pageSize);
        Page<SysDictData> dictDataPage = sysDictDataMapper.selectPage(page, buildDictDataWrapper(dto));
        List<DictDataVO> list = dictDataPage.getRecords().stream()
                .map(this::convertDictDataVO)
                .collect(Collectors.toList());
        return PageResult.build(dictDataPage.getTotal(), list);
    }

    @Override
    public List<DictDataVO> getEnabledDictData(String dictType) {
        checkDictTypeExists(dictType);
        return sysDictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, SystemConstants.USER_NORMAL)
                        .orderByAsc(SysDictData::getSort)
                        .orderByAsc(SysDictData::getId))
                .stream()
                .map(this::convertDictDataVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictData(DictDataAddDTO dto) {
        checkDictTypeExists(dto.getDictType());
        checkDictValueUnique(dto.getDictType(), dto.getDictValue(), null);
        SysDictData dictData = new SysDictData();
        dictData.setDictType(dto.getDictType());
        dictData.setDictLabel(dto.getDictLabel());
        dictData.setDictValue(dto.getDictValue());
        dictData.setSort(dto.getSort() == null ? 0 : dto.getSort());
        dictData.setStatus(dto.getStatus() == null ? SystemConstants.USER_NORMAL : dto.getStatus());
        dictData.setRemark(dto.getRemark());
        int rows = sysDictDataMapper.insert(dictData);
        if (rows <= 0) {
            throw new BusinessException("新增字典数据失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(DictDataUpdateDTO dto) {
        SysDictData dbDictData = getDictDataById(dto.getId());
        String targetDictType = dto.getDictType() == null || dto.getDictType().isBlank() ? dbDictData.getDictType() : dto.getDictType();
        String targetDictValue = dto.getDictValue() == null || dto.getDictValue().isBlank() ? dbDictData.getDictValue() : dto.getDictValue();
        if (!targetDictType.equals(dbDictData.getDictType())) {
            checkDictTypeExists(targetDictType);
        }
        if (!targetDictType.equals(dbDictData.getDictType()) || !targetDictValue.equals(dbDictData.getDictValue())) {
            checkDictValueUnique(targetDictType, targetDictValue, dto.getId());
        }
        dbDictData.setDictType(targetDictType);
        dbDictData.setDictValue(targetDictValue);
        if (dto.getDictLabel() != null) {
            dbDictData.setDictLabel(dto.getDictLabel());
        }
        if (dto.getSort() != null) {
            dbDictData.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            dbDictData.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            dbDictData.setRemark(dto.getRemark());
        }
        int rows = sysDictDataMapper.updateById(dbDictData);
        if (rows <= 0) {
            throw new BusinessException("修改字典数据失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long id) {
        int rows = sysDictDataMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "字典数据不存在");
        }
    }

    private LambdaQueryWrapper<SysDictType> buildDictTypeWrapper(DictTypeSearchDTO dto) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            wrapper.like(dto.getDictName() != null && !dto.getDictName().isBlank(), SysDictType::getDictName, dto.getDictName());
            wrapper.like(dto.getDictType() != null && !dto.getDictType().isBlank(), SysDictType::getDictType, dto.getDictType());
            wrapper.eq(dto.getStatus() != null, SysDictType::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(SysDictType::getCreateTime);
        return wrapper;
    }

    private LambdaQueryWrapper<SysDictData> buildDictDataWrapper(DictDataSearchDTO dto) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            wrapper.eq(dto.getDictType() != null && !dto.getDictType().isBlank(), SysDictData::getDictType, dto.getDictType());
            wrapper.like(dto.getDictLabel() != null && !dto.getDictLabel().isBlank(), SysDictData::getDictLabel, dto.getDictLabel());
            wrapper.eq(dto.getStatus() != null, SysDictData::getStatus, dto.getStatus());
        }
        wrapper.orderByAsc(SysDictData::getDictType).orderByAsc(SysDictData::getSort).orderByAsc(SysDictData::getId);
        return wrapper;
    }

    private void updateDictDataType(String oldDictType, String newDictType) {
        List<SysDictData> dictDataList = sysDictDataMapper.selectList(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, oldDictType));
        for (SysDictData item : dictDataList) {
            item.setDictType(newDictType);
            sysDictDataMapper.updateById(item);
        }
    }

    private SysDictType getDictTypeById(Long id) {
        SysDictType dictType = sysDictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "字典类型不存在");
        }
        return dictType;
    }

    private SysDictData getDictDataById(Long id) {
        SysDictData dictData = sysDictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "字典数据不存在");
        }
        return dictData;
    }

    private void checkDictTypeExists(String dictType) {
        Long count = sysDictTypeMapper.selectCount(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType)
                .eq(SysDictType::getStatus, SystemConstants.USER_NORMAL));
        if (count == null || count <= 0) {
            throw new BusinessException("字典类型不存在或已禁用");
        }
    }

    private void checkDictTypeUnique(String dictType, Long excludeId) {
        Long count = sysDictTypeMapper.selectCount(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType)
                .ne(excludeId != null, SysDictType::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException("字典类型已存在");
        }
    }

    private void checkDictValueUnique(String dictType, String dictValue, Long excludeId) {
        Long count = sysDictDataMapper.selectCount(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue)
                .ne(excludeId != null, SysDictData::getId, excludeId));
        if (count != null && count > 0) {
            throw new BusinessException("同一字典类型下字典值已存在");
        }
    }

    private DictTypeVO convertDictTypeVO(SysDictType entity) {
        DictTypeVO vo = new DictTypeVO();
        vo.setId(entity.getId());
        vo.setDictName(entity.getDictName());
        vo.setDictType(entity.getDictType());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private DictDataVO convertDictDataVO(SysDictData entity) {
        DictDataVO vo = new DictDataVO();
        vo.setId(entity.getId());
        vo.setDictType(entity.getDictType());
        vo.setDictLabel(entity.getDictLabel());
        vo.setDictValue(entity.getDictValue());
        vo.setSort(entity.getSort());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
