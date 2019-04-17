import React from 'react';
import { Row, Form, Col, Card, Table, Icon } from 'antd';
import { connect } from 'dva';
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
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      columns: [
        {
          title: '预警状态',
          dataIndex: 'warningStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '预警信息',
          dataIndex: 'warningData',
          width: 100,
          align: 'center',
        },
        {
          title: '来源单据号',
          dataIndex: 'tradeCode',
          width: 100,
          align: 'center',
        },
        {
          title: '收款户名',
          dataIndex: 'gatherAccountName',
          width: 100,
          align: 'center',
        },
        {
          title: '收款分行',
          dataIndex: 'gatherBranchBankName',
          width: 100,
          align: 'center',
        },
        {
          title: '收款账号',
          dataIndex: 'gatherAccount',
          width: 100,
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'center',
        },
        {
          title: '付款用途',
          dataIndex: 'paymentPurposeDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '摘要',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
        {
          title: '公私标志',
          dataIndex: 'propFlagDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '卡折标志',
          dataIndex: 'cardSignDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '是否付款',
          dataIndex: 'ifPaymentDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '银行反馈信息',
          dataIndex: 'bankFeedback',
          width: 100,
          align: 'center',
        },
        {
          title: '支付日期',
          dataIndex: 'paymentDate',
          width: 100,
          align: 'center',
        },
        {
          title: '回单',
          dataIndex: 'receiptNum',
          width: 100,
          align: 'center',
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
            <h3>明细信息:</h3>
          </div>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>付款单号：{paymentBatchNumber || ''}</Col>
            <Col span={6}>付款方式：{paymentMethodDesc || ''}</Col>
            <Col span={6}>付款公司：{paymentCompanyName || ''}</Col>
            <Col span={5}>单据日期：{billDateDesc || ''}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={7}>付款账号：{paymentAccount || ''}</Col>
            <Col span={6}>付款银行：{bankCodeName || ''}</Col>
            <Col span={6}>付款账户：{paymentAccountName || ''}</Col>
            <Col span={5}>币种：{currencyCode || ''}</Col>
          </Row>
          <Row style={{ marginTop: '15px' }}>
            <Col span={5}>描述：{description || ''}</Col>
          </Row>
        </Card>
        <div style={{ marginTop: '30px', paddingBottom: '15px' }}>
          <h3>跟踪单据：</h3>
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePagerBank}
            bordered
            size="middle"
            scroll={{ x: 1500 }}
          />
          <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
            <Icon type="rollback" style={{ marginRight: '5px' }} />返回
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
