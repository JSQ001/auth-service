package com.hand.hcf.app.mdata.currency.web;

import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.mdata.currency.domain.CurrencyStatus;
import com.hand.hcf.app.mdata.currency.service.CurrencyStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/28
 */
@RestController
@RequestMapping("/api/currency/status")
public class CurrencyStatusResource {


    @Autowired
    private CurrencyStatusService currencyStatusService;

    /**
     * @apiDefine currencyStatus
     * @apiSuccess {String} currencyCode 币种code
     * @apiSuccess {Boolean} enable 启用true|禁用false币种
     * @apiSuccess {Boolean} enableAutoUpdate 启用true|禁用false自动更新
     * @apiSuccess {long} setOfBooksId 账套ID
     * @apiSuccess {long} tenantId 租户ID
     * @apiSuccess {UUID} createdBy 创建人
     * @apiSuccess {DateTime} createdDate 创建时间
     * @apiSuccess {UUID} lastUpdatedBy 最近操作人
     * @apiSuccess {DateTime} lastUpdatedDate 最近操作时间
     */

    /**
     * @api {PUT} /api/currency/status/enable 更新币种启用、禁用状态
     * @apiGroup currencyStatus
     * @apiParam {UUID} currencyRateOid 币种汇率Oid
     * @apiParam {Boolean} enable 启用|禁用币种
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": {
     * "id": 3,
     * "currencyCode": "HKD",
     * "enable": false,
     * "enableAutoUpdate": false,
     * "setOfBooksId": 943461273926242305,
     * "tenantId": 1111111111,
     * "createdBy": "64e887d8-6a48-4194-8e56-3c1183471182",
     * "createdDate": "2018-03-20T02:15:17Z",
     * "lastUpdatedDate": "2018-03-20T02:23:14Z",
     * "lastUpdatedBy": "64e887d8-6a48-4194-8e56-3c1183471182"
     * }
     * }
     * @apiUse currencyStatus
     */
    @RequestMapping(value = "/enable", method = RequestMethod.PUT)
    public ResponseEntity<CurrencyStatus> enableCurrencyStatus(@RequestParam(value = "currencyRateOid", required = true) UUID currencyRateOid, @RequestParam(value = "enable", required = true) Boolean enable) {
        //参数校验
        List<ValidationError> validateErrors = new ArrayList<ValidationError>();
        if (null == currencyRateOid) {
            validateErrors.add(new ValidationError("currencyRateOid", "param currencyRateOid can not be null"));
        }
        if (null == enable) {
            validateErrors.add(new ValidationError("enable", "param enable can not be null"));
        }
        if (!CollectionUtils.isEmpty(validateErrors)) {
            throw new ValidationException(validateErrors);
        }
        return ResponseEntity.ok(currencyStatusService.enableCurrencyStatus(currencyRateOid, enable));
    }

    /**
     * @api {PUT} /api/currency/status/enable/auto/update 启用、禁用汇率自动更新功能
     * @apiGroup currencyStatus
     * @apiParam {Long} tenantId 租户ID
     * @apiParam {Long} setOfBooksId 账套ID
     * @apiParam {String} currencyCode 币种code (currencyCode不为空,更新单个币种状态.currencyCode为空，更新账套下的所有币种 "自动更新"启用状态)
     * @apiParam {Boolean} enableAutoUpdate 启用true|禁用 false
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": true
     * }
     * @apiSuccess rows true成功|false未启用
     */
    @RequestMapping(value = "/enable/auto/update", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateEnableAutoStatus(@RequestParam(name = "tenantId", required = true) Long tenantId,
                                                          @RequestParam(name = "setOfBooksId", required = true) Long setOfBooksId,
                                                          @RequestParam(name = "currencyCode", required = false) String currencyCode,
                                                          @RequestParam(name = "enableAutoUpdate", required = true) Boolean enableAutoUpdate) {
        //参数校验
        List<ValidationError> validateErrors = new ArrayList<ValidationError>();
        if (null == tenantId) {
            validateErrors.add(new ValidationError("tenantId", "param tenantId can not be null"));
        }
        if (null == setOfBooksId) {
            validateErrors.add(new ValidationError("setOfBooksId", "param setOfBooksId can not be null"));
        }
        if (null == enableAutoUpdate) {
            validateErrors.add(new ValidationError("enableAutoUpdate", "param enableAutoUpdate can not be null"));
        }
        if (!CollectionUtils.isEmpty(validateErrors)) {
            throw new ValidationException(validateErrors);
        }
        return ResponseEntity.ok(currencyStatusService.updateEnableAutoStatus(tenantId, setOfBooksId, currencyCode, enableAutoUpdate));
    }

    /**
     * @api {GET} /api/currency/status/enable/auto/update 查询租户是否开启汇率自动更新
     * @apiGroup currencyStatus
     * @apiParam {Long} tenantId 租户ID
     * @apiParam {Long} setOfBooksId 账套ID
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": true
     * }
     * @apiSuccess rows true启用|false未启用
     */
    @RequestMapping(value = "/enable/auto/update", method = RequestMethod.GET)
    public ResponseEntity<Boolean> selectByTenantIdAndSetOfBooksId(@RequestParam(name = "tenantId", required = true) Long tenantId,
                                                                   @RequestParam(name = "setOfBooksId", required = true) Long setOfBooksId) {

        //参数校验
        List<ValidationError> validateErrors = new ArrayList<ValidationError>();
        if (null == tenantId) {
            validateErrors.add(new ValidationError("tenantId", "param tenantId can not be null"));
        }
        if (null == setOfBooksId) {
            validateErrors.add(new ValidationError("setOfBooksId", "param setOfBooksId can not be null"));
        }
        if (!CollectionUtils.isEmpty(validateErrors)) {
            throw new ValidationException(validateErrors);
        }
        return ResponseEntity.ok(currencyStatusService.checkAllEnableAutoStatusTrue(tenantId, setOfBooksId));

    }
}
