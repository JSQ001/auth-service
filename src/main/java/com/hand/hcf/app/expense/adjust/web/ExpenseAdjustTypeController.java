package com.hand.hcf.app.expense.adjust.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.adjust.domain.ExpAdjustTypeDimension;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustHeader;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.adjust.dto.ExpenseAdjustTypeRequestDTO;
import com.hand.hcf.app.expense.adjust.service.ExpAdjustTypeDimensionService;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustHeaderService;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustTypeService;
import com.hand.hcf.app.expense.adjust.web.dto.ExpAdjustTypeDimensionDTO;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@RestController
@RequestMapping("/api/expense/adjust/types")
@Api(tags = "费用调整单单据类型控制器")
public class ExpenseAdjustTypeController {
    @Autowired
    private ExpenseAdjustTypeService expenseAdjustTypeService;
    @Autowired
    private ExpenseAdjustHeaderService headerService;
    @Autowired
    private ExpAdjustTypeDimensionService adjustTypeDimensionService;

    /**
     * 新增 费用调整单类型定义
     *
     * @param expenseAdjustTypeRequestDTO
     * @return
     */
    @ApiOperation(value = "新增 费用调整单类型定义", notes = "新增 费用调整单类型定义 开发:韩雪")
    @RequestMapping(method = RequestMethod.POST, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseAdjustType> createExpenseAdjustType(@ApiParam(value = "费用调整单类型请求") @RequestBody @NotNull ExpenseAdjustTypeRequestDTO expenseAdjustTypeRequestDTO) {
        return ResponseEntity.ok(expenseAdjustTypeService.createExpenseAdjustType(expenseAdjustTypeRequestDTO));
    }

    /**
     * 根据ID查询 费用调整单类型定义
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询 费用调整单类型定义", notes = "根据ID查询 费用调整单类型定义 开发:韩雪")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseAdjustTypeRequestDTO> getExpenseAdjustType(@PathVariable Long id) {
        return ResponseEntity.ok(expenseAdjustTypeService.getExpenseAdjustType(id));
    }

    /**
     * 修改 费用调整单类型定义
     *
     * @param expenseAdjustTypeRequestDTO
     * @return
     */
    @ApiOperation(value = "修改 费用调整单类型定义", notes = "修改 费用调整单类型定义 开发:韩雪")
    @RequestMapping(method = RequestMethod.PUT, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseAdjustType> updateExpenseAdjustType(@ApiParam(value = "费用调整单类型请求") @RequestBody ExpenseAdjustTypeRequestDTO expenseAdjustTypeRequestDTO) {
        return ResponseEntity.ok(expenseAdjustTypeService.updateExpenseAdjustType(expenseAdjustTypeRequestDTO));
    }

    @PutMapping("/update/budget/or/account")
    @ApiOperation(value = "更新预算或账户", notes = "更新预算或账户 开发:韩雪")
    public ResponseEntity updateBudgetOrAccount(@ApiParam(value = "id") @RequestParam("id") Long id,
                                                @ApiParam(value = "预算标志") @RequestParam("budgetFlag") Boolean budgetFlag,
                                                @ApiParam(value = "账户标志") @RequestParam("accountFlag") Boolean accountFlag) {
        return ResponseEntity.ok(expenseAdjustTypeService.updateBudgetOrAccount(id, budgetFlag, accountFlag));
    }

    /**
     * 删除 费用调整单类型定义
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "删除 费用调整单类型定义", notes = "删除 费用调整单类型定义 开发:韩雪")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteExpenseAdjustType(@PathVariable Long id) {
        int count = headerService.selectCount(new EntityWrapper<ExpenseAdjustHeader>().eq("exp_adjust_type_id", id));
        if (count > 0) {
            throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_APPLY_DOCUMENT);
        }
        expenseAdjustTypeService.deleteExpenseAdjustType(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 自定义条件查询 费用调整单类型定义(分页)
     *
     * @param setOfBooksId
     * @param expAdjustTypeCode
     * @param expAdjustTypeName
     * @param adjustTypeCategory
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "自定义条件查询 费用调整单类型定义(分页)", notes = "自定义条件查询 费用调整单类型定义(分页) 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExpenseAdjustType>> getExpenseAdjustTypeByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "费用调整单类型代码") @RequestParam(value = "expAdjustTypeCode", required = false) String expAdjustTypeCode,
            @ApiParam(value = "费用调整单类型名称") @RequestParam(value = "expAdjustTypeName", required = false) String expAdjustTypeName,
            @ApiParam(value = "调整类型") @RequestParam(value = "adjustTypeCategory", required = false) String adjustTypeCategory,
            @ApiParam(value = "启用标志") @RequestParam(value = "enabled", required = false) Boolean enabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseAdjustType> list = expenseAdjustTypeService.getExpenseAdjustTypeByCond(setOfBooksId, expAdjustTypeCode, expAdjustTypeName, adjustTypeCategory, enabled, page, false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link", "/api/expense/adjust/types/query");
        return new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 费用调整单类型定义(分页)
     *
     * @param setOfBooksId
     * @param expAdjustTypeCode
     * @param expAdjustTypeName
     * @param adjustTypeCategory
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "自定义条件查询 费用调整单类型定义(分页)", notes = "自定义条件查询 费用调整单类型定义(分页) 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @RequestMapping(value = "/query/enable/dataAuth", method = RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExpenseAdjustType>> getExpenseAdjustTypeByCondEnableDataAuth(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @ApiParam(value = "费用调整单类型代码") @RequestParam(value = "expAdjustTypeCode", required = false) String expAdjustTypeCode,
            @ApiParam(value = "费用调整单类型名称") @RequestParam(value = "expAdjustTypeName", required = false) String expAdjustTypeName,
            @ApiParam(value = "调整类型") @RequestParam(value = "adjustTypeCategory", required = false) String adjustTypeCategory,
            @ApiParam(value = "启用标志") @RequestParam(value = "enabled", required = false) Boolean enabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseAdjustType> list = expenseAdjustTypeService.getExpenseAdjustTypeByCond(setOfBooksId, expAdjustTypeCode, expAdjustTypeName, adjustTypeCategory, enabled, page, true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link", "/api/expense/adjust/types/query");
        return new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }


    @GetMapping("/document/query")
    @ApiOperation(value = "列表文档查询参数", notes = "列表文档查询参数 开发:韩雪")
    public ResponseEntity listDocumentQueryParam(@ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId) {
        List<ExpenseAdjustType> result = expenseAdjustTypeService.selectList(
                new EntityWrapper<ExpenseAdjustType>()
                        .eq("set_of_books_id", setOfBooksId == null ? OrgInformationUtil.getCurrentSetOfBookId() : setOfBooksId));
        return ResponseEntity.ok(result);
    }

    /**
     * 查询当前用户可以新建的费用调整单类型
     *
     * @return
     */
    @GetMapping("/queryExpenseAdjustType")
    @ApiOperation(value = "查询当前用户可以新建的费用调整单类型",
            notes = "查询当前用户可以新建的费用调整单类型 修改： 谢宾")
    public ResponseEntity listTypeByCurrentUser(
            @ApiParam("是否包含授权") @RequestParam(required = false, defaultValue = "true") Boolean authFlag) {

        return ResponseEntity.ok(expenseAdjustTypeService.queryByUser(authFlag));
    }


    /**
     * 新建行时选择已分配的费用类型
     *
     * @param id
     * @param code
     * @param name
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping("/getExpenseType")
    @ApiOperation(value = "新建行时选择已分配的费用类型", notes = "新建行时选择已分配的费用类型 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseType>> getExpenseType(@ApiParam(value = "id") @RequestParam(value = "id") Long id,
                                                            @ApiParam(value = "编码") @RequestParam(value = "code", required = false) String code,
                                                            @ApiParam(value = "名称") @RequestParam(value = "name", required = false) String name,
                                                            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseType> list = expenseAdjustTypeService.getExpenseType(id, code, name, page);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotal());
        headers.add("Link", "/api/expense/adjust/types/getExpenseType");
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/expense/adjust/types/users 【调整单类型】根据单据id查询有该单据权限的用户
     */
    @GetMapping("/users")
    @ApiOperation(value = "根据单据id查询有该单据权限的用户", notes = "根据单据id查询有该单据权限的用户 开发:韩雪")
    public ResponseEntity listUsersByExpenseAdjustType(@ApiParam(value = "调整单类型ID")  @RequestParam(value = "adjustTypeId") Long adjustTypeId,
                                                       @ApiParam(value = "用户编码")  @RequestParam(value = "userCode", required = false) String userCode,
                                                       @ApiParam(value = "用户名称")  @RequestParam(value = "userName", required = false) String userName,
                                                       @ApiParam(value = "页面")  @RequestParam(defaultValue = "0") int page,
                                                       @ApiParam(value = "大小")  @RequestParam(defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = expenseAdjustTypeService.listUsersByExpenseAdjustType(adjustTypeId, userCode, userName, queryPage);
        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    /**
     * 添加维度
     *
     * @param dimensions
     * @param expAdjustTypeId
     * @return
     */
    @ApiOperation(value = "添加维度", notes = "添加维度 开发:韩雪")
    @PostMapping("/{expAdjustTypeId}/assign/dimension")
    public ResponseEntity assignDimensions(@ApiParam(value = "维度") @RequestBody List<ExpAdjustTypeDimension> dimensions,
                                           @PathVariable("expAdjustTypeId") Long expAdjustTypeId) {

        return ResponseEntity.ok(adjustTypeDimensionService.assignDimensions(expAdjustTypeId, dimensions));
    }

    /**
     * 查询维度
     * @param expAdjustTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "查询维度", notes = "查询维度 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/{expAdjustTypeId}/dimension/query")
    public ResponseEntity<List<ExpAdjustTypeDimension>> queryDimension(@PathVariable("expAdjustTypeId") Long expAdjustTypeId,
                                                                       @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpAdjustTypeDimension> result = adjustTypeDimensionService.queryDimension(expAdjustTypeId, page);
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
    @ApiOperation(value = "删除维度", notes = "删除维度 开发:韩雪")
    public ResponseEntity deleteDimension(@PathVariable("id") Long id) {
        return ResponseEntity.ok(adjustTypeDimensionService.deleteDimension(id));
    }

    /**
     * 获取当前账套下未分配的维度
     *
     * @param expAdjustTypeId
     * @param setOfBooksId
     * @param dimensionCode
     * @param dimensionName
     * @param enabled
     * @return
     */
    @GetMapping("/{expAdjustTypeId}/dimensions/query/filter")
    @ApiOperation(value = "获取当前账套下未分配的维度", notes = "获取当前账套下未分配的维度 开发:韩雪")
    public List<DimensionCO> listDimensionByConditionFilter(@PathVariable("expAdjustTypeId") Long expAdjustTypeId,
                                                            @ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                            @ApiParam(value = "维度编码") @RequestParam(value = "dimensionCode", required = false) String dimensionCode,
                                                            @ApiParam(value = "维度名称") @RequestParam(value = "dimensionName", required = false) String dimensionName,
                                                            @ApiParam(value = "启动标志") @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<DimensionCO> result = adjustTypeDimensionService.listDimensionByConditionFilter(expAdjustTypeId, setOfBooksId, dimensionCode, dimensionName, enabled);
        return result;
    }

    /**
     * 根据单据类型id查询单据类型及维度信息
     * @param expAdjustTypeId
     * @return
     */
    @GetMapping("/query/typeAndDimension/{expAdjustTypeId}")
    @ApiOperation(value = "根据单据类型id查询单据类型及维度信息", notes = "根据单据类型id查询单据类型及维度信息 开发:韩雪")
    public ResponseEntity<ExpAdjustTypeDimensionDTO> queryTypeAndDimensionById(@PathVariable("expAdjustTypeId") Long expAdjustTypeId){
        return ResponseEntity.ok(expenseAdjustTypeService.queryTypeAndDimensionById(expAdjustTypeId, true));
    }
}
