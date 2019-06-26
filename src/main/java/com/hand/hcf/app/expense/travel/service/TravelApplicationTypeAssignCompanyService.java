package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignCompany;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationTypeAssignCompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Service
public class TravelApplicationTypeAssignCompanyService extends BaseService<TravelApplicationTypeAssignCompanyMapper, TravelApplicationTypeAssignCompany> {

    @Autowired
    private TravelApplicationTypeService travelApplicationTypeService;
    @Autowired
    private OrganizationService organizationService;

    /**
     * 新建
     * @param travelTypeId
     * @param companyIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<TravelApplicationTypeAssignCompany> createAssignCompanyBatch(Long travelTypeId, List<Long> companyIds) {
        if (CollectionUtils.isEmpty(companyIds)){
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_ASSIGN_COMPANY_IS_NULL);
        }
        //查询类型是否存在
        if (travelApplicationTypeService.selectById(travelTypeId) == null) {
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_NOT_EXISTS);
        }

        List<TravelApplicationTypeAssignCompany> assignCompanyList = new ArrayList<>();
        companyIds.stream().forEach(id ->
            assignCompanyList.add(TravelApplicationTypeAssignCompany.builder().typeId(travelTypeId).companyId(id).build())
        );
        try{
            this.insertBatch(assignCompanyList);
        }catch (DataAccessException e){
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_ASSIGN_COMPANY_REPEAT);
        }
        return assignCompanyList;
    }

    /**
     * 更新启用状态
     * @param assignCompanyId
     * @param enabled
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelApplicationTypeAssignCompany updateAssignCompanyStatus(Long assignCompanyId, Boolean enabled) {
        if (assignCompanyId == null) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        TravelApplicationTypeAssignCompany assignCompany = baseMapper.selectById(assignCompanyId);
        if (assignCompany == null) {
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_ASSIGN_COMPANY_NOT_EXISTS);
        }
        assignCompany.setEnabled(enabled);
        baseMapper.updateById(assignCompany);
        return  assignCompany;
    }

    /**
     * 分页查询
     * @param travelTypeId
     * @param queryPage
     * @return
     */
    public List<TravelApplicationTypeAssignCompany> pageAssignCompany(Long travelTypeId, Page queryPage) {
        List<TravelApplicationTypeAssignCompany> assignCompanyList = baseMapper.selectPage(queryPage,
                new EntityWrapper<TravelApplicationTypeAssignCompany>().eq("type_id", travelTypeId)
        );
        //设置名称
        if (!CollectionUtils.isEmpty(assignCompanyList)) {
            assignCompanyList.stream().forEach(e -> {
                CompanyCO companyCO = organizationService.getCompanyById(e.getCompanyId());
                e.setCompanyCode(companyCO.getCompanyCode());
                e.setCompanyName(companyCO.getName());
                e.setCompanyType(companyCO.getCompanyTypeName());
            });
        }

        return assignCompanyList;
    }

    /**
     * 未分配公司查询
     * @param travelTypeId
     * @param setOfBooksId
     * @param companyCode
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param companyName
     * @param page
     * @return
     */
    public List<CompanyCO> pageCompanyByConditionFilter(Long travelTypeId, Long setOfBooksId, String companyCode, String companyCodeFrom, String companyCodeTo, String companyName, Page page) {
        List<Long> companyIdList = baseMapper.selectList(
                new EntityWrapper<TravelApplicationTypeAssignCompany>()
                        .eq("type_id", travelTypeId)
        ).stream().map(TravelApplicationTypeAssignCompany::getCompanyId).collect(Collectors.toList());

        List<CompanyCO> companyList = organizationService.pageCompanyByCond(setOfBooksId, companyCode, companyCodeFrom, companyCodeTo, companyName, companyIdList, page).getRecords();
        return companyList;
    }
}
