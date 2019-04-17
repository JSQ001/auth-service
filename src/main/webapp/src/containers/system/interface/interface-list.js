import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Divider, Popconfirm, message } from 'antd';
import config from 'config';
import CustomTable from 'widget/custom-table';
import SearchArea from 'widget/search-area';
import SlideFrame from 'widget/slide-frame';
import NewInterface from './new-interface';
import interfaceService from './interface.service';

class InterfaceList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      formVisible: false,
      record: {},
    };
    this.columns = [
      {
        title: '接口名称',
        dataIndex: 'interfaceName',
        width: 240,
      },
      {
        title: '请求地址',
        dataIndex: 'reqUrl',
        width: 320,
      },
      {
        title: '备注',
        dataIndex: 'remark',
      },
      {
        title: '操作',
        dataIndex: 'id',
        width: 140,
        align: 'center',
        render: (value, record) => (
          <span>
            <a onClick={() => this.edit(record)}>编辑</a>
            <Divider type="vertical" />
            <a onClick={() => this.detail(record)}>详情</a>
            <Divider type="vertical" />
            <Popconfirm
              title="确定删除？"
              onConfirm={() => {
                interfaceService
                  .delete(value)
                  .then(() => {
                    message.success('删除成功！');
                    this.table.reload();
                  })
                  .catch(err => {
                    message.error(err.response.data.message);
                  });
              }}
              okText="确定"
              cancelText="取消"
            >
              <a>删除</a>
            </Popconfirm>
          </span>
        ),
      },
    ];
    this.searchForm = [
      {
        type: 'input',
        id: 'name',
        label: '接口名称',
        colSpan: 6,
      },
    ];
  }

  search = values => {
    this.table.search(values);
  };

  clear = () => {
    this.table.reload();
  };

  back = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/setting/interface/index',
      })
    );
  };

  add = () => {
    this.setState({ formVisible: true });
  };

  edit = record => {
    this.setState({ record: { ...record }, formVisible: true });
  };

  detail = record => {
    const {
      dispatch,
      match: {
        params: { appId },
      },
    } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/setting/interface/interface-detail/${record.id}/${appId}`,
      })
    );
  };

  formClose = flag => {
    this.setState({ formVisible: false, record: {} });
    if (flag) {
      this.table.reload();
    }
  };

  render() {
    const {
      match: {
        params: { appId },
      },
    } = this.props;
    const { formVisible, record } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={this.searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
        />
        <div style={{ margin: '12px 0' }}>
          <Button onClick={this.add} icon="plus" type="primary">
            添加
          </Button>
          <Button icon="rollback" style={{ marginLeft: 10 }} onClick={this.back}>
            返回到应用列表
          </Button>
        </div>
        <CustomTable
          columns={this.columns}
          url={`${config.baseUrl}/api/interface/query`}
          params={{ appId }}
          // eslint-disable-next-line no-return-assign
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title="新建/编辑接口"
          show={formVisible}
          onClose={() => {
            this.setState({ formVisible: false, record: {} });
          }}
        >
          <NewInterface params={record} onClose={this.formClose} appId={appId} />
        </SlideFrame>
      </div>
    );
  }
}
export default connect()(InterfaceList);
