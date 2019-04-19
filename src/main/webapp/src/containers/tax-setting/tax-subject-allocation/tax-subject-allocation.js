/*eslint-disable*/
import React from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
import { Button, message } from 'antd';
import config from 'config';
import FileSaver from 'file-saver';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import ExcelExporter from 'widget/excel-exporter';
import TaxSubjectAllocationService from './tax-subject-allocation.service';

class TaxSubjectAllocation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // excel表格不可见
      excelVisible: false,
      visibel: false,
      model: {},
      exportColumns: [
        { title: '纳税人识别号', dataIndex: 'taxpayerNumber' },
        { title: '纳税人名称', dataIndex: 'taxpayerName' },
        { title: '税号类型', dataIndex: 'taxpayerNumberTypeName' },
        { title: '纳税资质', dataIndex: 'taxQualification' },
        { title: '税号状态', dataIndex: 'registrationStatus' },
      ],
      searchForm: [
        {
          type: 'input',
          id: 'taxpayerNumber',
          label: '纳税人识别号',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'taxpayerName',
          label: '纳税人名称',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'taxQualification',
          label: '纳税资质',
          colSpan: 6,
          options: [
            { label: '一般纳税人', value: '一般纳税人' },
            { label: '小规模纳税人', value: '小规模纳税人' },
          ],
        },
        {
          type: 'select',
          id: 'taxpayerNumberType',
          label: '税号类型',
          colSpan: 6,
          options: [
            { label: '普通税号', value: '普通税号' },
            { label: '临时税号', value: '临时税号' },
            { label: '虚拟税号', value: '虚拟税号' },
          ],
        },
        {
          type: 'select',
          id: 'status',
          label: '税号状态',
          colSpan: 6,
          options: [{ label: '启用', value: '启用' }, { label: '禁用', value: '禁用' }],
        },
      ],
      dataSource: [{}],
      columns: [
        {
          title: '纳税人识别号',
          align: 'center',
          dataIndex: 'taxpayerNumber',
          key: 'taxpayerNumber',
        },
        {
          title: '纳税人名称',
          align: 'center',
          dataIndex: 'taxpayerName',
          key: 'taxpayerName',
          render: (value, record) => (
            <a onClick={() => this.toTaxRegisterSee(record.id, record.taxpayerName)}>
              {record.taxpayerName}
            </a>
          ),
        },
        {
          title: '税号类型',
          align: 'center',
          dataIndex: 'taxpayerNumberTypeName',
          key: 'taxpayerNumberTypeName',
        },
        {
          title: '纳税资质',
          align: 'center',
          dataIndex: 'taxQualificationName',
          key: 'taxQualificationName',
        },
        {
          title: '核算主体分配',
          align: 'center',
          render: (value, record) => (
            <a onClick={() => this.toDistributionAccounting(record.id, value)}>核算主体分配</a>
          ),
        },
        {
          title: '税种信息维护',
          align: 'center',
          render: (value, record) => (
            <a onClick={() => this.toTaxInformationMaintenance(record.id, record.taxCategoryName)}>
              税种信息维护
            </a>
          ),
        },
        {
          title: '发票管理配置',
          align: 'center',
          render: (value, record) => (
            <a onClick={() => this.toInvoiceMgConfiguration(record.id, record.taxCategoryName)}>
              配置
            </a>
          ),
        },
        {
          title: '税号状态',
          align: 'center',
          dataIndex: 'status',
          key: 'status',
        },
      ],
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
  // 点击搜索按钮
  search = result => {
    let pagination = this.state.pagination;
    pagination.page = 0;
    pagination.current = 1;
    pagination.total = 0;
    this.setState(
      {
        pagination,
        // searchParams: Object.assign(this.state.searchParams, result),
        searchParams: { ...result },
      },
      () => {
        this.customTable.search(this.state.searchParams);
      }
    );
  };
  // 点击清空按钮
  reset = () => {
    // this.setState({
    //     searchParams: {
    //         setOfBook: '',
    //     },
    // });
    this.customTable.search();
  };

  //导出维值--可视化导出模态框
  handleExport = () => {
    this.setState({ excelVisible: true });
  };
  //确认导出
  confirmExport = result => {
    let hide = message.loading('正在生成文件，请等待......');
    const { dimensionId } = this.state;
    TaxSubjectAllocationService.exportSelfTax(result, { page: 1, size: 10 }, dimensionId)
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
  // 纳税人跳转
  toTaxRegisterSee = (id, taxCategoryName) => {
    const { dispatch } = this.props;

    dispatch(
      routerRedux.push({
        pathname: `/basic-tax-information-management/tax-register-see/tax-register-see`,
      })
    );
  };
  // 跳转核算主体分配页面
  toDistributionAccounting = (id, taxCategoryName) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/basic-tax-information-management/tax-subject-allocation/distribution-accounting/${id}`,
      })
    );
  };
  // 跳转税种信息维护页面
  toTaxInformationMaintenance = (id, taxCategoryName) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/basic-tax-information-management/tax-subject-allocation/tax-information-maintenance/${id}`,
      })
    );
  };
  // 跳转发票管理配置页面
  toInvoiceMgConfiguration = (id, taxCategoryName) => {
    const { dispatch } = this.props;
    console.log();
    dispatch(
      routerRedux.push({
        pathname: `/basic-tax-information-management/tax-subject-allocation/invoice-mg-configuration/${id}`,
      })
    );
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
    this.setState({ pagination }, () => {
      this.getList();
    });
  };
  render() {
    let { searchForm, columns, exportColumns, excelVisible } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.reset}
          maxLength={4}
        />
        <Button style={{ margin: '10px 20px 10px 0' }} onClick={this.handleExport} type="primary">
          导出
        </Button>
        <CustomTable
          columns={columns}
          url={`${config.taxUrl}/api/tax/taxRegister/basic/subject/configure`}
          ref={ref => (this.customTable = ref)}
        />
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'纳税主体信息管理'}
          onCancel={this.onExportCancel}
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
        />
      </div>
    );
  }
}
export default connect()(TaxSubjectAllocation);
