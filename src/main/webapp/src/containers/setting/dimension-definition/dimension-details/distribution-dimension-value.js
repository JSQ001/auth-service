import React, { Component } from 'react';
import { Button, Input, Icon, Badge, Popconfirm, message } from 'antd';
import CustomTable from 'widget/table';
import BasicInfo from 'widget/basic-info';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import ListSelector from 'widget/list-selector';
import config from 'config';
import service from './dimension-group-service';

const Search = Input.Search;

class DistributionDimension extends Component {
  constructor(props) {
    super(props);
    this.state = {
      infoData: {},
      infoList: [
        { type: 'input', id: 'dimensionItemGroupCode', label: '维值组代码' },
        { type: 'input', id: 'dimensionItemGroupName', label: '维值组名称' },
        { type: 'switch', id: 'enabled', label: '状态' },
      ],
      data: [],
      columns: [
        { title: '维值代码', dataIndex: 'dimensionItemCode', align: 'center' },
        { title: '维值名称', dataIndex: 'dimensionItemName', align: 'center' },
        {
          title: '状态',
          dataIndex: 'enabled',
          align: 'center',
          render: value => {
            return <Badge status={value ? 'success' : 'error'} text={value ? '启用' : '禁用'} />;
          },
        },
        {
          title: '操作',
          dataIndex: 'tableName3',
          align: 'center',
          render: (value, record, index) => (
            <Popconfirm
              title="你确定删除？"
              onConfirm={() => this.delete(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <a>删除</a>
            </Popconfirm>
          ),
        },
      ],
      pagination: {
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0,
        pageSize: 10,
        current: 1,
        showTotal: total => `共有${total}条数据`,
        onChange: this.indexChange,
        onShowSizeChange: this.sizeChange,
      },
      page: 0,
      size: 10,
      visible: false,
      selectedKey: [],
      loading: false,
      dimensionItemGroupId: props.match.params.id,
      searchParams: {},
      selectorItem: {
        title: '分配子维值',
        url: `${config.mdataUrl}/api/dimension/item/group/subDimensionItem/filter`,
        searchForm: [
          { type: 'input', id: 'dimensionItemCode', label: '维值代码' },
          { type: 'input', id: 'dimensionItemName', label: '维值名称' },
          {
            type: 'value_list',
            id: 'enabled',
            label: '状态',
            options: [{ label: '启用', value: true }, { label: '禁用', value: false }],
          },
        ],
        columns: [
          { title: '维值代码', dataIndex: 'dimensionItemCode', align: 'center' },
          { title: '维值名称', dataIndex: 'dimensionItemName', align: 'center' },
          {
            title: '状态',
            dataIndex: 'enabled',
            align: 'center',
            render: value => {
              return <Badge status={value ? 'success' : 'error'} text={value ? '启用' : '禁用'} />;
            },
          },
        ],
        key: 'id',
      },
    };
  }

  // 生命周期获取数据
  componentDidMount() {
    this.getList();
    this.getDimensionGroup();
  }

  // 维值组详情
  getDimensionGroup = () => {
    const id = this.state.dimensionItemGroupId;
    service
      .getDimensionGroupDetail(id)
      .then(res => {
        this.setState({ infoData: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 获取数据
  getList = () => {
    const { dimensionItemGroupId, page, size, searchParams, pagination } = this.state;
    const params = { dimensionItemGroupId, page, size, ...searchParams };
    this.setState({ loading: true });
    service
      .getDimensionItem(params)
      .then(res => {
        const total = Number(res.headers['x-total-count']);
        this.setState({
          data: res.data,
          loading: false,
          pagination: { ...pagination, total },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 删除
  delete = id => {
    const { dimensionItemGroupId } = this.state;
    service
      .deleteDimensionItem(dimensionItemGroupId, id)
      .then(() => {
        message.success('删除成功');
        this.mySetState();
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 批量删除
  batchDelete = () => {
    const { selectedKey, dimensionItemGroupId } = this.state;
    service
      .batchDeleteDimensionItem(dimensionItemGroupId, selectedKey)
      .then(() => {
        message.success('删除成功');
        this.setState({ selectedKey: [] });
        this.mySetState();
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 删除判断是否有数据
  deleteClick = () => {
    const { selectedKey } = this.state;
    if (!selectedKey.length) {
      message.warning('请选择你要删除的内容');
    }
  };

  // 搜索
  search = value => {
    this.mySetState({ searchParams: { dimensionItemCode: value } });
  };

  // 设置state
  mySetState = params => {
    const pagination = this.state.pagination;
    this.setState({ page: 0, pagination: { ...pagination, current: 1 }, ...params }, this.getList);
  };

  // 表格选择
  selectChange = key => {
    this.setState({ selectedKey: key });
  };

  // 跳转到某页
  indexChange = (page, size) => {
    const pagination = this.state.pagination;
    pagination.current = page;
    this.setState({ page: page - 1, pagination }, this.getList);
  };

  // 改变pagesize
  sizeChange = (current, size) => {
    const pagination = this.state.pagination;
    pagination.current = 1;
    pagination.pageSize = size;
    this.setState({ page: 0, size, pagination }, this.getList);
  };

  // 返回
  onBackClick = e => {
    const dimensionId = this.props.match.params.dimensionId;
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/admin-setting/dimension-definition/dimension-details/${dimensionId}?tabKey=2`,
      })
    );
  };

  // 弹出框取消
  onDimensionCancel = () => {
    this.setState({ visible: false });
  };

  // 分配子维值
  onDimensionOk = values => {
    const { dimensionItemGroupId } = this.state;
    const dimensionItemIds = values.result.map(item => item.id);
    if (dimensionItemIds.length) {
      service
        .distributeDimensionItem(dimensionItemGroupId, dimensionItemIds)
        .then(() => {
          message.success('分配子维值成功');
          this.setState({ visible: false });
          this.getList();
        })
        .catch(err => message.error(err.response.data.message));
    } else {
      message.warning('请选择要分配的维值');
    }
  };

  render() {
    const {
      infoList,
      infoData,
      columns,
      data,
      pagination,
      visible,
      loading,
      dimensionItemGroupId,
      selectorItem,
      selectedKey,
    } = this.state;
    const rowSelection = {
      onChange: this.selectChange,
      selectedRowKeys: selectedKey,
    };

    return (
      <div>
        <BasicInfo infoList={infoList} infoData={infoData} isHideEditBtn />
        <div style={{ margin: '20px 0' }}>
          <Button
            type="primary"
            style={{ marginRight: '15px' }}
            onClick={() => {
              this.setState({ visible: true });
            }}
          >
            分配子维值
          </Button>
          <Button onClick={this.deleteClick} style={{ padding: '0' }}>
            {selectedKey.length ? (
              <Popconfirm
                title="你确定删除？"
                onConfirm={this.batchDelete}
                okText="确定"
                cancelText="取消"
              >
                <div style={{ lineHeight: '30px', padding: '0 15px' }}>删除</div>
              </Popconfirm>
            ) : (
              <div style={{ lineHeight: '30px', padding: '0 15px' }}>删除</div>
            )}
          </Button>
          <Search
            placeholder="请输入维值代码"
            onSearch={this.search}
            style={{ width: '300px', float: 'right' }}
          />
        </div>
        <CustomTable
          rowKey={record => record.id}
          dataSource={data}
          columns={columns}
          rowSelection={rowSelection}
          pagination={pagination}
          onChange={this.pageChange}
          loading={loading}
        />
        <p style={{ margin: '20px 0' }}>
          <a onClick={this.onBackClick}>
            <Icon type="rollback" />返回
          </a>
        </p>

        {/* 分配子维值 */}
        <ListSelector
          visible={visible}
          onOk={this.onDimensionOk}
          onCancel={this.onDimensionCancel}
          selectorItem={selectorItem}
          extraParams={{ dimensionItemGroupId }}
        />
      </div>
    );
  }
}

export default connect()(DistributionDimension);
