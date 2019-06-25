package com.hand.hcf.app.ant.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.ant.accrual.domain.AccruedReimburse;
import com.hand.hcf.app.ant.accrual.persistence.AccrualExpenseTypeMapper;
import com.hand.hcf.app.ant.accrual.persistence.AccruedExpensesReimbuseMapper;
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
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

/**
 * @description: 预提报销单service
 * @version: 1.0
 * @author: dazhuang.xie@hand-china.com
 * @date: 2019/6/18
 */
@Service
@Transactional
public class AccruedExpensesReimbuseService extends BaseService<AccrualExpenseTypeMapper,ExpenseAccrualType> {

    @Autowired
    private AccruedExpensesReimbuseMapper accruedExpensesReimbuseMapper;

    public List<AccruedReimburse> getAccruedReimbuse(AccruedReimburse accruedReimburse){
        return accruedExpensesReimbuseMapper.queryAccruedExpensesReimbuse(accruedReimburse);
    }
}
