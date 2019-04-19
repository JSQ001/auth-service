package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.implement.web.CommonControllerImpl;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.dto.DroolsRuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.constant.FormConstants;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.WorkFlowApprovers;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalDashboardDTO;
import com.hand.hcf.app.workflow.dto.ApprovalDashboardDetailDTO;
import com.hand.hcf.app.workflow.dto.UserApprovalDTO;
import com.hand.hcf.app.workflow.dto.WorkFlowDocumentRefDTO;
import com.hand.hcf.app.workflow.dto.WorkflowDocumentDTO;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.persistence.WorkFlowDocumentRefMapper;
import com.hand.hcf.app.workflow.util.StringUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/12.
 * 工作流方法调用 统一入口方法
 */
@Service
public class WorkFlowApprovalService {
    @Autowired
    private BaseClient baseClient;
    @Autowired
    private DefaultWorkflowIntegrationServiceImpl defaultWorkflowIntegrationService;

    @Autowired
    private WorkFlowDocumentRefMapper workFlowDocumentRefMapper;

    @Autowired
    private ApprovalFormService approvalFormService;
    @Autowired
    private WorkFlowRefApproversService workFlowRefApproversService;
    @Autowired
    private CommonControllerImpl organizationInterface;

    @Autowired
    private MapperFacade mapperFacade;


    public Map<String, Set<UUID>> getRuleApproverUserOIDs(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        return defaultWorkflowIntegrationService.getApproverUserOids(droolsRuleApprovalNodeDTO.getRuleApproverDTOs(),
                droolsRuleApprovalNodeDTO.getFormValues(),
                droolsRuleApprovalNodeDTO.getApplicantOid(),
                droolsRuleApprovalNodeDTO);
    }


