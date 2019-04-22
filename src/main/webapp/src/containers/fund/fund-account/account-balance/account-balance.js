import React from 'react';
import Table from 'widget/table';
import { connect } from 'dva';
import { Form, Row, Col } from 'antd';
import SlideFrame from 'widget/slide-frame';
import FundSearchForm from '../../fund-components/fund-search-form';
import AccountHistoryBalance from './account-history-balance';
import accountBalanceService from './account-balance-service';

class AccountBalance extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // loading状态
      slideVisible: false,
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
      },
      itemLayout: {
        labelCol: { span: 6 },
        wrapperCol: { span: 18 },
      },
      searchForm: [
        {
          type: 'input',
          label: '银行账号',
          id: 'accountNumber',
        },
        {
          type: 'input',
          label: '账户户名',
          id: 'accountName',
        },
        {
          type: 'modalList',
          label: '开户公司',
          id: 'accountOpeningCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        {
          type: 'valueList',
          label: '开户银行',
          id: 'openBank',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        // 状态
        {
          type: 'valueList',
          label: '存款类型',
          id: 'depositType',
          options: [],
          valueListCode: 'ZJ_ACCOUNT_DEP_TYPE',
        },
      ],
      columns: [
        {
          title: '银行账号',
          dataIndex: 'accountNumber',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: '账户户名',
          dataIndex: 'accountName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '存款类型',
          dataIndex: 'accountDepositTypeName',
          align: 'center',
          width: 130,
        },
        {
          title: '所属公司',
          dataIndex: 'companyName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: '开户银行',
          dataIndex: 'openBankName',
          align: 'center',
          width: 90,
          tooltips: true,
        },
        {
          title: '账户余额',
          dataIndex: 'balanceAmount',
          align: 'center',
          width: 100,
          render: value => this.formatMoney(value),
        },
        {
          title: '最近更新时间',
          dataIndex: 'lastUpdate',
          align: 'center',
          width: 150,
        },
        {
          title: '账户用途',
          dataIndex: 'accountUseName',
          align: 'center',
          width: 100,
        },
        {
          title: '历史余额',
          align: 'center',
          width: 100,
          render: record => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.handleClose(record);
              }}
            >
              查看
            </a>
          ),
        },
      ],
      searchParams: {},
    };
  }

  componentDidMount() {
    this.getList();
    this.renderFundDeposit();
    this.renderDepositType();
  }

  // 获取列表
  getList = () => {
    const { searchParams, pagination } = this.state;
    this.setState({ loading: true });
    accountBalanceService
      .getAccountBalanceList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            current: pagination.page + 1,
            pageSize: pagination.pageSize,
            onChange: this.onChangePager,
            onShowSizeChange: this.onShowSizeChange,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              this.$t('common.show.total', { range0: `${range[0]}`, range1: `${range[1]}`, total }),
          },
        });
      });
  };

  /**
   * 分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 改变每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    const temp = {};
    temp.page = current - 1;
    temp.pageSize = pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 搜索
   */
  handleSearch = values => {
    const { searchParams } = this.state;
    const params = values;
    this.setState(
      {
        searchParams: {
          ...searchParams,
          accountNumber: params.accountNumber ? params.accountNumber : '',
          accountName: params.accountName ? params.accountName : '',
          corpId: params.accountOpeningCompany ? params.accountOpeningCompany[0].id : '',
          openBank: params.openBank ? params.openBank.key : '',
          accountDepositType: params.depositType ? params.depositType.key : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 渲染柱状图
   */
  renderFundDeposit = () => {
    // 资金存款分布
    accountBalanceService.getCapitalDistribution().then(res => {
      const { data } = res;
      this.setState(
        {
          capitalDistribution: data,
        },
        () => {
          const dom = document.getElementById('fundDeposit');
          const { capitalDistribution } = this.state;
          const capitalopenBankNameArray = [];
          const capitalAmountArray = [];
          capitalDistribution.forEach(item => {
            capitalopenBankNameArray.push(item.openBankName);
            capitalAmountArray.push(item.amount);
          });
          // eslint-disable-next-line no-undef
          const myChart = echarts.init(dom, 'macarons');
          const option = {
            title: {
              text: '资金存款分布',
            },
            tooltip: {
              trigger: 'item',
              axisPointer: {
                type: 'shadow',
              },
            },
            grid: {
              left: '0%',
              right: '0%',
              bottom: '10%',
              top: '16%',
              containLabel: true,
            },
            xAxis: {
              type: 'value',
              boundaryGap: [0, 1],
              axisLabel: {
                formatter: '{value} 万元',
                rotate: '-20',
              },
            },
            yAxis: {
              type: 'category',
              // data: ['中国银行', '中国农业银行', '中国建设银行'],
              data: capitalopenBankNameArray,
            },
            color: ['#1890FF', '#13C2C2', '#2FC25B'],
            series: [
              {
                type: 'bar',
                barWidth: '30px',
                // data: [5, 20, 30],
                data: capitalAmountArray,
              },
            ],
          };
          myChart.setOption(option, true);
        }
      );
    });
  };

  /**
   * 渲染饼状图
   */
  renderDepositType = () => {
    // 存款类型分布
    accountBalanceService.getCapitalType().then(res => {
      const { data } = res;
      this.setState(
        {
          accountType: data,
        },
        () => {
          const dom = document.getElementById('depositType');
          // eslint-disable-next-line no-undef
          const myChart = echarts.init(dom, 'macarons');
          const { accountType } = this.state;
          const accountTypeStatus = []; // 存款类型分布的状态
          const accountTypeArray = []; // 存款类型分布的数组键值对
          accountType.forEach(item => {
            accountTypeStatus.push(item.accountDepositTypeDesc);
            // let itemValue = item.accountDepositTypeDesc;
            // if(itemValue === null) {
            //   itemValue = 0
            //   accountTypeArray.push({value: itemValue, name: item.number});
            // }
            accountTypeArray.push({ value: item.number, name: item.accountDepositTypeDesc });
          });
          const option = {
            title: {
              text: '存款类型分布',
              x: 'right',
            },
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b} : {c} ({d}%)',
            },
            legend: {
              orient: 'vertical',
              left: 'left',
              // data: ['正常', '变更', '冻结', '销户'],
              data: accountTypeStatus,
            },
            color: [
              '#1890FF',
              '#13C2C2',
              '#2FC25B',
              '#FACC14',
              '#F04864',
              '#8543E0',
              '#3436C7',
              '#223273',
            ],
            series: [
              {
                name: '账户状态',
                type: 'pie',
                radius: '55%',
                center: ['50%', '60%'],
                data: accountTypeArray,
                // data: [
                //   { value: 335, name: '正常' },
                //   { value: 310, name: '变更' },
                //   { value: 234, name: '冻结' },
                //   { value: 135, name: '销户' },
                // ],
                itemStyle: {
                  emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)',
                  },
                },
              },
            ],
          };
          myChart.setOption(option, true);
        }
      );
    });
  };

  handleClose = value => {
    const { slideVisible } = this.state;
    this.setState({
      slideVisible: !slideVisible,
      accountHistoryBalance: value,
    });
  };

  render() {
    const {
      searchForm,
      columns,
      pagination,
      loading,
      tableData,
      slideVisible,
      accountHistoryBalance,
      itemLayout,
    } = this.state;
    const salesData = [];
    for (let i = 0; i < 12; i += 1) {
      salesData.push({
        x: `${i + 1}月`,
        y: Math.floor(Math.random() * 1000) + 200,
      });
    }
    return (
      <div>
        <Row type="flex" style={{ paddingBottom: '30px' }}>
          <Col xs={{ span: 5 }} lg={{ span: 7 }} style={{ background: '#f0f2f5', padding: '10px' }}>
            <div style={{ height: '100%', width: '100%' }} id="fundDeposit" />
          </Col>
          <Col
            xs={{ span: 11, offset: 1 }}
            lg={{ span: 7, offset: 1 }}
            style={{ background: '#f0f2f5', padding: '10px' }}
          >
            <div style={{ height: '100%', width: '100%' }} id="depositType" />
          </Col>
          <Col
            xs={{ span: 6, offset: 1 }}
            lg={{ span: 8, offset: 1 }}
            style={{ background: '#f0f2f5', padding: '10px' }}
          >
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              itemLayout={itemLayout}
            />
          </Col>
        </Row>

        <Table
          rowKey={record => record.id}
          columns={columns}
          dataSource={tableData}
          pagination={pagination}
          loading={loading}
          onChange={this.onChangePager}
          onRowClick={this.handleRowClick}
          bordered
          size="middle"
        />
        <SlideFrame
          title="历史余额"
          width="70vw"
          show={slideVisible}
          onClose={() => this.handleClose('')}
        >
          <AccountHistoryBalance accountHistoryBalance={accountHistoryBalance} />
        </SlideFrame>
      </div>
    );
  }
}
// export default AccountBalance;

function accountBalance(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(accountBalance)(Form.create()(AccountBalance));
