import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import { Badge, Button, Divider, Popconfirm, Popover, message, Tabs } from 'antd';
import SearchArea from 'widget/search-area';
import Table from 'widget/table';
import expensePolicyService from 'containers/setting/expense-policy/expense-policy.service';
import moment from 'moment';
const TabPane = Tabs.TabPane;
import CustomTable from 'components/Widget/custom-table';

class ExpensePolicy extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      visible: false,
      loading: false,
      tabValue: 'apply',
      data: [],
      searchParams: {
        typeFlag: props.match.params.typeFlag === ':typeFlag' ? '0' : props.match.params.typeFlag,
        setOfBooksId:
          props.match.params.sob === ':sob' ? props.company.setOfBooksId : props.match.params.sob,
      },
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
      },
      searchForm: [
        {
          type: 'select',
          id: 'setOfBooksId',
          label: this.$t({ id: 'form.setting.set.of.books' }),
          options: [],
          labelKey: 'setOfBooksName',
          allowClear: false,
          valueKey: 'id',
          colSpan: 6,
          event: 'SOB',
          getUrl: `${config.mdataUrl}/api/setOfBooks/by/tenant`,
          method: 'get',
          renderOption: item => item.setOfBooksCode + '-' + item.setOfBooksName,
          getParams: { roleType: 'TENANT', enabled: true },
        },
        {
          type: 'list',
          id: 'expenseTypeId',
          colSpan: 6,
          listType: 'expense_item',
          labelKey: 'name',
          valueKey: 'id',
          event: 'expenseTypeId',
          single: true,
          listExtraParams: {
            setOfBooksId: this.props.company.setOfBooksId,
            typeFlag: 0,
            roleType: 'TENANT',
          },
          label: this.$t({ id: 'expense.policy.expenseTypeName' }) /*申请项目*/,
        },
        {
          type: 'value_list',
          id: 'dutyType',
          colSpan: 6,
          options: [],
          valueListCode: 1002,
          label: this.$t({ id: 'expense.policy.dutyName' }) /*申请人职务*/,
        },
        {
          type: 'select',
          id: 'companyLevelId',
          options: [],
          getUrl: `${config.mdataUrl}/api/companyLevel/selectByTenantId`,
          colSpan: 6,
          method: 'get',
          labelKey: 'description',
          valueKey: 'id',
          event: 'companyLevelId',
          label: this.$t({ id: 'expense.policy.companyLevelName' }) /*申请人公司级别*/,
        },
      ],
      columns: [
        {
          title: this.$t('expense.policy.setOfBooksName') /*账套*/,
          dataIndex: 'setOfBooksName',
          align: 'center',
          tooltips: true,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          // width: 120
        },
        {
          title: this.$t('expense.policy.priority') /*优先级*/,
          dataIndex: 'priority',
          align: 'center',
          // width: 80
        },
        {
          title: this.$t('expense.policy.expenseTypeName') /*申请项目*/,
          dataIndex: 'expenseTypeName',
          align: 'center',
          tooltips: true,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          // width: 120
        },
        {
          title: this.$t('expense.policy.companyLevelName') /*申请人公司级别*/,
          dataIndex: 'companyLevelName',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          align: 'center',
          // width: 120
        },
        {
          title: this.$t('expense.policy.dutyName') /*申请人职务*/,
          dataIndex: 'dutyTypeName',
          align: 'center',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          // width: 120
        },
        {
          title: this.$t('expense.policy.staffLevelName') /*申请人员工级别*/,
          dataIndex: 'staffLevelName',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          align: 'center',
          // width: 120
        },
        {
          title: this.$t('expense.policy.departmentName') /*申请人部门*/,
          dataIndex: 'departmentName',
          align: 'center',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          // width: 120
        },
        {
          title: this.$t('expense.policy.currencyName') /*币种*/,
          dataIndex: 'currencyName',
          align: 'center',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          // width: 120
        },
        {
          title: this.$t('expense.policy.controlStrategyName') /*控制策略*/,
          dataIndex: 'controlStrategyName',
          align: 'center',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
          // width: 120
        },
        {
          title: this.$t('expense.policy.startDate') /*有效日期从*/,
          dataIndex: 'startDate',
          render: value => (
            <Popover content={moment(value).format('YYYY-MM-DD')}>
              {moment(value).format('YYYY-MM-DD')}
            </Popover>
          ),
          align: 'center',

          // width: 120
        },
        {
          title: this.$t('expense.policy.endDate') /*有效日期至*/,
          dataIndex: 'endDate',
          render: value =>
            value ? (
              <Popover content={moment(value).format('YYYY-MM-DD')}>
                {moment(value).format('YYYY-MM-DD')}
              </Popover>
            ) : (
              '-'
            ),
          align: 'center',
          // width: 120
        },
        {
          title: this.$t('expense.policy.enabled') /*状态*/,
          dataIndex: 'enabled',
          // width: 80,
          align: 'center',
          render: value =>
            value ? <Badge status="success" text="启用" /> : <Badge status="error" text="禁用" />,
        },
        {
          title: this.$t('expense.policy.options') /*操作*/,
          dataIndex: 'options',
          width: 120,
          align: 'center',
          render: (value, record) => (
            <span>
              <a onClick={e => this.handleCreate(e, record)}>{this.$t('common.edit')}</a>
              <Divider type="vertical" />
              <Popconfirm
                placement="top"
                title={'确认删除？'}
                onConfirm={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.handleDelete(record);
                }}
                onCancel={e => {
                  e.preventDefault();
                  e.stopPropagation();
                }}
                okText="确定"
                cancelText="取消"
              >
                <a
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                  }}
                >
                  删除
                </a>
              </Popconfirm>
            </span>
          ),
        },
      ],
    };
  }

  handleDelete = record => {
    expensePolicyService
      .deletePolicy(record.id)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        this.customTable.search(this.state.searchParams);
      })
      .catch(e => {
        message.error(this.$t('common.delete.failed'));
      });
  };

  componentDidMount() {
    const { searchForm } = this.state;
    let params = { roleType: 'TENANT', enabled: true };
    let setOfBooksId =
      this.props.match.params.sob === ':sob'
        ? this.props.company.setOfBooksId
        : this.props.match.params.sob;
    expensePolicyService.getTenantAllSob(params).then(res => {
      searchForm[0].options = res.data.map(item => {
        if (item.id === setOfBooksId) {
          searchForm[0].defaultValue = {
            key: item.id,
            label: item.setOfBooksCode + '-' + item.setOfBooksName,
          };
        }
        return {
          key: item.id,
          value: item.id,
          label: item.setOfBooksCode + '-' + item.setOfBooksName,
        };
      });
      this.setState({ searchForm });
    });
  }

  //员工信息，工号，电话等关键字是即时搜索
  eventSearchAreaHandle = (e, item) => {
    let { searchParams, searchForm } = this.state;
    let pagination = this.state.pagination;
    pagination.page = 0;
    switch (e) {
      case 'SOB': {
        searchParams.setOfBooksId = item;
        searchForm[1].listExtraParams = {
          ...searchForm[0].listExtraParams,
          setOfBooksId: item,
        };

        break;
      }
      case 'expenseTypeId': {
        searchParams.expenseTypeId = item[0] ? item[0].id : null;
        break;
      }
      case 'companyLevelId': {
        searchParams.companyLevelId = item;
        break;
      }
    }
    this.setState(
      {
        pagination,
        searchParams,
        searchForm,
      },
      () => {
        this.customTable.search(this.state.searchParams);
      }
    );
  };
  // 点击搜索按钮
  search = result => {
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.current = 1;
    pagination.total = 0;
    this.setState(
      {
        pagination,
        searchParams: Object.assign(this.state.searchParams, result),
      },
      () => {
        this.customTable.search(this.state.searchParams);
      }
    );
  };
  // 点击清空按钮
  clear = () => {
    this.setState({
      searchParams: {
        setOfBook: '',
      },
    });
  };

  // 新建、编辑费用政策
  handleCreate = (e, record) => {
    !!record && e.preventDefault();
    !!record && e.stopPropagation();
    let id = record ? record.id : 'new';
    let url = record
      ? `edit-expense-policy/${record.id}`
      : e.id
        ? `expense-policy-detail/${e.id}`
        : 'new-expense-policy/new';
    this.props.dispatch(
      routerRedux.push({
        pathname: `/admin-setting/expense-policy/${url}/${this.state.searchParams.setOfBooksId}/${
          this.state.searchParams.typeFlag
        }`,
      })
    );
  };

  handleTab = key => {
    const { searchParams, searchForm, columns } = this.state;
    columns[2].title = searchForm[1].label =
      key === 'apply' ? this.$t({ id: 'expense.policy.expenseTypeName' }) : '报销项目';
    searchForm[1].listExtraParams = {
      ...searchForm[1].listExtraParams,
      typeFlag: key === 'apply' ? '0' : '1',
    };
    this.setState(
      {
        tabValue: key,
        searchForm,
        searchParams: {
          ...searchParams,
          typeFlag: key === 'apply' ? '0' : '1',
        },
      },
      () => {
        this.customTable && this.customTable.search(this.state.searchParams);
      }
    );
  };

  render() {
    const { columns, searchParams, searchForm, tabValue } = this.state;
    return (
      <div className="train">
        <Tabs defaultActiveKey={tabValue} onChange={this.handleTab}>
          <TabPane tab="费用申请政策" key="apply" />
          <TabPane tab="费用报销政策" key="export" />
        </Tabs>
        <SearchArea
          eventHandle={this.eventSearchAreaHandle}
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
        />
        <div style={{ margin: '20px 0' }}>
          {/*新建*/}
          <Button type="primary" onClick={this.handleCreate}>
            {this.$t({ id: 'common.create' })}
          </Button>
        </div>
        <CustomTable
          columns={columns}
          onClick={this.handleCreate}
          url={`${config.expenseUrl}/api/expense/policy/pageByCondition`}
          params={searchParams}
          ref={ref => (this.customTable = ref)}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ExpensePolicy);
