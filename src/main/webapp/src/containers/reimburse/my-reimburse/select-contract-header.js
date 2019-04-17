/* eslint-disable react/destructuring-assignment,no-unused-vars,react/sort-comp,array-callback-return,react/no-access-state-in-setstate,no-undef,prefer-const,no-shadow */
import React from 'react';
import { connect } from 'dva';
import { Modal, message } from 'antd';
import Table from 'widget/table';
import httpFetch from 'share/httpFetch';
import SearchArea from 'widget/search-area';
import 'styles/pre-payment/my-pre-payment/select-contract.scss';
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
      selectedData: [], // 已经选择的数据项
      searchParams: {}, // 搜索需要的参数
      rowSelection: {
        type: 'radio',
        selectedRowKeys: [],
        onChange: this.onSelectChange,
        onSelect: this.onSelectItem,
      },
      expandedRowKeys: [],
      params: {},
      searchForm: [
        { type: 'input', label: '合同编号', id: 'contractNumber' },
        { type: 'input', label: '合同类型', id: 'contractTypeName' },
        { type: 'input', label: '合同名称', id: 'contractName' },
      ],
      columns: [
        { title: '合同编号', dataIndex: 'contractNumber', align: 'center' },
        { title: '合同类型', dataIndex: 'contractTypeName', align: 'center' },
        { title: '合同名称', dataIndex: 'contractName', align: 'center' },
        { title: '币种', dataIndex: 'contractCurrency', align: 'center' },
        { title: '总金额', dataIndex: 'contractAmount', align: 'center' },
      ],
    };
  }

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
    const searchParams = {};
    this.state.selectorItem.searchForm.map(form => {
      searchParams[form.id] = form.defaultValue;
    });
    this.setState(
      {
        page: 0,
        searchParams,
      },
      () => {
        this.getList();
      }
    );
  };

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
            this.refreshSelected(); // 刷新当页选择器
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
      type: this.props.type,
    });
  };

  /**
   * 根据selectedData刷新当页selection
   */
  refreshSelected() {
    const { selectedData, data, rowSelection } = this.state;
    const nowSelectedRowKeys = [];
    selectedData.map(selected => {
      data.map(item => {
        if (item.lineList) {
          item.lineList.map(o => {
            if (o.contractLineId === selected) {
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
    let { selectedData, selectorItem, rowSelection } = this.state;
    selectedData = [record];
    rowSelection.selectedRowKeys = [record.contractHeaderId];
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
    const { data, pagination, loading, columns, searchForm, rowSelection } = this.state;
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
        {searchForm && searchForm.length > 0 ? (
          <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} />
        ) : null}
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
          rowKey={record => record.contractHeaderId}
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
