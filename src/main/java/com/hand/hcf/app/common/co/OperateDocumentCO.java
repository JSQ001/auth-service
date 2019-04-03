package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/28
 */
@Data
public class OperateDocumentCO implements Serializable {
    private String lastRejectType;
    private String rejectType;
    private String rejectReason;
    private String documentOid;
    /**
     * 预算占用是否忽略警告
     */
    private Boolean ignoreWarningFlag;

    private Integer status;
}
