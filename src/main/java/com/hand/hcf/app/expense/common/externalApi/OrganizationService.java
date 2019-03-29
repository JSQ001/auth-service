package com.hand.hcf.app.expense.common.externalApi;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.implement.web.AttchmentControllerImpl;
import com.hand.hcf.app.base.implement.web.CommonControllerImpl;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.base.org.SysCodeValueCO;
import com.hand.hcf.app.base.org.OrderNumberCO;
import com.hand.hcf.app.base.org.OrderNumberCO.Message;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.expense.common.utils.ParameterConstant;
import com.hand.hcf.app.mdata.implement.web.*;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 组织架构三方接口
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/24
 */
@Service
public class OrganizationService {

    @Autowired
    private CompanyControllerImpl companyClient;

    @Autowired
    private ContactControllerImpl userClient;

    @Autowired
    private SobControllerImpl sobClient;

    @Autowired
    private DepartmentControllerImpl departmentClient;

    @Autowired
    private CurrencyControllerImpl currencyClient;
    @Autowired
    private AttchmentControllerImpl attachmentClient;
    @Autowired
    private PeriodControllerImpl periodClient;
    @Autowired
    private CommonControllerImpl organizationClient;

    @Autowired
    private DimensionControllerImpl dimensionClient;

    @Autowired
    private WorkflowControllerImpl workflowClient;
    @Autowired
    private ParameterClient parameterClient;
    @Autowired
    private ResponsibilityCenterControllerImpl responsibilityCenterClient;

    @Autowired
    private SupplierImplementControllerImpl supplierClient;

    @Autowired
    private LocationControllerImpl locationInterface;




    public Map<Long, CompanyCO> getCompanyMapByCompanyIds(List<Long> ids){
        List<CompanyCO> companyCOS = companyClient.listByIds(ids);
        if (CollectionUtils.isEmpty(companyCOS)){
            return new HashMap<>(16);
        }
        return companyCOS.stream().collect(Collectors.toMap(CompanyCO::getId, e -> e, (k1,k2) -> k1));
    }

    public Map<Long, ContactCO> getUserMapByUserIds(List<Long> ids){
        List<ContactCO> userCOS = userClient.listByUserIds(ids);
        if (CollectionUtils.isEmpty(userCOS)){
            return new HashMap<>(16);
        }
        return userCOS.stream().collect(Collectors.toMap(ContactCO::getId, e -> e, (k1,k2) -> k1));
    }

    public Map<Long, DepartmentCO> getDepartmentMapByDepartmentIds(List<Long> ids){
        //jiu.zhao 修改三方接口 20190329
        //List<DepartmentCO> departmentCOS = departmentClient.listDepartmentsByIds(ids);
        List<DepartmentCO> departmentCOS = departmentClient.listDepartmentsByIds(ids, (String)null);
        if (CollectionUtils.isEmpty(departmentCOS)) {
            return new HashMap<>(16);
        }
        return departmentCOS.stream().collect(Collectors.toMap(DepartmentCO::getId, e -> e, (k1,k2) -> k1));
    }

    /**
     * 根据账套id获取账套信息，如果不存在则根据参数决定是否创建一个对象
     * @param id
     * @return
     */
    public SetOfBooksInfoCO getSetOfBooksInfoCOById(Long id, boolean isNullNew){
        SetOfBooksInfoCO setOfBooksInfoCO = sobClient.getSetOfBooksById(id);
        if (isNullNew) {
            if (setOfBooksInfoCO == null) {
                SetOfBooksInfoCO result = new SetOfBooksInfoCO();
                result.setId(id);
                return result;
            }
        }
        return setOfBooksInfoCO;
    }

    public DepartmentCO getDepartmentById(Long id){
        return departmentClient.getDepartmentById(id);
    }

    public Map<Long, UserGroupCO> getUserGroupMapByGroupIds(List<Long> ids){
        List<UserGroupCO> userGroupCOS = userClient.listUserGroupByUserGroupIds(ids);
        if (CollectionUtils.isEmpty(userGroupCOS)){
            return new HashMap<>(16);
        }
        return userGroupCOS.stream().collect(Collectors.toMap(UserGroupCO::getId, e -> e, (k1,k2) -> k1));
    }

