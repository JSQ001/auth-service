import React from 'react';
import { connect } from 'dva';
import Table from 'widget/table';
import { Form, Input, Row, Col } from 'antd';
import moment from 'moment';
import FundSearchForm from '../../fund-components/fund-search-form';
import PaymentDetailQueryService from './payment-detail-query-service';

const { Search } = Input;

class PaymentDetailQuery extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      status: 'ALL_LINE',
      searchParams: {},
      loading: false,
      tableData: [], // 数据列表
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      searchForm: [
        {
          colSpan: 6,
          type: 'input',
          label: '来源单据编号',
          id: 'sourceNumber',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '单据流水号',
          id: 'documentNumber',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '收款账号',
          id: 'paymentAccount',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '收款分行',
          id: 'collectionBranch',
        },
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'billDate',
          fromlabel: '日期从',
          fromId: 'dateFrom',
          tolabel: '日期到',
          toId: 'dateTo',
        },
        {
          colSpan: 6,
          type: 'intervalInput',
          label: '金额',
          id: 'money',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '是否付款',
          id: 'ifPayment',
          options: [],
          valueListCode: 'if_payment',
        },
      ],
      columns: [
        {
          title: '来源单据编号',
          dataIndex: 'sourceNumber',
          width: 200,
        },
        {
          title: '单据流水号',
          dataIndex: 'documentNumber',
          width: 170,
        },
        {
          title: '批单据编号',
          dataIndex: 'paymentBatchNumber',
          width: 170,
        },
        {
          title: '收款户名',
          dataIndex: 'gatherAccountName',
          width: 130,
        },
        {
          title: '收款分行',
          dataIndex: 'gatherBranchBankName',
          width: 150,
        },
        {
          title: '收款账号',
          dataIndex: 'gatherAccount',
          width: 170,
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'right',
        },
        {
          title: '摘要',
          dataIndex: 'description',
          width: 150,
        },
        {
          title: '公私标志',
          dataIndex: 'propFlagDesc',
          width: 150,
        },
        {
          title: '卡折标志',
          dataIndex: 'cardSignDesc',
          width: 130,
        },
        {
          title: '是否付款',
          dataIndex: 'ifPaymentDesc',
          width: 100,
        },
        {
          title: '支付账号',
          dataIndex: 'paymentBaseId',
          width: 200,
        },
        {
          title: '支付状态',
          dataIndex: 'paymentStatusDesc',
          width: 100,
        },
        {
          title: '银行反馈信息',
          dataIndex: 'bankFeedback',
          width: 150,
        },
        {
          title: '支付日期',
          dataIndex: 'paymentDateDesc',
          width: 150,
        },
        {
          title: '回单',
          dataIndex: 'receiptNum',
          width: 150,
        },
      ],
    };
  }

  componentWillMount() {}

  componentDidMount() {
    this.getList();
  }

  /**
   * 获取列表数据
   */
  getList = () => {
    const { pagination, searchParams, status } = this.state;
    this.setState({ loading: true });
    PaymentDetailQueryService.getPaymentDetailList(
      pagination.page,
      pagination.pageSize,
      searchParams,
      status
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
          pageSize: pagination.pageSize,
          onShowSizeChange: this.onShowSizeChange,
          showTotal: (total, range) =>
            this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
        },
      });
    });
  };

  /**
   * 改变每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const temp = {};
    temp.page = current - 1;
    temp.pageSize = pageSize;
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
   * 分页点击
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
   * 搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          sourceNum: values.sourceNumber || '', // 来源单据编号
          serialNum: values.documentNumber || '', // 单据流水号
          gatherAccount: values.paymentAccount || '', // 收款账号
          gatherBranchBankName: values.collectionBranch || '', // 收款分行
          billDateFrom: values.dateFrom // 单据日期
            ? moment(values.dateFrom)
                .format()
                .slice(0, 10)
            : '',
          billDateTo: values.dateTo
            ? moment(values.dateTo)
                .format()
                .slice(0, 10)
            : '',
          amountFrom: values.money.intervalFrom || '', // 金额从
          amountTo: values.money.intervalTo || '', // 金额至
          ifPayment: values.ifPayment ? values.ifPayment.key : '', // 是否收款
        },
      },
      () => {
        // console.log('--values--', values);
        // console.log('--searchParams--', searchParams);
        this.getList();
      }
    );
  };

  render() {
    const { tableData, loading, pagination, columns, searchForm } = this.state;
    return (
      <div className="payment-detail">
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={4}
            />
          </Row>
        </div>
        <div className="table-header">
          <div className="table-search" style={{ marginBottom: '10px' }}>
            <Row>
              <Col span={6} offset={18}>
                <Search placeholder="请输入批单据编号" enterButton onSearch={this.searchNumber} />
              </Col>
            </Row>
          </div>
          <Table
            rowKey={record => record.id}
            dataSource={tableData}
            loading={loading}
            columns={columns}
            pagination={pagination}
            onChange={this.onChangePager}
            scroll={{ x: 1500 }}
          />
        </div>
      </div>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(map)(Form.create()(PaymentDetailQuery));
