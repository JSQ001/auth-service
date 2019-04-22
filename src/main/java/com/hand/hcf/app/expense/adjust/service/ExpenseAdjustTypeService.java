package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.FormTypeEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.expense.adjust.domain.*;
import com.hand.hcf.app.expense.adjust.dto.DepartmentOrUserGroupReturnDTO;
import com.hand.hcf.app.expense.adjust.dto.ExpenseAdjustTypeDTO;
import com.hand.hcf.app.expense.adjust.dto.ExpenseAdjustTypeRequestDTO;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustTypeAssignCompanyMapper;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustTypeMapper;
import com.hand.hcf.app.expense.adjust.web.dto.ExpAdjustTypeDimensionDTO;
import com.hand.hcf.app.expense.adjust.web.dto.ExpenseAdjustTypeWebDTO;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.AssignUserEnum;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.AuthorizeControllerImpl;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Service
public class ExpenseAdjustTypeService extends ServiceImpl<ExpenseAdjustTypeMapper,ExpenseAdjustType>{

    @Autowired
    private ExpenseAdjustTypeAssignExpenseTypeService expenseAdjustTypeAssignExpenseTypeService;
    @Autowired
    private ExpenseAdjustTypeMapper expenseAdjustTypeMapper;
    @Autowired
    private ExpenseAdjustTypeAssignDimensionService expenseAdjustTypeAssignDimensionService;
    @Autowired
    private ExpenseAdjustTypeAssignDepartmentService expenseAdjustTypeAssignDepartmentService;
    @Autowired
    private  ExpenseAdjustTypeAssignUserGroupService  expenseAdjustTypeAssignUserGroupService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExpenseAdjustTypeAssignCompanyMapper expenseAdjustTypeAssignCompanyMapper;


    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private AuthorizeControllerImpl authorizeClient;

    @Autowired
    private ExpenseReportTypeService reportTypeService;

    @Autowired
    private ContactControllerImpl contactInterface;
    @Autowired
    private ExpAdjustTypeDimensionService adjustTypeDimensionService;

    /**
     * 自定义条件查询 费用调整单类型定义(分页)
     *
     * @param setOfBooksId
     * @param expAdjustTypeCode
     * @param expAdjustTypeName
     * @param adjustTypeCategory
     * @param enabled
     * @param page
     * @return
     */
    public Page<ExpenseAdjustType> getExpenseAdjustTypeByCond(Long setOfBooksId, String expAdjustTypeCode, String expAdjustTypeName,String adjustTypeCategory,Boolean enabled, Page page){
        Page<ExpenseAdjustType> list = new Page<>();

        if (setOfBooksId == null){
            return list;
        }

        list = this.selectPage(page,
            new EntityWrapper<ExpenseAdjustType>()
                .eq("set_of_books_id",setOfBooksId)
                .like(expAdjustTypeCode != null, "exp_adjust_type_code",expAdjustTypeCode, SqlLike.DEFAULT)
                .like(expAdjustTypeName != null, "exp_adjust_type_name",expAdjustTypeName, SqlLike.DEFAULT)
                .eq(adjustTypeCategory != null,"adjust_type_category",adjustTypeCategory)
                .eq(enabled != null, "enabled",enabled)
                .orderBy("enabled",false)
                .orderBy("exp_adjust_type_code")
        );
        for (ExpenseAdjustType expenseAdjustType : list.getRecords()){
            //返回账套code、账套name
            SetOfBooksInfoCO setOfBooks = organizationService.getSetOfBooksInfoCOById(expenseAdjustType.getSetOfBooksId(), true);
            expenseAdjustType.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
            expenseAdjustType.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        }
        return list;
    }

    /**
     * 新增 费用调整单类型定义
     *
     * @param expenseAdjustTypeRequestDTO
     * @return
     */
    @Transactional
    public ExpenseAdjustType createExpenseAdjustType(ExpenseAdjustTypeRequestDTO expenseAdjustTypeRequestDTO){
        //插入 费用调整单类型定义
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeRequestDTO.getExpenseAdjustType();
        if (null != expenseAdjustType.getId()){
            throw new BizException(RespCode.SYS_ID_IS_NOT_NULL);
        }
        if (expenseAdjustTypeMapper.selectList(
                new EntityWrapper<ExpenseAdjustType>()
                        .eq("set_of_books_id",expenseAdjustType.getSetOfBooksId())
                        .eq("exp_adjust_type_code",expenseAdjustType.getExpAdjustTypeCode())
        ).size() > 0){
            throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_CODE_IS_EXISTS);
        }

        expenseAdjustTypeMapper.insert(expenseAdjustType);

