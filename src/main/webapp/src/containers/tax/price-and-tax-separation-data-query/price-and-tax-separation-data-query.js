/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, message, Modal, Form, DatePicker, Popover } from 'antd';
import Table from 'widget/table';
import config from 'config';
import moment from 'moment';
import { connect } from 'dva';
import Service from './price-and-tax-separation-data-query.service';
import ExcelExporter from 'widget/excel-exporter';
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
        {
          type: 'value_list',
          valueListCode: 'TAX_SOURCE_SYSTEM',
          id: 'sourceSystem',
          placeholder: '请选择',
          label: '来源系统',
          colSpan: 6,
          options: [],
        },
        {
          type: 'input',
          id: 'transNum',
          placeholder: '请输入',
          label: '交易流水号',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'clientAcc',
          colSpan: 6,
          listType: 'customer_information_query',
          labelKey: 'clientName',
          valueKey: 'clientNumber',
          event: 'clientName',
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
          id: 'org',
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
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'transDateFrom', label: '交易日期从' },
            { type: 'date', id: 'transDateTo', label: '交易日期至' },
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
        pageSize: 5,
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
          title: '交易流水号',
          dataIndex: 'transNum',
          align: 'center',
          width: 200,
        },
        {
          title: '来源数据价税状态',
          dataIndex: 'sourceDataStatusName',
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
          dataIndex: 'transPeriod',
          align: 'center',
          width: 200,
        },
        {
          title: '客户编号',
          dataIndex: 'clientNumber',
          align: 'center',
          width: 200,
        },
        {
          title: '客户名称',
          dataIndex: 'clientName',
          align: 'center',
          width: 200,
        },
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
          render: recode => {
            return (
              <Popover content={moment(recode).format('YYYY-MM-DD')}>
                {recode ? moment(recode).format('YYYY-MM-DD') : ''}
              </Popover>
            );
          },
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
      lsVisible: false,
    };
  }
  componentDidMount() {
    this.getList();
    this.setColumns();
  }

  // 获得数据
  getList() {
    const { pagination, searchParams } = this.state;
    const params = { ...searchParams };
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
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
  // 动态创建columns
  setColumns = () => {
    const { columns } = this.state;
    Service.getColumns().then(res => {
      if (res.data && res.data.length) {
        columns.splice(
          2,
          0,
          ...res.data.map(item => {
            return {
              dataIndex: item.colName.replace('_f', 'F'),
              title: item.dimensionName,
              key: item.dimensionId,
              width: 100,
              align: 'center',
              render: (value, record) => (
                <span>
                  {value}-{record[item.colName.replace('_f', 'F') + 'Name']}
                </span>
              ),
            };
          })
        );
        this.setState({ columns });
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
  // componentDidMount() {
  //   const { searchForm } = this.state;
  //   let params = { roleType: 'TENANT', enabled: true };
  //   let setOfBooksId =
  //     this.props.match.params.sob === ':sob'
  //       ? this.props.company.setOfBooksId
  //       : this.props.match.params.sob;
  //   expensePolicyService.getTenantAllSob(params).then(res => {
  //     searchForm[0].options = res.data.map(item => {
  //       if (item.id === setOfBooksId) {
  //         searchForm[0].defaultValue = {
  //           key: item.id,
  //           label: item.setOfBooksCode + '-' + item.setOfBooksName,
  //         };
  //       }
  //       return {
  //         key: item.id,
  //         value: item.id,
  //         label: item.setOfBooksCode + '-' + item.setOfBooksName,
  //       };
  //     });
  //     this.setState({ searchForm });
  //   });
  // }
  //导出维值--可视化导出模态框
  handleExport = () => {
    this.setState({ excelVisible: true });
  };
  //确认导出
  confirmExport = result => {
    let hide = message.loading('正在生成文件，请等待......');
    const { dimensionId } = this.state;
    invoicingSiteService
      .exportSelfTax(result, { page: 1, size: 5 }, dimensionId)
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

  //搜索
  handleSearch = params => {
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.current = 1;

    this.setState(
      {
        searchParams: params,
        loading: true,
        pagination,
      },
      () => {
        this.getList();
      }
    );
  };
  //重置
  reset = () => {};

  onCancel = () => {
    this.setState({
      ruleParameterTypeArray: [],
      lsVisible: false,
    });
  };

  render() {
    let {
      searchForm,
      columns,
      selectedKey,
      exportColumns,
      excelVisible,
      loading,
      data,
      pagination,
    } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.reset}
          maxLength={4}
        />

        <Button style={{ margin: '20px 0' }} onClick={this.handleExport} type="primary">
          导出
        </Button>
        <Table
          onClick={this.handleRowClick}
          dataSource={data}
          pagination={pagination}
          loading={loading}
          bordered
          columns={columns}
          ref={ref => (this.table = ref)}
          onRowClick={this.handleRowClick}
          scroll={{ x: 1500 }}
        />
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
