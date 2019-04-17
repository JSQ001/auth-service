import React from 'react';

import { connect } from 'dva';
import constants from 'share/constants';
import { deepFullCopy, dealCache } from 'utils/extend';
import { Form, Tabs, Badge, Popover, Affix, message } from 'antd';
import Table from 'widget/table';
const TabPane = Tabs.TabPane;
import { routerRedux } from 'dva/router';

import moment from 'moment';
import SearchArea from 'widget/search-area';
import ApproveBar from 'widget/Template/approve-bar';
import approveRequestService from 'containers/approve/request/request.service';
import 'styles/approve/request/request.scss';

class ApproveRequest extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      passLoading: false,
      rejectLoading: false,
      tab: 'approving',
      approvePendingCheckboxList: [
        {
          id: 'formOids',
          items: [
            {
              label: this.$t('request.bill.name' /*单据名称*/),
              key: 'formOid',
              options: [],
              checkAllOption: true,
            },
          ],
        },
      ],
      approvedCheckboxList: [
        {
          id: 'formOids',
          items: [
            {
              label: this.$t('request.bill.name' /*单据名称*/),
              key: 'formOid',
              options: [],
              checkAllOption: true,
            },
          ],
        },
      ],
      searchForm: [
        {
          type: 'input',
          id: 'businessCode',
          label: this.$t('finance.view.search.documentNo' /*单号*/),
        },
        {
          type: 'list',
          id: 'applicantOids',
          label: this.$t('finance.view.search.applicant' /*申请人*/),
          listType: 'user',
          labelKey: 'fullName',
          valueKey: 'userOid',
          listExtraParams: { roleType: 'TENANT' },
        },
        {
          type: 'list',
          id: 'departmentOids',
          label: this.$t('request.detail.department.name' /*部门*/),
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentOid',
          single: true,
        },
        {
          type: 'items',
          id: 'priceRange',
          items: [
            {
              type: 'input',
              id: 'minAmount',
              label: this.$t('approve.request.moneyFrom') /*金额从*/,
            },
            {
              type: 'input',
              id: 'maxAmount',
              label: this.$t('approve.request.moneyTo') /*金额至*/,
            },
          ],
        },
        {
          type: 'items',
          id: 'dateRange',
          items: [
            { type: 'date', id: 'beginDate', label: this.$t('finance.audit.startDate' /*日期从*/) },
            { type: 'date', id: 'endDate', label: this.$t('finance.audit.endDate' /*日期到*/) },
          ],
        },
      ],
      approvePendingSearchParams: {},
      approvedSearchParams: {},

      status: [
        {
          label: this.$t('my.contract.state.generate' /*编辑中*/),
          value: '10011000',
          state: 'processing',
        },
        {
          label: this.$t('my.contract.state.withdrawal' /*已撤回*/),
          value: '10011001',
          state: 'warning',
        },
        {
          label: this.$t('finance.view.search.reject' /*已驳回*/),
          value: '10011002',
          state: 'error',
        },
        {
          label: this.$t('finance.view.search.auditReject' /*审核驳回*/),
          value: '10011003',
          state: 'error',
        },
        {
          label: this.$t('finance.view.search.submitted' /*审批中*/),
          value: '10021000',
          state: 'processing',
        },
        {
          label: this.$t('finance.view.search.pass' /*已通过*/),
          value: '10031000',
          state: 'success',
        },
        {
          label: this.$t('finance.view.search.auditPass' /*审核通过*/),
          value: '1004',
          state: 'success',
        },
        { label: this.$t('finance.view.search.payed' /*已付款*/), value: '1005', state: 'success' },
        {
          label: this.$t('finance.view.search.refund' /*还款中*/),
          value: '1006',
          state: 'processing',
        },
        {
          label: this.$t('finance.view.search.repaid' /*已还款*/),
          value: '1007',
          state: 'success',
        },
        {
          label: this.$t('finance.view.search.paying' /*付款中*/),
          value: '1008',
          state: 'processing',
        },
        {
          label: this.$t('supplier.management.disuse' /*已停用*/),
          value: '1009',
          state: 'default',
        },
      ],

      columns: [
        {
          title: this.$t('common.sequence' /*序号*/),
          dataIndex: 'index',
          width: '8%',
          render: (value, record, index) =>
            index +
            1 +
            this.state.pageSize * this.state.page +
            (record.approverOid &&
            record.currentUserOid &&
            record.approverOid !== record.currentUserOid
              ? `(${this.$t('approve.request.proxy') /*代理*/})`
              : ''),
        },
        {
          title: this.$t('finance.view.search.jobNumber' /*工号*/),
          dataIndex: 'employeeID',
          render: value =>
            value ? (
              <Popover placement="topLeft" content={value}>
                {value}
              </Popover>
            ) : (
              '-'
            ),
        },
        {
          title: this.$t('finance.view.search.applicant' /*申请人*/),
          dataIndex: 'applicant',
          render: value =>
            value && value.fullName ? (
              <Popover placement="topLeft" content={value.fullName}>
                {value.fullName}
              </Popover>
            ) : (
              '-'
            ),
        },
        {
          title: this.$t('finance.view.search.submitDate' /*提交日期*/),
          dataIndex: 'submittedDate',
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t('request.bill.name' /*单据名称*/),
          dataIndex: 'formName',
          render: value =>
            value ? (
              <Popover placement="topLeft" content={value}>
                {value}
              </Popover>
            ) : (
              '-'
            ),
        },
        {
          title: this.$t('finance.view.search.documentNo' /*单号*/),
          dataIndex: 'businessCode',
          render: value =>
            value ? (
              <Popover placement="topLeft" content={value}>
                {value}
              </Popover>
            ) : (
              '-'
            ),
        },
        {
          title: this.$t('approve.request.matter' /*事由*/),
          dataIndex: 'title',
          render: value =>
            value ? (
              <Popover placement="topLeft" content={value}>
                {value}
              </Popover>
            ) : (
              '-'
            ),
        },
        {
          title: this.$t('finance.view.search.currency' /*币种*/),
          dataIndex: 'originCurrencyCode',
          width: '8%',
        },
        {
          title: this.$t('borrowing.limit.control.amount' /*金额*/),
          dataIndex: 'originCurrencyTotalAmount',
          render: this.filterMoney,
        },
        {
          title: this.$t('request.base.amount' /*本币金额*/),
          dataIndex: 'totalAmount',
          sorter: true,
          render: value =>
            value ? this.filterMoney(value) : <span className="money-cell">-</span>,
        },
        {
          title: this.$t('common.column.status'),
          dataIndex: 'status',
          width: this.props.language.code === 'zh_cn' ? '8%' : '13%',
          render: (value, record) => {
            return (
              <Badge
                text={
                  constants.getTextByValue(
                    String(value + '' + record.applicationType),
                    'documentStatus'
                  ) ||
                  constants.getTextByValue(
                    String(value + '' + record.rejectType),
                    'documentStatus'
                  ) ||
                  constants.getTextByValue(String(value), 'documentStatus')
                }
                status={
                  constants.getTextByValue(
                    String(value + '' + record.applicationType),
                    'documentStatus',
                    'state'
                  ) ||
                  constants.getTextByValue(
                    String(value + '' + record.rejectType),
                    'documentStatus',
                    'state'
                  ) ||
                  constants.getTextByValue(String(value), 'documentStatus', 'state')
                }
              />
            );
          },
        },
      ],
      sort: '',
      data: [],
      page: 0,
      pageSize: 10,
      pagination: {
        total: 0,
      },
      batchEnabled: false, //是否有批量审批
      selectedRowKeys: [],
      selectedRows: [], //选中的单据Oid
      cacheSearchData: {}, //缓存筛选的数据
      approveRequestDetail:
        '/approval-management/approve-request/approve-request-detail/:formOid/:applicationOid/:pageFrom', //申请单审批详情页
    };
  }

  componentDidMount() {
    this.setState(
      {
        tab: (this.props.location && this.props.location.query.tab) || 'approving',
        batchEnabled: true, //this.props.profile['all.Approval.batch.Enable']
      },
      () => {
        this.getForms();
      }
    );
  }

  //存储筛选数据缓存
  setCache = result => {
    const { tab, page } = this.state;
    result.tab = tab;
    result.page = page;
    this.setState({ cacheSearchData: result });
  };

  //获取筛选数据缓存
  getCache = () => {
    let result = this.props.approveRequest;
    let { tab, approvePendingCheckboxList, approvedCheckboxList } = this.state;
    if (result && JSON.stringify(result) !== '{}') {
      if (tab === 'approving') {
        approvePendingCheckboxList[0].items[0].checked = result['formOidsLable'] || [];
      } else {
        approvedCheckboxList[0].items[0].checked = result['formOidsLable'] || [];
      }
      this.setState(
        { cacheSearchData: result, approvePendingCheckboxList, approvedCheckboxList },
        () => {
          this.dealCache(result);
        }
      );
    } else {
      let defaultSearchForm = deepFullCopy(this.state.searchForm);
      this.getList();
      this.setState({ defaultSearchForm });
    }
  };

  //处理筛选缓存数据
  dealCache = result => {
    const { searchForm } = this.state;
    let defaultSearchForm = deepFullCopy(this.state.searchForm);
    if (result) {
      dealCache(searchForm, result);
      this.setState(
        {
          tab: result.tab,
          page: result.page,
          defaultSearchForm,
        },
        () => {
          this.search(result);
          this.props.dispatch({
            type: 'cache/setApproveRequest',
            payload: { approveRequest: null },
          });
        }
      );
    }
  };

  getForms = () => {
    ['approving', 'approved'].map(tab => {
      let checkboxList = this.state[
        tab === 'approving' ? 'approvePendingCheckboxList' : 'approvedCheckboxList'
      ];
      approveRequestService.getDocumentType(tab === 'approved').then(res => {
        let options = [];
        Object.keys(res.data).map(key => {
          options.push({ label: key, value: res.data[key][0] });
        });
        checkboxList.map(form => {
          if (form.id === 'formOids') {
            form.items.map(item => {
              item.key === 'formOid' && (item.options = options);
            });
          }
        });
        this.setState(
          {
            [tab === 'approving'
              ? 'approvePendingCheckboxList'
              : 'approvedCheckboxList']: checkboxList,
          },
          () => {
            tab === 'approving' && this.getCache();
          }
        );
      });
    });
  };

  getList = () => {
    const {
      tab,
      page,
      pageSize,
      approvePendingSearchParams,
      approvedSearchParams,
      sort,
    } = this.state;
    if (sort && tab === 'approved') {
      approvedSearchParams.sort = sort;
    }
    if (sort && tab !== 'approved') {
      approvePendingSearchParams.sort = sort;
    }
    this.setCache(tab === 'approving' ? approvePendingSearchParams : approvedSearchParams);
    this.setState({ loading: true });
    approveRequestService
      .getApproveRequestList(
        tab === 'approved',
        page,
        pageSize,
        tab === 'approved' ? approvedSearchParams : approvePendingSearchParams
      )
      .then(res => {
        let data = [];
        res.data.map(item => {
          if (item.application) {
            item.application.entityOid = item.entityOid;
            data.push(item.application || {});
          }
        });
        this.setState({
          loading: false,
          data,
          pagination: {
            total: Number(res.headers['x-total-count']) || 0,
            current: page + 1,
            onChange: this.onChangePaper,
          },
        });
      });
  };

  onChangePaper = page => {
    if (page - 1 !== this.state.page) {
      this.setState({ page: page - 1 }, () => {
        const { tab, approvePendingSearchParams, approvedSearchParams } = this.state;
        this.setCache(tab === 'approving' ? approvePendingSearchParams : approvedSearchParams);
        //this.getList()
      });
    }
  };

  onTabChange = tab => {
    this.setState(
      {
        tab,
        page: 0,
        pagination: { total: 0 },
      },
      () => {
        this.setCache({});
        this.getList();
      }
    );
  };

  search = values => {
    this.setCache({ ...values });
    values.beginDate && (values.beginDate = moment(values.beginDate).format('YYYY-MM-DD'));
    values.endDate && (values.endDate = moment(values.endDate).format('YYYY-MM-DD'));
    this.setState(
      {
        [this.state.tab === 'approving'
          ? 'approvePendingSearchParams'
          : 'approvedSearchParams']: values,
        page: 0,
        pagination: { total: 0 },
      },
      () => {
        this.getList();
      }
    );
  };

  searchClear = () => {
    this.setState({ cacheSearchData: {} });
    this.setCache({});
    let { defaultSearchForm } = this.state;
    this.setState(
      {
        [this.state.tab === 'approving'
          ? 'approvePendingSearchParams'
          : 'approvedSearchParams']: {},
        page: 0,
        searchForm: deepFullCopy(defaultSearchForm),
        pagination: { total: 0 },
      },
      () => {
        this.getList();
      }
    );
  };

  handleTableChange = (pagination, filters, sorter) => {
    let page = pagination.current;
    let sort = '';
    if (sorter.order) {
      sort = `${sorter.columnKey},${sorter.order === 'ascend' ? 'ASC' : 'DESC'}`;
    }
    this.setState(
      {
        page: page - 1,
        sort,
      },
      () => {
        this.getList();
      }
    );
  };

  handleRowClick = record => {
    this.props.dispatch({
      type: 'cache/setApproveRequest',
      payload: { approveRequest: this.state.cacheSearchData },
    });
    let url = this.state.approveRequestDetail
      .replace(':formOid', record.formOid)
      .replace(':pageFrom', this.state.tab)
      .replace(':applicationOid', record.applicationOid);
    this.props.dispatch(
      routerRedux.push({
        pathname: url,
      })
    );
  };

  //列表选择更改
  onSelectChange = selectedRowKeys => {
    if (selectedRowKeys.length > 20) {
      message.warning(this.$t('approve.request.maximumData' /*最多选择20条数据*/));
    } else {
      this.setState({ selectedRowKeys });
    }
  };

  //选择一行
  //选择逻辑：每一项设置selected属性，如果为true则为选中
  onSelectRow = (record, selected) => {
    let selectedRows = deepFullCopy(this.state.selectedRows);
    let item = JSON.stringify({
      approverOid: record.approverOid,
      entityOid: record.entityOid,
      entityType: 1001, //申请单
    });
    selected ? selectedRows.push(item) : selectedRows.delete(item);
    selectedRows.length <= 20 && this.setState({ selectedRows });
  };

  //全选
  onSelectAllRow = selected => {
    let selectedRows = deepFullCopy(this.state.selectedRows);
    this.state.data.map(item => {
      let row = JSON.stringify({
        approverOid: item.approverOid,
        entityOid: item.entityOid,
        entityType: 1001, //申请单
      });
      selected ? selectedRows.addIfNotExist(row) : selectedRows.delete(row);
    });
    selectedRows.length <= 20 && this.setState({ selectedRows });
  };

  //审批操作
  handleApprove = (value, type) => {
    let entities = [];
    this.state.selectedRows.map(item => {
      entities.push(JSON.parse(item));
    });
    let params = {
      entities,
      approvalTxt: value,
    };
    this.setState({ [type === 'pass' ? 'passLoading' : 'rejectLoading']: true });
    approveRequestService[
      type === 'pass' ? 'handleRequestApprovePass' : 'handleRequestApproveReject'
    ](params)
      .then(res => {
        if (res.status === 200) {
          message.success(
            this.$t(
              'approve.request.successNum',
              { total: res.data.successNum } /*成功处理 ${res.data.successNum} 笔单据*/
            ) +
              this.$t(
                'approve.request.failNum',
                { total: res.data.failNum } /*失败 ${res.data.failNum} 笔单据*/
              )
          );
          this.setState({ passLoading: false, rejectLoading: false });
          this.setState(
            {
              tab: 'approving',
              page: 0,
              pagination: { total: 0 },
              selectedRowKeys: [],
              selectedRows: [],
            },
            () => {
              this.getList();
            }
          );
        }
      })
      .catch(e => {
        this.setState({ passLoading: false, rejectLoading: false });
        message.error(
          `${this.$t('common.operate.filed' /*操作失败*/)}，${e.response.data.message}`
        );
      });
  };

  render() {
    const {
      loading,
      tab,
      searchForm,
      approvePendingCheckboxList,
      approvedCheckboxList,
      columns,
      data,
      pagination,
      batchEnabled,
      selectedRowKeys,
      selectedRows,
      passLoading,
      rejectLoading,
      page,
      pageSize,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
      onSelect: this.onSelectRow,
      onSelectAll: this.onSelectAllRow,
    };
    return (
      <div
        className={`approve-request ${
          selectedRows.length && tab === 'approving' ? 'bottom-100' : ''
        }`}
      >
        <Tabs activeKey={tab} onChange={this.onTabChange}>
          <TabPane tab={this.$t('approve.request.processing' /*待审批*/)} key="approving" />
          <TabPane tab={this.$t('approve.request.approved' /*已审批*/)} key="approved" />
        </Tabs>
        {tab === 'approving' && (
          <SearchArea
            searchForm={searchForm}
            checkboxListForm={approvePendingCheckboxList}
            isReturnLabel
            submitHandle={this.search}
            clearHandle={this.searchClear}
          />
        )}
        {tab === 'approved' && (
          <SearchArea
            searchForm={searchForm}
            checkboxListForm={approvedCheckboxList}
            isReturnLabel
            submitHandle={this.search}
            clearHandle={this.searchClear}
          />
        )}
        <div className="table-header">
          <div className="table-header-title">
            {this.$t('common.total', { total: pagination.total } /*共搜索到 {total} 条数据*/)}
            {tab === 'approving' &&
              batchEnabled &&
              ` / ${
                this.$t('common.total.selected', { total: selectedRows.length }) /*已选 {total} 条*/
              }`}
          </div>
        </div>
        <Table
          rowKey="applicationOid"
          loading={loading}
          columns={columns}
          dataSource={data}
          pagination={pagination}
          onChange={this.handleTableChange}
          onRow={record => ({
            onClick: () => this.handleRowClick(record),
          })}
          rowSelection={tab === 'approving' && batchEnabled ? rowSelection : null}
          bordered
          size="middle"
        />
        <Affix
          offsetBottom={0}
          className={`bottom-bar-approve ${
            selectedRows.length && tab === 'approving' ? 'show' : 'hide'
          }`}
        >
          <ApproveBar
            passLoading={passLoading}
            rejectLoading={rejectLoading}
            batchNumber={selectedRows.length}
            handleApprovePass={value => this.handleApprove(value, 'pass')}
            handleApproveReject={value => this.handleApprove(value, 'reject')}
          />
        </Affix>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    //profile: state.user.profile,
    language: state.languages,
    approveRequest: state.cache.approveRequest,
  };
}

const wrappedApproveRequest = Form.create()(ApproveRequest);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedApproveRequest);