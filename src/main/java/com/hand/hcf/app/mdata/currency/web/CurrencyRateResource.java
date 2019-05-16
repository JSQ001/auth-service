package com.hand.hcf.app.mdata.currency.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.currency.cover.CurrencyRateCover;
import com.hand.hcf.app.mdata.currency.domain.CurrencyRate;
import com.hand.hcf.app.mdata.currency.domain.TenantConfig;
import com.hand.hcf.app.mdata.currency.dto.CompanyStandardCurrencyDTO;
import com.hand.hcf.app.mdata.currency.dto.CurrencyRateDTO;
import com.hand.hcf.app.mdata.currency.dto.TenantConfigDTO;
import com.hand.hcf.app.mdata.currency.service.CurrencyI18nService;
import com.hand.hcf.app.mdata.currency.service.CurrencyRateService;
import com.hand.hcf.app.mdata.currency.service.TenantConfigService;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.system.enums.CurrencyRateSourceEnum;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.util.DateUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/28
 */
@Api(tags = "币种汇率")
@RestController
@RequestMapping("/api/currency/rate")
public class CurrencyRateResource {

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private CurrencyI18nService currencyI18nService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private SetOfBooksService setOfBooksService;

    @Autowired
    private TenantConfigService tenantConfigService;

    /**
     *
     * @apiDefine currencyRateDTORequest
     * @apiParam {UUID} [currencyRateOid] 币种Oid
     * @apiParam {String} baseCurrencyCode 持有币种code
     * @apiParam {String} [baseCurrencyName] 持有币种名称
     * @apiParam {String} currencyCode 兑换币种code
     * @apiParam {String} [currencyName] 兑换币种名称
     * @apiParam {Double} rate 汇率
     * @apiParam {DateTime} applyDate 生效日期
     * @apiParam {String} source 币种汇率来源【欧行ECB 、手动更新MANUAL】
     * @apiParam {Boolean} enable 币种汇率是否启用 【true-启用，false-未启用】
     * @apiParam {Boolean} enableAutoUpdate 是否启用汇率自动更新
     * @apiParam {Long} setOfBooksId 账套ID
     * @apiParam {Long} tenantID 租户ID
     */

    /**
     *
     * @apiDefine currencyRateDTOResponse
     * @apiSuccess {UUID} currencyRateOid 币种Oid
     * @apiSuccess {String} baseCurrencyCode 持有币种code
     * @apiSuccess {String} baseCurrencyName 持有币种名称
     * @apiSuccess {String} currencyCode 兑换币种code
     * @apiSuccess {String} currencyName 兑换币种名称
     * @apiSuccess {Double} rate 汇率
     * @apiSuccess {DateTime} applyDate 生效日期
     * @apiSuccess {String} source 币种汇率来源【欧行ECB 、手动更新MANUAL】
     * @apiSuccess {Boolean} enable 币种汇率是否启用 【true-启用，false-未启用】
     * @apiSuccess {Boolean} enableAutoUpdate 是否启用汇率自动更新
     * @apiSuccess {Long} setOfBooksId 账套ID
     * @apiSuccess {Long} tenantId 租户Id
     * @apiSuccess {DateTime} lastUpdatedDate 最近操作日期
     */


