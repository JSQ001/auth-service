package com.hand.hcf.app.mdata.legalEntity.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.legalEntity.domain.LegalEntity;
import com.hand.hcf.app.mdata.legalEntity.dto.LegalEntityDTO;
import com.hand.hcf.app.mdata.legalEntity.service.LegalEntityService;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * comment
 * Created by fanfuqiang 2018/11/19
 */
@RestController
@RequestMapping(value = "/api")
public class LegalEntityResource {

    @Autowired
    private LegalEntityService legalEntityService;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private CompanyService companyService;

    private final Logger log = LoggerFactory.getLogger(LegalEntityResource.class);

    /**
     * @apiDefine LegalEntityParam
     * @apiParam {Long} id   法人实体id
     * @apiParam {Long} tenantId   租户id
     * @apiParam {Long} setOfBooksId   账套id
     * @apiParam {UUID} [companyReceiptedOid]   法人实体Oid
     * @apiParam {String} entityName   法人实体名称
     * @apiParam {Long}  [parentLegalEntityId]   上级法人实体id
     * @apiParam {String} address    地址
     * @apiParam {String} taxpayerNumber    纳税人识别编号
     * @apiParam {String} accountBank    开户行
     * @apiParam {String} telePhone    电话
     * @apiParam {String} accountNumber    账号
     *
     */

    /**
     * @api {get} /api/all/legalentitys 查询法人实体列表
     * @apiGroup LegalEntity
     * @apiSuccess {Object[]} legalEntityDTOs       法人实体视图对象集合.
     * @apiSuccess {Long}   legalEntityDTOs.id           法人实体id.
     * @apiSuccess {Long}   legalEntityDTOs.tenantId     租户id.
     * @apiSuccess {Long}   legalEntityDTOs.setOfBooksId 账套id.
     * @apiSuccess {String} legalEntityDTOs.setOfBooksName 账套名称.
     * @apiSuccess {UUID}   legalEntityDTOs.companyReceiptedOid 法人实体oid.
     * @apiSuccess {String} legalEntityDTOs.entityName 法人实体名称.
     * @apiSuccess {String} legalEntityDTOs.address 地址.
     * @apiSuccess {String} legalEntityDTOs.taxpayerNumber 纳税人识别号.
     * @apiSuccess {String} legalEntityDTOs.accountBank 开户支行.
     * @apiSuccess {String} legalEntityDTOs.telePhone 电话号码.
     * @apiSuccess {String} legalEntityDTOs.accountNumber 账号.
     * @apiSuccess {String} legalEntityDTOs.createdBy 创建人.
     * @apiSuccess {String} legalEntityDTOs.lastUpdatedBy 修改人.
     * @apiSuccess {Long} legalEntityDTOs.attachmentId 附件id.
     * @apiSuccess {String} legalEntityDTOs.fileURL 法人实体二维码.
     * @apiSuccess {String} legalEntityDTOs.depth 路径.
     * @apiSuccess {boolean}   legalEntityDTOs.enabled 是否启用.
     * @apiSuccess {boolean}   legalEntityDTOs.delete 是否删除.
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id": "938404627033485313",
     * "tenantId": 933327677580263425,
     * "setOfBooksId": "933328216846123009",
     * "setOfBooksName": "默认账套",
     * "companyReceiptedOid": "6fcd0471-1788-4b4d-afb6-d2e763b9e4f3",
     * "entityName": "测试测试11",
     * "address": "上海",
     * "taxpayerNumber": "3243243242323",
     * "accountBank": "上海",
     * "telePhone": "15000004322",
     * "accountNumber": "423423423423",
     * "createdBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
     * "lastUpdatedBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
     * "attachmentId": "251861",
     * "fileURL": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/invoices/f03621df-6a48-4a76-8ddd-d798dd0407f5-IMG_3012.JPG?Expires=1514453896&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=RyzNDdvIl0a%2Fi%2F7FFqW1B0r8%2FX8%3D",
     * "thumbnailUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/invoices/f03621df-6a48-4a76-8ddd-d798dd0407f5-IMG_3012.JPG?Expires=1514453896&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=oWhN2cUAe74APDGTGthv9%2B3ZORY%3D&x-oss-process=image%2Fresize%2Ch_200",
     * "iconUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/invoices/f03621df-6a48-4a76-8ddd-d798dd0407f5-IMG_3012.JPG?Expires=1514453896&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=LtI919TYl0oJnPUmIGFCNkJjsRo%3D&x-oss-process=image%2Fresize%2Ch_80",
     * "depth": 1,
     * "enabled": true,
     * "deleted": false
     * }
     * ]
     */
    @RequestMapping(value = "/all/legalentitys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LegalEntityDTO>> findAllLegalEntity() {
        return ResponseEntity.ok(legalEntityService.findByCompanyId(OrgInformationUtil.getCurrentTenantId()));
    }

