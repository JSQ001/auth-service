package com.hand.hcf.app.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * 操作日志
 * Created by 刘亮 on 2017/12/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDTO {

    //操作类型
    private String operationType;
    private String operationTypeName;

    //操作人
    private String operationMan;

    //银行报文
    private String bankMessage;

    //操作时间
    private ZonedDateTime operationTime;


    //备注
    private String remark;

}
