/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import config from 'config';
import { Button, message, Spin } from 'antd';
import Table from 'widget/table';
import NewSeparationRules from './new-separation-rules';
import SearchArea from 'widget/search-area';
import SlideFrame from 'widget/slide-frame';
import ImporterNew from 'widget/Template/importer-new'; //导入
import ExcelExporter from 'widget/excel-exporter';
import Service from './service';
import { routerRedux } from 'dva/router';
import httpFetch from 'share/httpFetch';
import FileSaver from 'file-saver';
class TaxPriceSeparationRules extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      // excel表格不可见
      excelVisible: false,
      visibel: false,
      loading: true,
      showImportSeparateModel: false, //导入自定义银行弹窗
      data: [],
      exportColumns: [
        { title: '来源系统', dataIndex: 'sourceSystem' },
        { title: '来源数据价税状态', dataIndex: 'sourceDataStatus' },
        { title: '价税分离科目', dataIndex: 'separateAccountName' },
        { title: '税目', dataIndex: 'taxRateName' },
        { title: '增值税科目方向', dataIndex: 'drOrCr' },
        { title: '增值税科目', dataIndex: 'taxAccountName' },
        { title: '计税规则', dataIndex: 'taxRuleCodeName' },
        { title: '是否申报', dataIndex: 'declareFlag' },
        { title: '是否开票', dataIndex: 'invoiceFlag' },
        { title: '是否生成凭证', dataIndex: 'accountingFlag' },
        { title: '是否启用', dataIndex: 'enabled' },
      ],
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
          type: 'select',
          id: 'sourceDataStatus',
          placeholder: '请输入',
          label: '来源数据价税状态',
          colSpan: 6,
          options: [{ label: '已拆分', value: '1' }, { label: '未拆分', value: '2' }],
        },
        {
          type: 'lov',
          id: 'taxRateId',
          code: 'tax-rate-definition',
          label: '税目',
          valueKey: 'id',
          labelKey: 'taxCategoryName',
          single: true,
          extraParams: { taxCategoryCode: 'VAT', enabled: 'Y' },
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'taxRuleCode',
          placeholder: '请输入',
          label: '计税规则',
          colSpan: 6,
          options: [{ label: '一般计税', value: '1' }, { label: '简易计税', value: '2' }],
        },
        {
          type: 'select',
          id: 'declareFlag',
          placeholder: '请输入',
          label: '是否申报',
          colSpan: 6,
          options: [{ label: '是', value: '1' }, { label: '否', value: '0' }],
        },
        {
          type: 'select',
          id: 'invoiceFlag',
          placeholder: '请输入',
          label: '是否开票',
          colSpan: 6,
          options: [{ label: '是', value: '1' }, { label: '否', value: '0' }],
        },
        {
          type: 'select',
          id: 'accountingFlag',
          placeholder: '请输入',
          label: '是否生成凭证',
          colSpan: 6,
          options: [{ label: '是', value: '1' }, { label: '否', value: '0' }],
        },
        {
          type: 'select',
          id: 'enabled',
          placeholder: '请输入',
          label: '是否启用',
          colSpan: 6,
          options: [{ label: '是', value: '1' }, { label: '否', value: '0' }],
        },
      ],
      dataSource: [],
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
          dataIndex: 'sourceSystemName',
          align: 'center',
          width: 100,
        },
        {
          title: '来源数据价税状态',
          dataIndex: 'sourceDataStatusName',
          align: 'center',
          width: 160,
        },
        {
          title: '价税分离科目',
          dataIndex: 'separateAccountName',
          align: 'center',
          width: 120,
        },

        {
          title: '税目',
          dataIndex: 'taxRateName',
          align: 'center',
          width: 100,
        },

        {
          title: '增值税科目方向',
          dataIndex: 'drOrCr',
          align: 'center',
          width: 130,
        },
        {
          title: '增值税科目',
          dataIndex: 'taxAccountName',
          align: 'center',
          width: 120,
        },
        {
          title: '计税规则',
          dataIndex: 'taxRuleCodeName',
          align: 'center',
          width: 100,
        },
        {
          title: '是否申报',
          dataIndex: 'declareFlag',
          align: 'center',
          render: res => (res === true ? '是' : '否'),
          width: 100,
        },
        {
          title: '是否开票',
          dataIndex: 'invoiceFlag',
          align: 'center',
          render: res => (res === true ? '是' : '否'),
          width: 100,
        },
        {
          title: '是否生成凭证',
          dataIndex: 'accountingFlag',
          align: 'center',
          render: res => (res === true ? '是' : '否'),
          width: 120,
        },
        {
          title: '是否启用',
          dataIndex: 'enabled',
          align: 'center',
          render: res => (res === true ? '是' : '否'),
          width: 100,
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => (
            <div>
              <a onClick={() => this.edit(record)}>编辑</a>
            </div>
          ),
          width: 100,
        },
        {
          title: '开票规则',
          align: 'center',
          dataIndex: 'taxpayerName',
          key: 'taxpayerName',
          width: 100,
          render: (value, record) => (
            <a onClick={() => this.toVatInvoiceRule(record.id, record.taxpayerName)}>开票规则</a>
          ),
        },
      ],
      model: {},
      searchParams: {},
    };
  }

  componentWillMount() {
    this.getList();
    this.getTaxAccountingMethod();
    this.setColumns();
  }
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
  // 获得数据
  getList() {
    const { pagination, searchParams } = this.state;
    const params = { ...searchParams };
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
    Service.pageInvoicingSiteByCond(params)
      .then(response => {
        pagination.total = Number(response.headers['x-total-count']) || 0;
        response.data.map(item => {
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
              width: 150,
              align: 'center',
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

  // 关闭侧滑页面
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
  // 跳转开票规则定义页面
  toVatInvoiceRule = (id, taxCategoryName) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/rule-definition/vat-invoice-rule`,
      })
    );
  };

  // 导出
  //导出维值--可视化导出模态框
  handleExport = () => {
    this.setState({ excelVisible: true });
  };

  //确认导出
  confirmExport = result => {
    let hide = message.loading('正在生成文件，请等待......');
    const { dimensionId } = this.state;
    Service.exportSelfTax(result, { page: 1, size: 10 }, dimensionId)
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

  handleImportShow = () => {
    this.setState({
      showImportSeparateModel: true,
    });
  };
  handleFileUpload = () => {
    const { fileList } = this.state;
    const formData = new FormData();
    formData.append('file', fileList[0]);
    this.setState({
      uploading: true,
      flieUploading: true,
    });
    BSService.importSelfBank(formData)
      .then(res => {
        this.setState(
          {
            uploading: false,
            fileList: [],
            flieUploading: false,
            showImportSeparateModel: false,
            transactionOid: res.data.transactionOid, //这个transactionOid在导出错误信息的时候，需要用到
          },
          () => {
            this.showImportErrInfo();
            this.showTransactionLogDialog(this.state.transactionOid); // 将参数传给dialog
          }
        );
      })
      .catch(res => {
        this.setState({
          uploading: false,
          flieUploading: false,
        });
      });
  };
  handleImportOk = transactionID => {
    httpFetch
      .post(`${config.taxUrl}/api/tax/vat/separate/rule/import/new/confirm/${transactionID}`)
      .then(res => {
        if (res.status === 200) {
          this.getList();
        }
      })
      .catch(() => {
        message.error(this.$t('importer.import.error.info') /*导入失败，请重试*/);
      });
    this.showImport(false);
  };
  showImport = flag => {
    this.setState({ showImportSeparateModel: flag });
  };
  render() {
    const {
      columns,
      model,
      visibel,
      data,
      pagination,
      searchForm,
      exportColumns,
      excelVisible,
      showImportSeparateModel,
      loading,
    } = this.state;
    return (
      <Spin spinning={loading}>
        <div className="train">
          <SearchArea
            searchForm={searchForm}
            submitHandle={this.handleSearch}
            clearHandle={this.reset}
            maxLength={4}
          />
          <div style={{ margin: '20px 0' }}>
            {/*新建*/}
            <Button
              style={{ margin: '20px 20px 20px 0' }}
              className="create-btn"
              type="primary"
              onClick={this.create}
            >
              新建
            </Button>
            <Button
              style={{ margin: '10px 20px 10px 0' }}
              onClick={this.handleImportShow}
              type="primary"
            >
              导入
            </Button>
            <Button
              style={{ margin: '10px 20px 10px 0' }}
              onClick={this.handleExport}
              type="primary"
            >
              导出
            </Button>
            <Table
              columns={columns}
              dataSource={data}
              pagination={pagination}
              bordered
              rowKey="id"
              scroll={{ x: 1500 }}
            />
            <SlideFrame
              show={visibel}
              onClose={() => {
                this.setState({
                  visibel: false,
                  model: {},
                });
              }}
            >
              <NewSeparationRules params={model} close={this.close} />
            </SlideFrame>
          </div>
          {/* 导入 */}
          <ImporterNew
            visible={showImportSeparateModel}
            title={this.$t({ id: 'section.mapping.set.import' })}
            templateUrl={`${config.taxUrl}/api/tax/vat/separate/rule/template`}
            uploadUrl={`${config.taxUrl}/api/tax/vat/separate/rule/import/new`}
            errorUrl={`${config.taxUrl}/api/tax/vat/separate/rule/import/new/error/export`}
            errorDataQueryUrl={`${config.taxUrl}/api/tax/vat/separate/rule/import/new/query/result`}
            deleteDataUrl={`${config.taxUrl}/api/tax/vat/separate/rule/import/new/delete`}
            fileName={this.$t('bank.customBank.temp')}
            onOk={this.handleImportOk}
            afterClose={() => this.showImport(false)}
          />
          {/* 导出 */}
          <ExcelExporter
            visible={excelVisible}
            onOk={this.confirmExport}
            columns={exportColumns}
            canCheckVersion={false}
            fileName={'价税分离规则'}
            onCancel={this.onExportCancel}
            excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
          />
        </div>
      </Spin>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(TaxPriceSeparationRules);
