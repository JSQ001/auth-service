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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(method = RequestMethod.POST, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseAdjustType> createExpenseAdjustType(@RequestBody @NotNull ExpenseAdjustTypeRequestDTO expenseAdjustTypeRequestDTO) {
        return ResponseEntity.ok(expenseAdjustTypeService.createExpenseAdjustType(expenseAdjustTypeRequestDTO));
    }

    /**
     * 根据ID查询 费用调整单类型定义
     *
     * @param id
     * @return
     */
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
    @RequestMapping(method = RequestMethod.PUT, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseAdjustType> updateExpenseAdjustType(@RequestBody ExpenseAdjustTypeRequestDTO expenseAdjustTypeRequestDTO) {
        return ResponseEntity.ok(expenseAdjustTypeService.updateExpenseAdjustType(expenseAdjustTypeRequestDTO));
    }

    @PutMapping("/update/budget/or/account")
    public ResponseEntity updateBudgetOrAccount(@RequestParam("id") Long id,
                                                @RequestParam("budgetFlag") Boolean budgetFlag,
                                                @RequestParam("accountFlag") Boolean accountFlag) {
        return ResponseEntity.ok(expenseAdjustTypeService.updateBudgetOrAccount(id, budgetFlag, accountFlag));
    }

    /**
     * 删除 费用调整单类型定义
     *
     * @param id
     * @return
     */
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
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExpenseAdjustType>> getExpenseAdjustTypeByCond(
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(value = "expAdjustTypeCode", required = false) String expAdjustTypeCode,
            @RequestParam(value = "expAdjustTypeName", required = false) String expAdjustTypeName,
            @RequestParam(value = "adjustTypeCategory", required = false) String adjustTypeCategory,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseAdjustType> list = expenseAdjustTypeService.getExpenseAdjustTypeByCond(setOfBooksId, expAdjustTypeCode, expAdjustTypeName, adjustTypeCategory, enabled, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link", "/api/expense/adjust/types/query");
        return new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }


    @GetMapping("/document/query")
    public ResponseEntity listDocumentQueryParam(@RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId) {
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
    public ResponseEntity<List<ExpenseType>> getExpenseType(@RequestParam(value = "id") Long id,
                                                            @RequestParam(value = "code", required = false) String code,
                                                            @RequestParam(value = "name", required = false) String name,
                                                            Pageable pageable) throws URISyntaxException {
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
    public ResponseEntity listUsersByExpenseAdjustType(@RequestParam(value = "adjustTypeId") Long adjustTypeId,
                                                       @RequestParam(value = "userCode", required = false) String userCode,
                                                       @RequestParam(value = "userName", required = false) String userName,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
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
    @PostMapping("/{expAdjustTypeId}/assign/dimension")
    public ResponseEntity assignDimensions(@RequestBody List<ExpAdjustTypeDimension> dimensions,
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
    @GetMapping("/{expAdjustTypeId}/dimension/query")
    public ResponseEntity<List<ExpAdjustTypeDimension>> queryDimension(@PathVariable("expAdjustTypeId") Long expAdjustTypeId,
                                                                       Pageable pageable) throws URISyntaxException {
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
    public List<DimensionCO> listDimensionByConditionFilter(@PathVariable("expAdjustTypeId") Long expAdjustTypeId,
                                                            @RequestParam("setOfBooksId") Long setOfBooksId,
                                                            @RequestParam(value = "dimensionCode", required = false) String dimensionCode,
                                                            @RequestParam(value = "dimensionName", required = false) String dimensionName,
                                                            @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<DimensionCO> result = adjustTypeDimensionService.listDimensionByConditionFilter(expAdjustTypeId, setOfBooksId, dimensionCode, dimensionName, enabled);
        return result;
    }

    /**
     * 根据单据类型id查询单据类型及维度信息
     * @param expAdjustTypeId
     * @return
     */
    @GetMapping("/query/typeAndDimension/{expAdjustTypeId}")
    public ResponseEntity<ExpAdjustTypeDimensionDTO> queryTypeAndDimensionById(@PathVariable("expAdjustTypeId") Long expAdjustTypeId){
        return ResponseEntity.ok(expenseAdjustTypeService.queryTypeAndDimensionById(expAdjustTypeId, true));
    }
}
