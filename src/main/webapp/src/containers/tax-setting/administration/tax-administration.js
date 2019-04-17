/* eslint-disable no-sequences */
/**
 * Created by 5716 on 2019/3/7.
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Badge, message } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import SearchArea from 'widget/search-area';
import FileSaver from 'file-saver';
import ExcelExporter from 'widget/excel-exporter';
import WrappedOrganManagement from './create-or-update-tax';
import organManagementService from './tax-administration.service';

class OrganManagement extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      excelVisible: false,
      data: [],
      // eslint-disable-next-line react/no-unused-state
      slideFrame: {
        title: '',
        visible: false,
        params: {},
      },
      timestamp: new Date().valueOf(),
      columns: [
        {
          /* 税务机关代码 */
          title: '税务机关代码',
          dataIndex: 'taxDepartmentCode',
          key: 'taxDepartmentCode',
          align: 'center',
        },
        {
          /* 税务机关名称 */
          title: '税务机关名称',
          dataIndex: 'taxDepartment',
          key: 'taxDepartment',
          align: 'center',
        },
        {
          /* 税务机关地址 */
          title: '税务机关地址',
          dataIndex: 'taxDepartmentAddress',
          key: 'taxDepartmentAddress',
          align: 'center',
        },
        {
          /* 联系方式 */
          title: '联系方式',
          dataIndex: 'telephone',
          key: 'telephone',
          align: 'center',
        },
        {
          /* 是否启用 */
          title: '是否启用',
          dataIndex: 'enableFlag',
          key: 'status',
          align: 'center',
          render: enableFlag => (
            <Badge
              status={enableFlag ? 'success' : 'error'}
              text={enableFlag ? this.$t('common.status.enable') : this.$t('common.status.disable')}
            />
          ),
        },
      ],
      searchForm: [
        {
          type: 'input',
          colSpan: 6,
          id: 'taxDepartmentCode',
          label: '税务机关代码',
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'taxDepartment',
          label: '税务机关名称',
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
      searchParams: {
        taxDepartmentCode: '',
        taxDepartment: '',
      },
      updateParams: {},
      showSlideFrameNew: false,
      exportColumns: [
        { title: '税务机关代码', dataIndex: 'taxDepartmentCode' },
        { title: '税务机关名称', dataIndex: 'taxDepartment' },
        { title: '税务机关地址', dataIndex: 'taxDepartmentAddress' },
        { title: '联系方式', dataIndex: 'telephone' },
        { title: '是否启用', dataIndex: 'enableFlag' },
      ],
    };
  }

  componentWillMount() {
    this.getList();
  }

  // 获得数据
  getList() {
    const { searchParams, pagination } = this.state;
    const params = {};
    params.taxDepartmentCode = searchParams.taxDepartmentCode;
    params.taxDepartment = searchParams.taxDepartment;
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
    organManagementService
      .getSelfTaxList(params)
      .then(response => {
        // eslint-disable-next-line array-callback-return
        response.data.map(item => {
          // eslint-disable-next-line no-param-reassign
          item.key = item.id;
        });
        // eslint-disable-next-line no-unused-expressions
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
    this.setState({ pagination }, () => {
      this.getList();
    });
  };

  // 清空搜索区域
  clear = () => {
    this.setState({
      updateParams: {
        taxDepartmentCode: '',
        taxDepartment: '',
      },
    });
  };

  // 搜索
  search = result => {
    const searchParams = {
      taxDepartmentCode: result.taxDepartmentCode,
      taxDepartment: result.taxDepartment,
    };
    this.setState(
      {
        searchParams,
        loading: true,
        // eslint-disable-next-line react/no-unused-state
        page: 0,
        // eslint-disable-next-line react/no-unused-state
        current: 1,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 点击导出按钮
   */
  onExportClick = () => {
    this.setState({
      loading: true,
      excelVisible: true,
    });
  };

  handleClose = params => {
    this.setState(
      {
        showSlideFrameNew: false,
      },
      () => {
        // eslint-disable-next-line no-unused-expressions
        params && this.getList();
      }
    );
  };

  showSlideNew = flag => {
    this.setState({
      showSlideFrameNew: flag,
    });
  };

  newItemTypeShowSlide = () => {
    const timestamp = new Date().valueOf();
    this.setState(
      {
        timestamp,
        updateParams: {},
      },
      () => {
        this.showSlideNew(true);
      }
    );
  };

  putItemTypeShowSlide = recode => {
    const timestamp = new Date().valueOf();
    this.setState(
      {
        updateParams: recode,
        timestamp,
      },
      () => {
        this.showSlideNew(true);
      }
    );
  };

  // 导出
  handleDownLoad = result => {
    const { searchParams, pagination } = this.state;

    const ps = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };
    const hide = message.loading(this.$t({ id: 'importer.spanned.file' } /* 正在生成文件.. */));
    organManagementService
      .exportSelfTax(result, ps, searchParams)
      .then(response => {
        const b = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        });
        const name = this.$t({ id: '税务机关管理导出文件' });
        FileSaver.saveAs(b, `${name}.xlsx`);
        this.setState({
          loading: false,
        });
        hide();
      })
      .catch(() => {
        message.error(this.$t({ id: 'importer.download.error.info' } /* 下载失败，请重试 */));
        this.setState({
          // eslint-disable-next-line react/no-unused-state
          btLoading: false,
        });
        hide();
      });
  };

  /**
   * 导出取消
   */
  onExportCancel = () => {
    this.setState({
      loading: false,
      excelVisible: false,
    });
  };

  render() {
    const {
      columns,
      data,
      pagination,
      searchForm,
      showSlideFrameNew,
      loading,
      updateParams,
      timestamp,
      excelVisible,
      exportColumns,
    } = this.state;
    return (
      <div className="tax-administration">
        <div className="searchFrom">
          <SearchArea
            searchForm={searchForm}
            submitHandle={this.search}
            clearHandle={this.clear}
            maxLength={4}
            eventHandle={this.searchEventHandle}
          />
        </div>

        <div className="table-header">
          <div className="table-header-buttons">
            <Button type="primary" onClick={this.newItemTypeShowSlide}>
              {this.$t('common.create')}
            </Button>
            {/* <Button onClick={this.handleImportShow}>{this.$t({ id: 'importer.import' })}</Button>{' '} */}
            {/* <Button loading={loading} onClick={this.onExportClick}>
              {this.$t({ id: 'importer.importOut' })}
            </Button>{' '} */}
          </div>
        </div>
        <div className="Table_div" style={{ backgroundColor: 111 }}>
          <Table
            columns={columns}
            dataSource={data}
            pagination={pagination}
            loading={loading}
            bordered
            onRow={record => ({
              onClick: () => this.putItemTypeShowSlide(record),
            })}
            size="middle"
          />
        </div>

        <SlideFrame
          // eslint-disable-next-line react/destructuring-assignment
          title={JSON.stringify(this.state.updateParams) === '{}' ? '新建税务机关' : '编辑税务机关'}
          show={showSlideFrameNew}
          afterClose={this.handleCloseNewSlide}
          onClose={this.handleClose}
        >
          <WrappedOrganManagement
            params={{ updateParams, timestamp }}
            onClose={e => {
              this.handleClose(e);
            }}
          />
        </SlideFrame>
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.handleDownLoad}
          columns={exportColumns}
          canCheckVersion={false}
          fileName="税务机关管理"
          onCancel={this.onExportCancel}
          excelItem="PREPAYMENT_FINANCIAL_QUERY"
        />
      </div>
    );
  }
}

function mapStateToProps() {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(OrganManagement);
