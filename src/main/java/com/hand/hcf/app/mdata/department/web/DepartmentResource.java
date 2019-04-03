package com.hand.hcf.app.mdata.department.web;

import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.dto.DepartmentAssignUserDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentTreeDTO;
import com.hand.hcf.app.mdata.department.dto.DepartmentUserSummaryDTO;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.department.service.DepartmentUserService;
import com.hand.hcf.app.mdata.system.constant.Constants;
import com.hand.hcf.app.mdata.utils.HeaderUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing Department.
 */
@RestController
@RequestMapping("/api")
public class DepartmentResource {

    private final Logger log = LoggerFactory.getLogger(DepartmentResource.class);
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentUserService departmentUserService;

    /**
     * @apiDefine DepartmentDTO
     * @apiParam {String} departmentOid   部门Oid
     * @apiParam {String} parentDepartmentOid   部门父Oid
     * @apiParam {String} name   部门名称
     * @apiParam {String} path   部门路径
     * @apiParam {String} companyOid 公司Oid
     * @apiParam {String} companyName   公司名称
     * @apiParam {String} managerOid  部门经理Oid
     * @apiParam {String} fullName    部门经理名称
     * @apiParam {Boolean} hasChildrenDepartments   是否有子部门
     * @apiParam {Boolean} hasUsers     部门是否有用户
     * @apiParam {Object} departmentRole     部门角色列表
     * @apiParam {Int} status     部门状态 101-正常，102-禁用，103-删除
     * @apiParam {Date} lastUpdatedDate     最后修改时间
     * @apiParam {String} departmentCode     部门编码
     */

    /**
     * POST  /departments -> Create a new department.
     */
    /**
     *
     * @api {post} /api/departments 创建部门
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParamExample {json} 请求样例
    {
    "companyOid" : "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "name" : "测试部门555",
    "path" : "测试部门555"
    }
     * @apiSuccessExample {json} 响应示例
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "parentDepartmentOid": null,
    "name": "测试部门555",
    "path": "测试部门555",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": false,
    "departmentRole": null,
    "status": 101,
    "lastUpdatedDate": "2017-12-06T01:50:23Z",
    "departmentCode": null
    }
     *
     */
    @RequestMapping(value = "/departments",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) throws URISyntaxException {
        log.debug("REST request to save Department : {}", departmentDTO);
        if (departmentDTO.getDepartmentOid() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("department", "idexists", "A new department cannot already have an Oid")).body(null);
        }
        if(StringUtils.isBlank(departmentDTO.getName()) || departmentDTO.getName().contains(Constants.DEPARTMENT_SPLIT)){
            throw new BizException(RespCode.DEPARTMENT_NAME_CANNOT_BE_EMPTY_OR_CONTAIN_BAR);
        }
        DepartmentDTO result = departmentService.createDepartment(departmentDTO,OrgInformationUtil.getCurrentUserOid(),OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.created(new URI("/api/departments/" + result.getDepartmentOid()))
            .headers(HeaderUtil.createEntityCreationAlert("department", result.getDepartmentOid().toString()))
            .body(result);
    }

