import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import contractService from 'containers/contract/contract-approve/contract.service';
import {
  Form,
  Tabs,
  Button,
  Row,
  Card,
  Col,
  Spin,
  message,
  Popover,
  Popconfirm,
  Breadcrumb,
  Tag,
  Badge,
  Modal,
} from 'antd';
import Table from 'widget/table';
const TabPane = Tabs.TabPane;
import config from 'config';
import moment from 'moment';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import SlideFrame from 'components/Widget/slide-frame';
import NewPayPlan from 'containers/contract/my-contract/new-pay-plan';
import NewContractInfo from 'containers/contract/my-contract/new-contract-info';
import FormCard from 'containers/contract/my-contract/form-card';
//import 'styles/contract/my-contract/contract-detail.scss'
import 'styles/pre-payment/my-pre-payment/pre-payment-detail.scss';
import ApproveHistory from 'widget/Template/approve-history-work-flow';

import CustomTable from 'components/Widget/custom-table';
import PropTypes from 'prop-types';
import PrePaymentCommon from 'containers/pre-payment/my-pre-payment/detail-readonly';
import ExpenseApplication from 'containers/expense-application-form/detail-readonly';
import ProjectApplication from 'containers/project-manage/my-project-apply/project-apply-details/mol-project-details-common';
import ReimburseDetail from 'containers/reimburse/reimburse-approve/reimburse-detail-common';
import ExpenseReportDetail from 'containers/reimburse/my-reimburse/reimburse-detail-finance';
import httpFetch from 'share/httpFetch';

class ContractDetailCommon extends React.Component {
  constructor(props) {
    super(props);
    let operationTypeList = {
      payment: this.$t('my.pay'), //付款
      reserved: this.$t('acp.payment.reserved'), //反冲
      refund: this.$t('acp.payment.refund'), //退票
      return: this.$t('acp.payment.return'), //退款
    };

    let paymentStatus = {
      reserved: {
        N: this.$t('common.create'), //新建
        P: this.$t('my.reserving'), //反冲中
        S: this.$t('my.reserve.success'), //反冲成功
        F: this.$t('my.reserve.failed'), //反冲失败
        //R: '重新退票',
        //C: '取消退票'
      },
      refund: {
        P: this.$t('my.refunding'), //退票中
        S: this.$t('my.refund.success'), //退票成功
        F: this.$t('my.refund.success'), //退票失败
        R: this.$t('my.refund.again'), //重新退票
        C: this.$t('my.refund.cancel'), //取消退票
      },
      return: {
        P: this.$t('my.returning'), //退款中
        S: this.$t('my.return.success'), //退款成功
        F: this.$t('my.return.failed'), //退款失败
        R: this.$t('my.piao.kuang'), //退票退款
        C: this.$t('my.kuang.piao'), //退款退票
      },
      payment: {
        P: this.$t('my.paying'), //
        S: this.$t('my.pay.success'), //支付成功
        F: this.$t('my.pay.failed'), //支付失败
        R: this.$t('pay.workbench.RePay'), //重新支付
        C: this.$t('pay.workbench.CancelPay'),
      },
    };

    this.state = {
      detailLoading: true,
      planLoading: false,
      historyLoading: false,
      topTapValue: 'contractInfo',
      currencyOptions: [],
      headerData: {},
      payPlanVisible: false,
      payInfo: {},
      approveHistory: [],
      prepaymentData: [], //关联的预付款单
      AccountData: [], //关联的报账单
      contractEdit: false, //合同是否可编辑
      contractStatus: {
        6002: { label: this.$t({ id: 'my.contract.state.cancel' } /*已取消*/), state: 'default' },
        6003: { label: this.$t({ id: 'my.contract.state.finish' } /*已完成*/), state: 'success' },
        1001: {
          label: this.$t({ id: 'my.contract.state.generate' } /*编辑中*/),
          state: 'processing',
        },
        6001: { label: this.$t({ id: 'my.contract.state.hold' } /*暂挂*/), state: 'warning' },
        1002: {
          label: this.$t({ id: 'my.contract.state.submitted' } /*审批中*/),
          state: 'processing',
        },
        1005: { label: this.$t({ id: 'my.contract.state.rejected' } /*已驳回*/), state: 'error' },
        1004: { label: this.$t({ id: 'my.contract.state.confirm' } /*已通过*/), state: 'success' },
        1003: {
          label: this.$t({ id: 'my.contract.state.withdrawal' } /*已撤回*/),
          state: 'warning',
        },
      },
      paginationAccount: {
        //  报账单分页
        total: 0,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total: total }
          ),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 5,
        current: 1,
        pageSizeOptions: ['5', '10', '20', '30', '40'],
      },

