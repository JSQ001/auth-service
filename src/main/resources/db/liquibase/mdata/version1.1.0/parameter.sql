insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('1', 'EXPENSE', '费用模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('2', 'PREPAYMENT', '预付款模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('3', 'PAYMENT', '支付模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('4', 'CONTACT', '合同模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('5', 'BUDGET', '预算模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('6', 'ACCOUNTING', '核算模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('7', 'WORKBENCH', '工作台模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('8', 'WORKFLOW', '工作流模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('9', 'BASE', '基础设置', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('10', 'MDATA', '主数据模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('11', 'TAX', '税务模块', 0, '1', '1', '1');

insert into sys_parameter_module (id, module_code, module_name, deleted, created_by, last_updated_by, version_number)
values ('12', 'FUND', '资金模块', 0, '1', '1', '1');










insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('1', '0', 'EXPENSE', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('2', '0', 'PREPAYMENT', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('3', '0', 'PAYMENT', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('4', '0', 'CONTACT', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('5', '0', 'BUDGET', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('6', '0', 'ACCOUNTING',1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('7', '0', 'WORKBENCH', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('8', '0', 'WORKFLOW', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('9', '0', 'BASE', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('10', '0', 'MDATA', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('11', '0', 'TAX', 1, '1', '1', '1');

insert into sys_para_module_status (id, tenant_id, module_code, enabled, created_by, last_updated_by, version_number)
values ('12', '0', 'FUND', 1, '1', '1', '1');



INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (1, 'BGT_REVERSE_PERIOD', '单据反冲期间', 'BUDGET', 1, 1, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (2, 'BGT_CLOSED_PERIOD', '单据关闭期间', 'BUDGET', 1, 1, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (3, 'BGT_OCCUPY_DATE', '预算占用期间', 'BUDGET', 1, 1, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (4, 'UNMAPPED_BUDGET_ITEM', '未映射预算项目处理方式', 'BUDGET', 1, 1, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (5, 'DATA_AUTHORITY', '数据权限启用标志', 'BASE', 0, 0, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (6, 'EXP_TAX_DIST', '税金分摊方式', 'EXPENSE', 0, 0, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (7, 'PROJEST_DIMENSION_CODE', '项目参数维度', 'TAX', 1, 0, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (8, 'COMPANY_UNIT_RELATION', '公司部门是否维护关联关系', 'MDATA', 0, 0, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (9, 'PAYMENT_ACCOUNTING_ENABLED', '支付数据是否启用核算', 'PAYMENT', 1, 1, '1001', '', '', '', 0, 1, 1, 1);
INSERT INTO sys_parameter(id, parameter_code, parameter_name, module_code, sob_parameter, company_parameter, parameter_value_type, api_source_module, api, remark, deleted, created_by, last_updated_by, version_number) VALUES (10, 'TAX_AISINO_REQUEST_ADDRESS', '航信请求地址', 'TAX', 0, 0, '1003', '', '', '', 0, 1, 1, 1);


insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values (1, 'ORIGINAL_PERIOD', '预算释放在原期间', 'BGT_REVERSE_PERIOD', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('2', 'CURRENT_PERIOD', '预算释放在当前期间', 'BGT_REVERSE_PERIOD', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('3', 'ORIGINAL_PERIOD', '预算释放在原期间', 'BGT_CLOSED_PERIOD', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('4', 'CURRENT_PERIOD', '预算释放在当前期间', 'BGT_CLOSED_PERIOD', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('5', 'SUBMIT_DATE', '单据提交期间', 'BGT_OCCUPY_DATE', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('6', 'EXPENSE_DATE', '费用发生期间', 'BGT_OCCUPY_DATE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('7', 'BUDGET_CHECK_ERROR', '预算校验错误', 'UNMAPPED_BUDGET_ITEM', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('8', 'NO_BUDGET_CONTROL', '不控制预算', 'UNMAPPED_BUDGET_ITEM', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('9', 'Y', '启用数据权限', 'DATA_AUTHORITY', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('10', 'N', '不启用数据权限', 'DATA_AUTHORITY', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('11', 'TAX_IN', '按含税金额占分摊', 'EXP_TAX_DIST', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('12', 'TAX_OFF', '按不含税金额占分摊', 'EXP_TAX_DIST', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('13', 'DIMENSION1', '维度1', 'PROJEST_DIMENSION_CODE', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('14', 'DIMENSION2', '维度2', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('15', 'DIMENSION3', '维度3', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('16', 'DIMENSION4', '维度4', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('17', 'DIMENSION5', '维度5', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('18', 'DIMENSION6', '维度6', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('19', 'DIMENSION7', '维度7', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('20', 'DIMENSION8', '维度8', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('21', 'DIMENSION9', '维度9', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('22', 'DIMENSION10', '维度10', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('23', 'DIMENSION11', '维度11', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('24', 'DIMENSION12', '维度12', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('25', 'DIMENSION13', '维度13', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('26', 'DIMENSION14', '维度14', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('27', 'DIMENSION15', '维度15', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('28', 'DIMENSION16', '维度16', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('29', 'DIMENSION17', '维度17', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('30', 'DIMENSION18', '维度18', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('31', 'DIMENSION19', '维度19', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('32', 'DIMENSION20', '维度20', 'PROJEST_DIMENSION_CODE', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('33', 'Y', '公司部门需维护关联关系', 'COMPANY_UNIT_RELATION', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('34', 'N', '公司部门不需维护关联关系', 'COMPANY_UNIT_RELATION', 0, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('35', 'Y', '支付数据需核算', 'PAYMENT_ACCOUNTING_ENABLED', 1, 0, 1, 1, 1);

insert into sys_parameter_values (id, parameter_value_code, parameter_value_name, parameter_code, paramete_default_value, deleted, created_by, last_updated_by, version_number)
values ('36', 'N', '支付数据不需核算', 'PAYMENT_ACCOUNTING_ENABLED', 0, 0, 1, 1, 1);

INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (1, '2001', 0, NULL, NULL, 1, '2', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (2, '2001', 0, NULL, NULL, 2, '4', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (3, '2001', 0, NULL, NULL, 3, '5', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (4, '2001', 0, NULL, NULL, 4, '8', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (5, '2001', 0, NULL, NULL, 5, '9', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (6, '2001', 0, NULL, NULL, 6, '12', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (7, '2001', 0, NULL, NULL, 7, '13', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (8, '2001', 0, NULL, NULL, 8, '33', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (9, '2001', 0, NULL, NULL, 9, '35', 0, 1, 1, 1);
INSERT INTO sys_parameter_setting(id, parameter_level, tenant_id, set_of_books_id, company_id, parameter_id, parameter_value_id, deleted, created_by, last_updated_by, version_number) VALUES (10, '2001', 0, NULL, NULL, 10, NULL, 0, 1, 1, 1);
