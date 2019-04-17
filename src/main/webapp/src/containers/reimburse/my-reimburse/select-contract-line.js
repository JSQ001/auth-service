/* eslint-disable react/destructuring-assignment,no-unused-vars,react/sort-comp,array-callback-return,react/no-access-state-in-setstate,no-undef,prefer-const,no-shadow */
import React from 'react';
import { connect } from 'dva';
import { Modal, message, Popover } from 'antd';
import Table from 'widget/table';
import httpFetch from 'share/httpFetch';
import moment from 'moment';
// import SearchArea from 'widget/search-area';
import 'styles/pre-payment/my-pre-payment/select-contract.scss';
import config from 'config';

// import PropTypes from 'prop-types';

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
      selectedData: [], // 已经选择的数据项
      searchParams: {}, // 搜索需要的参数
      rowSelection: {
        type: 'radio',
        selectedRowKeys: [],
        onChange: this.onSelectChange,
        onSelect: this.onSelectItem,
      },
      contractInfo: [],
      expandedRowKeys: [],
      params: {},

      columns: [
        {
          title: '付款计划序号',
          dataIndex: 'lineNumber',
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          align: 'center',
          render: desc => (
            <span>
              <Popover content={this.filterMoney(desc, 2)}>{this.filterMoney(desc, 2)}</Popover>
            </span>
          ),
        },
        {
          title: '计划付款日期',
          dataIndex: 'dueDate',
          align: 'center',
          render: desc => (
            <span>
              <Popover content={moment(desc).format('YYYY-MM-DD')}>
                {desc ? moment(desc).format('YYYY-MM-DD') : ''}
              </Popover>
            </span>
          ),
        },
        {
          title: '已关联',
          dataIndex: 'associatedAmount',
          align: 'center',
          render: desc => (
            <span>
              <Popover content={this.filterMoney(desc, 2)}>{this.filterMoney(desc, 2)}</Popover>
            </span>
          ),
        },
        {
          title: '可关联',
          dataIndex: 'availableAmount',
          align: 'center',
          render: desc => (
            <span>
              <Popover content={this.filterMoney(desc, 2)}>{this.filterMoney(desc, 2)}</Popover>
            </span>
          ),
        },
      ],
    };
  }

  // 得到数据
  getList() {
    const { selectorItem } = this.state;
    const { page, pageSize } = this.state;
    const searchParams = { ...this.state.searchParams, ...this.state.params };

    let url = `${
      config.baseUrl
    }/contract/api/contract/document/relations/associate/query?page=${page}&size=${pageSize}`;
    for (const key in searchParams) {
      if (searchParams[key]) {
        url += `&${key}=${searchParams[key]}`;
      }
    }
    httpFetch
      .get(url)
      .then(response => {
        if (response.status == 200) {
          if (response.data.length) {
            this.setState(
              {
                data: response.data[0].lineList,
                contractInfo: response.data,
                loading: false,
                pagination: {
                  total: Number(response.headers['x-total-count']),
                  onChange: this.onChangePager,
                  current: this.state.page + 1,
                },
              },
              () => {
                this.refreshSelected(); // 刷新当页选择器
              }
            );
          }
        }
      })
      .catch(e => {
        message.error('获取数据失败，请稍后重试或联系管理员');
        this.setState({
          loading: false,
        });
      });
  }

  /**
   * 分页方法
   */
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

  componentDidMount() {
    if (this.props.viewContract) {
      let columns = this.state.columns;
      this.setState({
        columns: [
          ...columns,
          {
            title: '操作',
            dataIndex: 'contractHeaderId',
            align: 'center',
            render: value => {
              return <a onClick={() => this.props.viewContract(value)}>查看</a>;
            },
          },
        ],
      });
    }
  }
  /**
   * 每次父元素进行setState时调用的操作，判断nextProps内是否有type的变化
   * 如果selectedData有值则代表有默认值传入需要替换本地已选择数组，
   * 如果没有值则需要把本地已选择数组置空
   * @param nextProps 下一阶段的props
   */
  componentWillReceiveProps = nextProps => {
    //console.log(nextProps);
    if (!this.props.visible && nextProps.visible) {
      this.setState(
        {
          page: 0,
          selectedData: nextProps.selectedData,
          params: nextProps.params,
        },
        () => {
          this.getList();
        }
      );
    }
  };

  handleOk = () => {
    this.setState({ expandedRowKeys: [] });
    this.props.onOk({
      result: this.state.selectedData,
    });
  };

  /**
   * 根据selectedData刷新当页selection
   */
  refreshSelected() {
    const { selectedData, data, rowSelection, contractInfo } = this.state;
    const nowSelectedRowKeys = [];
    selectedData.map(selected => {
      data.map(item => {
        if (item.contractLineId === selected) {
          nowSelectedRowKeys.push(item.contractLineId);
          this.setState({
            expandedRowKeys: [contractInfo[0].contractHeaderId],
            selectedData: [
              {
                ...item,
                contractNumber: contractInfo[0].contractNumber,
                contractId: contractInfo[0].contractHeaderId,
              },
            ],
          });
        }
      });
    });

    rowSelection.selectedRowKeys = nowSelectedRowKeys;
    this.setState({ rowSelection });
  }

  // 选项改变时的回调，重置selection
  onSelectChange = (selectedRowKeys, selectedRows) => {
    const { rowSelection } = this.state;
    rowSelection.selectedRowKeys = selectedRowKeys;
    this.setState({ rowSelection });
  };

  /**
   * 选择单个时的方法，遍历selectedData，根据是否选中进行插入或删除操作
   * @param record 被改变的项
   * @param selected 是否选中
   */
  onSelectItem = (record, selected) => {
    let { selectedData, selectorItem } = this.state;
    if (this.props.single) {
      selectedData = [record];
    } else if (!selected) {
      selectedData.map((selected, index) => {
        if (selected[selectorItem.key] === record[selectorItem.key]) {
          selectedData.splice(index, 1);
        }
      });
    } else {
      selectedData.push(record);
    }
    this.setState({ selectedData });
  };

  // 点击行时的方法，遍历遍历selectedData，根据是否选中进行遍历遍历selectedData和rowSelection的插入或删除操作
  handleRowClick = record => {
    let { selectedData, selectorItem, rowSelection, contractInfo } = this.state;
    //设置报账单所需数据
    record.contractId = contractInfo[0].contractHeaderId;
    record.contractNumber = contractInfo[0].contractNumber;
    selectedData = [record];
    rowSelection.selectedRowKeys = [record.contractLineId];
    this.setState({
      selectedData,
      rowSelection,
    });
  };

  onCancel = () => {
    this.setState({ expandedRowKeys: [] });
    if (this.props.onCancel) {
      this.props.onCancel();
    }
  };

  render() {
    const { visible, afterClose } = this.props;
    const { data, pagination, loading, columns, rowSelection } = this.state;
    return (
      <Modal
        title="选择合同"
        visible={visible}
        onCancel={this.onCancel}
        afterClose={afterClose}
        width={800}
        onOk={this.handleOk}
        className="list-selector select-contract"
      >
        <div className="table-header">
          <div className="table-header-title">
            {this.$t('common.total', { total: pagination.total })}
          </div>
        </div>
        <Table
          columns={columns}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          rowSelection={rowSelection}
          onRow={record => {
            return {
              onClick: e => this.handleRowClick(record),
            };
          }}
          size="middle"
          rowKey={record => record.contractLineId}
        />
      </Modal>
    );
  }
}

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
