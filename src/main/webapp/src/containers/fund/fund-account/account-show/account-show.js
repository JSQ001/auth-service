import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import { Form, Row, Col } from 'antd';
import moment from 'moment';
import accountShowService from './account-show.service';
import FundSearchForm from '../../fund-components/fund-search-form';
import 'styles/fund/account.scss';

class AccountShow extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      searchParams: {},
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
          label: '状态',
          id: 'accountStatus',
          options: [],
          valueListCode: 'ZJ_ACCOUNT_STATUS',
        },
      ],
      columns: [
        {
          title: '银行账号',
          dataIndex: 'accountNumber',
          width: 100,
          align: 'center',
          render: (accountNumber, record) => (
            <a
              onClick={event => {
                event.stopPropagation();
                this.goDetail(record.id);
              }}
            >
              {accountNumber}
            </a>
          ),
        },
        {
          title: '账号户名',
          dataIndex: 'accountName',
          width: 100,
          align: 'center',
        },
        {
          title: '账户状态',
          dataIndex: 'statusDesc',
          width: 100,
          align: 'center',
        },
        {
          title: '开户公司',
          dataIndex: 'companyName',
          width: 100,
          align: 'center',
        },
        {
          title: '开户银行',
          dataIndex: 'openBankName',
          width: 100,
          align: 'center',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          width: 100,
          align: 'center',
        },
        {
          title: '直联状态',
          dataIndex: 'directFlagDes',
          width: 100,
          align: 'center',
        },
        {
          title: '账户用途',
          dataIndex: 'accountUseName',
          width: 100,
          align: 'center',
        },
        {
          title: '最近使用时间',
          dataIndex: 'openDate',
          width: 100,
          align: 'center',
          render: record => {
            return moment(record).format('YYYY-MM-DD');
          },
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
    this.renderAccountDistribution();
    this.renderAccountStatus();
  }

  /**
   * 列表数据
   */
  getList() {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    accountShowService
      .accountInformationList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        data.map(item => {
          const copyItem = item;
          if (copyItem.directFlag) {
            copyItem.directFlagDes = '直联';
          } else {
            copyItem.directFlagDes = '不直联';
          }
          return copyItem;
        });
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
    // .catch(err => {
    //   message.error(err);
    // });
  }

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
   * 选择公司
   */
  selectCompany = () => {
    const { modalVisible } = this.state;
    this.setState({
      modalVisible: !modalVisible,
    });
    this.getCompanyListByBooksId();
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
          companyId: params.accountOpeningCompany ? params.accountOpeningCompany[0].id : '',
          openBank: params.openBank ? params.openBank.key : '',
          accountStatus: params.accountStatus ? params.accountStatus.key : '',
        },
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 跳转详情
   */
  goDetail = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/account-manage/account-show/account-show-detail/${id}`,
      })
    );
  };

  /**
   * 渲染柱状图
   */
  renderAccountDistribution = () => {
    // 获取银行预览分布一览的报表数据
    accountShowService.getBankRenderAccountStatus().then(res => {
      const { data } = res;
      this.setState(
        {
          bankData: data,
        },
        () => {
          const dom = document.getElementById('accountDistribution');
          // eslint-disable-next-line no-undef
          const myChart = echarts.init(dom, 'macarons');
          const { bankData } = this.state;
          const openBankNameArray = []; // 银行数组
          const bankNumberArray = []; // 银行number数组
          bankData.forEach(item => {
            openBankNameArray.push(item.openBankName);
            bankNumberArray.push(item.number);
          });
          const option = {
            title: {
              text: '银行账户分布一览',
            },
            tooltip: {
              trigger: 'item',
              axisPointer: {
                type: 'shadow',
              },
            },
            grid: {
              left: '0%',
              right: '10%',
              bottom: '0%',
              top: '16%',
              containLabel: true,
            },
            xAxis: {
              name: '个',
              type: 'value',
              boundaryGap: [0, 1],
            },
            yAxis: {
              type: 'category',
              // data: ['中国银行', '中国农业银行', '中国建设银行'],
              data: openBankNameArray,
            },
            color: ['#1890FF', '#13C2C2', '#2FC25B'],
            series: [
              {
                type: 'bar',
                barWidth: '30px',
                // data: [5, 20, 30],
                data: bankNumberArray,
              },
            ],
          };
          myChart.setOption(option, true);
        }
      );
    });
  };

  renderAccountStatus = () => {
    // 获取银行预览状态一览的报表数据
    accountShowService.getBankStatusRenderAccountStatus().then(res => {
      const { data } = res;
      this.setState(
        {
          statusData: data,
        },
        () => {
          const dom = document.getElementById('accountStatus');
          // eslint-disable-next-line no-undef
          const myChart = echarts.init(dom, 'macarons');
          const { statusData } = this.state;
          const statusArray = [];
          const legendStatusArray = [];
          statusData.forEach(item => {
            statusArray.push({ value: item.number, name: item.statusDesc });
            legendStatusArray.push(item.statusDesc);
          });
          const option = {
            title: {
              text: '银行账户状态一览',
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
              data: legendStatusArray,
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
                data: statusArray,
                // data: [
                //   // { value: 335, name: '正常' },
                //   // { value: 310, name: '变更' },
                //   // { value: 234, name: '冻结' },
                //   // { value: 135, name: '销户' },
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

  render() {
    const { searchForm, columns, pagination, loading, tableData, itemLayout } = this.state;
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
            <div style={{ height: '100%', width: '100%' }} id="accountDistribution" />
          </Col>
          <Col
            xs={{ span: 11, offset: 1 }}
            lg={{ span: 7, offset: 1 }}
            style={{ background: '#f0f2f5', padding: '10px' }}
          >
            <div style={{ height: '100%', width: '100%' }} id="accountStatus" />
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
        {/* 列表 */}
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
      </div>
    );
  }
}

function show(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(show)(Form.create()(AccountShow));
