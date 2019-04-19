import React from 'react';
import { connect } from 'dva';
import Table from 'widget/table';
import 'styles/fund/account.scss';
import { routerRedux } from 'dva/router';
import { Form, Button, Alert, Popconfirm, message, Row, Tabs } from 'antd';
import moment from 'moment';
import FundSearchForm from '../../fund-components/fund-search-form';
import accountService from './fund-transfer-application.service';

const { TabPane } = Tabs;
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
      // editModel: {}, // 点击行时的编辑数据
      queryFlag: 'OTHER',
      isShow: {
        display: 'block',
      },
      isAppro: {
        display: 'none',
      },
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
          customizeOptions: [
            { value: 'ZJ_ADD', name: '新建' },
            { value: 'ZJ_REFUSE', name: '驳回' },
            { value: 'ZJ_RESUME', name: '收回' },
            { value: 'ZJ_SUBMIT', name: '审批中' },
          ],
        },
        // 申请人
        {
          colSpan: 6,
          type: 'modalList',
          label: '申请人',
          id: 'creater',
          listType: 'select_authorization_user',
          labelKey: 'userName',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'userId',
          single: true,
          isNeedUserInitialValue: true,
        },
        // 调拨日期从，调拨日期到
        {
          colSpan: 6,
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
      searchForm1: [
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
          customizeOptions: [{ value: 'ZJ_APPROVED', name: '已审批' }],
        },
        // 申请人
        {
          colSpan: 6,
          type: 'modalList',
          label: '申请人',
          id: 'creater',
          listType: 'select_authorization_user',
          labelKey: 'userName',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'userId',
          single: true,
          isNeedUserInitialValue: true,
        },
        // 调拨日期从，调拨日期到
        {
          colSpan: 6,
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

  componentWillMount() {
    this.getList('OTHER');
    this.getApprovalList(); // 审批状态
  }

  /**
   * 获取列表数据
   */

  getList(queryFlag) {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true, noticeAlert: null, selectedRowKeys: [] });
    accountService
      .getTransferApplHeader(pagination.page, pagination.pageSize, searchParams, queryFlag)
      .then(response => {
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
      })
      .catch(err => {
        message.error(err.response ? err.response.data.message : '请求失败');
        this.setState({
          loading: false,
        });
      });
  }

  /**
   * 审批状态
   */
  getApprovalList = () => {
    const arrApproval = [];
    const arrOther = [];
    this.getSystemValueList('ZJ_BILL_STATUS')
      .then(res => {
        if (res.data.values.length > 0) {
          res.data.values.forEach(element => {
            if (element.value === 'ZJ_APPROVED') {
              const objApproval = { value: element.value, name: element.name };
              arrApproval.push(objApproval);
            } else {
              const objOther = { value: element.value, name: element.name };
              arrOther.push(objOther);
            }
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

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
    const { selectedRowKeys, queryFlag } = this.state;
    accountService
      .deleteList(selectedRowKeys)
      .then(res => {
        if (res.status === 200) {
          message.success('删除成功！');
          this.getList(queryFlag);
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const { queryFlag } = this.setState;
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList(queryFlag);
      }
    );
  };

  /**
   * 搜索
   */
  search = value => {
    const { searchParams, queryFlag } = this.state;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          employeeId: value.creater ? value.creater[0].userId : '',
          companyId: value.documentCompany ? value.documentCompany[0].id : '',
          departmentId: value.documentDepartment ? value.documentDepartment[0].departmentId : '',
          billDateFrom: value.dateFrom ? moment(value.dateFrom).format('YYYY-MM-DD') : '',
          billDateTo: value.dateTo ? moment(value.dateTo).format('YYYY-MM-DD') : '',
          billStatus: value.billStatus ? value.billStatus.key : '',
          amountFrom: value.intervalFrom ? value.intervalFrom : '',
          amountTo: value.intervalTo ? value.intervalTo : '',
        },
      },
      () => {
        this.getList(queryFlag);
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

  /**
   * tab切换
   */
  selectTab = key => {
    this.forceUpdate();
    let queryFlag = null;
    if (key === '1') {
      queryFlag = 'OTHER';
      this.setState({
        isShow: {
          display: 'block',
        },
        isAppro: {
          display: 'none',
        },
        queryFlag,
      });
      this.getList(queryFlag);
    }
    if (key === '2') {
      queryFlag = 'APPROVED';
      this.setState({
        isShow: {
          display: 'none',
        },
        isAppro: {
          display: 'block',
        },
        queryFlag,
      });
      this.getList(queryFlag);
    }
  };

  render() {
    const {
      pagination,
      batchDelete,
      tableData,
      loading,
      buttonLoading,
      columns,
      noticeAlert,
      selectedRowKeys,
      selectedRow,
      isShow,
      searchForm,
      searchForm1,
      isAppro,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div>
        <Tabs defaultActiveKey="1" onChange={this.selectTab}>
          <TabPane tab="未审批" key="1" />
          <TabPane tab="已审批" key="2" />
        </Tabs>
        <div>
          {/* 搜索区域 */}
          {/* <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} /> */}
          {/* {
            this.getFundSearchForm(approval)
          } */}
          <Row style={isShow}>
            <FundSearchForm searchForm={searchForm} submitHandle={this.search} maxLength={4} />
          </Row>
          <Row style={isAppro}>
            <FundSearchForm searchForm={searchForm1} submitHandle={this.search} maxLength={4} />
          </Row>
          <div className="table-header">
            <div className="table-header-buttons" style={isShow}>
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
