package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 *     单据审批界面 查询dto
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/29
 */
@Data
public class DocumentQueryParamCO implements Serializable {
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
    String currencyCode;
}