        //插入 费用调整单类型关联费用类型
        if ( false == expenseAdjustType.getAllExpense() ){
            List<Long> expenseTypeIdList = expenseAdjustTypeRequestDTO.getExpenseIdList();
            if ( null != expenseTypeIdList ){
                List<ExpenseAdjustTypeAssignExpenseType> expenseTypeList = new ArrayList<>();
                expenseTypeIdList.stream().forEach(expenseTypeId -> {
                    ExpenseAdjustTypeAssignExpenseType expenseType = ExpenseAdjustTypeAssignExpenseType
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).expExpenseId(expenseTypeId).build();
                    expenseTypeList.add(expenseType);
                });
                expenseAdjustTypeAssignExpenseTypeService.createExpenseAdjustTypeAssignExpenseTypeBatch(expenseAdjustType.getId(), expenseTypeList);
            }
        }

        //插入 费用调整单类型关联维度
        if ( false == expenseAdjustType.getAllDimension() ){
            List<Long> dimensionIdList =expenseAdjustTypeRequestDTO.getDimensionIdList();
            if ( null != dimensionIdList ){
                List<ExpenseAdjustTypeAssignDimension> dimensionList = new ArrayList<>();
                dimensionIdList.stream().forEach(dimensionId -> {
                    ExpenseAdjustTypeAssignDimension dimension = ExpenseAdjustTypeAssignDimension
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).dimensionId(dimensionId).build();
                    dimensionList.add(dimension);
                });
                expenseAdjustTypeAssignDimensionService.createExpenseAdjustTypeAssignDimensionBatch(expenseAdjustType.getId(), dimensionList);
            }
        }

        //插入部门或人员组
        if ( 1002 == expenseAdjustType.getApplyEmployee() ){
            List<Long> departmentIdList = expenseAdjustTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != departmentIdList ){
                List<ExpenseAdjustTypeAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId ->{
                    ExpenseAdjustTypeAssignDepartment department = ExpenseAdjustTypeAssignDepartment
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).departmentId(departmentId).build();
                    departmentList.add(department);
                });
                expenseAdjustTypeAssignDepartmentService.createExpenseAdjustTypeAssignDepartmentBatch(expenseAdjustType.getId(), departmentList);
            }
        }else if ( 1003 == expenseAdjustType.getApplyEmployee() ){
            List<Long> userGroupIdList = expenseAdjustTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if ( null != userGroupIdList ){
                List<ExpenseAdjustTypeAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.stream().forEach(userGroupId -> {
                    ExpenseAdjustTypeAssignUserGroup userGroup = ExpenseAdjustTypeAssignUserGroup
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).userGroupId(userGroupId).build();
                    userGroupList.add(userGroup);
                });
                expenseAdjustTypeAssignUserGroupService.createExpenseAdjustTypeAssignUserGroupBatch(expenseAdjustType.getId(), userGroupList);
            }
        }

        //返回给前台新增的费用调整单类型
        return expenseAdjustType;
    }

    /**
     * 根据ID查询 费用调整单类型
     *
     * @param id
     * @return
     */
    public ExpenseAdjustTypeRequestDTO getExpenseAdjustType(Long id){
        ExpenseAdjustTypeRequestDTO expenseAdjustTypeRequestDTO = new ExpenseAdjustTypeRequestDTO();

        //返回 费用调整单类型数据
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeMapper.selectById(id);
        if (expenseAdjustType == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_IS_NOT_EXISTS);
        }
        //返回账套code、账套name
        SetOfBooksInfoCO setOfBooks = organizationService.getSetOfBooksInfoCOById(expenseAdjustType.getSetOfBooksId(), true);
        expenseAdjustType.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
        expenseAdjustType.setSetOfBooksName(setOfBooks.getSetOfBooksName());

        expenseAdjustTypeRequestDTO.setExpenseAdjustType(expenseAdjustType);

        //返回 费用调整单类型关联费用类型数据
        if (expenseAdjustType.getAllExpense() == false){
            List<Long> expenseIdList = expenseAdjustTypeAssignExpenseTypeService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignExpenseType>()
                            .eq("exp_adjust_type_id",id)
            ).stream().map(ExpenseAdjustTypeAssignExpenseType::getExpExpenseId).collect(toList());
            //返回Long 类型的费用类型id集合
            expenseAdjustTypeRequestDTO.setExpenseIdList(expenseIdList);
            //返回String 类型的费用类型id集合
            List<String> returnExpenseIdList = new ArrayList<>();
            expenseIdList.stream().forEach(expenseId -> {
                returnExpenseIdList.add(expenseId.toString());
            });
            expenseAdjustTypeRequestDTO.setReturnExpenseIdList(returnExpenseIdList);
        }

        //返回 费用调整单类型关联维度数据
        if (expenseAdjustType.getAllDimension() == false){
            List<Long> dimensionIdList = expenseAdjustTypeAssignDimensionService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignDimension>()
                            .eq("exp_adjust_type_id",id)
            ).stream().map(ExpenseAdjustTypeAssignDimension::getDimensionId).collect(toList());
            //返回Long 类型的维度id集合
            expenseAdjustTypeRequestDTO.setDimensionIdList(dimensionIdList);
            //返回String 类型的维度id集合
            List<String> returnDimensionIdList = new ArrayList<>();
            dimensionIdList.stream().forEach(dimensionId -> {
                returnDimensionIdList.add(dimensionId.toString());
            });
            expenseAdjustTypeRequestDTO.setReturnDimensionIdList(returnDimensionIdList);
        }

        //返回 费用调整单类型关联的部门或人员组
        if (expenseAdjustType.getApplyEmployee() != (1001)){
            List<Long> departmentOrUserGroupIdList = new ArrayList<>();
            if (expenseAdjustType.getApplyEmployee() == 1002){
                departmentOrUserGroupIdList = expenseAdjustTypeAssignDepartmentService.selectList(
                        new EntityWrapper<ExpenseAdjustTypeAssignDepartment>()
                                .eq("exp_adjust_type_id",expenseAdjustType.getId())
                ).stream().map(ExpenseAdjustTypeAssignDepartment::getDepartmentId).collect(toList());
                if (departmentOrUserGroupIdList != null){
                    Map<Long, DepartmentCO> departmentMap = organizationService.getDepartmentMapByDepartmentIds(departmentOrUserGroupIdList);
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
                    expenseAdjustTypeRequestDTO.setDepartmentOrUserGroupList(departmentList);
                }
            }else if (expenseAdjustType.getApplyEmployee() == 1003 ){
                departmentOrUserGroupIdList = expenseAdjustTypeAssignUserGroupService.selectList(
                        new EntityWrapper<ExpenseAdjustTypeAssignUserGroup>()
                                .eq("exp_adjust_type_id",expenseAdjustType.getId())
                ).stream().map(ExpenseAdjustTypeAssignUserGroup::getUserGroupId).collect(toList());
                if (departmentOrUserGroupIdList != null){
                    Map<Long, UserGroupCO> userGroupCOMap = organizationService.getUserGroupMapByGroupIds(departmentOrUserGroupIdList);
                    List<DepartmentOrUserGroupReturnDTO> userGroupList = new ArrayList<>();
                    departmentOrUserGroupIdList.stream().forEach(e -> {
                        if (userGroupCOMap.containsKey(e)) {
                            UserGroupCO userGroupCO = userGroupCOMap.get(e);
                            DepartmentOrUserGroupReturnDTO ug = new DepartmentOrUserGroupReturnDTO();
                            ug.setId(userGroupCO.getId());
                            ug.setPathOrName(userGroupCO.getName());
                            userGroupList.add(ug);
                        }
                    });
                    expenseAdjustTypeRequestDTO.setDepartmentOrUserGroupList(userGroupList);
                }
            }
            expenseAdjustTypeRequestDTO.setDepartmentOrUserGroupIdList(departmentOrUserGroupIdList);
        }
        return expenseAdjustTypeRequestDTO;
    }
    /**
     * 根据ID查询 费用调整单类型
     *
     * @param typeId
     * @return
     */
    public ExpenseAdjustType getExpenseAdjustTypeById(Long typeId){
        //返回 费用调整单类型数据
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeMapper.selectById(typeId);
        if (expenseAdjustType == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_IS_NOT_EXISTS);
        }
        return expenseAdjustType;
    }
    /**
     * 修改 费用调整单类型定义
     *
     * @param expenseAdjustTypeRequestDTO
     * @return
     */
    @Transactional
    public ExpenseAdjustType updateExpenseAdjustType(ExpenseAdjustTypeRequestDTO expenseAdjustTypeRequestDTO){
        //修改 费用调整单类型定义
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeRequestDTO.getExpenseAdjustType();
        ExpenseAdjustType expAdjustType = expenseAdjustTypeMapper.selectById(expenseAdjustType.getId());
        if ( null == expAdjustType ){
            throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_IS_NOT_EXISTS);
        }
        expenseAdjustTypeMapper.updateById(expenseAdjustType);

        //修改 费用调整单类型关联费用类型
        if (false == expenseAdjustType.getAllExpense() ){
            List<Long> expenseIdList = expenseAdjustTypeRequestDTO.getExpenseIdList();
            if (expenseIdList != null){
                //将原来的数据删除
                if (false == expAdjustType.getAllExpense() ){
                    expenseAdjustTypeAssignExpenseTypeService.delete( new EntityWrapper<ExpenseAdjustTypeAssignExpenseType>()
                            .eq("exp_adjust_type_id",expenseAdjustType.getId()));
                }
                //将新数据插入
                List<ExpenseAdjustTypeAssignExpenseType> expenseTypeList = new ArrayList<>();
                expenseIdList.stream().forEach(expenseId -> {
                    ExpenseAdjustTypeAssignExpenseType expenseType = ExpenseAdjustTypeAssignExpenseType
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).expExpenseId(expenseId).build();
                    expenseTypeList.add(expenseType);
                });
                expenseAdjustTypeAssignExpenseTypeService.createExpenseAdjustTypeAssignExpenseTypeBatch(expenseAdjustType.getId(), expenseTypeList);
            }
        }else {
            if (false == expAdjustType.getAllExpense() ){
                expenseAdjustTypeAssignExpenseTypeService.delete(new EntityWrapper<ExpenseAdjustTypeAssignExpenseType>()
                        .eq("exp_adjust_type_id",expenseAdjustType.getId()));
            }
        }
        //修改 费用调整单类型关联维度
        if (false == expenseAdjustType.getAllDimension() ){
            List<Long> dimensionIdList = expenseAdjustTypeRequestDTO.getDimensionIdList();
            if (dimensionIdList != null){
                //将原来的数据删除
                if (false == expAdjustType.getAllDimension() ){
                    expenseAdjustTypeAssignDimensionService.delete(new EntityWrapper<ExpenseAdjustTypeAssignDimension>()
                            .eq("exp_adjust_type_id",expenseAdjustType.getId()));
                }
                //将新数据插入
                List<ExpenseAdjustTypeAssignDimension> dimensionList = new ArrayList<>();
                dimensionIdList.stream().forEach(dimensionId -> {
                    ExpenseAdjustTypeAssignDimension dimension = ExpenseAdjustTypeAssignDimension
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).dimensionId(dimensionId).build();
                    dimensionList.add(dimension);
                });
                expenseAdjustTypeAssignDimensionService.createExpenseAdjustTypeAssignDimensionBatch(expenseAdjustType.getId(), dimensionList);
            }
        }else {
            if (false == expAdjustType.getAllDimension() ){
                expenseAdjustTypeAssignDimensionService.delete(new EntityWrapper<ExpenseAdjustTypeAssignDimension>()
                        .eq("exp_adjust_type_id",expenseAdjustType.getId()));
            }
        }

        //修改预付款单类型关联部门或人员组
        //删除原有数据
        if ( 1002 == expAdjustType.getApplyEmployee() ){
            List<Long> departmentIdList = expenseAdjustTypeAssignDepartmentService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignDepartment>()
                            .eq("exp_adjust_type_id",expenseAdjustType.getId())
            ).stream().map(ExpenseAdjustTypeAssignDepartment::getId).collect(toList());
            expenseAdjustTypeAssignDepartmentService.deleteBatchIds(departmentIdList);
        }else if ( 1003 == expAdjustType.getApplyEmployee() ){
            List<Long> userGroupIdList = expenseAdjustTypeAssignUserGroupService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignUserGroup>()
                            .eq("exp_adjust_type_id",expenseAdjustType.getId())
            ).stream().map(ExpenseAdjustTypeAssignUserGroup::getId).collect(toList());
            expenseAdjustTypeAssignUserGroupService.deleteBatchIds(userGroupIdList);
        }
        //插入更新的数据
        if ( 1002 == expenseAdjustType.getApplyEmployee() ){
            List<Long> departmentIdList = expenseAdjustTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if (departmentIdList != null){
                List<ExpenseAdjustTypeAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.forEach(departmentId ->{
                    ExpenseAdjustTypeAssignDepartment department = ExpenseAdjustTypeAssignDepartment
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).departmentId(departmentId).build();
                    departmentList.add(department);
                });
                expenseAdjustTypeAssignDepartmentService.createExpenseAdjustTypeAssignDepartmentBatch(expenseAdjustType.getId(), departmentList);
            }
        }else if ( 1003 == expenseAdjustType.getApplyEmployee() ){
            List<Long> userGroupIdList = expenseAdjustTypeRequestDTO.getDepartmentOrUserGroupIdList();
            if (userGroupIdList != null){
                List<ExpenseAdjustTypeAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.forEach(userGroupId -> {
                    ExpenseAdjustTypeAssignUserGroup userGroup = ExpenseAdjustTypeAssignUserGroup
                            .builder().expAdjustTypeId(expenseAdjustType.getId()).userGroupId(userGroupId).build();
                    userGroupList.add(userGroup);
                });
                expenseAdjustTypeAssignUserGroupService.createExpenseAdjustTypeAssignUserGroupBatch(expenseAdjustType.getId(), userGroupList);
            }
        }

        //返回修改以后的费用调整单类型
        return expenseAdjustType;
    }

    /**
     * 根据单据类型ID获取单据类型及其分配的维度信息
     * @param id
     * @return
     */
    public ExpenseAdjustTypeWebDTO getTypeAndDimensions(Long id) {
        ExpenseAdjustTypeWebDTO typeWebDTO = new ExpenseAdjustTypeWebDTO();
        ExpenseAdjustType expenseAdjustType = this.selectById(id);
        if (expenseAdjustType == null){
            throw new BizException(RespCode.EXPENSE_ADJUST_TYPE_IS_NOT_EXISTS);
        }
        BeanUtils.copyProperties(expenseAdjustType, typeWebDTO);
        List<DimensionCO> centers = new ArrayList<>();
        if (expenseAdjustType.getAllDimension() != null && expenseAdjustType.getAllDimension()){
            // 所有维度
            centers = organizationService.listAllDimensionsBySetOfBooksId(expenseAdjustType.getSetOfBooksId());
        }else{
            List<ExpenseAdjustTypeAssignDimension> typeAssignDimensions = expenseAdjustTypeAssignDimensionService
                    .selectList(new EntityWrapper<ExpenseAdjustTypeAssignDimension>()
                            .eq("exp_adjust_type_id", expenseAdjustType.getId()));
            if (!CollectionUtils.isEmpty(typeAssignDimensions)){
                List<Long> dimensionIdList = typeAssignDimensions.stream().map(ExpenseAdjustTypeAssignDimension::getDimensionId).collect(toList());
                centers = organizationService.listDimensionsByIds(dimensionIdList);
            }
        }
        List<ExpenseDimension> dimensions = new ArrayList<>();
        for (int i = 1; i <= centers.size() ; i++) {
            ExpenseDimension dimension = new ExpenseDimension();
            DimensionCO costCenterDTO = centers.get(i - 1);
            dimension.setDocumentType(ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey());
            dimension.setHeaderFlag(true);
            dimension.setDimensionField("dimension" + costCenterDTO.getDimensionSequence() + "Id");
            dimension.setSequence(i);
            dimension.setName(costCenterDTO.getDimensionName());
            dimension.setDimensionId(costCenterDTO.getId());
            dimensions.add(dimension);
        }
        typeWebDTO.setDimensions(dimensions);
        return typeWebDTO;
    }

    /**
     * 删除 费用调整单类型定义
     *
     * @param id
     */
    @Transactional
    public void deleteExpenseAdjustType(Long id){
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeMapper.selectById(id);
        if (expenseAdjustType == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        //删除该费用调整单类型
        expenseAdjustTypeMapper.deleteById(id);
        //删除该费用调整单类型 关联的 费用类型
        if (expenseAdjustType.getAllExpense() == false){
            List<Long> expenseTypeIds = expenseAdjustTypeAssignExpenseTypeService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignExpenseType>()
                            .eq("exp_adjust_type_id",id)
            ).stream().map(ExpenseAdjustTypeAssignExpenseType::getId).collect(toList());
            expenseAdjustTypeAssignExpenseTypeService.deleteBatchIds(expenseTypeIds);
        }
        //删除该费用调整单类型 关联的 维度
        if (expenseAdjustType.getAllDimension() == false){
            List<Long> dimensionIds = expenseAdjustTypeAssignDimensionService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignDimension>()
                            .eq("exp_adjust_type_id",id)
            ).stream().map(ExpenseAdjustTypeAssignDimension::getId).collect(toList());
            expenseAdjustTypeAssignDimensionService.deleteBatchIds(dimensionIds);
        }
        //删除该费用调整单类型 关联的 适用人员(部门或人员组)
        //删除关联的部门
        if (1002 == expenseAdjustType.getApplyEmployee() ){
            List<Long> departmentIds = expenseAdjustTypeAssignDepartmentService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignDepartment>()
                            .eq("exp_adjust_type_id",id)
            ).stream().map(ExpenseAdjustTypeAssignDepartment::getId).collect(toList());
            expenseAdjustTypeAssignDepartmentService.deleteBatchIds(departmentIds);
        }
        //删除关联的人员组
        if (1003 == expenseAdjustType.getApplyEmployee() ){
            List<Long> userGroupIds = expenseAdjustTypeAssignUserGroupService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignUserGroup>()
                            .eq("exp_adjust_type_id",id)
            ).stream().map(ExpenseAdjustTypeAssignUserGroup::getId).collect(toList());
            expenseAdjustTypeAssignUserGroupService.deleteBatchIds(userGroupIds);
        }
        //删除该费用调整单类型 关联的 公司
        if (expenseAdjustType.getAllDimension() == false){
            List<Long> companyIds = expenseAdjustTypeAssignCompanyMapper.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignCompany>()
                            .eq("exp_adjust_type_id",id)
            ).stream().map(ExpenseAdjustTypeAssignCompany::getId).collect(toList());
            if (companyIds.size() > 0) {
                expenseAdjustTypeAssignCompanyMapper.deleteBatchIds(companyIds);
            }
        }

        return;
    }


    public List<ExpenseAdjustTypeDTO> queryByUser(Boolean isAuth) {
        //获取当前员工ID
        UUID userId = OrgInformationUtil.getCurrentUserOid();
        DepartmentCO departmentCO = organizationService.getDepartementCOByUserOid(userId.toString());
        Long departmentId = -1L;
        if (departmentCO != null){
            departmentId = departmentCO.getId();
        }
        Long companyId = OrgInformationUtil.getCurrentCompanyId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        // 先查询权限为全部人员和当前用户所属的部门的单据类型
        List<ExpenseAdjustTypeDTO> adjustTypeDTOList = baseMapper.selectByUser(departmentId, companyId, setOfBooksId);

        // 然后查询权限为人员组的单据类型
        List<ExpenseAdjustTypeDTO> list = baseMapper.selectByUserGroup(companyId, setOfBooksId);
        if (!CollectionUtils.isEmpty(list)) {
            for (ExpenseAdjustTypeDTO type : list) {
                JudgeUserCO judgeUserCO = new JudgeUserCO();
                judgeUserCO.setIdList(type.getUserGroupIdList());
                judgeUserCO.setUserId(OrgInformationUtil.getCurrentUserId());
                Boolean isExists = organizationService.judgeUserInUserGroups(judgeUserCO);
                if (isExists) {
                    adjustTypeDTOList.add(type);
                }
            }
        }
        if (isAuth != null && isAuth) {
            //获取当前用户被授权的单据类型
            List<ExpenseAdjustTypeDTO> collect = listExpenseAdjustTypesByAuthorize()
                    .stream()
                    .map(e -> {
                        ExpenseAdjustTypeDTO expenseAdjustTypeDTO = new ExpenseAdjustTypeDTO();
                        expenseAdjustTypeDTO.setCode(e.getExpAdjustTypeCode());
                        expenseAdjustTypeDTO.setName(e.getExpAdjustTypeName());
                        expenseAdjustTypeDTO.setId(e.getId());
                        return expenseAdjustTypeDTO;
                    }).collect(toList());

            adjustTypeDTOList.addAll(collect);
            //根据ID去重
            adjustTypeDTOList = adjustTypeDTOList.stream().collect(
                    collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(ExpenseAdjustTypeDTO::getId))), ArrayList::new)
            );
        }
        return adjustTypeDTOList;
    }

    /**
     * 获取当前用户被授权的单据类型
     * @return
     */
    public List<ExpenseAdjustType> listExpenseAdjustTypesByAuthorize(){
        List<ExpenseAdjustType> expenseAdjustTypeList = new ArrayList<>();
        List<FormAuthorizeCO> formAuthorizeCOList = authorizeClient.listFormAuthorizeByDocumentCategoryAndUserId(FormTypeEnum.EXPENSE_ADJUSTMENT.getCode(), OrgInformationUtil.getCurrentUserId());

        for(FormAuthorizeCO item : formAuthorizeCOList) {
            OrganizationUserCO contactCO = new OrganizationUserCO();
            if (item.getMandatorId() != null) {
                contactCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
            }
            List<Long> typeIdList = expenseAdjustTypeAssignCompanyMapper.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignCompany>()
                            .eq(item.getCompanyId() != null, "company_id", item.getCompanyId())
                            .eq(contactCO.getCompanyId() != null, "company_id", contactCO.getCompanyId())
                            .eq("enabled",true)
            ).stream().map(ExpenseAdjustTypeAssignCompany::getExpAdjustTypeId).collect(Collectors.toList());
            if (typeIdList.size() == 0) {
                continue;
            }
            List<ExpenseAdjustType> expenseAdjustTypes = this.selectList(
                    new EntityWrapper<ExpenseAdjustType>()
                            .in(typeIdList.size() != 0, "id", typeIdList)
                            .eq(item.getFormId() != null, "id", item.getFormId())
                            .eq("enabled", true));

            expenseAdjustTypes = expenseAdjustTypes.stream().filter(expenseAdjustType -> {
                // 全部人员
                if (AssignUserEnum.USER_ALL.getKey().equals(expenseAdjustType.getApplyEmployee())){
                    return true;
                }
                // 部门
                if (AssignUserEnum.USER_DEPARTMENT.getKey().equals(expenseAdjustType.getApplyEmployee())){
                    List<Long> departmentIdList = expenseAdjustTypeAssignDepartmentService.selectList(
                            new EntityWrapper<ExpenseAdjustTypeAssignDepartment>()
                                    .eq("exp_adjust_type_id",expenseAdjustType.getId())
                    ).stream().map(ExpenseAdjustTypeAssignDepartment::getDepartmentId).collect(toList());
                    if (!CollectionUtils.isEmpty(departmentIdList)) {
                        
                        if (item.getMandatorId() != null) {
                            OrganizationUserCO userCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
                            if (!departmentIdList.contains(userCO.getDepartmentId())){
                                return false;
                            }
                        }
                        if (item.getUnitId() != null && !departmentIdList.contains(item.getUnitId())) {
                            return false;
                        }
                    }
                }
                // 人员组
                if (AssignUserEnum.USER_GROUP.getKey().equals(expenseAdjustType.getApplyEmployee())){
                    List<Long> userGroupIdList = expenseAdjustTypeAssignUserGroupService.selectList(
                            new EntityWrapper<ExpenseAdjustTypeAssignUserGroup>()
                                    .eq("exp_adjust_type_id",expenseAdjustType.getId())
                    ).stream().map(ExpenseAdjustTypeAssignUserGroup::getUserGroupId).collect(toList());
                    if (!CollectionUtils.isEmpty(userGroupIdList)) {

                        if (item.getMandatorId() != null) {
                            JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(userGroupIdList).userId(item.getMandatorId()).build();
                            if (!organizationService.judgeUserInUserGroups(judgeUserCO)) {
                                return false;
                            }
                        }

                        if (item.getUnitId() != null){
                            List<Long> userIds = organizationService.listUsersByDepartmentId(item.getUnitId()).stream().map(ContactCO::getId).collect(Collectors.toList());
                            for(Long e : userIds){
                                JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(userGroupIdList).userId(e).build();
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

            expenseAdjustTypeList.addAll(expenseAdjustTypes);
        }
        return expenseAdjustTypeList;
    }

    public List<ExpenseType> getExpenseType(Long id, String code, String name, Page page) {
        ExpenseAdjustType expenseAdjustType = this.selectById(id);
        if (null == expenseAdjustType){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ExpenseType> result = baseMapper.listAssignExpenseType(expenseAdjustType.getAllExpense() == null ? false : expenseAdjustType.getAllExpense(),
                expenseAdjustType.getSetOfBooksId(),
                id,
                code,
                name,
                page);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateBudgetOrAccount(Long id, Boolean budgetFlag, Boolean accountFlag) {
        ExpenseAdjustType expenseAdjustType = this.selectById(id);
        expenseAdjustType.setBudgetFlag(budgetFlag == null ? false : budgetFlag);
        expenseAdjustType.setAccountFlag(accountFlag == null ? false : accountFlag);
        return this.updateById(expenseAdjustType);
    }

    public Page<BasicCO> pageExpenseTypeByInfoResultBasic(Long selectId, String code, String name, String securityType, Long filterId, Page<BasicCO> page) {
        if(selectId != null){
            ExpenseType expenseType = expenseTypeService.selectById(selectId);
            if(expenseType==null){
                return page;
            }
            BasicCO basicCO =BasicCO
                    .builder()
                    .id(expenseType.getId())
                    .name(expenseType.getName())
                    .code(expenseType.getCode())
                    .build();
            page.setRecords(Collections.singletonList(basicCO));
        }else {
            Long tenandId = null;
            Long setOfBooksId = null;
            if("TENANT".equals(securityType)){
                tenandId = filterId;
            }else if("SET_OF_BOOKS".equals(securityType)){
                setOfBooksId = filterId;
            }
            List<BasicCO> basicCOS = expenseTypeService.listByExpenseTypesAndCond(tenandId,setOfBooksId,true,code,name,page);

            if(!CollectionUtils.isEmpty(basicCOS)){
                page.setRecords(basicCOS);
            }
        }
        return page;
    }

    /**
     * 条件查询账套下报账单类型
     * @param code
     * @param name
     * @param selectId
     * @param securityType
     * @param filterId
     * @param page
     * @return
     */
    public Page<BasicCO> pageExpenseReportTypeByInfoResultBasic(String code, String name, Long selectId, String securityType, Long filterId, Page page) {
        List<BasicCO> basicCOS = new ArrayList<>();
        if(selectId != null){
            ExpenseReportType expenseReportType = reportTypeService.selectById(selectId);
            if(expenseReportType == null){
                return page;
            }else{
                BasicCO basicCO = BasicCO
                        .builder()
                        .id(expenseReportType.getId())
                        .name(expenseReportType.getReportTypeName())
                        .code(expenseReportType.getReportTypeCode())
                        .build();
                basicCOS.add(basicCO);
            }
        }else{
            List<ExpenseReportType> expenseReportTypes = reportTypeService.getExpenseReportType(code, name, filterId, page);
            expenseReportTypes.forEach(expenseReportType -> {
                BasicCO basicCO = BasicCO.builder()
                        .id(expenseReportType.getId())
                        .name(expenseReportType.getReportTypeName())
                        .code(expenseReportType.getReportTypeCode())
                        .build();
                basicCOS.add(basicCO);
            });
        }
        page.setRecords(basicCOS);
        return page;
    }

    public List<ContactCO> listUsersByExpenseAdjustType(Long id, String userCode, String userName, Page queryPage) {
        List<ContactCO> userCOList = new ArrayList<>();

        ExpenseAdjustType expenseAdjustType = this.getExpenseAdjustTypeById(id);
        if (expenseAdjustType == null){
            return userCOList;
        }

        List<Long> companyIdList = expenseAdjustTypeAssignCompanyMapper.selectList(
                new EntityWrapper<ExpenseAdjustTypeAssignCompany>()
                        .eq("enabled", true)
                        .eq("exp_adjust_type_id", id)
        ).stream().map(ExpenseAdjustTypeAssignCompany::getCompanyId).collect(toList());
        if (companyIdList.size() == 0){
            return userCOList;
        }

        List<Long> departmentIdList = null;
        List<Long> userGroupIdList = null;

        // 部门
        if (1002 == expenseAdjustType.getApplyEmployee()){
            departmentIdList = expenseAdjustTypeAssignDepartmentService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignDepartment>()
                            .eq("exp_adjust_type_id",expenseAdjustType.getId())
            ).stream().map(ExpenseAdjustTypeAssignDepartment::getDepartmentId).collect(toList());
        }
        // 人员组
        if (1003 == expenseAdjustType.getApplyEmployee()){
            userGroupIdList = expenseAdjustTypeAssignUserGroupService.selectList(
                    new EntityWrapper<ExpenseAdjustTypeAssignUserGroup>()
                            .eq("exp_adjust_type_id",expenseAdjustType.getId())
            ).stream().map(ExpenseAdjustTypeAssignUserGroup::getUserGroupId).collect(toList());
        }

        AuthorizeQueryCO queryCO = AuthorizeQueryCO
                .builder()
                .documentCategory(FormTypeEnum.EXPENSE_ADJUSTMENT.getCode())
                .formTypeId(id)
                .companyIdList(companyIdList)
                .departmentIdList(departmentIdList)
                .userGroupIdList(userGroupIdList)
                .currentUserId(OrgInformationUtil.getCurrentUserId())
                .build();
        //jiu.zhao 修改三方接口
        //Page<ContactCO> contactCOPage = authorizeClient.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, queryPage);
        Page<ContactCO> contactCOPage = authorizeClient.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, queryPage.getCurrent() - 1, queryPage.getSize());
        queryPage.setTotal(contactCOPage.getTotal());
        userCOList = contactCOPage.getRecords();

        return userCOList;
    }

    /**
     * 根据单据类型id查询单据类型及维度信息
     * @param expAdjustTypeId
     * @param isHeader
     * @return
     */
    public ExpAdjustTypeDimensionDTO queryTypeAndDimensionById(Long expAdjustTypeId, boolean isHeader) {
        ExpenseAdjustType expenseAdjustType = this.selectById(expAdjustTypeId);
        if (expenseAdjustType == null){
            throw new BizException(RespCode.EXPENSE_APPLICATION_TYPE_IS_NUTT);
        }
        List<ExpAdjustTypeDimension> typeDimensions = adjustTypeDimensionService.selectList(
                new EntityWrapper<ExpAdjustTypeDimension>()
                        .eq("exp_adjust_type_id", expenseAdjustType.getId())
                        .orderBy("sequence", true));
        ExpAdjustTypeDimensionDTO dto = new ExpAdjustTypeDimensionDTO();
        BeanUtils.copyProperties(expenseAdjustType, dto);
        List<ExpenseDimension> dimensions = new ArrayList<>();
        // 根据维度ID查询相关维度信息
        if (!CollectionUtils.isEmpty(typeDimensions)){
            List<Long> ids = typeDimensions.stream().map(ExpAdjustTypeDimension::getDimensionId).collect(Collectors.toList());
            List<DimensionDetailCO> dimensionDetails = organizationService.listDetailCOByDimensionIdsAndCompany(
                    OrgInformationUtil.getCurrentCompanyId(),
                    OrgInformationUtil.getCurrentDepartmentId(),
                    OrgInformationUtil.getCurrentUserId(),
                    Boolean.TRUE,
                    ids);
            Map<Long, DimensionDetailCO> dimensionDetailMap = dimensionDetails
                    .stream()
                    .collect(Collectors.toMap(DimensionDetailCO::getId, e -> e, (k1, k2) -> k1));

            for (int i = 1; i <= typeDimensions.size(); i++) {
                ExpAdjustTypeDimension typeDimension = typeDimensions.get(i -1);
                // 查询所有的维度，只有当是位置是头的时候才返回，以便确认 dimension*Id
                ExpenseDimension expenseDimension = new ExpenseDimension();
                expenseDimension.setDimensionId(typeDimension.getDimensionId());
                expenseDimension.setHeaderFlag(typeDimension.getHeaderFlag());
                expenseDimension.setSequence(typeDimension.getSequence());
                expenseDimension.setRequiredFlag(typeDimension.getRequiredFlag());
                if (dimensionDetailMap.containsKey(typeDimension.getDimensionId())) {
                    DimensionDetailCO detailCO = dimensionDetailMap.get(typeDimension.getDimensionId());
                    expenseDimension.setDimensionField("dimension" + detailCO.getDimensionSequence() + "Id");
                    Map<Long, DimensionItemCO> collect = detailCO
                            .getSubDimensionItemCOS()
                            .stream()
                            .collect(Collectors.toMap(DimensionItemCO::getId, e -> e));
                    expenseDimension.setName(detailCO.getDimensionName());
                    expenseDimension.setOptions(detailCO.getSubDimensionItemCOS());
                    if (collect.containsKey(typeDimension.getDefaultValue())) {
                        DimensionItemCO itemCO = collect.get(typeDimension.getDefaultValue());
                        expenseDimension.setValue(itemCO.getId());
                        expenseDimension.setValueName(itemCO.getDimensionItemName());
                    }else{
                        expenseDimension.setValue(null);
                        expenseDimension.setValueName(null);
                    }
                }else{
                    expenseDimension.setOptions(new ArrayList<>());
                }
                if (expenseDimension.getHeaderFlag() && isHeader) {
                    dimensions.add(expenseDimension);
                }
                if (!isHeader){
                    dimensions.add(expenseDimension);
                }
            }
            dto.setDimensions(dimensions);
        }
        return dto;
    }
}
