package com.hand.hcf.app.expense.travel.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignCompany;
import com.hand.hcf.app.expense.travel.service.TravelApplicationTypeAssignCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "差旅申请单关联公司")
@RestController
@RequestMapping("/api/travel/application/type/company")
public class TravelApplicationTypeAssignCompanyController {

    @Autowired
    private TravelApplicationTypeAssignCompanyService assignCompanyService;


    @PostMapping
    @ApiOperation(value = "【差旅申请单关联公司】创建", notes = "【差旅申请单关联公司】创建 开发:shouting")
    public List<TravelApplicationTypeAssignCompany> createAssignCompanyBatch(@ApiParam(value = "差旅申请单ID") @RequestParam(value = "travelTypeId") Long travelTypeId,
                                                                             @ApiParam(value = "公司ID集合") @RequestBody List<Long> companyIds){
        return assignCompanyService.createAssignCompanyBatch(travelTypeId, companyIds);
    }


    @PutMapping("/update/status")
    @ApiOperation(value = "【差旅申请单关联公司】更新状态", notes = "【差旅申请单关联公司】更新状态 开发:shouting")
    public TravelApplicationTypeAssignCompany updateAssignCompanyStatus(@ApiParam(value = "ID") @RequestParam(value = "id") Long id,
                                                                        @ApiParam(value = "是否启用") @RequestParam(value = "enabled") Boolean enabled){
        return assignCompanyService.updateAssignCompanyStatus(id, enabled);
    }


    @GetMapping("/pageAssignCompany")
    @ApiOperation(value = "【差旅申请单关联公司】分页查询", notes = "【差旅申请单关联公司】分页查询 开发:shouting")
    public ResponseEntity<List<TravelApplicationTypeAssignCompany>> pageAssignCompany(@ApiParam(value = "差旅申请单类型ID") @RequestParam(value = "travelTypeId") Long travelTypeId,
                                                                                      @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                      @ApiParam(value = "每页多少条") @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<TravelApplicationTypeAssignCompany> result = assignCompanyService.pageAssignCompany(travelTypeId, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/{travelTypeId}/query/filter")
    @ApiOperation(value = "【差旅申请单关联公司】未分配公司查询", notes = "【差旅申请单关联公司】未分配公司查询 开发:shouting")
    public ResponseEntity<List<CompanyCO>> pageCompanyByConditionFilter(@PathVariable("travelTypeId") Long travelTypeId,
                                                                        @ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId,
                                                                        @ApiParam(value = "公司代码") @RequestParam(value = "companyCode", required = false) String companyCode,
                                                                        @ApiParam(value = "公司名称") @RequestParam(value = "companyName", required = false) String companyName,
                                                                        @ApiParam(value = "公司代码从") @RequestParam(value = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                        @ApiParam(value = "公司代码到") @RequestParam(value = "companyCodeTo", required = false) String companyCodeTo,
                                                                        @ApiParam(value = "分页page") @RequestParam(value = "page",defaultValue = "0") int page,
                                                                        @ApiParam(value = "分页size") @RequestParam(value = "size",defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<CompanyCO> result = assignCompanyService.pageCompanyByConditionFilter(travelTypeId, setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }
}
