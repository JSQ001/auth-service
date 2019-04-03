package com.hand.hcf.app.mdata.company.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.domain.CompanyGroup;
import com.hand.hcf.app.mdata.company.domain.CompanyGroupAssign;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyGroupAssignDTO;
import com.hand.hcf.app.mdata.company.persistence.CompanyGroupAssignMapper;
import com.hand.hcf.app.mdata.company.persistence.CompanyGroupMapper;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.system.enums.SystemCustomEnumerationTypeEnum;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/*import com.hand.hcf.app.base.org.SysCodeValueCO;*/

/*
import com.hand.hcf.app.client.org.SysCodeValueCO;
*/

/**
 * Created by silence on 2017/9/18.
 */
@Service
@Transactional
public class CompanyGroupAssignService extends ServiceImpl<CompanyGroupAssignMapper, CompanyGroupAssign> {

    private final Logger log = LoggerFactory.getLogger(CompanyGroupAssignService.class);

    @Autowired
    private CompanyGroupAssignMapper companyGroupAssignMapper;

    @Autowired
    private CompanyGroupAssignService companyGroupAssignService;

    @Autowired
    private CompanyGroupMapper companyGroupMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    /**
     * 新建公司组分配明细
     *
     * @param companyGroupAssign
     * @return CompanyGroupAssign
     */
    public CompanyGroupAssign addCompanyGroupAssign(CompanyGroupAssign companyGroupAssign) {
        //  参数是否为空校验
        if (companyGroupAssign.getId() != null) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28001);
        }
        if (companyGroupAssign.getCompanyGroupId() == null) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28002);
        }
        if (companyGroupAssign.getCompanyId() == null) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28003);
        }
        //  查询公司组的账套ID
        CompanyGroup companyGroup = new CompanyGroup();
        companyGroup.setId(companyGroupAssign.getCompanyGroupId());
        CompanyGroup result = companyGroupMapper.selectById(companyGroup);
        if (result == null || result.getDeleted() == true) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28004);
        }
        //  查询公司的账套ID
        Company company = companyService.selectById(companyGroupAssign.getCompanyId());
        if (company == null) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28005);
        }
        //  判断账套ID是否相同
        if (company.getSetOfBooksId() - result.getSetOfBooksId() != 0) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28006);
        }
        //  判断是否重复
        CompanyGroupAssign target = companyGroupAssignService.selectOne(new EntityWrapper<CompanyGroupAssign>()
                .eq("company_group_id", companyGroupAssign.getCompanyGroupId())
                .eq("company_id", companyGroupAssign.getCompanyId())
        );
        if (target != null) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28008);
        }
        //  Set租户ID
        companyGroupAssign.setTenantId(OrgInformationUtil.getCurrentTenantId());
        //  校验是否获取到租户ID
        if (companyGroupAssign.getTenantId() == null) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28007);
        }
        //  插入检验后数据
        companyGroupAssignMapper.insert(companyGroupAssign);
        return companyGroupAssign;
    }


    /**
     * 批量 新建公司组分配明细
     *
     * @param list
     * @param list
     * @return CompanyGroupAssign
     */
    public Boolean addCompanyGroupAssignBatch(List<CompanyGroupAssign> list) {
        list.stream().forEach((CompanyGroupAssign companyGroupAssign) -> {
            addCompanyGroupAssign(companyGroupAssign);
        });
        //  返回成功标志
        return true;
    }


    /**
     * 删除公司组分配明细
     *
     * @param id 主键ID
     * @return
     */
    public Boolean deleteCompanyGroupAssign(Long id) {
        CompanyGroupAssign companyGroupAssign = new CompanyGroupAssign();
        companyGroupAssign.setId(id);
        CompanyGroupAssign result = companyGroupAssignMapper.selectOne(companyGroupAssign);
        if (null != result ) { // 删除成功
           // result.setDeleted(true);
            companyGroupAssignMapper.deleteById(result);
        } else { // 删除失败
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28010);
        }
        //  返回成功标志
        return true;
    }


    /**
     * 批量 删除公司组分配明细
     *
     * @param list
     * @return
     */
    public Boolean deleteCompanyGroupAssignBatch(List<Long> list) {
        list.stream().forEach(id -> {
            deleteCompanyGroupAssign(id);
        });
        //  返回成功标志
        return true;
    }


    /**
     * 分页查询 公司组分配明细表查询
     *
     * @param companyGroupId 公司组ID
     * @param page           分页对象
     * @return
     */
    public Page<CompanyGroupAssign> findCompanyGroupAssignById(Long companyGroupId, Page<CompanyGroupAssign> page) {
        //  根据公司组ID查询公司组
        CompanyGroup result = companyGroupMapper.selectById(companyGroupId);
        if (result == null || result.getDeleted() == true) {
            throw new BizException(RespCode.COMPANY_GROUP_ASSIGN_28004);
        }
        //  根据公司组ID获取此公司组下所有公司
        List<CompanyGroupAssign> list = companyGroupAssignMapper.selectPage(page, new EntityWrapper<CompanyGroupAssign>()
                .eq("company_group_id", result.getId())
        );
        //  判断是否查询到数据
        if (CollectionUtils.isNotEmpty(list)) {
            page.setRecords(list);
        }
        return page;
    }

    /**
     * 公司分配明细转换为公司分配明细DTO
     *
     * @param companyGroupAssign 公司分配明细实体
     * @return
     */
    public CompanyGroupAssignDTO transCompanyGroupAssignDTO(CompanyGroupAssign companyGroupAssign) {
        Company company = companyService.selectById(companyGroupAssign.getCompanyId());
        //  封装DTO
        CompanyGroupAssignDTO companyGroupAssignDTO = new CompanyGroupAssignDTO();
        companyGroupAssignDTO.setId(companyGroupAssign.getId());
        companyGroupAssignDTO.setCompanyId(companyGroupAssign.getCompanyId());
        companyGroupAssignDTO.setCompanyCode(company.getCompanyCode());
        companyGroupAssignDTO.setCompanyName(company.getName());
        // 查询公司类型名称
        if (null != company.getCompanyTypeCode()) {
            SysCodeValueCO sysCodeValue = hcfOrganizationInterface.getValueBySysCodeAndValue(SystemCustomEnumerationTypeEnum.COMPANY_TYPE.getId().toString(),company.getCompanyTypeCode());
            if (null != sysCodeValue) {
                companyGroupAssignDTO.setCompanyTypeName(sysCodeValue.getName());
            }
        }
        return companyGroupAssignDTO;
    }

    /**
     * 预算项目查询 根据公司ID查询公司组
     *
     * @param companyId
     * @return ResponseEntity
     */
    public List<CompanyGroup> findCompanyGroupByCompanyId(Long companyId) {
        return companyGroupAssignMapper.findCompanyGroupByCompanyId(companyId);
    }


    /**
     * 预算项目查询 根据公司组ID查询公司
     *
     * @param companyGroupId
     * @return ResponseEntity
     */
    public List<CompanyGroupAssign> findCompanyGroupByCompanyGroupId(Long companyGroupId) {
        return companyGroupAssignMapper.selectList(new EntityWrapper<CompanyGroupAssign>()
                .eq("company_group_id", companyGroupId)
        );
    }

    /**
     * 根据公司组明细分配封装公司DTO
     *
     * @return ResponseEntity
     */
    public List<CompanyGroupAssignDTO> CompanyGroupAssignAdapter(List<CompanyGroupAssign> list) {
        //  创建DTO集合
        List<CompanyGroupAssignDTO> result = new ArrayList<CompanyGroupAssignDTO>();
        //  判断是否为空
        if (CollectionUtils.isNotEmpty(list)) {
            //  遍历
            list.stream().forEach((CompanyGroupAssign companyGroupAssign) -> {
                //  查询公司
                Company company = companyService.selectById(companyGroupAssign.getCompanyId());
                //  封装DTO
                if (company.getEnabled() == true) {
                    CompanyGroupAssignDTO companyGroupAssignDTO = new CompanyGroupAssignDTO();
                    companyGroupAssignDTO.setId(companyGroupAssign.getId());
                    companyGroupAssignDTO.setCompanyId(companyGroupAssign.getCompanyId());
                    companyGroupAssignDTO.setCompanyCode(company.getCompanyCode());
                    companyGroupAssignDTO.setCompanyName(company.getName());
                    result.add(companyGroupAssignDTO);
                }
            });
        }
        return result;
    }

    /**
     * 预算项目查询 通过公司ID集合查询公司信息
     *
     * @param list
     * @return ResponseEntity
     */
