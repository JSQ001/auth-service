package com.hand.hcf.app.mdata.company.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.CompanyLevel;
import com.hand.hcf.app.mdata.company.service.CompanyLevelService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by 刘亮 on 2017/9/4.
 */
@RestController
@RequestMapping("/api/companyLevel")
public class CompanyLevelResource {

    private final CompanyLevelService companyLevelService;


    public CompanyLevelResource(CompanyLevelService companyLevelService) {
        this.companyLevelService = companyLevelService;
    }




    /**
     *
     * @api {post} /api/companyLevel/insertOrUpdate 新建或修改公司级别
     * @apiGroup CompanyLevel
     * @apiSuccess {Object} json 公司级别实体
     * @apiSuccessExample {json} Success-Result
     * {
        "code": "0000",
        "rows": [
            {
                "i18n": {},
                "id": "909604476331200514",
                "createdDate": "2017-09-18T02:26:21Z",
                "lastUpdatedDate": "2017-09-18T02:26:21Z",
                "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
                "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
                "companyLevelCode": "dddd",
                "tenantId": 907943944140546049,
                "description": "我是公司级别描述1",
                "enabled": true,
                "deleted": false
            }
        ],
        "success": true
    }
     */
    @Timed
    @RequestMapping(value = "/insertOrUpdate",method= RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyLevel> insertOrUpdateCompanyLevel(@RequestBody @Valid CompanyLevel companyLevel){
        return ResponseEntity.ok(companyLevelService.insertOrUpdateCompanyLevel(companyLevel, OrgInformationUtil.getCurrentUserOid()));
    }



    /**
     * @api {delete} /api/companyLevel/deleteById/{id} 逻辑删除公司级别
     * @apiGroup CompanyLevel
     * @apiParam {Long} id  待删除的id
     * @apiSuccess {Boolean} success 是否成功
     */
    @Timed
    @RequestMapping(value = "/deleteById/{id}",method= RequestMethod.DELETE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteById(@PathVariable Long id){
        return ResponseEntity.ok(companyLevelService.deleteCompanyLevelById(id));
    }


    /**
     * 条件查询
     * @param companyLevelCode
     * @param description
     * @param pageable
     * @return
     */
    /**
     * @api {GET} /api/companyLevel/selectByInput 根据条件查询公司级别列表
     * @apiGroup CompanyLevel
     * @apiParam {String} [companyLevelCode] 公司级别代码
     * @apiParam {String} [description] 公司级别描述
     * @apiSuccess {Object[]} json 返回公司级别实体对象
     * @apiSuccess {Long} id  公司级别id
     * @apiSuccess {Long} tenantId  租户id
     * @apiSuccess {String} companyLevelCode  公司级别code
     * @apiSuccess {String} description  公司级别描述
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
                "i18n": {},
                "id": "909604476331200514",
                "createdDate": "2017-09-18T02:26:21Z",
                "lastUpdatedDate": "2017-09-18T02:26:21Z",
                "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
                "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
                "companyLevelCode": "dddd",
                "tenantId": 907943944140546049,
                "description": "我是公司级别描述1",
                "enabled": true,
                "deleted": false
            }
        ],
        "success": true
    }
     */
    @Timed
    @RequestMapping(value = "/selectByInput",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyLevel>> selectByInput(
        @RequestParam(required = false) String companyLevelCode,
        @RequestParam(required = false) String description,
        Pageable pageable
        ){
        Page page = PageUtil.getPage(pageable);
        Page<CompanyLevel> result = companyLevelService.selectByInput(companyLevelCode,description,page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/companyLevel/selectByInput");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {get} /api/companyLevel/selectByInput 根据id查询公司级别详情
     * @apiGroup CompanyLevel
     * @apiParam {Long} id
     * @apiSuccess {Object[]} json 返回公司级别实体对象
     * @apiSuccess {Long} id  公司级别id
     * @apiSuccess {Long} tenantId  租户id
     * @apiSuccess {String} companyLevelCode  公司级别code
     * @apiSuccess {String} description  公司级别描述
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
    "i18n": {},
    "id": "909604476331200514",
    "createdDate": "2017-09-18T02:26:21Z",
    "lastUpdatedDate": "2017-09-18T02:26:21Z",
    "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
    "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
    "companyLevelCode": "dddd",
    "tenantId": 907943944140546049,
    "description": "我是公司级别描述1",
    "enabled": true,
    "deleted": false
    }
    ,
    "success": true
    }
     */
    @Timed
    @RequestMapping(value = "/selectById",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyLevel> selectById(@RequestParam Long id){
        return ResponseEntity.ok(companyLevelService.selectById(id));
    }






    /**
     * @api {get} /api/companyLevel/selectByTenantId 查询当前租户下公司级别列表
     * @apiGroup CompanyLevel
     * @apiSuccess {Object[]} json 返回公司级别实体对象
     * @apiSuccess {Long} id  公司级别id
     * @apiSuccess {Long} tenantId  租户id
     * @apiSuccess {String} companyLevelCode  公司级别code
     * @apiSuccess {String} description  公司级别描述
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
                "i18n": {},
                "id": "909604476331200514",
                "createdDate": "2017-09-18T02:26:21Z",
                "lastUpdatedDate": "2017-09-18T02:26:21Z",
                "createdBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
                "lastUpdatedBy": "683edfba-4e52-489e-8ce4-6e820d5478b2",
                "companyLevelCode": "dddd",
                "tenantId": 907943944140546049,
                "description": "我是公司级别描述1",
                "enabled": true,
                "deleted": false
            }
        ],
        "success": true
    }
     *
     **/
    @Timed
    @RequestMapping(value = "/selectByTenantId",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyLevel>> selectByTenantId(){
       return ResponseEntity.ok(companyLevelService.selectByTenantId());
    }




    /**
     * @api {get} /api/DepartmentGroup/selectTranslatedTableInfoWithI18nById 根据id查询公司级别翻译后详情
     * @apiGroup CompanyLevel
     * @apiParam {Long} id  待查询的id
     * @apiSuccess {Object[]} json 返回公司级别实体对象
     */
    @Timed
    @RequestMapping(value = "/selectTranslatedTableInfoWithI18nById",method= RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyLevel> selectOneTranslatedTableInfoWithI18nById(@RequestParam("id") Long id){
        return ResponseEntity.ok(companyLevelService.selectOneTranslatedTableInfoWithI18nById(id));
    }

}
