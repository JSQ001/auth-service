package com.hand.hcf.app.workflow.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单-申请单配置
 * Created by Wkit on 2017/4/12.
 */
public abstract class ApprovalFormPropertyConstants {

    //差旅/费用申请单是否启用自动停用（前端根据此配置，在创建单据时设置停用时间）
    public static final String APPLICATION_CLOSE_ENABLED = "application.close.enabled";

    //差旅/费用申请单完成后N天停用（前端根据此配置，在创建单据时设置停用时间）
    public static final String APPLICATION_CLOSE_CLOSEDAY = "application.close.closeDay";

    //差旅/费用申请单是否允许员工操作停用
    public static final String APPLICATION_CLOSE_PARTICIPANT_ENABLED = "application.close.participant.enabled";

    //差旅/费用申请单是否允许员工修改停用时间
    public static final String APPLICATION_CLOSE_CHANGE_ENABLED = "application.close.change.enabled";

    //差旅/费用申请单是否允许员工操作重新启用
    public static final String APPLICATION_CLOSE_RESTART_ENABLED = "application.close.restart.enabled";

    //差旅/费用申请单重启后M天停用
    public static final String APPLICATION_CLOSE_RESTART_CLOSEDAY = "application.close.restart.closeDay";

    //差旅/费用申请单 /1001:地点集管控 /1002:行程管控
    public static final String APPLICATION_PROPERTY_MANAGE_TYPE = "application.property.manage.type";

    //差旅申请单机票行程管控字段配置 json格式
    public static final String APPLICATION_PROPERTY_CONTROL_FIELDS = "application.property.control.fields";

    //差旅申请单酒店行程管控字段配置 json格式
    public static final String APPLICATION_PROPERTY_CONTROL_FIELDS_HOTEL = "application.property.control.fields.hotel";

    //差旅申请单火车行程管控字段配置 json格式
    public static final String APPLICATION_PROPERTY_CONTROL_FIELDS_TRAIN = "application.property.control.fields.train";

    //差旅/费用申请单是否支持携程统一订票
    public static final String APPLICATION_PROPERTY_UNIFORM_BOOKING_ENABLE = "application.property.uniform.booking.enable";

    //申请单参与人的范围
    public static final String APPLICATION_PARTICIPANTS_IMPORT_SCOPE = "application.participants.import.scope";

    //差旅报销单关联申请单
    public static final String FROM_TRAVEL_RELEVANCE_APPLICATION = "from.travel.relevance.application";

    //费用报销单关联申请单
    public static final String FROM_EXPENSE_RELEVANCE_APPLICATION = "from.expense.relevance.application";

    //是否同步校验预算
    public static final String FROM_BUDGET_SYNCHRONIZE_ENABLED = "from.budget.synchronize.enabled";

    //配置是否开启审批加签  true 开启加签  false 关闭加签  默认是开启加签 走顺序审批
    public static final String ENABEL_ADD_SIGN = "enableAddSign";

    //配置审批加签类型　１为只需一人审批通过  2 顺序全部审批
    public static final String COUNTERSIGN_TYPE = "countersignType";

    //配置是否开启自选审批人加签  true 开启加签  false 关闭加签  默认是开启加签 走顺序审批
    public static final String ENABEL_ADD_SIGN_FOR_SUBMITTER = "enableAddSignForSubmitter";

    //配置自选审批人加签类型　１为只需一人审批通过  2 顺序全部审批
    public static final String COUNTERSIGN_TYPE_FOR_SUBMITTER = "countersignTypeForSubmitter";

    //配置过滤规则　１和审批人重复过滤　２和加签审批人重复过滤 3和审批人加签人重复过滤
    public static final String FILTER_RULE = "filterRule";

    //配置过滤规则　１全局 比对审批历史　２每次提交的审批链 比对审批链
    public static final String FILTER_TYPE_RULE = "filterTypeRule";

    //配置根据驳回的金额变大是否开启过滤
    public static final String AMOUNT_FILTER = "amountFilter";

    //配置根据驳回的费用类型发生变化是否开启过滤
    public static final String EXPENSETYPE_FILTER = "expenseTypeFilter";

    //配置被代理人策略 1需要被代理人审批  2知会被代理人 3ALL
    public static final String PROXY_STRATEGY = "proxy_strategy";

