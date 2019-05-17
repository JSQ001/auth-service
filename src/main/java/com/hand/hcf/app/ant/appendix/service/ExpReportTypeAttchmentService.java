package com.hand.hcf.app.ant.appendix.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.appendix.domain.ExpReportTypeAttchment;
import com.hand.hcf.app.ant.appendix.persistence.ExpReportTypeAttchMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private ExpenseReportTypeMapper expenseReportTypeMapper;

    /**
     * 新增 单据类型附件设置
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
     * 自定义条件查询 单据类型附件设置查询(分页)
     */
    public List<ExpReportTypeAttchment> pageAttachmentSettingByCond(String reportTypeId, Page page){
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        String reportTypeCode = null;
        ExpenseReportType expenseReportTypeOne = new ExpenseReportType();
        if(null != reportTypeId) {
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
     * @param id
     * @return
     */


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
