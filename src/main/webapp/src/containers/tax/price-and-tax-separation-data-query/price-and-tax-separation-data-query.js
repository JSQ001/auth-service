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
import Service from './price-and-tax-separation-data-query.service';
import ExcelExporter from 'widget/excel-exporter';
import priceAndTaxSeparationDataQueryService from './price-and-tax-separation-data-query.service';
import expensePolicyService from 'containers/setting/expense-policy/expense-policy.service';
import invoicingSiteService from '../../tax-setting/vat-invoicing-site/invoicing-site.service';
const confirm = Modal.confirm;
const { MonthPicker, RangePicker } = DatePicker;
const monthFormat = 'YYYY/MM';
class TransactioDetailsDataSupplement extends Component {
  constructor(props) {
    super(props);
    this.state = {
      ruleParameterTypeArray: [], //值列表
      importSysId: '',
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
        // {
        //   type: 'select',
        //   colSpan: 6,
        //   id: 'invoiceTypeId',
        //   //  label: this.$t('expense.wallet.invoiceType') /* 发票类型 */,
        //   label: "来源系统" /* 发票类型 */,
        //   options: [],
        //   method: 'get',
        //   getUrl: `${config.expenseUrl}/api/invoice/type/query/for/invoice`,
        //   getParams: { tenantId: props.company.tenantId, setOfBooksId: props.company.setOfBooksId },
        //   labelKey: 'invoiceTypeName',
        //   valueKey: 'id',
        // },
        {
          type: 'value_list',
          valueListCode: 'TAX_SOURCE_SYSTEM',
          colSpan: 6,
          // id为传到后台的字段
          id: 'invoiceTypeId',
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
          dataIndex: 'tranNum',
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
          title: '交易期间',
          dataIndex: 'tranPeriod',
          align: 'center',
          width: 200,
        },
        {
          title: '客户纳税人名称',
          dataIndex: 'taxpayerName',
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
          title: '本币交易金额',
          dataIndex: 'funTranAmount',
          align: 'center',
          width: 200,
        },
        {
          title: '原币应税销售额',
          dataIndex: 'sales',
          align: 'center',
          width: 200,
        },
        {
          title: '本币应税销售额',
          dataIndex: 'funSales',
          align: 'center',
          width: 200,
        },
        {
          title: '原币销项税额',
          dataIndex: 'outTaxes',
          align: 'center',
          width: 200,
        },
        {
          title: '本币销项税额',
          dataIndex: 'funOutTaxes',
          align: 'center',
          width: 200,
        },
        {
          title: '增值税科目方向',
          dataIndex: 'drOrCr',
          align: 'center',
          width: 200,
        },
        {
          title: '交易说明',
          dataIndex: 'tranDesc',
          align: 'center',
          width: 200,
        },
        {
          title: '交易日期',
          dataIndex: 'tranDate',
          align: 'center',
          width: 200,
        },
      ],
      // excel表格不可见,导出功能
      excelVisible: false,
      visibel: false,
      model: {},
      exportColumns: [
        { title: '开票点编码', dataIndex: 'invoicingSiteCode' },
        { title: '开票点名称', dataIndex: 'invoicingSiteName' },
        { title: '所属纳税主体名称', dataIndex: 'taxpayerName' },
        { title: '开票点地址', dataIndex: 'invoicingSiteAdd' },
        { title: '税控钥匙密码', dataIndex: 'passwod' },
        { title: '开票终端标识', dataIndex: 'invoicingTerminal' },
        { title: '安全码', dataIndex: 'securityCode' },
        { title: '上边距', dataIndex: 'printTop' },
        { title: '左边距', dataIndex: 'printLeft' },
        { title: '备注', dataIndex: 'remarks' },
        { title: '状态', dataIndex: 'enabled' },
      ],
      searchParams: {},
      visibel: false,
      model: {},
      taxCategory: {},
      selectedKey: [],
      id: props.match.params.id,
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
  }

  // 获得数据
  getList() {
    const { pagination } = this.state;
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
          this.setState({
            data: response.data,
            loading: false,
            pagination,
          });
      })
      .catch(() => {});
  }

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
  //导出维值--可视化导出模态框
  handleExport = () => {
    this.setState({ excelVisible: true });
  };
  //确认导出
  confirmExport = result => {
    let hide = message.loading('正在生成文件，请等待......');
    const { dimensionId } = this.state;
    invoicingSiteService
      .exportSelfTax(result, { page: 1, size: 10 }, dimensionId)
      .then(res => {
        if (res.status === 200) {
          message.success('导出成功');
          let fileName = res.headers['content-disposition'].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          hide();
        }
      })
      .catch(err => {
        //  message.error(err.response.data.message);
        message.error('下载失败，请重试!');
        hide();
      });
  };
  onExportCancel = () => {
    this.setState({ excelVisible: false });
  };
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
  submit = () => {
    const { selectedKey } = this.state;
    // console.log(selectedKey);
    invoicingDimensionService
      .deleteInvoicingDimensionBatch(selectedKey)
      .then(() => {
        message.success('提交成功');
        this.setState({ selectedKey: [] });
        this.getList();
      })
      .catch(err => message.warning(err.response.data.message));
  };

  submitClick = () => {
    const { selectedKey } = this.state;
    if (!selectedKey.length) {
      message.warning('请选择要提交的数据！');
    }
  };
  selectChange = key => {
    // console.log(key)
    this.setState({ selectedKey: key });
  };
  render() {
    let {
      searchForm,
      columns,
      lsVisible,
      ruleParameterTypeArray,
      selectedKey,
      exportColumns,
      excelVisible,
      tabValue,
      model,
      visibel,
      loading,
      data,
      pagination,
    } = this.state;
    const { getFieldDecorator, id } = this.props.form;
    const rowSelection = {
      onChange: this.selectChange,
      selectedRowKeys: selectedKey,
    };
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
        <Button
          style={{ margin: '20px 0', padding: '0' }}
          type="primary"
          onClick={this.submitClick}
        >
          {selectedKey.length ? (
            <Popconfirm
              title="你确定要提交吗？"
              onConfirm={this.delete}
              okText="确定"
              cancelText="取消"
            >
              <div style={{ lineHeight: '20px', padding: '0 15px' }}>提交</div>
            </Popconfirm>
          ) : (
            <div style={{ lineHeight: '20px', padding: '0 15px' }}>提交</div>
          )}
        </Button>
        <Button style={{ marginRight: '20px' }} onClick={this.handleExport}>
          导出
        </Button>
        <Table
          onClick={this.handleRowClick}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          bordered
          columns={columns}
          url={`${config.baseUrl}/tax/api/tax/client/interface/pageByCondition`}
          ref={ref => (this.table = ref)}
          onRowClick={this.handleRowClick}
          scroll={{ x: 1500 }}
        />
        {/* <MonthPicker defaultValue={moment('2015/01', monthFormat)} format={monthFormat} />
        <br /> */}
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'价税分离数据查询'}
          onCancel={this.onExportCancel}
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
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

export default connect(mapStateToProps)(Form.create()(TransactioDetailsDataSupplement));
