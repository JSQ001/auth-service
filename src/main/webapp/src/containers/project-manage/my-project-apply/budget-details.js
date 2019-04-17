import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import config from 'config';
import { Button, Popover, Card, Affix } from 'antd';

const bottomStyle = {
  position: 'fixed',
  bottom: 0,
  width: '100%',
  height: '50px',
  boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.15)',
  background: '#fff',
  lineHeight: '50px',
  paddingLeft: '20px',
  zIndex: 1,
};

class BudgetDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          label: '预算日记账编号',
          type: 'input',
          id: 'journalCode',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'journalTypeId',
          label: '预算日记账类型',
          options: [],
          method: 'get',
          getUrl: `${config.budgetUrl}/api/budget/journals/journalType/selectByInput`,
          getParams: { organizationId: props.organization.id },
          labelKey: 'journalTypeName',
          valueKey: 'id',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'applyDate',
          items: [
            { type: 'date', id: 'applyDateFrom', label: '申请日期从' },
            { type: 'date', id: 'applyDateTo', label: '申请日期至' },
          ],
          colSpan: 6,
        },
        {
          type: 'value_list',
          label: '编制期段',
          id: 'periodStrategy',
          options: [],
          valueListCode: 2002,
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '预算日记账编号',
          dataIndex: 'journalCode',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '行号',
          dataIndex: 'lineCode',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('contract.amount'), // '金额'
          dataIndex: 'amount',
          align: 'center',
          width: 100,
          render: amount => (
            <span>
              <Popover content={<span>{this.filterMoney(amount, 2)}</span>}>
                {this.filterMoney(amount, 2)}
              </Popover>
            </span>
          ),
        },
        {
          title: '币种',
          dataIndex: 'currencyCode',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: '预算日记账类型',
          dataIndex: 'journalTypeName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '申请日期',
          dataIndex: 'applyDate',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '预算表',
          dataIndex: 'structureName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '编制期段',
          dataIndex: 'periodStrategyName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '预算场景',
          dataIndex: 'scenarioName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '预算版本',
          dataIndex: 'versionName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
      ],
      searchParams: {},
    };
  }

  // 搜索域搜索
  handleSearch = values => {
    console.log(values);
    this.setState({ searchParams: { ...values } }, () => {
      const { searchParams } = this.state;
      this.table.search(searchParams);
    });
  };

  // 重置搜索
  handleClear = () => {
    this.setState({ searchParams: {} }, () => {
      const { searchParams } = this.state;
      this.table.search(searchParams);
    });
  };

  handleReturn = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/project-manage/my-project-apply/my-project-apply`,
      })
    );
  };

  render() {
    const { searchForm, columns } = this.state;
    const { readOnly } = this.props;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.handleClear}
        />
        <Card style={{ marginBottom: '50px' }}>
          <div style={{ margin: '10px 0' }}>
            <span>汇总 | 金额:</span>
          </div>
          {/*
            接口如果是需要根据申请单id查询的话，则此时判断是否有readOnly
            readOnly ? props.id : props.match.params.id
            前者是放在模态框内显示组件，后者是通过跳转页面
          */}
          <CustomTable
            columns={columns}
            ref={ref => {
              this.table = ref;
            }}
            url={`${config.budgetUrl}/api/budget/journals/finance/query`}
            scroll={{ x: 1300 }}
          />
        </Card>
        {readOnly ? (
          <div />
        ) : (
          <Affix offsetBottom={0} className="bottom-bar bottom-bar-approve" style={bottomStyle}>
            <Button onClick={this.handleReturn}>返回</Button>
          </Affix>
        )}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    organization: state.user.organization || {},
  };
}

export default connect(mapStateToProps)(BudgetDetail);
