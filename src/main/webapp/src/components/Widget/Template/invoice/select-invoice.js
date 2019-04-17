import React from 'react';
import { connect } from 'dva';
import { Modal, message, Button, Input, Table, Row, Col, Popover } from 'antd';
import httpFetch from 'share/httpFetch';
import SearchArea from 'widget/search-area';
import moment from 'moment';
import config from 'config';

import PropTypes from 'prop-types';
import InvoiceDetail from 'components/Widget/Template/invoice/invoice-info';

class SelectInvoice extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      data: [],
      invoiceId: '',
      showInvoiceDetail: false,
      pagination: {
        current: 1,
        page: 0,
        total: 0,
        pageSize: 10,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      selectedData: [], //已经选择的数据项
      selectorItem: {}, //当前的选择器类型数据项, 包含url、searchForm、columns
      searchParams: {}, //搜索需要的参数
      rowSelection: {
        type: this.props.single ? 'radio' : 'checkbox',
        selectedRowKeys: [],
      },
      expandedRowKeys: [],
      params: {},
      searchForm: [
        {
          type: 'input',
          id: 'invoiceCode',
          label: this.$t('expense.invoice.code'), //发票代码,
        },
        {
          type: 'input',
          id: 'invoiceNo',
          label: '发票号码',
        },
        {
          type: 'items',
          id: 'dateRange',
          items: [
            {
              type: 'date',
              id: 'invoiceDateFrom',
              label: '开票日期从',
              event: 'SIGN_DATE_FROM',
            },
            {
              type: 'date',
              id: 'invoiceDateTo',
              label: '开票日期至',
              event: 'SIGN_DATE_TO',
            },
          ],
        },
        {
          type: 'input',
          id: 'salerName',
          label: '销售方名称',
        },
        {
          type: 'select',
          options: [],
          disabled: true,
          defaultValue: this.props.company.baseCurrency,
          id: 'currency',
          label: this.$t('common.currency'), //,
        },
      ],
      columns: [
        {
          title: this.$t('expense.invoice.code'), //发票代码
          dataIndex: 'invoiceCode',
          align: 'center',
        },
        {
          title: this.$t('expense.invoice.number'), //发票号码
          dataIndex: 'invoiceNo',
          align: 'center',
        },
        {
          title: this.$t('expense.invoice.date'), //开票日期
          dataIndex: 'invoiceDate',
          align: 'center',
          render: value => value && moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t('expense.invoice.seller.name'), //销方名称
          dataIndex: 'salerName',
          align: 'center',
        },
        {
          title: this.$t('common.operation'),
          dataIndex: 'operation',
          align: 'center',
          render: (value, record, index) => (
            <a onClick={e => this.checkDetails(record, e)}>{this.$t('common.view')}</a>
          ),
        },
      ],
      innerColumns: [
        {
          title: '发票行号',
          width: 90,
          dataIndex: 'invoiceLineNum',
          align: 'center',
          key: 'invoiceLineNum',
        },
        {
          title: '货物或应税劳务、服务名称',
          key: 'goodsName',
          align: 'center',
          //width:200,
          dataIndex: 'goodsName',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: this.$t('common.currency'),
          dataIndex: 'currencyCode',
          key: 'currencyCode',
          width: 90,
          align: 'center',
        },
        {
          title: this.$t('common.amount'),
          dataIndex: 'detailAmount',
          align: 'center',
          key: 'detailAmount',
          width: 90,
          render: value => {
            return <span>{this.formatMoney(value)}</span>;
          },
        },
        {
          title: this.$t('expense.invoice.tax.rate'),
          align: 'center',
          width: 90,
          dataIndex: 'taxRate',
          key: 'taxRate',
        },
        {
          title: this.$t('common.tax') /*税额*/,
          dataIndex: 'taxAmount',
          align: 'center',
          width: 90,
          key: 'taxAmount',
        },
      ],
    };
  }

  componentWillReceiveProps = nextProps => {
    if (nextProps.visible && !this.props.visible) {
      console.log(nextProps);
      let selectedRowKeys = nextProps.selectedData.map(item => item.id);
      console.log(selectedRowKeys);
      this.setState(
        {
          selectedData: nextProps.selectedData || [],
          rowSelection: {
            ...this.state.rowSelection,
            selectedRowKeys,
          },
        },
        this.getList
      );
    }
  };

  getList() {
    let selectorItem = this.state.selectorItem;
    const { page, pageSize } = this.state.pagination;
    let params = {
      page,
      pageSize,
      ...this.props.params,
    };

    let url = `${config.expenseUrl}/api/invoice/head/query/invoice/all/by/cond`;
    httpFetch
      .get(url, params)
      .then(response => {
        this.setState(
          {
            data: response.data,
            loading: false,

            pagination: {
              total: Number(response.headers['x-total-count']),
              current: this.state.pagination.current,
              page: this.state.pagination.page,
              pageSize: this.state.pagination.pageSize,
              showSizeChanger: true,
              showQuickJumper: true,
            },
          },
          () => {
            //this.refreshSelected(); //刷新当页选择器
          }
        );
      })
      .catch(e => {
        message.error('获取数据失败，请稍后重试或联系管理员');
        this.setState({
          loading: false,
        });
      });
  }

  // 查看详情
  checkDetails = (record, e) => {
    e.preventDefault();
    this.setState({ showInvoiceDetail: true, invoiceId: record.id });
  };
  search = params => {
    this.setState(
      {
        page: 0,
        searchParams: params,
        loading: true,
      },
      () => {
        this.getList();
      }
    );
  };
  clear = () => {
    let searchParams = {};
    this.state.selectorItem.searchForm.map(form => {
      searchParams[form.id] = form.defaultValue;
    });
    this.setState(
      {
        page: 0,
        searchParams: searchParams,
      },
      () => {
        this.getList();
      }
    );
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

  handleOk = () => {
    const { selectedData } = this.state;
    console.log(selectedData);
    this.props.onOk(selectedData);
  };

  onCancel = () => {
    this.setState({ expandedRowKeys: [] });
    this.props.onCancel && this.props.onCancel();
  };
  /**
   * 根据selectedData刷新当页selection
   */
  refreshSelected() {
    let { selectorItem, selectedData, data, rowSelection } = this.state;
    let nowSelectedRowKeys = [];
    selectedData.map(selected => {
      data.map(item => {
        if (item.lineList) {
          item.lineList.map(o => {
            if (o.contractLineId == selected) {
              nowSelectedRowKeys.push(o.contractLineId);
              this.setState({
                expandedRowKeys: [item.contractHeaderId],
                selectedData: [
                  {
                    ...o,
                    contractNumber: item.contractNumber,
                    contractId: item.contractHeaderId,
                  },
                ],
              });
            }
          });
        }
      });
    });
    rowSelection.selectedRowKeys = nowSelectedRowKeys;
    this.setState({ rowSelection });
  }
  //选项改变时的回调，重置selection
  onSelectChange = (selectedRowKeys, selectedRows) => {
    let { rowSelection, selectedData } = this.state;
    let obj = {};

    console.log(selectedRowKeys);
    console.log(selectedRows);
    console.log(selectedData);
    console.log(rowSelection);

    selectedRowKeys.map(item => (obj[item] = true));
    selectedData = selectedData.concat(selectedRows);
    selectedData = selectedData.filter(item => obj[item.id]);
    rowSelection.selectedRowKeys = selectedRowKeys;
    this.setState({ rowSelection, selectedData });
  };

  //渲染额外行
  expandedRowRender = (record, index) => {
    const { innerColumns, rowSelection } = this.state;
    let data = record.invoiceLineList.map(item => {
      return {
        invoice: record,
        ...item,
      };
    });
    return (
      <Table
        rowKey="id"
        columns={innerColumns}
        dataSource={data}
        pagination={false}
        rowSelection={{
          ...rowSelection,
          onChange: (key, row) => this.onSelectChange(key, row, record),
        }}
      />
    );
  };

  render() {
    const { visible, onCancel, afterClose } = this.props;
    const {
      data,
      pagination,
      loading,
      columns,
      selectorItem,
      selectedData,
      rowSelection,
      inputValue,
      searchForm,
      invoiceId,
      showInvoiceDetail,
      expandedRowKeys,
    } = this.state;
    return (
      <Modal
        title="从票夹导入"
        visible={visible}
        onCancel={this.onCancel}
        afterClose={afterClose}
        bodyStyle={{
          height: '65vh',
          overflow: 'auto',
        }}
        width={800}
        onOk={this.handleOk}
      >
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} />
        <div className="table-header">
          <div className="table-header-title">
            {this.$t('common.total', { total: pagination.total })}
          </div>
        </div>
        <Table
          rowKey="id"
          columns={columns}
          className="components-table-demo-nested"
          dataSource={data}
          pagination={pagination}
          onChange={this.onChangePager}
          loading={loading}
          bordered
          expandedRowRender={this.expandedRowRender}
        />

        {/* 发票详情弹出框 */}
        <InvoiceDetail
          cancel={() => {
            this.setState({ showInvoiceDetail: false });
          }}
          id={invoiceId}
          visible={showInvoiceDetail}
          validate={true}
        />
      </Modal>
    );
  }
}
SelectInvoice.propTypes = {
  visible: PropTypes.bool, //对话框是否可见
  onOk: PropTypes.func, //点击OK后的回调，当有选择的值时会返回一个数组
  onCancel: PropTypes.func, //点击取消后的回调
  afterClose: PropTypes.func, //关闭后的回调
  type: PropTypes.string, //选择类型
  selectedData: PropTypes.array, //默认选择的值id数组
  params: PropTypes.object, //搜索时额外需要的参数,如果对象内含有组件内存在的变量将替换组件内部的数值
  selectorItem: PropTypes.object, //组件查询的对象，如果存在普通配置没法实现的可单独传入，例如参数在url中间动态变换时，表单项需要参数搜索时
  single: PropTypes.bool, //是否单选
};

SelectInvoice.defaultProps = {
  afterClose: () => {},
  extraParams: {},
  single: false,
};
function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(SelectInvoice);
