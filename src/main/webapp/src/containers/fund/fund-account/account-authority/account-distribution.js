import React from 'react';
import { connect } from 'dva';
import 'styles/fund/account.scss';
import { routerRedux } from 'dva/router';
import SlideFrame from 'widget/slide-frame';
import { Form, Table, Button, message, Input, Alert, Badge, Modal, Row, Col, Icon } from 'antd';
import { messages } from 'utils/utils';
import accountService from './account-authority.service';
import AccountDistributionAdd from './account-distribution-add';

const { confirm } = Modal;
const { Search } = Input;
class AccountAuthority extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // loading状态
      buttonLoading: false, // 按钮loading状态
      batchDelete: true, // 批量删除标志
      noticeAlert: null, // 提示信息
      slideVisible: false, // 侧边栏显示
      baseId: '', // 点击分配传过来的id
      userData: '', // 获取到的点击分配传过来的数据
      selectedRow: [],
      selectedRowKeys: [],
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
      columns: [
        {
          title: this.$t('fund.bank.account') /*银行账号*/,
          dataIndex: 'accountNumber',
          width: 170,
        },
        {
          title: this.$t('fund.account.name') /*账户名称*/,
          dataIndex: 'accountName',
          tooptips: true,
        },
        {
          title: this.$t('fund.query.permissions') /*查询权限*/,
          dataIndex: 'queryFlag',
          width: 100,
          tooptips: true,
          render: queryFlag => (
            <Badge
              status={queryFlag ? 'success' : 'error'}
              text={
                queryFlag ? messages('common.status.enable') : messages('common.status.disable')
              }
            />
          ),
        },
        {
          title: this.$t('fund.payment.permissions') /*付款权限*/,
          dataIndex: 'payFlag',
          width: 100,
          render: payFlag => (
            <Badge
              status={payFlag ? 'success' : 'error'}
              text={payFlag ? messages('common.status.enable') : messages('common.status.disable')}
            />
          ),
        },
        {
          title: this.$t('fund.receiving.permissions') /*收款权限*/,
          dataIndex: 'gatherFlag',
          width: 100,
          render: gatherFlag => (
            <Badge
              status={gatherFlag ? 'success' : 'error'}
              text={
                gatherFlag ? messages('common.status.enable') : messages('common.status.disable')
              }
            />
          ),
        },
        {
          title: this.$t('fund.check.permissions') /*对账权限*/,
          dataIndex: 'checkFlag',
          width: 100,
          render: checkFlag => (
            <Badge
              status={checkFlag ? 'success' : 'error'}
              text={
                checkFlag ? messages('common.status.enable') : messages('common.status.disable')
              }
            />
          ),
        },
        {
          title: this.$t('fund.operation') /*操作*/,
          dataIndex: '',
          width: 90,
          render: () => <a>{this.$t('fund.edit')}</a> /*编辑*/,
        },
      ],
    };
  }

  componentDidMount() {
    const { match } = this.props;
    this.searchUser(match.params.id);
  }

  /**
   * 列表数据
   */
  getList(baseId, searchParam) {
    const { pagination } = this.state;
    this.setState({ loading: true, noticeAlert: null, selectedRowKeys: [] });
    accountService.getDistributionList(baseId, searchParam).then(response => {
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
   * 点击行
   */
  handleRowClick = record => {
    this.setState({
      editModel: record,
      slideVisible: true, // 侧边栏是否展示
    });
  };

  /**
   * 关闭侧边栏
   */
  handleClose = () => {
    const { baseId } = this.state; // 头点击分配传过来的id
    this.setState({
      slideVisible: false, // 侧边栏是否展示
      editModel: {},
    });
    this.getList(baseId);
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const { baseId } = this.state; // 头点击分配传过来的id
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList(baseId);
      }
    );
  };

  /**
   * 搜索
   */
  search = e => {
    const { baseId } = this.state; // 头点击分配传过来的id
    if (e) {
      this.getList(baseId, e);
    } else {
      message.error(this.$t('fund.fill.complete.data')); // 请填写完整数据
      this.getList(baseId);
    }
  };

  /**
   * 重置
   */
  searchClear = e => {
    const { form } = this.props;
    const { baseId } = this.state; // 头点击分配传过来的id
    e.preventDefault();
    form.resetFields(); // 重置form中输入控件的值
    this.getList(baseId);
  };

  /**
   * 新建
   */
  handleCreateClick = () => {
    this.setState({
      slideVisible: true, // 侧边栏是否展示
    });
  };

  /**
   * 删除
   */
  deleteItems = () => {
    const { selectedRowKeys, baseId } = this.state;
    accountService
      .batchDeleteAccount(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('fund.delete.successful1')); // 删除成功！
          this.getList(baseId);
          this.setState({
            selectedRowKeys: {},
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 展示删除弹框
   */
  showDeleteConfirm = () => {
    const aThis = this;
    confirm({
      title: this.$t('fund.determine.delete.selected.documents'), // 确定删除选中的单据吗?
      okText: this.$t('fund.determine') /*确定*/,
      okType: 'danger',
      cancelText: this.$t('fund.cancel') /*取消*/,
      onOk() {
        aThis.deleteItems();
      },
    });
  };

  /**
   * 提示框显示
   */
  noticeAlert = rows => {
    const noticeAlert = (
      <span>
        {this.$t('fund.selected')}
        <span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span>{' '}
        {this.$t('fund.')}
        {/*已选择*/}
        {/*项*/}
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
      batchDelete: !rows.length,
    });
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        batchDelete: !(selectedRowKeys.length > 0),
      },
      () => {
        if (selectedRowKeys.length > 0) {
          this.noticeAlert(selectedRow);
        } else {
          this.setState({
            noticeAlert: null,
          });
        }
      }
    );
  };

  /**
   * 返回账户权限管理头
   */
  backToHead = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/account-manage/account-authority/account-authority/`,
      })
    );
  };

  searchUser(employeeId) {
    accountService.searchList1(employeeId).then(response => {
      const { data } = response;
      this.setState({
        userData: data[0],
        baseId: data[0].id, // 头点击分配传过来的id
      });
      this.getList(data[0].id);
    });
  }

  render() {
    const {
      columns,
      slideVisible,
      batchDelete,
      buttonLoading,
      tableData,
      pagination,
      loading,
      selectedRowKeys,
      selectedRow,
      noticeAlert,
      editModel,
      baseId,
      userData,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        <Row style={{ padding: '30px' }}>
          <Col span={8}>
            {this.$t('fund.employee.name1')}
            {userData.employeeName}
          </Col>
          {/*员工姓名：*/}
          <Col span={8}>
            {this.$t('fund.employee.code1')}
            {userData.employeeCode}
          </Col>
          {/*员工代码：*/}
        </Row>
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={8}>
                <Button type="primary" onClick={this.handleCreateClick}>
                  {this.$t('common.create')}
                </Button>
                <Button
                  type="danger"
                  disabled={batchDelete}
                  loading={buttonLoading}
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.showDeleteConfirm();
                  }}
                >
                  {this.$t('common.delete')}
                </Button>
              </Col>
              <Col span={6} offset={10}>
                <Search
                  placeholder={this.$t('fund.input.bank.account')}
                  enterButton
                  onSearch={this.search}
                />
                {/*请输入银行账号*/}
              </Col>
            </Row>

            {/* 侧边栏 */}
            <SlideFrame
              title={
                editModel.id
                  ? this.$t('fund.edit.employee.account.permissions')
                  : this.$t('fund.edit.employee.account.permissions')
              } // 编辑员工账号权限 : 编辑员工账号权限
              show={slideVisible}
              onClose={() => this.handleClose()}
            >
              <AccountDistributionAdd
                onClose={this.handleClose}
                params={editModel}
                baseId={baseId}
              />
            </SlideFrame>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={rowSelection}
            loading={loading}
            onChange={this.onChangePager}
            onRowClick={this.handleRowClick}
            bordered
            size="middle"
          />
          <a style={{ fontSize: '14px', paddingBottom: '40px' }} onClick={this.backToHead}>
            <Icon type="rollback" style={{ marginRight: '5px', paddingBottom: '15px  ' }} />
            {this.$t('fund.return')}
            {/*返回*/}
          </a>
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
