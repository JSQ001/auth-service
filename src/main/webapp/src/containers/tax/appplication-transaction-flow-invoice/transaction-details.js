import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { message, Table, Spin } from 'antd';

class TransactionDetails extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      loading: true,
      columns: [
        {
          title: '交易流水号',
          dataIndex: 'writeOffAmount',
          key: 'writeOffAmount',
          width: 200,
        },
        {
          title: '来源系统',
          dataIndex: 'invoiceNumber',
          key: 'invoiceNumber',
          width: 200,
        },
        {
          title: '机构',
          dataIndex: 'invoiceCode',
          key: 'invoiceCode',
          width: 200,
        },
        {
          title: '交易名称',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
          width: 200,
        },
        {
          title: '客户编号',
          dataIndex: 'clientName',
          key: 'clientName',
          width: 200,
        },
        {
          title: '客户名称',
          dataIndex: 'taxpyerNum',
          key: 'taxpyerNum',
          width: 200,
        },
        {
          title: '纳税人资质',
          dataIndex: 'totalAmount',
          key: 'totalAmount',
          width: 200,
        },
        {
          title: '纳税人识别号',
          dataIndex: 'sales',
          key: 'sales',
          width: 200,
        },
        {
          title: '开票名称',
          dataIndex: 'taxes',
          key: 'taxes',
          width: 200,
        },
        {
          title: '开票类型',
          dataIndex: 'invoiceStatus',
          key: 'invoiceStatus',
          width: 200,
        },
        {
          title: '交易日期',
          dataIndex: 'invoiceDate1',
          key: 'invoiceDate1',
          width: 200,
        },
        {
          title: '币种',
          dataIndex: 'invoiceDate2',
          key: 'invoiceDate2',
          width: 200,
        },
        {
          title: '原币交易金额',
          dataIndex: 'invoiceDate3',
          key: 'invoiceDate3',
          width: 200,
        },
        {
          title: '本币交易金额',
          dataIndex: 'invoiceDate4',
          key: 'invoiceDate4',
          width: 200,
        },
        {
          title: '税目代码',
          dataIndex: 'invoiceDate5',
          key: 'invoiceDate5',
          width: 200,
        },
        {
          title: '税目名称',
          dataIndex: 'invoiceDate6',
          key: 'invoiceDate6',
          width: 200,
        },
        {
          title: '税率',
          dataIndex: 'invoiceDate7',
          key: 'invoiceDate7',
          width: 200,
        },
        {
          title: '本次开票金额（本币）',
          dataIndex: 'invoiceDate8',
          key: 'invoiceDate8',
          width: 200,
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

export default connect()(TransactionDetails);
