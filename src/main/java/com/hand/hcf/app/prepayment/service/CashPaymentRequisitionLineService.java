package com.hand.hcf.app.prepayment.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.CashPaymentRequisitionLineCO;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import com.hand.hcf.app.prepayment.persistence.CashPaymentRequisitionLineMapper;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbc on 2017/10/26.
 */
@Service
@AllArgsConstructor
public class CashPaymentRequisitionLineService extends BaseService<CashPaymentRequisitionLineMapper,CashPaymentRequisitionLine> {
    @Autowired
    private MapperFacade mapper;

    @Transactional
    @Override
    public boolean updateAllColumnById(CashPaymentRequisitionLine entity){

        return super.updateAllColumnById(entity);
    }

    public List<CashPaymentRequisitionLineCO> getLineByHeadID(@RequestParam(value = "headId") Long headId){
        List<CashPaymentRequisitionLine> list = baseMapper.selectList(new EntityWrapper<CashPaymentRequisitionLine>()
                .eq("payment_requisition_header_id", headId)
                .orderBy("created_date")
        );
        List<CashPaymentRequisitionLineCO> lineDTOS = new ArrayList<>();
        list.stream().forEach(cashPaymentRequisitionLine -> {
            lineDTOS.add(mapper.map(cashPaymentRequisitionLine,CashPaymentRequisitionLineCO.class));
        });
        return lineDTOS;
    }

    public List<CashPaymentRequisitionLine> getLinesByHeadID(@RequestParam(value = "headId") Long headId){
        return baseMapper.selectList(new EntityWrapper<CashPaymentRequisitionLine>()
                .eq("payment_requisition_header_id", headId)
        );
    }
}
