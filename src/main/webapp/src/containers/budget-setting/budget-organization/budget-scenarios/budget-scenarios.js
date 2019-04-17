import React from 'react';
import { connect } from 'dva';
import { Button, Badge, Popover, message } from 'antd';
import Table from 'widget/table';
import httpFetch from 'share/httpFetch';
import config from 'config';
import SearchArea from 'widget/search-area';
import SlideFrame from 'widget/slide-frame';
import NewBudgetScenarios from 'containers/budget-setting/budget-organization/budget-scenarios/new-budget-scenarios';
import UpdateBudgetScenarios from 'containers/budget-setting/budget-organization/budget-scenarios/update-budget-scenarios';

class BudgetScenarios extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      organizationInfo: {},
      newParams: {},
      updateParams: {},
      searchForm: [
        {
          type: 'input',
          id: 'scenarioCode',
          label: this.$t({ id: 'budget.scenarios.code' } /*预算场景代码*/),
        },
        {
          type: 'input',
          id: 'scenarioName',
          label: this.$t({ id: 'budget.scenarios.name' } /*预算场景名称*/),
        },
      ],
      searchParams: {
        scenarioCode: '',
        scenarioName: '',
      },
      loading: true,
      columns: [
        {
          title: this.$t({ id: 'budget.scenarios.code' } /*预算场景代码*/),
          align: 'center',
          dataIndex: 'scenarioCode',
          key: 'scenarioCode',
        },
        {
          title: this.$t({ id: 'budget.scenarios.name' } /*预算场景名称*/),
          align: 'center',
          dataIndex: 'scenarioName',
          key: 'scenarioName',
          render: desc => (
            <Popover placement="topLeft" content={desc}>
              {desc}
            </Popover>
          ),
        },
        {
          title: this.$t({ id: 'common.remark' } /*备注*/),
          align: 'center',
          dataIndex: 'description',
          key: 'description',
          render: desc =>
            desc ? (
              <Popover placement="topLeft" content={desc}>
                {desc}
              </Popover>
            ) : (
              '-'
            ),
        },
        {
          title: this.$t({ id: 'budget.scenarios.default' } /*默认场景*/),
          align: 'center',
          dataIndex: 'defaultFlag',
          key: 'defaultFlag',
          width: '10%',
          render: defaultFlag => (defaultFlag ? 'Y' : '-'),
        },
        {
          title: this.$t({ id: 'common.column.status' } /*状态*/),
          align: 'center',
          dataIndex: 'enabled',
          key: 'enabled',
          width: '10%',
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={
                enabled
                  ? this.$t({ id: 'common.status.enable' } /*启用*/)
                  : this.$t({ id: 'common.status.disable' } /*禁用*/)
              }
            />
          ),
        },
      ],
      pagination: {
        total: 0,
      },
      page: 0,
      pageSize: 10,
      data: [],
      showSlideFrame: false,
      showUpdateSlideFrame: false,
    };
  }

  componentWillMount() {
    this.setState(
      {
        organizationInfo: this.props.organization,
        newParams: {
          organizationName: this.props.organization.organizationName,
          organizationId: this.props.organization.id,
        },
      },
      () => {
        this.getList();
      }
    );
  }

  getList() {
    let { page, pageSize, organizationInfo, searchParams } = this.state;
    let url = `${
      config.budgetUrl
    }/api/budget/scenarios/query?page=${page}&size=${pageSize}&organizationId=${
      organizationInfo.id
    }`;
    for (let paramsName in searchParams) {
      url += searchParams[paramsName] ? `&${paramsName}=${searchParams[paramsName]}` : '';
    }
    this.setState({ loading: true });
    organizationInfo.id &&
      httpFetch
        .get(url)
        .then(res => {
          if (res.status === 200) {
            res.data.map((item, index) => {
              item.index = this.state.page * this.state.pageSize + index + 1;
              item.key = item.index;
            });
            this.setState({
              data: res.data,
              loading: false,
              pagination: {
                total: Number(res.headers['x-total-count'])
                  ? Number(res.headers['x-total-count'])
                  : 0,
                onChange: this.onChangePager,
                current: page + 1,
              },
            });
          }
        })
        .catch(() => {
          this.setState({ loading: false });
          message.error(
            this.$t({ id: 'common.error' } /*哦呼，服务器出了点问题，请联系管理员或稍后再试:(*/)
          );
        });
  }

  //分页点击
  onChangePager = page => {
    if (page - 1 !== this.state.page)
      this.setState(
        {
          page: page - 1,
        },
        () => {
          this.getList();
        }
      );
  };

  //搜索
  search = result => {
    let searchParams = {
      scenarioCode: result.scenarioCode,
      scenarioName: result.scenarioName,
    };
    this.setState(
      {
        searchParams: searchParams,
        page: 0,
        pagination: {
          current: 1,
        },
      },
      () => {
        this.getList();
      }
    );
  };

  //清空搜索区域
  clear = () => {
    this.setState({
      searchParams: {
        scenarioCode: '',
        scenarioName: '',
      },
    });
  };

  showSlide = flag => {
    this.setState({
      showSlideFrame: flag,
    });
  };

  showUpdateSlide = flag => {
    this.setState({
      showUpdateSlideFrame: flag,
    });
  };

  handleCloseSlide = params => {
    this.setState(
      {
        showSlideFrame: false,
      },
      () => {
        params && this.getList();
      }
    );
  };
  handleCloseUpdateSlide = params => {
    this.setState(
      {
        showUpdateSlideFrame: false,
      },
      () => {
        params && this.getList();
      }
    );
  };

  handleRowClick = record => {
    record.organizationName = this.state.organizationInfo.organizationName;
    record.organizationId = this.state.organizationInfo.id;
    this.setState(
      {
        updateParams: record,
      },
      () => {
        this.showUpdateSlide(true);
      }
    );
  };

  render() {
    const {
      searchForm,
      columns,
      pagination,
      loading,
      data,
      showSlideFrame,
      showUpdateSlideFrame,
      updateParams,
      newParams,
    } = this.state;
    return (
      <div className="budget-scenarios">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          eventHandle={this.searchEventHandle}
        />
        <div className="table-header">
          <div className="table-header-title">
            {this.$t(
              { id: 'common.total' },
              { total: `${pagination.total || 0}` } /*共搜索到 {total} 条数据*/
            )}
          </div>
          <div className="table-header-buttons">
            <Button type="primary" onClick={() => this.showSlide(true)}>
              {this.$t({ id: 'common.create' } /*新建*/)}
            </Button>
          </div>
        </div>
        <Table
          columns={columns}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          onRow={record => ({
            onClick: () => this.handleRowClick(record),
          })}
          bordered
          size="middle"
        />

        <SlideFrame
          title={this.$t({ id: 'budget.scenarios.new' } /*新建预算场景*/)}
          show={showSlideFrame}
          onClose={() => this.showSlide(false)}
        >
          <NewBudgetScenarios
            onClose={this.handleCloseSlide}
            params={{ ...newParams, flag: showSlideFrame }}
          />
        </SlideFrame>
        <SlideFrame
          title={this.$t({ id: 'budget.scenarios.edit' } /*编辑预算场景*/)}
          show={showUpdateSlideFrame}
          onClose={() => this.showUpdateSlide(false)}
        >
          <UpdateBudgetScenarios
            onClose={this.handleCloseUpdateSlide}
            params={{ ...updateParams, flag: showUpdateSlideFrame }}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps() {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BudgetScenarios);
