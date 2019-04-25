package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.PaymentCompanyConfig;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.PaymentCompanyConfigMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.adapter.PaymentCompanyConfigAdapter;
import com.hand.hcf.app.payment.web.dto.PaymentCompanyConfigDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@Service
public class PaymentCompanyConfigService extends ServiceImpl<PaymentCompanyConfigMapper, PaymentCompanyConfig> {

    @Autowired
    private PaymentCompanyConfigMapper paymentCompanyConfigMapper;

    @Autowired
    private PaymentCompanyConfigAdapter paymentCompanyConfigAdapter;

    @Autowired
    private PaymentOrganizationService organizationService;

    public PaymentCompanyConfigDTO insertOrUpdatePaymentCompanyConfig(PaymentCompanyConfig paymentCompanyConfig) {
        /*校验优先级不能重复*/
        if (paymentCompanyConfig.getId() == null) {//新增
            checkPriorty(paymentCompanyConfig);
            paymentCompanyConfig.setCreatedDate(ZonedDateTime.now());
            paymentCompanyConfig.setLastUpdatedDate(ZonedDateTime.now());
            paymentCompanyConfig.setDeleted(false);
            paymentCompanyConfig.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            paymentCompanyConfig.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            paymentCompanyConfig.setVersionNumber(1);
            paymentCompanyConfigMapper.insert(paymentCompanyConfig);
            return paymentCompanyConfigAdapter.toDTO(paymentCompanyConfigMapper.selectById(paymentCompanyConfig.getId()));
        } else {
            //修改
//            paymentCompanyConfig.setLastUpdatedDate(ZonedDateTime.now());
//            paymentCompanyConfig.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            PaymentCompanyConfig oldConfig = paymentCompanyConfigMapper.selectById(paymentCompanyConfig.getId());
            //校验版本号
            if (paymentCompanyConfigMapper.selectById(paymentCompanyConfig.getId()).getVersionNumber() != paymentCompanyConfig.getVersionNumber()) {
                throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
            }
//            else {
//                paymentCompanyConfig.setVersionNumber(paymentCompanyConfig.getVersionNumber() + 1);
//            }
            if (!paymentCompanyConfig.getPriorty().equals(oldConfig.getPriorty())) {
                throw new BizException(RespCode.PAYMENT_PRIORITIES_ARE_NOT_MODIFIABLE);
            }
//            paymentCompanyConfig.setCreatedBy(oldConfig.getCreatedBy());
//            paymentCompanyConfig.setCreatedDate(oldConfig.getCreatedDate());
//            paymentCompanyConfig.setEnabled(oldConfig.getEnabled());
//            paymentCompanyConfig.setDeleted(oldConfig.getDeleted());
//            paymentCompanyConfigMapper.updateAllColumnById(paymentCompanyConfig);
            paymentCompanyConfigMapper.updateById(paymentCompanyConfig);
            return paymentCompanyConfigAdapter.toDTO(paymentCompanyConfigMapper.selectById(paymentCompanyConfig.getId()));
        }
    }

    private void checkPriorty(PaymentCompanyConfig paymentCompanyConfig) {
        List<PaymentCompanyConfig> list = paymentCompanyConfigMapper.selectList(
                new EntityWrapper<PaymentCompanyConfig>()
                        .eq("priorty", paymentCompanyConfig.getPriorty())
                        .eq("set_of_books_id", paymentCompanyConfig.getSetOfBooksId())
                        .eq("enabled", true)
                        .eq("deleted", false)
        );
        if (CollectionUtils.isNotEmpty(list)) {
            throw new BizException(RespCode.PAYMENT_PRIORITY_ALREADY_EXISTS);
        }
        return;
    }


    public Boolean deleteById(Long id) {
        PaymentCompanyConfig paymentCompanyConfig = paymentCompanyConfigMapper.selectById(id);
        if (paymentCompanyConfig == null) {
            return true;
        }
        paymentCompanyConfig.setDeleted(true);
        int i = paymentCompanyConfigMapper.updateById(paymentCompanyConfig);
        return i != 0 ? true : false;
    }


    //付款公司配置，根据单据公司代码，单据公司名称，单据类型查询
    public Page<PaymentCompanyConfigDTO> selectByInput(
            String companyCode,//单据公司代码
            String companyName,//单据公司名称
            String ducumentCategory,//单据类别
            Long setOfBooksId,//账套id
            Page page,
            boolean dataAuthFlag
    ) {
        List<CompanyCO> companyCOS = organizationService.listCompanyBySetOfBooksIdAndCodeAndName(setOfBooksId, companyCode, companyName);
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(companyCOS)) {
            for (CompanyCO companyCO : companyCOS) {
                ids.add(companyCO.getId());
            }
        }else{
            return new Page<>();
        }

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME,"csh_payment_company_config");
            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_books_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }

        List<PaymentCompanyConfig> list = paymentCompanyConfigMapper.selectList(
                new EntityWrapper<PaymentCompanyConfig>()
                        .eq(setOfBooksId != null,"set_of_books_id",setOfBooksId)
                        .eq("deleted",false)
                        .eq(StringUtils.isNotEmpty(ducumentCategory),"ducument_category",ducumentCategory)
                        .and(!StringUtils.isEmpty(dataAuthLabel), dataAuthLabel)
                        .in("company_id",ids)
                        .orderBy("priorty")
        );

        List<PaymentCompanyConfigDTO> listDTO = new ArrayList<>();
        for (PaymentCompanyConfig paymentCompanyConfig : list) {
            listDTO.add(paymentCompanyConfigAdapter.toDTO(paymentCompanyConfig));
        }
        if (CollectionUtils.isNotEmpty(listDTO)) {
            page.setRecords(listDTO);
        }
        return page;
    }


    //支付模块使用--条件查询付款公司配置的公司
    public CompanyCO getCompanyByCompanyIdAndDocumentCategoryAndDocumentTypeId(Long companyId, String ducumentCategory, Long ducumentTypeId) {
        if (companyId == null) {
            throw new BizException(RespCode.SYS_COMPANY_INFO_NOT_EXISTS);
        }
        EntityWrapper<PaymentCompanyConfig> wrapper = new EntityWrapper<>();
        wrapper.eq("company_id", companyId)
                .eq(ducumentCategory != null, "ducument_category", ducumentCategory)
                .eq(ducumentTypeId != null, "ducument_type_id", ducumentTypeId)
                .orderBy("priorty");
        List<PaymentCompanyConfig> list = paymentCompanyConfigMapper.selectList(wrapper);
        if (list.size() == 0) {
            return null;
        }
        return organizationService.getById(list.get(0).getPaymentCompanyId());

    }
}
