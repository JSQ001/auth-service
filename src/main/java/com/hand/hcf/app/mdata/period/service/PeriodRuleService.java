package com.hand.hcf.app.mdata.period.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.period.domain.PeriodRules;
import com.hand.hcf.app.mdata.period.persistence.PeriodRulesMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.hand.hcf.core.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PeriodRuleService extends ServiceImpl<PeriodRulesMapper, PeriodRules> {

    private final Logger log = LoggerFactory.getLogger(PeriodRuleService.class);

    @Autowired
    private PeriodRulesMapper periodRulesMapper;

    /**
     * 增加一个规则
     * @param periodRules
     * @return
     */

    /**
     * 月份从：GLD_PERIOD_RULES. MONTH_FROM，数字格式，必输字段，月份<=12。
     * 日期从：GLD_PERIOD_RULES. DATE_FROM，数字格式，必输字段，if 月份从 in（1,3,5,7,8,10,12） then 日期从<=31 elseif 月份从 in (4,6,9,11) then 日期从<=30 else if 月份从=2 then 日期从<=28。
     * 月份到：GLD_PERIOD_RULES. MONTH_TO，数字格式，必输字段，月份<=12。
     * 日期到：GLD_PERIOD_RULES. DATE_TO，数字格式，必输字段，if 月份从 in（1,3,5,7,8,10,12） then 日期从<=31 elseif 月份从 in (4,6,9,11) then 日期从<=30 else if 月份从=2 then 日期从<=28。
     * 季度：GLD_PERIOD_RULES. QUARTER_NUM，数字格式，必输字段。数字<=4。
     * 调整：GLD_PERIOD_RULES. ADJUSTMENT_FLAG，勾选框。非必输。调整勾选时，日期从=日期到 and 月份从=月份到，如果不是上述格式，则给出错误提示：调整期维护格式错误，请检查。
     * 租户：当前登录用户所在的租户ID。
     * 如果非调整期间汇总数<12条，则不能保存，保存时给出错误提示：期间维护不完整！
     * 检查月份之间是否有日期遗漏，公式：date(前一笔月份到||日期到,’MMDD’)+1=date(后一笔月份从||日期从,’MMDD’)。
     * 检查起始月份从||日期从到最终月份到||日期到是否是一整年。公式为：date（最终月份到||日期到,’MMDD’）+1=date（起始月份从||日期从,’MMDD’）。
     */
    public PeriodRules addPeriodRules(PeriodRules periodRules) throws Exception {
        ValidationPeriodRules(periodRules);
        // 季度 QUARTER_NUM 数字<=4。
        if (periodRules.getQuarterNum() > 4) {
            throw new BizException(RespCode.QUARTER_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_4);
        }
        //月份从：GLD_PERIOD_RULES. MONTH_FROM，数字格式，必输字段，月份<=12。
        if (periodRules.getMonthFrom() > 12) {
            throw new BizException(RespCode.MONTHS_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_12);
        }
        //日期从： DATE_FROM的校验
        ValidationDate(periodRules.getMonthFrom(), periodRules.getDateFrom());
        //月份到：GLD_PERIOD_RULES. MONTH_TO，数字格式，必输字段，月份<=12。
        if (periodRules.getMonthTo() > 12) {
            throw new BizException(RespCode.MONTHS_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_12);
        }
        // 日期到DATE_TO的校验
        ValidationDate(periodRules.getMonthTo(), periodRules.getDateTo());
        // 校验期间名附加是否为空
        if (StringUtil.isNullOrEmpty(periodRules.getPeriodAdditionalName())){
            throw new BizException(RespCode.PERIOD_ADDITIONAL_NAME_IS_EMPTY);
        }
        // 校验期间名附加的长度
        if (periodRules.getPeriodAdditionalName().length() > 20){
            throw new BizException(RespCode.PERIOD_ADDITIONAL_NAME_IS_TOO_LONG);
        }
        periodRules.setTenantId(OrgInformationUtil.getCurrentTenantId());
        periodRulesMapper.insert(periodRules);
         return periodRules;
}
    /**
     * 更新一个规则
     * @param periodRules
     */
    public void updatePeriodRules(PeriodRules periodRules){

	         ValidationPeriodRules(periodRules);
            //月份到：GLD_PERIOD_RULES. MONTH_TO，数字格式，必输字段，月份<=12。
            if (periodRules.getMonthTo() > 12) {
                throw new BizException(RespCode.MONTHS_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_12);
            }
            // 日期到DATE_TO的校验
            ValidationDate(periodRules.getMonthTo(), periodRules.getDateTo());
            //日期从： DATE_FROM的校验
            //月份从：GLD_PERIOD_RULES. MONTH_FROM，数字格式，必输字段，月份<=12。
            if (periodRules.getMonthFrom() > 12) {
                throw new BizException(RespCode.MONTHS_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_12);
            }
            ValidationDate(periodRules.getMonthFrom(), periodRules.getDateFrom());
            // 季度 QUARTER_NUM 数字<=4。
            if (periodRules.getQuarterNum() > 4) {
                throw new BizException(RespCode.QUARTER_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_4);
            }
            // 校验期间名附加是否为空
            if (StringUtil.isNullOrEmpty(periodRules.getPeriodAdditionalName())){
                throw new BizException(RespCode.PERIOD_ADDITIONAL_NAME_IS_EMPTY);
            }
            // 校验期间名附加的长度
            if (periodRules.getPeriodAdditionalName().length()> 20){
                throw new BizException(RespCode.PERIOD_ADDITIONAL_NAME_IS_TOO_LONG);
            }
            periodRules.setTenantId(OrgInformationUtil.getCurrentTenantId());
       List<PeriodRules> list=   periodRulesMapper.selectList( new EntityWrapper<PeriodRules>()
            .where("deleted = false")
            .eq(periodRules.getPeriodSetId() != null,"period_set_id",periodRules.getPeriodSetId())
            .eq("tenant_id",OrgInformationUtil.getCurrentTenantId()));
        List<PeriodRules> result=  list.stream().filter(u->!u.getId().equals(periodRules.getId())).collect(Collectors.toList());
        result.add(periodRules);
        //规则的完整性校验,检查是否填满365天
        ValidationRulesComplete(result);
        periodRulesMapper.updateById(periodRules);
    }
    /**
     * 逻辑删除一个规则
     * @param id
     */
    public void deletePeriodRules(Long id){
        PeriodRules periodRules = new PeriodRules();
        periodRules.setId(id);
        PeriodRules result = periodRulesMapper.selectOne(periodRules);
        if(null != result && result.getDeleted()==false){
            result.setDeleted(true);
            periodRulesMapper.updateById(result);
        }
    }
    /**
     * 通过id 查询一个规则
     * @param id
     * @return
     */
    public PeriodRules getPeriodRules(Long id){
        PeriodRules periodRules=new PeriodRules();
        periodRules.setId(id);
        periodRules.setDeleted(false);
        PeriodRules result=  periodRulesMapper.selectOne(periodRules);
        return result;
    }

    /**
     * 通过会计期id 分页查询规则
     * @param page
     * @param periodSetId
     * @return
     */
    public Page<PeriodRules> findPeriodRulesByPeriodSetId(Page<PeriodRules> page, Long periodSetId){
        List<PeriodRules> periodRulesList = periodRulesMapper.selectPage(page,
            new EntityWrapper<PeriodRules>()
                .where("deleted = false")
                .eq(periodSetId != null,"period_set_id",periodSetId)
                .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
        );
        Page<PeriodRules> periodRulesPage=page;
        if(CollectionUtils.isNotEmpty(periodRulesList)){
            periodRulesPage.setRecords(periodRulesList);
        }
        return periodRulesPage;
    }

    /**
     *
     * @param list
     * @return
     * @throws Exception
     */

    public List<PeriodRules> addPeriodRulesBatch(List<PeriodRules> list) throws  Exception {
            //数据校验
            list.stream().forEach(periodRules-> {
                  ValidationPeriodRules(periodRules);
                    // 季度 QUARTER_NUM 数字<=4。
                    // 校验期间名附加的长度
                    if (periodRules.getPeriodAdditionalName().length() > 20){
                        throw new BizException(RespCode.PERIOD_ADDITIONAL_NAME_IS_TOO_LONG);
                    }
                    //月份从：GLD_PERIOD_RULES. MONTH_FROM，数字格式，必输字段，月份<=12。
                    if (periodRules.getMonthFrom() > 12) {
                        throw new BizException(RespCode.MONTHS_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_12);
                    }
                    if (periodRules.getQuarterNum() > 4) {
                        throw new BizException(RespCode.QUARTER_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_4);
                    }
                    //日期从： DATE_FROM的校验
                    ValidationDate(periodRules.getMonthFrom(), periodRules.getDateFrom());
                    //月份到：GLD_PERIOD_RULES. MONTH_TO，数字格式，必输字段，月份<=12。
                    if (periodRules.getMonthTo() > 12) {
                        throw new BizException(RespCode.MONTHS_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_12);
                    }
                    // 日期到DATE_TO的校验
                    ValidationDate(periodRules.getMonthTo(), periodRules.getDateTo());
                    periodRules.setTenantId(OrgInformationUtil.getCurrentTenantId());
            });
        //规则的完整性校验,检查是否填满365天
        ValidationRulesComplete(list);
        list.stream().forEach(periodRules-> periodRulesMapper.insert(periodRules));
        return list;
    }

    /**
     * 日期的校验
     * if 月份从 in（1,3,5,7,8,10,12） then 日期从<=31 elseif 月份从 in (4,6,9,11) then 日期从<=30 else if 月份从=2 then 日期从<=28。
     * @param month
     * @param date
     */
    public void ValidationDate(Integer month, Integer date){
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if(date>31)
                {
                    throw new BizException(RespCode.THE_DATE_SHOULD_BE_LESS_THAN_31, new String[]{String.valueOf(month)});
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if(date>30)
                {
                    throw new BizException(RespCode.THE_DATE_SHOULD_BE_LESS_THAN_30, new String[]{String.valueOf(month)});
                }
                break;
            case 2:

                if(date>28)
                {
                    throw new BizException(RespCode.THE_DATE_SHOULD_BE_LESS_THAN_29, new String[]{String.valueOf(month)});
                }
                break;
        }
    }
    public void ValidationPeriodRules(PeriodRules periodRules){
        //会计期id为空校验
        if(StringUtils.isEmpty(periodRules.getPeriodSetId()))
        {
            throw new BizException(RespCode.ACCOUNTING_PERIOD_IS_EMPTY);
        }//期间名称附加为空校验
        else if (StringUtils.isEmpty(periodRules.getPeriodAdditionalName()))
        {
            throw new BizException(RespCode.TIME_NAME_ADDITION_CANNOT_BE_EMPTY);
        }//月份从为空校验
         else if (StringUtils.isEmpty(periodRules.getMonthFrom()))
        {
            throw new BizException(RespCode.MONTH_FROM_CANT_BE_EMPTY);
        }//月份至
         else if (StringUtils.isEmpty(periodRules.getMonthTo()))
        {
            throw new BizException(RespCode.MONTH_TO_CANT_BE_EMPTY);

        }//日期从为空校验
         else if (StringUtils.isEmpty(periodRules.getDateFrom()))
        {
            throw new BizException(RespCode.DATE_FROM_CANT_BE_EMPTY);
        }
        //日期至为空校验
         else if (StringUtils.isEmpty(periodRules.getDateTo()))
        {
            throw new BizException(RespCode.DATE_TO_CANT_BE_EMPTY);
        }
        //季度为空校验
         else if (StringUtils.isEmpty(periodRules.getQuarterNum()))
        {
            throw new BizException(RespCode.QUARTER_CANT_BE_EMPTY);
        }
    }
    public static void ValidationRulesComplete(List<PeriodRules> periodRulesList) {
        //存在重复日期的list
        List list=new ArrayList();
        //不存在重复日期的list
        HashSet hashSet=new HashSet();
        periodRulesList.stream().forEach(u->{
            LocalDate localDate= LocalDate.of(2002,u.getMonthFrom(),u.getDateFrom());
            LocalDate localDate2= LocalDate.of(2002,u.getMonthTo(),u.getDateTo());
            //以2001年的平年为标准, 取两个日期的时间差
            for(int i=0;i<=localDate2.getDayOfYear() - localDate.getDayOfYear(); i++)
            {//月份加天作为唯一key
                list.add(localDate.plusDays(i).getMonthValue() + "-" + localDate.plusDays(i).getDayOfMonth());
                hashSet.add(localDate.plusDays(i).getMonthValue() + "-" + localDate.plusDays(i).getDayOfMonth());
            }
        });
        if(list.size()<365 && hashSet.size()!= 365)
        {
            throw new BizException(RespCode.DATE_MAINTENANCE_IS_INCOMPLETE);
        }
    }
}
