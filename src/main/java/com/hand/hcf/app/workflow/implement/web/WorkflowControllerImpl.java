package com.hand.hcf.app.workflow.implement.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.common.co.ApprovalHistoryCO;
import com.hand.hcf.app.common.co.CommonApprovalHistoryCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.workflow.approval.service.ApprovalSubmitService;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.workflow.util.StringUtil;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.service.ApprovalFormService;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.core.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class WorkflowControllerImpl /*implements WorkflowInterface*/ {
    @Autowired
    private ApprovalFormService approvalFormService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private ApprovalSubmitService approvalSubmitService;

    /*@Override*/
    public ApprovalHistoryCO saveHistory(@RequestBody CommonApprovalHistoryCO commonApprovalHistoryDTO) {
        ApprovalHistory approvalHistory = new ApprovalHistory();
        //默认的操作类型
        approvalHistory.setOperationType(1000);
        approvalHistory.setLastUpdatedDate(ZonedDateTime.now());
        approvalHistory.setCreatedDate(ZonedDateTime.now());
        approvalHistory.setRemark("第三方接口插入审批记录");
        approvalHistory.setEntityOid(commonApprovalHistoryDTO.getEntityOid());
        approvalHistory.setEntityType(commonApprovalHistoryDTO.getEntityType());
        approvalHistory.setOperationDetail(commonApprovalHistoryDTO.getOperationDetail());
        approvalHistory.setOperatorOid(commonApprovalHistoryDTO.getOperatorOid());
        approvalHistory.setOperation(commonApprovalHistoryDTO.getOperation());
        approvalHistoryService.save(approvalHistory);

        ApprovalHistoryCO approvalHistoryCO = new ApprovalHistoryCO();
        approvalHistoryCO.setId(approvalHistory.getId());
        approvalHistoryCO.setEntityType(approvalHistory.getEntityType());
        approvalHistoryCO.setEntityOid(StringUtil.getUuidString(approvalHistory.getEntityOid()));
        approvalHistoryCO.setOperationType(approvalHistory.getOperationType());
        approvalHistoryCO.setOperation(approvalHistory.getOperation());
        approvalHistoryCO.setCountersignType(approvalHistory.getCountersignType());
        approvalHistoryCO.setOperatorOid(StringUtil.getUuidString(approvalHistory.getOperatorOid()));
        approvalHistoryCO.setCurrentApplicantOid(StringUtil.getUuidString(approvalHistory.getCurrentApplicantOid()));
        approvalHistoryCO.setOperationDetail(approvalHistory.getOperationDetail());
        approvalHistoryCO.setStepID(approvalHistory.getStepID());
        approvalHistoryCO.setRemark(approvalHistory.getRemark());
        approvalHistoryCO.setCreatedDate(StringUtil.getStandardDateString(approvalHistory.getCreatedDate()));
        approvalHistoryCO.setLastModifiedDate(StringUtil.getStandardDateString(approvalHistory.getLastUpdatedDate()));
        approvalHistoryCO.setRuleApprovalNodeOid(StringUtil.getUuidString(approvalHistory.getApprovalNodeOid()));
        approvalHistoryCO.setRefApprovalChainId(approvalHistory.getRefApprovalChainId());
        return approvalHistoryCO;
    }

    /*@Override*/
    public List<ApprovalHistoryCO> saveBatchHistory(@RequestBody List<CommonApprovalHistoryCO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)){
            return new ArrayList<>();
        }
        List<ApprovalHistory> histories = dtoList.stream().map(commonApprovalHistoryDTO -> {
            ApprovalHistory approvalHistory = new ApprovalHistory();
            //默认的操作类型
            approvalHistory.setOperationType(1000);
            approvalHistory.setLastUpdatedDate(ZonedDateTime.now());
            approvalHistory.setCreatedDate(ZonedDateTime.now());
            approvalHistory.setRemark("第三方接口插入审批记录");
            approvalHistory.setEntityOid(commonApprovalHistoryDTO.getEntityOid());
            approvalHistory.setEntityType(commonApprovalHistoryDTO.getEntityType());
            approvalHistory.setOperationDetail(commonApprovalHistoryDTO.getOperationDetail());
            approvalHistory.setOperatorOid(commonApprovalHistoryDTO.getOperatorOid());
            approvalHistory.setOperation(commonApprovalHistoryDTO.getOperation());
            return approvalHistory;
        }).collect(Collectors.toList());
        approvalHistoryService.insertBatch(histories);
        List<ApprovalHistoryCO> result = histories.stream().map(approvalHistory -> {
            ApprovalHistoryCO approvalHistoryCO = new ApprovalHistoryCO();
            approvalHistoryCO.setId(approvalHistory.getId());
            approvalHistoryCO.setEntityType(approvalHistory.getEntityType());
            approvalHistoryCO.setEntityOid(StringUtil.getUuidString(approvalHistory.getEntityOid()));
            approvalHistoryCO.setOperationType(approvalHistory.getOperationType());
            approvalHistoryCO.setOperation(approvalHistory.getOperation());
            approvalHistoryCO.setCountersignType(approvalHistory.getCountersignType());
            approvalHistoryCO.setOperatorOid(StringUtil.getUuidString(approvalHistory.getOperatorOid()));
            approvalHistoryCO.setCurrentApplicantOid(StringUtil.getUuidString(approvalHistory.getCurrentApplicantOid()));
            approvalHistoryCO.setOperationDetail(approvalHistory.getOperationDetail());
            approvalHistoryCO.setStepID(approvalHistory.getStepID());
            approvalHistoryCO.setRemark(approvalHistory.getRemark());
            approvalHistoryCO.setCreatedDate(StringUtil.getStandardDateString(approvalHistory.getCreatedDate()));
            approvalHistoryCO.setLastModifiedDate(StringUtil.getStandardDateString(approvalHistory.getLastUpdatedDate()));
            approvalHistoryCO.setRuleApprovalNodeOid(StringUtil.getUuidString(approvalHistory.getApprovalNodeOid()));
            approvalHistoryCO.setRefApprovalChainId(approvalHistory.getRefApprovalChainId());
            return approvalHistoryCO;
        }).collect(Collectors.toList());
        return result;
    }

    /*@Override*/
    public Boolean deleteBatchLogs(@RequestBody List<ApprovalHistoryCO> dto) {
        List<Long> idList = dto.stream().map(ApprovalHistoryCO::getId).collect(Collectors.toList());
        return approvalHistoryService.deleteByIds(idList);
    }


    /*@Override*/
    public List<ApprovalHistoryCO> listApprovalHistory(@RequestParam("entityType") Integer entityType,
                                                       @RequestParam("entityOid") String entityOid) {
        return null;
    }

    /**
     * 工作流单据提交统一入口方法(三方Feign调用)
     * <p>
     * applicantOid: 申请人Oid,
     * userOid: 用户Oid,
     * formOid: 表单Oid,
     * documentOid: 单据Oid,
     * documentCategory: 单据大类 （如801003),
     * countersignApproverOids: 加签审批人Oid,
     * documentNumber:单据编号 ,
     * remark:描述说明 ,
     * companyId:公司ID,
     * unitOid:部门Oid,
     * amount:金额,
     * currencyCode:币种,
     * documentTypeId:单据类型ID
     */
    /*@Override*/
    public String submitWorkflow(@RequestBody WorkFlowDocumentRefCO workFlowDocumentRefDTO) {
        return approvalSubmitService.submitWorkflow(workFlowDocumentRefDTO);
    }

    /**
     * @author mh.z
     * @date 2019/03/22
     * @description 工作流单据提交统一入口方法(三方Feign调用)
     *
     * @param approvalDocumentCO 提交的单据
     * @return 提交结果
     */
    /*@Override*/
    public ApprovalResultCO submitWorkflow(ApprovalDocumentCO approvalDocumentCO) {
        return approvalSubmitService.submitWorkflow(approvalDocumentCO);
    }

    /**
     * 根据单据Oid获取单据名称
     * @param formOid
     * @return
     */
    /*@Override*/
    public ApprovalFormCO getApprovalFormByOid(String formOid) {
        ApprovalForm approvalForm = approvalFormService.getByOid(UUID.fromString(formOid));
        return ApprovalFormCO.builder().formOid(approvalForm.getFormOid())
                .formType(approvalForm.getFormTypeId())
                .formName(approvalForm.getFormName())
                .build();
    }

    /**
     * 根据单据id获取单据名称
     * @param formId
     * @return
     */
    /*@Override*/
    public ApprovalFormCO getApprovalFormById(Long formId) {
        ApprovalForm approvalForm = approvalFormService.getById(formId);
        return ApprovalFormCO.builder()
                .formId(approvalForm.getId())
                .formOid(approvalForm.getFormOid())
                .formType(approvalForm.getFormTypeId())
                .formName(approvalForm.getFormName())
                .build();
    }


    /**
     * 根据单据Oid列表获取单据名称列表
     * @param formOids
     * @return
     */
    /*@Override*/
    public List<ApprovalFormCO> listApprovalFormByOids(List<UUID> formOids) {
        return approvalFormService.listByOids(formOids)
                .stream().map(form -> ApprovalFormCO.builder().formOid(form.getFormOid())
                        .formType(form.getFormTypeId())
                        .formName(form.getFormName())
                        .build()).collect(Collectors.toList());
    }

    /*@Override*/
    public List<ApprovalFormCO> listApprovalFormByIds(@RequestBody List<Long> formIds) {
        if (formIds.size() == 0) {
            return new ArrayList<>();
        }
        List<ApprovalForm> approvalFormList = approvalFormService.selectList(
                new EntityWrapper<ApprovalForm>().in("id", formIds)
        );

        return approvalFormList
                .stream().map(form -> ApprovalFormCO.builder().formOid(form.getFormOid())
                        .formId(form.getId())
                        .formType(form.getFormTypeId())
                        .formName(form.getFormName())
                        .build()).collect(Collectors.toList());
    }

    /**
     * @Author mh.z
     * @Date 2019/01/23
     * @Description 获取未审批/已审批的单据
     */
    /*@Override*/
    public List<String> listApprovalDocument(@RequestParam(value = "entityType", required = false) Integer entityType,
                                             @RequestParam(value = "userOid", required = false) String userOid,
                                             @RequestParam(value = "approved", required = true) boolean approved,
                                             @RequestParam(value = "startDate", required = false) String startDate,
                                             @RequestParam(value = "endDate", required = false) String endDate) {
        ZonedDateTime startDateTime = null;
        ZonedDateTime endDateTime = null;

        // 最小提交日期
        if (StringUtils.isNotEmpty(startDate)) {
            startDateTime = DateUtil.stringToZonedDateTime(startDate);
        }

        // 最大提交日期
        if (StringUtils.isNotEmpty(endDate)) {
            endDateTime = DateUtil.stringToZonedDateTime(endDate);
            endDateTime = endDateTime.plusDays(1);
        }

        // 获取未审批/已审批的单据
        List<WorkFlowDocumentRef> approvalDocumentList = workFlowDocumentRefService.listApprovalDocument(entityType, userOid, approved, startDateTime, endDateTime);
        List<String> documentOidList = new ArrayList<String>();

        // approvalDocumentList => documentOidList
        for (WorkFlowDocumentRef approvalDocument : approvalDocumentList) {
            documentOidList.add(approvalDocument.getDocumentOid().toString());
        }

        return documentOidList;
    }

    /**
     * @author lsq
     * @date 2019/03/29
     * @description 获取指定单据的当前审批人
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @return 当前审批人
     */
    /*@Override*/
    public List<ContactCO> listCurrentApprover(@RequestParam(value = "entityType") Integer entityType,
                                               @RequestParam(value = "entityOid")UUID entityOid) {
        List<ContactCO> contactCOList = workFlowDocumentRefService.listCurrentApprover(entityType, entityOid);
        return contactCOList;
    }
}
