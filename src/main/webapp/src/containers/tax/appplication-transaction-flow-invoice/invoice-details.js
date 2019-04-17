import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { message, Table, Spin } from 'antd';

class InvoiceDetails extends React.Component {
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
          title: '发票代码',
          dataIndex: 'invoiceNumber',
          key: 'invoiceNumber',
        },
        {
          title: '发票号码',
          dataIndex: 'invoiceCode',
          key: 'invoiceCode',
        },
        {
          title: '发票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
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
          title: '总价税合计金额',
          dataIndex: 'totalAmount',
          key: 'totalAmount',
        },
        {
          title: '不含税金额',
          dataIndex: 'sales',
          key: 'sales',
        },
        {
          title: '税额',
          dataIndex: 'taxes',
          key: 'taxes',
        },
        {
          title: '开票状态',
          dataIndex: 'invoiceStatus',
          key: 'invoiceStatus',
        },
        {
          title: '开票日期',
          dataIndex: 'invoiceDate',
          key: 'invoiceDate',
        },
      ],
    };
  }

  render() {
    const { dataSource, columns } = this.state;
    return (
      <Spin spinning={this.state.loading}>
        <Table
          // style={{marginTop:'15px'}}
          columns={columns}
          dataSource={dataSource}
          scroll={{ x: 1500 }}
          rowKey="id"
        />
      </Spin>
    );
  }
}

export default connect()(InvoiceDetails);
