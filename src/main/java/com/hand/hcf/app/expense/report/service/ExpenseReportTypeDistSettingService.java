package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.ResponsibilityCenterCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDistRange;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDistSetting;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeDistSettingRequestDTO;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeDistSettingMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/1
 */
@Service
@Transactional
@AllArgsConstructor
public class ExpenseReportTypeDistSettingService extends BaseService<ExpenseReportTypeDistSettingMapper,ExpenseReportTypeDistSetting> {
    private final ExpenseReportTypeDistSettingMapper expenseReportTypeDistSettingMapper;

    private final ExpenseReportTypeDistRangeService expenseReportTypeDistRangeService;

    private final OrganizationService organizationService;

    /**
     * 单个新增 报账单类型分摊设置
     * @param expenseReportTypeDistSettingRequestDTO
     * @return
     */
    public ExpenseReportTypeDistSetting createExpenseReportTypeDistSetting(ExpenseReportTypeDistSettingRequestDTO expenseReportTypeDistSettingRequestDTO){
        ExpenseReportTypeDistSetting expenseReportTypeDistSetting = expenseReportTypeDistSettingRequestDTO.getExpenseReportTypeDistSetting();

        if (expenseReportTypeDistSetting.getId() != null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIST_SETTING_ALREADY_EXISTS);
        }
        expenseReportTypeDistSettingMapper.insert(expenseReportTypeDistSetting);

