package com.hand.hcf.app.ant.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.ant.accrual.persistence.AccrualExpenseTypeMapper;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.FormTypeEnum;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualCompany;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualTypeAssignDepartment;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualTypeAssignUserGroup;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualCompanyService;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualTypeAssignDepartmentService;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualTypeAssignUserGroupService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.AuthorizeControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/21
 */
@Service
@Transactional
public class AccrualExpenseTypeService extends BaseService<AccrualExpenseTypeMapper,ExpenseAccrualType> {

     @Autowired
     private AccrualExpenseTypeMapper accrualExpenseTypeMapper;

    @Autowired
    private OrganizationService organizationService;

    //人员组
    @Autowired
    private ExpenseAccrualTypeAssignUserGroupService expenseAccrualTypeAssignUserGroupService;

    @Autowired
    private AuthorizeControllerImpl authorizeClient;

    //公司
    @Autowired
    private ExpenseAccrualCompanyService expenseAccrualCompanyService;

    //部门
    @Autowired
    private ExpenseAccrualTypeAssignDepartmentService departmentService;

    /**
     * 获取当前用户有权限的费用预提单类型
     * @param isAuth
     * @return
     */
    public List<ExpenseAccrualType> getCurrentUserExpenseAccrualType(Boolean isAuth){
        Long currentSetOfBookId = OrgInformationUtil.getCurrentSetOfBookId();
        Long currentCompanyId = OrgInformationUtil.getCurrentCompanyId();
        Long currentDepartmentId = OrgInformationUtil.getCurrentDepartmentId();
        Long currentUserId = OrgInformationUtil.getCurrentUserId();
        List<ExpenseAccrualType> expenseAccrualTypeList  = accrualExpenseTypeMapper.getCurrentUserExpenseAccrualType(currentDepartmentId, currentCompanyId, currentSetOfBookId);
        List<ExpenseAccrualType> userGroupTypes = expenseAccrualTypeList.stream().filter(e -> "1003".equals(e.getVisibleUserScope())).collect(toList());
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(userGroupTypes)){
            // 筛选出不符合员工组条件的类型
            List<ExpenseAccrualType> notMatchUserGroupTypes = userGroupTypes.stream().filter(userGroupType -> {
                List<ExpenseAccrualTypeAssignUserGroup> userGroups = expenseAccrualTypeAssignUserGroupService.selectList(
                        new EntityWrapper<ExpenseAccrualTypeAssignUserGroup>()
                        .eq("exp_accrual_type_id", userGroupType.getId()));
                JudgeUserCO judgeUserCO = new JudgeUserCO();
                judgeUserCO.setIdList(userGroups.stream().map(ExpenseAccrualTypeAssignUserGroup::getUserGroupId).collect(toList()));
                judgeUserCO.setUserId(currentUserId);
                Boolean isExists = organizationService.judgeUserInUserGroups(judgeUserCO);
                return !isExists;
            }).collect(toList());
            expenseAccrualTypeList.removeAll(notMatchUserGroupTypes);
        }
        if (isAuth != null && isAuth) {
            expenseAccrualTypeList.addAll(listAuthorizedExpenseReportType());
            //根据ID去重
            expenseAccrualTypeList = expenseAccrualTypeList.stream().collect(
                    collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(ExpenseAccrualType::getId))), ArrayList::new)
            );
        }
        return expenseAccrualTypeList;
    }

    /**
     * 获取当前用户被授权的单据类型
     * @return
     */
    public List<ExpenseAccrualType> listAuthorizedExpenseReportType(){
        List<ExpenseAccrualType> accrualReportTypeList = new ArrayList<>();

        List<FormAuthorizeCO> formAuthorizeCOList = authorizeClient.listFormAuthorizeByDocumentCategoryAndUserId(FormTypeEnum.BULLETIN_BILL.getCode(), OrgInformationUtil.getCurrentUserId());

        for(FormAuthorizeCO item : formAuthorizeCOList) {
            OrganizationUserCO contactCO = new OrganizationUserCO();
            if (item.getMandatorId() != null) {
                contactCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
            }
            List<Long> typeIdList = expenseAccrualCompanyService.selectList(
                    new EntityWrapper<ExpenseAccrualCompany>()
                            .eq(item.getCompanyId() != null, "company_id", item.getCompanyId())
                            .eq(contactCO.getCompanyId() != null, "company_id", contactCO.getCompanyId())
                            .eq("enable_flag",true)
            ).stream().map(ExpenseAccrualCompany::getExpAccrualTypeId).collect(Collectors.toList());
            if (typeIdList.size() == 0) {
                continue;
            }
            List<ExpenseAccrualType> accrualReportTypes = this.selectList(
                    new EntityWrapper<ExpenseAccrualType>()
                            .in(typeIdList.size() != 0, "id", typeIdList)
                            .eq(item.getFormId() != null, "id", item.getFormId())
                            .eq("enable_flag", true));

            accrualReportTypes = accrualReportTypes.stream().filter(accrualReportType -> {
                // 全部人员
                if ("1001".equals(accrualReportType.getVisibleUserScope())){
                    return true;
                    // 部门
                }else if("1002".equals(accrualReportType.getVisibleUserScope())){
                    List<Long> deparmentIds = departmentService.selectList(new EntityWrapper<ExpenseAccrualTypeAssignDepartment>()
                            .eq("exp_accrual_type_id", accrualReportType.getId())).stream().map(ExpenseAccrualTypeAssignDepartment::getDepartmentId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(deparmentIds)) {
                        if (item.getMandatorId() != null) {
                            OrganizationUserCO userCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
                            if (!deparmentIds.contains(userCO.getDepartmentId())){
                                return false;
                            }
                        }
                        if (item.getUnitId() != null && !deparmentIds.contains(item.getUnitId())) {
                            return false;
                        }
                    }
                    // 人员组
                }else if("1003".equals(accrualReportType.getVisibleUserScope())){
                    List<Long> userGroupIds = expenseAccrualTypeAssignUserGroupService.selectList(new EntityWrapper<ExpenseAccrualTypeAssignUserGroup>()
                            .eq("exp_accrual_type_id", accrualReportType.getId())).stream().map(ExpenseAccrualTypeAssignUserGroup::getUserGroupId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(userGroupIds)) {
                        if (item.getMandatorId() != null) {
                            JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(userGroupIds).userId(item.getMandatorId()).build();
                            if (!organizationService.judgeUserInUserGroups(judgeUserCO)) {
                                return false;
                            }
                        }
                        if (item.getUnitId() != null){
                            List<Long> userIds = organizationService.listUsersByDepartmentId(item.getUnitId()).stream().map(ContactCO::getId).collect(Collectors.toList());
                            for(Long e : userIds){
                                JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(userGroupIds).userId(e).build();
                                if (!organizationService.judgeUserInUserGroups(judgeUserCO)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                }
                return true;
            }).collect(Collectors.toList());
            accrualReportTypeList.addAll(accrualReportTypes);
        }
        return accrualReportTypeList;
    }

    /**
     * 根据报账单类型ID获取所有有权限创建该单据的人员
     * @param id
     * @return
     */
    public List<ContactCO> listUsersByAccuralType(Long id, String userCode, String userName, Page queryPage) {
        List<ContactCO> userCOList = new ArrayList<>();

        ExpenseAccrualType expenseAccrualType = this.selectById(id);
        if (expenseAccrualType == null){
            return userCOList;
        }

        List<Long> companyIdList = expenseAccrualCompanyService.selectList(
                new EntityWrapper<ExpenseAccrualCompany>()
                        .eq("enable_flag", true)
                        .eq("exp_accrual_type_id", id)
        ).stream().map(ExpenseAccrualCompany::getCompanyId).collect(toList());
        if (companyIdList.size() == 0){
            return userCOList;
        }

        List<Long> departmentIdList = null;
        List<Long> userGroupIdList = null;

        // 部门
        if ("1002".equals(expenseAccrualType.getVisibleUserScope())){
            departmentIdList = departmentService.selectList(new EntityWrapper<ExpenseAccrualTypeAssignDepartment>()
                    .eq("exp_accrual_type_id", expenseAccrualType.getId())).stream().map(ExpenseAccrualTypeAssignDepartment::getDepartmentId).collect(Collectors.toList());
        }
        // 人员组
        if ("1003".equals(expenseAccrualType.getVisibleUserScope())){
            userGroupIdList = expenseAccrualTypeAssignUserGroupService.selectList(new EntityWrapper<ExpenseAccrualTypeAssignUserGroup>()
                    .eq("exp_accrual_type_id", expenseAccrualType.getId())).stream().map(ExpenseAccrualTypeAssignUserGroup::getUserGroupId).collect(Collectors.toList());
        }

        AuthorizeQueryCO queryCO = AuthorizeQueryCO
                .builder()
                .documentCategory(FormTypeEnum.BULLETIN_BILL.getCode())
                .formTypeId(id)
                .companyIdList(companyIdList)
                .departmentIdList(departmentIdList)
                .userGroupIdList(userGroupIdList)
                .currentUserId(OrgInformationUtil.getCurrentUserId())
                .build();
        Page<ContactCO> contactCOPage = authorizeClient.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, queryPage.getCurrent() - 1, queryPage.getSize());
        queryPage.setTotal(contactCOPage.getTotal());
        userCOList = contactCOPage.getRecords();

        return userCOList;
    }

}
