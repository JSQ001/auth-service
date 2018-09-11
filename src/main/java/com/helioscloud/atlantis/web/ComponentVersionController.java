package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.ComponentVersion;
import com.helioscloud.atlantis.service.ComponentVersionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 组件版本控制类
 */
@RestController
@RequestMapping("/api/componentVersion")
public class ComponentVersionController {
    private final ComponentVersionService componentVersionService;

    public ComponentVersionController(ComponentVersionService componentVersionService) {
        this.componentVersionService = componentVersionService;
    }

    /**
     * @api {POST} /api/componentVersion/create 【系统框架】组件版本创建
     * @apiDescription 创建组件版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} componentId 组件ID
     * @apiParam (请求参数) {String} remark 备注说明
     * @apiParam (请求参数) {String} contents 内容信息
     * @apiParamExample {json} 请求报文:
     * {
     * "componentId":"1031480667845984258",
     * "remark":"测试版本1",
     * "contents":"测试测试1"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (请求参数) {Long} componentId 组件ID
     * @apiSuccess (请求参数) {String} remark 备注说明
     * @apiSuccess (请求参数) {String} contents 内容信息
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031484643240869889",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T18:14:37.663+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:14:37.663+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本1",
     * "contents": "测试测试1",
     * "componentId": "1031480667845984258"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<ComponentVersion> createComponentVersion(@RequestBody ComponentVersion componentVersion) {
        return ResponseEntity.ok(componentVersionService.createComponentVersion(componentVersion));
    }

    /**
     * @api {PUT} /api/componentVersion/update 【系统框架】组件版本更新
     * @apiDescription 更新组件版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 版本ID
     * @apiSuccess (请求参数) {Long} componentId 组件ID
     * @apiSuccess (请求参数) {String} remark 备注说明
     * @apiSuccess (请求参数) {String} contents 内容信息
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} enabled 启用标志
     * @apiParam (请求参数) {String} deleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031484643240869889",
     * "enabled": false,
     * "deleted": false,
     * "versionNumber": 1,
     * "remark": "测试版本11",
     * "contents": "测试测试1",
     * "componentId": "1031480667845984258"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Long} componentId 组件ID
     * @apiSuccess (返回参数) {String} remark 备注说明
     * @apiSuccess (返回参数) {String} contents 内容信息
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031484643240869889",
     * "enabled": false,
     * "deleted": false,
     * "createdDate": "2018-08-20T18:14:37.663+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "remark": "测试版本11",
     * "contents": "测试测试1",
     * "componentId": "1031480667845984258"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<ComponentVersion> updateComponentVersion(@RequestBody ComponentVersion componentVersion) {
        return ResponseEntity.ok(componentVersionService.updateComponentVersion(componentVersion));
    }

    /**
     * @api {DELETE} /api/componentVersion/delete/{id} 【系统框架】组件版本删除
     * @apiDescription 删除组件版本
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/componentVersion/delete/1031484643240869889
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteComponentVersion(@PathVariable Long id) {
        componentVersionService.deleteComponentVersion(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/componentVersion/batch/delete 【系统框架】组件版本批量删除
     * @apiDescription 批量删除组件版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 版本ID
     * @apiParamExample {json} 请求报文
     * [1031484643240869889,1031485782829072385]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteBatchComponentVersion(@RequestBody List<Long> ids) {
        componentVersionService.deleteBatchComponentVersion(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/componentVersion/query/{id} 【系统框架】组件版本查询
     * @apiDescription 查询组件版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 组件版本ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/componentVersion/query/1031485826063958018
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031485826063958018",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T18:19:19.671+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:19:19.671+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本4",
     * "contents": "测试测试4",
     * "componentId": "1031480667845984258"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<ComponentVersion> getComponentVersionById(@PathVariable Long id) {
        return ResponseEntity.ok(componentVersionService.getComponentVersionById(id));
    }

    /**
     * @api {GET} /api/componentVersion/query 【系统框架】组件版本查询分页
     * @apiDescription 根据组件Id，查询所有组件版本 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} componentId 组件ID
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/componentVersion/query?componentId=1031480667845984258&enabled=true&page=0&size=10
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031485802370334721",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T18:19:14.022+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:19:14.022+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本3",
     * "contents": "测试测试3",
     * "componentId": "1031480667845984258"
     * },
     * {
     * "id": "1031485826063958018",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T18:19:19.671+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T18:19:19.671+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "remark": "测试版本4",
     * "contents": "测试测试4",
     * "componentId": "1031480667845984258"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<ComponentVersion>> getComponentVersionsByComponentId(@RequestParam Long componentId,
                                                                                    @RequestParam(required = false) Boolean enabled,
                                                                                    Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ComponentVersion> list = componentVersionService.getComponentVersionsByComponentId(componentId, enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/componentVersion/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/componentVersion/query/latest/byMenuId 【系统框架】菜单获取组件版本
     * @apiDescription 通过菜单id 获取最新的组件版本
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} menuId 菜单ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/componentVersion/query/latest/byMenuId?menuId=1031480667845984258
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1037345649706258435",
     * "createdDate": "2018-09-05T22:24:10+08:00",
     * "createdBy": null,
     * "lastUpdatedDate": "2018-09-05T22:24:10+08:00",
     * "lastUpdatedBy": null,
     * "versionNumber": null,
     * "deleted": null,
     * "enabled": true,
     * "remark": "新建2",
     * "contents": "[{\"type\":\"button\",\"id\":\"3208990402\",\"props\":{\"id\":\"3208990402\",\"style\":{}},\"text\":\"btn\",\"parent\":0}]",
     * "componentId": "1037345649253273601"
     * }
     */
    @GetMapping("/query/latest/byMenuId")
    public ComponentVersion getLatestComponentVersionByMenuId(@RequestParam Long menuId) {
        return componentVersionService.getLatestComponentVersionByMenuId(menuId);
    }
}
