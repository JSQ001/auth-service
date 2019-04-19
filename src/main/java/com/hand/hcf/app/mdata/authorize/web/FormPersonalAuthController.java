package com.hand.hcf.app.mdata.authorize.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.authorize.domain.FormPersonalAuth;
import com.hand.hcf.app.mdata.authorize.dto.FormPersonalAuthDTO;
import com.hand.hcf.app.mdata.authorize.service.FormPersonalAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 单据个人授权接口类
 * @author shouting.cheng
 * @date 2019/1/22
 */
@RestController
@RequestMapping("/api/authorize/form/personal/auth")
public class FormPersonalAuthController {
    @Autowired
    private FormPersonalAuthService formPersonalAuthService;

    /**
     * @apiDefine Authorize 代理授权
     */

    /**
     * @api {POST} /api/authorize/form/personal/auth 【单据个人授权】创建
     * @apiGroup Authorize
     * @apiParam {Object} auth  单据个人授权
     * @apiParam {String} auth.documentCategory  单据大类
     * @apiParam {Long} [auth.formId]  单据类型ID
     * @apiParam {Long} auth.mandatorId  委托人ID
     * @apiParam {Long} auth.baileeId  受托人ID
     * @apiParam {ZonedDateTime} auth.startDate  有效日期从
     * @apiParam {ZonedDateTime} [auth.endDate]  有效日期至
     * @apiParamExample {json} 请求参数:
     * {
     * 	"documentCategory": "800100",
     * 	"formId": "234232525",
     * 	"mandatorId": "1",
     * 	"baileeId": "2",
     * 	"startDate": "2018-12-24T16:56:37.077+08:00",
     * 	"endDate": ""
     * }
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "id": "1087996525412352001",
     *     "createdDate": "2019-01-23T16:52:40.325+08:00",
     *     "createdBy": "1083751705402064897",
     *     "lastUpdatedDate": "2019-01-23T16:52:40.326+08:00",
     *     "lastUpdatedBy": "1083751705402064897",
     *     "versionNumber": 1,
     *     "documentCategory": "800100",
     *     "formId": "234232525",
     *     "mandatorId": "1",
     *     "baileeId": "2",
     *     "tenantId": "1083751703623680001",
     *     "startDate": "2018-12-24T16:56:37.077+08:00",
     *     "endDate": null
     * }
     */
    @PostMapping
    public FormPersonalAuth createFormPersonalAuth(@RequestBody FormPersonalAuth auth){

        return formPersonalAuthService.createFormPersonalAuth(auth);
    }

    /**
     * @api {PUT} /api/authorize/form/personal/auth 【单据个人授权】更新
     * @apiGroup Authorize
     * @apiParam {Object} auth  单据个人授权
     * @apiParam {String} [auth.documentCategory]  单据大类
     * @apiParam {Long} [auth.formId]  单据类型ID
     * @apiParam {Long} [auth.mandatorId]  委托人ID
     * @apiParam {Long} [auth.baileeId]  受托人ID
     * @apiParam {ZonedDateTime} [auth.startDate]  有效日期从
     * @apiParam {ZonedDateTime} [auth.endDate]  有效日期至
     * @apiParam {Long} auth.id  ID
     * @apiParam {Integer} auth.versionNumber  版本号
     * @apiParamExample {json} 请求参数:
     * {
     *     "id": "1087996525412352001",
     *     "createdDate": "2019-01-23T16:52:40.325+08:00",
     *     "createdBy": "1083751705402064897",
     *     "lastUpdatedDate": "2019-01-23T16:52:40.326+08:00",
     *     "lastUpdatedBy": "1083751705402064897",
     *     "versionNumber": 1,
     *     "documentCategory": "800100",
     *     "formId": "234232525",
     *     "mandatorId": "1",
     *     "baileeId": "2",
     *     "tenantId": "1083751703623680001",
     *     "startDate": "2018-12-24T16:56:37.077+08:00",
     *     "endDate": null
     * }
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "id": "1087996525412352001",
     *     "createdDate": "2019-01-23T16:52:40.325+08:00",
     *     "createdBy": "1083751705402064897",
     *     "lastUpdatedDate": "2019-01-23T16:52:40.326+08:00",
     *     "lastUpdatedBy": "1083751705402064897",
     *     "versionNumber": 2,
     *     "documentCategory": "800100",
     *     "formId": "234232525",
     *     "mandatorId": "1",
     *     "baileeId": "23",
     *     "tenantId": "1083751703623680001",
     *     "startDate": "2018-12-24T16:56:37.077+08:00",
     *     "endDate": null
     * }
     */
    @PutMapping
    public FormPersonalAuth updateFormPersonalAuth(@RequestBody FormPersonalAuth auth){

        return formPersonalAuthService.updateFormPersonalAuth(auth);
    }

