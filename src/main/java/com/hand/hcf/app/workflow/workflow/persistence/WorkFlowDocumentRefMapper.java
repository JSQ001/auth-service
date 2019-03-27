package com.hand.hcf.app.workflow.workflow.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalDashboardDetailDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 *@author dicky 2018/12/06
 */

public interface WorkFlowDocumentRefMapper extends BaseMapper<WorkFlowDocumentRef> {

    List<ApprovalDashboardDetailDTO> getApprovalListDashboard(@Param("userOid") UUID userOid);

    /**
     * @Author mh.z
     * @Date 2019/01/29
     * @Description 查询未审批/已审批的单据
     *
     * @param documentCategory 单据类型（可选）
     * @param approverOid 审批人（可选）
     * @param approved true已审批，false未审批（必输）
     * @param startDate 最小提交日期（可选）
     * @param endDate 最大提交日期（可选）
     * @return
     */
    List<WorkFlowDocumentRef> listApprovalDocument(@Param("documentCategory") Integer documentCategory,
                                                   @Param("approverOid") String approverOid,
                                                   @Param("approved") boolean approved,
                                                   @Param("startDate") ZonedDateTime startDate,
                                                   @Param("endDate") ZonedDateTime endDate);

    /**
     * @author mh.z
     * @date 2019/03/07
     * @description 查询未审批/已审批的单据
     *
     * @param approverOid 申请人oid
     * @param documentCategory 单据大类
     * @param documentNumber 单据编号
     * @param documentName 单据名称
     * @param documentTypeId 单据类型id
     * @param currencyCode 币种
     * @param amountFrom 最小金额
     * @param amountTo 最大金额
     * @param applicantOid 申请人oid
     * @param startDate 最小提交日期
     * @param endDate 最大提交日期
     * @param description 备注
     * @param approved true已审批，false未审批
     * @param rowBounds
     * @return
     */
    List<WorkFlowDocumentRef> pageApprovalDocument(@Param("approverOid") UUID approverOid,
                                                   @Param("documentCategory") Integer documentCategory,
                                                   @Param("documentNumber") String documentNumber,
                                                   @Param("documentName") String documentName,
                                                   @Param("documentTypeId") Long documentTypeId,
                                                   @Param("currencyCode") String currencyCode,
                                                   @Param("amountFrom") Double amountFrom,
                                                   @Param("amountTo") Double amountTo,
                                                   @Param("applicantOid") UUID applicantOid,
                                                   @Param("startDate") ZonedDateTime startDate,
                                                   @Param("endDate") ZonedDateTime endDate,
                                                   @Param("description") String description,
                                                   @Param("approved") boolean approved,
                                                   RowBounds rowBounds);

    /**
     * 待办事项-待审批单据-单据列表
     * @param userOid 审批人oid
     * @param documentCategory 单据大类
     * @param documentTypeId 单据类型id
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param remark 备注
     * @param documentNumber 单据编号
     * @param rowBounds 分页信息
     * @return
     */
    List<WorkFlowDocumentRef> getApprovalToPendDeatil(@Param("userOid") UUID userOid,
                                                      @Param("documentCategory") Integer documentCategory,
                                                      @Param("documentTypeId") Long documentTypeId,
                                                      @Param("applicantName") String applicantName,
                                                      @Param("beginDate") ZonedDateTime beginDate,
                                                      @Param("endDate") ZonedDateTime endDate,
                                                      @Param("amountFrom") Double amountFrom,
                                                      @Param("amountTo") Double amountTo,
                                                      @Param("remark") String remark,
                                                      @Param("documentNumber") String documentNumber,
                                                      RowBounds rowBounds);

    /**
     * 待办事项-待审批单据-分类信息
     * @param userOid 审批人oid
     * @param documentCategory 单据大类
     * @param documentTypeId 单据类型id
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param remark 备注
     * @param documentNumber 单据编号
     * @return
     */
    List<ApprovalDashboardDetailDTO> getApprovalToPendTotal(@Param("userOid") UUID userOid,
                                                            @Param("documentCategory") Integer documentCategory,
                                                            @Param("documentTypeId") Long documentTypeId,
                                                            @Param("applicantName") String applicantName,
                                                            @Param("beginDate") ZonedDateTime beginDate,
                                                            @Param("endDate") ZonedDateTime endDate,
                                                            @Param("amountFrom") Double amountFrom,
                                                            @Param("amountTo") Double amountTo,
                                                            @Param("remark") String remark,
                                                            @Param("documentNumber") String documentNumber);

    /**
     * 待办事项-未完成单据
     * @param documentCategory 单据大类
     * @param documentTypeId 单据类型id
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param lastApproverOid 当前审批人oid
     * @param approvalNodeName 当前审批节点名称
     * @param remark 备注
     * @param documentNumber 单据编号
     * @param rowBounds 分页信息
     * @return
     */
    List<WorkFlowDocumentRef> getUnFinishedList(@Param("documentCategory") Integer documentCategory,
                                                @Param("documentTypeId") Long documentTypeId,
                                                @Param("applicantName") String applicantName,
                                                @Param("beginDate") ZonedDateTime beginDate,
                                                @Param("endDate") ZonedDateTime endDate,
                                                @Param("amountFrom") Double amountFrom,
                                                @Param("amountTo") Double amountTo,
                                                @Param("lastApproverOid") UUID lastApproverOid,
                                                @Param("approvalNodeName") String approvalNodeName,
                                                @Param("remark") String remark,
                                                @Param("documentNumber") String documentNumber,
                                                RowBounds rowBounds);
}