    /**
     * @api {POST} /api/currency/rate 新增单个汇率值
     * @apiGroup currencyRate
     * @apiUse currencyRateDTORequest
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": {
     * "currencyRateOid": "5ea59c26-3af7-4fa3-abb8-52b4d217374f",
     * "baseCurrencyCode": "CNY",
     * "baseCurrencyName": null,
     * "currencyCode": "USD",
     * "currencyName": null,
     * "rate": 0.63,
     * "applyDate": "2018-03-19T08:40:36Z",
     * "source": "MANUAL",
     * "enable": true,
     * "enableAutoUpdate": false,
     * "setOfBooksId": 943461273926242305,
     * "tenantId": 1111111111
     * }
     * }
     * @apiUse currencyRateDTOResponse
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<CurrencyRateDTO> insertCurrencyRate(@RequestBody @Valid CurrencyRateDTO currencyRateDTO) {
        //参数校验
        if (null != currencyRateDTO.getCurrencyRateOid()) {
            throw new ValidationException(Arrays.asList(new ValidationError("currencyRateOid", "currencyRateOid must be null ")));
        }
        currencyRateDTO.setSource(CurrencyRateSourceEnum.MANUAL.getSource());
        return ResponseEntity.ok(currencyRateService.insertCurrencyRate(currencyRateDTO));
    }

    /**
     * @api {PUT} /api/currency/rate?language 更改汇率
     * @apiGroup currencyRate
     * @apiUse currencyRateDTORequest
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": {
     * "currencyRateOid": "7306d9b9-be2a-44b0-ad9a-8bf0db5e66f0",
     * "baseCurrencyCode": "CNY",
     * "baseCurrencyName": "人民币",
     * "currencyCode": "HKD",
     * "currencyName": "港元",
     * "rate": 0.79,
     * "applyDate": "2018-03-19T16:00:00Z",
     * "source": "MANUAL",
     * "enable": false,
     * "enableAutoUpdate": false,
     * "setOfBooksId": 943461273926242305,
     * "tenantId": 1111111111,
     * "lastUpdatedDate": "2018-03-20T05:18:07Z"
     * }
     * }
     * @apiUse currencyRateDTOResponse
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<CurrencyRateDTO> updateCurrencyRate(@RequestBody @Valid CurrencyRateDTO currencyRateDTO, @RequestParam(name = "language", required = false) String language) {
        //参数校验
        if (null == currencyRateDTO.getCurrencyRateOid()) {
            throw new ValidationException(Arrays.asList(new ValidationError("currencyRateOid", "currencyRateOid can not be null ")));
        }
        currencyRateDTO.setSource(CurrencyRateSourceEnum.MANUAL.getSource());
        return ResponseEntity.ok(currencyRateService.updateCurrencyRate(currencyRateDTO, language));
    }

    /**
     * @api {GET} /api/currency/rate 生效汇率查询---单条
     * @apiGroup currencyRate
     * @apiParam {UUID} currencyOid 币种Oid
     * @apiParam {String} [language] 语言
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<CurrencyRateDTO> selectByCurrencyOidAndLanguage(@RequestParam(name = "currencyOid", required = true)
                                                                                  UUID currencyOid, @RequestParam(name = "language", required = false) String language) {
        return ResponseEntity.ok(currencyRateService.selectByCurrencyOidAndLanguage(currencyOid, language));
    }

    /**
     * @api {DELETE} /api/currency/rate 根据currencyOid进行删除
     * @apiGroup currencyRateOids
     * @apiParam {List} currencyRateOids 待删除币种汇率Oid数组
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": true
     * }
     * @apiSuccess {Boolean} rows true删除成功|false删除失败
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteByCurrencyOid(@RequestParam(name = "currencyRateOids", required = true) List<UUID> currencyRateOids) {
        return ResponseEntity.ok(currencyRateService.deleteByCurrencyRateOid(currencyRateOids));
    }


    /**
     * @api {GET} /api/currency/rate/history/list 汇率历史信息查询--分页（0-10)
     * @apiGroup currencyRate
     * @apiParam {UUID} currencyRateOid 生效币种汇率Oid
     * @apiParam {String} [language] 语言(默认'ZH_CN')
     * @apiParam {int} [page] 必填(默认0）
     * @apiParam {int} [size] 必填(默认10)
     * @apiParam {String} [startDate] 查询边界起始时间(时间格式示例：2018-03-20 23:59:59)
     * @apiParam {String} [endDate] 查询边界结束时间(时间格式示例：2018-03-20 23:59:59)
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": [
     * {
     * "currencyRateOid": "f280ba8d-fedd-4b88-9ae9-d5d8dc4b470b",
     * "baseCurrencyCode": "CNY",
     * "baseCurrencyName": "人民币",
     * "currencyCode": "USD",
     * "currencyName": "美元",
     * "rate": 0.78,
     * "applyDate": "2018-03-18T16:00:00Z",
     * "source": "MANUAL",
     * "enable": true,
     * "enableAutoUpdate": false,
     * "setOfBooksId": 943461273926242305,
     * "tenantId": 1111111111,
     * "lastUpdatedDate": "2018-03-19T18:14:08Z"
     * }
     * ],
     * "total": 0
     * }
     * @apiUse currencyRateDTOResponse
     */
    @RequestMapping(value = "/history/list", method = RequestMethod.GET)
    public ResponseEntity<Page<CurrencyRateDTO>> selectHistoryByCurrencyCodeAndSetOfBooksIdAndTenantId(@RequestParam(name = "currencyRateOid", required = true) UUID currencyRateOid,
                                                                                                       @RequestParam(name = "language", required = false) String language,
                                                                                                       @RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                                                                                       @RequestParam(name = "size", defaultValue = "10", required = false) int size,
                                                                                                       @RequestParam(name = "startDate", required = false) String startDate,
                                                                                                       @RequestParam(name = "endDate", required = false) String endDate) {

        if (null == currencyRateOid) {
            throw new ValidationException(Arrays.asList(new ValidationError("currencyRateOid", "currencyRateOid can not be null")));
        }

        return ResponseEntity.ok(currencyRateService.selectHistoryByCurrencyOidAndLanguage(currencyRateOid, language, startDate, endDate, new Page<CurrencyRateDTO>(page + 1, size)));

    }

