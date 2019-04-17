/**
 * Created by fudebao on 2018/12/04.
 */
import React from 'react';
import { connect } from 'dva';
import {
  Row,
  Col,
  Card,
  Carousel,
  Tabs,
  DatePicker,
  Tag,
  Icon,
  message,
  Modal,
  Radio,
  Badge,
  Button,
  Divider,
} from 'antd';
import 'styles/dashboard.scss';
import moment from 'moment';
import userImage from 'images/user1.png';
import FileSaver from 'file-saver';

import service from './dashboard.service';
// // 引入 ECharts 主模块
// import echarts from 'echarts/lib/echarts';
// // 引入环图
// import 'echarts/lib/chart/pie';
// // 引入柱状图
// import 'echarts/lib/chart/bar';
// // 引入提示框和标题组件
// import 'echarts/lib/component/tooltip';
// import 'echarts/lib/component/title';
// import 'echarts/theme/macarons'

import { routerRedux } from 'dva/router';

const TabPane = Tabs.TabPane;
const { MonthPicker } = DatePicker;
const RadioGroup = Radio.Group;

class Dashboard extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      timerStr: '',
      height: 0,
      backList: [],
      hello: '',
      chartsType: 1, //1.饼状图   2.折线图
      timeType: 3, //1.本月 2.本季 3.全年
      startTime: null,
      endTime: null,
      carousels: [], //公告信息
      unApprovals: [],
      totol: 0,
      expenseTrendData: {}, //费用趋势数据
      expenseProportionData: [], //费用占比数据
      payInfoData: {}, //付款信息数据
      doingList: [],
      payType: 801003, //报账：801001;  预付款：801003
      tabKey: '1',
      expenseTrendFlag: true, //进入费用趋势选项卡是否需要重新请求数据
      expenseProportionFlag: true, //进入费用占比选项卡是否需要重新请求数据
      payInfoFlag: true, //进入付款状态选项卡是否需要重新请求数据
      unFinishLoading: false, // 未完成单据loading
      backLoading: false, // 退回单据loading
      unFinishPage: 0,
      backPage: 0,
      unFinishTotal: 0,
      backTotal: 0,
      loadMoreBackBtn: false,
      loadMoreUnFinishBtn: false,
    };
  }

  componentDidMount() {
    this.getCurrentDate();
    this.getCarousels();
    this.getUnApprovals();
    this.getBackDocument();
    this.getDoingDocument();

    let year = moment().year();
    this.setState(
      { startTime: moment(year + '-' + '01'), endTime: moment(year + '-' + '12') },
      () => {
        this.renderReport(year + '-' + '01', year + '-' + '12');
      }
    );
  }

  //时间类型变化
  changeTimeType = type => {
    this.setState({ timeType: type }, () => {
      if (type == 1) {
        let start = moment().format('YYYY-MM');
        this.setState({ startTime: moment(start), endTime: moment(start) });
        this.renderReport(start, start);
      } else if (type == 2) {
        let month = moment().month() + 1;

        let tmp = Math.ceil(month / 3);

        let year = moment().year();

        let end = year + '-' + tmp * 3;
        let start = year + '-' + (tmp * 3 - 2);

        this.setState({
          startTime: moment(start),
          endTime: moment(end),
        });
        this.renderReport(start, end);

        //为了处理2018-9这种情况，后台不支持2018-9，所以转成2018-09
        // this.renderExpenseTrend(moment(start).format("YYYY-MM"), moment(end).format("YYYY-MM"));
      } else if (type == 3) {
        let year = moment().year();
        this.setState({ startTime: moment(year + '-' + '01'), endTime: moment(year + '-' + '12') });
        this.renderReport(year + '-' + '01', year + '-' + '12');
      }
    });
  };

  //渲染待审批单据
  renderPie = (data = []) => {
    var dom = document.getElementById('pie');
    var myChart = echarts.init(dom, 'macarons');

    let option = {
      tooltip: {
        trigger: 'item',
        formatter: '{b} : {c}笔 ({d}%)',
      },
      color: [
        'rgba(255,99,132,1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(255, 159, 64, 1)',
      ],
      series: [
        {
          type: 'pie',
          radius: ['40%', '60%'],
          avoidLabelOverlap: false,
          label: {
            show: true,
            formatter: '{b}: {c}笔',
          },
          data: data.map(o => ({
            name: o.name,
            value: o.count,
            type: o.type,
          })),
        },
      ],
    };
    myChart.on('click', 'series.pie', params => {
      const { dispatch } = this.props;
      dispatch(
        routerRedux.push({
          pathname: `/approval-management/todo-list/todo-list/to-do/${params.data.type}`,
        })
      );
    });
    myChart.setOption(option, true);
  };

  //渲染费用趋势
  renderExpenseTrend = (startDate, endDate) => {
    let { expenseTrendFlag } = this.state;

    if (!expenseTrendFlag) return;

    service.getExpenceTrend(startDate, endDate).then(res => {
      this.setState(
        {
          expenseTrendData: res.data,
          expenseTrendFlag: false,
        },
        () => {
          if (this.state.chartsType == 1) {
            this.renderExpenseTrendBar();
          } else {
            this.renderExpenseTrendLine();
          }
        }
      );
    });
  };

  //渲染费用趋势柱状图
  renderExpenseTrendBar = () => {
    let { expenseTrendData } = this.state;

    var dom = document.getElementById('expanseTrend');
    var myChart = echarts.init(dom, 'macarons');

    let names = [];
    let values = expenseTrendData.list.map(item => {
      names.push(item.date);
      return {
        value: item.value,
        tooltip: {
          formatter: params => {
            return this.filterMoney(params.value, 2, true);
          },
        },
      };
    });

    let option = {
      color: ['#1890ff'],
      tooltip: {
        trigger: 'item',
        axisPointer: {
          type: 'shadow',
        },
      },
      grid: {
        left: '0%',
        right: '0%',
        bottom: '0%',
        top: '16%',
        containLabel: true,
      },
      xAxis: [
        {
          type: 'category',
          data: names,
          axisTick: {
            alignWithLabel: true,
          },
        },
      ],
      yAxis: [
        {
          type: 'value',
        },
      ],
      series: [
        {
          name: '金额',
          type: 'bar',
          barWidth: '20px',
          data: values,
          markPoint: {
            data: [{ type: 'max', name: '最大值' }, { type: 'min', name: '最小值' }],
          },
          markLine: {
            data: [{ type: 'average', name: '平均值' }],
          },
        },
      ],
    };

    myChart.setOption(option, true);

    this.setState({ chartsType: 1 });
  };

  //渲染费用趋势折线图
  renderExpenseTrendLine = () => {
    let { expenseTrendData } = this.state;

    var dom = document.getElementById('expanseTrend');
    var myChart = echarts.init(dom, 'macarons');

    let names = [];
    let values = expenseTrendData.list.map(item => {
      names.push(item.date);
      return {
        value: item.value,
        tooltip: {
          formatter: params => {
            return this.filterMoney(params.value, 2, true);
          },
        },
      };
    });

    let option = {
      color: ['#1890ff'],
      xAxis: {
        type: 'category',
        data: names,
        axisTick: {
          alignWithLabel: true,
        },
      },
      yAxis: {
        type: 'value',
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
        bottom: '0%',
        top: '16%',
        containLabel: true,
      },
      series: [
        {
          name: '金额',
          data: values,
          type: 'line',
          markPoint: {
            data: [{ type: 'max', name: '最大值' }, { type: 'min', name: '最小值' }],
          },
          markLine: {
            data: [{ type: 'average', name: '平均值' }],
          },
        },
      ],
    };

    myChart.setOption(option, true);

    this.setState({ chartsType: 2 });
  };

  loadMoreunFinish = () => {
    const { unFinishPage } = this.state;
    this.setState({ unFinishPage: unFinishPage + 1 }, this.getDoingDocument);
  };

  getDoingDocument = () => {
    const { unFinishPage, doingList } = this.state;
    this.setState({ unFinishLoading: true });
    service
      .getDoingDocument({ page: unFinishPage, size: 10 })
      .then(res => {
        let loadMoreUnFinishBtn = true;
        if (res.data.length < 10) {
          loadMoreUnFinishBtn = false;
        }
        this.setState({
          doingList: [...doingList, ...res.data],
          unFinishLoading: false,
          unFinishTotal: Number(res.headers['x-total-count']),
          loadMoreUnFinishBtn,
        });
      })
      .catch(err => {
        message.error('获取数据失败,请稍后重试！');
      });
  };

  // 获取更多退回单据
  loadMoreBack = () => {
    const { backPage } = this.state;
    this.setState({ backPage: backPage + 1 }, this.getBackDocument);
  };

  getBackDocument = () => {
    const { backPage, backList } = this.state;
    this.setState({ backLoading: true });
    service
      .getBackDocument({ page: backPage, size: 10 })
      .then(res => {
        let loadMoreBackBtn = true;
        if (res.data.length < 10) {
          loadMoreBackBtn = false;
        }
        this.setState({
          backList: [...backList, ...res.data],
          backLoading: false,
          backTotal: Number(res.headers['x-total-count']),
          loadMoreBackBtn,
        });
      })
      .catch(err => {
        message.error('获取数据失败,请稍后重试！');
      });
  };

  getUnApprovals = () => {
    service
      .getUnApprovals()
      .then(res => {
        if (!!res.data.totalCount) {
          this.setState({
            total: res.data.totalCount,
            unApprovals: res.data.approvalDashboardDetailDTOList,
          });
          this.renderPie(res.data.approvalDashboardDetailDTOList);
        }
      })
      .catch(err => {
        message.error('获取待审批列表失败,请稍后重试！');
      });
  };

  getCarousels = () => {
    service.getCarouselsByCompany(this.props.company.companyOid).then(res => {
      this.setState({ carousels: res.data });
    });
  };

  //结束时间改变
  endTimeChange = value => {
    if (!value) return;

    this.setState(
      {
        endTime: value,
        timeType: 0,
      },
      () => {
        if (this.state.startTime) {
          this.renderReport(
            this.state.startTime.format('YYYY-MM'),
            this.state.endTime.format('YYYY-MM')
          );
          this.formatTimeType();
        }
      }
    );
  };

  //开始时间改变
  startTimeChange = value => {
    if (!value) return;

    this.setState(
      {
        startTime: value,
        timeType: 0,
      },
      () => {
        if (this.state.endTime) {
          this.renderReport(
            this.state.startTime.format('YYYY-MM'),
            this.state.endTime.format('YYYY-MM')
          );
          this.formatTimeType();
        }
      }
    );
  };

  //当时间改变，根据当前选项卡，重新渲染当前选项卡,并把别的选项卡设置为重新请求
  renderReport = (startTime, endTime) => {
    let { tabKey } = this.state;
    if (tabKey == 1) {
      this.setState(
        { expenseProportionFlag: true, expenseTrendFlag: true, payInfoFlag: true },
        () => {
          this.renderExpenseTrend(startTime, endTime);
        }
      );
    } else if (tabKey == 2) {
      this.setState(
        { expenseProportionFlag: true, expenseTrendFlag: true, payInfoFlag: true },
        () => {
          this.getExpenseProportion(startTime, endTime);
        }
      );
    } else if (tabKey == 3) {
      this.setState(
        { expenseProportionFlag: true, expenseTrendFlag: true, payInfoFlag: true },
        () => {
          this.getPayInfo(startTime, endTime);
        }
      );
    }
  };

  //判断时间是什么类型 本月 本季 本年
  formatTimeType = () => {
    let year = moment().year();

    let { startTime, endTime } = this.state;

    if (startTime.year() != year || endTime.year() != year) return;

    let month = moment().month() + 1;

    if (startTime.month() + 1 == month && endTime.month() + 1 == month) {
      this.setState({ timeType: 1 });
      return;
    }

    let tmp = Math.ceil(month / 3);

    if (tmp * 3 == endTime.month() + 1 && tmp * 3 - 2 == startTime.month() + 1) {
      this.setState({ timeType: 2 });
      return;
    }

    if (startTime.month() == 0 && endTime.month() == 11) {
      this.setState({ timeType: 3 });
    }
  };

  //可选开始日期
  startDisabledDate = current => {
    const { endTime } = this.state;

    if (!endTime) return false;

    let end = endTime.format('YYYY-MM');

    if (current.isSame(moment(end))) return false;

    return !(
      current &&
      moment(end)
        .subtract(12, 'M')
        .isBefore(current) &&
      current.isBefore(moment(end))
    );
  };

  //可选结束日期
  endDisabledDate = current => {
    const { startTime } = this.state;

    if (!startTime) return false;

    let start = startTime.format('YYYY-MM');

    if (current.isSame(moment(start))) return false;

    return !(
      current &&
      current.isBefore(moment(start).add(12, 'M')) &&
      moment(start).isBefore(current, 'month')
    );
  };

  //渲染日期组件
  renderDate = () => {
    const { timeType, startTime, endTime } = this.state;

    return (
      <div>
        <span
          onClick={() => {
            this.changeTimeType(1);
          }}
          style={{ marginRight: 20, cursor: 'pointer', color: timeType == 1 ? '#1890ff' : '#000' }}
        >
          本月
        </span>
        <span
          onClick={() => {
            this.changeTimeType(2);
          }}
          style={{ marginRight: 20, cursor: 'pointer', color: timeType == 2 ? '#1890ff' : '#000' }}
        >
          本季
        </span>
        <span
          onClick={() => {
            this.changeTimeType(3);
          }}
          style={{ marginRight: 20, cursor: 'pointer', color: timeType == 3 ? '#1890ff' : '#000' }}
        >
          全年
        </span>
        <MonthPicker
          style={{ marginRight: 20 }}
          placeholder="开始日期"
          value={startTime}
          onChange={this.startTimeChange}
          disabledDate={this.startDisabledDate}
          allowClear={false}
        />
        <MonthPicker
          placeholder="结束日期"
          value={endTime}
          disabledDate={this.endDisabledDate}
          allowClear={false}
          onChange={this.endTimeChange}
        />
      </div>
    );
  };

  //导出图片
  downloadImage = () => {
    var ctx = document.querySelector('.data canvas');
    var dataURL = ctx.toDataURL('image/png', 1.0);

    FileSaver.saveAs(dataURL, '费用趋势.png');
  };

  //获取当前时间
  getCurrentDate() {
    let date = new Date();

    let hours = date.getHours();

    let hello = '';

    if (hours >= 6 && hours <= 11) {
      hello = '早上好';
    } else if (hours == 12) {
      hello = '中午好';
    } else if (hours > 12 && hours < 18) {
      hello = '下午好';
    } else {
      hello = '晚上好';
    }

    let time = moment(date).format('YYYY-MM-DD dddd');

    this.setState({ timerStr: time, hello });
  }

  goCarouselDetail = item => {
    if (!!item.carouselOid) {
      if (item.outLinkUrl) {
        window.open(item.outLinkUrl, '_blank');
      } else {
        service.getCatouselsContent(item.carouselOid).then(res => {
          if (res.status === 200) {
            item.content = res.data.content;
            item.localDate = moment(res.data.createdDate).format('YYYY-MM-DD');
            Modal.info({
              title: item.title,
              content: (
                <div className="carousel-modal">
                  <p>{item.localDate}</p>
                  <div dangerouslySetInnerHTML={{ __html: item.content }} />
                </div>
              ),
            });
          }
        });
      }
    }
  };

  // 跳转到详情
  click = record => {
    let path = '';
    switch (record.documentCategory) {
      case 801009: //费用申请单
        path =
          '/expense-application/expense-application/expense-application-detail/' +
          record.documentId;
        break;
      case 801003: //预付款单
        path = `/pre-payment/my-pre-payment/pre-payment-detail/${record.documentId}/prePayment`;
        break;
      case 801002: //预算日记账
        path = `/project-manage/my-project-apply/my-project-apply`;
        break;
      case 801008: //核算工单
        path = `/my-gl-work-order/my-gl-work-order-detail/${record.documentId}`;
        break;
      case 801011: //项目申请单
        path = `/project-manage/my-project-apply/project-apply-details/${record.documentId}`;
        break;
      case 801010: //差旅申请单
        path = '/travel-application/travel-application-detail/' + record.documentId;
        break;
      case 801004: //合同
        path = `/contract-manager/my-contract/contract-detail/${record.documentId}`;
        break;
      case 801001: //对公报账单
        path = '';
        break;
      case 801005: //付款申请单
        path = '/my-payment-requisition/payment-requisition-detail/:id'.replace(
          ':id',
          record.documentId
        );
        break;
      case 801006: //费用调整单
        path = '/my-expense-adjust/expense-adjust-detail/:id/:expenseAdjustTypeId/:type'
          .replace(':expenseAdjustTypeId', record.documentTypeId)
          .replace(':id', record.documentId)
          .replace(':type', record.documentCategory);
        break;
      case 901001: //开户申请
        path = '';
        break;
      case 901002: //开户维护
        path = `/account-manage/account-open-maintenance/account-open-maintenance-detail/${
          record.documentId
        }`;
        break;
      case 901003: //账户变更
        path = '';
        break;
      case 901004: //手工付款
        path = '';
        break;
      case 901005: //批量付款
        path = '';
        break;
      case 902001: //税务登记新增
        path = '';
        break;
      case 904001: //客户信息新增
        path =
          '/basic-tax-information-management/tax-client/tax-client-detail/' + record.documentId;
        break;
      default:
        message.error('未找到单据详情界面路径！');
    }

    this.props.dispatch(
      routerRedux.push({
        pathname: path,
      })
    );

    // const detailpathname = {
    //   801001: '/my-reimburse/reimburse-detail/',
    //   801002: '/budget/budget-journal/budget-journal-detail-submit/',
    //   801003: '/pre-payment/my-pre-payment/pre-payment-detail/:id/prePayment',
    //   801004: '/contract-manager/my-contract/contract-detail/',
    //   801005: '/payment-requisition/my-payment-requisition/payment-requisition-detail/',
    //   //801006: '/expense-adjust/my-expense-adjust/expense-adjust-detail/:id/:expenseAdjustTypeId/:type',
    //   //801007: '/financial-management/expense-reverse/new-reverse/${record.documentId}/${record.businessClass}/${this.state.isNew}/${record.currencyCode}',
    //   801008: '/my-gl-work-order/my-gl-work-order-detail/',
    //   801009: '/expense-application/expense-application/expense-application-detail/',
    // };
    // if (detailpathname[item.type]) {
    //   let pathname = detailpathname[item.type];
    //   if (pathname.indexOf(":id") >= 0) {
    //     pathname = pathname.replace(":id", item.id)
    //   } else {
    //     pathname = pathname + item.id;
    //   }
    //   this.props.dispatch(
    //     routerRedux.push({
    //       pathname,
    //     })
    //   );
    // } else {
    //   message.error('未找到单据详情界面路径！');
    // }
  };

  // 跳转到被退回的单据
  toBack = e => {
    e.stopPropagation();
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/approval-management/todo-list/todo-list/reject/0`,
      })
    );
  };

  // 跳转未完成单据
  toUnFinsh = e => {
    e.stopPropagation();
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/approval-management/todo-list/todo-list/un-finish/0`,
      })
    );
  };

  // 获取费用占比
  getExpenseProportion = (startTime, endTime) => {
    let { expenseProportionFlag } = this.state;

    if (!expenseProportionFlag) return;

    service.getExpenceRatio(startTime, endTime).then(res => {
      this.setState({ expenseProportionData: res.data, expenseProportionFlag: false }, () => {
        this.renderExpenseProportion(res.data);
      });
    });
  };

  //费用占比
  renderExpenseProportion = (data = []) => {
    var dom = document.getElementById('expenseProportion');
    var myChart = echarts.init(dom, 'macarons');

    let names = data.map(item => item.name);
    let option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)',
      },
      legend: {
        orient: 'vertical',
        x: 'right',
        y: 'top',
        data: names,
      },
      grid: {
        left: '0%',
        right: '0%',
        bottom: '0%',
        top: '4%',
        containLabel: true,
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
          name: '金额',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
          },
          data: data.map(item => ({ name: item.name, value: item.value })),
        },
      ],
    };
    myChart.setOption(option, true);
  };

  //获取付款信息
  getPayInfo = (startTime, endTime) => {
    let { payInfoData, payType, payInfoFlag } = this.state;

    if (!payInfoFlag) return;

    service.getPayInfo(startTime, endTime, payType).then(res => {
      this.setState({ payInfoData: res.data, payInfoFlag: false }, () => {
        this.renderPayInfo(res.data);
      });
    });
  };

  // 渲染付款信息
  renderPayInfo = (data = []) => {
    var dom = document.getElementById('payInfo');
    var myChart = echarts.init(dom, 'macarons');

    let option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)',
      },
      legend: {
        orient: 'vertical',
        x: 'right',
        y: 'top',
        data: ['已付款', '未付款'],
      },
      grid: {
        left: '0%',
        right: '0%',
        bottom: '0%',
        top: '4%',
        containLabel: true,
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
          name: '金额',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
          },
          data: [
            {
              name: '已付款',
              value: data.paid,
            },
            {
              name: '未付款',
              value: data.unpaid,
            },
          ],
        },
      ],
    };
    myChart.setOption(option, true);
  };

  // 付款方式切换
  payTypeChange = e => {
    this.setState({ payType: e.target.value, payInfoFlag: true }, () => {
      let { startTime, endTime } = this.state;
      this.getPayInfo(startTime.format('YYYY-MM'), endTime.format('YYYY-MM'));
    });
  };

  // 报表标签页切换
  reportChange = key => {
    let { startTime, endTime } = this.state;
    if (key == '2') {
      this.getExpenseProportion(startTime.format('YYYY-MM'), endTime.format('YYYY-MM'));
    } else if (key == '3') {
      this.getPayInfo(startTime.format('YYYY-MM'), endTime.format('YYYY-MM'));
    } else if (key == '1') {
      this.renderExpenseTrend(startTime.format('YYYY-MM'), endTime.format('YYYY-MM'));
    }
    this.setState({
      tabKey: key,
    });
  };

  render() {
    const {
      timerStr,
      backList,
      hello,
      chartsType,
      carousels,
      total,
      unFinishTotal,
      doingList,
      payType,
      tabKey,
      backTotal,
      backLoading,
      unFinishLoading,
      loadMoreBackBtn,
      loadMoreUnFinishBtn,
    } = this.state;
    const { user } = this.props;

    return (
      <div className="dashboard-container">
        <Row gutter={12}>
          <Col className="user-info" span={8}>
            <Card title="员工信息" extra={<span style={{ fontSize: 18 }}>{timerStr}</span>}>
              <div className="user-info-box">
                <img style={{ height: 120, width: 120, flex: '0 0 120px' }} src={userImage} />
                <div style={{ height: 180 }} className="user-info">
                  <div className="info-item">
                    <span>
                      <Icon type="smile" theme="twoTone" style={{ marginRight: 6 }} />
                      {hello}，{user.fullName}
                    </span>
                  </div>
                  <div className="info-item">
                    <span>
                      <Icon type="bank" theme="twoTone" style={{ marginRight: 6 }} />
                      {user.companyName}
                    </span>
                  </div>
                  <div className="info-item">
                    <span>
                      <Icon type="project" theme="twoTone" style={{ marginRight: 6 }} />
                      {user.departmentName}
                    </span>
                  </div>
                  <div className="info-item">
                    <Icon type="mail" theme="twoTone" style={{ marginRight: 6 }} /> {user.email}
                  </div>
                  <div className="info-item">
                    <Icon type="mobile" theme="twoTone" style={{ marginRight: 6 }} /> {user.mobile}
                  </div>
                </div>
              </div>
            </Card>
          </Col>
          <Col span={8}>
            {carousels && !!carousels.length ? (
              <Carousel arrows autoplay>
                {carousels.map(item => {
                  return (
                    <div
                      onClick={() => this.goCarouselDetail(item)}
                      key={item.id}
                      style={{ textAlign: 'center' }}
                    >
                      <img src={item.attachment ? item.attachment.thumbnailUrl : ''} />
                      <div className="title">{item.title}</div>
                    </div>
                  );
                })}
              </Carousel>
            ) : (
              <div
                style={{
                  height: 260,
                  fontSize: 18,
                  lineHeight: '260px',
                  textAlign: 'center',
                  backgroundColor: '#fff',
                }}
              >
                暂无公告信息
              </div>
            )}
          </Col>
          <Col span={8}>
            <Card
              title="待审批的单据"
              extra={<span style={{ fontSize: 18 }}>共{total || 0}笔</span>}
            >
              {!!total ? (
                <div id="pie" style={{ width: '100%', height: 160 }} />
              ) : (
                <div style={{ lineHeight: '160px', textAlign: 'center', fontSize: 18 }}>
                  暂无待审批单据
                </div>
              )}
            </Card>
          </Col>
        </Row>
        <Row className="disboard-bottom" style={{ marginTop: 12 }} gutter={12}>
          <Col className="data" span={16}>
            <Card title="个人报表" extra={this.renderDate()}>
              <Tabs onChange={this.reportChange} defaultActiveKey={tabKey}>
                <TabPane forceRender tab="费用趋势" key="1">
                  <div style={{ padding: 12 }}>
                    <div style={{ textAlign: 'right' }}>
                      <Icon
                        onClick={() => {
                          this.renderExpenseTrendBar();
                        }}
                        type="bar-chart"
                        style={{
                          marginRight: 20,
                          fontSize: 18,
                          color: chartsType == 1 ? '#1890ff' : '#000',
                          cursor: 'pointer',
                        }}
                      />
                      <Icon
                        onClick={() => {
                          this.renderExpenseTrendLine();
                        }}
                        type="line-chart"
                        style={{
                          fontSize: 18,
                          cursor: 'pointer',
                          marginRight: 30,
                          color: chartsType == 2 ? '#1890ff' : '#000',
                        }}
                      />
                      <span onClick={this.downloadImage} style={{ cursor: 'pointer' }}>
                        <Icon type="download" style={{ fontSize: 18 }} />导出
                      </span>
                    </div>
                    <div id="bar-chat">
                      <div style={{ height: 360, width: '100%' }} id="expanseTrend" />
                    </div>
                  </div>
                </TabPane>
                <TabPane forceRender tab="费用占比" key="2">
                  <div style={{ padding: 15 }}>
                    <div style={{ height: 375, width: '100%' }} id="expenseProportion" />
                  </div>
                </TabPane>
                <TabPane forceRender tab="付款状态" key="3">
                  <div style={{ padding: 12 }}>
                    <RadioGroup
                      name="radiogroup"
                      onChange={this.payTypeChange}
                      defaultValue={payType}
                    >
                      <Radio value={801003}>预付款金额</Radio>
                      <Radio value={801001}>报账金额</Radio>
                    </RadioGroup>
                    <div id="payInfo-chart">
                      <div style={{ height: 360, width: '100%' }} id="payInfo" />
                    </div>
                  </div>
                </TabPane>
              </Tabs>
            </Card>
          </Col>
          <Col className="tabs" span={8}>
            <Card title="我的单据" className="info-card">
              <Tabs defaultActiveKey="1">
                <TabPane
                  forceRender
                  key="1"
                  tab={
                    <span>
                      被退回的单据<Badge
                        onClick={this.toBack}
                        style={{ marginLeft: 5 }}
                        count={backTotal}
                      />
                    </span>
                  }
                >
                  <div style={{ height: '100%', overflowY: 'auto' }}>
                    {backList &&
                      backList.map(item => {
                        return (
                          <Card
                            title={<span style={{ fontSize: 14 }}>{item.documentNumber}</span>}
                            extra={<span>{moment(item.rejectTime).format('YYYY-MM-DD')}</span>}
                            style={{ marginTop: 12, cursor: 'pointer' }}
                            key={item.documentId}
                            hoverable
                            onClick={() => this.click(item)}
                          >
                            <Row>
                              <Col span={12}>{item.documentTypeName}</Col>
                              <Col
                                span={12}
                                style={{ textAlign: 'right', fontWeight: 600, fontSize: 16 }}
                              >
                                {item.currency} {this.filterMoney(item.amount, 2, true)}
                              </Col>
                            </Row>
                            <Row style={{ marginTop: 16 }}>
                              <Col
                                style={{
                                  textOverflow: 'ellipsis',
                                  overflow: 'hidden',
                                  whiteSpace: 'nowrap',
                                }}
                                span={14}
                              >
                                {item.remark}
                              </Col>
                              <Col span={10} style={{ textAlign: 'right' }}>
                                <Tag color={this.$statusList[item.status].color}>
                                  {this.$statusList[item.status].label}
                                </Tag>
                              </Col>
                            </Row>
                            <div
                              style={{
                                textAlign: 'right',
                                marginTop: 10,
                                paddingTop: 10,
                                borderTop: '1px solid #eee',
                              }}
                            >
                              驳回人：{item.nodeName}-{item.rejecterName}
                            </div>
                          </Card>
                        );
                      })}
                    {loadMoreBackBtn ? (
                      <div style={{ width: '100%', textAlign: 'center', marginTop: 20 }}>
                        <Button
                          onClick={this.loadMoreBack}
                          loading={backLoading}
                          style={{ width: 180 }}
                          type="primary"
                        >
                          加载更多
                        </Button>
                      </div>
                    ) : (
                      <Divider dashed>我是有底线的</Divider>
                    )}
                  </div>
                </TabPane>
                <TabPane
                  forceRender
                  key="2"
                  tab={
                    <span>
                      未完成的单据<Badge
                        onClick={this.toUnFinsh}
                        style={{ marginLeft: 5 }}
                        count={unFinishTotal}
                      />
                    </span>
                  }
                >
                  <div style={{ height: '100%', overflowY: 'auto' }}>
                    {doingList &&
                      doingList.map(item => {
                        return (
                          <Card
                            title={<span style={{ fontSize: 14 }}>{item.documentNumber}</span>}
                            extra={<span>{moment(item.submittedDate).format('YYYY-MM-DD')}</span>}
                            style={{ marginTop: 12, cursor: 'pointer' }}
                            key={item.documentId}
                            hoverable
                            onClick={() => this.click(item)}
                          >
                            <Row>
                              <Col span={12}>{item.documentTypeName}</Col>
                              <Col
                                span={12}
                                style={{ textAlign: 'right', fontWeight: 600, fontSize: 16 }}
                              >
                                {item.currency} {this.filterMoney(item.amount, 2, true)}
                              </Col>
                            </Row>
                            <Row style={{ marginTop: 16 }}>
                              <Col
                                style={{
                                  textOverflow: 'ellipsis',
                                  overflow: 'hidden',
                                  whiteSpace: 'nowrap',
                                }}
                                span={14}
                              >
                                {item.remark}
                              </Col>
                              <Col span={10} style={{ textAlign: 'right' }}>
                                <Tag color={this.$statusList[item.status].color}>
                                  {this.$statusList[item.status].label}
                                </Tag>
                              </Col>
                            </Row>
                            <div
                              style={{
                                textAlign: 'right',
                                marginTop: 10,
                                paddingTop: 10,
                                borderTop: '1px solid #eee',
                              }}
                            >
                              审批人：{item.nodeName}-{item.rejecterName}
                            </div>
                          </Card>
                        );
                      })}
                    {loadMoreUnFinishBtn ? (
                      <div style={{ width: '100%', textAlign: 'center', marginTop: 20 }}>
                        <Button
                          onClick={this.loadMoreunFinish}
                          loading={unFinishLoading}
                          style={{ width: 180 }}
                          type="primary"
                        >
                          加载更多
                        </Button>
                      </div>
                    ) : (
                      <Divider dashed>我是有底线的</Divider>
                    )}
                  </div>
                </TabPane>
              </Tabs>
            </Card>
          </Col>
        </Row>
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
)(Dashboard);
