package com.hand.hcf.app.mdata.dimension.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.dimension.domain.Dimension;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItem;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemAssignCompany;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemAssignCompanyMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DimensionItemAssignCompanyService extends BaseService<DimensionItemAssignCompanyMapper, DimensionItemAssignCompany> {

    @Autowired
    private DimensionItemAssignCompanyMapper dimensionItemAssignCompanyMapper;

    @Autowired
    private DimensionItemService dimensionItemService;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private CompanyService companyService;

    /**
     * 批量新增 维值关联的公司表
     * @param list
     * @return
     */
    @Transactional
    public List<DimensionItemAssignCompany> insertDimensionItemAssignCompanyBatch(List<DimensionItemAssignCompany> list){
        list.stream().forEach(assignCompany -> {
            if (dimensionItemService.selectById(assignCompany.getDimensionItemId()) == null) {
                throw new BizException(RespCode.DIMENSION_ITEM_NOT_EXIST);
            }
            if (assignCompany.getId() != null){
                throw new BizException(RespCode.SYS_ID_NOT_NULL);
            }

            //重复校验
            if (dimensionItemAssignCompanyMapper.selectList(
                    new EntityWrapper<DimensionItemAssignCompany>()
                            .eq("dimension_item_id",assignCompany.getDimensionItemId())
                            .eq("company_id",assignCompany.getCompanyId())
            ).size() == 0){
                dimensionItemAssignCompanyMapper.insert(assignCompany);
            }

        });
        return list;
    }

    /**
     * 批量更新 维值关联的公司表启用状态
     * @param list
     * @return
     */
    @Transactional
    public List<DimensionItemAssignCompany> updateStatusBatch(List<DimensionItemAssignCompany> list){
        list.stream().forEach(assignCompany -> {
            DimensionItemAssignCompany oldAssignCompany = this.selectById(assignCompany.getId());
            if (oldAssignCompany != null) {
                this.updateById(assignCompany);
            } else {
                throw new BizException(RespCode.DIMENSION_ITEM_COMPANY_NOT_EXIST);
            }
        });
        return list;
    }


    /**
     * 根据维值Id查询出与之对应的公司表中的数据，前台显示公司代码以及公司名称(分页)
     * @param dimensionItemId
     * @param enabled
     * @param page
     * @return
     */
    public List<DimensionItemAssignCompany> pageDimensionItemAssignCompanyByCond(Long dimensionItemId,
                                                                                 Boolean enabled,
                                                                                 Page page){
        List<DimensionItemAssignCompany> list = dimensionItemAssignCompanyMapper.selectPage(
                page,
                new EntityWrapper<DimensionItemAssignCompany>()
                        .eq("dimension_item_id",dimensionItemId)
                        .eq(enabled != null,"enabled", enabled)
                        .orderBy("company_code")
        );
        list.stream().forEach(assignCompany -> {
            CompanyDTO companyDTO = companyService.findCompanyById(assignCompany.getCompanyId());
            if (companyDTO != null){
                assignCompany.setCompanyName(companyDTO.getName());
                assignCompany.setCompanyType(companyDTO.getCompanyTypeName());
            }
        });
        return list;
    }

    /**
     * 分配页面的公司筛选查询
     * @param dimensionItemId
     * @param companyCode
     * @param companyName
     * @param companyCodeFrom
     * @param companyCodeTo
     * @param page
     * @return
     */
    public List<CompanyCO> pageCompanyByCond(Long dimensionItemId,
                                             String companyCode,
                                             String companyCodeFrom,
                                             String companyCodeTo,
                                             String companyName,
                                             Page<CompanyCO> page) {
        List<Long> companyIdList = dimensionItemAssignCompanyMapper.selectList(
                new EntityWrapper<DimensionItemAssignCompany>()
                        .eq("dimension_item_id", dimensionItemId)
        ).stream().map(DimensionItemAssignCompany::getCompanyId).collect(Collectors.toList());
        DimensionItem dimensionItem = dimensionItemService.selectById(dimensionItemId);
        if (dimensionItem != null){
            Dimension dimension = dimensionService.selectById(dimensionItem.getDimensionId());
            List<CompanyCO> companyList = companyService.pageBySetOfBooksIdConditionByIgnoreIds(dimension.getSetOfBooksId(),companyCode,companyCodeFrom,companyCodeTo,companyName,true,companyIdList, page).getRecords();
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

