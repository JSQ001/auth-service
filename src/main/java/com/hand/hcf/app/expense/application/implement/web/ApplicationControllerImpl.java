package com.hand.hcf.app.expense.application.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.apply.expense.ExpenseApplicationInterface;
import com.hand.hcf.app.apply.expense.dto.*;
import com.hand.hcf.app.apply.mdata.ApplyApplicationInterface;
import com.hand.hcf.app.apply.prepayment.dto.CashPaymentRequisitionLineCO;
import com.hand.hcf.app.expense.application.domain.ApplicationHeader;
import com.hand.hcf.app.expense.application.domain.ApplicationType;
import com.hand.hcf.app.expense.application.domain.PrepaymentRequisitionRelease;
import com.hand.hcf.app.expense.application.service.ApplicationHeaderService;
import com.hand.hcf.app.expense.application.service.ApplicationTypeService;
import com.hand.hcf.app.expense.application.service.PrepaymentRequisitionReleaseService;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.MessageService;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.RespCode;
import com.netflix.discovery.converters.Auto;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/27
 */
@RestController
public class ApplicationControllerImpl implements ExpenseApplicationInterface, ApplyApplicationInterface {

    @Autowired
    private ApplicationHeaderService applicationHeaderService;

    @Autowired
    private ApplicationTypeService applicationTypeService;

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private PrepaymentRequisitionReleaseService prepaymentRequisitionReleaseService;

    @Override
    public ApplicationHeaderCO getHeaderByOid(@RequestParam("documentOid") String documentOid) {
        return applicationHeaderService.getHeaderByDocumentOid(documentOid);
    }

    @Override
    public ApplicationCO getApplicationByDocumentId(Long documentId) {
        return applicationHeaderService.getApplicationByDocumentId(documentId);
    }


    @Override
    public void updateStatus(@RequestBody OperateDocumentCO operateDocumentDTO) {
       applicationHeaderService.updateStatus(operateDocumentDTO);
    }

    @Override
    public void updateFilterRule(@RequestParam("documentOid") String documentOid, @RequestParam("filterRule") Boolean filterRule) {
        applicationHeaderService.updateFilterRule(documentOid);
    }

    @Override
    public void updateByRollBackBudget(@RequestParam("id") Long id) {
        applicationHeaderService.rollBackBudget(id);
    }

    @Override
    public List<ApplicationHeaderCO> listHeaderByCondition(@RequestBody DocumentQueryParamCO paramDTO) {
        List<ApplicationHeaderCO> result = applicationHeaderService.queryByConditionByWorkFlow(paramDTO);
        return result;
    }

    @Override
    public Page<ApplicationTypeCO> queryApplicationTypeByCond(ApplicationTypeForOtherCO applicationTypeForOtherCO, int page, int size) {
        Page pageInfo = PageUtil.getPage(page,size);
        return applicationTypeService.queryApplicationTypeByCond(applicationTypeForOtherCO,pageInfo);
    }

    @Override
    public String getFormTypeNameByFormTypeId(@RequestParam("id") Long id) {
        ApplicationType applicationType = applicationTypeService.selectById(id);
        return applicationType != null ? applicationType.getTypeName() : null;
    }

    @Override
    public List<ApplicationAmountCO> getApplicationAmountById(@RequestParam("applicationId") Long applicationId) {
        return applicationHeaderService.getApplicationAmountById(applicationId);
    }

    @Override
    public void releasePrepaymentRequisitionRelease(@RequestParam("prepaymentId") Long prepaymentId) {
        prepaymentRequisitionReleaseService.releasePrepaymentRequisitionRelease(prepaymentId);
    }

    @Override
    public void createPrepaymentRequisitionRelease(@RequestBody List<PrepaymentRequisitionReleaseCO> prepaymentRequisitionReleaseCOS) {
        List<PrepaymentRequisitionRelease> prepaymentRequisitionRelease = mapper.mapAsList(prepaymentRequisitionReleaseCOS, PrepaymentRequisitionRelease.class);
        prepaymentRequisitionReleaseService.insertBatch(prepaymentRequisitionRelease);

    }

    @Override
    public List<PrepaymentRequisitionReleaseCO> getPrepaymentByDocumentNumber(@RequestParam(value = "documentNumber",required = true)String documentNumber) {
        return  applicationHeaderService.getPrepaymentByDocumentNumber(documentNumber);
    }
}
