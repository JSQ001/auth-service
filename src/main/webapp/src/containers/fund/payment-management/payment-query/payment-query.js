import React from 'react';
import 'styles/fund/account.scss';
import { routerRedux } from 'dva/router';
import { Form, Input, Row, Col } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import Table from 'widget/table';
import FundSearchForm from '../../fund-components/fund-search-form';
import PaymentMaintenanceService from '../payment-slip-maintenance/payment-maintenance-service';

const { Search } = Input;
const defaultSelectDate = {
  startDate: moment()
    .startOf('day')
    .subtract(6, 'days'),
  endDate: moment().endOf('day'),
};
class PaymentQuery extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
      },
      columns: [
        {
          title: this.$t('fund.receipt.number') /* 单据编号 */,
          dataIndex: 'paymentBatchNumber',
          width: 150,
        },
        {
          title: this.$t('fund.type.of.document') /* 单据类型 */,
          dataIndex: 'billTypeDesc',
          width: 100,
        },
        {
          title: this.$t('fund.payment.account') /* 付款户名 */,
          dataIndex: 'paymentAccountName',
          width: 100,
        },
        {
          title: this.$t('fund.payment.account') /* 付款账号 */,
          dataIndex: 'paymentAccount',
          width: 130,
        },
        {
          title: '所属公司',
          dataIndex: 'paymentCompanyName',
          width: 100,
          align: 'center',
        },
        {
          title: '付款方式',
          dataIndex: 'paymentMethodDesc',
          width: 100,
        },
        {
          title: this.$t('fund.amount') /* 金额 */,
          dataIndex: 'amount',
          width: 100,
          render: value => <div style={{ textAlign: 'right' }}>{this.filterMoney(value)}</div>,
        },
        {
          title: this.$t('fund.the.number') /* 笔数 */,
          dataIndex: 'lineCount',
          width: 80,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        },
        {
          title: this.$t('fund.currency.code') /* 币种 */,
          dataIndex: 'currencyCode',
          width: 80,
        },
        {
          title: '支付状态',
          dataIndex: 'headStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '审批状态',
          dataIndex: 'billStatusDesc',
          width: 80,
        },
        {
          title: this.$t('fund.document.date') /* 单据日期 */,
          dataIndex: 'billDateDesc',
          width: 100,
          align: 'center',
        },
        {
          title: this.$t('fund.single.person') /* 制单人 */,
          dataIndex: 'employeeName',
          width: 100,
        },
      ],
      searchForm: [
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.type.of.document') /* 单据类型 */,
          id: 'billType',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.payment.method') /* 付款方式 */,
          id: 'paymentType',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: this.$t('fund.payment.account') /* 付款账号 */,
          id: 'accountNumber',
          listType: 'paymentAccount',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '单据公司',
          id: 'paymentCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: this.$t('fund.the.documents.state') /* 单据状态 */,
          id: 'billStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: this.$t('fund.date.from') /* 日期从 */,
          fromId: 'dateFrom',
          tolabel: this.$t('fund.the.date.to') /* 日期到 */,
          toId: 'dateTo',
        },
      ],
    };
  }

  componentWillMount() {
    this.setState({
      searchParams: {
        billDateFrom: moment(defaultSelectDate.startDate)
          .format()
          .slice(0, 10),
        billDateTo: moment(defaultSelectDate.endDate)
          .format()
          .slice(0, 10),
      },
    });
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 获取列表数据
   */
  getList = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    PaymentMaintenanceService.getPaymentQueryListAll(
      pagination.page,
      pagination.pageSize,
      searchParams
    ).then(response => {
      const { data } = response;
      this.setState({
        tableData: data,
        loading: false,
        pagination: {
          ...pagination,
          total: Number(response.headers['x-total-count'])
            ? Number(response.headers['x-total-count'])
            : 0,
          onChange: this.onChangePager,
          current: pagination.page + 1,
        },
        searchParams: {},
      });
    });
  };

  /**
   * 搜索
   */
  handleSearch = params => {
    // 获取表单数据

    let { searchParams } = this.state;
    searchParams = {};
    this.setState(
      {
        searchParams: {
          ...searchParams,
          billType: params.billType ? params.billType.key : '',
          paymentMethod: params.paymentType ? params.paymentType.key : '',
          paymentCompanyId: params.paymentCompany ? params.paymentCompany[0].id : '',
          billStatus: params.billStatus ? params.billStatus.key : '',
          // paymentAccount: paymentAccount || '',
          billDateFrom: params.dateFrom
            ? moment(params.dateFrom)
                .format()
                .slice(0, 10)
            : '',
          billDateTo: params.dateTo
            ? moment(params.dateTo)
                .format()
                .slice(0, 10)
            : '',
          paymentAccount: params.accountNumber ? params.accountNumber.accountNumber : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  // 单号搜索
  searchNumber = value => {
    let { searchParams } = this.state;
    searchParams = {};
    this.setState(
      {
        searchParams: { ...searchParams, paymentBatchNumber: value },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 数据列表分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 点击行
   */
  handleRowClick = record => {
    // console.log(record);
    const { dispatch } = this.props;
    // console.log(record)
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/payment-query/payment-query-details/${record.id}/${
          record.paymentBatchNumber
        }`,
      })
    );
  };

  render() {
    const { pagination, columns, loading, tableData, searchForm } = this.state;
    return (
      <div className="train">
        {/* 搜索区域 */}
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={4}
            />
          </Row>
        </div>
        <div>
          <div
            className="table-header-buttons"
            style={{ marginTop: '20px', paddingBottom: '10px' }}
          >
            <Row>
              <Col span={6} offset={18}>
                <Search
                  placeholder={this.$t('fund.please.enter.the.receipt.number')}
                  enterButton
                  onSearch={this.searchNumber}
                />
                {/* 请输入单据编号 */}
              </Col>
            </Row>
          </div>
          {/* 数据列表 */}
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePager}
            onRowClick={this.handleRowClick}
            bordered
            size="middle"
            scroll={{ x: 1300 }}
          />
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(mapStateToProps)(Form.create()(PaymentQuery));
