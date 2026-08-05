package com.system.service;

import com.system.common.PageResult;
import com.system.dto.DictDataAddDTO;
import com.system.dto.DictDataSearchDTO;
import com.system.dto.DictDataUpdateDTO;
import com.system.dto.DictTypeAddDTO;
import com.system.dto.DictTypeSearchDTO;
import com.system.dto.DictTypeUpdateDTO;
import com.system.vo.DictDataVO;
import com.system.vo.DictTypeVO;

import java.util.List;

/**
 * 字典配置服务。
 */
public interface SysDictService {

    PageResult<DictTypeVO> getDictTypePage(DictTypeSearchDTO dto, Integer pageNum, Integer pageSize);

    void addDictType(DictTypeAddDTO dto);

    void updateDictType(DictTypeUpdateDTO dto);

    void deleteDictType(Long id);

    PageResult<DictDataVO> getDictDataPage(DictDataSearchDTO dto, Integer pageNum, Integer pageSize);

    List<DictDataVO> getEnabledDictData(String dictType);

    void addDictData(DictDataAddDTO dto);

    void updateDictData(DictDataUpdateDTO dto);

    void deleteDictData(Long id);
}
