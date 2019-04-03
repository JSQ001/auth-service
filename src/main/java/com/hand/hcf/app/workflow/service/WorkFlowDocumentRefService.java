package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.util.StringUtil;
import com.hand.hcf.app.workflow.domain.WorkFlowApprovers;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentDTO;
import com.hand.hcf.app.workflow.persistence.WorkFlowDocumentRefMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.RespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by dick on 2018/12/06.
 */
@Service
@Transactional
public class WorkFlowDocumentRefService extends BaseService<WorkFlowDocumentRefMapper, WorkFlowDocumentRef> {

    @Autowired
    private WorkFlowDocumentRefMapper workflowDocumentRefMapper;
    @Autowired
    private WorkFlowRefApproversService workflowApproversService;

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private ContactControllerImpl contactClient;

    /**
     * 存在，则更新，不存在，则插入
     *
     * @param workFlowDocumentRef
     */
    public WorkFlowDocumentRef saveOrUpdate(WorkFlowDocumentRef workFlowDocumentRef) {
        if (workFlowDocumentRef != null && workFlowDocumentRef.getApprovalNodeOid() != null) {
            WorkFlowDocumentRef doc = getByDocumentOidAndDocumentCategory(workFlowDocumentRef.getDocumentOid(), workFlowDocumentRef.getDocumentCategory());
            try {
                // 先保存 单据工作流关联表
                if (doc != null) {
                    workFlowDocumentRef.setId(doc.getId());
                    workFlowDocumentRef.setVersionNumber(doc.getVersionNumber());
                    this.updateSysWorkFlowDocumentRef(workFlowDocumentRef);
                } else {
                    this.createSysWorkFlowDocumentRef(workFlowDocumentRef);
                }
                List<UUID> approvalUserOids = workFlowDocumentRef.getCurrentApproverOids();
                // 再保存关联的审批人表
                if (approvalUserOids != null && approvalUserOids.size() > 0) {
                    ZonedDateTime now = ZonedDateTime.now();
                    Long userId = OrgInformationUtil.getCurrentUserId();
                    List<WorkFlowApprovers> approversList = approvalUserOids.stream().map(m -> {
                        WorkFlowApprovers approvers = new WorkFlowApprovers();
                        approvers.setApproverOid(m);
                        approvers.setApproveNodeOid(workFlowDocumentRef.getApprovalNodeOid());
                        approvers.setCreatedDate(ZonedDateTime.now());
                        approvers.setWorkFlowDocumentRefId(workFlowDocumentRef.getId());
                        approvers.setCreatedBy(userId);
                        approvers.setCreatedDate(now);
                        approvers.setLastUpdatedDate(now);
                        approvers.setLastUpdatedBy(userId);
                        approvers.setVersionNumber(1);
                        return approvers;
                    }).collect(Collectors.toList());
                    workFlowDocumentRef.setCurrentApproverList(approversList);
                    workflowApproversService.createBatchSysWorkflowApprovers(approversList);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException("单据关联工作流操作数据库保存");
            }
        }
        return workFlowDocumentRef;
    }

    /**
     * 保存方法
     *
     * @param workFlowDocumentRef
     * @return
     */
    @Transactional
    public WorkFlowDocumentRef createSysWorkFlowDocumentRef(WorkFlowDocumentRef workFlowDocumentRef) {
        //校验
        if (workFlowDocumentRef == null || workFlowDocumentRef.getId() != null) {
            throw new BizException(RespCode.SYS_FAILED);
        }
        workFlowDocumentRef.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        ZonedDateTime now = ZonedDateTime.now();
        workFlowDocumentRef.setCreatedDate(now);
        workFlowDocumentRef.setLastUpdatedDate(now);
        workFlowDocumentRef.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        workFlowDocumentRef.setVersionNumber(1);
        workflowDocumentRefMapper.insert(workFlowDocumentRef);
        return workFlowDocumentRef;
    }

    /**
     * 更新workFlowDocumentRef
     *
     * @param workFlowDocumentRef
     * @return
     */
    @Transactional
    public WorkFlowDocumentRef updateSysWorkFlowDocumentRef(WorkFlowDocumentRef workFlowDocumentRef) {
        //校验
        if (workFlowDocumentRef == null || workFlowDocumentRef.getId() == null) {
            throw new BizException(RespCode.SYS_FAILED);
        }
        //校验ID是否在数据库中存在
        WorkFlowDocumentRef rr = workflowDocumentRefMapper.selectById(workFlowDocumentRef.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_FAILED);
        }
        workFlowDocumentRef.setCreatedBy(rr.getCreatedBy());
        workFlowDocumentRef.setCreatedDate(rr.getCreatedDate());
        ZonedDateTime now = ZonedDateTime.now();
        workFlowDocumentRef.setLastUpdatedDate(now);
        workFlowDocumentRef.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        // modify by mh.z 20190124 撤回单据时更新workFlowDocumentRef失败（版本号错误）
        //workFlowDocumentRef.setVersionNumber(workFlowDocumentRef.getVersionNumber() + 1);
        workFlowDocumentRef.setVersionNumber(rr.getVersionNumber());
        this.updateById(workFlowDocumentRef);
        return workFlowDocumentRef;
    }