    @RequestMapping(value = "/all/legalentitys/withoutCompany", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LegalEntityDTO>> findAllLegalEntityWithoutCompany(@RequestParam(value = "companyOid", required = false) UUID companyOid) {
        return ResponseEntity.ok(legalEntityService.findLegalEntityWithoutCompany(OrgInformationUtil.getCurrentTenantId(), companyOid));
    }

    /**
     * @api {get} /api/legalentitys/by/setofbooks/or/user 根据账套id或用户oid查询法人实体信息
     * @apiGroup LegalEntity
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {UUID} userOid 用户Oid
     * @apiParam {Integer} page 页码
     * @apiParam {Integer} size 条数
     * @apiSuccess {Object[]} legalEntityDTOs       法人实体视图对象集合.
     * @apiSuccess {Long}   legalEntityDTOs.id           法人实体id.
     * @apiSuccess {Long}   legalEntityDTOs.tenantId     租户id.
     * @apiSuccess {Long}   legalEntityDTOs.setOfBooksId 账套id.
     * @apiSuccess {String} legalEntityDTOs.setOfBooksName 账套名称.
     * @apiSuccess {UUID}   legalEntityDTOs.companyReceiptedOid 法人实体oid.
     * @apiSuccess {String} legalEntityDTOs.entityName 法人实体名称.
     * @apiSuccess {String} legalEntityDTOs.address 地址.
     * @apiSuccess {String} legalEntityDTOs.taxpayerNumber 纳税人识别号.
     * @apiSuccess {String} legalEntityDTOs.accountBank 开户支行.
     * @apiSuccess {String} legalEntityDTOs.telePhone 电话号码.
     * @apiSuccess {String} legalEntityDTOs.accountNumber 账号.
     * @apiSuccess {String} legalEntityDTOs.createdBy 创建人.
     * @apiSuccess {String} legalEntityDTOs.lastUpdatedBy 修改人.
     * @apiSuccess {Long} legalEntityDTOs.attachmentId 附件id.
     * @apiSuccess {String} legalEntityDTOs.fileURL 法人实体二维码.
     * @apiSuccess {String} legalEntityDTOs.depth 路径.
     * @apiSuccess {boolean}   legalEntityDTOs.enabled 是否启用.
     * @apiSuccess {boolean}   legalEntityDTOs.delete 是否删除.
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id": "938404627033485313",
     * "tenantId": 933327677580263425,
     * "setOfBooksId": "933328216846123009",
     * "setOfBooksName": "默认账套",
     * "companyReceiptedOid": "6fcd0471-1788-4b4d-afb6-d2e763b9e4f3",
     * "entityName": "测试测试11",
     * "address": "上海",
     * "taxpayerNumber": "3243243242323",
     * "accountBank": "上海",
     * "telePhone": "15000004322",
     * "accountNumber": "423423423423",
     * "createdBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
     * "lastUpdatedBy": "363d8ebf-28f8-48d9-aae7-c0e37a46e682",
     * "attachmentId": "251861",
     * "fileURL": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/invoices/f03621df-6a48-4a76-8ddd-d798dd0407f5-IMG_3012.JPG?Expires=1514453896&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=RyzNDdvIl0a%2Fi%2F7FFqW1B0r8%2FX8%3D",
     * "thumbnailUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/invoices/f03621df-6a48-4a76-8ddd-d798dd0407f5-IMG_3012.JPG?Expires=1514453896&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=oWhN2cUAe74APDGTGthv9%2B3ZORY%3D&x-oss-process=image%2Fresize%2Ch_200",
     * "iconUrl": "https://huilianyi-uat.oss-cn-shanghai.aliyuncs.com/2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/invoices/f03621df-6a48-4a76-8ddd-d798dd0407f5-IMG_3012.JPG?Expires=1514453896&OSSAccessKeyId=zmKqYB24JQrTqfiH&Signature=LtI919TYl0oJnPUmIGFCNkJjsRo%3D&x-oss-process=image%2Fresize%2Ch_80",
     * "depth": 1,
     * "enabled": true,
     * "deleted": false
     * }
     * ]
     */
    @RequestMapping(value = "/legalentitys/by/setofbooks/or/user", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<LegalEntityDTO>> findLegalEntityBySetOfBooksId(@RequestParam(name = "setOfBooksId", required = false) Long setOfBooksId, @RequestParam(name = "userOid", required = false) UUID userOid, Pageable pageable) throws URISyntaxException {
        // 优先获取账套下的法人实体
        if (setOfBooksId == null && userOid != null) {
            Company company = companyService.findCompanyByUserOid(userOid);
            if (company == null) {
                throw new BizException("6045003");
            }
            setOfBooksId = company.getSetOfBooksId();
        } else if (setOfBooksId == null && userOid == null) {
            setOfBooksId = companyService.findCompanyByUserOid(OrgInformationUtil.getCurrentUserOid()).getSetOfBooksId();
        }
        if (setOfBooksId == null) {
            throw new BizException("6018014");
        }
        Page<LegalEntityDTO> page = legalEntityService.findLegalEntityBySetOfBooksId(setOfBooksId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/legalentitys/by/setofbooks/or/user");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * @api {get} /api/listDTOByQO/parent/legalentitys 查询上级法人实体列表
     * @apiGroup LegalEntity
     * @apiParam {Long} legalEntityId 法人实体id
     * @apiSuccess {Object[]} legalEntityDTOs       法人实体视图对象集合.
     * @apiSuccess {String}   legalEntityDTOs.companyReceiptedOid   法人实体oid.
     * @apiSuccess {String}   legalEntityDTOs.entityName 法人实体名称.
     * @apiSuccess {String}   legalEntityDTOs.taxpayerNumber 纳税人识别号.
     * @apiSuccess {String}   legalEntityDTOs.address 地址.
     * @apiSuccess {String}   legalEntityDTOs.telephone 电话.
     * @apiSuccess {String}   legalEntityDTOs.accountNumber 银行账号.
     * @apiSuccess {String}   legalEntityDTOs.accountBank 开户行.
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id":924897837610795000,
     * "companyReceiptedOid":"88885c42-0ee4-481f-86c4-35be38bb8eb5",
     * "entityName":"Dev租户测试账号02",
     * "enable":false,
     * "taxpayerNumber":"ABCNBNB1987654321",
     * "address":"上海市真光路",
     * "accountBank":"中国牛叉银行",
     * "telePhone":"010-22123456",
     * "accountNumber":"3879080987673342222"
     * }
     * ]
     */
    @RequestMapping(value = "/find/parent/legalentitys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LegalEntityDTO>> findParentLegalEntity(@RequestParam(name = "legalEntityId", required = false) Long legalEntityId, @RequestParam(name = "setOfBookID", required = false) Long setOfBookID) {
        return ResponseEntity.ok(legalEntityService.findParentLegalEntityByCompanyId(OrgInformationUtil.getCurrentTenantId(), legalEntityId, setOfBookID));
    }

    /**
     * @api {get} /api/legalentitys 根据输入文本查询法人实体信息
     * @apiGroup LegalEntity
     * @apiParam {String} keyword 输入文本
     * @apiParam {Integer} page 页码
     * @apiParam {Integer} size 条数
     * @apiSuccess {Object[]} legalEntityDTOs       法人实体视图对象集合.
     * @apiSuccess {UUID}   legalEntityDTOs.companyReceiptedOid   法人实体oid.
     * @apiSuccess {String}   legalEntityDTOs.entityName 法人实体名称.
     * @apiSuccess {String}   legalEntityDTOs.taxpayerNumber 纳税人识别号.
     * @apiSuccess {String}   legalEntityDTOs.address 地址.
     * @apiSuccess {String}   legalEntityDTOs.telephone 电话.
     * @apiSuccess {String}   legalEntityDTOs.accountNumber 银行账号.
     * @apiSuccess {String}   legalEntityDTOs.accountBank 开户行.
     * @apiSuccessExample {json} Success-Result
     * [
     * {
     * "id":924897837610795000,
     * "companyReceiptedOid":"88885c42-0ee4-481f-86c4-35be38bb8eb5",
     * "entityName":"Dev租户测试账号02",
     * "enable":false,
     * "taxpayerNumber":"ABCNBNB1987654321",
     * "address":"上海市真光路",
     * "accountBank":"中国牛叉银行",
     * "telePhone":"010-22123456",
     * "accountNumber":"3879080987673342222"
     * }
     * ]
     */
    @RequestMapping(value = "/legalentitys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LegalEntityDTO>> findLegalEntityByKeyWord(@RequestParam(name = "keyword", required = false) String keyword, Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        page = legalEntityService.findLegalEntityByKeyWord(page, OrgInformationUtil.getCurrentUserOid(), keyword);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/legalentitys");


        return new ResponseEntity<>(page.getRecords(), httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {delete} /api/legalentitys/legalEntityID 根据法人实体ID删除法人实体信息
     * @apiGroup LegalEntity
     * @apiParam {Long} legalEntityID 法人实体id
     */
    @RequestMapping(value = "/legalentitys/{legalEntityID}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteLegalEntity(@PathVariable Long legalEntityID) {
        legalEntityService.deleteLegalEntity(legalEntityID);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {get} /api/legalentitys/legalEntityID 根据法人实体ID查询法人实体信息
     * @apiGroup LegalEntity
     * @apiParam {Long} legalEntityID 法人实体id
     * @apiSuccess {Object} legalEntityDTO       法人实体视图对象
     * @apiSuccess {UUID}   legalEntityDTO.companyReceiptedOid   法人实体oid.
     * @apiSuccess {String}   legalEntityDTO.entityName 法人实体名称.
     * @apiSuccess {String}   legalEntityDTO.taxpayerNumber 纳税人识别号.
     * @apiSuccess {String}   legalEntityDTO.address 地址.
     * @apiSuccess {String}   legalEntityDTO.telephone 电话.
     * @apiSuccess {String}   legalEntityDTO.accountNumber 银行账号.
     * @apiSuccess {String}   legalEntityDTO.accountBank 开户行.
     * @apiSuccessExample {json} Success-Result
     * {
     * "id":924897837610795000,
     * "companyReceiptedOid":"88885c42-0ee4-481f-86c4-35be38bb8eb5",
     * "entityName":"Dev租户测试账号02",
     * "enable":false,
     * "taxpayerNumber":"ABCNBNB1987654321",
     * "address":"上海市真光路",
     * "accountBank":"中国牛叉银行",
     * "telePhone":"010-22123456",
     * "accountNumber":"3879080987673342222"
     * }
     */
    @RequestMapping(value = "/legalentitys/{legalEntityID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LegalEntityDTO> getLegalEntity(@PathVariable Long legalEntityID) {
        return ResponseEntity.ok(legalEntityService.getLegalEntity(legalEntityID));
    }

    /**
     * @api {post} /api/legalentitys 创建法人实体列表
     * @apiGroup LegalEntity
     * @apiUse LegalEntityParam
     * @apiSuccess {Object} legalEntityDTO       法人实体视图对象集合.
     * @apiSuccess {UUID}   legalEntityDTO.companyReceiptedOid   法人实体oid.
     * @apiSuccess {String}   legalEntityDTO.entityName 法人实体名称.
     * @apiSuccess {String}   legalEntityDTO.taxpayerNumber 纳税人识别号.
     * @apiSuccess {String}   legalEntityDTO.address 地址.
     * @apiSuccess {String}   legalEntityDTO.telephone 电话.
     * @apiSuccess {String}   legalEntityDTO.accountNumber 银行账号.
     * @apiSuccess {String}   legalEntityDTO.accountBank 开户行.
     * @apiSuccessExample {json} Success-Result
     * {
     * "id":924897837610795000,
     * "companyReceiptedOid":"88885c42-0ee4-481f-86c4-35be38bb8eb5",
     * "entityName":"Dev租户测试账号02",
     * "enable":false,
     * "taxpayerNumber":"ABCNBNB1987654321",
     * "address":"上海市真光路",
     * "accountBank":"中国牛叉银行",
     * "telePhone":"010-22123456",
     * "accountNumber":"3879080987673342222"
     * }
     */
    @RequestMapping(value = "/legalentitys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LegalEntityDTO> addLegalEntity(@RequestBody LegalEntityDTO legalEntityDTO) throws URISyntaxException {
        LegalEntityDTO respLegalEntityDTO = null;
        try {
            respLegalEntityDTO = legalEntityService.addOrUpdateLegalEntity(legalEntityDTO, OrgInformationUtil.getCurrentTenantId(), true);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("create legal entity appear exception!");
        }
        return ResponseEntity.ok(respLegalEntityDTO);
    }

    /**
     * @api {put} /api/legalentitys 修改法人实体接口
     * @apiGroup LegalEntity
     * @apiUse LegalEntityParam
     * @apiSuccess {Object} legalEntityDTO       法人实体视图对象集合.
     * @apiSuccess {UUID}   legalEntityDTO.companyReceiptedOid   法人实体oid.
     * @apiSuccess {String}   legalEntityDTO.entityName 法人实体名称.
     * @apiSuccess {String}   legalEntityDTO.taxpayerNumber 纳税人识别号.
     * @apiSuccess {String}   legalEntityDTO.address 地址.
     * @apiSuccess {String}   legalEntityDTO.telephone 电话.
     * @apiSuccess {String}   legalEntityDTO.accountNumber 银行账号.
     * @apiSuccess {String}   legalEntityDTO.accountBank 开户行.
     * @apiSuccessExample {json} Success-Result
     * {
     * "id":924897837610795000,
     * "companyReceiptedOid":"88885c42-0ee4-481f-86c4-35be38bb8eb5",
     * "entityName":"Dev租户测试账号02",
     * "enable":false,
     * "taxpayerNumber":"ABCNBNB1987654321",
     * "address":"上海市真光路",
     * "accountBank":"中国牛叉银行",
     * "telePhone":"010-22123456",
     * "accountNumber":"3879080987673342222"
     * }
     */
    @RequestMapping(value = "/legalentitys", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LegalEntityDTO> updateLegalEntity(HttpServletResponse response, @RequestBody LegalEntityDTO legalEntityDTO) throws URISyntaxException {
        LegalEntityDTO respLegalEntityDTO = null;
        try {
            respLegalEntityDTO = legalEntityService.addOrUpdateLegalEntity(legalEntityDTO, OrgInformationUtil.getCurrentTenantId(), true);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("update legal entity appear exception!");
        }
        return ResponseEntity.ok(respLegalEntityDTO);
    }

    @RequestMapping(value = "/legalentitys/i18n/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Map<String, LegalEntity>> getLegalEntityI18n(@PathVariable Long id) {
        return ResponseEntity.ok(baseI18nService.getI18nInfo(id, LegalEntity.class));
    }

    /**
     * 查询公司法人实体
     *
     * @param isAll
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/v2/my/company/receipted/invoices",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LegalEntityDTO>> getCompanyReceiptedInvoicesV2(@RequestParam(required = false) Boolean isAll, @RequestParam(name = "keyword", required = false) String keyword, Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<LegalEntityDTO> legalEntityDTOS = new ArrayList<>();
        // 验证是否配置为只能查看所属公司的开票信息
        /*FunctionProfile functionProfile = functionProfileService.getFunctionProfileByCompanyOid(OrgInformationUtil.getCurrentCompanyOid());
        Boolean only = false;
        // 判断是否查询全部
        if(isAll == null || !isAll){
            if (functionProfile != null) {
                try {
                    only = functionProfile.getProfileDetail().getBoolean("billing.choose.affiliatedcompany.only");
                } catch (JSONException e) {
                    only = false;
                }
            }
        }
        if (only) {
            User user = userService.getByUserOid(OrgInformationUtil.getCurrentUserOid());
            CompanyCO company = companyService.getById(user.getCompanyId());
            LegalEntityDTO dto = legalEntityService.getLegalEntity(company.getLegalEntityId());
            legalEntityDTOS.add(dto);
           *//* if (pageable.hasPrevious()) {
                page = new PageImpl<>(new ArrayList(), pageable, 1);
            } else {
                page = new PageImpl<>(Collections.singletonList(dto), pageable, 1);
            }*//*
        } else {
            legalEntityDTOS = legalEntityService.findByTenantAndKeyword(OrgInformationUtil.getCurrentTenantId(), keyword, page);
        }*/
        legalEntityDTOS = legalEntityService.findByTenantAndKeyword(OrgInformationUtil.getCurrentTenantId(), keyword, page);
        page.setRecords(legalEntityDTOS);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/v2/my/company/receipted/invoices");
        return new ResponseEntity<>(page.getRecords(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/company/receipted/invoice",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LegalEntityDTO> createCompanyReceiptedInvoice(@Valid @RequestBody LegalEntityDTO legalEntityDTO) throws URISyntaxException {
        log.debug("REST request to update CompanyReceiptedInvoice : {}", legalEntityDTO);

        legalEntityDTO.setCreatedDate(ZonedDateTime.now());
        LegalEntityDTO result = legalEntityService.addOrUpdateLegalEntity(legalEntityDTO, OrgInformationUtil.getCurrentTenantId(), true);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/company/receipted/invoice",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LegalEntityDTO> updateCompanyReceiptedInvoice(@Valid @RequestBody LegalEntityDTO legalEntityDTO) throws URISyntaxException {
        log.debug("REST request to update CompanyReceiptedInvoice : {}", legalEntityDTO);
        legalEntityDTO.setCreatedDate(ZonedDateTime.now());
        LegalEntityDTO result = legalEntityService.addOrUpdateLegalEntity(legalEntityDTO, OrgInformationUtil.getCurrentTenantId(), true);
        return ResponseEntity.ok(result);
    }

    //开票信息详情，包含所有关联用户OID
    @RequestMapping(value = "/company/receipted/invoices/users/{companyReceiptedOID}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LegalEntityDTO> getCompanyReceiptedInvoiceWithUserOIDsByOID(@PathVariable UUID companyReceiptedOID) {
        LegalEntityDTO legalEntityDTO = legalEntityService.findLegalEntityByOid(companyReceiptedOID);
        return ResponseEntity.ok(legalEntityDTO);
    }


}

