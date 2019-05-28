package com.hand.hcf.app.ant.attachment.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.accrual.persistence.AccrualExpenseTypeMapper;
import com.hand.hcf.app.ant.attachment.domain.AttachmentType;
import com.hand.hcf.app.ant.attachment.domain.ExpReportTypeAttchment;
import com.hand.hcf.app.ant.attachment.persistence.AttachmentTypeMapper;
import com.hand.hcf.app.ant.attachment.persistence.ExpReportTypeAttchMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 单据类型附件权限设置service
 * @date 2019/5/16 15:38
 */
@Service
@Transactional
public class ExpReportTypeAttchmentService extends BaseService<ExpReportTypeAttchMapper, ExpReportTypeAttchment> {

    @Autowired
    private ExpReportTypeAttchMapper expReportTypeAttchMapper;

    @Autowired
    private AttachmentTypeMapper attachmentTypeMapper;

    @Autowired
    private ExpenseReportTypeMapper expenseReportTypeMapper;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AccrualExpenseTypeMapper accrualExpenseTypeMapper;

    @Autowired
    private WorkflowControllerImpl workflowClient;

    /**
     * 新增 单据类型
     *
     * @param expReportTypeAttchment
     * @return
     */
    @Transactional
    public ExpReportTypeAttchment createAttachmentSetting(ExpReportTypeAttchment expReportTypeAttchment) {

        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        expReportTypeAttchment.setTenantId(tenantId);
        expReportTypeAttchment.setSetOfBooksId(setOfBooksId);
        String docTypeName = null;
        if (null != expReportTypeAttchment) {
            String docTypeCode = expReportTypeAttchment.getDocTypeCode();
            if (StringUtils.isEmpty(docTypeCode)) {
                throw new BizException("create failed");
            }
            ExpenseReportType expenseReportTypeOne = new ExpenseReportType();
            expenseReportTypeOne.setTenantId(tenantId);
            expenseReportTypeOne.setSetOfBooksId(setOfBooksId);
            expenseReportTypeOne.setReportTypeCode(docTypeCode);
            ExpenseReportType expenseReportType = expenseReportTypeMapper.selectOne(expenseReportTypeOne);
            if (null != expenseReportType) {
                docTypeName = expenseReportType.getReportTypeName();
            }
            //在单据类型名称为空的情况下再次查询
            if(StringUtils.isEmpty(docTypeName)) {
                ExpenseAccrualType expenseAccrualTypeOne = new ExpenseAccrualType();
                expenseAccrualTypeOne.setTenantId(tenantId);
                expenseAccrualTypeOne.setSetOfBooksId(setOfBooksId);
                expenseAccrualTypeOne.setExpAccrualTypeCode(docTypeCode);
                ExpenseAccrualType expenseAccrualType = accrualExpenseTypeMapper.selectOne(expenseAccrualTypeOne);
                if (null != expenseAccrualType) {
                    docTypeName = expenseAccrualType.getExpAccrualTypeName();
                }
            }
        }
        expReportTypeAttchment.setDocTypeName(docTypeName);
        expReportTypeAttchMapper.insert(expReportTypeAttchment);
        return expReportTypeAttchment;
    }

    /**
     * 新增 单据类型附件设置
     *
     * @param attchmentTypeList
     * @param expReportTypeId
     * @return
     */
    @Transactional
    public List<AttachmentType> createAttachmentType(List<AttachmentType> attchmentTypeList, String expReportTypeId) {
        if (null == expReportTypeId) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        Long expReportTypeIdvlaue = Long.valueOf(expReportTypeId);
        for (AttachmentType attchmentType : attchmentTypeList) {
            if(StringUtils.isNotEmpty(attchmentType.getAttachmentName())) {
                AttachmentType attchmentType1 = new AttachmentType();
                attchmentType1.setAttachmentName(attchmentType.getAttachmentName());
                attchmentType1.setShowed(attchmentType.getShowed());
                attchmentType1.setUploaded(attchmentType.getUploaded());
                attchmentType1.setExpReportTypeId(expReportTypeIdvlaue);
                if (null != attchmentType.getId()) {
                    AttachmentType attchmentType2 = attachmentTypeMapper.selectById(attchmentType.getId());
                    if (null != attchmentType2) {
                        attchmentType1.setId(attchmentType.getId());
                        int flag = attachmentTypeMapper.updateById(attchmentType1);
                        if (flag < 0) {
                            throw new BizException("update failed");
                        }
                    }
                } else {
                        int flag = attachmentTypeMapper.insert(attchmentType1);
                        if (flag < 0) {
                            throw new BizException("create failed");
                        }

                }
            }
            else{
                if (null != attchmentType.getId()) {
                    attachmentTypeMapper.deleteById(attchmentType.getId());
                }
            }
        }
        return attchmentTypeList;
    }

    /**
     * 更新 单据类型附件设置
     *
     * @param expReportTypeId
     * @param attchmentTypeList
     * @return
     */
    @Transactional
    public List<AttachmentType> updateAttachmentType(String expReportTypeId, List<AttachmentType> attchmentTypeList) {
        for (AttachmentType attchmentType : attchmentTypeList) {
            if (null != attchmentType) {
                attchmentType.setExpReportTypeId(Long.valueOf(expReportTypeId));
                int flag = attachmentTypeMapper.updateById(attchmentType);
                if (flag < 0) {
                    throw new BizException("update failed");
                }
            }
        }
        return attchmentTypeList;
    }