    /**
     * 根据本币种代码查询币种信息
     *
     * @param code
     * @param otherCode
     * @param setOfBooksId
     * @return
     */
    public CurrencyRateCO getForeignCurrencyByCode(String code, String otherCode, Long setOfBooksId){
        return currencyClient.getForeignCurrencyByCode(code, otherCode, setOfBooksId);
    }

    public List<AttachmentCO> listAttachmentsByOids(List<String> oids){
        return attachmentClient.listByOids(oids);
    }

    /**
     * 获取附件信息
     * @param oid
     * @return
     */
    public AttachmentCO getAttachmentByOid(String oid) {
        return attachmentClient.getByOid(oid);
    }

    public void deleteAttachmentsByOids(List<String> oids){
        attachmentClient.deleteByOids(oids);
    }

    public ContactCO getUserById(Long id){
        return userClient.getById(id);
    }

    public List<CompanyCO> listCompanyBySetOfBooksId(Long setOfBooksId, Boolean enabled){
        List<CompanyCO> companyCOS = companyClient.listBySetOfBooksIdConditionByEnabled(setOfBooksId, enabled);
        return companyCOS;
    }

    public List<DepartmentCO> listDepartmentsByTenantId(){
        Page<DepartmentCO> page = new Page<>(1,100000);
        //jiu.zhao 修改三方接口 20190329
        //Page<DepartmentCO> departmentInfoByTenantId = departmentClient.pageDepartmentInfoByTenantId( null, page);
        Page<DepartmentCO> departmentInfoByTenantId = departmentClient.pageDepartmentInfoByTenantId( null, page.getCurrent() - 1, page.getSize());
        if (page.getRecords() == null){
            return new ArrayList<>();
        }
        return page.getRecords();
    }

    public List<DepartmentCO> listDepartmentByStatus(Boolean enabled){
        return  departmentClient.listDepartmentByStatus(enabled);
    }

