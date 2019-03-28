package com.hand.hcf.app.mdata.period.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.PeriodCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.period.domain.PeriodRules;
import com.hand.hcf.app.mdata.period.domain.PeriodSet;
import com.hand.hcf.app.mdata.period.domain.PeriodStatus;
import com.hand.hcf.app.mdata.period.domain.Periods;
import com.hand.hcf.app.mdata.period.dto.PeriodsDTO;
import com.hand.hcf.app.mdata.period.persistence.PeriodsMapper;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.hand.hcf.core.exception.BizException;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PeriodsService extends ServiceImpl<PeriodsMapper, Periods> {
    private final Logger log = LoggerFactory.getLogger(PeriodsService.class);

    @Autowired
    private PeriodsMapper periodsMapper;
    @Autowired
    private PeriodStatusService periodStatusService;
    @Autowired
    private PeriodSetService periodSetService;
    @Autowired
    private PeriodRuleService periodRuleService;
    @Autowired
    private SetOfBooksService setOfBooksService;
    @Autowired
    private MapperFacade mapperFacade;

    /**
     * 根据periodSetCod和tenantId分页查询未打开的期间数据数据（一次也没有未打开的数据）
     * @param periodSetId
     * @param page
     * @return
     * @throws URISyntaxException
     */
    public Page<PeriodsDTO> findClosePeriodsByTenantIdAndPeriodSetId(Page<PeriodsDTO> page, Long periodSetId, Long setOfBooksId){
        List<PeriodsDTO> periodsList = periodsMapper.findClosePeriodsByTenantIdAndPeriodSetId(page, periodSetId, OrgInformationUtil.getCurrentTenantId(),setOfBooksId);
           if(CollectionUtils.isNotEmpty(periodsList)){page.setRecords(periodsList);}
        return page;
    }
    /**
     * 根据periodSetCodeh和tenantId分页查询已经打开过的期间数据（包含已打开和已关闭数据）
     * @param periodSetId
     * @param page
     * @return
     * @throws URISyntaxException
     */
    public Page<PeriodsDTO> findOpenPeriodsByTenantIdAndPeriodSetCode(Page<PeriodsDTO> page, Long periodSetId, Long setOfBooksId){
        List<PeriodsDTO> periodsList = periodsMapper.findOpenPeriodsByTenantIdAndPeriodSetId(page, periodSetId, OrgInformationUtil.getCurrentTenantId(),setOfBooksId);
        if(CollectionUtils.isNotEmpty(periodsList)){page.setRecords(periodsList);}
        return page;
    }

    /**
     *
     ** 打开期间
     * 通过期间id查询账套级会计期间定义查询是否已经打开过
     * 如果没有
     * 1.通过期间id查询art_periods要打开的月份的所有信息
     * 2.通过sql查询出要打开的月份的上一个月，如果不是第一个，
     * 3.查询上一个月份是否打开过
     *如果已经打开过
     * 如果不是打开的最后一个期间,下一个期间必须是打开过的
     * @param periodId
     * @param periodSetId
     */
    public Boolean openPeriodByPeriodIdAndPeriodSetIdAndTenantId(Long periodId, //总账期间id
                                                                 Long periodSetId,//会计期id
                                                                 Long  setOfBooksId //账套ID
    ){
        //取出art_periods要打开的月份的所有信息
        Periods periods= periodsMapper.selectById(periodId);
        //1.判断是否第一次打开
        PeriodStatus isPeriodStatus = periodStatusService.selectOne(
                new EntityWrapper<PeriodStatus>()
                        .eq("period_id", periodId)
                        .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                        .eq("set_of_books_id", setOfBooksId)
        );
        //是第一次打开
        if(isPeriodStatus==null) {
         //前一个期间必须是打开过的 ,取前一条数据
         List<Periods>  periodsList=  periodsMapper.selectList( new EntityWrapper<Periods>()
                .where("deleted = false")
                .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
                .eq("period_set_id", periodSetId)
                .le("period_seq",periods.getPeriodSeq())
                .orderBy("period_seq",true)
            );
         //如果是头一个期间打开则不需要判断
         if(periodsList.size()==1)
         {
             PeriodStatus periodStatusSave = new PeriodStatus();
             periodStatusSave.setPeriodId(periodId);
             periodStatusSave.setTenantId(periods.getTenantId());
             periodStatusSave.setSetOfBooksId(setOfBooksId);
             periodStatusSave.setPeriodName(periods.getPeriodName());
             periodStatusSave.setPeriodSeq(periods.getPeriodSeq());
             periodStatusSave.setPeriodStatusCode("O");
             periodStatusSave.setPeriodId(periodId);
             periodStatusService.insert(periodStatusSave);
         }
         else {
             Periods periodsMax = periodsList.get(periodsList.size()-2);
             //3.判断上一个月份期间是否打开过
             PeriodStatus lastPeriodStatusResut = periodStatusService.selectOne(
                     new EntityWrapper<PeriodStatus>()
                             .eq("period_id", periodsMax.getId())
                             .eq("tenant_id", periodsMax.getTenantId())
                             .eq("set_of_books_id", setOfBooksId)
             );
             //如果为空则说明上一个期间没有被打开
             if (lastPeriodStatusResut != null) {
                 PeriodStatus periodStatusSave = new PeriodStatus();
                 periodStatusSave.setPeriodId(periodId);
                 periodStatusSave.setSetOfBooksId(setOfBooksId);
                 periodStatusSave.setPeriodSeq(periods.getPeriodSeq());
                 periodStatusSave.setTenantId(periods.getTenantId());
                 periodStatusSave.setPeriodStatusCode("O");
                 periodStatusSave.setPeriodName(periods.getPeriodName());
                 periodStatusService.insert(periodStatusSave);
             } else {
                 throw new BizException(RespCode.LAST_PERIOD_UNOPENED);

             }
         }
        }
        //不是第一次打开
        else {
            //--如果不是打开的最后一个期间,下一个期间必须是打开过的
            //取出所有当前租户和会计期间的所有总账id
            List<Periods> periodsListId = periodsMapper.selectList(
                new EntityWrapper<Periods>()
                    .where("deleted = false")
                    .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                    .eq("period_set_id", periodSetId));
            //查询出所有已经打开的总账id，并排序
            List<PeriodStatus> periodStatusList = periodStatusService.selectList(new EntityWrapper<PeriodStatus>()
                .where("deleted = false")
                .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                .in("period_id", periodsListId.stream().map(u -> u.getId()).collect(Collectors.toList()))
                .eq("set_of_books_id", setOfBooksId)
                .ge("period_seq", periods.getPeriodSeq())
                .orderBy("period_seq", false)
            );
            //如果数量为1说明是已经打开过的最后一条重新打开
            if(periodStatusList.size()==1)
            {
                PeriodStatus findPeriodStatus = periodStatusService.selectOne(
                        new EntityWrapper<PeriodStatus>()
                                .eq("period_id", periodId)
                                .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                                .eq("set_of_books_id", setOfBooksId)
                );
                findPeriodStatus.setPeriodStatusCode("O");
                periodStatusService.updateById(findPeriodStatus);
            } else {
                //取当前期间的后一个期间，判断是否关闭，如果是关闭状态则抛错
                PeriodStatus periodStatus1 = periodStatusList.get(periodStatusList.size() - 2);
                if (periodStatus1.getPeriodStatusCode().equals("C")) {
                    throw new BizException(RespCode.NEXT_PERIOD_UNCLOSED);
                } else {
                    PeriodStatus findPeriodStatus = periodStatusService.selectOne(
                            new EntityWrapper<PeriodStatus>()
                                    .eq("period_id", periodId)
                                    .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                                    .eq("set_of_books_id", setOfBooksId)
                    );
                    findPeriodStatus.setPeriodStatusCode("O");
                    periodStatusService.updateById(findPeriodStatus);
                }
            }
        }
      return true;
    }

    /**
     *  关闭期间
     * @param periodId
     * @param periodSetId
     * @param setOfBooksId
     * @return
     */

    public Boolean closePeriodByPeriodIdAndPeriodSetIdAndTenantId(Long periodId, //总账期间id
                                                                  Long periodSetId,//会计期id
                                                                  Long  setOfBooksId )//账套ID
    {
        //  获取传入的期间ID,获取会计期间序列号
        PeriodStatus periodStatus = periodStatusService.selectOne(
                new EntityWrapper<PeriodStatus>()
                        .eq("period_id", periodId)
                        .eq("set_of_books_id", setOfBooksId)
        );
        Integer periodSeq = periodStatus.getPeriodSeq();
//        //  根据会计期ID 租户ID,获取总账期间数据
//        List<Periods> periodsList = periodsMapper.selectList(new EntityWrapper<Periods>()
//            .where("deleted = false")
//            .eq("period_set_id",periodSetId)
//            .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
//            .orderBy("period_seq")
//        );
//        //  遍历总账数据,根据总账期间ID去查会计期间表中是否存在此ID下的数据
//        List<PeriodStatus> periodStatusList =  periodStatusMapper.selectList(new EntityWrapper<PeriodStatus>()
//            .where("deleted = false")
//            .in("period_id",periodsList.stream().map(u->u.getId()).collect(Collectors.toList()))
//            .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
//            .eq("set_of_books_id",setOfBooksId)
//            .lt("period_seq",periodSeq)
//            .orderBy("period_seq")
//        );
        List<PeriodStatus> periodStatusList =  periodStatusService.findPeriodStatusByTenantIdAndPeriodSetId(
            periodSetId,
            OrgInformationUtil.getCurrentTenantId(),
            setOfBooksId,
            periodSeq
        );

        //  如果会计期间为空,说明此时关闭的期间为最前面的期间
        if (periodStatusList.size() == 0) {
            periodStatus.setPeriodStatusCode("C");
            periodStatusService.updateById(periodStatus);
            return true;
        }
        //  如果会计期间集合不为空,则验证前面一位的期间是否关闭
        PeriodStatus periodStatusResult = periodStatusList.get( periodStatusList.size() - 1 );
        if ( periodStatusResult.getPeriodStatusCode().equals("C") ){
            periodStatus.setPeriodStatusCode("C");
            periodStatusService.updateById(periodStatus);

        }else {
            throw new BizException(RespCode.CLOSE_THE_PRIOR_PERIOD_FIRST);
        }
           return true;
    }


    /**
     * 新增会计期生成程序
     *
     * xiaoting.pan
     * @param periodSetCode
     * @param yearFrom
     * @param yearTo
     * @return
     */
    public Boolean createPeriodsBatch(String periodSetCode,Integer yearFrom,Integer yearTo){
        //  会计期代码是否为空
        if(StringUtil.isNullOrEmpty(periodSetCode)){
            throw new BizException(RespCode.PERIODSETCODE_IS_EMPTY);
        }
        //  年度是否为空
        if(yearFrom == null || yearTo == null){
            throw new BizException(RespCode.Year_IS_EMPTY);
        }
        //  判断输入的年度从是否满足小于等于年度到
        if(yearFrom > yearTo){
            throw new BizException(RespCode.YEAR_FROM_SHOULD_BE_LESS_THEN_YEAR_TO);
        }
       /* //判断日期从大于等于当前年份
        LocalDate now=LocalDate.now();
        if(yearFrom < now.getYear()){
            throw new BizException(RespCode.PERIODS_26010);
        }*/
        //  根据传入的会计期代码获取会计期实体
        PeriodSet periodSetResult = periodSetService.selectOne(
                new EntityWrapper<PeriodSet>()
                        .eq("period_set_code", periodSetCode)
                        .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
        );
        //  判断是否查询到会计期实体
        if (periodSetResult == null){
            throw new BizException(RespCode.ACCOUNTING_ENTITY_DOES_NOT_EXIST);
        }
        //  从会计期实体获取会计期ID
        Long periodSetId = periodSetResult.getId();
        //  从会计期实体获取附加前缀或附件后缀的标志
        String PeriodAdditionalFlag = periodSetResult.getPeriodAdditionalFlag();
        //  从会计期实体获取租户ID
        Long tenantId = periodSetResult.getTenantId();
        //  查询会计期规则,根据会计期ID
        List<PeriodRules> periodRulesList = periodRuleService.selectList(new EntityWrapper<PeriodRules>()
            .where("deleted = false")
            .eq("period_set_id",periodSetId));
        //  判断会计期规则是否存在
        if(periodRulesList.size() == 0){
            throw new BizException(RespCode.ACCOUNTING_RULES_DO_NOT_EXIST);
        }
        //  遍历年度从与年度到区间
        for(int year = yearFrom ; year <= yearTo; year++){
            //  根据年度, 租户ID, 会计期ID去查询是否已存在数据
            List<Periods> list = periodsMapper.selectList(new EntityWrapper<Periods>()
                .where("deleted = false")
                .eq("period_year",year)
                .eq("tenant_id",tenantId)
                .eq("period_set_id",periodSetId)
            );
            if (list.size()!=0) {
                throw new BizException(RespCode.FILLIN_YEAR_REPEAT);
            }
            //  遍历会计期规则
            List<Periods> periodsList = new ArrayList<Periods>();
            for(PeriodRules rules : periodRulesList) {
                Periods periods = new Periods();
                //  set会计期ID
                periods.setPeriodSetId(periodSetId);
                //  set年
                periods.setPeriodYear(year);
                //  set月
                periods.setPeriodNum(rules.getMonthFrom());
                //  set序号
                periods.setPeriodSeq(year*10000+rules.getPeriodNum());
                //  set期间
                //  根据会计期定义表PeriodAdditionalFlag判断是前缀还是后缀
                if (PeriodAdditionalFlag.equals("P")) { // 前缀
                    periods.setPeriodName(rules.getPeriodAdditionalName() + "-" + year);
                } else { // 后缀
                    periods.setPeriodName(year + "-" + rules.getPeriodAdditionalName());
                }
                //  set日期从
                periods.setStartDate( getTransformDate(year,rules.getMonthFrom(),rules.getDateFrom()) );
                //  set日期到
                periods.setEndDate( getTransformDate(year,rules.getMonthTo(),rules.getDateTo()) );
                //  set季度
                periods.setQuarterNum(rules.getQuarterNum());
                //  set租户ID
                periods.setTenantId(tenantId);
                //  填装好的Periods放入集合
                periodsList.add(periods);
            }
            //  同一年份批量插入
            periodsList.stream().forEach((Periods periods)->
                {
                    try {
                        periodsMapper.insert(periods);
                    } catch (DataIntegrityViolationException e) {
                        throw new BizException(RespCode.YEAR_EXCEEDS_THE_PREDEFINED_TIME_LIMIT_OF_TIME_STAMP);
                    }
                }
            );
        }
        //  成功时返回true
        return true;
    }
    //  判断是否为闰年
    static Boolean isLeapYear(Integer year){
        if (year%4 == 0 && year%100 != 0 || year%400 == 0){
            return  true;
        }else {
            return  false;
        }
    }
    //  日期转换
    static String getTransformDate(Integer year,Integer month,Integer day){
        //  如果月份是2月,并且日期大于等于28,则需要判断是否为闰年
        if ( month == 2 && day >= 28 ){
            if (isLeapYear(year)){ // 闰年
                String endDate = year+"-02-29";
                return endDate;
            }else { // 平年
                String endDate = year+"-02-28";
                return endDate;
            }
        }else {
            String resultDate = year+"";
            //  月拼接零
            if (month < 10){
                resultDate = resultDate+"-0"+month;
            }else {
                resultDate = resultDate+"-"+month;
            }
            //  日拼接零
            if (day < 10) {
                resultDate = resultDate+"-0"+day;
            }else {
                resultDate = resultDate+"-"+day;
            }
            return resultDate;
        }
    }


    /**
     * 预算项目查询  根据年度查询所有会计期代码
     *
     * xiaoting.pan
     * @param year 年度
     * @return
     */
    public List<PeriodSet> findPeriodSetCodeByYear(Integer year){
        return periodsMapper.findPeriodSetCodeByYear(year);
    }


    /**
     * 预算项目查询  根据账套ID与期间Name查询总账期间信息
     *
     * xiaoting.pan
     * @param setOfBooksId
     * @param periodName
     *
     * @return
     */
    public PeriodCO getPeriodBySetOfBooksIdAndName(Long setOfBooksId, String periodName){
        //  根据账套ID获取会计期Code
        SetOfBooks setOfBooks = setOfBooksService.selectById(setOfBooksId);
        //  根据会计期Code与租户定位一条会计期
        PeriodSet periodSet = new PeriodSet();
        periodSet.setPeriodSetCode(setOfBooks.getPeriodSetCode());
        periodSet.setTenantId(setOfBooks.getTenantId());
        PeriodSet target = periodSetService.selectOne(
                new EntityWrapper<PeriodSet>()
                        .eq("period_set_code", setOfBooks.getPeriodSetCode())
                        .eq("tenant_id", setOfBooks.getTenantId())
        );
        //  根据会计期ID与期间Name定位一条总账期间信息
        Periods periods = new Periods();
        periods.setPeriodSetId(target.getId());
        periods.setPeriodName(periodName);
        Periods result = periodsMapper.selectOne(periods);
        return mapperFacade.map(result,PeriodCO.class);
    }

    /**
     * 预算项目查询  根据账套ID与DateTime查询总账期间信息
     *
     * xiaoting.pan
     * @param setOfBooksId
     * @param dateTime
     *
     * @return
     */
    public PeriodCO getPeriodBysetOfBooksIdAndDateTime(Long setOfBooksId, String dateTime){
        //  根据账套ID获取会计期Code
        SetOfBooks setOfBooks = setOfBooksService.selectById(setOfBooksId);
        //  根据会计期Code与租户定位一条会计期
        PeriodSet periodSet = new PeriodSet();
        periodSet.setPeriodSetCode(setOfBooks.getPeriodSetCode());
        periodSet.setTenantId(setOfBooks.getTenantId());
        PeriodSet target = periodSetService.selectOne(
                new EntityWrapper<PeriodSet>()
                        .eq("period_set_code", setOfBooks.getPeriodSetCode())
                        .eq("tenant_id", setOfBooks.getTenantId())
        );
        //  根据会计期ID与期间Name定位一条总账期间信息
        Periods periods = periodsMapper.getPeriodBysetOfBooksIdAndDateTime(target.getId(),dateTime);
        return mapperFacade.map(periods,PeriodCO.class);
    }

    /**
     * 预算项目查询  根据账套ID查询打开的期间信息
     *
     * xiaoting.pan
     * @param setOfBooksId
     * @param page
     *
     * @return
     */
    public Page<Periods> findOpenPeriodsByBookID(Long setOfBooksId, Page<Periods> page){
        List<Periods> list = periodsMapper.findOpenPeriodsByBookID(setOfBooksId,page);
        if(CollectionUtils.isNotEmpty(list)){
            page.setRecords(list);
        }
        return page;
    }

    public Page<Periods> selectYearsBySetOfBooksId(Long setOfBooksId, Page<Periods> page){
        page.getRecords();
        //  根据账套ID获取会计期Code
        SetOfBooks setOfBooks = setOfBooksService.selectById(setOfBooksId);
        //  根据会计期Code与租户定位一条会计期
        PeriodSet periodSet = new PeriodSet();
        periodSet.setPeriodSetCode(setOfBooks.getPeriodSetCode());
        periodSet.setTenantId(setOfBooks.getTenantId());
        PeriodSet target = periodSetService.selectOne(
                new EntityWrapper<PeriodSet>()
                        .eq("period_set_code", setOfBooks.getPeriodSetCode())
                        .eq("tenant_id", setOfBooks.getTenantId())
        );
        List<Periods> list = periodsMapper.selectPage(page,new EntityWrapper<Periods>().setSqlSelect("period_year")
                                            .eq("period_set_id",target.getId())
                                            .eq("deleted",false)
                                            .groupBy("period_year")
        );
        if(CollectionUtils.isNotEmpty(list)){
            page.setRecords(list);
        }
        return page;
    }


    public Page<String> selectPeriodSetCodeBySetOfBooksId(Long setOfBooksId, Page<String> page){
        page.getRecords();
        //  根据账套ID获取会计期Code
        SetOfBooks setOfBooks = setOfBooksService.selectById(setOfBooksId);
        //  根据会计期Code与租户定位一条会计期
        PeriodSet periodSet = new PeriodSet();
        periodSet.setPeriodSetCode(setOfBooks.getPeriodSetCode());
        periodSet.setTenantId(setOfBooks.getTenantId());
        PeriodSet target = periodSetService.selectOne(
                new EntityWrapper<PeriodSet>()
                        .eq("period_set_code", setOfBooks.getPeriodSetCode())
                        .eq("tenant_id", setOfBooks.getTenantId())
        );
        List<String> list = periodsMapper.selectPage(page,
            new EntityWrapper<Periods>()
                .eq("period_set_id",target.getId())
                .eq("deleted",false)
        ).stream().map(Periods::getPeriodName).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(list)){
            page.setRecords(list);
        }
        return page;
    }



    /*期间name+账套id,判断期间是否打开*/
    public Boolean ifOpenPeriod(String periodName,Long setOfBooksId){
        List<PeriodStatus> list = periodStatusService.selectList(new EntityWrapper<PeriodStatus>()
                                            .eq("set_of_books_id",setOfBooksId)
                                            .eq("period_name",periodName)
                                            .eq("deleted",false)

        );
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(RespCode.OBJECT_NOT_FOUND);
        }
        return list.get(0).getPeriodStatusCode().equals("O");
    }

    /**
     * 通过账套获取已定义的期间年度
     * @param setOfBooksId
     * @return
     */
    public List<Integer> getPeriodYearsForSetOfBooksId(Long setOfBooksId){
        //  根据账套ID获取会计期Code
        SetOfBooks setOfBooks = setOfBooksService.selectById(setOfBooksId);
        //  根据会计期Code与租户定位一条会计期
        PeriodSet periodSet = new PeriodSet();
        periodSet.setPeriodSetCode(setOfBooks.getPeriodSetCode());
        periodSet.setTenantId(setOfBooks.getTenantId());
        periodSet = periodSetService.selectOne(
                new EntityWrapper<PeriodSet>()
                        .eq("period_set_code", setOfBooks.getPeriodSetCode())
                        .eq("tenant_id", setOfBooks.getTenantId())
        );
        return periodsMapper.getPeriodYearsForPeriodSetId(periodSet.getId());
    }

    /**
     * 预算项目查询 通过账套ID期间Name查询总账期间信息
     * @param setOfBooksId
     * @param periodName
     * @param periodYear
     * @param tenantId
     * @return
     */
    public List<Periods> findPeriodsByIdAndName(Long setOfBooksId, String periodName, Integer periodYear, Long tenantId) {
        return periodsMapper.findPeriodsByIdAndName(setOfBooksId,periodName,periodYear,tenantId);
    }
}
