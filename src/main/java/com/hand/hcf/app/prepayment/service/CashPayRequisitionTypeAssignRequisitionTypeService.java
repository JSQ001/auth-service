package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ApplicationTypeCO;
import com.hand.hcf.app.common.co.ApplicationTypeForOtherCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignRequisitionType;
import com.hand.hcf.app.prepayment.externalApi.ExpenseModuleInterface;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeAssignRequisitionTypeMapper;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeMapper;
import com.hand.hcf.app.prepayment.utils.RespCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 韩雪 on 2017/12/5.
 */
@Service
@Transactional
@AllArgsConstructor
public class CashPayRequisitionTypeAssignRequisitionTypeService extends BaseService<CashPayRequisitionTypeAssignRequisitionTypeMapper, CashPayRequisitionTypeAssignRequisitionType> {

    private final CashPayRequisitionTypeMapper cashPayRequisitionTypeMapper;

    private final CashPayRequisitionTypeAssignRequisitionTypeMapper cashPayRequisitionTypeAssignRequisitionTypeMapper;

    private final ExpenseModuleInterface expenseModuleInterface;

    /**
     * 批量新增 预付款单类型关联申请单类型
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignRequisitionType> createCashPayRequisitionTypeAssignRequisitionTypeBatch(List<CashPayRequisitionTypeAssignRequisitionType> list){
        list.stream().forEach(cashPayRequisitionTypeAssignRequisitionType -> {
            if(cashPayRequisitionTypeAssignRequisitionType.getId() != null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_REQUISITIONTYPE_ALREADY_EXISTS);
            }

            //设置条件
            EntityWrapper<CashPayRequisitionTypeAssignRequisitionType> ew = new EntityWrapper<>();
            ew.where("pay_requisition_type_id = {0}", cashPayRequisitionTypeAssignRequisitionType.getPayRequisitionTypeId());
            //一个预付款单类型id 下，申请单类型不允许重复;通过form_id查询list集合
            List<CashPayRequisitionTypeAssignRequisitionType> cprtarts = baseMapper.selectList(ew);
            //对查询结果进行校验
            if (cprtarts.stream().anyMatch(e -> e.getRequisitionTypeId().equals(cashPayRequisitionTypeAssignRequisitionType.getRequisitionTypeId()))) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_REQUISITIONTYPE_NOT_ALLOWED_TO_REPEAT);
            }
            this.insert(cashPayRequisitionTypeAssignRequisitionType);
        });
        return list;
    }

    /**
     * 批量修改 预付款单类型关联申请单类型
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignRequisitionType> updateCashPayRequisitionTypeAssignRequisitionTypeBatch(List<CashPayRequisitionTypeAssignRequisitionType> list){
        list.stream().forEach(cashPayRequisitionTypeAssignRequisitionType -> {
            if (cashPayRequisitionTypeAssignRequisitionType.getId() == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_REQUISITIONTYPE_NOT_EXIST);
            }

            if (cashPayRequisitionTypeAssignRequisitionType.getRequisitionTypeId() != null) {
                //设置条件
                EntityWrapper<CashPayRequisitionTypeAssignRequisitionType> ew = new EntityWrapper<>();
                ew.where("pay_requisition_type_id = {0}", cashPayRequisitionTypeAssignRequisitionType.getPayRequisitionTypeId());
                //一个预付款单类型id 下，申请单类型id不允许重复;通过form_id查询list集合
                List<CashPayRequisitionTypeAssignRequisitionType> cprtarts = baseMapper.selectList(ew);
                //对查询结果进行校验
                if (cprtarts.stream().anyMatch(e -> e.getRequisitionTypeId().equals(cashPayRequisitionTypeAssignRequisitionType.getRequisitionTypeId()))) {
                    throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_REQUISITIONTYPE_NOT_ALLOWED_TO_REPEAT);
                }
            }
            this.updateById(cashPayRequisitionTypeAssignRequisitionType);
        });
        return list;
    }

    /**
     * 批量删除 预付款单类型关联申请单类型(物理删除)
     *
     * @param list
     */
    @Transactional
    public void deleteCashPayRequisitionTypeAssignRequisitionTypeBatch(List<Long> list){
        list.stream().forEach(id -> {
            CashPayRequisitionTypeAssignRequisitionType cashPayRequisitionTypeAssignRequisitionType = baseMapper.selectById(id);
            if(cashPayRequisitionTypeAssignRequisitionType == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_REQUISITIONTYPE_NOT_EXIST);
            }

            baseMapper.deleteById(id);
        });
    }

    /**
     * 根据预付款单类型id查询所有已关联的申请单类型(分页)
     *
     * @param payRequisitionTypeId
     * @param page
     * @return
     */
    public List<CashPayRequisitionTypeAssignRequisitionType> getCashPayRequisitionTypeAssignRequisitionTypeByCond(Long payRequisitionTypeId, Page page){
        List<CashPayRequisitionTypeAssignRequisitionType> list = baseMapper.selectPage(page,
                new EntityWrapper<CashPayRequisitionTypeAssignRequisitionType>()
                        .eq(payRequisitionTypeId != null,"pay_requisition_type_id",payRequisitionTypeId)
        );
        return list;
    }


    /**
     * 根据所选范围 查询申请单类型(分页)
     * @param setOfBooksId
     * @param range
     * @param payRequisitionTypeId
     * @param page
     * @return
     */
    public Page<ApplicationTypeCO> getCustomFormByRange(Long setOfBooksId, String range, Long payRequisitionTypeId, String code, String name, Page page){
        Page<ApplicationTypeCO> result = new Page<>();

        ApplicationTypeForOtherCO applicationTypeForOtherCO = new ApplicationTypeForOtherCO();
        applicationTypeForOtherCO.setSetOfBooksId(setOfBooksId);
        applicationTypeForOtherCO.setRange(range);
        applicationTypeForOtherCO.setCode(code);
        applicationTypeForOtherCO.setName(name);


        if (payRequisitionTypeId != null){
            List<Long> requisitionTypeIdList = cashPayRequisitionTypeAssignRequisitionTypeMapper.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignRequisitionType>()
                            .eq("pay_requisition_type_id",payRequisitionTypeId)
            ).stream().map(CashPayRequisitionTypeAssignRequisitionType::getRequisitionTypeId).collect(Collectors.toList());
            applicationTypeForOtherCO.setIdList(requisitionTypeIdList);
        }

        result = expenseModuleInterface.queryApplicationTypeByCond(applicationTypeForOtherCO, page);
        page.setTotal(result.getTotal());
        return result;
    }
}
