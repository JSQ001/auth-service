import React, { Component } from 'react';
import { Button, Divider, Popconfirm, message } from 'antd';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import NewPage from './new';

import config from 'config';
import service from '../service';

class PageManager extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'pageName',
          label: '页面名称',
          colSpan: 6,
        },
      ],
      columns: [
        {
          dataIndex: 'pageName',
          title: '页面名称',
        },
        {
          dataIndex: 'fullRouter',
          title: '路由',
        },
        {
          dataIndex: 'fullUrl',
          title: 'url',
        },
        {
          dataIndex: 'filePath',
          title: '文件地址',
        },
        {
          dataIndex: 'id',
          title: '操作',
          align: 'center',
          width: 120,
          render: (value, record) => (
            <span>
              <a
                onClick={() => {
                  this.edit(record);
                }}
              >
                编辑
              </a>
              <Divider type="vertical" />
              <Popconfirm onConfirm={() => this.delete(value)} title="确定删除?">
                <a>删除</a>
              </Popconfirm>
            </span>
          ),
        },
      ],
      visible: false,
      editModel: {},
    };
  }

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
    this.setState({ visible: true });
  };

  //删除
  delete = id => {
    service
      .deletePage(id)
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
    this.setState({ editModel: record, visible: true });
  };

  //侧拉关闭
  handleClose = flag => {
    this.setState({ visible: false, editModel: {} });
    flag && this.table.reload();
  };

  render() {
    const { searchForm, columns, visible, editModel } = this.state;
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
          url={`${config.baseUrl}/api/page/list/query/by/cond`}
        />
        <SlideFrame
          title={!editModel.id ? '新建页面' : '编辑页面'}
          show={visible}
          onClose={() => this.handleClose()}
        >
          <NewPage onClose={this.handleClose} params={editModel} />
        </SlideFrame>
      </div>
    );
  }
}

export default PageManager;
