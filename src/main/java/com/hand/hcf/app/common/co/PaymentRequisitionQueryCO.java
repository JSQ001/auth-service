package com.hand.hcf.app.common.co;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by 刘亮 on 2018/3/13.
 */
@Data
public class PaymentRequisitionQueryCO {
    List<String> documentOid;
    List<String> userList;
    String businessCode;
    BigDecimal amountFrom;
    BigDecimal amountTo;
    Long typeId;
    Long companyId;
    ZonedDateTime submitDateFrom;
    ZonedDateTime submitDateTo;
    String dateFrom;
    String dateTo;
    String description;
    Page page;
}
