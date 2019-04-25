package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToCompany;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.PaymentRequisitionTypesToCompanyMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionTypesCompanyDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: bin.xie
 * @Description:付款申请单分配机构
 * @Date: Created in 11:43 2018/1/22
 * @Modified by
 */
@Service
@AllArgsConstructor
@Transactional
public class PaymentRequisitionTypesToCompanyService extends BaseService<
        PaymentRequisitionTypesToCompanyMapper, PaymentRequisitionTypesToCompany> {
    private final PaymentOrganizationService organizationService;

    /**
     * @Author: bin.xie
     * @Description: 根据付款申请单分页查询分配的机构
     * @param: acpReqTypesId
     * @param: page
     * @return: java.util.List<com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToCompany>
     * @Date: Created in 2018/1/22 11:49
     * @Modified by
     */
    @Transactional(readOnly = true)
    public List<PaymentRequisitionTypesToCompany> queryCompanyByTypeId(Long acpReqTypesId, Page<PaymentRequisitionTypesToCompany> page) {
        List<PaymentRequisitionTypesToCompany> lsit = baseMapper.selectPage(page, new EntityWrapper<PaymentRequisitionTypesToCompany>()
                .eq("acp_req_types_id", acpReqTypesId));
        return lsit;
    }


    /**
     * @Author: bin.xie
     * @Description: 根据ID删除已分配的机构
     * @param: id
     * @return: void
     * @Date: Created in 2018/1/22 11:56
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAcpRequstTypesToCompany(Long id) {
        this.deleteById(id);
    }

    /**
     * @Author: bin.xie
     * @Description: 更改分配机构的启用状态
     * @param: id
     * @param: setOfBooksId
     * @param: isEnabled
     * @return: com.hand.hcf.app.payment.CompanyDTO
     * @Date: Created in 2018/1/25 11:11
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCompanyEnabledById(PaymentRequisitionTypesToCompany paymentRequisitionTypesToCompany) {
        PaymentRequisitionTypesToCompany u = baseMapper.selectById(paymentRequisitionTypesToCompany.getId());
        if (u == null){
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        u.setEnabled(paymentRequisitionTypesToCompany.getEnabled());
        u.setVersionNumber(paymentRequisitionTypesToCompany.getVersionNumber());
        this.updateById(u);
        return true;
    }

    /**
     * @Author: bin.xie
     * @Description: 查询该单据已分配的机构
     * @param: contractTypeId
     * @param: page
     * @param: setOfBooksId
     * @return: java.util.List<CompanyDTO>
     * @Date: Created in 2018/1/23 16:06
     * @Modified by
     */
    @Transactional(readOnly = true)
    public List<PaymentRequisitionTypesToCompany> getAcpReqTypeAssignCompanys(Long acpReqTypeId, Page page, Long setOfBooksId) {
        List<PaymentRequisitionTypesToCompany> result = new ArrayList<>();

        Page<PaymentRequisitionTypesToCompany> list = this.selectPage(page,
                    new EntityWrapper<PaymentRequisitionTypesToCompany>()
                .eq(acpReqTypeId != null , "acp_req_types_id",acpReqTypeId)
                );

        if (list.getRecords().size() > 0) {
            List<Long> companyIds = list.getRecords().stream().map(PaymentRequisitionTypesToCompany::getCompanyId).collect(Collectors.toList());
            List<CompanyCO> companyListByIds = organizationService.listByIds(companyIds);

            companyListByIds.forEach(companySumDTO -> {
                for (PaymentRequisitionTypesToCompany paymentRequisitionTypesToCompany : list.getRecords()){
                    if (companySumDTO.getId().equals(paymentRequisitionTypesToCompany.getCompanyId())){
                        paymentRequisitionTypesToCompany.setCompanyCode(companySumDTO.getCompanyCode());
                        paymentRequisitionTypesToCompany.setCompanyName(companySumDTO.getName());
                        paymentRequisitionTypesToCompany.setCompanyTypeName(companySumDTO.getCompanyTypeName());
                    }
                }
            });
            list.getRecords().sort(Comparator.comparing(PaymentRequisitionTypesToCompany::getCompanyCode));
            result = list.getRecords();
        }
        return result;
    }

    /**
     * @Author: bin.xie
     * @Description: 查询当前项目下未被添加的公司
     * @param: setOfBooksId 账套ID
     * @param: acpReqTypesId 借款申请单类型ID
     * @param: companyCode 机构代码
     * @param: companyName 机构名称
     * @param: companyCodeFrom 机构代码从
     * @param: companyCodeTo 机构代码至
     * @param: page 分页
     * @return: java.util.List<com.hand.hcf.app.payment.OrganizationStandardDto>
     * @Date: Created in 2018/1/23 14:03
     * @Modified by
     */
    public List<CompanyCO> getCompanyByConditionFilter(Long setOfBooksId, Long acpReqTypesId, String companyCode,
                                                       String companyName, String companyCodeFrom, String companyCodeTo,
                                                       Page page) {
        List<Long> idList = this.selectList(new EntityWrapper<PaymentRequisitionTypesToCompany>().eq(acpReqTypesId != null, "acp_req_types_id", acpReqTypesId)
        ).stream().map(PaymentRequisitionTypesToCompany::getCompanyId).collect(Collectors.toList());

        Page<CompanyCO> result = organizationService.pageBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, companyCode,
                companyCodeFrom,companyCodeTo,companyName, page,idList);

        return result.getRecords();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean saveAcpReqTypesToCompany(PaymentRequisitionTypesCompanyDTO paymentRequisitionTypesCompanyDTO) {

        Long acpReqTypesId = paymentRequisitionTypesCompanyDTO.getAcpReqTypesId();
        List<Long> companyIds = paymentRequisitionTypesCompanyDTO.getCompanyIds();
        List<PaymentRequisitionTypesToCompany> companies = companyIds.stream().map(
                companyId -> {
                    PaymentRequisitionTypesToCompany build = PaymentRequisitionTypesToCompany
                            .builder()
                            .acpReqTypesId(acpReqTypesId)
                            .companyId(companyId)
                            .build();
                    return build;
                }
        ).collect(Collectors.toList());
        this.insertBatch(companies);
        return true;
    }

}
