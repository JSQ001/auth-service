/*eslint-disable*/
import React from 'react';
import SearchArea from 'widget/search-area';
import { routerRedux } from 'dva/router';
import { Button, Badge, message } from 'antd';
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
      selectedRowKeys: [],
      //搜索
      searchForm: [
        {
          type: 'select',
          id: 'invoiceType',
          label: '发票类型',
          colSpan: 6,
          options: [
            { label: '普通发票', value: '普通发票' },
            { label: '专用发票', value: '专用发票' },
            { label: '电子普票', value: '电子普票' },
          ],
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
          type: 'list',
          id: 'orgId',
          listType: 'orgame',
          labelKey: 'name',
          valueKey: 'orgId',
          single: true,
          listExtraParams: {},
          label: '机构',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'costCenterId',
          listType: 'costCenter',
          labelKey: 'costCenterName',
          valueKey: 'costCenterId',
          listExtraParams: {},
          label: '责任中心',
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
          type: 'select',
          id: 'writeOffStaus',
          label: '核销状态',
          colSpan: 6,
          options: [
            { label: '未核销', value: '未核销' },
            { label: '部分核销', value: '部分核销' },
            { label: '全部核销', value: '全部核销' },
          ],
        },
        {
          type: 'input',
          id: 'taxpyerNum',
          label: '纳税人识别号',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'currencyCode',
          label: '币种',
          colSpan: 6,
          options: [
            { label: '币种1', value: '币种1' },
            { label: '币种2', value: '币种2' },
            { label: '币种3', value: '币种3' },
          ],
        },
        {
          type: 'input',
          id: 'canAmount',
          label: '可开票金额',
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'taxId',
          listType: 'tax',
          labelKey: 'taxName',
          valueKey: 'taxId',
          listExtraParams: {},
          label: '税目',
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
        },
        {
          //交易名称
          title: '交易名称',
          dataIndex: 'tradedName',
          key: 'tradedName',
          align: 'center',
          width: 150,
        },
        {
          //费用项目
          title: '费用项目',
          dataIndex: 'expenseItem',
          key: 'expenseItem',
          align: 'center',
          width: 150,
        },
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
          //税目代码
          title: '税目代码',
          dataIndex: 'taxRateId',
          key: 'taxRateId',
          align: 'center',
          width: 150,
        },
        {
          //税目名称
          title: '税目名称',
          dataIndex: 'taxName',
          key: 'taxName',
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
          // render: (value, record) => (
          //   <a onClick={() => this.toAlreadyAmount(record.id, record)}></a>
          // )
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
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
      },
    };
  }
  // 跳转已开票金额(本币)页面
  // toAlreadyAmount = (id, record) => {
  //   const {dispatch} = this.props;
  //   dispatch(
  //     routerRedux.push({
  //       pathname: `/already-amount/already-amount/${id}`
  //     })
  //   )
  // }
  componentWillMount() {
    this.getList();
  }
  // 获得数据
  getList() {
    const { searchParams, pagination } = this.state;
    const params = {};
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
  // toCreateInoviceApply = (id, record) => {
  //   const {dispatch} = this.props;
  //   dispatch(
  //     routerRedux.push({
  //       pathname: `/appplication-transaction-flow-invoice/appplication-transaction-flow-invoice${id}`
  //     })
  //   )
  // }
  // 创建开票申请
  handleSubmitClick = () => {
    // const selectedRowKeys = this.state.selectedRowKeys;
    const selectedRow = this.state.selectedRow;
    Service.routerNewPage(selectedRow)
      .then(res => {
        if (res.status === 200) {
          // this.toCreateInoviceApply(selectedRowKeys, record);
          message.success('提交成功！');
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
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
  search = values => {
    console.log(values);
  };
  //重置
  reset = () => {};
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
    console.log(record);
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
          submitHandle={this.search}
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
