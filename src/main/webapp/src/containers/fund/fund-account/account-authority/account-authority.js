import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import 'styles/fund/account.scss';
import SearchArea from 'components/Widget/search-area';
import SlideFrame from 'widget/slide-frame';
import { Form, Table, Button, Badge } from 'antd';
import { messages } from 'utils/utils';
import accountService from './account-authority.service';
import AccountAuthorityAdd from './account-authority-add';

class AccountAuthority extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      slideVisible: false, // 侧边栏显示
      searchParams: {}, // 查询条件
      loading: false, // loading状态
      editModel: {}, // 点击行时的编辑数据
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total: total,
          }),
      },
      searchForm: [
        // 员工信息
        {
          label: this.$t('fund.employee.information') /*员工信息*/,
          type: 'list',
          listType: 'select_authorization_user',
          options: [],
          id: 'userId',
          labelKey: 'userName',
          valueKey: 'userId',
          colSpan: 6,
          single: true,
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          event: 'userId',
          placeholder: this.$t('fund.please.choose') /*请选择*/,
        },
      ],
      columns: [
        {
          title: this.$t('fund.employee.name') /*员工姓名*/,
          dataIndex: 'employeeName',
          width: 120,
        },
        {
          title: this.$t('fund.employee.code') /*员工代码*/,
          dataIndex: 'employeeCode',
          width: 200,
        },
        {
          title: this.$t('fund.assigned.account') /*分配账号*/,
          dataIndex: 'employeeId',
          width: 90,
          render: employeeId => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.distribution(employeeId);
              }}
            >
              {' '}
              {this.$t('fund.distribution')}
              {/*分配*/}
            </a>
          ),
        },
        {
          title: this.$t('fund.default.authentication.type') /*默认验证类型*/,
          dataIndex: 'defaultUkeyType',
          width: 120,
        },
        {
          title: this.$t('fund.serial.number') /*序列号*/,
          dataIndex: 'defaultUkeyDesc',
        },
        {
          title: this.$t('fund.status') /*状态*/,
          dataIndex: 'enabled',
          width: 120,
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={enabled ? messages('common.status.enable') : messages('common.status.disable')}
            />
          ),
        },
        {
          title: this.$t('fund.operation') /*操作*/,
          dataIndex: '',
          width: 100,
          render: () => <a>{this.$t('fund.edit')}</a> /*编辑*/,
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 列表数据
   */
  getList() {
    const { pagination } = this.state;
    // console.log(searchParams)
    this.setState({ loading: true });
    accountService.getAccountHeadList(pagination.page, pagination.pageSize).then(response => {
      const { data } = response;
      this.setState({
        tableData: data,
        loading: false,
        pagination: {
          ...pagination,
          total: Number(response.headers['x-total-count'])
            ? Number(response.headers['x-total-count'])
            : 0,
          onChange: this.onChangePager,
          current: pagination.page + 1,
        },
      });
    });
  }

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 搜索
   */
  search = value => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          employeeId: value.userId[0] ? value.userId[0] : '',
        },
      },
      () => {
        this.searchUser();
      }
    );
  };

  /**
   * 重置
   */
  clear = () => {
    const { searchParams } = this.state;
    this.setState({
      searchParams: {
        ...searchParams,
        businessCode: '',
        reportStatus: '',
      },
    });
    this.getList();
  };

  /**
   * 新建
   */
  handleCreateClick = () => {
    this.setState({ slideVisible: true });
  };

  /**
   * 点击行
   */
  handleRowClick = record => {
    this.setState({ editModel: record, slideVisible: true });
  };

  /**
   * 关闭侧边栏
   */
  handleClose = () => {
    this.setState({
      slideVisible: false,
      editModel: {},
    });
    this.getList();
  };

  /**
   * 分配
   */
  distribution = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/account-manage/account-authority/account-distribution/${id}`,
      })
    );
  };

  searchUser() {
    const { pagination, searchParams } = this.state;
    // console.log(searchParams)
    this.setState({ loading: true });
    accountService.searchList(pagination.page, pagination.pageSize, searchParams).then(response => {
      const { data } = response;
      this.setState({
        tableData: data,
        loading: false,
        pagination: {
          ...pagination,
          total: Number(response.headers['x-total-count'])
            ? Number(response.headers['x-total-count'])
            : 0,
          onChange: this.onChangePager,
          current: pagination.page + 1,
        },
      });
    });
  }

  render() {
    const {
      searchForm,
      slideVisible,
      columns,
      tableData, // 数据列表
      pagination,
      loading,
      editModel,
    } = this.state;
    return (
      <div className="train">
        {/* 搜索区域 */}
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} />
        <div className="table-header">
          <div className="table-header-buttons">
            {/* 新建 */}
            <Button type="primary" onClick={this.handleCreateClick}>
              {this.$t('common.create')}
            </Button>
            {/* 侧边栏 */}
            <SlideFrame
              title={editModel.id ? this.$t('fund.edit') : this.$t('fund.new')} /*新建*/ /*编辑*/
              show={slideVisible}
              onClose={() => this.handleClose()}
            >
              <AccountAuthorityAdd onClose={this.handleClose} params={editModel} />
            </SlideFrame>
          </div>
          {/* 数据列表 */}
          <Table
            rowKey={record => record.employeeId}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            loading={loading}
            onChange={this.onChangePager}
            onRowClick={this.handleRowClick}
            bordered
            size="middle"
          />
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(Form.create()(AccountAuthority));
