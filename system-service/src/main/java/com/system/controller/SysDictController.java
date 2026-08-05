package com.system.controller;

import com.system.annotation.OperationLog;
import com.system.common.PageResult;
import com.system.common.Result;
import com.system.common.SystemConstants;
import com.system.dto.DictDataAddDTO;
import com.system.dto.DictDataSearchDTO;
import com.system.dto.DictDataUpdateDTO;
import com.system.dto.DictTypeAddDTO;
import com.system.dto.DictTypeSearchDTO;
import com.system.dto.DictTypeUpdateDTO;
import com.system.service.SysDictService;
import com.system.vo.DictDataVO;
import com.system.vo.DictTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典配置接口。
 */
@RestController
@RequestMapping("/system/dict")
@Validated
@Tag(name = "字典管理模块", description = "提供字典类型和字典数据的查询及维护接口")
public class SysDictController {

    @Resource
    private SysDictService sysDictService;

    @Operation(summary = "分页查询字典类型", description = "根据字典名称、字典类型和状态分页查询字典类型")
    @GetMapping("/type/search_list")
    @PreAuthorize("hasAuthority('sys:dict:type:list')")
    public Result<PageResult<DictTypeVO>> getDictTypeList(@Valid DictTypeSearchDTO dto,
                                                          @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_NUM) @Min(value = 1, message = "页码必须大于等于1") Integer pageNum,
                                                          @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_SIZE) @Min(value = 1, message = "每页条数必须大于等于1") @Max(value = 100, message = "每页条数不能超过100") Integer pageSize) {
        PageResult<DictTypeVO> page = sysDictService.getDictTypePage(dto, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "新增字典类型", description = "新增字典类型并校验字典类型编码唯一性")
    @PostMapping("/type/add")
    @PreAuthorize("hasAuthority('sys:dict:type:add')")
    @OperationLog(module = "字典管理", operation = "新增字典类型")
    public Result<String> addDictType(@Valid @RequestBody DictTypeAddDTO dto) {
        sysDictService.addDictType(dto);
        return Result.success("新增字典类型成功");
    }

    @Operation(summary = "修改字典类型", description = "根据字典类型ID修改名称、状态和备注")
    @PostMapping("/type/modify")
    @PreAuthorize("hasAuthority('sys:dict:type:edit')")
    @OperationLog(module = "字典管理", operation = "修改字典类型")
    public Result<Boolean> updateDictType(@Valid @RequestBody DictTypeUpdateDTO dto) {
        sysDictService.updateDictType(dto);
        return Result.success(true);
    }

    @Operation(summary = "删除字典类型", description = "删除字典类型及其对应的字典数据")
    @DeleteMapping("/type/delete/{id}")
    @PreAuthorize("hasAuthority('sys:dict:type:remove')")
    @OperationLog(module = "字典管理", operation = "删除字典类型")
    public Result<String> deleteDictType(@PathVariable @Min(value = 1, message = "字典类型ID必须大于等于1") Long id) {
        sysDictService.deleteDictType(id);
        return Result.success("删除字典类型成功");
    }

    @Operation(summary = "分页查询字典数据", description = "根据字典类型、标签、值和状态分页查询字典数据")
    @GetMapping("/data/search_list")
    @PreAuthorize("hasAuthority('sys:dict:data:list')")
    public Result<PageResult<DictDataVO>> getDictDataList(@Valid DictDataSearchDTO dto,
                                                          @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_NUM) @Min(value = 1, message = "页码必须大于等于1") Integer pageNum,
                                                          @RequestParam(defaultValue = SystemConstants.DEFAULT_PAGE_SIZE) @Min(value = 1, message = "每页条数必须大于等于1") @Max(value = 100, message = "每页条数不能超过100") Integer pageSize) {
        PageResult<DictDataVO> page = sysDictService.getDictDataPage(dto, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "查询可用字典数据", description = "通过字典类型编码查询已启用的字典数据列表")
    @GetMapping("/data/type/{dictType}")
    @PreAuthorize("isAuthenticated()")
    public Result<List<DictDataVO>> getEnabledDictData(@PathVariable @NotBlank(message = "字典类型不能为空") String dictType) {
        List<DictDataVO> list = sysDictService.getEnabledDictData(dictType);
        return Result.success(list);
    }

    @Operation(summary = "新增字典数据", description = "为指定字典类型新增标签和值")
    @PostMapping("/data/add")
    @PreAuthorize("hasAuthority('sys:dict:data:add')")
    @OperationLog(module = "字典管理", operation = "新增字典数据")
    public Result<String> addDictData(@Valid @RequestBody DictDataAddDTO dto) {
        sysDictService.addDictData(dto);
        return Result.success("新增字典数据成功");
    }

    @Operation(summary = "修改字典数据", description = "根据字典数据ID修改标签、值、排序和状态")
    @PostMapping("/data/modify")
    @PreAuthorize("hasAuthority('sys:dict:data:edit')")
    @OperationLog(module = "字典管理", operation = "修改字典数据")
    public Result<Boolean> updateDictData(@Valid @RequestBody DictDataUpdateDTO dto) {
        sysDictService.updateDictData(dto);
        return Result.success(true);
    }

    @Operation(summary = "删除字典数据", description = "通过字典数据ID删除指定字典项")
    @DeleteMapping("/data/delete/{id}")
    @PreAuthorize("hasAuthority('sys:dict:data:remove')")
    @OperationLog(module = "字典管理", operation = "删除字典数据")
    public Result<String> deleteDictData(@PathVariable @Min(value = 1, message = "字典数据ID必须大于等于1") Long id) {
        sysDictService.deleteDictData(id);
        return Result.success("删除字典数据成功");
    }
}
