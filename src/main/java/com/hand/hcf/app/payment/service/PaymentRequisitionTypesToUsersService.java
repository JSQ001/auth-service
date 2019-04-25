package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.UserGroupCO;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToUsers;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.PaymentRequisitionTypesToUsersMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:34 2018/1/22
 * @Modified by
 */
@Service
public class PaymentRequisitionTypesToUsersService extends BaseService<PaymentRequisitionTypesToUsersMapper, PaymentRequisitionTypesToUsers> {

    private PaymentOrganizationService organizationService;

    public PaymentRequisitionTypesToUsersService(PaymentOrganizationService organizationService){
        this.organizationService = organizationService;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionTypesToUsers saveAcpRequstTypesToUsers(PaymentRequisitionTypesToUsers paymentRequisitionTypesToUsers){
        if (paymentRequisitionTypesToUsers.getId() == null){
            try {
                this.insert(paymentRequisitionTypesToUsers);
            } catch (DuplicateKeyException e) {
                throw new ValidationException(new ValidationError("unq_acp_req_types_to_users", "已经分配的部门或者员工组不允许再分配！"));
            }
        }else{
            this.updateById(paymentRequisitionTypesToUsers);
        }
        return paymentRequisitionTypesToUsers;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<PaymentRequisitionTypesToUsers> getTypesToUsersByTypeId(Long acpReqTypesId){
        List<PaymentRequisitionTypesToUsers> typesToUsers = baseMapper.selectList(
                new EntityWrapper<PaymentRequisitionTypesToUsers>().eq("acp_req_types_id", acpReqTypesId));
        if (typesToUsers == null || typesToUsers.size() == 0){
            return null;
        }else{
            if ("BASIS_02".equals(typesToUsers.get(0).getUserType())){
                List<DepartmentCO> departments = organizationService.listPathByIds(
                        typesToUsers.stream()
                                .map(PaymentRequisitionTypesToUsers::getUserGroupId).collect(Collectors.toList()));

                Map<Long, String> map = new HashMap<>();
                departments.forEach(department -> map.put(department.getId(), department.getName()));

                typesToUsers.forEach(u -> {
                    u.setPathOrName(map.get(u.getUserGroupId()));
                });
            }
            if ("BASIS_03".equals(typesToUsers.get(0).getUserType())){
                List<UserGroupCO> userGroups = organizationService.listUserGroupAndUserIdByGroupIds(
                        typesToUsers.stream()
                                .map(PaymentRequisitionTypesToUsers::getUserGroupId).collect(Collectors.toList()));
                Map<Long, String> map = new HashMap<>();
                userGroups.forEach(userGroupDTO -> map.put(userGroupDTO.getId(), userGroupDTO.getName()));
                typesToUsers.forEach(u -> {
                    u.setPathOrName(map.get(u.getUserGroupId()));
                });
            }
        }
        return typesToUsers;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByTypeId(Long acpReqTypesId){
        baseMapper.delete(new EntityWrapper<PaymentRequisitionTypesToUsers>().eq("acp_req_types_id",acpReqTypesId));
    }
}
