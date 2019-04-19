import React from 'react';
import { connect } from 'dva';
// /* eslint-disable */

import { Button, Row, message, Popconfirm, Alert } from 'antd';
import Table from 'widget/table';
import { routerRedux } from 'dva/router';
import moment from 'moment';
import FundSearchForm from '../../fund-components/fund-search-form';
import accountOpenEntryService from './account-open-entry.service';

// const { confirm } = Modal;

class AccountOpenEntryList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      loading: false,
      noticeAlert: null, // 提示信息
      batchDelete: true, // 批量删除标志
      searchParams: {},
      selectedRowKeys: [], // 列表选择的行ID
      selectedRow: [], // 列表选择的对象
      searchForm: [
        // 开户公司
        {
          colSpan: 6,
          type: 'modalList',
          label: '开户公司',
          id: 'documentCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        // 开户银行
        {
          colSpan: 6,
          type: 'valueList',
          label: '开户银行',
          id: 'openBank',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        // 银行账号
        {
          colSpan: 6,
          type: 'input',
          label: '银行账号',
          id: 'accountNumber',
        },
        // 审批状态
        {
          colSpan: 6,
          type: 'valueList',
          label: '审批状态',
          id: 'billStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
        // 申请日期从，申请日期到
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: '申请日期从',
          fromId: 'dateFrom',
          tolabel: '申请日期到',
          toId: 'dateTo',
        },
        // 银行账户
        {
          colSpan: 6,
          type: 'input',
          label: '银行账户',
          id: 'accountName',
        },
      ],
      columns: [
        {
          title: '申请单号',
          dataIndex: 'documentNumber',
          width: 200,
          align: 'center',
        },
        {
          title: '银行账号',
          dataIndex: 'accountNumber',
          width: 100,
          align: 'center',
        },
        {
          title: '银行账户',
          dataIndex: 'accountName',
          width: 100,
          align: 'center',
        },
        {
          title: '币种',
          dataIndex: 'currencyCode',
          width: 100,
          align: 'center',
        },
        {
          title: '开户银行',
          dataIndex: 'openBankName',
          width: 100,
          align: 'center',
        },
        {
          title: '开户公司',
          dataIndex: 'companyName',
          width: 100,
          align: 'center',
        },
        {
          title: '开户部门',
          dataIndex: 'departmentName',
          width: 100,
          align: 'center',
        },
        {
          title: '申请日期',
          dataIndex: 'requisitionDate',
          width: 100,
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          width: 100,
          align: 'center',
        },
        {
          title: '状态',
          dataIndex: 'maintainApproveStatusDesc',
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
   * 新建和编辑
   */
  handleCreateClick = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/fund-account/account-open-entry/account-open-entry-detail/${id}`,
      })
    );
  };

  /**
   * 获取列表页数据
   */
  getList = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true, noticeAlert: null, selectedRowKeys: [] });
    accountOpenEntryService
      .getAccountOpenMaintenanceList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        data.map(item => {
          return item.requisitionDate
            ? moment(new Date(item.requisitionDate)).format('YYYY-MM-DD')
            : '';
        });
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            current: pagination.page + 1,
            // pageSize: pagination.pageSize,
            onChange: this.onChangePager,
            // onShowSizeChange: this.onShowSizeChange,
            // showSizeChanger: true,
            // showQuickJumper: true,
            // showTotal: (total, range) =>
            //   this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
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

  /**
   * 搜索
   */
  search = value => {
    this.setState(
      {
        searchParams: {
          companyId: value.documentCompany ? value.documentCompany[0].id : '',
          openBank: value.openBank ? value.openBank.key : '',
          accountNumber: value.accountNumber ? value.accountNumber : '',
          accountName: value.accountName ? value.accountName : '',
          requisitionDateFrom: value.dateFrom ? moment(value.dateFrom).format('YYYY-MM-DD') : '',
          requisitionDateTo: value.dateTo ? moment(value.dateTo).format('YYYY-MM-DD') : '',
          maintainApproveStatus: value.billStatus ? value.billStatus.key : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 删除
   */
  deleteItems = () => {
    const { selectedRowKeys } = this.state;
    accountOpenEntryService
      .deleteList(selectedRowKeys)
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
      searchForm,
      columns,
      tableData,
      noticeAlert,
      batchDelete,
      selectedRowKeys,
      selectedRow,
      loading,
      pagination,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div>
        {/* 搜索区域 */}
        <Row>
          <FundSearchForm searchForm={searchForm} submitHandle={this.search} maxLength={4} />
        </Row>
        <div className="table-header">
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
        </div>
        {noticeAlert ? (
          <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
        ) : (
          ''
        )}
        <Table
          onRow={record => {
            return {
              onClick: () => {
                this.handleCreateClick(record.id);
              },
            };
          }}
          pagination={pagination}
          loading={loading}
          rowKey={record => record.id}
          rowSelection={rowSelection}
          columns={columns}
          onChange={this.onChangePager}
          dataSource={tableData}
          bordered
          size="middle"
        />
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

export default connect(mapStateToProps)(AccountOpenEntryList);
