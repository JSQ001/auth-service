package com.hand.hcf.app.mdata.responsibilityCenter.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityAssignCompany;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.app.mdata.responsibilityCenter.persistence.ResponsibilityAssignCompanyMapper;
import com.hand.hcf.app.mdata.responsibilityCenter.persistence.ResponsibilityCenterMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponsibilityAssignCompanyService extends BaseService<ResponsibilityAssignCompanyMapper,ResponsibilityAssignCompany> {

    @Autowired
    private ResponsibilityAssignCompanyMapper responsibilityAssignCompanyMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ResponsibilityCenterMapper responsibilityCenterMapper;

    /**
     * 分页获取分配公司
     * @param responsibilityCenterId 责任中心
     * @param enabled 启用禁用
     * @param page 分页
     * @return
     */
    public List<ResponsibilityAssignCompany> pageResponsibilityCenterAssignCompany(Long responsibilityCenterId,
                                                                                    Boolean enabled,
                                                                                    Page page) {
        List<ResponsibilityAssignCompany> list = responsibilityAssignCompanyMapper.selectPage(page,
                new EntityWrapper<ResponsibilityAssignCompany>()
                        .eq("responsibility_center_id",responsibilityCenterId)
                        .eq(enabled != null,"enabled", enabled)
                        .orderBy("company_code"));
        list.stream().forEach(assignCompany->{
            CompanyCO companyCO = companyService.getById(assignCompany.getCompanyId());
            if (companyCO != null){
                assignCompany.setCompanyName(companyCO.getName());
                assignCompany.setCompanyType(companyCO.getCompanyTypeName());
            }
        });
        return list;
    }

    /**
     * 批量创建公司分配
     * @param list
     * @return
     */
    @Transactional
    public List<ResponsibilityAssignCompany> insertResponsibilityAssignCompanyBatch(List<ResponsibilityAssignCompany> list) {
        list.stream().forEach(assignCompany->{
            Long responsibilityCenterId = assignCompany.getResponsibilityCenterId();
            ResponsibilityCenter responsibilityCenter =responsibilityCenterMapper.selectById(responsibilityCenterId);
            if(responsibilityCenter == null){
                throw new BizException(RespCode.RESPONSIBILITY_CENTER_NOT_EXIST);
            }
            if(responsibilityAssignCompanyMapper.selectCount(
                    new EntityWrapper<ResponsibilityAssignCompany>()
                            .eq("responsibility_center_id",assignCompany.getResponsibilityCenterId())
                            .eq("company_id",assignCompany.getCompanyId()))
                     == 0){
                responsibilityAssignCompanyMapper.insert(assignCompany);
            }
        });
        return  list;
    }
    /**
     * 批量更新分配公司状态
     * @param list
     * @return
     */
    @Transactional
    public List<ResponsibilityAssignCompany> updateStatusBatch(List<ResponsibilityAssignCompany> list) {
        list.stream().forEach(assignCompany->{
          ResponsibilityAssignCompany oldAssignCompany = responsibilityAssignCompanyMapper.selectById(assignCompany.getId());
          if(oldAssignCompany != null){
              this.updateById(assignCompany);
          }else {
              throw new BizException(RespCode.RESPONSIBILITY_CENTER_COMPANY_NOT_EXIST);
          }
        });
        return list;
    }

    /**
     * 获取未分配公司信息
     * @param responsibilityCenterId 责任中心Id
     * @param companyCode 公司代码
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param companyName
     * @param page
     * @return
     */
    public List<CompanyCO> pageCompanyByCond(Long responsibilityCenterId,
                                             String companyCode,
                                             String companyCodeFrom,
                                             String companyCodeTo,
                                             String companyName,
                                             Page page) {
        List<Long> companyIdList = responsibilityAssignCompanyMapper.selectList(
                new EntityWrapper<ResponsibilityAssignCompany>()
                        .eq("responsibility_center_id", responsibilityCenterId)
        ).stream().map(ResponsibilityAssignCompany::getCompanyId).collect(Collectors.toList());
        ResponsibilityCenter responsibilityCenter = responsibilityCenterMapper.selectById(responsibilityCenterId);
        if (responsibilityCenter != null){
            List<CompanyCO> companyList = companyService.pageBySetOfBooksIdConditionByIgnoreIds(responsibilityCenter.getSetOfBooksId(),
                    companyCode,
                    companyCodeFrom,
                    companyCodeTo,companyName,
                    true,
                    companyIdList,
                    page).getRecords();
            return companyList;
        }
        return null;
    }

    /**
     * 分配页面的公司筛选查询
     * @param setOfBooksId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public List<CompanyCO> pageCompanyBySetOfBooksId(Long setOfBooksId, String companyCode, String companyCodeFrom, String companyCodeTo, String companyName, Page page) {
        return companyService.pageBySetOfBooksIdConditionByIgnoreIds(setOfBooksId,companyCode,companyCodeFrom,companyCodeTo,companyName,true,new ArrayList<>(), page).getRecords();
    }
}
