package com.hand.hcf.app.mdata.company.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ClientCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.conver.CompanyCover;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.dto.CompanySimpleDTO;
import com.hand.hcf.app.mdata.company.dto.CompanySobDTO;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.parameter.service.ParameterSettingService;
import com.hand.hcf.app.mdata.utils.ParameterCodeConstants;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.security.AuthoritiesConstants;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.util.*;

/*import com.hand.hcf.app.client.oauth.ClientCO;*/

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/1 14:15
 * @remark
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class CompanyController {

    @Autowired
    Environment env;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ContactService contactService;
    @Autowired
    private ParameterSettingService parameterSettingService;

    /**
     * @api {get} /api/my/companies 获取当前登录用户公司详情
     * @apiGroup Company
     * @apiVersion 0.1.0
     * @apiSuccessExample {json} 响应示例
     * {
     * "groupCompanyOid": null,
     * "companyOid": "2ec774f5-7aba-486c-bd48-cf2ae74c9d9f",
     * "name": "三全科技",
     * "logoURL": "https://helioscloud-uat-static.oss-cn-shanghai.aliyuncs.com/company/logo/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f-001.jpg?x-oss-process=image/resize,h_80",
     * "createdDate": "2016-09-22T11:29:33Z",
     * "doneRegisterLead": true,
     * "taxId": "2121",
     * "noticeType": 0,
     * "dimissionDelayDays": 0,
     * "passwordExpireDays": 0,
     * "passwordRule": null,
     * "passwordLengthMin": 0,
     * "passwordLengthMax": 0,
     * "passwordRepeatTimes": 0,
     * "createDataType": 0
     * }
     */
    @RequestMapping(value = "/my/companies",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    // @PreAuthorize("hasRole('" + AuthoritiesConstants.USER + "')")
    public ResponseEntity<CompanyDTO> getMyCompany() {
        UUID companyOid = OrgInformationUtil.getCurrentCompanyOid();
        if (null == companyOid){
            return ResponseEntity.ok().build();
        }else {
            CompanyDTO companyDTO = companyService.getByCompanyOid(companyOid);
            return Optional.ofNullable(companyDTO)
                    .map(result -> {
                        String value = parameterSettingService.getTenantParameterValueByCode(result.getTenantId(),
                                ParameterCodeConstants.COMPANY_UNIT_RELATION);
                        String flag = "Y";
                        if (flag.equals(value)){
                            result.setCompanyUnitFlag(Boolean.TRUE);
                        } else {
                            result.setCompanyUnitFlag(Boolean.FALSE);
                        }
                        return new ResponseEntity<>(
                            result,
                            HttpStatus.OK);
                    })
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    /**
     * @api {get} /api/companyId/logo/{companyOid} 更新公司LOGO
     * @apiGroup Company
     * @apiVersion 0.1.0
     * @apiParam {String} companyOid 公司Oid
     * @apiParam {file} file Logo
     */
    @RequestMapping(value = "/company/logo/{companyOid}", method = RequestMethod.POST)
    public ResponseEntity<CompanyDTO> uploadCompanyLogo(@PathVariable("companyOid") UUID companyOid, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(CompanyCover.companyToCompanyDTO(companyService.uploadCompanyLogo(companyOid, file)));
    }

    /**
     * 获取公司统计信息
     *
     * @return
     */
    @RequestMapping(value = "/company/info/count", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Long>> getCompanyCountsInfo() {
        return ResponseEntity.ok(companyService.getCompanyCountInfo(companyService.findOne(OrgInformationUtil.getCurrentCompanyId())));


    }

    /**
     * @api {GET} /api/companyId/my/clientInfo 获取公司的客户端认证账户
     * @apiGroup Company
     * @apiParam {String} roleType 集团（TENANT）或公司模式
     * @apiSuccess {Object} ClientCO 客户端信息实体
     * @apiSuccess {String} ClientCO.clientId 客户端Id
     * @apiSuccess {String} ClientCO.clientSecret 客户端Id
     * @apiSuccessExample Success-Response:
     * {
     * "clientId": "odo-integration",
     * "clientSecret": "K2QzPPz3fqQNEnsbwupD1b1IDPPg0RfkdWalXysL7wd"
     * }
     */
    @RequestMapping(value = "/company/my/clientInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.COMPANY_ADMIN)
    public ResponseEntity<ClientCO> getMyCompanyClient(@RequestParam(value = "roleType", required = false) String roleType) {
        List<ClientCO> companyClient = null;
        if (OrgInformationUtil.hasTenantAuthority(roleType)) {
            companyClient = companyService.getTenantClient(OrgInformationUtil.getCurrentTenantId());
        } else {
            companyClient = companyService.getMyCompanyClient(OrgInformationUtil.getCurrentCompanyOid());
        }
        if (CollectionUtils.isNotEmpty(companyClient)) {
            return ResponseEntity.ok(companyClient.get(0));
        } else {
            return ResponseEntity.ok(null);
        }
    }

    /**
     * @apiDefine CompanyDTO
     * @apiSuccess  {Object[]} CompanyDTOS 公司集合
     * @apiSuccess  {Long} CompanyDTOS.id 公司id
     * @apiSuccess  {UUID} CompanyDTOS.groupCompanyOid 公司组Oid
     * @apiSuccess  {UUID} CompanyDTOS.companyOid 公司Oid
     * @apiSuccess  {String} CompanyDTOS.name 公司名
     * @apiSuccess  {String} CompanyDTOS.logoURL 公司logoURL路径
     * @apiSuccess  {DateTime} CompanyDTOS.createdDate 创建日期
     * @apiSuccess  {boolean} CompanyDTOS.doneRegisterLead 是否创建引导完毕
     * @apiSuccess  {String} CompanyDTOS.taxId 税号
     * @apiSuccess  {int} CompanyDTOS.noticeType 绑定类型
     * @apiSuccess  {int} CompanyDTOS.dimissionDelayDays 离职延迟天数
     * @apiSuccess  {String} CompanyDTOS.passwordRule 密码失效日期
     * @apiSuccess  {int} CompanyDTOS.passwordLengthMin 密码最小长度
     * @apiSuccess  {int} CompanyDTOS.passwordLengthMax 密码最大长度
     * @apiSuccess  {int} CompanyDTOS.passwordRepeatTimes 密码多久时间内允许重复,0即为允许重复
     * @apiSuccess  {int} CompanyDTOS.createDataType 数据导入方式:1001-手工创建和接口导入，1002-excel导入
     * @apiSuccess  {String} CompanyDTOS.companyCode 公司代码
     * @apiSuccess  {String} CompanyDTOS.address 公司地址
     * @apiSuccess  {Long} CompanyDTOS.companyLevelId 公司级别Id
     * @apiSuccess  {String} CompanyDTOS.companyLevelName 公司级别名
     * @apiSuccess  {Long} CompanyDTOS.parentCompanyId 父部门Id
     * @apiSuccess  {String} CompanyDTOS.parentCompanyName 父部门名
     * @apiSuccess  {DateTime} CompanyDTOS.startDateActive 有效开始日期
     * @apiSuccess  {DateTime} CompanyDTOS.endDateActive 有效结束日期
     * @apiSuccess  {Long} CompanyDTOS.companyTypeId 公司类型id
     * @apiSuccess  {String} CompanyDTOS.companyTypeName 公司类型名称
     * @apiSuccess  {Long} CompanyDTOS.setOfBooksId 账套id
     * @apiSuccess  {String} CompanyDTOS.setOfBooksName 账套名称
     * @apiSuccess  {Long} CompanyDTOS.legalEntityId 法人实体id
     * @apiSuccess  {String} CompanyDTOS.legalEntityName 法人实体名称
     * @apiSuccess  {String} CompanyDTOS.baseCurrency 本位币
     * @apiSuccess  {String} CompanyDTOS.baseCurrencyName 本位币名称
     * @apiSuccess  {Long} CompanyDTOS.tenantId 租户id
     */


    /**
     * @api {GET} /api/companyId/by/condition  根据账套id及组合条件查询公司信息
     * @apiGroup Company
     * @apiParam {Long} setOfBooksId 帐套ID
     * @apiParam {String} [companyCode] 公司编码
     * @apiParam {String} [name] 公司名，模糊查询
     * @apiParam {String} [companyCodeFrom] 公司编码从
     * @apiParam {String} [companyCodeTo] 公司编码到
     * @apiUse CompanyDTO
     * @apiSuccessExample Success-Response:
     * [
     * {
     * "id": 3,
     * "groupCompanyOid": null,
     * "companyOid": "ccfe7be4-0e4f-4fd9-8439-07566124e924",
     * "name": "日出东方太阳能股份有限公司",
     * "logoURL": null,
     * "createdDate": null,
     * "doneRegisterLead": false,
     * "taxId": null,
     * "noticeType": 0,
     * "dimissionDelayDays": 0,
     * "passwordExpireDays": 0,
     * "passwordRule": null,
     * "passwordLengthMin": 0,
     * "passwordLengthMax": 0,
     * "passwordRepeatTimes": 0,
     * "createDataType": 0,
     * "companyCode": null,
     * "address": null,
     * "companyLevelId": "0",
     * "companyLevelName": null,
     * "parentCompanyId": "0",
     * "parentCompanyName": null,
     * "startDateActive": null,
     * "endDateActive": null,
     * "companyTypeId": "0",
     * "companyTypeName": null,
     * "setOfBooksId": "908690135607222274",
     * "setOfBooksName": null,
     * "legalEntityId": "0",
     * "legalEntityName": null,
     * "baseCurrency": "CNY",
     * "baseCurrencyName": "人民币",
     * "tenantId": null
     * }
     * ]
     */
    @RequestMapping(value = "/company/by/condition", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanyDTO>> getCompanyByCondition(@RequestParam(name = "setOfBooksId") Long setOfBooksId,
                                                                  @RequestParam(name = "companyCode", required = false) String companyCode,
                                                                  @RequestParam(name = "name", required = false) String name,
                                                                  @RequestParam(name = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                  @RequestParam(name = "companyCodeTo", required = false) String companyCodeTo,
                                                                  @RequestParam(name = "companyGroupId", required = false) Long companyGroupId,
                                                                  Pageable pageable) throws URISyntaxException {
        Page<CompanyDTO> page = companyService.getCompanyBySetOfBooksIdAndCondition(setOfBooksId, companyCode, name, companyCodeFrom, companyCodeTo, companyGroupId, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/companyId/by/condition");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }

    /**
     * 根据条件分页查询公司信息(公司code、公司名称、账套id、法人实体id)
     *
     * @param companyCode：公司code
     * @param name：公司名称
     * @param setOfBooksId：账套id
     * @param legalEntityId：法人实体id
     * @return
     */
    @RequestMapping(value = "/company/by/term", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyByCondition(@RequestParam(name = "companyCode", required = false) String companyCode,
                                                                   @RequestParam(name = "name", required = false) String name,
                                                                   @RequestParam(name = "setOfBooksId", required = false) Long setOfBooksId,
                                                                   @RequestParam(name = "legalEntityId", required = false) Long legalEntityId,
                                                                   @RequestParam(name = "enabled", required = false) Boolean enabled,
                                                                   Pageable pageable) throws URISyntaxException {
        Page<CompanyDTO> page = companyService.findCompanyByTerm(OrgInformationUtil.getCurrentTenantId(), companyCode, name, setOfBooksId, legalEntityId, enabled, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companyId/by/term");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/companyId/deploy/enumeration  查询未分配值列表的公司
     * @apiGroup Company
     * @apiParam {String} [companyCode] 公司编码
     * @apiParam {String} [name] 公司名，模糊查询
     * @apiParam {Long} [source] 需分配的值列表Id
     * @apiParam {Long} [legalEntityId] 法人实体Id
     * @apiParam {Long} [companyLevelId] 公司级别Id
     * @apiParam {String} [companyCodeFrom] 公司编码从
     * @apiParam {String} [companyCodeTo] 公司编码到
     * @apiUse CompanyDTO
     * @apiSuccessExample Success-Response:
     * [
     * {
     * "id": 3,
     * "groupCompanyOid": null,
     * "companyOid": "ccfe7be4-0e4f-4fd9-8439-07566124e924",
     * "name": "日出东方太阳能股份有限公司",
     * "logoURL": null,
     * "createdDate": null,
     * "doneRegisterLead": false,
     * "taxId": null,
     * "noticeType": 0,
     * "dimissionDelayDays": 0,
     * "passwordExpireDays": 0,
     * "passwordRule": null,
     * "passwordLengthMin": 0,
     * "passwordLengthMax": 0,
     * "passwordRepeatTimes": 0,
     * "createDataType": 0,
     * "companyCode": null,
     * "address": null,
     * "companyLevelId": "0",
     * "companyLevelName": null,
     * "parentCompanyId": "0",
     * "parentCompanyName": null,
     * "startDateActive": null,
     * "endDateActive": null,
     * "companyTypeId": "0",
     * "companyTypeName": null,
     * "setOfBooksId": "908690135607222274",
     * "setOfBooksName": null,
     * "legalEntityId": "0",
     * "legalEntityName": null,
     * "baseCurrency": "CNY",
     * "baseCurrencyName": "人民币",
     * "tenantId": null
     * }
     * ]
     */
    @RequestMapping(value = "/company/deploy/enumeration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyDeployEnumeration(@RequestParam(name = "companyCode", required = false) String companyCode,
                                                                         @RequestParam(name = "name", required = false) String name,
                                                                         @RequestParam(name = "source", required = false) Long source,
                                                                         @RequestParam(name = "legalEntityId", required = false) Long legalEntityId,
                                                                         @RequestParam(name = "companyLevelId", required = false) Long companyLevelId,
                                                                         @RequestParam(name = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                         @RequestParam(name = "companyCodeTo", required = false) String companyCodeTo,
                                                                         Pageable pageable) throws URISyntaxException {
        Page<CompanyDTO> page = companyService.findCompanyForEnumerationDeploy(OrgInformationUtil.getCurrentTenantId(), name, source, companyCode, companyLevelId, legalEntityId, companyCodeFrom, companyCodeTo, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/companyId/deploy/enumeration");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/companyId/deploy/carousel  查询未分配公告的公司
     * @apiGroup Company
     * @apiParam {String} [companyCode] 公司编码
     * @apiParam {String} [name] 公司名，模糊查询
     * @apiParam {Long} [source] 需分配的公告Id
     * @apiParam {Long} [legalEntityId] 法人实体Id
     * @apiParam {Long} [companyLevelId] 公司级别Id
     * @apiParam {String} [companyCodeFrom] 公司编码从
     * @apiParam {String} [companyCodeTo] 公司编码到
     * @apiUse CompanyDTO
     * @apiSuccessExample Success-Response:
     * [
     * {
     * "id": 3,
     * "groupCompanyOid": null,
     * "companyOid": "ccfe7be4-0e4f-4fd9-8439-07566124e924",
     * "name": "日出东方太阳能股份有限公司",
     * "logoURL": null,
     * "createdDate": null,
     * "doneRegisterLead": false,
     * "taxId": null,
     * "noticeType": 0,
     * "dimissionDelayDays": 0,
     * "passwordExpireDays": 0,
     * "passwordRule": null,
     * "passwordLengthMin": 0,
     * "passwordLengthMax": 0,
     * "passwordRepeatTimes": 0,
     * "createDataType": 0,
     * "companyCode": null,
     * "address": null,
     * "companyLevelId": "0",
     * "companyLevelName": null,
     * "parentCompanyId": "0",
     * "parentCompanyName": null,
     * "startDateActive": null,
     * "endDateActive": null,
     * "companyTypeId": "0",
     * "companyTypeName": null,
     * "setOfBooksId": "908690135607222274",
     * "setOfBooksName": null,
     * "legalEntityId": "0",
     * "legalEntityName": null,
     * "baseCurrency": "CNY",
     * "baseCurrencyName": "人民币",
     * "tenantId": null
     * }
     * ]
     */
    @RequestMapping(value = "/company/deploy/carousel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyDeployCarousel(@RequestParam(name = "companyCode", required = false) String companyCode,
                                                                      @RequestParam(name = "name", required = false) String name,
                                                                      @RequestParam(name = "source", required = false) Long source,
                                                                      @RequestParam(name = "legalEntityId", required = false) Long legalEntityId,
                                                                      @RequestParam(name = "companyLevelId", required = false) Long companyLevelId,
                                                                      @RequestParam(name = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                      @RequestParam(name = "companyCodeTo", required = false) String companyCodeTo,
                                                                      Pageable pageable) throws URISyntaxException {
        Page<CompanyDTO> page = companyService.findCompanyForCarouselDeploy(OrgInformationUtil.getCurrentTenantId(), name, source, companyCode, companyLevelId, legalEntityId, companyCodeFrom, companyCodeTo, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/companyId/deploy/carousel");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/companyId/deploy/levels  查询未分配城市级别的公司
     * @apiGroup Company
     * @apiParam {String} [companyCode] 公司编码
     * @apiParam {String} [name] 公司名，模糊查询
     * @apiParam {Long} [source] 需分配的城市级别Id
     * @apiParam {Long} [legalEntityId] 法人实体Id
     * @apiParam {Long} [companyLevelId] 公司级别Id
     * @apiParam {String} [companyCodeFrom] 公司编码从
     * @apiParam {String} [companyCodeTo] 公司编码到
     * @apiUse CompanyDTO
     * @apiSuccessExample Success-Response:
     * [
     * {
     * "id": 3,
     * "groupCompanyOid": null,
     * "companyOid": "ccfe7be4-0e4f-4fd9-8439-07566124e924",
     * "name": "日出东方太阳能股份有限公司",
     * "logoURL": null,
     * "createdDate": null,
     * "doneRegisterLead": false,
     * "taxId": null,
     * "noticeType": 0,
     * "dimissionDelayDays": 0,
     * "passwordExpireDays": 0,
     * "passwordRule": null,
     * "passwordLengthMin": 0,
     * "passwordLengthMax": 0,
     * "passwordRepeatTimes": 0,
     * "createDataType": 0,
     * "companyCode": null,
     * "address": null,
     * "companyLevelId": "0",
     * "companyLevelName": null,
     * "parentCompanyId": "0",
     * "parentCompanyName": null,
     * "startDateActive": null,
     * "endDateActive": null,
     * "companyTypeId": "0",
     * "companyTypeName": null,
     * "setOfBooksId": "908690135607222274",
     * "setOfBooksName": null,
     * "legalEntityId": "0",
     * "legalEntityName": null,
     * "baseCurrency": "CNY",
     * "baseCurrencyName": "人民币",
     * "tenantId": null
     * }
     * ]
     */
    @RequestMapping(value = "/company/deploy/levels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyDeployLevels(@RequestParam(name = "companyCode", required = false) String companyCode,
                                                                    @RequestParam(name = "name", required = false) String name,
                                                                    @RequestParam(name = "source", required = false) Long source,
                                                                    @RequestParam(name = "legalEntityId", required = false) Long legalEntityId,
                                                                    @RequestParam(name = "companyLevelId", required = false) Long companyLevelId,
                                                                    @RequestParam(name = "companyCodeFrom", required = false) String companyCodeFrom,
                                                                    @RequestParam(name = "companyCodeTo", required = false) String companyCodeTo,
                                                                    Pageable pageable) throws URISyntaxException {
        Page<CompanyDTO> page = companyService.findCompanyForLevelsDeploy(OrgInformationUtil.getCurrentTenantId(), name, source, companyCode, companyLevelId, legalEntityId, companyCodeFrom, companyCodeTo, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/companyId/deploy/levels");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据租户id查询公司信息
     *
     * @return
     */
    @RequestMapping(value = "/company/by/tenant", method = RequestMethod.GET)
    public ResponseEntity<List<CompanyDTO>> getTenantAllCompany(@RequestParam(value = "companyType", required = false) String companyType,
                                                                @RequestParam(value = "filterCompanyOids", required = false) List<UUID> filterCompanyOids,
                                                                @RequestParam(value = "legalEntityId", required = false) Long legalEntityId) {
        List<CompanyDTO> tenantAllCompany = companyService.findAllCompanyByTenantIdAndCompanyType(OrgInformationUtil.getCurrentTenantId(), legalEntityId, companyType, filterCompanyOids);
        return ResponseEntity.ok(tenantAllCompany);
    }

    /**
     * 根据租户id查询启用的(公司名称和公司oid或法人实体名称和法人oid)方法
     * 老公司情况下查询对应法人实体
     * 新公司则直接查询公司
     *
     * @return
     */
    @RequestMapping(value = "/company/name/oid/by/tenant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompanySimpleDTO>> getTenantAllCompanyNameAndOid(@RequestParam(name = "keyword", required = false) String keyword,
                                                                                @RequestParam(name = "enabled", required = false) Boolean enabled,
                                                                                Pageable pageable) throws URISyntaxException {
        Page<CompanySimpleDTO> page = companyService.getTenantAllCompanyNameAndOid(OrgInformationUtil.getCurrentTenantId(), keyword, enabled,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/companyId/deploy/carousel");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/available", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    // @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "') or hasRole('" + AuthoritiesConstants.COMPANY_ADMIN + "') or hasRole('" + AuthoritiesConstants.ROLE_TENANT_ADMIN + "') or hasRole('" + AuthoritiesConstants.COMPANY_FINANCE_ADMIN + "')")
    public ResponseEntity<List<Company>> getCurrentCompanyAuthority(@RequestParam(required = false) String keyword, @RequestParam(value = "enabled", required = false) Boolean enabled, Pageable pageable) throws URISyntaxException {
        List<Company> result = new ArrayList<Company>();
        Page<Company> companies = new Page<Company>();
        Boolean isTenantAdmin =OrgInformationUtil.isCurrentUserInRole(AuthoritiesConstants.ROLE_TENANT_ADMIN);
        if (isTenantAdmin) {
            //查询当前租户下的公司
            companies = companyService.findByTenantIdAndNameLike(OrgInformationUtil.getCurrentTenantId(), keyword, enabled, pageable);
        } else {
            //返回当前用户对应的公司
            UUID userOid = OrgInformationUtil.getCurrentUserOid();
            Company company = companyService.findCompanyByUserOid(userOid);
            if (StringUtils.isNotEmpty(keyword) && !keyword.equalsIgnoreCase(company.getName())) {
                companies.setRecords(result);
            } else {
                result.add(company);
                companies.setRecords(result);
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(companies, "/companyId/available");
        return new ResponseEntity<>(companies.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/company/available/by/setOfBooks 根据账套ID和公司名称/代码查找公司（公司名称和代码是模糊查找）
     * @apiGroup Company
     * @apiParam {string} [setOfBooksId] 账套ID
     * @apiParam {string} [keyword] 关键字（公司名称/代码)
     * @apiParam {boolean} [enabled]
     * @apiUse Comapny
     * @apiSuccessExample Success-Response:
     * [
     * {
     * "i18n": null,
     * "id": "1083751704185716737",
     * "deleted": false,
     * "createdDate": "2019-01-11T23:45:16.068+08:00",
     * "createdBy": "1",
     * "lastUpdatedDate": "2019-01-17T16:11:45.612+08:00",
     * "lastUpdatedBy": "1083751705402064897",
     * "versionNumber": 7,
     * "enabled": true,
     * "companyOid": "49fa21ac-f072-4717-bf43-7e2609c3cd67",
     * "name": "小嘛呀小二郎公司",
     * "doneRegisterLead": true,
     * "taxId": null,
     * "initFinance": false,
     * "groupCompanyOid": null,
     * "tenantId": "1083751703623680001",
     * "setOfBooksId": "1083762150064451585",
     * "legalEntityId": "1083762392822378498",
     * "companyCode": "GS00001",
     * "address": "adadad",
     * "companyLevelId": null,
     * "parentCompanyId": null,
     * "companyTypeId": null,
     * "startDateActive": "2016-01-01T00:28:25+08:00",
     * "endDateActive": null,
     * "path": "001",
     * "depth": 1,
     * "showCustomLogo": null,
     * "logoId": null
     * }
     * ]
     *
     * @Author mh.z
     * @Date 2019/01/23
     * @Description 根据账套ID和公司名称/代码查找公司（公司名称和代码是模糊查找）
     *
     * @param setOfBooksId 账套ID
     * @param keyword 关键字（公司名称/代码）
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/company/available/by/setOfBooks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Company>> getSetOfBookAllCompany(@RequestParam() Long setOfBooksId, @RequestParam(required = false) String keyword, @RequestParam(value = "enabled", required = false) Boolean enabled, @RequestParam(value = "ignoreCompanyId", required = false) Long ignoreCompanyId, Pageable pageable) throws URISyntaxException {
        List<Company> result = new ArrayList<Company>();
        Page<Company> companies = new Page<Company>();

        // 根据账套ID和公司名称/代码查找公司（公司名称和代码是模糊查找）
        companies = companyService.findBySetOfBookAndNameLike(setOfBooksId, keyword, enabled,ignoreCompanyId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(companies, "/companyId/available");

        return new ResponseEntity<>(companies.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据法人实体id查询公司信息
     *
     * @param legalEntityId：法人实体id
     * @param keyword：输入文本
     * @return
     */
    @RequestMapping(value = "/company/by/legalentity", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyByLegalEntityId(@RequestParam(name = "legalEntityId") Long legalEntityId,
                                                                       @RequestParam(name = "keyword") String keyword, Pageable pageable) throws URISyntaxException {
        Page<CompanyDTO> page = companyService.findCompanyByLegalEntityId(legalEntityId, keyword, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companyId/by/legalentity");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }

    /**
     * 查询公司信息或法人实体信息(法人实体信息封装到公司dto中)
     *
     * @return
     */
    @RequestMapping(value = "/company/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompany() {
        return ResponseEntity.ok(companyService.findCompanyOrLegalEntityInfo(OrgInformationUtil.getCurrentCompanyId(), OrgInformationUtil.getCurrentTenantId()));
    }

    /**
     * 有租户信息只查询租户下的公司信息(启用和禁用的)
     */
    @RequestMapping(value = "/company/by/tenantID", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyByTenantID() {
        return ResponseEntity.ok(companyService.findTenantAllCompanySorted(OrgInformationUtil.getCurrentTenantId()));
    }

    /**
     * 根据companyOid查询公司详情
     */
    @RequestMapping(value = "/company/by/companyOids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Company>> findCompanyByCompanyOids(@RequestParam(name = "companyOids") List<UUID> companyOids) {
        return ResponseEntity.ok(companyService.findByCompanyOidIn(companyOids));
    }

    @RequestMapping(value = "/company/{companyId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long companyId) {
        log.debug("REST request to get Company : {}", companyId);
        CompanyDTO companyDTO = companyService.quoteAttributeAssignmentPublic(CompanyCover.companyToCompanyDTO(companyService.findOne(companyId)));
        return Optional.ofNullable(companyDTO)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/company/user",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CompanyDTO> getUserCompany(@RequestParam(name = "useroid") UUID userOid) {
        if (userOid == null) {
            return null;
        }
        UserDTO user = contactService.getUserDTOByUserOid(userOid);
        if (user == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        if (!user.getTenantId().equals(OrgInformationUtil.getCurrentTenantId())) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        CompanyDTO companyDTO = companyService.companyToCompanyDTO(companyService.findOne(user.getCompanyId()));
        return Optional.ofNullable(companyDTO)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 运营平台服务调用，根据租户id获取有效的公司
     *
     * @param tenantId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @GetMapping(value = "/company/by/tenant/page", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize(("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')"))
    public ResponseEntity<List<CompanyDTO>> getCompaniesByTenantId(@RequestParam("tenantId") Long tenantId,
                                                                   Pageable pageable) throws URISyntaxException {
        Page page= PageUtil.getPage(pageable);
        List<CompanyDTO> coms = companyService.getCompaniesByTenantId(tenantId, page);
        return new ResponseEntity<>(coms, PageUtil.getTotalHeader(page), HttpStatus.OK);
    }


    @GetMapping(value = "/company/by/one", produces = MediaType.APPLICATION_JSON_VALUE)
    // @PreAuthorize(("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')"))
    public ResponseEntity<CompanyDTO> findCompanyById(@RequestParam("id") Long id) throws URISyntaxException {
        return ResponseEntity.ok(companyService.findCompanyById(id));
    }

    /**
     * 根据条件分页查询公司信息
     *
     * @param tenantId
     * @param infoId
     * @param companyCode
     * @param name
     * @param setOfBooksId
     * @param isEnabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/company/dto/by/tenant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyDTO>> findCompanyDTOByTenantId(@RequestParam(value = "tenantId") Long tenantId,
                                                                     @RequestParam(value = "infoId", required = false) Long infoId,
                                                                     @RequestParam(value = "companyCode", required = false) String companyCode,
                                                                     @RequestParam(value = "name", required = false) String name,
                                                                     @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                     @RequestParam(value = "isEnabled", required = false, defaultValue = "true") Boolean isEnabled,
                                                                     Pageable pageable) throws URISyntaxException {
        Page<CompanyDTO> page = companyService.findCompanyDTOByTenantId(tenantId, infoId, companyCode, name, setOfBooksId, isEnabled, pageable);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companyId/dto/by/tenant");
        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/name/setOfBooksId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanySobDTO>> getCompanyByCondition(@RequestParam(required = false) String keyword,
                                                                     @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                     @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
                                                                     Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CompanySobDTO> companies = companyService.getCompanyByCondition(keyword, OrgInformationUtil.getCurrentTenantId(), enabled, setOfBooksId, page);
        page.setRecords(companies);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/companyId/name/setOfBooksId");
        return new ResponseEntity<>(companies, headers, HttpStatus.OK);
    }

    /**
     * @api {GET} /api/company/query 【公司-获取公司】
     * @apiGroup Company
     * @apiDescription  根据当前所选账套下所有启用公司
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {String} keyword 公司代码或者名称
     * @apiParam {String} companyCodeFrom 公司代码从
     * @apiParam {String} companyCodeTo 公司代码至
     * @apiParam {int} page 分页
     * @apiParam {int} size 分页
     * @apiParamExample {json} Request-Param:
     *  http://localhost:9083/api/company/query?setOfBooksId=1078107093880250370
     * @apiSuccessExample {json} Success-Result:
     *   [
    {
    "i18n": null,
    "id": "1",
    "deleted": false,
    "createdDate": "2018-12-13T15:14:14+08:00",
    "createdBy": "0",
    "lastUpdatedDate": "2018-12-15T00:21:31.143344+08:00",
    "lastUpdatedBy": "0",
    "versionNumber": 1,
    "enabled": true,
    "companyOid": "e4b4a421-0355-4449-a610-26ff99322ab1",
    "name": "0",
    "doneRegisterLead": true,
    "taxId": null,
    "initFinance": false,
    "groupCompanyOid": null,
    "tenantId": "1",
    "setOfBooksId": "1078107093880250370",
    "legalEntityId": null,
    "companyCode": "0",
    "address": null,
    "companyLevelId": null,
    "parentCompanyId": null,
    "companyTypeId": null,
    "startDateActive": null,
    "endDateActive": null,
    "path": null,
    "depth": null,
    "showCustomLogo": null,
    "logoId": null
    },
    ]
     */
    @GetMapping(value = "/company/query",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Company>> pageCompanyBySetOfBooksIdAndCond(@RequestParam(value="setOfBooksId") Long setOfBooksId,
                                                                          @RequestParam(value = "keyword",required = false) String keyword,
                                                                          @RequestParam(value = "companyCodeFrom",required = false) String codeFrom,
                                                                          @RequestParam(value = "companyCodeTo",required = false) String codeTo,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page mybaitsPage = PageUtil.getPage(page,size);
        List<Company> companyList = companyService.pageCompanyBySetOfBooksIdAndCond(setOfBooksId,keyword,codeFrom,codeTo,mybaitsPage);
        companyList.stream()
                .forEach(company -> {
                    StringBuilder strBuilder = new StringBuilder()
                            .append(company.getCompanyCode())
                            .append("-")
                            .append(company.getName());
                    company.setCompanyCodeName(strBuilder.toString());
                });
        HttpHeaders headers = PageUtil.getTotalHeader(mybaitsPage);
        return new ResponseEntity<>(companyList,headers, HttpStatus.OK);

    }

    /**
     * 查询租户下的公司信息 enabled为null时查询全部包括启用和禁用
     *
     */
    @RequestMapping(value = "/company/by/tenantId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CompanyCO>> findCompanyByTenantId(@RequestParam(required = false) String keyword,
                                                                 @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                                 @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                 @RequestParam(value = "size", required = false, defaultValue = "10") int size) throws URISyntaxException {
        Page mybatisPage = PageUtil.getPage(page, size);
        Page result = companyService.pageConditionKeyWordAndEnabled(keyword, enabled, mybatisPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(result);
        return new ResponseEntity<>(result.getRecords(),httpHeaders,HttpStatus.OK);
    }



}

