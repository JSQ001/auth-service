package com.hand.hcf.app.base.web;

import com.hand.hcf.app.base.domain.ErrorMessage;
import com.hand.hcf.app.base.service.ErrorMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @description: 报错信息controller
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/10/18
 */
@RestController
@RequestMapping("/api/error/message")
public class ErrorMessageController {
    private ErrorMessageService errorMessageService;

    public ErrorMessageController(ErrorMessageService errorMessageService){
        this.errorMessageService = errorMessageService;
    }

    /**
     * 新增单个 错误信息
     * @param errorMessage
     * @return
     */
    /**
     * @api {POST} /api/error/message/create 【错误信息】单个新增
     * @apiDescription 新增单个错误信息
     * @apiGroup Auth2Service
     * @apiParam {String} moduleCode 模块代码
     * @apiParam {String} language 语言
     * @apiParam {String} errorCode 报错信息代码
     * @apiParam {String} errorMessage 报错信息
     * @apiSuccess {Long} id 主键id
     * @apiSuccess {String} moduleCode 模块代码
     * @apiSuccess {String} language 语言
     * @apiSuccess {String} errorCode 报错信息代码
     * @apiSuccess {String} errorMessage 报错信息
     * @apiSuccess {Integer} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建日期
     * @apiSuccess {Long} createdBy 创建用户id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess {Long} lastUpdatedBy 最后更新用户id
     * @apiParamExample {json} 请求参数：
     * {
     *   "moduleCode":"PAYMENT",
     *   "language":"zh_CN",
     *   "errorCode":"10001",
     *   "errorMessage":"该数据不存在！"
     * }
     * @apiSuccessExample {json} 成功返回值：
     * {
     *   "id": "1064360038309027841",
     *   "createdDate": "2018-11-19T11:29:42.649+08:00",
     *   "createdBy": "1054",
     *   "lastUpdatedDate": "2018-11-19T11:29:42.65+08:00",
     *   "lastUpdatedBy": "1054",
     *   "versionNumber": 1,
     *   "moduleCode": "PAYMENT",
     *   "language": "zh_CN",
     *   "errorCode": "10001",
     *   "errorMessage": "该数据不存在！"
     * }
     */
    @PostMapping("/create")
    public ResponseEntity<ErrorMessage> createErrorMessage(@Valid @RequestBody ErrorMessage errorMessage){
        return ResponseEntity.ok(errorMessageService.createErrorMessage(errorMessage));
    }

    /**
     * 修改单个 错误信息
     * @param errorMessage
     * @return
     */
    /**
     * @api {PUT} /api/error/message/create/update 【错误信息】单个修改
     * @apiDescription 修改单个错误信息
     * @apiGroup Auth2Service
     * @apiParam {Long} id 主键id
     * @apiParam {String} moduleCode 模块代码
     * @apiParam {String} language 语言
     * @apiParam {String} errorCode 报错信息代码
     * @apiParam {String} errorMessage 报错信息
     * @apiParam {Integer} versionNumber 版本号
     * @apiSuccess {Long} id 主键id
     * @apiSuccess {String} moduleCode 模块代码
     * @apiSuccess {String} language 语言
     * @apiSuccess {String} errorCode 报错信息代码
     * @apiSuccess {String} errorMessage 报错信息
     * @apiSuccess {Integer} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建日期
     * @apiSuccess {Long} createdBy 创建用户id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess {Long} lastUpdatedBy 最后更新用户id
     * @apiParamExample {json} 请求参数：
     * {
     *   "id":1064360038309027841,
     *   "moduleCode":"PAYMENT",
     *   "language":"zh_CN",
     *   "errorCode":"10001",
     *   "errorMessage":"该数据不存在update！",
     *   "versionNumber":1
     * }
     * @apiSuccessExample {json} 返回参数：
     * {
     *   "id": "1064360038309027841",
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "versionNumber": 2,
     *   "moduleCode": "PAYMENT",
     *   "language": "zh_CN",
     *   "errorCode": "10001",
     *   "errorMessage": "该数据不存在update！"
     * }
    }
     */
    @PutMapping("/update")
    public ResponseEntity<ErrorMessage> updateErrorMessage(@RequestBody ErrorMessage errorMessage){
        return ResponseEntity.ok(errorMessageService.updateErrorMessage(errorMessage));
    }

    /**
     * 根据id查询单个 错误信息
     * @param id
     * @return
     */
    /**
     * @api {GET} /api/error/message/query/{id} 【错误信息】根据id查询单个错误信息
     * @apiDescription 根据id查询单个错误信息
     * @apiGroup Auth2Service
     * @apiParam {Long} id 主键id
     * @apiSuccess {Long} id 主键id
     * @apiSuccess {String} moduleCode 模块代码
     * @apiSuccess {String} language 语言
     * @apiSuccess {String} errorCode 报错信息代码
     * @apiSuccess {String} errorMessage 报错信息
     * @apiSuccess {Integer} versionNumber 版本号
     * @apiSuccess {ZonedDateTime} createdDate 创建日期
     * @apiSuccess {Long} createdBy 创建用户id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess {Long} lastUpdatedBy 最后更新用户id
     * @apiParamExample {json} 请求参数：
     * /api/error/message/query/1064360038309027841
     * @apiSuccessExample {json} 返回参数：
     * {
     *   "id": "1064360038309027841",
     *   "createdDate": "2018-11-19T11:29:42.649+08:00",
     *   "createdBy": "1054",
     *   "lastUpdatedDate": "2018-11-19T11:40:23.414+08:00",
     *   "lastUpdatedBy": "1054",
     *   "versionNumber": 2,
     *   "moduleCode": "PAYMENT",
     *   "language": "zh_CN",
     *   "errorCode": "10001",
     *   "errorMessage": "该数据不存在update！"
     * }
     */
    @GetMapping("/query/{id}")
    public ResponseEntity<ErrorMessage> getErrorMessageById(@PathVariable Long id){
        return ResponseEntity.ok(errorMessageService.selectById(id));
    }
}
