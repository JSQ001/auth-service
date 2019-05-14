package com.hand.hcf.app.payment.externalApi;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.implement.web.AttachmentControllerImpl;
import com.hand.hcf.app.base.implement.web.CommonControllerImpl;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.implement.web.*;
import com.hand.hcf.app.payment.web.dto.PartnerBankInfo;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @description: 调用组织架构三方接口
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/25
 */
@Service
@AllArgsConstructor
public class PaymentOrganizationService {
    private MapperFacade mapperFacade;

    private CompanyControllerImpl companyClient;
    private ContactControllerImpl userClient;
    private AttachmentControllerImpl attachmentClient;
    private CommonControllerImpl organizationClient;
    private SobControllerImpl sobClient;
    private DepartmentControllerImpl departmentClient;
    private PeriodControllerImpl periodClient;
    private CurrencyControllerImpl currencyRateService;
    private WorkflowControllerImpl workflowClient;

    private ParameterControllerImpl parameterClient;
    private CurrencyControllerImpl currencyClient;

    /**
     * 根据公司id查询公司信息
     *
     * @param id 公司Id
     * @return 公司对象
     */
    public CompanyCO getById(Long id) {
        return companyClient.getById(id);
    }


    /**
     * 根据ID集合查询公司信息
     *
     * @param companyIds 公司Id集合
     * @return 公司对象集合
     */
    public List<CompanyCO> listByIds(List<Long> companyIds) {
        if (CollectionUtils.isEmpty(companyIds)) {
            return new ArrayList<>();
        }
        return companyClient.listByIds(companyIds);
    }

    /**
     * 分页条件查询当前账套下的公司，排除指定的机构
     *
     * @param setOfBooksId     账套Id
     * @param companyCode      公司代码 条件查询
     * @param companyCodeFrom  公司代码从 条件查询
     * @param companyCodeTo    公司代码至 条件查询
     * @param companyName      公司名称
     * @param page             分页对象
     * @param ignoreCompanyIds 需要排除的公司Id集合 条件查询
     * @return 公司分页对象
     */
    public Page<CompanyCO> pageBySetOfBooksIdConditionByIgnoreIds(Long setOfBooksId,
                                                                  String companyCode,
                                                                  String companyCodeFrom,
                                                                  String companyCodeTo,
                                                                  String companyName,
                                                                  Page page,
                                                                  List<Long> ignoreCompanyIds) {
        //bo.liu 修改三方接口
        /*return companyClient.pageBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName, true, page, ignoreCompanyIds);*/
        return this.companyClient.pageBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName, true,page.getCurrent() - 1, page.getSize(), (List)(ignoreCompanyIds == null ? new ArrayList() : ignoreCompanyIds));
    }

    public List<CompanyCO> listChildrenCompaniesByCondition(Long companyId, Boolean ignoreOwn, String companyCode, String companyCodeFrom, String companyCodeTo, String companyName, String keyWord) {
        return  companyClient.listChildrenCompaniesByCondition(companyId, ignoreOwn, companyCode, companyCodeFrom, companyCodeTo, companyName, keyWord, Arrays.asList());
    }

    /**
     * 根据公司代码、公司名称不分页查询账套下的公司
     * @param setOfBooksId
     * @param companyCode
     * @param companyName
     * @return
     */
    public List<CompanyCO> listCompanyBySetOfBooksIdAndCodeAndName(Long setOfBooksId,String companyCode,String companyName){
        return companyClient.listCompanyBySetOfBooksIdAndCodeAndName(setOfBooksId, companyCode, companyName);
    }

/*
员工
 */

    /**
     * 根据员工id集合查询员工信息
     *
     * @param ids 员工Id集合
     * @return
     */
    public List<ContactCO> listByUserIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return userClient.listByUserIdsConditionByKeyWord(ids, null);
    }

    /**
     * 判断该用户是否属于所传的人员组
     *
     * @return
     * @param: judgeUserCO
     */
    public Boolean judgeUserInUserGroups(JudgeUserCO judgeUserCO) {
        return userClient.judgeUserInUserGroups(judgeUserCO);
    }

    /**
     * 根据人员组id集合查询人员组信息及其固定分配的人员id
     *
     * @param ids
     * @return
     */
    public List<UserGroupCO> listUserGroupAndUserIdByGroupIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return userClient.listUserGroupAndUserIdByGroupIds(ids);
    }

    /*查询单个员工*/
    public ContactCO getByUserOid(String userOid) {
        //bo.liu 修改三方接口
        /*return userClient.getByUserOid(userOid);*/
        return this.userClient.getByUserOid(UUID.fromString(userOid));
    }

    /*查询单个员工*/
    public ContactCO getByUserCode(String UserCode) {
        return userClient.getByUserCode(UserCode);
    }

    /**
     * 根据员工ID和银行账号获取银行账户信息
     *
     * @param userId
     * @param bankAccountNumber
     * @return
     */
    public PartnerBankInfo getEmployeeCompanyBankByCode(Long userId, String bankAccountNumber) {
        UserBankAccountCO userBankAccountCO = userClient.getUserBankAccountByUserIdAndAccountNumber(userId, bankAccountNumber);

        PartnerBankInfo partnerBankInfo = new PartnerBankInfo();
        mapperFacade.map(userBankAccountCO, partnerBankInfo);
        return partnerBankInfo;
    }



