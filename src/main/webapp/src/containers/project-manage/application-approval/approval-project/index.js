import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import moment from 'moment';
import { Input, Tabs, Badge, Popover, Col, Row, message } from 'antd';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import Service from './service';
import { from } from 'rxjs';

const TabPane = Tabs.TabPane;
const Search = Input.Search;

class ApprovalProject extends Component {
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
          type: 'select',
          id: 'documentTypeId',
          label: '单据类型',
          options: [],
          colSpan: 6,
          method: 'get',
          labelKey: 'typeName',
          valueKey: 'id',
          getUrl: `${config.expenseUrl}/api/expense/application/type/query/all`,
          getParams: { setOfBooksId: this.props.company.setOfBooksId, enabled: true },
        },
        {
          type: 'select',
          id: 'applicantOid',
          label: '申请人',
          options: [],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'applicantDate',
          colSpan: 6,
          items: [
            {
              type: 'date',
              id: 'applicantDateFrom',
              label: '申请日期从',
            },
            {
              type: 'date',
              id: 'applicantDateTo',
              label: '申请日期至',
            },
          ],
        },
        {
          type: 'input',
          id: 'documentNumber',
          label: '项目编号',
          colSpan: 6,
        },
        { type: 'input', id: 'documentId', label: '项目名称', colSpan: 6 },
        { type: 'input', id: 'remark', label: '项目说明', colSpan: 6 },
      ],
      columns: [
        {
          title: '项目申请单编号',
          dataIndex: 'documentNumber',
          align: 'center',
          width: 200,
          render: value => (
            <span>
              <Popover content={value}>{value}</Popover>
            </span>
          ),
        },
        {
          title: '项目名称',
          dataIndex: 'documentName',
          align: 'center',
          width: 160,
          render: value => (
            <span>
              <Popover content={value}>{value}</Popover>
            </span>
          ),
        },
        {
          title: '申请人',
          dataIndex: 'applicantName',
          align: 'center',
          width: 120,
        },
        {
          title: '申请日期',
          dataIndex: 'applicantDate',
          align: 'center',
          width: 180,
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: '审批状态',
          dataIndex: 'status',
          align: 'center',
          width: 140,
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
      searchUnParams: {},
      searchParams: {},
      documentId: '',
    };
  }

  componentDidMount() {
    this.setState({ tabValue: false ? 'approved' : 'unapproved' });
    this.getCreatedApplicationList();
  }

  // 表格切换
  handleTabsChange = key => {
    this.setState({
      tabValue: key,
    });
  };

  // 获取申请人
  getCreatedApplicationList = () => {
    let list = [];
    const { searchForm: form } = this.state;
    Service.getCreatedApplicationUserList()
      .then(res => {
        res.data.map(o => {
          list.push({ value: o.userOid, label: o.fullName, key: o.id, id: o.id });
        });
        form[1].options = list;
        this.setState({ searchForm: form });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 未审批搜索框
  handleSearchUnapproved = values => {
    values.applicantDateFrom &&
      (values.applicantDateFrom = moment(values.applicantDateFrom).format('YYYY-MM-DD'));
    values.applicantDateTo &&
      (values.applicantDateTo = moment(values.applicantDateTo).format('YYYY-MM-DD'));
    const { searchUnParams } = this.state;
    this.setState({ searchUnParams: { ...searchUnParams, ...values } }, () => {
      this.customUnTable.search({ ...values, finished: false });
    });
  };

  // 未审批页面搜索
  handleUnSearchNumber = value => {
    const { searchUnParams } = this.state;
    const values = { ...searchUnParams, documentNumber: value, finished: false };
    this.setState({ searchUnParams: values }, () => {
      this.customUnTable.search(values);
    });
  };

  // 已审批页面搜索
  handleSearchNumber = value => {
    const { searchParams } = this.state;
    const values = { ...searchParams, documentNumber: value, finished: true };
    this.setState({ searchParams: values }, () => {
      this.customTable.search(values);
    });
  };

  // 已审批搜索框
  handleSearch = values => {
    values.applicantDateFrom &&
      (values.applicantDateFrom = moment(values.applicantDateFrom).format('YYYY-MM-DD'));
    values.applicantDateTo &&
      (values.applicantDateTo = moment(values.applicantDateTo).format('YYYY-MM-DD'));
    const { searchParams } = this.state;
    this.setState({ searchParams: { ...searchParams, ...values } }, () => {
      this.customTable.search({ ...values, finished: true });
    });
  };

  //进入详情页
  handleRowClick = record => {
    const { tabValue } = this.state;
    let place = {
      pathname: '/approval-management/approval-project/detail/:status/:id/:oId'
        .replace(':id', record.documentId)
        .replace(':status', tabValue)
        .replace(':oId', record.entityOid),
    };
    this.props.dispatch(
      routerRedux.replace({
        pathname: place.pathname,
      })
    );
  };

  render() {
    const { tabValue, searchForm, columns, documentId } = this.state;
    return (
      <div className="approve-contract">
        <Tabs defaultActiveKey={tabValue} onChange={this.handleTabsChange}>
          {/* 未审批 */}
          <TabPane tab={'未审批'} key="unapproved">
            <div>
              <SearchArea
                searchForm={searchForm}
                maxLength={4}
                clearHandle={() => {
                  this.customUnTable.reload();
                }}
                submitHandle={this.handleSearchUnapproved}
              />
              {/* 搜索 */}
              <Row gutter={24} style={{ marginBottom: 12, marginTop: 20 }}>
                <Col span={18} />
                <Col span={6}>
                  <Search
                    placeholder="请输入项目编号"
                    onSearch={this.handleUnSearchNumber}
                    className="search-number"
                    enterButton
                  />
                </Col>
              </Row>
              {/* 表格 */}
              <CustomTable
                tableKey="documentNumber"
                ref={ref => (this.customUnTable = ref)}
                columns={columns}
                onClick={this.handleRowClick}
                params={{ finished: tabValue === 'approved' }}
                url={`${config.workflowUrl}/api/workflow/document/approvals/filters/801011`}
              />
            </div>
          </TabPane>

          {/* 已审批 */}
          <TabPane tab={'已审批'} key="approved">
            <div>
              <SearchArea
                searchForm={searchForm}
                maxLength={4}
                clearHandle={() => {
                  this.customTable.reload();
                }}
                submitHandle={this.handleSearch}
              />
              {/* 搜索 */}
              <Row gutter={24} style={{ marginBottom: 12, marginTop: 20 }}>
                <Col span={18} />
                <Col span={6}>
                  <Search
                    placeholder="请输入项目编号"
                    onSearch={this.handleSearchNumber}
                    className="search-number"
                    enterButton
                  />
                </Col>
              </Row>
              {/* 表格 */}
              <CustomTable
                tableKey="documentNumber"
                ref={ref => (this.customTable = ref)}
                columns={columns}
                onClick={this.handleRowClick}
                params={{ finished: tabValue === 'approved' }}
                url={`${config.workflowUrl}/api/workflow/document/approvals/filters/801011`}
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
)(ApprovalProject);
