package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeBasisEnum;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeEmployeeEnum;
import com.hand.hcf.app.common.enums.CashPayRequisitionTypeTypeEnum;
import com.hand.hcf.app.common.enums.FormTypeEnum;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.AuthorizeControllerImpl;
import com.hand.hcf.app.prepayment.domain.*;
import com.hand.hcf.app.prepayment.externalApi.PrepaymentHcfOrganizationInterface;
import com.hand.hcf.app.prepayment.externalApi.PaymentModuleInterface;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeAssignCompanyMapper;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeMapper;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.prepayment.web.dto.CashPayRequisitionTypeDTO;
import com.hand.hcf.app.core.domain.SystemCustomEnumerationType;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

/**
 * Created by 韩雪 on 2017/10/24.
 */
@Service
@Transactional
@AllArgsConstructor
public class CashPayRequisitionTypeService extends BaseService<CashPayRequisitionTypeMapper,CashPayRequisitionType> {
    private static final Logger log = LoggerFactory.getLogger(CashPayRequisitionTypeService.class);

    private final CashPayRequisitionTypeAssignRequisitionTypeService cashPayRequisitionTypeAssignRequisitionTypeService;

    private final CashPayRequisitionTypeAssignTransactionClassService cashPayRequisitionTypeAssignTransactionClassService;

    private final CashPayRequisitionTypeAssignDepartmentService cashPayRequisitionTypeAssignDepartmentService;

    private final CashPayRequisitionTypeAssignUserGroupService cashPayRequisitionTypeAssignUserGroupService;

    private final CashPayRequisitionTypeAssignCompanyMapper cashPayRequisitionTypeAssignCompanyMapper;

    @Autowired
    private PrepaymentHcfOrganizationInterface prepaymentHcfOrganizationInterface;

    @Autowired
    private CashPayRequisitionTypeMapper cashPayRequisitionTypeMapper;
    @Autowired
    private PaymentModuleInterface paymentModuleInterface;
    @Autowired
    private AuthorizeControllerImpl authorizeClient;
    /**
     * 新增 预付款单类型定义
     *
     * @param cashPayRequisitionTypeDTO
     * @return
     */
    @Transactional
    public CashPayRequisitionType createCashPayRequisitionType(CashPayRequisitionTypeDTO cashPayRequisitionTypeDTO){
        //插入预付款单类型定义表
        CashPayRequisitionType dtoCashPayRequisitionType = cashPayRequisitionTypeDTO.getCashPayRequisitionType();
        CashPayRequisitionType cashPayRequisitionType = new CashPayRequisitionType();
        BeanUtils.copyProperties(dtoCashPayRequisitionType,cashPayRequisitionType);
        if (cashPayRequisitionType.getId() != null){
            throw new BizException(RespCode.PREPAY_CASH_PAY_REQUISITION_TYPE_ALREADY_EXISTS);
        }
        if (baseMapper.selectList(
                new EntityWrapper<CashPayRequisitionType>()
                        .eq("set_of_book_id",cashPayRequisitionType.getSetOfBookId())
                        .eq("type_code",cashPayRequisitionType.getTypeCode())
        ).size() > 0){
            throw new BizException(RespCode.PREPAY_CASH_PAY_REQUISITION_TYPE_NOT_ALLOWED_TO_REPEAT);
        }
        if (false == cashPayRequisitionType.getNeedApply() ){
            cashPayRequisitionType.setAllType(CashPayRequisitionTypeTypeEnum.BASIS_00);
        }

        this.insert(cashPayRequisitionType);

        //插入预付款单类型关联申请单类型表
        if ( (true == cashPayRequisitionType.getNeedApply() ) && (CashPayRequisitionTypeTypeEnum.BASIS_02.equals(cashPayRequisitionType.getAllType())) ){
            List<Long> requisitionTypeIdList = cashPayRequisitionTypeDTO.getRequisitionTypeIdList();
            if (requisitionTypeIdList != null) {
                List<CashPayRequisitionTypeAssignRequisitionType> requisitionTypeList = new ArrayList<>();
                requisitionTypeIdList.stream().forEach(requisitionTypeId -> {
                    CashPayRequisitionTypeAssignRequisitionType requisitionType = CashPayRequisitionTypeAssignRequisitionType
                            .builder().payRequisitionTypeId(cashPayRequisitionType.getId()).requisitionTypeId(requisitionTypeId).build();
                    requisitionTypeList.add(requisitionType);
                });
                cashPayRequisitionTypeAssignRequisitionTypeService.createCashPayRequisitionTypeAssignRequisitionTypeBatch(requisitionTypeList);
            }
        }

        //插入预付款单类型关联现金事务分类表
        if ( false == cashPayRequisitionType.getAllClass() ){
            List<Long> transactionClassIdList =cashPayRequisitionTypeDTO.getTransactionClassIdList();
            if (transactionClassIdList != null){
                List<CashPayRequisitionTypeAssignTransactionClass> transactionClassList = new ArrayList<>();
                transactionClassIdList.stream().forEach(transactionClassId -> {
                    CashPayRequisitionTypeAssignTransactionClass transactionClass = CashPayRequisitionTypeAssignTransactionClass
                            .builder().sobPayReqTypeId(cashPayRequisitionType.getId()).transactionClassId(transactionClassId).build();
                    transactionClassList.add(transactionClass);
                });
                cashPayRequisitionTypeAssignTransactionClassService.createCashPayRequisitionTypeAssignTransactionClassBatch(transactionClassList);
            }
        }

        //插入部门或人员组
        if (CashPayRequisitionTypeEmployeeEnum.BASIS_02.equals(cashPayRequisitionType.getApplyEmployee())){
            List<Long> departmentIdList = cashPayRequisitionTypeDTO.getDepartmentOrUserGroupIdList();
            if (departmentIdList != null){
                List<CashPayRequisitionTypeAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId ->{
                    CashPayRequisitionTypeAssignDepartment department = CashPayRequisitionTypeAssignDepartment
                            .builder().payRequisitionTypeId(cashPayRequisitionType.getId()).departmentId(departmentId).build();
                    departmentList.add(department);
                });
                cashPayRequisitionTypeAssignDepartmentService.createCashPayRequisitionTypeAssignDepartmentBatch(departmentList);
            }
        }else if (CashPayRequisitionTypeEmployeeEnum.BASIS_03.equals(cashPayRequisitionType.getApplyEmployee())){
            List<Long> userGroupIdList = cashPayRequisitionTypeDTO.getDepartmentOrUserGroupIdList();
            if (userGroupIdList != null){
                List<CashPayRequisitionTypeAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.stream().forEach(userGroupId -> {
                    CashPayRequisitionTypeAssignUserGroup userGroup = CashPayRequisitionTypeAssignUserGroup
                            .builder().payRequisitionTypeId(cashPayRequisitionType.getId()).userGroupId(userGroupId).build();
                    userGroupList.add(userGroup);
                });
                cashPayRequisitionTypeAssignUserGroupService.createCashPayRequisitionTypeAssignUserGroupBatch(userGroupList);
            }
        }

