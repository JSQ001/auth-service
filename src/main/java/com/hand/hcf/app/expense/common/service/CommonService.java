package com.hand.hcf.app.expense.common.service;

import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *     该service将一些各个service相同的处理逻辑进行抽取
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/22
 */
@Service
@Slf4j
public class CommonService {

    //jiu.zhao 预算
    /*@Autowired
    private BudgetService budgetService;*/

    @Autowired
    private OrganizationService orgService;
    /**
     * 根据维度ID查询维度并且赋值
     * @param dimensions
     */
    public void setDimensionValueName(List<ExpenseDimension> dimensions, Long companyId, Long unitId, Long userId){
        if (!CollectionUtils.isEmpty(dimensions)){
            List<Long> dimensionIds = dimensions
                    .stream()
                    .map(ExpenseDimension::getDimensionId)
                    .collect(Collectors.toList());
            List<DimensionDetailCO> detailCOS = orgService.listDetailCOByDimensionIdsAndCompany(
                    companyId, unitId, userId, null, dimensionIds);

            Map<Long, DimensionDetailCO> detailCOMap = detailCOS
                    .stream()
                    .collect(Collectors.toMap(DimensionDetailCO::getId, e -> e, (k1, k2) -> k1));
            dimensions.forEach(e -> {
                DimensionDetailCO dimensionDetailCO = detailCOMap.get(e.getDimensionId());
                if (null != dimensionDetailCO) {
                    e.setName(dimensionDetailCO.getDimensionName());
                    Map<Long, String> valueMap = dimensionDetailCO
                            .getSubDimensionItemCOS()
                            .stream()
                            .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName));
                    e.setValueName(valueMap.get(e.getValue()));
                }

            });
        }
    }

    public void setBudgetReserveOtherInfo(List<BudgetReserveCO> dtoList){
        Set<Long> companyIds = new HashSet<>();
        Set<Long> departmentIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();
        Set<Long> dimensionIds = new HashSet<>();
        Map<Long, String> companyMap = new HashMap<>(16);
        Map<Long, String> departmentMap = new HashMap<>(16);
        Map<Long, String> usersMap = new HashMap<>(16);
        Map<Long, String> dimensionMap = new HashMap<>(16);
        dtoList.stream().forEach(e -> {
            companyIds.add(e.getCompanyId());
            departmentIds.add(e.getUnitId());
            employeeIds.add(e.getEmployeeId());
            dimensionIds.addAll(DimensionUtils.getDimensionId(e, BudgetReserveCO.class));
        });

        // 查询公司
        if (!CollectionUtils.isEmpty(companyIds)) {
            List<CompanyCO> companySumDTOS = orgService.listCompaniesByIds(new ArrayList<>(companyIds));
            if (!CollectionUtils.isEmpty(companySumDTOS)) {
                companyMap = companySumDTOS
                        .stream()
                        .collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getCompanyCode, (k1, k2) -> k1));
            }
        }
        if (!CollectionUtils.isEmpty(departmentIds)) {
            // 查询部门
            List<DepartmentCO> departments = orgService.listDepartmentsByIds(new ArrayList<>(departmentIds));
            if (!CollectionUtils.isEmpty(departments)) {
                departmentMap = departments
                        .stream()
                        .collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getDepartmentCode, (k1, k2) -> k1));
            }
        }

        if (!CollectionUtils.isEmpty(employeeIds)) {
            // 查询员工
            List<ContactCO> users = orgService.listUsersByIds(new ArrayList<>(employeeIds));
            if (!CollectionUtils.isEmpty(users)) {
                usersMap = users
                        .stream()
                        .collect(Collectors.toMap(ContactCO::getId, ContactCO::getEmployeeCode, (k1, k2) -> k1));
            }
        }

        // 查询维度值代码
        if (!CollectionUtils.isEmpty(dimensionIds)) {
            List<DimensionItemCO> valueDTOs = orgService.listDimensionItemsByIds(new ArrayList<>(dimensionIds));
            if (!CollectionUtils.isEmpty(valueDTOs)) {
                dimensionMap = valueDTOs
                        .stream()
                        .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemCode, (k1, k2) -> k1));
            }
        }

        for (int i = 0; i < dtoList.size(); i++) {
            BudgetReserveCO budgetReserveDTO = dtoList.get(i);
            if (companyMap.containsKey(budgetReserveDTO.getCompanyId())){
                budgetReserveDTO.setCompanyCode(companyMap.get(budgetReserveDTO.getCompanyId()));
            }
            if (departmentMap.containsKey(budgetReserveDTO.getUnitId())){
                budgetReserveDTO.setUnitCode(departmentMap.get(budgetReserveDTO.getUnitId()));
            }
            if (usersMap.containsKey(budgetReserveDTO.getEmployeeId())){
                budgetReserveDTO.setEmployeeCode(usersMap.get(budgetReserveDTO.getEmployeeId()));
            }
            DimensionUtils.setDimensionCodeOrName("Code", budgetReserveDTO, BudgetReserveCO.class, dimensionMap);
        }
    }

    public BudgetCheckResultDTO checkBudget(BudgetCheckMessageCO param){
        //jiu.zhao 预算
        /*BudgetCheckReturnCO checkResult;
        log.info("调用预算模块校验预算！");
        try {
            checkResult = budgetService.saveBudgetCheck(param);
        }catch (Exception e){
            log.error("调用预算模块发生异常！原因:{}",e.getMessage());
            throw new BizException(RespCode.EXPENSE_EXPENSE_PERIODS_ERROR);
        }
        BudgetCheckResultDTO checkResultDTO = new BudgetCheckResultDTO();

        if (BudgetCheckConstant.NO_MESSAGE.equals(checkResult.getMessageLevel())) {
            //校验通过
            log.info("预算校验通过");
            return BudgetCheckResultDTO.ok();
        } else if (BudgetCheckConstant.COMPEL.equals(checkResult.getMessageLevel())) {
            log.info("弱管控，预算校验通过，是因为用户选择继续提交，【{}】", checkResult.getErrorMessage());
            //弱管控-确认后提交通过
            //若管控-超预算标记
            checkResultDTO.setPassFlag(true);
            checkResultDTO.setCode(BudgetCheckConstant.SUCCESS);
            checkResultDTO.setMessage(checkResult.getErrorMessage());
            return checkResultDTO;
        } else if (BudgetCheckConstant.ERROR.equals(checkResult.getMessageLevel()) ||
                BudgetCheckConstant.BLOCK.equals(checkResult.getMessageLevel())) {
            //强管控-超预算
            log.info("预算校验不通过，messageLevel = 【{}】，原因 =【{}】,", checkResult.getErrorMessage());
            checkResultDTO.setPassFlag(false);
            checkResultDTO.setCode(BudgetCheckConstant.FAILURE);
            checkResultDTO.setMessage(checkResult.getErrorMessage());
            return checkResultDTO;
        } else if (BudgetCheckConstant.ALLOWED.equals(checkResult.getMessageLevel()) ||
                BudgetCheckConstant.REMIND.equals(checkResult.getMessageLevel())) {
            //弱管控-超预算
            log.info("弱管控，预算校验不通过，需要用户确认，【{}】", checkResult.getErrorMessage());
            checkResultDTO.setPassFlag(false);
            checkResultDTO.setCode(BudgetCheckConstant.WARNING);
            checkResultDTO.setMessage(checkResult.getErrorMessage());
            return checkResultDTO;
        } else {
            log.info("预算校验错误信息，{}", checkResult.getErrorMessage());
            checkResultDTO.setPassFlag(false);
            checkResultDTO.setCode(BudgetCheckConstant.FAILURE);
            checkResultDTO.setMessage(checkResult.getErrorMessage());
            return checkResultDTO;
        }*/
        return null;
    }

    /**
     * @Description: 获取编码规则的值
     * @param: documentType
     * @return: java.lang.String
     * @Date: Created in 2018/4/19 10:32
     * @Modified by
     */
    public String getCoding(String documentType, Long companyId, String companyCode){
        if (!StringUtils.hasText(companyCode)) {
            CompanyCO companyCO = orgService.getCompanyById(companyId);
            companyCode = companyCO == null ? null : companyCO.getCompanyCode();
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(date);
        return orgService.getOrderNumber(documentType, companyCode, now);
    }


    public void rollbackBudget(List<BudgetReverseRollbackCO> message){
        //jiu.zhao 预算
        /*try {
            budgetService.updateBudgetRollback(message);
        }catch (Exception e){
            throw new BizException(RespCode.EXPENSE_ROLLBACK_BUDGET_ERROR);
        }*/
    }


    public void setDimensionValueNameAndOptions(List<ExpenseDimension> dimensions, Long companyId, Long unitId, Long userId){
        if (!CollectionUtils.isEmpty(dimensions)){
            List<Long> dimensionIds = dimensions
                    .stream()
                    .map(ExpenseDimension::getDimensionId)
                    .collect(Collectors.toList());
            // 查询当前公司这些维度启用的维值
            List<DimensionDetailCO> valueDTOs = orgService.listDetailCOByDimensionIdsAndCompany(
                    companyId, unitId, userId, Boolean.TRUE, dimensionIds);

            Map<Long, DimensionDetailCO> detailCOMap = valueDTOs
                    .stream()
                    .collect(Collectors.toMap(DimensionDetailCO::getId, e -> e, (k1, k2) -> k1));
            dimensions.forEach(e -> {
                if (detailCOMap.containsKey(e.getDimensionId())){
                    // 设置可以的维值
                    DimensionDetailCO detailCO = detailCOMap.get(e.getDimensionId());
                    e.setOptions(detailCO.getSubDimensionItemCOS());
                    e.setName(detailCO.getDimensionName());
                    Map<Long, DimensionItemCO> collect = detailCO
                            .getSubDimensionItemCOS()
                            .stream()
                            .collect(Collectors.toMap(DimensionItemCO::getId, v -> v));
                    // 如果当前的维值是分配的，则赋值，反之为null
                    if (collect.containsKey(e.getValue())) {
                        DimensionItemCO itemCO = collect.get(e.getValue());
                        e.setValue(itemCO.getId());
                        e.setValueName(itemCO.getDimensionItemName());
                    }else{
                        e.setValue(null);
                        e.setValueName(null);
                    }
                }
            });
        }
    }
}