    /**
     * @api {GET} /api/currency/rate/list 账套生效汇率批量查询--带分页&根据currencyCode来进行排序
     * @apiGroup currencyRate
     * @apiParam {String} [baseCurrencyCode] 本位币code (默认取当前切换账套本位币)
     * @apiParam {Long} [setOfBooksId] 账套ID (默认取当前切换账套id)
     * @apiParam {Long} [tenantId] 租户ID (默认取当前切换租户id)
     * @apiParam {String} language 多语言类型(默认'zh_cn')
     * @apiParam {Boolean} enable 是否启用(为null查询所有 true查询启用的汇率  false查询禁用的汇率)
     * @apiParam {int} page 当前页数(默认0)
     * @apiParam {int} size 每页大小(默认10)
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": [
     * {
     * "currencyRateOid": "f280ba8d-fedd-4b88-9ae9-d5d8dc4b470b",
     * "baseCurrencyCode": "CNY",
     * "baseCurrencyName": "人民币",
     * "currencyCode": "USD",
     * "currencyName": "美元",
     * "rate": 0.78,
     * "applyDate": "2018-03-18T16:00:00Z",
     * "source": "MANUAL",
     * "enable": true,
     * "enableAutoUpdate": false,
     * "setOfBooksId": 943461273926242305,
     * "tenantId": 1111111111,
     * "lastUpdatedDate": "2018-03-19T18:14:08Z"
     * }
     * ],
     * "total": 0
     * }
     * @apiUse currencyRateDTOResponse
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<Page<CurrencyRateDTO>> selectListBySetOfBooksId(
            @RequestParam(name = "baseCurrencyCode", required = false) String baseCurrencyCode,
            @RequestParam(name = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(name = "tenantId", required = false) Long tenantId,
            @RequestParam(name = "language", required = false) String language,
            @RequestParam(name = "enable", required = false) Boolean enable,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        //参数校验
        if (null == setOfBooksId) {
            setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        }
        if (tenantId == null) {
            tenantId = OrgInformationUtil.getCurrentTenantId();
        }

        if (StringUtils.isEmpty(baseCurrencyCode)) {
            SetOfBooks setOfBooksInfoCO = setOfBooksService.getSetOfBooksById(setOfBooksId);
            baseCurrencyCode = setOfBooksInfoCO.getFunctionalCurrencyCode();
        }

        return ResponseEntity.ok(currencyRateService.selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(baseCurrencyCode, setOfBooksId, tenantId, language, enable, new Page(page + 1, size)));
    }

    /**
     * @api {PUT} /api/currency/rate/list 批量更改币种汇率
     * @apiGroup currencyRate
     * @apiUse currencyRateDTORequest
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": [
     * {
     * "currencyRateOid": "f280ba8d-fedd-4b88-9ae9-d5d8dc4b470b",
     * "baseCurrencyCode": "CNY",
     * "baseCurrencyName": "人民币",
     * "currencyCode": "USD",
     * "currencyName": "美元",
     * "rate": 0.78,
     * "applyDate": "2018-03-18T16:00:00Z",
     * "source": "MANUAL",
     * "enable": true,
     * "enableAutoUpdate": false,
     * "setOfBooksId": 943461273926242305,
     * "tenantId": 1111111111,
     * "lastUpdatedDate": "2018-03-19T18:14:08Z"
     * }
     * ],
     * "total": 0
     * }
     * @apiUse currencyRateDTOResponse
     */
    @RequestMapping(value = "/list", method = RequestMethod.PUT)
    public ResponseEntity<Page<CurrencyRateDTO>> updateBatch(@RequestBody @Valid List<CurrencyRateDTO> currencyRateDTOS,
                                                             @RequestParam(name = "language", required = false) String language,
                                                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        if (CollectionUtils.isEmpty(currencyRateDTOS)) {
            return ResponseEntity.ok(null);
        }
        //手动更新重写汇率来源
        currencyRateDTOS.stream().forEach(u -> {
            u.setSource(CurrencyRateSourceEnum.MANUAL.getSource());
        });
        return ResponseEntity.ok(currencyRateService.updateBatch(currencyRateDTOS, language, new Page(page + 1, size)));
    }

