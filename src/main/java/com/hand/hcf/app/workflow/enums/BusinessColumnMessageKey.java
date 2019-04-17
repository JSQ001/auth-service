package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.app.core.domain.enumeration.BusinessEnum;

/**
 * 表单控件定义
 * 具有业务用途的字段需单独定义
 */
public enum BusinessColumnMessageKey implements BusinessEnum {
    WRITEOFF_FLAG("writeoff_flag"),//是否核销借款
    SUBSTITUTION_INVOICE("substitution_invoice"),//替票
    VAT_INVOICE("vat_invoice"),//增值票
    TOTAL_BUDGET("total_budget"),//总预算
    TITLE("title"),//事由
    START_DATE("start_date"),//开始日期
    END_DATE("end_date"),//结束日期
    REMARK("remark"),//备注
    CURRENCY_CODE("currency_code"),//币种
    BUDGET_DETAIL("budget_detail"),//预算明细
    AVERAGE_BUDGET("average_budget"),//人均预算
    SELECT_PARTICIPANT("select_participant"),//参与人员
    SELECT_APPROVER("select_approver"),//选人审批（根据配置动态生成
    SELECT_APPLICANT("applicant"),//选择申请人
    SELECT_USER("select_user"),//普通控件，选择一个用户
    SELECT_DEPARTMENT("select_department"),//部门
    DESTINATION("destination"),//目的地
    OUT_PARTICIPANT_NUM("out_participant_num"),//（原外部乘机人数）2017-05-24 更新为外部参与人数量
    SELECT_SPECIAL_BOOKING_PERSON("select_special_booking_person"),//订票专员（for订票申请）
    SELECT_CORPORATION_ENTITY("select_corporation_entity"),//法人实体
    SELECT_COMPANY("select_company"),//公司
    SELECT_AIR_TICKET_SUPPLIER("select_air_ticket_supplier"),//选择机票供应商(for 蓝标)
    DATE("date"),
    EXPECTED_REPAYMENT_DATE("expected_repayment_date"),//借款单预计还款日期(目前用的是date)
    CONTACT_BANK_ACCOUNT("contact_bank_account"),//报销单对应的银行卡信息的OID
    ATTACHMENT("attachment"),   //附件
    IMAGE("image"),   //图片
    OUT_PARTICIPANT_NAME("out_participant_name"),//外部参与人姓名
    EXTERNAL_PARTICIPANT_NAME("external_participant_name"),//外部参与人姓名
    SELECT_BOX("select_box"),//选择框
    CUST_LIST("cust_list"),//自定义列表
    NUMBER("number"),//数字
    INPUT("input"),//单行输入框
    TEXT_AREA("text_area"),//多行输入框
    TIME("time"),//时间
    SWITCH("switch"),//开关
    LINKAGE_SWITH("linkage_switch"),//联动开关
    VEN_MASTER("venMaster"),//收款单位/个人
    EXP_ALLOCATE("exp_allocate"),//费用分摊
    VEN_MASTER_SWITCH("venMasterSwitch"),//是否对供应商支付组合控件
    YING_FU_SELECT_APPROVER("ying_fu_select_approver"), //英孚选人审批控件  动态获取
    EMPLOYEE_EXPAND("employee_expand"),//员工个人信息扩展
    PAYEE("payee"), //收款方
    COMMON_DATE("common.date"),//时间
    DATETIME("dateTime"),//时间
    LONG("LONG"),//整数
    ;

    private String key;

    BusinessColumnMessageKey(String key) {
        this.key = key;
    }

    public static BusinessColumnMessageKey parse(String key) {
        for (BusinessColumnMessageKey fieldType : BusinessColumnMessageKey.values()) {
            if (fieldType.getKey().equals(key)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * 判断messageKey是否已经存在
     */
    public static boolean exists(String messageKey){
        boolean result = false;
        BusinessColumnMessageKey[] values = BusinessColumnMessageKey.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getKey().equals(messageKey)){
                result = true;
                break;
            }
        }
        return result;
    }
}
