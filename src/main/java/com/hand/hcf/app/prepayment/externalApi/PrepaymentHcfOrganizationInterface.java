package com.hand.hcf.app.prepayment.externalApi;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.implement.web.AttchmentControllerImpl;
import com.hand.hcf.app.base.implement.web.CommonControllerImpl;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.*;
import com.hand.hcf.app.prepayment.web.dto.PartnerBankInfo;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

//import com.hand.hcf.app.application.ApplicationService;
//import com.hand.hcf.app.application.dto.ApplicationDTO;
//import com.hand.hcf.app.application.dto.CustomFormForOtherRequestDTO;
//import com.hand.hcf.app.application.dto.PrepaymentRequisitionReleaseDTO;


/**
 * Created by liuzhiyu on 2017/9/15.
 */
@Service
public class PrepaymentHcfOrganizationInterface {


    private static final Logger log = LoggerFactory.getLogger(PrepaymentHcfOrganizationInterface.class);
//
//    //申请单相关服务
//    private  ApplicationService applicationService;

    @Autowired
    private CurrencyControllerImpl currencyClient;
    //TODO 增加组织架构调用接口
    @Autowired
    private CompanyControllerImpl companyClient;

    @Autowired
    private ContactControllerImpl userClient;

    // 附件接口
    @Autowired
    private AttchmentControllerImpl attachmentClient;

    @Autowired
    private CommonControllerImpl orgClient;

    @Autowired
    private SobControllerImpl sobClient;

    @Autowired
    private DepartmentControllerImpl departmentClient;

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private WorkflowControllerImpl workflowClient;


    public AttachmentCO getAttachmentByOID(String oid) {
        AttachmentCO attachmentCO = attachmentClient.getByOid(oid);
        if (attachmentCO == null) {
            return null;
        }
        return attachmentCO;
    }
//
//    public  List<PrepaymentRequisitionRelease> queryByRelated(Long refId) {
//
//        List<PrepaymentRequisitionRelease> result = new ArrayList<>();
//       /* String url = QUERY_BY_RELATED;
//        List<LinkedHashMap> list = artemisRestService.doRest(organizationBaseURL + url + "?sourceDocumentCategory=EXP_REQUISITION&sourceDocumentId=" + refId, List.class, null, HttpMethod.GET, null, false);*/
//       // 修改为Feign方式
//        List<PrepaymentRequisitionReleaseDTO> list = applicationService.getPrepaymentRequisitionReleaseBySourceDocumentMsg("EXP_REQUISITION",refId);
//
//        //此处需要反序列化，待处理，暂时这样处理
//        for (PrepaymentRequisitionReleaseDTO dto : list) {
//
//            PrepaymentRequisitionRelease p = new PrepaymentRequisitionRelease();
//            p.setAmount(dto.getAmount());
//            p.setCurrencyCode(dto.getCurrencyCode());
//            p.setExchangeRate(dto.getExchangeRate());
//            p.setFunctionalAmount(dto.getFunctionalAmount());
//            p.setId(dto.getId());
//            result.add(p);
//        }
//        return result;
//
//    }

//
//    /**
//     * 根据指定范围 查询申请单类型
//     *
//     * @param customFormForOtherRequestDTO
//     * @param page
//     * @return
//     */
//    public  List<CustomFormDTO> getCustomFormByRange(CustomFormForOtherRequestDTO customFormForOtherRequestDTO, Page page) {
//        //改用Feign方式
//        Page<CustomFormDTO> byRange = applicationService.getCustomFormByRange(customFormForOtherRequestDTO, page.getCurrent(), page.getSize());
//        if (byRange != null) {
//            return byRange.getRecords();
//        }
//        return null;
//    }
//
//    //保存时创建
//    public  Boolean createPrepaymentRequisitionRelease(List<PrepaymentRequisitionRelease> prepaymentRequisitionRelease) {
//        List<PrepaymentRequisitionReleaseDTO> list = null;
//        if(prepaymentRequisitionRelease != null && prepaymentRequisitionRelease.size() > 0){
//            List<PrepaymentRequisitionReleaseDTO> dtoList =  prepaymentRequisitionRelease.stream().map(e->{
//                PrepaymentRequisitionReleaseDTO dto = new PrepaymentRequisitionReleaseDTO();
//                BeanUtils.copyProperties(e,dto);
//                return dto;
//            }).collect(toList());
//            list =  applicationService.savePrepaymentRequisitionReleaseBatch(dtoList);
//            return CollectionUtils.isNotEmpty(list);
//        }
//        return false;
//    }
//    @TxTransaction
//    //撤回,驳回时释放
//    public  Boolean releasePrepaymentRequisitionRelease(Long prepaymentHeadId) {
//        //TODO 暂时注释
//        applicationService.deleteExpenseRequisitionReleaseMsg("CSH_PREPAYMENT",prepaymentHeadId,null);
//        return true;
//    }
//
//
//    public  Map<String, Double> getApplicationAmountById(Long id) {
//
//        Map<String, Double> map = applicationService.getRefAmountByApplicationId(id);
//        return map;
//
//    }
//
//
//    public  ApplicationDTO getApplicationById(Long id) {
//       //改用Feign接口形式
//       ApplicationDTO dto = applicationService.getApplicationById(id);
//       return dto;
//    }