    /**
     * 自定义条件查询 单据类型附件查询(分页)--List中全部信息以及搜索区域
     *
     * @param docTypeCode
     * @param page
     * @return
     */
    public List<ExpReportTypeAttchment> pageAttachmentSettingByCond(String docTypeCode,String dcoTypeName, Page page) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        List<ExpReportTypeAttchment> list = expReportTypeAttchMapper.selectPage(page,
                new EntityWrapper<ExpReportTypeAttchment>()
                        .eq("tenant_id", tenantId)
                        .eq("set_of_books_id", setOfBooksId)
                        .like("doc_type_code", docTypeCode, SqlLike.DEFAULT)
                        .like("doc_type_name", dcoTypeName, SqlLike.DEFAULT)
                        .orderBy("doc_type_code"));

        return list;
    }

    /**
     * 获取所有已创建的单据类型--头信息 LOV弹窗控制避免重复
     *
     * @param page
     * @return
     */
    public List<ExpReportTypeAttchment> getByTypeCode(Page page) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        List<ExpReportTypeAttchment> list = expReportTypeAttchMapper.selectPage(page,
                new EntityWrapper<ExpReportTypeAttchment>()
                        .eq("tenant_id", tenantId)
                        .eq("set_of_books_id", setOfBooksId)
                        );

        return list;
    }


    /**
     * 根据ID查询 单据类型附件设置
     *
     * @param attachTypeId --附件类型id
     * @return
     */
    public AttachmentType getAttachmentTypeById(String attachTypeId) {
        return attachmentTypeMapper.selectById(attachTypeId);
    }

    /**
     * 根据外键id,获取单据对应的所有附件设置信息
     *
     * @param attachTypeId --附件类型id
     * @return
     */
    public List<AttachmentType> getAttachmentTypeListById(String attachTypeId) {
        Long expReportTypeIdvlaue = Long.valueOf(attachTypeId);
        List<AttachmentType> list = attachmentTypeMapper.selectList(
                new EntityWrapper<AttachmentType>().eq("exp_report_type_id", expReportTypeIdvlaue));
        return list;
    }

    /**
     * 修改 单据类型附件设置
     *
     * @param expReportTypeAttchment
     * @return
     */
    @Transactional
    public ExpReportTypeAttchment updateAttachmentSetting(ExpReportTypeAttchment expReportTypeAttchment) {
        //校验
        if (expReportTypeAttchment == null || expReportTypeAttchment.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        ExpReportTypeAttchment typeAttchment = expReportTypeAttchMapper.selectById(expReportTypeAttchment.getId());
        if (typeAttchment == null) {
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        if (expReportTypeAttchment.getEnabled() == null || "".equals(expReportTypeAttchment.getEnabled())) {
            expReportTypeAttchment.setEnabled(typeAttchment.getEnabled());
        }
        expReportTypeAttchMapper.updateById(expReportTypeAttchment);
        return expReportTypeAttchment;
    }

    /**
     * 删除 根据单据类型附件设置id
     *
     * @param id
     * @return
     */
    @Transactional
    public void deleteAttachmentSetting(Long id) {
        expReportTypeAttchMapper.deleteById(id);
    }

    /**
     * 自定义条件查询 费用预提单类型定义(分页)
     *
     * @param setOfBooksId
     * @param expAccrualTypeCode
     * @param expAccrualTypeName
     * @param page
     * @return
     */
    public List<ExpenseAccrualType> getExpenseAccrualTypeByCond(Long setOfBooksId,
                                                                String expAccrualTypeCode,
                                                                String expAccrualTypeName,
                                                                Page page) {
        List<ExpenseAccrualType> list = new ArrayList<>();

        if (setOfBooksId == null) {
            return list;
        } else{
            setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        }

        String dataAuthLabel = null;
        list = accrualExpenseTypeMapper.selectPage(page,
                new EntityWrapper<ExpenseAccrualType>()
                        .eq("set_of_books_id", setOfBooksId)
                        .like("exp_accrual_type_code", expAccrualTypeCode, SqlLike.DEFAULT)
                        .like("exp_accrual_type_name", expAccrualTypeName, SqlLike.DEFAULT)
                        .eq("enable_flag", true)
                        .orderBy("exp_accrual_type_code")
        );
        return list;
    }

    /**
     * 自定义条件查询 报账单类型(分页)
     *
     * @param setOfBooksId
     * @param reportTypeCode
     * @param reportTypeName
     * @param page
     * @return
     */
    public List<ExpenseReportType> getExpenseReportTypeByCond(Long setOfBooksId, String reportTypeCode, String reportTypeName, Page page) {
        List<ExpenseReportType> list = new ArrayList<>();

        if (setOfBooksId == null) {
            return list;
        }else{
            setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        }

        list = expenseReportTypeMapper.selectPage(page,
                new EntityWrapper<ExpenseReportType>()
                        .where("deleted = false")
                        .eq("set_of_books_id", setOfBooksId)
                        .like("report_type_code", reportTypeCode, SqlLike.DEFAULT)
                        .like("report_type_name", reportTypeName, SqlLike.DEFAULT)
                        .eq( "enabled", true)
                        .orderBy("report_type_code")
        );
        return list;
    }
}