    /**
     * @api {DELETE} /api/authorize/form/personal/auth/{id} 【单据个人授权】删除
     * @apiGroup Authorize
     * @apiParam {Long} dimensionId  待删除的id
     */
    @DeleteMapping("/{id}")
    public void deleteFormPersonalAuthById(@PathVariable(value = "id") Long id) {
        formPersonalAuthService.deleteFormPersonalAuthById(id);
    }

    /**
     * @api {GET} /api/authorize/form/personal/auth/{id} 【单据个人授权】查询
     * @apiGroup Authorize
     * @apiParam {Long} id  单据个人授权id
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "documentCategory": "单据大类1",
     *     "formId": "234232525",
     *     "formName": "表单1",
     *     "mandatorId": "1",
     *     "mandatorName": "系统管理员",
     *     "baileeId": "23",
     *     "baileeName": "员工1",
     *     "tenantId": "1083751703623680001",
     *     "startDate": "2018-12-24T16:56:37.077+08:00",
     *     "endDate": null
     * }
     */
    @GetMapping("/{id}")
    public FormPersonalAuthDTO getFormPersonalAuthById(@PathVariable(value = "id") Long id) {
        return formPersonalAuthService.getFormPersonalAuthById(id);
    }

    /**
     * @api {GET} /api/authorize/form/personal/auth/pageByCondition 【单据个人授权】条件查询
     * @apiDescription 用于单据个人授权列表界面的条件查询
     * @apiGroup Authorize
     * @apiParam  {String} [documentCategory] 单据大类
     * @apiParam  {Long} [formId] 单据类型
     * @apiParam  {Long} [baileeId] 受托人
     * @apiParam  {String} [startDate] 有效日期从
     * @apiParam  {String} [endDate] 有效日期至
     * @apiParam  {int} [page] 分页page
     * @apiParam  {int} [size] 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "documentCategory": "单据大类1",
     *         "formId": "234232525",
     *         "formName": "单据类型1",
     *         "mandatorId": "1",
     *         "mandatorName": "系统管理员",
     *         "baileeId": "23",
     *         "baileeName": "员工1",
     *         "tenantId": "1083751703623680001",
     *         "startDate": "2018-12-24T16:56:37.077+08:00",
     *         "endDate": null
     *     }
     * ]
     */
    @GetMapping("/pageByCondition")
    public ResponseEntity<List<FormPersonalAuthDTO>> pageFormPersonalAuthByCondition(@RequestParam(required = false) String documentCategory,
                                                                                     @RequestParam(required = false) Long formId,
                                                                                     @RequestParam(required = false) Long baileeId,
                                                                                     @RequestParam(required = false) String startDate,
                                                                                     @RequestParam(required = false) String endDate,
                                                                                     @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                     @RequestParam(value = "size",defaultValue = "10") int size) {
        Page mybatisPage = PageUtil.getPage(page, size);
        ZonedDateTime requisitionStartDate = DateUtil.stringToZonedDateTime(startDate);
        ZonedDateTime requisitionEndDate = DateUtil.stringToZonedDateTime(endDate);
        List<FormPersonalAuthDTO> reportLineList = formPersonalAuthService.pageFormPersonalAuthByCondition(documentCategory, formId, baileeId, requisitionStartDate, requisitionEndDate, mybatisPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(mybatisPage);
        return new ResponseEntity<>(reportLineList, httpHeaders, HttpStatus.OK);
    }
}
