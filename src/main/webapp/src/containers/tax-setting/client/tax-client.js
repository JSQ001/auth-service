import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Menu, Dropdown, Icon, Row, Col, Input, message, Badge } from 'antd';
import config from 'config';
import moment from 'moment';

import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';

import service from './service';
import ExcelExporter from 'widget/excel-exporter';
import FileSaver from 'file-saver';

const Search = Input.Search;

const statusList = [
  { value: 'GENERATE', label: '编辑中' },
  { value: 'APPROVAL_PASS', label: '审批通过' },
  { value: 'APPROVAL', label: '审批中' },
  { value: 'APPROVAL_REJECT', label: '审批驳回' },
  { value: 'WITHDRAW', label: '撤回' },
];
const sourceList = [{ value: 'MANUAL', label: '手工' }, { value: 'INTERFACE', label: '系统接口' }];
const clientTypeList = [
  { value: '01', label: '企业' },
  { value: '02', label: '个人' },
  { value: '03', label: '事业单位' },
];

class TaxClientForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      excelVisible: false,
      pagination: {
        total: 0,
      },
      status: {
        GENERATE: { label: '编辑中', state: 'default' },
        APPROVAL_PASS: { label: '审批通过', state: 'success' },
        APPROVAL: { label: '审批中', state: 'processing' },
        APPROVAL_REJECT: { label: '审批驳回', state: 'error' },
        WITHDRAW: { label: '撤回', state: 'warning' },
      },
      source: {
        MANUAL: { label: '手工', state: '1' },
        INTERFACE: { label: '系统接口', state: '2' },
      },
      searchForm: [
        {
          type: 'input',
          id: 'clientNumber',
          label: '客户编号',
          options: [],
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'clientName',
          label: '客户名称',
          options: [],
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'clientType',
          label: '客户类型',
          options: clientTypeList,
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'importFlag',
          label: '数据来源',
          options: sourceList,
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '客户编号',
          dataIndex: 'clientNumber',
          align: 'center',
          width: 100,
          tooltips: true,
        },
        {
          title: '客户名称',
          dataIndex: 'clientName',
          align: 'center',
          width: 150,
        },
        // {
        //   title: '申请日期',
        //   dataIndex: 'requisitionDate',
        //   align: 'center',
        //   width: 120,
        //   render: value => moment(value).format('YYYY-MM-DD'),
        // },
        {
          title: '客户类型',
          dataIndex: 'clientTypeName',
          align: 'center',
          width: 90,
        },
        {
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          align: 'center',
          tooltips: true,
          width: 200,
        },
        {
          title: '数据来源',
          dataIndex: 'importFlag',
          align: 'center',
          width: 100,
          render: value => (
            <Badge status={this.state.source[value].state} text={this.state.source[value].label} />
          ),
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
      exportColumns: [
        { title: '客户编号', dataIndex: 'clientNumber' },
        { title: '客户名称', dataIndex: 'clientName' },
        { title: '客户类型', dataIndex: 'clientTypeName' },
        { title: '纳税人名称', dataIndex: 'taxpayerName' },
        { title: '数据来源', dataIndex: 'importFlag' },
      ],
      searchParams: {},
      menus: [],
    };
  }

  componentDidMount() {
    // this.getApplicationTypeList();
  }

  //获取列表
  getList = () => {
    let { searchParams } = this.state;

    this.table.search(searchParams);
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

  //搜索
  search = values => {
    this.setState({ searchParams: { ...this.state.searchParams, ...values } }, () => {
      this.getList();
    });
  };

  //清除
  clear = () => {
    this.setState({ searchParams: {} }, () => {
      this.getList();
    });
  };

  //跳转到详情
  handleRowClick = recode => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/basic-tax-information-management/tax-client/tax-client-detail/' + recode.id,
      })
    );
  };

  /**
   * 点击导出按钮
   */
  onExportClick = () => {
    this.setState({
      // loading: true,
      excelVisible: true,
    });
  };

  /**
   * 导出取消
   */
  onExportCancel = () => {
    this.setState({
      // loading: false,
      excelVisible: false,
    });
  };

  // 导出
  handleDownLoad = result => {
    const { searchParams, pagination } = this.state;

    const ps = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };
    const hide = message.loading(this.$t({ id: 'importer.spanned.file' } /* 正在生成文件.. */));
    service
      .exportTaxClient(result, ps, searchParams)
      .then(response => {
        const b = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        });
        const name = this.$t({ id: '客户信息查询导出文件' });
        FileSaver.saveAs(b, `${name}.xlsx`);
        this.setState({
          // loading: false,
        });
        hide();
      })
      .catch(() => {
        message.error(this.$t({ id: 'importer.download.error.info' } /* 下载失败，请重试 */));
        this.setState({
          // eslint-disable-next-line react/no-unused-state
          // btLoading: false,
        });
        hide();
      });
  };

  render() {
    const { searchForm, columns, menus, excelVisible, exportColumns } = this.state;

    return (
      <div className="reimburse-container">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          maxLength={4}
          clearHandle={this.clear}
        />
        <div className="table-header">
          <div className="table-header-buttons">
            <Button onClick={this.onExportClick}>{this.$t({ id: 'importer.importOut' })}</Button>{' '}
          </div>
        </div>
        <CustomTable
          onClick={this.handleRowClick}
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.taxUrl}/api/tax/client/query/condition`}
          onRowClick={this.handleRowClick}
        />

        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.handleDownLoad}
          columns={exportColumns}
          canCheckVersion={false}
          fileName="客户信息查询"
          onCancel={this.onExportCancel}
          excelItem="PREPAYMENT_FINANCIAL_QUERY"
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(mapStateToProps)(TaxClientForm);