        //判断公司范围
        if ( expenseReportTypeDistSetting.getCompanyDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getCompanyDistRange())){
            if (expenseReportTypeDistSettingRequestDTO.getCompanyIdList().size() > 0){
                List<Long> companyIdList = expenseReportTypeDistSettingRequestDTO.getCompanyIdList();
                List<ExpenseReportTypeDistRange> expenseReportTypeDistRangeList = new ArrayList<>();
                companyIdList.stream().forEach(companyId -> {
                    ExpenseReportTypeDistRange expenseReportTypeDistRange = ExpenseReportTypeDistRange.builder()
                            .reportTypeId(expenseReportTypeDistSetting.getReportTypeId())
                            .distDimension("COMPANY")
                            .valueId(companyId)
                            .build();
                    expenseReportTypeDistRangeList.add(expenseReportTypeDistRange);
                });
                expenseReportTypeDistRangeService.insertBatch(expenseReportTypeDistRangeList);
            }
        }

        //判断部门范围
        if ( expenseReportTypeDistSetting.getDepartmentDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getDepartmentDistRange())){
            if (expenseReportTypeDistSettingRequestDTO.getDepartmentIdList().size() > 0){
                List<Long> departmentIdList = expenseReportTypeDistSettingRequestDTO.getDepartmentIdList();
                List<ExpenseReportTypeDistRange> expenseReportTypeDistRangeList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId -> {
                    ExpenseReportTypeDistRange expenseReportTypeDistRange = ExpenseReportTypeDistRange.builder()
                            .reportTypeId(expenseReportTypeDistSetting.getReportTypeId())
                            .distDimension("DEPARTMENT")
                            .valueId(departmentId)
                            .build();
                    expenseReportTypeDistRangeList.add(expenseReportTypeDistRange);
                });
                expenseReportTypeDistRangeService.insertBatch(expenseReportTypeDistRangeList);
            }
        }

        //判断责任中心范围
        if ( expenseReportTypeDistSetting.getResCenterDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getResDistRange())){
            if (expenseReportTypeDistSettingRequestDTO.getResIdList().size() > 0){
                List<Long> resIdList = expenseReportTypeDistSettingRequestDTO.getResIdList();
                List<ExpenseReportTypeDistRange> expenseReportTypeDistRangeList = new ArrayList<>();
                resIdList.stream().forEach(resId -> {
                    ExpenseReportTypeDistRange expenseReportTypeDistRange = ExpenseReportTypeDistRange.builder()
                            .reportTypeId(expenseReportTypeDistSetting.getReportTypeId())
                            .distDimension("RESPONSIBILITY_CENTER")
                            .valueId(resId)
                            .build();
                    expenseReportTypeDistRangeList.add(expenseReportTypeDistRange);
                });
                expenseReportTypeDistRangeService.insertBatch(expenseReportTypeDistRangeList);
            }
        }

        return expenseReportTypeDistSetting;
    }

    /**
     * 单个修改 报账单类型分摊设置
     * @param expenseReportTypeDistSettingRequestDTO
     * @return
     */
    public ExpenseReportTypeDistSetting updateExpenseReportTypeDistSetting(ExpenseReportTypeDistSettingRequestDTO expenseReportTypeDistSettingRequestDTO){
        ExpenseReportTypeDistSetting expenseReportTypeDistSetting = expenseReportTypeDistSettingRequestDTO.getExpenseReportTypeDistSetting();

        ExpenseReportTypeDistSetting oldExpenseReportTypeDistSetting = expenseReportTypeDistSettingMapper.selectById(expenseReportTypeDistSetting);
        if (expenseReportTypeDistSetting.getId() == null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIST_SETTING_NOT_EXIST);
        }
        expenseReportTypeDistSettingMapper.updateAllColumnById(expenseReportTypeDistSetting);

        //判断公司范围
        if ( oldExpenseReportTypeDistSetting.getCompanyDistFlag() && "CUSTOM_RANGE".equals(oldExpenseReportTypeDistSetting.getCompanyDistRange()) ){
            List<Long> expenseReportTypeDistRangeIdList = expenseReportTypeDistRangeService.selectList(
                    new EntityWrapper<ExpenseReportTypeDistRange>()
                            .eq("report_type_id",expenseReportTypeDistSetting.getReportTypeId())
                            .eq("dist_dimension","COMPANY")
            ).stream().map(ExpenseReportTypeDistRange::getId).collect(Collectors.toList());
            expenseReportTypeDistRangeService.deleteBatchIds(expenseReportTypeDistRangeIdList);
        }
        if ( expenseReportTypeDistSetting.getCompanyDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getCompanyDistRange())){
            if (expenseReportTypeDistSettingRequestDTO.getCompanyIdList().size() > 0){
                List<Long> companyIdList = expenseReportTypeDistSettingRequestDTO.getCompanyIdList();
                List<ExpenseReportTypeDistRange> expenseReportTypeDistRangeList = new ArrayList<>();
                companyIdList.stream().forEach(companyId -> {
                    ExpenseReportTypeDistRange expenseReportTypeDistRange = ExpenseReportTypeDistRange.builder()
                            .reportTypeId(expenseReportTypeDistSetting.getReportTypeId())
                            .distDimension("COMPANY")
                            .valueId(companyId)
                            .build();
                    expenseReportTypeDistRangeList.add(expenseReportTypeDistRange);
                });
                expenseReportTypeDistRangeService.insertBatch(expenseReportTypeDistRangeList);
            }
        }

        //判断部门范围
        if ( oldExpenseReportTypeDistSetting.getDepartmentDistFlag() && "CUSTOM_RANGE".equals(oldExpenseReportTypeDistSetting.getDepartmentDistRange()) ){
            List<Long> expenseReportTypeDistRangeIdList = expenseReportTypeDistRangeService.selectList(
                    new EntityWrapper<ExpenseReportTypeDistRange>()
                            .eq("report_type_id",expenseReportTypeDistSetting.getReportTypeId())
                            .eq("dist_dimension","DEPARTMENT")
            ).stream().map(ExpenseReportTypeDistRange::getId).collect(Collectors.toList());
            expenseReportTypeDistRangeService.deleteBatchIds(expenseReportTypeDistRangeIdList);
        }
        if ( expenseReportTypeDistSetting.getDepartmentDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getDepartmentDistRange())){
            if (expenseReportTypeDistSettingRequestDTO.getDepartmentIdList().size() > 0){
                List<Long> departmentIdList = expenseReportTypeDistSettingRequestDTO.getDepartmentIdList();
                List<ExpenseReportTypeDistRange> expenseReportTypeDistRangeList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId -> {
                    ExpenseReportTypeDistRange expenseReportTypeDistRange = ExpenseReportTypeDistRange.builder()
                            .reportTypeId(expenseReportTypeDistSetting.getReportTypeId())
                            .distDimension("DEPARTMENT")
                            .valueId(departmentId)
                            .build();
                    expenseReportTypeDistRangeList.add(expenseReportTypeDistRange);
                });
                expenseReportTypeDistRangeService.insertBatch(expenseReportTypeDistRangeList);
            }
        }

        //判断责任中心范围
        if ( oldExpenseReportTypeDistSetting.getResCenterDistFlag() && "CUSTOM_RANGE".equals(oldExpenseReportTypeDistSetting.getResDistRange())  ){
            List<Long> expenseReportTypeDistRangeIdList = expenseReportTypeDistRangeService.selectList(
                    new EntityWrapper<ExpenseReportTypeDistRange>()
                            .eq("report_type_id",expenseReportTypeDistSetting.getReportTypeId())
                            .eq("dist_dimension","RESPONSIBILITY_CENTER")
            ).stream().map(ExpenseReportTypeDistRange::getId).collect(Collectors.toList());
            expenseReportTypeDistRangeService.deleteBatchIds(expenseReportTypeDistRangeIdList);
        }
        if ( expenseReportTypeDistSetting.getResCenterDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getResDistRange())){
            if (expenseReportTypeDistSettingRequestDTO.getResIdList().size() > 0){
                List<Long> resIdList = expenseReportTypeDistSettingRequestDTO.getResIdList();
                List<ExpenseReportTypeDistRange> expenseReportTypeDistRangeList = new ArrayList<>();
                resIdList.stream().forEach(resId -> {
                    ExpenseReportTypeDistRange expenseReportTypeDistRange = ExpenseReportTypeDistRange.builder()
                            .reportTypeId(expenseReportTypeDistSetting.getReportTypeId())
                            .distDimension("RESPONSIBILITY_CENTER")
                            .valueId(resId)
                            .build();
                    expenseReportTypeDistRangeList.add(expenseReportTypeDistRange);
                });
                expenseReportTypeDistRangeService.insertBatch(expenseReportTypeDistRangeList);
            }
        }


        return expenseReportTypeDistSetting;
    }

    /**
     * 根据报账单类型id查询 报账单类型分摊设置
     * @param id
     * @return
     */
    public ExpenseReportTypeDistSettingRequestDTO getExpenseReportTypeDistSettingById(Long id){
        ExpenseReportTypeDistSettingRequestDTO expenseReportTypeDistSettingRequestDTO = new ExpenseReportTypeDistSettingRequestDTO();

        List<ExpenseReportTypeDistSetting> list = expenseReportTypeDistSettingMapper.selectList(
                new EntityWrapper<ExpenseReportTypeDistSetting>().eq("report_type_id",id)
        );
        if (list.size() > 0){
            ExpenseReportTypeDistSetting expenseReportTypeDistSetting = list.get(0);
            //返回默认公司代码、名称
            if ( expenseReportTypeDistSetting.getCompanyDefaultId() != null ){
                CompanyCO companyById = organizationService.getCompanyById(expenseReportTypeDistSetting.getCompanyDefaultId());
                if (companyById != null){
                    expenseReportTypeDistSetting.setCompanyCode(companyById.getCompanyCode());
                    expenseReportTypeDistSetting.setCompanyName(companyById.getName());
                }
            }
            //返回默认部门代码、名称
            if ( expenseReportTypeDistSetting.getDepartmentDefaultId() != null ){
                DepartmentCO departmentById = organizationService.getDepartmentById(expenseReportTypeDistSetting.getDepartmentDefaultId());
                if (departmentById != null){
                    expenseReportTypeDistSetting.setCompanyCode(departmentById.getDepartmentCode());
                    expenseReportTypeDistSetting.setCompanyName(departmentById.getName());
                }
            }
            //返回默认责任中心代码、名称
            if ( expenseReportTypeDistSetting.getResDefaultId() != null ){
                ResponsibilityCenterCO responsibilityCenterById = organizationService.getResponsibilityCenterById(expenseReportTypeDistSetting.getResDefaultId());
                if (responsibilityCenterById != null){
                    expenseReportTypeDistSetting.setCompanyCode(responsibilityCenterById.getResponsibilityCenterCode());
                    expenseReportTypeDistSetting.setCompanyName(responsibilityCenterById.getResponsibilityCenterName());
                }
            }

            expenseReportTypeDistSettingRequestDTO.setExpenseReportTypeDistSetting(expenseReportTypeDistSetting);

            //返回自定义公司id集合
            if (expenseReportTypeDistSetting.getCompanyDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getCompanyDistRange())){
                List<Long> companyIdList = expenseReportTypeDistRangeService.selectList(
                        new EntityWrapper<ExpenseReportTypeDistRange>()
                                .eq("report_type_id",id)
                                .eq("dist_dimension","COMPANY")
                ).stream().map(ExpenseReportTypeDistRange::getValueId).collect(Collectors.toList());
                expenseReportTypeDistSettingRequestDTO.setCompanyIdList(companyIdList);
                //返回自定义公司集合
                expenseReportTypeDistSettingRequestDTO.setCompanyCOList(organizationService.listCompaniesByIds(companyIdList));
            }
            //返回自定义部门id集合
            if (expenseReportTypeDistSetting.getDepartmentDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getDepartmentDistRange())){
                List<Long> departmentIdList = expenseReportTypeDistRangeService.selectList(
                        new EntityWrapper<ExpenseReportTypeDistRange>()
                                .eq("report_type_id",id)
                                .eq("dist_dimension","DEPARTMENT")
                ).stream().map(ExpenseReportTypeDistRange::getValueId).collect(Collectors.toList());
                expenseReportTypeDistSettingRequestDTO.setDepartmentIdList(departmentIdList);
                //返回自定义部门集合
                expenseReportTypeDistSettingRequestDTO.setDepartmentCOList(organizationService.listDepartmentsByIds(departmentIdList));
            }
            //返回自定义责任中心id集合
            if (expenseReportTypeDistSetting.getResCenterDistFlag() && "CUSTOM_RANGE".equals(expenseReportTypeDistSetting.getResDistRange())){
                List<Long> resIdList = expenseReportTypeDistRangeService.selectList(
                        new EntityWrapper<ExpenseReportTypeDistRange>()
                                .eq("report_type_id",id)
                                .eq("dist_dimension","RESPONSIBILITY_CENTER")
                ).stream().map(ExpenseReportTypeDistRange::getValueId).collect(Collectors.toList());
                expenseReportTypeDistSettingRequestDTO.setResIdList(resIdList);
                //返回自定义责任中心集合
                expenseReportTypeDistSettingRequestDTO.setResponsibilityCenterCOList(organizationService.getResponsibilityCenterByIdList(resIdList));
            }

        }

        return expenseReportTypeDistSettingRequestDTO;
    }

    /**
     * 根据 “公司分摊范围” 分页查询公司
     * @param companyDistRange
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public Page<CompanyCO> queryCompanyByCompanyDistRange(String companyDistRange, String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, Page page){
        Page<CompanyCO> result = new Page<>();

        if (companyDistRange != null && companyDistRange != ""){
            //查询 账套下所有公司  或者  查询 自定义范围公司(账套下所有启用公司)
            if ("ALL_COM_IN_SOB".equals(companyDistRange) || "CUSTOM_RANGE".equals(companyDistRange)){
                result = organizationService.pageCompanyByCond(OrgInformationUtil.getCurrentSetOfBookId(), companyCode, companyName, companyCodeFrom, companyCodeTo, null, page);
            }
            //查询 本公司及下属公司
            if ("CURRENT_COM_&_SUB_COM".equals(companyDistRange)){
                result = organizationService.pageChildrenCompaniesByCondition(OrgInformationUtil.getCurrentCompanyId(),
                        true,
                        companyCode,
                        companyCodeFrom,
                        companyCodeTo,
                        companyName,
                        null,
                        page);
            }
            //查询 下属公司
            if ("SUB_COM".equals(companyDistRange)) {
                result = organizationService.pageChildrenCompaniesByCondition(OrgInformationUtil.getCurrentCompanyId(),
                        false,
                        companyCode,
                        companyCodeFrom,
                        companyCodeTo,
                        companyName,
                        null,
                        page);
            }

        }

        return result;
    }

    /**
     * 根据 “部门分摊范围” 分页查询部门
     * @param departmentDistRange
     * @param departmentCode
     * @param departmentName
     * @param departmentCodeFrom
     * @param departmentCodeTo
     * @param page
     * @return
     */
    public Page<DepartmentCO> queryDepartmentByDepartmentDistRange(String departmentDistRange,String departmentCode,String departmentName,String departmentCodeFrom,String departmentCodeTo,Page page){
        Page<DepartmentCO> result = new Page<>();

        if (departmentDistRange != null && departmentDistRange != ""){
            //查询 租户下所有部门  或者  查询 自定义范围部门(租户下所有启用)
            if ("ALL_DEP_IN_TENANT".equals(departmentDistRange) || "CUSTOM_RANGE".equals(departmentDistRange)){
                //等三方接口
                result = organizationService.pageDepartmentByTenantId(departmentCode, departmentName, departmentCodeFrom, departmentCodeTo, page);
            }
            //查询 账套下所有部门
            if ("ALL_DEP_IN_SOB".equals(departmentDistRange)){
                //等公司与部门建立联系之后再补充代码
            }
            //查询 公司下所有部门
            if ("ALL_DEP_IN_COM".equals(departmentDistRange)){
                //等公司与部门建立联系之后再补充代码
            }
        }

        return result;
    }

    /**
     * 根据费用类型获取公司范围
     * @param expenseTypeId
     * @param companyCode
     * @param companyName
     * @param page
     * @return
     */
    public Page<CompanyCO> queryCompanyByExpenseTypeId(Long expenseTypeId, Long companyId, String companyCode, String companyName, Page page){
        Page<CompanyCO> companyCOPage = new Page<>();
        ExpenseReportTypeDistSetting reportTypeDistSetting = selectOne(new EntityWrapper<ExpenseReportTypeDistSetting>().eq("report_type_id", expenseTypeId));
        if(reportTypeDistSetting.getCompanyDistFlag() != null) {
            if (reportTypeDistSetting.getCompanyDistFlag()) {
                String companyDistRange = reportTypeDistSetting.getCompanyDistRange();
                //查询 账套下所有公司
                if ("ALL_COM_IN_SOB".equals(companyDistRange)) {
                    return organizationService.pageCompanyByCond(OrgInformationUtil.getCurrentSetOfBookId(), companyCode, companyName, null, null, null, page);
                    //查询 本公司及下属公司
                } else if ("CURRENT_COM_&_SUB_COM".equals(companyDistRange)) {
                    return organizationService.pageChildrenCompaniesByCondition(OrgInformationUtil.getCurrentCompanyId(),
                            false,
                            companyCode,
                            null,
                            null,
                            companyName,
                            null,
                            page);
                    //下属公司
                } else if ("SUB_COM".equals(companyDistRange)) {
                    return organizationService.pageChildrenCompaniesByCondition(OrgInformationUtil.getCurrentCompanyId(),
                            true,
                            companyCode,
                            null,
                            null,
                            companyName,
                            null,
                            page);
                    // 自定义取值范围
                } else if ("CUSTOM_RANGE".equals(companyDistRange)) {
                    List<Long> collect = expenseReportTypeDistRangeService.selectList(
                            new EntityWrapper<ExpenseReportTypeDistRange>().eq("report_type_id", expenseTypeId)
                                    .eq("dist_dimension", "COMPANY")).stream().map(ExpenseReportTypeDistRange::getValueId).collect(Collectors.toList());
                    return organizationService.pageBySetOfBooksIdConditionByIds(OrgInformationUtil.getCurrentSetOfBookId(),
                            companyCode,
                            null,
                            null,
                            companyCode, page,
                            collect);
                }
            }else {
                //不参与分摊：获取当前的单据头公司信息
                if(companyId != null){
                    CompanyCO companyCO = organizationService.getCompanyById(companyId);
                    companyCOPage.setRecords(Collections.singletonList(companyCO));
                    return companyCOPage;
                }
            }
        }
        return new Page<>();
    }

    /**
     * 根据费用类型获取部门范围
     * @param expenseTypeId
     * @param departmentCode
     * @param departmentName
     * @param page
     * @return
     */
    public Page<DepartmentCO> queryDepartmentByExpenseTypeId(Long expenseTypeId,Long departmentId, String departmentCode, String departmentName, Page page){
        Page<DepartmentCO> result = new Page<>();
        ExpenseReportTypeDistSetting reportTypeDistSetting = selectOne(new EntityWrapper<ExpenseReportTypeDistSetting>().eq("report_type_id", expenseTypeId));
        if(reportTypeDistSetting.getDepartmentDistFlag() != null) {
           if (reportTypeDistSetting.getDepartmentDistFlag()) {
               String departmentDistRange = reportTypeDistSetting.getDepartmentDistRange();
               if ("ALL_DEP_IN_TENANT".equals(departmentDistRange)) {
                   //等三方接口
                   return organizationService.pageDepartmentByTenantId(departmentCode, departmentName, null, null, page);
                   //查询 账套下所有部门
               } else if ("ALL_DEP_IN_SOB".equals(departmentDistRange)) {
                   //等公司与部门建立联系之后再补充代码
                   //查询 公司下所有部门
               } else if ("ALL_DEP_IN_COM".equals(departmentDistRange)) {
                   //等公司与部门建立联系之后再补充代码
                   // 自定义范围
               } else if ("CUSTOM_RANGE".equals(departmentDistRange)) {
                   List<Long> collect = expenseReportTypeDistRangeService.selectList(
                           new EntityWrapper<ExpenseReportTypeDistRange>().eq("report_type_id", expenseTypeId)
                                   .eq("dist_dimension", "DEPARTMENT")).stream().map(ExpenseReportTypeDistRange::getValueId).collect(Collectors.toList());
                   organizationService.pageDepartmentsByCond(departmentCode,
                           null,
                           null,
                           departmentName,
                           collect,
                           null,
                           page);
               }
           }else {
               //不参与分摊:获取当前的单据头部门信息
               if (departmentId != null) {
                   DepartmentCO departmentCO = organizationService.getDepartmentById(departmentId);
                   result.setRecords(Collections.singletonList(departmentCO));
               }
           }
       }
        return result;
    }

    /**
     * 根据费用类型获取责任中心范围
     * @param expenseTypeId
     * @param responsibilityCenterCode
     * @param responsibilityCenterCodeName
     * @param page
     * @return
     */
    public Page<ResponsibilityCenterCO> queryResponsibilityCenterByExpenseTypeId(Long expenseTypeId,
                                                                                 Long companyId,
                                                                                 Long departmentId,
                                                                                 String responsibilityCenterCode,
                                                                                 String responsibilityCenterCodeName,
                                                                                 Page page){
        ExpenseReportTypeDistSetting reportTypeDistSetting = selectOne(new EntityWrapper<ExpenseReportTypeDistSetting>().eq("report_type_id", expenseTypeId));
        if(reportTypeDistSetting.getResCenterDistFlag() != null && reportTypeDistSetting.getResCenterDistFlag()){
            String respCenterDistRange = reportTypeDistSetting.getResDistRange();
            //账套下所有的责任中心
            if ("ALL_RES_CENTER_IN_SOB".equals(respCenterDistRange)){
                return organizationService.pageByResponsibilityCenterByCond(OrgInformationUtil.getCurrentSetOfBookId(),
                        responsibilityCenterCode,
                        null,
                        null,
                        responsibilityCenterCodeName,
                        null,
                        null,
                        null,
                        page);
            // 部门对应的责任中心
            }else if ("DEP_RES_CENTER".equals(respCenterDistRange)){
                if(companyId != null && departmentId != null){
                    ResponsibilityCenterCO defaultResponsibilityCenter = organizationService.getDefaultResponsibilityCenter(companyId, departmentId);
                    if(defaultResponsibilityCenter != null){
                        page.setTotal(1);
                        page.setRecords(Arrays.asList(defaultResponsibilityCenter));
                        return page;
                    }
                }
            // 自定义范围
            }else if ("CUSTOM_RANGE".equals(respCenterDistRange)){
                List<Long> collect = expenseReportTypeDistRangeService.selectList(
                        new EntityWrapper<ExpenseReportTypeDistRange>().eq("report_type_id", expenseTypeId)
                                .eq("dist_dimension", "RESPONSIBILITY_CENTER")).stream().map(ExpenseReportTypeDistRange::getValueId).collect(Collectors.toList());
                return organizationService.pageByResponsibilityCenterByCond(OrgInformationUtil.getCurrentSetOfBookId(),
                        responsibilityCenterCode,
                        null,
                        null,
                        responsibilityCenterCodeName,
                        null,
                        null,
                        collect,
                        page);
            }
        }

        return new Page<>();
    }
}