    /**
     * 【仪表盘】-我的单据
     * tabNumber=1(被退回的单据)
     * tabNumber=2(未完成的单据)
     * @param tabNumber
     * @return
     */
    public List<WorkflowDocumentDTO> listMyDocument(Integer tabNumber){
        List<WorkflowDocumentDTO> list = new ArrayList<>();
        List<Integer> statusList = new ArrayList<>();
        if(tabNumber == 1){
            statusList.add(DocumentOperationEnum.APPROVAL_REJECT.getId());//1005审批驳回
            statusList.add(2001);//2001审核驳回
        }else if(tabNumber == 2){
            statusList.add(DocumentOperationEnum.APPROVAL.getId());//1002审批中
            //statusList.add(DocumentOperationEnum.APPROVAL_PASS.getId());//1004审批通过
        }
        List<WorkFlowDocumentRef> workFlowDocumentRefList = workFlowDocumentRefMapper.selectList(
                new EntityWrapper<WorkFlowDocumentRef>()
                        .in(CollectionUtils.isNotEmpty(statusList), "status", statusList)
                        .eq("applicant_oid", OrgInformationUtil.getCurrentUserOid())
                        .orderBy("applicant_date", false));

        //封装数据
        if(workFlowDocumentRefList.size() > 0){
            for(WorkFlowDocumentRef workFlowDocumentRef : workFlowDocumentRefList) {
                WorkflowDocumentDTO documentDTO = new WorkflowDocumentDTO();
                documentDTO.setId(workFlowDocumentRef.getDocumentId());
                documentDTO.setType(workFlowDocumentRef.getDocumentCategory());
                documentDTO.setCode(workFlowDocumentRef.getDocumentNumber());
                //documentDTO.setName(EntityTypeDescEnum.parse(type).getDes());
                ApprovalForm approvalForm = approvalFormService.getByOid(workFlowDocumentRef.getFormOid());
                if (approvalForm != null){
                    documentDTO.setName(approvalForm.getFormName());
                }
                documentDTO.setRemark(workFlowDocumentRef.getRemark());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                ZonedDateTime applicantDate = workFlowDocumentRef.getApplicantDate();

                if (applicantDate != null) {
                    documentDTO.setCreatedTime(applicantDate.format(formatter));
                }

                documentDTO.setCurrency(workFlowDocumentRef.getCurrencyCode());
                documentDTO.setAmount(workFlowDocumentRef.getAmount());
                documentDTO.setStatusCode(workFlowDocumentRef.getStatus());
                String statusName = null;
                if(DocumentOperationEnum.APPROVAL_REJECT.getId().equals(workFlowDocumentRef.getStatus())){
                    statusName = "审批驳回";
                }else if(new Integer(2001).equals(workFlowDocumentRef.getStatus())){
                    statusName = "审核驳回";
                }else if(DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())){
                    statusName = "审核中";
                }else if(DocumentOperationEnum.APPROVAL_PASS.getId().equals(workFlowDocumentRef.getStatus())){
                    statusName = "审批通过";
                }
                documentDTO.setStatusName(statusName);

                if(tabNumber == 1) {
                    //对于退回单据，rejecterName是指驳回人
                    if (workFlowDocumentRef.getLastApproverOid() !=null) {
                        //最后审批人
                        UserApprovalDTO userDTO = baseClient.getUserByUserOid(workFlowDocumentRef.getLastApproverOid());
                        if (userDTO != null) {
                            documentDTO.setRejecterName(userDTO.getFullName());
                        }
                    }
                }else if(tabNumber == 2){
                    //对于未完成单据，rejecterName是指当前审批人
                    List<WorkFlowApprovers> workFlowApprovers = workFlowRefApproversService.getWorkflowApproversByRefIdAndNodeOid(workFlowDocumentRef.getId(),workFlowDocumentRef.getApprovalNodeOid());
                    StringBuilder sb = new StringBuilder();
                    for(WorkFlowApprovers workFlowApproversl : workFlowApprovers){
                        UserApprovalDTO userDTO =baseClient.getUserByUserOid(workFlowApproversl.getApproverOid());
                        if (userDTO != null) {
                            sb.append(userDTO.getFullName()+",");
                        }
                    }
                    documentDTO.setRejecterName(sb.toString());
                }
//                documentDTO.setNodeName();
                list.add(documentDTO);
            }
        }
        return list;
    }

    public ApprovalDashboardDTO getApprovalDashboardDetailDTOList(){
        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        List<ApprovalDashboardDetailDTO>  approvalDashboardDetailDTOList = workFlowDocumentRefMapper.getApprovalListDashboard(userOid);
        List<ApprovalDashboardDetailDTO> list = new LinkedList<>();
        Integer totalCount = 0;
        Map<String, String> categoryMap = new HashMap<String, String>();

        if (approvalDashboardDetailDTOList != null && approvalDashboardDetailDTOList.size() > 0) {
            List<SysCodeValueCO> sysCodeValueCOList = organizationInterface.listSysValueByCodeConditionByEnabled(FormConstants.SYS_CODE_FORM_TYPE, true);

            for (SysCodeValueCO sysCodeValueCO : sysCodeValueCOList) {
                categoryMap.put(sysCodeValueCO.getValue(), sysCodeValueCO.getName());
            }
        }

        for (ApprovalDashboardDetailDTO approvalDashboardDetailDTO : approvalDashboardDetailDTOList) {
            ApprovalDashboardDetailDTO detailDTO = new ApprovalDashboardDetailDTO();
            detailDTO.setCount(approvalDashboardDetailDTO.getCount());
            detailDTO.setType(approvalDashboardDetailDTO.getType());
            String name = null;
            if(approvalDashboardDetailDTO.getType() != null){
                //name = EntityTypeDescEnum.parse(Integer.valueOf(approvalDashboardDetailDTO.getType())).getDes();
                name = categoryMap.get(approvalDashboardDetailDTO.getType());
            }
            detailDTO.setName(name);
            totalCount += approvalDashboardDetailDTO.getCount();
            list.add(detailDTO);
        }
        ApprovalDashboardDTO approvalDashboardDTO = new ApprovalDashboardDTO();
        approvalDashboardDTO.setTotalCount(totalCount);
        approvalDashboardDTO.setApprovalDashboardDetailDTOList(list);
        return approvalDashboardDTO;
    }

    /**
     * 待办事项-待审批单据-单据列表
     * @param documentCategory 单据大类
     * @param documentTypeId 单据类型id
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param remark 备注
     * @param documentNumber 单据编号
     * @param mybatisPage 分页信息
     * @return
     */
    public List<WorkFlowDocumentRefDTO> getApprovalToPendDeatil(Integer documentCategory, Long documentTypeId, String applicantName, ZonedDateTime beginDate, ZonedDateTime endDate, Double amountFrom, Double amountTo, String remark, String documentNumber, Page mybatisPage) {

        List<WorkFlowDocumentRefDTO> list = new LinkedList<>();

        Map<String, String> categoryMap = new HashMap<String, String>();

        List<SysCodeValueCO> sysCodeValueCOList = organizationInterface.listSysValueByCodeConditionByEnabled(FormConstants.SYS_CODE_FORM_TYPE, true);
        for (SysCodeValueCO sysCodeValueCO : sysCodeValueCOList) {
            categoryMap.put(sysCodeValueCO.getValue(), sysCodeValueCO.getName());
        }

        //获取当前登录用户(审批人)
        UUID userOid = OrgInformationUtil.getCurrentUserOid();

        // 过滤特殊字符
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = StringUtil.escapeSpecialCharacters(documentNumber);
            //单据编号模糊查询
            documentNumber = '%' + documentNumber + '%';
        }

        //申请人模糊查询
        if (StringUtils.isNotEmpty(applicantName)) {
            applicantName = '%' + applicantName + '%';
        }

        // 备注模糊查询
        if (StringUtils.isNotEmpty(remark)) {
            remark = '%' + remark + '%';
        }

        //单据编号模糊查询
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = '%' + documentNumber + '%';
        }

        List<WorkFlowDocumentRef> workFlowDocumentRefList = workFlowDocumentRefMapper.getApprovalToPendDeatil(userOid,documentCategory,documentTypeId,applicantName,beginDate,endDate,amountFrom, amountTo,remark,documentNumber,mybatisPage);
        for (WorkFlowDocumentRef workFlowDocumentRef : workFlowDocumentRefList) {

            //设置相同属性
            WorkFlowDocumentRefDTO workFlowDocumentRefDTO = mapperFacade.map(workFlowDocumentRef, WorkFlowDocumentRefDTO.class);
            //转化其他属性
            if (workFlowDocumentRef.getDocumentCategory() != null) {
                workFlowDocumentRefDTO.setDocumentCategoryName(categoryMap.get(workFlowDocumentRef.getDocumentCategory().toString()));
            }
            if (workFlowDocumentRef.getSubmitDate() != null) {
                workFlowDocumentRefDTO.setSubmittedDate(workFlowDocumentRef.getSubmitDate());
            }
            if (workFlowDocumentRef.getDocumentOid() != null) {
                workFlowDocumentRefDTO.setEntityOid(workFlowDocumentRef.getDocumentOid());
            }
            list.add(workFlowDocumentRefDTO);
        }


        return list;
    }

    /**
     * 待办事项-待审批单据-分类信息
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
    public List<ApprovalDashboardDetailDTO> getApprovalToPendTotal(Integer documentCategory, Long documentTypeId, String applicantName, ZonedDateTime beginDate, ZonedDateTime endDate, Double amountFrom, Double amountTo, String remark, String documentNumber) {
        Map<String, String> categoryMap = new HashMap<String, String>();

        List<SysCodeValueCO> sysCodeValueCOList = organizationInterface.listSysValueByCodeConditionByEnabled(FormConstants.SYS_CODE_FORM_TYPE, true);
        for (SysCodeValueCO sysCodeValueCO : sysCodeValueCOList) {
            categoryMap.put(sysCodeValueCO.getValue(), sysCodeValueCO.getName());
        }
        //获取当前登录用户(审批人)
        UUID userOid = OrgInformationUtil.getCurrentUserOid();

        // 过滤特殊字符
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = StringUtil.escapeSpecialCharacters(documentNumber);
            //单据编号模糊查询
            documentNumber = '%' + documentNumber + '%';
        }

        //申请人模糊查询
        if (StringUtils.isNotEmpty(applicantName)) {
            applicantName = '%' + applicantName + '%';
        }

        // 备注模糊查询
        if (StringUtils.isNotEmpty(remark)) {
            remark = '%' + remark + '%';
        }

        //单据编号模糊查询
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = '%' + documentNumber + '%';
        }

        List<ApprovalDashboardDetailDTO>  approvalDashboardDetailDTOList = workFlowDocumentRefMapper.getApprovalToPendTotal(userOid,documentCategory,documentTypeId,applicantName,beginDate,endDate,amountFrom, amountTo,remark,documentNumber);
        if (approvalDashboardDetailDTOList.size() > 0 && approvalDashboardDetailDTOList != null) {
            for (ApprovalDashboardDetailDTO approvalDashboardDetailDTO : approvalDashboardDetailDTOList) {
                if (approvalDashboardDetailDTO.getType() != null) {
                    approvalDashboardDetailDTO.setName((categoryMap.get(approvalDashboardDetailDTO.getType())));
                }
            }
        }

        return approvalDashboardDetailDTOList;
    }

    /**
     * 待办事项-被退回单据/未完成单据
     * @param tabNumber tabNumber=1(被退回的单据) tabNumber=2(未完成的单据)
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
     * @param mybatisPage 分页信息
     * @return
     */
    public List<WorkFlowDocumentRefDTO> listMyDocumentDetail(Integer tabNumber,Integer documentCategory, Long documentTypeId, String applicantName, ZonedDateTime beginDate, ZonedDateTime endDate, Double amountFrom, Double amountTo, UUID lastApproverOid, String approvalNodeName, String remark, String documentNumber, Page mybatisPage){
        List<WorkFlowDocumentRefDTO> list = new ArrayList<>();
        List<WorkFlowDocumentRef> workFlowDocumentRefList = null;
        Map<String, String> categoryMap = new HashMap<String, String>();

        List<SysCodeValueCO> sysCodeValueCOList = organizationInterface.listSysValueByCodeConditionByEnabled(FormConstants.SYS_CODE_FORM_TYPE, true);
        for (SysCodeValueCO sysCodeValueCO : sysCodeValueCOList) {
            categoryMap.put(sysCodeValueCO.getValue(), sysCodeValueCO.getName());
        }

        //审批节点名称模糊查询
        if (StringUtils.isNotEmpty(approvalNodeName)) {
            approvalNodeName = '%' + approvalNodeName + '%';
        }

        //申请人名称模糊查询
        if (StringUtils.isNotEmpty(applicantName)) {
            applicantName = '%' + applicantName + '%';
        }

        //单据编号模糊查询
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = '%' + documentNumber + '%';
        }

        //申请人
        UUID applicantOid = OrgInformationUtil.getCurrentUserOid();

        //用于传递参数
        WorkFlowDocumentRefDTO workFlowDocumentRefDTO = new WorkFlowDocumentRefDTO();
        workFlowDocumentRefDTO.setDocumentCategory(documentCategory);
        workFlowDocumentRefDTO.setDocumentTypeId(documentTypeId);
        workFlowDocumentRefDTO.setApplicantName(applicantName);
        workFlowDocumentRefDTO.setApproverOid(lastApproverOid);
        workFlowDocumentRefDTO.setNodeName(approvalNodeName);
        workFlowDocumentRefDTO.setRemark(remark);
        workFlowDocumentRefDTO.setDocumentNumber(documentNumber);
        workFlowDocumentRefDTO.setApplicantOid(applicantOid);

        workFlowDocumentRefList = workFlowDocumentRefMapper
                .getRejectORUnFinishedList(workFlowDocumentRefDTO,beginDate,endDate,amountFrom, amountTo,tabNumber,mybatisPage);

        //封装数据
        if(workFlowDocumentRefList.size() > 0){
            for(WorkFlowDocumentRef workFlowDocumentRef : workFlowDocumentRefList) {
                //设置相同属性
                WorkFlowDocumentRefDTO documentDTO = mapperFacade.map(workFlowDocumentRef, WorkFlowDocumentRefDTO.class);

                //转化其他属性
                if (workFlowDocumentRef.getDocumentCategory() != null) {
                    documentDTO.setDocumentCategoryName(categoryMap.get(workFlowDocumentRef.getDocumentCategory().toString()));
                }
                if (workFlowDocumentRef.getLastUpdatedDate() != null) {
                    documentDTO.setRejectTime(workFlowDocumentRef.getLastUpdatedDate());
                }
                if (workFlowDocumentRef.getSubmitDate() != null) {
                    documentDTO.setSubmittedDate(workFlowDocumentRef.getSubmitDate());
                }
                if (workFlowDocumentRef.getDocumentOid() != null) {
                    documentDTO.setEntityOid(workFlowDocumentRef.getDocumentOid());
                }

                if(tabNumber == 1) {
                    //对于退回单据，rejecterName是指驳回人
                    if (workFlowDocumentRef.getLastApproverOid() !=null) {
                        //最后审批人
                        UserApprovalDTO userDTO = baseClient.getUserByUserOid(workFlowDocumentRef.getLastApproverOid());
                        if (userDTO != null) {
                            documentDTO.setRejecterName(userDTO.getFullName());
                        }
                    }
                }else if(tabNumber == 2){
                    //对于未完成单据，rejecterName是指当前审批人
                    List<WorkFlowApprovers> workFlowApprovers = workFlowRefApproversService.getWorkflowApproversByRefIdAndNodeOid(workFlowDocumentRef.getId(),workFlowDocumentRef.getApprovalNodeOid());
                    StringBuilder sb = new StringBuilder();
                    for(WorkFlowApprovers workFlowApproversl : workFlowApprovers){
                        UserApprovalDTO userDTO =baseClient.getUserByUserOid(workFlowApproversl.getApproverOid());
                        if (userDTO != null) {
                            sb.append(userDTO.getFullName()+",");
                        }
                    }
                    documentDTO.setRejecterName(sb.toString());
                }
                documentDTO.setNodeName(workFlowDocumentRef.getApprovalNodeName());
                list.add(documentDTO);
            }
        }
        return list;
    }

}
