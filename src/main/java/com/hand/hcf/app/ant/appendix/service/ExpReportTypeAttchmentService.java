package com.hand.hcf.app.ant.appendix.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.appendix.domain.AttachmentType;
import com.hand.hcf.app.ant.appendix.domain.ExpReportTypeAttchment;
import com.hand.hcf.app.ant.appendix.persistence.AttachmentTypeMapper;
import com.hand.hcf.app.ant.appendix.persistence.ExpReportTypeAttchMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:  单据类型附件权限设置service
 * @author xu.chen02@hand-china.com
 * @version 1.0
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

    /**
     * 新增 单据类型
     *
     * @param expReportTypeAttchment
     * @return
     */
    @Transactional
    public ExpReportTypeAttchment createAttachmentSetting(ExpReportTypeAttchment expReportTypeAttchment){

        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        expReportTypeAttchment.setTenantId(tenantId);
        expReportTypeAttchment.setSetOfBooksId(setOfBooksId);
        String reportTypeName = null;
        if(null != expReportTypeAttchment) {
            String reportTypeCode = expReportTypeAttchment.getReportTypeCode();
            if(StringUtils.isEmpty(reportTypeCode)){
                throw new BizException("create failed");
            }
            ExpenseReportType expenseReportTypeOne = new  ExpenseReportType();
            expenseReportTypeOne.setTenantId(tenantId);
            expenseReportTypeOne.setSetOfBooksId(setOfBooksId);
            expenseReportTypeOne.setReportTypeCode(reportTypeCode);
            ExpenseReportType expenseReportType = expenseReportTypeMapper.selectOne(expenseReportTypeOne);
            if(null != expenseReportType) {
                reportTypeName = expenseReportType.getReportTypeName();
            }
        }
        expReportTypeAttchment.setReportTypeName(reportTypeName);
        expReportTypeAttchMapper.insert(expReportTypeAttchment);
        return expReportTypeAttchment;
    }

    /**
     * 新增 单据类型附件设置
     *
     * @param attchmentTypeList
     * @return
     */
    @Transactional
    public List<AttachmentType> createAttachmentType(List<AttachmentType> attchmentTypeList,String expReportTypeId){
        if(null == expReportTypeId){
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        Long expReportTypeIdvlaue = Long.valueOf(expReportTypeId);
        for(AttachmentType attchmentType:attchmentTypeList){
            AttachmentType attchmentType1 = new AttachmentType();
            if(null != attchmentType){
                String attachmentTypeName = attchmentType.getAttachmentTypeName();
                Boolean showed = attchmentType.getShowed();
                Boolean uploaded = attchmentType.getUploaded();
                attchmentType1.setAttachmentTypeName(attachmentTypeName);
                attchmentType1.setShowed(showed);
                attchmentType1.setUploaded(uploaded);
                attchmentType1.setExpReportTypeId(expReportTypeIdvlaue);
                if(null != attchmentType.getId()){
                    AttachmentType  attchmentType2 = attachmentTypeMapper.selectById(attchmentType.getId());
                    if(null != attchmentType2){
                        int flag = attachmentTypeMapper.updateById(attchmentType1);
                        if (flag < 0) {
                            throw new BizException("create failed");
                        }
                    }
                }else {
                    int flag = attachmentTypeMapper.insert(attchmentType1);
                    if (flag < 0) {
                        throw new BizException("create failed");
                    }
                }
            }
        }
        return attchmentTypeList ;
    }

    /**
     * 更新 单据类型附件设置
     *
     * @param attchmentTypeList
     * @return
     */
    @Transactional
    public List<AttachmentType> updateAttachmentType(List<AttachmentType> attchmentTypeList){
        for(AttachmentType attchmentType:attchmentTypeList){
            if(null != attchmentType) {
                int flag  = attachmentTypeMapper.updateById(attchmentType);
                if(flag<0){
                    throw new BizException("update failed");
                }
            }
        }
        return attchmentTypeList ;
    }

    /**
     * 自定义条件查询 单据类型附件设置查询(分页)
     */
    public List<ExpReportTypeAttchment> pageAttachmentSettingByCond(String reportTypeId, Page page){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        String reportTypeCode = null;
        ExpenseReportType expenseReportTypeOne = new ExpenseReportType();
        if(StringUtils.isNotEmpty(reportTypeId)) {
             expenseReportTypeOne = expenseReportTypeMapper.selectById(reportTypeId);
             if(null != expenseReportTypeOne ){
                 reportTypeCode = expenseReportTypeOne.getReportTypeCode();
             }
        }
        List<ExpReportTypeAttchment> list =  expReportTypeAttchMapper.selectPage(page,
                new EntityWrapper<ExpReportTypeAttchment>()
                       .eq("tenant_id",tenantId)
                       .eq("set_of_books_id",setOfBooksId)
                        .like( "report_type_code",reportTypeCode, SqlLike.DEFAULT)
                        .orderBy("report_type_code"));

        return list;
    }


    /**
     * 根据ID查询 单据类型附件设置
     *
     * @param attachTypeId --附件类型id
     * @return
     */
     public AttachmentType getAttachmentTypeById(String attachTypeId ){
         return attachmentTypeMapper.selectById(attachTypeId);
     }

    /**
     * 根据外键id,获取单据对应的所有附件设置信息
     *
     * @param attachTypeId --附件类型id
     * @return
     */
    public List<AttachmentType> getAttachmentTypeListById(String attachTypeId ){
        Long expReportTypeIdvlaue = Long.valueOf(attachTypeId);
        List<AttachmentType> list = attachmentTypeMapper.selectList(
                new EntityWrapper<AttachmentType>().eq("exp_report_type_id",expReportTypeIdvlaue));
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
}
