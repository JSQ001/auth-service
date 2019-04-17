import React, { Component } from 'react';
import { Button, Divider, Popconfirm, message } from 'antd';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';

import config from 'config';
import service from './service';
import NewLov from './new';

class LovManager extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'lovCode',
          label: this.$t('lov.info.code'),
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'lovName',
          label: this.$t('lov.info.name'),
          colSpan: 6,
        },
      ],
      columns: [
        {
          dataIndex: 'lovCode',
          title: this.$t('lov.info.code'),
        },
        {
          dataIndex: 'lovName',
          title: this.$t('lov.info.name'),
        },
        {
          dataIndex: 'appName',
          title: this.$t('app.info.name'),
        },
        {
          dataIndex: 'apiName',
          title: this.$t('lov.info.interface'),
        },
        {
          dataIndex: 'id',
          title: this.$t('common.operation'),
          align: 'center',
          width: 120,
          render: (value, record) => (
            <span>
              <a
                onClick={() => {
                  this.edit(record);
                }}
              >
                {this.$t('common.edit')}
              </a>
              <Divider type="vertical" />
              <Popconfirm
                onConfirm={() => this.delete(value)}
                title={this.$t('common.confirm.delete')}
              >
                <a>{this.$t('common.delete')}</a>
              </Popconfirm>
            </span>
          ),
        },
      ],
      visible: false,
      editModel: {},
    };
  }

  // 预览
  preview = record => {
    this.setState({ lovCode: record.lovCode, selectorShow: true });
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
    this.setState({ visible: true });
  };

  //删除
  delete = id => {
    service
      .delete(id)
      .then(res => {
        message.success(this.$t('commmon.delete.success'));
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
          {this.$t('common.create')}
        </Button>
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.baseUrl}/api/lov/page`}
        />
        <SlideFrame
          title={!editModel.id ? this.$t('common.create') : this.$t('common.edit')}
          show={visible}
          onClose={() => this.handleClose()}
        >
          <NewLov onClose={this.handleClose} params={editModel} />
        </SlideFrame>
      </div>
    );
  }
}

export default LovManager;
