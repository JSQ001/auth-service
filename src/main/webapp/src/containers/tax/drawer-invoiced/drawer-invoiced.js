/*eslint-disable*/
import React from 'react';
import SearchArea from 'widget/search-area';
import { Button, Badge, message } from 'antd';
import Table from 'widget/table';
import config from 'config';
import { connect } from 'dva';
import ExcelExporter from 'widget/excel-exporter';
import SlideFrame from 'widget/slide-frame';
import FileSaver from 'file-saver';
import CustomTable from 'components/Widget/custom-table';
import invoiceRuleService from './service';
class VatInvoiceRule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      data: [],
      //搜索
      searchForm: [
        {
          type: 'value_list',
          valueListCode: 'TAX_SOURCE_SYSTEM',
          id: 'sourceSystem',
          placeholder: '请选择',
          label: '系统名称',
          colSpan: 6,
          options: [],
        },
        {
          type: 'lov',
          id: 'commodityId',
          code: 'commodity',
          label: '商品编码',
          valueKey: 'id',
          labelKey: 'commodityCode',
          single: true,
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
          type: 'input',
          id: 'invoiceTitle',
          label: '开票名称',
          colSpan: 6,
        },
      ],
      //表格
      columns: [
        {
          //系统名称
          title: '系统名称',
          dataIndex: 'sourceSystem',
          key: 'sourceSystem',
          align: 'center',
          width: 150,
        },
        {
          //数据类型
          title: '数据类型',
          dataIndex: 'dataType',
          key: 'dataType',
          align: 'center',
          width: 150,
        },
        {
          //商品编码
          title: '商品编码',
          dataIndex: 'commodityId',
          key: 'commodityId',
          align: 'center',
          width: 100,
        },
        {
          //开票名称
          title: '开票名称',
          dataIndex: 'invoiceTitle',
          key: 'invoiceTitle',
          align: 'center',
          width: 100,
        },
        {
          //发票类型
          title: '发票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
          align: 'center',
          width: 100,
        },
        {
          //单位
          title: '单位',
          dataIndex: 'unit',
          key: 'unit',
          align: 'center',
          width: 80,
        },
        {
          //状态
          title: '状态',
          dataIndex: 'enableFlag',
          key: 'enableFlag',
          align: 'center',
          render: enableFlag => (
            <Badge
              status={enableFlag ? 'success' : 'error'}
              text={enableFlag ? this.$t('common.status.enable') : this.$t('common.status.disable')}
            />
          ),
          width: 60,
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => {
            return <a onClick={() => this.edit(record)}>编辑</a>;
          },
          width: 80,
        },
      ],
      // excel表格不可见
      excelVisible: false,
      visibel: false,
      model: {},
      exportColumns: [
        { title: '系统名称', dataIndex: 'sourceSystem' },
        { title: '数据类型', dataIndex: 'dataType' },
        // { title: '交易名称', dataIndex: 'taxpayerName' },
        // { title: '费用项目', dataIndex: 'invoicingSiteAdd' },
        { title: '商品编码', dataIndex: 'commodityId' },
        { title: '开票名称', dataIndex: 'invoiceTitle' },
        { title: '发票类型', dataIndex: 'invoiceType' },
        { title: '单位', dataIndex: 'unit' },
        { title: '状态', dataIndex: 'enableFlag' },
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
      searchParams: {
        sourceSystem: '',
        commodityId: '',
        invoiceTitle: '',
        invoiceType: '',
      },
    };
  }

  componentWillMount() {
    this.getList();
    this.setColumns();
  }

  // 获得数据
  getList() {
    const { searchParams, pagination } = this.state;
    const params = { ...searchParams };
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
    invoiceRuleService
      .getInvoicexRuleList(params)
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
  //编辑
  edit = record => {
    this.setState({ model: JSON.parse(JSON.stringify(record)), visibel: true });
  };
  //关闭侧拉框回调
  close = flag => {
    this.setState({ visibel: false, model: {} }, () => {
      flag && this.getList();
    });
  };
  //获取下拉列表的option
  getTaxAccountingMethod() {
    // eslint-disable-next-line prefer-const
    let sourceSystemMethodOptions = [];
    this.getSystemValueList('TAX_SOURCE_SYSTEM').then(res => {
      res.data.values.map(data => {
        console.log(data);
        sourceSystemMethodOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        sourceSystemMethodOptions,
      });
    });
  }
  // 动态创建columns
  setColumns = () => {
    const { columns } = this.state;
    invoiceRuleService.getColumns().then(res => {
      if (res.data && res.data.length) {
        columns.splice(
          2,
          0,
          ...res.data.map(item => {
            return {
              dataIndex: item.dimensionCode,
              title: item.dimensionName,
              width: 100,
              align: 'center',
            };
          })
        );
        this.setState({ columns });
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
    invoiceRuleService
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
      data,
      model,
      columns,
      pagination,
      exportColumns,
      excelVisible,
      loading,
      visibel,
    } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.reset}
        />
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
            dataSource={data}
            pagination={pagination}
            loading={loading}
            bordered
            rowKey="id"
            size="middle"
            scroll={{ x: 1500 }}
          />
        </div>
        {/* 导出  */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'开票规则定义'}
          onCancel={this.onExportCancel}
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
        />
      </div>
    );
  }
}
export default connect()(VatInvoiceRule);
