package com.hand.hcf.app.common.message;

/**
 * Created by kai.zhang on 2017-12-29.
 * 核算页面展示信息相关，由于数据量较大，为维护方便，重新创建工具类
 */
public interface ModuleMessageCode {

    /*数据结构相关*/
    //对公报账单
    String DATA_SOURCE_EXP_REPORT_HEADER = "90001";         //对公报账单头
    String DATA_SOURCE_EXP_REPORT_LINE = "90002";         //对公报账单行
    String DATA_SOURCE_EXP_REPORT_DIST = "90003";         //对公报账单分摊行
    String DATA_SOURCE_EXP_REPORT_INVOICE = "90004";         //对公报账单发票行
    String DATA_SOURCE_EXP_REPORT_SCHEDULE = "90005";         //对公报账单计划付款行
    String DATA_SOURCE_EXP_REPORT_INVOICE_HEADER = "90006";         //对公报账单发票头
    String DATA_SOURCE_EXP_REPORT_INVOICE_LINE = "90007";           //对公报账单发票行
    String DATA_SOURCE_EXP_REPORT_INVOICE_LINE_DIST = "90008";       //发票分配行
    String DATA_SOURCE_EXP_REPORT_TAX_DIST = "90009";               //分摊税行


    /*数据结构属性相关*/
    //对公报账单头相关
    String EXP_REPORT_HEADER_ID = "90001.001";              //单据ID
    String EXP_REPORT_HEADER_BUSINESS_CODE = "90001.002";                  //单据编号
    String EXP_REPORT_HEADER_COMPANY_ID = "90001.003";               //公司ID
    String EXP_REPORT_HEADER_UNIT_ID = "90001.004";               //部门ID
    String EXP_REPORT_HEADER_APPLICATION_ID = "90001.005";               //申请人ID
    String EXP_REPORT_HEADER_FORM_ID = "90001.006";               //报销单类型ID
    String EXP_REPORT_HEADER_REMARK = "90001.007";               //对公报账单头备注
    String EXP_REPORT_HEADER_TENANT_ID = "90001.008";               //租户ID
    String EXP_REPORT_HEADER_SET_OF_BOOKS_ID = "90001.009";               //账套ID
    String EXP_REPORT_HEADER_REPORT_DATE = "90001.010";               //报账日期
    String EXP_REPORT_HEADER_REPORT_STATUS = "90001.011";               //报账单状态
    String EXP_REPORT_HEADER_JE_CREATION_STATUS = "90001.012";               //创建凭证状态
    String EXP_REPORT_HEADER_JE_CREATION_DATE = "90001.013";               //
    String EXP_REPORT_HEADER_JE_CREATION_PERIOD = "90001.014";               //
    String EXP_REPORT_HEADER_REVERSED_FLAG = "90001.015";               //反冲标志

    //对公报账单分摊行相关
    String EXP_REPORT_DIST_ID = "90003.001";                        //分摊行ID
    String EXP_REPORT_DIST_TENANT_ID = "90003.002";                        //租户ID
    String EXP_REPORT_DIST_SET_OF_BOOKS_ID = "90003.003";                        //帐套ID
    String EXP_REPORT_DIST_COMPANY_ID = "90003.004";                        //公司ID
    String EXP_REPORT_DIST_UNIT_ID = "90003.005";                        //部门ID
    String EXP_REPORT_DIST_RES_CENTER_ID = "90003.055";                        //责任中心ID
    String EXP_REPORT_DIST_EMPLOYEE_ID = "90003.006";                        //员工ID
    String EXP_REPORT_DIST_DESCRIPTION = "90003.007";                        //分摊行描述
    String EXP_REPORT_DIST_EXPENSE_TYPE_ID = "90003.008";                        //费用类型ID
    String EXP_REPORT_DIST_CURRENCY_CODE = "90003.009";                        //币种
    String EXP_REPORT_DIST_RATE = "90003.010";                        //汇率
    String EXP_REPORT_DIST_AMOUNT = "90003.011";                        //原币金额
    String EXP_REPORT_DIST_FUNCTIONAL_AMOUNT = "90003.012";                        //本币金额
    String EXP_REPORT_DIST_DATE = "90003.013";                        //分摊日期

