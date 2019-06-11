package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.invoice.domain.InvoiceBagNoScan;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceBagNoScanMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhuo.zhang
 * @description 发票袋号码扫描(InvoiceBagNoScan)表服务接口
 * @date 2019-04-29 16:08:06
 */
@Service
public class InvoiceBagNoScanService extends BaseService<InvoiceBagNoScanMapper, InvoiceBagNoScan> {

    public String saveInvoiceBagNoScan(InvoiceBagNoScan invoiceBagNoScan){
        if(invoiceBagNoScan != null && TypeConversionUtils.isNotEmpty(invoiceBagNoScan.getInvoiceBagNo())){
            Long currentUserId = OrgInformationUtil.getCurrentUserId();
            List<InvoiceBagNoScan> invoiceBagNoScans = baseMapper.selectList(
              new EntityWrapper<InvoiceBagNoScan>()
                    .eq("invoice_bag_no", invoiceBagNoScan.getInvoiceBagNo())
                    .eq("created_by", currentUserId)
            );
            if(invoiceBagNoScans.size() > 0 ){
                throw new BizException("发票袋号码："+invoiceBagNoScan.getInvoiceBagNo()+
                        "已扫描！");
            }else{
                baseMapper.insert(invoiceBagNoScan);
                return "SUCCESS";
            }
        }
        return "ERROR";
    }

}