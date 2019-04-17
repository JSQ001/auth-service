package com.hand.hcf.app.mdata.company.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.CompanyLevelCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.CompanyLevel;
import com.hand.hcf.app.mdata.company.persistence.CompanyLevelMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by 刘亮 on 2017/9/4.
 */
@Service

public class CompanyLevelService extends ServiceImpl<CompanyLevelMapper, CompanyLevel> {


    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private MapperFacade mapperFacade;

    //新增或更新公司级别
    public CompanyLevel insertOrUpdateCompanyLevel(CompanyLevel companyLevel, UUID userId) {
        companyLevel.setTenantId(OrgInformationUtil.getCurrentTenantId());
        if (StringUtils.isEmpty(companyLevel.getDescription())) {
            throw new BizException(RespCode.COMPANY_LEVEL_DESCRIPTION_IS_NULL_20005);
        }
        //根据传来的公司级别实体是否有id判断是新增或修改
        if (companyLevel.getId() == null) {//新增

            //校验公司级别代码
            checkCompanyLevelCode(companyLevel.getCompanyLevelCode());

            //  Code过滤单引号
            String result = StringEscapeUtils.escapeSql(companyLevel.getCompanyLevelCode());
            //  Code过滤特殊字符
            String codeResult = StringUtil.filterSpecialCharacters(result);
            //  过滤后重新set
            companyLevel.setCompanyLevelCode(codeResult);

            companyLevel.setCreatedDate(ZonedDateTime.now());
            baseMapper.insert(companyLevel);
            return baseI18nService.selectOneTranslatedTableInfoWithI18n(companyLevel.getId(), CompanyLevel.class);
        }
        //修改
        //公司级别被使用后不可被修改状态
        if (!companyLevel.getEnabled().equals(baseMapper.selectById(companyLevel.getId()).getEnabled())
                &&
                !CollectionUtils.isEmpty(baseMapper.selectByCompanyLevelCode(companyLevel.getId()))) {
            throw new BizException(RespCode.COMPANY_LEVEL_HAS_BEEN_ENABLED_6020006);
        }
        CompanyLevel oldCompanyLevel = baseMapper.selectById(companyLevel.getId());
        if (!oldCompanyLevel.getCompanyLevelCode().equals(companyLevel.getCompanyLevelCode())) {//如果不相等，说明此次修改，修改到了code，则需要检验
            checkCompanyLevelCode(companyLevel.getCompanyLevelCode());
            //  Code过滤单引号
            String result = StringEscapeUtils.escapeSql(companyLevel.getCompanyLevelCode());
            //  Code过滤特殊字符
            String codeResult = StringUtil.filterSpecialCharacters(result);
            //  过滤后重新set
            companyLevel.setCompanyLevelCode(codeResult);
        }

        baseMapper.updateById(companyLevel);
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(companyLevel.getId(), CompanyLevel.class);
    }

    @Transactional
    //删除公司级别
    public boolean deleteCompanyLevelById(Long id) {
        CompanyLevel companyLevel = baseMapper.selectById(id);
        if (companyLevel == null) {
            throw new BizException(RespCode.COMPANY_LEVEL_NOT_FOUND_20003);
        }
        companyLevel.setDeleted(true);
        companyLevel.setCompanyLevelCode(companyLevel.getCompanyLevelCode() + "_DELETE_" + RandomStringUtils.randomNumeric(6));
        int i = baseMapper.updateById(companyLevel);
        if (i != 0) {
            return true;
        }
        return false;
    }


    //条件（公司级别代码，描述）查询公司级别
    public Page<CompanyLevel> selectByInput(String companyLevelCode, String description, Page<CompanyLevel> page) {
        EntityWrapper wrapper = new EntityWrapper();
        if (!StringUtil.isNullOrEmpty(companyLevelCode)) {
            wrapper.like("company_level_code", companyLevelCode);
        }
        if (!StringUtil.isNullOrEmpty(description)) {
            wrapper.like("description", description);
        }
        wrapper.eq("deleted", false);
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        wrapper.orderBy("enabled", false);
        wrapper.orderBy("COMPANY_LEVEL_CODE", true);
        List<CompanyLevel> list = baseMapper.selectPage(page, wrapper);


        List<CompanyLevel> i18ns = new ArrayList<>();
        list.stream().forEach((CompanyLevel companyLevel) -> {
            i18ns.add(baseI18nService.selectOneTranslatedTableInfoWithI18n(companyLevel.getId(), CompanyLevel.class));
        });

        if (CollectionUtils.isNotEmpty(i18ns)) {
            page.setRecords(i18ns);
        }
        return page;
    }

    //根据租户查询公司级别
    public List<CompanyLevel> selectByTenantId() {
        EntityWrapper<CompanyLevel> wrapper = new EntityWrapper<>();
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        wrapper.eq("enabled", true);
        wrapper.eq("deleted", false);
        List<CompanyLevel> list = baseMapper.selectList(wrapper);
        return list;

    }


    //校验公司级别代码是否合法
    public void checkCompanyLevelCode(String companyLevelCode) {
        if (StringUtils.isEmpty(companyLevelCode)) {
            throw new BizException(RespCode.COMPANY_LEVEL_CODE_NULL_20001);
        }

        String reg2 = "[a-zA-Z0-9_]{1,35}";
        //判断公司级别code长度是否超过限制
        if (!companyLevelCode.matches(reg2)) {//36=50-14(删除的时候需要加占14个字符)
            throw new BizException(RespCode.COMPANY_LEVEL_CODE_LENGTH_MORE_THEN_LIMIT_0R_NOT_INNEGAL_20004);
        }
        //判断此code是否在数据库存在，如果存在则抛出唯一性异常
        EntityWrapper<CompanyLevel> wrapper = new EntityWrapper<>();
        wrapper.eq("company_level_code", companyLevelCode);
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        wrapper.eq("deleted", false);
        if (baseMapper.selectList(wrapper).size() != 0) {
            throw new BizException(RespCode.COMPANY_LEVEL_CODE_REPEAT_20002);
        }
    }

    ;

    //根据id查询公司级别
    public CompanyLevel selectById(Long id) {
        return baseI18nService.selectOneBaseTableInfoWithI18n(id, CompanyLevel.class);
    }

    //根据公司级别id查看翻译后的信息以及i18n信息
    public CompanyLevel selectOneTranslatedTableInfoWithI18nById(Long id) {
        return baseI18nService.selectOneTranslatedTableInfoWithI18n(id, CompanyLevel.class);
    }

    /**
     * 根据公司级别id查询公司级别
     *
     * @param id：公司级别id
     * @return：公司级别
     */
    public CompanyLevel getCompanyLevelById(Long id) {
        CompanyLevel companyLevel = baseMapper.selectById(id);
        return baseI18nService.convertOneByLocale(companyLevel);
    }

    public List<CompanyLevelCO> selectByCondition(Long companyLevelId, String companyLevelCode){

        List<CompanyLevel> list = baseMapper.selectList(new EntityWrapper<CompanyLevel>()
                .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                .eq(companyLevelId!=null,"id",companyLevelId)
                .eq(companyLevelCode!=null, "company_level_code",companyLevelCode)
                .eq("enabled",true));
        List<CompanyLevelCO> collect = list.stream().map(item -> {
            CompanyLevelCO companyLevelCO = new CompanyLevelCO();
            mapperFacade.map(item, companyLevelCO);
            return companyLevelCO;
        }).collect(Collectors.toList());
        return collect;
    }

}
