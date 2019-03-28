package com.hand.hcf.app.prepayment.web.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by 刘亮 on 2018/1/31.
 */
@Data
public class CashPrepaymentQueryDTO {
    @NotNull
    List<String> documentOid;
    List<String> userList;
    String businessCode;
    Double amountFrom;
    Double amountTo;
    Long typeId;
    ZonedDateTime submitDateFrom;
    ZonedDateTime submitDateTo;
    @NotNull
    Long companyId;
    String dateFrom;
    String dateTo;
    String description;
}
