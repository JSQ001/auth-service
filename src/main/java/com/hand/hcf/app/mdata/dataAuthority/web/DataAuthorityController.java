package com.hand.hcf.app.mdata.dataAuthority.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthority;
import com.hand.hcf.app.mdata.dataAuthority.service.DataAuthorityService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:48
 * @remark
 */
@RestController
@RequestMapping("/api/system/data/authority")
@AllArgsConstructor
public class DataAuthorityController {

    private final DataAuthorityService dataAuthorityService;

    /**
     * @api {POST} /api/system/data/authority 【数据权限】保存数据权限
     * @apiDescription 保存数据权限
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} [id]  主键id
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {String} dataAuthorityCode 权限代码
     * @apiParam (请求参数) {String} dataAuthorityName 权限名称
     * @apiParam (请求参数) {String} [description] 描述
     * @apiParam (请求参数) {Map} [i18n] 多语言
     * @apiParam (请求参数) {Boolean} enabled    启用标志
     * @apiParam (请求参数) {Boolean} [deleted]   删除标志
     * @apiParam (请求参数) {Integer} [versionNumber]    版本号
     * @apiParam (请求参数) {ZonedDateTime} [createdDate]  创建时间
     * @apiParam (请求参数) {Long} [createdBy]    创建人ID
     * @apiParam (请求参数) {ZonedDateTime} [lastUpdatedDate]    最后更新时间
     * @apiParam (请求参数) {Long} [lastUpdatedBy]    更新人ID
     * @apiParam (请求参数) {List} [dataAuthorityRules]    数据权限规则
     *
     * @apiParam (数据权限规则参数) {Long} [id]  主键id
     * @apiParam (数据权限规则参数) {Long} [dataAuthorityId]  数据权限ID
     * @apiParam (数据权限规则参数) {String} dataAuthorityRuleName 规则名称
     * @apiParam (数据权限规则参数) {Map} [i18n] 多语言
     * @apiParam (数据权限规则参数) {Boolean} [deleted]    删除标志
     * @apiParam (数据权限规则参数) {Integer} [versionNumber]    版本号
     * @apiParam (数据权限规则参数) {ZonedDateTime} [createdDate]  创建时间
     * @apiParam (数据权限规则参数) {Long} [createdBy]    创建人ID
     * @apiParam (数据权限规则参数) {ZonedDateTime} [lastUpdatedDate]    最后更新时间
     * @apiParam (数据权限规则参数) {Long} [lastUpdatedBy]    更新人ID
     * @apiParam (数据权限规则参数) {List} [dataAuthorityRuleDetails]    数据权限规则明细
     *
     * @apiParam (数据权限规则明细参数) {Long} [id]  主键id
     * @apiParam (数据权限规则明细参数) {Long} [dataAuthorityId]  数据权限ID
     * @apiParam (数据权限规则明细参数) {Long} [dataAuthorityRuleId] 数据权限规则ID
     * @apiParam (数据权限规则明细参数) {String} dataType 数据类型
     * @apiParam (数据权限规则明细参数) {String} dataScope 数据范围
     * @apiParam (数据权限规则明细参数) {String} [filtrateMethod] 数据取值方式
     * @apiParam (数据权限规则明细参数) {Boolean} [deleted]    删除标志
     * @apiParam (数据权限规则明细参数) {Integer} [versionNumber]    版本号
     * @apiParam (数据权限规则明细参数) {ZonedDateTime} [createdDate]  创建时间
     * @apiParam (数据权限规则明细参数) {Long} [createdBy]    创建人ID
     * @apiParam (数据权限规则明细参数) {ZonedDateTime} [lastUpdatedDate]    最后更新时间
     * @apiParam (数据权限规则明细参数) {Long} [lastUpdatedBy]    更新人ID
     * @apiParam (数据权限规则明细参数) {List} [dataAuthorityRuleDetails]    明细值
     *
     * @apiParamExample {json} 请求报文:
     * {
        "i18n": null,
        "enabled": true,
        "tenantId": "1",
        "dataAuthorityCode": "1",
        "dataAuthorityName": "1",
        "description": "1",
        "dataAuthorityRules": [
            {
            "i18n": null,
            "dataAuthorityRuleName": "测试",
            "dataAuthorityRuleDetails": [
                {
                "dataType": "SOB",
                "dataScope": "1001",
                "filtrateMethod": "INCLUDE",
                "dataAuthorityRuleDetailValues": [
                    "1"
                    ]
                }
                ]
            }
        ]
       }
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {String} dataAuthorityCode 权限代码
     * @apiSuccess (返回参数) {String} dataAuthorityName 权限名称
     * @apiSuccess (返回参数) {String} [description] 描述
     * @apiSuccess (返回参数) {Boolean} enabled 启用标志
     * @apiSuccess (返回参数) {Map} [i18n] 多语言
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (返回参数) {List} dataAuthorityRules    数据权限规则
     *
     * @apiSuccess (数据权限规则参数) {Long} id  主键id
     * @apiSuccess (数据权限规则参数) {Long} dataAuthorityId  数据权限ID
     * @apiSuccess (数据权限规则参数) {String} dataAuthorityRuleName 规则名称
     * @apiSuccess (数据权限规则参数) {Map} [i18n] 多语言
     * @apiSuccess (数据权限规则参数) {Boolean} deleted    删除标志
     * @apiSuccess (数据权限规则参数) {Integer} versionNumber    版本号
     * @apiSuccess (数据权限规则参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (数据权限规则参数) {Long} createdBy    创建人ID
     * @apiSuccess (数据权限规则参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (数据权限规则参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (数据权限规则参数) {List} dataAuthorityRuleDetails    数据权限规则明细
     *
     * @apiSuccess (数据权限规则明细参数) {Long} id  主键id
     * @apiSuccess (数据权限规则明细参数) {Long} dataAuthorityId  数据权限ID
     * @apiSuccess (数据权限规则明细参数) {Long} dataAuthorityRuleId 数据权限规则ID
     * @apiSuccess (数据权限规则明细参数) {String} dataType 数据类型
     * @apiSuccess (数据权限规则明细参数) {String} dataScope 数据范围
     * @apiSuccess (数据权限规则明细参数) {String} filtrateMethod 数据取值方式
     * @apiSuccess (数据权限规则明细参数) {Boolean} deleted    删除标志
     * @apiSuccess (数据权限规则明细参数) {Integer} versionNumber    版本号
     * @apiSuccess (数据权限规则明细参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (数据权限规则明细参数) {Long} createdBy    创建人ID
     * @apiSuccess (数据权限规则明细参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (数据权限规则明细参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (数据权限规则明细参数) {List} dataAuthorityRuleDetails    明细值
     *
     * @apiSuccessExample {json} 返回报文:
     * {
    "i18n": null,
    "id": "1",
    "deleted": false,
    "createdDate": "2018-10-15T11:05:00.244+08:00",
    "createdBy": "1014",
    "lastUpdatedDate": "2018-10-15T11:05:00.244+08:00",
    "lastUpdatedBy": "1014",
    "versionNumber": 1,
    "enabled": true,
    "tenantId": "1",
    "dataAuthorityCode": "1",
    "dataAuthorityName": "1",
    "description": "1",
    "dataAuthorityRules": [
    {
    "i18n": null,
    "id": "1",
    "deleted": false,
    "createdDate": "2018-10-16T16:27:08+08:00",
    "createdBy": "1",
    "lastUpdatedDate": "2018-10-16T16:27:13+08:00",
    "lastUpdatedBy": "1",
    "versionNumber": 1,
    "dataAuthorityId": "1",
    "dataAuthorityRuleName": "测试",
    "dataAuthorityRuleDetails": [
    {
    "id": "1",
    "createdDate": "2018-10-16T16:28:36+08:00",
    "createdBy": "1",
    "lastUpdatedDate": "2018-10-16T16:28:39+08:00",
    "lastUpdatedBy": "1",
    "versionNumber": 1,
    "deleted": false,
    "dataAuthorityId": "1",
    "dataAuthorityRuleId": "1",
    "dataType": "SOB",
    "dataScope": "1001",
    "filtrateMethod": "INCLUDE",
    "dataAuthorityRuleDetailValues": [
     "1"
    ]
    }
    ]
    }
    ]
    }
     */
    @PostMapping
    public ResponseEntity<DataAuthority> saveDataAuthority(@RequestBody DataAuthority entity){
        return ResponseEntity.ok(dataAuthorityService.saveDataAuthority(entity));
    }