    /**
     * PUT  /departments -> Updates an existing department.
     */
    /**
     *
     * @api {put} /api/departments 更新部门
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParamExample {json} 请求样例
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "companyOid" : "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "name" : "测试部门555改名",
    "path" : "测试部门555改名"
    }
     * @apiSuccessExample {json} 响应示例
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "parentDepartmentOid": null,
    "name": "测试部门555改名",
    "path": "测试部门555改名",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": false,
    "departmentRole": null,
    "status": 101,
    "lastUpdatedDate": "2017-12-06T01:55:39Z",
    "departmentCode": null
    }
     *
     */
    @RequestMapping(value = "/departments",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DepartmentDTO> updateDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) throws URISyntaxException {
        log.debug("REST request to update Department : {}", departmentDTO);
        if (departmentDTO.getDepartmentOid() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("department", "idexists", "A new department cannot already have an Oid")).body(null);
        }
        if(StringUtils.isBlank(departmentDTO.getName()) || departmentDTO.getName().contains(Constants.DEPARTMENT_SPLIT)){
            throw new BizException(RespCode.DEPARTMENT_NAME_CANNOT_BE_EMPTY_OR_CONTAIN_BAR);
        }
        if(StringUtils.isBlank(departmentDTO.getDepartmentCode())){
            throw new BizException(RespCode.DEPARTMENT_CODE_NOT_NULL_23009);
        }
        departmentDTO.setTenantId(OrgInformationUtil.getCurrentTenantId());
        DepartmentDTO result = departmentService.updateDepartment(departmentDTO,true,OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("department", departmentDTO.getDepartmentOid().toString()))
            .body(result);
    }

    /**
     * GET  /departments/:id -> get the "id" department.
     */
    /**
     *
     * @api {get} /api/departments/{departmentOid} 获取部门详情
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParam {String} departmentOid 部门Oid
     * @apiSuccessExample {json} 响应示例
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "parentDepartmentOid": null,
    "name": "测试部门555改名",
    "path": "测试部门555改名",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": false,
    "departmentRole": {},
    "status": 101,
    "lastUpdatedDate": "2017-12-06T01:55:40Z",
    "departmentCode": null
    }
     *
     */
    @RequestMapping(value = "/departments/{departmentOid}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable UUID departmentOid) {
        log.debug("REST request to get Department : {}", departmentOid);
        DepartmentDTO departmentDTO = departmentService.findOne(departmentOid);
        return Optional.ofNullable(departmentDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     *
     * @api {post} /api/departments/oids 根据Oids获取部门列表
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParam {String} userOid 用户Oid
     * @apiParamExample {json} 请求样例
     ["732e0855-baa0-49a6-b565-5f36195a4d50"]
     * @apiSuccessExample {json} 响应示例
    [
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "parentDepartmentOid": null,
    "name": "测试部门555改名",
    "path": "测试部门555改名",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": false,
    "departmentRole": {},
    "status": 101,
    "lastUpdatedDate": "2017-12-06T01:55:40Z",
    "departmentCode": null
    }
    ]
     *
     */
    @RequestMapping(value = "/departments/oids",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DepartmentDTO>> getDepartments(@RequestBody List<UUID> departmentOids) {
        log.debug("REST request to get Departments : {}", departmentOids);
        List<DepartmentDTO> departmentDTOList = departmentService.findDepartmentByOids(departmentOids);
        return Optional.ofNullable(departmentDTOList)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/departments/oids/simple",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DepartmentDTO>> getDepartmentsSimple(@RequestBody List<UUID> departmentOids) {
        log.debug("REST request to get Departments : {}", departmentOids);
        List<DepartmentDTO> departmentDTOList = departmentService.findDepartmentByOidsSimple(departmentOids);
        return ResponseEntity.ok(departmentDTOList);
    }

    /**
     *
     * @param flag 1001查询全部部门,1002查询启用部门,1003查询禁用部门
     * @return
     */
    /**
     *
     * @api {get} /api/departments/root 查询一级部门
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParam {String} flag 1001查询全部部门,1002查询启用部门,1003查询禁用部门
     * @apiSuccessExample {json} 响应示例
    [
    {
    "departmentOid": "2cbd0780-ca07-4a34-9827-d8da0132aea6",
    "parentDepartmentOid": null,
    "name": "市场部",
    "path": "市场部",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": "b92d5351-de36-4aef-b713-6b82c904dc78",
    "fullName": "123",
    "hasChildrenDepartments": true,
    "hasUsers": false,
    "departmentRole": {
    "managerOid": "b92d5351-de36-4aef-b713-6b82c904dc78",
    "managerName": "123",
    "chargeManagerOid": "6e16708c-02fd-45d4-bb4b-7cace7027945",
    "chargeManagerName": "报表测试6",
    "financialBPOid": "e392fc39-e624-426f-b7c8-5767e2424572",
    "financialBPName": "1",
    "viceManagerOid": "05aaaa1f-9661-443f-bb38-c53c61172669",
    "viceManagerName": "姓名1357",
    "departmentManagerOid": "fbead0eb-e228-46bd-afdd-d768ba5cee4d",
    "departmentManagerName": "姓名10281",
    "vicePresidentOid": "16f6a778-f1d3-4b5a-93e8-a0de224b35a0",
    "vicePresidentName": "测试8",
    "presidentOid": "ae1ed592-948f-4c29-be8e-e933ec223bb7",
    "presidentName": "sun11111",
    "financialManagerOid": "187bd25a-282a-4468-8870-bf64ca08100a",
    "financialManagerName": "测试10"
    },
    "status": 101,
    "lastUpdatedDate": "2017-12-01T07:51:21Z",
    "departmentCode": "18889809999"
    },
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "parentDepartmentOid": null,
    "name": "测试部门555改名",
    "path": "测试部门555改名",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": false,
    "departmentRole": {},
    "status": 101,
    "lastUpdatedDate": "2017-12-06T01:55:40Z",
    "departmentCode": null
    }
    ]
     *
     */
    @RequestMapping(value = "/departments/root", method = RequestMethod.GET)
    List<DepartmentDTO> getRootDepartments(@RequestParam(name = "flag" ,required = false) Integer flag) {
        if(flag == null){
            flag = 1001;
        }
        return departmentService.getCompanyRootDepartments(OrgInformationUtil.getCurrentCompanyOid(),flag);
    }

    //优化
    /**
     *
     * @api {get} /api/departments/root/v2 查询一级部门V2
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParam {String} flag 1001查询全部部门,1002查询启用部门,1003查询禁用部门
     * @apiSuccessExample {json} 响应示例
    [
    {
    "departmentOid": "2cbd0780-ca07-4a34-9827-d8da0132aea6",
    "parentDepartmentOid": null,
    "name": "市场部",
    "path": "市场部",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": "b92d5351-de36-4aef-b713-6b82c904dc78",
    "fullName": "123",
    "hasChildrenDepartments": true,
    "hasUsers": false,
    "departmentRole": {
    "managerOid": "b92d5351-de36-4aef-b713-6b82c904dc78",
    "managerName": "123",
    "chargeManagerOid": "6e16708c-02fd-45d4-bb4b-7cace7027945",
    "chargeManagerName": "报表测试6",
    "financialBPOid": "e392fc39-e624-426f-b7c8-5767e2424572",
    "financialBPName": "1",
    "viceManagerOid": "05aaaa1f-9661-443f-bb38-c53c61172669",
    "viceManagerName": "姓名1357",
    "departmentManagerOid": "fbead0eb-e228-46bd-afdd-d768ba5cee4d",
    "departmentManagerName": "姓名10281",
    "vicePresidentOid": "16f6a778-f1d3-4b5a-93e8-a0de224b35a0",
    "vicePresidentName": "测试8",
    "presidentOid": "ae1ed592-948f-4c29-be8e-e933ec223bb7",
    "presidentName": "sun11111",
    "financialManagerOid": "187bd25a-282a-4468-8870-bf64ca08100a",
    "financialManagerName": "测试10"
    },
    "status": 101,
    "lastUpdatedDate": "2017-12-01T07:51:21Z",
    "departmentCode": "18889809999"
    },
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "parentDepartmentOid": null,
    "name": "测试部门555改名",
    "path": "测试部门555改名",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": false,
    "departmentRole": {},
    "status": 101,
    "lastUpdatedDate": "2017-12-06T01:55:40Z",
    "departmentCode": null
    }
    ]
     *
     */
    @RequestMapping(value = "/departments/root/v2", method = RequestMethod.GET)
    List<DepartmentDTO> getRootDepartmentsV2(@RequestParam(name = "flag" ,required = false) Integer flag, @RequestParam(name = "isCompany" ,required = false) boolean isCompany) {
        if(flag == null){
            flag = 1001;
        }
        Long tenantId=OrgInformationUtil.getCurrentTenantId();
        return departmentService.getCompanyRootDepartmentsV2(OrgInformationUtil.getCurrentCompanyId(),tenantId,flag,isCompany);
    }

    /**
     *
     * @api {get} /api/department/child/{parentDepartmentOid} 查询部门子部门
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParam {String} parentDepartmentOid 部门Oid
     * @apiSuccessExample {json} 响应示例
    [
    {
    "departmentOid": "f38fba38-b51c-4d2f-b090-fcf5709296f2",
    "parentDepartmentOid": "6c92e975-da3c-4b42-87f2-f3fd1bc66087",
    "name": "A-1部门",
    "path": "销售部啊|A-1部门",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": true,
    "hasUsers": false,
    "departmentRole": {},
    "status": 101,
    "lastUpdatedDate": null,
    "departmentCode": null
    },
    {
    "departmentOid": "0eb302d6-5175-4d2d-a7f6-61ded9c4bf5b",
    "parentDepartmentOid": "6c92e975-da3c-4b42-87f2-f3fd1bc66087",
    "name": "测试1234",
    "path": "销售部啊|测试1234",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": true,
    "departmentRole": {},
    "status": 101,
    "lastUpdatedDate": "2017-08-11T03:43:15Z",
    "departmentCode": null
    }
    ]
     *
     */
    @RequestMapping(value = "/department/child/{parentDepartmentOid}", method = RequestMethod.GET)
    List<DepartmentDTO> getChildrenDepartment(@PathVariable UUID parentDepartmentOid,
                                              @RequestParam(name = "flag" ,required = false) Integer flag,
                                              @RequestParam(name = "isCompany" ,required = false) boolean isCompany) {
        if(flag == null){
            flag = 1001;
        }
        return departmentService.getChildrenDepartment(parentDepartmentOid,flag,isCompany);
    }

    /**
     *
     * @api {get} /api/department/like 根据部门名字搜索部门
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParam {String} name 部门名称
     * @apiParam {String} flag 1001查询全部部门,1002查询启用部门,1003查询禁用部门
     * @apiParam {boolean} hasChildren 是否带子部门
     * @apiSuccessExample {json} 响应示例
    [
    {
    "departmentOid": "732e0855-baa0-49a6-b565-5f36195a4d50",
    "parentDepartmentOid": null,
    "name": "测试部门555改名",
    "path": "测试部门555改名",
    "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
    "companyName": "三全科技",
    "managerOid": null,
    "fullName": null,
    "hasChildrenDepartments": false,
    "hasUsers": false,
    "departmentRole": {},
    "status": 101,
    "lastUpdatedDate": "2017-12-06T01:55:40Z",
    "departmentCode": null
    }
    ]
     *
     */
    @RequestMapping(value = "/department/like",
        method= RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DepartmentDTO>> searchByLikeName(@RequestParam("name") String name, @RequestParam(name = "flag" ,required = false) Integer flag, @RequestParam(name = "hasChildren" ,required = false) boolean hasChildren){
       if(flag == null){
           flag = 1001;
       }
       return ResponseEntity.ok(departmentService.searchByName(OrgInformationUtil.getCurrentCompanyOid(),name,flag,hasChildren));
    }

    /**
     *
     * @api {post} /api/department/enable/{departmentOid} 启用部门
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiUse DepartmentDTO
     * @apiParam {String} departmentOid 部门Oid
     *
     */
   @RequestMapping(value = "/department/enable/{departmentOid}",
        method = RequestMethod.PUT)
    public ResponseEntity<Void> departmentEnable(@PathVariable UUID departmentOid){
       log.debug("REST request to enable department : {}", departmentOid);
       departmentService.departmentEnable(departmentOid,OrgInformationUtil.getCurrentUserOid(),OrgInformationUtil.getCurrentCompanyOid(),OrgInformationUtil.getCurrentTenantId());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("department", departmentOid.toString())).build();
    }

    /**
     *
     * @api {post} /api/department/disable/{departmentOid} 禁用部门
     * @apiGroup Department
     * @apiVersion 0.1.0
     * @apiParam {String} departmentOid 部门Oid
     *
     */
    @RequestMapping(value = "/department/disable/{departmentOid}",
        method = RequestMethod.PUT)
    public ResponseEntity<Void> departmentDisable(@PathVariable UUID departmentOid){
        log.debug("REST request to disable department : {}", departmentOid);
        departmentService.departmentDisable(departmentOid,OrgInformationUtil.getCurrentUserOid());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("department", departmentOid.toString())).build();
    }

    /**
     * 获取部门树-限制才角色
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/department/tree/by/finance/role" , method = RequestMethod.GET)
    public ResponseEntity<List<DepartmentTreeDTO>> getDepartmentTree(@RequestParam(value = "keyword",required = false) String keyword){
        List<DepartmentTreeDTO> tree = departmentService.getDepartmentTreeByFinanceRole(OrgInformationUtil.getCurrentUserOid(),OrgInformationUtil.getCurrentTenantId(), OrgInformationUtil.getCurrentLanguage(), keyword);
        return ResponseEntity.ok(tree);
    }


    /**
     * 查询租户下所有的部门
     * @param status
     * @return
     */
    @RequestMapping(value = "/department/tenant/all" , method = RequestMethod.GET)
    public ResponseEntity<List<DepartmentTreeDTO>> getDepartmentsAll(@RequestParam(value = "code", required = false) String code,
                                                                     @RequestParam(value = "name", required = false) String name,
                                                                     @RequestParam(value = "status",required = false) Integer status ){
        List<DepartmentTreeDTO> result = departmentService.getTenantDepartmentAll(code, name, OrgInformationUtil.getCurrentTenantId(),status,OrgInformationUtil.getCurrentLanguage());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/departments/change/list", method = RequestMethod.PUT)
    public void changeUserDepartmentList(@RequestBody @Valid DepartmentAssignUserDTO dto) {
        departmentUserService.changeUsersDepartmentList(OrgInformationUtil.getCurrentTenantId(),dto);
    }

    /**
     * @api 模糊查询部门和员工
     */
    @RequestMapping(value = "/department/user/keyword" , method = RequestMethod.GET)
    public ResponseEntity<DepartmentUserSummaryDTO> searchDepartmentsAndUsers(@RequestParam(value = "keyword") String keyword,
                                                                              @RequestParam(value = "needEmployeeId",required = false) Boolean needEmployeeId,
                                                                              @RequestParam(value = "departmentStatus",required = false) Integer departmentStatus ){
        DepartmentUserSummaryDTO result = departmentService.getDepartmentsAndUsersBykeywords(OrgInformationUtil.getCurrentTenantId(),keyword,needEmployeeId,departmentStatus);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my/department")
    public ResponseEntity<Department> getCurrentDepartmentMessage(){
        Optional<Department> departmentByUserId = departmentUserService.getDepartmentByUserId(OrgInformationUtil.getCurrentUserId());
        if (departmentByUserId.isPresent()) {
            return ResponseEntity.ok(departmentByUserId.get());
        }
        return null;
    }
}
