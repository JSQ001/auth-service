package com.helioscloud.atlantis.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Module;
import com.helioscloud.atlantis.service.ModuleService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 模块控制类
 */
@RestController
@RequestMapping("/api/module")
public class ModuleController {
    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    /**
     * @api {POST} /api/module/create 【系统框架】模块创建
     * @apiDescription 创建模块
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {String} moduleCode 模块代码
     * @apiParam (请求参数) {String} moduleName 模块名称
     * @apiParamExample {json} 请求报文:
     * {
     * "moduleCode":"M001",
     * "moduleName":"费用管理模块"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} moduleCode 模块代码
     * @apiSuccess (返回参数) {String} moduleName 模块名称
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031475475788365825",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:38:11.985+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:38:11.985+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "moduleCode": "M001",
     * "moduleName": "费用管理模块"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<Module> createModule(@RequestBody Module module) {
        return ResponseEntity.ok(moduleService.createModule(module));
    }

    /**
     * @api {PUT} /api/module/update 【系统框架】模块更新
     * @apiDescription 更新模块
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 模块ID
     * @apiParam (请求参数) {String} [moduleCode] 模块代码不允许修改
     * @apiParam (请求参数) {String} moduleName 模块名称
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiParam (请求参数) {String} enabled 启用标志
     * @apiParam (请求参数) {String} deleted 删除标志
     * @apiParamExample {json} 请求报文:
     * {
     * "id": "1031475475788365825",
     * "enabled": true,
     * "deleted": false,
     * "versionNumber": 1,
     * "moduleName": "费用管理模块111"
     * }
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} moduleCode 模块代码
     * @apiSuccess (返回参数) {String} moduleName 模块名称
     * @apiSuccess (返回参数) {Boolean} enabled    启用标志
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031475475788365825",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:38:11.985+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": null,
     * "lastUpdatedBy": null,
     * "versionNumber": 1,
     * "moduleCode": "M001",
     * "moduleName": "费用管理模块111"
     * }
     */
    @PutMapping("/update")
    public ResponseEntity<Module> updateModule(@RequestBody Module module) {
        return ResponseEntity.ok(moduleService.updateModule(module));
    }

    /**
     * @api {DELETE} /api/module/delete/{id} 【系统框架】模块删除
     * @apiDescription 删除模块
     * @apiGroup SysFrameWork
     * @apiParamExample {json} 请求报文:
     * http://localhost:9082/api/module/delete/1031475475788365825
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} /api/module/batch/delete 【系统框架】模块批量删除
     * @apiDescription 批量删除模块
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 模块ID
     * @apiParamExample {json} 请求报文
     * [1031475475788365825,1031476014311833602]
     * @apiSuccessExample {json} 返回报文:
     * []
     */
    @PostMapping("/batch/delete")
    public ResponseEntity deleteModuleByIds(@RequestBody List<Long> ids) {
        moduleService.deleteBatchModule(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} /api/module/query/{id} 【系统框架】模块查询
     * @apiDescription 查询模块
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Long} id 模块ID
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/module/query/1031476034830368769
     * @apiSuccessExample {json} 返回报文:
     * {
     * "id": "1031476034830368769",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:40:25.259+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:40:25.259+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "moduleCode": "M003",
     * "moduleName": "费用管理模块3"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    /**
     * @api {GET} /api/module/query 【系统框架】模块查询分页
     * @apiDescription 查询所有模块 分页
     * @apiGroup SysFrameWork
     * @apiParam (请求参数) {Boolean} [enabled] 启用标识 如果不传，则不控制，如果传了，则根据传的值控制
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页大小
     * @apiParamExample {json} 请求报文
     * http://localhost:9082/api/module/query?enabled=true&page=0&size=2
     * @apiSuccessExample {json} 返回报文:
     * [
     * {
     * "id": "1031476034830368769",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:40:25.259+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:40:25.259+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "moduleCode": "M003",
     * "moduleName": "费用管理模块3"
     * },
     * {
     * "id": "1031476064928694273",
     * "enabled": true,
     * "deleted": false,
     * "createdDate": "2018-08-20T17:40:32.435+08:00",
     * "createdBy": 1005,
     * "lastUpdatedDate": "2018-08-20T17:40:32.435+08:00",
     * "lastUpdatedBy": 1005,
     * "versionNumber": 1,
     * "moduleCode": "M004",
     * "moduleName": "费用管理模块4"
     * }
     * ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<Module>> getModules(@RequestParam(required = false) Boolean enabled,
                                                   Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<Module> list = moduleService.getModules(enabled, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/module/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

}
