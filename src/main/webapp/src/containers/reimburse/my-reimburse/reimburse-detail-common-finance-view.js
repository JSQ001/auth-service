import React from 'react';
import { connect } from 'dva';
import PropTypes from 'prop-types';
import {
  Form,
  Tabs,
  Button,
  Menu,
  Radio,
  Dropdown,
  Row,
  Col,
  Spin,
  Timeline,
  message,
  Popover,
  Popconfirm,
  Icon,
  Select,
  Card,
} from 'antd';
const TabPane = Tabs.TabPane;

const RadioGroup = Radio.Group;
import moment from 'moment';
import SlideFrame from 'widget/slide-frame';
import 'styles/reimburse/reimburse-common.scss';
import 'styles/contract/my-contract/contract-detail.scss';
import CostDetail from 'containers/reimburse/my-reimburse/cost-detail-readOnly';
import PayInfo from 'containers/reimburse/my-reimburse/pay-info-readOnly';
import NewExpense from 'containers/reimburse/my-reimburse/new-expense';
import DetailExpense from 'containers/reimburse/my-reimburse/expense-detail';
import NewPayPlan from 'containers/reimburse/my-reimburse/new-pay-plan';
import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import ListSelector from 'components/Widget/list-selector';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import Invoice from 'containers/my-wallet/invoice';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import VoucherInfo from 'containers/reimburse/my-reimburse/voucher-info';
import { routerRedux } from 'dva/router';

