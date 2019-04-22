package com.hand.hcf.app.mdata.department.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DepartmentGroupDepartmentCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.dto.ContactBankAccountDTO;
import com.hand.hcf.app.mdata.contact.dto.UserInfoDTO;
import com.hand.hcf.app.mdata.department.domain.DepartmentGroup;
import com.hand.hcf.app.mdata.department.dto.DepartmentDTO;
import com.hand.hcf.app.mdata.department.service.DepartmentGroupService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/DepartmentGroup")
@RestController
public class DepartmentGroupResource {

    private final DepartmentGroupService departmentGroupService;


    public DepartmentGroupResource(DepartmentGroupService departmentGroupService) {
        this.departmentGroupService = departmentGroupService;
    }

    /**
     *
     * @api {post} /api/DepartmentGroup/insertOrUpdate 新建或修改部门组
     * @apiGroup DepartmentGroup
     * @apiSuccess {Object[]} json 返回部门组实体对象
     * @apiSuccess {Long} id  部门组id
     * @apiSuccess {Long} tenantId  租户id
     * @apiSuccess {String} deptGroupCode  部门组code
     * @apiSuccess {Long} companyId  公司id
     * @apiSuccess {DateTime} createdDate  创建时间
     * @apiSuccess {String} lastUpdatedBy  最后修改人
     * @apiSuccess {DateTime} lastUpdatedDate  最后修改时间
     * @apiSuccess {String} createdBy  创建人
     * @apiSuccess {Boolean} enabled  启用标志
     * @apiSuccess {deleted} deleted  是否删除
     * @apiSuccessExample {json} Success-Result
     * {
    "code": "0000",
    "rows": [
        {
            "id": "912974742421102594",
            "createdDate": "2017-09-27T09:38:35Z",
            "lastUpdatedDate": "2017-11-01T02:20:39Z",
            "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
            "lastUpdatedBy": null,
            "tenantId": 907943971227361281,
            "deptGroupCode": "code28333",
            "description": "简体中文111",
            "companyId": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
            "enabled": true,
            "deleted": false
        }
        ],
        "success": true
    }
     */
    @Timed
    @RequestMapping(value = "/insertOrUpdate" ,method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentGroup> saveDepartment(@RequestBody @Valid DepartmentGroup departmentGroup){
        return ResponseEntity.ok(departmentGroupService.insertOrUpdateDepartmentGroup(departmentGroup, OrgInformationUtil.getCurrentUserOid()));
    }

    //条件查询（根据部门组代码和部门组描述）
    /**
     * @api {GET} /api/DepartmentGroup/selectByInput 根据条件查询部门组列表
     * @apiGroup DepartmentGroup
     * @apiParam  {String} [deptGroupCode] 部门组代码
     * @apiParam  {String} [description] 部门组描述
     * @apiSuccess {Object[]} json 返回部门组实体对象
     * @apiSuccess {Long} id  部门组id
     * @apiSuccess {Long} tenantId  租户id
     * @apiSuccess {String} deptGroupCode  部门组code
     * @apiSuccess {Long} companyId  公司id
     * @apiSuccess {DateTime} createdDate  创建时间
     * @apiSuccess {String} lastUpdatedBy  最后修改人
     * @apiSuccess {DateTime} lastUpdatedDate  最后修改时间
     * @apiSuccess {String} createdBy  创建人
     * @apiSuccess {Boolean} enabled  启用标志
     * @apiSuccess {deleted} deleted  是否删除
     * @apiSuccessExample {json} Success-Result
         * {
        "code": "0000",
        "rows": [
            {
                "i18n": {
                    "description": [
                        {
                        "language": "en",
                        "value": "英文"
                        },
                        {
                        "language": "zh_cn",
                        "value": "简体中文111"
                        }
                    ]
                },
                "id": "912974742421102594",
                "createdDate": "2017-09-27T09:38:35Z",
                "lastUpdatedDate": "2017-11-01T02:20:39Z",
                "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
                "lastUpdatedBy": null,
                "tenantId": 907943971227361281,
                "deptGroupCode": "code28333",
                "description": "简体中文111",
                "companyId": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
                "enabled": true,
                "deleted": false
            }
        ],
        "success": true
        }
     */
    @Timed
    @RequestMapping(value = "/selectByInput",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentGroup>> selectByInput(@RequestParam String deptGroupCode, @RequestParam String description, @RequestParam(value = "enabled",required = false) boolean enabled , Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<DepartmentGroup> result= departmentGroupService.selectDepartmentGroupByInput(page,deptGroupCode, description,enabled);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/DepartmentGroup/selectByInput");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    //根据部门组code和描述查询部门信息是否在当前部门组Id下
    @Timed
    @RequestMapping(value = "/selectDepartmentByGroupCodeAndDescription",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentGroupDepartmentCO>> selectDepartmentByGroupCode(@RequestParam String deptGroupCode, @RequestParam String description, @RequestParam Long departmentGroupId, Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<DepartmentGroupDepartmentCO> result=departmentGroupService.selectDepartmentByGroupCode(deptGroupCode,description,departmentGroupId,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/DepartmentGroup/selectDepartmentByGroupCodeAndDescription");
        return  new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }



    //根据部门组id查询当前部门组下的部门,分页
    /**
     * @api {GET} /api/DepartmentGroup/selectDepartmentByGroupId 根据部门组id查询当前部门组下的部门,分页
     * @apiGroup DepartmentGroup
     * @apiParam {Long} departmentGroupId 部门组id
     * @apiSuccess {Object} json 部门组视图数组
     * @apiSuccess {Long} departmentDetailId  部门组明细id
     * @apiSuccess {Long} departmentId  部门id
     * @apiSuccess {String} departmentOid  部门oid
     * @apiSuccess {String} departmentCode  部门编码
     * @apiSuccess {String} name  部门名称
     * @apiSuccessExample {json} Success-Result
     * {
        "rows": [
            {
                "departmentDetailId": "913707684808179714",
                "departmentId": "7",
                "departmentOid": null,
                "departmentCode": "7",
                "name": "Global事业部1"
            }
        ],
        "success": true
    }
     */
    @Timed
    @RequestMapping(value = "/selectDepartmentByGroupId",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentGroupDepartmentCO>> selectCurrentDeparmentGroupDepartment(@RequestParam Long departmentGroupId, Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<DepartmentGroupDepartmentCO> result= departmentGroupService.selectCurrentGroupDepartment(departmentGroupId,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/DepartmentGroup/selectDepartmentByGroupId");
        return  new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    //根据部门组id查询部门组详情
    /**
     * @api {get} /api/DepartmentGroup/selectById 根据id查询部门组详情
     * @apiGroup DepartmentGroup
     * @apiParam {Long} id  待查询的id
     * @apiSuccess {Object[]} json 返回部门组实体对象
     * @apiSuccess {Long} id  部门组id
     * @apiSuccess {Long} tenantId  租户id
     * @apiSuccess {String} deptGroupCode  部门组code
     * @apiSuccess {Long} companyId  公司id
     * @apiSuccess {DateTime} createdDate  创建时间
     * @apiSuccess {String} lastUpdatedBy  最后修改人
     * @apiSuccess {DateTime} lastUpdatedDate  最后修改时间
     * @apiSuccess {String} createdBy  创建人
     * @apiSuccess {Boolean} enabled  启用标志
     * @apiSuccess {deleted} deleted  是否删除
     * @apiSuccessExample {json} Success-Result
     * {
    "code": "0000",
    "row":
        {
            "i18n": {
                "description": [
                    {
                    "language": "en",
                    "value": "英文"
                    },
                    {
                    "language": "zh_cn",
                    "value": "简体中文111"
                    }
                ]
            },
            "id": "912974742421102594",
            "createdDate": "2017-09-27T09:38:35Z",
            "lastUpdatedDate": "2017-11-01T02:20:39Z",
            "createdBy": "329e6ede-ff54-4e87-a213-684e89bb4b30",
            "lastUpdatedBy": null,
            "tenantId": 907943971227361281,
            "deptGroupCode": "code28333",
            "description": "简体中文111",
            "companyId": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
            "enabled": true,
            "deleted": false
        }
        ,
        "success": true
    }
     */
    @Timed
    @RequestMapping(value = "/selectById",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentGroup> selectById(@RequestParam Long id){
        return ResponseEntity.ok(departmentGroupService.selectById(id));
    }

    @Timed
    @RequestMapping(value = "/selectDepartmentGroupByInput",method = RequestMethod.GET)
    public ResponseEntity<List<DepartmentGroup>> selectDepartmentGroupByInput(@RequestParam String deptGroupCode, @RequestParam String description ,@RequestParam(required = false) Boolean enable,Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<DepartmentGroup> result= departmentGroupService.selectDepartmentGroupByInput(page,deptGroupCode, description,enable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/DepartmentGroup/selectDepartmentGroupByInput");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/selectDept/enabled",method = RequestMethod.GET)
    public ResponseEntity<List<DepartmentGroupDepartmentCO>> selectDeptEnabled(
            @RequestParam(value = "deptCode",required = false) String deptCode,
            @RequestParam(value = "leafEnable",required = false)Boolean leafEnable,
            @RequestParam(value = "departmentCode",required = false) String departmentCode,
            @RequestParam(value = "departmentId",required = false) Long departmentId,
            @RequestParam(value = "name",required = false) String name, Pageable pageable){

        Page page = PageUtil.getPage(pageable);
        if (!StringUtils.hasText(deptCode) && StringUtils.hasText(departmentCode)){
            deptCode = departmentCode;
        }
        Page<DepartmentGroupDepartmentCO> result = departmentGroupService.selectDepartmentByTenantIdAndEnabled(
                OrgInformationUtil.getCurrentTenantId(),deptCode,name,leafEnable, departmentId,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/DepartmentGroup/selectDept/enabled");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/selectDepartment/enabled",method = RequestMethod.GET)
    public ResponseEntity<List<DepartmentDTO>> selectDepartmentEnabled(@RequestParam("deptCode") String deptCode, @RequestParam("name") String name, Pageable pageable){

        Page page = PageUtil.getPage(pageable);
        Page<DepartmentDTO> result = departmentGroupService.selectDepartmentsByTenantIdAndEnabled(OrgInformationUtil.getCurrentTenantId(),deptCode,name,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/DepartmentGroup/selectDepartment/enabled");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/users/by/department/and/company",method = RequestMethod.GET)
    public ResponseEntity<List<UserInfoDTO>> selectUsersByDepartmentAndCompany(
        @RequestParam(value = "companyId", required = false) Long companyId,
        @RequestParam(value = "departmentId", required = false) Long departmentId,
        @RequestParam(value = "userCode", required = false) String userCode,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "companyName", required = false) String companyName,
        Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page<UserInfoDTO> result = departmentGroupService.selectUsersByCompanyAndDepartment1(companyId, departmentId, userCode, userName, companyName, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/DepartmentGroup/get/users/by/department/and/company");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);

    }


    @RequestMapping(value = "/getContactBankByUserOid",method = RequestMethod.GET)
    public ResponseEntity<List<ContactBankAccountDTO>> selectUserBankByUserOid(@RequestParam String userOid){
        return ResponseEntity.ok(departmentGroupService.selectContactBankAccountDTOByUserOid(userOid));
    }
    /**
     * 根据员工ID查询可用的银行账户
     *
     * @param: userId
     * @return
     */
    @RequestMapping(value = "/getContactBankByUserId",method = RequestMethod.GET)
    public ResponseEntity<List<ContactBankAccountDTO>> selectUserBankByUserOid(@RequestParam Long userId){
        return ResponseEntity.ok(departmentGroupService.selectContactBankAccountDTOByUserId(userId));
    }

    /*根据部门oid和name（子部门）查询这个部门下的子部门*/
    @RequestMapping(value = "/get/dept/by/id",method = RequestMethod.GET)
    public ResponseEntity<List<DepartmentDTO>> selectDepartmentByName(@RequestParam String name, @RequestParam Long id, @RequestParam Integer status){
        // public ResponseEntity<List<DepartmentDTO>> selectDepartmentByName(@RequestParam String name, @RequestParam Long id){

        return ResponseEntity.ok(departmentGroupService.findByParentDepartmentOidAndStatusNotAndNameLike(id,name,status));
    }
}
