

package com.hand.hcf.app.mdata.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.system.domain.BatchTransactionLog;
import com.hand.hcf.app.mdata.system.persistence.BatchTransactionLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
public class BatchTransactionLogService extends BaseService<BatchTransactionLogMapper, BatchTransactionLog> {


    BatchTransactionLog findByTransactionOid(UUID transactionOid) {
        return selectOne(new EntityWrapper<BatchTransactionLog>()
                .eq("transaction_oid",transactionOid));
    }

}
