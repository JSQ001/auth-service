package com.hand.hcf.app.ant.withholdingReimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.withholdingReimburse.dto.WithholdingReimburse;
import com.hand.hcf.app.ant.withholdingReimburse.persistence.WithholdingReimburseMapper;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.accrual.service.ExpenseAccrualService;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class WithholdingReimburseService extends BaseService<WithholdingReimburseMapper, WithholdingReimburse> {

    @Autowired
    private WithholdingReimburseMapper withholdingReimburseMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ExpenseAccrualService expenseAccrualService;
    /*
     * 设置dto
     */
    private WithholdingReimburse getDto( WithholdingReimburse withholdingReimburse){
        // 设置单据类型明称
        if(withholdingReimburse.getDocumentTypeId()!=null){
            withholdingReimburse.setDocumentTypeName(expenseAccrualService.selectById(withholdingReimburse.getDocumentTypeId()).getTypeName());
        }
        // 设置创建人名称
        withholdingReimburse.setCreatedByName(OrgInformationUtil.getUser().getUsername());

        return withholdingReimburse;
    }

    /*
    * 分页查询
    */
    public List<WithholdingReimburse> queryPages(String categoryType, Page page){
        System.out.println(categoryType);
        return  withholdingReimburseMapper.selectPage(page,new EntityWrapper<WithholdingReimburse>()
                .eq("set_of_book_id",OrgInformationUtil.getCurrentSetOfBookId())
                .orderBy("last_updated_date")
        );
    }
    /*
     * id单个查询
     */
    public WithholdingReimburse selectDocumentById(Long id){
        WithholdingReimburse withholdingReimburse = withholdingReimburseMapper.selectById(id);
        return this.getDto(withholdingReimburse);
    }
    /*
     * 新增或修改
     */
    public WithholdingReimburse insertOrUpdateWithholdingReimburse (WithholdingReimburse withholdingReimburse){

        if(withholdingReimburse.getSetOfBooksId()==null){
            withholdingReimburse.setSetOfBooksId(OrgInformationUtil.getCurrentSetOfBookId());
        }
        if(withholdingReimburse.getCreatedBy()==null){
            withholdingReimburse.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            withholdingReimburse.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        }else {
            withholdingReimburse.setLastUpdatedBy(withholdingReimburse.getCreatedBy());
        }
        if(withholdingReimburse.getTenantId()==null){
            withholdingReimburse.setTenantId(OrgInformationUtil.getCurrentTenantId());
        }
        withholdingReimburse.setDocumentNumber(commonService.getCoding(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(),OrgInformationUtil.getCurrentCompanyId(),null));
        withholdingReimburse.setStatus(DocumentOperationEnum.GENERATE.getId().toString());
        withholdingReimburse.setAmount(BigDecimal.ZERO);

        if(withholdingReimburse.getId()!= null){
            // 更新的时候，设置最后更新时间
            withholdingReimburse.setLastUpdatedDate(ZonedDateTime.now());
        }

        this.insertOrUpdate(withholdingReimburse);


        return withholdingReimburse;
    }
}
