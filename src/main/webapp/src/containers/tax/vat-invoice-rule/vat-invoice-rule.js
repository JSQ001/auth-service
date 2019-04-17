/*eslint-disable*/
import React from 'react';
import SearchArea from 'widget/search-area';
import { Button } from 'antd';
import config from 'config';
import { connect } from 'dva';
import CustomTable from 'components/Widget/custom-table';

class VatInvoiceRule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'systemName',
          label: '系统名称',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'tradedName',
          label: '交易名称',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'invoiceType',
          label: '发票类型',
          colSpan: 6,
          options: [
            { label: '普通发票', value: '普通发票' },
            { label: '专用发票', value: '专用发票' },
            { label: '电子普票', value: '电子普票' },
          ],
        },
        {
          type: 'input',
          id: 'invoiceName',
          label: '开票名称',
          colSpan: 6,
        },
      ],
    };
  }

  //搜索
  search = values => {
    console.log(values);
  };
  //重置
  reset = () => {};
  render() {
    let { searchForm } = this.state;
    return (
      <div>
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.reset} />
        <div style={{ margin: '10px 10px' }}>
          <Button type="primary" onClick={this.export}>
            导出
          </Button>
        </div>
      </div>
    );
  }
}
export default connect()(VatInvoiceRule);
