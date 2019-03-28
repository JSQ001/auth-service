package com.hand.hcf.app.mdata.period.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.period.domain.PeriodRules;
import com.hand.hcf.app.mdata.period.domain.PeriodSet;
import com.hand.hcf.app.mdata.period.dto.PeriodSetDTO;
import com.hand.hcf.app.mdata.period.persistence.PeriodSetMapper;
import com.hand.hcf.app.mdata.utils.DataFilteringUtil;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class PeriodSetService extends ServiceImpl<PeriodSetMapper, PeriodSet> {
    private final Logger log = LoggerFactory.getLogger(PeriodSetService.class);

    @Autowired
    private PeriodSetMapper periodSetMapper;
    @Autowired
    private PeriodRuleService periodRuleService;
    @Autowired
    private BaseI18nService baseI18nService;

    private static String regex = "[\u4e00-\u9fa5]";

    /**
     * 通过租户ID初始化新增一个会计期
     *
     * @param tenantId：租户ID
     */
    @Transactional()
    public PeriodSet initPeriodSet(Long tenantId){
        PeriodSet periodSet = new PeriodSet();
        periodSet.setTenantId(tenantId);
        periodSet.setPeriodAdditionalFlag("S");
        periodSet.setPeriodSetCode("DEFAULT_CAL");
        periodSet.setPeriodSetName("默认会计期");
        periodSet.setTotalPeriodNum(12);

        Map<String, List<Map<String, String>>> i18n=new HashMap<String, List<Map<String, String>>>();
        List<Map<String, String>> detail=new ArrayList<Map<String, String>>();
        Map<String, String> en=new HashMap<String, String>();
        en.put("language","en_US");
        en.put("value","Default period set");
        detail.add(en);
        Map<String, String> cn=new HashMap<String, String>();
        cn.put("language","zh_cn");
        cn.put("value","默认会计期");
        detail.add(cn);
        i18n.put("accountSetDesc",detail);
        periodSet.setI18n(i18n);
        periodSetMapper.insert(periodSet);
        return periodSet;
    }
    /**
     * 通过租户ID初始化新增一个会计期
     *
     * @param periodSetId: 租户ID
     * @param index: 日期参数
     */
    @Transactional()
    public void initPeriods(Long periodSetId, Integer index,Long tenantId){
        PeriodRules periodRules = new PeriodRules();
        periodRules.setPeriodSetId(periodSetId);
        periodRules.setPeriodNum(index);
        //  期间名称附加，补位 个位数前面加0 ，两位数啥也不加
        periodRules.setPeriodAdditionalName(String.format("%02d", index));
        periodRules.setMonthFrom(index);
        periodRules.setMonthTo(index);
        periodRules.setDateFrom(1);
        //日期到设置
        switch (index) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                periodRules.setDateTo(31);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                periodRules.setDateTo(30);
                break;
            case 2:
                periodRules.setDateTo(28);
                break;
        }
        //季度设置
        switch (index) {
            case 1:
            case 2:
            case 3:
                periodRules.setQuarterNum(1);
                break;
            case 4:
            case 5:
            case 6:
                periodRules.setQuarterNum(2);
                break;
            case 7:
            case 8:
            case 9:
                periodRules.setQuarterNum(3);
                break;
            case 10:
            case 11:
            case 12:
                periodRules.setQuarterNum(4);
                break;
        }
        periodRules.setTenantId(tenantId);
        periodRuleService.insert(periodRules);
    }

    /**
     * 增加一个会计期间
     *
     * @param periodSet
     * @return
     */
    public PeriodSet addPeriodSet(PeriodSet periodSet) {
        //期间总数应该大于等于12小于等于20
       if(periodSet.getTotalPeriodNum()<12 || periodSet.getTotalPeriodNum()>20)
       {
           throw new BizException(RespCode.TOTAL_NUMBER_SHOULD_MORE_THEN_12_AND_LESS_THEN_20);
       }
        //会计期附加校验
        if(StringUtils.isEmpty(periodSet.getPeriodAdditionalFlag()))
        {
            throw new BizException(RespCode.PERIOD_NAME_ATTACHED_CANNOT_BE_EMPTY);
        }
         //查询当前租户下的所有有效的会计期间
        if (OrgInformationUtil.getCurrentTenantId() != null) {
            List<PeriodSet> periodSetList = periodSetMapper.selectList(
                new EntityWrapper<PeriodSet>()
                    .where("deleted = false")
                    .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
            );
            if (periodSetList.stream().anyMatch(u -> u.getPeriodSetCode().equals(periodSet.getPeriodSetCode()))) {
                throw new BizException(RespCode.PERIOD_CODE_REPEAT);
            } else {
                periodSet.setTenantId(OrgInformationUtil.getCurrentTenantId());
                //  Code过滤
                periodSet.setPeriodSetCode(DataFilteringUtil.getDataFilterCode(periodSet.getPeriodSetCode()));
                //  Name过滤
                periodSet.setPeriodSetName(DataFilteringUtil.getDataFilterName(periodSet.getPeriodSetName()));
                periodSetMapper.insert(periodSet);
            }
        } else {
            throw new BizException(RespCode.TENANT_ID_CANNOT_BE_EMPTY);
        }
        return periodSet;
    }

    /**
     * 更新一个会计期间
     *
     * @param periodSet
     */
    public PeriodSet updatePeriodSet(PeriodSet periodSet) {
        //期间总数应该大于等于12小于等于20
        if(periodSet.getTotalPeriodNum()<12 || periodSet.getTotalPeriodNum()>20)
        {
            throw new BizException(RespCode.TOTAL_NUMBER_SHOULD_MORE_THEN_12_AND_LESS_THEN_20);
        }
        //会计期附加校验
        if(StringUtils.isEmpty(periodSet.getPeriodAdditionalFlag()))
        {
            throw new BizException(RespCode.PERIOD_NAME_ATTACHED_CANNOT_BE_EMPTY);
        }
         //查询当前租户下的所有有效的会计期间
        if (OrgInformationUtil.getCurrentTenantId() != null) {
            List<PeriodSet> periodSetList = periodSetMapper.selectList(
                new EntityWrapper<PeriodSet>()
                    .where("deleted = false")
                    .where("id!=" + periodSet.getId())
                    .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
            );
            if (periodSetList.stream().anyMatch(u -> u.getPeriodSetCode().equals(periodSet.getPeriodSetCode()))) {
                throw new BizException(RespCode.PERIOD_CODE_REPEAT);
            } else {
                periodSetMapper.updateById(periodSet);
            }
        } else {
            throw new BizException(RespCode.TENANT_ID_CANNOT_BE_EMPTY);
        }
        return periodSet;
    }

    /**
     * 逻辑删除会计期间
     *
     * @param id
     */
    public void deletePeriodSet(Long id) {
        PeriodSet periodSet = new PeriodSet();
        periodSet.setId(id);
        PeriodSet result = periodSetMapper.selectOne(periodSet);
        if (null != result && result.getDeleted() == false) {
            result.setDeleted(true);
            String randomNumeric = RandomStringUtils.randomNumeric(6);
            result.setPeriodSetCode(result.getPeriodSetCode() + "_DELETED_" + randomNumeric);
            periodSetMapper.updateById(result);
        }
    }

    /**
     * 通过id 查询会计期间
     *
     * @param id
     * @return
     */
    public PeriodSet getPeriodSet(Long id) {
        return baseI18nService.selectOneBaseTableInfoWithI18n(id, PeriodSet.class);
    }

    /**
     * 通过id 查询会计期间(多语言翻译后)
     *
     * @param id
     * @return
     */
    public PeriodSet getTransPeriodSet(Long id) {
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(id, PeriodSet.class);
    }

    /**
     * 根据periodSetCode和tenantId分页查询当前所有有效的会计期间
     *
     * @param page
     * @param periodSetCode
     * @return
     */
    public Page<PeriodSetDTO> findperiodsetByPeriodSetCodeAndTenantId(Page<PeriodSetDTO> page, String periodSetCode) {
        List<PeriodSet> periodSetList = periodSetMapper.selectPage(page,
            new EntityWrapper<PeriodSet>()
                .where("deleted = false")
                .like("period_set_code", periodSetCode)
                .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                .orderBy("period_set_code")
        );
        if(CollectionUtils.isNotEmpty(periodSetList)){
            List<PeriodSet> i18ns = baseI18nService.selectListTranslatedTableInfoWithI18n(periodSetList.stream().map(u->u.getId()).collect(toList()), PeriodSet.class);
            i18ns.stream().forEach(u -> u.getI18n());
            List<PeriodSetDTO> periodSetDTOListr = periodSetListToPeriodSetDTOList(i18ns);


            page.setRecords(periodSetDTOListr);
        }

        return page;
    }

    /**
     * 会计期间实体类转换会计期间实体视图对象
     * @param periodSet：会计期间实体对象
     * @return
     */
    public PeriodSetDTO periodSetToPeriodSetDTO(PeriodSet periodSet) {
        PeriodSetDTO periodSetDTO = new PeriodSetDTO();
        if (periodSet != null) {
            periodSetDTO.setId(periodSet.getId());
            periodSetDTO.setTenantId(periodSet.getTenantId());
            periodSetDTO.setPeriodAdditionalFlag(periodSet.getPeriodAdditionalFlag());
            periodSetDTO.setPeriodSetCode(periodSet.getPeriodSetCode());
            periodSetDTO.setTotalPeriodNum(periodSet.getTotalPeriodNum());
            periodSetDTO.setEnabled(periodSet.getEnabled());
            periodSetDTO.setDeleted(periodSet.getDeleted());
            periodSet.setLastUpdatedBy(periodSetDTO.getLastUpdatedBy());
            periodSet.setLastUpdatedDate(periodSetDTO.getLastUpdatedDate());
        }
        return periodSetDTO;
    }

    public List<PeriodSetDTO> periodSetListToPeriodSetDTOList(List<PeriodSet> periodSetlist) {
        List<PeriodSetDTO> periodSetDTOList = new ArrayList<PeriodSetDTO>();
        String language= OrgInformationUtil.getCurrentLanguage();
        if(periodSetlist.size()>0) {
            periodSetlist.stream().forEach(periodSet -> {
                PeriodSetDTO periodSetDTO = new PeriodSetDTO();
                periodSetDTO.setId(periodSet.getId());
                if(language.equals("zh_cn")) {
                    switch (periodSet.getPeriodAdditionalFlag()) {
                        case "P":
                            periodSetDTO.setPeriodAdditionalFlagDes("附加前缀");
                            break;
                        case "S":
                            periodSetDTO.setPeriodAdditionalFlagDes("附加后缀");
                            break;
                    }
                }
                else {
                    switch (periodSet.getPeriodAdditionalFlag()) {
                        case "P":
                            periodSetDTO.setPeriodAdditionalFlagDes("prefix");
                            break;
                        case "S":
                            periodSetDTO.setPeriodAdditionalFlagDes("suffix");
                            break;
                    }

                }
                periodSetDTO.setPeriodSetName(periodSet.getPeriodSetName());
                periodSetDTO.setI18n(periodSet.getI18n());
                periodSetDTO.setTenantId(periodSet.getTenantId());
                periodSetDTO.setPeriodAdditionalFlag(periodSet.getPeriodAdditionalFlag());
                periodSetDTO.setPeriodSetCode(periodSet.getPeriodSetCode());
                periodSetDTO.setTotalPeriodNum(periodSet.getTotalPeriodNum());
                periodSetDTO.setEnabled(periodSet.getEnabled());
                periodSetDTO.setDeleted(periodSet.getDeleted());
                periodSetDTO.setCreatedBy(periodSet.getCreatedBy());
                periodSetDTO.setCreatedDate(periodSet.getCreatedDate());
                periodSetDTO.setLastUpdatedBy(periodSetDTO.getLastUpdatedBy());
                periodSetDTO.setLastUpdatedDate(periodSetDTO.getLastUpdatedDate());
                periodSetDTOList.add(periodSetDTO);
            });
        }
        return periodSetDTOList;
    }
    /**
     * 会计期间实体视图对象转换会计期间实体类
     * @param periodSetDTO：会计期间实视图对象
     * @return
     */
    public PeriodSet periodSetDTOToPeriodSet(PeriodSetDTO periodSetDTO) {
        PeriodSet periodSet = new PeriodSet();

        if (periodSetDTO != null) {
            periodSet.setId(periodSetDTO.getId());
            periodSet.setTenantId(periodSetDTO.getTenantId());
            periodSet.setPeriodSetCode(periodSetDTO.getPeriodSetCode());
            periodSet.setTotalPeriodNum(periodSetDTO.getTotalPeriodNum());
            periodSet.setPeriodAdditionalFlag(periodSetDTO.getPeriodAdditionalFlag());
            periodSet.setEnabled(periodSetDTO.getEnabled());
            periodSet.setDeleted(periodSetDTO.getDeleted());
            periodSet.setLastUpdatedBy(periodSetDTO.getLastUpdatedBy());
            periodSet.setLastUpdatedDate(periodSetDTO.getLastUpdatedDate());
        }
        return periodSet;
    }

    public List<PeriodSet> periodSetDTOListToPeriodSetList(List<PeriodSetDTO> periodSetDTOList) {
        List<PeriodSet> periodSetList = new ArrayList<PeriodSet>();
        if(periodSetDTOList.size()>0) {
            periodSetDTOList.stream().forEach(periodSetDTO -> {
                PeriodSet periodSet = new PeriodSet();
                periodSet.setId(periodSetDTO.getId());
                periodSet.setTenantId(periodSetDTO.getTenantId());
                periodSet.setPeriodAdditionalFlag(periodSetDTO.getPeriodAdditionalFlag());
                periodSet.setPeriodSetCode(periodSetDTO.getPeriodSetCode());
                periodSet.setTotalPeriodNum(periodSetDTO.getTotalPeriodNum());
                periodSet.setEnabled(periodSetDTO.getEnabled());
                periodSet.setDeleted(periodSetDTO.getDeleted());
                periodSet.setLastUpdatedBy(periodSetDTO.getLastUpdatedBy());
                periodSet.setLastUpdatedDate(periodSetDTO.getLastUpdatedDate());
            });
        }
        return periodSetList;
    }
}
