import React from 'react';
import { connect } from 'dva';
import PropTypes from 'prop-types';
import { Row, Col, Table, Tag, Button, Popover, Icon, Alert, Badge } from 'antd';
import InvoiceInfo from 'components/Widget/Template/invoice/invoice-info';

class InvoiceDetail extends React.Component {
  constructor(props) {
    super(props);
    this.hidden = true;
    this.state = {
      brief: true,
      isInvoiceFree: false, //免贴票标签是否显示
      invoiceFreePrint: true, //免贴票标签存在的情况下，打印按钮是否显示
      columns: [
        {
          title: this.$t('common.sequence'),
          dataIndex: 'sequence',
          width: 60,
          align: 'center',
          render: (desc, value, index) => this.getFinalIndex(index) + 1,
        },
        {
          title: this.$t('expense.invoice.goods.name') /*货物或应税劳务、服务名称*/,
          dataIndex: 'goodsName',
          width: 180,
          align: 'center',
          render: goodsName => <Popover content={goodsName}> {goodsName || '-'}</Popover>,
        },
        {
          title: this.$t('common.amount') /*金额*/,
          dataIndex: 'detailAmount',
          align: 'center',
          width: 100,
          render: amount => (amount && this.filterMoney(amount)) || '-',
        },
        {
          title: this.$t('expense.invoice.tax.rate') /*税率*/,
          width: 100,
          align: 'center',
          dataIndex: 'taxRate',
          render: taxRate => `${taxRate || 0}`,
        },
        {
          title: this.$t('common.tax') /*税额*/,
          dataIndex: 'taxAmount',
          width: 100,
          align: 'center',
          render: amount => (amount && this.filterMoney(amount)) || '-',
        },
        {
          title: <a onClick={this.handleCell}>{this.$t('expense.invoice.code')}</a>,
          dataIndex: 'invoiceCode',
          align: 'center',
          width: 200,
          render: (desc, record) =>
            <a onClick={e => this.linkDetail(e, record)}>{record.invoice.invoiceCode}</a> || '-',
        },
        {
          title: this.$t('common.operation') /*操作*/,
          align: 'center',
          dataIndex: 'operate',
          width: 100,
          fixed: 'right',
          render: (desc, record) => {
            console.log(record);
            return (
              <span>
                <a onClick={e => props.deleteLink(record)}>
                  {props.disabled ? '-' : record.fromBook ? '删除' : '退回票夹'}
                </a>
              </span>
            );
          },
        },
      ],
      col: [
        {
          title: this.$t('expense.invoice.number'),
          dataIndex: 'invoiceNo',
          align: 'center',
          width: 90,
          render: (desc, record) => record.invoice.invoiceNo || '-',
        },
        {
          title: '验真状态',
          dataIndex: 'checkResult',
          width: 90,
          align: 'center',
          render: (desc, record) => (
            <Badge
              status={record.invoice.checkResult ? 'success' : 'error'}
              text={desc ? '已验真' : '未验真'}
            />
          ),
        },
        {
          title: '发票行号',
          dataIndex: 'invoiceLineNum',
          align: 'center',
          width: 90,
        },
        {
          title: this.$t('common.from') /*来源*/,
          dataIndex: 'createdMethodName',
          width: 90,
          align: 'center',
          render: (desc, record) => record.invoice.createdMethodName || '-',
        },
      ],
      pagination: {
        current: 1,
        page: 0,
        total: 0,
        pageSize: 10,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      showInvoiceDetail: false,
      invoiceId: '',
    };
  }

  linkDetail = (e, record) => {
    e.preventDefault();
    this.setState({ showInvoiceDetail: true, invoiceId: record.invoice.id });
  };

  handleCell = e => {
    e.preventDefault();
    const { columns, col } = this.state;
    this.hidden = !this.hidden;
    columns[5].title = <a onClick={this.handleCell}>{this.$t('expense.invoice.code')}</a>;
    if (this.hidden) {
      columns.splice(6, 4);
    } else {
      col.reverse().map(item => {
        columns.splice(6, 0, { ...item });
      });
      col.reverse();
    }
    this.setState({ columns });
  };

  onChangePager = (pagination, filters, sorter) => {
    let temp = this.state.pagination;
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        loading: true,
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };
  handleReturn = record => {
    this;
  };

  renderDetail = (title, value, span = 12) => {
    let titleSpan = 8;
    return (
      <Col span={span} className="invoice-row">
        <Row>
          <Col span={titleSpan} className="invoice-title">
            {title}:
          </Col>
          <Col span={24 - titleSpan} className="invoice-detail">
            {value || '-'}
          </Col>
        </Row>
      </Col>
    );
  };

  getFinalIndex = index => {
    const { page, pageSize } = this.state.pagination;
    return index + page * pageSize;
  };

  render() {
    const { columns, showInvoiceDetail, invoiceId } = this.state;
    return (
      <div>
        <Table
          rowKey="id"
          columns={columns}
          onChange={this.onChangePager}
          bordered
          size="middle"
          scroll={{ x: columns.length > 10 ? 800 : false }}
          dataSource={this.props.invoiceDetail}
        />
        <InvoiceInfo
          cancel={() => {
            this.setState({ showInvoiceDetail: false });
          }}
          id={invoiceId}
          visible={showInvoiceDetail}
          validate={this.props.invoiceDetail.checkResult}
        />
      </div>
    );
  }
}
InvoiceDetail.propTypes = {
  invoiceDetail: PropTypes.any, // 费用数据
  disabledEdit: PropTypes.bool, // 是否可编辑
  handleEdit: PropTypes.func, // 编辑回调
};

function mapStateToProps(state) {
  return {
    company: state.login.company,
    profile: state.login.profile,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(InvoiceDetail);
