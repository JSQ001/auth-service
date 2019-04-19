import React from 'react';
import config from 'config';
import moment from 'moment';
// import constants from 'share/constants'
import { Badge, Popover, Avatar, Tooltip } from 'antd';
import { messages } from 'utils/utils';

const formatMoney = (number, decimals = 2, isString = false) => {
  number = (number + '').replace(/[^0-9+-Ee.]/g, '');
  var n = !isFinite(+number) ? 0 : +number,
    prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
    sep = typeof thousands_sep === 'undefined' ? ',' : thousands_sep,
    dec = typeof dec_point === 'undefined' ? '.' : dec_point,
    s = '',
    toFixedFix = function(n, prec) {
      var k = Math.pow(10, prec);
      return '' + Math.ceil(n * k) / k;
    };

  s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
  var re = /(-?\d+)(\d{3})/;
  while (re.test(s[0])) {
    s[0] = s[0].replace(re, '$1' + sep + '$2');
  }

  if ((s[1] || '').length < prec) {
    s[1] = s[1] || '';
    s[1] += new Array(prec - s[1].length + 1).join('0');
  }

  if (isString === true) {
    return s.join(dec);
  } else {
    return <span className="money-cell">{s.join(dec)}</span>;
  }
};

const chooserData = {
  user: {
    title: 'chooser.data.selectPerson', //选择人员
    url: config.mdataUrl + '/api/users/v3/search',
    searchForm: [
      {
        type: 'input',
        id: 'keyword',
        label: 'chooser.data.employeeID.fullName.mobile.email', //员工工号、姓名、手机号、邮箱
      },
    ],
    columns: [
      {
        title: 'chooser.data.employeeID', //工号
        dataIndex: 'employeeId',
        width: '10%',
      },
      {
        title: 'chooser.data.fullName', //姓名
        dataIndex: 'fullName',
        width: '15%',
      },
      {
        title: 'chooser.data.mobile', //手机号
        dataIndex: 'mobile',
        width: '20%',
      },
      {
        title: 'chooser.data.email', //邮箱
        dataIndex: 'email',
        width: '25%',
      },
      {
        title: 'chooser.data.dep', //部门名称
        dataIndex: 'departmentName',
        width: '15%',
        render: value => value || '-',
      },
      {
        title: 'chooser.data.duty', //职务
        dataIndex: 'title',
        width: '15%',
        render: value => value || '-',
      },
    ],
    key: 'userOid',
  },
  contract_user: {
    title: '选择人员',
    url: `${config.mdataUrl}/api/select/user/by/name/or/code/and/company`,
    searchForm: [{ type: 'input', id: 'keyword', label: '员工工号、姓名' }],
    columns: [
      { title: '工号', dataIndex: 'employeeID', width: '25%' },
      { title: '姓名', dataIndex: 'fullName', width: '25%' },
      { title: '部门名称', dataIndex: 'departmentName', width: '25%' },
      { title: '职务', dataIndex: 'title', width: '25%' },
    ],
    key: 'userOid',
  },
  user_group: {
    title: '选择人员组',
    // url: `${config.mdataUrl}/api/user/groups/company`,/*wjk 注释：该接口不支持搜所 20180712*/
    url: `${config.mdataUrl}/api/user/groups/search`,
    searchForm: [{ type: 'input', id: 'name', label: 'chooser.data.name' }],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code', width: '30%' },
      { title: 'chooser.data.name', dataIndex: 'name', width: '30%' },
      { title: 'chooser.data.description', dataIndex: 'comment', width: '40%' },
    ],
    key: 'id',
  },
  budget_journal_structure: {
    title: 'chooser.data.budget_journal_structure', //选择预算日记账所需的预算表',
    url: `${config.budgetUrl}/api/budget/journal/type/assign/structures/queryStructure`,
    searchForm: [
      { type: 'input', id: 'structureCode', label: 'chooser.data.code' },
      { type: 'input', id: 'structureName', label: 'chooser.data.name' },
      {
        type: 'select',
        id: 'structureCodeFrom',
        label: 'chooser.data.codeFrom',
        options: [],
        getUrl: `${config.budgetUrl}/api/budget/structures/queryAll`,
        labelKey: 'structureCode',
        valueKey: 'structureCode',
        method: 'get',
        renderOption: data => `${data.structureCode}(${data.structureName})`,
      },
      {
        type: 'select',
        id: 'structureCodeTo',
        label: 'chooser.data.codeTo',
        options: [],
        getUrl: `${config.budgetUrl}/api/budget/structures/queryAll`,
        labelKey: 'structureCode',
        valueKey: 'structureCode',
        method: 'get',
        renderOption: data => `${data.structureCode}(${data.structureName})`,
      },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'structureCode', width: '45%' },
      { title: 'chooser.data.name', dataIndex: 'structureName', width: '55%' },
    ],
    key: 'structureCode',
  },
  budget_journal_item: {
    title: '选择预算日记账所需的预算项目',
    url: `${config.budgetUrl}/api/budget/journal/type/assign/items/queryItem`,
    searchForm: [
      { type: 'input', id: 'itemCode', label: 'chooser.data.code' },
      { type: 'input', id: 'itemName', label: 'chooser.data.name' },
      {
        type: 'select',
        id: 'itemCodeFrom',
        label: 'chooser.data.codeFrom',
        options: [],
        getUrl: `${config.budgetUrl}/api/budget/items/find/all`,
        labelKey: 'itemCode',
        valueKey: 'itemCode',
        method: 'get',
        renderOption: data => `${data.itemCode}(${data.itemName})`,
      },
      {
        type: 'select',
        id: 'itemCodeTo',
        label: 'chooser.data.codeTo',
        options: [],
        getUrl: `${config.budgetUrl}/api/budget/items/find/all`,
        labelKey: 'itemCode',
        valueKey: 'itemCode',
        method: 'get',
        renderOption: data => `${data.itemCode}(${data.itemName})`,
      },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'itemCode', width: '45%' },
      { title: 'chooser.data.name', dataIndex: 'itemName', width: '55%' },
    ],
    key: 'itemCode',
  },
  budget_journal_company: {
    title: 'chooser.data.budget_journal_company',
    url: `${config.budgetUrl}/api/budget/journal/type/assign/companies/filter`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: 'chooser.data.companyCode' },
      { type: 'input', id: 'companyName', label: 'chooser.data.companyName' },
      { type: 'input', id: 'companyCodeFrom', label: 'chooser.data.companyCode.from' },
      { type: 'input', id: 'companyCodeTo', label: 'chooser.data.companyCode.to' },
    ],
    columns: [
      { title: 'chooser.data.companyCode', dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName', dataIndex: 'name' },
      { title: 'chooser.data.companyType', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  budget_item: {
    title: 'chooser.data.budget_item',
    url: `${config.budgetUrl}/api/budget/items/query`,
    searchForm: [{ type: 'input', id: 'itemCode', label: 'chooser.data.code' }],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'itemCode', width: '45%' },
      { title: 'chooser.data.name', dataIndex: 'itemName', width: '55%' },
    ],
    key: 'id',
  },
  //账套下公司
  available_company_setOfBooks: {
    title: 'chooser.data.company', //选择公司
    url: `${config.mdataUrl}/api/refactor/companies/user/setOfBooks`,
    searchForm: [
      {
        type: 'input',
        id: 'name',
        label: 'chooser.data.companyName', //公司名称
      },
    ],
    columns: [
      {
        title: 'chooser.data.companyCode', //公司代码
        dataIndex: 'companyCode',
      },
      {
        title: 'chooser.data.companyName', //公司名称
        dataIndex: 'name',
      },
      {
        title: 'chooser.data.companyType', //公司类型
        dataIndex: 'companyTypeName',
      },
    ],
    key: 'id',
  },
  //表单管理中权限设置下选择费用
  available_expense: {
    title: 'chooser.data.expense', //选择费用
    url: `${config.baseUrl}/api/expense/type/by/setOfBooks`,
    searchForm: [
      //这部分搜索先隐藏，等后台接口好了再打开
      // {
      //   type: 'input',
      //   id: 'keyword',
      //   label: ("chooser.data.expenseName")//费用名称
      // }
    ],
    columns: [
      {
        title: 'Icon', //费用Icon
        dataIndex: 'iconURL',
        render: value =>
          (
            <div>
              <img style={{ width: 30, height: 30 }} src={value} />
            </div>
          ) || '-',
      },
      {
        title: 'chooser.data.expenseName', //费用名称
        dataIndex: 'name',
      },
    ],
    key: 'expenseTypeOid',
  },
  available_company: {
    title: '选择公司',
    url: `${config.mdataUrl}/api/company/available/by/setOfBooks`,
    searchForm: [{ type: 'input', id: 'keyword', label: '公司名称、代码' }],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
  all_company_by_tenantId: {
    title: '选择公司',
    url: `${config.mdataUrl}/api/company/by/tenantId`,
    searchForm: [{ type: 'input', id: 'keyword', label: '公司名称、代码' }],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
  budget_item_type: {
    title: 'chooser.data.budget_item_type',
    url: `${config.budgetUrl}/api/budget/itemType/query`,
    searchForm: [
      { type: 'input', id: 'itemTypeCode', label: 'chooser.data.code' },
      { type: 'input', id: 'itemTypeName', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'itemTypeCode' },
      { title: 'chooser.data.name', dataIndex: 'itemTypeName' },
    ],
    key: 'id',
  },
  budget_item_budget: {
    title: 'chooser.data.budget_item',
    url: `${config.budgetUrl}/api/budget/items/query`,
    searchForm: [{ type: 'input', id: 'itemCode', label: 'chooser.data.code' }],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'itemCode', width: '45%' },
      { title: 'chooser.data.name', dataIndex: 'itemName', width: '55%' },
    ],
    key: 'id',
  },
  budget_item_filter: {
    title: 'chooser.data.budget_item_filter',
    searchForm: [
      { type: 'input', id: 'itemCode', label: 'chooser.data.code' },
      { type: 'input', id: 'itemName', label: 'chooser.data.name' },
      {
        type: 'select',
        id: 'itemCodeFrom',
        label: 'chooser.data.codeFrom',
        options: [],
        renderOption: data => `${data.itemCode}(${data.itemName})`,
      },
      {
        type: 'select',
        id: 'itemCodeTo',
        label: 'chooser.data.codeTo',
        options: [],
        renderOption: data => `${data.itemCode}(${data.itemName})`,
      },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'itemCode', width: '25%' },
      { title: 'chooser.data.name', dataIndex: 'itemName', width: '40%' },
      { title: 'chooser.data.type', dataIndex: 'itemTypeName', width: '35%' },
    ],
    key: 'id',
  },
  select_dimension: {
    title: 'chooser.data.select_dimension',
    url: `${config.mdataUrl}/api/dimension/page/by/cond`,
    searchForm: [
      { type: 'input', id: 'dimensionCode', label: 'chooser.data.code' },
      { type: 'input', id: 'dimensionName', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'dimensionCode', width: '50%' },
      { title: 'chooser.data.name', dataIndex: 'dimensionName', width: '50%' },
    ],
    key: 'id',
  },
  //核算工单类型定义使用
  gl_select_dimension: {
    title: '选择维度',
    url: `${config.accountingUrl}/api/general/ledger/work/order/types/queryDimensionByRange`,
    searchForm: [
      { type: 'input', id: 'code', label: '维度代码' },
      { type: 'input', id: 'name', label: '维度名称' },
      {
        type: 'select',
        id: 'range',
        label: '查看',
        defaultValue: 'all',
        allowClear: false,
        options: [
          { value: 'all', label: '全部' },
          { value: 'selected', label: '已选' },
          { value: 'notChoose', label: '未选' },
        ],
        labelKey: 'label',
        valueKey: 'value',
      },
    ],
    columns: [
      { title: '维度代码', dataIndex: 'dimensionCode' },
      { title: '维度名称', dataIndex: 'dimensionName' },
    ],
    key: 'id',
    listKey: 'records',
  },
  //核算工单行上公司
  gl_line_company: {
    title: '选择公司',
    url: `${config.accountingUrl}/api/general/ledger/work/order/types/queryCompanyForWorkOrderLine`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: '公司代码' },
      { type: 'input', id: 'companyName', label: '公司名称' },
      { type: 'input', id: 'companyCodeForm', label: '公司代码从' },
      { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
      { title: '公司类型', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  //核算工单行上科目
  gl_line_account: {
    title: '选择科目',
    url: `${config.accountingUrl}/api/general/ledger/work/order/types/queryAccountForWorkOrderLine`,
    searchForm: [
      { type: 'input', id: 'code', label: '科目代码' },
      { type: 'input', id: 'name', label: '科目名称' },
      {
        type: 'select',
        id: 'type',
        label: '科目类型',
        options: [
          { value: 'ASSET', label: '资产类' },
          { value: 'LIABILITY', label: '负债类' },
          { value: 'OWNERS_EQUITY', label: '所有者权益类' },
          { value: 'PROFIT_AND_LOSS', label: '损益类' },
          { value: 'COST', label: '成本类' },
        ],
        valueKey: 'value',
        labelKey: 'label',
      },
    ],
    columns: [
      { title: '科目代码', dataIndex: 'accountCode' },
      { title: '科目名称', dataIndex: 'accountName' },
      { title: '科目类型', dataIndex: 'accountTypeName' },
    ],
    key: 'id',
    listKey: 'records',
  },
  //核算工单类型定义使用
  gl_select_account: {
    title: '可用科目',
    url: `${config.accountingUrl}/api/general/ledger/work/order/types/queryAccountByRange`,
    searchForm: [
      { type: 'input', id: 'code', label: '科目代码' },
      { type: 'input', id: 'name', label: '科目名称' },
      {
        type: 'select',
        id: 'type',
        label: '科目类型',
        allowClear: false,
        options: [
          { value: 'ASSET', label: '资产类' },
          { value: 'LIABILITY', label: '负债类' },
          { value: 'OWNERS_EQUITY', label: '所有者权益类' },
          { value: 'PROFIT_AND_LOSS', label: '损益类' },
          { value: 'COST', label: '成本类' },
        ],
        valueKey: 'value',
        labelKey: 'label',
      },
      {
        type: 'select',
        id: 'range',
        label: '查看',
        defaultValue: 'all',
        options: [
          { value: 'all', label: '全部' },
          { value: 'selected', label: '已选' },
          { value: 'notChoose', label: '未选' },
        ],
        labelKey: 'label',
        valueKey: 'value',
      },
    ],
    columns: [
      { title: '科目代码', dataIndex: 'accountCode' },
      { title: '科目名称', dataIndex: 'accountName' },
      { title: '科目类型', dataIndex: 'accountTypeName' },
    ],
    key: 'id',
  },
  //核算工单类型定义使用
  gl_distribution_company: {
    title: '批量分配公司',
    url: `${config.accountingUrl}/api/general/ledger/work/order/type/companies/filter`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: '公司代码' },
      { type: 'input', id: 'companyName', label: '公司名称' },
      { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
      { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
      { title: '公司类型', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  gl_type_distribution_company: {
    title: '批量分配公司',
    url: `${config.accountingUrl}/api/general/ledger/work/order/type/companies/filter`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: '公司代码' },
      { type: 'input', id: 'companyName', label: '公司名称' },
      { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
      { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
      { title: '公司类型', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  budget_journal_type: {
    title: 'budgetJournal.journalTypeId' /*"预算日记账类型"*/,
    url: `${config.budgetUrl}/api/budget/journals/journalType/selectByInput`,
    searchForm: [
      {
        type: 'input',
        id: 'journalTypeCode',
        label: 'budgetJournal.journalTypeId.code' /*'预算日记账类型代码'*/,
      },
      {
        type: 'input',
        id: 'journalTypeName',
        label: 'budgetJournal.journalTypeId.name' /*'预算日记账类型名称'*/,
      },
    ],
    columns: [
      {
        title: 'budgetJournal.journalTypeId.code' /*'预算日记账类型代码'*/,
        dataIndex: 'journalTypeCode',
      },
      {
        title: 'budgetJournal.journalTypeId.name' /*'预算日记账类型名称'*/,
        dataIndex: 'journalTypeName',
      },
    ],
    key: 'id',
  },
  budget_versions: {
    title: 'budgetVersion.version' /*"预算版本"*/,
    url: `${config.budgetUrl}/api/budget/versions/query`,
    searchForm: [
      {
        type: 'input',
        id: 'versionCode',
        label: 'budgetVersion.versionCode' /*'预算版本代码'*/,
      },
      {
        type: 'input',
        id: 'versionName',
        label: 'budgetVersion.versionName' /*'预算版本名称'*/,
      },
    ],
    columns: [
      { title: 'budgetVersion.versionCode' /*'预算版本代码'*/, dataIndex: 'versionCode' },
      { title: 'budgetVersion.versionName' /*'预算版本名称'*/, dataIndex: 'versionName' },
    ],
    key: 'id',
  },
  budget_scenarios: {
    title: 'budgetJournal.scenarios' /*"预算场景"*/,
    url: `${config.budgetUrl}/api/budget/scenarios/query`,
    searchForm: [
      {
        type: 'input',
        id: 'scenarioCode',
        label: 'budget.scenarios.code' /*'预算场景代码'*/,
      },
      {
        type: 'input',
        id: 'scenarioName',
        label: 'budget.scenarios.name' /*'预算场景名称'*/,
      },
    ],
    columns: [
      { title: 'budget.scenarios.code' /*'预算场景代码'*/, dataIndex: 'scenarioCode' },
      { title: 'budget.scenarios.name' /*'预算场景名称'*/, dataIndex: 'scenarioName' },
    ],
    key: 'id',
  },
  budget_item_group: {
    title: 'chooser.data.budget_item_group',
    url: `${config.budgetUrl}/api/budget/groups/query`,
    searchForm: [
      { type: 'input', id: 'itemGroupCode', label: 'chooser.data.code' },
      { type: 'input', id: 'itemGroupName', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'itemGroupCode' },
      { title: 'chooser.data.name', dataIndex: 'itemGroupName' },
    ],
    key: 'id',
  },
  currency: {
    title: 'chooser.data.currency',
    url: `${config.mdataUrl}/api/currency/rate/list`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.currencyName', dataIndex: 'currencyName' },
      { title: 'chooser.data.code', dataIndex: 'currencyCode' },
      { title: 'chooser.data.exchangeRate', dataIndex: 'rate' },
    ],
    key: 'currencyCode',
    listKey: 'rows',
  },
  company_structure: {
    title: 'chooser.data.company',
    url: `${config.budgetUrl}/api/budget/structure/assign/companies/filter`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: 'chooser.data.companyCode' },
      { type: 'input', id: 'companyName', label: 'chooser.data.companyName' },
      { type: 'input', id: 'companyCodeFrom', label: 'chooser.data.companyCode.from' },
      { type: 'input', id: 'companyCodeTo', label: 'chooser.data.companyCode.to' },
    ],
    columns: [
      { title: 'chooser.data.companyCode', dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName', dataIndex: 'name' },
      { title: 'chooser.data.companyType', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  auto_audit_add_company: {
    title: '添加公司',
    url: `${config.mdataUrl}/api/company/by/term`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: '公司代码' },
      { type: 'input', id: 'name', label: '公司名称' },
      {
        type: 'select',
        options: [],
        id: 'legalEntityId',
        label: 'value.list.employee.legal.entity' /*"法人实体"*/,
        getUrl: `${config.mdataUrl}/api/all/legalentitys`,
        labelKey: 'entityName',
        valueKey: 'id',
        method: 'get',
        renderOption: option => `${option.entityName}`,
      },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
      { title: '公司类型', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  company_item: {
    title: 'chooser.data.company',
    url: `${config.budgetUrl}/api/budget/item/companies/query/filter`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: 'chooser.data.companyCode' },
      { type: 'input', id: 'companyName', label: 'chooser.data.companyName' },
      { type: 'input', id: 'companyCodeFrom', label: 'chooser.data.companyCode.from' },
      { type: 'input', id: 'companyCodeTo', label: 'chooser.data.companyCode.to' },
    ],
    columns: [
      { title: 'chooser.data.companyCode', dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName', dataIndex: 'name' },
      { title: 'chooser.data.companyType', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  company_group: {
    title: 'chooser.data.company_group',
    url: `${config.mdataUrl}/api/company/group/query/section/dto`,
    searchForm: [
      { type: 'input', id: 'companyGroupCode', label: 'chooser.data.code' },
      { type: 'input', id: 'companyGroupName', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'companyGroupCode' },
      { title: 'chooser.data.name', dataIndex: 'companyGroupName' },
    ],
    key: 'id',
  },
  deptCode: {
    title: 'chooser.data.dep.title', //部门
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
    searchForm: [
      {
        type: 'input',
        id: 'deptCode',
        label: 'chooser.data.dep.num', //部门编码
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'name',
        label: 'chooser.data.dep', //部门名称
        defaultValue: '',
      },
    ],
    columns: [
      {
        title: 'chooser.data.dep.num', //部门编码
        dataIndex: 'departmentCode',
        render: value => {
          return (
            <Popover placement="topLeft" content={value}>
              {value}
            </Popover>
          );
        },
      },
      {
        title: 'chooser.data.dep', //部门名称
        dataIndex: 'name',
        render: (value, record) => {
          //之前洪阳林这么加了一句：record.name = record.path && React.Component.prototype.checkFunctionProfiles('department.full.path.disabled', [undefined, false]) ? 。。。。。
          //我实在看不懂，我就先去掉 record.name = record.path，解决部门列表选择bug
          return (
            <Popover placement="topLeft" content={record.name}>
              {record.name}
            </Popover>
          );
        },
      },
    ],
    key: 'departmentId',
  },
  department: {
    title: 'chooser.data.dep.title', //部门
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
    searchForm: [
      {
        type: 'input',
        id: 'deptCode',
        label: 'chooser.data.dep.num', //部门编码
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'name',
        label: 'chooser.data.dep', //部门名称
        defaultValue: '',
      },
    ],
    columns: [
      {
        title: 'chooser.data.dep.num', //部门编码
        dataIndex: 'departmentCode',
        render: value => {
          return (
            <Popover placement="topLeft" content={value}>
              {value}
            </Popover>
          );
        },
      },
      {
        title: 'chooser.data.dep', //部门名称
        dataIndex: 'name',
        render: (value, record) => {
          //之前洪阳林这么加了一句：record.name = record.path && React.Component.prototype.checkFunctionProfiles('department.full.path.disabled', [undefined, false]) ? 。。。。。
          //我实在看不懂，我就先去掉 record.name = record.path，解决部门列表选择bug
          return (
            <Popover placement="topLeft" content={record.name}>
              {record.name}
            </Popover>
          );
        },
      },
    ],
    key: 'departmentOid',
  },
  department_group: {
    title: 'chooser.data.department_group',
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDepartmentGroupByInput`,
    searchForm: [
      {
        type: 'input',
        id: 'deptGroupCode',
        label: 'chooser.data.code',
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'description',
        label: 'chooser.data.description',
        defaultValue: '',
      },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'deptGroupCode' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'id',
  },
  version_company: {
    title: 'chooser.data.company',
    url: `${config.budgetUrl}/api/budget/version/assign/companies/query/filter`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.companyCode' },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' },
      { type: 'input', id: 'companyCodeFrom', label: 'chooser.data.companyCode.form' },
      { type: 'input', id: 'companyCodeTo', label: 'chooser.data.companyCode.to' },
    ],
    columns: [
      { title: 'chooser.data.companyCode', dataIndex: 'code' },
      { title: 'chooser.data.companyName', dataIndex: 'name' },
      { title: 'chooser.data.description', dataIndex: 'description' },
    ],
    key: 'id',
  },
  company: {
    title: 'chooser.data.company' /*选择公司*/,
    url: `${config.mdataUrl}/api/company/by/condition`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
      {
        type: 'input',
        id: 'companyCodeFrom',
        label: 'chooser.data.companyCode.from' /*公司代码从*/,
      },
      {
        type: 'input',
        id: 'companyCodeTo',
        label: 'chooser.data.companyCode.to' /*公司代码至*/,
      },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      { title: 'chooser.data.companyType' /*公司类型*/, dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  company_budget: {
    title: 'chooser.data.company' /*选择公司*/,
    url: `${config.mdataUrl}/api/company/by/condition`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
    ],
    key: 'id',
  },
  cost_center_item_by_id: {
    title: 'chooser.data.dimension', //维度
    url: `${config.mdataUrl}/api/dimension/item/page/by/dimensionId`,
    searchForm: [
      {
        type: 'input',
        id: 'dimensionItemCode',
        label: `${'chooser.data.dimension.code'}`,
      },
      {
        type: 'input',
        id: 'dimensionItemName',
        label: `${'chooser.data.dimension.name'}`,
      },
    ],
    columns: [
      {
        title: 'chooser.data.dimension.code', //维度代码
        dataIndex: 'dimensionItemCode',
      },
      {
        title: 'chooser.data.dimension.name', //维度名称
        dataIndex: 'dimensionItemName',
      },
    ],
    key: 'id',
  },
  cost_center_item: {
    title: 'chooser.data.dimension', //维度
    url: `${config.mdataUrl}/api/dimension/item/page/by/cond`,
    //成本中心名称"成本中心项名称／经理／编号
    searchForm: [
      {
        type: 'input',
        id: 'dimensionItemCode',
        label: `${'chooser.data.dimension.code'}`,
      },
      {
        type: 'input',
        id: 'dimensionItemName',
        label: `${'chooser.data.dimension.name'}`,
      },
    ],
    columns: [
      {
        title: 'chooser.data.dimension.code', //维度代码
        dataIndex: 'dimensionItemCode',
      },
      {
        title: 'chooser.data.dimension.name', //维度名称
        dataIndex: 'dimensionItemName',
      },
    ],
    key: 'dimensionItemId',
  },
  expense_cost_center_item: {
    //费用分摊用成本中心
    title: 'chooser.data.dimension', //成本中心
    url: `${config.mdataUrl}/api/dimension/item/page/by/cond`,
    //成本中心名称"成本中心项名称／经理／编号
    searchForm: [
      {
        type: 'input',
        id: 'dimensionItemCode',
        label: `${'chooser.data.dimension.code'}`,
      },
      {
        type: 'input',
        id: 'dimensionItemName',
        label: `${'chooser.data.dimension.name'}`,
      },
    ],
    columns: [
      {
        title: 'chooser.data.dimension.code', //成本中心代码
        dataIndex: 'dimensionItemCode',
      },
      {
        title: 'chooser.data.dimension.name', //成本中心名称
        dataIndex: 'dimensionItemName',
      },
    ],
    key: 'id',
  },
  cost_center: {
    title: 'chooser.data.dimension', //成本中心
    url: `${config.mdataUrl}/api/dimension/item/page/by/cond`,
    searchForm: [
      {
        type: 'input',
        id: 'dimensionItemName',
        label: 'chooser.data.dimension.name',
        defaultValue: '',
      },
    ],
    columns: [
      {
        title: 'chooser.data.dimension.code', //成本中心代码
        dataIndex: 'dimensionItemCode',
      },
      {
        title: 'chooser.data.dimension.name', //成本中心名称
        dataIndex: 'dimensionItemName',
      },
    ],
    key: 'id',
  },
  journal_line_department: {
    title: 'chooser.data.dep.title', //选择部门
    url: `${config.mdataUrl}/api//department/tenant/all`,
    searchForm: [
      {
        type: 'input',
        id: 'code',
        label: 'chooser.data.dep.code', //部门代码
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'name',
        label: 'chooser.data.dep', //部门名称
        defaultValue: '',
      },
    ],
    columns: [
      {
        title: 'chooser.data.dep.code', //部门代码
        dataIndex: 'departmentCode',
      },
      {
        title: 'chooser.data.dep', //部门名称
        dataIndex: 'name',
      },
    ],
    key: 'id',
    //listKey: 'records',
  },
  department_budget: {
    title: 'chooser.data.dep.title', //选择部门
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
    searchForm: [
      {
        type: 'input',
        id: 'departmentCode',
        label: 'chooser.data.dep.num',
        defaultValue: '',
      },
      { type: 'input', id: 'name', label: 'chooser.data.dep', defaultValue: '' },
    ],
    columns: [
      { title: 'chooser.data.dep.num', dataIndex: 'departmentCode' },
      { title: 'chooser.data.dep', dataIndex: 'name' },
    ],
    key: 'departmentId',
  },
  cash_flow_item: {
    title: 'chooser.data.cash_flow_item',
    url: `${config.payUrl}/api/cash/flow/items/query`,
    searchForm: [
      { type: 'input', id: 'flowCode', label: 'chooser.data.code', defaultValue: '' },
      { type: 'input', id: 'description', label: 'chooser.data.name', defaultValue: '' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'flowCode' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'id',
  },
  cash_flow_item_no_save: {
    title: '现金流量项',
    url: `${config.payUrl}/api/cash/default/flowitems/queryNotSaveFlowItem`,
    searchForm: [
      { type: 'input', id: 'flowCode', label: '	现金流量项代码', defaultValue: '' },
      { type: 'input', id: 'description', label: '现金流量项名称', defaultValue: '' },
    ],
    columns: [
      { title: '现金流量项代码', dataIndex: 'flowCode' },
      { title: '现金流量项名称', dataIndex: 'description' },
    ],
    key: 'id',
  },
  'assign-transaction': {
    title: 'chooser.data.assign-transaction',
    url: `${config.payUrl}/api/cash/transaction/classes/query`,
    searchForm: [
      {
        type: 'input',
        id: 'setOfBookId',
        label: 'chooser.data.setOfBooks',
        defaultValue: '',
      },
      { type: 'input', id: 'classCode', label: 'chooser.data.transaction.code' },
      {
        type: 'input',
        id: 'description',
        label: 'chooser.data.cash.flow.item.description',
      },
    ],
    columns: [
      { title: 'chooser.data.setOfBooks', dataIndex: 'setOfBookId' },
      { title: 'chooser.data.transaction.type', dataIndex: 'typeCode' },
      { title: 'chooser.data.transaction.code', dataIndex: 'classCode' },
      { title: 'chooser.data.cash.flow.item.description', dataIndex: 'description' },
    ],
    key: 'id',
  },
  journal_item: {
    title: 'budget.balance.item' /*'预算项目'*/,
    url: `${config.budgetUrl}/api/budget/journals/selectItemsByJournalTypeAndCompany`,
    searchForm: [
      {
        type: 'input',
        id: 'itemCode',
        label: 'budget.itemCode' /*"预算项目代码",*/,
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'itemName',
        label: 'budget.itemName' /*"预算项目名称"*/,
        defaultValue: '',
      },
    ],
    columns: [
      { title: 'budget.itemCode' /*"预算项目代码",*/, dataIndex: 'itemCode' },
      { title: 'budget.itemName' /*"预算项目名称"*/, dataIndex: 'itemName' },
    ],
    key: 'id',
  },
  user_budget: {
    title: 'chooser.data.selectPerson',
    url: `${config.mdataUrl}/api/select/user/by/name/or/code`,
    searchForm: [
      { type: 'input', id: 'keyword', label: 'chooser.data.employeeID' },
      { type: 'input', id: 'fullName', label: 'chooser.data.fullName' },
    ],
    columns: [
      { title: 'chooser.data.employeeID', dataIndex: 'employeeId', width: '25%' },
      { title: 'chooser.data.fullName', dataIndex: 'fullName', width: '75%' },
    ],
    key: 'id',
  },
  budget_structure: {
    title: 'chooser.data.budget_structure',
    url: `${config.budgetUrl}/api/budget/structures/query`,
    searchForm: [
      {
        type: 'input',
        id: 'structureCode',
        label: 'chooser.data.code',
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'structureName',
        label: 'chooser.data.name',
        defaultValue: '',
      },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'structureCode' },
      { title: 'chooser.data.name', dataIndex: 'structureName' },
    ],
    key: 'id',
  },
  pre_payment_type: {
    title: 'chooser.data.pre_payment_type',
    url: `${config.prePaymentUrl}/api/cash/pay/requisition/types/query`,
    searchForm: [
      { type: 'input', id: 'typeCode', label: 'chooser.data.code', defaultValue: '' },
      { type: 'input', id: 'typeName', label: 'chooser.data.name', defaultValue: '' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'typeCode' },
      { title: 'chooser.data.name', dataIndex: 'typeName' },
    ],
    key: 'id',
  },
  bank_account: {
    title: 'chooser.data.select.bank',
    url: `${config.baseUrl}/api/cash/bank/user/defineds/query`,
    searchForm: [
      { type: 'input', id: 'keyword', label: 'chooser.data.bankName', defaultValue: '' },
    ],
    columns: [
      { title: 'chooser.data.bankName', dataIndex: 'bankName' },
      { title: 'chooser.data.country', dataIndex: 'countryName' },
      { title: 'chooser.data.city', dataIndex: 'cityName' },
      { title: 'chooser.data.bank.address', dataIndex: 'address' },
    ],
    key: 'id',
  },
  select_authorization_user: {
    title: 'chooser.data.selectPerson',
    url: `${config.mdataUrl}/api/DepartmentGroup/get/users/by/department/and/company`,
    searchForm: [
      {
        type: 'input',
        id: 'companyName',
        label: 'chooser.data.companyName',
        defaultValue: '',
      },
      //  { type: 'input', id: 'departmentId', label: ('chooser.data.dep'), defaultValue: '' },
      { type: 'input', id: 'userCode', label: 'chooser.data.employeeID', defaultValue: '' },
      { type: 'input', id: 'userName', label: 'chooser.data.fullName', defaultValue: '' },
    ],
    columns: [
      { title: 'chooser.data.companyName', dataIndex: 'companyName' },
      { title: 'chooser.data.employeeID', dataIndex: 'userCode' },
      { title: 'chooser.data.fullName', dataIndex: 'userName' },
    ],
    key: 'userId',
  },
  year: {
    title: 'chooser.data.year',
    url: `${config.mdataUrl}/api/periods/select/years/by/setOfBooksId`,
    searchForm: [],
    columns: [
      {
        title: 'chooser.data.year',
        dataIndex: 'periodYear',
        //render: (desc, record) => record.key || record,
      },
    ],
    key: 'periodYear',
    //isValue: true,
  },
  period: {
    title: 'chooser.data.period',
    searchForm: [],
    url: `${config.mdataUrl}/api/periods/query/open/periods/by/setOfBook/id`,
    columns: [{ title: 'chooser.data.period', dataIndex: 'periodName' }],
    key: 'id',
  },
  quarter: {
    title: 'chooser.data.quarter',
    searchForm: [],
    url: `${config.baseUrl}/api/custom/enumerations/template/by/type`,
    columns: [{ title: 'chooser.data.quarter', dataIndex: 'name' }],
    key: 'id',
  },
  select_supplier_employee: {
    title: '选择收款方',
    url: `${config.prePaymentUrl}/api/cash/prepayment/requisitionHead/getReceivablesByName`,
    searchForm: [{ type: 'input', id: 'name', label: '供应商或员工', defaultValue: '' }],
    columns: [
      { title: '代码', dataIndex: 'code' },
      { title: '名称', dataIndex: 'name' },
      {
        title: '类型',
        dataIndex: 'isEmp',
        render: value => {
          return <span>{value ? '员工' : '供应商'}</span>;
        },
      },
    ],
    key: 'code',
  },
  select_vendor: {
    title: '供应商',
    url: `${config.mdataUrl}/api/ven/info`,
    searchForm: [
      { type: 'input', id: 'venderCode', label: '供应商代码', defaultValue: '' },
      { type: 'input', id: 'venNickname', label: '供应商名称', defaultValue: '' },
    ],
    columns: [
      { title: '供应商代码', dataIndex: 'venderCode' },
      { title: '供应商名称', dataIndex: 'venNickname' },
      { title: '供应商类型', dataIndex: 'venderTypeName' },
    ],
    key: 'code',
  },
  select_application_reimburse: {
    title: '选择申请单',
    url: `${config.expenseUrl}/api/expense/application/associated/by/prepayment`,
    searchForm: [
      { label: '申请单编号', id: 'applicationNumber', type: 'input' },
      { label: '申请单类型', id: 'applicationType', type: 'input' },
    ],
    columns: [
      {
        title: '申请单号',
        dataIndex: 'applicationNumber',
        align: 'center',
        render: text => (
          <span>
            {text ? (
              <Popover placement="topLeft" content={text}>
                {text}
              </Popover>
            ) : (
              '-'
            )}
          </span>
        ),
      },
      {
        title: '申请单类型',
        dataIndex: 'applicationType',
        align: 'center',
        render: text => (
          <span>
            {text ? (
              <Popover placement="topLeft" content={text}>
                {text}
              </Popover>
            ) : (
              '-'
            )}
          </span>
        ),
      },
      {
        title: '提交时间',
        align: 'center',
        dataIndex: 'requisitionDate',
        render: value => (
          <span>
            {value ? (
              <Popover placement="topLeft" content={moment(value).format('YYYY-MM-DD')}>
                {moment(value).format('YYYY-MM-DD')}
              </Popover>
            ) : (
              '-'
            )}
          </span>
        ),
      },
      {
        title: '币种',
        dataIndex: 'currencyCode',
        align: 'center',
        render: text => (
          <span>
            {text ? (
              <Popover placement="topLeft" content={text}>
                {text}
              </Popover>
            ) : (
              '-'
            )}
          </span>
        ),
      },
      {
        title: '总金额',
        align: 'center',
        dataIndex: 'amount',
        render: text => (
          <span>
            {text ? (
              <Popover placement="topLeft" content={formatMoney(text)}>
                {formatMoney(text)}
              </Popover>
            ) : (
              formatMoney(0)
            )}
          </span>
        ),
      },
      {
        title: '已关联金额',
        dataIndex: 'associatedAmount',
        align: 'center',
        render: text => (
          <span>
            {text ? (
              <Popover placement="topLeft" content={formatMoney(text)}>
                {formatMoney(text)}
              </Popover>
            ) : (
              formatMoney(0)
            )}
          </span>
        ),
      },
      {
        title: '可关联金额',
        dataIndex: 'associableAmount',
        align: 'right',
        render: text => (
          <span>
            {text ? (
              <Popover placement="topLeft" content={formatMoney(text)}>
                {formatMoney(text)}
              </Popover>
            ) : (
              formatMoney(0)
            )}
          </span>
        ),
      },
      {
        title: '备注',
        dataIndex: 'remarks',
        align: 'center',
        render: text => (
          <span>
            {text ? (
              <Popover placement="topLeft" content={text}>
                {text}
              </Popover>
            ) : (
              '-'
            )}
          </span>
        ),
      },
    ],
    key: 'applicationId',
  },
  section: {
    title: 'chooser.data.section',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/segments/page`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  contract_type: {
    title: 'chooser.data.contract_type',
    url: `${config.contractUrl}/api/contract/type/contract/type/by/company`,
    searchForm: [
      { type: 'input', id: 'contractTypeCode', label: 'chooser.data.code' },
      { type: 'input', id: 'contractTypeName', label: 'chooser.data.name' },
      {
        type: 'value_list',
        id: 'contractCategory',
        label: 'chooser.data.contract.category',
        options: [],
        valueListCode: 2202,
      },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'contractTypeCode' },
      { title: 'chooser.data.name', dataIndex: 'contractTypeName' },
      { title: 'chooser.data.contract.category', dataIndex: 'contractCategoryName' },
    ],
    key: 'id',
  },
  source_transactions_data: {
    title: 'chooser.data.source_transactions_data',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/data/source`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  accounting_scenarios: {
    title: '添加核算场景',
    url: `${config.accountingUrl}/api/generalLedgerSceneMapping/select/unassigned/scene`,
    searchForm: [
      { type: 'input', id: 'glSceneCode', label: 'chooser.data.code' },
      { type: 'input', id: 'glSceneName', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'glSceneCode' },
      { title: 'chooser.data.name', dataIndex: 'glSceneName' },
    ],
    key: 'id',
  },
  requisition_type: {
    title: '关联表单类型',
    url: `${config.baseUrl}/api/custom/forms/company/my/available/all`,
    searchForm: [
      {
        type: 'input',
        id: 'formType',
        label: 'chooser.data.form.type',
        defaultValue: 107,
      },
      { type: 'input', id: 'sectionName', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'sectionCode' },
      { title: 'chooser.data.name', dataIndex: 'sectionName' },
    ],
    key: 'id',
  },
  select_application: {
    title: 'chooser.data.select_application',
    url: `${config.baseUrl}/api/applications/v3/search`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.submit.date', dataIndex: 'createdDate' },
      { title: 'chooser.data.reason', dataIndex: 'title' },
      { title: 'chooser.data.business.code', dataIndex: 'businessCode' },
      { title: 'chooser.data.total.amount', dataIndex: 'totalAmount' },
    ],
    key: 'id',
  },
  add_employee: {
    title: '按条件添加员工',
    url: `${config.mdataUrl}/api/users/search/company/term`,
    searchForm: [{ type: 'input', id: 'keyword', label: '姓名/工号/手机号/邮箱 ' }],
    columns: [
      { title: '工号', dataIndex: 'employeeID' },
      { title: '姓名', dataIndex: 'fullName' },
      { title: '法人实体', dataIndex: 'corporationName' },
      { title: '部门', dataIndex: 'department', render: value => value.name },
      { title: '职务', dataIndex: 'title', render: value => value || '-' },
      { title: '邮箱', dataIndex: 'email' },
    ],
    key: 'id',
  },
  //账套级来源事务
  sob_sourceTransaction: {
    title: 'chooser.data.sob_sourceTransaction',
    url: `${config.accountingUrl}/api/company/by/condition`,
    searchForm: [
      { type: 'input', id: 'sourceTransactionCode', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'sourceTransactionCode' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'id',
  },

  //系统级来源事务(未添加)
  sys_sourceTransaction: {
    title: 'chooser.data.sys_sourceTransaction',
    url: `${config.accountingUrl}/api/general/source/transactions/all/codeValue`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'name', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'name' },
    ],
    key: 'code',
  },
  sobLineModel: {
    title: 'chooser.data.sobLineModel',
    url: `${config.accountingUrl}/api/general/ledger/sob/journal/line/model/query/filter`,
    searchForm: [
      { type: 'input', id: 'journalLineModelCode', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'journalLineModelCode' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'id',
  },
  // 人员类型
  personTypeModel: {
    title: 'chooser.data.personType', //选择人员类型
    url: `${config.baseUrl}/api/custom/enumeration/system/by/type/condition`,
    searchForm: [
      {
        type: 'input',
        id: 'codeFrom',
        label: 'chooser.data.codeFrom', //编码从
      },
      {
        type: 'input',
        id: 'codeTo',
        label: 'chooser.data.codeTo', //编码至
      },
      {
        type: 'input',
        id: 'value',
        label: 'chooser.data.code', //编码
      },
    ],
    columns: [
      {
        title: 'chooser.data.code', //编码
        dataIndex: 'value',
        width: '40%',
      },
      {
        title: 'chooser.data.name', //名称
        dataIndex: 'name',
        width: '60%',
      },
    ],
    key: 'value',
  },
  // 人员职务
  personDutyModel: {
    title: 'chooser.data.personDuty', //选择人员职务
    url: `${config.baseUrl}/api/custom/enumeration/system/by/type/condition`,
    searchForm: [
      {
        type: 'input',
        id: 'codeFrom',
        label: 'chooser.data.codeFrom', //编码从
      },
      {
        type: 'input',
        id: 'codeTo',
        label: 'chooser.data.codeTo', //编码至
      },
      {
        type: 'input',
        id: 'value',
        label: 'chooser.data.code', //编码
      },
    ],
    columns: [
      {
        title: 'chooser.data.code', //编码
        dataIndex: 'value',
        width: '40%',
      },
      {
        title: 'chooser.data.name', //名称
        dataIndex: 'name',
        width: '60%',
      },
    ],
    key: 'value',
  },
  // 人员级别
  personRankModel: {
    title: 'chooser.data.personLevel', //选择人员级别
    url: `${config.baseUrl}/api/custom/enumeration/system/by/type/condition`,
    searchForm: [
      {
        type: 'input',
        id: 'codeFrom',
        label: 'chooser.data.codeFrom', //编码从
      },
      {
        type: 'input',
        id: 'codeTo',
        label: 'chooser.data.codeTo', //编码至
      },
      {
        type: 'input',
        id: 'value',
        label: 'chooser.data.code', //编码
      },
    ],
    columns: [
      {
        title: 'chooser.data.code', //编码
        dataIndex: 'value',
        width: '40%',
      },
      {
        title: 'chooser.data.name', //名称
        dataIndex: 'name',
        width: '60%',
      },
    ],
    key: 'value',
  },
  accounting_elements: {
    title: 'chooser.data.accounting_elements',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/account/elements/page`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.description' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  deploy_company: {
    title: 'chooser.data.distribute.company' /*分配公司*/,
    url: `${config.mdataUrl}/api/company/deploy/enumeration`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      {
        title: 'chooser.data.companyType' /*公司类型*/,
        dataIndex: 'companyTypeName',
        render: value => value || '-',
      },
    ],
    key: 'companyOid',
  },
  batch_deploy_company: {
    title: 'chooser.data.distribute.company' /*分配公司*/,
    url: `${config.mdataUrl}/api/company/batch/deploy/enumeration`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      {
        title: 'chooser.data.companyType' /*公司类型*/,
        dataIndex: 'companyTypeName',
        render: value => value || '-',
      },
    ],
    key: 'companyOid',
  },
  allotSetOfBookCompany: {
    title: 'chooser.data.distribute.company' /*分配公司*/,
    url: `${config.mdataUrl}/api/company/by/condition`,
    searchForm: [
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司编码*/,
      },
    ],
    columns: [
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      {
        title: 'common.column.status' /*状态*/,
        dataIndex: 'enabled',
        render: enable => (
          <Badge
            status={enable ? 'success' : 'error'}
            text={enable ? messages('common.status.enable') : messages('common.status.disable')}
          />
        ),
      },
    ],
    key: 'id',
  },
  selectInvoiceType: {
    title: 'itemMap.expenseType',
    url: `${config.baseUrl}/api/company/integration/expense/types/and/name`,
    searchForm: [{ type: 'input', id: 'name', label: 'itemMap.expenseTypeName' }],
    columns: [
      {
        title: 'itemMap.icon',
        dataIndex: 'iconURL',
        render: value => {
          return <img src={value} height="20" width="20" />;
        },
      },
      { title: 'itemMap.expenseTypeName', dataIndex: 'name' },
      {
        title: 'common.column.status',
        dataIndex: 'enabled',
        render: isEnabled => (
          <Badge
            status={isEnabled ? 'success' : 'error'}
            text={isEnabled ? messages('common.status.enable') : messages('common.status.disable')}
          />
        ),
      },
    ],
    key: 'id',
  },
  accounting_journalField: {
    title: 'chooser.data.accounting_journalField',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/account/page`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'id',
  },
  accounting_scene_elements_user: {
    title: 'chooser.data.accounting_scene_elements_user',
    url: `${config.accountingUrl}/api/account/general/ledger/scene/elements/query`,
    searchForm: [
      {
        type: 'input',
        id: 'input',
        label: `${'chooser.data.code'}/${'chooser.data.name'}`,
      },
    ],
    columns: [
      { title: 'chooser.data.accounting.elements.code', dataIndex: 'accountElementCode' },
      { title: 'chooser.data.accounting.elements.name', dataIndex: 'accountElementName' },
      {
        title: 'chooser.data.accounting.elements.property.code',
        dataIndex: 'mappingGroupCode',
      },
      {
        title: 'chooser.data.accounting.elements.property.name',
        dataIndex: 'mappingGroupName',
      },
    ],
    key: 'id',
  },
  accounting_scene_elements: {
    title: 'accounting.scenarios.elements',
    url: `${config.accountingUrl}/api/account/general/ledger/scene/elements/queryAll/page`,
    searchForm: [
      { type: 'input', id: 'accountElementCode', label: 'chooser.data.code' },
      { type: 'input', id: 'elementNature', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  segment_map: {
    title: '选择科目',
    url: `${config.mdataUrl}/api/accounts/query/accounts/setOfBooksId`,
    searchForm: [
      { type: 'input', id: 'accountCode', label: 'chooser.data.code' },
      { type: 'input', id: 'accountName', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'accountCode' },
      { title: 'chooser.data.name', dataIndex: 'accountName' },
    ],
    key: 'id',
  },
  'data-source-fields': {
    title: '根据来源事务代码获取来源事务数据结构下的明细字段',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/data/source/fields`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  'data-source-fields_dataRules': {
    title: 'accounting.source.sourceDatafile',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/data/source/fields`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  vendor_type: {
    title: '供应商类型',
    url: `${config.mdataUrl}/api/ven/type/query`,
    searchForm: [
      { type: 'input', id: 'code', label: '供应商类型代码' },
      { type: 'input', id: 'name', label: '供应商类型名称' },
    ],
    columns: [
      { title: '供应商类型代码', dataIndex: 'vendorTypeCode' },
      { title: '供应商类型名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
  sqlAPI: {
    title: '取至API',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/custom/methods`,
    searchForm: [
      { type: 'input', id: 'code', label: '代码' },
      { type: 'input', id: 'description', label: '名称' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'name' },
    ],
    key: 'code',
  },
  participants: {
    title: 'chooser.data.selectPerson',
    url: `${config.baseUrl}/api/application/participantsList`,
    searchForm: [{ type: 'input', id: 'keyword', label: 'chooser.data.employeeID.fullName' }],
    columns: [
      { title: 'chooser.data.fullName', dataIndex: 'fullName' },
      { title: 'chooser.data.employeeID', dataIndex: 'employeeID' },
      {
        title: 'chooser.data.dep',
        dataIndex: 'departmentName',
        render: value => value || '-',
      },
      { title: 'chooser.data.duty', dataIndex: 'title', render: value => value || '-' },
    ],
    key: 'id',
  },
  // 启用法人
  corporation_entity: {
    title: 'chooser.data.select.legal.entity',
    url: `${config.mdataUrl}/api/v2/my/company/receipted/invoices`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.legal.entity.name', dataIndex: 'companyName' },
      { title: 'chooser.data.bank', dataIndex: 'accountBank' },
    ],
    key: 'companyReceiptedOid',
  },
  // 所有法人实体
  corporation_entity_all: {
    title: 'chooser.data.select.legal.entity',
    url: `${config.baseUrl}/api/v2/all/company/receipted/invoices`,
    searchForm: [],
    columns: [
      {
        title: 'chooser.data.legal.entity.name',
        dataIndex: 'companyName',
        render: (value, record) => {
          return record.enable ? (
            value
          ) : (
            <span>
              {value}
              <span style={{ color: '#959595' }}>({'common.disabling'})</span>
            </span>
          );
        },
      },
      { title: 'chooser.data.bank', dataIndex: 'accountBank' },
    ],
    key: 'companyReceiptedOid',
  },
  deploy_company_by_carousel: {
    //公告信息分配公司用
    title: 'chooser.data.distribute.company' /*分配公司*/,
    url: `${config.mdataUrl}/api/company/deploy/carousel`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      {
        title: 'chooser.data.companyType' /*公司类型*/,
        dataIndex: 'companyTypeName',
        render: value => value || '-',
      },
    ],
    key: 'companyOid',
  },

  bank_card: {
    title: 'chooser.data.bank_card',
    url: `${config.mdataUrl}/api/contact/bank/account/enable`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.fullName', dataIndex: 'bankAccountName' },
      { title: 'chooser.data.bankName', dataIndex: 'bankName' },
      { title: 'chooser.data.bank.card.num', dataIndex: 'bankAccountNo' },
    ],
    key: 'contactBankAccountOid',
  },
  booker: {
    title: 'chooser.data.booker',
    url: `${config.baseUrl}/api/travel/booker/get/bookers`,
    searchForm: [{ type: 'input', id: 'name', label: 'chooser.data.fullName' }],
    columns: [
      { title: 'chooser.data.fullName', dataIndex: 'fullName' },
      { title: 'chooser.data.employeeID', dataIndex: 'employeeID' },
      {
        title: 'chooser.data.dep',
        dataIndex: 'departmentName',
        render: value => value || '-',
      },
      { title: 'chooser.data.duty', dataIndex: 'title', render: value => value || '-' },
    ],
    key: 'userOid',
  },
  my_request: {
    title: 'chooser.data.my.application' /*'我的申请单'*/,
    url: `${config.baseUrl}/api/applications/passed/search`,
    searchForm: [{ type: 'input', id: 'keyword', label: 'common.matter' /*事由*/ }],
    columns: [
      {
        title: 'my.contract.create.date' /*创建时间*/,
        dataIndex: 'createdDate',
        width: '15%',
        render: submittedDate => new Date(submittedDate).format('yyyy-MM-dd'),
      },
      { title: 'common.matter' /*事由*/, dataIndex: 'title', width: '30%' },
      {
        title: 'bookingManagement.businessCode' /*'申请单号'*/,
        dataIndex: 'businessCode',
        width: '20%',
      },
      {
        title: 'customField.base.amount' /*本币金额*/,
        dataIndex: 'totalAmount',
        width: '15%',
        render: (value, record) => {
          return `${record.currencyCode}${record.originCurrencyTotalAmount}`;
        },
      },
      {
        title: 'chooser.data.my.relevantMemeber' /*'相关人员'*/,
        dataIndex: 'applicantName',
        width: '20%',
        render: (value, record) => {
          let applicationParticipants = [];
          record.applicationParticipants.map(item => {
            applicationParticipants.push(item.fullName);
          });
          applicationParticipants.indexOf(record.applicantName) &&
            applicationParticipants.push(record.applicantName);
          return applicationParticipants.join(',');
        },
      },
    ],
    key: 'applicationOid',
  },
  //这个包含新老集团的：老集团是法人实体，新集团是公司，但是后端返回的结构都是一样，前端统一按照公司处理
  //todo
  //后端还在做功能keyword关键字查询公司
  //目前返回的列表每一个对象只有两个字段，companyOid与companyName
  //后端还在重构添加companyCode,companyID等字段，如果法人实体没有这个字段，就返回null
  all_company_with_legal_entity: {
    title: 'chooser.data.company', //选择公司
    url: `${config.mdataUrl}/api/company/name/oid/by/tenant`,
    searchForm: [
      {
        type: 'input',
        id: 'keyword',
        label: 'chooser.data.companyName', //公司名称
      },
    ],
    columns: [
      {
        title: 'chooser.data.companyName', //公司名称
        dataIndex: 'companyName',
      },
    ],
    key: 'companyOid',
  },
  //这个尽量使用all_company_with_legal_entity去代替，all_company，不要用这个了
  //如果发现all_company_with_legal_entity列表字段缺少，就叫后端立即开发
  //之后这个接口就删除了
  all_company: {
    title: 'chooser.data.tenant.company', //选择集团下的所有公司
    url: `${config.mdataUrl}/api/company/all`,
    searchForm: [],
    columns: [
      {
        title: 'chooser.data.companyName', //公司名称
        dataIndex: 'name',
      },
      {
        title: 'chooser.data.baseCurrency', //币种
        dataIndex: 'baseCurrency',
      },
    ],
    key: 'companyOid',
  },
  company_bank_account: {
    title: 'chooser.data.company_bank_account',
    url: `${config.payUrl}/api/companyBankAuth/get/own/info`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.account.name', dataIndex: 'bankAccountName' },
      { title: 'chooser.data.account.num', dataIndex: 'bankAccountNumber' },
      { title: 'chooser.data.bankName', dataIndex: 'bankName' },
    ],
    key: 'companyBank.bankAccountNumber',
  },
  enabled_company: {
    title: 'chooser.data.tenant.company', //选择集团下的所有公司
    url: `${config.mdataUrl}/api/company/by/tenant`,
    searchForm: [],
    columns: [
      {
        title: 'chooser.data.companyName', //公司名称
        dataIndex: 'name',
      },
      {
        title: 'chooser.data.baseCurrency', //币种
        dataIndex: 'baseCurrency',
      },
    ],
    key: 'companyOid',
  },
  payment_type: {
    title: 'payment.batch.company.payWay' /*付款方式*/,
    url: `${config.payUrl}/api/cash/payment/method/query/lov`,
    searchForm: [],
    columns: [
      { title: 'payment.batch.company.payWay' /*'付款方式'*/, dataIndex: 'description' },
      {
        title: 'payment.batch.company.payCode' /*'付款代码'*/,
        dataIndex: 'paymentMethodCode',
      },
      {
        title: 'payment.batch.company.payType' /*'付款类别'*/,
        dataIndex: 'paymentMethodCategory',
        render: text => constants.getTextByValue(text, 'paymentMethodCategory'),
      },
    ],
    key: 'paymentMethodCode',
  },
  //获取自定义银行以及通用银行
  select_bank: {
    title: 'chooser.data.select.bank', //选择银行
    url: `${config.mdataUrl}/api/bank/infos/search`,
    searchForm: [
      {
        type: 'input',
        id: 'bankBranchName',
        label: 'chooser.data.branchBankName', //支行名称
      },
      {
        type: 'input',
        id: 'bankCode',
        label: 'chooser.data.bankCode', //银行代码
      },
      {
        type: 'input',
        id: 'openAccount',
        label: 'chooser.data.bankAddress', //开户地
      },

      {
        type: 'input',
        id: 'countryCode',
        label: 'chooser.data.countryCode', //国家编码
      },
      {
        type: 'input',
        id: 'cityCode',
        label: 'chooser.data.cityCode', //城市编码
      },
      {
        type: 'input',
        id: 'swiftCode',
        label: 'chooser.data.swiftCode', //swift编码
      },

      // 默认查询全部启用的（大多数情况）
      // 还有两个参数可以额外传
      // { type: 'input', id: 'enable', label: '启用状态' },可以查询启用与禁用
      // { type: 'input', id: 'isAll', label: '是否查询所有' },可以返回全部启用与禁用的
    ],
    columns: [
      {
        title: 'chooser.data.branchBankName', //支行名称
        dataIndex: 'bankBranchName',
        render: (value, record) => {
          return (
            <Popover placement="topLeft" content={record.bankBranchName}>
              {record.bankBranchName}
            </Popover>
          );
        },
      },
      {
        title: 'chooser.data.bankName', //银行名称
        dataIndex: 'bankName',
        width: '40%',
      },
      {
        title: 'chooser.data.bankCode', //银行代码
        dataIndex: 'bankCode',
        width: 130,
      },
    ],
    key: 'bankCode',
  },
  select_bank_supplier: {
    title: 'chooser.data.select.bank', //选择银行
    url: `${config.mdataUrl}/api/bank/infos/search`,
    searchForm: [
      {
        type: 'input',
        id: 'bankBranchName',
        label: 'chooser.data.branchBankName', //支行名称
      },
      {
        type: 'input',
        id: 'bankCode',
        label: 'chooser.data.bankCode', //银行代码
      },
      {
        type: 'input',
        id: 'openAccount',
        label: 'chooser.data.bankAddress', //开户地
      },
      // 默认查询全部启用的（大多数情况）
      // 还有两个参数可以额外传
      // { type: 'input', id: 'enable', label: '启用状态' },可以查询启用与禁用
      // { type: 'input', id: 'isAll', label: '是否查询所有' },可以返回全部启用与禁用的
    ],
    columns: [
      {
        title: 'chooser.data.branchBankName', //支行名称
        dataIndex: 'bankBranchName',
        render: (value, record) => {
          return (
            <Popover placement="topLeft" content={record.bankBranchName}>
              {record.bankBranchName}
            </Popover>
          );
        },
      },
      {
        title: 'chooser.data.bankName', //银行名称
        dataIndex: 'bankName',
        width: '40%',
      },
      {
        title: 'chooser.data.bankCode', //银行代码
        dataIndex: 'bankCode',
        width: 130,
      },
    ],
    key: 'bankCode',
  },
  //科目表定义，添加子科目（未被添加到科目下的子科目）
  subjectSelectorItem: {
    title: 'subject.sub.subject' /*"子科目"*/,
    url: `${config.mdataUrl}/api/accounts/hierarchy/child/query`,
    searchForm: [
      { type: 'input', id: 'accountCode', label: 'subject.code' /*"科目代码"*/ },
      { type: 'input', id: 'accountName', label: 'subject.name' /*"科目名称"*/ },
    ],
    columns: [
      { title: 'subject.code' /*"科目代码"*/, dataIndex: 'accountCode' },
      { title: 'subject.name' /*"科目名称"*/, dataIndex: 'accountDesc' },
      { title: 'subject.type' /*"科目类型"*/, dataIndex: 'accountTypeName' },
    ],
    key: 'id',
  },
  expense_report_invoice: {
    title: 'chooser.data.my.select.expense' /*'选择费用'*/,
    url: `${config.baseUrl}/api/v2/invoices/currency`,
    searchForm: [],
    columns: [
      { title: 'common.expense.type' /*"费用类型"*/, dataIndex: 'expenseTypeName' },
      {
        title: 'common.date' /*"日期"*/,
        dataIndex: 'createdDate',
        render: createdDate => new Date(createdDate).format('yyyy-MM-dd'),
      },
      { title: 'common.currency' /*"币种"*/, dataIndex: 'invoiceCurrencyCode' },
      { title: 'common.amount' /*"金额"*/, dataIndex: 'amount' },
      { title: 'common.currency.rate' /*"汇率"*/, dataIndex: 'actualCurrencyRate' },
      {
        title: 'common.base.currency.amount' /*"本位币金额"*/,
        dataIndex: 'baseAmount',
        render: React.Component.prototype.filterMoney,
      },
      { title: 'common.comment' /*"备注"*/, dataIndex: 'comment' },
    ],
    key: 'invoiceOid',
  },
  //预算用的新版货币接口
  new_currency: {
    title: 'chooser.data.currency',
    url: `${config.mdataUrl}/api/currency/rate/list`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.currencyName', dataIndex: 'currencyName' },
      { title: 'chooser.data.code', dataIndex: 'currencyCode' },
      { title: 'chooser.data.exchangeRate', dataIndex: 'rate' },
    ],
    key: 'currencyRateOid',
    listKey: 'records',
  },
  //预算余额方案用的货币接口
  currency_budget: {
    title: 'chooser.data.currency',
    url: `${config.mdataUrl}/api/currency/rate/list`,
    searchForm: [
      {
        label: 'chooser.data.code',
        id: 'currencyCode',
        type: 'input',
      },
    ],
    columns: [
      { title: 'chooser.data.currencyName', dataIndex: 'currencyName' },
      { title: 'chooser.data.code', dataIndex: 'currencyCode' },
    ],
    key: 'currencyRateOid',
    listKey: 'records',
  },
  //预算余额方案定义用的货币接口
  base_currency: {
    title: 'chooser.data.currency',
    url: `${config.mdataUrl}/api/currency/rate/list`,
    searchForm: [],
    columns: [
      { title: 'chooser.data.currencyName', dataIndex: 'currencyName' },
      { title: 'chooser.data.code', dataIndex: 'currencyCode' },
      { title: 'chooser.data.exchangeRate', dataIndex: 'rate' },
    ],
    key: 'currencyCode',
    listKey: 'rows',
  },
  /*"批量分配公司"*/
  'batch-allot-company': {
    title: 'budget.item.batchCompany',
    url: `${config.mdataUrl}/api/company/deploy/levels`,
    searchForm: [
      {
        type: 'select',
        options: [],
        id: 'companyLevelId',
        label: 'company.maintain.company.companyLevelName' /*"公司级别"*/,
        getUrl: `${config.mdataUrl}/api/companyLevel/selectByTenantId`,
        labelKey: 'description',
        valueKey: 'id',
        method: 'get',
        renderOption: option => `${option.description}`,
      },
      {
        type: 'select',
        options: [],
        id: 'legalEntityId',
        label: 'value.list.employee.legal.entity' /*"法人实体"*/,
        getUrl: `${config.mdataUrl}/api/all/legalentitys`,
        labelKey: 'entityName',
        valueKey: 'id',
        method: 'get',
        renderOption: option => `${option.entityName}`,
      },
      {
        type: 'input',
        id: 'companyCode',
        label: 'value.list.company.code' /*"公司代码"*/,
      },
      { type: 'input', id: 'name', label: 'value.list.company.name' /*"公司名称"*/ },
      {
        type: 'input',
        id: 'companyCodeFrom',
        label: 'structure.companyCodeFrom' /*"公司代码从"*/,
      },
      {
        type: 'input',
        id: 'companyCodeTo',
        label: 'structure.companyCodeTo' /*"公司代码至"*/,
      },
    ],
    columns: [
      {
        title: 'value.list.company.code' /*"公司代码"*/,
        dataIndex: 'companyCode',
        render: text => {
          return (
            <Tooltip
              title={text}
              style={{ width: '100%' }}
              placement={'topLeft'}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              {text}
            </Tooltip>
          );
        },
      },
      {
        title: 'value.list.company.name' /*"公司名称"*/,
        dataIndex: 'name',
        render: text => {
          return (
            <Tooltip
              title={text}
              style={{ width: '100%' }}
              placement={'topLeft'}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              {text}
            </Tooltip>
          );
        },
      },
      {
        title: 'chooser.data.companyType' /*"公司类型"*/,
        dataIndex: 'companyTypeName',
        render: text => {
          return (
            <Tooltip
              title={text}
              style={{ width: '100%' }}
              placement={'topLeft'}
              getPopupContainer={triggerNode => triggerNode.parentNode}
            >
              {text}
            </Tooltip>
          );
        },
      },
    ],
    key: 'id',
  },
  /*  'bgtUser': {
      title: '选择人员',
    // /*"添加成本中心组"*/
  'add-cost-center-group': {
    title: 'dimension.group.connect.item' /*"关联成本中心项"*/,
    url: `${config.mdataUrl}/api/dimension/item/group/subDimensionItem/filter?size=10`,
    searchForm: [
      {
        type: 'select',
        options: [],
        id: 'id',
        label: 'chooser.data.dimension', //成本中心
        getUrl: `${config.mdataUrl}/api/dimension/page/by/cond?enabled=true`,
        labelKey: 'dimensionName',
        valueKey: 'id',
        method: 'get',
        renderOption: option => `${option.dimensionName}`,
      },
      {
        type: 'input',
        id: 'dimensionItemCodeStart',
        label: 'dimension.group.code.from' /*"成本中心项代码从"*/,
      },
      {
        type: 'input',
        id: 'dimensionItemCodeEnd',
        label: 'dimension.group.code.to' /*"成本中心项代码至"*/,
      },
      {
        type: 'input',
        id: 'dimensionItemNameOrCode',
        label: 'dimension.group.item.code.name' /*"成本中心名称/代码"*/,
      },
    ],
    columns: [
      {
        title: 'chooser.data.dimension.name',
        dataIndex: 'dimensionName',
        render: text => {
          return (
            <Tooltip
              title={text}
              style={{ width: '100%' }}
              placement={'topLeft'}
              overlayStyle={{ maxWidth: 200, whiteSpace: 'pre-wrap' }}
            >
              {text}
            </Tooltip>
          );
        },
      }, //成本中心名称
      {
        title: 'new.dimension.item.code',
        dataIndex: 'dimensionItemCode',
        render: text => {
          return (
            <Tooltip
              title={text}
              style={{ width: '100%' }}
              placement={'topLeft'}
              overlayStyle={{ maxWidth: 200, whiteSpace: 'pre-wrap' }}
            >
              {text}
            </Tooltip>
          );
        },
      }, //成本中心项代码
      {
        title: 'dimension.detail.name',
        dataIndex: 'dimensionItemName',
        render: text => {
          return (
            <Tooltip
              title={text}
              style={{ width: '100%' }}
              placement={'topLeft'}
              overlayStyle={{ maxWidth: 200, whiteSpace: 'pre-wrap' }}
            >
              {text}
            </Tooltip>
          );
        },
      }, //成本中心项名称
    ],
    key: 'id',
  },
  bgtUserOid: {
    title: 'chooser.data.selectPerson',
    url: `${config.mdataUrl}/api/select/user/by/name/or/code`,
    searchForm: [{ type: 'input', id: 'keyword', label: 'chooser.data.employeeID.fullName' }],
    columns: [
      { title: 'chooser.data.employeeID', dataIndex: 'employeeId', width: '25%' },
      { title: 'chooser.data.fullName', dataIndex: 'fullName', width: '25%' },
      { title: 'chooser.data.dep', dataIndex: 'departmentName', width: '25%' },
      // { title: '职务', dataIndex: 'title', width: '25%' },
    ],
    key: 'userOid',
  },
  bgtUser: {
    title: 'chooser.data.selectPerson',
    url: `${config.mdataUrl}/api/select/user/by/name/or/code`,
    searchForm: [{ type: 'input', id: 'keyword', label: 'chooser.data.employeeID.fullName' }],
    columns: [
      { title: 'chooser.data.employeeID', dataIndex: 'employeeId', width: '25%' },
      { title: 'chooser.data.fullName', dataIndex: 'fullName', width: '25%' },
      { title: 'chooser.data.dep', dataIndex: 'departmentName', width: '25%' },
      // { title: '职务', dataIndex: 'title', width: '25%' },
    ],
    key: 'id',
  },
  dimUser: {
    title: 'chooser.data.selectPerson',
    url: `${config.mdataUrl}/api/select/user/by/name/or/code`,
    searchForm: [{ type: 'input', id: 'keyword', label: 'chooser.data.employeeID.fullName' }],
    columns: [
      { title: 'chooser.data.employeeID', dataIndex: 'employeeId', width: '25%' },
      { title: 'chooser.data.fullName', dataIndex: 'fullName', width: '25%' },
      { title: 'chooser.data.dep', dataIndex: 'departmentName', width: '25%' },
      // { title: '职务', dataIndex: 'title', width: '25%' },
    ],
    key: 'contactId',
  },
  select_user: {
    title: 'chooser.data.selectPerson',
    url: `${config.mdataUrl}/api/select/user/by/name/or/code`,
    searchForm: [{ type: 'input', id: 'keyword', label: 'chooser.data.employeeID.fullName' }],
    columns: [
      { title: 'chooser.data.employeeID', dataIndex: 'employeeId', width: '25%' },
      { title: 'chooser.data.fullName', dataIndex: 'fullName', width: '25%' },
      { title: 'chooser.data.dep', dataIndex: 'departmentName', width: '25%' },
      // { title: '职务', dataIndex: 'title', width: '25%' },
    ],
    key: 'userOid',
  },
  //预算余额方案类型定义选择部门
  budget_department: {
    title: 'chooser.data.dep.title', //部门
    url: `${config.baseUrl}/api//department/tenant/all`,
    searchForm: [
      {
        type: 'input',
        id: 'deptCode',
        label: 'chooser.data.dep.num', //部门编码
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'name',
        label: 'chooser.data.dep', //部门名称
        defaultValue: '',
      },
    ],
    columns: [
      {
        title: 'chooser.data.dep.num', //部门编码
        dataIndex: 'departmentCode',
      },
      {
        title: 'chooser.data.dep', //部门名称
        dataIndex: 'name',
        render: (value, record) => (record.name = record.path && record.path),
      },
    ],
    key: 'id',
  },
  //预算余额方案类型定义选择部门
  contract_department: {
    title: 'chooser.data.dep.title', //部门
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
    searchForm: [
      {
        type: 'input',
        id: 'deptCode',
        label: 'chooser.data.dep.num', //部门编码
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'name',
        label: 'chooser.data.dep', //部门名称
        defaultValue: '',
      },
    ],
    columns: [
      {
        title: 'chooser.data.dep.num', //部门编码
        dataIndex: 'departmentCode',
      },
      {
        title: 'chooser.data.dep', //部门名称
        dataIndex: 'name',
        render: (value, record) => (record.path !== value ? record.path : value),
      },
    ],
    key: 'departmentId',
  },
  //供应商下未分配的公司
  vendor_company: {
    title: 'chooser.data.company',
    url: `${config.mdataUrl}/api/ven/info/assign/company/query/unassigned/company/by/cond`,
    searchForm: [
      {
        type: 'select',
        id: 'setOfBooksId',
        label: 'supplier.company.setOfBook',
        options: [],
        getUrl: `${config.mdataUrl}/api/setOfBooks/by/tenant`,
        getParams: { roleType: 'TENANT' },
        method: 'get',
        labelKey: 'setOfBooksName',
        valueKey: 'id',
      },
      {
        type: 'input',
        id: 'companyCode',
        label: 'value.list.company.code' /*"公司代码"*/,
      },
      {
        type: 'input',
        id: 'companyName',
        label: 'value.list.company.name' /*"公司名称"*/,
      },
    ],
    columns: [
      { title: 'value.list.company.code' /*"公司代码"*/, dataIndex: 'companyCode' },
      { title: 'value.list.company.name' /*"公司名称"*/, dataIndex: 'name' },
      { title: 'chooser.data.companyType' /*"公司类型"*/, dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  //供应商分配公司
  vendor_company_all: {
    title: 'chooser.data.company',
    url: `supplier/api/ven/info/assign/company/query/batch/unassigned/company/by/cond`,
    searchForm: [
      {
        type: 'select',
        id: 'setOfBooksId',
        label: 'supplier.company.setOfBook',
        options: [],
        getUrl: `${config.mdataUrl}/api/setOfBooks/by/tenant`,
        getParams: { roleType: 'TENANT' },
        method: 'get',
        labelKey: 'setOfBooksName',
        valueKey: 'id',
      },
      {
        type: 'input',
        id: 'companyCode',
        label: 'value.list.company.code' /*"公司代码"*/,
      },
      {
        type: 'input',
        id: 'companyName',
        label: 'value.list.company.name' /*"公司名称"*/,
      },
    ],
    columns: [
      { title: 'value.list.company.code' /*"公司代码"*/, dataIndex: 'companyCode' },
      { title: 'value.list.company.name' /*"公司名称"*/, dataIndex: 'name' },
      { title: 'chooser.data.companyType' /*"公司类型"*/, dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  //选择合同
  select_contract: {
    title: '选择合同',
    url: `${config.contractUrl}/api/contract/document/relations/associate/header/query`,
    searchForm: [
      {
        type: 'input',
        label: '合同编号',
        id: 'contractNumber',
      },
    ],
    columns: [
      { title: '合同编号', dataIndex: 'contractNumber' },
      { title: '合同类型', dataIndex: 'contractTypeName' },
      { title: '合同名称', dataIndex: 'contractName' },
      { title: '操作', dataIndex: 'id', align: 'center', render: value => <a>查看详情</a> },
    ],
    key: 'contractHeaderId',
  },
  'expense-adjust-type': {
    title: '费用维护类型',
    url: `${config.baseUrl}/api/expense/adjust/types/queryExpenseAdjustType`,
    searchForm: [
      { type: 'input', id: 'expAdjustTypeCode', label: '代码' },
      { type: 'input', id: 'expAdjustTypeName', label: '名称' },
    ],
    columns: [
      { title: '代码', dataIndex: 'expAdjustTypeCode' },
      { title: '名称', dataIndex: 'expAdjustTypeName' },
    ],
    key: 'id',
  },
  //供应商下未分配的公司
  select_company_reimburse: {
    title: '选择公司',
    url: `${config.mdataUrl}/api/company/dto/by/tenant`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: '公司代码' },
      { type: 'input', id: 'name', label: '公司名称' },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
  select_department_reimburse: {
    title: '部门',
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
    searchForm: [
      { type: 'input', id: 'deptCode', label: '部门号', defaultValue: '' },
      { type: 'input', id: 'name', label: '部门名称', defaultValue: '' },
    ],
    columns: [
      { title: '部门号', dataIndex: 'departmentCode' },
      { title: '部门名称', dataIndex: 'name' },
    ],
    key: 'departmentOid',
  },
  select_invoices: {
    title: '选择账本',
    url: `${config.expenseUrl}/api/expense/book/release`,
    searchForm: [
      { type: 'input', id: 'expenseTypeName', label: '费用类型' },
      { type: 'input', id: 'amountFrom', label: '金额从' },
      { type: 'input', id: 'amountTo', label: '金额至' },
    ],
    columns: [
      { title: '费用类型', dataIndex: 'expenseTypeName', align: 'center' },
      { title: '币种', dataIndex: 'currencyCode', align: 'center' },
      {
        title: '金额',
        dataIndex: 'amount',
        align: 'center',
        render: money => {
          money = Number(money || 0)
            .toFixed(2)
            .toString();
          let numberString = '';
          if (money.indexOf('.') > -1) {
            let integer = money.split('.')[0];
            let decimals = money.split('.')[1];
            numberString = integer.replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + '.' + decimals;
          } else {
            numberString = money.replace(/(\d)(?=(\d{3})+(?!\d))\./g, '$1,');
          }
          numberString += numberString.indexOf('.') > -1 ? '' : '.00';
          return <span className="money-cell">{numberString}</span>;
        },
      },
    ],
    key: 'id',
  },
  select_payee: {
    title: '选择收款方',
    url: `${config.prePaymentUrl}/api/cash/prepayment/requisitionHead/getReceivablesByName`,
    searchForm: [{ type: 'input', id: 'name', label: '名称', defaultValue: '' }],
    columns: [{ title: '代码', dataIndex: 'code' }, { title: '名称', dataIndex: 'name' }],
    key: 'id',
  },
  select_payee_name_code: {
    title: '选择收款方',
    url: `${config.mdataUrl}/api/contact/account/by/name/code`,
    searchForm: [
      { type: 'input', id: 'code', label: '代码', defaultValue: '' },
      { type: 'input', id: 'name', label: '名称', defaultValue: '' },
    ],
    columns: [{ title: '代码', dataIndex: 'code' }, { title: '名称', dataIndex: 'name' }],
    key: 'id',
  },
  select_employee: {
    title: '选择员工',
    url: `${config.mdataUrl}/api/contact/account/by/name/code`,
    searchForm: [{ type: 'input', id: 'name', label: '名称', defaultValue: '' }],
    columns: [{ title: '代码', dataIndex: 'code' }, { title: '名称', dataIndex: 'name' }],
    key: 'id',
  },
  select_ven: {
    title: '选择供应商',
    url: `${config.mdataUrl}/api/vendor/account/by/companyId/name/code`,
    searchForm: [{ type: 'input', id: 'name', label: '名称', defaultValue: '' }],
    columns: [{ title: '代码', dataIndex: 'code' }, { title: '名称', dataIndex: 'name' }],
    key: 'id',
  },
  select_returnee: {
    title: '选择退款方',
    url: `${config.prePaymentUrl}/api/cash/prepayment/requisitionHead/getReceivablesByNameAndCode`,
    searchForm: [
      { type: 'input', id: 'code', label: '代码' },
      { type: 'input', id: 'name', label: '名称' },
    ],
    columns: [{ title: '代码', dataIndex: 'code' }, { title: '名称', dataIndex: 'name' }],
    key: 'id',
  },
  select_department_contract: {
    title: '部门',
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
    searchForm: [
      { type: 'input', id: 'deptCode', label: '部门号', defaultValue: '' },
      { type: 'input', id: 'name', label: '部门名称', defaultValue: '' },
    ],
    columns: [
      { title: '部门号', dataIndex: 'departmentCode' },
      { title: '部门名称', dataIndex: 'name' },
    ],
    key: 'departmentId',
  },
  accounting_scene_data_elements: {
    title: '核算要素',
    url: `${
      config.accountingUrl
    }/api/general/ledger/journal/line/model/data/rules/query/fitler/element`,
    searchForm: [
      { type: 'input', id: 'accountElementCode', label: '核算要素代码' },
      { type: 'input', id: 'elementNature', label: '核算要素名称' },
    ],
    columns: [
      { title: '核算要素代码', dataIndex: 'code' },
      { title: '核算要素名称', dataIndex: 'description' },
    ],
    key: 'code',
  },
  accounting_journalField_system: {
    title: '未选择过的核算分录段',
    url: `${config.accountingUrl}/api/accounting/util/general/ledger/fields/account/system/page`,
    searchForm: [
      { type: 'input', id: 'code', label: '分录段代码' },
      { type: 'input', id: 'description', label: '分录段名称' },
    ],
    columns: [
      { title: '分录段代码', dataIndex: 'code' },
      { title: '分录段名称', dataIndex: 'description' },
    ],
    key: 'code',
  },
  accounting_scene_data_elements_system: {
    title: '核算要素',
    url: `${
      config.accountingUrl
    }/api/general/ledger/journal/line/model/system/data/rules/query/fitler/element`,
    searchForm: [
      { type: 'input', id: 'accountElementCode', label: '核算要素代码' },
      { type: 'input', id: 'elementNature', label: '核算要素名称' },
    ],
    columns: [
      { title: '核算要素代码', dataIndex: 'code' },
      { title: '核算要素名称', dataIndex: 'description' },
    ],
    key: 'code',
  },
  adjust_expense_type: {
    title: 'itemMap.expenseType',
    url: `${config.expenseUrl}/api/expense/adjust/types/getExpenseType`,
    searchForm: [{ type: 'input', id: 'name', label: 'itemMap.expenseTypeName' }],
    columns: [
      {
        title: 'itemMap.icon',
        dataIndex: 'iconUrl',
        render: value => {
          return <img src={value} height="20" width="20" />;
        },
      },
      { title: 'itemMap.expenseTypeName', dataIndex: 'name' },
      {
        title: 'common.column.status',
        dataIndex: 'enabled',
        render: isEnabled => (
          <Badge
            status={isEnabled ? 'success' : 'error'}
            text={isEnabled ? messages('common.status.enable') : messages('common.status.disable')}
          />
        ),
      },
    ],
    key: 'id',
  },
  dimension_value: {
    title: '选择维值',
    url: `${config.mdataUrl}/api/dimension/item/page/enabled/date/item/by/dimensionId`,
    searchForm: [
      { type: 'input', id: 'dimensionItemCode', label: '维值代码' },
      { type: 'input', id: 'dimensionItemName', label: '维值名称' },
    ],
    columns: [
      { title: '代码', dataIndex: 'dimensionItemCode' },
      { title: '名称', dataIndex: 'dimensionItemName' },
    ],
    key: 'id',
  },
  //已启用维值,且按公司筛选
  dimension_value_enabled: {
    title: '选择维值',
    url: `${config.mdataUrl}/api/dimension/item/page/by/dimensionId/enabled/companyId`,
    searchForm: [
      { type: 'input', id: 'dimensionItemCode', label: '维值代码' },
      { type: 'input', id: 'dimensionItemName', label: '维值名称' },
    ],
    columns: [
      { title: '代码', dataIndex: 'dimensionItemCode' },
      { title: '名称', dataIndex: 'dimensionItemName' },
    ],
    key: 'id',
  },
  //乘机人
  passenger: {
    title: 'bookingManagement.bookingDetails.passenger',
    url: `${config.mdataUrl}/api/users/oids`,
    searchForm: [
      {
        type: 'input',
        id: 'fullName',
        label:
          'bookingManagement.bookingDetails.maintain.chooser.tips' /*"乘机人姓名，部门，职位"*/,
      },
    ],
    columns: [
      {
        title: 'bookingManagement.bookingDetails.maintain.chooser.avatar' /*"头像"*/,
        dataIndex: 'avatar',
        render: value => <Avatar src={value} />,
        width: '10%',
      },
      {
        title: 'bookingManagement.bookingDetails.maintain.chooser.name' /*"姓名"*/,
        dataIndex: 'fullName',
      },
      {
        title: 'bookingManagement.bookingDetails.maintain.chooser.dep' /*"部门"*/,
        dataIndex: 'departmentName',
      },
      {
        title: 'bookingManagement.bookingDetails.maintain.chooser.title' /*"职位"*/,
        dataIndex: 'title',
      },
    ],
    key: 'userOid',
  },
  department_document: {
    title: '部门', //部门
    url: `${config.mdataUrl}/api/DepartmentGroup/selectDept/enabled`,
    searchForm: [
      {
        type: 'input',
        id: 'deptCode',
        label: '部门编码', //部门编码
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'name',
        label: '部门名称', //部门名称
        defaultValue: '',
      },
    ],
    columns: [
      {
        title: 'chooser.data.dep.num', //部门编码
        dataIndex: 'departmentCode',
        render: value => {
          return (
            <Popover placement="topLeft" content={value}>
              {value}
            </Popover>
          );
        },
      },
      {
        title: 'chooser.data.dep', //部门名称
        dataIndex: 'name',
        render: (value, record) => {
          return (
            <Popover placement="topLeft" content={record.path}>
              {record.path}
            </Popover>
          );
        },
      },
    ],
    key: 'departmentId',
  },
  select_setOfBooks_accounts: {
    title: '选择科目',
    url: `${config.mdataUrl}/api/accounts/query/accounts/setOfBooksId`,
    searchForm: [
      { type: 'input', id: 'accountCode', label: '科目代码' },
      { type: 'input', id: 'accountName', label: '科目名称' },
    ],
    columns: [
      { title: '科目代码', dataIndex: 'accountCode' },
      { title: '科目名称', dataIndex: 'accountName' },
    ],
    key: 'id',
  },
  select_companies: {
    title: '选择公司', // 选择当前租户下的所有公司，展示账套
    url: `${config.mdataUrl}/api/company/name/setOfBooksId`,
    searchForm: [
      { type: 'input', id: 'keyword', label: '公司名称、代码' },
      {
        type: 'select',
        id: 'setOfBooksId',
        label: '所属账套',
        options: [],
        getUrl: `${config.mdataUrl}/api/setOfBooks/by/tenant`,
        method: 'get',
        labelKey: 'setOfBooksName',
        valueKey: 'id',
      } /*账套*/,
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
      { title: '所属账套', dataIndex: 'setOfBooksName' },
    ],
    key: 'id',
  },
  accounting_company: {
    title: 'chooser.data.company' /*选择公司*/,
    url: `${config.accountingUrl}/api/general/ledger/work/order/type/companies/query/company`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
      {
        type: 'input',
        id: 'companyCodeFrom',
        label: 'chooser.data.companyCode.from' /*公司代码从*/,
      },
      {
        type: 'input',
        id: 'companyCodeTo',
        label: 'chooser.data.companyCode.to' /*公司代码至*/,
      },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      { title: 'chooser.data.companyType' /*公司类型*/, dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  application_type: {
    title: '选择申请类型',
    url: `${config.expenseUrl}/api/expense/types/chooser/query?enabled=true`,
    searchForm: [
      {
        type: 'input',
        id: 'code',
        label: '申请类型代码',
      },
      { type: 'input', id: 'name', label: '申请类型名称' },
    ],
    columns: [
      {
        title: '图标',
        dataIndex: 'iconUrl',
        align: 'center',
        render: value => {
          return <img src={value} height="24" width="24" />;
        },
      },
      { title: '申请类型代码', dataIndex: 'code' },
      { title: '申请类型名称', dataIndex: 'name' },
      { title: '申请大类', dataIndex: 'typeCategoryName' },
    ],
    key: 'id',
  },
  //租户下启用的公司
  enableCompanyByTenant: {
    title: 'chooser.data.company' /*选择公司*/,
    url: `${config.mdataUrl}/api/company/dto/by/tenant`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      { title: 'chooser.data.companyType' /*公司类型*/, dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  //组织架构下责任中心配置的公司弹框
  responsibility_company: {
    title: '公司选择',
    url: `${config.mdataUrl}/api/company/query`,
    searchForm: [
      { type: 'input', id: 'keyword', label: '公司', placeholder: '请输入代码或名称' },
      { type: 'input', id: 'companyCodeFrom', label: 'chooser.data.companyCode.from' },
      { type: 'input', id: 'companyCodeTo', label: 'chooser.data.companyCode.to' },
    ],
    columns: [
      { title: 'chooser.data.companyCode', dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName', dataIndex: 'name' },
    ],
    key: 'id',
  },
  //组织架构下责任中心配置的默认责任中心弹框
  responsibility_default: {
    title: '默认责任中心',
    url: `${config.mdataUrl}/api/responsibilityCenter/query/default`,
    searchForm: [
      { type: 'input', id: 'info', label: '责任中心', placeholder: '请输入代码或名称' },
      { type: 'input', id: 'codeFrom', label: '责任中心代码从' },
      { type: 'input', id: 'codeTo', label: '责任中心代码至' },
    ],
    columns: [
      { title: '责任中心代码', dataIndex: 'responsibilityCenterCode' },
      { title: '责任中心名称', dataIndex: 'responsibilityCenterName' },
    ],
    key: 'id',
  },
  //获取账套下责任中心组
  responsibility_group: {
    title: '默认责任中心',
    url: `${config.mdataUrl}/api/responsibilityCenter/group/query?enabled=true`,
    searchForm: [
      { type: 'input', id: 'groupName', label: '责任组中心', placeholder: '请输入名称' },
      { type: 'input', id: 'groupCode', label: '责任组中心代码' },
    ],
    columns: [
      { title: '责任中心组代码', dataIndex: 'groupCode' },
      { title: '责任中心组名称', dataIndex: 'groupName' },
    ],
    key: 'id',
  },
  //组织架构下责任中心配置的可用责任中心弹框
  responsibility_usable: {
    title: '可用责任中心',
    url: `${config.mdataUrl}/api/responsibilityCenter/query/default`,
    searchForm: [
      { type: 'input', id: 'info', label: '责任中心', placeholder: '请输入代码或名称' },
      { type: 'input', id: 'codeFrom', label: '责任中心代码从' },
      { type: 'input', id: 'codeTo', label: '责任中心代码至' },
    ],
    columns: [
      { title: '责任中心代码', dataIndex: 'responsibilityCenterCode' },
      { title: '责任中心名称', dataIndex: 'responsibilityCenterName' },
    ],
    key: 'id',
  },
  //代理授权下个人授权弹框
  personal_authorization: {
    title: '选择人员',
    url: `${config.mdataUrl}/api/responsibilityCenter/query/default`,
    searchForm: [
      { type: 'input', id: 'info', label: '公司名称' },
      { type: 'input', id: 'companyCodeFrom', label: '员工工号' },
      { type: 'input', id: 'companyCodeTo', label: '姓名' },
    ],
    columns: [
      { title: '公司名称', dataIndex: 'responsibilityCenterCode' },
      { title: '员工工号', dataIndex: 'responsibilityCenterName' },
      { title: '姓名', dataIndex: 'responsibilityCenterName' },
    ],
    key: 'id',
  },
  //核算工单详情选择责任中心弹框
  responsibility_select: {
    title: '选择责任中心',
    url: `${config.accountingUrl}/api/general/ledger/work/order/types/res/center/query`,
    searchForm: [
      { type: 'input', id: 'info', label: '责任中心', placeholder: '请输入代码或名称' },
      { type: 'input', id: 'codeFrom', label: '责任中心代码从' },
      { type: 'input', id: 'codeTo', label: '责任中心代码至' },
    ],
    columns: [
      { title: '责任中心代码', dataIndex: 'responsibilityCenterCode' },
      { title: '责任中心名称', dataIndex: 'responsibilityCenterName' },
    ],
    key: 'id',
  },
  //选择页面
  select_pages: {
    title: '选择页面',
    url: `${config.baseUrl}/api/function/page/relation/filter`,
    searchForm: [{ type: 'input', id: 'pageName', label: '名称' }],
    columns: [
      { title: '名称', dataIndex: 'pageName' },
      { dataIndex: 'fullRouter', title: '路由' },
      { dataIndex: 'filePath', title: '组件地址' },
    ],
    key: 'id',
  },
  //分配过的页面
  allowed_pages: {
    title: '选择页面',
    url: `${config.baseUrl}/api/function/page/relation/query/by/cond`,
    searchForm: [{ type: 'input', id: 'pageName', label: '名称' }],
    columns: [{ title: '名称', dataIndex: 'pageName', align: 'center' }],
    key: 'id',
  },
  //选择功能
  select_function: {
    title: '选择功能',
    url: `${config.baseUrl}/api/content/function/relation/filter`,
    searchForm: [{ type: 'input', id: 'functionName', label: '名称' }],
    columns: [{ dataIndex: 'functionName', title: '名称' }],
    key: 'id',
  },
  //分配过的功能
  allowed_functions: {
    title: '选择功能',
    url: `${config.baseUrl}/api/content/function/relation/query/by/cond`,
    searchForm: [{ type: 'input', id: 'functionName', label: '名称' }],
    columns: [{ title: '名称', dataIndex: 'functionName', align: 'center' }],
    key: 'id',
  },
  //费用项目
  expense_item: {
    title: 'setting.key1504',
    url: `${config.expenseUrl}/api/expense/types/chooser/query`,
    searchForm: [{ type: 'input', id: 'name', label: '类型名称', placeholder: '请输入类型名称' }],
    columns: [
      { title: '分类', dataIndex: 'typeCategoryName' },
      {
        title: '类型名称',
        dataIndex: 'name',
        render: (desc, record) => (record ? record.name : ''),
      },
    ],
    key: 'id',
  },
  // 税务机关
  tax_authority: {
    title: '主管税务机关',
    url: `${config.taxUrl}/api/tax/department/taxDepartment/data`,
    searchForm: [
      {
        type: 'input',
        id: 'taxDepartmentCode',
        label: '税务机关代码',
      },
      {
        type: 'input',
        id: 'taxDepartment',
        label: '税务机关名称',
      },
    ],
    columns: [
      {
        title: '税务机关代码',
        dataIndex: 'taxDepartmentCode',
        width: '50%',
      },
      {
        title: '税务机关名称',
        dataIndex: 'taxDepartment',
        width: '50%',
      },
    ],
    key: 'taxDepartmentCode',
  },
  'data-source-fields_dataRules-new': {
    title: 'accounting.source.sourceDatafile',
    url: `${
      config.accountingUrl
    }/api/accounting/util/general/ledger/fields/data/source/fields/ignoreDimension`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  'data-source-fields_dataRules-sob-new': {
    title: 'accounting.source.sourceDatafile',
    url: `${
      config.accountingUrl
    }/api/accounting/util/general/ledger/fields/data/source/fields/ignoreDimension`,
    searchForm: [
      { type: 'input', id: 'code', label: 'chooser.data.code' },
      { type: 'input', id: 'description', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  accounting_scene_elements_new: {
    title: 'accounting.scenarios.elements',
    url: `${config.accountingUrl}/api/account/general/ledger/scene/elements/query/hasDataRule/page`,
    searchForm: [
      { type: 'input', id: 'accountElementCode', label: 'chooser.data.code' },
      { type: 'input', id: 'elementNature', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  accounting_scene_elements_sob_new: {
    title: 'accounting.scenarios.elements',
    url: `${config.accountingUrl}/api/account/general/ledger/scene/elements/query/hasDataRule/page`,
    searchForm: [
      { type: 'input', id: 'accountElementCode', label: 'chooser.data.code' },
      { type: 'input', id: 'elementNature', label: 'chooser.data.name' },
    ],
    columns: [
      { title: 'chooser.data.code', dataIndex: 'code' },
      { title: 'chooser.data.name', dataIndex: 'description' },
    ],
    key: 'code',
  },
  //差旅申请单新建行选择人员
  travel_line_user: {
    title: 'chooser.data.selectPerson',
    url: `${config.expenseUrl}/api/travel/associate/people`,
    searchForm: [
      {
        type: 'input',
        id: 'userCode',
        label: 'chooser.data.userCode',
        defaultValue: '',
      },
      {
        type: 'input',
        id: 'fullName',
        label: 'chooser.data.fullName',
        defaultValue: '',
      },
    ],
    columns: [
      { title: 'chooser.data.userCode', dataIndex: 'employeeCode' },
      { title: 'chooser.data.fullName', dataIndex: 'fullName' },
    ],
    key: 'id',
  },

  // 所属纳税主体
  tax_payer: {
    title: '所属纳税主体',
    url: `${config.taxUrl}/api/tax/taxRegister/basic/status/condition`,
    searchForm: [
      {
        type: 'input',
        id: 'parentTaxpayerId',
        label: '上级纳税主体代码',
      },
      {
        type: 'input',
        id: 'taxpayerName',
        label: '纳税主体名称',
      },
      {
        type: 'input',
        id: 'taxpayerNumber',
        label: '统一社会信用代码',
      },
    ],
    columns: [
      {
        title: '上级纳税主体代码',
        dataIndex: 'parentTaxpayerId',
        width: '50%',
      },
      {
        title: '上级纳税主体名称',
        dataIndex: 'parentTaxpayerName',
        width: '50%',
      },
    ],
    key: 'taxDepartmentCode',
  },
  tax_company: {
    title: '添加公司',
    url: `${config.taxUrl}/api/tax/taxRegister/basic/getCompany`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: '公司代码' },
      { type: 'input', id: 'name', label: '公司名称' },
      {
        type: 'select',
        options: [],
        id: 'legalEntityId',
        label: 'value.list.employee.legal.entity' /*"法人实体"*/,
        getUrl: `${config.mdataUrl}/api/all/legalentitys`,
        labelKey: 'entityName',
        valueKey: 'id',
        method: 'get',
        renderOption: option => `${option.entityName}`,
      },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
      { title: '公司类型', dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  //国家
  select_country: {
    title: 'bank.country',
    url: `${config.mdataUrl}/api/localization/query/country`,
    searchForm: [
      { type: 'input', id: 'countryCode', label: '国家代码', defaultValue: '' },
      { type: 'input', id: 'countryName', label: '国家名称', defaultValue: '' },
    ],
    columns: [
      { title: '国家代码', dataIndex: 'code' },
      { title: '国家名称', dataIndex: 'country' },
    ],
    key: 'code',
  },
  coding_rule_company: {
    title: 'chooser.data.company', //选择租户下的所有公司
    url: `${config.mdataUrl}/api/company/deploy/carousel`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: 'chooser.data.companyCode' },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' },
      { type: 'input', id: 'companyCodeFrom', label: 'chooser.data.companyCode.from' },
      { type: 'input', id: 'companyCodeTo', label: 'chooser.data.companyCode.to' },
    ],
    columns: [
      {
        title: 'chooser.data.companyCode', //公司名称
        dataIndex: 'companyCode',
      },
      {
        title: 'chooser.data.companyName', //公司名称
        dataIndex: 'name',
      },
    ],
    key: 'companyOid',
  },
  // 选择父项目申请单编号
  select_parent_project: {
    title: '选择父项目申请单编号',
    url: `${config.contractUrl}/api/project/requisition/query/parent/project/requisition`,
    searchForm: [
      { type: 'input', id: 'projectReqNumber', label: '项目申请单编号' },
      { type: 'input', id: 'projectName', label: '项目名称' },
    ],
    columns: [
      {
        title: '项目申请单编号',
        dataIndex: 'projectReqNumber',
      },
      {
        title: '项目名称',
        dataIndex: 'projectName',
      },
      {
        title: '项目负责人',
        dataIndex: 'pmName',
      },
      {
        title: '项目地',
        dataIndex: 'projectLocation',
      },
      {
        title: '项目类型',
        dataIndex: 'projectType',
      },
      {
        title: '项目说明',
        dataIndex: 'description',
      },
    ],
    key: 'id',
  },
  // 上级纳税主体
  parent_tax_payer: {
    title: '上级纳税主体',
    url: `${config.taxUrl}/api/tax/taxRegister/basic/status/condition`,
    searchForm: [
      { type: 'input', id: 'taxpayerNumber', label: '纳税人识别号' },
      { type: 'input', id: 'taxpayerName', label: '纳税人名称' },
    ],
    columns: [
      { title: '纳税人识别号', dataIndex: 'taxpayerNumber' },
      { title: '纳税人名称', dataIndex: 'taxpayerName' },
    ],
    key: 'id',
  },
  // 选择纳税主体
  // tax_payer_select: {
  //   title: '选择纳税主体',
  //   url: `${config.taxUrl}/api/tax/taxRegister/basic/status/condition`,
  //   searchForm: [
  //     {
  //       type: 'input',
  //       id: 'taxpayerName',
  //       label: '纳税主体名称',
  //     },
  //     {
  //       type: 'input',
  //       id: 'taxpayerNumber',
  //       label: '纳税人识别号',
  //     },
  //   ],
  //   columns: [
  //     {
  //       title: '纳税主体名称',
  //       dataIndex: 'taxpayerName',
  //       width: '50%',
  //     },
  //     {
  //       title: '纳税人识别号',
  //       dataIndex: 'taxpayerNumber',
  //       width: '50%',
  //     },
  //   ],
  //   key: 'taxDepartmentCode',
  // },
  // 请求参数
  select_requset_fields: {
    title: '请求参数',
    url: `${config.baseUrl}/api/interfaceRequest/query`,
    searchForm: [
      { type: 'input', id: 'keyCode', label: '字段' },
      { type: 'input', id: 'name', label: '名称' },
    ],
    columns: [{ title: '字段', dataIndex: 'keyCode' }, { title: '名称', dataIndex: 'name' }],
    key: 'id',
  },
  // 响应参数
  select_response_fields: {
    title: '响应参数',
    url: `${config.baseUrl}/api/interfaceResponse/query`,
    searchForm: [
      { type: 'input', id: 'keyCode', label: '字段' },
      { type: 'input', id: 'name', label: '名称' },
    ],
    columns: [{ title: '字段', dataIndex: 'keyCode' }, { title: '名称', dataIndex: 'name' }],
    key: 'id',
  },
  pay_source_from: {
    title: '付款用途',
    url: `${config.expenseUrl}/api/expense/report/type/section/cash/transaction/class`,
    searchForm: [
      { type: 'input', id: 'code', label: '付款用途代码' },
      { type: 'input', id: 'name', label: '付款用途名称' },
    ],
    columns: [
      { title: '付款用途代码', dataIndex: 'classCode' },
      { title: '付款用途名称', dataIndex: 'description' },
    ],
    key: 'id',
  },
  select_authorization_user_new: {
    title: 'chooser.data.selectPerson',
    url: `${config.mdataUrl}/api/get/users/by/department/and/company`,
    searchForm: [
      {
        type: 'input',
        id: 'companyName',
        label: 'chooser.data.companyName',
        defaultValue: '',
      },
      //  { type: 'input', id: 'departmentId', label: ('chooser.data.dep'), defaultValue: '' },
      { type: 'input', id: 'userCode', label: 'chooser.data.userCode', defaultValue: '' },
      { type: 'input', id: 'userName', label: 'chooser.data.fullName', defaultValue: '' },
    ],
    columns: [
      { title: 'chooser.data.companyName', dataIndex: 'companyName' },
      { title: 'chooser.data.userCode', dataIndex: 'userCode' },
      { title: 'chooser.data.fullName', dataIndex: 'userName' },
    ],
    key: 'userId',
  },
  // 业务类型
  bussiness_type: {
    title: '业务类型', //选择业务类型
    url: `${config.workbenchUrl}/api/workbench/businessType/query`,
    searchForm: [
      {
        type: 'input',
        id: 'businessTypeName',
        label: '业务类型名称', //业务类型名称
      },
      {
        type: 'input',
        id: 'businessTypeCode',
        label: '业务类型代码', //业务类型代码
      },
    ],
    columns: [
      {
        title: '业务类型名称', //业务类型名称
        dataIndex: 'businessTypeName',
      },
      {
        title: '业务类型代码', //业务类型代码
        dataIndex: 'businessTypeCode',
      },
    ],
    key: 'id',
  },
  // 业务节点
  business_node: {
    title: '选择节点', //选择节点
    url: `${config.workbenchUrl}/api/workbench/businessNode/query/lov`,
    searchForm: [
      {
        type: 'input',
        id: 'businessNodeName',
        label: '节点名称', //节点名称、代码
      },
      {
        type: 'input',
        id: 'businessNodeCode',
        label: '节点代码', //节点名称、代码
      },
    ],
    columns: [
      {
        title: '节点名称', //业务类型名称
        dataIndex: 'businessNodeName',
      },
      {
        title: '节点代码', //业务类型代码
        dataIndex: 'businessNodeCode',
      },
    ],
    key: 'id',
  },
  // 工作组
  work_team: {
    title: '选择工作组', //选择工作组
    url: `${config.workbenchUrl}/api/workbench/businessWorkTeam/query/workTeam/lov`,
    searchForm: [
      {
        type: 'input',
        id: 'workTeamName',
        label: '工作组名称', //工作组名称、代码
      },
      {
        type: 'input',
        id: 'workTeamCode',
        label: '工作组代码', //工作组名称、代码
      },
    ],
    columns: [
      {
        title: '工作组名称', //工作组名称
        dataIndex: 'workTeamName',
      },
      {
        title: '工作组代码', //工作组代码
        dataIndex: 'workTeamCode',
      },
    ],
    key: 'id',
  },
  // 节点动作
  node_action: {
    title: '选择节点动作', //选择节点动作
    url: `${config.workbenchUrl}/api/workbench/businessNodeAction/query/lov`,
    searchForm: [
      {
        type: 'input',
        id: 'actionName',
        label: '节点动作名称', //节点动作名称
      },
      {
        type: 'input',
        id: 'actionCode',
        label: '节点动作代码', //节点动作代码
      },
    ],
    columns: [
      {
        title: '工作组名称', //工作组名称
        dataIndex: 'nodeActionName',
      },
      {
        title: '工作组代码', //工作组代码
        dataIndex: 'nodeActionCode',
      },
    ],
    key: 'id',
  },
  // 核算主体分配后的公司详情
  company_detail_taxpayerId: {
    title: 'chooser.data.company' /*选择公司*/,
    url: `${config.taxUrl}/api/tax/taxpayer/org/companyList`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      { title: 'chooser.data.companyType' /*公司类型*/, dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  // 项目申请单详情下 当前账套下的人员
  select_setOfBooksId_employee: {
    title: '选择人员',
    url: `${config.mdataUrl}/api/select/user/by/name/or/code`,
    // searchForm: [{ type: 'input', id: 'key', label: '员工工号、姓名' }],
    searchForm: [],
    columns: [
      { title: '工号', dataIndex: 'employeeID', width: '25%' },
      { title: '姓名', dataIndex: 'fullName', width: '25%' },
      { title: '部门名称', dataIndex: 'departmentName', width: '25%' },
      { title: '职务', dataIndex: 'title', width: '25%' },
    ],
    key: 'id',
  },
  // 项目申请单详情下 当前账套下的部门
  select_setOfBooksId_department: {
    title: '选择部门',
    url: `${config.mdataUrl}/api/departments/root/v2`,
    searchForm: [
      // { type: 'input', id: 'departmentCode', label: '部门代码' },
      // { type: 'input', id: 'name', label: '部门名称' },
    ],
    columns: [
      { title: '部门代码', dataIndex: 'departmentCode' },
      { title: '部门名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
  //组织架构下责任中心配置的默认责任中心弹框(去掉了责任中心代码从和责任中心代码至)
  responsibility_default_detail: {
    title: '默认责任中心',
    url: `${config.mdataUrl}/api/responsibilityCenter/query/default`,
    searchForm: [{ type: 'input', id: 'info', label: '责任中心', placeholder: '请输入代码或名称' }],
    columns: [
      { title: '责任中心代码', dataIndex: 'responsibilityCenterCode' },
      { title: '责任中心名称', dataIndex: 'responsibilityCenterName' },
    ],
    key: 'id',
  },
  // 公司详情
  company_detail: {
    title: 'chooser.data.company' /*选择公司*/,
    url: `${config.mdataUrl}/api/company/by/condition`,
    searchForm: [
      {
        type: 'input',
        id: 'companyCode',
        label: 'chooser.data.companyCode' /*公司代码*/,
      },
      { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
    ],
    columns: [
      { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
      { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
      { title: 'chooser.data.companyType' /*公司类型*/, dataIndex: 'companyTypeName' },
    ],
    key: 'id',
  },
  // 核算主体分配后的公司详情
  // company_detail_taxpayerId: {
  //   title: 'chooser.data.company' /*选择公司*/,
  //   url: `${config.mdataUrl}/api/tax/taxpayer/org/companyList/${taxpayerId}`,
  //   searchForm: [
  //     {
  //       type: 'input',
  //       id: 'companyCode',
  //       label: 'chooser.data.companyCode' /*公司代码*/,
  //     },
  //     { type: 'input', id: 'name', label: 'chooser.data.companyName' /*公司名称*/ },
  //   ],
  //   columns: [
  //     { title: 'chooser.data.companyCode' /*公司代码*/, dataIndex: 'companyCode' },
  //     { title: 'chooser.data.companyName' /*公司名称*/, dataIndex: 'name' },
  //     { title: 'chooser.data.companyType' /*公司类型*/, dataIndex: 'companyTypeName' },
  //   ],
  //   key: 'id',
  // },
  taxpayer_name: {
    title: '选择纳税人',
    url: `${config.taxUrl}/api/tax/taxRegister/basic/status/condition`,
    searchForm: [
      { type: 'input', id: 'taxpayerName', label: '纳税人名称' },
      { type: 'input', id: 'taxpayerNumber', label: '纳税人识别号' },
    ],
    columns: [
      { title: '纳税人名称', dataIndex: 'taxpayerName' },
      { title: '纳税人识别号', dataIndex: 'taxpayerNumber' },
    ],
    key: 'id',
  },
  select_city: {
    title: '选择城市',
    url: `${config.mdataUrl}/api/location/search/cities`,
    searchForm: [{ type: 'input', id: 'description', label: '城市名称' }],
    columns: [
      { title: '城市代码', dataIndex: 'code' },
      { title: '城市名称', dataIndex: 'description' },
    ],
    key: 'id',
  },
  //报账单类型
  select_report_type: {
    title: '报账单类型',
    url: `${config.expenseUrl}/api/expense/report/type/query`,
    searchForm: [
      {
        type: 'input',
        id: 'reportTypeCode',
        label: '代码',
      },
      {
        type: 'input',
        id: 'reportTypeName',
        label: '名称',
      },
    ],
    columns: [
      {
        title: '代码',
        dataIndex: 'reportTypeCode',
      },
      {
        title: '名称',
        dataIndex: 'reportTypeName',
      },
    ],
    key: 'id',
  },
  subject: {
    title: '科目',
    url: `${config.mdataUrl}/api/accounts/query/accounts/setOfBooksId`,
    searchForm: [
      { type: 'input', id: 'accountCode', label: '科目代码' },
      { type: 'input', id: 'accountName', label: '科目名称' },
    ],
    columns: [
      { title: '科目代码', dataIndex: 'accountCode' },
      { title: '科目名称', dataIndex: 'accountName' },
    ],
    key: 'id',
  },
  specific_item: {
    title: '子目',
    url: `${config.mdataUrl}/api/dimension/item/page/by/cond`,
    searchForm: [
      { type: 'input', id: 'segment4', label: '子目代码' },
      { type: 'input', id: 'segment4_des', label: '子目名称' },
    ],
    columns: [
      { title: '子目代码', dataIndex: 'segment4' },
      { title: '子目名称', dataIndex: 'segment4_des' },
    ],
    key: 'id',
  },
  company_detail_description: {
    title: '公司',
    url: `${config.taxUrl}/api/tax/taxRegister/basic/status/condition`,
    searchForm: [
      { type: 'input', id: 'segment1', label: '公司代码' },
      { type: 'input', id: 'segment1Des', label: '公司名称' },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'segment1' },
      { title: '公司名称', dataIndex: 'segment1Des' },
    ],
    key: 'id',
  },
  cost_center: {
    title: '成本中心',
    url: `${config.taxUrl}/api/tax/taxRegister/basic/status/condition`,
    searchForm: [
      { type: 'input', id: 'segment2', label: '成本中心代码' },
      { type: 'input', id: 'segment2Des', label: '成本中心名称' },
    ],
    columns: [
      { title: '成本中心代码', dataIndex: 'segment2' },
      { title: '成本中心名称', dataIndex: 'segment2Des' },
    ],
    key: 'id',
  },
  //报账单分摊公司
  company_share_report: {
    title: '分摊公司',
    url: `${config.expenseUrl}/api/expense/report/type/dist/setting/query/company/by/expenseTypeId`,
    searchForm: [
      { type: 'input', id: 'companyCode', label: '公司代码' },
      { type: 'input', id: 'name', label: '公司名称' },
    ],
    columns: [
      { title: '公司代码', dataIndex: 'companyCode' },
      { title: '公司名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
  //报账单分摊部门
  department_share_report: {
    title: '分摊部门',
    url: `${
      config.expenseUrl
    }/api/expense/report/type/dist/setting/query/department/by/expenseTypeId`,
    searchForm: [
      { type: 'input', id: 'departmentCode', label: '部门号', defaultValue: '' },
      { type: 'input', id: 'departmentName', label: '部门名称', defaultValue: '' },
    ],
    columns: [
      { title: '部门号', dataIndex: 'departmentCode' },
      { title: '部门名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
  //报账单责任中心配置
  responsibility_report: {
    title: '分摊责任中心',
    url: `${
      config.expenseUrl
    }/api/expense/report/type/dist/setting/query/respCenter/by/expenseTypeId`,
    searchForm: [
      { type: 'input', id: 'info', label: '责任中心', placeholder: '请输入代码或名称' },
      { type: 'input', id: 'codeFrom', label: '责任中心代码从' },
      { type: 'input', id: 'codeTo', label: '责任中心代码至' },
    ],
    columns: [
      { title: '责任中心代码', dataIndex: 'responsibilityCenterCode' },
      { title: '责任中心名称', dataIndex: 'responsibilityCenterName' },
    ],
    key: 'id',
  },
  //客户信息查询
  customer_information_query: {
    title: '客户信息查询',
    url: `${config.taxUrl}/api/tax/client/query/condition`,
    searchForm: [
      { type: 'input', id: 'clientNumber', label: '客户编号' },
      { type: 'input', id: 'clientName', label: '客户名称' },
    ],
    columns: [
      { title: '客户编号', dataIndex: 'clientNumber' },
      { title: '客户名称', dataIndex: 'clientName' },
    ],
    key: 'id',
  },
  place_level: {
    title: '地点级别',
    url: `${config.mdataUrl}/api/location/level/query`,
    searchForm: [
      { type: 'input', id: 'code', label: '地点级别代码' },
      { type: 'input', id: 'name', label: '地点级别名称' },
    ],
    columns: [
      { title: '地点级别代码', dataIndex: 'code' },
      { title: '地点级别名称', dataIndex: 'name' },
    ],
    key: 'id',
  },
};

export default chooserData;