        //返回给前台新增的预付款单类型
        return cashPayRequisitionType;
    }

    /**
     * 修改 预付款单类型定义
     *
     * @param cashPayRequisitionTypeDTO
     * @return
     */
    @Transactional
    public CashPayRequisitionType updateCashPayRequisitionType(CashPayRequisitionTypeDTO cashPayRequisitionTypeDTO){
        //修改预付款单类型定义表
        CashPayRequisitionType cashPayRequisitionTypeDto = cashPayRequisitionTypeDTO.getCashPayRequisitionType();
        CashPayRequisitionType cashPayRequisitionType = new CashPayRequisitionType();
        BeanUtils.copyProperties(cashPayRequisitionTypeDto,cashPayRequisitionType);

        CashPayRequisitionType payRequisitionType = baseMapper.selectById(cashPayRequisitionType.getId());
        if (payRequisitionType == null){
            throw new BizException(RespCode.PREPAY_CASH_PAY_REQUISITION_TYPE_NOT_EXIST);
        }
        cashPayRequisitionType.setTypeCode(payRequisitionType.getTypeCode());
//        //对formOid进行判断
//        if (null == cashPayRequisitionType.getFormOid()) {
            //如果更新时的formOid参数为null，则将预付款单类型数据库中的formOid、formName、formType数据清空
        if (null == cashPayRequisitionType.getFormOid()) {
            cashPayRequisitionType.setFormOid(null);
            cashPayRequisitionType.setFormName(null);
            cashPayRequisitionType.setFormType(null);
        }
        //如果needApply为false，则将"是否全部申请单类型->allType"、"申请单依据->applicationFormBasis"的数据清空，设为BASIS_00
        if (false == cashPayRequisitionType.getNeedApply() ){
            cashPayRequisitionType.setAllType(CashPayRequisitionTypeTypeEnum.BASIS_00);
            cashPayRequisitionType.setApplicationFormBasis(CashPayRequisitionTypeBasisEnum.BASIS_00);
        }
        this.updateById(cashPayRequisitionType);

        //修改预付款单类型关联申请单类型表
        if (false == cashPayRequisitionType.getNeedApply() ){
            if (true == payRequisitionType.getNeedApply() ){
                //将申请单依据数据清空
//                cashPayRequisitionType.setApplicationFormBasis(null);
                //将申请单类型数据清空
                if (CashPayRequisitionTypeTypeEnum.BASIS_02.equals(payRequisitionType.getAllType())){
                    List<Long> typeIdList = cashPayRequisitionTypeAssignRequisitionTypeService.selectList(
                            new EntityWrapper<CashPayRequisitionTypeAssignRequisitionType>()
                                    .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                    ).stream().map(CashPayRequisitionTypeAssignRequisitionType::getId).collect(toList());
                    if (typeIdList != null){
                        cashPayRequisitionTypeAssignRequisitionTypeService.deleteCashPayRequisitionTypeAssignRequisitionTypeBatch(typeIdList);
                    }
                }

            }
        }else {
            if (false == payRequisitionType.getNeedApply() ){
                if (CashPayRequisitionTypeTypeEnum.BASIS_02.equals(cashPayRequisitionType.getAllType())){
                    List<Long> requisitionTypeIdList = cashPayRequisitionTypeDTO.getRequisitionTypeIdList();
                    if (requisitionTypeIdList != null) {
                        List<CashPayRequisitionTypeAssignRequisitionType> requisitionTypeList = new ArrayList<>();
                        requisitionTypeIdList.stream().forEach(requisitionTypeId -> {
                            CashPayRequisitionTypeAssignRequisitionType requisitionType = CashPayRequisitionTypeAssignRequisitionType
                                    .builder().payRequisitionTypeId(cashPayRequisitionType.getId()).requisitionTypeId(requisitionTypeId).build();
                            requisitionTypeList.add(requisitionType);
                        });
                        cashPayRequisitionTypeAssignRequisitionTypeService.createCashPayRequisitionTypeAssignRequisitionTypeBatch(requisitionTypeList);
                    }
                }
            }else {
                if (CashPayRequisitionTypeTypeEnum.BASIS_02.equals(cashPayRequisitionType.getAllType())){
                    //将原来的数据删除
                    if (CashPayRequisitionTypeTypeEnum.BASIS_02.equals(payRequisitionType.getAllType())){
                        List<Long> typeIdList = cashPayRequisitionTypeAssignRequisitionTypeService.selectList(
                                new EntityWrapper<CashPayRequisitionTypeAssignRequisitionType>()
                                        .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                        ).stream().map(CashPayRequisitionTypeAssignRequisitionType::getId).collect(toList());
                        if (typeIdList != null){
                            cashPayRequisitionTypeAssignRequisitionTypeService.deleteCashPayRequisitionTypeAssignRequisitionTypeBatch(typeIdList);
                        }
                    }
                    //插入新的数据
                    List<Long> requisitionTypeIdList = cashPayRequisitionTypeDTO.getRequisitionTypeIdList();
                    if (requisitionTypeIdList != null) {
                        List<CashPayRequisitionTypeAssignRequisitionType> requisitionTypeList = new ArrayList<>();
                        requisitionTypeIdList.stream().forEach(requisitionTypeId -> {
                            CashPayRequisitionTypeAssignRequisitionType requisitionType = CashPayRequisitionTypeAssignRequisitionType
                                    .builder().payRequisitionTypeId(cashPayRequisitionType.getId()).requisitionTypeId(requisitionTypeId).build();
                            requisitionTypeList.add(requisitionType);
                        });
                        cashPayRequisitionTypeAssignRequisitionTypeService.createCashPayRequisitionTypeAssignRequisitionTypeBatch(requisitionTypeList);
                    }
                }else {
                    //将原来的数据删除
                    if (CashPayRequisitionTypeTypeEnum.BASIS_02.equals(payRequisitionType.getAllType())){
                        List<Long> typeIdList = cashPayRequisitionTypeAssignRequisitionTypeService.selectList(
                                new EntityWrapper<CashPayRequisitionTypeAssignRequisitionType>()
                                        .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                        ).stream().map(CashPayRequisitionTypeAssignRequisitionType::getId).collect(toList());
                        if (typeIdList != null){
                            cashPayRequisitionTypeAssignRequisitionTypeService.deleteCashPayRequisitionTypeAssignRequisitionTypeBatch(typeIdList);
                        }
                    }
                }
            }
        }

        //修改预付款单类型关联现金事务分类表
        cashPayRequisitionTypeAssignTransactionClassService.deleteDataByTypeId(cashPayRequisitionType.getId());
        if (!cashPayRequisitionType.getAllClass()){
            List<CashPayRequisitionTypeAssignTransactionClass> transactionClassList = new ArrayList<>();
            List<Long> transactionClassIdList = cashPayRequisitionTypeDTO.getTransactionClassIdList();
            if (CollectionUtils.isEmpty(transactionClassIdList)){
                throw new BizException(RespCode.PREPAY_CASH_REALTATION_SHOULD_NOT_EMPTY);
            }
            transactionClassIdList.stream().forEach(transactionClassId -> {
                CashPayRequisitionTypeAssignTransactionClass transactionClass = CashPayRequisitionTypeAssignTransactionClass
                        .builder().sobPayReqTypeId(cashPayRequisitionType.getId()).transactionClassId(transactionClassId).build();
                transactionClassList.add(transactionClass);
            });
            cashPayRequisitionTypeAssignTransactionClassService.insertBatch(transactionClassList);
        }

        //修改预付款单类型关联部门或人员组
        //删除原有数据
        if (CashPayRequisitionTypeEmployeeEnum.BASIS_02.equals(payRequisitionType.getApplyEmployee())){
            List<Long> departmentIdList = cashPayRequisitionTypeAssignDepartmentService.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                            .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
            ).stream().map(CashPayRequisitionTypeAssignDepartment::getId).collect(toList());
            cashPayRequisitionTypeAssignDepartmentService.deleteBatchIds(departmentIdList);
        }else if (CashPayRequisitionTypeEmployeeEnum.BASIS_03.equals(payRequisitionType.getApplyEmployee())){
            List<Long> userGroupIdList = cashPayRequisitionTypeAssignUserGroupService.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                            .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
            ).stream().map(CashPayRequisitionTypeAssignUserGroup::getId).collect(toList());
            cashPayRequisitionTypeAssignUserGroupService.deleteBatchIds(userGroupIdList);
        }
        //插入更新的数据
        if (CashPayRequisitionTypeEmployeeEnum.BASIS_02.equals(cashPayRequisitionType.getApplyEmployee())){
            List<Long> departmentIdList = cashPayRequisitionTypeDTO.getDepartmentOrUserGroupIdList();
            if (departmentIdList != null){
                List<CashPayRequisitionTypeAssignDepartment> departmentList = new ArrayList<>();
                departmentIdList.stream().forEach(departmentId ->{
                    CashPayRequisitionTypeAssignDepartment department = CashPayRequisitionTypeAssignDepartment
                            .builder().payRequisitionTypeId(cashPayRequisitionType.getId()).departmentId(departmentId).build();
                    departmentList.add(department);
                });
                cashPayRequisitionTypeAssignDepartmentService.createCashPayRequisitionTypeAssignDepartmentBatch(departmentList);
            }
        }else if (CashPayRequisitionTypeEmployeeEnum.BASIS_03.equals(cashPayRequisitionType.getApplyEmployee())){
            List<Long> userGroupIdList = cashPayRequisitionTypeDTO.getDepartmentOrUserGroupIdList();
            if (userGroupIdList != null){
                List<CashPayRequisitionTypeAssignUserGroup> userGroupList = new ArrayList<>();
                userGroupIdList.stream().forEach(userGroupId -> {
                    CashPayRequisitionTypeAssignUserGroup userGroup = CashPayRequisitionTypeAssignUserGroup
                            .builder().payRequisitionTypeId(cashPayRequisitionType.getId()).userGroupId(userGroupId).build();
                    userGroupList.add(userGroup);
                });
                cashPayRequisitionTypeAssignUserGroupService.createCashPayRequisitionTypeAssignUserGroupBatch(userGroupList);
            }
        }


        //返回修改以后的预付款单类型定义
        return cashPayRequisitionType;
    }

    /**
     * 根据ID查询 预付款单类型定义
     *
     * @param id
     * @return
     */
    public CashPayRequisitionTypeDTO getCashPayRequisitionType(Long id){
        CashPayRequisitionTypeDTO cashPayRequisitionTypeDTO = new CashPayRequisitionTypeDTO();

        //返回预付款单类型数据
        CashPayRequisitionType cashPayRequisitionType = baseMapper.selectById(id);
        //返回账套code、账套name
        SetOfBooksInfoCO standardDto = prepaymentHcfOrganizationInterface.getSetOfBookById(cashPayRequisitionType.getSetOfBookId());
        if (standardDto != null) {
            cashPayRequisitionType.setSetOfBookCode(standardDto.getSetOfBooksCode());
            cashPayRequisitionType.setSetOfBookName(standardDto.getSetOfBooksName());
        }
        //返回付款方式类型name
        if (cashPayRequisitionType.getPaymentMethodCategory() != null) {
            cashPayRequisitionType.setPaymentMethodCategoryName(prepaymentHcfOrganizationInterface.getSysCodeValue(SystemCustomEnumerationType.CSH_PAYMENT_TYPE,
                    cashPayRequisitionType.getPaymentMethodCategory(), RespCode.SYS_CODE_TYPE_NOT_EXIT).get(cashPayRequisitionType.getPaymentMethodCategory()));
        }
        CashPayRequisitionType dtoCashPayRequisitionType = new CashPayRequisitionType();
        BeanUtils.copyProperties(cashPayRequisitionType,dtoCashPayRequisitionType);

        cashPayRequisitionTypeDTO.setCashPayRequisitionType(dtoCashPayRequisitionType);

        //返回预付款单类型关联申请单类型表数据
        if (cashPayRequisitionType.getAllType().equals(CashPayRequisitionTypeTypeEnum.BASIS_02)) {
            List<Long> requisitionTypeIdList = cashPayRequisitionTypeAssignRequisitionTypeService.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignRequisitionType>()
                            .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
            ).stream().map(CashPayRequisitionTypeAssignRequisitionType::getRequisitionTypeId).collect(toList());
            //返回Long 类型的申请单类型id集合
            cashPayRequisitionTypeDTO.setRequisitionTypeIdList(requisitionTypeIdList);
        }

        //返回预付款单类型关联现金事务分类表
        if (cashPayRequisitionType.getAllClass() == false){
            List<Long> transactionClassIdList = cashPayRequisitionTypeAssignTransactionClassService.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignTransactionClass>()
                            .eq("sob_pay_req_type_id",id)
            ).stream().map(CashPayRequisitionTypeAssignTransactionClass::getTransactionClassId).collect(toList());
            //jiu.zhao 修改三方接口 20190328
            /*List<CashTransactionClassCO> classDTOS = paymentModuleInterface.listCashTransactionClassByIdList(transactionClassIdList);
            List<Long> ids = classDTOS.stream().map(CashTransactionClassCO::getId).collect(Collectors.toList());
            //返回Long 类型的现金事务分类id集合
            cashPayRequisitionTypeDTO.setTransactionClassIdList(ids);*/
        }
        //返回预付款单类型关联的部门或人员组
        if (!cashPayRequisitionType.getApplyEmployee().equals(CashPayRequisitionTypeEmployeeEnum.BASIS_01)){
            List<Long> departmentOrUserGroupIdList = new ArrayList<>();
            if (cashPayRequisitionType.getApplyEmployee().equals(CashPayRequisitionTypeEmployeeEnum.BASIS_02)){
                departmentOrUserGroupIdList = cashPayRequisitionTypeAssignDepartmentService.selectList(
                        new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                                .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                ).stream().map(CashPayRequisitionTypeAssignDepartment::getDepartmentId).collect(toList());
                if (departmentOrUserGroupIdList != null){
                    List<AssignDepartmentOrUserGroupCO> departmentGroupDepartmentList = new ArrayList<>();
                    List<DepartmentCO> departmentList = prepaymentHcfOrganizationInterface.getDepartmentByDepartmentIds(departmentOrUserGroupIdList);
                    departmentList.forEach(e ->{
                        AssignDepartmentOrUserGroupCO organizationStandardDto = new AssignDepartmentOrUserGroupCO();
                        organizationStandardDto.setId(e.getId());
                        organizationStandardDto.setName(e.getName());
                        departmentGroupDepartmentList.add(organizationStandardDto);
                    });
                    cashPayRequisitionTypeDTO.setDepartmentOrUserGroupList(departmentGroupDepartmentList);
                }
            }else if (cashPayRequisitionType.getApplyEmployee().equals(CashPayRequisitionTypeEmployeeEnum.BASIS_03)){
                departmentOrUserGroupIdList = cashPayRequisitionTypeAssignUserGroupService.selectList(
                        new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                                .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                ).stream().map(CashPayRequisitionTypeAssignUserGroup::getUserGroupId).collect(toList());
                if (departmentOrUserGroupIdList != null){
                    List<UserGroupCO> userGroupList = prepaymentHcfOrganizationInterface.listUserGroupAndUserIdByGroupIds(departmentOrUserGroupIdList);
                    List<AssignDepartmentOrUserGroupCO> departmentGroupDepartmentList = new ArrayList<>();
                    userGroupList.forEach(e ->{
                        AssignDepartmentOrUserGroupCO organizationStandardDto = new AssignDepartmentOrUserGroupCO();
                        organizationStandardDto.setId(e.getId());
                        organizationStandardDto.setName(e.getName());
                        departmentGroupDepartmentList.add(organizationStandardDto);
                    });
                    cashPayRequisitionTypeDTO.setDepartmentOrUserGroupList(departmentGroupDepartmentList);
                }
            }
            cashPayRequisitionTypeDTO.setDepartmentOrUserGroupIdList(departmentOrUserGroupIdList);
        }


        //返回CashPayRequisitionTypeDTO
        return cashPayRequisitionTypeDTO;
    }

    /**
     * 自定义条件查询 预付款单类型定义(分页)
     *
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @param page
     * @return
     */
    public List<CashPayRequisitionType> getCashPayRequisitionTypeByCond(Long setOfBookId, String typeCode, String typeName,String paymentMethodCategory,Boolean isEnabled, Page page){
        List<CashPayRequisitionType> list = new ArrayList<>();

        if (setOfBookId == null){
            return list;
        }

        list = baseMapper.selectPage(page,
                new EntityWrapper<CashPayRequisitionType>()
                .where("deleted = false")
                .eq("set_of_book_id",setOfBookId)
                .like(typeCode != null, "type_code",typeCode, SqlLike.DEFAULT)
                .like(typeName != null, "type_name",typeName, SqlLike.DEFAULT)
                .eq(paymentMethodCategory != null,"payment_method_category",paymentMethodCategory)
                .eq(isEnabled != null, "enabled",isEnabled)
                .orderBy("enabled",false)
                .orderBy("type_code")
        );
        for (CashPayRequisitionType cashPayRequisitionType : list){
            //返回账套code、账套name
            SetOfBooksInfoCO standardDto = prepaymentHcfOrganizationInterface.getSetOfBookById(cashPayRequisitionType.getSetOfBookId());
            if (standardDto != null) {
                cashPayRequisitionType.setSetOfBookCode(standardDto.getSetOfBooksCode());
                cashPayRequisitionType.setSetOfBookName(standardDto.getSetOfBooksName());
            }

            //返回付款方式类型name
            if (cashPayRequisitionType.getPaymentMethodCategory() != null) {
                cashPayRequisitionType.setPaymentMethodCategoryName(prepaymentHcfOrganizationInterface.getSysCodeValue(
                        SystemCustomEnumerationType.CSH_PAYMENT_TYPE, cashPayRequisitionType.getPaymentMethodCategory(),
                        RespCode.SYS_CODE_TYPE_NOT_EXIT).get(cashPayRequisitionType.getPaymentMethodCategory()));
            }
        }
        return list;
    }

    /**
     * 自定义条件查询 预付款单类型定义(不分页)
     *
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @return
     */
    public List<CashPayRequisitionType> getCashPayRequisitionTypeAllByCond(Long setOfBookId, String typeCode, String typeName, Boolean isEnabled, String companyCode, String companyName, Long companyId, Boolean assginEnable){
        List<CashPayRequisitionType> list = new ArrayList<>();

        if (setOfBookId == null){
            return list;
        }

        List<Long> longs = new ArrayList<>();
        List<Long> typeIds = new ArrayList<>();

        //如果传了公司id，公司code和name不生效
        if(companyId !=null ){
            longs = Arrays.asList(companyId);
        }else {
            Pageable pageable =  PageRequest.of(0,10000);
              ///这里仅仅设置10000，因为es限制了10000...
            Page page = PageUtil.getPage(pageable);
            Page<CompanyCO> companies = prepaymentHcfOrganizationInterface.pageBySetOfBooksIdConditionByIgnoreIds(setOfBookId, companyCode, companyName, null, null, null, page);
            if(!CollectionUtils.isEmpty(companies.getRecords())){
                longs = companies.getRecords().stream().map(CompanyCO::getId).collect(toList());
            }else {
                return list;
            }
        }


        //查询公司分配的预付款单据类型
         typeIds = cashPayRequisitionTypeAssignCompanyMapper.selectList(
                new EntityWrapper<CashPayRequisitionTypeAssignCompany>()
                        .in(!CollectionUtils.isEmpty(longs), "company_id", longs)
                        .eq(assginEnable!=null,"enabled",assginEnable)
        ).stream().map(CashPayRequisitionTypeAssignCompany::getSobPayReqTypeId).collect(toList());


        list = baseMapper.selectList(
                new EntityWrapper<CashPayRequisitionType>()
                .where("deleted = false")
                .eq("set_of_book_id",setOfBookId)
                .like(typeCode != null, "type_code",typeCode, SqlLike.DEFAULT)
                .like(typeName != null, "type_name",typeName, SqlLike.DEFAULT)
                .eq(isEnabled != null, "enabled",isEnabled)
                .in(!CollectionUtils.isEmpty(typeIds),"id",typeIds)
                .orderBy("type_code"));
        for (CashPayRequisitionType cashPayRequisitionType : list){
            //返回账套code、账套name
            SetOfBooksInfoCO standardCo = prepaymentHcfOrganizationInterface.getSetOfBookById(cashPayRequisitionType.getSetOfBookId());
            if (standardCo != null) {
                cashPayRequisitionType.setSetOfBookCode(standardCo.getSetOfBooksCode());
                cashPayRequisitionType.setSetOfBookName(standardCo.getSetOfBooksName());
            }

            //返回付款方式类型name
            if (cashPayRequisitionType.getPaymentMethodCategory() != null) {
                cashPayRequisitionType.setPaymentMethodCategoryName(prepaymentHcfOrganizationInterface.getSysCodeValue(
                        SystemCustomEnumerationType.CSH_PAYMENT_TYPE,cashPayRequisitionType.getPaymentMethodCategory(),
                        RespCode.SYS_CODE_TYPE_NOT_EXIT
                ).get(cashPayRequisitionType.getPaymentMethodCategory()));
            }
        }
        return list;
    }

    /**
     * 根据预付款单类型id，获取其下已分配的现金事务分类
     * 为预付款单提供
     *
     * @param typeId
     * @return
     */
    public List<CashTransactionClassCO> getTransactionClassByTypeId(Long typeId){
        List<CashTransactionClassCO> list = new ArrayList<>();

        CashPayRequisitionType cashPayRequisitionType = this.selectById(typeId);
        if (cashPayRequisitionType != null){
            if (cashPayRequisitionType.getAllClass() == true){
                //jiu.zhao 修改三方接口 20190328
                //list = paymentModuleInterface.listCashTransactionClassBySetOfBookId(cashPayRequisitionType.getSetOfBookId());
            }else if (cashPayRequisitionType.getAllClass() == false){
                List<Long> transactionClassIdList = cashPayRequisitionTypeAssignTransactionClassService.selectList(
                        new EntityWrapper<CashPayRequisitionTypeAssignTransactionClass>()
                                .eq("sob_pay_req_type_id",typeId)
                ).stream().map(CashPayRequisitionTypeAssignTransactionClass::getTransactionClassId).collect(toList());
                //jiu.zhao 修改三方接口 20190328
                //list = paymentModuleInterface.listCashTransactionClassByIdList(transactionClassIdList);
            }
        }
        return list;
    }


    /**
     * 给新建预付款时选择预付款单类型提供
     * 根据想要新建预付款的人来筛选预付款单类型
     *
     * @param userId
     * @param setOfBookId
     * @param typeCode
     * @param typeName
     * @param isEnabled
     * @return
     */
    public List<CashPayRequisitionType> getCashPayRequisitionTypeByEmployeeId(Long userId, Long setOfBookId, String typeCode, String typeName, Boolean isEnabled){
        List<CashPayRequisitionType> list = baseMapper.selectList(
                new EntityWrapper<CashPayRequisitionType>()
                        .eq("deleted",false)
                        .eq(setOfBookId != null,"set_of_book_id",setOfBookId)
                        .like(typeCode != null,"type_code",typeCode,SqlLike.DEFAULT)
                        .like(typeName != null,"type_name",typeName,SqlLike.DEFAULT)
                        .eq(isEnabled != null,"enabled",isEnabled)
                        .orderBy("type_code")
        );
        //存放从分配人员权限中筛选出来的预付款单类型
        List<CashPayRequisitionType> list1 = new ArrayList<>();
        //存放从分配公司中筛选出来的预付款单类型
        List<CashPayRequisitionType> list2 = new ArrayList<>();
        //最终分页返回的集合
        List<CashPayRequisitionType> list3 = new ArrayList<>();
        if ( list.size() > 0 ){
            list.stream().forEach(cashPayRequisitionType -> {
                if (CashPayRequisitionTypeEmployeeEnum.BASIS_01.equals(cashPayRequisitionType.getApplyEmployee())){
                    list1.add(cashPayRequisitionType);
                }else if (CashPayRequisitionTypeEmployeeEnum.BASIS_02.equals(cashPayRequisitionType.getApplyEmployee())){
                    List<Long> departmentIdList = cashPayRequisitionTypeAssignDepartmentService.selectList(
                            new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                                    .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                    ).stream().map(CashPayRequisitionTypeAssignDepartment::getDepartmentId).collect(toList());
                    if (departmentIdList.size() > 0 ) {
                        /*JudgeUserCO judgeUserCO = JudgeUserCO.builder().userId(userId).idList(departmentIdList).build();
                        Boolean result = hcfOrganizationInterface.judgeDepartmentAndUser(judgeUserCO);
                        if (result == true) {
                            list1.add(cashPayRequisitionType);
                        }*/
                        DepartmentCO departmentByEmployeeId = prepaymentHcfOrganizationInterface.getDepartmentByEmployeeId(userId);
                        if (departmentIdList.contains(departmentByEmployeeId.getId())){
                            list1.add(cashPayRequisitionType);
                        }
                    }
                }else if (CashPayRequisitionTypeEmployeeEnum.BASIS_03.equals(cashPayRequisitionType.getApplyEmployee())){
                    List<Long> userGroupIdList = cashPayRequisitionTypeAssignUserGroupService.selectList(
                            new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                                    .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                    ).stream().map(CashPayRequisitionTypeAssignUserGroup::getUserGroupId).collect(toList());
                    if (userGroupIdList.size() > 0) {
                        JudgeUserCO judgeUserCO = JudgeUserCO.builder().userId(userId).idList(userGroupIdList).build();
                        Boolean result = prepaymentHcfOrganizationInterface.judgeUserInUserGroups(judgeUserCO);
                        if (result == true) {
                            list1.add(cashPayRequisitionType);
                        }
                    }
                }
            });
            if (list1.size() > 0) {
                List<Long> ids=new ArrayList<>();
                ids.add(userId);
                //jiu.zhao 修改三方接口 20190328
                /*List<ContactCO> userInfoCOS = hcfOrganizationInterface.listByUserIdsConditionByKeyWord(ids,null);
                List<CashPayRequisitionTypeAssignCompany> cashPayRequisitionTypeAssignCompanyList = cashPayRequisitionTypeAssignCompanyMapper.selectList(
                        new EntityWrapper<CashPayRequisitionTypeAssignCompany>()
                                .eq("enabled", true)
                                .eq("company_id", userInfoCOS.get(0).getCompanyId()));
                if (!CollectionUtils.isEmpty(cashPayRequisitionTypeAssignCompanyList)) {
                    list1.stream().forEach(u->{
                        cashPayRequisitionTypeAssignCompanyList.stream().forEach(cashPayRequisitionTypeAssignCompany->{
                          if( cashPayRequisitionTypeAssignCompany.getSobPayReqTypeId().equals(u.getId()))
                          {
                              list2.add(u);
                          }
                        });
                    });
                }*/
            }
        }

        //继续筛选未筛选的预付款单类型
        list2.addAll(getCashPayRequisitionTypeByAuthorize());
        //根据ID去重
        list3 = list2.stream().collect(
                collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(CashPayRequisitionType::getId))), ArrayList::new)
        );

        return list3;
    }

    /**
     * 获取当前用户被授权的单据类型
     * @return
     */
    public List<CashPayRequisitionType> getCashPayRequisitionTypeByAuthorize(){
        List<CashPayRequisitionType> cashPayRequisitionTypeList = new ArrayList<>();

        List<FormAuthorizeCO> formAuthorizeCOList = authorizeClient.listFormAuthorizeByDocumentCategoryAndUserId(FormTypeEnum.PREPAYMENT.getCode(), OrgInformationUtil.getCurrentUserId());

        for(FormAuthorizeCO item : formAuthorizeCOList) {
            OrganizationUserCO contactCO = new OrganizationUserCO();
            if (item.getMandatorId() != null) {
                contactCO = prepaymentHcfOrganizationInterface.getOrganizationCOByUserId(item.getMandatorId());
            }
            List<Long> typeIdList = cashPayRequisitionTypeAssignCompanyMapper.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignCompany>()
                            .eq(item.getCompanyId() != null, "company_id", item.getCompanyId())
                            .eq(contactCO.getCompanyId() != null, "company_id", contactCO.getCompanyId())
                            .eq("enabled",true)
            ).stream().map(CashPayRequisitionTypeAssignCompany::getSobPayReqTypeId).collect(Collectors.toList());
            if (typeIdList.size() == 0) {
                continue;
            }
            List<CashPayRequisitionType> cashPayRequisitionTypes = this.selectList(
                    new EntityWrapper<CashPayRequisitionType>()
                            .in(typeIdList.size() != 0, "id", typeIdList)
                            .eq(item.getFormId() != null, "id", item.getFormId())
                            .eq("enabled", true));

            cashPayRequisitionTypes = cashPayRequisitionTypes.stream().filter(cashPayRequisitionType -> {

                //全部人员
                if (CashPayRequisitionTypeEmployeeEnum.BASIS_01.equals(cashPayRequisitionType.getApplyEmployee())){
                    return true;
                }

                if (CashPayRequisitionTypeEmployeeEnum.BASIS_02.equals(cashPayRequisitionType.getApplyEmployee())){
                    List<Long> departmentIdList = cashPayRequisitionTypeAssignDepartmentService.selectList(
                            new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                                    .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                    ).stream().map(CashPayRequisitionTypeAssignDepartment::getDepartmentId).collect(toList());
                    if (!CollectionUtils.isEmpty(departmentIdList)) {

                        if (item.getMandatorId() != null) {
                            OrganizationUserCO userCO = prepaymentHcfOrganizationInterface.getOrganizationCOByUserId(item.getMandatorId());
                            if (!departmentIdList.contains(userCO.getDepartmentId())){
                                return false;
                            }
                        }

                        if (item.getUnitId() != null && !departmentIdList.contains(item.getUnitId())) {
                            return false;
                        }
                    }
                }else if (CashPayRequisitionTypeEmployeeEnum.BASIS_03.equals(cashPayRequisitionType.getApplyEmployee())){
                    List<Long> userGroupIdList = cashPayRequisitionTypeAssignUserGroupService.selectList(
                            new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                                    .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
                    ).stream().map(CashPayRequisitionTypeAssignUserGroup::getUserGroupId).collect(toList());
                    if (!CollectionUtils.isEmpty(userGroupIdList)) {

                        if (item.getMandatorId() != null) {
                            JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(userGroupIdList).userId(item.getMandatorId()).build();
                            if (!prepaymentHcfOrganizationInterface.judgeUserInUserGroups(judgeUserCO)) {
                                return false;
                            }
                        }

                        if (item.getUnitId() != null){
                            List<Long> userIds = prepaymentHcfOrganizationInterface.listUsersByDepartmentId(item.getUnitId()).stream().map(ContactCO::getId).collect(Collectors.toList());
                            for(Long e : userIds){
                                JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(userGroupIdList).userId(e).build();
                                if (!prepaymentHcfOrganizationInterface.judgeUserInUserGroups(judgeUserCO)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                }
                return true;
            }).collect(Collectors.toList());
            cashPayRequisitionTypeList.addAll(cashPayRequisitionTypes);
        }
        return cashPayRequisitionTypeList;
    }

    public CashPayRequisitionType getCashPayRequisitionTypeById(Long typeId){
        return  cashPayRequisitionTypeMapper.selectById(typeId);
    }

    /**
     * 根据单据id查询用户（登录人和委托人）
     * @param id
     * @return
     */
    public List<ContactCO> listUsersByCashSobPayReqTypeId(Long id, String userCode, String userName, Page queryPage) {
        List<ContactCO> userCOList = new ArrayList<>();

        CashPayRequisitionType cashPayRequisitionType = this.getCashPayRequisitionTypeById(id);
        if (cashPayRequisitionType == null){
            return userCOList;
        }

        List<Long> companyIdList = cashPayRequisitionTypeAssignCompanyMapper.selectList(
                new EntityWrapper<CashPayRequisitionTypeAssignCompany>()
                        .eq("enabled", true)
                        .eq("sob_pay_req_type_id", id)
        ).stream().map(CashPayRequisitionTypeAssignCompany::getCompanyId).collect(toList());

        if (companyIdList.size() == 0){
            return userCOList;
        }

        List<Long> departmentIdList = null;
        List<Long> userGroupIdList = null;

        // 部门
        if (CashPayRequisitionTypeEmployeeEnum.BASIS_02.equals(cashPayRequisitionType.getApplyEmployee())){
            departmentIdList = cashPayRequisitionTypeAssignDepartmentService.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                            .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
            ).stream().map(CashPayRequisitionTypeAssignDepartment::getDepartmentId).collect(toList());
        }
        // 人员组
        if (CashPayRequisitionTypeEmployeeEnum.BASIS_03.equals(cashPayRequisitionType.getApplyEmployee())){
            userGroupIdList = cashPayRequisitionTypeAssignUserGroupService.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                            .eq("pay_requisition_type_id",cashPayRequisitionType.getId())
            ).stream().map(CashPayRequisitionTypeAssignUserGroup::getUserGroupId).collect(toList());
        }

        AuthorizeQueryCO queryCO = AuthorizeQueryCO
                .builder()
                .documentCategory(FormTypeEnum.PREPAYMENT.getCode())
                .formTypeId(id)
                .companyIdList(companyIdList)
                .departmentIdList(departmentIdList)
                .userGroupIdList(userGroupIdList)
                .currentUserId(OrgInformationUtil.getCurrentUserId())
                .build();
        Page<ContactCO> contactCOPage = authorizeClient.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, queryPage);
        queryPage.setTotal(contactCOPage.getTotal());
        userCOList = contactCOPage.getRecords();

        return userCOList;
    }
}
