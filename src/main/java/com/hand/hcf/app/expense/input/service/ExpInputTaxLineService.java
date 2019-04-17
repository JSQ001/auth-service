package com.hand.hcf.app.expense.input.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxDist;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxLine;
import com.hand.hcf.app.expense.input.dto.*;
import com.hand.hcf.app.expense.input.persistence.ExpInputTaxLineMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.StringUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Service
public class ExpInputTaxLineService extends BaseService<ExpInputTaxLineMapper, ExpInputTaxLine> {
    @Autowired
    private ExpInputTaxLineMapper expInputTaxLineMapper;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MapperFacade mapperFacade;


    @Autowired
    private ExpInputTaxDistService expInputTaxDistService;

    @Autowired
    private ExpInputTaxHeaderService expInputTaxHeaderService;


    public Page<ExpInputTaxLine> queryByHeaderId(Long headerId, Page page) {
        List<ExpInputTaxLineDTO> expInputTaxLines = expInputTaxLineMapper.listLineById(headerId, page);
        for (ExpInputTaxLineDTO line : expInputTaxLines) {

            SysCodeValueCO sysCodeValueCO1 = organizationService.getSysCodeValueByCodeAndValue("useType", line.getUseType());
            if (sysCodeValueCO1 != null) {
                line.setUseTypeName(sysCodeValueCO1.getName());
            }
            List<ExpInputForReportDistDTO> expInputForReportDistDTOS = expInputTaxDistService.listDistByLineId(line.getExpReportLineId(), line.getId());
            int x = 0;
            for (ExpInputForReportDistDTO dist : expInputForReportDistDTOS) {
                CompanyCO companyCO = organizationService.getCompanyById(dist.getCompanyId());
                dist.setCompanyName(companyCO == null ? null : companyCO.getName());
                DepartmentCO departmentCO = organizationService.getDepartmentById(dist.getDepartmentId());
                dist.setDepartmentName(departmentCO == null ? null : departmentCO.getName());
                if (dist.getSelectFlag().equals("Y")) {
                    x++;
                }
                if (x == expInputForReportDistDTOS.size()) {
                    line.setSelectFlag("Y");
                } else if (x == 0) {
                    line.setSelectFlag("N");
                } else {
                    line.setSelectFlag("P");
                }
            }
            line.setExpInputForReportDistDTOS(expInputForReportDistDTOS);
        }
        return page.setRecords(expInputTaxLines);
    }

