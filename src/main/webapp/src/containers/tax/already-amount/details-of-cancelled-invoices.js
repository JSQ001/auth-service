import React from 'react';
import { connect } from 'dva';
import { Table, Spin } from 'antd';
import Service from './already-amount.service';

class DetailsOfCancelledInvoices extends React.Component {
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
          title: '发票代码',
          dataIndex: 'invoiceNumber',
          key: 'invoiceNumber',
          align: 'center',
        },
        {
          title: '发票号码',
          dataIndex: 'invoiceCode',
          key: 'invoiceCode',
          align: 'center',
        },
        {
          title: '发票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
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
          title: '总价税合计金额',
          dataIndex: 'totalAmount',
          key: 'totalAmount',
          align: 'center',
        },
        {
          title: '不含税金额',
          dataIndex: 'sales',
          key: 'sales',
          align: 'center',
        },
        {
          title: '税额',
          dataIndex: 'taxes',
          key: 'taxes',
          align: 'center',
        },
        {
          title: '开票状态',
          dataIndex: 'invoiceStatus',
          key: 'invoiceStatus',
          align: 'center',
        },
        {
          title: '开票日期',
          dataIndex: 'invoiceDate',
          key: 'invoiceDate',
          align: 'center',
        },
      ],
    };
  }

  componentDidMount() {
    const { id } = this.props;
    this.getDeailCancellInvoice(id);
  }
  //获取核销发票明细
  getDeailCancellInvoice = id => {
    Service.deailCancellInvoice(id).then(res => {
      this.setState({
        dataSource: res.data,
      });
    });
  };

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

export default DetailsOfCancelledInvoices;
