package com.hand.hcf.app.ant.accrualExpense.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/21
 */
@Service
@Transactional
public class AccrualExpenseTypeService extends BaseService<ExpenseReportTypeMapper,ExpenseReportType> {

    /**
     * 获取当前用户有权限的单据类型
     * @return
     */
//    public List<ExpenseReportType> getCurrentUserExpenseReportType(Boolean isAuth){
//        Long currentSetOfBookId = OrgInformationUtil.getCurrentSetOfBookId();
//        Long currentCompanyId = OrgInformationUtil.getCurrentCompanyId();
//        Long currentDepartmentId = OrgInformationUtil.getCurrentDepartmentId();
//        Long currentUserId = OrgInformationUtil.getCurrentUserId();
//        List<ExpenseReportType> currentUserExpenseReportType = baseMapper.getCurrentUserExpenseReportType(currentDepartmentId, currentCompanyId, currentSetOfBookId);
//        List<ExpenseReportType> useUserGroupTypes = currentUserExpenseReportType.stream().filter(e -> "1003".equals(e.getApplyEmployee())).collect(toList());
//        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(useUserGroupTypes)){
//            // 筛选出不符合员工组条件的类型
//            List<ExpenseReportType> notMatchUserGroupTypes = useUserGroupTypes.stream().filter(useUserGroupType -> {
//                List<ExpenseReportTypeUserGroup> userGroups = expenseReportTypeUserGroupService.selectList(new EntityWrapper<ExpenseReportTypeUserGroup>()
//                        .eq("report_type_id", useUserGroupType.getId()));
//                JudgeUserCO judgeUserCO = new JudgeUserCO();
//                judgeUserCO.setIdList(userGroups.stream().map(ExpenseReportTypeUserGroup::getUserGroupId).collect(toList()));
//                judgeUserCO.setUserId(currentUserId);
//                Boolean isExists = organizationService.judgeUserInUserGroups(judgeUserCO);
//                return !isExists;
//            }).collect(toList());
//            currentUserExpenseReportType.removeAll(notMatchUserGroupTypes);
//        }
//        if (isAuth != null && isAuth) {
//            currentUserExpenseReportType.addAll(listAuthorizedExpenseReportType());
//            //根据ID去重
//            currentUserExpenseReportType = currentUserExpenseReportType.stream().collect(
//                    collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(ExpenseReportType::getId))), ArrayList::new)
//            );
//        }
//        return currentUserExpenseReportType;
//    }

}
