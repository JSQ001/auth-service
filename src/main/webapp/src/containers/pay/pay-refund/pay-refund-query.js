import React from 'react';
import { connect } from 'dva';
import { Form, Tabs, message, Badge, Popover } from 'antd';
import Table from 'widget/table';
const TabPane = Tabs.TabPane;
import payRefundService from './pay-refund.service';
import SlideFrame from 'widget/slide-frame';
import SearchArea from 'widget/search-area';
import moment from 'moment';
import NewPayRefund from './new-pay-refund';
import PayRefundDetail from './pay-refund-detail';
import config from 'config';
import httpFetch from 'share/httpFetch';
import { messages } from 'utils/utils';

class PayRefund extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      status: {
        N: { label: this.$t({ id: 'pay.refund.default' } /*编辑中*/), state: 'default' },
        P: { label: this.$t({ id: 'pay.refund.processing' } /*复核中*/), state: 'processing' },
        S: { label: this.$t({ id: 'pay.refund.success' } /*复核通过*/), state: 'success' },
        F: { label: this.$t({ id: 'pay.refund.error' } /*复核驳回*/), state: 'error' },
      },
      tabValue: 'unRefund',
      loading1: false,
      loading2: false,
      searchForm1: [
        {
          type: 'input',
          colSpan: 6,
          id: 'billcode',
          label: `${this.$t({ id: 'pay.refund.billCode' } /*付款流水号*/)}`,
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'documentNumber',
          label: `${this.$t({ id: 'pay.refund.documentNumber' } /*单据编号*/)}`,
        },
        {
          type: 'value_list',
          colSpan: 6,
          id: 'documentCategory',
          label: this.$t({ id: 'pay.refund.documentTypeName' } /*单据类型*/),
          options: [],
          valueListCode: 2106,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'partner',
          items: [
            {
              type: 'value_list',
              id: 'partnerCategory',
              label: messages('pay.workbench.type') /*类型*/,
              valueListCode: 2107,
              options: [],
              event: 'PARTNER',
            },
            {
              type: 'list',
              id: 'partnerId',
              label: `${this.$t({ id: 'pay.refund.partnerName' } /*收款方*/)}`,
              listType: 'select_ven',
              single: true,
              disabled: true,
              labelKey: 'name',
              valueKey: 'id',
              listExtraParams: { companyId: props.company.id },
            },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'amountRange',
          items: [
            {
              type: 'input',
              id: 'amountFrom',
              label: `${this.$t({ id: 'pay.refund.paymentAmountFrom' } /*付款金额从*/)}`,
            },
            {
              type: 'input',
              id: 'amountTo',
              label: `${this.$t({ id: 'pay.refund.paymentAmountTo' } /*付款金额至*/)}`,
            },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'dateRange',
          items: [
            {
              type: 'date',
              id: 'payDateFrom',
              label: `${this.$t({ id: 'pay.refund.payDateFrom' } /*支付日期从*/)}`,
            },
            {
              type: 'date',
              id: 'payDateTo',
              label: `${this.$t({ id: 'pay.refund.payDateTo' } /*支付日期至*/)}`,
            },
          ],
        },
        {
          type: 'select',
          id: 'employeeId',
          colSpan: 6,
          label: `${this.$t({ id: 'pay.refund.employeeName' } /*申请人*/)}`,
          defaultValue: this.props.user.id,
          options: [
            {
              value: this.props.user.id,
              label: this.props.user.userName,
            },
          ],
          disabled: true,
        },
      ],
      searchForm2: [
        {
          type: 'input',
          colSpan: 6,
          id: 'billcode',
          label: `${this.$t({ id: 'pay.refund.RefundBillCode' } /*退款支付流水号*/)}`,
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'refBillCode',
          label: `${this.$t({ id: 'pay.refund.refBillCode' } /*原支付流水号*/)}`,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'partner',
          items: [
            {
              type: 'value_list',
              colSpan: 1,
              id: 'partnerCategory',
              label: messages('pay.workbench.type') /*类型*/,
              valueListCode: 2107,
              options: [],
              event: 'PARTNER',
            },
            {
              type: 'list',
              colSpan: 5,
              id: 'partnerId',
              label: `${this.$t({ id: 'pay.refund.partnerCategoryName' } /*退款方*/)}`,
              listType: 'select_ven',
              single: true,
              disabled: true,
              labelKey: 'name',
              valueKey: 'id',
              listExtraParams: { companyId: props.company.id },
            },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'dateRange',
          items: [
            {
              type: 'date',
              id: 'returnDateFrom',
              label: `${this.$t({ id: 'pay.refund.returnDateFrom' } /*退款日期从*/)}`,
            },
            {
              type: 'date',
              id: 'returnDateTo',
              label: `${this.$t({ id: 'pay.refund.returnDateTo' } /*退款日期至*/)}`,
            },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'amountRange',
          items: [
            {
              type: 'input',
              id: 'amountFrom',
              label: `${this.$t({ id: 'pay.refund.amountFrom' } /*金额从*/)}`,
            },
            {
              type: 'input',
              id: 'amountTo',
              label: `${this.$t({ id: 'pay.refund.amountTo' } /*金额至*/)}`,
            },
          ],
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'backFlashStatus',
          label: `${this.$t({ id: 'pay.refund.status' } /*状态*/)}`,
          options: [
            { value: 'N', label: this.$t({ id: 'pay.refund.default' } /*编辑中*/) },
            { value: 'P', label: this.$t({ id: 'pay.refund.processing' } /*复核中*/) },
            { value: 'S', label: this.$t({ id: 'pay.refund.success' } /*复核通过*/) },
            { value: 'F', label: this.$t({ id: 'pay.refund.error' } /*复核驳回*/) },
          ],
          key: '',
        }, //状态
        {
          type: 'select',
          colSpan: 6,
          id: 'draweeAccountNumber',
          label: this.$t({ id: 'pay.refund.draweeAccountNumber' } /*收款方账号*/),
          options: [],
        }, //收款账号
      ],
      unRefundSearchParams: {
        employeeId: this.props.user.id,
      },
      myRefundSearchParams: {
        employeeId: this.props.user.id,
      },
      columns1: [
        {
          title: `${this.$t({ id: 'pay.refund.billCode' } /*付款流水号*/)}`,
          dataIndex: 'billcode',
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc ? desc : ''}</Popover>
            </span>
          ),
        },
        {
          title: `${this.$t({ id: 'pay.refund.documentNumber' } /*单据编号*/)}`,
          dataIndex: 'documentNumber',
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc ? desc : ''}</Popover>
            </span>
          ),
        },
        {
          title: `${this.$t({ id: 'pay.refund.documentTypeName' } /*单据类型*/)}`,
          dataIndex: 'documentTypeName',
          align: 'center',
        },
        {
          title: `${this.$t({ id: 'pay.refund.employeeName' } /*申请人*/)}`,
          dataIndex: 'employeeName',
          align: 'center',
          render: (value, record) => {
            return (
              <Popover
                content={
                  <div>
                    {' '}
                    {record.employeeCode} <span className="ant-divider" /> {value}{' '}
                  </div>
                }
              >
                <div>
                  {record.employeeCode}
                  <span className="ant-divider" />
                  {value}
                </div>
              </Popover>
            );
          },
        },
        {
          title: `${this.$t({ id: 'pay.refund.currency' } /*币种*/)}`,
          dataIndex: 'currency',
          align: 'center',
        },
        {
          title: `${this.$t({ id: 'pay.refund.amount' } /*付款金额*/)}`,
          align: 'center',
          dataIndex: 'amount',
          render: this.filterMoney,
        },
        {
          title: `${this.$t({ id: 'pay.refund.abledRefundAmount' } /*可退款金额*/)}`,
          align: 'center',
          dataIndex: 'abledRefundAmount',
          render: this.filterMoney,
        },
        {
          title: `${this.$t({ id: 'pay.refund.partnerName' } /*收款方*/)}`,
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <Popover
                content={
                  <div>
                    {' '}
                    {record.partnerCategoryName} <span className="ant-divider" /> {value}{' '}
                  </div>
                }
              >
                <div>
                  {record.partnerCategoryName}
                  <span className="ant-divider" />
                  {value}
                </div>
              </Popover>
            );
          },
        },
        {
          title: `${this.$t({ id: 'pay.refund.payDate' } /*付款日期*/)}`,
          dataIndex: 'payDate',
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: `${this.$t({ id: 'pay.refund.operate' } /*操作*/)}`,
          dataIndex: 'id',
          align: 'center',
          render: (id, record) => (
            <a onClick={() => this.doRefund(record)}>
              {' '}
              {this.$t({ id: 'pay.refund.doRefund' } /*发起退款*/)}
            </a>
          ),
        },
      ],
      columns2: [
        {
          title: `${this.$t({ id: 'pay.refund.RefundBillCode' } /*退款支付流水号*/)}`,
          dataIndex: 'billcode',
          align: 'center',
        },
        {
          title: `${this.$t({ id: 'pay.refund.refBillCode' } /*原支付流水号*/)}`,
          dataIndex: 'refBillCode',
          align: 'center',
        },
        {
          title: `${this.$t({ id: 'pay.refund.returnDate' } /*退款日期*/)}`,
          dataIndex: 'payDate',
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: `${this.$t({ id: 'pay.refund.partnerCategoryName' } /*退款方*/)}`,
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <Popover
                content={
                  <div>
                    {' '}
                    {record.partnerCategoryName} <span className="ant-divider" /> {value}{' '}
                  </div>
                }
              >
                <div>
                  {record.partnerCategoryName}
                  <span className="ant-divider" />
                  {value}
                </div>
              </Popover>
            );
          },
        },
        {
          title: `${this.$t({ id: 'pay.refund.payeeAccountNumber' } /*退款账号*/)}`,
          align: 'center',
          dataIndex: 'payeeAccountNumber',
          render: desc => (
            <span>
              <Popover content={desc}>{desc ? desc : ''}</Popover>
            </span>
          ),
        },
        {
          title: `${this.$t({ id: 'pay.refund.returnAmount' } /*本次退款金额*/)}`,
          align: 'center',
          dataIndex: 'amount',
          render: this.filterMoney,
        },
        {
          title: `${this.$t({ id: 'pay.refund.draweeAccountNumber' } /*收款方账号*/)}`,
          align: 'center',
          dataIndex: 'draweeAccountNumber',
        },
        {
          title: `${this.$t({ id: 'pay.refund.draweeAccountName' } /*收款方账户名*/)}`,
          align: 'center',
          dataIndex: 'draweeAccountName',
        },
        {
          title: `${this.$t({ id: 'pay.refund.status' } /*状态*/)}`,
          dataIndex: 'paymentStatus',
          align: 'center',
          render: value => (
            <Badge status={this.state.status[value].state} text={this.state.status[value].label} />
          ),
        },
      ],
      unRefundData: [],
      myRefundData: [],
      unRefundPagination: {
        total: 0,
      },
      myRefundPagination: {
        total: 0,
      },
      unRefundPage: 0,
      unRefundPageSize: 10,
      myRefundPage: 0,
      myRefundPageSize: 10,
      openWindowFlag: false, //发起退款窗口
      deRecord: null,
      frameTitle: '', //侧滑标题
      frameFlag: false, //侧滑开关
      myRefundRecord: null,
      tabName: 'unRefund',
    };
  }

  componentWillMount() {
    this.getUnRefundList();
    this.getCompanyAccount();
  }

  //获取收款账户 即原付款公司
  getCompanyAccount = () => {
    let url = `${config.payUrl}/api/CompanyBank/selectByCompanyId?companyId=${
      this.props.company.id
    }`;
    httpFetch
      .get(url)
      .then(res => {
        if (res.status === 200) {
          let list = [];
          res.data.map(item => {
            list.push({ value: item.bankAccountNumber, label: item.bankAccountNumber });
          });
          let form = this.state.searchForm2;

          form[6].options = list;

          this.setState({ searchForm2: form });
        }
      })
      .catch(() => {
        message.error(
          this.$t({ id: 'pay.refund.getDraweeAccountNumberError' } /*获取收款方银行账户信息失败*/)
        );
      });
  };

  clearHandle = () => {
    this.setState({ unRefundSearchParams: {}, myRefundSearchParams: {} }, () => {
      if (this.state.tabName === 'myRefund') {
        this.searchEventHandle('PARTNER', '');
      } else {
        this.searchEventHandle1('PARTNER', '');
      }
    });
  };
  doRefund = value => {
    this.setState({ openWindowFlag: true, deRecord: value });
  };
  //获取待退款数据
  getUnRefundList = () => {
    const { unRefundPage, unRefundPageSize, unRefundSearchParams } = this.state;

    this.setState({ loading1: true });
    let params = { ...unRefundSearchParams };
    params.employeeId = params.employeeId || this.props.user.id;
    payRefundService
      .queryUnRefundList(params, unRefundPage, unRefundPageSize)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            unRefundData: res.data || [],
            loading1: false,
            unRefundPagination: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: unRefundPage + 1,
              pageSize: unRefundPageSize,
              onChange: this.onUnRefundChangePaper,
              pageSizeOptions: ['10', '20', '30', '40'],
              showSizeChanger: true,
              onShowSizeChange: this.onUnRefundChangePageSize,
              showQuickJumper: true,
              showTotal: (total, range) =>
                this.$t(
                  { id: 'common.show.total' },
                  { range0: `${range[0]}`, range1: `${range[1]}`, total: total }
                ),
            },
          });
        }
      })
      .catch(() => {
        this.setState({ loading1: false });
        message.error(
          this.$t({ id: 'common.error' } /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/)
        );
      });
  };
  //待退款点击页码
  onUnRefundChangePaper = page => {
    if (page - 1 !== this.state.unRefundPage) {
      this.setState({ unRefundPage: page - 1 }, () => {
        this.getUnRefundList();
      });
    }
  };

  //每页多少条未退款
  onUnRefundChangePageSize = (page, pageSize) => {
    if (page - 1 !== this.state.unRefundPage || pageSize !== this.state.unRefundPageSize) {
      this.setState({ unRefundPage: page - 1, unRefundPageSize: pageSize }, () => {
        this.getUnRefundList();
      });
    }
  };

  //每页多少条 我发起退款
  onMyRefundChangePageSize = (page, pageSize) => {
    if (page - 1 !== this.state.myRefundPage || pageSize !== this.state.myRefundPageSize) {
      this.setState({ myRefundPage: page - 1, myRefundPageSize: pageSize }, () => {
        this.getMyRefundList();
      });
    }
  };
  //待退款搜索
  unRefundSearch = values => {
    values.payDateFrom && (values.payDateFrom = values.payDateFrom.format('YYYY-MM-DD'));
    values.payDateTo && (values.payDateTo = values.payDateTo.format('YYYY-MM-DD'));
    if (JSON.stringify(values.partnerId) !== '[]' && values.partnerId) {
      values.partnerId = values.partnerId[0].id;
    }
    this.setState({ unRefundSearchParams: values, unRefundPage: 0 }, () => {
      this.getUnRefundList();
    });
  };

  //获取我的退款数据
  getMyRefundList = () => {
    const { myRefundPage, myRefundPageSize, myRefundSearchParams } = this.state;

    this.setState({ loading2: true });
    let params = { ...myRefundSearchParams };

    payRefundService
      .queryMyRefundList(params, myRefundPage, myRefundPageSize)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            myRefundData: res.data || [],
            loading2: false,
            myRefundPagination: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: myRefundPage + 1,
              pageSize: myRefundPageSize,
              onChange: this.onMyRefundChangePaper,
              pageSizeOptions: ['10', '20', '30', '40'],
              showSizeChanger: true,
              onShowSizeChange: this.onMyRefundChangePageSize,
              showQuickJumper: true,
              showTotal: (total, range) =>
                this.$t(
                  { id: 'common.show.total' },
                  { range0: `${range[0]}`, range1: `${range[1]}`, total: total }
                ),
            },
          });
        }
      })
      .catch(() => {
        this.setState({ loading2: false });
        message.error(
          this.$t({ id: 'common.error' } /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/)
        );
      });
  };
  //我的退款点击页码
  onMyRefundChangePaper = page => {
    if (page - 1 !== this.state.myRefundPage) {
      this.setState({ myRefundPage: page - 1 }, () => {
        this.getMyRefundList();
      });
    }
  };
  //我的退款搜索
  myRefundSearch = values => {
    values.returnDateFrom && (values.returnDateFrom = values.returnDateFrom.format('YYYY-MM-DD'));
    values.returnDateTo && (values.returnDateTo = values.returnDateTo.format('YYYY-MM-DD'));
    if (JSON.stringify(values.partnerId) !== '[]' && values.partnerId) {
      values.partnerId = values.partnerId[0].id;
    }
    this.setState({ myRefundSearchParams: values, myRefundPage: 0 }, () => {
      this.getMyRefundList();
    });
  };
  //tab页改变函数
  handleTabsChange = value => {
    this.setState({
      tabName: value,
    });
    this.formRef.setValues({ partnerId: undefined, partnerCategory: '' });
    this.setState(
      { unRefundSearchParams: { employeeId: this.props.user.id }, myRefundSearchParams: {} },
      () => {
        if (value === 'myRefund') {
          this.searchEventHandle('PARTNER', '');
          this.getMyRefundList();
        } else {
          this.searchEventHandle1('PARTNER', '');
          this.getUnRefundList();
        }
      }
    );
  };
  //退款窗口关闭
  cancelWindow = () => {
    this.setState({ openWindowFlag: false });
  };
  //退款窗口完全关闭后回掉
  restFormFunc = () => {
    this.setState({ openWindowFlag: false });
    this.getUnRefundList();
  };
  //退款窗口关闭
  cancelWindow1 = () => {
    this.setState({ frameFlag: false });
  };
  //退款窗口完全关闭后回掉
  restFormFunc1 = () => {
    this.setState({ frameFlag: false }, () => {
      this.getMyRefundList();
    });
  };
  //行点击
  rowClick = record => {
    let title;
    if (record.backFlashStatus === 1001 || record.backFlashStatus === 1005) {
      title = this.$t({ id: 'pay.refund.editRefund' } /*编辑退款*/);
    } else {
      title = this.$t({ id: 'pay.refund.refundDetail' } /*退款详情*/);
    }
    this.setState({ frameFlag: true, myRefundRecord: record, frameTitle: title });
  };

  searchEventHandle = (event, value) => {
    if (event === 'PARTNER') {
      let searchForm2 = this.state.searchForm2;
      value = value ? value : '';
      if (value === 'EMPLOYEE') {
        let item = searchForm2[2];
        item.items[1].disabled = false;
        item.items[1].listExtraParams['empFlag'] = '1001';
        searchForm2[2] = item;
        this.formRef.setValues({ partnerId: undefined });
        this.setState({ searchForm2: searchForm2 });
      }
      if (value === 'VENDER') {
        let item = searchForm2[2];
        item.items[1].disabled = false;
        item.items[1].listExtraParams['empFlag'] = '1002';
        searchForm2[2] = item;
        this.formRef.setValues({ partnerId: undefined });
        this.setState({ searchForm2: searchForm2 });
      }
      if (value === '') {
        let myRefundSearchParams = this.state.myRefundSearchParams;
        let item = searchForm2[2];
        item.items[1].disabled = true;
        searchForm2[2] = item;
        this.formRef.setValues({ partnerId: undefined });
        this.setState({ searchForm2: searchForm2 });
      }
    }
  };

  searchEventHandle1 = (event, value) => {
    if (event === 'PARTNER') {
      let searchForm1 = this.state.searchForm1;
      value = value ? value : '';
      if (value === 'EMPLOYEE') {
        let item = searchForm1[3];
        item.items[1].disabled = false;
        item.items[1].listExtraParams['empFlag'] = '1001';
        searchForm1[3] = item;
        this.formRef.setValues({ partnerId: undefined });
        this.setState({ searchForm1: searchForm1 });
      }
      if (value === 'VENDER') {
        let item = searchForm1[3];
        item.items[1].disabled = false;
        item.items[1].listExtraParams['empFlag'] = '1002';
        searchForm1[3] = item;
        this.formRef.setValues({ partnerId: undefined });
        this.setState({ searchForm1: searchForm1 });
      }
      if (value === '') {
        let item = searchForm1[3];
        item.items[1].disabled = true;
        searchForm1[3] = item;
        this.formRef.setValues({ partnerId: undefined });
        this.setState({ searchForm1: searchForm1 });
      }
    }
  };
  renderContent = () => {
    const {
      tabValue,
      loading1,
      loading2,
      searchForm1,
      searchForm2,
      columns1,
      unRefundData,
      deRecord,
      myRefundData,
      unRefundPagination,
      myRefundPagination,
      openWindowFlag,
      columns2,
      frameTitle,
      frameFlag,
      myRefundRecord,
    } = this.state;
    if (this.state.tabName === 'unRefund') {
      return (
        <div>
          <SearchArea
            searchForm={searchForm1}
            submitHandle={this.unRefundSearch}
            maxLength={4}
            eventHandle={this.searchEventHandle1}
            wrappedComponentRef={inst => (this.formRef = inst)}
            clearHandle={this.clearHandle}
          />
          <div className="table-header" />
          <Table
            rowKey={record => record.id}
            columns={columns1}
            dataSource={unRefundData}
            pagination={unRefundPagination}
            loading={loading1}
            bordered
            size="middle"
          />
        </div>
      );
    } else {
      return (
        <div>
          <SearchArea
            searchForm={searchForm2}
            submitHandle={this.myRefundSearch}
            maxLength={4}
            eventHandle={this.searchEventHandle}
            wrappedComponentRef={inst => (this.formRef = inst)}
            clearHandle={this.clearHandle}
          />
          <div className="table-header" />
          <Table
            rowKey={record => record.id}
            columns={columns2}
            dataSource={myRefundData}
            pagination={myRefundPagination}
            onRow={record => ({
              onClick: () => this.rowClick(record),
            })}
            loading={loading2}
            bordered
            size="middle"
          />
        </div>
      );
    }
  };
  render() {
    const {
      tabValue,
      loading1,
      loading2,
      searchForm1,
      searchForm2,
      columns1,
      unRefundData,
      deRecord,
      myRefundData,
      unRefundPagination,
      myRefundPagination,
      openWindowFlag,
      columns2,
      frameTitle,
      frameFlag,
      myRefundRecord,
    } = this.state;
    return (
      <div>
        <Tabs defaultActiveKey={tabValue} onChange={this.handleTabsChange}>
          <TabPane tab={this.$t({ id: 'pay.refund.unRefund' } /*待退款*/)} key="unRefund" />
          <TabPane tab={this.$t({ id: 'pay.refund.myRefund' } /*我发起的退款*/)} key="myRefund" />
        </Tabs>
        {this.renderContent()}
        <SlideFrame
          title={this.$t({ id: 'pay.refund.refundDetail' } /*退款详情*/)}
          show={openWindowFlag}
          onClose={this.cancelWindow}
          afterClose={this.restFormFunc}
        >
          <NewPayRefund
            params={{ record: deRecord, flag: openWindowFlag }}
            onClose={this.cancelWindow}
          />
        </SlideFrame>

        <SlideFrame
          title={frameTitle}
          show={frameFlag}
          onClose={this.cancelWindow1}
          afterClose={this.restFormFunc1}
        >
          <PayRefundDetail
            params={{ record: myRefundRecord, flag: frameFlag }}
            onClose={this.cancelWindow1}
          />
        </SlideFrame>
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

const wrappedPayRefund = Form.create()(PayRefund);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedPayRefund);
