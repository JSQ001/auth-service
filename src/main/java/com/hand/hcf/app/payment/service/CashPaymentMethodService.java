package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashPaymentMethod;
import com.hand.hcf.app.payment.domain.CompanyBankPayment;
import com.hand.hcf.app.payment.domain.PaymentSystemCustomEnumerationType;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.CashPaymentMethodMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.utils.SpecificationUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;


/**
 * Created by 刘亮 on 2017/9/6.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CashPaymentMethodService extends BaseService<CashPaymentMethodMapper, CashPaymentMethod> {

    private final PaymentOrganizationService organizationService;
    private final CompanyBankPaymentService companyBankPaymentService;

    public CashPaymentMethodService(PaymentOrganizationService organizationService, CompanyBankPaymentService companyBankPaymentService) {
        this.organizationService = organizationService;
        this.companyBankPaymentService = companyBankPaymentService;
    }

    //单个增加或修改支付方式
    public CashPaymentMethod insertOrUpdateCashPaymentMethod(CashPaymentMethod cashPaymentMethod){
        cashPaymentMethod.setLastUpdatedDate(ZonedDateTime.now());
        cashPaymentMethod.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        if(cashPaymentMethod.getId()==null){//新增
            checkPaymentMethod(cashPaymentMethod);
            cashPaymentMethod.setCreatedDate(ZonedDateTime.now());
            cashPaymentMethod.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            cashPaymentMethod.setTenantId(OrgInformationUtil.getCurrentTenantId());
            cashPaymentMethod.setDeleted(false);
            baseMapper.insert(cashPaymentMethod);
            return baseMapper.selectById(cashPaymentMethod.getId());
        }else {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id",cashPaymentMethod.getId().toString());
            this.update(cashPaymentMethod,wrapper);
            return baseMapper.selectById(cashPaymentMethod.getId());
        }

    }


    /*新增修改校验逻辑*/
    public void checkPaymentMethod(CashPaymentMethod cashPaymentMethod){

        List<CashPaymentMethod> list = baseMapper.selectList(
                new EntityWrapper<CashPaymentMethod>()
                    .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                    .eq("payment_method_code",cashPaymentMethod.getPaymentMethodCode())
                    .eq("deleted",false)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new BizException(RespCode.PAYMENT_PAYMENT_METHOD_CODE_EXIT);
        }
    }

    //逻辑删除
    public boolean deleteById(Long id){
        CashPaymentMethod cashPaymentMethod = baseMapper.selectById(id);
        if(cashPaymentMethod==null){
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        cashPaymentMethod.setDeleted(true);
        cashPaymentMethod.setPaymentMethodCode(cashPaymentMethod.getPaymentMethodCode()+"_DELETE_"+ RandomStringUtils.randomNumeric(6));
        return this.updateById(cashPaymentMethod);
    }

    //条件查询（付款方式代码，描述）
    public List<CashPaymentMethod> selectByInput(String paymentMethodCode, String description, Page page){

        EntityWrapper<CashPaymentMethod> wrapper = new EntityWrapper<>();
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        wrapper.like(paymentMethodCode != null,"payment_method_code",paymentMethodCode);
        wrapper.like(description!=null,"description",description);
        wrapper.orderBy("enabled",false);
        wrapper.orderBy("payment_method_category",false);
        wrapper.orderBy("payment_method_code");
        List<CashPaymentMethod> list = baseMapper.selectPage(page , wrapper);
        return list;
    }

    //条件查询（付款方式代码，描述）
    public List<CashPaymentMethod> selectByInputLOV(String paymentMethodCode, String description, Page page){

        EntityWrapper<CashPaymentMethod> wrapper = new EntityWrapper<>();
        wrapper.like(paymentMethodCode != null,"payment_method_code",paymentMethodCode);
        wrapper.like(description!=null,"description",description);
        wrapper.orderBy("payment_method_category",false);
        wrapper.eq("enabled",true);
        wrapper.eq("deleted",false);
        wrapper.orderBy("payment_method_code");
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        List<CashPaymentMethod> list = baseMapper.selectPage(page, wrapper);
        return list;
    }

    // 查询所有
    public List<CashPaymentMethod> selectAll(String paymentMethodCode, String description){

        EntityWrapper<CashPaymentMethod> wrapper = new EntityWrapper<>();
        wrapper.like(paymentMethodCode != null,"payment_method_code",paymentMethodCode);
        wrapper.like(description!=null,"description",description);
        wrapper.orderBy("payment_method_category",false);
        wrapper.eq("enabled",true);
        wrapper.eq("deleted",false);
        wrapper.orderBy("payment_method_code");
        wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
        List<CashPaymentMethod> list = baseMapper.selectList(wrapper);
        return list;
    }
    //批量新增或修改付款方式
    public List<CashPaymentMethod> insertOrUpdateCashPaymentMethodBatch(List<CashPaymentMethod> list){

//        for(CashPaymentMethod c:list){
//            returnList.add(insertOrUpdateCashPaymentMethod(c));
//        }
        list.forEach(cashPaymentMethod -> {
            insertOrUpdateCashPaymentMethod(cashPaymentMethod);
        });
        return list;
    }

    //批量删除付款方式
    public void deleteCashPaymentMethodBatch(List<CashPaymentMethod> list){
        if(list==null||list.size()==0){
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY);
        }

        list.forEach(cashPaymentMethod -> {
            deleteById(cashPaymentMethod.getId());
        });
    }


    //根据付款方式类型，查询付款方式下拉列表
    public List<CashPaymentMethod> selectByPaymentType(String paymentType){

        if(StringUtils.isEmpty(paymentType)){
            throw new BizException(RespCode.PAYMENT_PAYMENT_METHOD_IS_NULL);
        }
        if(paymentType.equals(SpecificationUtil.ONLINE_PAYMENT)
                ||
           paymentType.equals(SpecificationUtil.OFFLINE_PAYMENT)
                ||
            paymentType.equals(SpecificationUtil.EBANK_PAYMENT)){//是系统指定值
            EntityWrapper<CashPaymentMethod> wrapper = new EntityWrapper<>();
            wrapper.eq("deleted",false);
            wrapper.eq("payment_method_category",paymentType);
            wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
            List<CashPaymentMethod> list = baseMapper.selectList(wrapper);
            return list;

        }else {
            throw new BizException(RespCode.PAYMENT_PAYMENT_METHOD_NOT_SYSTEM_VALUE);

        }
    }



    public List<CashPaymentMethod> selectByTypeAndCompanyBankId(String type, Long companyBankId, Long companyPaymentId, Long paymentMethodId){
        if(StringUtils.isEmpty(type)){
            throw new BizException(RespCode.PAYMENT_PAYMENT_METHOD_IS_NULL);
        }
        if(type.equals(SpecificationUtil.ONLINE_PAYMENT)
                ||
                type.equals(SpecificationUtil.OFFLINE_PAYMENT)
                ||
                type.equals(SpecificationUtil.EBANK_PAYMENT)){//是系统指定值
            //根据公司银行账户id获取公司银行账户的付款方式列表
            List<Long> ids = companyBankPaymentService.listPaymentMethodIdsByBankAccountId(companyBankId);
            EntityWrapper<CashPaymentMethod> wrapper = new EntityWrapper<>();
            wrapper.eq("deleted",false);
            wrapper.eq("enabled",true);
            wrapper.notIn(CollectionUtils.isNotEmpty(ids),"id",ids);
            wrapper.eq("payment_method_category",type);
            wrapper.eq("tenant_id", OrgInformationUtil.getCurrentTenantId());
            wrapper.orderBy("payment_method_code");
            List<CashPaymentMethod> list = baseMapper.selectList(wrapper);
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(
                        cashPaymentMethod -> {
                            cashPaymentMethod.setPaymentMethodCategoryName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_PAYMENT_TYPE,
                                    cashPaymentMethod.getPaymentMethodCategory()).getName());
                        }
                );
            }
            if(companyPaymentId!=null){
                CompanyBankPayment companyBankPayment = companyBankPaymentService.selectById(companyPaymentId);
                if(companyBankPayment == null){
                    throw new BizException(RespCode.PAYMENT_COMPANY_BANK_PAYMENT_NOT_EXIT);
                }
                if(paymentMethodId!=null && type.equals(companyBankPayment.getPaymentMethodCategory())){
                    list.add(baseMapper.selectById(paymentMethodId));
                }
            }
            return list;

        }else {
            throw new BizException(RespCode.PAYMENT_PAYMENT_METHOD_NOT_SYSTEM_VALUE);

        }
    }



    /*查看当前租户下的所有付款方式（不分页）---公司银行账户详情下页签使用
    * */

    public List<CashPaymentMethod> selectPaymentMethodByTenantId(Long tenantId){
        EntityWrapper<CashPaymentMethod> wrapper = new EntityWrapper<>();
        wrapper.eq("tenant_id",tenantId);
        wrapper.eq("deleted",false);
        List<CashPaymentMethod> list = baseMapper.selectList(wrapper);
        return  list;
    }

    /*根据付款方式id查询付款方式对象
    * */
    public CashPaymentMethod selectPaymentMethodById(Long id, String paymentMethod){

        if(id==null){
            return null;
        }
        return baseMapper.getById(id,paymentMethod);
    }






}
