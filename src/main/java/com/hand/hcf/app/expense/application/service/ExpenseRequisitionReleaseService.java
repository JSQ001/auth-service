package com.hand.hcf.app.expense.application.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.application.domain.ExpenseRequisitionRelease;
import com.hand.hcf.app.expense.application.persistence.ExpenseRequisitionReleaseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/15 09:28
 * @remark
 */
@Service
public class ExpenseRequisitionReleaseService extends BaseService<ExpenseRequisitionReleaseMapper,ExpenseRequisitionRelease> {

    /**
     * 根据来源单据信息获取申请释放信息
     * @param sourceDocumentCategory
     * @param sourceDocumentId
     * @param sourceDocumentLineId
     * @param sourceDocumentDistId
     * @return
     */
    @Transactional(readOnly = true)
    public List<ExpenseRequisitionRelease> getExpenseRequisitionReleaseBySourceDocumentMsg(String sourceDocumentCategory,
                                                                                           Long sourceDocumentId,
                                                                                           Long sourceDocumentLineId,
                                                                                           Long sourceDocumentDistId){
        return baseMapper.selectList(new EntityWrapper<ExpenseRequisitionRelease>()
                .eq(TypeConversionUtils.isNotEmpty(sourceDocumentCategory),"source_doc_category",sourceDocumentCategory)
                .eq(TypeConversionUtils.isNotEmpty(sourceDocumentId),"source_doc_id",sourceDocumentId)
                .eq(TypeConversionUtils.isNotEmpty(sourceDocumentLineId),"source_doc_line_id",sourceDocumentLineId)
                .eq(TypeConversionUtils.isNotEmpty(sourceDocumentDistId),"source_doc_dist_id",sourceDocumentDistId));
    }

    /**
     * 通过关联单据信息获取申请释放信息
     * @param relatedDocumentCategory
     * @param relatedDocumentId
     * @param relatedDocumentLineId
     * @param relatedDocumentDistId
     * @return
     */
    @Transactional(readOnly = true)
    public List<ExpenseRequisitionRelease> getExpenseRequisitionReleaseByRelatedDocumentMsg(String relatedDocumentCategory,
                                                                                            Long relatedDocumentId,
                                                                                            Long relatedDocumentLineId,
                                                                                            Long relatedDocumentDistId){
        return baseMapper.selectList(new EntityWrapper<ExpenseRequisitionRelease>()
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentCategory),"related_doc_category",relatedDocumentCategory)
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentId),"related_doc_id",relatedDocumentId)
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentLineId),"related_doc_line_id",relatedDocumentLineId)
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentDistId),"related_doc_dist_id",relatedDocumentDistId));
    }

    /**
     * 保存申请释放信息
     * @param release
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseRequisitionRelease saveExpenseRequisitionRelease(ExpenseRequisitionRelease release){
        if(release.getId() == null){
            baseMapper.insert(release);
        }else{
            baseMapper.updateAllColumnById(release);
        }
        return release;
    }

    /**
     * 清除申请释放信息
     * 若其他单据关联申请单，在工作流过程中导致单据退回申请人手中，并处于可修改状态时，需要清除释放信息
     * @param relatedDocumentCategory   单据类别(关联申请单的单据)
     * @param relatedDocumentId         单据ID(关联申请单的单据)
     * @param relatedDocumentLineId     单据行ID(关联申请单的单据)
     * @param relatedDocumentDistId     单据分摊航ID(关联申请单的单据)
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpenseRequisitionReleaseMsg(String relatedDocumentCategory,
                                                   Long relatedDocumentId,
                                                   Long relatedDocumentLineId,
                                                   Long relatedDocumentDistId){
        baseMapper.delete(new EntityWrapper<ExpenseRequisitionRelease>()
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentCategory),"related_doc_category",relatedDocumentCategory)
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentId),"related_doc_id",relatedDocumentId)
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentLineId),"related_doc_line_id",relatedDocumentLineId)
                .eq(TypeConversionUtils.isNotEmpty(relatedDocumentDistId),"related_doc_dist_id",relatedDocumentDistId));
    }
}
