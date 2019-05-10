package com.hand.hcf.app.expense.policy.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.policy.dto.ExpensePolicyDTO;
import com.hand.hcf.app.expense.policy.service.ExpensePolicyService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 13:43
 */
@Api(tags = "费用政策控制器")
@RestController
@RequestMapping("/api/expense/policy")
public class ExpensePolicyController {
    @Autowired
    private ExpensePolicyService expensePolicyService;

    /**
     * 新增费用政策
     * @param expensePolicyDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增费用政策", notes = "新增费用政策 开发:zhanhua.cheng")
    public ResponseEntity<ExpensePolicyDTO> insertExpensePolicy(@ApiParam(value = "费用政策") @RequestBody ExpensePolicyDTO expensePolicyDTO){
        return ResponseEntity.ok(expensePolicyService.insertExpensePolicy(expensePolicyDTO));
    }

    /**
     * 更新费用政策
     *
     * @param expensePolicyDTO
     * @return ResponseEntity<ExpensePolicyDTO>
     */
    @PutMapping
    @ApiOperation(value = "更新费用政策", notes = "更新费用政策 开发:zhanhua.cheng")
    public ResponseEntity<ExpensePolicyDTO> updateCodingRule(@ApiParam(value = "费用政策") @RequestBody ExpensePolicyDTO expensePolicyDTO) {
        return ResponseEntity.ok(expensePolicyService.updateExpensePolicy(expensePolicyDTO));
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除费用调整类型", notes = "删除费用调整类型 开发:zhanhua.cheng")
    public ResponseEntity deleteExpenseAdjustType(@PathVariable Long id){
        expensePolicyService.deleteExpensePolicyById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID获取报告头", notes = "根据ID获取报告头 开发:zhanhua.cheng")
    public ExpensePolicyDTO getTraReportHeaderById(@PathVariable(value = "id") Long id){
        return expensePolicyService.getExpensePolicyById(id);
    }

    /**
     * 按条件查询获得分页数据
     * @param setOfBooksId
     * @param expenseTypeId
     * @param dutyType
     * @param companyLevelId
     * @param {Integer} typeFlag 类型类别 0-申请 1-费用
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/pageByCondition")
    @ApiOperation(value = "按条件查询获得分页数据", notes = "按条件查询获得分页数据 开发:zhanhua.cheng")
    public ResponseEntity<List<ExpensePolicyDTO>> pageExpensePolicyByCond(@ApiParam(value = "账套ID") @RequestParam(required = false) Long setOfBooksId,
                                                                          @ApiParam(value = "费用类型ID") @RequestParam(required = false) Long expenseTypeId,
                                                                          @ApiParam(value = "税收类型") @RequestParam(required = false) String dutyType,
                                                                          @ApiParam(value = "公司等级ID") @RequestParam(required=false)Long companyLevelId,
                                                                          @ApiParam(value = "类型标识") @RequestParam(value = "typeFlag",defaultValue = "0")Integer typeFlag,
                                                                          @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0")int page,
                                                                          @ApiParam(value = "每页多少条") @RequestParam(value="size",defaultValue = "10") int size){
        Page mybatisPage = PageUtil.getPage(page,size);
        List<ExpensePolicyDTO> headerDTOS = expensePolicyService.pageExpensePolicyByCond(setOfBooksId,expenseTypeId,dutyType,companyLevelId,typeFlag, mybatisPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(headerDTOS,httpHeaders, HttpStatus.OK);
    }
}
