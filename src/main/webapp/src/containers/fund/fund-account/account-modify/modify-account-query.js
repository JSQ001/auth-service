import React from 'react';
import { connect } from 'dva';
import SearchArea from 'components/Widget/search-area';
import Table from 'widget/table';
import 'styles/fund/account.scss';
import { routerRedux } from 'dva/router';
import { Form, Button, Alert, Popconfirm, message } from 'antd';
import moment from 'moment';
import accountService from './modify-account.service';

class ModifyAccountList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // loading状态
      buttonLoading: false, // 按钮loading状态
      batchDelete: true, // 批量删除标志
      noticeAlert: null, // 提示信息
      selectedRow: [],
      selectedRowKeys: [],
      searchParams: {}, // 查询条件
      editModel: {}, // 点击行时的编辑数据
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      searchForm: [
        // 申请单号
        {
          colSpan: 6,
          type: 'input',
          id: 'documentNumber',
          label: '申请单号',
          placeholder: '请输入',
        },
        // 时间
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'dateFrom', label: '时间从' },
            { type: 'date', id: 'dateTo', label: '时间至' },
          ],
          colSpan: 6,
        },
        // 银行账号
        {
          colSpan: 6,
          type: 'input',
          label: '银行账号',
          id: 'accountNumber',
          placeholder: '请输入',
        },
        // 状态
        {
          colSpan: 6,
          type: 'value_list',
          label: '状态',
          id: 'approveStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
      ],
      // 列表columns
      columns: [
        {
          title: '申请单号',
          dataIndex: 'documentNumber',
          width: 100,
          align: 'center',
        },
        {
          title: '单据类型',
          dataIndex: 'applicationName',
          width: 100,
          align: 'center',
        },
        {
          title: '银行账号',
          dataIndex: 'accountNumber',
          width: 100,
          align: 'center',
        },
        {
          title: '申请公司',
          dataIndex: 'companyName',
          width: 100,
          align: 'center',
        },
        {
          title: '申请日期',
          dataIndex: 'requisitionDate',
          width: 100,
          align: 'center',
        },

        {
          title: '单据状态',
          dataIndex: 'approveStatusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          width: 100,
          align: 'center',
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 获取列表数据
   */

  getList() {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true, noticeAlert: null, selectedRowKeys: [] });
    accountService
      .getAccountHeadList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        /* eslint-disable */
        data.map(item => {
          item.requisitionDate = item.requisitionDate
            ? moment(new Date(item.requisitionDate)).format('YYYY-MM-DD')
            : '';
          item.applicationName = '账户变更';
        });
        /* eslint-disable */

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
   * 新建和编辑
   */
  handleCreateClick = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/account-manage/account-modify/modify-account-add/${id}`,
      })
    );
  };

  /**
   * 删除
   */
  deleteItems = () => {
    const selectedRowKeys = this.state.selectedRowKeys;
    accountService
      .batchDeleteAccount(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          message.success('删除成功！');
          this.getList();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 提交
   */
  handleSubmitClick = () => {
    const selectedRowKeys = this.state.selectedRowKeys;
    accountService
      .submitAccountEdit(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          message.success('提交成功！');
          this.getList();
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
    return 'submit';
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
  };

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
          documentNumber: value.documentNumber ? value.documentNumber : '',
          accountNumber: value.accountNumber ? value.accountNumber : '',
          requisitionDateFrom: value.dateFrom ? moment(value.dateFrom).format('YYYY-MM-DD') : '',
          requisitionDateTo: value.dateTo ? moment(value.dateTo).format('YYYY-MM-DD') : '',
          approveStatus: value.approveStatus ? value.approveStatus : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 提示框显示
   */

  noticeAlert = rows => {
    const noticeAlert = (
      <span>
        已选择<span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
      batchDelete: !rows.length,
    });
  };

  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState({ selectedRowKeys, batchDelete: !(selectedRowKeys.length > 0) }, () => {
      if (selectedRowKeys.length > 0) {
        this.noticeAlert(selectedRow);
      } else {
        this.setState({
          noticeAlert: null,
        });
      }
    });
  };

  render() {
    const {
      pagination,
      searchForm,
      batchDelete,
      tableData,
      loading,
      buttonLoading,
      columns,
      noticeAlert,
      selectedRowKeys,
      selectedRow,
      editModel,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        {/* 搜索区域 */}
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} />
        <div className="table-header">
          {/* 共total条数据 */}
          <div className="table-header-title">
            {this.$t('common.total', { total: pagination.total ? pagination.total : '0' })}
          </div>
          <div className="table-header-buttons">
            {/* 新建 */}
            <Button type="primary" onClick={() => this.handleCreateClick('new')}>
              {this.$t('common.create')}
            </Button>
            {/* 删除 */}
            <Popconfirm
              onConfirm={e => this.deleteItems(e)}
              title={this.$t('common.confirm.delete')}
            >
              <Button
                disabled={batchDelete}
                loading={buttonLoading}
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                }}
              >
                {this.$t('common.delete')}
              </Button>
            </Popconfirm>
            {/* 提交 */}
            <Button type="primary" onClick={this.handleSubmitClick}>
              {this.$t('common.submit')}
            </Button>
          </div>
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          {/* 数据列表 */}
          <Table
            onRow={record => {
              return {
                onClick: () => {
                  this.handleCreateClick(record.id);
                },
              };
            }}
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={rowSelection}
            loading={loading}
            onChange={this.onChangePager}
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

export default connect(mapStateToProps)(Form.create()(ModifyAccountList));