    public Page<CompanyCO> pageCompanyByCond(Long setOfBooksId,
                                             String companyCode,
                                             String companyName,
                                             String companyCodeFrom,
                                             String companyCodeTo,
                                             List<Long> ignoreIds,
                                             Page page) {
        //jiu.zhao 修改三方接口 20190329
        //return companyClient.pageBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName,true, page, ignoreIds);
        return companyClient.pageBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName,true, page.getCurrent() - 1, page.getSize(), ignoreIds);
    }

    public DepartmentCO getDepartementCOByUserOid(String userOid){
        return departmentClient.getDepartmentByEmpOid(userOid);
    }

    public Boolean judgeUserInUserGroups(JudgeUserCO judgeUserCO) {
        return userClient.judgeUserInUserGroups(judgeUserCO);
    }

    public PeriodCO getPeriodsByIDAndTime(Long setOfBooksId, String zonedDateTimeToString) {
        return periodClient.getPeriodBysetOfBooksIdAndDateTime(setOfBooksId, zonedDateTimeToString);
    }

    public List<CompanyCO> listCompaniesByIds(List<Long> ids) {
        return companyClient.listByIds(ids);
    }

    public List<DepartmentCO> listDepartmentsByIds(List<Long> ids){
        return departmentClient.listDepartmentsByIds(ids);
    }

    public List<ContactCO> listUsersByIds(List<Long> ids) {
        return userClient.listByUserIds(ids);
    }

    public CompanyCO getCompanyById(Long companyId) {
        return companyClient.getById(companyId);
    }

    public String getOrderNumber(String documentType, String companyCode, String now) {
        //jiu.zhao 修改三方接口 20190329
        //return organizationClient.getOrderNumber(documentType, companyCode, now);
        String language = LoginInformationUtil.getCurrentLanguage();
        OrderNumberCO orderNumberCO = (OrderNumberCO)this.organizationClient.getOrderNumber(documentType, companyCode, now).getBody();
        if (StringUtils.isEmpty(orderNumberCO.getOrderNumber())) {
            throw new BizException(orderNumberCO.getCode(), (String)orderNumberCO.getMessage().stream().filter((u) -> {
                return u.getLanguage().equalsIgnoreCase(language);
            }).findFirst().map(Message::getContent).get());
        } else {
            return orderNumberCO.getOrderNumber().toString();
        }
    }

    public List<UserGroupCO> listUserGroupsByIds(List<Long> ids) {
        return userClient.listUserGroupByUserGroupIds(ids);
    }


    public List<DimensionCO> listDimensionsByIds(List<Long> ids){
        List<DimensionCO> dimensionCOS = dimensionClient.listDimensionsByIds(ids);
        if (CollectionUtils.isEmpty(dimensionCOS)){
            return new ArrayList<>();
        }
        return dimensionCOS;
    }

    public List<DimensionDetailCO> listDimensionDetailsByIds(List<Long> ids, Boolean enabled){
        List<DimensionDetailCO> dimensionDetailCOS = dimensionClient.listDimensionsByIdsAndEnabled(ids, enabled);
        if (CollectionUtils.isEmpty(dimensionDetailCOS)){
            return new ArrayList<>();
        }
        return dimensionDetailCOS;
    }

    public List<DimensionItemCO> listDimensionItemsByIds(List<Long> ids){
        List<DimensionItemCO> dimensionItemCOS = dimensionClient.listDimensionItemsByIds(ids);
        if (CollectionUtils.isEmpty(dimensionItemCOS)){
            return new ArrayList<>();
        }
        return dimensionItemCOS;
    }

    public List<DimensionCO> listAllDimensionsBySetOfBooksId(Long setOfBooksId){
        return dimensionClient.listDimensionBySetOfBooksIdAndEnabled(setOfBooksId ,true);
    }

    public List<DimensionDetailCO> listDimensionsBySetOfBooksIdAndIds(Long setOfBooksId, List<Long> ids){
        //jiu.zhao 修改三方接口 20190329
        //return dimensionClient.listDimensionsBySetOfBooksIdAndIdsAndEnabled(setOfBooksId, ids, null);
        return dimensionClient.listDimensionsBySetOfBooksIdAndIdsAndEnabled(setOfBooksId,null, ids);
    }

    public ApprovalFormCO getApprovalFormByOid(String formOid){
        return workflowClient.getApprovalFormByOid(formOid);
    }

    public List<DimensionCO> listDimensionsByCompanyId(Long companyId) {
        return dimensionClient.listDimensionsByCompanyId(companyId);
    }
    /**
     * 根据币种代码查询币种信息
     * @param code
     * @param enabled
     * @param setOfBooksId
     * @return
     */
    public List<CurrencyRateCO> listCurrencysByCode(
            String code,
            Boolean enabled,
            Long setOfBooksId
    ) {
        return currencyClient.listCurrencysByCode(code, enabled, setOfBooksId);
    }


    public List<DimensionCO> listDimensionsBySetOfBooksIdConditionByIgnoreIds(Long setOfBooksId, String dimensionCode, String dimensionName, Boolean enabled, List<Long> ignoreIds) {
        return dimensionClient.listDimensionsBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, dimensionCode, dimensionName, enabled, ignoreIds);
    }
    /**
     * 根据系统代码的code,以及值的code获取具体的系统代码
     * @param code
     * @param value
     * @return
     */
    public SysCodeValueCO getSysCodeValueByCodeAndValue(String code, String value){
        return organizationClient.getSysCodeValueByCodeAndValue(code, value);
    }

    public List<SysCodeValueCO> listSysCodeValueCOByOid(UUID codeOid){
        return organizationClient.listEnabledSysCodeValueByCodeOid(codeOid);
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
     * 根据人员组ID查询用户信息
     * @param userGroupId
     * @return
     */
    public List<ContactCO> listUsersByUserGroupId(Long userGroupId){
        return userClient.listByUserGroupId(userGroupId);
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
     * 获取账套信息
     * @param setOfBookId
     * @return
     */
    public SetOfBooksInfoCO getSetOfBooksById(Long setOfBookId) {
        return  sobClient.getSetOfBooksById(setOfBookId);
    }

    /**
     * 获取参数指定的值
     *
     * @param companyId  公司Id
     * @param sobId 账套Id
     * @param parameterCode 参数代码
     * @return 参数值
     */
    public String getParameterValue(Long companyId, Long sobId, String parameterCode){
        if(ParameterConstant.EXP_TAX_DIST.equals(parameterCode)){
            return "TAX_IN";
        }
        return parameterClient.getParameterValueByParameterCode(parameterCode, sobId, companyId);
    }


    /**
     * 获取可用的维值
     *
     * @param companyId 公司id
     * @param enabled 是否启用
     * @param  dimensionIds 维度集合
     */
    public List<DimensionDetailCO> listDetailCOByDimensionIdsAndCompany(Long companyId,
                                                                        Boolean enabled,
                                                                        List<Long> dimensionIds){
        return dimensionClient.listDetailByIdsConditionCompanyId(dimensionIds, companyId, enabled);
    }


    public List<CompanyLevelCO> listCompanyLevel(Long companyLevelId,String companyLevelCode){
        return companyClient.listCompanyLevel(companyLevelId,companyLevelCode);
    }


    public List<ApprovalFormCO> listApprovalFormsByIds(List<Long> ids){
        return workflowClient.listApprovalFormsByIds(ids);
    }

    /**
     * 根据公司Id分页获取指定公司的子公司
     *
     * @param companyId       公司Id
     * @param ignoreOwn       是否包含本公司 必输
     * @param companyCode     公司代码  条件查询
     * @param companyName     公司名称 条件查询
     * @param companyCodeFrom 公司代码从 条件查询
     * @param companyCodeTo   公司代码至 条件查询
     * @param keyWord         关键字 条件查询
     * @param page            第几页
     * @return 公司分页对象
     */
    public Page<CompanyCO> pageChildrenCompaniesByCondition(Long companyId,
                                                            Boolean ignoreOwn,
                                                            String companyCode,
                                                            String companyCodeFrom,
                                                            String companyCodeTo,
                                                            String companyName,
                                                            String keyWord,
                                                            Page page) {
        Page<CompanyCO> companyCOPage = companyClient.pageChildrenCompaniesByCondition(companyId,
                ignoreOwn,
                companyCode,
                companyCodeFrom,
                companyCodeTo,
                companyName,
                keyWord,
                page);
        return companyCOPage;
    }

    /**
     * 根据单据id获取单据名称
     * @param formId
     * @return
     */
    public ApprovalFormCO getApprovalFormById(Long formId){
        return workflowClient.getApprovalFormById(formId);
    }

    public Page<DepartmentCO> pageDepartmentByTenantId(String deptCode,String deptName,String deptCodeFrom, String deptCodeTo,Page page){
        return departmentClient.pageDepartmentByTenantId(deptCode, deptName,deptCodeFrom,deptCodeTo,page);
    }

    /**
     * 根据维度ID获取维度信息
     * @param id
     * @return
     */
    public DimensionCO getDimensionById(Long id){
        return dimensionClient.getDimensionById(id);
    }

    /**
     * 根据维值ID获取维值信息
     * @param id
     * @return
     */
    public DimensionItemCO getDimensionItemById(Long id){
        return dimensionClient.getDimensionItemById(id);
    }

    /**
     * 获取员工所在账套的本位币
     *
     * @param employeeOid oid
     * @return 币种代码
     */
    public String getUserSetOfBooksCurrencyCode(String employeeOid){
        return currencyClient.getUserSetOfBooksBaseCurrencyByApplicatinonOid(employeeOid);
    }

    /**
     * 根据id获取责任中心信息
     * @param id 责任中心id
     * @return
     */
    public ResponsibilityCenterCO getResponsibilityCenterById(Long id){
        return responsibilityCenterClient.getResponsibilityCenterById(id);
    }

    /**
     * 保存审批历史
     * @param commonApprovalHistoryCO
     * @return
     */
    public ApprovalHistoryCO saveHistory(CommonApprovalHistoryCO commonApprovalHistoryCO){
       return workflowClient.saveHistory(commonApprovalHistoryCO);
    }

    public Page<ContactCO> pageConditionNameAndIds(String employeeCode, String fullName, String keyWord, List<Long> ids, Page page){
        return userClient.pageConditionNameAndIds(employeeCode,fullName, keyWord,ids, page);
    }

    /**
     * 根据条件查询账套下的公司
     * @param setOfBooksId
     * @param companyCode
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param companyName
     * @param page
     * @param existsCompanyIds    公司ID集合
     * @return
     */
    public Page<CompanyCO> pageBySetOfBooksIdConditionByIds(Long setOfBooksId, String companyCode, String companyCodeFrom, String companyCodeTo, String companyName, Page page, List<Long> existsCompanyIds){
        return companyClient.pageBySetOfBooksIdConditionByIds(setOfBooksId,companyCode,companyCodeFrom,companyCodeTo,companyName,page,existsCompanyIds);
    }

    /**
     * 条件筛选责任中心 - 分页
     * @param setOfBooksId
     * @param code
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param keyWord
     * @param enabled
     * @param ids
     * @param page
     * @return
     */
    public Page<ResponsibilityCenterCO> pageByResponsibilityCenterByCond(Long setOfBooksId,
                                                 String code,
                                                 String codeFrom,
                                                 String codeTo,
                                                 String name,
                                                 String keyWord,
                                                 Boolean enabled,
                                                 List<Long> ids,
                                                 Page page){
        return responsibilityCenterClient.pageByResponsibilityCenterByCond(setOfBooksId,code,codeFrom,codeTo,name,keyWord,enabled,ids,page);
    }


    /**
     * 条件查询租户下启用的部门信息 - 分页
     * @param departmentCode
     * @param codeFrom
     * @param codeTo
     * @param name
     * @param ids
     * @param keyWord
     * @param page
     * @return
     */
    public Page<DepartmentCO> pageDepartmentsByCond(String departmentCode,
                                                    String codeFrom,
                                                    String codeTo,
                                                    String name,
                                                    List<Long> ids,
                                                    String keyWord,
                                                    Page page) {
        return departmentClient.pageDepartmentsByCond(departmentCode,codeFrom,codeTo,name,ids,keyWord,page);
    }

    /**
     * 获取公司部门对应的责任中心
     * @param companyId
     * @param departmentId
     * @param info
     * @param codeFrom
     * @param codeTo
     * @param enabled
     * @param page
     * @return
     */
    public Page<ResponsibilityCenterCO> pageDepartmentSobResponsibilityByCond(Long companyId,
                                                                              Long departmentId,
                                                                              String info,
                                                                              String codeFrom,
                                                                              String codeTo,
                                                                              Boolean enabled,
                                                                              Page page){
        return responsibilityCenterClient.pageDepartmentSobResponsibilityByCond(companyId,departmentId,info,codeFrom,codeTo,enabled,page);
    }

    /**
     * 根据公司部门获取默认的成本中心
     * @param companyId
     * @param departmentId
     * @return
     */
    public ResponsibilityCenterCO getDefaultResponsibilityCenter(Long companyId,Long departmentId) {
        return responsibilityCenterClient.getDefaultResponsibilityCenter(companyId, departmentId);
    }
    /**
     * 根据责任中心id集合获取责任中集合
     * @param responsibilityCenterIds
     * @return
     */
    public List<ResponsibilityCenterCO> getResponsibilityCenterByIdList(List<Long> responsibilityCenterIds){
        return responsibilityCenterClient.getResponsibilityCenterByIdList(responsibilityCenterIds);
    }

    public String submitWorkflow(WorkFlowDocumentRefCO workFlowDocumentRef){
        return workflowClient.submitWorkflow(workFlowDocumentRef);
    }

    /**
     * 根据ID获取供应商信息
     * @param id
     * @return
     */
    public VendorInfoCO getOneVendorInfoById(Long id){
        return supplierClient.getOneVendorInfoByArtemis(id.toString());
    }

    /**
     * 根据用户ID获取员工主银行账号信息
     * @param userId
     * @return
     */
    public UserBankAccountCO getContactPrimaryBankAccountByUserId(Long userId){
        return userClient.getContactPrimaryBankAccountByUserId(userId);
    }

    /**
     * 获取供应商银行账户信息
     * @param vendorInfoId
     * @return
     */
    public List<VendorBankAccountCO> listVendorBankAccounts(String vendorInfoId){
        return supplierClient.listVendorBankAccounts(vendorInfoId);
    }

    /**
     * 查询城市
     * @param placeList
     * @return
     */
    public List<LocationDTO> listCityByIds(List<Long> placeList) {
        return locationInterface.listCityByIds(placeList,"standard");
    }
}
