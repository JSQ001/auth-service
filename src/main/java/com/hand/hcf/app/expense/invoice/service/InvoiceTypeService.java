package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.InvoiceType;
import com.hand.hcf.app.expense.invoice.dto.InvoiceTypeDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceTypeMouldDTO;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceTypeMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/16 16:34
 * @version: 1.0.0
 */
@Service
public class InvoiceTypeService extends BaseService<InvoiceTypeMapper, InvoiceType> {


    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private InvoiceTypeMouldService invoiceTypeMouldService;

    /**
     * 新增 发票类型定义
     * @param invoiceType
     * @return
     */
    @Transactional
    public InvoiceType insertInvoiceType(InvoiceType invoiceType) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        //发票类型代码 租户下校验唯一性
      if(baseMapper.selectCount(
               new EntityWrapper<InvoiceType>()
                       .eq("tenant_id", tenantId)
                       .eq("invoice_type_code",invoiceType.getInvoiceTypeCode()))
                != 0){
          throw new BizException(RespCode.INVOICE_TYPE_CODE_REPEAT);
      };
      invoiceType.setTenantId(tenantId);
      invoiceType.setInvoiceCodeLength(uniqueAndCheckArray(invoiceType.getInvoiceCodeLength(),","));
      invoiceType.setInvoiceNumberLength(uniqueAndCheckArray(invoiceType.getInvoiceNumberLength(),","));
      baseMapper.insert(invoiceType);
      return invoiceType;
    }

    /**
     * String 字符串去重与校验
     * @param str 字符串
     * @param separator ，.
     * @return
     */
    public static String uniqueAndCheckArray(String str,String separator){
        if(StringUtils.isEmpty(str)) {
            return null;
        }
        String[] strings = str.split(separator);
        Pattern pattern = Pattern.compile("^[1-9]\\d*");
        Arrays.asList(strings).stream().forEach(s-> {
            if(!pattern.matcher(s).matches()){
                throw new BizException(RespCode.INVOICR_TYPE_NUMBER_IS_INTEGER);
            }
        });
        return Arrays.asList(strings).stream().distinct().sorted().collect(Collectors.joining(separator));
    }

    /**
     * 修改发票类型定义
     * @param invoiceType
     * @return
     */
    @Transactional
    public InvoiceType updateInvoiceType(InvoiceType invoiceType) {
        InvoiceType oldInvoiceType = baseMapper.selectById(invoiceType.getId());
        String invoiceNumberLength = invoiceType.getInvoiceNumberLength();
        String invoiceCodeLength = invoiceType.getInvoiceCodeLength();
        if(oldInvoiceType == null){
            throw new BizException(RespCode.INVOICE_TYPE_NOT_EXIST);
        }
        invoiceType.setInvoiceNumberLength(uniqueAndCheckArray(invoiceNumberLength,","));
        invoiceType.setInvoiceCodeLength(uniqueAndCheckArray(invoiceCodeLength,","));
        baseMapper.updateById(invoiceType);
        return invoiceType;
    }

    /**
     * 分页获取发票类型定义
     * @param invoiceTypeCode 发票类型代码
     * @param invoiceTypeName 发票类型名称
     * @param deductionFlag 抵扣标志
     * @param enabled 启用/禁用
     * @param setOfBooksId 账套Id
     * @param interfaceMapping 接口映射值
     * @param queryPage
     * @return
     */
    public List<InvoiceType> pageInvoiceTypeByCond(String invoiceTypeCode,
                                                   String invoiceTypeName,
                                                   String deductionFlag,
                                                   Boolean enabled,
                                                   Long setOfBooksId,
                                                   String interfaceMapping,
                                                   Page queryPage) {
        List<InvoiceType> result =  baseMapper.selectPage(queryPage,
                new EntityWrapper<InvoiceType>()
                        .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                        .like(StringUtils.hasText(invoiceTypeCode),"invoice_type_code",invoiceTypeCode)
                        .like(StringUtils.hasText(invoiceTypeName),"invoice_type_name",invoiceTypeName)
                        .eq(StringUtils.hasText(deductionFlag),"deduction_flag",deductionFlag)
                        .eq(setOfBooksId != null,"set_of_books_id",setOfBooksId)
                        .eq(enabled != null,"enabled",enabled)
                        .like(StringUtils.hasText(interfaceMapping),"interface_mapping",interfaceMapping)
                        .orderBy("enabled",false)
                        .orderBy("invoice_type_code")
        );
        result.stream().forEach(invoiceType -> {
            if(invoiceType.getSetOfBooksId() != null){
                SetOfBooksInfoCO setOfBooksInfoDTO = organizationService.getSetOfBooksInfoCOById(invoiceType.getSetOfBooksId(), true);
                if(setOfBooksInfoDTO != null){
                    invoiceType.setSetOfBooksCode(setOfBooksInfoDTO.getSetOfBooksCode());
                    invoiceType.setSetOfBooksName(setOfBooksInfoDTO.getSetOfBooksName());
                }
            }
        });
        return  baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(result, InvoiceType.class);
    }

    /**
     * 查询当前租户下所有启用的，以及账套下所有启用的发票类型
     * @return
     */
    public List<InvoiceTypeDTO> listInvoiceTypeBySobAndTenant() {
        //查询当前租户下所有启用的，以及账套下所有启用
       List<InvoiceType> invoiceTypes =  baseMapper.selectList(
               new EntityWrapper<InvoiceType>()
                       .eq("enabled",true)
                       .andNew()
                       .eq("set_of_books_id", OrgInformationUtil.getCurrentSetOfBookId())
                       .or()
                       .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                       .isNull("set_of_books_id")
       );
        List<InvoiceTypeDTO> invoiceTypeDTOS = mapperFacade.mapAsList(invoiceTypes, InvoiceTypeDTO.class);
        //查询当前模板
        invoiceTypeDTOS.stream().forEach(invoiceTypeDTO -> {
            InvoiceTypeMouldDTO invoiceTypeMouldDTO = invoiceTypeMouldService.getInvoiceTypeMouldByTypeId(invoiceTypeDTO.getId());
            if(invoiceTypeMouldDTO != null){
                invoiceTypeDTO.setInvoiceTypeMouldHeadColumn(invoiceTypeMouldDTO.getInvoiceTypeMouldHeadColumn());
                invoiceTypeDTO.setInvoiceTypeMouldLineColumn(invoiceTypeMouldDTO.getInvoiceTypeMouldLineColumn());
            }
        });
        return invoiceTypeDTOS;
    }

    public List<InvoiceType> queryInvoiceTypeForInvoice(Long tenantId, Long setOfBooksId){
        List<InvoiceType> invoiceTypes = this.selectList(
                new EntityWrapper<InvoiceType>()
                        .eq("deleted",false)
                        .eq(setOfBooksId != null, "set_of_books_id", setOfBooksId)
                        .or()
                        .eq("deleted",false)
                        .eq(tenantId != null, "tenant_id", tenantId)
                        .isNull("set_of_books_id")
        );
        return invoiceTypes;
    }

    /**
     * 初始化租户
     * @param tenantId
     * @return
     */
    public List<InvoiceType> initInvoiceTypeByTenant(Long tenantId ){
        //获取模板数据
        List<InvoiceType> invoiceTypes = this.selectList(
                new EntityWrapper<InvoiceType>()
                        .eq("deleted",false)
                        .eq("tenant_id",0));
        invoiceTypes.stream().forEach(invoiceType -> {
            invoiceType.setId(null);
            invoiceType.setTenantId(tenantId);
        });
        //插入到数据库中
        this.insertBatch(invoiceTypes);
        return invoiceTypes;

    }

    /**
     * 条件查询账套下发票类型
     * @param selectId
     * @param code
     * @param name
     * @param securityType
     * @param filterId
     * @param queryPage
     * @return
     */
    public Page<BasicCO> pageInvoiceTypeByInfoResultBasic(Long selectId, String code, String name, String securityType, Long filterId, Page queryPage){
        List<BasicCO> basicCOS  = new ArrayList<>();
        if(selectId != null){
            InvoiceType invoiceType = this.selectById(selectId);
            if(invoiceType == null){
                return queryPage;
            }else{
                BasicCO basicCO = BasicCO
                        .builder()
                        .id(invoiceType.getId())
                        .name(invoiceType.getInvoiceTypeName())
                        .code(invoiceType.getInvoiceTypeCode())
                        .build();
                basicCOS.add(basicCO);
            }
        }else{
            List<InvoiceType> invoiceTypes = baseMapper.selectPage(queryPage, new EntityWrapper<InvoiceType>()
                    .eq("set_of_books_id", filterId)
                    .like(StringUtils.hasText(code), "invoice_type_code", code)
                    .like(StringUtils.hasText(name), "invoice_type_name", name));
            invoiceTypes.forEach(invoiceType -> {
                BasicCO basicCO = BasicCO
                        .builder()
                        .id(invoiceType.getId())
                        .name(invoiceType.getInvoiceTypeName())
                        .code(invoiceType.getInvoiceTypeCode())
                        .build();
                basicCOS.add(basicCO);
            });
        }
        queryPage.setRecords(basicCOS);
        return queryPage;
    }
}
