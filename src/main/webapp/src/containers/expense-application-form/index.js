import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Menu, Dropdown, Icon, Row, Col, Input, message, Badge } from 'antd';
import config from 'config';
import moment from 'moment';

import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';

import service from './service';

const Search = Input.Search;

class ExpenseApplicationForm extends React.Component {
  constructor(props) {
    super(props);
    this.statusList = [
      { value: 1001, label: this.$t('expense.editing') },
      { value: 1002, label: this.$t('expense.in.the.examination.and.approval') },
      { value: 1003, label: this.$t('expense.taken.back.to.the') },
      { value: 1004, label: this.$t('expense.the.examination.and.approval.by') },
      { value: 1005, label: this.$t('expense.approval.to.dismiss') },
    ];

    this.state = {
      pagination: {
        total: 0,
      },
      closeType: {
        NOT_CLOSED: this.$t('expense.not.closed') /*未关闭*/,
        CLOSED: this.$t('expense.closed') /*已关闭*/,
        PARTIAL_CLOSED: this.$t('expense.part.of.the.closing') /*部分关闭*/,
      },
      status: {
        1001: { label: this.$t('expense.editing'), state: 'default' } /*编辑中*/,
        1004: {
          label: this.$t('expense.the.examination.and.approval.by'),
          state: 'success',
        } /*审批通过*/,
        1002: {
          label: this.$t('expense.in.the.examination.and.approval'),
          state: 'processing',
        } /*审批中*/,
        1005: { label: this.$t('expense.approval.to.dismiss'), state: 'error' } /*审批驳回*/,
        1003: { label: this.$t('expense.taken.back.to.the'), state: 'warning' } /*撤回*/,
        0: { label: this.$t('expense.unknown'), state: 'warning' } /*未知*/,
        2004: { label: this.$t('expense.pay.for.success'), state: 'success' } /*支付成功*/,
        2003: { label: this.$t('expense.paying'), state: 'processing' } /*支付中*/,
        2002: { label: this.$t('expense.approved'), state: 'success' } /*审核通过*/,
        2001: { label: this.$t('expense.review.rejected'), state: 'error' } /*审核驳回*/,
      },
      searchForm: [
        {
          type: 'select',
          id: 'typeId',
          label: this.$t('expense.document.type') /*单据类型*/,
          options: [],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            {
              type: 'date',
              id: 'dateFrom',
              label: this.$t('expense.application.date.from'),
            } /*申请日期从*/,
            {
              type: 'date',
              id: 'dateTo',
              label: this.$t('expense.application.date.to'),
            } /*申请日期至*/,
          ],
          colSpan: 7,
        },
        {
          type: 'select',
          id: 'employeeId',
          label: this.$t('expense.reverse.apply.name') /*申请人*/,
          options: [],
          colSpan: 5,
        },
        {
          type: 'select',
          id: 'status',
          label: this.$t('expense.policy.enabled'),
          options: this.statusList,
          colSpan: 6,
        } /*状态*/,
        {
          type: 'select',
          key: 'currency',
          id: 'currencyCode',
          label: this.$t('expense.policy.currencyName') /*币种*/,
          getUrl: `${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`,
          getParams: { setOfBooksId: this.props.company.setOfBooksId },
          options: [],
          method: 'get',
          labelKey: '${currency}-${currencyName}',
          valueKey: 'currency',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 7,
          items: [
            {
              type: 'input',
              id: 'amountFrom',
              label: this.$t('expense.the.amount.from'),
            } /*金额从*/,
            { type: 'input', id: 'amountTo', label: this.$t('expense.the.amount.to') } /*金额至*/,
          ],
        },
        {
          type: 'select',
          key: 'closedFlag',
          id: 'closedFlag',
          label: this.$t('expense.the.closed.position') /*关闭状态*/,
          options: [
            { label: this.$t('expense.not.closed'), value: 'NOT_CLOSED' } /*未关闭*/,
            { label: this.$t('expense.part.of.the.closing'), value: 'PARTIAL_CLOSED' } /*部分关闭*/,
            { label: this.$t('expense.closed'), value: 'CLOSED' } /*已关闭*/,
          ],
          labelKey: 'label',
          valueKey: 'value',
          colSpan: 5,
        },
        {
          type: 'input',
          id: 'remarks',
          colSpan: 6,
          label: this.$t('expense.reverse.remark') /*备注*/,
        },
      ],
      columns: [
        {
          title: this.$t('expense.odd.numbers') /*单号*/,
          dataIndex: 'documentNumber',
          width: 160,
          tooltips: true,
        },
        {
          title: this.$t('expense.document.type') /*单据类型*/,
          dataIndex: 'typeName',
          width: 130,
          tooltips: true,
        },
        {
          title: this.$t('expense.reverse.apply.name') /*申请人*/,
          dataIndex: 'employeeName',
          width: 90,
        },
        {
          title: this.$t('expense.application.date') /*申请日期*/,
          dataIndex: 'requisitionDate',
          width: 110,
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t('expense.policy.currencyName') /*币种*/,
          dataIndex: 'currencyCode',
          width: 80,
        },
        {
          title: this.$t('expense.amount') /*金额*/,
          dataIndex: 'amount',
          width: 130,
          render: value => this.filterMoney(value),
        },
        {
          title: this.$t('expense.local.currency.amount') /*本币金额*/,
          dataIndex: 'functionalAmount',
          width: 130,
          render: value => this.filterMoney(value),
        },
        {
          title: this.$t('expense.reverse.remark') /*备注*/,
          dataIndex: 'remarks',
          tooltips: true,
        },
        {
          title: this.$t('expense.policy.enabled') /*状态*/,
          dataIndex: 'closedFlag',
          width: 70,
          render: value => this.state.closeType[value],
        },
        {
          title: this.$t('expense.policy.enabled') /*状态*/,
          dataIndex: 'status',
          width: 100,
          render: value => (
            <Badge status={this.state.status[value].state} text={this.state.status[value].label} />
          ),
        },
      ],
      searchParams: {},
      menus: [],
    };
  }

  componentDidMount() {
    this.getApplicationTypeList();
  }

  //获取申请单类型
  getApplicationTypeList = () => {
    let searchForm = this.state.searchForm;
    //获取可新建的单据类型
    service
      .getApplicationTypeList({ setOfBooksId: this.props.company.setOfBooksId, enabled: true })
      .then(res => {
        this.setState({ menus: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
    //获取已创建的单据类型（条件查询可查到的单据类型）
    service
      .getCreatedApplicationTypeList({
        setOfBooksId: this.props.company.setOfBooksId,
      })
      .then(res => {
        searchForm[0].options = res.data.map(o => ({ value: o.id, label: o.typeName }));
        this.setState({ searchForm });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
    //查询当前机构下所有已创建的申请单的申请人（查询下拉框)
    service
      .getCreatedApplicationUserList()
      .then(res => {
        searchForm[2].options = res.data.map(o => ({ value: o.id, label: o.fullName }));
        this.setState({ searchForm });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 获取列表
  getList = () => {
    let { searchParams } = this.state;

    searchParams.dateFrom &&
      (searchParams.dateFrom = moment(searchParams.dateFrom).format('YYYY-MM-DD'));
    searchParams.dateTo && (searchParams.dateTo = moment(searchParams.dateTo).format('YYYY-MM-DD'));

    this.table.search(searchParams);
  };

  //搜索
  search = values => {
    this.setState({ searchParams: { ...this.state.searchParams, ...values } }, () => {
      this.getList();
    });
  };

  //单号搜索
  searchNumber = value => {
    this.setState({ searchParams: { ...this.state.searchParams, documentNumber: value } }, () => {
      this.getList();
    });
  };

  //清除
  clear = () => {
    this.setState(
      { searchParams: { documentNumber: this.state.searchParams.documentNumber } },
      () => {
        this.getList();
      }
    );
  };

  //跳转到新建页面
  newReimburseForm = value => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/expense-application/expense-application/new-expense-application/' + value.key,
      })
    );
  };

  //跳转到详情
  handleRowClick = recode => {
    this.props.dispatch(
      routerRedux.push({
        pathname:
          '/expense-application/expense-application/expense-application-detail/' + recode.id,
      })
    );
  };

  render() {
    const { searchForm, columns, menus } = this.state;

    const menusUi = (
      <Menu onClick={this.newReimburseForm}>
        {menus.map(item => {
          return <Menu.Item key={item.id}>{item.typeName}</Menu.Item>;
        })}
      </Menu>
    );

    return (
      <div className="reimburse-container">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          maxLength={4}
          clearHandle={this.clear}
        />
        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col id="application-form-drop" style={{ position: 'relative' }} span={18}>
            <Dropdown
              getPopupContainer={() => document.getElementById('application-form-drop')}
              trigger={['click']}
              overlay={menusUi}
            >
              <Button type="primary">
                {this.$t('expense.new.expense.applition.form')}
                <Icon type="down" />
                {/*新建费用申请单*/}
              </Button>
            </Dropdown>
          </Col>
          <Col span={6}>
            <Search
              placeholder={this.$t(
                'expense.please.enter.the.application.number.alone'
              )} /*请输入申请单单号*/
              style={{ width: '100%' }}
              onSearch={this.searchNumber}
              enterButton
            />
          </Col>
        </Row>
        <CustomTable
          onClick={this.handleRowClick}
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.expenseUrl}/api/expense/application/header/query/condition`}
          onRowClick={this.handleRowClick}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(mapStateToProps)(ExpenseApplicationForm);
