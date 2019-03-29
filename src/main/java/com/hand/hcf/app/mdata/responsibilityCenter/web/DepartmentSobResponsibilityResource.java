package com.hand.hcf.app.mdata.responsibilityCenter.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.DepartmentSobResponsibility;
import com.hand.hcf.app.mdata.responsibilityCenter.dto.DepartmentSobResponsibilityDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.service.DepartmentSobResponsibilityService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department/sob/responsibility")
public class DepartmentSobResponsibilityResource {
    @Autowired
    private DepartmentSobResponsibilityService departmentSobResponsibilityService;

    /**
     * @api {POST} /api/department/sob/responsibility/insertOrUpdate 【部门账套责任中心配置-新增修改】
     * @apiGroup DepartmentSobResponsibility
     * @apiParam {Long} tenantId 租户id
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {Long} departmentId 部门id
     * @apiParam {Long} companyId 公司id
     * @apiParam {Long} defaultResponsibilityCenter 默认责任中心id
     * @apiParam {String} allResponsibilityCenter 是否全部责任中心（Y/N）
     * @apiParam  {Array} ids 责任中心id
     * @apiParamExample {json} Request-Param:
     *       {
     *       "tenantId": 1,
     *       "setOfBooksId": 2,
     *       "departmentId": "1",
     *       "companyId": "2",
     *       "defaultResponsibilityCenter":"1080290498884669442",
     *       "allResponsibilityCenter":"N",
     *       "ids":[1,46,65]
     *       }
     * @apiSuccessExample {json} Success-Response:
     * {
        "id": "1080313942925967361",
        "createdDate": "2019-01-02T12:04:49.93+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2019-01-02T12:04:49.931+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "tenantId": "1",
        "setOfBooksId": "1",
        "departmentId": "1",
        "companyId": "1",
        "defaultResponsibilityCenter": "1080290498884669442",
        "allResponsibilityCenter": "Y"
        }
     */
    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentSobResponsibility> insertOrUpdateDepartmentSobResponsibility(@RequestBody DepartmentSobResponsibilityDTO departmentSobResponsibility){
        return ResponseEntity.ok(departmentSobResponsibilityService.insertOrUpdateDepartmentSobResponsibility(departmentSobResponsibility));
    }
    /**
     * @api {GET} /api/department/sob/responsibility/query 【部门账套责任中心配置-查询】根据部门id
     * @apiDescription 根据部门id获取部门下所有账套责任中心配置
     * @apiGroup DepartmentSobResponsibility
     * @apiParam {Long} departmentId 部门id
     * @apiaram {String} keyword 账套名称或者代码
     * @apiSuccessExample {json} Success-Response:
     *
     * [
        {
        "id": "1080313942925967361",
        "createdDate": "2019-01-02T12:04:49.93+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2019-01-02T12:04:49.931+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "tenantId": "1",
        "setOfBooksId": "1",
        "setOfBooksName":"测试账套3",
        "departmentId": "1",
        "companyId": "1",
        "companyName":"",
        "defaultResponsibilityCenter": "1080290498884669442",
        "defaultResponsibilityCenterName":"测试"
        "allResponsibilityCenter": "Y"
        }
        ]
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentSobResponsibilityDTO>> pageDepartmentSobResponsibilityByDepartmentId(@RequestParam(value = "departmentId",required = true) Long departmentId,
                                                                                                              @RequestParam(value = "keyword",required = false) String keyword,
                                                                                                              Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<DepartmentSobResponsibilityDTO> result = departmentSobResponsibilityService.pageDepartmentSobResponsibilityByDepartmentId(departmentId,keyword,page);
        return new ResponseEntity<>(result.getRecords(), PageUtil.getTotalHeader(page), HttpStatus.OK);
    }
    
    /**
     * @api {GET} api/department/sob/responsibility/{Id}
     * @apiDescription  根据部门责任中心Id获取部门
     * @apiGroup DepartmentSobResponsibility
     * @apiParam {Long} id 部门责任中心Id
     * @apiParamExample {json} Request-Param:
     *      http://127.0.0.1:9098/mdata/api/department/sob/responsibility/1102403321370456065
     * @apiSuccessExample {json} Success-Response:
     * {
        "id": "1102403321370456065",
        "createdDate": "2019-03-04T11:00:08.135+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-03-04T11:15:14.355+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "setOfBooksId": "1083762150064451585",
        "setOfBooksCode": "test_sob",
        "setOfBooksName": "测试账套",
        "companyId": "1085129866609070082",
        "companyCode": "GS00003",
        "companyName": "NO.3公司",
        "defaultResponsibilityCenter": "1087903225576206337",
        "defaultResponsibilityCenterName": "测试002",
        "defaultResponsibilityCenterCode": "1004",
        "allResponsibilityCenter": "Y",
        "allResponsibilityCenterCount": null,
        "departmentId": "1084803809249693697",
        "tenantId": "1083751703623680001",
        "ids": null,
        "responsibilityCentersList": [
            {
            "i18n": null,
            "id": "1106126734335459330",
            "deleted": false,
            "createdDate": "2019-03-14T17:35:40.27+08:00",
            "createdBy": "1083751705402064897",
            "lastUpdatedDate": "2019-03-14T17:35:59.131+08:00",
            "lastUpdatedBy": "1083751705402064897",
            "versionNumber": 1,
            "enabled": true,
            "tenantId": "1083751703623680001",
            "setOfBooksId": "1083762150064451585",
            "setOfBooksName": null,
            "responsibilityCenterCodeName": null,
            "responsibilityCenterCode": "1009",
            "responsibilityCenterName": "测试009",
            "responsibilityCenterType": null
            }
        ]
     }
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentSobResponsibilityDTO> getDepartmentSobResponsibilityById(@PathVariable Long id){
        return ResponseEntity.ok(departmentSobResponsibilityService.getDepartmentSobResponsibilityById(id));
    }


}
