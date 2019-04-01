package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignCompany;
import com.hand.hcf.app.expense.travel.service.TravelApplicationTypeAssignCompanyService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@RestController
@RequestMapping("/api/travel/application/type/company")
public class TravelApplicationTypeAssignCompanyController {

    @Autowired
    private TravelApplicationTypeAssignCompanyService assignCompanyService;

    /**
     * @api {POST} /api/travel/application/type/company 【差旅申请单关联公司】创建
     * @apiParam {Long} travelTypeId  差旅申请单ID
     * @apiParam {List} companyIds  公司ID集合
     * @apiGroup Travel
     */
    @PostMapping
    public List<TravelApplicationTypeAssignCompany> createAssignCompanyBatch(@RequestParam(value = "travelTypeId") Long travelTypeId,
                                                                             @RequestBody List<Long> companyIds){
        return assignCompanyService.createAssignCompanyBatch(travelTypeId, companyIds);
    }

    /**
     * @api {PUT} /api/travel/application/type/company/update/status 【差旅申请单关联公司】更新状态
     * @apiParam {Long} id  差旅申请单关联公司ID
     * @apiParam {Boolean} enabled  是否启用
     * @apiGroup Travel
     */
    @PutMapping("/update/status")
    public TravelApplicationTypeAssignCompany updateAssignCompanyStatus(@RequestParam(value = "id") Long id,
                                                                        @RequestParam(value = "enabled") Boolean enabled){
        return assignCompanyService.updateAssignCompanyStatus(id, enabled);
    }

    /**
     * @api {GET} /api/travel/application/type/company/pageAssignCompany 【差旅申请单关联公司】分页查询
     * @apiParam {Long} travelTypeId  差旅申请单ID
     * @apiParam {int} [page]
     * @apiParam {int} [size]
     * @apiGroup Travel
     */
    @GetMapping("/pageAssignCompany")
    public ResponseEntity<List<TravelApplicationTypeAssignCompany>> pageAssignCompany(@RequestParam(value = "travelTypeId") Long travelTypeId,
                                                                                      @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                      @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<TravelApplicationTypeAssignCompany> result = assignCompanyService.pageAssignCompany(travelTypeId, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/travel/application/type/company/{travelTypeId}/query/filter 【差旅申请单关联公司】未分配公司查询
     * @apiGroup Travel
     * @apiParam {Long} travelTypeId  差旅申请单ID
     * @apiParam {Long} setOfBooksId  账套ID
     * @apiParam {String} [companyCode]  公司代码
     * @apiParam {String} [companyName]  公司名称
     * @apiParam {String} [companyCodeFrom]  公司代码从
     * @apiParam {String} [companyCodeTo]  公司代码到
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     */
    @GetMapping("/{travelTypeId}/query/filter")
    public ResponseEntity<List<CompanyCO>> pageCompanyByConditionFilter(@PathVariable("travelTypeId") Long travelTypeId,
                                                                        @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                        @RequestParam(value = "companyCode", required = false) String companyCode,
                                                                        @RequestParam(value = "companyName", required = false) String companyName,
                                                                        @RequestParam(value = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                        @RequestParam(value = "companyCodeTo", required = false) String companyCodeTo,
                                                                        @RequestParam(value = "page",defaultValue = "0") int page,
                                                                        @RequestParam(value = "size",defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<CompanyCO> result = assignCompanyService.pageCompanyByConditionFilter(travelTypeId, setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }
}
