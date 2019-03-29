package com.hand.hcf.app.mdata.system.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.mdata.system.enums.BatchOperationTypeEnum;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by markfredchen on 16/10/6.
 */
@Data
@NoArgsConstructor
@TableName("sys_batch_transaction_log")
public class BatchTransactionLog extends Domain {

    @TableField( "transaction_oid")
    private UUID transactionOid;
    private BatchOperationTypeEnum operation;
    private Integer totalEntities;
    private Integer successEntities;
    private Integer failureEntities;
    private ZonedDateTime startTime;

    private ZonedDateTime finishTime;
    private int status;

    private JSONObject errors;

    private String errorDetail;

    @TableField( "parent_transaction_oid")
    private UUID parentTransactionOid;

    public BatchTransactionLog(BatchOperationTypeEnum batchOperationTypeEnum, Integer totalEntities) {
        this.operation = batchOperationTypeEnum;
        this.totalEntities = totalEntities;
        this.successEntities = 0;
        this.failureEntities = 0;
        this.transactionOid = UUID.randomUUID();
        this.startTime = ZonedDateTime.now();
    }

}
