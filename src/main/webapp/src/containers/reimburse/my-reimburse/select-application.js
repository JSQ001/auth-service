import React from 'react';
import { connect } from 'react-redux';
import { injectIntl } from 'react-intl';
import { Modal, message, Button, Input, Row, Col, Popover } from 'antd';
import Table from 'widget/table';
import httpFetch from 'share/httpFetch';
import SearchArea from 'widget/search-area';
import 'styles/pre-payment/my-pre-payment/select-contract.scss';
import moment from 'moment';
import config from 'config';
import PropTypes from 'prop-types';

class SelectContract extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      data: [],
      page: 0,
      pageSize: 10,
      pagination: {
        total: 0,
      },
      selectedData: [], //已经选择的数据项
      searchParams: {}, //搜索需要的参数
      rowSelection: {
        type: 'checkbox',
        selectedRowKeys: [],
        onChange: this.onSelectChange,
        onSelect: this.onSelectItem,
        onSelectAll: this.onSelectAll,
      },
      expandedRowKeys: [],
      searchForm: [{ type: 'input', label: '申请单编号', id: 'contractNumber' }],
      columns: [
        {
          title: '提交日期',
          dataIndex: 'submittedDate',
          align: 'center',
          render: value => {
            return <span>{moment(value).format('YYYY-MM-DD')}</span>;
          },
        },
        { title: '申请单号', dataIndex: 'documentNumber', align: 'center' },
        { title: '申请单名称', dataIndex: 'typeName', align: 'center' },
        {
          title: '总金额',
          dataIndex: 'amount',
          align: 'center',
          render: desc => this.filterMoney(desc),
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          align: 'center',
        },
        // {
        //     title: '操作', dataIndex: 'id', render: (value) => {
        //         return (
        //             <a onClick={() => this.detail(value)}>查看</a>
        //         )
        //     }
        // }
      ],
      lineColumns: [
        {
          title: '序号',
          dataIndex: 'index',
          key: 'lineNumber',
          render: (value, record, index) => {
            return <span>{index + 1}</span>;
          },
        },
        { title: '费用类型', dataIndex: 'expenseTypeName', key: 'expenseTypeName' },
        {
          title: '分摊金额',
          dataIndex: 'amount',
          key: 'amount',
          render: (value, record) => {
            return (
              <span>
                {record.currencyCode} {this.filterMoney(value, 2, true)}
              </span>
            );
          },
        },
        {
          title: '已报账金额',
          dataIndex: 'usedAmount',
          key: 'usedAmount',
          render: (value, record) => {
            return (
              <span>
                {record.currencyCode} {this.filterMoney(value, 2, true)}
              </span>
            );
          },
        },
        {
          title: '可报账金额',
          dataIndex: 'usableAmount',
          key: 'usableAmount',
          render: (value, record) => {
            return (
              <span>
                {record.currencyCode} {this.filterMoney(value, 2, true)}
              </span>
            );
          },
        },
        { title: '公司', dataIndex: 'companyName', key: 'companyName' },
        { title: '部门', dataIndex: 'departmentName', key: 'departmentName' },
      ],
    };
  }

  //查看申请单详情
  detail = id => {
    // let url = menuRoute.getRouteItem('contract-detail', 'key');
    // window.open(url.url.replace(':id', id), '_blank');
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

  //得到数据
  getList() {
    const { applicationParams, type } = this.props.params;
    const { page, pageSize } = this.state;
    applicationParams.page = page;
    applicationParams.size = pageSize;
    let url = `${config.expenseUrl}/api/expense/application/release`;
    httpFetch
      .get(url, applicationParams)
      .then(response => {
        this.setState(
          {
            data: response.data,
            loading: false,
            pagination: {
              total: Number(response.headers['x-total-count']),
              onChange: this.onChangePager,
              current: this.state.page + 1,
            },
          },
          () => {
            this.refreshSelected(); //刷新当页选择器
          }
        );
      })
      .catch(e => {
        message.error('获取数据失败，请稍后重试或联系管理员');
        this.setState({ loading: false });
      });
  }

  onChangePager = page => {
    if (page - 1 !== this.state.page)
      this.setState(
        {
          page: page - 1,
          loading: true,
        },
        () => {
          this.getList();
        }
      );
  };

  componentWillReceiveProps = nextProps => {
    if (nextProps.visible && !this.props.visible) {
      this.setState({ page: 0, selectedData: nextProps.selectedData || [] }, () => {
        this.getList();
      });
    }
  };

  handleOk = () => {
    this.props.onOk(this.state.selectedData);
  };

  /**
   * 根据selectedData刷新当页selection
   */
  refreshSelected() {
    let { selectedData, data, rowSelection } = this.state;
    let nowSelectedRowKeys = [];
    let selected = {};
    selectedData.map(item => (selected[item.id] = item));
    data.map(item => item.lines.map(o => !!selected[o.id] && nowSelectedRowKeys.push(o.id)));
    rowSelection.selectedRowKeys = nowSelectedRowKeys;
    this.setState({ rowSelection });
  }

  //选项改变时的回调，重置selection
  onSelectChange = (selectedRowKeys, selectedRows) => {
    let { rowSelection } = this.state;
    rowSelection.selectedRowKeys = selectedRowKeys;
    this.setState({ rowSelection });
  };

  /**
   * 选择单个时的方法，遍历selectedData，根据是否选中进行插入或删除操作
   * @param record 被改变的项
   * @param selected 是否选中
   */
  onSelectItem = (record, selected) => {
    let { selectedData } = this.state;
    if (this.props.single) {
      selectedData = [record];
    } else {
      if (!selected) {
        selectedData.map((selected, index) => {
          if (selected.id === record.id) {
            selectedData.splice(index, 1);
          }
        });
      } else {
        selectedData.push(record);
      }
    }
    this.setState({ selectedData });
  };

  //点击行时的方法，遍历遍历selectedData，根据是否选中进行遍历遍历selectedData和rowSelection的插入或删除操作
  handleRowClick = record => {
    let { selectedData, rowSelection } = this.state;
    if (this.props.single) {
      selectedData = [record];
      rowSelection.selectedRowKeys = [record.id];
    } else {
      let haveIt = false;
      selectedData.map((selected, index) => {
        if (selected.id === record.id) {
          selectedData.splice(index, 1);
          haveIt = true;
        }
      });
      if (!haveIt) {
        selectedData.push(record);
        rowSelection.selectedRowKeys.push(record.id);
      } else {
        rowSelection.selectedRowKeys.map((item, index) => {
          if (item === record.id) {
            rowSelection.selectedRowKeys.splice(index, 1);
          }
        });
      }
    }
    this.setState({ selectedData, rowSelection });
  };

  //选择当页全部时的判断
  onSelectAll = (selected, selectedRows, changeRows) => {
    changeRows.map(changeRow => this.onSelectItem(changeRow, selected));
  };

  expandedRowhange = values => {
    this.setState({ expandedRowKeys: values });
  };

  //渲染额外行
  expandedRowRender = (record, index) => {
    const { lineColumns, rowSelection } = this.state;
    let data = record.lines.map(item => (item.requestId = record.id));
    return (
      <Table
        columns={lineColumns}
        dataSource={record.lines}
        pagination={false}
        rowSelection={rowSelection}
        rowKey={record => record['id']}
        size="middle"
        onRow={record => ({ onClick: () => this.handleRowClick(record) })}
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
      selectedData,
      searchForm,
      expandedRowKeys,
    } = this.state;
    return (
      <Modal
        title={'选择申请单'}
        visible={visible}
        onCancel={onCancel}
        afterClose={afterClose}
        width={1000}
        onOk={this.handleOk}
        className="list-selector select-contract"
      >
        {searchForm && searchForm.length > 0 ? (
          <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} />
        ) : null}
        <div className="table-header">
          <div className="table-header-title">
            {this.$t({ id: 'common.total' }, { total: pagination.total })}
            &nbsp;<span>/</span>&nbsp;
            {this.$t(
              { id: 'common.total.selected' },
              { total: selectedData.length === 0 ? '0' : selectedData.length }
            )}
          </div>
        </div>
        <Table
          columns={columns}
          className="components-table-demo-nested"
          dataSource={data}
          pagination={pagination}
          loading={loading}
          size="middle"
          expandRowByClick={true}
          expandedRowKeys={expandedRowKeys}
          rowKey={record => record.id}
          onExpandedRowsChange={this.expandedRowhange}
          expandedRowRender={this.expandedRowRender}
        />
      </Modal>
    );
  }
}

SelectContract.propTypes = {
  visible: PropTypes.bool, //对话框是否可见
  onOk: PropTypes.func, //点击OK后的回调，当有选择的值时会返回一个数组
  onCancel: PropTypes.func, //点击取消后的回调
  afterClose: PropTypes.func, //关闭后的回调
  type: PropTypes.string, //选择类型
  selectedData: PropTypes.array, //默认选择的值id数组
  extraParams: PropTypes.object, //搜索时额外需要的参数,如果对象内含有组件内存在的变量将替换组件内部的数值
  selectorItem: PropTypes.object, //组件查询的对象，如果存在普通配置没法实现的可单独传入，例如参数在url中间动态变换时，表单项需要参数搜索时
  single: PropTypes.bool, //是否单选
};

// SelectContract.contextTypes = {
//   router: React.PropTypes.object,
// };

SelectContract.defaultProps = {
  afterClose: () => {},
  extraParams: {},
  single: false,
};

function mapStateToProps() {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(SelectContract);