class ContractDetailCommon extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
      planLoading: false,
      topTapValue: 'contractInfo',
      headerData: {},
      dimensionsColumns: [],
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
        2004: { label: '支付成功', state: 'success' },
        2003: { label: '支付中', state: 'processing' },
        2002: { label: '审核通过', state: 'success' },
      },
      subTabsList: [{ label: this.$t({ id: 'my.contract.detail' } /*详情*/), key: 'DETAIL' }],
      infoList: {
        title: '报账单',
        headItems: [
          { label: '单据编号', key: 'expAdjustHeaderNumber' },
          {
            label: '申请日期',
            key: 'adjustDate',
            render: item => moment(new Date(item)).format('YYYY-MM-DD'),
          },
          { label: '创建人', key: 'employeeName' },
        ],
        items: [
          { label: '申请人', key: 'employeeName' },
          { label: '公司', key: 'companyName' },
          { label: '部门', key: 'unitName' },
          { label: '调整类型', key: 'expAdjustTypeName' },
          { label: '备注', key: 'description' },
          { label: '附件信息', key: '6', isInline: true },
        ],
      },
      columns: [
        {
          title: this.$t({ id: 'common.sequence' } /*序号*/),
          dataIndex: 'index',
          render: (value, record, index) => this.state.pageSize * this.state.page + index + 1,
        },
        { title: this.$t({ id: 'my.contract.currency' } /*币种*/), dataIndex: 'currency' },
        {
          title: this.$t({ id: 'my.contract.plan.amount' } /*计划金额*/),
          dataIndex: 'amount',
          render: this.filterMoney,
        },
        {
          title: this.$t({ id: 'my.contract.partner.category' } /*合同方类型*/),
          dataIndex: 'partnerCategoryName',
        },
        { title: this.$t({ id: 'my.contract.partner' } /*合同方*/), dataIndex: 'partnerName' },
        {
          title: this.$t({ id: 'my.contract.plan.pay.date' } /*计划付款日期*/),
          dataIndex: 'dueDate',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t({ id: 'common.remark' } /*备注*/),
          dataIndex: 'remark',
          render: value => {
            return value ? (
              <Popover placement="topLeft" content={value} overlayStyle={{ maxWidth: 300 }}>
                {value}
              </Popover>
            ) : (
              '-'
            );
          },
        },
      ],
      data: [],
      isCopy: false,
      page: 0,
      pageSize: 10,
      pagination: {
        total: 0,
      },
      visible: false,
      writeoffShow: false,
      payPlanVisible: false,
      showSlideFrame: false,
      isLoadPayData: false,
      isLoadCostData: false,
      slideFrameTitle: '',
      costRecord: {},
      payRecord: {},
      record: {}, //资金计划行信息
      historyData: [], //历史信息
      defaultApportion: {},
      // editReimburePage: menuRoute.getRouteItem('edit-reimburse', 'key'),
      // myReimburse: menuRoute.getRouteItem('my-reimburse', 'key'),    //我的报账单
      flag: true,
      showInvoices: false,
      invoicesLoading: false,
      approveHistory: [],
      historyLoading: false,
      detailVisible: false,
      showInvoiceDetail: false,
      invoiceData: {},
      remburseInfo: {},
      tabIndex: '1',
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.headerData != this.props.headerData) {
      this.setDocumentInfo(nextProps);
    }

    if (nextProps.headerData.expenseReportOid && !this.props.headerData.expenseReportOid) {
      this.setState({ historyLoading: true });
      reimburseService
        .getReportsHistory(nextProps.headerData.expenseReportOid)
        .then(res => {
          this.setState({ approveHistory: res.data, historyLoading: false });
        })
        .catch(err => {
          message.error('获取审批历史失败！');
        });
    }
  }

  setDocumentInfo = nextProps => {
    let { headerData } = nextProps;
    let list = [];
    headerData.customFormValues &&
      headerData.customFormValues.map(o => {
        if (
          o.messageKey != 'select_company' &&
          o.messageKey != 'select_department' &&
          o.messageKey != 'remark' &&
          o.messageKey != 'currency_code'
        ) {
          list.push({ label: o.fieldName, value: o.showValue });
        }
      });
    this.setState({
      remburseInfo: {
        businessCode: headerData.requisitionNumber,
        createdDate: headerData.reportDate,
        formName: headerData.documentTypeName,
        createByName: `${headerData.applicantName}-${headerData.applicantCode}`,
        totalAmount: headerData.totalAmount,
        statusCode: headerData.status,
        currencyCode: headerData.currencyCode,
        remark: headerData.description,
        infoList: [
          { label: '申请人', value: `${headerData.applicantName}-${headerData.applicantCode}` },
          { label: '公司', value: headerData.companyName },
          { label: '部门', value: headerData.departmentName },
          headerData.contractHeaderId
            ? {
                label: '合同',
                value: headerData.contractNumber,
                linkId: headerData.contractHeaderId,
              }
            : null,
        ],
        // customList: list
        customList: headerData.expenseDimensions
          ? headerData.expenseDimensions
              .filter(o => o.headerFlag)
              .map(o => ({ label: o.name, value: o.valueName }))
          : [],
      },
    });
  };

  //获取付款列表
  getPayList = flag => {
    this.setState({ payPlanVisible: false }, () => {
      if (flag) {
        this.setState({ isLoadPayData: !this.state.isLoadPayData });
        this.props.getInfo && this.props.getInfo();
      }
    });
  };

  //获取费用列表
  getCostList = flag => {
    this.setState({ visible: false, detailVisible: false }, () => {
      if (flag) {
        this.setState({ isLoadCostData: !this.state.isLoadCostData });
        //this.getPayList(true);
        //this.props.getInfo && this.props.getInfo();
      }
    });
  };

  //刷新数据
  refresh = () => {
    this.setState({ isLoadCostData: !this.state.isLoadCostData });
    this.getPayList(true);
    this.props.getInfo && this.props.getInfo();
  };

  renderList = (title, value) => {
    return (
      <Row
        style={{ fontSize: '12px', lineHeight: '32px', overflow: 'hidden' }}
        className="list-info"
      >
        <Col span={8}>
          <Row>
            <Col
              style={{
                textAlign: 'right',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              span={20}
            >
              <span title={title}>{title}</span>
            </Col>
            <Col span={4} style={{ textAlign: 'center' }}>
              :
            </Col>
          </Row>
        </Col>
        <Col
          style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}
          span={16}
        >
          <span title={title} className="content">
            {value}
          </span>
        </Col>
      </Row>
    );
  };

  //取消
  contractCancel = () => {
    contractService
      .cancelContract(this.props.id)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t({ id: 'common.operate.success' } /*操作成功*/));
        }
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'common.operate.filed' } /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  //新建费用按钮事件
  createCost = () => {
    this.setState({ visible: true, costRecord: {}, isCopy: false });
  };

  //新建付款行
  addPayPlan = () => {
    this.setState({ payPlanVisible: true, payRecord: {} });
  };

  //编辑付款行
  payEdit = record => {
    this.setState({ payPlanVisible: true, payRecord: record });
  };

  //编辑费用行
  costEdit = record => {
    this.setState({ visible: true, costRecord: record, isCopy: false });
  };

  //费用行详情
  costDetail = record => {
    this.setState({ detailVisible: true, costRecord: record });
  };

  //编辑报账单
  edit = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/edit-reimburse/${this.props.headerData.id}/${
          this.props.headerData.documentTypeId
        }`,
      })
    );
  };

  //删除费用行
  deleteCost = record => {
    reimburseService
      .deleteCostDetail(record.id)
      .then(res => {
        message.success('删除成功！');
        this.getCostList(true);
      })
      .catch(err => {
        message.error('删除失败！');
      });
  };

  //复制费用行
  costCopy = record => {
    this.setState({ visible: true, costRecord: record, isCopy: true });
  };

  //删除付款行
  deletePay = record => {
    reimburseService
      .deletePayDetail(record.id)
      .then(res => {
        message.success('删除成功！');
        this.getPayList(true);
      })
      .catch(err => {
        message.error('删除失败！');
      });
  };

  //跳转到合同详情
  contractDetail = () => {
    let url = menuRoute.getRouteItem('contract-detail', 'key');
    window.open(
      url.url.replace(':id', this.props.headerData.contractHeaderId).replace(':from', 'reimburse'),
      '_blank'
    );
  };

  //选取报账单后
  handleListOk = values => {
    const { headerData } = this.props;
    if (values.result && values.result.length) {
      this.setState({ invoicesLoading: true });

      let data = values.result.map(item => item.id);
      reimburseService
        .import(headerData.id, data)
        .then(res => {
          message.success('导入费用成功！');
          this.setState({ showInvoices: false, invoicesLoading: false });
          this.getCostList(true);
          this.refresh();
        })
        .catch(e => {
          message.error(`导入失败！${e.response && e.response.data.message}`);
        });
    } else {
      message.warn('请选择费用');
    }
  };

  //撤回
  withdraw = () => {
    let params = {
      entities: [
        {
          entityOid: this.props.headerData.documentOid,
          entityType: 801001,
        },
      ],
    };
    reimburseService
      .withdraw(params)
      .then(res => {
        message.success('撤回成功！');
        this.onCancel();
      })
      .catch(err => {
        message.error('撤回失败：' + err.response.data.message);
      });
  };

  //取消
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/my-reimburse`,
      })
    );
  };

  //显示发票
  showInvoiceDetail = record => {
    this.setState({ showInvoiceDetail: true, invoiceData: record });
  };

  //切换tab
  tabChange = value => {
    this.setState({ tabIndex: value });
  };

  render() {
    const { headerData } = this.props;
    const {
      detailLoading,
      showInvoiceDetail,
      invoiceData,
      remburseInfo,
      showInvoices,
      isLoadCostData,
      isLoadPayData,
      writeoffShow,
      visible,
      planLoading,
      historyLoading,
      contractEdit,
      topTapValue,
      subTabsList,
      pagination,
      columns,
      data,
      showSlideFrame,
      contractStatus,
      record,
      slideFrameTitle,
      historyData,
      detailVisible,
    } = this.state;

    let isEdit = [1001, 1003, 1005].includes(headerData.status);

    let subContent = {};
    subContent.DETAIL = (
      <div>
        <Card
          style={{ marginTop: 20, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}
          title="费用信息"
        >
          <div className="table-header" style={{ marginTop: '0px' }}>
            <div style={{ float: 'right' }}>
              <span>
                金额总计：<span style={{ color: 'green' }}>
                  {this.props.headerData.currencyCode}{' '}
                  {this.filterMoney(this.props.headerData.totalAmount)}
                </span>
              </span>
            </div>
          </div>
          <CostDetail
            showInvoiceDetail={this.showInvoiceDetail}
            costDetail={this.costDetail}
            disabled={isEdit === false}
            deleteCost={this.deleteCost}
            costCopy={this.costCopy}
            costEdit={this.costEdit}
            flag={isLoadCostData}
            headerData={this.props.headerData}
          />
        </Card>
        <PayInfo
          ref="payInfo"
          flag={isLoadPayData}
          headerData={this.props.headerData}
          deletePay={this.deletePay}
          addPayPlan={this.addPayPlan}
          payEdit={this.payEdit}
          summaryView={headerData.summaryView || {}}
          writeOffOk={this.getPayList}
          disabled={isEdit === false}
        />
        <div style={{ marginTop: 20, marginBottom: 0, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}>
          <ApproveHistory type="801001" oid={headerData.documentOid} />
        </div>
      </div>
    );
    return (
      <div style={{ paddingBottom: '20px' }}>
        <Spin spinning={false}>
          <Card style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)' }}>
            <Tabs forceRender defaultActiveKey="1" onChange={this.tabChange}>
              <TabPane tab="单据信息" key="1" style={{ border: 'none' }}>
                <DocumentBasicInfo params={remburseInfo} />
              </TabPane>
              {this.props.headerData.reportStatus === 1006 && (
                <TabPane tab="凭证信息" key="2" style={{ border: 'none' }}>
                  <VoucherInfo voucherParams={this.props.headerData} />
                </TabPane>
              )}
            </Tabs>
          </Card>
          <div style={{ display: this.state.tabIndex == '1' ? 'block' : 'none' }}>
            {subContent['DETAIL']}
          </div>
        </Spin>

        <SlideFrame
          title={slideFrameTitle}
          show={showSlideFrame}
          onClose={() => this.showSlide(false)}
        >
          <NewPayPlan
            close={this.handleCloseSlide}
            params={{
              id: this.props.id,
              currency: headerData.currency,
              partnerCategory: headerData.partnerCategory,
              companyId: headerData.companyId,
              partnerId: headerData.partnerId,
              partnerName: headerData.partnerName,
              record,
            }}
          />
        </SlideFrame>

        <SlideFrame
          show={detailVisible}
          title="费用详情"
          width="800px"
          afterClose={() => this.setState({ detailVisible: false })}
          onClose={() => this.setState({ detailVisible: false })}
        >
          <DetailExpense
            close={this.getCostList}
            params={{
              visible: this.state.detailVisible,
              record: this.state.costRecord,
              headerId: this.props.id,
              headerData: this.props.headerData,
              defaultApportion: this.state.defaultApportion,
              isCopy: this.state.isCopy,
              refresh: this.refresh,
            }}
          />
        </SlideFrame>
        <ListSelector
          single={false}
          visible={showInvoices}
          type="select_invoices"
          onCancel={() => {
            this.setState({ showInvoices: false });
          }}
          onOk={this.handleListOk}
          extraParams={{
            expenseReportTypeId: headerData.documentTypeId,
            currencyCode: headerData.currencyCode,
            //expenseTypeId:
          }}
        />
        <Invoice
          cancel={() => {
            this.setState({ showInvoiceDetail: false });
          }}
          id={invoiceData.id}
          visible={showInvoiceDetail}
          validate
        />
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

const wrappedContractDetailCommon = Form.create()(ContractDetailCommon);
function mapStateToProps(state) {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedContractDetailCommon);