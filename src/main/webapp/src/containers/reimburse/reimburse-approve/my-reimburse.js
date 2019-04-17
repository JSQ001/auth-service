import React from 'react';
import { connect } from 'dva';
import { Form, Tabs, Badge, Popover, Row, Col, Input } from 'antd';
const TabPane = Tabs.TabPane;
import config from 'config';

import SearchArea from 'widget/search-area';
import moment from 'moment';
import { routerRedux } from 'dva/router';

import CustomTable from 'widget/custom-table';
const Search = Input.Search;

class Reimburse extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabValue: 'unapproved',
      loading1: false,
      loading2: false,
      status: {
        1001: { label: '编辑中', state: 'default' },
        1004: { label: '审批通过', state: 'success' },
        1002: { label: '审批中', state: 'processing' },
        1005: { label: '审批驳回', state: 'error' },
        1003: { label: '撤回', state: 'warning' },
        2002: { label: '审核通过', state: 'success' },
        2004: { label: '支付成功', state: 'success' },
        2003: { label: '支付中', state: 'processing' },
      },
      searchForm: [
        {
          type: 'list',
          listType: 'select_report_type',
          options: [],
          id: 'documentTypeId',
          label: '单据类型',
          valueKey: 'id',
          labelKey: 'reportTypeName',
          single: true,
          colSpan: 6,
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
        },
        {
          type: 'list',
          listType: 'select_user',
          options: [],
          id: 'applicantOid',
          label: this.$t({ id: 'common.applicant' }),
          labelKey: 'fullName',
          valueKey: 'userOid',
          colSpan: 6,
          single: true,
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          event: 'createdBy',
        },
        {
          type: 'items',
          id: 'dateRange',
          colSpan: 6,
          items: [
            {
              type: 'date',
              id: 'beginDate',
              label: '提交日期从',
            },
            {
              type: 'date',
              id: 'endDate',
              label: '提交日期至',
            },
          ],
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            { type: 'input', id: 'amountFrom', label: this.$t('exp.money.from') },
            { type: 'input', id: 'amountTo', label: this.$t('exp.money.to') },
          ],
        },
        {
          type: 'select',
          key: 'currency',
          id: 'currencyCode',
          label: this.$t('common.currency'),
          getUrl: `${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`,
          options: [],
          method: 'get',
          labelKey: '${currency}-${currencyName}',
          valueKey: 'currency',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'description',
          label: this.$t('common.comment'),
          colSpan: 6,
        },
      ],
      unApproveSearchParams: {},
      approveSearchParams: {},
      columns: [
        { title: '单据编号', dataIndex: 'documentNumber', width: 180, align: 'center' },
        { title: '单据类型', dataIndex: 'documentTypeName', align: 'center' },
        { title: '申请人', dataIndex: 'applicantName', width: 100, align: 'center' },
        {
          title: '提交日期',
          dataIndex: 'submittedDate',
          width: 120,
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        { title: '币种', dataIndex: 'currencyCode', width: 80, align: 'center' },
        { title: '金额', dataIndex: 'amount', render: this.filterMoney, align: 'center' },
        {
          title: '本币金额',
          dataIndex: 'functionAmount',
          render: this.filterMoney,
          align: 'center',
        },
        {
          title: '备注',
          dataIndex: 'remark',
          align: 'center',
          render: (desc, record) => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 90,
          align: 'center',
          render: (value, record) => {
            return (
              <Badge status={this.$statusList[value].state} text={this.$statusList[value].label} />
            );
          },
        },
      ],
      unapprovedData: [],
      approvedData: [],
      unapprovedPagination: {
        total: 0,
      },
      approvedPagination: {
        total: 0,
      },
      unapprovedPage: 0,
      unapprovedPageSize: 10,
      approvedPage: 0,
      approvedPageSize: 10,
    };
  }

  componentWillMount() {
    // this.setState({ tabValue: this.props.location.query.approved ? 'approved' : 'unapproved' });
    this.setState({ tabValue: 'unapproved' });
  }

  //未审批搜索
  unapprovedSearch = values => {
    values.beginDate && (values.beginDate = moment(values.beginDate).format('YYYY-MM-DD'));
    values.endDate && (values.endDate = moment(values.endDate).format('YYYY-MM-DD'));
    if (values.fullName && values.fullName[0]) {
      values.fullName = values.fullName[0];
    }
    this.setState({ unApproveSearchParams: values }, () => {
      //this.getUnapprovedList();
      this.unApprovedtable.search({ ...this.state.unApproveSearchParams, finished: 'false' });
    });
  };

  //审批搜索
  approvedSearch = values => {
    values.beginDate && (values.beginDate = moment(values.beginDate).format('YYYY-MM-DD'));
    values.endDate && (values.endDate = moment(values.endDate).format('YYYY-MM-DD'));
    if (values.fullName && values.fullName[0]) {
      values.fullName = values.fullName[0];
    }
    this.setState({ approveSearchParams: values }, () => {
      this.approvedTable.search({ ...this.state.approveSearchParams, finished: 'true' });
    });
  };

  //进入详情页
  handleRowClick = record => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/approval-management/approval-my-reimburse/approve-reimburse-detail/${
          record.documentId
        }/${record.entityOid}/${this.state.tabValue}`,
      })
    );
  };

  filterData = data => {
    return data.map(item => {
      return { ...item.publicExpenseReportApprovalView, entityOid: item.entityOid };
    });
  };
  /**未审批根据单据编号查询 */
  onDocumentSearch = value => {
    this.setState(
      {
        unApproveSearchParams: { ...this.state.unApproveSearchParams, documentNumber: value },
      },
      () => {
        this.unApprovedtable.search({ ...this.state.unApproveSearchParams, finished: 'false' });
      }
    );
  };
  /**已审批根据单据编号查询 */
  onApprovedSearch = value => {
    this.setState(
      {
        approveSearchParams: { ...this.state.approveSearchParams, documentNumber: value },
      },
      () => {
        this.approvedTable.search({ ...this.state.approveSearchParams, finished: 'true' });
      }
    );
  };
  handleTabsChange = key => {
    this.setState({
      tabValue: key,
    });
  };

  render() {
    const { tabValue, searchForm, columns } = this.state;
    return (
      <div className="approve-contract">
        <Tabs defaultActiveKey={tabValue} onChange={this.handleTabsChange}>
          <TabPane tab={this.$t({ id: 'contract.unapproved' } /*未审批*/)} key="unapproved">
            {tabValue === 'unapproved' && (
              <div>
                <SearchArea
                  searchForm={searchForm}
                  submitHandle={this.unapprovedSearch}
                  maxLength={4}
                />
                <div className="divider" />
                <div className="table-header">
                  <Row>
                    <Col span={18} />
                    <Col span={6}>
                      <Search
                        placeholder="请输入单据编号"
                        onSearch={this.onDocumentSearch}
                        enterButton
                      />
                    </Col>
                  </Row>
                </div>
                <div className="table-header" />
                <CustomTable
                  url={`${config.workflowUrl}/api/workflow/document/approvals/filters/801001`}
                  ref={ref => (this.unApprovedtable = ref)}
                  params={{ finished: false }}
                  columns={columns}
                  tableKey="documentId"
                  onClick={this.handleRowClick}
                />
              </div>
            )}
          </TabPane>
          <TabPane tab={this.$t({ id: 'contract.approved' } /*已审批*/)} key="approved">
            {tabValue === 'approved' && (
              <div>
                <SearchArea
                  searchForm={searchForm}
                  submitHandle={this.approvedSearch}
                  maxLength={4}
                />
                <div className="divider" />
                <div className="table-header">
                  <Row>
                    <Col span={18} />
                    <Col span={6}>
                      <Search
                        placeholder="请输入单据编号"
                        onSearch={this.onApprovedSearch}
                        enterButton
                      />
                    </Col>
                  </Row>
                </div>
                <div className="table-header" />
                <CustomTable
                  url={`${config.workflowUrl}/api/workflow/document/approvals/filters/801001`}
                  ref={ref => (this.approvedTable = ref)}
                  params={{ finished: 'true' }}
                  columns={columns}
                  tableKey="documentId"
                  onClick={this.handleRowClick}
                />
              </div>
            )}
          </TabPane>
        </Tabs>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.currentUser,
    company: state.user.company,
  };
}

const wrappedReimburse = Form.create()(Reimburse);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedReimburse);
