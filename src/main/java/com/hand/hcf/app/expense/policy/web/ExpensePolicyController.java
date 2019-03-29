package com.hand.hcf.app.expense.policy.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.policy.dto.ExpensePolicyDTO;
import com.hand.hcf.app.expense.policy.service.ExpensePolicyService;
import com.hand.hcf.core.util.PageUtil;
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
    public ResponseEntity<ExpensePolicyDTO> insertExpensePolicy(@RequestBody ExpensePolicyDTO expensePolicyDTO){
        return ResponseEntity.ok(expensePolicyService.insertExpensePolicy(expensePolicyDTO));
    }

    /**
     * 更新费用政策
     *
     * @param expensePolicyDTO
     * @return ResponseEntity<ExpensePolicyDTO>
     */
    @PutMapping
    public ResponseEntity<ExpensePolicyDTO> updateCodingRule(@RequestBody ExpensePolicyDTO expensePolicyDTO) {
        return ResponseEntity.ok(expensePolicyService.updateExpensePolicy(expensePolicyDTO));
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteExpenseAdjustType(@PathVariable Long id){
        expensePolicyService.deleteExpensePolicyById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<List<ExpensePolicyDTO>> pageExpensePolicyByCond(@RequestParam(required = false) Long setOfBooksId,
                                                                          @RequestParam(required = false) Long expenseTypeId,
                                                                          @RequestParam(required = false) String dutyType,
                                                                          @RequestParam(required=false)Long companyLevelId,
                                                                          @RequestParam(value = "typeFlag",defaultValue = "0")Integer typeFlag,
                                                                          @RequestParam(value = "page",defaultValue = "0")int page,
                                                                          @RequestParam(value="size",defaultValue = "10") int size){
        Page mybatisPage = PageUtil.getPage(page,size);
        List<ExpensePolicyDTO> headerDTOS = expensePolicyService.pageExpensePolicyByCond(setOfBooksId,expenseTypeId,dutyType,companyLevelId,typeFlag, mybatisPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(headerDTOS,httpHeaders, HttpStatus.OK);
    }
}
