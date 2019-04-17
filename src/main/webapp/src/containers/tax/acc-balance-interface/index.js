import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, Divider, message, Popconfirm, Modal, Form, Select, Row, DatePicker } from 'antd';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import moment from 'moment';
import { connect } from 'dva';
import CustomTable from 'widget/custom-table';
import { routerRedux } from 'dva/router';
import jobService from 'containers/job/job.service';
const confirm = Modal.confirm;
const { MonthPicker, RangePicker } = DatePicker;
const monthFormat = 'YYYY/MM';
class CustomerInterface extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ruleParameterTypeArray: [], //值列表
      importSysId: '',
      searchForm: [
        {
          type: 'list',
          id: 'companyCode',
          colSpan: 6,
          listType: 'company_detail',
          labelKey: 'companyName',
          valueKey: 'id',
          event: 'companyCode',
          single: true,
          listExtraParams: {},
          label: '机构' /*机构*/,
        },
        {
          type: 'list',
          id: 'companyCode',
          colSpan: 6,
          listType: 'subject',
          labelKey: 'companyName',
          valueKey: 'id',
          event: 'companyCode',
          single: true,
          listExtraParams: {},
          label: '科目' /*科目*/,
        },
        {
          type: 'list',
          id: 'companyCode',
          colSpan: 6,
          listType: 'specific_item',
          labelKey: 'companyName',
          valueKey: 'id',
          event: 'companyCode',
          single: true,
          listExtraParams: {},
          label: '子目' /*子目*/,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'dateFrom', label: '更新日期从' },
            { type: 'date', id: 'dateTo', label: '更新日期至' },
          ],
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '分类账名称',
          dataIndex: 'ledgerName',
          align: 'center',
          width: 200,
        },
        {
          title: '期间',
          dataIndex: 'periodName',
          align: 'center',
          width: 200,
        },
        {
          title: '币种代码',
          dataIndex: 'currencyCode',
          align: 'center',
          width: 200,
        },
        {
          title: '公司代码',
          dataIndex: 'segment1',
          align: 'center',
          width: 200,
        },
        {
          title: '公司名称',
          dataIndex: 'segment1Des',
          align: 'center',
          width: 200,
        },
        {
          title: '成本中心代码',
          dataIndex: 'segment2',
          align: 'center',
          width: 200,
        },
        {
          title: '成本中心名称',
          dataIndex: 'segment2Des',
          align: 'center',
          width: 200,
        },
        {
          title: '科目代码',
          dataIndex: 'segment3',
          align: 'center',
          width: 200,
        },
        {
          title: '科目名称',
          dataIndex: 'segment3Des',
          align: 'center',
          width: 200,
        },
        {
          title: '子目代码',
          dataIndex: 'segment4',
          align: 'center',
          width: 200,
        },
        {
          title: '子目名称',
          dataIndex: 'segment4Des',
          align: 'center',
          width: 200,
        },
        {
          title: '期初借方余额',
          dataIndex: 'beginBalanceDr',
          align: 'center',
          width: 200,
        },
        {
          title: '期初贷方余额',
          dataIndex: 'beginBalanceCr',
          align: 'center',
          width: 200,
        },
        {
          title: '期初余额¬',
          dataIndex: 'beginBalance',
          align: 'center',
          width: 200,
        },
        {
          title: '本期借方发生额',
          dataIndex: 'periodNetDr',
          align: 'center',
          width: 200,
        },
        {
          title: '本期贷方发生额',
          dataIndex: 'periodNetCr',
          align: 'center',
          width: 200,
        },
        {
          title: '期末借方余额',
          dataIndex: 'endBalanceDr',
          align: 'center',
          width: 200,
        },
        {
          title: '期末贷方余额',
          dataIndex: 'endBalanceCr',
          align: 'center',
          width: 200,
        },
        {
          title: '期末余额',
          dataIndex: 'endBalance',
          align: 'center',
          width: 200,
        },
        {
          title: '记账方向',
          dataIndex: 'accDirection',
          align: 'center',
          width: 200,
        },
        {
          title: '账套期间状态',
          dataIndex: 'ledgerPeriodStatus',
          align: 'center',
          width: 200,
        },
        {
          title: '数据产生日期',
          dataIndex: 'sysDate',
          align: 'center',
          width: 200,
        },
      ],
      importSys: [
        {
          type: 'value_list',
          id: 'importSys',
          //  label: messages('code.rule.document.type') /*单据类型'*/,
          valueListCode: 2023,
          options: [],
        },
      ],
      searchParams: {},
      visibel: false,
      model: {},
      lsVisible: false,
    };
    this.showConfirm = this.showConfirm.bind(this);
  }
  /**
   * 获取值列表
   * @param code :值列表代码
   * @param name :值列表名称
   */
  getValueList(code, name) {
    name.splice(0, name.length);
    this.getSystemValueList(code).then(response => {
      response.data.values.map(item => {
        let option = {
          key: item.value,
          id: item.value,
          value: item.name,
        };
        name.addIfNotExist(option);
      });
      this.setState({
        name,
      });
    });
    return;
  }

  showConfirm() {
    console.log(this.state);
    confirm({
      title: '提示',
      content: '请求提交成功，是否跳转请求运行监控界面？',
      onOk: () => {
        this.setState({
          ruleParameterTypeArray: [],
          lsVisible: false,
        });
        this.props.dispatch(
          routerRedux.push({
            pathname: '/job/job-log/job-log',
          })
        );
      },
      onCancel: () => {
        this.setState({
          ruleParameterTypeArray: [],
          lsVisible: false,
        });
      },
    });
  }
  //搜索
  search = values => {
    let params = { ...this.state.searchParams, ...values };

    params.dateFrom && (params.dateFrom = moment(params.dateFrom).format('YYYY-MM-DD'));
    params.dateTo && (params.dateTo = moment(params.dateTo).format('YYYY-MM-DD'));

    this.setState({ searchParams: params }, () => {
      this.table.search(this.state.searchParams);
    });
  };
  //重置
  reset = () => {};

  //获取数据
  getdata = () => {
    this.setState({
      lsVisible: true,
    });
  };

  onCancel = () => {
    this.setState({
      ruleParameterTypeArray: [],
      lsVisible: false,
    });
  };
  handleSubmit = preps => {
    this.props.form.validateFields((err, values) => {
      this.run(values.importSysId);
    });
  };
  跳转到获取科目余额界面;
  toDistributionAccounting = (id, taxCategoryName) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/inter-management/acc-balance-interface/get-account-balance`,
      })
    );
  };
  // 立即运行
  run = id => {
    jobService
      .runJobInfo(id)
      .then(res => {
        if (res.data.code === 200) {
          this.showConfirm();
        } else {
          message.error(this.$t({ id: 'common.operate.filed' } /*操作失败*/) + '!' + res.data.msg);
        }
      })
      .catch(e => {
        message.error(
          this.$t({ id: 'common.operate.filed' } /*操作失败*/) + '!' + e.response.data.message
        );
      });
  };

  render() {
    let { searchForm, columns, lsVisible, ruleParameterTypeArray, loading } = this.state;
    const { getFieldDecorator } = this.props.form;

    return (
      <div>
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.reset} />
        <div style={{ margin: '20px 0' }}>
          <Button type="primary" onClick={this.toDistributionAccounting}>
            获取数据
          </Button>
        </div>

        <CustomTable
          columns={columns}
          url={`${config.baseUrl}/tax/api/tax/acc/balance/interface/pageByCondition`}
          ref={ref => (this.table = ref)}
          scroll={{ x: 1500 }}
        />
      </div>
    );
  }
}

//export default CustomerInterface

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(mapStateToProps)(Form.create()(CustomerInterface));