    //对公报账单发票行相关
    String EXP_REPORT_INVOICE_TENANT_ID = "90004.002";                        //租户ID
    String EXP_REPORT_INVOICE_SET_OF_BOOKS_ID = "90004.003";                        //帐套ID
    String EXP_REPORT_INVOICE_COMPANY_ID = "90004.004";                        //公司ID
    String EXP_REPORT_INVOICE_EXPENSE_SEARCH_ENTITY_ID = "90004.005";                        //法人ID
    String EXP_REPORT_INVOICE_TAX_PAYER_NAME = "90004.006";                        //销方名称
    String EXP_REPORT_INVOICE_TAX_PAYER_NUMBER = "90004.007";                        //销方纳税人识别号
    String EXP_REPORT_INVOICE_INVOICE_CODE = "90004.008";                        //发票代码
    String EXP_REPORT_INVOICE_INVOICE_NUMBER = "90004.009";                        //发票号码
    String EXP_REPORT_INVOICE_INVOICE_APPLY_TYPE = "90004.010";                        //发票类型(1普票,2专票)
    String EXP_REPORT_INVOICE_TAX_TOTAL_AMOUNT = "90004.011";                        //发票总金额
    String EXP_REPORT_INVOICE_TAX_RATE = "90004.012";                        //税率
    String EXP_REPORT_INVOICE_TAX_AMOUNT = "90004.013";                        //税额
    String EXP_REPORT_INVOICE_SALE_AMOUNT = "90004.014";                        //不含税金额
    String EXP_REPORT_INVOICE_FIXED_ASSETS_FLAG = "90004.015";                        //不动产标志

    //对公报账单计划付款行相关
    String EXP_REPORT_SCHEDULE_ID = "90005.001";                        //计划付款行ID
    String EXP_REPORT_SCHEDULE_TENANT_ID = "90005.002";                        //租户ID
    String EXP_REPORT_SCHEDULE_SET_OF_BOOKS_ID = "90005.003";                        //帐套ID
    String EXP_REPORT_SCHEDULE_COMPANY_ID = "90005.004";                        //公司ID
    String EXP_REPORT_SCHEDULE_DESCRIPTION = "90005.005";                        //计划付款行描述
    String EXP_REPORT_SCHEDULE_CURRENCY = "90005.006";                        //币种
    String EXP_REPORT_SCHEDULE_AMOUNT = "90005.007";                        //金额
    String EXP_REPORT_SCHEDULE_FUNCTIONAL_AMOUNT = "90005.008";                        //本币金额
    String EXP_REPORT_SCHEDULE_SCHEDULE_PAYMENT_DATE = "90005.009";                        //计划付款日期
    String EXP_REPORT_SCHEDULE_PAYMENT_METHOD = "90005.010";                        //付款方式大类
    String EXP_REPORT_SCHEDULE_CSH_TRANSACTION_CLASS_ID = "90005.011";                        //现金事务分类ID
    String EXP_REPORT_SCHEDULE_CASH_FLOW_ITEM_ID = "90005.012";                        //现金流量项ID
    String EXP_REPORT_SCHEDULE_PAYEE_CATEGORY = "90005.013";                        //收款对象类型
    String EXP_REPORT_SCHEDULE_PAYEE_ID = "90005.014";                        //收款对象ID
    String EXP_REPORT_SCHEDULE_PAYEE_CODE = "90005.015";                        //收款对象CODE
    String EXP_REPORT_SCHEDULE_ACCOUNT_NUMBER = "90005.016";                        //银行账号
    String EXP_REPORT_SCHEDULE_ACCOUNT_NAME = "90005.017";                        //银行户名

    String JOURNAL_EXPORT_HEADER_DESC = "60000";
    String JOURNAL_EXPORT_LINE_DESC = "60001";
    String JOURNAL_EXPORT_SOB = "60002";
    String JOURNAL_EXPORT_COMPANY = "60003";
    String JOURNAL_EXPORT_DEPARTMENT = "60004";
    String JOURNAL_EXPORT_TRAN_TYPE = "60005";
    String JOURNAL_EXPORT_BUSINESS_NUMBER = "60006";
    String JOURNAL_EXPORT_JOURNAL_DATE = "60007";
    String JOURNAL_EXPORT_BUSINESS_DATE = "60008";
    String JOURNAL_EXPORT_ACCOUNT = "60009";
    String JOURNAL_EXPORT_CURRENCY = "60010";
    String JOURNAL_EXPORT_CHANGE_RATE = "60011";
    String JOURNAL_EXPORT_ORIGINAL_DR = "60012";
    String JOURNAL_EXPORT_ORIGINAL_CR = "60013";
    String JOURNAL_EXPORT_LOCAL_DR = "60014";
    String JOURNAL_EXPORT_LOCAL_CR = "60015";
}
