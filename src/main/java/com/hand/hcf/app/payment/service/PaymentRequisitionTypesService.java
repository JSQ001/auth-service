package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.FormTypeEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.AuthorizeControllerImpl;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypes;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToCompany;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToUsers;
import com.hand.hcf.app.payment.domain.enumeration.PaymentAssignUserEnum;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.PaymentRequisitionTypesMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesAllDTO;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesAndUserGroupDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

/**
 * @Author: bin.xie
 * @Description: 付款申请单类型定义
 * @Date: Created in 11:14 2018/1/22
 * @Modified by
 */
@Service
@AllArgsConstructor
public class PaymentRequisitionTypesService extends BaseService<PaymentRequisitionTypesMapper,PaymentRequisitionTypes> {

    private final PaymentRequisitionTypesToUsersService paymentRequisitionTypesToUsersService;
    private final PaymentRequisitionTypesToCompanyService paymentRequisitionTypesToCompanyService;
    private final PaymentRequisitionTypesToRelatedService paymentRequisitionTypesToRelatedService;

    private final PaymentOrganizationService organizationService;

    @Autowired
    private AuthorizeControllerImpl authorizeClient;
    /**
     * @Author: bin.xie
     * @Description: 保存付款申请单类型
     * @param: paymentRequstTypes
     * @return: paymentRequstTypes
     * @Date: Created in 2018/1/22 11:17
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionTypesAllDTO savePaymentRequstTypes(PaymentRequisitionTypesAllDTO paymentRequisitionTypesAllDTO){
        if (paymentRequisitionTypesAllDTO.getPaymentRequisitionTypes().getId() == null){
            try {
                this.insert(paymentRequisitionTypesAllDTO.getPaymentRequisitionTypes());
            } catch (DuplicateKeyException e) {
                throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_TYPE_EXISTS);
            }
        }else{
            this.updateById(paymentRequisitionTypesAllDTO.getPaymentRequisitionTypes());
        }
        /*适用员工*/
        //先删除，后新增
        paymentRequisitionTypesToUsersService.deleteByTypeId(paymentRequisitionTypesAllDTO.getPaymentRequisitionTypes().getId());

        paymentRequisitionTypesAllDTO.getPaymentRequisitionTypesToUsers().stream().forEach(u ->{
            u.setAcpReqTypesId(paymentRequisitionTypesAllDTO.getPaymentRequisitionTypes().getId());
            paymentRequisitionTypesToUsersService.saveAcpRequstTypesToUsers(u);
        });

        /*申请类型*/
        paymentRequisitionTypesToRelatedService.deleteByTypeId(paymentRequisitionTypesAllDTO.getPaymentRequisitionTypes().getId());
        paymentRequisitionTypesAllDTO.getPaymentRequisitionTypesToRelateds().stream().forEach(u ->{
            u.setAcpReqTypesId(paymentRequisitionTypesAllDTO.getPaymentRequisitionTypes().getId());
            paymentRequisitionTypesToRelatedService.saveAcpRequstTypesToRelated(u);
        });

