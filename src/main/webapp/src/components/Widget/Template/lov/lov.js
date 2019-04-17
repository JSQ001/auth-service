/**
 * create 2019-01-14 fudebao
 */
import React, { Component } from 'react';
import { message, Tag } from 'antd';
import Table from 'widget/table';
import httpFetch from 'share/httpFetch';
import SearchArea from 'widget/search-area';
import PropTypes from 'prop-types';

class Lov extends Component {
  constructor(props) {
    super(props);
    this.state = {
      tableData: [],
      loading: false,
      pagination: {
        current: 1,
        showSizeChanger: true,
        pageSize: 10,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: total => `共 ${total} 条数据`,
      },
      searchParams: {},
      selectedRows: [],
      selectedRowKeys: [],
      isAll: false,
    };
  }

  componentDidMount() {
    const { lov, selectedData, single } = this.props;
    let data = [];
    if (selectedData) {
      if (single) {
        data = [selectedData];
      } else {
        data = selectedData;
      }
    }
    this.setState(
      {
        selectedRows: JSON.parse(JSON.stringify(data)),
        selectedRowKeys: data.map(item => item[lov.key]),
      },
      this.getList
    );
  }

  // 获取表格数据
  getList = () => {
    const {
      lov: { url, key, method },
      extraParams,
    } = this.props;
    const {
      pagination: { pageSize, current },
      pagination,
      searchParams,
      isAll,
      selectedRows,
      selectedRowKeys,
    } = this.state;
    const params = { ...extraParams, ...searchParams, size: pageSize, page: current - 1 };
    this.setState({ loading: true });
    if (!url || !method) return;
    httpFetch[method](url, params)
      .then(res => {
        if (isAll) {
          if (res.data) {
            res.data.forEach(o => {
              if (selectedRowKeys.indexOf(o[key]) < 0) {
                selectedRowKeys.push(o[key]);
                selectedRows.push(o);
              }
            });
          }
          this.setState({
            selectedRowKeys,
            selectedRows,
          });
        }
        this.setState({
          tableData: res.data || [],
          loading: false,
          pagination: { ...pagination, total: Number(res.headers['x-total-count']) || 0 },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loading: false });
      });
  };

  // 搜素
  search = values => {
    const { pagination, searchParams } = this.state;
    pagination.current = 1;
    this.setState({ searchParams: { ...searchParams, ...values }, pagination }, this.getList);
  };

  // 清除搜索条件
  clear = () => {
    const { pagination } = this.state;
    pagination.current = 1;
    this.setState({ searchParams: {}, pagination }, this.getList);
  };

  // 分页改变
  tableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  // table行点击事件
  onTableClick = record => {
    const { selectedRows, selectedRowKeys } = this.state;
    const { single } = this.props;
    const {
      lov: { key },
    } = this.props;

    // 单选
    if (single) {
      this.setState({ selectedRows: [record], selectedRowKeys: [record[key]] });
      return;
    }

    const index = selectedRowKeys.indexOf(record[key]);
    if (index >= 0) {
      selectedRows.splice(index, 1);
      selectedRowKeys.splice(index, 1);
    } else {
      selectedRows.push(record);
      selectedRowKeys.push(record[key]);
    }
    this.setState({ selectedRows, selectedRowKeys });
  };

  // 取消选中
  onTagClose = index => {
    const { selectedRows, selectedRowKeys } = this.state;
    selectedRows.splice(index, 1);
    selectedRowKeys.splice(index, 1);
    this.setState({ selectedRows, selectedRowKeys });
  };

  // 取消
  onCancel = () => {
    const { onCancel } = this.props;
    if (onCancel) {
      onCancel();
    }
  };

  // 确定
  onOk = () => {
    const { onOk, single } = this.props;
    const { selectedRows, type } = this.state;
    if (onOk) {
      if (single) {
        onOk({
          result: selectedRows[0] || {},
          type,
        });
      } else {
        onOk({
          result: selectedRows,
          type,
        });
      }
    }
  };

  // 全选/取消全选
  onSelectAll = (selected, rows, changeRows) => {
    const { tableData, selectedRowKeys, selectedRows } = this.state;
    const {
      lov: { key },
    } = this.props;
    if (selected) {
      tableData.forEach(o => {
        if (selectedRowKeys.indexOf(o[key]) < 0) {
          selectedRowKeys.push(o[key]);
          selectedRows.push(o);
        }
      });
    } else {
      tableData.forEach(() => {
        const index = selectedRowKeys.indexOf(item => item[key]);
        selectedRowKeys.splice(index, 1);
        selectedRows.splice(index, 1);
      });
    }
    this.setState({ isAll: selected, selectedRowKeys, selectedRows });

    const { onSelectAll } = this.props;
    if (onSelectAll) {
      onSelectAll(selected, selectedRows, changeRows);
    }
  };

  render() {
    const {
      lov: { columns, key, searchForm },
      labelKey,
      single,
      showDetail,
    } = this.props;
    const { tableData, loading, pagination, selectedRows, selectedRowKeys } = this.state;

    const rowSelection = {
      type: single ? 'radio' : 'checkbox',
      selectedRowKeys,
      onSelect: this.onTableClick,
      onSelectAll: this.onSelectAll,
    };

    return (
      <div>
        {searchForm &&
          !!searchForm.length && (
            <SearchArea
              searchForm={searchForm}
              submitHandle={this.search}
              clearHandle={this.clear}
              // eslint-disable-next-line no-return-assign
              wrappedComponentRef={inst => (this.formRef = inst)}
            />
          )}
        {showDetail && (
          <div id="tag-box" style={{ margin: '10px 0', whiteSpace: 'nowrap', overflow: 'auto' }}>
            {selectedRows.map((item, index) => {
              return (
                <Tag key={item[key]} onClose={() => this.onTagClose(index)} closable={!single}>
                  {item[labelKey]}
                </Tag>
              );
            })}
          </div>
        )}
        {columns &&
          !!columns.length && (
            <Table
              rowKey={record => record[key]}
              columns={columns}
              dataSource={tableData}
              loading={loading}
              size="middle"
              pagination={pagination}
              onChange={this.tableChange}
              rowSelection={rowSelection || null}
              onRow={record => ({
                onClick: () => this.onTableClick(record),
              })}
            />
          )}
      </div>
    );
  }
}

Lov.propTypes = {
  extraParams: PropTypes.object, // 搜索时额外需要的参数,如果对象内含有组件内存在的变量将替换组件内部的数值
  single: PropTypes.bool, // 是否单选
  onSelectAll: PropTypes.func, // 点击选择全部时的回调
};

Lov.defaultProps = {
  extraParams: {},
  single: false,
  onSelectAll: () => {},
};

export default Lov;