    public String getPrepaymentCode() {
        Long companyID = OrgInformationUtil.getCurrentCompanyId();
        String companyCode = companyClient.getById(companyID).getCompanyCode();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(date);

        //jiu.zhao 修改三方接口 20190328
        //return orgClient.getOrderNumber("PREPAYMENT_REQUISITION", companyCode, now);
        String language = LoginInformationUtil.getCurrentLanguage();
        OrderNumberCO orderNumberCO = (OrderNumberCO) this.orgClient.getOrderNumber("PREPAYMENT_REQUISITION", companyCode, now).getBody();
        if (StringUtils.isEmpty(orderNumberCO.getOrderNumber())) {
            throw new BizException(orderNumberCO.getCode(), (String) orderNumberCO.getMessage().stream().filter((u) -> {
                return u.getLanguage().equalsIgnoreCase(language);
            }).findFirst().map(OrderNumberCO.Message::getContent).get());
        } else {
            return orderNumberCO.getOrderNumber().toString();
        }
    }


    /**
     * 通过币种code获取币种信息
     *
     * @param currency
     * @return
     */
    public CurrencyRateCO selectCurrencyByOtherCurrency(String baseCurrencyCode, String currency) {
        return currencyClient.getForeignCurrencyByCode(baseCurrencyCode, currency, OrgInformationUtil.getCurrentSetOfBookId());
    }

//    /*根据公司id和用户name，查询用户银行信息*/
//    public static Page<ReceivablesDTO> getContactBankAccountDTO(Long tenantId, String userName, Integer page, Integer size){
//        Page<ReceivablesDTO> dtoPage = new Page<>();
//        Long start = System.currentTimeMillis();
//
//        ResponseEntity<List<UserAccountCO>> responseEntity = userService.getContactBankByTenantIdAndUserName(tenantId, userName, page, size);
//        if(responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())){
//            return dtoPage;
//        }
//
//        log.info("根据公司id和用户name，查询用户银行信息,耗时:{}ms", System.currentTimeMillis() - start);
//        if(CollectionUtils.isNotEmpty(responseEntity.getBody())){
//            List<ReceivablesDTO> receivablesDTOS = contactAccountDTO2ReceivablesDTO(responseEntity.getBody());
//
//
//            dtoPage.setRecords(receivablesDTOS);
//            dtoPage.setTotal(Integer.parseInt( CollectionUtils.isNotEmpty(responseEntity.getHeaders().get("X-Total-Count")) ? responseEntity.getHeaders().get("X-Total-Count").get(0) : "0"));
//            return dtoPage;
//        }
//        return dtoPage;
//    }
//
//    /*根据公司id和用户name,code，查询用户银行信息*/
//    public  Page<ReceivablesDTO> getContactBankAccountDTO(Long tenantId,String userName,String userCode,Integer page,Integer size){
//        Page<ReceivablesDTO> dtoPage = new Page<>();
//        Long start = System.currentTimeMillis();
//
//        ResponseEntity<List<UserBankAccountCO>> responseEntity = userClient.getContactBankByTenantIdAndUserNameAndCode(tenantId, userName, userCode, page, size);
//        if(responseEntity == null || CollectionUtils.isEmpty(responseEntity.getBody())){
//            return dtoPage;
//        }
//        log.info("根据公司id和用户name 、code，查询用户银行信息,耗时:{}ms", System.currentTimeMillis() - start);
//        if(CollectionUtils.isNotEmpty(responseEntity.getBody())){
//            List<ReceivablesDTO> receivablesDTOS = contactAccountDTO2ReceivablesDTO(responseEntity.getBody());
//            dtoPage.setRecords(receivablesDTOS);
//            dtoPage.setTotal(Integer.parseInt( CollectionUtils.isNotEmpty(responseEntity.getHeaders().get("X-Total-Count")) ? responseEntity.getHeaders().get("X-Total-Count").get(0) : "0"));
//            return dtoPage;
//        }
//        return dtoPage;
//    }
//
//    private static List<ReceivablesDTO> contactAccountDTO2ReceivablesDTO(List<ContactBankAccountDTO> sourceDTOS){
//
//        return sourceDTOS.stream().map(e -> {
//            ReceivablesDTO dto = new ReceivablesDTO();
//            String fullName = e.getFullName();
//            dto.setIsEmp(true);
//            dto.setId(e.getId());
//            dto.setSign(dto.getId()+"_"+dto.getIsEmp());
//            dto.setCode(e.getCode());
//            dto.setName(fullName);
//            dto.setDepartment(e.getDepartmentName());
//            dto.setJob(e.getJob());
//            List<ContactBankAccountDTO> bankInfos = e.getBankInfos();
//            List<BankInfo> bankInfoList = new ArrayList<>();
//            if(CollectionUtils.isNotEmpty(bankInfos)){
//                bankInfos.forEach(
//                        u ->{
//                            BankInfo b = new BankInfo();
//                            b.setNumber(u.getBankAccountNo());
//                            b.setBankNumberName(u.getBankAccountName());
//                            b.setBankCode(u.getBankCode());
//                            b.setBankName(u.getBankName());
//                            b.setPrimary(u.isPrimary());
//                            bankInfoList.add(b);
//                        }
//                );
//            }
//            dto.setBankInfos(bankInfoList);
//            return dto;
//        }).collect(toList());
//    }

