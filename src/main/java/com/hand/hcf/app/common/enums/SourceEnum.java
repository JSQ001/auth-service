package com.hand.hcf.app.common.enums;

/**
 * @Author: 魏胜
 * @Description: 供应商数据来源[级别] BILL 表单, TENANT 租户, COMPANY 公司, DUPLICATION 历史数据同一个租户下面有重复供应商数据标识
 *               租户级：TENANT, 公司级：BILL COMPANY
 * @Date: 2018/6/13 19:58
 */
public enum SourceEnum {
    BILL,
    TENANT,
    COMPANY,
    DUPLICATION,
}