        return paymentRequisitionTypesAllDTO;
    }
    /**
     * @Author: bin.xie
     * @Description: 通过账套、代码、名称分页查询付款申请单类型
     * @param: setOfBooksId
     * @param: acpReqTypeCode
     * @param: description
     * @param: page
     * @return: java.util.List<com.hand.hcf.app.payment.domain.PaymentRequisitionTypes>
     * @Date: Created in 2018/1/22 13:16
     * @Modified by
     */
    @Transactional(readOnly = true)
    public List<PaymentRequisitionTypes> getTypesByCondition(Long setOfBooksId,
                                                             String acpReqTypeCode,
                                                             String description,
                                                             Page<PaymentRequisitionTypes> page, boolean dataAuthFlag){

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME, "csh_req_types");
            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_books_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }

        return baseMapper.selectPage(page,new EntityWrapper<PaymentRequisitionTypes>()
                .like(TypeConversionUtils.isNotEmpty(description), "description",description)
                .eq(setOfBooksId != null,"set_of_books_id",setOfBooksId)
                .like(TypeConversionUtils.isNotEmpty(acpReqTypeCode),"acp_req_type_code",acpReqTypeCode)
                .and(!StringUtils.isEmpty(dataAuthLabel), dataAuthLabel)
                .orderBy("enabled",false)
                .orderBy("acp_req_type_code",true));
    }

    /**
     * @Author: bin.xie
     * @Description: 通过ID获取借款申请单
     * @param: id
     * @return: com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesAllDTO
     * @Date: Created in 2018/1/23 13:55
     * @Modified by
     */
    @Transactional(readOnly = true)
    public PaymentRequisitionTypesAllDTO getTypesAllDTOById(Long id){
        PaymentRequisitionTypesAllDTO paymentRequisitionTypesAllDTO = new PaymentRequisitionTypesAllDTO();
        paymentRequisitionTypesAllDTO.setPaymentRequisitionTypes(baseMapper.selectById(id));
        paymentRequisitionTypesAllDTO.setPaymentRequisitionTypesToUsers(paymentRequisitionTypesToUsersService.getTypesToUsersByTypeId(id));
        paymentRequisitionTypesAllDTO.setPaymentRequisitionTypesToRelateds(paymentRequisitionTypesToRelatedService.getRelatedsByTypeId(id));
        return paymentRequisitionTypesAllDTO;
    }

    /**
     * @Author: bin.xie
     * @Description: 根据ID查询类型ID
     * @param: id
     * @return: com.hand.hcf.app.payment.domain.PaymentRequisitionTypes
     * @Date: Created in 2018/1/23 16:36
     * @Modified by
     */
    @Transactional(readOnly = true)
    public PaymentRequisitionTypes getTypesById(Long id){
        return this.selectById(id);
    }


    @Transactional(readOnly = true)
    public List<PaymentRequisitionTypes> selectTypesByCompanyIdAndSetOfBooksId(Long setOfBooksId, Long companyId){

        return baseMapper.selectAcpReqTypesByCompanyId(setOfBooksId,companyId);
    }

    /**
     * @Author: bin.xie
     * @Description: 根据当前机构当前账套分页查询类型
     * @param: setOfBooksId
     * @param: acpReqTypeCode
     * @param: description
     * @param: page
     * @return: java.util.List<com.hand.hcf.app.payment.domain.PaymentRequisitionTypes>
     * @Date: Created in 2018/1/26 13:41
     * @Modified by
     */
    @Transactional(readOnly = true)
    public List<PaymentRequisitionTypes> getTypeBySetBookIdAndCompanyIdAndCondition(Long setOfBooksId,
                                                                                    Long companyId,
                                                                                    String acpReqTypeCode,
                                                                                    String description,
                                                                                    Page<PaymentRequisitionTypes> page,
                                                                                    Boolean isAuth){
        //获取当前员工ID
        UUID userId = OrgInformationUtil.getCurrentUserOid();
        Long departmentId = organizationService.getDepartmentByEmpOid(userId.toString()).getId();
        // 先查询权限为全部人员和当前用户所属的部门的单据类型
        List<PaymentRequisitionTypes> paymentRequisitionTypes = baseMapper.selectByUser(page, setOfBooksId, companyId, acpReqTypeCode, description, departmentId);
        // 然后查询权限为人员组的单据类型

        List<PaymentRequisitionTypesAndUserGroupDTO> list = baseMapper.selectByUserGroup(setOfBooksId, companyId);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(type -> {

                JudgeUserCO judgeUserCO = new JudgeUserCO();
                judgeUserCO.setIdList(type.getUserGroupIds());
                judgeUserCO.setUserId(OrgInformationUtil.getCurrentUserId());
                Boolean isExists = organizationService.judgeUserInUserGroups(judgeUserCO);
                if (isExists) {
                    paymentRequisitionTypes.add(type);
                }
            });
        }

        //获取授权的单据类型
        paymentRequisitionTypes.addAll(getPaymentRequisitionTypeByAuthorize());
        //根据ID去重
        List<PaymentRequisitionTypes> paymentRequisitionTypeList = paymentRequisitionTypes.stream().collect(
                collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(PaymentRequisitionTypes::getId))), ArrayList::new)
        );

        if (isAuth != null && isAuth) {
            paymentRequisitionTypeList.addAll(getPaymentRequisitionTypeByAuthorize());
            //根据ID去重
            paymentRequisitionTypeList = paymentRequisitionTypeList.stream().collect(
                    collectingAndThen(toCollection(() -> new TreeSet<>(comparingLong(PaymentRequisitionTypes::getId))), ArrayList::new)
            );
        }

        return paymentRequisitionTypeList;
    }

    /**
     * 获取当前用户被授权的单据类型
     * @return
     */
    public List<PaymentRequisitionTypes> getPaymentRequisitionTypeByAuthorize(){
        List<PaymentRequisitionTypes> paymentRequisitionTypesList = new ArrayList<>();

        List<FormAuthorizeCO> formAuthorizeCOList = authorizeClient.listFormAuthorizeByDocumentCategoryAndUserId(FormTypeEnum.PAYMENT_REQUISITION.getCode(), OrgInformationUtil.getCurrentUserId());

        for(FormAuthorizeCO item : formAuthorizeCOList) {
            OrganizationUserCO contactCO = new OrganizationUserCO();
            if (item.getMandatorId() != null) {
                contactCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
            }
            List<Long> typeIdList = paymentRequisitionTypesToCompanyService.selectList(
                    new EntityWrapper<PaymentRequisitionTypesToCompany>()
                            .eq(item.getCompanyId() != null, "company_id", item.getCompanyId())
                            .eq(contactCO.getCompanyId() != null, "company_id", contactCO.getCompanyId())
                            .eq("enabled",true)
            ).stream().map(PaymentRequisitionTypesToCompany::getAcpReqTypesId).collect(Collectors.toList());
            if (typeIdList.size() == 0) {
                continue;
            }
            List<PaymentRequisitionTypes> paymentRequisitionTypess = this.selectList(
                    new EntityWrapper<PaymentRequisitionTypes>()
                            .in(typeIdList.size() != 0, "id", typeIdList)
                            .eq(item.getFormId() != null, "id", item.getFormId())
                            .eq("enabled", true));

            paymentRequisitionTypess = paymentRequisitionTypess.stream().filter(paymentRequisitionTypes -> {
                List<PaymentRequisitionTypesToUsers> assignUsers = null;
                List<Long> ids = null;
                // 如果不是全部人员就去查询分配的部门或者人员组
                if (!PaymentAssignUserEnum.USER_ALL.getKey().equals(paymentRequisitionTypes.getApplyEmployee())){
                    assignUsers = paymentRequisitionTypesToUsersService.selectList(
                            new EntityWrapper<PaymentRequisitionTypesToUsers>().eq("acp_req_types_id", paymentRequisitionTypes.getId()));
                    ids = assignUsers.stream().map(PaymentRequisitionTypesToUsers::getUserGroupId).collect(Collectors.toList());
                } else {
                    return true;
                }
                // 部门
                if (PaymentAssignUserEnum.USER_DEPARTMENT.getKey().equals(paymentRequisitionTypes.getApplyEmployee())){
                    if (!CollectionUtils.isEmpty(ids)) {

                        if (item.getMandatorId() != null) {
                            OrganizationUserCO userCO = organizationService.getOrganizationCOByUserId(item.getMandatorId());
                            if (!ids.contains(userCO.getDepartmentId())){
                                return false;
                            }
                        }

                        if (item.getUnitId() != null && !ids.contains(item.getUnitId())) {
                            return false;
                        }
                    }
                }
                // 人员组
                if (PaymentAssignUserEnum.USER_GROUP.getKey().equals(paymentRequisitionTypes.getApplyEmployee())){
                    if (!CollectionUtils.isEmpty(ids)) {

                        if (item.getMandatorId() != null) {
                            JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(ids).userId(item.getMandatorId()).build();
                            if (!organizationService.judgeUserInUserGroups(judgeUserCO)) {
                                return false;
                            }
                        }

                        if (item.getUnitId() != null){
                            List<Long> userIds = organizationService.listUsersByDepartmentId(item.getUnitId()).stream().map(ContactCO::getId).collect(Collectors.toList());
                            for(Long e : userIds){
                                JudgeUserCO judgeUserCO = JudgeUserCO.builder().idList(ids).userId(e).build();
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

            paymentRequisitionTypesList.addAll(paymentRequisitionTypess);
        }
        return paymentRequisitionTypesList;
    }

    /**
     * @Author: bin.xie
     * @Description: 查询该机构分配的付款申请单类型
     * @param: acpReqTypeCode 付款申请单类型代码
     * @param: description 描述
     * @param: id 类型ID
     * @param: page 分页参数
     * @param: companyId  公司id
     * @return: java.util.List<com.hand.hcf.app.payment.domain.PaymentRequisitionTypes>
     * @Date: Created in 2018/4/26 13:13
     * @Modified by
     */
    public List<PaymentRequisitionTypes> getTypeByCompanyIdAndCondition(String acpReqTypeCode,
                                                               String description,
                                                               Long id,
                                                               Page page,
                                                               Long companyId) {
        StringBuffer inSQL = new StringBuffer("id in (SELECT arc.acp_req_types_id FROM csh_req_types_to_company arc\n");
        inSQL.append("WHERE arc.company_id = " + companyId + "\n")
                .append("AND enabled = 1)\n");

        List<PaymentRequisitionTypes> list = baseMapper.selectPage(
                page,
                new EntityWrapper<PaymentRequisitionTypes>()
                .where(inSQL.toString())
                .eq("enabled",true)
                .like(TypeConversionUtils.isNotEmpty(acpReqTypeCode),"acp_req_type_code",acpReqTypeCode)
                .like(TypeConversionUtils.isNotEmpty(description),"description",description)
                .eq(TypeConversionUtils.isNotEmpty(id),"id",id)
        );


        return list;
    }

    public List<ContactCO> listUsersByPaymentRequisitionType(Long id, String userCode, String userName, Page queryPage) {
        List<ContactCO> userCOList = new ArrayList<>();

        PaymentRequisitionTypes paymentRequisitionTypes = this.selectById(id);
        if (paymentRequisitionTypes == null){
            return userCOList;
        }

        List<Long> companyIdList = paymentRequisitionTypesToCompanyService.selectList(
                new EntityWrapper<PaymentRequisitionTypesToCompany>()
                        .eq("enabled", true)
                        .eq("acp_req_types_id", id)
        ).stream().map(PaymentRequisitionTypesToCompany::getCompanyId).collect(toList());

        if (companyIdList.size() == 0){
            return userCOList;
        }

        List<Long> departmentIdList = null;
        List<Long> userGroupIdList = null;

        // 部门
        if (PaymentAssignUserEnum.USER_DEPARTMENT.getKey().equals(paymentRequisitionTypes.getApplyEmployee())){
            departmentIdList = paymentRequisitionTypesToUsersService.selectList(
                    new EntityWrapper<PaymentRequisitionTypesToUsers>()
                            .eq("acp_req_types_id", paymentRequisitionTypes.getId())
            ).stream().map(PaymentRequisitionTypesToUsers::getUserGroupId).collect(Collectors.toList());
        }
        // 人员组
        if (PaymentAssignUserEnum.USER_GROUP.getKey().equals(paymentRequisitionTypes.getApplyEmployee())){
            userGroupIdList = paymentRequisitionTypesToUsersService.selectList(
                    new EntityWrapper<PaymentRequisitionTypesToUsers>()
                            .eq("acp_req_types_id", paymentRequisitionTypes.getId())
            ).stream().map(PaymentRequisitionTypesToUsers::getUserGroupId).collect(Collectors.toList());
        }

        AuthorizeQueryCO queryCO = AuthorizeQueryCO
                .builder()
                .documentCategory(FormTypeEnum.PAYMENT_REQUISITION.getCode())
                .formTypeId(id)
                .companyIdList(companyIdList)
                .departmentIdList(departmentIdList)
                .userGroupIdList(userGroupIdList)
                .currentUserId(OrgInformationUtil.getCurrentUserId())
                .build();
        //bo.liu 修改三方接口
        /*Page<ContactCO> contactCOPage = authorizeClient.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, queryPage);*/
        Page<ContactCO> contactCOPage = authorizeClient.pageUsersByAuthorizeAndCondition(queryCO, userCode, userName, queryPage.getCurrent() - 1, queryPage.getSize());
        queryPage.setTotal(contactCOPage.getTotal());
        userCOList = contactCOPage.getRecords();

        return userCOList;
    }
}
