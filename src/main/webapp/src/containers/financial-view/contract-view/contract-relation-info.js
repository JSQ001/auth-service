import React from 'react';
import { connect } from 'dva';
import { Form, Tabs, Popover, Badge, message, Modal } from 'antd';
import Table from 'widget/table';
import contractService from 'containers/contract/contract-approve/contract.service';
import moment from 'moment';
import PropTypes from 'prop-types';
import ExpenseApplicationForm from 'containers/expense-application-form/detail-readonly';
import PrepaymentDetail from 'containers/pre-payment/my-pre-payment/detail-readonly';
import ExpenseReportDetail from 'containers/reimburse/my-reimburse/reimburse-detail-finance';
import { routerRedux } from 'dva/router';
const { TabPane } = Tabs;

class relationInfo extends React.Component {
  constructor(props) {
    super(props);
    const operationTypeList = {
      payment: this.$t('my.pay'), // 付款
      reserved: this.$t('acp.payment.reserved'), // 反冲
      refund: this.$t('acp.payment.refund'), // 退票
      return: this.$t('acp.payment.return'), // 退款
    };
    const paymentStatus = {
      reserved: {
        N: this.$t('common.create'), // 新建
        P: this.$t('my.reserving'), // 反冲中
        S: this.$t('my.reserve.success'), // 反冲成功
        F: this.$t('my.reserve.failed'), // 反冲失败
        // R: '重新退票',
        // C: '取消退票'
      },
      refund: {
        P: this.$t('my.refunding'), // 退票中
        S: this.$t('my.refund.success'), // 退票成功
        F: this.$t('my.refund.success'), // 退票失败
        R: this.$t('my.refund.again'), // 重新退票
        C: this.$t('my.refund.cancel'), // 取消退票
      },
      return: {
        P: this.$t('my.returning'), // 退款中
        S: this.$t('my.return.success'), // 退款成功
        F: this.$t('my.return.failed'), // 退款失败
        R: this.$t('my.piao.kuang'), // 退票退款
        C: this.$t('my.kuang.piao'), // 退款退票
      },
      payment: {
        P: this.$t('my.paying'), //
        S: this.$t('my.pay.success'), // 支付成功
        F: this.$t('my.pay.failed'), // 支付失败
        R: this.$t('pay.workbench.RePay'), // 重新支付
        C: this.$t('pay.workbench.CancelPay'),
      },
    };
    this.state = {
      tabValue: '',
      tabName: '',
      expenseApplicationShow: false,
      expenseApplicationId: 0,
      prePaymentShow: false,
      reportShow: false,
      prepaymentId: 0,
      reportId: '',
      params: {},
      columns1: [
        {
          title: this.$t('my.contract.line.number'),
          dataIndex: 'contractLineNumber',
          align: 'center',
          width: 90,
        },
        {
          title: this.$t('pay.refund.documentNumber'),
          dataIndex: 'requisitionNumber',
          align: 'center',
          render: (requisitionNumber, record) => (
            <Popover content={requisitionNumber}>
              <a onClick={() => this.showPrepayment(record)}>{requisitionNumber}</a>
            </Popover>
          ),
        },
        { title: this.$t('my.line.number'), dataIndex: 'lineNumber', align: 'center', width: 90 },
        {
          /* 提交日期 */
          title: this.$t('acp.requisitionDate'),
          dataIndex: 'createdDate',
          width: 100,
          align: 'center',
          render: value => (
            <Popover content={value ? moment(value).format('YYYY-MM-DD') : ''}>
              {value ? moment(value).format('YYYY-MM-DD') : ''}
            </Popover>
          ),
        },
        {
          title: this.$t('my.link.amount'),
          dataIndex: 'amount',
          align: 'center',
          render: desc => this.filterMoney(desc),
        },

        {
          title: this.$t({ id: 'my.receivable' } /* 收款方 */),
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <div style={{ whiteSpace: 'normal' }}>
                  {record.payeeCategory === 'EMPLOYEE'
                    ? this.$t('acp.employee')
                    : `${this.$t('acp.vendor')}-${record.partnerName}`}
                </div>
              </div>
            );
          },
        },
        {
          /* 状态 */
          title: this.$t({ id: 'common.column.status' }),
          key: 'status',
          width: '10%',
          align: 'center',
          dataIndex: 'status',
          render: status => (
            <Badge status={this.$statusList[status].state} text={this.$statusList[status].label} />
          ),
        },
      ],
      columns2: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          align: 'center',
          render: (documentNumber, record) => (
            <Popover content={documentNumber}>
              <a onClick={() => this.showExpenseApplication(record)}>{documentNumber}</a>
            </Popover>
          ),
        },
        {
          title: '单据类型',
          dataIndex: 'typeName',
          align: 'center',
          render: typeName => <Popover content={typeName}>{typeName}</Popover>,
        },
        {
          title: '申请日期',
          dataIndex: 'requisitionDate',
          align: 'center',
          render: requisitionDate => (
            <Popover content={requisitionDate ? moment(requisitionDate).format('YYYY-MM-DD') : ''}>
              {requisitionDate ? moment(requisitionDate).format('YYYY-MM-DD') : ''}
            </Popover>
          ),
        },
        {
          title: '关联金额',
          dataIndex: 'functionalAmount',
          align: 'center',
          render: functionalAmount => this.filterMoney(functionalAmount),
        },
        {
          title: '状态',
          dataIndex: 'status',
          align: 'center',
          render: status => (
            <Badge status={this.$statusList[status].state} text={this.$statusList[status].label} />
          ),
        },
      ],
      columns3: [
        // 合同行号
        {
          title: this.$t('my.contract.line.number'),
          dataIndex: 'contractLineNumber',
          align: 'center',
          width: 90,
        },
        {
          title: this.$t('pay.refund.documentNumber'),
          dataIndex: 'businessCode',
          align: 'center',
          render: (documentNumber, record) => (
            <Popover content={documentNumber}>
              <a onClick={() => this.handleLinkReport(record)}>{documentNumber}</a>
            </Popover>
          ),
        },
        {
          title: this.$t('my.line.number'),
          dataIndex: 'scheduleLineNumber',
          align: 'center',
          width: 90,
        },
        {
          /* 提交日期 */
          title: this.$t('acp.requisitionDate'),
          dataIndex: 'requisitionDate',
          width: 100,
          align: 'center',
          render: value => (
            <Popover content={value ? moment(value).format('YYYY-MM-DD') : ''}>
              {value ? moment(value).format('YYYY-MM-DD') : ''}
            </Popover>
          ),
        }, // 关联金额
        {
          title: this.$t('my.link.amount'),
          dataIndex: 'relationAmount',
          align: 'center',
          render: desc => this.filterMoney(desc),
        },

        {
          title: this.$t({ id: 'my.receivable' } /* 收款方 */),
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <div style={{ whiteSpace: 'normal' }}>
                  {record.payeeCategory === 'EMPLOYEE'
                    ? `${this.$t('acp.employee')}-${record.partnerName}`
                    : `${this.$t('acp.vendor')}-${record.partnerName}`}
                </div>
              </div>
            );
          },
        },
        {
          /* 状态 */
          title: this.$t({ id: 'common.column.status' }),
          key: 'status',
          width: '10%',
          align: 'center',
          dataIndex: 'status',
          render: accountStatus => (
            <Badge
              status={this.$statusList[accountStatus].state}
              text={this.$statusList[accountStatus].label}
            />
          ),
        },
      ],
      columns4: [
        // 合同行号
        {
          title: this.$t('my.contract.line.number'),
          dataIndex: 'paymentReturnStatus',
          align: 'center',
          width: 90,
        },
        {
          title: this.$t('pay.refund.billCode'),
          dataIndex: 'billcode',
          align: 'center',
          render: desc => <Popover content={desc}>{desc}</Popover>,
        },
        {
          // 付款流水好
          title: this.$t('pay.workbench.receiptNumber'),
          dataIndex: 'documentNumber',
          align: 'center',
          render: desc => <Popover content={desc}>{desc}</Popover>,
        }, // 行序号
        {
          title: this.$t('my.line.number'),
          dataIndex: 'num',
          align: 'center',
          width: 90,
          render: (text, record, index) => (
            <Popover content={Number(index + 1)}>{Number(index + 1)}</Popover>
          ),
        },
        {
          // 操作类型
          title: this.$t('operate.log.operation.type'),
          dataIndex: 'operationType',
          align: 'center',
          width: 100,
          render: desc => (
            <Popover content={operationTypeList[desc]}>{operationTypeList[desc]}</Popover>
          ),
        },
        {
          title: this.$t('common.amount'),
          dataIndex: 'amount',
          align: 'center',
          width: 120,
          render: desc => this.filterMoney(desc),
        },
        {
          /* 提交日期 */
          title: this.$t('common.date'),
          dataIndex: 'createdDate',
          width: 100,
          align: 'center',
          render: value => (
            <Popover content={value ? moment(value).format('YYYY-MM-DD') : ''}>
              {value ? moment(value).format('YYYY-MM-DD') : ''}
            </Popover>
          ),
        },
        {
          /* 状态 */
          title: this.$t('my.deal.status'),
          key: 'status',
          width: '10%',
          dataIndex: 'paymentStatus',
          align: 'center',
          render: (desc, record) => paymentStatus[record.operationType][desc],
        },
      ],
      applymentData: [],
      accountData: [],
      payDetailData: [],
      applyPagination: {
        total: 0,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 5,
        current: 1,
        pageSizeOptions: ['5', '10', '20', '30', '40'],
      },
      accountPagination: {
        total: 0,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 5,
        current: 1,
        onChange: this.onChangeAccountPage,
        onShowSizeChange: this.onShowSizeAccount,
        //pageSizeOptions: ['5', '10', '20', '30', '40'],
      },
      prepaymentPagination: {
        total: 0,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 5,
        current: 1,
        page: 0,
        pageSizeOptions: ['5', '10', '20', '30', '40'],
      },
      pagination1: {
        total: 0,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 5,
        current: 1,
        page: 0,
        pageSizeOptions: ['5', '10', '20', '30', '40'],
      },
      accountPage: 0,
      prepaymentPage: 0,
      applyPage: 0,
      page: 0,
    };
  }

  componentDidMount() {
    this.getPrepaymentHeadByContract();
    this.setState({
      tabValue: 'prepayment',
      tabName: 'prepayment',
    });
  }
  //accountPagination
  //accountPage:0,
  //prepaymentPage:0,
  //applyPage:0,
  //page:0,
  onChangeAccountPage = page => {
    console.log(page);
    if (page - 1 !== this.state.accountPage) {
      this.setState(
        {
          accountPage: page - 1,
        },
        () => {
          this.getAccountHeadByContract();
        }
      );
    }
  };
  onShowSizeAccount = (current, pageSize) => {
    this.setState(
      {
        loading: true,
        accountPage: current - 1,
        accountPagination: { pageSize: pageSize },
      },
      () => {
        this.getAccountHeadByContract();
      }
    );
  };

  // 显示费用申请单
  showExpenseApplication = record => {
    this.setState({
      expenseApplicationShow: true,
      expenseApplicationId: record.id,
    });
  };

  // 显示预付款
  showPrepayment = record => {
    this.setState({
      prePaymentShow: true,
      prepaymentId: record.headerId,
    });
  };
  handleLinkReport(record) {
    this.setState({
      reportShow: true,
      reportId: record.expReportHeaderId,
      params: { id: record.expReportHeaderId },
    });
  }
  // 获取合同关联的预付款单
  getPrepaymentHeadByContract = () => {
    const { prepaymentPagination, prepaymentPage } = this.state;
    contractService
      .getPrepaymentHeadByContractNumber(this.props.headerData.contractNumber)
      .then(res => {
        const data = [];
        res.data.map(item =>
          item.line.map((i, index) =>
            data.push({
              ...item.head,
              ...i,
              lineNumber: index + 1 + prepaymentPagination.page * prepaymentPagination.pageSize,
              headerId: item.head.id,
            })
          )
        );
        prepaymentPagination.total = data.length;
        this.setState(
          {
            prepaymentData: data,
            pagination1: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: prepaymentPage + 1,
              onChange: this.onChangePrepaymentPage,
            },
          },
          () => {}
        );
      })
      .catch(e => {
        if (e && e.response) message.error(e.response.data.message);
      });
  };

  // 获取合同关联申请单
  getApplyHeadByContrcat = () => {
    const { applyPagination, applyPage } = this.state;
    contractService
      .getApplyHeadByContrcat(this.props.headerData.id)
      .then(res => {
        const data = [];
        res.data.map((item, index) =>
          data.push({
            ...item,
            lineNumber: index + 1 + applyPagination.page * applyPagination.pageSize,
          })
        );
        applyPagination.total = data.length;
        this.setState(
          {
            applymentData: data,
            pagination1: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: applyPage + 1,
              onChange: this.onChangeApplytPage,
            },
          },
          () => {}
        );
      })
      .catch(e => {
        if (e && e.response) message.error(e.response.data.message);
      });
  };

  // 获取合同关联的报账单
  getAccountHeadByContract = () => {
    const { accountPage, accountPagination } = this.state;
    console.log(accountPagination);
    contractService
      .getAccountHeadByContract(this.props.headerData.id, accountPage, accountPagination.pageSize)
      .then(res => {
        /*const data = [];
      res.data.map(item => {
        item.expensePaymentScheduleList.map(i => {
          data.push({ ...item.expenseaccountHeader, ...i });
        });
      });*/
        this.setState({
          accountData: res.data,
          accountPagination: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            current: accountPage + 1,
            accountPagination,
            pageSize: accountPagination.pageSize,
            onChange: this.onChangeAccountPage,
            onShowSizeChange: this.onShowSizeAccount,
            showTotal: total => `共搜到 ${total} 条数据`,
          },
        });
      });
  };

  // 获取支付明细数据payData
  getPayDetailByContractHeaderId = () => {
    const { page, pageSize1 } = this.state;
    contractService
      .getPayDetailByContractHeaderId(this.props.headerData.id, page, pageSize1)
      .then(res => {
        this.setState({
          payDetailData: res.data,
          pagination1: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            current: page + 1,
            onChange: this.onChangePaper1,
          },
        });
      });
  };

  // 支付明细页面切换
  onChangePaper1 = page1 => {
    if (page1 - 1 !== this.state.page1) {
      this.setState({ page1: page1 - 1 }, () => {
        this.getPayDetailByContractHeaderId();
      });
    }
  };

  renderContent = () => {
    const {
      columns1,
      columns2,
      columns3,
      columns4,
      prepaymentData,
      applymentData,
      accountData,
      payDetailData,
      prepaymentPagination,
      applyPagination,
      accountPagination,
      pagination1,
      tabName,
    } = this.state;
    if (tabName === 'prepayment') {
      return (
        <div>
          <Table
            rowKey={record => record.id}
            columns={columns1}
            dataSource={prepaymentData}
            pagination={prepaymentPagination}
            bordered
            size="middle"
            onRow={record => {
              return {
                onClick: () => {
                  this.showPrepayment(record);
                },
              };
            }}
          />
        </div>
      );
    } else if (tabName === 'apply') {
      return (
        <div>
          <Table
            rowKey={record => record.id}
            columns={columns2}
            dataSource={applymentData}
            pagination={applyPagination}
            bordered
            size="middle"
            onRow={record => {
              return {
                onClick: () => {
                  this.showExpenseApplication(record);
                },
              };
            }}
          />
        </div>
      );
    } else if (tabName === 'account') {
      return (
        <div>
          <Table
            rowKey={record => record.id}
            columns={columns3}
            dataSource={accountData}
            pagination={accountPagination}
            bordered
            size="middle"
          />
        </div>
      );
    } else if (tabName === 'payDetail') {
      return (
        <Table
          rowKey={record => record.id}
          columns={columns4}
          dataSource={payDetailData}
          pagination={pagination1}
          bordered
          size="middle"
        />
      );
    }
  };

  handleTabsChange = value => {
    this.setState(
      {
        tabName: value,
        accountPage: 0,
        prepaymentPage: 0,
        applyPage: 0,
        page: 0,
      },
      () => {
        if (value === 'prepayment') {
          this.getPrepaymentHeadByContract();
        } else if (value === 'account') {
          this.getAccountHeadByContract();
        } else if (value === 'apply') {
          this.getApplyHeadByContrcat();
        } else if (value === 'payDetail') {
          this.getPayDetailByContractHeaderId();
        }
      }
    );
  };

  render() {
    const {
      tabValue,
      expenseApplicationShow,
      expenseApplicationId,
      prePaymentShow,
      reportShow,
      prepaymentId,
      reportId,
      params,
    } = this.state;
    return (
      <div>
        <Tabs defaultActiveKey={tabValue} onChange={this.handleTabsChange}>
          <TabPane tab="关联预付款单" key="prepayment" />
          <TabPane tab="关联申请单" key="apply" />
          <TabPane tab="关联报账单" key="account" />
          <TabPane tab="支付明细" key="payDetail" />
        </Tabs>
        {this.renderContent()}
        <Modal
          title="合同详情"
          visible={expenseApplicationShow}
          onCancel={() => {
            this.setState({ expenseApplicationShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
          destroyOnClose
        >
          <ExpenseApplicationForm id={expenseApplicationId} />
        </Modal>
        <Modal
          title="预付款详情"
          visible={prePaymentShow}
          onCancel={() => {
            this.setState({ prePaymentShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
          destroyOnClose
        >
          <PrepaymentDetail id={prepaymentId} />
        </Modal>
        <Modal
          title="报账单详情"
          visible={reportShow}
          onCancel={() => {
            this.setState({ reportShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
          destroyOnClose
        >
          <ExpenseReportDetail params={params} />
        </Modal>
      </div>
    );
  }
}

relationInfo.propTypes = {
  headerData: PropTypes.any.isRequired,
};

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

const wrappedRelationInfo = Form.create()(relationInfo);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedRelationInfo);