    //配置加签选人范围
    public static final String APPROVAL_ADD_SIGN_SCOPE = "approval_add_sign_scope";

    //默认最大机票数量是否允许修改管控字段配置值
    public static final String MAX_FLIGHT_TICKET_AMOUNT_MODIFIED_ENABLE = "application.property.max.ticket.amount.modified.enable";

    //是否允许更改申请单
    public static final String APPLICATION_CHANGE_ENABLE = "application.change.enable";

    //表单配置携程扩展字段
    public static final String TRAVEL_APPLICATION_CTRIP_COST_CENTER_CUSTOM = "travel.application.ctrip.cost.center.custom";

    //订票申请是否必须走审批流，默认支持
    public static final String TRAVEL_BOOKER_SKIP_WORKFLOW_DISABLE = "travel.booker.skip.workflow.disable";

    //是否启用价格审核，默认不启用
    public static final String TRAVEL_BOOKER_PRICE_AUDIT_ENABLE = "travel.booker.price.audit.enable";

    //订票申请审批通过是否发送邮件，默认不发送
    public static final String TRAVEL_BOOKER_PASS_SEND_EMAIL_ENABLE = "travel.booker.pass.send.email.enable";

    //是否允许先订票／退改签，再补走流程，默认支持
    public static final String TRAVEL_BOOKER_SUPPLEMENT_ENABLE = "travel.booker.supplement.enable";

    //差补维度
    public static final String TRAVEL_SUBSIDIES_DIMENSION = "travel.subsidies.dimension";

    //机票行程按钮是否隐藏
    public static final String TRAVEL_FLIGHT_ITINERARY_DISABLED = "ca.travel.flight.disabled";

    //酒店行程按钮是否展示
    public static final String TRAVEL_HOTEL_ITINERARY_ENABLE = "hotel.itinerary.enable";

    //火车行程按钮是否隐藏
    public static final String TRAVEL_TRAIN_ITINERARY_DISABLED = "ca.travel.train.disabled";

    //其他行程按钮是否隐藏
    public static final String TRAVEL_OTHER_ITINERARY_DISABLED = "ca.travel.other.disabled";

    //行程备注按钮是否隐藏
    public static final String TRAVEL_ITINERARY_REMARK_DISABLED = "ca.travel.remark.disabled";

    //差补行程按钮是否隐藏
    public static final String TRAVEL_ALLOWANCE_DISABLED = "travel.allowance.disabled";

    //失效时间
    public static final String EXPIRED_TIME_CONFIG = "ca.travel.deactivatedate.enabled";

    //借款单参与报销单还款  默认true
    public static final String LOAN_APPLICTION_PAETICIPATION_REPAY_ENABLE = "loan.application.participation.repay.enable";

    //订票方式的propertyName
    public static final String CUSTOM_FORM_PROP_BOOKING_PREFERENCE_PROPERTYNAME = "ca.travel.bookingpreference";

    //优先统一订票
    public static final String CONSOLIDATED_BOOKING_PREFERRED = "Consolidated";

    //优先各自订票
    public static final String INDIVIDUAL_BOOKING_PREFERRED = "Individual";

    //仅统一订票
    public static final String ONLY_CONSOLIDATED_BOOKING_ALLOWE = "OnlyConsolidated";

    //仅各自订票
    public static final String ONLY_INDIVIDUAL_BOOKING_ALLOWED = "OnlyIndividual";

    //审批流复制，需要复制的表单属性
    public static final List<String> copyApprovalFormProperty = new ArrayList();

    static{
        copyApprovalFormProperty.add(ENABEL_ADD_SIGN);
        copyApprovalFormProperty.add(COUNTERSIGN_TYPE);
        copyApprovalFormProperty.add(ENABEL_ADD_SIGN_FOR_SUBMITTER);
        copyApprovalFormProperty.add(COUNTERSIGN_TYPE_FOR_SUBMITTER);
        copyApprovalFormProperty.add(FILTER_RULE);
        copyApprovalFormProperty.add(FILTER_TYPE_RULE);
        copyApprovalFormProperty.add(AMOUNT_FILTER);
        copyApprovalFormProperty.add(EXPENSETYPE_FILTER);
        copyApprovalFormProperty.add(PROXY_STRATEGY);
        copyApprovalFormProperty.add(APPROVAL_ADD_SIGN_SCOPE);
    }

}
