package com.hand.hcf.app.expense.accrual.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.common.co.UserGroupCO;
import com.hand.hcf.app.expense.accrual.domain.*;
import com.hand.hcf.app.expense.accrual.dto.DepartmentOrUserGroupReturnDTO;
import com.hand.hcf.app.expense.accrual.dto.ExpenseAccrualTypeRequestDTO;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualCompanyMapper;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualTypeMapper;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.mdata.implement.web.AuthorizeControllerImpl;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@Service
public class ExpenseAccrualTypeService extends ServiceImpl<ExpenseAccrualTypeMapper,ExpenseAccrualType> {

    @Autowired
    private ExpenseAccrualDimensionService expenseAccrualDimensionService;
    @Autowired
    private ExpenseAccrualTypeAssignDepartmentService expenseAccrualTypeAssignDepartmentService;
    @Autowired
    private ExpenseAccrualExpenseTypeService expenseAccrualExpenseTypeService;
    @Autowired
    private ExpenseAccrualTypeAssignUserGroupService expenseAccrualTypeAssignUserGroupService;
    @Autowired
    private ExpenseAccrualTypeMapper expenseAccrualTypeMapper;
    @Autowired
    private ExpenseAccrualCompanyMapper expenseAccrualCompanyMapper;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @Autowired
    private AuthorizeControllerImpl authorizeClient;
    @Autowired
    private ExpenseReportTypeService reportTypeService;
    @Autowired
    private ContactControllerImpl contactInterface;
    @Autowired
    private WorkflowControllerImpl workflowClient;

    /**
     * 根据id查询 费用预提单类型
     * @param id
     * @return
     */
    public ExpenseAccrualTypeRequestDTO getExpenseAccrualType(Long id) {

        ExpenseAccrualTypeRequestDTO expenseAccrualTypeRequestDTO = new ExpenseAccrualTypeRequestDTO();

        //费用预提单类型数据
        ExpenseAccrualType expenseAccrualType = expenseAccrualTypeMapper.selectById(id);
        if (expenseAccrualType == null) {
            throw new BizException(RespCode.EXPENSE_ACCRUAL_TYPE_IS_NOT_EXISTS);
        }

        //帐套信息
        SetOfBooksInfoCO setOfBooks = organizationService.getSetOfBooksInfoCOById(expenseAccrualType.getSetOfBooksId(), true);
        expenseAccrualType.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
        expenseAccrualType.setSetOfBooksName(setOfBooks.getSetOfBooksName());

        expenseAccrualTypeRequestDTO.setExpenseAccrualType(expenseAccrualType);

        //费用预提单关联费用类型数据
        if (expenseAccrualType.getAllExpense() == false) {
            List<Long> expenseIdList = expenseAccrualExpenseTypeService.selectList(
                    new EntityWrapper<ExpenseAccrualExpenseType>()
                            .eq("exp_accrual_type_id", id)
            ).stream().map(ExpenseAccrualExpenseType::getExpExpenseId).collect(Collectors.toList());

            //Long类型 费用类型id集合
            expenseAccrualTypeRequestDTO.setExpenseIdList(expenseIdList);

            List<String> returnExpenseIdList = new ArrayList<>();
            expenseIdList.stream().forEach(expenseId -> {
                returnExpenseIdList.add(expenseId.toString());
            });

            //String类型 费用类型id集合
            expenseAccrualTypeRequestDTO.setReturnExpenseIdList(returnExpenseIdList);
        }

        //费用预提单类型关联维度数据
        List<Long> dimensionIdList = expenseAccrualDimensionService.selectList(
                new EntityWrapper<ExpenseAccrualDimension>()
                        .eq("exp_accrual_type_id", id)
        ).stream().map(ExpenseAccrualDimension::getDimensionId).collect(Collectors.toList());

        //Long类型 维度id集合
        expenseAccrualTypeRequestDTO.setDimensionIdList(dimensionIdList);

        List<String> returnDimensionIdList = new ArrayList<>();
        dimensionIdList.stream().forEach(dimensionId -> {
            returnDimensionIdList.add(dimensionId.toString());
        });

        //String类型 维度id集合
        expenseAccrualTypeRequestDTO.setReturnDimensionIdList(returnDimensionIdList);

        //费用预提单类型关联的部门或人员组
        if (!expenseAccrualType.getVisibleUserScope().equals("1001")){
            List<Long> departmentOrUserGroupIdList = new ArrayList<>();
            if (expenseAccrualType.getVisibleUserScope().equals("1002")){
                departmentOrUserGroupIdList = expenseAccrualTypeAssignDepartmentService.selectList(
                        new EntityWrapper<ExpenseAccrualTypeAssignDepartment>()
                                .eq("exp_accrual_type_id", id)
                ).stream().map(ExpenseAccrualTypeAssignDepartment::getDepartmentId).collect(Collectors.toList());
                if (departmentOrUserGroupIdList != null) {
                    Map<Long, DepartmentCO> departmentMap = organizationService
                            .getDepartmentMapByDepartmentIds(departmentOrUserGroupIdList);
                    List<DepartmentOrUserGroupReturnDTO> departmentList = new ArrayList<>();
                    departmentOrUserGroupIdList.stream().forEach(e -> {
                        if (departmentMap.containsKey(e)) {
                            DepartmentCO departmentCO = departmentMap.get(e);
                            DepartmentOrUserGroupReturnDTO dep = new DepartmentOrUserGroupReturnDTO();
                            dep.setId(departmentCO.getId());
                            dep.setPathOrName(departmentCO.getName());
                            departmentList.add(dep);
                        }
                    });

                    //部门数据
                    expenseAccrualTypeRequestDTO.setDepartmentOrUserGroupList(departmentList);
                }

            }else if (expenseAccrualType.getVisibleUserScope().equals("1003")){
                departmentOrUserGroupIdList = expenseAccrualTypeAssignUserGroupService.selectList(
                        new EntityWrapper<ExpenseAccrualTypeAssignUserGroup>()
                                .eq("exp_accrual_type_id", id)
                ).stream().map(ExpenseAccrualTypeAssignUserGroup::getUserGroupId).collect(Collectors.toList());
                if (departmentOrUserGroupIdList != null) {
                    Map<Long, UserGroupCO> userGroupMap = organizationService.getUserGroupMapByGroupIds(departmentOrUserGroupIdList);
                    List<DepartmentOrUserGroupReturnDTO> userGroupList = new ArrayList<>();
                    departmentOrUserGroupIdList.stream().forEach(e -> {
                        if (userGroupMap.containsKey(e)) {
                            UserGroupCO userGroupCO = userGroupMap.get(e);
                            DepartmentOrUserGroupReturnDTO ug = new DepartmentOrUserGroupReturnDTO();
                            ug.setId(userGroupCO.getId());
                            ug.setPathOrName(userGroupCO.getName());
                            userGroupList.add(ug);
                        }
                    });

                    //用户组数据
                    expenseAccrualTypeRequestDTO.setDepartmentOrUserGroupList(userGroupList);
                }
            }
            expenseAccrualTypeRequestDTO.setDepartmentOrUserGroupIdList(departmentOrUserGroupIdList);
        }
        return expenseAccrualTypeRequestDTO;
    }

