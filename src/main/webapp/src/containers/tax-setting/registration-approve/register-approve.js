/* eslint-disable no-constant-condition */
/* eslint-disable no-return-assign */
/* eslint-disable no-unused-expressions */
/* eslint-disable no-param-reassign */
/* eslint-disable no-template-curly-in-string */
/* eslint-disable react/destructuring-assignment */
/* eslint-disable import/first */
/* eslint-disable prefer-destructuring */
import React from 'react';
import { connect } from 'dva';
import { Input, Tabs, Badge, Popover, Col, Row } from 'antd';

const TabPane = Tabs.TabPane;
import config from 'config';
import { routerRedux } from 'dva/router';
import SearchArea from 'widget/search-area';
import moment from 'moment';
import CustomTable from 'widget/custom-table';

const Search = Input.Search;

class ExpenseApplicationApprove extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabValue: 'unapproved',
      status: {
        1001: { label: '编辑中', state: 'default' },
        1004: { label: '审批通过', state: 'success' },
        1002: { label: '审批中', state: 'processing' },
        1005: { label: '审批驳回', state: 'error' },
        1003: { label: '撤回', state: 'warning' },
      },
      searchForm: [
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
      ],
      searchParams: {},
      searchUnParams: {},
      columns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: 180,
          align: 'center',
          render: value => (
            <span>
              <Popover content={value}>{value}</Popover>
            </span>
          ),
        },
        {
          title: '单据类型',
          dataIndex: 'documentTypeName',
          width: 150,
          align: 'center',
          render: value => (
            <span>
              <Popover content={value}>{value}</Popover>
            </span>
          ),
        },
        {
          title: '纳税人名称',
          dataIndex: 'documentName',
          width: 150,
          align: 'center',
          render: value => (
            <span>
              <Popover content={value}>{value}</Popover>
            </span>
          ),
        },
        {
          title: '申请人',
          dataIndex: 'applicantName',
          width: 100,
          align: 'center',
        },
        {
          title: '提交日期',
          dataIndex: 'submittedDate',
          width: 100,
          align: 'center',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 100,
          align: 'center',
          render: value => {
            return (
              <Badge
                status={this.state.status[value].state}
                text={this.state.status[value].label}
              />
            );
          },
        },
      ],
    };
  }

  componentDidMount() {
    this.setState({ tabValue: false ? 'approved' : 'unapproved' });
  }

  handleSearchUnapproved = values => {
    values.fullName = values.fullName && values.fullName[0];
    values.beginDate && (values.beginDate = moment(values.beginDate).format('YYYY-MM-DD'));
    values.endDate && (values.endDate = moment(values.endDate).format('YYYY-MM-DD'));
    const { searchUnParams } = this.state;
    this.setState({ searchUnParams: { ...searchUnParams, ...values } }, () => {
      this.customUnTable.search({ ...values, finished: false });
    });
  };

  handleSearch = values => {
    values.fullName = values.fullName && values.fullName[0];
    values.beginDate && (values.beginDate = moment(values.beginDate).format('YYYY-MM-DD'));
    values.endDate && (values.endDate = moment(values.endDate).format('YYYY-MM-DD'));
    const { searchParams } = this.state;
    this.setState({ searchParams: { ...searchParams, ...values } }, () => {
      this.customTable.search({ ...values, finished: true });
    });
  };

  handleTabsChange = key => {
    this.setState({
      tabValue: key,
    });
  };

  // 进入详情页
  handleRowClick = record => {
    const { tabValue } = this.state;
    // eslint-disable-next-line prefer-const
    let place = {
      pathname: '/approval-management/registration-approve/detail/:status/:id'
        .replace(':id', record.documentId)
        .replace(':status', tabValue),
    };
    this.props.dispatch(
      routerRedux.replace({
        pathname: place.pathname,
      })
    );
  };

  handleSearchNumber = value => {
    const { searchParams } = this.state;
    const values = { ...searchParams, documentNumber: value, finished: true };
    this.setState({ searchParams: values }, () => {
      this.customTable.search(values);
    });
  };

  handleUnSearchNumber = value => {
    const { searchUnParams } = this.state;
    const values = { ...searchUnParams, documentNumber: value, finished: false };
    this.setState({ searchUnParams: values }, () => {
      this.customUnTable.search(values);
    });
  };

  render() {
    const { tabValue, searchForm, columns } = this.state;
    return (
      <div className="approve-contract">
        <Tabs defaultActiveKey={tabValue} onChange={this.handleTabsChange}>
          <TabPane tab={this.$t({ id: 'contract.unapproved' } /* 未审批 */)} key="unapproved">
            <div>
              <SearchArea
                searchForm={searchForm}
                maxLength={4}
                clearHandle={() => {
                  this.customUnTable.reload();
                }}
                submitHandle={this.handleSearchUnapproved}
              />
              <Row gutter={24} style={{ marginBottom: 12, marginTop: 20 }}>
                <Col span={18} />
                <Col span={6}>
                  <Search
                    placeholder="请输入单据编号"
                    onSearch={this.handleUnSearchNumber}
                    className="search-number"
                    enterButton
                  />
                </Col>
              </Row>
              <CustomTable
                tableKey="documentNumber"
                ref={ref => (this.customUnTable = ref)}
                columns={columns}
                onClick={this.handleRowClick}
                scroll={{ x: true, y: false }}
                params={{ finished: tabValue === 'approved' }}
                url={`${config.workflowUrl}/api/workflow/document/approvals/filters/902001`}
              />
            </div>
          </TabPane>
          <TabPane tab={this.$t({ id: 'contract.approved' } /* 已审批 */)} key="approved">
            <div>
              <SearchArea
                searchForm={searchForm}
                maxLength={4}
                clearHandle={() => {
                  this.customTable.reload();
                }}
                submitHandle={this.handleSearch}
              />
              <Row gutter={24} style={{ marginBottom: 12, marginTop: 20 }}>
                <Col span={18} />
                <Col span={6}>
                  <Search
                    placeholder="请输入单据编号"
                    onSearch={this.handleSearchNumber}
                    className="search-number"
                    enterButton
                  />
                </Col>
              </Row>
              <CustomTable
                tableKey="documentNumber"
                ref={ref => (this.customTable = ref)}
                columns={columns}
                onClick={this.handleRowClick}
                scroll={{ x: true, y: false }}
                params={{ finished: tabValue === 'approved' }}
                url={`${config.workflowUrl}/api/workflow/document/approvals/filters/902001`}
              />
            </div>
          </TabPane>
        </Tabs>
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
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ExpenseApplicationApprove);