    /**
     * @api {GET} /api/company/standard/currency/getAll/companyOID 获取公司所属账套下的所有生效汇率
     * @apiGroup CompatibleApp_currencyRate
     */
    @RequestMapping(value = "/company/standard/currency/getAll/companyOid", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyStandardCurrencyDTO>> getAllCurrencyRatesByCompanyOID(@RequestParam(name = "companyOid", required = false) UUID companyOid) {


        if (null == companyOid) {
            throw new ObjectNotFoundException(UUID.class, "companyOID=" + companyOid);
        }
        //数据查询
        String baseCurrencyCode = currencyRateService.getCompanySetOfBooksBaseCurrency(companyOid);
        CompanyCO companyCO = companyService.getByCompanyOid(String.valueOf(companyOid));
        Long setOfBooksId = companyCO.getSetOfBooksId();
        Long tenantID = OrgInformationUtil.getCurrentTenantId();
        if (null == tenantID) {
            throw new ObjectNotFoundException(Long.class, "companyOid=" + companyOid);
        }
        List<CurrencyRateDTO> currencyRateDTOS = currencyRateService.selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(baseCurrencyCode, setOfBooksId, tenantID, OrgInformationUtil.getCurrentLanguage(), true);
        List<CompanyStandardCurrencyDTO> targets = new ArrayList<CompanyStandardCurrencyDTO>();
        if (!CollectionUtils.isEmpty(currencyRateDTOS)) {
            currencyRateDTOS.stream().forEach(u -> {
                if (u != null) {
                    targets.add(CurrencyRateCover.toCompanyStandardCurrencyDTO(u));
                }
            });
        }
        return ResponseEntity.ok(targets);
    }

    /**
     * @api {GET} /api/company/standard/currency/getAll 获取单签用户所属账套下的所有生效汇率
     * @apiGroup CompatibleApp_currencyRate
     */
    @GetMapping("/company/standard/currency/getAll")
    public ResponseEntity<List<CompanyStandardCurrencyDTO>> getAllCurrencyRates(@RequestParam(name = "userOid", required = false) UUID userOid) {
        String language = OrgInformationUtil.getCurrentLanguage();

        userOid = userOid != null ? userOid : OrgInformationUtil.getCurrentUserOid();
        //数据查询
        String baseCurrencyCode = currencyRateService.getUserSetOfBooksBaseCurrency(userOid);
        CompanyCO companyCO = companyService.getById(contactService.getContactByUserOid(userOid).getCompanyId());
        Long setOfBooksId = companyCO.getSetOfBooksId();
        //Tenant tenant = userService.findCurrentTenantByUSerOid(userOid);
//        if (null == tenant) {
//            throw new BizException(RespCode.TENANT_NOT_EXIST);
//        }
//        Long tenantID = tenant.getId();
        Long tenantID = companyCO.getTenantId();
        List<CurrencyRateDTO> currencyRateDTOS = currencyRateService.selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(baseCurrencyCode, setOfBooksId, tenantID, language, true);
        List<CompanyStandardCurrencyDTO> targets = new ArrayList<CompanyStandardCurrencyDTO>();
        if (!CollectionUtils.isEmpty(currencyRateDTOS)) {
            currencyRateDTOS.stream().forEach(u -> {
                if (u != null) {
                    targets.add(CurrencyRateCover.toCompanyStandardCurrencyDTO(u));
                }
            });
        }
        return ResponseEntity.ok(targets);
    }

    /**
     * @api {GET} /api/currency/rate/listBaseAndCurrentMessage 通过baseCode获取币种信息
     * @apiGroup currencyRate
     * @apiUse currencyRateDTORequest
     * @apiSuccessExample {json} Success-Response:
     * {
     * "success": true,
     * "code": "0000",
     * "rows": [
     * {
     * "currencyRateOid": "f280ba8d-fedd-4b88-9ae9-d5d8dc4b470b",
     * "baseCurrencyCode": "CNY",
     * "baseCurrencyName": "人民币",
     * "currencyCode": "USD",
     * "currencyName": "美元",
     * "rate": 0.78,
     * "applyDate": "2018-03-18T16:00:00Z",
     * "source": "MANUAL",
     * "enable": true,
     * "enableAutoUpdate": false,
     * "setOfBooksId": 943461273926242305,
     * "tenantId": 1111111111,
     * "lastUpdatedDate": "2018-03-19T18:14:08Z"
     * }
     * ],
     * "total": 0
     * }
     * @apiUse currencyRateDTOResponse
     */
    @GetMapping(value = "/listBaseAndCurrentMessage")
    public List<CurrencyRateDTO> listCurrenciesByCode(@RequestParam(value = "code") String code) {
        String language = OrgInformationUtil.getCurrentLanguage();
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<CurrencyRateDTO> list = currencyRateService.selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(code,OrgInformationUtil.getCurrentSetOfBookId(),tenantId,language,true);
        return list;
    }

    /**
     * @api {GET} /api/currency/rate/company/standard/currency/get X用户X币种X生效日期X语言汇率查询
     * @apiGroup currencyRate
     * @apiParam {String} currency 币种
     * @apiParam {String} currencyDate 生效日期
     */
    @GetMapping("/company/standard/currency/get")
    public ResponseEntity<CompanyStandardCurrencyDTO> getActiveCurrencyRate(@RequestParam(name = "currency") String currency,
                                                                            @RequestParam(name = "currencyDate") String currencyDate,
                                                                            @RequestParam(name = "userOid", required = false) UUID userOid) {
        if (null == userOid) {
            userOid = OrgInformationUtil.getCurrentUserOid();
        }

        CurrencyRate currencyRate = currencyRateService.selectActiveCurrencyRateByUserOid(userOid, currency, DateUtil.stringToZonedDateTime(currencyDate));
        CurrencyRateDTO currencyRateDTO = CurrencyRateCover.parse(currencyRate);
        currencyI18nService.i18nTranslateCurrencyRateDTOs(Arrays.asList(currencyRateDTO), OrgInformationUtil.getCurrentLanguage());
        return ResponseEntity.ok(CurrencyRateCover.toCompanyStandardCurrencyDTO(currencyRateDTO));
    }

    /**
     * 新增或更新租户及账套下的汇率容差
     *
     * @param tenantConfig 汇率容差
     * @return 更新后的汇率容差
     */
    @PostMapping(value = "/tenant/config/input")
    @ApiOperation(value = "新增或更新租户及账套下的汇率容差", notes = "需传入账套id 告警汇率容差 禁止汇率容差 开发：王帅")
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "body", dataType = "TenantConfig", name = "tenantConfig", value = "汇率容差", required = true) })
    public ResponseEntity<TenantConfigDTO> updateTenantConfig(@ApiParam(value = "汇率容差信息") @RequestBody TenantConfig tenantConfig) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        tenantConfig.setTenantId(tenantId);
        return ResponseEntity.ok(tenantConfigService.updateTenantConfig(tenantConfig));
    }

    /**
     * 根据账套id查询汇率容差
     * @param setOfBooksId 账套id
     * @return 汇率容差
     */
    @GetMapping(value = "/tenant/config/get")
    @ApiOperation(value = "查询汇率容差", notes = "根据账套id查询汇率容差 开发：王帅")
    public ResponseEntity<TenantConfigDTO> getTenantConfig(@ApiParam(value = "账套id")@RequestParam(name = "setOfBooksId") long setOfBooksId) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        TenantConfigDTO tenantConfig = new TenantConfigDTO();
        tenantConfig.setTenantId(tenantId);
        tenantConfig.setSetOfBooksId(setOfBooksId);
        return ResponseEntity.ok(tenantConfigService.getTenantConfig(tenantConfig));
    }
}