    /**
     * 新增费用预提单类型
     * @param expenseAccrualTypeRequestDTO
     * @return
     */
    @Transactional
    public ExpenseAccrualType createExpenseAccrualType(ExpenseAccrualTypeRequestDTO expenseAccrualTypeRequestDTO) {

        //插入 费用预提单类型定义
        ExpenseAccrualType expenseAccrualType = expenseAccrualTypeRequestDTO.getExpenseAccrualType();
        if (null != expenseAccrualType.getId()){
            throw new BizException(RespCode.SYS_ID_IS_NOT_NULL);
        }
        if (expenseAccrualTypeMapper.selectList(
                new EntityWrapper<ExpenseAccrualType>()
                        .eq("set_of_books_id",expenseAccrualType.getSetOfBooksId())
                        .eq("exp_accrual_type_code",expenseAccrualType.getExpAccrualTypeCode())
        ).size() > 0){
            throw new BizException(RespCode.EXPENSE_ACCRUAL_TYPE_CODE_IS_EXISTS);
        }

        expenseAccrualTypeMapper.insert(expenseAccrualType);

        //插入 费用预提单类型关联费用类型
        if ( false == expenseAccrualType.getAllExpense() ){
            List<Long> expenseTypeIdList = expenseAccrualTypeRequestDTO.getExpenseIdList();
            if ( null != expenseTypeIdList ){
                List<ExpenseAccrualExpenseType> expenseTypeList = new ArrayList<>();
                expenseTypeIdList.stream().forEach(expenseTypeId -> {
                    ExpenseAccrualExpenseType expenseType = ExpenseAccrualExpenseType
                            .builder().expAccrualTypeId(expenseAccrualType.getId()).expExpenseId(expenseTypeId).build();
                    expenseTypeList.add(expenseType);
                });
                expenseAccrualExpenseTypeService.createExpenseAccrualExpenseTypeBatch(expenseAccrualType.getId(), expenseTypeList);
            }
        }

        //插入部门或人员组
        if ( expenseAccrualType.getVisibleUserScope().equals("1002") ){
            List<Long> departmentIdList = expenseAccrualTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != departmentIdList ){
                List<ExpenseAccrualTypeAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId ->{
                    ExpenseAccrualTypeAssignDepartment department = ExpenseAccrualTypeAssignDepartment
                            .builder().expAccrualTypeId(expenseAccrualType.getId()).departmentId(departmentId).build();
                    departmentList.add(department);
                });
                expenseAccrualTypeAssignDepartmentService
                        .createExpenseAccrualTypeAssignDepartmentBatch(expenseAccrualType.getId(), departmentList);
            }
        }else if ( expenseAccrualType.getVisibleUserScope().equals("1003") ){
            List<Long> userGroupIdList = expenseAccrualTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != userGroupIdList ){
                List<ExpenseAccrualTypeAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.stream().forEach(userGroupId -> {
                    ExpenseAccrualTypeAssignUserGroup userGroup = ExpenseAccrualTypeAssignUserGroup
                            .builder().expAccrualTypeId(expenseAccrualType.getId()).userGroupId(userGroupId).build();
                    userGroupList.add(userGroup);
                });
                expenseAccrualTypeAssignUserGroupService
                        .createExpenseAccrualTypeAssignUserGroupBatch(expenseAccrualType.getId(), userGroupList);
            }
        }

        //返回给前台新增的费用预提单类型
        return expenseAccrualType;
    }

    /**
     * 修改 费用预提单类型定义
     *
     * @param expenseAccrualTypeRequestDTO
     * @return
     */
    @Transactional
    public ExpenseAccrualType updateExpenseAccrualType(ExpenseAccrualTypeRequestDTO expenseAccrualTypeRequestDTO){
        //新费用预提单类型数据
        ExpenseAccrualType expenseAccrualType = expenseAccrualTypeRequestDTO.getExpenseAccrualType();
        //原费用预提单类型数据
        ExpenseAccrualType expAccrualType = expenseAccrualTypeMapper.selectById(expenseAccrualType.getId());
        if ( null == expAccrualType ){
            throw new BizException(RespCode.EXPENSE_ACCRUAL_TYPE_IS_NOT_EXISTS);
        }
        expenseAccrualTypeMapper.updateById(expenseAccrualType);

        //修改 费用预提单类型关联费用类型
        if (false == expenseAccrualType.getAllExpense() ){
            List<Long> expenseIdList = expenseAccrualTypeRequestDTO.getExpenseIdList();
            if (expenseIdList != null){
                //将原来的数据删除
                if (false == expAccrualType.getAllExpense() ){
                    expenseAccrualExpenseTypeService.delete( new EntityWrapper<ExpenseAccrualExpenseType>()
                            .eq("exp_accrual_type_id",expenseAccrualType.getId()));
                }
                //将新数据插入
                List<ExpenseAccrualExpenseType> expenseTypeList = new ArrayList<>();
                expenseIdList.stream().forEach(expenseId -> {
                    ExpenseAccrualExpenseType expenseType = ExpenseAccrualExpenseType
                            .builder().expAccrualTypeId(expenseAccrualType.getId()).expExpenseId(expenseId).build();
                    expenseTypeList.add(expenseType);
                });
                expenseAccrualExpenseTypeService.createExpenseAccrualExpenseTypeBatch(expenseAccrualType.getId(), expenseTypeList);
            }
        }else {
            if (false == expAccrualType.getAllExpense() ){
                expenseAccrualExpenseTypeService.delete(new EntityWrapper<ExpenseAccrualExpenseType>()
                        .eq("exp_accrual_type_id",expenseAccrualType.getId()));
            }
        }

        //修改预付款单类型关联部门或人员组
        //删除原有数据
        if ( expAccrualType.getVisibleUserScope().equals("1002") ){
            List<Long> departmentIdList = expenseAccrualTypeAssignDepartmentService.selectList(
                    new EntityWrapper<ExpenseAccrualTypeAssignDepartment>()
                            .eq("exp_accrual_type_id",expAccrualType.getId())
            ).stream().map(ExpenseAccrualTypeAssignDepartment::getId).collect(Collectors.toList());
            expenseAccrualTypeAssignDepartmentService.deleteBatchIds(departmentIdList);
        }else if ( expAccrualType.getVisibleUserScope().equals("1003") ){
            List<Long> userGroupIdList = expenseAccrualTypeAssignUserGroupService.selectList(
                    new EntityWrapper<ExpenseAccrualTypeAssignUserGroup>()
                            .eq("exp_accrual_type_id",expAccrualType.getId())
            ).stream().map(ExpenseAccrualTypeAssignUserGroup::getId).collect(Collectors.toList());
            expenseAccrualTypeAssignUserGroupService.deleteBatchIds(userGroupIdList);
        }
        //插入更新的数据
        if ( expenseAccrualType.getVisibleUserScope().equals("1002") ){
            List<Long> departmentIdList = expenseAccrualTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if (departmentIdList != null){
                List<ExpenseAccrualTypeAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.forEach(departmentId ->{
                    ExpenseAccrualTypeAssignDepartment department = ExpenseAccrualTypeAssignDepartment
                            .builder().expAccrualTypeId(expenseAccrualType.getId()).departmentId(departmentId).build();
                    departmentList.add(department);
                });
                expenseAccrualTypeAssignDepartmentService
                        .createExpenseAccrualTypeAssignDepartmentBatch(expenseAccrualType.getId(), departmentList);
            }
        }else if ( expenseAccrualType.getVisibleUserScope().equals("1003") ){
            List<Long> userGroupIdList = expenseAccrualTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if (userGroupIdList != null){
                List<ExpenseAccrualTypeAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.forEach(userGroupId -> {
                    ExpenseAccrualTypeAssignUserGroup userGroup = ExpenseAccrualTypeAssignUserGroup
                            .builder().expAccrualTypeId(expenseAccrualType.getId()).userGroupId(userGroupId).build();
                    userGroupList.add(userGroup);
                });
                expenseAccrualTypeAssignUserGroupService
                        .createExpenseAccrualTypeAssignUserGroupBatch(expenseAccrualType.getId(), userGroupList);
            }
        }

        //返回修改以后的费用预提单类型
        return expenseAccrualType;
    }

    /**
     * 更新预算管控
     * @param id
     * @param budgtFlag
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBudgt(Long id, Boolean budgtFlag) {
        ExpenseAccrualType expenseAccrualType = this.selectById(id);
        expenseAccrualType.setBudgtFlag(budgtFlag == null ? false : budgtFlag);
        return this.updateById(expenseAccrualType);
    }

    /**
     * 自定义条件查询 费用预提单类型定义(分页)
     *
     * @param setOfBooksId
     * @param expAccrualTypeCode
     * @param expAccrualTypeName
     * @param enableFlag
     * @param page
     * @return
     */
    public Page<ExpenseAccrualType> getExpenseAccrualTypeByCond(Long setOfBooksId,
                                                                String expAccrualTypeCode,
                                                                String expAccrualTypeName,
                                                                Boolean enableFlag,
                                                                Page page,
                                                                boolean dataAuthFlag){
        Page<ExpenseAccrualType> list = new Page<>();

        if (setOfBooksId == null){
            return list;
        }

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME,"exp_accrual_type");
            //map.put(DataAuthorityUtil.TABLE_ALIAS,"t");
            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_books_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }

        list = this.selectPage(page,
                new EntityWrapper<ExpenseAccrualType>()
                        .eq("set_of_books_id",setOfBooksId)
                        .like(expAccrualTypeCode != null, "exp_accrual_type_code",expAccrualTypeCode, SqlLike.DEFAULT)
                        .like(expAccrualTypeName != null, "exp_accrual_type_name",expAccrualTypeName, SqlLike.DEFAULT)
                        .eq(enableFlag != null, "enable_flag",enableFlag)
                        .and(!StringUtils.isEmpty(dataAuthLabel), dataAuthLabel)
                        .orderBy("enable_flag",false)
                        .orderBy("exp_accrual_type_code")
        );
        for (ExpenseAccrualType expenseAccrualType : list.getRecords()){
            //返回账套code、账套name
            SetOfBooksInfoCO setOfBooks = organizationService.getSetOfBooksInfoCOById(expenseAccrualType.getSetOfBooksId(), true);
            expenseAccrualType.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
            expenseAccrualType.setSetOfBooksName(setOfBooks.getSetOfBooksName());
            if (expenseAccrualType.getFormId() != null) {
                ApprovalFormCO form = workflowClient.getApprovalFormById(expenseAccrualType.getFormId());
                expenseAccrualType.setFormName(form.getFormName());
            }
        }
        return list;
    }

    public List<ExpenseType> getExpenseType(Long id, String code, String name, Page page) {
        ExpenseAccrualType expenseAccrualType = this.selectById(id);
        if (null == expenseAccrualType){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ExpenseType> result = baseMapper
                .listAssignExpenseType(expenseAccrualType.getAllExpense() == null ? false : expenseAccrualType.getAllExpense(),
                expenseAccrualType.getSetOfBooksId(),
                id,
                code,
                name,
                page);
        return result;
    }
}
