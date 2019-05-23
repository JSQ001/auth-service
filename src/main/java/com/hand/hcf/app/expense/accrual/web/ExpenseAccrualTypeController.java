package com.hand.hcf.app.expense.accrual.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualDimension;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.accrual.dto.ExpenseAccrualTypeRequestDTO;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualDimensionService;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualTypeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @description: 费用预提单类型定义
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@Api(tags = "费用预提单类型定义")
@RestController
@RequestMapping("/api/expense/accrual/types")
public class ExpenseAccrualTypeController {

    @Autowired
    private ExpenseAccrualTypeService expenseAccrualTypeService;
    @Autowired
    private ExpenseAccrualDimensionService expenseAccrualDimensionService;

    /**
     * 根据ID查询 费用预提单类型定义
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询 费用预提单类型定义", notes = "根据ID查询 费用预提单类型定义 开发:liguo.zhao")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ExpenseAccrualTypeRequestDTO> getExpenseAccrualType(
            @ApiParam(value = "费用预提单类型id") @PathVariable Long id) {
        return ResponseEntity.ok(expenseAccrualTypeService.getExpenseAccrualType(id));
    }

    /**
     * 新增 费用预提单类型定义
     *
     * @param expenseAccrualTypeRequestDTO
     * @return
     */
    @ApiOperation(value = "新增 费用预提单类型定义", notes = "新增 费用预提单类型定义 开发:liguo.zhao")
    @PostMapping
    public ResponseEntity<ExpenseAccrualType> createExpenseAccrualType(
            @ApiParam(value = "费用预提单类型请求") @RequestBody @NotNull ExpenseAccrualTypeRequestDTO expenseAccrualTypeRequestDTO) {
        return ResponseEntity.ok(expenseAccrualTypeService.createExpenseAccrualType(expenseAccrualTypeRequestDTO));
    }

    /**
     * 修改 费用预提单类型定义
     *
     * @param expenseAccrualTypeRequestDTO
     * @return
     */
    @ApiOperation(value = "修改 费用预提单类型定义", notes = "修改 费用预提单类型定义 开发:liguo.zhao")
    @PutMapping
    public ResponseEntity<ExpenseAccrualType> updateExpenseAccrualType(
            @ApiParam(value = "费用预提单类型请求") @RequestBody ExpenseAccrualTypeRequestDTO expenseAccrualTypeRequestDTO) {
        return ResponseEntity.ok(expenseAccrualTypeService.updateExpenseAccrualType(expenseAccrualTypeRequestDTO));
    }

    @PutMapping("/update/budgt")
    @ApiOperation(value = "更新预算管控", notes = "更新预算管控 开发:liguo.zhao")
    public ResponseEntity<Boolean> updateBudgt(@ApiParam(value = "id") @RequestParam("id") Long id,
                                               @ApiParam(value = "预算管控") @RequestParam("budgtFlag") Boolean budgtFlag) {
        return ResponseEntity.ok(expenseAccrualTypeService.updateBudgt(id, budgtFlag));
    }