    /**
     * @return
     * @Description: 根据员工ID和银行账号获取银行账户信息
     * @param: userID 员工ID
     * @param: number  账户
     * @Date: Created in 2018/6/27 15:37
     * @Modified by
     */
    public PartnerBankInfo getEmployeeCompanyBankByCode(Long userID, String number) {
        UserBankAccountCO userBankAccountCO = userClient.getUserBankAccountByUserIdAndAccountNumber(userID, number);
        PartnerBankInfo partnerBankInfo = new PartnerBankInfo();
        mapper.map(userBankAccountCO, partnerBankInfo);
        return partnerBankInfo;
    }

    public CompanyCO getCompanyById(Long companyId) {
        return companyClient.getById(companyId);
    }

    public Page<CompanyCO> pageBySetOfBooksIdConditionByIgnoreIds(Long setOfBookId, String companyCode, String companyName, String companyCodeFrom, String companyCodeTo, List<Long> collect, Page page) {
        //Page<CompanyCO> companyCOPage = companyClient.pageBySetOfBooksIdConditionByIgnoreIds(setOfBookId, companyCode, companyCodeFrom, companyCodeTo, companyName,true, page, collect);
        //jiu.zhao 修改三方接口 20190328
        Page<CompanyCO> pageCompanies = this.companyClient.pageBySetOfBooksIdConditionByIgnoreIds(setOfBookId, companyCode, companyCodeFrom, companyCodeTo, companyName, true, page.getCurrent() - 1, page.getSize(), (List) (collect == null ? new ArrayList() : collect));
        return pageCompanies;
    }

    public List<CompanyCO> listCompanyById(List<Long> companyIds) {
        return companyClient.listByIds(companyIds);
    }

    public SetOfBooksInfoCO getSetOfBookById(Long setOfBookId) {
        return sobClient.getSetOfBooksById(setOfBookId);
    }

    public Map<String, String> getSysCodeValue(String s, String operationType, String sysCodeTypeNotExit) {
        SysCodeValueCO sysCodeValueCO = orgClient.getSysCodeValueByCodeAndValue(s, operationType);
        if (sysCodeValueCO == null) {
            throw new BizException(sysCodeTypeNotExit);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put(operationType, sysCodeValueCO.getName());
            return map;
        }
    }

    public ContactCO getUserById(Long userId) {
        return userClient.getById(userId);
    }

    public DepartmentCO getUnitsByUnitId(Long unitId) {
        return departmentClient.getDepartmentById(unitId);
    }

