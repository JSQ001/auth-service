package com.hand.hcf.app.mdata.responsibilityCenter.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityAssignCompany;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.enums.ResponsibilityCenterImportCode;

//import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityLov;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.ResponsibilityLovDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.service.ResponsibilityAssignCompanyService;
import com.hand.hcf.app.mdata.responsibilityCenter.service.ResponsibilityCenterService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.itextpdf.text.io.StreamUtil;
import io.swagger.annotations.*;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/responsibilityCenter")
@Api(tags = "责任中心")
public class ResponsibilityCenterResource {
    @Autowired
    private ResponsibilityCenterService responsibilityCenterService;

    @Autowired
    private ResponsibilityAssignCompanyService assignCompanyService;
    /**
     * @api {POST} /api/responsibilityCenter/insertOrUpdate 【责任中心-新增】
     * @apiGroup ResponsibilityCenter
     * @apiParam {Long} tenantId 租户id
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {String} responsibilityCenterCode 责任中心代码
     * @apiParam {String} responsibilityCenterName 责任中心名称
     * @apiParam {String} responsibilityCenterType 责任中心类型
     * @apiSuccessExample {json} Success-Response:
     *  {
     *   "id": "1080291279696302082",
     *   "createdDate": "2019-01-02T10:34:46.582+08:00",
     *   "createdBy": "1",
     *   "lastUpdatedDate": "2019-01-02T10:34:46.583+08:00",
     *   "lastUpdatedBy": "1",
     *   "versionNumber": 1,
     *   "enabled": true,
     *   "tenantId": "1",
     *   "setOfBooksId": "1",
     *   "responsibilityCenterCode": "test3",
     *   "responsibilityCenterName": "测试3",
     *   "responsibilityCenterType": null
     *   }
     */
    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponsibilityCenter> insertOrUpdateResponsibilityCenter(@RequestBody ResponsibilityCenter responsibilityCenter){
        return ResponseEntity.ok(responsibilityCenterService.insertOrUpdateResponsibilityCenter(responsibilityCenter));
    }

    /**
     * @api {GET} /api/responsibilityCenter/{responsibilityCenterId} 【责任中心-详情】获取责任中心详情
     * @apiGroup ResponsibilityCenter
     * @apiParam responsibilityCenterId 责任中心Id
     * @apiParamExample json} Request-Param:
     *  http://127.0.0.1:9083/api/responsibilityCenter/1083343376337649665
     * @apiSuccessExample
     * {
            "i18n": null,
            "id": "1083343376337649665",
            "deleted": false,
            "createdDate": "2019-01-10T20:42:43.123+08:00",
            "createdBy": "1",
            "lastUpdatedDate": "2019-01-10T20:42:43.123+08:00",
            "lastUpdatedBy": "1",
            "versionNumber": 1,
            "enabled": true,
            "tenantId": "1",
            "setOfBooksId": "1078107093880250370",
            "setOfBooksName":"测试账套",
            "responsibilityCenterCode": "1001",
            "responsibilityCenterName": "测试1",
            "responsibilityCenterType": null
            }
     */
    @GetMapping(value = "/{responsibilityCenterId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponsibilityCenter getResponsibilityCenterById(@PathVariable(value = "responsibilityCenterId") Long responsibilityCenterId){
        return responsibilityCenterService.getResponsibilityCenterById(responsibilityCenterId);
    }


