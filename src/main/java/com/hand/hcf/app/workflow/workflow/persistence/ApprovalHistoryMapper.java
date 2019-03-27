package com.hand.hcf.app.workflow.workflow.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.workflow.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalHistoryViewDTO;
import com.hand.hcf.app.workflow.workflow.dto.AuditScanCodeDTO;
import com.hand.hcf.app.workflow.workflow.dto.CheckAuditNoticeDTO;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface ApprovalHistoryMapper extends BaseMapper<ApprovalHistory> {

    /**
     * 查询最近的
     * @param entityOids
     * @return
     */
    List<CheckAuditNoticeDTO> checkAuditNotice(@Param("entityOids") List<UUID> entityOids, @Param("operations") List<Integer> operations);

    /**
     * 查询用户最新单据审批历史
     * @param entityOids
     * @return
     */
    List<ApprovalHistory> getApprovalHistoryByEntityOids(@Param("entityOids") List<UUID> entityOids);





    /**
     * 查询所有审批通过的审批历史
     *
     * @return
     */
    List<ApprovalHistory> getByEntityTypeAndEntityOidAndOperation(@Param("entityType") Integer entityType, @Param("entityOid") UUID entityOid, @Param("operation") Integer operation);

    /**
     * 查询审批人重复审批通过的审批历史
     *
     * @return
     */
    List<ApprovalHistory> getByEntityTypeAndEntityOidAndOperationAndCountersignTypeIsNull(@Param("entityType") Integer entityType, @Param("entityOid") UUID entityOid, @Param("operation") Integer operation);
    /**
     * 查询加签人重复审批通过的审批历史
     *
     * @return
     */
    List<ApprovalHistory> getByEntityTypeAndEntityOidAndOperationAndCountersignTypeNotNull(@Param("entityType") Integer entityType, @Param("entityOid") UUID entityOid, @Param("operation") Integer operation);

    /**
     * 查询最后一个人的审批历史
     *
     * @return
     */
    ApprovalHistory findTopOneByEntityTypeAndEntityOidAndOperationOrderByIdDesc(@Param("entityType") Integer entityType, @Param("entityOid") UUID entityOid, @Param("operation") Integer operation);


}
