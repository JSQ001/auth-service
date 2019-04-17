/**
 * create 2019-01-14 fudebao
 */
import React, { Component } from 'react';
import { Modal, message, Tag } from 'antd';
import chooserData from 'chooserData';
import Table from 'widget/table';
import httpFetch from 'share/httpFetch';
import SearchArea from 'widget/search-area';
import PropTypes from 'prop-types';

class NewListSelector extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectorItem: {},
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

  componentWillReceiveProps(nextProps) {
    //显示
    if (nextProps.visible && !this.props.visible) {
      if (nextProps.selectorItem) {
        this.setState({
          selectorItem: nextProps.selectorItem,
          selectedRows: JSON.parse(JSON.stringify(nextProps.selectedData)),
          selectedRowKeys: nextProps.selectedData.map(item => item[nextProps.selectorItem.key]),
        });
      } else {
        this.setContent(nextProps).then(() => {
          this.getList();
        });
      }
    }

    //关闭
    if (!nextProps.visible && this.props.visible) {
      //重置搜索条件
      this.formRef && this.formRef.clearSearchAreaSelectData();
      this.setState({
        isAll: false,
        pagination: {
          current: 1,
          showSizeChanger: true,
          pageSize: 10,
          pageSizeOptions: ['5', '10', '20', '50', '100'],
          showTotal: total => `共 ${total} 条数据`,
        },
        tableData: [],
      });
    }
  }

  // 设置搜索区域和表格配置信息
  setContent = nextProps => {
    return new Promise((resolve, rejetc) => {
      let selectorItem = chooserData[nextProps.type];
      if (!selectorItem) {
        message.error('list-selector无效的type');
        rejetc();
        return;
      }
      this.setState(
        {
          selectorItem,
          selectedRows: JSON.parse(JSON.stringify(nextProps.selectedData)),
          selectedRowKeys: nextProps.selectedData.map(item => item[selectorItem.key]),
        },
        () => {
          resolve();
        }
      );
    });
  };

  //获取表格数据
  getList = () => {
    const { url, key, listKey } = this.state.selectorItem;
    const { extraParams, method } = this.props;
    let {
      pagination: { pageSize, current },
      pagination,
      searchParams,
      isAll,
      selectedRows,
      selectedRowKeys,
    } = this.state;

    let params = { ...extraParams, ...searchParams, size: pageSize, page: current - 1 };
    this.setState({ loading: true });
    httpFetch[method](url, params)
      .then(res => {
        if (listKey && typeof res.data === 'object') {
          let result = JSON.stringify(res.data);
          res.data = new Function(`try {return ${result}.${listKey} } catch(e) {}`)();
        }
        if (isAll) {
          res.data &&
            res.data.map(o => {
              if (selectedRowKeys.indexOf(o[key]) < 0) {
                selectedRowKeys.push(o[key]);
                selectedRows.push(o);
              }
            });
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

  //搜素
  search = values => {
    let { pagination, searchParams } = this.state;
    pagination.current = 1;
    this.setState({ searchParams: { ...searchParams, ...values }, pagination }, this.getList);
  };

  //清除搜索条件
  clear = () => {
    let { pagination } = this.state;
    pagination.current = 1;
    this.setState({ searchParams: {}, pagination }, this.getList);
  };

  //分页改变
  tableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  //table行点击事件
  onTableClick = record => {
    let {
      selectorItem: { key },
      selectedRows,
      selectedRowKeys,
    } = this.state;
    let { single } = this.props;

    //单选
    if (single) {
      this.setState({ selectedRows: [record], selectedRowKeys: [record[key]] });
      return;
    }

    let index = selectedRowKeys.indexOf(record[key]);
    if (index >= 0) {
      selectedRows.splice(index, 1);
      selectedRowKeys.splice(index, 1);
    } else {
      selectedRows.push(record);
      selectedRowKeys.push(record[key]);
    }
    this.setState({ selectedRows, selectedRowKeys });
  };

  //取消选中
  onTagClose = index => {
    let { selectedRows, selectedRowKeys } = this.state;
    selectedRows.splice(index, 1);
    selectedRowKeys.splice(index, 1);
    this.setState({ selectedRows, selectedRowKeys });
  };

  //取消
  onCancel = () => {
    this.props.onCancel && this.props.onCancel();
  };

  //确定
  onOk = () => {
    this.props.onOk &&
      this.props.onOk({
        result: this.state.selectedRows,
        type: this.props.type,
      });
  };

  //全选/取消全选
  onSelectAll = (selected, rows, changeRows) => {
    let {
      tableData,
      selectedRowKeys,
      selectedRows,
      selectorItem: { key },
    } = this.state;
    if (selected) {
      tableData.map(o => {
        if (selectedRowKeys.indexOf(o[key]) < 0) {
          selectedRowKeys.push(o[key]);
          selectedRows.push(o);
        }
      });
    } else {
      tableData.map(o => {
        let index = selectedRowKeys.indexOf(o => o[key]);
        selectedRowKeys.splice(index, 1);
        selectedRows.splice(index, 1);
      });
    }
    this.setState({ isAll: selected, selectedRowKeys, selectedRows });

    this.props.onSelectAll && this.props.onSelectAll(selected, selectedRows, changeRows);
  };

  render() {
    const { visible, labelKey, single, modalWidth, showDetail, okLoading, okText } = this.props;
    const {
      selectorItem: { columns, searchForm, title, key },
      tableData,
      loading,
      pagination,
      selectedRows,
      selectedRowKeys,
    } = this.state;

    const rowSelection = {
      type: single ? 'radio' : 'checkbox',
      selectedRowKeys,
      onSelect: this.onTableClick,
      onSelectAll: this.onSelectAll,
    };

    return (
      <Modal
        title={this.$t(title)}
        width={modalWidth || 800}
        visible={visible}
        onCancel={this.onCancel}
        onOk={this.onOk}
        bodyStyle={{ maxHeight: '65vh', overflow: 'auto' }}
        confirmLoading={okLoading}
        okText={okText}
      >
        {searchForm &&
          !!searchForm.length && (
            <SearchArea
              searchForm={searchForm}
              submitHandle={this.search}
              clearHandle={this.clear}
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
              columns={columns.map(o => ({ ...o, title: this.$t(o.title) }))}
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
      </Modal>
    );
  }
}

NewListSelector.propTypes = {
  visible: PropTypes.bool, //对话框是否可见
  onOk: PropTypes.func, //点击OK后的回调，当有选择的值时会返回一个数组
  onCancel: PropTypes.func, //点击取消后的回调
  afterClose: PropTypes.func, //关闭后的回调
  type: PropTypes.string, //选择类型
  selectedData: PropTypes.array, //默认选择的值id数组
  extraParams: PropTypes.object, //搜索时额外需要的参数,如果对象内含有组件内存在的变量将替换组件内部的数值
  selectorItem: PropTypes.object, //组件查询的对象，如果存在普通配置没法实现的可单独传入，例如参数在url中间动态变换时，表单项需要参数搜索时
  single: PropTypes.bool, //是否单选
  onSelectAll: PropTypes.func, //点击选择全部时的回调
  labelKey: PropTypes.string, //Tag内显示的
  hideFooter: PropTypes.bool, //是否去掉底部确定取消按钮
  modalWidth: PropTypes.number, //modal的宽度
  okLoading: PropTypes.bool, //ok按钮loading
};

NewListSelector.defaultProps = {
  afterClose: () => {},
  extraParams: {},
  single: false,
  method: 'get',
  selectAll: false,
  showDetail: true,
  selectAllLoading: false,
  onSelectAll: () => {},
  selectedData: [],
};

export default NewListSelector;
