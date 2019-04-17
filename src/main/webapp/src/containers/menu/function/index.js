import React, { Component } from 'react';
import { Button, Divider, Popconfirm, message } from 'antd';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import NewPage from './new';
import ListSeletor from 'widget/new-list-selector';

import config from 'config';
import service from '../service';

class FunctionManager extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'functionName',
          label: '功能名称',
          colSpan: 6,
        },
      ],
      columns: [
        {
          dataIndex: 'sequenceNumber',
          title: '序号',
          align: 'center',
          width: 80,
        },
        {
          dataIndex: 'functionName',
          title: '功能名称',
        },
        {
          dataIndex: 'param',
          title: '参数',
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
                  this.allowedPage(value);
                }}
              >
                已分配页面
              </a>
              <Divider type="vertical" />
              <a
                onClick={() => {
                  this.allowPage(value);
                }}
              >
                分配页面
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
      allowPageVisible: false,
      allowedPageVisible: false,
      functionId: '',
      editModel: {},
    };
  }

  //分配页面
  allowedPage = id => {
    this.setState({ functionId: id, allowedPageVisible: true });
  };

  //选择页面后的回调
  onOk = values => {
    if (!values.result.length) {
      this.setState({ functionId: '', allowedPageVisible: false });
      return;
    }
    service
      .deleteFunctionPage(values.result.map(item => item.id))
      .then(() => {
        this.setState({ functionId: '', allowedPageVisible: false });
        message.success('取消分配成功');
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //分配页面
  allowPage = id => {
    this.setState({ functionId: id, allowPageVisible: true });
  };

  //选择页面后的回调
  handleOk = values => {
    if (!values.result.length) {
      this.setState({ functionId: '', allowPageVisible: false });
      return;
    }
    const { functionId } = this.state;
    values = values.result.map(item => {
      return {
        functionId,
        pageId: item.id,
      };
    });
    service
      .functionAllowPage(values)
      .then(() => {
        this.setState({ functionId: '', allowPageVisible: false });
        message.success('分配成功');
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  handleCancel = () => {
    this.setState({ functionId: '', allowPageVisible: false, allowedPageVisible: false });
  };

  //搜索
  handleSearch = (values = {}) => {
    this.table.search(values);
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
      .deleteFunction(id)
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
    flag && this.table.reload();
  };

  render() {
    const {
      searchForm,
      columns,
      slideVisible,
      editModel,
      allowPageVisible,
      functionId,
      allowedPages,
      allowedPageVisible,
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
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.baseUrl}/api/function/list/query/by/cond`}
        />
        <SlideFrame
          title={!editModel.id ? '新建页面' : '编辑页面'}
          show={slideVisible}
          onClose={() => this.handleClose()}
        >
          <NewPage onClose={this.handleClose} params={editModel} />
        </SlideFrame>
        <ListSeletor
          type="select_pages"
          labelKey="pageName"
          visible={allowPageVisible}
          extraParams={{ functionId }}
          selectedData={allowedPages}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        />
        <ListSeletor
          type="allowed_pages"
          labelKey="pageName"
          visible={allowedPageVisible}
          extraParams={{ functionId }}
          onOk={this.onOk}
          onCancel={this.handleCancel}
          okText="取消分配"
        />
      </div>
    );
  }
}

export default FunctionManager;