    /**
     * @param id 删除
     * @return
     */
    @Transactional
    public void deleteSysWorkFlowDocumentRef(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param documentOid      删除
     * @param documentCategory 单据大类 801001 对公报账单，801002 预算日记账 801003 预付款单 801004 合同  801005 付款申请单 801006 费用调整单
     * @return
     */
    @Transactional
    public Integer deleteByDocumentOidAndDocumentCategory(String documentOid, Integer documentCategory) {
        return workflowDocumentRefMapper.delete(new EntityWrapper<WorkFlowDocumentRef>()
                .eq("document_oid", documentOid)
                .eq("document_category", documentCategory));
    }

    /**
     * 根据单据Oid和大类，查询
     *
     * @param documentOid
     * @param documentCategory 单据大类 801001 对公报账单，801002 预算日记账 801003 预付款单 801004 合同  801005 付款申请单 801006 费用调整单
     * @return
     */
    public WorkFlowDocumentRef getByDocumentOidAndDocumentCategory(UUID documentOid, Integer documentCategory) {
        return workflowDocumentRefMapper.selectOne(WorkFlowDocumentRef.builder().documentOid(documentOid)
                .documentCategory(documentCategory).build());
    }

    /**
     * 根据事件消息ID，获取工作流关联单据信息
     *
     * @param eventId
     * @return
     */
    public WorkFlowDocumentRef getByEventId(String eventId) {
        return workflowDocumentRefMapper.selectOne(WorkFlowDocumentRef.builder().eventId(eventId).build());
    }

    /**
     * fuzzySearch 模糊搜索，如果从单据号和申请人去搜索。
     * page 页数
     * size 每页条数
     * finished 是否已审批
     *
     * @param
     * @return
     */
    public List<WorkFlowDocumentRef> listPendingApproval(String fuzzySearch, boolean finished, Long documentCategory, UUID formOid, Page page) {
        String whereSql = null;
        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        if (!finished) {
            //查询当前待审批的
            whereSql = "id in\n" +
                    "          (select swra.workflow_document_ref_id\n" +
                    "             from sys_wfl_ref_approvers swra\n" +
                    "            where swra.approver_oid = \'" + userOid + "\')";
        } else {
            //查询当前员工审批过的单据
            whereSql = "document_oid in\n" +
                    "          (select aah.entity_oid\n" +
                    "             from sys_approval_history aah, sys_approval_chain aac\n" +
                    "            where aah.entity_oid = aac.entity_oid\n" +
                    "              and aac.finish_flag = '1'\n" +
                    "              and aah.operator_oid = \'" + userOid + "\')";
        }
        Wrapper wrapper = new EntityWrapper<WorkFlowDocumentRef>().where(whereSql);
        if (documentCategory != null) {
            wrapper = wrapper.eq("document_category", documentCategory);
        }
        if(formOid != null&&!formOid.equals("")){
            wrapper = wrapper.eq("form_oid",formOid);
        }
        if (fuzzySearch != null&&!fuzzySearch.equals("")) {
            wrapper = wrapper.and("(document_number like '%" + fuzzySearch + "%' or applicant_name like '%" + fuzzySearch + "%')");
        }

        return workflowDocumentRefMapper.selectPage(page, wrapper.orderBy("last_updated_date",false));
    }

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
    public List<WorkFlowDocumentRef> listApprovalDocument(Integer documentCategory, String approverOid, boolean approved,
                                                          ZonedDateTime startDate, ZonedDateTime endDate) {
        return workflowDocumentRefMapper.listApprovalDocument(documentCategory, approverOid, approved, startDate, endDate);
    }


    /**
     * @author mh.z
     * @date 2019/03/06
     * @description 获取未审批已审批的单据
     *
     * @param approverOid 审批人oid
     * @param documentCategory 单据大类
     * @param documentNumber 单据编号
     * @param documentName 单据名称
     * @param documentTypeId 单据类型id
     * @param currencyCode 币种
     * @param amountFrom 最小金额
     * @param amountTo 最大金额
     * @param applicantOid 申请人oid
     * @param startDate 开始提交日期
     * @param endDate 结束提交日期
     * @param description 备注
     * @param approved true已审批，false未审批
     * @param page
     * @return
     */
    public List<ApprovalDocumentDTO> pageApprovalDocument(UUID approverOid, Integer documentCategory, String documentNumber, String documentName, Long documentTypeId,
                                                          String currencyCode, Double amountFrom, Double amountTo, UUID applicantOid, ZonedDateTime startDate,
                                                          ZonedDateTime endDate, ZonedDateTime applicantDateFrom, ZonedDateTime applicantDateTo, String description, boolean approved, Page page) {
        if (approverOid == null) {
            throw new IllegalArgumentException("approverOid null");
        }

        if (documentCategory == null) {
            throw new IllegalArgumentException("documentCategory null");
        }

        // 过滤特殊字符
        if (documentNumber != null) {
            documentNumber = StringUtil.escapeSpecialCharacters(documentNumber);
            documentNumber = documentNumber.toUpperCase();
        }

        // 单据编号模糊查询
        if (documentNumber != null) {
            documentNumber = '%' + documentNumber + '%';
        }

        // 单据名称模糊查询
        if (documentName != null) {
            documentName = '%' + documentName + '%';
        }

        // 备注模糊查询
        if (description != null) {
            description = '%' + description + '%';
        }

        // 查询未审批/已审批的单据
        List<WorkFlowDocumentRef> workFlowDocumentRefList = workflowDocumentRefMapper.pageApprovalDocument(approverOid, documentCategory, documentNumber, documentName, documentTypeId,
                currencyCode, amountFrom, amountTo, applicantOid, startDate, endDate,
                applicantDateFrom, applicantDateTo, description, approved, page);

        List<ApprovalDocumentDTO> approvalDocumentDTOList = new ArrayList<ApprovalDocumentDTO>();
        // 组装数据
        for (WorkFlowDocumentRef workFlowDocumentRef : workFlowDocumentRefList) {
            ApprovalDocumentDTO approvalDocumentDTO = new ApprovalDocumentDTO();
            approvalDocumentDTO.setEntityOid(workFlowDocumentRef.getDocumentOid());
            approvalDocumentDTO.setEntityType(workFlowDocumentRef.getDocumentCategory());
            approvalDocumentDTO.setDocumentId(workFlowDocumentRef.getDocumentId());
            approvalDocumentDTO.setDocumentNumber(workFlowDocumentRef.getDocumentNumber());
            approvalDocumentDTO.setDocumentName(workFlowDocumentRef.getContractName());
            approvalDocumentDTO.setDocumentTypeId(workFlowDocumentRef.getDocumentTypeId());
            approvalDocumentDTO.setDocumentTypeCode(workFlowDocumentRef.getDocumentTypeCode());
            approvalDocumentDTO.setDocumentTypeName(workFlowDocumentRef.getDocumentTypeName());
            approvalDocumentDTO.setCurrencyCode(workFlowDocumentRef.getCurrencyCode());
            approvalDocumentDTO.setAmount(workFlowDocumentRef.getAmount());
            approvalDocumentDTO.setFunctionAmount(workFlowDocumentRef.getFunctionAmount());
            approvalDocumentDTO.setApplicantOid(workFlowDocumentRef.getApplicantOid());
            approvalDocumentDTO.setApplicantName(workFlowDocumentRef.getApplicantName());
            approvalDocumentDTO.setSubmittedDate(workFlowDocumentRef.getSubmitDate());
            approvalDocumentDTO.setApplicantDate(workFlowDocumentRef.getApplicantDate());
            approvalDocumentDTO.setStatus(workFlowDocumentRef.getStatus());
            approvalDocumentDTO.setRemark(workFlowDocumentRef.getRemark());
            approvalDocumentDTOList.add(approvalDocumentDTO);
        }

        return approvalDocumentDTOList;
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
    public List<ContactCO> listCurrentApprover(Integer entityType, UUID entityOid){
        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq("current_flag", 1);
        wrapper.eq("status", ApprovalChainStatusEnum.NORMAL.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type",entityType);
        List<ApprovalChain> approvalChainList = approvalChainService.selectList(wrapper);

        List<String> userOidStrList  = new ArrayList<String>();
        for (ApprovalChain approvalChain : approvalChainList) {
            userOidStrList.add(approvalChain.getApproverOid().toString());
        }

        // 获取用户信息
        List<ContactCO> contactCOList = contactClient.listByUserOids(userOidStrList);
        return contactCOList;
    }

}
