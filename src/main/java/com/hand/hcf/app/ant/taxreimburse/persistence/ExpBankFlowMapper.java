package com.hand.hcf.app.ant.taxreimburse.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 银行流水mapper
 * @date 2019/5/29 11:23
 */
public interface ExpBankFlowMapper extends BaseMapper<ExpBankFlow> {

    //根据相同的公司和币种更新数据状态
    void updateBankFlow(Long companyId, String currencyCode);

    //根据相同的公司和币种更新数据状态
    void updateStatusByGroup(Long companyId, String currencyCode, Long taxReimburseHeadId);

    //根据外键taxReimburseHeadId修改数据--删除报账单时调用
    int updateBankFlowByHeadId(Long taxReimburseHeadId);

}