    public List<DepartmentCO> getDepartmentByDepartmentIds(List<Long> departmentOrUserGroupIdList) {
        //jiu.zhao 修改三方接口 20190328
        //return departmentClient.listDepartmentsByIds(departmentOrUserGroupIdList);
        return this.departmentClient.listDepartmentsByIds(departmentOrUserGroupIdList, (String) null);
    }

    public List<UserGroupCO> listUserGroupAndUserIdByGroupIds(List<Long> departmentOrUserGroupIdList) {
        return userClient.listUserGroupAndUserIdByGroupIds(departmentOrUserGroupIdList);
    }

    public List<ContactCO> listByUserIdsConditionByKeyWord(List<Long> ids, String keyWord) {
        return userClient.listByUserIdsConditionByKeyWord(ids, keyWord);
    }

    public Boolean judgeUserInUserGroups(JudgeUserCO judgeUserCO) {
        return userClient.judgeUserInUserGroups(judgeUserCO);
    }

    public Boolean judgeDepartmentAndUser(JudgeUserCO judgeUserCO) {
        return null;
    }

    public List<ContactCO> listByKeyWord(String empInfo) {
        return userClient.listByKeyWord(empInfo);
    }

    /**
     * 根据员工id获取员工所在部门
     *
     * @param empId
     * @return
     */
    public DepartmentCO getDepartmentByEmployeeId(Long empId) {
        return departmentClient.getDepartmentByEmployeeId(empId);
    }

    /**
     * 根据用户id查询其组织架构Id信息
     *
     * @param userId
     * @return
     */
    public OrganizationUserCO getOrganizationCOByUserId(Long userId) {
        return userClient.getOrganizationCOByUserId(userId);
    }

    /**
     * 根据部门ID查询用户信息
     *
     * @param departmentId
     * @return
     */
    public List<ContactCO> listUsersByDepartmentId(Long departmentId) {
        return userClient.listUsersByDepartmentId(departmentId);
    }

    /**
     * 查询当前租户下的所有员工
     *
     * @param tenantId
     * @return
     */
    public List<ContactCO> listUserByTenantId(Long tenantId) {
        return userClient.listUserByTenantId(tenantId);
    }

    /**
     * 根据人员组ID查询用户信息
     *
     * @param userGroupId
     * @return
     */
    public List<ContactCO> listUsersByUserGroupId(Long userGroupId) {
        return userClient.listByUserGroupId(userGroupId);
    }

    /**
     * @param userOid
     * @return
     * @author mh.z
     * @date 2019/02/19
     * @description 根据用户oid获取用户信息
     */
    public ContactCO getEmployeeByOid(String userOid) {
        //return userClient.getByUserOid(userOid);
        //jiu.zhao 修改三方接口 20190328
        return this.userClient.getByUserOid(UUID.fromString(userOid));
    }

    /**
     * 保存审批历史至工作流模块
     *
     * @param commonApprovalHistoryCO
     * @return
     */
    public ApprovalHistoryCO saveHistory(CommonApprovalHistoryCO commonApprovalHistoryCO) {
        return workflowClient.saveHistory(commonApprovalHistoryCO);
    }

    public List<ContactCO> listUsersByIds(List<Long> userList) {
        //return userClient.listByUserIds(userList);
        //jiu.zhao 修改三方接口 20190328
        return (List) (CollectionUtils.isEmpty(userList) ? new ArrayList() : this.userClient.listByUserIdsConditionByKeyWord(userList, (String) null));
    }

    /**
     * 分页条件获取当前租户下的用户信息
     *
     * @param employeeCode 员工代码
     * @param fullName     员工名称
     * @param keyWord      员工id、员工名称 关键词
     * @param ignoreIds    忽略id
     * @param page
     * @return
     */
    public Page<ContactCO> pageConditionNameAndIgnoreIds(String employeeCode,
                                                         String fullName,
                                                         String keyWord,
                                                         List<Long> ignoreIds,
                                                         Page page) {
        //bo.liu 修改三方接口 20190329
        //return userClient.pageConditionNameAndIgnoreIds(employeeCode, fullName, keyWord, ignoreIds, page);
        return userClient.pageConditionNameAndIgnoreIds(employeeCode,
                fullName,
                keyWord,
                ignoreIds == null ? new ArrayList<>() : ignoreIds,
                page.getCurrent() - 1,
                page.getSize());
    }
}