//    public List<BudgetInfoDTO> findCompanyById(List<Long> list) {
//        List<BudgetInfoDTO> budgetInfoDTOS = new ArrayList<>();
//        list.stream().forEach( id -> {
//            Company companyId = companyService.selectById(id);
//            BudgetInfoDTO dto = new BudgetInfoDTO();
//            dto.setObjectId(companyId.getId());
//            dto.setObjectName(companyId.getName());
//            budgetInfoDTOS.add(dto);
//        });
//        return budgetInfoDTOS;
//    }

    /**
     * 预算项目查询 根据公司组ID集合查询每个ID下的公司
     *
     * @param list
     * @return ResponseEntity
     */
//    public List<BudgetInfoDTO> findCompaniesByGroupId(List<Long> list) {
//        List<BudgetInfoDTO> budgetInfoDTOS = new ArrayList<>();
//        list.stream().forEach( id -> {
//            BudgetInfoDTO dto = new BudgetInfoDTO();
//            dto.setObjectId(id);
//            dto.setObjectName(companyGroupMapper.selectById(id).getCompanyGroupName());
//            List<CompanyGroupAssign> results = companyGroupAssignMapper.selectList(new EntityWrapper<CompanyGroupAssign>()
//                    .where("deleted = false")
//                    .eq("company_group_id",id)
//            );
//            dto.setDetailIds(results.stream().map(u-> u.getCompanyId()).collect(Collectors.toList()));
//            budgetInfoDTOS.add(dto);
//        });
//        return budgetInfoDTOS;
//    }

    /**
     * 分页查询 查询所有公司
     *
     * @param page 分页对象
     * @return
     */
    public Page<Company> findAllCompany(Page<Company> page) {
        //  查询所有公司
        List<Company> list = companyService.findAll();
        //  判断是否查询到数据
        if(CollectionUtils.isNotEmpty(list)){
            page.setRecords(list);
        }
        return page;
    }

    /**
     * 预算项目查询 条件查询公司
     *
     * @param companyCode
     * @param companyName
     * @return ResponseEntity
     */
    public List<Company> findCompanyByCodeOrName(String companyCode, String companyName) {
        return companyGroupAssignMapper.findCompanyByCodeOrName(companyCode,companyName);
    }

    /**
     * 预算项目查询 通过公司从与公司到获取某区间的公司信息
     *
     * @param companyFrom
     * @param companyTo
     * @return ResponseEntity
     */
    public List<Company> findCompaniesByInterval(String companyFrom, String companyTo) {
        return companyGroupAssignMapper.findCompaniesByInterval(companyFrom,companyTo);
    }

    /**
     * 根据租户ID查询公司信息
     *
     * @param tenantId
     * @return ResponseEntity
     */
    public List<CompanyDTO> findCompanyByTenantId(Long tenantId){
        return companyService.findTenantAllCompany(tenantId);
    }

    /**
     * 预算项目查询 账套ID查询公司组及其明细公司信息
     * @param setOfBooksId 账套ID
     * @return
     */
//    public List<BudgetInfoDTO> findBudgetInfoDTOByBookId(Long setOfBooksId) {
//        List<BudgetInfoDTO> budgetInfoDTOS = new ArrayList<>();
//        //
//        List<CompanyGroup> list = companyGroupMapper.selectList(new EntityWrapper<CompanyGroup>()
//                .where("deleted = false")
//                .eq("set_of_books_id",setOfBooksId)
//        );
//        //
//        list.stream().forEach((CompanyGroup companyGroup) -> {
//            BudgetInfoDTO dto = new BudgetInfoDTO();
//            dto.setObjectId(companyGroup.getId());
//            dto.setObjectName(companyGroup.getCompanyGroupName());
//            List<CompanyGroupAssign> results = companyGroupAssignMapper.selectList(new EntityWrapper<CompanyGroupAssign>()
//                    .where("deleted = false")
//                    .eq("company_group_id",companyGroup.getId())
//            );
//            dto.setDetailIds(results.stream().map(u-> u.getCompanyId()).collect(Collectors.toList()));
//            budgetInfoDTOS.add(dto);
//        });
//        return budgetInfoDTOS;
//    }


}
