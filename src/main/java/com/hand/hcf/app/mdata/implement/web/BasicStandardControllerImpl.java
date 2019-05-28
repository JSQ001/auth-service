package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.currency.service.CurrencyRateService;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.dto.DepartmentGroupDepartmentDTO;
import com.hand.hcf.app.mdata.department.service.DepartmentGroupService;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.legalEntity.service.LegalEntityService;
import com.hand.hcf.app.mdata.responsibilityCenter.service.ResponsibilityCenterService;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/25 15:21
 */
@RestController
public class BasicStandardControllerImpl  {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentGroupService departmentGroupService;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private LegalEntityService legalEntityService;

    @Autowired
    private ResponsibilityCenterService responsibilityCenterService;

    /**
     * 获取公司信息
     * @param selectId      id
     * @param code          代码
     * @param name          名称
     * @param securityType  筛选类型：SYSTEM 系统(filterId默认传1)；TENANT 租户；SET_OF_BOOKS 账套； COMPANY 公司
     * @param filterId      筛选值
     * @param page
     * @param size
     * @return
     */
  //  @Override
    public Page<BasicCO> pageCompaniesByInfoResultBasic(@RequestParam(value = "selectId",required = false) Long selectId,
                                                        @RequestParam(value = "code",required = false) String code,
                                                        @RequestParam(value = "name",required = false) String name,
                                                        @RequestParam("securityType") String securityType,
                                                        @RequestParam("filterId") Long filterId,
                                                        @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                        @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page myBatisPage = PageUtil.getPage(page,size);
        Page<BasicCO> result = companyService.pageCompaniesByInfoResultBasic(selectId,code, name, securityType, filterId, myBatisPage);
        return result;
    }

    /**
     * 条件查询租户下的部门
     * @param selectId
     * @param code
     * @param name
     * @param securityType  筛选类型：SYSTEM 系统(filterId默认传1)；TENANT 租户；SET_OF_BOOKS 账套； COMPANY 公司
     * @param filterId      筛选值
     * @param page
     * @param size
     * @return
     */
  //  @Override
    public Page<BasicCO> pageDepartmentsByInfoResultBasic(@RequestParam(value = "selectId",required = false) Long selectId,
                                                          @RequestParam(value = "code",required = false) String code,
                                                          @RequestParam(value = "name",required = false) String name,
                                                          @RequestParam("securityType") String securityType,
                                                          @RequestParam("filterId") Long filterId,
                                                          @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page myBatisPage = PageUtil.getPage(page,size);
        List<BasicCO> basicCOS = new ArrayList<>();
        if (selectId != null) {
            Department department = departmentService.findOneByDepartmentId(selectId);
            if (department == null) {
                throw new BizException(RespCode.SYS_REQUEST_RESOURCE_COULD_NOT_BE_FOUND);
            }
            BasicCO basicCO = BasicCO.builder()
                    .id(department.getId())
                    .code(department.getDepartmentCode())
                    .name(department.getName())
                    .build();
            basicCOS.add(basicCO);
        } else {
            Page<DepartmentGroupDepartmentDTO> departmentGroupDepartmentDTOS = departmentGroupService.selectDepartmentByTenantIdAndEnabled(filterId, code, name, false, null,false,  myBatisPage);
            myBatisPage.setTotal(departmentGroupDepartmentDTOS.getTotal());
            departmentGroupDepartmentDTOS.getRecords().forEach(
                    departmentGroupDepartmentDTO -> {
                        BasicCO basicCO = BasicCO.builder()
                                .id(departmentGroupDepartmentDTO.getDepartmentId())
                                .code(departmentGroupDepartmentDTO.getDepartmentCode())
                                .name(departmentGroupDepartmentDTO.getName())
                                .build();
                        basicCOS.add(basicCO);
                    }
            );
        }
        myBatisPage.setRecords(basicCOS);
        return myBatisPage;
    }

    /**
     * 条件查询币种信息
     * @param selectId    币种代码
     * @param code        币种代码
     * @param name
     * @param securityType  筛选类型：SYSTEM 系统(filterId默认传1)；TENANT 租户；SET_OF_BOOKS 账套； COMPANY 公司
     * @param filterId      筛选值
     * @param page
     * @param size
     * @return
     */
   // @Override
    public Page<BasicCO> pageCurrenciesByInfoResultBasic(@RequestParam(value = "selectId",required = false) String selectId,
                                                         @RequestParam(value = "code",required = false) String code,
                                                         @RequestParam(value = "name",required = false) String name,
                                                         @RequestParam("securityType") String securityType,
                                                         @RequestParam("filterId") Long filterId,
                                                         @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                         @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page myBatisPage = PageUtil.getPage(page,size);
        Page<BasicCO> result = currencyRateService.pageCurrenciesByInfoResultBasic(code,name,selectId,securityType,filterId,myBatisPage);
        return result;
    }

    /**
     * 条件查询法人
     * @param selectId
     * @param code
     * @param name
     * @param securityType  筛选类型：SYSTEM 系统(filterId默认传1)；TENANT 租户；SET_OF_BOOKS 账套； COMPANY 公司
     * @param filterId      筛选值
     * @param page
     * @param size
     * @return
     */
    //@Override
    public Page<BasicCO> pageLegalEntityByInfoResultBasic(@RequestParam(value = "selectId",required = false) Long selectId,
                                                          @RequestParam(value = "code",required = false) String code,
                                                          @RequestParam(value = "name",required = false) String name,
                                                          @RequestParam("securityType") String securityType,
                                                          @RequestParam("filterId") Long filterId,
                                                          @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page myBatisPage = PageUtil.getPage(page,size);
        Page<BasicCO> result = legalEntityService.pageLegalEntityByInfoResultBasic(code, name, selectId, securityType, filterId, myBatisPage);
        return result;
    }

   // @Override
    public Page<BasicCO> pageByLevelAndCompanyIdOrCond(Long selectId, String code, String name, String securityType, Long filterId, int page, int size) {
        Page queryPage = PageUtil.getPage(page,size);
        List<BasicCO> result = companyService.pageByLevelAndCompanyIdOrCond(selectId,code,name,securityType,filterId,queryPage);
        queryPage.setRecords(result);
        return queryPage;
    }

    /**
     * 条件查询租户下的部门
     * @param selectId
     * @param code
     * @param name
     * @param securityType  筛选类型：SYSTEM 系统(filterId默认传1)；TENANT 租户；SET_OF_BOOKS 账套； COMPANY 公司
     * @param filterId      筛选值
     * @param page
     * @param size
     * @return
     */
    //@Override
    public Page<BasicCO> pageResponsibilityCenterByInfoResultBasic(@RequestParam(value = "selectId",required = false) Long selectId,
                                                                   @RequestParam(value = "code",required = false) String code,
                                                                   @RequestParam(value = "name",required = false) String name,
                                                                   @RequestParam("securityType") String securityType,
                                                                   @RequestParam("filterId") Long filterId,
                                                                   @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                                   @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Page queryPage = PageUtil.getPage(page,size);
        Page<BasicCO> result = responsibilityCenterService.pageResponsibilityCenterByInfoResultBasic(selectId, code, name, securityType, filterId, queryPage);
        return result;
    }
}
