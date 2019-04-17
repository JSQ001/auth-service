import React, { Component } from 'react';
import { connect } from 'dva';
import config from 'config';
import moment from 'moment';
import { routerRedux } from 'dva/router';
import { Badge, Popover } from 'antd';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';

class ChangeProject extends Component {
  constructor(props) {
    super(props);
    const statusList = [
      { value: 1001, label: this.$t('common.editing') },
      { value: 1002, label: this.$t('common.approving') },
      { value: 1003, label: this.$t('common.withdraw') },
      { value: 1004, label: this.$t('common.approve.pass') },
      { value: 1005, label: this.$t('common.approve.rejected') },
    ];
    this.state = {
      status: {
        1001: { label: '编辑中', state: 'default' },
        1004: { label: '审批通过', state: 'success' },
        1002: { label: '审批中', state: 'processing' },
        1005: { label: '审批驳回', state: 'error' },
        1003: { label: '撤回', state: 'warning' },
      },
      searchForm: [
        {
          label: this.$t('contract.project.req.num'), // '项目申请单编号'
          type: 'input',
          id: 'projectReqNumber',
          colSpan: 6,
        },
        {
          label: this.$t('contract.project.name'), // 项目名称,
          type: 'input',
          id: 'projectName',
          colSpan: 6,
        },
        {
          label: '是否立项',
          type: 'value_list',
          id: 'projectFlag',
          colSpan: 6,
          options: [
            { label: this.$t('common.yes'), value: true },
            { label: this.$t('common.no'), value: false },
          ],
          valueKey: 'value',
          labelKey: 'label',
        },
        {
          type: 'select',
          id: 'status',
          label: '状态',
          options: statusList,
          colSpan: 6,
        },
        {
          label: this.$t('contract.project.leader'), // '项目负责人',
          id: 'pmId',
          type: 'list',
          listType: 'select_employee',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          single: true,
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'projectNumber',
          label: '项目编号',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'startUseDate',
          colSpan: 6,
          items: [
            {
              type: 'date',
              id: 'startUseDateFrom',
              label: '启用日期从',
            },
            {
              type: 'date',
              id: 'startUseDateTo',
              label: '启用日期至',
            },
          ],
        },
        {
          type: 'items',
          id: 'closeUseDate',
          colSpan: 6,
          items: [
            {
              type: 'date',
              id: 'closeUseDateFrom',
              label: '禁用日期从',
            },
            {
              type: 'date',
              id: 'closeUseDateTo',
              label: '禁用日期至',
            },
          ],
        },
      ],
      columns: [
        {
          title: this.$t('contract.project.req.num'),
          dataIndex: 'projectReqNumber',
          align: 'center',
          width: 280,
          tooltips: true,
        },
        {
          title: this.$t('contract.project.name'),
          dataIndex: 'projectName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('common.applicant'),
          dataIndex: 'employeeName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('common.apply.data'),
          dataIndex: 'requisitionDate',
          align: 'center',
          width: 150,
          render: data => {
            return (
              <Popover content={data ? moment(data).format('YYYY-MM-DD') : '-'}>
                {data ? moment(data).format('YYYY-MM-DD') : '-'}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.project.leader'),
          dataIndex: 'pmName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('contract.start.date'), // '启用日期',
          dataIndex: 'startUseDate',
          align: 'center',
          width: 150,
          render: data => {
            return (
              <Popover content={data ? moment(data).format('YYYY-MM-DD') : '-'}>
                {data ? moment(data).format('YYYY-MM-DD') : '-'}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.close.date'), // '禁用日期',
          dataIndex: 'closeUseDate',
          align: 'center',
          width: 150,
          render: data => {
            return (
              <Popover content={data ? moment(data).format('YYYY-MM-DD') : '-'}>
                {data ? moment(data).format('YYYY-MM-DD') : '-'}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.project.mark'), // '立项标志',
          dataIndex: 'projectFlag',
          align: 'center',
          width: 120,
          render: text => {
            return (
              <Popover
                content={text ? this.$t('contract.hasProject') : this.$t('contract.noProject')} // '已立项' : '未立项'
              >
                {text ? this.$t('contract.hasProject') : this.$t('contract.noProject')}
              </Popover>
            );
          },
        },
        {
          title: this.$t('contract.approval.status'), // '审批状态',
          dataIndex: 'status',
          align: 'center',
          width: 120,
          render: status => {
            return (
              <Popover content={this.$statusList[status].label}>
                <Badge
                  status={this.$statusList[status].state}
                  text={this.$statusList[status].label}
                />
              </Popover>
            );
          },
        },
        {
          title: '项目预算',
          align: 'center',
          width: 150,
          dataIndex: 'operation',
          fixed: 'right',
          render: (value, record, index) => {
            return (
              <span>
                <a onClick={e => this.detailClick(e, record)}>预算</a>
              </span>
            );
          },
        },
      ],
      searchParams: {},
    };
  }

  componentDidMount() {}

  // 已审批搜索框
  handleSearch = values => {
    values.closeUseDateFrom &&
      (values.closeUseDateFrom = moment(values.closeUseDateFrom).format('YYYY-MM-DD'));
    values.closeUseDateTo &&
      (values.closeUseDateTo = moment(values.closeUseDateTo).format('YYYY-MM-DD'));
    values.startUseDateTo &&
      (values.startUseDateTo = moment(values.startUseDateTo).format('YYYY-MM-DD'));
    values.startUseDateFrom &&
      (values.startUseDateFrom = moment(values.startUseDateFrom).format('YYYY-MM-DD'));
    const { searchParams } = this.state;
    this.setState({ searchParams: { ...searchParams, ...values } }, () => {
      this.customTable.search({ ...values });
    });
  };

  //进入详情页
  handleRowClick = record => {
    let place = {
      pathname: '/project-manage/project-change/detail/:id'.replace(':id', record.id),
    };
    this.props.dispatch(
      routerRedux.replace({
        pathname: place.pathname,
      })
    );
  };

  // 预算
  detailClick = (e, record) => {
    e.preventDefault();
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: `/project-manage/project-change/budget-details/${record.id}`,
      })
    );
  };

  render() {
    const { searchForm, columns } = this.state;
    return (
      <div className="approve-contract">
        <SearchArea
          searchForm={searchForm}
          maxLength={4}
          clearHandle={() => {
            this.customTable.reload();
          }}
          submitHandle={this.handleSearch}
        />
        <div style={{ width: '100%', height: '40px' }} />
        {/* 表格 */}
        <CustomTable
          ref={ref => (this.customTable = ref)}
          columns={columns}
          url={`${config.contractUrl}/api/project/requisition/query/by/cond?status=1004&createdBy=${
            this.props.user.id
          }`}
          scroll={{ x: 1300 }}
          // &status=1004
          onClick={this.handleRowClick}
        />
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
)(ChangeProject);
