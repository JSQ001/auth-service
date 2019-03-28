package com.hand.hcf.app.mdata.dimension.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemAssignCompany;
import com.hand.hcf.app.mdata.dimension.service.DimensionItemAssignCompanyService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dimension/item/assign/company")
public class DimensionItemAssignCompanyController {

    @Autowired
    private DimensionItemAssignCompanyService dimensionItemAssignCompanyService;

    /**
     * @api {POST} /api/dimension/item/assign/company/batch 【维值-维值关联公司】批量创建
     * @apiDescription 分配公司弹窗点击确定
     * @apiGroup Dimension
     * @apiParam {Object} dimensionItemAssignCompany  维值关联公司定义
     * @apiParam {String} dimensionItemAssignCompany.companyCode  公司代码
     * @apiParam {Long} dimensionItemAssignCompany.companyId  公司id
     * @apiParam {Long} dimensionItemAssignCompany.dimensionItemId  维值id
     * @apiParam {Boolean} dimensionItemAssignCompany.enabled  是否启用
     * @apiParamExample {json} 请求参数:
     * [
     *  {
     *      "companyCode":"company1",
     *      "companyId":"2342",
     *      "dimensionItemId":"23425232543345",
     *      "enabled":true
     *  }
     * ]
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077181160250990593",
     *         "createdDate": "2018-12-24T20:36:16.32+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T20:36:16.32+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "dimensionItemId": "1077180351433990145",
     *         "companyId": "2342",
     *         "companyCode": "company1",
     *         "companyName": null,
     *         "companyType": null
     *     }
     * ]
     */
    @PostMapping(value = "/batch")
    public List<DimensionItemAssignCompany> insertDimensionItemAssignCompanyBatch(@RequestBody List<DimensionItemAssignCompany> list){
        return dimensionItemAssignCompanyService.insertDimensionItemAssignCompanyBatch(list);
    }

    /**
     * @api {PUT} /api/dimension/item/assign/company/batch 【维值-维值关联公司】启用状态批量更新
     * @apiDescription 分配公司界面点击启用图标切换启用状态
     * @apiGroup Dimension
     * @apiParam {Object} dimensionItemAssignCompany  维值关联公司定义
     * @apiParamExample {json} 请求参数:
     * [
     *  {
     *      "id": "1077184088189755393",
     *      "enabled":false
     *  }
     * ]
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077184088189755393",
     *         "createdDate": null,
     *         "createdBy": null,
     *         "lastUpdatedDate": "2018-12-24T20:58:02.719+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": null,
     *         "enabled": false,
     *         "dimensionItemId": null,
     *         "companyId": null,
     *         "companyCode": null,
     *         "companyName": null,
     *         "companyType": null
     *     }
     * ]
     */
    @PutMapping(value = "/batch")
    public List<DimensionItemAssignCompany> updateStatusBatch(@RequestBody List<DimensionItemAssignCompany> list){
        return dimensionItemAssignCompanyService.updateStatusBatch(list);
    }

    /**
     * @api {GET} /api/dimension/item/assign/company/query 【维值-维值关联公司】公司条件查询
     * @apiDescription 分配公司界面条件查询
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemId  维值id
     * @apiParam {Boolean} [enabled]  是否启用
     * @apiParam  {int} page 分页page
     * @apiParam  {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077181160250990593",
     *         "createdDate": "2018-12-24T20:36:16.32+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T20:37:27.753+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 2,
     *         "enabled": false,
     *         "dimensionItemId": "1077180351433990145",
     *         "companyId": "2342",
     *         "companyCode": "company1",
     *         "companyName": null,
     *         "companyType": null
     *     },
     *     {
     *         "id": "1077184088189755393",
     *         "createdDate": "2018-12-24T20:47:54.395+08:00",
     *         "createdBy": "1",
     *         "lastUpdatedDate": "2018-12-24T20:58:02.719+08:00",
     *         "lastUpdatedBy": "1",
     *         "versionNumber": 4,
     *         "enabled": false,
     *         "dimensionItemId": "1077180351433990145",
     *         "companyId": "23442",
     *         "companyCode": "company2",
     *         "companyName": null,
     *         "companyType": null
     *     }
     * ]
     */
    @GetMapping(value = "/query")
    public ResponseEntity<List<DimensionItemAssignCompany>> pageDimensionItemAssignCompanyByCond(
                                            @RequestParam(value = "dimensionItemId") Long dimensionItemId,
                                            @RequestParam(value = "enabled",required = false) Boolean enabled,
                                            @RequestParam(value = "page",defaultValue = "0") int page,
                                            @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<DimensionItemAssignCompany> result = dimensionItemAssignCompanyService.pageDimensionItemAssignCompanyByCond(dimensionItemId,enabled,queryPage);

        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/assign/company/query");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/dimension/item/assign/company/filter 【维值-维值关联公司】分配公司条件查询
     * @apiDescription 批量分配公司弹窗的条件查询
     * @apiGroup Dimension
     * @apiParam {Long} dimensionItemId  维值id
     * @apiParam {String} [companyCode]  公司代码
     * @apiParam {String} [companyName]  公司名称
     * @apiParam {String} [companyCodeFrom]  公司代码从
     * @apiParam {String} [companyCodeTo]  公司代码到
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *  {
     *      "id": null,
     *      "companyOid": null,
     *      "name": "公司1",
     *      "setOfBooksId": null,
     *      "setOfBooksName": null,
     *      "legalEntityId": null,
     *      "companyCode": "company1",
     *      "address": null,
     *      "companyLevelId": null,
     *      "parentCompanyId": null,
     *      "companyTypeId": null,
     *      "companyTypeName": "公司类型1",
     *      "tenantId": 2332
     *  }
     * ]
     */
    @GetMapping(value = "/filter")
    public ResponseEntity<List<CompanyCO>> pageCompanyByCond(@RequestParam(value = "dimensionItemId") Long dimensionItemId,
                                                             @RequestParam(value = "companyCode", required = false) String companyCode,
                                                             @RequestParam(value = "companyName", required = false) String companyName,
                                                             @RequestParam(value = "companyCodeFrom", required = false) String companyCodeFrom,
                                                             @RequestParam(value = "companyCodeTo", required = false) String companyCodeTo,
                                                             @RequestParam(value = "page",defaultValue = "0") int page,
                                                             @RequestParam(value = "size",defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<CompanyCO> result = dimensionItemAssignCompanyService.pageCompanyByCond(dimensionItemId, companyCode, companyCodeFrom, companyCodeTo, companyName, queryPage);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/assign/company/filter");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/dimension/item/assign/company/filter/by/setOfBooksId 【维值-维值关联公司】分配公司条件查询（账套下所有公司）
     * @apiDescription 批量分配公司弹窗的条件查询
     * @apiGroup Dimension
     * @apiParam {Long} setOfBooksId  账套id
     * @apiParam {String} [companyCode]  公司代码
     * @apiParam {String} [companyName]  公司名称
     * @apiParam {String} [companyCodeFrom]  公司代码从
     * @apiParam {String} [companyCodeTo]  公司代码到
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *  {
     *      "id": null,
     *      "companyOid": null,
     *      "name": "公司1",
     *      "setOfBooksId": null,
     *      "setOfBooksName": null,
     *      "legalEntityId": null,
     *      "companyCode": "company1",
     *      "address": null,
     *      "companyLevelId": null,
     *      "parentCompanyId": null,
     *      "companyTypeId": null,
     *      "companyTypeName": "公司类型1",
     *      "tenantId": 2332
     *  }
     * ]
     */
    @GetMapping(value = "/filter/by/setOfBooksId")
    public ResponseEntity<List<CompanyCO>> pageCompanyBySetOfBooksId(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                     @RequestParam(value = "companyCode", required = false) String companyCode,
                                                                     @RequestParam(value = "companyName", required = false) String companyName,
                                                                     @RequestParam(value = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                     @RequestParam(value = "companyCodeTo", required = false) String companyCodeTo,
                                                                     @RequestParam(value = "page",defaultValue = "0") int page,
                                                                     @RequestParam(value = "size",defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<CompanyCO> result = dimensionItemAssignCompanyService.pageCompanyBySetOfBooksId(setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName, queryPage);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/assign/company/filter/by/setOfBooksId");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }
}
