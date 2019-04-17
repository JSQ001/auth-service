package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignDepartment;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeAssignDepartmentMapper;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩雪 on 2017/12/29.
 */
@Service
@Transactional
public class CashPayRequisitionTypeAssignDepartmentService extends BaseService<CashPayRequisitionTypeAssignDepartmentMapper,CashPayRequisitionTypeAssignDepartment> {


    /**
     * 批量新增 预付款单类型关联部门
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignDepartment> createCashPayRequisitionTypeAssignDepartmentBatch(List<CashPayRequisitionTypeAssignDepartment> list){
        list.stream().forEach(cashPayRequisitionTypeAssignDepartment -> {
            if(cashPayRequisitionTypeAssignDepartment.getId() != null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_DEPARTMENT_ALREADY_EXISTS);
            }

            //设置条件
            if (baseMapper.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                            .eq("pay_requisition_type_id",cashPayRequisitionTypeAssignDepartment.getPayRequisitionTypeId())
                            .eq("department_id",cashPayRequisitionTypeAssignDepartment.getDepartmentId())
            ).size() > 0){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_DEPARTMENT_NOT_ALLOWED_TO_REPEAT);
            }

            baseMapper.insert(cashPayRequisitionTypeAssignDepartment);
        });
        return list;
    }

    /**
     * 批量修改 预付款单类型关联部门
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignDepartment> updateCashPayRequisitionTypeAssignDepartmentBatch(List<CashPayRequisitionTypeAssignDepartment> list){
        list.stream().forEach(cashPayRequisitionTypeAssignDepartment -> {
            if (cashPayRequisitionTypeAssignDepartment.getId() == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_DEPARTMENT_NOT_EXIST);
            }

            if (cashPayRequisitionTypeAssignDepartment.getDepartmentId() != null) {
                if (baseMapper.selectList(
                        new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                                .eq("pay_requisition_type_id",baseMapper.selectById(cashPayRequisitionTypeAssignDepartment.getId()).getPayRequisitionTypeId())
                                .eq("department_id",cashPayRequisitionTypeAssignDepartment.getDepartmentId())
                ).size() > 0){
                    throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_DEPARTMENT_NOT_ALLOWED_TO_REPEAT);
                }
            }
            this.updateById(cashPayRequisitionTypeAssignDepartment);
        });
        return list;
    }

    /**
     * 批量删除 预付款单类型关联部门(物理删除)
     *
     * @param list
     */
    @Transactional
    public void deleteCashPayRequisitionTypeAssignDepartmentBatch(List<Long> list){
        list.stream().forEach(id -> {
            CashPayRequisitionTypeAssignDepartment cashPayRequisitionTypeAssignDepartment = baseMapper.selectById(id);
            if(cashPayRequisitionTypeAssignDepartment == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_DEPARTMENT_NOT_EXIST);
            }

            baseMapper.deleteById(id);
        });
    }

    /**
     * 根据预付款单类型id查询所有已关联的部门(分页)
     *
     * @param payRequisitionTypeId
     * @param page
     * @return
     */
    public List<CashPayRequisitionTypeAssignDepartment> getCashPayRequisitionTypeAssignDepartmentByCond(Long payRequisitionTypeId, Page page){
        List<CashPayRequisitionTypeAssignDepartment> list = baseMapper.selectPage(page,
                new EntityWrapper<CashPayRequisitionTypeAssignDepartment>()
                        .eq(payRequisitionTypeId != null,"pay_requisition_type_id",payRequisitionTypeId)
        );
        return list;
    }

}
