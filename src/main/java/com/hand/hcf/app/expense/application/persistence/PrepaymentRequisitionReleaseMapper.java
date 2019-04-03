package com.hand.hcf.app.expense.application.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.application.domain.PrepaymentRequisitionRelease;
import org.apache.ibatis.annotations.Param;

/**
 * @author qianjun.gong@hand-china.com
 * @create 2019/3/26
 * @remark 预付款释放申请信息
 */
public interface PrepaymentRequisitionReleaseMapper extends BaseMapper<PrepaymentRequisitionRelease> {
    void releasePrepaymentRequisitionRelease(@Param("prepaymentId") Long prepaymentId);
}
