package com.hand.hcf.app.expense.application.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.common.co.ApplicationAmountCO;
import com.hand.hcf.app.common.co.ApplicationHeaderCO;
import com.hand.hcf.app.common.co.PrepaymentRequisitionReleaseCO;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.application.domain.ExpenseRequisitionReqRelease;
import com.hand.hcf.app.expense.application.web.dto.ApplicationAssociateDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationAssociatePrepaymentDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationHeaderAbbreviateDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationHeaderWebDTO;
import com.hand.hcf.app.expense.application.web.dto.ApplicationFinancRequsetDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 * 费用申请单头表 Mapper 接口
 * </p>
 *
 * @author bin.xie
 * @since 2018-11-21
 */
public interface ApplicationHeaderMapper extends BaseMapper<ApplicationHeader> {

    /**
     * 根据ID查询申请单头信息，以及其头维度信息
     * @param id
     * @param headerFlag
     * @return
     */
    ApplicationHeaderWebDTO getHeaderWebDTOById(@Param("id") Long id,
                                                @Param("headerFlag") Integer headerFlag);

    /**
     * 分页条件查询费用申请单头信息
     * @param rowBounds
     * @param wrapper
     * @return
     */
    List<ApplicationHeaderWebDTO> listByCondition(RowBounds rowBounds,
                                                  @Param("ew") Wrapper wrapper);

    /**
     * List<ApplicationHeaderWebDTO>
     * @param rowBounds 分页参数
     * @param wrapper wrapper
     * @return List<ApplicationHeaderWebDTO>
     */
    List<ApplicationHeaderWebDTO> listCloseByCondition(RowBounds rowBounds,
                                                       @Param("ew") Wrapper wrapper);
    /**
     * 获取查询条件的总数
     *
     * @param wrapper
     * @return
     */
    int getCountByCondition(@Param("ew") Wrapper wrapper);
    /**
     * 根据ID查询详情
     * @param id
     * @return
     */
    ApplicationHeaderWebDTO getHeaderDetailById(@Param("id") Long id);

    ApplicationHeader getHeaderByOid(@Param("documentOid") String documentOid);

    /**
     * 根据单据OID查询单据头信息
     * @param documentOid
     * @return
     */
    ApplicationHeaderCO getHeaderByDocumentOid(String documentOid);

    /**
     * 根据单据id获取单据头信息
     * @param documentId
     * @return
     */
    ApplicationHeaderCO getHeaderByDocumentId(Long documentId);

    /**
     * 工作流查询
     * @param submitDateFrom
     * @param submitDateTo
     * @param amountFrom
     * @param amountTo
     * @param businessCode
     * @param typeId
     * @param currencyCode
     * @param description
     * @param companyId
     * @return
     */
    List<ApplicationHeaderCO> listConditionByWorkFlow(@Param("submitDateFrom") ZonedDateTime submitDateFrom,
                                                       @Param("submitDateTo") ZonedDateTime submitDateTo,
                                                       @Param("amountFrom") BigDecimal amountFrom,
                                                       @Param("amountTo") BigDecimal amountTo,
                                                       @Param("businessCode") String businessCode,
                                                       @Param("typeId") Long typeId,
                                                       @Param("currencyCode") String currencyCode,
                                                       @Param("description") String description,
                                                       @Param("companyId") Long companyId);
    /**
     * 分页条件查询费用申请单头信息
     * @param rowBounds
     * @param wrapper
     * @return
     */
    List<ApplicationHeaderWebDTO> listByFinancial(RowBounds rowBounds,
                                                  @Param("ew") Wrapper wrapper);

    /**
     * 查询可关闭的申请单
     * @param ids 单据id集合
    * @return
     */
    List<ApplicationAssociateDTO> listAssociateInfo(@Param("ids") List<Long> ids);

    List<ApplicationAssociatePrepaymentDTO> listInfoByCondition(RowBounds rowBounds,
                                                                @Param("ew") Wrapper wrapper,
                                                                @Param("sourceDocumentCategory") String sourceDocumentCategory,
                                                                @Param("currencyCode") String currencyCode,
                                                                @Param("typeId") List<Long> typeId,
                                                                @Param("status") Integer status,
                                                                @Param("applicationNumber") String applicationNumber,
                                                                @Param("applicationType") String applicationType);

    /**
     * 查询报账单可关联的申请单信息
     * @param rowBounds
     * @param dto
     * @return
     */
    List<ApplicationHeaderAbbreviateDTO> selectRelateExpenseReportApplications(RowBounds rowBounds,
                                                                               ApplicationHeaderAbbreviateDTO dto);

    List<ApplicationFinancRequsetDTO>  listByfincancies (RowBounds rowBounds,
                                                         @Param("ew") Wrapper wrapper,
                                                         @Param("associatedAmountFrom")BigDecimal associatedAmountFrom,
                                                         @Param("associatedAmountTo")BigDecimal associatedAmountTo,
                                                         @Param("relevanceAmountFrom")BigDecimal relevanceAmountFrom,
                                                         @Param("relevanceAmountTo")BigDecimal relevanceAmountTo);

    public List<ApplicationAmountCO> getApplicationAmountById(@Param("applicationId") Long applicationId);

 /*
       获取关联申请单的预付款信息。
     */
    List<PrepaymentRequisitionReleaseCO> getPrepaymentBydocumentNumber(@Param("documentNumber") String documentNumber,
                                                         @Param("ew") Wrapper<ExpenseRequisitionReqRelease> eq);

    /**
     * 获取报账单关联申请单信息
     */
    List<ApplicationHeaderWebDTO>queryReleaseByReport(@Param("documentNumber") String businessCode, RowBounds pageable);
}
