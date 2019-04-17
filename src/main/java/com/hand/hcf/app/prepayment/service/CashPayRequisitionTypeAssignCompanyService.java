package com.hand.hcf.app.prepayment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignCompany;
import com.hand.hcf.app.prepayment.externalApi.PrepaymentHcfOrganizationInterface;
import com.hand.hcf.app.prepayment.persistence.CashPayRequisitionTypeAssignCompanyMapper;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 韩雪 on 2017/10/25.
 */
@Service
@Transactional
public class CashPayRequisitionTypeAssignCompanyService extends BaseService<CashPayRequisitionTypeAssignCompanyMapper,CashPayRequisitionTypeAssignCompany> {
    @Autowired
    private CashPayRequisitionTypeService cashPayRequisitionTypeService;
    @Autowired
    private PrepaymentHcfOrganizationInterface prepaymentHcfOrganizationInterface;


    /**
     * 批量新增 预付款单类型关联的公司表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignCompany> createCashPayRequisitionTypeAssignCompanyBatch(List<CashPayRequisitionTypeAssignCompany> list){
        list.stream().forEach(cashPayRequisitionTypeAssignCompany -> {
            if (cashPayRequisitionTypeAssignCompany.getId() != null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_COMPANY_ALREADY_EXISTS);
            }

            //设置条件
            if (baseMapper.selectList(
                    new EntityWrapper<CashPayRequisitionTypeAssignCompany>()
                            .eq("sob_pay_req_type_id",cashPayRequisitionTypeAssignCompany.getSobPayReqTypeId())
                            .eq("company_id",cashPayRequisitionTypeAssignCompany.getCompanyId())
            ).size() > 0){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_COMPANY_NOT_ALLOWED_TO_REPEAT);
            }

            this.insert(cashPayRequisitionTypeAssignCompany);
        });
        return list;
    }

    /**
     * 批量修改 预付款单类型关联的公司表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashPayRequisitionTypeAssignCompany> updateCashPayRequisitionTypeAssignCompanyBatch(List<CashPayRequisitionTypeAssignCompany> list){
        list.stream().forEach(cashPayRequisitionTypeAssignCompany -> {
            if (cashPayRequisitionTypeAssignCompany.getId() == null){
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITIONTYPE_ASSIGN_COMPANY_NOT_EXIST);
            }

            this.updateById(cashPayRequisitionTypeAssignCompany);
        });
        return list;
    }

    /**
     * 根据预付款单类型ID->sobPayReqTypeId 查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     *
     * @param sobPayReqTypeId
     * @param page
     * @return
     */
    public List<CashPayRequisitionTypeAssignCompany> getCashPayRequisitionTypeAssignCompanyByCond(Long sobPayReqTypeId, Page page){
        List<CashPayRequisitionTypeAssignCompany> list = baseMapper.selectPage(page,
                new EntityWrapper<CashPayRequisitionTypeAssignCompany>()
                        .where("1 = 1")
                        .eq("sob_pay_req_type_id",sobPayReqTypeId)
                        .orderBy("company_code")
        );
        list.stream().forEach(cashPayRequisitionTypeAssignCompany -> {
            CompanyCO company = prepaymentHcfOrganizationInterface.getCompanyById(cashPayRequisitionTypeAssignCompany.getCompanyId());
            if (company != null){
                cashPayRequisitionTypeAssignCompany.setCompanyName(company.getName());
                cashPayRequisitionTypeAssignCompany.setCompanyType(company.getCompanyTypeName());
            }
        });
        return list;
    }

    /**
     * 分配页面的公司筛选查询
     *
     * @param sobPayReqTypeId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public Page<CompanyCO> assignCompanyQuery(Long sobPayReqTypeId, String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, Page page) {
        List<Long> collect = baseMapper.selectList(new EntityWrapper<CashPayRequisitionTypeAssignCompany>()
                .eq("sob_pay_req_type_id", sobPayReqTypeId)
        ).stream().map(CashPayRequisitionTypeAssignCompany::getCompanyId).collect(Collectors.toList());
        CashPayRequisitionType cashPayRequisitionType = cashPayRequisitionTypeService.selectById(sobPayReqTypeId);

        if (cashPayRequisitionType != null){
            Page<CompanyCO> companyByCond = prepaymentHcfOrganizationInterface.pageBySetOfBooksIdConditionByIgnoreIds(cashPayRequisitionType.getSetOfBookId(), companyCode, companyName,
                    companyCodeFrom, companyCodeTo, collect, page);
            return companyByCond;
        }
        return page;
    }
}
