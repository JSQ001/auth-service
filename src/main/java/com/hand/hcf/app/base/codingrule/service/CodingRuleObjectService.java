package com.hand.hcf.app.base.codingrule.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.codingrule.domain.CodingRule;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleDetail;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleObject;
import com.hand.hcf.app.base.codingrule.domain.enums.CodingRuleTypeEnum;
import com.hand.hcf.app.base.codingrule.persistence.CodingRuleObjectMapper;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.LoginInformationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
//@CacheConfig(cacheNames = {CacheConstants.CODING_RULE_OBJECT})
public class CodingRuleObjectService extends ServiceImpl<CodingRuleObjectMapper, CodingRuleObject> {


    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private CodingRuleService codingRuleService;

    @Autowired
    private CodingRuleDetailService codingRuleDetailService;
    @Autowired
    private CodingRuleCacheObjectService codingRuleCacheObjectService;

    /**
     * 新增
     *
     * @param codingRuleObject
     * @return
     */
    public CodingRuleObject insertCodingRuleObject(CodingRuleObject codingRuleObject) {
        if (codingRuleObject.getId() != null) {
            //创建数据不允许有ID
            throw new BizException(RespCode.ID_NOT_ALLOWED_21001);
        }
        if (codingRuleObject.getEnabled() != null) {
            codingRuleObject.setEnabled(codingRuleObject.getEnabled());
        }
        if (codingRuleObject.getTenantId() == null) {
            codingRuleObject.setTenantId(LoginInformationUtil.getCurrentTenantId()); //当前租户Id
        }
        //去除多余空格,如果字段为null就写死成""
        codingRuleObject.setCompanyCode((Optional.ofNullable(codingRuleObject.getCompanyCode()).orElse("")).trim());
        codingRuleObject.setDocumentTypeCode((Optional.ofNullable(codingRuleObject.getDocumentTypeCode()).orElse("")).trim());
        CodingRuleObject codingRuleObject_old = this.selectOne(new EntityWrapper<CodingRuleObject>()
            .where("deleted = false")
            .eq("tenant_id", codingRuleObject.getTenantId())
            .eq("document_type_code", codingRuleObject.getDocumentTypeCode())
            .eq("company_code", codingRuleObject.getCompanyCode())
        );
        if (codingRuleObject_old != null) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_OBJECT_CODE_NOT_UNIQUE);
        }
        this.insert(codingRuleObject);

        if (StringUtils.isNotBlank(codingRuleObject.getCompanyCode())) {
            codingRuleCacheObjectService.evictByCompanyCodeAndDocumentType(codingRuleObject.getCompanyCode(), codingRuleObject.getDocumentTypeCode(), codingRuleObject.getTenantId(), codingRuleObject.getEnabled());
        } else {
            codingRuleCacheObjectService.evictByTenantIdAndDocumentType(codingRuleObject.getDocumentTypeCode(), codingRuleObject.getTenantId(), codingRuleObject.getEnabled());
        }
        return codingRuleObject;
    }

    /**
     * 更新
     *
     * @param codingRuleObject
     * @return
     */
//    @CacheEvict(key = "#codingRuleObject.documentTypeCode+#codingRuleObject.tenantId.toString()+#codingRuleObject.companyCode")
    public CodingRuleObject updateCodingRuleObject(CodingRuleObject codingRuleObject) {
        if (codingRuleObject.getId() == null) {
            //更新数据ID必填
            throw new BizException(RespCode.ID_REQUIRED_21002);
        }
        if (codingRuleObject.getEnabled() != null) {
            codingRuleObject.setEnabled(codingRuleObject.getEnabled());
        } else {
            codingRuleObject.setEnabled(false);
        }
        this.updateById(codingRuleObject);

        if (StringUtils.isNotBlank(codingRuleObject.getCompanyCode())) {
            codingRuleCacheObjectService.evictByCompanyCodeAndDocumentType(codingRuleObject.getCompanyCode(), codingRuleObject.getDocumentTypeCode(), codingRuleObject.getTenantId(), codingRuleObject.getEnabled());
        } else {
            codingRuleCacheObjectService.evictByTenantIdAndDocumentType(codingRuleObject.getDocumentTypeCode(), codingRuleObject.getTenantId(), codingRuleObject.getEnabled());
        }

        return codingRuleObject;
    }

    /**
     * 删除一个编码规则定义
     *
     * @param codingRuleObject
     */
