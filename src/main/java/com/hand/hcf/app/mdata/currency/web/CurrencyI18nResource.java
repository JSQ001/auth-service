package com.hand.hcf.app.mdata.currency.web;

import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.cover.CurrencyI18nCover;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.dto.CurrencyI18nDTO;
import com.hand.hcf.app.mdata.currency.service.CurrencyI18nService;
import com.hand.hcf.app.mdata.currency.service.CurrencyRateService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by fanfuqiang 2018/11/28
 */
@RestController
@RequestMapping("/api/currencyI18n")
public class CurrencyI18nResource {

    @Autowired
    private CurrencyI18nService currencyI18nService;

    @Autowired
    private CurrencyRateService currencyRateService;

    /**
     * @apiDefine currencyI18nResponse
     * @apiSuccess {Long} id 主键
     * @apiSuccess {String} currencyCode 币种code
     * @apiSuccess {String} currencyName 币种名称
     * @apiSuccess {String} language 语言类型
     *@apiSuccess {UUID} createdBy 创建人
     * @apiSuccess {DateTime} createdDate 创建日期
     * @apiSuccess {DateTime} lastUpdatedDate 最近操作日期
     *@apiSuccess {UUID} lastUpdatedBy 最近操作人
     *
     *
     *
     */

    /**
     * @apiDefine currencyI18nDTOResponse
     * @apiSuccess {String} currencyCode 币种code
     * @apiSuccess {String} currencyName 币种名称
     * @apiSuccess {String} language 语言类型
     *
     */


    /**
     * @api {GET} /api/currencyI18n 币种查询
     * @apiGroup CurrencyI18n
     * @apiParam {String} currencyCode 币种code (1.currencyCode ==null或""查询所有  2.currencyCode != null"或"根据币种code查询)
     * @apiParam {String} language 语言(默认null[查询所有],en英文,zh_CN中文）
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": [
     * {
     * "id": "1",
     * "currencyCode": "CNY",
     * "currencyName": "人民币",
     * "language": "zh_cn",
     * "createdBy": "64e887d8-6a48-4194-8e56-3c1183471182",
     * "createdDate": "2018-03-19T06:29:13Z",
     * "lastUpdatedDate": "2018-03-19T06:29:13Z",
     * "lastUpdatedBy": "64e887d8-6a48-4194-8e56-3c1183471182"
     * }]
     * }
     * @apiUse currencyI18nResponse
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<CurrencyI18n>> queryByCode(@RequestParam(name = "currencyCode", required = false) String currencyCode) {
        String language = OrgInformationUtil.getCurrentLanguage();
        return ResponseEntity.ok(currencyI18nService.queryByCurrencyCode(currencyCode, language));
    }


    /**
     * @api {POST} /api/currencyI18n
     * @apiGroup CurrencyI18n
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CurrencyI18n> insertData(@RequestBody @Valid CurrencyI18n currencyI18n) {
        return ResponseEntity.ok(currencyI18nService.insertCurrencyI18n(currencyI18n));
    }

    /**
     * @api {PUT} /api/currencyI18n
     * @apiGroup CurrencyI18n
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<CurrencyI18n> updateByCurrencyCodeAndLanguage(@RequestBody @Valid CurrencyI18n currencyI18n) {
        return ResponseEntity.ok(currencyI18nService.updateByCurrencyCodeAndLanguage(currencyI18n));
    }

    /**
     * @api {DELETE} /api/currencyI18n
     * @apiGroup CurrencyI18n
     * @apiParam {Long} id 数据主键ID
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteByID(@RequestParam(name = "id", required = true) Long id) {
        return ResponseEntity.ok(currencyI18nService.deleteByID(id));
    }

    /**
     * @api {GET} /api/currencyI18n/select/not/created/currency 查询用户可新建的币种
     * @apiGroup CurrencyI18n
     * @apiParam {Long} [tenantId] 租户ID（默认当前切换账户tenantId)
     * @apiParam {Long} [setOfBooksId] 账套ID(默认当前切换账户setOfBooksId)
     * @apiParam {String} [baseCurrencyCode] 账套本位币(默认当前切换账套本位币)
     * @apiParam {String} [language] 语言类型（中文-zh_cn,英文-en,默认zh_CN)
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": [
     * {
     * "currencyCode": "TWD",
     * "currencyName": "Taiwan Dollar",
     * "language": "en",
     * }]}
     * @apiuse currencyI18nDTOResponse
     */
    @RequestMapping(value = "/select/not/created/currency", method = RequestMethod.GET)
    public ResponseEntity<List<CurrencyI18nDTO>> selectSefOfBooksNotCreatedCurrency(@RequestParam(name = "setOfBooksId", required = false) Long setOfBooksId,
                                                                                    @RequestParam(name = "baseCurrencyCode", required = false) String baseCurrencyCode) {
        Long  tenantId = OrgInformationUtil.getCurrentTenantId();
        if (setOfBooksId == null) {
            setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        }
        if (StringUtils.isEmpty(baseCurrencyCode)) {
            baseCurrencyCode = currencyRateService.getSetOfBooksBaseCurrency(setOfBooksId);
        }
        if (null == baseCurrencyCode) {
            throw new BizException(RespCode.CURRENCY_5010, new Object[]{tenantId, setOfBooksId});
        }
        List<CurrencyI18n> currencyI18nList = currencyI18nService.selectSefOfBooksNotCreatedCurrency(tenantId, setOfBooksId, baseCurrencyCode, OrgInformationUtil.getCurrentLanguage());
        return ResponseEntity.ok(CurrencyI18nCover.toCurrencyI18nDTO(currencyI18nList));

    }
}
