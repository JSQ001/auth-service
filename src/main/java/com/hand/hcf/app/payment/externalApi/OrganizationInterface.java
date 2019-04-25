package com.hand.hcf.app.payment.externalApi;


import org.springframework.stereotype.Component;


/**
 * Created by cbc on 2017/11/28.
 */
@Component
public class OrganizationInterface {

    /*private static OrganizationService orgService;
    private static CompanyService companyService;
    private static DepartmentService departmentService;
    private static UserService userService;
    private static PeriodService periodService;
    private static MapperFacade mapperFacade;

    private OrganizationInterface(PeriodService periodService,
                                  OrganizationService orgService,
                                  CompanyService companyService,
                                  DepartmentService departmentService,
                                  UserService userService,
                                  MapperFacade mapperFacade) {
        this.orgService = orgService;
        this.periodService = periodService;
        this.companyService= companyService;
        this.departmentService=departmentService;
        this.userService=userService;
        this.mapperFacade = mapperFacade;
    }*/


    /**
     * 通过账套、期间编码获取期间详细信息
     *
     * @param setOfBooksId 账套ID
     * @param periodName 期间
     * @return
     */
    /*public static PeriodDTO getPeriodInfoBySetOfBooksId(Long setOfBooksId , String periodName){
        return periodService.getPeriodInfoBySetOfBooksId(setOfBooksId, periodName);
    }*/

    /**
     * 通过账套、时间获取期间详细信息
     *
     * @param setOfBooksId 账套ID
     * @param time 时间
     * @return
     */
    /*public static PeriodDTO getPeriodInfoBySetOfBooksId(Long setOfBooksId , ZonedDateTime time){
        return periodService.findPeriodsByIDAndTime(setOfBooksId, TypeConversionUtils.timeToString(time));
    }*/

    /**
     * 根据部门id查询部门详情
     * @param queryParameter
     * @return
     */
    /*public static DepartmentInfoDTO getUnitById(Object queryParameter){
        List<DepartmentInfoDTO> list = departmentService.getDepartmentByDepartmentIds(Arrays.asList(TypeConversionUtils.parseLong(queryParameter)));
        if (list.size() > 0 ){
            return list.get(0);
        }else{
            return null;
        }
    }*/


    /**
     * @Description: 根据员工ID和银行账号获取银行账户信息
     * @param: userID 员工ID
     * @param: number  账户
     * @return
     * @Date: Created in 2018/6/27 15:37
     * @Modified by
     */
    /*public static PartnerBankInfo getEmployeeCompanyBankByCode(Long userID, String number){
        ContactBankAccountDTO contactBankAccountDTO = userService.getEmployeeCompanyBankByCode(userID, number);
        PartnerBankInfo partnerBankInfo = new PartnerBankInfo();
        mapperFacade.map(contactBankAccountDTO, partnerBankInfo);
        return partnerBankInfo;
    }*/

    /**
     * @Description: 获取编码规则的值
     * @param: documentType
     * @return: java.lang.String
     * @Date: Created in 2018/4/19 10:32
     * @Modified by
     */
    /*public static String getCoding(String documentType, Long companyId){
        String companyCode = companyService.getCompanyById(companyId).getCompanyCode();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String now = sdf.format(date);
        return orgService.getOrderNumber(documentType, companyCode, now, OrgInformationUtil.getCurrentTenantID());
    }*/
}
