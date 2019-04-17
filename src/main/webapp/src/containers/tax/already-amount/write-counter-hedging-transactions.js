import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { message, Table, Spin } from 'antd';

class WriteCounterHedgingTransactions extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      loading: true,
      columns: [
        {
          title: '核销金额',
          dataIndex: 'writeOffAmount',
          key: 'writeOffAmount',
        },
        {
          title: '交易流水号',
          dataIndex: 'tarnNum',
          key: 'tarnNum',
        },
        {
          title: '来源系统',
          dataIndex: 'scourceSystem',
          key: 'scourceSystem',
        },
        {
          title: '交易名称',
          dataIndex: 'tarnName',
          key: 'tarnName',
        },
        {
          title: '客户名称',
          dataIndex: 'clientName',
          key: 'clientName',
        },
        {
          title: '纳税人识别号',
          dataIndex: 'taxpyerNum',
          key: 'taxpyerNum',
        },
        {
          title: '交易日期',
          dataIndex: 'tarnDate',
          key: 'tarnDate',
        },
        {
          title: '本币交易金额',
          dataIndex: 'funTarnAmount',
          key: 'funTarnAmount',
        },
        {
          title: '开票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
        },
        {
          title: '税目代码',
          dataIndex: 'taxItem',
          key: 'taxItem',
        },
        {
          title: '税率%',
          dataIndex: 'vatRate',
          key: 'vatRate',
        },
      ],
    };
  }

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
