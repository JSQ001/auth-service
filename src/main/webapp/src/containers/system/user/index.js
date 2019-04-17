/**
 * Created by weishan on 2019/03/07.
 * 用户管理
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Badge, message, Tooltip, Tag } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'components/Widget/slide-frame';

import userService from './user.service';
import NewUser from './user-info';
import SelectRoles from 'containers/employee/roles';
import { messages } from 'utils/utils';
import SearchArea from 'widget/search-area';

class User extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      buttonFlag: false,
      loading: false,
      data: [],
      searchParams: {},
      columns: [
        {
          title: messages('common.sequence' /*序号*/),
          dataIndex: 'index',
          align: 'center',
        },
        {
          title: messages('user.info.code' /*用户名*/),
          dataIndex: 'login',
          align: 'center',
        },
        {
          title: messages('user.info.name' /*用户名称名称*/),
          dataIndex: 'userName',
          align: 'center',
          render: (value, record) => (
            <Tooltip title={record.userName}>
              <Tag color="green">{value}</Tag>
            </Tooltip>
          ),
        },
        {
          title: messages('user.info.remark' /*用户备注*/),
          dataIndex: 'remark',
          align: 'center',
        },
        {
          title: messages('user.info.email' /*用户邮箱*/),
          dataIndex: 'email',
          align: 'center',
        },
        {
          title: messages('user.info.mobile' /*用户手机号*/),
          dataIndex: 'mobile',
          align: 'center',
        },
        {
          title: messages('common.column.status' /*状态*/),
          dataIndex: 'activated',
          align: 'center',
          render: activated => (
            <Badge
              status={activated ? 'success' : 'error'}
              text={
                activated ? messages('common.status.enable') : messages('common.status.disable')
              }
            />
          ),
        },
        {
          title: this.$t('common.operation'), //操作
          dataIndex: 'option',
          align: 'center',
          //width: 140,
          render: (value, record) => {
            return (
              <span>
                <a onClick={e => this.allocRole(e, record)}>{this.$t('user.info.selectrole')}</a>
              </span>
            );
          },
        },
      ],
      searchForm: [
        {
          type: 'input',
          id: 'login',
          label: messages('user.info.code' /*用户名*/),
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'username',
          label: messages('user.info.name' /*用户姓名*/),
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'email',
          label: messages('user.info.email' /*邮箱*/),
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'mobile',
          label: messages('user.info.mobile' /*手机*/),
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'keyword',
          label: messages('user.info.keyword' /*关键字*/),
          colSpan: 6,
        },
      ],
      pagination: {
        page: 0,
        total: 0,
        pageSize: 10,
      },
      showListSelector: false,
      updateParams: {},
      showSlideFrameNew: false,
      allocRoleShow: false,
      userId: '',
    };
  }

  componentDidMount() {
    //记住页面
    let _pagination = this.getBeforePage();
    let pagination = this.state.pagination;
    pagination.page = _pagination.page;
    pagination.current = _pagination.page + 1;
    this.setState(
      {
        pagination,
      },
      () => {
        this.clearBeforePage();
        this.getList();
      }
    );
  }

  //得到值列表数据
  getList() {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    userService
      .getUserList(pagination.page, pagination.pageSize, searchParams)
      .then(res => {
        res.data.map((item, index) => {
          item.index = pagination.page * pagination.pageSize + index + 1;
          item.key = item.index;
        });
        pagination.total = Number(res.headers['x-total-count']) || 0;
        this.setState({
          data: res.data,
          loading: false,
          pagination: {
            ...pagination,
            onChange: this.onChangePager,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', {
                range0: `${range[0]}`,
                range1: `${range[1]}`,
                total: total,
              }),
          },
        });
      })
      .catch(() => {
        this.setState({ loading: false });
        message.error(this.$t('common.error' /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/));
      });
  }

  //改变每页显示的条数
  onShowSizeChange = (current, pageSize) => {
    let pagination = this.state.pagination;
    pagination.page = current - 1;
    pagination.pageSize = pageSize;
    this.setState(
      {
        pagination,
      },
      () => {
        this.getList();
      }
    );
  };

  //分页点击
  onChangePager = p => {
    let pagination = this.state.pagination;
    pagination.page = p.current - 1;
    pagination.current = p.current;
    this.setState(
      {
        pagination,
      },
      () => {
        this.getList();
      }
    );
  };

  editItem = (e, record) => {
    this.setState({
      updateParams: record,
      showSlideFrameNew: true,
    });
  };

  handleCloseNewSlide = params => {
    this.setState(
      {
        showSlideFrameNew: false,
      },
      () => {
        if (params) {
          this.getList();
        }
      }
    );
  };

  showSlideNew = flag => {
    this.setState({
      showSlideFrameNew: flag,
    });
  };

  newItemShowSlide = () => {
    this.setState(
      {
        updateParams: { record: {} },
      },
      () => {
        this.showSlideNew(true);
      }
    );
  };

  search = values => {
    let pagination = this.state.pagination;
    this.setState(
      {
        searchParams: { ...this.state.searchParams, ...values },
        pagination: { ...pagination, current: 0, page: 0 },
      },
      () => {
        this.getList();
      }
    );
  };

  // 清除
  clearFunction = () => {
    this.setState({ searchParams: {} });
  };

  // 点击分配角色 回调
  allocRole = (e, record) => {
    e.stopPropagation();
    this.setState({ allocRoleShow: true, userId: record.id });
  };

  // 分配角色弹出框 关闭
  closeAllocRole = flag => {
    this.setState({ allocRoleShow: false }, () => {
      flag && this.getList();
    });
  };

  render() {
    const {
      columns,
      data,
      loading,
      pagination,
      searchParams,
      searchForm,
      userId,
      allocRoleShow,
      updateParams,
      showSlideFrameNew,
    } = this.state;
    return (
      <div style={{ paddingBottom: 20 }} className="value-list">
        <SearchArea
          maxLength={4}
          searchParams={searchParams}
          submitHandle={this.search}
          clearHandle={this.clearFunction}
          searchForm={searchForm}
        />
        <div className="table-header">
          <div className="table-header-buttons" style={{ paddingTop: 15 }}>
            <Button type="primary" onClick={this.newItemShowSlide} style={{ marginRight: 15 }}>
              {/*新增用户*/}
              {messages('common.create')}
            </Button>
          </div>
        </div>
        <Table
          columns={columns}
          dataSource={data}
          pagination={pagination}
          onChange={this.onChangePager}
          loading={loading}
          onRow={record => ({
            onClick: e => this.editItem(e, record),
          })}
          rowKey={record => record.id}
          bordered
          size="middle"
        />

        <SelectRoles userId={userId} onCancel={this.closeAllocRole} visible={allocRoleShow} />

        <SlideFrame
          title={updateParams.id ? this.$t('common.edit') : this.$t('common.create')}
          show={showSlideFrameNew}
          onClose={() => this.setState({ showSlideFrameNew: false })}
        >
          <NewUser
            onClose={this.handleCloseNewSlide}
            params={{ ...updateParams, visible: showSlideFrameNew }}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(User);