    /**
     * 自定义条件查询 费用预提单类型定义(分页) 数据权限
     *
     * @param setOfBooksId
     * @param expAccrualTypeCode
     * @param expAccrualTypeName
     * @param enableFlag
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "自定义条件查询 费用预提单类型定义(分页) 数据权限",
                  notes = "自定义条件查询 费用预提单类型定义(分页) 数据权限 开发:liguo.zhao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping(value = "/query/enable/dataAuth")
    public ResponseEntity<List<ExpenseAccrualType>> getExpenseAccrualTypeByCondEnableDataAuth(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "费用预提单类型代码") @RequestParam(value = "expAccrualTypeCode", required = false) String expAccrualTypeCode,
            @ApiParam(value = "费用预提单类型名称") @RequestParam(value = "expAccrualTypeName", required = false) String expAccrualTypeName,
            @ApiParam(value = "启用标志") @RequestParam(value = "enableFlag", required = false) Boolean enableFlag,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseAccrualType> list = expenseAccrualTypeService
                .getExpenseAccrualTypeByCond(setOfBooksId, expAccrualTypeCode, expAccrualTypeName, enableFlag, page, true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link", "/api/expense/accrual/types/query");
        return new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 费用预提单类型定义(分页)
     *
     * @param setOfBooksId
     * @param expAccrualTypeCode
     * @param expAccrualTypeName
     * @param enableFlag
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "自定义条件查询 费用预提单类型定义(分页) ", notes = "自定义条件查询 费用预提单类型定义(分页)  开发:liguo.zhao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping(value = "/query")
    public ResponseEntity<List<ExpenseAccrualType>> getExpenseAccrualTypeByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "费用预提单类型代码") @RequestParam(value = "expAccrualTypeCode", required = false) String expAccrualTypeCode,
            @ApiParam(value = "费用预提单类型名称") @RequestParam(value = "expAccrualTypeName", required = false) String expAccrualTypeName,
            @ApiParam(value = "启用标志") @RequestParam(value = "enableFlag", required = false) Boolean enableFlag,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseAccrualType> list = expenseAccrualTypeService
                .getExpenseAccrualTypeByCond(setOfBooksId, expAccrualTypeCode, expAccrualTypeName, enableFlag, page, false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link", "/api/expense/accrual/types/query");
        return new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 添加维度
     *
     * @param dimensions
     * @param expAccrualTypeId
     * @return
     */
    @ApiOperation(value = "添加维度", notes = "添加维度 开发:liguo.zhao")
    @PostMapping("/{expAccrualTypeId}/assign/dimension")
    public ResponseEntity<List<ExpenseAccrualDimension>> assignDimensions(
            @ApiParam(value = "维度") @RequestBody List<ExpenseAccrualDimension> dimensions,
            @ApiParam(value = "费用预提单类型id") @PathVariable("expAccrualTypeId") Long expAccrualTypeId) {

        return ResponseEntity.ok(expenseAccrualDimensionService.assignDimensions(expAccrualTypeId, dimensions));
    }

    /**
     * 查询维度
     * @param expAccrualTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "查询维度", notes = "查询维度 开发:liguo.zhao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/{expAccrualTypeId}/dimension/query")
    public ResponseEntity<List<ExpenseAccrualDimension>> queryDimension(
            @ApiParam(value = "费用预提单类型id") @PathVariable("expAccrualTypeId") Long expAccrualTypeId,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseAccrualDimension> result = expenseAccrualDimensionService.queryDimension(expAccrualTypeId, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * 删除维度
     *
     * @param id
     * @return
     */
    @DeleteMapping("/dimension/{id}")
    @ApiOperation(value = "删除维度", notes = "删除维度 开发:liguo.zhao")
    public ResponseEntity<Boolean> deleteDimension(@ApiParam(value = "费用预提单类型id") @PathVariable("id") Long id) {
        return ResponseEntity.ok(expenseAccrualDimensionService.deleteDimension(id));
    }

    /**
     * 获取当前账套下未分配的维度
     *
     * @param expAccrualTypeId
     * @param setOfBooksId
     * @param dimensionCode
     * @param dimensionName
     * @return
     */
    @GetMapping("/{expAccrualTypeId}/dimensions/query/filter")
    @ApiOperation(value = "获取当前账套下未分配的维度", notes = "获取当前账套下未分配的维度 开发:liguo.zhao")
    public ResponseEntity<List<DimensionCO>> listDimensionByConditionFilter(
            @ApiParam(value = "费用预提单类型id") @PathVariable("expAccrualTypeId") Long expAccrualTypeId,
            @ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
            @ApiParam(value = "维度编码") @RequestParam(value = "dimensionCode", required = false) String dimensionCode,
            @ApiParam(value = "维度名称") @RequestParam(value = "dimensionName", required = false) String dimensionName,
            @ApiParam(value = "启用标志") Boolean enabled) {
        List<DimensionCO> result = expenseAccrualDimensionService
                .listDimensionByConditionFilter(expAccrualTypeId, setOfBooksId, dimensionCode, dimensionName, enabled);
        return ResponseEntity.ok(result);
    }

}
