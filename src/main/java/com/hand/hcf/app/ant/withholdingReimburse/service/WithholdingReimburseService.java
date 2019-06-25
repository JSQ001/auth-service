package com.hand.hcf.app.ant.withholdingReimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.withholdingReimburse.dto.CompanyOrDeptAuthority;
import com.hand.hcf.app.ant.withholdingReimburse.dto.WithholdingReimburse;
import com.hand.hcf.app.ant.withholdingReimburse.dto.WithholdingReimburse;
import com.hand.hcf.app.ant.withholdingReimburse.persistence.CompanyOrDeptAuthorityMapper;
import com.hand.hcf.app.ant.withholdingReimburse.persistence.WithholdingReimburseMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WithholdingReimburseService extends BaseService<WithholdingReimburseMapper, WithholdingReimburse> {

    @Autowired
    private WithholdingReimburseMapper withholdingReimburseMapper;

    @Autowired
    private CompanyOrDeptAuthorityService companyOrDeptAuthorityService;

    @Autowired
    private CompanyOrDeptAuthorityMapper companyOrDeptAuthorityMapper;


    public List<WithholdingReimburse> queryPages(String categoryType, Page page){
        return  withholdingReimburseMapper.selectPage(page,new EntityWrapper<WithholdingReimburse>()
                .eq("category_type",categoryType)
                .eq("set_of_book_id",OrgInformationUtil.getCurrentSetOfBookId())
                .orderBy("last_updated_date")
        );
    }


    public WithholdingReimburse insertOrUpdateWithholdingReimburse (WithholdingReimburse expenseCategory){

        expenseCategory.setSetOfBooksId(OrgInformationUtil.getCurrentSetOfBookId());
        expenseCategory.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        expenseCategory.setCreatedBy(OrgInformationUtil.getCurrentUserId());

        if(expenseCategory.getId()!= null){
            // 更新的时候，设置最后更新时间
            expenseCategory.setLastUpdatedDate(ZonedDateTime.now());
        }

        this.insertOrUpdate(expenseCategory);


        return expenseCategory;
    }
}