    public List<ExpInputTaxLine> queryByHeaderId(Long headerId) {
        List<ExpInputTaxLine> expInputTaxLines = expInputTaxLineMapper.selectList(new EntityWrapper<ExpInputTaxLine>().eq("input_tax_header_id", headerId));
        return expInputTaxLines;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrUpdateLine(List<ExpInputForReportLineDTO> expInputForReportLineDTOS) {
        for (ExpInputForReportLineDTO line : expInputForReportLineDTOS) {
            //获取到头数据
            ExpInputTaxHeaderDTO header = expInputTaxHeaderService.queryById(line.getInputTaxHeaderId());

            //然后操作行数据 三种可能，新增，更新，删除
            ExpInputTaxLine expInputTaxLine = mapperFacade.map(line, ExpInputTaxLine.class);
            //头字段插入
            expInputTaxLine.setStatus(header.getStatus());
            expInputTaxLine.setAuditStatus(header.getAuditStatus());
            expInputTaxLine.setReverseFlag(header.getReverseFlag());
            expInputTaxLine.setUseType(header.getUseType());
            expInputTaxLine.setTransferProportion(header.getTransferProportion());
            //这里必须采用累加才能避免尾差
            expInputTaxLine.setBaseAmount(BigDecimal.ZERO);
            expInputTaxLine.setBaseFunctionAmount(BigDecimal.ZERO);
            expInputTaxLine.setAmount(BigDecimal.ZERO);
            expInputTaxLine.setFunctionAmount(BigDecimal.ZERO);
            //日志信息
            expInputTaxLine.setLastUpdatedBy(OrgInformationUtil.getUser().getId());
            expInputTaxLine.setLastUpdatedDate(ZonedDateTime.now());
            String lineSelectFlag = line.getSelectFlag() == null ? "N":line.getSelectFlag();
            //新增
            if (TypeConversionUtils.isEmpty(line.getId()) && !lineSelectFlag.equals("N")) {
                expInputTaxLine.setCreatedBy(OrgInformationUtil.getUser().getId());
                expInputTaxLine.setCreatedDate(ZonedDateTime.now());
                expInputTaxLineMapper.insert(expInputTaxLine);
            } else if (line.getId() != null && !lineSelectFlag.equals("N")) {
                //更新
                expInputTaxLineMapper.updateById(expInputTaxLine);
            } else if (line.getId() != null && lineSelectFlag.equals("N")) {
                //删除
                expInputTaxLineMapper.deleteById(line.getId());
            }

            //首先保存分配行数据,对于分配行，只有新增和删除两个可能
            for (ExpInputForReportDistDTO dist : line.getExpInputForReportDistDTOS()) {
                //如果id存在，但是选择标志变为N，则说明这个行被删除了
                if (dist.getId() != null && dist.getSelectFlag().equals("N")) {
                    expInputTaxDistService.deleteDistById(dist.getId());
                } else if (dist.getId() == null && dist.getSelectFlag().equals("Y")) {
                    ExpInputTaxDist expInputTaxDist = mapperFacade.map(dist, ExpInputTaxDist.class);
                    //头字段插入
                    expInputTaxDist.setInputTaxLineId(expInputTaxLine.getId());
                    expInputTaxDist.setStatus(header.getStatus());
                    expInputTaxDist.setAuditStatus(header.getAuditStatus());
                    expInputTaxDist.setReverseFlag(header.getReverseFlag());
                    expInputTaxDist.setUseType(header.getUseType());
                    expInputTaxDist.setTransferProportion(header.getTransferProportion());
                    //金额计算
                    BigDecimal tp = new BigDecimal(header.getTransferProportion());
                    expInputTaxDist.setAmount(dist.getBaseAmount().multiply(tp).setScale(2));
                    expInputTaxDist.setFunctionAmount(dist.getBaseFunctionAmount().multiply(tp).setScale(2));
                    //日志信息
                    expInputTaxDist.setLastUpdatedBy(OrgInformationUtil.getUser().getId());
                    expInputTaxDist.setLastUpdatedDate(ZonedDateTime.now());
                    expInputTaxDist.setCreatedBy(OrgInformationUtil.getUser().getId());
                    expInputTaxDist.setCreatedDate(ZonedDateTime.now());
                    //插入
                    expInputTaxDistService.insertDistData(expInputTaxDist);
                }
            }

            //更新行金额
            if(!lineSelectFlag.equals("N")) {
                ExpInputTaxLine l = expInputTaxLineMapper.selectById(expInputTaxLine.getId());
                if(l != null) {
                    ExpInputTaxSumAmountDTO sumAmount = expInputTaxDistService.getSumAmount(expInputTaxLine.getId());
                    l.setBaseAmount(sumAmount.getBaseAmount());
                    l.setBaseFunctionAmount(sumAmount.getBaseFunctionAmount());
                    l.setAmount(sumAmount.getAmount());
                    l.setFunctionAmount(sumAmount.getFunctionAmount());
                    expInputTaxLineMapper.updateById(l);
                }
            }

            //最后需要更新头的金额
            ExpInputTaxSumAmountDTO sumLineAmount = getSumAmount(line.getInputTaxHeaderId());
            header.setBaseAmount(sumLineAmount.getBaseAmount());
            header.setBaseFunctionAmount(sumLineAmount.getBaseFunctionAmount());
            header.setAmount(sumLineAmount.getAmount());
            header.setFunctionAmount(sumLineAmount.getFunctionAmount());
            header.setLastUpdatedBy(OrgInformationUtil.getUser().getId());
            header.setLastUpdatedDate(ZonedDateTime.now());
            expInputTaxHeaderService.insertOrUpdateHeader(header);
        }


        return true;
    }

    public Page getReportData(String documentNumber,
                              Long applicantId,
                              Long expenseTypeId,
                              BigDecimal amountFrom,
                              BigDecimal amountTo,
                              Long companyId,
                              Long departmentId,
                              String transferDateFrom,
                              String transferDateTo,
                              String description,
                              Long headerId,
                              Page page) {
        Wrapper<ExpInputTaxLine> wrapper = new EntityWrapper<ExpInputTaxLine>().like(StringUtils.hasText(documentNumber), "documentNumber", documentNumber)
                .eq(applicantId != null, "applicantId", applicantId)
                .eq(expenseTypeId != null, "expenseTypeId", expenseTypeId)
                .ge(amountFrom != null, "ableAmount", amountFrom)
                .le(amountTo != null, "ableAmount", amountTo)
                .eq(companyId != null, "company_id", companyId)
                .eq(departmentId != null, "department_id", departmentId)
                .like(StringUtils.hasText(description), "description", description)
                .ge(transferDateFrom != null, "transferDate", TypeConversionUtils.getStartTimeForDayYYMMDD(transferDateFrom))
                .le(transferDateTo != null, "transferDate", TypeConversionUtils.getEndTimeForDayYYMMDD(transferDateTo));

        List<ExpInputForReportLineDTO> expInputForReportLineDTOS = expInputTaxLineMapper.listExpInputTaxLine(wrapper, OrgInformationUtil.getCurrentSetOfBookId(), headerId, page);
        setDesc(expInputForReportLineDTOS);
        for (ExpInputForReportLineDTO line : expInputForReportLineDTOS) {
            List<ExpInputForReportDistDTO> expInputForReportDistDTOS = expInputTaxDistService.listDistByLineId(line.getExpReportLineId(), line.getId());
            int x = 0;
            for (ExpInputForReportDistDTO dist : expInputForReportDistDTOS) {
                CompanyCO companyCO = organizationService.getCompanyById(dist.getCompanyId());
                dist.setCompanyName(companyCO == null ? null : companyCO.getName());
                DepartmentCO departmentCO = organizationService.getDepartmentById(dist.getDepartmentId());
                dist.setDepartmentName(departmentCO == null ? null : departmentCO.getName());
                if (dist.getSelectFlag().equals("Y")) {
                    x++;
                }
                if (x == expInputForReportDistDTOS.size()) {
                    line.setSelectFlag("Y");
                } else if (x == 0) {
                    line.setSelectFlag("N");
                } else {
                    line.setSelectFlag("P");
                }
            }
            line.setExpInputForReportDistDTOS(expInputForReportDistDTOS);
        }
        page.setRecords(expInputForReportLineDTOS);
        return page;
    }

    public ExpInputTaxSumAmountDTO getSumAmount(Long inputTaxHeaderId) {
        return expInputTaxLineMapper.getSumAmount(inputTaxHeaderId);
    }

    public Boolean delete(Long id) {
        expInputTaxDistService.deleteByLineId(id);
        expInputTaxLineMapper.deleteById(id);
        //更新头的金额和相关信息
        ExpInputTaxHeaderDTO header = expInputTaxHeaderService.queryById(expInputTaxLineMapper.selectById(id).getInputTaxHeaderId());
        ExpInputTaxSumAmountDTO sumLineAmount = getSumAmount(id);
        header.setBaseAmount(sumLineAmount.getBaseAmount());
        header.setBaseFunctionAmount(sumLineAmount.getBaseFunctionAmount());
        header.setAmount(sumLineAmount.getAmount());
        header.setFunctionAmount(sumLineAmount.getFunctionAmount());
        header.setLastUpdatedBy(OrgInformationUtil.getUser().getId());
        header.setLastUpdatedDate(ZonedDateTime.now());
        return true;
    }

    public void deleteByHeaderId(Long id) {
        expInputTaxLineMapper.delete(new EntityWrapper<ExpInputTaxLine>().eq("input_tax_header_id", id));
    }

    public void setDesc(List<ExpInputForReportLineDTO> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            Set<Long> userIds = new HashSet<>();
            headers.stream().forEach(e -> {
                userIds.add(e.getApplicantId());
            });
            // 查询员工
            Map<Long, ContactCO> usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(userIds));
            headers
                    .stream()
                    .forEach(e -> {
                        if (usersMap.containsKey(e.getApplicantId())) {
                            e.setFullName(usersMap.get(e.getApplicantId()).getFullName());
                        }
                    });
        }
    }
}
