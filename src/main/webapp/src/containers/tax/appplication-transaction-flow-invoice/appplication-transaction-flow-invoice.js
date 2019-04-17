/*eslint-disable*/
import React from 'react';
import SearchArea from 'widget/search-area';
import { Button, Badge } from 'antd';
import Table from 'widget/table';
import config from 'config';
import { connect } from 'dva';
import ExcelExporter from 'widget/excel-exporter';
import SlideFrame from 'widget/slide-frame';
// import NewInvoicingSite from '../../tax-setting/vat-invoicing-site/new-invoicing-site';
import CustomTable from 'components/Widget/custom-table';

class VatInvoiceRule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      //搜索
      searchForm: [
        {
          type: 'input',
          id: 'applyNumber',
          label: '申请编号',
          colSpan: 6,
        },
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
          type: 'select',
          id: 'applyStatus',
          label: '审批状态',
          colSpan: 6,
          options: [
            { label: '新建', value: '新建' },
            { label: '审批中', value: '审批中' },
            { label: '已拒绝', value: '已拒绝' },
            { label: '已审批', value: '已审批' },
          ],
        },
        {
          type: 'select',
          id: 'invoiceStatus',
          label: '发票状态',
          colSpan: 6,
          options: [
            { label: '全部开票成功', value: '全部开票成功' },
            { label: '部分开票成功', value: '部分开票成功' },
            { label: '全部开票失败', value: '全部开票失败' },
          ],
        },
      ],
      //表格
      columns: [
        {
          //申请编号
          title: '申请编号',
          dataIndex: 'applyNumber',
          key: 'applyNumber',
          align: 'center',
          width: 200,
        },
        {
          //纳税主体
          title: '纳税主体',
          dataIndex: 'taxpayer',
          key: 'taxpayer',
          align: 'center',
          width: 200,
        },
        {
          //发票类型
          title: '发票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
          align: 'center',
          width: 200,
        },
        {
          //开票名称
          title: '开票名称',
          dataIndex: 'invoiceTitle',
          key: 'invoiceTitle',
          align: 'center',
          width: 200,
        },
        {
          //销项盘商
          title: '销项盘商',
          dataIndex: 'outputTaxDiskFirm',
          key: 'outputTaxDiskFirm',
          align: 'center',
          width: 200,
        },
        {
          //申请人
          title: '申请人',
          dataIndex: 'applyPerson',
          key: 'applyPerson',
          align: 'center',
          width: 200,
        },
        {
          //创建日期
          title: '创建日期',
          dataIndex: 'creationDate',
          key: 'creationDate',
          align: 'center',
          width: 200,
        },
        {
          //审批状态
          title: '审批状态',
          dataIndex: 'applyStatus',
          key: 'applyStatus',
          align: 'center',
          width: 200,
        },
        {
          //审批日期
          title: '审批日期',
          dataIndex: 'applyDate',
          key: 'applyDate',
          align: 'center',
          width: 200,
        },
        {
          //发票状态
          title: '发票状态',
          dataIndex: 'invoiceStatus',
          key: 'invoiceStatus',
          align: 'center',
          width: 200,
        },
        {
          //备注
          title: '备注',
          dataIndex: 'memo',
          key: 'memo',
          align: 'center',
          width: 200,
        },
        {
          title: '操作',
          dataIndex: 'operation',
          key: 'operation',
          align: 'center',
          width: 200,
          render: (value, record) => {
            return <a onClick={() => this.edit(record)}>编辑</a>;
          },
        },
      ],
      // excel表格不可见
      excelVisible: false,
      visibel: false,
      selectedKey: [],
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
  edit = record => {
    this.setState({ model: JSON.parse(JSON.stringify(record)), visibel: true });
  };
  //关闭侧拉框回调
  close = flag => {
    this.setState({ visibel: false, model: {} }, () => {
      if (flag) {
        this.customTable.search();
      }
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

  selectChange = key => {
    this.setState({ selectedKey: key });
  };

  render() {
    const {
      searchForm,
      columns,
      pagination,
      exportColumns,
      excelVisible,
      loading,
      visibel,
      selectedKey,
    } = this.state;
    const rowSelection = {
      onChange: this.selectChange,
      selectedRowKeys: selectedKey,
    };
    return (
      <div>
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.reset} />
        {/* <div style={{ margin: '10px 10px' }}>
          <Button type='primary' onClick={this.export}>导出</Button>
        </div> */}
        <div className="table-header">
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.handleExport}>
              导出
            </Button>
          </div>
        </div>
        <div className="Table_div" style={{ backgroundColor: 111 }}>
          <Table
            columns={columns}
            pagination={pagination}
            loading={loading}
            bordered
            size="middle"
            rowSelection={rowSelection}
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
        <SlideFrame
          // title={model.id ? '编辑税种' : '新增税种'}
          show={visibel}
          onClose={() => {
            this.setState({
              visibel: false,
              model: {},
            });
          }}
        >
          {/* <NewInvoicingSite params={model} close={this.close} /> */}
        </SlideFrame>
      </div>
    );
  }
}
export default connect()(VatInvoiceRule);
