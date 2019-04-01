package com.hand.hcf.app.common.co;


import com.baomidou.mybatisplus.plugins.Page;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by ll on 2017/8/22.
 * 合同头信息DTO
 */
@Data
public class ContractReponseBatchCO {
    List<String> doucumentOid;
    List<String> userList;
    String businessCode;
    Integer amountFrom;
    Integer amountTo;
    Long contractTypeId;
    String dateFrom;
    String dateTo;
    ZonedDateTime submitDateFrom;
    ZonedDateTime submitDateTo;
    String currencyCode;
    String contractName;//20181010 合同审批需要按名称查询
    String remark;//20181010 合同审批增加按备注查询
    Page page;//20181129 分页放到合同模块来做
    Boolean finished;//20181129 已审批标识，排序用
}
