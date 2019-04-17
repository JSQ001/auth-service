package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeCompany;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeCompanyService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/26
 */
@RestController
@RequestMapping("/api/expense/report/type/company")
public class ExpenseReportTypeCompanyController {
    private final ExpenseReportTypeCompanyService expenseReportTypeCompanyService;

    public ExpenseReportTypeCompanyController(ExpenseReportTypeCompanyService expenseReportTypeCompanyService){
        this.expenseReportTypeCompanyService = expenseReportTypeCompanyService;
    }

    /**
     * 批量新增 报账单类型关联公司表
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/expense/report/type/company/batch 【报账单类型关联公司】批量新增
     * @apiDescription 报账单类型批量分配公司
     * @apiGroup ReportTypeService
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiParam {Long} companyId 公司ID
     * @apiParam {String} companyCode 公司代码
     * @apiParam {Boolean} enabled    启用标志
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} companyId 公司ID
     * @apiSuccess {String} companyCode 公司代码
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccess {Boolean} deleted    删除标志
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    [
    {
    "reportTypeId":1100287347849232386,
    "companyId":1085765396731101185,
    "companyCode":"CST1",
    "enabled":true
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1100315634151542786",
    "createdDate": "2019-02-26T16:44:24.722+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-02-26T16:44:24.722+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "enabled": true,
    "reportTypeId": "1100287347849232386",
    "companyId": "1085765396731101185",
    "companyCode": "CST1",
    "companyName": null,
    "companyType": null
    }
    ]
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ExpenseReportTypeCompany>> createExpenseReportTypeCompanyBatch(@RequestBody List<ExpenseReportTypeCompany> list){
        return ResponseEntity.ok(expenseReportTypeCompanyService.createExpenseReportTypeCompanyBatch(list));
    }

    /**
     * 单个修改 报账单类型关联公司表
     *
     * @param expenseReportTypeCompany
     * @return
     */
    /**
     * @api {PUT} /api/expense/report/type/company 【报账单类型关联公司】单个修改
     * @apiDescription 批量修改报账单类型关联公司
     * @apiGroup ReportTypeService
     * @apiParam {Long} id 主键id
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiParam {Long} companyId 公司ID
     * @apiParam {String} companyCode 公司代码
     * @apiParam {Boolean} enabled    启用标志
     * @apiParam {Long} versionNumber 版本号
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} companyId 公司ID
     * @apiSuccess {String} companyCode 公司代码
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccess {Boolean} deleted    删除标志
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiParamExample {json} 请求参数:
    {
    "id":1100315635573411842,
    "enabled":false,
    "versionNumber":1
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "id": "1100315635573411842",
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "versionNumber": 2,
    "enabled": false,
    "reportTypeId": null,
    "companyId": null,
    "companyCode": null,
    "companyName": null,
    "companyType": null
    }
     */
    @PutMapping()
    public ResponseEntity<ExpenseReportTypeCompany> updateExpenseReportTypeCompany(@RequestBody ExpenseReportTypeCompany expenseReportTypeCompany){
        return ResponseEntity.ok(expenseReportTypeCompanyService.updateExpenseReportTypeCompany(expenseReportTypeCompany));
    }

    /**
     * 根据报账单类型ID->reportTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param reportTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     *
     * @api {GET} /api/expense/report/type/company/query?reportTypeId=1100287347849232386 【报账单类型关联公司】分页查询
     * @apiDescription 根据报账单类型ID->reportTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     * @apiGroup ReportTypeService
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} reportTypeId 报账单类型ID
     * @apiSuccess {Long} companyId 公司ID
     * @apiSuccess {String} companyCode 公司代码
     * @apiSuccess {String} companyName 公司名称
     * @apiSuccess {String} companyType 公司类型
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {Boolean} enabled    启用标志
     * @apiSuccess {Boolean} deleted    删除标志
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiSuccessExample {json} Success-Result
     * [
    {
    "id": "1100315634151542786",
    "createdDate": "2019-02-26T16:44:24.722+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-02-26T16:44:24.722+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "enabled": true,
    "reportTypeId": "1100287347849232386",
    "companyId": "1085765396731101185",
    "companyCode": "CST1",
    "companyName": "CST测试1",
    "companyType": "业务实体"
    },
    {
    "id": "1100315635573411842",
    "createdDate": "2019-02-26T16:44:25.053+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-02-26T16:46:38.198+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "enabled": false,
    "reportTypeId": "1100287347849232386",
    "companyId": "1085783889264308225",
    "companyCode": "CST2",
    "companyName": "CST测试2",
    "companyType": "业务实体"
    }
    ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<ExpenseReportTypeCompany>> getExpenseReportTypeCompanyByCond(
            @RequestParam(value = "reportTypeId") Long reportTypeId,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportTypeCompany> list = expenseReportTypeCompanyService.getExpenseReportTypeCompanyByCond(reportTypeId,page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param reportTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/expense/report/type/company/query/filter?reportTypeId=1100287347849232386&companyCode&companyName&companyCodeFrom&companyCodeTo&page=0&size=10 【报账单类型关联公司】分页查询未分配的公司
     * @apiDescription 分配页面的公司筛选查询
     * @apiGroup ReportTypeService
     * @apiParam {Long} reportTypeId 报账单类型ID
     * @apiParam {Long} [companyName] 公司名称
     * @apiParam {Long} [companyCode] 公司代码
     * @apiParam {Long} [companyCodeFrom] 公司代码从
     * @apiParam {Long} [companyCodeTo] 公司代码至
     * @apiParam {Long} page
     * @apiParam {Long} size
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {String} companyCode 公司代码
     * @apiSuccess {String} name 公司名称
     * @apiSuccess {String} companyTypeName 公司类型名称
     * @apiSuccessExample {json} 成功返回值
     * [
    {
    "id": "1085727901859676161",
    "companyOid": "d7294a95-5b4f-4de2-93b6-3ead152320b1",
    "name": "1",
    "setOfBooksId": "1085717261577322498",
    "setOfBooksName": "融晶账套001",
    "legalEntityId": "1085717874922979330",
    "companyCode": "1",
    "address": null,
    "companyLevelId": null,
    "parentCompanyId": null,
    "companyTypeId": "1085713595080343553",
    "companyTypeName": "业务实体",
    "tenantId": "1085713586410717186",
    "baseCurrency": null
    }
    ]
     */
    @GetMapping("/filter")
    public ResponseEntity<List<CompanyCO>> assignCompanyQuery(@RequestParam Long reportTypeId,
                                             @RequestParam(required = false) String companyCode,
                                             @RequestParam(required = false) String companyName,
                                             @RequestParam(required = false) String companyCodeFrom,
                                             @RequestParam(required = false) String companyCodeTo,
                                             Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CompanyCO> result = expenseReportTypeCompanyService.assignCompanyQuery(reportTypeId, companyCode, companyName, companyCodeFrom, companyCodeTo, page);
        HttpHeaders headers = PageUtil.getTotalHeader(result);
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
