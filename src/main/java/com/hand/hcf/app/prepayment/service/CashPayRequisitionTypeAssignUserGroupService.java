package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignUserGroup;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeAssignUserGroupMapper;
import com.hand.hcf.app.prepayment.utils.RespCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩雪 on 2017/12/29.
 */
@Service
@Transactional
public class CashPayRequisitionTypeAssignUserGroupService extends BaseService<CashPayRequisitionTypeAssignUserGroupMapper,CashPayRequisitionTypeAssignUserGroup> {


    /**
     * 批量新增 预付款单类型关联人员组
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignUserGroup> createCashPayRequisitionTypeAssignUserGroupBatch(List<CashPayRequisitionTypeAssignUserGroup> list){
        list.stream().forEach(cashPayRequisitionTypeAssignUserGroup -> {
            if(cashPayRequisitionTypeAssignUserGroup.getId() != null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_USER_GROUP_ALREADY_EXISTS);
            }

            if (baseMapper.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                            .eq("pay_requisition_type_id",cashPayRequisitionTypeAssignUserGroup.getPayRequisitionTypeId())
                            .eq("user_group_id",cashPayRequisitionTypeAssignUserGroup.getUserGroupId())
            ).size() > 0){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_USER_GROUP_NOT_ALLOWED_TO_REPEAT);
            }

            baseMapper.insert(cashPayRequisitionTypeAssignUserGroup);
        });
        return list;
    }

    /**
     * 批量修改 预付款单类型关联人员组
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignUserGroup> updateCashPayRequisitionTypeAssignUserGroupBatch(List<CashPayRequisitionTypeAssignUserGroup> list){
        list.stream().forEach(cashPayRequisitionTypeAssignUserGroup -> {
            if (cashPayRequisitionTypeAssignUserGroup.getId() == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_USER_GROUP_NOT_EXIST);
            }

            if (cashPayRequisitionTypeAssignUserGroup.getUserGroupId() != null) {
                if (baseMapper.selectList(
                        new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                                .eq("pay_requisition_type_id",baseMapper.selectById(cashPayRequisitionTypeAssignUserGroup.getId()).getPayRequisitionTypeId())
                                .eq("user_group_id",cashPayRequisitionTypeAssignUserGroup.getUserGroupId())
                ).size() > 0){
                    throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_USER_GROUP_NOT_ALLOWED_TO_REPEAT);
                }
            }
            baseMapper.updateById(cashPayRequisitionTypeAssignUserGroup);
        });
        return list;
    }

    /**
     * 批量删除 预付款单类型关联人员组(物理删除)
     *
     * @param list
     */
    @Transactional
    public void deleteCashPayRequisitionTypeAssignUserGroupBatch(List<Long> list){
        list.stream().forEach(id -> {
            CashPayRequisitionTypeAssignUserGroup cashPayRequisitionTypeAssignUserGroup = baseMapper.selectById(id);
            if(cashPayRequisitionTypeAssignUserGroup == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_USER_GROUP_NOT_EXIST);
            }

            baseMapper.deleteById(id);
        });
    }

    /**
     * 根据预付款单类型id查询所有已关联的人员组(分页)
     *
     * @param payRequisitionTypeId
     * @param page
     * @return
     */
    public List<CashPayRequisitionTypeAssignUserGroup> getCashPayRequisitionTypeAssignUserGroupByCond(Long payRequisitionTypeId, Page page){
        List<CashPayRequisitionTypeAssignUserGroup> list = baseMapper.selectPage(page,
                new EntityWrapper<CashPayRequisitionTypeAssignUserGroup>()
                        .eq(payRequisitionTypeId != null,"pay_requisition_type_id",payRequisitionTypeId)
        );
        return list;
    }
}
