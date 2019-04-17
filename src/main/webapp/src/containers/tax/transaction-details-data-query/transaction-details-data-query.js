/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import {
  Button,
  Divider,
  message,
  Popconfirm,
  Modal,
  Form,
  Select,
  Row,
  DatePicker,
  Table,
} from 'antd';
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
import Service from './transaction-details-data-query.service';
import registerApplyService from '../../tax-setting/register-manage/tax-register-apply.service';
class TransactionDetailsDataQuery extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      ruleParameterTypeArray: [], //值列表
      importSysId: '',
      data: [],
      searchForm: [
        // {
        //   type: 'list',
        //   id: 'companyCode',
        //   colSpan: 6,
        //   listType: 'company_detail',
        //   labelKey: 'companyName',
        //   valueKey: 'id',
        //   event: 'companyCode',
        //   single: true,
        //   listExtraParams: {},
        //   label: '机构' /*机构*/,
        // },
        {
          type: 'value_list',
          valueListCode: 'TAX_SOURCE_SYSTEM',
          colSpan: 6,
          // id为传到后台的字段
          id: 'sourceSystem',
          label: '来源系统',
          options: [],
        },
        {
          type: 'input',
          id: 'taxRate',
          placeholder: '请输入',
          label: '交易流水号',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'clientNumber',
          colSpan: 6,
          listType: 'customer_information_query',
          labelKey: 'clientName',
          valueKey: 'id',
          event: 'clientNumber',
          single: true,
          listExtraParams: {},
          label: '客户名称' /*客户名称*/,
        },
        {
          type: 'select',
          key: 'currency',
          id: 'currencyCode',
          label: '币种',
          getUrl: `${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`,
          getParams: { setOfBooksId: this.props.company.setOfBooksId },
          options: [],
          method: 'get',
          labelKey: '${currency}-${currencyName}',
          valueKey: 'currency',
          colSpan: 6,
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'companyId',
          //label: this.$t({ id: 'my.contract.contractCompany' } /*公司*/),
          label: '交易机构',
          listType: 'company_detail',
          valueKey: 'id',
          labelKey: 'name',
          options: [],
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          single: true,
          event: 'COMPANY_ID',
          allowClear: true,
        },
        // {
        //   type: 'list',
        //   id: 'companyCode',
        //   colSpan: 6,
        //   listType: 'company_detail',
        //   labelKey: 'companyName',
        //   valueKey: 'id',
        //   event: 'companyCode',
        //   single: true,
        //   listExtraParams: {},
        //   label: '交易机构' /*交易机构*/,
        // },
        {
          type: 'input',
          id: 'taxRate',
          placeholder: '请输入',
          label: '批次号',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'dateFrom', label: '交易日期从' },
            { type: 'date', id: 'dateTo', label: '交易日期至' },
          ],
          colSpan: 6,
        },
      ],
      // 分页代码1
      pagination: {
        total: 0,
        onChange: this.onChangePager,
        current: 1,
        onShowSizeChange: this.onChangePageSize,
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 10,
        pageSizeOptions: ['5', '10', '20', '50', '100'],
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
      },
      columns: [
        {
          title: '来源系统',
          dataIndex: 'sourceSystem',
          align: 'center',
          width: 200,
        },
        {
          title: '批次号',
          dataIndex: 'batchId',
          align: 'center',
          width: 200,
        },
        {
          title: '交易流水号',
          dataIndex: 'transNum',
          align: 'center',
          width: 200,
        },
        {
          title: '来源数据价税状态',
          dataIndex: 'sourceDataStatus',
          align: 'center',
          width: 200,
        },
        {
          title: '交易机构',
          dataIndex: 'org',
          align: 'center',
          width: 200,
        },
        {
          title: '责任中心',
          dataIndex: 'costCenter',
          align: 'center',
          width: 200,
        },
        {
          title: '客户编号',
          dataIndex: 'clientAcc',
          align: 'center',
          width: 200,
        },
        {
          title: '客户名称',
          dataIndex: 'clientName',
          align: 'center',
          width: 200,
        },
        // {
        //   title: '客户纳税人名称',
        //   dataIndex: 'taxpayerName',
        //   align: 'center',
        //   width: 200
        // },
        {
          title: '交易币种',
          dataIndex: 'currencyCode',
          align: 'center',
          width: 200,
        },
        {
          title: '原币种金额',
          dataIndex: 'amount',
          align: 'center',
          width: 200,
        },
        {
          title: '交易说明',
          dataIndex: 'transDesc',
          align: 'center',
          width: 200,
        },
        {
          title: '数据创建方式',
          dataIndex: 'dataType',
          align: 'center',
          width: 200,
        },
        {
          title: '价税分离状态',
          dataIndex: 'processFlag',
          align: 'center',
          width: 200,
        },
        {
          title: '交易日期',
          dataIndex: 'transDate',
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
  componentWillMount() {
    this.getList();
    this.setColumns();
    this.getColName();
  }

  // 获得数据
  getList() {
    const { pagination, colName } = this.state;
    const params = {};
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
    // console.log(this.props.params.id);
    Service.getCangeRecord(params)
      .then(response => {
        response.data.map(item => {
          item.key = item.id;
        });
        (pagination.total = Number(response.headers['x-total-count']) || 0),
          response.data.map(item => {
            item.dimensionCode = item[colName];
          });
        this.setState({
          data: response.data,
          loading: false,
          pagination,
        });
      })
      .catch(() => {});
  }
  // 动态创建columns
  setColumns = () => {
    const { columns } = this.state;
    Service.getColumns().then(res => {
      console.log(res.data);
      if (res.data && res.data.length) {
        columns.splice(
          2,
          0,
          ...res.data.map(item => {
            return {
              dataIndex: item.dimensionCode,
              title: item.dimensionName,
              width: 100,
              algin: 'center',
            };
          })
        );
        this.setState({ columns });
      }
    });
  };
  getColName = () => {
    Service.getColumns().then(res => {
      if (res.data && res.data.length) {
        res.data.map(item => {
          return (colName = item.colName.replace('_f', 'F'));
        });
        this.setState({ colName });
      }
    });
  };

  // 每页多少条
  onChangePageSize = (page, pageSize) => {
    const { pagination } = this.state;
    pagination.pageSize = pageSize;
    pagination.page = page;
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  // 分页点击
  onChangePager = page => {
    const { pagination } = this.state;
    pagination.current = page;
    this.setState({ pagination, loading: true }, () => {
      this.getList();
    });
  };
  close = flag => {
    this.setState({ newShow: false, record: {} }, () => {
      // eslint-disable-next-line no-unused-expressions
      flag && this.getList();
    });
  };

  handleCancel = () => {
    // eslint-disable-next-line no-unused-expressions
    this.props.onClose && this.props.onClose(true);
  };

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
  // 获取维值下拉列表
  getTaxQuaifation() {
    // eslint-disable-next-line prefer-const
    let taxQuaifationOptions = [];
    Service.getSystemValueList().then(res => {
      let taxClientTypeOptions = [];
      res.data.map(data => {
        taxClientTypeOptions.push({
          label: data.dimensionName,
          value: data.dimensionName,
          key: data.dimensionName,
          id: data.dimensionId,
        });
      });
      this.setState({
        taxClientTypeOptions,
      });
    });
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
      this.getList();
    });
  };
  //重置
  reset = () => {};

  //获取数据
  // getdata = () => {
  //   this.setState({
  //     lsVisible: true,
  //   });
  // };

  //跳转到详情
  // handleRowClick = record => {
  //   console.log('record.id=' + record.clientTaxName);
  //   this.props.dispatch(
  //     routerRedux.push({
  //       pathname: '/inter-management/cust-inter/customer-inter-detail/' + record.id,
  //     })
  //   );
  // };
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
  // 跳转到获取科目余额界面
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
    let {
      searchForm,
      columns,
      lsVisible,
      ruleParameterTypeArray,
      loading,
      data,
      pagination,
    } = this.state;
    const { getFieldDecorator } = this.props.form;

    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.reset}
          maxLength={4}
        />
        <div style={{ margin: '20px 0' }}>
          {/* <Button type="primary" onClick={this.toDistributionAccounting}>
            获取数据
          </Button> */}
        </div>
        {/* <Row style={{ textAlign: 'right' }}>
          <Button type="primary" htmlType="submit" loading={loading}>
            {this.$t({ id: 'common.submit' })}
          </Button>
          <Button style={{ marginLeft: 8 }} onClick={this.onCancel}>
            {this.$t({ id: 'common.cancel' })}
          </Button>
        </Row> */}
        <Table
          onClick={this.handleRowClick}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          bordered
          rowKey="id"
          columns={columns}
          // url={`${config.taxUrl}/api/tax/vat/trans/interface/pageByCondition`}
          ref={ref => (this.table = ref)}
          onRowClick={this.handleRowClick}
          scroll={{ x: 1500 }}
        />
        {/* <MonthPicker defaultValue={moment('2015/01', monthFormat)} format={monthFormat} />
        <br /> */}
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

export default connect(mapStateToProps)(Form.create()(TransactionDetailsDataQuery));
