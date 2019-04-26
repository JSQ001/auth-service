package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToRelated;
import com.hand.hcf.app.payment.persistence.PaymentRequisitionTypesToRelatedMapper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:34 2018/1/22
 * @Modified by
 */
@AllArgsConstructor
@Service
@Transactional
public class PaymentRequisitionTypesToRelatedService extends BaseService<PaymentRequisitionTypesToRelatedMapper,PaymentRequisitionTypesToRelated> {


    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionTypesToRelated saveAcpRequstTypesToRelated(PaymentRequisitionTypesToRelated paymentRequisitionTypesToRelated){
        if (paymentRequisitionTypesToRelated.getId() == null){
            try {
                this.insert(paymentRequisitionTypesToRelated);
            } catch (DuplicateKeyException e) {
                throw new ValidationException(new ValidationError("unq_acp_req_types_to_related", "已经选择的申请类型不允许再选择！"));
            }
        }else{
            this.updateById(paymentRequisitionTypesToRelated);
        }
        return paymentRequisitionTypesToRelated;
    }

    @Transactional(readOnly = true)
    public List<PaymentRequisitionTypesToRelated> getRelatedsByTypeId(Long acpReqTypesId){
        return baseMapper.selectList(new EntityWrapper<PaymentRequisitionTypesToRelated>().eq("acp_req_types_id",acpReqTypesId));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByTypeId(Long acpReqTypesId){
        baseMapper.delete(new EntityWrapper<PaymentRequisitionTypesToRelated>().eq("acp_req_types_id",acpReqTypesId));
    }
}