/*
附件
 */

    /**
     * 根据附件OID集合查询附件信息
     *
     * @param oidList 附件oid集合
     * @return 附件对象集合
     */
    public List<AttachmentCO> listByOids(List<String> oidList) {
        if (CollectionUtils.isEmpty(oidList)) {
            return new ArrayList<>();
        }
        return attachmentClient.listByOids(oidList);
    }

    /**
     * 根据附件OID集合删除附件信息
     *
     * @param oidList 附件oid集合
     */
    public void deleteByOids(List<String> oidList) {
        if (CollectionUtils.isEmpty(oidList)) {
            return;
        }
        attachmentClient.deleteByOids(oidList);
    }



/*
组织架构
 */

    /**
     * @Description: 通过公司Oid查询该公司配置信息
     * @param: companyOid
     */
//    public FunctionProfileCO getFunctionProfileByCompanyOid(UUID companyOid) {
//        return organizationClient.getFunctionProfileByCompanyOid(companyOid);
//    }

    /**
     * 根据系统代码的code,以及值的code获取具体的系统代码
     *
     * @param code
     * @param value
     * @return
     */
    public SysCodeValueCO getSysCodeValueByCodeAndValue(String code, String value) {
        return organizationClient.getSysCodeValueByCodeAndValue(code, value);
    }

    /**
     * 获取编码规则的值
     *
     * @param documentType
     * @param companyId
     * @return
     */
    public String getCoding(String documentType, Long companyId) {
        String companyCode = getById(companyId).getCompanyCode();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(date);
        /*return organizationClient.getOrderNumber(documentType, companyCode, now);*/
        //bo.liu 修改三方接口
        String language = LoginInformationUtil.getCurrentLanguage();
        OrderNumberCO orderNumberCO = (OrderNumberCO)this.organizationClient.getOrderNumber(documentType, companyCode, now);
        if (StringUtils.isEmpty(orderNumberCO.getOrderNumber())) {
            throw new BizException(orderNumberCO.getCode(), (String)orderNumberCO.getMessage().stream().filter((u) -> {
                return u.getLanguage().equalsIgnoreCase(language);
            }).findFirst().map(OrderNumberCO.Message::getContent).get());
        } else {
            return orderNumberCO.getOrderNumber().toString();
        }
    }

    /**
     * 根据代码获取其下所有的系统代码值
     *
     * @param code 值列表code
     * @return 系统值列表集合
     */
    public List<SysCodeValueCO> listAllSysCodeValueByCode(String code) {
        return organizationClient.listAllSysCodeValueByCode(code);
    }



/*
账套
 */

    /**
     * 根据账套id查询账套详情
     *
     * @param sobId
     * @return
     */
    public SetOfBooksInfoCO getSetOfBooksById(Long sobId) {
        return sobClient.getSetOfBooksById(sobId);
    }





    /**
     * 根据员工oid查找部门
     *
     * @param empOid 员工oid
     * @return
     */
    public DepartmentCO getDepartmentByEmpOid(String empOid) {
        return departmentClient.getDepartmentByEmpOid(empOid);
    }

    /**
     * 根据部门id集合查询详情
     *
     * @param departmentIds 部门id集合
     * @return
     */
    public List<DepartmentCO> listPathByIds(List<Long> departmentIds) {
        return departmentClient.listPathByIds(departmentIds);
    }

    /**
     * 根据部门id查询详情
     *
     * @param departmentId 部门id
     * @return
     */
    public DepartmentCO getDepartmentById(Long departmentId) {
        return departmentClient.getDepartmentById(departmentId);
    }


    /**
     * 通过账套、期间名称获取期间详细信息
     *
     * @param setOfBooksId 账套ID
     * @param periodName   期间代码
     * @return
     */
    public PeriodCO getPeriodBySetOfBooksIdAndName(Long setOfBooksId, String periodName) {
        return periodClient.getPeriodBySetOfBooksIdAndName(setOfBooksId, periodName);
    }

    /**
     * 通过账套、时间获取期间信息
     *
     * @param setOfBooksId 账套id
     * @param dateTime     时间
     * @return
     */
    public PeriodCO getPeriodBysetOfBooksIdAndDateTime(Long setOfBooksId, ZonedDateTime dateTime) {
        return periodClient.getPeriodBysetOfBooksIdAndDateTime(setOfBooksId, TypeConversionUtils.timeToString(dateTime));
    }

    /*本位币外币获取唯一币种*/
    public CurrencyRateCO getForeignCurrencyByCode(String code, String otherCode, Long setOfBookId) {
        return currencyRateService.getForeignCurrencyByCode(code, otherCode, setOfBookId);
    }