    /**
     * @api {DELETE} /api/system/data/authority/{id} 【数据权限】删除数据权限
     * @apiDescription 删除数据权限
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} id ID
     *
     * @apiParamExample {json} 请求报文:
     *  /api/system/data/authority/1
     *
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteDataAuthorityById(@PathVariable(value = "id") Long id){
        dataAuthorityService.deleteDataAuthorityById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {DELETE} /api/system/data/authority/batch 【数据权限】批量删除数据权限
     * @apiDescription 删除数据权限
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {List} ids ID集合
     *
     * @apiParamExample {json} 请求报文:
     *  {
     *      [1,2,3]
     *  }
     *
     */
    @DeleteMapping("/batch")
    public ResponseEntity deleteDataAuthorityByIds(@RequestBody List<Long> ids){
        dataAuthorityService.deleteDataAuthorityByIds(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/system/data/authority/query 【数据权限】查询数据权限
     * @apiDescription 查询数据权限
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} [id] 权限id
     * @apiParam (请求参数) {String} [dataAuthorityCode] 权限代码
     * @apiParam (请求参数) {String} [dataAuthorityName] 权限名称
     * @apiParam (请求参数) {Integer} [page] 页数
     * @apiParam (请求参数) {Integer} [size] 每页大小
     *
     * @apiParamExample {json} 请求报文:
     * /api/system/data/authority/query?dataAuthorityCode=1
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {String} dataAuthorityCode 权限代码
     * @apiSuccess (返回参数) {String} dataAuthorityName 权限名称
     * @apiSuccess (返回参数) {String} [description] 描述
     * @apiSuccess (返回参数) {Boolean} [enabled] 启用标志
     * @apiSuccess (返回参数) {Map} [i18n] 多语言
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     *     "i18n": null,
     *     "id": "1051670245335601153",
     *     "deleted": false,
     *     "createdDate": "2018-10-15T11:05:00.244+08:00",
     *     "createdBy": "1014",
     *     "lastUpdatedDate": "2018-10-15T11:06:00.244+08:00",
     *     "lastUpdatedBy": "1014",
     *     "versionNumber": 2,
     *     "enabled": true,
     *     "tenantId": "1",
     *     "dataAuthorityCode": "1",
     *     "dataAuthorityName": "1-1",
     *     "description": "1"
     * }
     */
    @GetMapping("/query")
    public ResponseEntity<List<DataAuthority>> getDataAuthorityByCond(@RequestParam(name = "id",required = false) Long id,
                                                                      @RequestParam(name = "dataAuthorityCode",required = false) String dataAuthorityCode,
                                                                      @RequestParam(name = "dataAuthorityName",required = false) String dataAuthorityName,
                                                                      Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<DataAuthority> dataAuthorityByCond = dataAuthorityService.getDataAuthorityByCond(id,dataAuthorityCode, dataAuthorityName, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/system/data/authority/query");
        return new ResponseEntity(dataAuthorityByCond,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/system/data/authority/detail/{id} 【数据权限】定位数据权限
     * @apiDescription 查询指定数据权限，并获取明细配置
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {Long} id 主键ID
     * @apiParam (请求参数) {Long} [ruleId] 规则ID
     *
     * @apiParamExample {json} 请求报文:
     * /api/system/data/authority/detail/1?ruleId=1
     *
     * @apiSuccess (数据权限参数) {Long} id  主键id
     * @apiSuccess (数据权限参数) {Long} tenantId 租户ID
     * @apiSuccess (数据权限参数) {String} dataAuthorityCode 权限代码
     * @apiSuccess (数据权限参数) {String} dataAuthorityName 权限名称
     * @apiSuccess (数据权限参数) {String} [description] 描述
     * @apiSuccess (数据权限参数) {Boolean} [enabled] 启用标志
     * @apiSuccess (数据权限参数) {Map} [i18n] 多语言
     * @apiSuccess (数据权限参数) {Boolean} enabled    启用标志
     * @apiSuccess (数据权限参数) {Boolean} deleted    删除标志
     * @apiSuccess (数据权限参数) {Integer} versionNumber    版本号
     * @apiSuccess (数据权限参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (数据权限参数) {Long} createdBy    创建人ID
     * @apiSuccess (数据权限参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (数据权限参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (数据权限参数) {List} dataAuthorityRules    数据权限规则
     *
     * @apiSuccess (数据权限规则参数) {Long} id  主键id
     * @apiSuccess (数据权限规则参数) {Long} dataAuthorityId  数据权限ID
     * @apiSuccess (数据权限规则参数) {String} dataAuthorityRuleName 规则名称
     * @apiSuccess (数据权限规则参数) {Map} [i18n] 多语言
     * @apiSuccess (数据权限规则参数) {Boolean} deleted    删除标志
     * @apiSuccess (数据权限规则参数) {Integer} versionNumber    版本号
     * @apiSuccess (数据权限规则参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (数据权限规则参数) {Long} createdBy    创建人ID
     * @apiSuccess (数据权限规则参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (数据权限规则参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (数据权限规则参数) {List} dataAuthorityRuleDetails    数据权限规则明细
     *
     * @apiSuccess (数据权限规则明细参数) {Long} id  主键id
     * @apiSuccess (数据权限规则明细参数) {Long} dataAuthorityId  数据权限ID
     * @apiSuccess (数据权限规则明细参数) {Long} dataAuthorityRuleId 数据权限规则ID
     * @apiSuccess (数据权限规则明细参数) {String} dataType 数据类型
     * @apiSuccess (数据权限规则明细参数) {String} dataScope 数据范围
     * @apiSuccess (数据权限规则明细参数) {String} filtrateMethod 数据取值方式
     * @apiSuccess (数据权限规则明细参数) {Boolean} deleted    删除标志
     * @apiSuccess (数据权限规则明细参数) {Integer} versionNumber    版本号
     * @apiSuccess (数据权限规则明细参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (数据权限规则明细参数) {Long} createdBy    创建人ID
     * @apiSuccess (数据权限规则明细参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (数据权限规则明细参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccess (数据权限规则明细参数) {List} dataAuthorityRuleDetails    明细值
     *
     *
     * @apiSuccessExample {json} 返回报文:
     * {
        "i18n": null,
        "id": "1",
        "deleted": false,
        "createdDate": "2018-10-15T11:05:00.244+08:00",
        "createdBy": "1014",
        "lastUpdatedDate": "2018-10-15T11:05:00.244+08:00",
        "lastUpdatedBy": "1014",
        "versionNumber": 1,
        "enabled": true,
        "tenantId": "1",
        "dataAuthorityCode": "1",
        "dataAuthorityName": "1",
        "description": "1",
        "dataAuthorityRules": [
        {
        "i18n": null,
        "id": "1",
        "deleted": false,
        "createdDate": "2018-10-16T16:27:08+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2018-10-16T16:27:13+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "dataAuthorityId": "1",
        "dataAuthorityRuleName": "测试",
        "dataAuthorityRuleDetails": [
        {
        "id": "1",
        "createdDate": "2018-10-16T16:28:36+08:00",
        "createdBy": "1",
        "lastUpdatedDate": "2018-10-16T16:28:39+08:00",
        "lastUpdatedBy": "1",
        "versionNumber": 1,
        "deleted": false,
        "dataAuthorityId": "1",
        "dataAuthorityRuleId": "1",
        "dataType": "SOB",
        "dataScope": "1001",
        "filtrateMethod": "INCLUDE",
        "dataAuthorityRuleDetailValues": [
        "1"
        ]
        }
        ]
        }
        ]
        }
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<DataAuthority> getDataAuthorityDetailById(@PathVariable(value = "id") Long id,
                                                                    @RequestParam(value = "ruleId",required = false) Long ruleId){
        return ResponseEntity.ok(dataAuthorityService.getDataAuthorityById(id,ruleId));
    }


    /**
     * @api {GET} /api/system/data/authority/query/all/data/authority【数据权限】查询数据权限  不分页
     * @apiDescription 查询数据权限
     * @apiGroup SysDataPermission
     * @apiParam (请求参数) {String} [dataAuthorityCode] 权限代码
     * @apiParam (请求参数) {String} [dataAuthorityName] 权限名称
     *
     * @apiParamExample {json} 请求报文:
     * /api/system/data/authority/query/all/data/authority?dataAuthorityCode=1
     *
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {String} dataAuthorityCode 权限代码
     * @apiSuccess (返回参数) {String} dataAuthorityName 权限名称
     * @apiSuccess (返回参数) {String} [description] 描述
     * @apiSuccess (返回参数) {Boolean} [enabled] 启用标志
     * @apiSuccess (返回参数) {Map} [i18n] 多语言
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     *     "i18n": null,
     *     "id": "1051670245335601153",
     *     "deleted": false,
     *     "createdDate": "2018-10-15T11:05:00.244+08:00",
     *     "createdBy": "1014",
     *     "lastUpdatedDate": "2018-10-15T11:06:00.244+08:00",
     *     "lastUpdatedBy": "1014",
     *     "versionNumber": 2,
     *     "enabled": true,
     *     "tenantId": "1",
     *     "dataAuthorityCode": "1",
     *     "dataAuthorityName": "1-1",
     *     "description": "1"
     * }
     */
    @GetMapping("/query/all/data/authority")
    public ResponseEntity<List<DataAuthority>> getDataAuthorityByCond(@RequestParam(name = "dataAuthorityCode",required = false) String dataAuthorityCode,
                                                                      @RequestParam(name = "dataAuthorityName",required = false) String dataAuthorityName) throws URISyntaxException {
        List<DataAuthority> dataAuthorityByCond = dataAuthorityService.listAllDataAuthorityByCond(dataAuthorityCode, dataAuthorityName);
        return new ResponseEntity(dataAuthorityByCond, HttpStatus.OK);
    }
}
