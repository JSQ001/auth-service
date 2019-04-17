import React from 'react';
import { connect } from 'dva';
import Table from 'widget/table';
import 'styles/fund/account.scss';
import { routerRedux } from 'dva/router';
import { Form, Button, Alert, Popconfirm, message, Row } from 'antd';
import moment from 'moment';
import FundSearchForm from '../../fund-components/fund-search-form';
import accountService from './fund-transfer-application.service';

class FundTransferApplication extends React.Component {
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
        // 申请公司
        {
          colSpan: 6,
          type: 'modalList',
          label: '申请公司',
          id: 'documentCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        // 申请部门
        {
          colSpan: 6,
          type: 'modalList',
          label: '申请部门',
          id: 'documentDepartment',
          listType: 'department_document',
          labelKey: 'name',
          listExtraParams: { tenantId: props.user.tenantId },
          valueKey: 'id',
          single: true,
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
        // 申请人
        {
          type: 'list',
          id: 'applyId',
          label: '申请人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        // 调拨日期从，调拨日期到
        {
          colSpan: 8,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: '申请日期从',
          fromId: 'dateFrom',
          tolabel: '申请日期到',
          toId: 'dateTo',
        },
        {
          colSpan: 6,
          type: 'intervalInput',
          label: '金额',
          id: 'coinAmount',
        },
      ],
      // 列表columns
      columns: [
        {
          title: '单据编号',
          dataIndex: 'applyNumber',
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
          title: '申请部门',
          dataIndex: 'departmentName',
          width: 100,
          align: 'center',
        },
        {
          title: '笔数',
          dataIndex: 'lineCount',
          width: 100,
          align: 'center',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'center',
        },

        {
          title: '说明',
          dataIndex: 'description',
          width: 100,
          align: 'center',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          width: 100,
          align: 'center',
        },
        {
          title: '申请日期',
          dataIndex: 'billDateDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '审批状态',
          dataIndex: 'billStatusDesc',
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
      .getTransferApplHeader(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        /* eslint-disable */
        data.map(item => {
          item.requisitionDate = item.requisitionDate
            ? moment(new Date(item.requisitionDate)).format('YYYY-MM-DD')
            : '';
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
        pathname: `/transfer-management/fund-transfer-application/fund-transfer-application-maintain/${id}`,
      })
    );
  };

  /**
   * 删除
   */
  deleteItems = () => {
    const selectedRowKeys = this.state.selectedRowKeys;
    console.log('selectedRowKeys');
    console.log(selectedRowKeys);
    accountService
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

  /**
   * 提交
   */
  handleSubmitClick = () => {
    const selectedRowKeys = this.state.selectedRowKeys;
    console.log('xxy');
    console.log(selectedRowKeys);
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
    const { user } = this.props;
    console.log('search----');
    console.log(value);
    this.setState(
      {
        searchParams: {
          ...searchParams,
          employeeId: user.id,
          companyId: value.documentCompany ? value.documentCompany[0].id : '',
          departmentId: value.documentDepartment ? value.documentDepartment[0].departmentId : '',
          billDateFrom: value.dateFrom ? moment(value.dateFrom).format('YYYY-MM-DD') : '',
          billDateTo: value.dateTo ? moment(value.dateTo).format('YYYY-MM-DD') : '',
          billStatus: value.billStatus ? value.billStatus.key : '',
          amountFrom: value.amountFrom ? value.amountFrom : '',
          amountTo: value.amountTo ? value.amountTo : '',
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
      <div>
        {/* 搜索区域 */}
        {/* <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} /> */}
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
            {/* <Button type="primary" onClick={this.handleSubmitClick}>
              {this.$t('common.submit')}
            </Button> */}
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

export default connect(mapStateToProps)(Form.create()(FundTransferApplication));
