package com.hand.hcf.app.mdata.responsibilityCenter.web;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenterGroup;
import com.hand.hcf.app.mdata.responsibilityCenter.service.ResponsibilityCenterGroupService;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsibilityCenter/group")
public class ResponsibilityCenterGroupResource {
    @Autowired
    private ResponsibilityCenterGroupService centerGroupService;
    /**
     * @api {POST} /api/responsibilityCenter/group/insertOrUpdate 【责任中心组-新建及更新】
     * @apiGroup ResponsibilityCenterGroup
     * @apiParam {Long} tenantId 租户id
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {String} groupCode 责任中心组代码
     * @apiParam {String} groupName 责任中心组名称
     * @apiParam {Boolean} enabled 启用禁用
     *
     * @apiSuccessExample {json} Success-Response:
     *{
     *   "id": "1080297484497981441",
     *   "createdDate": "2019-01-02T10:59:25.982+08:00",
     *   "createdBy": "1",
     *   "lastUpdatedDate": "2019-01-02T10:59:25.984+08:00",
     *   "lastUpdatedBy": "1",
     *   "versionNumber": 1,
     *   "enabled": true,
     *   "tenantId": "1",
     *   "setOfBooksId": "1",
     *   "groupCode": "groupCode1",
     *   "groupName": "责任中心组1"
     *   }
     */
    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponsibilityCenterGroup> insertOrUpdateResponsibilityCenterGroup(@RequestBody ResponsibilityCenterGroup responsibilityCenterGroup){
        return ResponseEntity.ok(centerGroupService.insertOrUpdateResponsibilityCenterGroup(responsibilityCenterGroup));
    }

    /**
     * @api {POST} /api/responsibilityCenter/group/insert/relationship 【责任中心组-添加责任中心】
     * @apiGroup ResponsibilityCenterGroup
     * @apiParam centerGroupId 责任中心组id
     * @apiParam {List} ids 责任中心id
     * @apiSuccess {Boolean} success 是否成功
     */
    @RequestMapping(value = "/insert/relationship", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> insertResponsibilityCenterGroupByRelationship(@RequestParam(value = "centerGroupId") Long centerGroupId,
                                                                                 @RequestBody List<Long> ids){
        return  ResponseEntity.ok(centerGroupService.insertResponsibilityCenterGroupByRelationship(centerGroupId,ids));
    }


    /**
     * @api {POST} /api/responsibilityCenter/group/query 【责任中心组-查询】根据当前账套
     * @apiDescription 根据当前账套获取所有责任中心组
     * @apiGroup ResponsibilityCenterGroup
     * @apiParam setOfBooksId 账套id
     * @apiParam groupCode 责任中心组代码
     * @apiParam groupName 责任中心组名称
     * @apiParam enabled 启用 禁用
     * @apiParam pageable 分页
     * @apiSuccessExample {json} 返回报文:
     *[
     *   {
     *   "id": "1080297484497981441",
     *   "createdDate": "2019-01-02T10:59:25.982+08:00",
     *   "createdBy": "1",
     *   "lastUpdatedDate": "2019-01-02T10:59:25.984+08:00",
     *   "lastUpdatedBy": "1",
     *   "versionNumber": 1,
     *   "enabled": true,
     *   "tenantId": "1",
     *   "setOfBooksId": "1",
     *   "groupCode": "groupCode1",
     *   "groupName": "责任中心组1"
     *   }
     *   ]
     */
    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResponsibilityCenterGroup>> pageResponsibilityCenterGroupBySetOfBooksId(@RequestParam(value="setOfBooksId") Long setOfBooksId,
                                                                                                        @RequestParam(value = "groupCode",required = false) String groupCode,
                                                                                                        @RequestParam(value="groupName",required = false) String groupName,
                                                                                                        @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                                                        Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<ResponsibilityCenterGroup> result = centerGroupService.pageResponsibilityCenterGroupBySetOfBooksId(setOfBooksId,groupCode,groupName,enabled,page);
        return new ResponseEntity<>(result.getRecords(), PageUtil.getTotalHeader(page), HttpStatus.OK);
    }

    /**
     * @api {delete} /api/responsibilityCenter/group/delete/{id} 【责任中心组-删除】
     * @apiDescription 删除责任中心组以及责任中心关联
     * @apiGroup  ResponsibilityCenterGroup
     * @apiParam {Long} [id]  待删除的id数组
     * @apiSuccess {Boolean} success 是否成功
     *
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> delecteResponsibilityCenterGroupById(@PathVariable Long id){
        return ResponseEntity.ok(centerGroupService.delecteResponsibilityCenterGroupById(id));
    }
}
