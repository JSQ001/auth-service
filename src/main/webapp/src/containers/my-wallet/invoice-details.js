import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import ExcelExporter from 'widget/excel-exporter';
import { Button, Checkbox, message, Popover } from 'antd';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import moment from 'moment';
import config from 'config';
import FileSaver from 'file-saver';
import service from './my-wallet-service';
import Invoice from './invoice';

class InvoiceDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          colSpan: 6,
          id: 'invoiceTypeId',
          label: this.$t('expense.wallet.invoiceType') /* 发票类型 */,
          options: [],
          method: 'get',
          getUrl: `${config.expenseUrl}/api/invoice/type/query/for/invoice?tenantId=${
            props.company.tenantId
          }&setOfBooksId=${props.company.setOfBooksId}`,
          labelKey: 'invoiceTypeName',
          valueKey: 'id',
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'invoiceCode',
          label: this.$t('expense.wallet.invoiceCode') /* 发票代码 */,
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'invoiceNo',
          label: this.$t('expense.wallet.invoiceNo') /* 发票号码 */,
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'expenseNum',
          label: this.$t('expense.wallet.expenseNo') /* 报账单单号 */,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'abc',
          items: [
            {
              type: 'date',
              id: 'invoiceDateFrom',
              colSpan: 3,
              label: this.$t('expense.wallet.invoiceDateFrom') /* 开票日期从 */,
            },
            {
              type: 'date',
              id: 'invoiceDateTo',
              colSpan: 3,
              label: this.$t('expense.wallet.invoiceDateTo') /* 开票日期到 */,
            },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'aaa',
          items: [
            {
              type: 'inputNumber',
              id: 'invoiceAmountFrom',
              colSpan: 3,
              label: this.$t('expense.wallet.invoiceAmountFrom'),
            },
            {
              type: 'inputNumber',
              id: 'invoiceAmountTo',
              colSpan: 3,
              label: this.$t('expense.wallet.invoiceAmountTo'),
            },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'number1',
          items: [
            {
              type: 'input',
              id: 'invoiceLineNumFrom',
              colSpan: 3,
              label: this.$t('expense.wallet.invoiceLine.from'),
            },
            {
              type: 'input',
              id: 'invoiceLineNumTo',
              colSpan: 3,
              label: this.$t('expense.wallet.invoiceLine.to'),
            },
          ],
        },
        {
          type: 'select',
          id: 'taxRate',
          label: this.$t('expense.invoice.tax.rate') /* 税率 */,
          colSpan: 6,
          options: [],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'number2',
          items: [
            {
              type: 'inputNumber',
              id: 'taxAmountFrom',
              colSpan: 3,
              label: this.$t('expense.invoice.taxAmount.from') /* 税额从 */,
            },
            {
              type: 'inputNumber',
              id: 'taxAmountTo',
              colSpan: 3,
              label: this.$t('expense.invoice.taxAmount.to') /* 税额到 */,
            },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'number3',
          items: [
            {
              type: 'date',
              id: 'applyDateFrom',
              colSpan: 3,
              label: this.$t('epx.adjust.apply.dateFrom') /* 申请日期从 */,
            },
            {
              type: 'date',
              id: 'applyDateTo',
              colSpan: 3,
              label: this.$t('exp.adjust.apply.date.to') /* 申请日期到 */,
            },
          ],
        },
        {
          type: 'select',
          id: 'applicant',
          label: this.$t('common.applicant'),
          colSpan: 6,
          options: [{ label: 'demo2', value: 'demo2' }],
        },
        {
          type: 'select',
          id: 'documentStatus',
          label: this.$t('expense.invoice.documentStatus') /* 单据状态 */,
          colSpan: 6,
          options: [{ label: 'demo3', value: 'demo3' }],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'number11',
          items: [
            {
              type: 'input',
              id: 'costLineNumberFrom',
              colSpan: 3,
              label: this.$t('expense.invoice.expenseLineFrom') /* 费用行号从 */,
            },
            {
              type: 'input',
              id: 'costLineNumberTo',
              colSpan: 3,
              label: this.$t('expense.invoice.expenseLineTo') /* 费用行号到 */,
            },
          ],
        },
        {
          type: 'select',
          id: 'costType',
          label: this.$t('expense.wallet.expenseType') /* 费用类型 */,
          colSpan: 6,
          options: [{ label: 'demo2', value: 'demo2' }],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'number00',
          items: [
            {
              type: 'inputNumber',
              id: 'costAmountFrom',
              colSpan: 3,
              label: this.$t('expense.wallet.expenseFrom') /* 费用金额从 */,
            },
            {
              type: 'inputNumber',
              id: 'costAmountTo',
              colSpan: 3,
              label: this.$t('expense.wallet.expenseTo') /* 费用金额到 */,
            },
          ],
        },
        {
          type: 'select',
          id: 'installmentDeduction',
          label: this.$t('expense.wallet.installment') /* 分期抵扣 */,
          colSpan: 6,
          options: [
            { label: this.$t('common.yes'), value: true },
            { label: this.$t('common.no'), value: false },
          ],
        },
      ],
      detailColumns: [
        {
          title: this.$t('expense.wallet.invoiceType') /* 发票类型 */,
          dataIndex: 'invoiceTypeName',
          align: 'left',
          width: 150,
          render: value => <Popover content={value}>{value}</Popover>,
        },
        {
          title: this.$t('expense.wallet.invoiceCode') /* 发票代码 */,
          dataIndex: 'invoiceCode',
          align: 'left',
          width: 150,
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a onClick={() => this.showInvoiceDetail(record)}>{value}</a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('expense.wallet.invoiceNo') /* 发票号码 */,
          dataIndex: 'invoiceNo',
          align: 'left',
          width: 150,
          render: value => <Popover content={value}>{value}</Popover>,
        },
        {
          title: this.$t('expense.invoice.date') /* 开票日期 */,
          dataIndex: 'invoiceDate',
          align: 'left',
          width: 150,
          render: value => value && moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t('expense.invoice.amount.without.tax') /* 金额合计 */,
          dataIndex: 'invoiceAmount',
          align: 'right',
          width: 150,
        },
        {
          title: this.$t('expense.invoice.lineNo') /* 发票行号 */,
          dataIndex: 'invoiceLineNum',
          align: 'left',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('expense.invoice.line.amount') /* 发票行金额 */,
          dataIndex: 'detailAmount',
          align: 'right',
          width: 150,
        },
        {
          title: this.$t('expense.invoice.tax.rate') /* 税率 */,
          dataIndex: 'taxRate',
          align: 'right',
          width: 100,
        },
        {
          title: this.$t('common.tax') /* 税额 */,
          dataIndex: 'taxAmount',
          align: 'right',
          width: 100,
        },
        {
          title: this.$t('expense.invoice.reimburse.number') /* 报账单号 */,
          dataIndex: 'expenseNum',
          align: 'left',
          width: 150,
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a onClick={() => this.showBillDetail(record.expenseReportId)}>{value}</a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('common.apply.data') /* 申请日期 */,
          dataIndex: 'applicationDate',
          align: 'left',
          width: 150,
          render: value => value && moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t('common.applicant'),
          dataIndex: 'applicant',
          align: 'left',
          width: 100,
          tooltips: true,
        },
        {
          title: this.$t('expense.invoice.documentStatus') /* 单据状态 */,
          dataIndex: 'documentState',
          align: 'left',
          width: 80,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.expenseLineNo') /* 费用行号 */,
          dataIndex: 'costLineNumber',
          align: 'left',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.expenseType') /* 费用类型 */,
          dataIndex: 'costType',
          align: 'left',
          width: 100,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.expenseAmount') /* 费用金额 */,
          dataIndex: 'costAmount',
          align: 'right',
          width: 150,
        },
        {
          title: this.$t('expense.wallet.installment') /* 分期抵扣 */,
          dataIndex: 'installmentDeduction',
          align: 'left',
          width: 80,
          render: value => <Checkbox onChange={this.checkboxChange} defaultChecked={value} />,
          fixed: 'right',
        },
      ],
      showInvoiceDetail: false,
      selectedRowKeys: [],
      exportVisible: false,
      exportColumns: [
        {
          title: this.$t('expense.wallet.invoiceType') /* 发票类型 */,
          dataIndex: 'invoiceTypeName',
        },
        { title: this.$t('expense.wallet.invoiceCode') /* 发票代码 */, dataIndex: 'invoiceCode' },
        { title: this.$t('expense.wallet.invoiceNo') /* 发票号码 */, dataIndex: 'invoiceNo' },
        { title: this.$t('expense.invoice.date') /* 开票日期 */, dataIndex: 'invoiceDate' },
        {
          title: this.$t('expense.invoice.amount.without.tax') /* 金额合计 */,
          dataIndex: 'invoiceAmount',
        },
        { title: this.$t('expense.invoice.lineNo') /* 发票行号 */, dataIndex: 'invoiceLineNum' },
        {
          title: this.$t('expense.invoice.line.amount') /* 发票行金额 */,
          dataIndex: 'detailAmount',
        },
        { title: this.$t('expense.invoice.tax.rate') /* 税率 */, dataIndex: 'taxRate' },
        { title: this.$t('common.tax') /* 税额 */, dataIndex: 'taxAmount' },
        {
          title: this.$t('expense.invoice.reimburse.number') /* 报账单号 */,
          dataIndex: 'expenseNum',
        },
        { title: this.$t('common.apply.data') /* 申请日期 */, dataIndex: 'applicationDate' },
        { title: this.$t('common.applicant'), dataIndex: 'applicant' },
        {
          title: this.$t('expense.invoice.documentStatus') /* 单据状态 */,
          dataIndex: 'documentState',
        },
        {
          title: this.$t('expense.wallet.expenseLineNo') /* 费用行号 */,
          dataIndex: 'costLineNumber',
        },
        { title: this.$t('expense.wallet.expenseType') /* 费用类型 */, dataIndex: 'costType' },
        { title: this.$t('expense.wallet.expenseAmount') /* 费用金额 */, dataIndex: 'costAmount' },
        {
          title: this.$t('expense.wallet.installment') /* 分期抵扣 */,
          dataIndex: 'installmentDeduction',
        },
      ],
      dataSource: [],
      invoiceId: '',
    };
  }

  componentDidMount() {
    this.taxRateSort();
  }

  // 税率排序
  taxRateSort = () => {
    service
      .getTaxRate()
      .then(res => {
        const { searchForm } = this.state;
        let data = res.data.map(item => item.value.split('%')[0]);
        data = data.sort((a, b) => a - b);
        const result = data.map(item => ({ label: `${item}%`, value: `${item}%` }));
        searchForm[7].options = result;
        this.setState({ searchForm });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 搜索
  search = values => {
    const params = {
      ...values,
      invoiceDateFrom: values.invoiceDateFrom && values.invoiceDateFrom.format('YYYY-MM-DD'),
      invoiceDateTo: values.invoiceDateTo && values.invoiceDateTo.format('YYYY-MM-DD'),
      applyDateFrom: values.applyDateFrom && values.applyDateFrom.format('YYYY-MM-DD'),
      applyDateTo: values.applyDateTo && values.applyDateTo.format('YYYY-MM-DD'),
    };
    this.detailTable.search(params);
  };

  // 导出
  confirmExport = params => {
    const hide = message.loading(this.$t('importer.spanned.file'));
    service
      .exportInvoiceDetail(params, this.props.user.id)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('finance.view.search.exportSuccess')); // 导出成功
          const fileName = res.headers['content-disposition'].split('filename=')[1];
          const f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          hide();
        }
      })
      .catch(() => {
        message.error(this.$t('importer.download.error.info')); // 下载失败，请重试
        hide();
      });
  };

  // 选择
  onSelect = selectedRowKeys => {
    this.setState({ selectedRowKeys });
  };

  // 显示发票
  showInvoiceDetail = record => {
    this.setState({ showInvoiceDetail: true, invoiceId: record.invoiceHeadId });
  };

  // 跳转报账单详情
  showBillDetail = id => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/reimburse-detail/${id}`,
      })
    );
  };

  checkboxChange = () => {};

  render() {
    const {
      searchForm,
      detailColumns,
      showInvoiceDetail,
      exportColumns,
      exportVisible,
      invoiceId,
    } = this.state;
    const rowSelection = { onSelect: this.onSelect };

    return (
      <div style={{ marginTop: '15px' }}>
        <SearchArea searchForm={searchForm} submitHandle={this.search} maxLength={4} />

        <div style={{ margin: '15px 0' }}>
          <Button onClick={() => this.setState({ exportVisible: true })}>
            {this.$t('common.export') /* 导出 */}
          </Button>
        </div>

        <CustomTable
          columns={detailColumns}
          rowSelection={rowSelection}
          scroll={{ x: 2200 }}
          url={`${config.expenseUrl}/api/invoice/head/query/invoice/line/dist/by/cond?createdBy=${
            this.props.user.id
          }`}
          ref={ref => (this.detailTable = ref)}
        />

        <Invoice
          cancel={() => this.setState({ showInvoiceDetail: false })}
          id={invoiceId}
          visible={showInvoiceDetail}
        />

        {/* 导出 */}
        <ExcelExporter
          visible={exportVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={this.$t('expense.wallet.invoiceDetail')} /* 发票报账明细 */
          onCancel={() => this.setState({ exportVisible: false })}
          excelItem="PREPAYMENT_FINANCIAL_QUERY"
        />
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

export default connect(mapStateToProps)(InvoiceDetails);
