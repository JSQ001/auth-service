import React from 'react';
import PropTypes from 'prop-types';
import { Form, Tabs, Spin, Card } from 'antd';
import SlideFrame from 'widget/slide-frame';
import CostDetail from 'containers/reimburse/my-reimburse/cost-detail';
import PayInfo from 'containers/reimburse/my-reimburse/pay-info';
import DetailExpense from 'containers/reimburse/my-reimburse/expense-detail';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import Invoice from 'containers/reimburse/my-reimburse/invoice';
import DocumentBasicInfo from 'widget/Template/document-basic-info';
import VoucherInfo from 'containers/reimburse/my-reimburse/voucher-info';
import 'styles/reimburse/reimburse-common.scss';
import 'styles/contract/my-contract/contract-detail.scss';

const TabPane = Tabs.TabPane;

class ContractDetailCommon extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      writeoffShow: false,
      payPlanVisible: false,
      isLoadPayData: false,
      isLoadCostData: false,
      costRecord: {},
      defaultApportion: {},
      detailVisible: false,
      showInvoiceDetail: false,
      invoiceData: {},
      reimburseInfo: {},
      tabIndex: '1',
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.headerData != this.props.headerData) {
      this.setDocumentInfo(nextProps);
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
      reimburseInfo: {
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
        customList: list,
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
      }
    });
  };

  //刷新数据
  refresh = () => {
    this.setState({ isLoadCostData: !this.state.isLoadCostData });
    this.getPayList(true);
    this.props.getInfo && this.props.getInfo();
  };

  //费用行详情
  costDetail = record => {
    this.setState({ detailVisible: true, costRecord: record });
  };

  //显示发票
  showInvoiceDetail = record => {
    this.setState({ showInvoiceDetail: true, invoiceData: record.digitalInvoice });
  };

  //切换tab
  tabChange = value => {
    this.setState({ tabIndex: value });
  };

  render() {
    const { headerData } = this.props;
    const { reimburseInfo, isLoadCostData, isLoadPayData, detailVisible } = this.state;

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
            disabled={true}
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
          disabled={true}
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
                <DocumentBasicInfo params={reimburseInfo}>{this.props.children}</DocumentBasicInfo>
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

        <Invoice
          cancel={() => {
            this.setState({ showInvoiceDetail: false });
          }}
          invoice={this.state.invoiceData || {}}
          visible={this.state.showInvoiceDetail}
        />
      </div>
    );
  }
}

ContractDetailCommon.propTypes = {
  id: PropTypes.any.isRequired, //显示数据
};

const wrappedContractDetailCommon = Form.create()(ContractDetailCommon);

export default wrappedContractDetailCommon;
