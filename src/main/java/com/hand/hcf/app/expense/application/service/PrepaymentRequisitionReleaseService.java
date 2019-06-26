package com.hand.hcf.app.expense.application.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.application.domain.PrepaymentRequisitionRelease;
import com.hand.hcf.app.expense.application.persistence.PrepaymentRequisitionReleaseMapper;
import org.springframework.stereotype.Service;

/**
 * @author qianjun.gong@hand-china.com
 * @create 2019/3/26
 * @remark 预付款释放申请信息
 */
@Service
public class PrepaymentRequisitionReleaseService extends BaseService<PrepaymentRequisitionReleaseMapper, PrepaymentRequisitionRelease> {
    /**
     * 释放关联关系
     * @param prepaymentId
     */
    public void releasePrepaymentRequisitionRelease(Long prepaymentId) {
        baseMapper.releasePrepaymentRequisitionRelease(prepaymentId);
    }

}
