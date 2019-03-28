package com.hand.hcf.app.expense.adjust.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustTypeAssignCompany;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustTypeAssignCompanyService;
import com.hand.hcf.app.mdata.client.com.CompanyCO;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@RestController
@RequestMapping("/api/expense/adjust/type/assign/companies")
public class ExpenseAdjustTypeAssignCompanyController {
    @Autowired
    private  ExpenseAdjustTypeAssignCompanyService expenseAdjustTypeAssignCompanyService;

    /**
     * 批量新增 费用调整单类型关联的公司表
     *
     * @param list
     * @return
     */
    @RequestMapping(value = "/batch",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExpenseAdjustTypeAssignCompany>> createExpenseAdjustTypeAssignCompanyBatch(@RequestBody List<ExpenseAdjustTypeAssignCompany> list){
        return ResponseEntity.ok(expenseAdjustTypeAssignCompanyService.createExpenseAdjustTypeAssignCompanyBatch(list));
    }

    /**
     * 批量新增 费用调整单类型关联的公司表
     *
     * @param list
     * @return
     */
    @PutMapping(value = "/batch")
    public ResponseEntity updateExpenseAdjustTypeAssignCompanyBatch(@RequestBody List<ExpenseAdjustTypeAssignCompany> list){
        return ResponseEntity.ok(expenseAdjustTypeAssignCompanyService.updateCompanyEnbaled(list));
    }
    /**
     * 根据费用调整单类型ID->expAdjustTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param expAdjustTypeId
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/query",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExpenseAdjustTypeAssignCompany>> getExpenseAdjustTypeAssignCompanyByCond(
            @RequestParam(value = "expAdjustTypeId") Long expAdjustTypeId,
            @RequestParam(value = "enabled",required = false) Boolean enabled,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ExpenseAdjustTypeAssignCompany> list = expenseAdjustTypeAssignCompanyService.getExpenseAdjustTypeAssignCompanyByCond(expAdjustTypeId,enabled,page);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link","/api/expense/adjust/type/assign/companies/query");
        return  new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param expAdjustTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/filter",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyCO>> assignCompanyQuery(@RequestParam Long expAdjustTypeId,
                                                              @RequestParam(required = false) String companyCode,
                                                              @RequestParam(required = false) String companyName,
                                                              @RequestParam(required = false) String companyCodeFrom,
                                                              @RequestParam(required = false) String companyCodeTo,
                                                              Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> list = expenseAdjustTypeAssignCompanyService.assignCompanyQuery(expAdjustTypeId, companyCode, companyName, companyCodeFrom, companyCodeTo, page);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + list.getTotal());
        headers.add("Link","/api/expense/adjust/type/assign/companies/filter");
        return  new ResponseEntity<>(list.getRecords(), headers, HttpStatus.OK);
    }

}
