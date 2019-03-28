package com.hand.hcf.app.mdata.parameter.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.parameter.domain.ParameterSetting;
import com.hand.hcf.app.mdata.parameter.dto.ParameterSettingDTO;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.service.ParameterSettingService;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 11:09
 */
@RestController
@RequestMapping("/api/parameter/setting")
public class ParameterSettingController {

    @Autowired
    private ParameterSettingService parameterSettingService;


    /**
     * @api {GET} /api/parameter/setting/page/by/level/cond 【参数明细】条件查询
     * @apiGroup ParameterSetting
     * @apiParam  {String} parameterLevel 参数级别
     * @apiParam  {Long} setOfBooksId 账套ID
     * @apiParam  {Long} companyId 公司id
     * @apiParam  {String} moduleCode 模块代码
     * @apiParam  {String} parameterCode 参数代码
     * @apiParam  {String} parameterName 参数名称
     * @apiParam  {int} page 分页page
     * @apiParam  {int} size 分页size
     * @apiSuccessExample {json} 成功返回值:
     * [
     *     {
     *         "id": "1077125882492088321",
     *         "parameterId": 1077125882492088322,
     *         "moduleCode": "ACCOUNT",
     *         "moduleName": "核算模块",
     *         "parameterCode": "ACCOUNT_ENABLE",
     *         "parameterName": "核算模块启用标志"
     *         "parameterValueType": "VALUE_LIST",
     *         "parameterLevel": "TENANT",
     *         "setOfBooksId": null,
     *         "setOfBooksName": null,
     *         "sobParameter": true,
     *         "companyId": null,
     *         "companyName": null,
     *         "companyParameter": true,
     *         "parameterValueId": "973385603146063874",
     *         "parameterValue": "Y",
     *         "parameterValueDesc": "启用预算模块",
     *     },
     * ]
     */
    @GetMapping("/page/by/level/cond")
    public ResponseEntity<List<ParameterSettingDTO>> pageParameterSettingByLevelAndCond(@RequestParam(value = "parameterLevel",required = false) ParameterLevel parameterLevel,
                                                                                        @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                                                                        @RequestParam(value = "companyId",required = false) Long companyId,
                                                                                        @RequestParam(value = "moduleCode",required = false) String moduleCode,
                                                                                        @RequestParam(value = "parameterCode",required = false) String parameterCode,
                                                                                        @RequestParam(value = "parameterName",required = false) String parameterName,
                                                                                        @RequestParam(value = "page",defaultValue = "0") int page,
                                                                                        @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ParameterSettingDTO> result = parameterSettingService.pageParameterSettingByLevelAndCond(parameterLevel, LoginInformationUtil.getCurrentTenantId(), setOfBooksId, companyId, moduleCode, parameterCode, parameterName, queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/parameter/setting 【参数明细】创建
     * @apiGroup ParameterSetting
     * @apiParam {Object} parameterSetting  参数明细
     * @apiParam {String} parameterSetting.parameterLevel  参数级别
     * @apiParam {Long} parameterSetting.setOfBooksId  账套id
     * @apiParam {Long} parameterSetting.companyId  公司id
     * @apiParam {Long} parameterSetting.parameterId  参数id
     * @apiParam {Long} parameterSetting.parameterValueId  参数值id
     * @apiParamExample {json} 请求参数:
     * {
     *      "parameterLevel":"SOB",
     *      "setOfBooksId":"2354525235252",
     *      "companyId":null,
     *      "parameterId":"2354525235252",
     *      "parameterValueId":"2354525235252"
     *}
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "id": "1077125882492088321",
     *     "parameterLevel":"SOB",
     *     "setOfBooksId":"2354525235252",
     *     "companyId":null,
     *     "parameterId":"2354525235252",
     *     "parameterValueId":"2354525235252"
     *     "createdDate": "2018-12-27T15:37:37.076+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-27T15:37:37.077+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 1
     * }
     */
    @PostMapping
    public ParameterSetting insertParameterSetting(@RequestBody ParameterSetting parameterSetting){

        return parameterSettingService.insertParameterSetting(parameterSetting);
    }

    /**
     * @api {PUT} /api/parameter/setting 【参数明细】更新
     * @apiGroup ParameterSetting
     * @apiParam {Object} parameterSetting  参数明细
     * @apiParam {String} parameterSetting.parameterLevel  参数级别
     * @apiParam {Long} parameterSetting.setOfBooksId  账套id
     * @apiParam {Long} parameterSetting.companyId  公司id
     * @apiParam {Long} parameterSetting.parameterId  参数id
     * @apiParam {Long} parameterSetting.parameterValueId  参数值id
     * @apiParamExample {json} 请求参数:
     * {
     *      "parameterLevel":"SOB",
     *      "setOfBooksId":"2354525235252",
     *      "companyId":null,
     *      "parameterId":"2354525235252",
     *      "parameterValueId":"1263523689423"
     *}
     * @apiSuccessExample {json} 成功返回值:
     * {
     *     "id": "1077125882492088321",
     *     "parameterLevel":"SOB",
     *     "setOfBooksId":"2354525235252",
     *     "companyId":null,
     *     "parameterId":"2354525235252",
     *     "parameterValueId":"2354525235252"
     *     "createdDate": "2018-12-27T15:37:37.076+08:00",
     *     "createdBy": "1",
     *     "lastUpdatedDate": "2018-12-27T15:37:37.077+08:00",
     *     "lastUpdatedBy": "1",
     *     "versionNumber": 2
     * }
     */
    @PutMapping
    public ParameterSetting updateParameterSetting(@RequestBody ParameterSetting parameterSetting){

        return parameterSettingService.updateParameterSetting(parameterSetting);
    }
    /**
     * @api {DELETE} /api/parameter/setting/{parameterSettingId} 【参数明细】删除
     * @apiGroup ParameterSetting
     * @apiParam {Long} parameterSettingiId  待删除的id
     */
    @DeleteMapping("/{parameterSettingId}")
    public void deleteParameterSettingById(@PathVariable("parameterSettingId") Long parameterSettingId){
        parameterSettingService.deleteParameterSettingById(parameterSettingId);
    }
}