/*
工作流
 */
    /**
     * 创建支付日志
     * @param dto
     * @return
     */
    public List<ApprovalHistoryCO> createLogsByPaymentService(List<CommonApprovalHistoryCO> dto){
        return workflowClient.saveBatchHistory(dto);
    }

    /**
     * 删除支付日志
     * @param dto
     * @return
     */
    public  Boolean deletLogsByPaymentService(List<ApprovalHistoryCO> dto){
        return workflowClient.deleteBatchLogs(dto);
    }

    /**
     * 根据用户id查询其组织架构Id信息
     * @param userId
     * @return
     */
    public OrganizationUserCO getOrganizationCOByUserId(Long userId) {
        return userClient.getOrganizationCOByUserId(userId);
    }

    /**
     * 根据部门ID查询用户信息
     * @param departmentId
     * @return
     */
    public List<ContactCO> listUsersByDepartmentId(Long departmentId){
        return userClient.listUsersByDepartmentId(departmentId);
    }

    /**
     * 查询当前租户下的所有员工
     * @param tenantId
     * @return
     */
    public List<ContactCO> listUserByTenantId(Long tenantId){
        return userClient.listUserByTenantId(tenantId);
    }

    /**
     * 根据人员组ID查询用户信息
     * @param userGroupId
     * @return
     */
    public List<ContactCO> listUsersByUserGroupId(Long userGroupId){
        return userClient.listByUserGroupId(userGroupId);
    }

    public ContactCO getUserById(Long employeeId) {
        return userClient.getById(employeeId);
    }

    /**
     * 根据参数代码获取参数配置信息
     * @param parameterCode
     * @param setOfBooksId
     * @param companyId
     * @return
     */
    public String getParameterValueByParameterCode(String parameterCode,Long setOfBooksId,Long companyId){
        //注释： 该接口需要重构
        //bo.liu 修改三方接口
        /*return parameterClient.getParameterValueByParameterCode(parameterCode,setOfBooksId,companyId);*/
        return this.parameterClient.getParameterValueByParameterCode(parameterCode, LoginInformationUtil.getCurrentTenantId(), setOfBooksId, companyId);
    }

    /**
     * 根据公司id集合和名称查询公司
     * @param companyName
     * @param ids
     * @return
     */
    public List<CompanyCO> listCompanyByCond(String companyCode,
                                             String companyCodeFrom,
                                             String companyCodeTo,
                                             String companyName,
                                             String keyWord,
                                             Boolean enabled,
                                             List<Long> ids){
        return companyClient.listCompanyByCond(companyCode, companyCodeFrom, companyCodeTo, companyName, keyWord, enabled, ids);
    }

    /**
     * 根据账套获取公司数据
     * @param setOfBooksId
     * @return
     */
    public List<CompanyCO> listAllBySetOfBooksId(Long setOfBooksId) {
        //bo.liu 修改三方接口
        /*return companyClient.listAllBySetOfBooksId(setOfBooksId);*/
        return this.companyClient.listBySetOfBooksIdConditionByEnabled(setOfBooksId, (Boolean)null);
    }
    /**
     * 根据公司code查询公司信息
     * @param companyCode
     * @return
     */
    public CompanyCO getByCompanyCode(String companyCode) {
        return companyClient.getByCompanyCode(companyCode);
    }
    /**
     * 查询币种信息
     * @param code
     * @param enabled
     * @param setOfBooksId
     * @return
     */
    public List<CurrencyRateCO> listCurrencysByCode(String code,Boolean enabled,Long setOfBooksId) {
        return currencyClient.listCurrencysByCode(code,enabled,setOfBooksId);
    }
    /**
     * 查询账套信息
     * @param setOfBooksCode
     * @return
     */
    public List<SetOfBooksInfoCO> getSetOfBooksBySetOfBooksCode(String setOfBooksCode) {
        return sobClient.getSetOfBooksBySetOfBooksCode(setOfBooksCode);
    }
}
