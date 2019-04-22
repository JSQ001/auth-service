import React from 'react';
import { connect } from 'dva';
import { Table, Spin } from 'antd';
import Service from './already-amount.service';

class WriteCounterHedgingTransactions extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      loading: false,
      columns: [
        {
          title: '核销金额',
          dataIndex: 'writeOffAmount',
          key: 'writeOffAmount',
          align: 'center',
        },
        {
          title: '交易流水号',
          dataIndex: 'tarnNum',
          key: 'tarnNum',
          align: 'center',
        },
        {
          title: '来源系统',
          dataIndex: 'scourceSystem',
          key: 'scourceSystem',
          align: 'center',
        },
        {
          title: '交易名称',
          dataIndex: 'tarnName',
          key: 'tarnName',
          align: 'center',
        },
        {
          title: '客户名称',
          dataIndex: 'clientName',
          key: 'clientName',
          align: 'center',
        },
        {
          title: '纳税人识别号',
          dataIndex: 'taxpyerNum',
          key: 'taxpyerNum',
          align: 'center',
        },
        {
          title: '交易日期',
          dataIndex: 'tarnDate',
          key: 'tarnDate',
          align: 'center',
        },
        {
          title: '本币交易金额',
          dataIndex: 'funTarnAmount',
          key: 'funTarnAmount',
          align: 'center',
        },
        {
          title: '开票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
          align: 'center',
        },
        {
          title: '税目代码',
          dataIndex: 'taxItem',
          key: 'taxItem',
          align: 'center',
        },
        {
          title: '税率%',
          dataIndex: 'vatRate',
          key: 'vatRate',
          align: 'center',
        },
      ],
    };
  }
  componentDidMount() {
    const { id } = this.props;
    this.getWeiteCounterHedg(id);
  }
  //获取核销发票明细
  getWeiteCounterHedg = id => {
    Service.getInvoiceTransaction(id).then(res => {
      this.setState({
        dataSource: res.data,
      });
    });
  };
  //返回列表页
  HandleReturn = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `tax/issuance-sales-invoices/waiting-Invoice-trading-flow`,
      })
    );
  };

  render() {
    const { dataSource, columns } = this.state;
    return (
      <Spin spinning={this.state.loading}>
        <Table columns={columns} dataSource={dataSource} scroll={{ x: 1500 }} rowKey="id" />
      </Spin>
    );
  }
}

export default connect()(WriteCounterHedgingTransactions);
