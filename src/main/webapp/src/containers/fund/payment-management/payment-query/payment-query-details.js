import React from 'react';
import { Row, Form, Col, Card, Icon, Popover } from 'antd';
import { connect } from 'dva';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import PaymentMaintenanceService from './payment-query-service';

class PaymentQueryDetails extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tableData: [], // 数据列表
      loading: false,
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
          title: this.$t('fund.early.warning.state') /* 预警状态 */,
          dataIndex: 'warningStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: this.$t('fund.early.warning.information') /* 预警信息 */,
          dataIndex: 'warningData',
          width: 100,
        },
        {
          title: this.$t('fund.source.document.no') /* 来源单据号 */,
          dataIndex: 'tradeCode',
          width: 140,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.name.receiver') /* 收款户名 */,
          dataIndex: 'gatherAccountName',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.collection.branch') /* 收款分行 */,
          dataIndex: 'gatherBranchBankName',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.receiving.account') /* 收款账号 */,
          dataIndex: 'gatherAccount',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.amount') /* 金额 */,
          dataIndex: 'amount',
          width: 140,
        },
        {
          title: this.$t('fund.payment.purpose') /* 付款用途 */,
          dataIndex: 'paymentPurposeDesc',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.abstract') /* 摘要 */,
          dataIndex: 'description',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: this.$t('fund.public.private.signs') /* 公私标志 */,
          dataIndex: 'propFlagDesc',
          width: 100,
          align: 'center',
        },
        {
          title: this.$t('fund.kashe.logo') /* 卡折标志 */,
          dataIndex: 'cardSignDesc',
          width: 100,
        },
        {
          title: this.$t('fund.whether.the.payment') /* 是否付款 */,
          dataIndex: 'ifPaymentDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '支付状态',
          dataIndex: 'paymentStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '银行反馈信息',
          dataIndex: 'bankFeedback',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: '支付日期',
          dataIndex: 'paymentDateDesc',
          width: 100,
          align: 'center',
        },
        {
          title: this.$t('fund.the.receipt') /* 回单 */,
          dataIndex: 'receiptNum',
          width: 100,
          render: record => {
            return <Popover content={record}>{record}</Popover>;
          },
        },
        {
          title: '日志关联信息',
          width: 100,
          render() {
            return <a>查看</a>;
          },
        },
      ],
    };
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id) {
      this.setState({
        baseId: match.params.id,
      });
      this.getList(match.params.id);
      this.getUserList(match.params.paymentBatchNumber);
    }
  }

  /**
   * 数据列表
   */
  getList = id => {
    const { pagination } = this.state;
    this.setState({ loading: true });
    PaymentMaintenanceService.getManualList(pagination.page, pagination.pageSize, id).then(
      response => {
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
        });
      }
    );
  };

  /**
   * 根据头id获取数据
   * @param {*} number
   */
  getUserList(number) {
    const { pagination } = this.state;
    const batchNumber = { paymentBatchNumber: number };
    PaymentMaintenanceService.getPaymentQueryList(
      pagination.page,
      pagination.pageSize,
      batchNumber
    ).then(response => {
      const { data } = response;
      this.setState({
        paymentBatchNumber: data[0].paymentBatchNumber || '', // 付款单号
        paymentMethodDesc: data[0].paymentMethodDesc || '', // 付款方式
        paymentCompanyName: data[0].paymentCompanyName || '', // 公司名称
        paymentAccountName: data[0].paymentAccountName || '',
        paymentAccount: data[0].paymentAccount || '', //
        currencyCode: data[0].currencyCode || '', // 币种
        description: data[0].description || '', // 描述
        bankCodeName: data[0].bankCodeName || '', // 付款银行名称
        billDateDesc: data[0].billDateDesc, // 单据日期
      });
    });
  }

  /**
   * 分页点击
   */
  onChangePagerBank = pagination => {
    const temp = {};
    const { baseId } = this.state;
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList(baseId);
      }
    );
  };

  /**
   * 返回上一页
   */
  handleBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/payment-management/payment-query/payment-query/',
      })
    );
  };

  render() {
    const {
      loading,
      tableData,
      pagination,
      columns,
      paymentBatchNumber,
      paymentMethodDesc,
      paymentCompanyName,
      paymentAccountName,
      billDateDesc,
      paymentAccount,
      currencyCode,
      description,
      // paymentCompanyId,
      bankCodeName,
    } = this.state;
    return (
      <div>
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            marginRight: 15,
            marginLeft: 15,
          }}
        >
          <div style={{ borderBottom: '1px solid rgb(236, 236, 236)', marginTop: '-20px' }}>
            <h3>{this.$t('fund.the.detail.information')}:</h3>
            {/* 明细信息 */}
          </div>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>
              {this.$t('fund.payment.order.no1')}
              {paymentBatchNumber || ''}
            </Col>
            {/* 付款单号： */}
            <Col span={6}>
              {this.$t('fund.payment.method1')}
              {paymentMethodDesc || ''}
            </Col>
            {/* 付款方式： */}
            <Col span={6}>
              {this.$t('fund.payment.companies')}
              {paymentCompanyName || ''}
            </Col>
            {/* 付款公司： */}
            <Col span={5}>
              {this.$t('fund.date.of.documents')}
              {billDateDesc || ''}
            </Col>
            {/* 单据日期： */}
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>
              {this.$t('fund.payment.account')}
              {paymentAccount || ''}
            </Col>
            {/* 付款账号： */}
            <Col span={6}>
              {this.$t('fund.paying.bank')}
              {bankCodeName || ''}
            </Col>
            {/* 付款银行： */}
            <Col span={6}>
              {this.$t('fund.payment.account:')}
              {paymentAccountName || ''}
            </Col>
            {/* 付款账户： */}
            <Col span={5}>
              {this.$t('fund.currency')}
              {currencyCode || ''}
            </Col>
            {/* 币种： */}
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={5}>
              {this.$t('fund.desc1')}
              {description || ''}
            </Col>
          </Row>
        </Card>
        <div style={{ marginTop: '30px', paddingBottom: '15px' }}>
          <h3>{this.$t('fund.desc.code13')}</h3>
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePagerBank}
            bordered
            size="middle"
            scroll={{ x: 1800 }}
          />
          <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
            <Icon type="rollback" style={{ marginRight: '5px' }} />
            {this.$t('fund.back')}
          </a>
        </div>
      </div>
    );
  }
}

function show(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(show)(Form.create()(PaymentQueryDetails));
