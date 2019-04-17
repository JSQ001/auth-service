package com.hand.hcf.app.mdata.system.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * Created by chenliangqin on 17/2/13.
 * 系统值列表类型标识
 */
public enum SystemCustomEnumerationTypeEnum implements SysEnum {
    /**
     * 人员类型
     */
    EMPLOYEETYPE(1001),
    /**
     * 职务
     */
    DUTY(1002),
    /**
     * 携程子账户
     */
    CTRIPSUBACCOUNT(1003),
    /**
     * 银行名称
     */
    BANKNAME(1004),
    /**
     * 国籍
     */
    NATIONALITY(1005),
    /**
     * 证件类型
     */
    CERTIFICATETYPE(1006),
    /**
     * 性别
     */
    SEX(1007),
    /**
     * 级别
     */
    LEVEL(1008),
    /**
     * 税率
     */
    TAX_RATE(1009),
    /**
     * 期间
     */
    PERIOD(1010),
    /**
     * 公司类型
     */
    COMPANY_TYPE(1011),
    /**
     * 发票类型
     */
    RECEIPT_TYPE(1012),
    /**
     * 报表维度费用类型代码
     */
    EXPENSE_TYPE_CODE(4001),
    /**
     * 报表维度费用字段代码
     */
    EXPENSE_FIELD_CODE(4002),
    /**
     * 表单报表属性
     */
    ADDITIONAL_FIELD(4003),
    /**
     * 报销单状态
     */
    REIMBURSEMENT_STATUS(5002),
    /**
     * 发票检验结果
     */
    INVOICE_INSPECTION_RESULTS(5003),
    /**
     * 驳回节点
     */
    REJECT_NODE(5004),
    /**
     * 发票状态
     */
    INVOICE_STATUS(5005),
    /**
     * 单据类型
     */
    DOCUMENT_TYPE(5006);

    private Integer id;

    SystemCustomEnumerationTypeEnum(Integer id) {
        this.id = id;
    }

    public static SystemCustomEnumerationTypeEnum parse(Integer id) {
        for (SystemCustomEnumerationTypeEnum systemCustomEnumerationTypeEnum : SystemCustomEnumerationTypeEnum.values()) {
            if (systemCustomEnumerationTypeEnum.getId().equals(id)) {
                return systemCustomEnumerationTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
