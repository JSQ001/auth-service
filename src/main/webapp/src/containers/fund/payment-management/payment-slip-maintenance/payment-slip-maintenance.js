import React from 'react';
import { routerRedux } from 'dva/router';
import 'styles/fund/account.scss';
import { Form, Table, Button, message, Input, Alert, Modal, Row, Col } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import FundSearchForm from '../../fund-components/fund-search-form';
import PaymentMaintenanceService from './payment-maintenance-service';

const { Search } = Input;
const { confirm } = Modal;
const defaultSelectDate = {
  startDate: moment()
    .startOf('day')
    .subtract(6, 'days'),
  endDate: moment().endOf('day'),
};
class PaymentSlipMaintenance extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedRow: [],
      noticeAlert: null, // 提示信息
      selectedRowKeys: [],
      buttonLoading: false, // 按钮loading状态
      batchDelete: true, // 批量删除标志
      noLine: false, // 笔数为0不可提交
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      columns: [
        {
          title: '单据编号',
          dataIndex: 'paymentBatchNumber',
          width: 150,
          align: 'center',
        },
        {
          title: '单据类型',
          dataIndex: 'billTypeDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '付款账户',
          dataIndex: 'paymentAccountName',
          width: 100,
          align: 'center',
        },
        {
          title: '付款账号',
          dataIndex: 'paymentAccount',
          width: 100,
          align: 'center',
        },
        {
          title: '付款方式',
          dataIndex: 'paymentMethodDesc',
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
          title: '币种',
          dataIndex: 'currencyCode',
          width: 100,
          align: 'center',
        },
        {
          title: '单据日期',
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
        {
          title: '制单人',
          dataIndex: 'employeeName',
          width: 150,
          align: 'center',
        },
      ],
      searchForm: [
        {
          colSpan: 6,
          type: 'valueList',
          label: '单据类型',
          id: 'billType',
          options: [],
          valueListCode: 'ZJ_FORM_TYPE',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '付款方式',
          id: 'paymentType',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '付款账号',
          id: 'accountNumber',
          listType: 'paymentAccount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '单据状态',
          id: 'billStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
        {
          colSpan: 9,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: '日期从',
          fromId: 'dateFrom',
          tolabel: '日期到',
          toId: 'dateTo',
        },
      ],
    };
  }

  componentWillMount() {
    this.setState({
      searchParams: {
        billDateFrom: moment(defaultSelectDate.startDate)
          .format()
          .slice(0, 10),
        billDateTo: moment(defaultSelectDate.endDate)
          .format()
          .slice(0, 10),
      },
    });
  }

  componentDidMount() {
    this.getList();
  }

  /**
   * 获取列表数据
   */
  getList = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    PaymentMaintenanceService.getPaymentQueryList(
      pagination.page,
      pagination.pageSize,
      searchParams
    ).then(response => {
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
        searchParams: {},
      });
    });
  };

  /**
   * 搜索
   */
  handleSearch = params => {
    // 获取表单数据
    let { searchParams } = this.state;
    searchParams = {};
    this.setState(
      {
        searchParams: {
          ...searchParams,
          billType: params.billType ? params.billType.key : '',
          paymentMethod: params.paymentType ? params.paymentType.key : '',
          billStatus: params.billStatus ? params.billStatus.key : '',
          // paymentAccount: paymentAccount || '',
          billDateFrom: params.dateFrom
            ? moment(params.dateFrom)
                .format()
                .slice(0, 10)
            : '',
          billDateTo: params.dateTo
            ? moment(params.dateTo)
                .format()
                .slice(0, 10)
            : '',
          paymentAccount: params.accountNumber || '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  // 清空
  handleClear = () => {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
    this.setState({
      searchParams: {},
    });
    // console.log(this.state.searchParams)
    this.getList();
  };

  // 单号搜索
  searchNumber = value => {
    let { searchParams } = this.state;
    searchParams = {};
    this.setState(
      {
        searchParams: { ...searchParams, paymentBatchNumber: value },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 新建
   */
  handleCreateClick = () => {
    const { dispatch } = this.props;
    // console.log(record)
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/payment-slip-maintenance/manual-maintain`,
      })
    );
  };

  /**
   * 点击行
   */
  handleRowClick = record => {
    // console.log(record);
    const { dispatch } = this.props;
    // console.log(record)
    dispatch(
      routerRedux.push({
        pathname: `/payment-management/payment-slip-maintenance/manual-maintain/${record.id}/${
          record.paymentBatchNumber
        }/${record.billType}`,
      })
    );
  };

  /**
   * 展示删除弹框
   */
  showDeleteConfirm = () => {
    const aThis = this;
    confirm({
      title: '您讲删除所选择的单据，删除后单据将退回至付款工作台，是否继续?',
      okText: '确定',
      okType: 'danger',
      cancelText: '取消',
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
        已选择<span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
      batchDelete: !rows.length,
    });
  };

  /**
   * 数据列表分页点击
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
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    // console.log(selectedRow);
    if (selectedRowKeys.length > 0) {
      for (let i = 1; i <= selectedRow.length; i += 1) {
        if (selectedRow[i - 1].lineCount === '0') {
          this.setState({
            noLine: true,
          });
          // message.error('存在笔数为0的单据，不能提交')
          break;
        }
      }
    }
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
   * 删除
   */
  deleteItems = () => {
    const { selectedRowKeys } = this.state;
    PaymentMaintenanceService.deleteAccount(selectedRowKeys).then(res => {
      if (res.status === 200) {
        message.success('删除成功！');
        this.getList();
        this.setState({
          selectedRowKeys: [],
          noticeAlert: null,
        });
      }
    });
    // .catch(err => {
    //   message.error(err.response.data.message);
    // });
  };

  /**
   * 提交
   */
  submit = () => {
    const { selectedRowKeys, noLine } = this.state;
    if (noLine) {
      message.error('金额不能为0');
    } else {
      PaymentMaintenanceService.submitAll(selectedRowKeys)
        .then(() => {
          message.success('提交成功');
          this.getList();
          this.setState({
            selectedRowKeys: [],
            noticeAlert: null,
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  render() {
    const {
      buttonLoading,
      pagination,
      batchDelete,
      columns,
      noticeAlert,
      loading,
      selectedRow,
      selectedRowKeys,
      tableData,
      searchForm,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div className="train">
        {/* 搜索区域 */}
        {/* <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.clear} /> */}
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={4}
            />
          </Row>
        </div>
        <div>
          {/* </Row> */}
          <div
            className="table-header-buttons"
            style={{ marginTop: '20px', paddingBottom: '10px' }}
          >
            <Row>
              <Col span={8}>
                {/* 新建 */}
                <Button type="primary" onClick={this.handleCreateClick}>
                  {this.$t('common.create')}
                </Button>
                {/* 删除 */}
                {/* <Popconfirm
              onConfirm={e => this.deleteItems(e)}
              title={this.$t('common.confirm.delete')}
            > */}
                <Button
                  style={{ marginLeft: '10px' }}
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
                <Button style={{ marginLeft: '10px' }} type="primary" onClick={this.submit}>
                  {this.$t('提交')}
                </Button>
              </Col>
              <Col span={6} offset={10}>
                <Search placeholder="请输入单据编号" enterButton onSearch={this.searchNumber} />
              </Col>
            </Row>
          </div>
          {/* 提示信息 */}
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          {/* 数据列表 */}
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
            scroll={{ x: 1500 }}
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
export default connect(mapStateToProps)(Form.create()(PaymentSlipMaintenance));
