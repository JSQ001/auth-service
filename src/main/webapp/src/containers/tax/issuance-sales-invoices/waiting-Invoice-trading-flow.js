/*eslint-disable*/
import React from 'react';
import SearchArea from 'widget/search-area';
import { routerRedux } from 'dva/router';
import { Button, message } from 'antd';
import Table from 'widget/table';
import config from 'config';
import { connect } from 'dva';
import ExcelExporter from 'widget/excel-exporter';
// import NewInvoicingSite from '../../tax-setting/vat-invoicing-site/new-invoicing-site';
import CustomTable from 'components/Widget/custom-table';
import alreadyAmount from '../already-amount/already-amount';
import Service from './waiting-Invoice-trading-flow.service';
import SeeDetails from './waiting-Invoice-trading-detail';

class WaitingInvoiceTradingFlow extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      newShow: false,
      data: [],
      record: {},
      selectedRow: [],
      taxInvoiceType: [],
      taxwriteOff: [],
      taxSourceSystem: [],
      selectedRowKeys: [],
      //搜索
      searchForm: [
        {
          type: 'input',
          id: 'tranNum',
          label: '交易流水号',
          colSpan: 6,
        },
        {
          type: 'value_list',
          valueListCode: 'TAX_SOURCE_SYSTEM',
          id: 'socrceSystem',
          placeholder: '请选择',
          label: '来源系统',
          colSpan: 6,
          options: [],
        },
        {
          type: 'value_list',
          valueListCode: 'TAX_VAT_INVOICE_TYPE',
          id: 'tradeName',
          placeholder: '请选择',
          label: '交易名称',
          colSpan: 6,
          options: [],
        },
        {
          type: 'lov',
          id: 'clientId',
          code: 'customer_information_query',
          label: '客户名称',
          valueKey: 'id',
          labelKey: 'clientName',
          single: true,
          colSpan: 6,
        },
        {
          type: 'value_list',
          valueListCode: 'TAX_VAT_INVOICE_TYPE',
          id: 'invoiceType',
          placeholder: '请选择',
          label: '发票类型',
          colSpan: 6,
          options: [],
        },
        {
          type: 'input',
          id: 'invoiceName',
          label: '开票名称',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'createdDate',
          colSpan: 6,
          items: [
            {
              type: 'date',
              id: 'createdDateFrom',
              label: '交易日期从',
            },
            {
              type: 'date',
              id: 'createdDateTo',
              label: '交易日期至',
            },
          ],
        },
        {
          type: 'lov',
          id: 'orgId',
          code: 'company',
          label: '机构',
          valueKey: 'id',
          labelKey: 'chooser.data.companyName',
          single: true,
          colSpan: 6,
        },
        {
          type: 'lov',
          id: 'costCenterId',
          code: 'responsibilityCenter',
          label: '责任中心',
          valueKey: 'id',
          labelKey: 'responsibilityCenterName',
          single: true,
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            { type: 'input', id: 'amountFrom', label: '金额从' },
            { type: 'input', id: 'amountTo', label: '金额至' },
          ],
        },
        {
          type: 'value_list',
          valueListCode: 'TAX_WRITE_OFF_STATUS',
          id: 'writeOffStatus',
          placeholder: '请选择',
          label: '核销状态',
          colSpan: 6,
          options: [],
        },
        {
          type: 'input',
          id: 'taxpyerNum',
          label: '纳税人识别号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'currencyCode',
          label: '币种',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'canAmount',
          label: '可开票金额',
          colSpan: 6,
        },
        {
          type: 'lov',
          id: 'taxRateName',
          code: 'tax-rate-definition',
          label: '税目',
          valueKey: 'id',
          labelKey: 'taxCategoryName',
          single: true,
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'taxRate',
          label: '税率',
          colSpan: 6,
        },
      ],
      //表格
      columns: [
        {
          //交易流水号
          title: '交易流水号',
          dataIndex: 'tranNum',
          key: 'tranNum',
          align: 'center',
          width: 150,
        },
        {
          //来源系统
          title: '来源系统',
          dataIndex: 'sourceSystem',
          key: 'sourceSystem',
          align: 'center',
          width: 150,
          render: res => {
            if (res == '101') {
              return '恒生系统';
            } else if (res == '102') {
              return '投行系统';
            }
          },
        },
        // {
        //   //交易名称
        //   title: '交易名称',
        //   dataIndex: 'tradedName',
        //   key: 'tradedName',
        //   align: 'center',
        //   width: 150,
        // },
        // {
        //   //费用项目
        //   title: '费用项目',
        //   dataIndex: 'expenseItem',
        //   key: 'expenseItem',
        //   align: 'center',
        //   width: 150,
        // },
        {
          //机构
          title: '机构',
          dataIndex: 'org',
          key: 'org',
          align: 'center',
          width: 150,
        },
        {
          //客户编号
          title: '客户编号',
          dataIndex: 'clientNumber',
          key: 'clientNumber',
          align: 'center',
          width: 150,
        },
        {
          //客户名称
          title: '客户名称',
          dataIndex: 'clientName',
          key: 'clientName',
          align: 'center',
          width: 150,
        },
        {
          //纳税人资质
          title: '纳税人资质',
          dataIndex: 'taxQualification',
          key: 'taxQualification',
          align: 'center',
          width: 150,
        },
        {
          //纳税人名称
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          key: 'taxpayerName',
          align: 'center',
          width: 150,
        },
        {
          //纳税人识别号
          title: '纳税人识别号',
          dataIndex: 'taxpayerNumber',
          key: 'taxpayerNumber',
          align: 'center',
          width: 150,
        },
        {
          //开票名称
          title: '开票名称',
          dataIndex: 'invoiceTitle',
          key: 'invoiceTitle',
          align: 'center',
          width: 150,
        },
        {
          //发票类型
          title: '发票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
          align: 'center',
          width: 150,
          // render: res => {
          //   if (res == "GENERAL_INVOICE") {
          //     return "专用发票"
          //   } else if (res == "ELECTRONIC_INVOICE") {
          //     return "电子普票"
          //   } else {
          //     return "普通发票"
          //   }
          // }
        },
        {
          //交易日期
          title: '交易日期',
          dataIndex: 'tranDate',
          key: 'tranDate',
          align: 'center',
          width: 150,
        },
        {
          //币种
          title: '币种',
          dataIndex: 'currencyCode',
          key: 'currencyCode',
          align: 'center',
          width: 150,
        },
        {
          //原币交易金额
          title: '原币交易金额',
          dataIndex: 'tranAmount',
          key: 'tranAmount',
          align: 'center',
          width: 150,
        },
        {
          //汇率
          title: '汇率',
          dataIndex: 'exchangeRate',
          key: 'exchangeRate',
          align: 'center',
          width: 150,
        },
        {
          //本币交易金额
          title: '本币交易金额',
          dataIndex: 'funTranAmount',
          key: 'funTranAmount',
          align: 'center',
          width: 150,
        },
        {
          //税目名称
          title: '税目',
          dataIndex: 'taxRateId',
          key: 'taxRateId',
          align: 'center',
          width: 150,
        },
        {
          //税率%
          title: '税率%',
          dataIndex: 'vatRate',
          key: 'vatRate',
          align: 'center',
          width: 150,
        },
        {
          //已开票金额
          title: '已开票金额',
          dataIndex: 'alreadyInvoiceAmount',
          key: 'alreadyInvoiceAmount',
          align: 'center',
          width: 150,
          render: (value, record) => <a onClick={() => this.toAlreadyAmount(record.id)}>{value}</a>,
        },
        {
          //可开票金额
          title: '可开票金额',
          dataIndex: 'mayInvoiceAmount',
          key: 'mayInvoiceAmount',
          align: 'center',
          width: 150,
        },
        {
          //本次开票金额
          title: '本次开票金额',
          dataIndex: 'invoiceAmount',
          key: 'invoiceAmount',
          align: 'center',
          width: 150,
        },
        {
          title: '操作',
          dataIndex: 'operation',
          key: 'operation',
          align: 'center',
          render: (value, record) => <a onClick={() => this.openDetails(record)}>编辑</a>,
          width: 80,
        },
      ],
      // excel表格不可见
      excelVisible: false,
      visibel: false,
      model: {},
      exportColumns: [
        { title: '系统名称', dataIndex: 'invoicingSiteCode' },
        { title: '数据类型', dataIndex: 'invoicingSiteName' },
        { title: '交易名称', dataIndex: 'taxpayerName' },
        { title: '费用项目', dataIndex: 'invoicingSiteAdd' },
        { title: '商品编码', dataIndex: 'passwod' },
        { title: '开票名称', dataIndex: 'invoicingTerminal' },
        { title: '发票类型', dataIndex: 'securityCode' },
        { title: '单位', dataIndex: 'printTop' },
        { title: '备注', dataIndex: 'remarks' },
        { title: '状态', dataIndex: 'enabled' },
      ],
      //分页
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
      searchParams: {},
    };
  }
  //获取发票类型
  getTaxAccountingMethod() {
    // eslint-disable-next-line prefer-const
    let taxInvoiceType = [];
    this.getSystemValueList('TAX_VAT_INVOICE_TYPE').then(res => {
      res.data.values.map(data => {
        taxInvoiceType.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxInvoiceType,
      });
    });
  }
  //获取核销状态
  getTaxAccountingMethod() {
    // eslint-disable-next-line prefer-const
    let taxwriteOff = [];
    this.getSystemValueList('TAX_WRITE_OFF_STATUS').then(res => {
      res.data.values.map(data => {
        taxwriteOff.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxwriteOff,
      });
    });
  }
  //获取来源系统
  getTaxAccountingMethod() {
    // eslint-disable-next-line prefer-const
    let taxSourceSystem = [];
    this.getSystemValueList('TAX_SOURCE_SYSTEM').then(res => {
      res.data.values.map(data => {
        taxSourceSystem.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxSourceSystem,
      });
    });
  }
  // 每页多少条
  onChangePageSize = (page, pageSize) => {
    const { pagination } = this.state;
    pagination.pageSize = pageSize;
    pagination.page = page;
    this.setState({ pagination, loading: false }, () => {
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
  // 跳转已开票金额(本币)页面
  toAlreadyAmount = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/tax/already-amount/already-amount/${id}`,
      })
    );
  };
  componentWillMount() {
    this.getList();
    this.setColumns();
  }
  // 获得数据
  getList() {
    const { searchParams, pagination } = this.state;
    const params = { ...searchParams };
    Service.getWaitInvoiceList(params)
      .then(response => {
        pagination.total = Number(response.headers['x-total-count']) || 0;
        response.data.map(item => {
          item.key = item.id;
          if (item.taxVatSeparateRuleAddList && item.taxVatSeparateRuleAddList.length) {
            item.taxVatSeparateRuleAddList.map(o => {
              item[o.dimensionCode] = `${o.dimensionValueCode}-${o.dimensionValueName}`;
            });
          }
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
  toCreateInoviceApply = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/tax/appplication-transaction-flow-invoice/appplication-transaction-flow-invoice/${record}`,
      })
    );
  };
  // 创建开票申请
  handleSubmitClick = () => {
    const selectedRowKeys = this.state.selectedRowKeys;
    const selectedRow = this.state.selectedRow;
    Service.routerNewPage(selectedRow)
      .then(res => {
        if (res.status === 200) {
          message.success('创建开票申请成功！');
          this.toCreateInoviceApply(selectedRowKeys);
        }
      })
      .catch(err => {
        message.error('创建开票申请失败！');
      });
    return 'submit';
  };
  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState({
      selectedRowKeys,
      selectedRow,
      batchDelete: !(selectedRowKeys.length > 0),
      disabledSubmit: !(selectedRowKeys.length > 0),
    });
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
  reset = () => {
    this.setState({ searchParams: {} }, () => {
      this.getList();
    });
  };
  // 新建
  create = () => {
    this.setState({
      visibel: true,
    });
  };
  //编辑
  openDetails = record => {
    record.sign = 1;
    this.setState({
      record: record,
      newShow: true,
    });
  };
  close = flag => {
    this.setState({ newShow: false, record: {} }, () => {
      // eslint-disable-next-line no-unused-expressions
      flag && this.getList();
    });
  };
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

  render() {
    const {
      searchForm,
      columns,
      pagination,
      exportColumns,
      excelVisible,
      selectedRowKeys,
      selectedRow,
      loading,
      data,
      record,
      visibel,
      selectedKey,
      newShow,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.reset}
          maxLength={4}
        />
        {/* <div style={{ margin: '10px 10px' }}>
          <Button type='primary' onClick={this.export}>导出</Button>
        </div> */}
        <div className="table-header">
          <div className="table-header-buttons">
            <Button
              style={{ margin: '20px 20px 20px 0' }}
              className="create-btn"
              type="primary"
              onClick={this.handleSubmitClick}
            >
              创建开票申请
            </Button>
            <Button onClick={this.handleExport}>导出</Button>
          </div>
        </div>
        <div className="Table_div" style={{ backgroundColor: 111 }}>
          <Table
            rowKey={record => record.id}
            columns={columns}
            pagination={pagination}
            rowSelection={rowSelection}
            loading={loading}
            dataSource={data}
            bordered
            scroll={{ x: 1500 }}
            size="middle"
          />
        </div>
        {/* 导出  */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'开票点信息'}
          onCancel={this.onExportCancel}
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
        />
        {newShow && <SeeDetails params={record} visible={newShow} onClose={this.close} />}
      </div>
    );
  }
}
export default connect()(WaitingInvoiceTradingFlow);