//    @CacheEvict(key = "#codingRuleObject.documentTypeCode+#codingRuleObject.tenantId.toString()+#codingRuleObject.companyCode")
    public void deleteCodingRuleObject(CodingRuleObject codingRuleObject) {
        //删除编码规则定义
        codingRuleObject.setDocumentTypeCode(codingRuleObject.getDocumentTypeCode());
        codingRuleObject.setDeleted(true);
        this.updateById(codingRuleObject);
    }

    /**
     * 根据特定条件获取当前租户下的编码规则定义id
     *
     * @param documentTypeCode 单据类型代码
     * @param companyCode      公司代码
     * @return
     */
    //@Cacheable(key = "#documentTypeCode+#tenantId+#companyCode", unless="#result == null")
    public Long getCodingRuleObjectIdByCond(String documentTypeCode, String companyCode, Long tenantId) {
        List<CodingRuleObject> codingRuleObjects =
            this.selectList(new EntityWrapper<CodingRuleObject>()
                .where("deleted = false")
                .eq("enabled", true)
                .eq("document_type_code", documentTypeCode)
                .eq("tenant_id", tenantId)
                .eq(StringUtils.isNotEmpty(companyCode),"company_code", companyCode)
                .isNull(StringUtils.isEmpty(companyCode),"company_code")
            );
        if (codingRuleObjects.size() != 1) {
            return null;
        }
        return codingRuleObjects.get(0).getId();
    }


    /**
     * 通用查询-分页
     *
     * @param documentTypeCode 单据类型代码
     * @param enabled        是否启用
     * @param page             页码
     * @return
     */
    public Page<CodingRuleObject> getCodingRuleObjectByCond(
        String documentTypeCode,
        String companyCode,
        Boolean enabled,
        Page page) {
        List<CodingRuleObject> codingRuleObjects = baseMapper.selectPage(page, new EntityWrapper<CodingRuleObject>()
            .where("deleted = false")
            .eq("tenant_id", LoginInformationUtil.getCurrentTenantId())
            .eq(companyCode != null, "company_code", companyCode)
            .eq(documentTypeCode != null, "document_type_code", documentTypeCode)
            .eq(enabled != null, "enabled", enabled)
            .orderBy("document_type_code")
        );
        codingRuleObjects.stream().forEach((CodingRuleObject u) -> {
            setCodingRuleObject(u);
        });
        page.setRecords(codingRuleObjects);
        return page;
    }

    public void setCodingRuleObject(CodingRuleObject u) {
        SysCodeValue sysCodeValue = sysCodeService.getValueBySysCodeAndValue(CodingRuleTypeEnum.DOCUMENT_TYPE.getId().toString(), u.getDocumentTypeCode());
        u.setDocumentTypeName(sysCodeValue == null ? "" : sysCodeValue.getName());
        //todo 现在还不能查询到公司名称，公司名称暂时赋值公司code
        u.setCompanyName(u.getCompanyCode());
    }

    @Transactional
    public boolean initDefaultCodingRule(Long tenantId) {
        /*
            差旅报销：TR
            JD申请：JA
            费用申请：EA
            借款申请：LA
            差旅申请：TA
            报销单: ER
        */
        String[][] documentTypeCodes = {
            {"TR_REQUISITION", "TR"}, {"JD_REQUISITION", "JA"}, {"EXP_REQUISITION", "EA"},
            {"PAYMENT_REQUISITION", "LA"}, {"TA_REQUISITION", "TA"}, {"ER_REQUISITION", "ER"}
        };
        for (int i = 0; i < documentTypeCodes.length; i++) {
            CodingRuleObject codingRuleObject = CodingRuleObject.builder()
                .companyCode("")
                .documentTypeCode(documentTypeCodes[i][0])
                .tenantId(tenantId)
                .build();
            insertCodingRuleObject(codingRuleObject);
            CodingRule codingRule = CodingRule.builder()
                .codingRuleCode(codingRuleObject.getDocumentTypeCode())
                .codingRuleObjectId(codingRuleObject.getId())
                .resetFrequence("NEVER")  //这里写死了，永不重置频率值列表对应的就是NEVER
                .codingRuleName("默认编码规则")
                .tenantId(tenantId)
                .build();
            Map mapName = new HashMap();
            List<Map> mapList = new ArrayList<>();
            Map map_zh = new HashMap();
            map_zh.put("language", LanguageEnum.ZH_CN.getKey());
            map_zh.put("value", "默认编码规则");
            Map map_en = new HashMap();
            map_en.put("language", LanguageEnum.EN_US.getKey());
            map_en.put("value", "Default encoding rules");
            mapList.add(map_zh);
            mapList.add(map_en);
            mapName.put("codingRuleName", mapList);
            codingRule.setI18n(mapName);
            codingRuleService.insetCodingRule(codingRule);
            //【固定字符】+【序列号】（8位），开始位数为00100000，步长为1
            CodingRuleDetail codingRuleDetail_documentType = CodingRuleDetail.builder()
                .segmentType("10") //这里写死,段这个值列表中固定字符对应的是10
                .segmentValue(documentTypeCodes[i][1])
                .sequence(10)
                .codingRuleId(codingRule.getId())
                .tenantId(tenantId)
                .build();
            CodingRuleDetail codingRuleDetail_sequence = CodingRuleDetail.builder()
                .segmentType("50") //这里写死,段这个值列表中序列号对应的是50
                .sequence(20)
                .incremental(1)
                .length(8)
                .codingRuleId(codingRule.getId())
                .startValue(100000)
                .tenantId(tenantId)
                .build();
            codingRuleDetailService.insertCodingRuleDetail(codingRuleDetail_documentType);
            codingRuleDetailService.insertCodingRuleDetail(codingRuleDetail_sequence);
        }
        return true;
    }

    /**
     * 某个租户是否启用了某种单据类型的编码规则定义
     *
     * @param companyOid
     * @param documentTypeCode
     * @return
     */
    public boolean isEnabledCodingRuleObject(UUID companyOid, String documentTypeCode) {
        Long tenantId =LoginInformationUtil.getCurrentTenantId();
        boolean companyExists = codingRuleCacheObjectService.findByCompanyCodeAndDocumentType(null, documentTypeCode, tenantId);
        if (companyExists) {
            return true;
        }
        boolean tenantExists = codingRuleCacheObjectService.findByTenantIdAndDocumentType(documentTypeCode, tenantId);
        if (tenantExists) {
            return true;
        }
        return false;
    }

    /**
     * 校验供应商是否支持自动编码。
     * 租户级供应商仅校验租户级编码规则；
     * 公司级供应商需优先检验公司级编码规则，如没有配置，再校验租户级编码规则；
     * @param companyOid
     * @param documentTypeCode
     * @param roleType
     * @return
     */
    public JSONObject validateVendorAutoCode(UUID companyOid, String documentTypeCode, String roleType) {
        boolean result = false;
        Long tenantId = LoginInformationUtil.getCurrentTenantId();
        if (!Constants.ROLE_TENANT.equals(roleType)) {
            result = codingRuleCacheObjectService.findByCompanyCodeAndDocumentType(null, documentTypeCode, tenantId);
        }
        if (!result) {
            result = codingRuleCacheObjectService.findByTenantIdAndDocumentType(documentTypeCode, tenantId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        return jsonObject;
    }
}
