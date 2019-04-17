import React, { Component } from 'react';
import { Button, Divider, Popconfirm, message, Icon } from 'antd';
import SearchArea from 'widget/search-area';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import NewPage from './new';
import ListSeletor from 'widget/new-list-selector';
import service from '../service';

class ContentManager extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'contentName',
          label: '目录名称',
          colSpan: 6,
        },
      ],
      columns: [
        {
          dataIndex: 'contentName',
          title: '目录名称',
        },
        {
          dataIndex: 'icon',
          title: '图标',
          align: 'center',
          render: value => <Icon type={value} />,
          width: 100,
        },
        {
          dataIndex: 'sequenceNumber',
          title: '序号',
          align: 'center',
          width: 80,
        },
        {
          dataIndex: 'id',
          title: '操作',
          align: 'center',
          width: 280,
          render: (value, record) => (
            <span>
              <a
                onClick={() => {
                  this.allowedFunction(value);
                }}
              >
                已分配功能
              </a>
              <Divider type="vertical" />
              <a
                onClick={() => {
                  this.allowFunction(value);
                }}
              >
                分配功能
              </a>
              <Divider type="vertical" />
              <a
                onClick={() => {
                  this.edit(record);
                }}
              >
                编辑
              </a>
              <Divider type="vertical" />
              <Popconfirm onConfirm={() => this.delete(value)} title="确定删除？">
                <a>删除</a>
              </Popconfirm>
            </span>
          ),
        },
      ],
      slideVisible: false,
      allowVisible: false,
      allowedVisible: false,
      contentId: '',
      editModel: {},
      allowedPages: [],
      list: [],
      loading: false,
      pagination: {
        current: 1,
        showSizeChanger: true,
        pageSize: 10,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: total => `共 ${total} 条数据`,
      },
      searchParams: {},
    };
  }

  componentDidMount() {
    this.getList();
  }

  getList = () => {
    let {
      pagination: { pageSize, current },
      pagination,
      searchParams,
    } = this.state;
    this.setState({ loading: true });
    service
      .getContentList({ size: pageSize, page: current - 1, ...searchParams })
      .then(res => {
        const list = res.data.map(item => {
          if (item.hasSonContent) {
            item.children = [];
          }
          return { ...item };
        });
        this.setState({
          list,
          loading: false,
          pagination: { ...pagination, total: Number(res.headers['x-total-count']) || 0 },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loading: false });
      });
  };

  //分配功能
  allowedFunction = id => {
    this.setState({ contentId: id, allowedVisible: true });
  };

  //选择功能后的回调
  onOk = values => {
    if (!values.result.length) {
      this.setState({ contentId: '', allowedVisible: false });
      return;
    }
    service
      .deleteContentFunction(values.result.map(item => item.id))
      .then(() => {
        this.setState({ contentId: '', allowedVisible: false });
        message.success('取消分配成功');
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //分配功能
  allowFunction = id => {
    this.setState({ contentId: id, allowVisible: true });
  };

  //选择功能后的回调
  handleOk = values => {
    if (!values.result.length) {
      this.setState({ contentId: '', allowVisible: false });
      return;
    }

    const { contentId } = this.state;
    values = values.result.map(item => ({
      contentId,
      functionId: item.id,
    }));
    service
      .contentAllowFunction(values)
      .then(() => {
        this.setState({ contentId: '', allowVisible: false });
        message.success('分配成功');
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  handleCancel = () => {
    this.setState({ contentId: '', allowVisible: false, allowedVisible: false });
  };

  //搜索
  handleSearch = (values = {}) => {
    this.setState({ searchParams: values }, this.getList);
  };

  //重置搜索条件
  handleClear = () => {
    this.handleSearch();
  };

  //添加
  add = () => {
    this.setState({ slideVisible: true });
  };

  //删除
  delete = id => {
    service
      .deleteContent(id)
      .then(res => {
        message.success('删除成功！');
        this.handleClear();
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //编辑
  edit = record => {
    this.setState({ editModel: record, slideVisible: true });
  };

  //侧拉关闭
  handleClose = flag => {
    this.setState({ slideVisible: false, editModel: {} });
    flag && this.getList();
  };

  //表格展开
  handleExpand = (expanded, record) => {
    if (!expanded || record.children.length > 0) return;

    const { list } = this.state;
    service
      .getChildrenContent(record.id)
      .then(res => {
        record.children = res.data;
        this.setState({ list });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  onTableChange = pagination => {
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  render() {
    const {
      searchForm,
      columns,
      slideVisible,
      editModel,
      allowVisible,
      allowedVisible,
      contentId,
      list,
      pagination,
      loading,
    } = this.state;
    return (
      <div style={{ padding: '10px 0' }}>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.handleClear}
          maxLength={4}
        />
        <Button type="primary" style={{ margin: '10px 0' }} onClick={this.add}>
          添加
        </Button>
        <Table
          rowKey={record => record.id}
          columns={columns}
          dataSource={list}
          onExpand={this.handleExpand}
          onChange={this.onTableChange}
          pagination={pagination}
          size="middle"
          loading={loading}
        />
        <SlideFrame
          title={!editModel.id ? '新建页面' : '编辑页面'}
          show={slideVisible}
          onClose={() => this.handleClose()}
        >
          <NewPage onClose={this.handleClose} params={editModel} />
        </SlideFrame>
        <ListSeletor
          type="select_function"
          labelKey="functionName"
          visible={allowVisible}
          extraParams={{ contentId }}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        />
        <ListSeletor
          type="allowed_functions"
          labelKey="functionName"
          visible={allowedVisible}
          extraParams={{ contentId }}
          onOk={this.onOk}
          onCancel={this.handleCancel}
          okText="取消分配"
        />
      </div>
    );
  }
}

export default ContentManager;
