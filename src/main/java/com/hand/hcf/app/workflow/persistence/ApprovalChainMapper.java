package com.hand.hcf.app.workflow.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.dto.ApprovalChainDTO;
import org.apache.ibatis.annotations.Param;

/**
 * Created by lichao on 2017/7/6.
 */
public interface ApprovalChainMapper extends BaseMapper<ApprovalChain> {

    ApprovalChainDTO getApprovalChainByRefId(@Param("refApprovalChainId") Long refApprovalChainId);

    /**
     * 根据chainID 修改AllFinish为true
     * @param approvalChainId
     */
    void updateAllFinshTrueById(@Param("approvalChainId") Long approvalChainId);

    /**
     * 通过chainID 获取审批链
     * @param approvalChainId
     * @return
     */
    ApprovalChain getApprovalChainById(@Param("approvalChainId") Long approvalChainId);
}
