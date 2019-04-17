import React from 'react';
import { Form, Table, Row, Col, Button } from 'antd';
import { connect } from 'dva';
import SlideFrame from 'widget/slide-frame';
import FundSearchForm from '../../fund-components/fund-search-form';
import AutoCashierReconciliation from './auto-reconciliation';

class CashierReconciliation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      slideVisible: false,
      pagination: {
        total: 0, // 数据总数
        page: 0, // 用于计算页数
        pageSize: 10, // 每页条数
        current: 1, // 当前页数
        showSizeChanger: true, // 是否可以改变 pageSize
        showQuickJumper: true, // 是否可以快速跳转至某页
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`, // 用于显示数据总量和当前数据顺序
      },
      pagination1: {
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
          type: 'valueList',
          label: '公司',
          id: 'billType',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '银行账号',
          id: 'paymentType',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '币种',
          id: 'accountNumber',
          listType: 'paymentAccount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '期间',
          id: 'billStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '对账状态',
          id: 'billStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
      ],
      columns: [
        {
          title: '回单编号',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '摘要',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '对方户名',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '借方金额',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '贷方金额',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '银行流水号',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
      ],
      columns1: [
        {
          title: '来源',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '凭证编号',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '凭证日期',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '对方名称',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '借方金额',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '贷方金额',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
        {
          title: '银行流水号',
          dataIndex: 'paymentBatchNumber',
          width: '10%',
          align: 'center',
        },
      ],
    };
  }

  /**
   * 搜索
   */
  handleSearch = params => {
    /* disable-eslint */
    console.log(params);
    /* enable-eslint */
  };

  /**
   * 自动对账
   */
  handleCreateClick = () => {
    this.setState({
      slideVisible: true,
    });
  };

  /**
   * 关闭侧边栏
   */
  handleClose = () => {
    this.setState({
      slideVisible: false,
    });
  };

  /**
   * 打印
   */
  print = () => {
    window.document.body.innerHTML = window.document.getElementById('print').innerHTML;
    window.print();
    window.location.reload();
  };

  render() {
    const {
      columns,
      columns1,
      pagination,
      pagination1,
      tableData,
      loading,
      selectedRowKeys,
      selectedRow,
      slideVisible,
      searchForm,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
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
          <Row style={{ marginTop: '20px' }}>
            <Button type="primary" htmlType="submit" onClick={this.handleCreateClick}>
              自动对账
            </Button>
            <Button
              type="primary"
              htmlType="submit"
              style={{ marginLeft: 14 }}
              onClick={this.handleSearch}
            >
              人工调节
            </Button>
            <Button
              type="primary"
              htmlType="submit"
              style={{ marginLeft: 14 }}
              onClick={this.handleSearch}
            >
              撤销对账
            </Button>
            <Button
              type="primary"
              htmlType="submit"
              style={{ marginLeft: 14 }}
              onClick={this.print}
            >
              生成调节表
            </Button>
          </Row>
          <SlideFrame
            title="自动对账条件选择"
            show={slideVisible}
            onClose={() => this.handleClose()}
          >
            <AutoCashierReconciliation onClose={this.handleClose} />
          </SlideFrame>
          <Row style={{ marginTop: '20px' }} id="print">
            <Col span={24}>
              <Table
                rowKey={record => record.id}
                columns={columns}
                dataSource={tableData}
                pagination={pagination}
                rowSelection={rowSelection}
                loading={loading}
                onChange={this.onChangePager}
                onRowClick={this.handleRowClick}
                bordered
                size="middle"
              />
              <Row>
                <Col span={6}>
                  <span>贷方合计：</span>
                </Col>
                <Col span={6} offset={1}>
                  <span>借方合计：</span>
                </Col>
              </Row>
            </Col>
            <Col span={24} style={{ marginTop: '20px', paddingBottom: '20px' }}>
              <Table
                rowKey={record => record.id}
                columns={columns1}
                dataSource={tableData}
                pagination={pagination1}
                rowSelection={rowSelection}
                loading={loading}
                onChange={this.onChangePager}
                onRowClick={this.handleRowClick}
                bordered
                size="middle"
              />
              <Row>
                <Col span={6}>
                  <span>贷方合计：</span>
                </Col>
                <Col span={6} offset={1}>
                  <span>借方合计：</span>
                </Col>
              </Row>
            </Col>
          </Row>
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
export default connect(map)(Form.create()(CashierReconciliation));
