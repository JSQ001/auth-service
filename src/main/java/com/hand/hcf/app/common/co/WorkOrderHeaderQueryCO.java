package com.hand.hcf.app.common.co;

import com.baomidou.mybatisplus.plugins.Page;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by liang.liu04@hand-china.com
 * on 2018/7/26
 */
@Data
public class WorkOrderHeaderQueryCO {
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
    String currency;
    Page page;
    //判断单据类型是已审批/未审批
    Boolean isFinished;
    /**
     *  根据单据驳回重新提交,金额或成本中心等是否变更 确认审批时候需要过滤
     */
    private Boolean filterFlag;// true表示跳过,false表示不跳
    /**
     * 历史驳回类型 RejectTypeEnum 驳回类型: 1000-正常, 1001-撤回, 1002-审批驳回 1003-审核驳回 1004-开票驳回
     */
    private String lastRejectType;
    private String rejectType;
    private String rejectReason;
}