    /**
     * @api {GET}/api/responsibilityCenter/query 【责任中心-查询】根据账套
     * @apiGroup ResponsibilityCenter
     * @apiParam {String} keyword 责任中心名称或者代码
     * @apiParam {String} codeFrom 责任中心代码从
     * @apiParam {String} codeTo 责任中心代码至
     * @apiParam {String} responsibilityCenterCode 责任中心代码
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {String} responsibilityCenterName 责任中心名称
     * @apiParam {Boolean}enabled 启用 禁用
     * @apiParam pageable 分页
     * @apiParamExample {json} Request-Param:
     * http://localhost:9083/api/responsibilityCenter/query?setOfBooksId=1078107093880250370&codeFrom=1004&enabled=false
     * @apiSuccessExample {json} Success-Response:
     *[
     *   {
     *   "id": "1080290498884669442",
     *   "createdDate": "2019-01-02T10:31:40.464+08:00",
     *   "createdBy": "1",
     *   "lastUpdatedDate": "2019-01-02T10:31:40.468+08:00",
     *   "lastUpdatedBy": "1",
     *   "versionNumber": 1,
     *   "enabled": null,
     *   "tenantId": "1",
     *   "setOfBooksId": "1",
     *   },
     *   {
     *   "id": "1080290737754476545",
     *   "createdDate": "2019-01-02T10:32:37.369+08:00",
     *   "createdBy": "1",
     *  "lastUpdatedDate": "2019-01-02T10:32:37.369+08:00",
     *   "lastUpdatedBy": "1",
     *   "versionNumber": 1,
     *   "enabled": null,
     *   "tenantId": "1",
     *   "setOfBooksId": "1",
     *   "responsibilityCenterCode": "test1",
     *   "responsibilityCenterName": "测试1",
     *   "responsibilityCenterType": null
     *   },
     *    ]
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResponsibilityCenter>> pageResponsibilityCenterBySetOfBooksId(@RequestParam(value = "keyword",required = false) String keyword,
                                                                                              @RequestParam(value="codeFrom",required = false) String codeFrom,
                                                                                              @RequestParam(value = "codeTo",required = false) String codeTo,
                                                                                              @RequestParam(value="setOfBooksId") Long setOfBooksId,
                                                                                              @RequestParam(value = "responsibilityCenterCode",required = false) String responsibilityCenterCode,
                                                                                              @RequestParam(value = "responsibilityCenterName",required = false) String responsibilityCenterName,
                                                                                              @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                              Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<ResponsibilityCenter> result = responsibilityCenterService.pageResponsibilityCenterBySetOfBooksId(keyword,codeFrom,codeTo,setOfBooksId,responsibilityCenterCode,responsibilityCenterName,enabled,page,false);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/responsibilityCenter/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     *
     * @param keyword  责任中心名称或者代码
     * @param codeFrom 责任中心代码从
     * @param codeTo   责任中心代码至
     * @param setOfBooksId 账套id
     * @param responsibilityCenterCode 责任中心代码
     * @param responsibilityCenterName 责任中心名称
     * @param enabled  启用 禁用
     * @param pageable 分页
     * @return   责任中心
     */
    @ApiOperation(value = "【责任中心-查询】根据账套", notes = "责任中心查询 数据权限控制 开发：王帅")
    @GetMapping(value = "/query/enable/dataAuth",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ResponsibilityCenter>> pageResponsibilityCenterBySetOfBooksIdDataAuth(
                                                                                             @ApiParam(value = "责任中心名称或者代码")
                                                                                             @RequestParam(value = "keyword",required = false) String keyword,
                                                                                             @ApiParam(value = "责任中心代码从")
                                                                                             @RequestParam(value="codeFrom",required = false) String codeFrom,
                                                                                             @ApiParam(value = "责任中心代码至")
                                                                                             @RequestParam(value = "codeTo",required = false) String codeTo,
                                                                                             @ApiParam(value = "账套id")
                                                                                             @RequestParam(value="setOfBooksId") Long setOfBooksId,
                                                                                             @ApiParam(value = "责任中心代码")
                                                                                             @RequestParam(value = "responsibilityCenterCode",required = false) String responsibilityCenterCode,
                                                                                             @ApiParam(value = "责任中心名称")
                                                                                             @RequestParam(value = "responsibilityCenterName",required = false) String responsibilityCenterName,
                                                                                             @ApiParam(value = "启用 禁用")
                                                                                             @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                             @ApiIgnore  Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<ResponsibilityCenter> result = responsibilityCenterService.pageResponsibilityCenterBySetOfBooksId(keyword,codeFrom,codeTo,setOfBooksId,responsibilityCenterCode,responsibilityCenterName,enabled,page,true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/responsibilityCenter/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/responsibilityCenter/query/default 【责任中心-获取默认或者可用责任中心】
     * @apiDescription 获取当前所选账套下所有启用的责任中心，如果选择了公司，则只能选到分配给此公司的责任中心
     * @apiGroup ResponsibilityCenter
     * @apiParam   {Long} [setOfBooksId] 账套id  必传
     * @apiParam  {Long}  [companyId] 公司id
     * @apiParam  {String} [info] 责任中心代码或名称
     * @apiParam  {String} [codeFrom] 责任中心代码从
     * @apiParam  {String} [codeTo] 责任中心代码至
     * @apiParamExample
     *    http://localhost:9083/api/responsibilityCenter/query/default?setOfBooksId=1078107093880250370&companyId=2
     * @apiSuccessExample {json} 成功返回值:
     *[
            {
            "i18n": null,
            "id": "1081475244784463873",
            "deleted": false,
            "createdDate": "2019-01-05T16:59:25.854+08:00",
            "createdBy": "1",
            "lastUpdatedDate": "2019-01-05T16:59:25.854+08:00",
            "lastUpdatedBy": "1",
            "versionNumber": 1,
            "enabled": true,
            "tenantId": "1",
            "setOfBooksId": "1078107093880250370",
            "responsibilityCenterCode": "1001",
            "responsibilityCenterName": "测试",
            "responsibilityCenterType": null
            }
       ]
     */
    @RequestMapping(value = "/query/default",method ={ RequestMethod.POST,RequestMethod.GET},produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<List<ResponsibilityCenter>> pageDefaultResponsibilityCenter(@RequestParam(value="setOfBooksId",required = false) Long setOfBooksId,
                                                                                       @RequestParam(required = false) Long companyId,
                                                                                       @RequestParam(required = false) String info,
                                                                                       @RequestParam(required = false) String codeFrom,
                                                                                       @RequestParam(required = false) String codeTo,
                                                                                       @RequestBody(required = false) List<Long> ids,
                                                                                       @RequestParam(required = false,defaultValue ="true") Boolean enabled,
                                                                                       Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<ResponsibilityCenter> result = responsibilityCenterService.pageDefaultResponsibilityCenter(setOfBooksId,companyId,info,codeFrom,codeTo,ids,enabled,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        return new ResponseEntity<>(result.getRecords(),headers,HttpStatus.OK);
    }



    /**
     * @api {delete} /api/responsibilityCenter/delete/{id} 【责任中心-删除】
     * @apiDescription 删除责任中心组以及责任中心关联
     * @apiGroup  ResponsibilityCenter
     * @apiParam {Long} [id]  待删除的id数组
     * @apiSuccess {Boolean} success 是否成功
     *
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> delecteResponsibilityCenterById(@PathVariable Long id){
        return ResponseEntity.ok(responsibilityCenterService.delecteResponsibilityCenterById(id));
    }

    /**
     * @api {GET}/api/responsibilityCenter/query/by/groupId?setOfBooksId=1 【责任中心-查询】根据责任中心组
     * @apiDescription 根据责任中心组查询责任中心定义
     * @apiGroup ResponsibilityCenter
     * @apiParam responsibilityCenterCode 责任中心代码
     * @apiParam setOfBooksId  账套id
     * @apiParam responsibilityCenterName 责任中心名称
     * @apiParam enabled 启用 禁用
     * @apiParam range  //全部：ALL、已选：selected、未选：notChoose
     * @apiParam pageable
     * @apiSuccessExample {json} Success-Response:
     *[
     *   {
     *   "id": "1080290498884669442",
     *   "createdDate": "2019-01-02T10:31:40.464+08:00",
     *   "createdBy": "1",
     *   "lastUpdatedDate": "2019-01-02T10:31:40.468+08:00",
     *   "lastUpdatedBy": "1",
     *   "versionNumber": 1,
     *   "enabled": null,
     *   "tenantId": "1",
     *   "setOfBooksId": "1",
     *   },
     *   {
     *   "id": "1080290737754476545",
     *   "createdDate": "2019-01-02T10:32:37.369+08:00",
     *   "createdBy": "1",
     *  "lastUpdatedDate": "2019-01-02T10:32:37.369+08:00",
     *   "lastUpdatedBy": "1",
     *   "versionNumber": 1,
     *   "enabled": null,
     *   "tenantId": "1",
     *   "setOfBooksId": "1",
     *   "responsibilityCenterCode": "test1",
     *   "responsibilityCenterName": "测试1",
     *   "responsibilityCenterType": null
     *   },
     *    ]
     */
    @RequestMapping(value = "/query/by/groupId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<List<ResponsibilityCenter>> pageResponsibilityCenterBySetOfBooksIdAndGroupId(@RequestParam(value = "groupId")Long  groupId,
                                                                                                         @RequestParam(value = "responsibilityCenterCode",required = false) String responsibilityCenterCode,
                                                                                                         @RequestParam(value="setOfBooksId") Long setOfBooksId,
                                                                                                         @RequestParam(value = "responsibilityCenterName",required = false) String responsibilityCenterName,
                                                                                                         @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                                         @RequestParam(value = "range",required = false) String range,
                                                                                                         Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<ResponsibilityCenter> result = responsibilityCenterService.pageResponsibilityCenterBySetOfBooksIdAndGroupId(groupId,responsibilityCenterCode,setOfBooksId,responsibilityCenterName,enabled,range,page);
        int count = responsibilityCenterService.getResponsibilityCenterCountByGroupId(groupId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal()); //搜索总条数
        headers.add("X-Total-Count-Enable",""+count);//已选条数
        headers.add("Link", "/api/CompanyBank/selectByCompanyId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }



    /**
     * @api {GET}/api/responsibilityCenter/company/assign/query 【责任中心-分配公司】
     * @apiGroup ResponsibilityCenter
     * @apiDescription 获取当前责任中心已分配的公司
     * @apiParam {Long} responsibilityCenterId  责任中心id
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
     *         "responsibilityCenterId": "1077180351433990145",
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
     *         "responsibilityCenterId": "1077180351433990145",
     *         "companyId": "23442",
     *         "companyCode": "company2",
     *         "companyName": null,
     *         "companyType": null
     *     }
     * ]
     */
    @GetMapping(value="/company/assign/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResponsibilityAssignCompany>> pageCenterAssignCompany(@Param("responsibilityCenterId") Long responsibilityCenterId,
                                                                                     @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                     @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                     @RequestParam(value = "size",defaultValue = "10") int size){

        Page queryPage = PageUtil.getPage(page, size);
        List<ResponsibilityAssignCompany> result = assignCompanyService.pageResponsibilityCenterAssignCompany(responsibilityCenterId,enabled, queryPage);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/assign/company/filter");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/responsibilityCenter/company/assign/batch 【责任中心-分配公司】批量创建
     * @apiGroup ResponsibilityCenter
     * @apiParam {Object} responsibilityAssignCompany  责任中心关联公司定义
     * @apiParam {String} responsibilityAssignCompany.companyCode  公司代码
     * @apiParam {Long} responsibilityAssignCompany.companyId  公司id
     * @apiParam {Long} responsibilityAssignCompany.responsibilityCenterId  维值id
     * @apiParam {Boolean} responsibilityAssignCompany.enabled  是否启用
     * @apiParamExample {json} 请求参数:
     * [
     *  {
     *      "companyCode":"company1",
     *      "companyId":"2342",
     *      "responsibilityCenterId":"23425232543345",
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
     *         "responsibilityCenterId": "1077180351433990145",
     *         "companyId": "2342",
     *         "companyCode": "company1",
     *         "companyName": null,
     *         "companyType": null
     *     }
     * ]
     */
    @PostMapping(value = "/company/assign/batch")
    public List<ResponsibilityAssignCompany> insertResponsibilityCenterAssignCompanyBatch(@RequestBody List<ResponsibilityAssignCompany> list){
        return assignCompanyService.insertResponsibilityAssignCompanyBatch(list);
    }

    /**
     * @api {PUT} /api/responsibilityCenter/company/assign/batch 【责任中心-分配公司】批量更新状态
     * @apiGroup ResponsibilityCenter
     * @apiParam {Object} ResponsibilityAssignCompany  责任中心关联公司定义
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
     *         "responsibilityCenterId": null,
     *         "companyId": null,
     *         "companyCode": null,
     *         "companyName": null,
     *         "companyType": null
     *     }
     * ]
     */
    @PutMapping(value = "/company/assign/batch")
    public List<ResponsibilityAssignCompany> updateStatusBatch(@RequestBody List<ResponsibilityAssignCompany> list){
        return assignCompanyService.updateStatusBatch(list);
    }

    /**
     * @api {GET} /api/responsibilityCenter/company/assign/filter/by/setOfBooksId 【责任中心-分配公司】分配公司条件查询（账套下所有公司）
     * @apiDescription 批量分配公司弹窗的条件查询
     * @apiGroup ResponsibilityCenter
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
    @GetMapping(value = "/company/assign/filter/by/setOfBooksId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyCO>> pageCompanyBySetOfBooksId(@RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                                     @RequestParam(value = "companyCode", required = false) String companyCode,
                                                                     @RequestParam(value = "companyName", required = false) String companyName,
                                                                     @RequestParam(value = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                     @RequestParam(value = "companyCodeTo", required = false) String companyCodeTo,
                                                                     @RequestParam(value = "page",defaultValue = "0") int page,
                                                                     @RequestParam(value = "size",defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<CompanyCO> result = assignCompanyService.pageCompanyBySetOfBooksId(setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName, queryPage);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/dimension/item/assign/company/filter/by/setOfBooksId");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/responsibilityCenter/company/assign/filter 【责任中心-分配公司】分配公司条件查询
     * @apiDescription 批量分配公司弹窗的条件查询
     * @apiGroup ResponsibilityCenter
     * @apiParam {Long} responsibilityCenterId  责任中心id
     * @apiParam {String} [companyCode]  公司代码
     * @apiParam {String} [companyName]  公司名称
     * @apiParam {String} [companyCodeFrom]  公司代码从
     * @apiParam {String} [companyCodeTo]  公司代码到
     * @apiParam {int} page 分页page
     * @apiParam {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
        {
        "id": "2",
        "companyOid": "cf2b3694-b4f8-4aca-b233-111748eb025b",
        "name": "上海汉得信息技术股份有限公司",
        "setOfBooksId": "1078107093880250370",
        "setOfBooksName": "自定义账套3",
        "legalEntityId": null,
        "companyCode": "300170",
        "address": null,
        "companyLevelId": null,
        "parentCompanyId": null,
        "companyTypeId": null,
        "companyTypeName": null,
        "tenantId": "1",
        "baseCurrency": null
        }
     * ]
     */
    @GetMapping(value = "/company/assign/filter" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyCO>> pageCompanyByCond(@RequestParam(value = "responsibilityCenterId") Long responsibilityCenterId,
                                                             @RequestParam(value = "companyCode", required = false) String companyCode,
                                                             @RequestParam(value = "companyName", required = false) String companyName,
                                                             @RequestParam(value = "companyCodeFrom", required = false) String companyCodeFrom,
                                                             @RequestParam(value = "companyCodeTo", required = false) String companyCodeTo,
                                                             @RequestParam(value = "page",defaultValue = "0") int page,
                                                             @RequestParam(value = "size",defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page, size);
        List<CompanyCO> result = assignCompanyService.pageCompanyByCond(responsibilityCenterId, companyCode, companyCodeFrom, companyCodeTo, companyName, queryPage);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(queryPage, "/api/responsibilityCenter/company/assign/filter");
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/responsibilityCenter/template 【责任中心-导入】模板下载
     * @apiGroup ResponsibilityCenter
     * @apiSuccess {byte[]} byte excel文件
     */
    @GetMapping(value = "/template")
    public byte[] exportResponsibilityCenterTemplate(){
        InputStream inputStream = null;
        ByteArrayOutputStream bos = null;
        try {
            inputStream = StreamUtil.getResourceStream(ResponsibilityCenterImportCode.IMPORT_TEMPLATE_PATH);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.READ_FILE_FAILED);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new BizException(RespCode.READ_FILE_FAILED);
            }
        }
    }
    /**
     * @api {GET} /api/responsibilityCenter/import/error/export/{transactionId} 【责任中心-导入】导出错误信息
     * @apiGroup ResponsibilityCenter
     * @apiParam {String} transactionId 批次Id
     * @apiSuccess {byte[]} byte excel文件
     */
    @GetMapping("/import/error/export/{transactionId}")
    public ResponseEntity errorExport(@PathVariable("transactionId") String transactionId) throws IOException {
        return ResponseEntity.ok(responsibilityCenterService.exportFailedData(transactionId));
    }
    /**
     * @api {POST} /api/responsibilityCenter/item/import 【责任中心-导入】导入责任中心
     * @apiGroup ResponsibilityCenter
     * @apiParam {MultipartFile} file excel文件
     * @apiParam {Long} setOfBooksId 套账id
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "transactionOid": "34534645354623"
     * }
     */
    @PostMapping(value = "/import",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, UUID>> importresponsibilityCenters(@RequestParam("file") MultipartFile file,
                                                                          @RequestParam(value="setOfBooksId") Long setOfBooksId) throws Exception {
        try(InputStream in = file.getInputStream()) {
            UUID transactionOid = responsibilityCenterService.importResponsibilityCenters(in, setOfBooksId);
            Map<String, UUID> result = new HashMap<>();
            result.put("transactionOid", transactionOid);
            return ResponseEntity.ok(result);
        }catch (IOException e){
            throw new BizException(RespCode.READ_FILE_FAILED);
        }
    }

    /**
     * @api /api/responsibilityCenter/import/query/result/{transactionOid} 【责任中心-导入】查询导入结果
     * @apiParam transactionOid 批次号
     * @apiParamExample {json} 请求参数:  http://localhost:9083/api/responsibilityCenter/import/query/result/34534645354623
     * @apiSuccessExample  {json} 成功返回值:
     *
     */
    @GetMapping(value = "/import/query/result/{transactionOid}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity queryResultInfo(@PathVariable("transactionOid") String transactionOid) throws IOException {
        ImportResultDTO importResultDTO = responsibilityCenterService.queryResultInfo(transactionOid);
        return ResponseEntity.ok(importResultDTO);
    }

    /**
     * @api {POST} /api/responsibilityCenter/item/import/confirm/{transactionId} 【责任中心-导入】确定导入
     * @apiGroup ResponsibilityCenter
     * @apiParam {String} transactionId 批次ID
     */
    @PostMapping(value = "/import/confirm/{transactionId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity confirmImport(@PathVariable("transactionId") String transactionId){
        return ResponseEntity.ok(responsibilityCenterService.confirmImport(transactionId));
    }


    /**
     * @api {DELETE} /api/responsibilityCenter/item/import/delete/{transactionId} 【责任中心-导入】取消导入
     * @apiGroup ResponsibilityCenter
     * @apiParam {String} transactionId 批次ID
     */
    @DeleteMapping(value = "/import/delete/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteImportData(@PathVariable("transactionId") String transactionId){
        return ResponseEntity.ok(responsibilityCenterService.deleteImportData(transactionId));
    }

    /**
     * @api {get} /api/responsibilityCenter/export 【责任中心-导出】导出责任中心
     * @apiGroup ResponsibilityCenter
     * @apiParam {Long} setOfBooksId 套账id
     * @apiSuccess {byte[]} byte excel文件
     */
    @PostMapping(value = "/export",produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportresponsibilityCenterData( @RequestParam(value="setOfBooksId") Long setOfBooksId,
                                        @RequestBody ExportConfig exportConfig,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        responsibilityCenterService.exportResponsibilityCenterData(setOfBooksId, request, response, exportConfig);
    }

    @ApiOperation(value = "责任中心LOV查询", notes = "根据部门、公司查询可用的责任中心")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "code", value = "责任中心代码",
                    required = true, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "name", value = "责任中心名称",
                    required = true, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "companyId", value = "公司id",
                    required = true, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "departmentId", value = "部门id",
                    required = true, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    @GetMapping("/query/by/company/department")
    public ResponseEntity<List<ResponsibilityLovDTO>> queryByCompanyAndDepartment(
            @RequestParam("companyId") Long companyId,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @ApiIgnore Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ResponsibilityLovDTO> result = responsibilityCenterService. pageByCompanyAndDepartment(page,
                companyId, departmentId, code, name, true, null, id);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result, totalHeader, HttpStatus.OK);
    }

}
