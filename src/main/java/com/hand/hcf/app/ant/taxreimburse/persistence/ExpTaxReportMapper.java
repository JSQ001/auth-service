package com.hand.hcf.app.ant.taxreimburse.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.dto.TaxBlendDataDTO;

import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金申报mapper
 * @date 2019/5/29 11:23
 */
public interface ExpTaxReportMapper extends BaseMapper<ExpTaxReport> {

    //分组获取要勾兑的数据
    List<TaxBlendDataDTO> getBlendDataByGroup();

    //根据相同的公司和币种更新数据状态
    void updateTaxReport(Long companyId, String currencyCode);

    //根据ou和币种查询数据的总数
    int getCountBygroup(Long companyId, String currencyCode);

    //根据id更新税金申报的数据的报账状态
    void updateStatusById(Long id,Long taxReimburseHeadId);

    //根据外键taxReimburseHeadId修改数据--删除报账单时调用
    int updateTaxByHeadId(Long taxReimburseHeadId);


}