      //  预付款单分页
      preTableLoading: false,
      paginationPre: {
        total: 0,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total: total }
          ),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 5,
        current: 1,
        page: 0,
        pageSizeOptions: ['5', '10', '20', '30', '40'],
      },

      // 关联申请单
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
        page: 0,
        pageSizeOptions: ['5', '10', '20', '30', '40'],
      },

      //合同关联项目申请单
      projectPagination: {
        total: 0,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total: total }
          ),
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 5,
        current: 1,
        pageSizeOptions: ['5', '10', '20', '30', '40'],
      },

      columns: [
        {
          title: this.$t({ id: 'my.contract.currency' } /*币种*/),
          dataIndex: 'currency',
          align: 'center',
          width: 90,
        },
        {
          title: this.$t({ id: 'request.amount' } /*金额*/),
          dataIndex: 'amount',
          align: 'center',
          render: desc => this.filterMoney(desc),
        },
        {
          title: this.$t({ id: 'request.base.amount' } /*本币金额*/),
          dataIndex: 'funcAmount',
          align: 'center',
          render: (desc, record) => this.filterMoney(record.functionAmount),
        },
        {
          title: this.$t({ id: 'my.receivable' } /*收款方*/),
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <Tag color="#000">
                  {record.partnerCategory == 'EMPLOYEE'
                    ? this.$t('acp.employee')
                    : this.$t('acp.vendor')}
                </Tag>
                <div style={{ whiteSpace: 'normal' }}>{record.partnerName}</div>
              </div>
            );
          },
        },
        {
          title: this.$t({ id: 'my.contract.plan.pay.date' } /*计划付款日期*/),
          dataIndex: 'dueDate',
          align: 'center',
          render: value => (
            <Popover content={value ? moment(value).format('YYYY-MM-DD') : '-'}>
              {value ? moment(value).format('YYYY-MM-DD') : '-'}
            </Popover>
          ),
        },
        {
          title: this.$t({ id: 'common.remark' } /*备注*/),
          dataIndex: 'remark',
          align: 'center',
          render: value =>
            value ? (
              <Popover content={value} overlayStyle={{ maxWidth: 300 }}>
                {value}
              </Popover>
            ) : (
              '-'
            ),
        },
      ],
      data: [],

      showSlideFrame: false,
      slideFrameTitle: '',
      btnVisible: true,
      documentParams: {},
      record: {}, //资金计划行信息
      historyData: [], //历史信息
      //EditContract: menuRoute.getRouteItem('edit-contract', 'key'), //编辑合同
      payData: [],
      applicationData: [],
      pagination1: {
        total: 0,
      },
      page1: 0,
      pageSize1: 10,
      accountColumns: [
        //合同行号
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
              <a onClick={() => this.skipToDocumentDetail(record, 3)}>{documentNumber}</a>
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
          /*提交日期*/
          title: this.$t('acp.requisitionDate'),
          dataIndex: 'requisitionDate',
          width: 100,
          align: 'center',
          render: value => (
            <Popover content={value ? moment(value).format('YYYY-MM-DD') : ''}>
              {value ? moment(value).format('YYYY-MM-DD') : ''}
            </Popover>
          ),
        }, //关联金额
        {
          title: this.$t('my.link.amount'),
          dataIndex: 'relationAmount',
          align: 'center',
          render: desc => this.filterMoney(desc),
        },

        {
          title: this.$t({ id: 'my.receivable' } /*收款方*/),
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <div style={{ whiteSpace: 'normal' }}>
                  {record.payeeCategory === 'EMPLOYEE'
                    ? this.$t('acp.employee') + '-' + record.partnerName
                    : this.$t('acp.vendor') + '-' + record.partnerName}
                </div>
              </div>
            );
          },
        },
        {
          /*状态*/
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
      preColumns: [
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
              <a onClick={() => this.skipToDocumentDetail(record, 1)}>{requisitionNumber}</a>
            </Popover>
          ),
        },
        { title: this.$t('my.line.number'), dataIndex: 'lineNumber', align: 'center', width: 90 },
        {
          /*提交日期*/
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
          title: this.$t({ id: 'my.receivable' } /*收款方*/),
          dataIndex: 'partnerName',
          align: 'center',
          render: (value, record) => {
            return (
              <div>
                <div style={{ whiteSpace: 'normal' }}>
                  {record.payeeCategory === 'EMPLOYEE'
                    ? this.$t('acp.employee')
                    : this.$t('acp.vendor') + '-' + record.partnerName}
                </div>
              </div>
            );
          },
        },
        {
          /*状态*/
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
      applicationColumns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          align: 'center',
          render: (documentNumber, record) => (
            <Popover content={documentNumber}>
              <a onClick={() => this.skipToDocumentDetail(record, 2)}>{documentNumber}</a>
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

      //合同关联项目申请单
      projectColumns: [
        {
          title: '单据编号',
          dataIndex: 'projectNumber',
          align: 'center',
          render: (projectNumber, record) => (
            <Popover content={projectNumber}>
              <a onClick={() => this.skipToDocumentDetail(record, 4)}>{projectNumber}</a>
            </Popover>
          ),
        },
        {
          title: '项目申请单名称',
          dataIndex: 'projectName',
          align: 'center',
          render: projectName => <Popover content={projectName}>{projectName}</Popover>,
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
          title: '项目负责人',
          dataIndex: 'pmName',
          align: 'center',
          //render: pmName => this.filterMoney(pmName),
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

      payColumns: [
        //合同行号
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
          render: (desc, record) => <Popover content={desc}>{desc}</Popover>,
        },
        {
          //付款流水好
          title: this.$t('pay.workbench.receiptNumber'),
          dataIndex: 'documentNumber',
          align: 'center',
          render: (desc, record) => <Popover content={desc}>{desc}</Popover>,
        }, //行序号
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
          //操作类型
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
          /*提交日期*/
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
          /*状态*/
          title: this.$t('my.deal.status'),
          key: 'status',
          width: '10%',
          dataIndex: 'paymentStatus',
          align: 'center',
          render: (desc, record) => paymentStatus[record.operationType][desc],
        },
      ],
      detailsShow: false,
      detailsTitle: '',
      detailsId: '',
      view: 1,
    };
  }

  componentDidMount() {
    this.getInfo();
    //this.getPayList();
    // this.getPayDetailByContractHeaderId()
  }

  //获取合同信息
  getInfo = () => {
    const { columns } = this.state;
    this.setState({ detailLoading: true });
    contractService
      .getContractHeaderInfo(this.props.id)
      .then(response => {
        if (
          (response.data.status === 1001 ||
            response.data.status === 1003 ||
            response.data.status === 1005) &&
          columns[columns.length - 1].dataIndex != 'id'
        ) {
          //编辑中、已驳回、已撤回
          columns.splice(columns.length, 0, {
            title: this.$t({ id: 'common.operation' } /*操作*/),
            dataIndex: 'id',
            width: '10%',
            align: 'center',
            render: (text, record) => (
              <span>
                <a onClick={e => this.editItem(e, record)}>
                  {this.$t({ id: 'common.edit' } /*编辑*/)}
                </a>
                <span className="ant-divider" />
                <Popconfirm
                  title={this.$t({ id: 'common.confirm.delete' } /*确定要删除吗？*/)}
                  onConfirm={e => this.deleteItem(e, record)}
                >
                  <a>{this.$t({ id: 'common.delete' } /*删除*/)}</a>
                </Popconfirm>
              </span>
            ),
          });
        }
        let documentParams = {
          formName: response.data.contractTypeName,
          totalAmount: response.data.amount ? response.data.amount : 0,
          statusCode: response.data.status,
          remark: response.data.remark,
          businessCode: '12',
          currencyCode: response.data.currency,
          infoList: [
            { label: this.$t('my.contract.number'), value: response.data.contractNumber },
            {
              label: this.$t('common.applicant'),
              value:
                response.data.employee &&
                response.data.employee.employeeCode + '-' + response.data.employee.fullName,
            },
            { label: this.$t('my.contract.category'), value: response.data.contractCategoryName },
          ],
          attachments: response.data.attachments,
        };
        //获取币种
        !this.state.currencyOptions.length &&
          this.service.getCurrencyListByCompany(this.props.company.companyOid).then(res => {
            let currencyOptions = res.data;
            let nowCurrencyObj = currencyOptions.find(
              item => item.currency === response.data.currency
            );
            if (nowCurrencyObj !== undefined) {
              response.data.currencyName = nowCurrencyObj.currencyName;
            }
          });
        this.setState(
          {
            columns,
            documentParams,
            btnVisible: !response.data.contractName,
            headerData: response.data,
            detailLoading: false,
          },
          () => {
            this.props.getContractStatus(this.state.headerData.status);
            if (response.data.status >= 1004) {
              this.getPrepaymentHeadByContract();
              this.getAccountHeadByContract();
              this.getApplyHeadByContrcat();
              this.getPayDetailByContractHeaderId();
            }
          }
        );
      })
      .catch(e => {
        message.error(
          this.$t({ id: 'common.error' } /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/)
        );
      });
  };

  //获取合同关联的预付款单
  getPrepaymentHeadByContract() {
    let { headerData, paginationPre } = this.state;

    let page = paginationPre.current - 1;
    let size = paginationPre.pageSize;
    let number = headerData.id;

    this.setState({ preTableLoading: true }, () => {
      contractService
        .getPrepaymentHeadByContractId(number, page, size)
        .then(res => {
          let data = [];
          res.data.map(item =>
            item.line.map((i, index) =>
              data.push({
                ...item.head,
                ...i,
                id: item.head.id,
                lineNumber: index + 1 + page * size,
              })
            )
          );

          this.setState({
            prepaymentData: data,
            preTableLoading: false,
            paginationPre: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: page + 1,
              onShowSizeChange: this.onShowPrepaymentSizeChange,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: total => `共搜到 ${total} 条数据`,
            },
          });
        })
        .catch(e => {
          if (e && e.response) message.error(e.response.data.message);
        });
    });
  }
  onShowPrepaymentSizeChange = (current, pageSize) => {
    this.setState(
      {
        paginationPre: {
          current: current,
          pageSize: pageSize,
        },
      },
      () => {
        this.getPrepaymentHeadByContract();
      }
    );
  };
  tablePreChange = paginationPre => {
    this.setState({ paginationPre }, () => {
      this.getPrepaymentHeadByContract();
    });
  };

  //获取合同关联申请单
  getApplyHeadByContrcat() {
    let { headerData, applyPagination } = this.state;

    let page = applyPagination.current - 1;
    let size = applyPagination.pageSize;
    let id = headerData.id;

    contractService
      .getApplyHeadByContrcat(id, page, size)
      .then(res => {
        const data = [];
        res.data.map((item, index) =>
          data.push({
            ...item,
            lineNumber: index + 1 + page * size,
          })
        );

        this.setState({
          applicationData: data,
          applyPagination: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            current: page + 1,
            onShowSizeChange: this.onShowApplySizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共搜到 ${total} 条数据`,
          },
        });
      })
      .catch(e => {
        if (e && e.response) message.error(e.response.data.message);
      });
  }
  onShowApplySizeChange = (current, pageSize) => {
    this.setState(
      {
        loading: true,
        applyPagination: {
          current: current,
          pageSize: pageSize,
        },
      },
      () => {
        this.getApplyHeadByContrcat();
      }
    );
  };
  tableApplyChange = applyPagination => {
    this.setState({ applyPagination }, () => {
      this.getApplyHeadByContrcat();
    });
  };

  //获取合同关联的报账单
  getAccountHeadByContract() {
    let { headerData, paginationAccount } = this.state;

    let page = paginationAccount.current - 1;
    let size = paginationAccount.pageSize;
    let contractHeaderId = headerData.id;

    httpFetch
      .get(
        `${
          config.contractUrl
        }/api/contract/document/relations/associate/expReport/${contractHeaderId}?page=${page}&size=${size}`
      )
      .then(res => {
        /* let data = []; 
      res.data.map(item => {
        item.expensePaymentScheduleList.map(i => {
          data.push({ ...item.expenseReportHeader, ...i });
        });
      }); */

        this.setState({
          AccountData: res.data,
          paginationAccount: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            current: page + 1,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共搜到 ${total} 条数据`,
          },
        });
      });
  }
  onShowSizeChange = (current, pageSize) => {
    this.setState(
      {
        loading: true,
        paginationAccount: {
          current: current,
          pageSize: pageSize,
        },
      },
      () => {
        this.getAccountHeadByContract();
      }
    );
  };

  //获取支付明细数据payData
  getPayDetailByContractHeaderId() {
    let { headerData, page1, pageSize1 } = this.state;
    contractService.getPayDetailByContractHeaderId(headerData.id, page1, pageSize1).then(res => {
      this.setState({
        payData: res.data,
        pagination1: {
          total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
          current: page1 + 1,
          onChange: this.onChangePaper1,
        },
      });
    });
  }

  //支付明细页面切换
  onChangePaper1 = page1 => {
    if (page1 - 1 !== this.state.page1) {
      this.setState({ page1: page1 - 1 }, () => {
        this.getPayDetailByContractHeaderId();
      });
    }
  };
  //资金计划表格页码切换
  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        this.getPayList();
      });
    }
  };

  //合同信息内容渲染格式
  renderList = (title, value) => {
    return (
      <div className="list-info">
        <span>{title}：</span>
        <span className="content">{value}</span>
      </div>
    );
  };

  //侧滑
  showSlide = flag => {
    this.setState({ showSlideFrame: flag });
  };

  handleClosePlan = params => {
    this.setState(
      {
        payPlanVisible: false,
      },
      () => {
        if (params) {
          this.getInfo();
          this.table.reload();
        }
      }
    );
  };

  //关闭侧滑
  handleCloseSlide = params => {
    let btnVisible = this.state.btnVisible;
    params && (btnVisible = params);
    this.setState(
      {
        showSlideFrame: false,
        btnVisible,
      },
      () => {
        if (params) {
          this.getInfo();
        }
      }
    );
  };
  //添加资金计划行
  addItem = () => {
    this.setState({
      payPlanTitle: this.$t('acp.add.payment'),
      payPlanVisible: true,
      payInfo: {},
    });
  };

  handleCreateInfo = () => {
    this.setState({
      showSlideFrame: true,
      slideFrameTitle: this.$t('my.create.contract.info'),
    });
  };

  //编辑付款计划
  editItem = (e, record) => {
    e.preventDefault();
    this.setState({
      payPlanTitle: this.$t('acp.edit.payment'),
      payPlanVisible: true,
      payInfo: record,
    });
  };

  //删除资金计划行
  deleteItem = (e, record) => {
    e.preventDefault();
    this.setState({ planLoading: true });
    contractService
      .deletePayPlan(record.id)
      .then(() => {
        this.setState({ planLoading: false }, () => {
          this.table.reload();
          this.getInfo();
        });
        message.success(this.$t({ id: 'common.delete.success' }, { name: '' } /*{name} 删除成功*/));
      })
      .catch(e => {
        this.setState({ planLoading: false });
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  //撤回
  contractRecall = () => {
    if (this.state.headerData.formOid) {
      // 走工作流
      const { headerData } = this.state;
      let params = {
        entities: [
          {
            entityOid: headerData.documentOid,
            entityType: headerData.documentType,
          },
        ],
      };
      contractService
        .recallWorkflowContract(params)
        .then(res => {
          if (res.status === 200) {
            message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
            this.getInfo();
          }
        })
        .catch(e => {
          message.error(
            `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
          );
        });
    } else {
      // 不走工作流，走审核
      contractService
        .recallContract(this.props.id)
        .then(res => {
          if (res.status === 200) {
            message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
            this.getInfo();
          }
        })
        .catch(e => {
          message.error(
            `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
          );
        });
    }
  };

  //暂挂
  contractHold = () => {
    contractService
      .holdContract(this.props.id)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
          this.getInfo();
        }
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  //取消暂挂
  contractCancelHold = () => {
    contractService
      .unHoldContract(this.props.id)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
          this.getInfo();
        }
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  //取消
  contractCancel = () => {
    contractService
      .cancelContract(this.props.id)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
          this.getInfo();
        }
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  //完成
  contractFinish = () => {
    contractService
      .finishContract(this.props.id)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
          this.getInfo();
        }
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  handleContractEdit = e => {
    e.preventDefault();
    this.setState({
      showSlideFrame: true,
      slideFrameTitle: this.$t('my.edit.contract.info'),
    });
  };

  /*  //渲染关联的预付款单
  renderPrepayment = () => {
    const { prepaymentData } = this.state
    let renderPayment = [];
    if (prepaymentData.length > 0) {
      prepaymentData.map((item) => {
        renderPayment.push(<FormCard key={item.head.id} basicsData={{ item }} formType={"prePayment"} />)
      })
    }
    return renderPayment;
  };
 */
  //渲染支付明细
  renderPay = () => {
    const { payData } = this.state;
    let renderPay = [];
    if (payData.length > 0) {
      payData.map(item => {
        renderPay.push(<FormCard key={item.head.id} basicsData={{ item }} formType={'12'} />);
      });
    }
    return renderPay;
  };

  handleHeadEdit = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/contract-manager/my-contract/edit-contract/${this.state.headerData.id}/${
          this.state.headerData.contractTypeId
        }`,
      })
    );
  };

  //渲染关联的报账单
  renderAccount = () => {
    const { AccountData } = this.state;
    let renderAccount = [];
    if (AccountData.length > 0) {
      AccountData.map(item => {
        renderAccount.push(
          <FormCard key={item.expenseReportHeader.id} basicsData={{ item }} formType={'account'} />
        );
      });
    }
    return renderAccount;
  };

  //切换tab
  tabChange = key => {
    this.setState({ tabIndex: key }, () => {
      if ((key = 'link')) {
        if (this.state.headerData.status >= 1004) {
          this.getAccountHeadByContract();
          this.getPrepaymentHeadByContract();
        }
      }
    });
  };

  renderContent() {
    const {
      payInfo,
      payPlanVisible,
      payPlanTitle,
      detailLoading,
      planLoading,
      documentParams,
      columns,
      showSlideFrame,
      headerData,
      slideFrameTitle,
    } = this.state;
    let flag =
      headerData.status !== 1001 && headerData.status !== 1003 && headerData.status !== 1005;

    let visible =
      headerData.status === 6001 ||
      headerData.status === 6003 ||
      headerData.status === 1002 ||
      headerData.status === 6002;
    return (
      <div>
        <Card
          style={{
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            marginRight: 15,
            marginLeft: 15,
            marginTop: 20,
          }}
        >
          <div style={flag ? { paddingTop: 0, marginTop: '-20px' } : { marginTop: '-20px' }}>
            <DocumentBasicInfo params={documentParams} noHeader={true}>
              {headerData.status &&
                (headerData.status == 1001 ||
                  headerData.status == 1003 ||
                  headerData.status == 1005) && (
                  <Button
                    onClick={this.handleHeadEdit}
                    type="primary"
                    style={{ float: 'right', top: -4 }}
                  >
                    {' '}
                    {this.$t('my.contract.edit')}
                  </Button>
                )}
              {headerData.status &&
                headerData.status === 1002 && (
                  <Button
                    type="primary"
                    onClick={this.contractRecall}
                    style={{ float: 'right', top: -4 }}
                  >
                    {this.$t('common.withdraw')}
                  </Button>
                )}

              {headerData.status &&
                headerData.status === 1004 && (
                  <span>
                    <Button
                      size="small"
                      type="primary"
                      onClick={this.contractFinish}
                      style={{ float: 'right', top: -4 }}
                    >
                      {this.$t('my.contract.state.finish')}
                    </Button>
                    <Button
                      size="small"
                      type="primary"
                      onClick={this.contractCancel}
                      style={{ marginRight: 5, float: 'right', top: -4 }}
                    >
                      {this.$t('common.cancel')}
                    </Button>
                    <Button
                      size="small"
                      type="primary"
                      onClick={this.contractHold}
                      style={{ marginRight: 5, float: 'right', top: -4 }}
                    >
                      {this.$t('my.contract.state.hold')}
                    </Button>
                  </span>
                )}
              {headerData.status &&
              headerData.status === 6001 && ( //暂挂
                  <Button
                    type="primary"
                    onClick={this.contractCancelHold}
                    style={{ float: 'right', top: -4 }}
                  >
                    {this.$t('my.contract.cancel.hold')}
                  </Button>
                )}
            </DocumentBasicInfo>
          </div>
        </Card>
        <Spin spinning={detailLoading}>
          <div className="contract-info" style={{ margin: 0 }}>
            <Card
              style={{
                boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
                marginRight: 15,
                marginLeft: 15,
                marginTop: 20,
              }}
            >
              <div
                className="contract-info-header"
                style={{ borderBottom: '1px solid rgb(236, 236, 236)', marginTop: '-20px' }}
              >
                <h3 style={{ display: 'inline', marginLeft: '8px', fontSize: '18px' }}>
                  {this.$t('my.contract.info')}
                </h3>
                <a
                  style={{
                    marginLeft: 15,
                    display:
                      headerData.status === 1004 ||
                      headerData.status === 6001 ||
                      headerData.status === 6003 ||
                      headerData.status === 1002 ||
                      headerData.status === 6002
                        ? 'none'
                        : !headerData.contractName
                          ? 'none'
                          : 'inline-block',
                  }}
                  onClick={this.handleContractEdit}
                >
                  {this.$t('common.edit')}
                </a>
              </div>
              <div style={{ marginTop: 5 }}>
                <Button
                  type="primary"
                  style={{
                    marginLeft: 20,
                    display: visible ? 'none' : !headerData.contractName ? 'inline-block' : 'none',
                  }}
                  onClick={this.handleCreateInfo}
                >
                  {this.$t('my.create.contract.info')}
                </Button>
              </div>
              <div style={{ marginLeft: 15 }}>
                <Row gutter={24} className="info-items">
                  <Col span={2} className="label-tips">
                    {this.$t('common.baseInfo')}:
                  </Col>

                  <Col span={2} offset={1} className="item-label">
                    {this.$t('my.contract.contractCompany')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span title={headerData.companyName}>{headerData.companyName}</span>
                  </Col>

                  <Col span={2} className="item-label">
                    {this.$t('acp.contract.name')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span title={headerData.contractName}>{headerData.contractName}</span>
                  </Col>

                  <Col span={2} className="item-label">
                    {this.$t('my.contract.signDate')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span
                      title={
                        headerData.contractName
                          ? headerData.signDate
                            ? moment(new Date(headerData.signDate)).format('YYYY-MM-DD')
                            : '-'
                          : ''
                      }
                    >
                      {headerData.contractName
                        ? headerData.signDate
                          ? moment(new Date(headerData.signDate)).format('YYYY-MM-DD')
                          : '-'
                        : ''}
                    </span>
                  </Col>
                </Row>
                <Row gutter={25} className="info-items">
                  <Col span={2} className="label-tips">
                    {this.$t('my.contract.party.info')}:
                  </Col>

                  <Col span={2} offset={1} className="item-label">
                    {this.$t('my.contract.partner.category')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span title={headerData.partnerCategoryName}>
                      {headerData.partnerCategoryName}
                    </span>
                  </Col>
                  <Col span={2} className="item-label">
                    {this.$t('my.contract.partner')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span title={headerData.partnerName}>{headerData.partnerName}</span>
                  </Col>
                </Row>
                <Row gutter={24} className="info-items">
                  <Col span={2} className="label-tips">
                    {this.$t('supplier.management.otherInfo')}:
                  </Col>

                  <Col span={2} offset={1} className="item-label">
                    {this.$t('my.contract.responsible.department')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span title={headerData.unitName}>
                      {headerData.contractName
                        ? headerData.unitName
                          ? headerData.unitName
                          : '-'
                        : ''}
                    </span>
                  </Col>

                  <Col span={2} className="item-label">
                    {this.$t('my.contract.responsible.person')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span title={headerData.employeeId}>
                      {headerData.contractName
                        ? headerData.employee && headerData.employeeId
                          ? headerData.employee.fullName
                          : '-'
                        : ''}
                    </span>
                  </Col>
                  <Col span={2} className="item-label">
                    {this.$t('budget.controlRule.effectiveDate')}:
                  </Col>
                  <Col span={5} className="item-value">
                    <span
                      title={
                        headerData.contractName
                          ? (headerData.startDate
                              ? moment(new Date(headerData.startDate)).format('YYYY-MM-DD')
                              : '-') +
                            ' ~ ' +
                            (headerData.endDate
                              ? moment(new Date(headerData.endDate)).format('YYYY-MM-DD')
                              : '-')
                          : ''
                      }
                    >
                      {headerData.contractName
                        ? (headerData.startDate
                            ? moment(new Date(headerData.startDate)).format('YYYY-MM-DD')
                            : '-') +
                          ' ~ ' +
                          (headerData.endDate
                            ? moment(new Date(headerData.endDate)).format('YYYY-MM-DD')
                            : '-')
                        : ''}
                    </span>
                  </Col>
                </Row>
              </div>
            </Card>
          </div>
        </Spin>
        <Spin spinning={planLoading}>
          <Card
            style={{
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
              marginRight: 15,
              marginLeft: 15,
              marginTop: 10,
            }}
          >
            <div className="pay-info" style={{ marginTop: '0px' }}>
              <h3
                className="info-header-title"
                style={{
                  borderBottom: '1px solid #ececec',
                  fontSize: 18,
                  margin: '-20px -20px 20px -8px',
                }}
              >
                {this.$t('acp.payment.info')}
              </h3>
              <Row
                gutter={24}
                className="pay-info-header"
                style={
                  headerData.status === 6001 ||
                  headerData.status === 6003 ||
                  headerData.status === 6002 ||
                  headerData.status === 1002
                    ? { marginTop: -20 }
                    : {}
                }
              >
                <Col span={12}>
                  <Button
                    style={{
                      display:
                        headerData.status === 1004 ||
                        headerData.status === 6001 ||
                        headerData.status === 6003 ||
                        headerData.status === 6002 ||
                        headerData.status === 1002
                          ? 'none'
                          : 'inline',
                    }}
                    className="header-btn"
                    onClick={this.addItem}
                    type="primary"
                  >
                    {this.$t('acp.new.payment')}
                  </Button>
                </Col>
                <Col span={12} className="header-tips" style={{ textAlign: 'right' }}>
                  <Breadcrumb style={{ marginBottom: '10px' }}>
                    <Breadcrumb.Item>
                      <span style={{ color: 'rgba(0, 0, 0, 0.60)' }}>
                        {this.$t('common.amount')}:
                      </span>&nbsp;<span style={{ color: 'Green' }}>
                        {' '}
                        {headerData.currency}&nbsp;{this.filterMoney(headerData.amount)}
                      </span>
                    </Breadcrumb.Item>
                    <Breadcrumb.Item>
                      <span style={{ color: 'rgba(0, 0, 0, 0.60)' }}>
                        {this.$t('acp.function.amount')}
                      </span>
                      <span style={{ color: 'Green' }}>
                        {this.props.company.baseCurrency}&nbsp;{this.filterMoney(
                          headerData.functionAmount
                        )}
                      </span>
                    </Breadcrumb.Item>
                  </Breadcrumb>
                </Col>
              </Row>
              <CustomTable
                ref={ref => (this.table = ref)}
                url={`${config.contractUrl}/api/contract/line/herder/${this.props.id}`}
                showNumber={true}
                pagination={{ pageSize: 5 }}
                columns={columns}
              />
            </div>
          </Card>
        </Spin>
        <div>
          <Card
            style={{
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
              marginRight: 15,
              marginLeft: 15,
              marginTop: 20,
              marginBottom: 50,
            }}
          >
            {/* <ApproveHistory loading={false} infoData={approveHistory} /> */}
            <ApproveHistory type="801004" oid={headerData.documentOid} />
          </Card>
        </div>
        <SlideFrame
          title={slideFrameTitle}
          show={showSlideFrame}
          onClose={() => this.showSlide(false)}
        >
          <NewContractInfo
            onClose={this.handleCloseSlide}
            params={{
              contractHead: headerData,
              flag: showSlideFrame,
            }}
          />
        </SlideFrame>

        <SlideFrame
          title={payPlanTitle}
          show={payPlanVisible}
          onClose={() => this.setState({ payPlanVisible: false })}
        >
          <NewPayPlan
            onClose={this.handleClosePlan}
            params={{
              contractHead: headerData,
              payInfo: payInfo,
              flag: payPlanVisible,
            }}
          />
        </SlideFrame>
      </div>
    );
  }

  tableChange = paginationAccount => {
    this.setState({ paginationAccount }, () => {
      this.getAccountHeadByContract();
    });
  };

  // 单据编号查看详情
  skipToDocumentDetail = (record, view = 1) => {
    this.setState({
      detailsId: view === 3 ? record.expReportHeaderId : record.id,
      view,
      detailsTitle:
        view === 1
          ? '预付款单详情'
          : view === 2
            ? '费用申请单详情'
            : view === 4
              ? '项目申请单详情'
              : '报账单详情',
      detailsShow: true,
    });
  };

  // 渲染关联弹窗
  renderDetailsModal = () => {
    const { detailsTitle, detailsShow, detailsId, view } = this.state;
    let params = { id: detailsId };
    // view === 1, 预付款单，view === 2 申请单， view === 3 报账单
    return (
      // {/* 关联详情 */}
      <Modal
        title={detailsTitle}
        visible={detailsShow}
        onCancel={() => {
          this.setState({ detailsShow: false });
        }}
        width="90%"
        bodyStyle={{
          maxHeight: '70vh',
          overflow: 'auto',
          padding: '0 10px',
        }}
        footer={null}
      >
        {Number(view) === 1 ? (
          <PrePaymentCommon id={detailsId} />
        ) : Number(view) === 2 ? (
          <ExpenseApplication id={detailsId} />
        ) : Number(view) === 4 ? (
          <ProjectApplication id={detailsId} />
        ) : (
          <ExpenseReportDetail params={params} />
        )}
      </Modal>
    );
  };

  render() {
    const {
      accountColumns,
      payColumns,
      paginationAccount,
      paginationPre,
      projectColumns,
      ProjectData,
      paginationProject,
      preColumns,
      headerData,
      prepaymentData,
      applicationColumns,
      applicationData,
      applyPagination,
      AccountData,
    } = this.state;

    return (
      <div>
        {headerData.status === 1001 || headerData.status === 1003 || headerData.status === 1005 ? (
          this.renderContent()
        ) : (
          <Tabs defaultActiveKey="detail" onChange={this.tabChange}>
            <TabPane style={{ marginLeft: 12 }} tab={this.$t('menu.contract-detail')} key="detail">
              {this.renderContent()}
            </TabPane>
            <TabPane tab={this.$t('my.link.info')} key="link">
              <div style={{ background: 'white', margin: '20px 0 50px 0px', padding: 0 }}>
                <Card
                  style={{
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
                    marginRight: 15,
                    marginLeft: 15,
                  }}
                >
                  <h3 style={{ fontSize: 18, borderBottom: '1px solid #ececec' }}>
                    {this.$t('my.link.pre')}
                  </h3>
                  <Table
                    rowKey={record => record.id}
                    columns={preColumns}
                    dataSource={prepaymentData}
                    pagination={paginationPre}
                    bordered
                    size="middle"
                    onChange={this.tablePreChange}
                    loading={this.state.preTableLoading}
                  />
                </Card>
              </div>
              <div style={{ background: 'white', margin: '20px 0 50px 0px', padding: 0 }}>
                <Card
                  style={{
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
                    marginRight: 15,
                    marginLeft: 15,
                  }}
                >
                  <h3 style={{ fontSize: 18, borderBottom: '1px solid #ececec' }}>
                    {this.$t('my.link.application')}
                  </h3>
                  <Table
                    rowKey={record => record.id}
                    columns={applicationColumns}
                    dataSource={applicationData}
                    pagination={applyPagination}
                    bordered
                    size="middle"
                    onChange={this.tableApplyChange}
                  />
                </Card>
              </div>
              <div style={{ background: 'white', margin: '-50px 0 50px 0px', padding: 0 }}>
                <Card
                  style={{
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
                    marginRight: 15,
                    marginLeft: 15,
                  }}
                >
                  <h3 style={{ fontSize: 18, borderBottom: '1px solid #ececec' }}>
                    {this.$t('my.link.rem')}
                  </h3>
                  <Table
                    rowKey={record => record.id}
                    columns={accountColumns}
                    dataSource={AccountData}
                    pagination={paginationAccount}
                    bordered
                    size="middle"
                    onChange={this.tableChange}
                  />
                </Card>
              </div>

              <div style={{ background: 'white', margin: '-50px 0 50px 0px', padding: 0 }}>
                <Card
                  style={{
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
                    marginRight: 15,
                    marginLeft: 15,
                  }}
                >
                  <h3 style={{ fontSize: 18, borderBottom: '1px solid #ececec' }}>
                    关联项目申请单
                  </h3>
                  <CustomTable
                    ref={ref => (this.projectDetail = ref)}
                    url={`${config.contractUrl}/api/project/requisition/by/contract/id`}
                    params={{ contractHeaderId: headerData.id }}
                    pagination={{ pageSize: 5 }}
                    columns={projectColumns}
                  />
                </Card>
              </div>

              <div style={{ background: 'white', margin: '-50px 0 50px 0px', padding: 0 }}>
                <Card
                  style={{
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
                    marginRight: 15,
                    marginLeft: 15,
                  }}
                >
                  <h3 style={{ fontSize: 18, borderBottom: '1px solid #ececec' }}>
                    {this.$t('my.pay.info')}
                  </h3>
                  <CustomTable
                    ref={ref => (this.payDetail = ref)}
                    url={`${
                      config.payUrl
                    }/api/cash/transaction/details/getDetailByContractHeaderId`}
                    params={{ contractHeaderId: headerData.id }}
                    pagination={{ pageSize: 5 }}
                    columns={payColumns}
                  />
                </Card>
              </div>
            </TabPane>
          </Tabs>
        )}
        {this.renderDetailsModal()}
      </div>
    );
  }
}

ContractDetailCommon.propTypes = {
  id: PropTypes.any.isRequired, //显示数据
  isApprovePage: PropTypes.bool, //是否在审批页面
  getContractStatus: PropTypes.func, //确认合同信息状态
};

ContractDetailCommon.defaultProps = {
  isApprovePage: false,
  getContractStatus: () => {},
};

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}
const wrappedContractDetailCommon = Form.create()(ContractDetailCommon);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedContractDetailCommon);
