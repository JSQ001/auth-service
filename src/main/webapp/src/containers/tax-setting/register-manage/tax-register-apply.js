/* eslint-disable react/no-access-state-in-setstate */
/* eslint-disable react/destructuring-assignment */
/* eslint-disable eqeqeq */
/* eslint-disable react/no-unused-state */
/* eslint-disable no-sequences */
/**
 * Created by 5716 on 2019/3/7.
 */
import React from 'react';
import { connect } from 'dva';
import { Button, message, Col, Dropdown, Menu, Icon } from 'antd';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import SearchArea from 'widget/search-area';
import FileSaver from 'file-saver';
import ExcelExporter from 'widget/excel-exporter';
import registerApplyService from './tax-register-apply.service';
import SeeDetails from './new-register-apply';

class RegisterApply extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      excelVisible: false,
      newShow: false,
      cangeRecordShow: false,
      record: {},
      data: [],
      timestamp: new Date().valueOf(),
      columns: [
        {
          /* 申请编号 */
          title: '申请编号',
          dataIndex: 'applyCode',
          align: 'center',
          render: (value, record) => <a onClick={() => this.openDetails(record)}>{value}</a>,
        },
        {
          /* 纳税人名称 */
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          align: 'center',
        },
        {
          /* 纳税人识别号 */
          title: '纳税人识别号',
          dataIndex: 'taxpayerNumber',
          align: 'center',
        },
        {
          /* 税号类型 */
          title: '税号类型',
          dataIndex: 'taxpayerNumberTypeName',
          align: 'center',
        },
        {
          /* 纳税资质 */
          title: '纳税资质',
          dataIndex: 'taxQualificationName',
          align: 'center',
        },
        {
          /* 申请人 */
          title: '申请人',
          dataIndex: 'applicantName',
          align: 'center',
        },
        {
          /* 事务类型 */
          title: '事务类型',
          dataIndex: 'transactionTypeName',
          align: 'center',
        },
        {
          /* 事务状态 */
          title: '事务状态',
          dataIndex: 'transactionStatusName',
          align: 'center',
        },
      ],
      searchForm: [
        {
          type: 'input',
          colSpan: 6,
          id: 'taxpayerName',
          label: '纳税人名称',
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'taxpayerNumber',
          label: '纳税人识别号',
        },
        {
          type: 'select',
          id: 'taxpayerNumberType',
          label: '税号类型',
          event: 'taxpayerNumberType',
          defaultValue: '',
          options: [
            {
              label: '普通税号',
              value: 1,
            },
            {
              label: '临时税号',
              value: 2,
            },
            {
              label: '虚拟税号',
              value: 3,
            },
          ],
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'taxQualification',
          label: '纳税资质',
          event: 'taxQualification',
          defaultValue: '',
          options: [
            {
              label: '一般纳税人',
              value: 1,
            },
            {
              label: '小规模纳税人',
              value: 2,
            },
          ],
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'transactionType',
          label: '事务类型',
          event: 'transactionType',
          defaultValue: '',
          options: [
            {
              label: '税务登记新增',
              value: 'TAX_REGISTER_NEW',
            },
            {
              label: '税务登记修改',
              value: 'TAX_REGISTER_EDIT',
            },
            {
              label: '税务登记注销',
              value: 'TAX_REGISTER_CANCEL',
            },
          ],
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'transactionStatus',
          label: '事务状态',
          event: 'transactionStatus',
          defaultValue: '',
          options: [
            {
              label: '编辑中',
              value: 'GENERATE',
            },
            {
              label: '审批中',
              value: 'APPROVAL',
            },
            {
              label: '审批驳回',
              value: 'APPROVAL_REJECT',
            },
            {
              label: '审批通过',
              value: 'APPROVAL_PASS',
            },
          ],
          colSpan: 6,
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
        taxpayerName: '',
        taxpayerNumber: '',
        taxpayerNumberType: null,
        taxQualification: null,
        transactionType: null,
        transactionStatus: null,
      },
      updateParams: {},
      showSlideFrameNew: false,
      exportColumns: [
        { title: '申请编码', dataIndex: 'applyCode' },
        { title: '纳税人名称', dataIndex: 'taxpayerName' },
        { title: '纳税人识别号', dataIndex: 'taxpayerNumber' },
        { title: '税号类型', dataIndex: 'taxpayerNumberTypeName' },
        { title: '纳税资质', dataIndex: 'taxQualificationName' },
        { title: '申请人', dataIndex: 'applicantName' },
        { title: '事务类型', dataIndex: 'transactionTypeName' },
        { title: '事务状态', dataIndex: 'transactionStatusName' },
      ],
      selectedRowKeys: [],
    };
  }

  componentWillMount() {
    this.getList();
  }

  // 根据申请编码跳转税务登记申请明细界面
  // eslint-disable-next-line react/sort-comp
  openDetails = record => {
    // eslint-disable-next-line react/no-unused-state
    this.setState({ record: { ...record, sign: 1 }, newShow: true });
  };

  // 获得数据
  getList() {
    const { searchParams, pagination } = this.state;
    const params = {};
    params.taxpayerName = searchParams.taxpayerName;
    params.taxpayerNumber = searchParams.taxpayerNumber;
    params.taxpayerNumberType = searchParams.taxpayerNumberType;
    params.taxQualification = searchParams.taxQualification;
    params.transactionType = searchParams.transactionType;
    params.transactionStatus = searchParams.transactionStatus;
    params.page = pagination.current - 1;
    params.size = pagination.pageSize;
    registerApplyService
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

  close = flag => {
    this.setState({ newShow: false, record: {} }, () => {
      // eslint-disable-next-line no-unused-expressions
      flag && this.getList();
    });
  };

  // 清空搜索区域
  clear = () => {
    this.setState({
      updateParams: {
        taxpayerName: '',
        taxpayerNumber: '',
        taxpayerNumberType: '',
        taxQualification: '',
        transactionType: '',
        transactionStatus: '',
      },
    });
  };

  // 搜索
  search = result => {
    const searchParams = {
      taxpayerName: result.taxpayerName,
      taxpayerNumber: result.taxpayerNumber,
      taxpayerNumberType: result.taxpayerNumberType,
      taxQualification: result.taxQualification,
      transactionType: result.transactionType,
      transactionStatus: result.transactionStatus,
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

  cangeClose = params => {
    this.setState(
      {
        cangeRecordShow: false,
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
    this.setState({ newShow: true });
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
    registerApplyService
      .exportAppleTax(result, ps, searchParams)
      .then(response => {
        const b = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        });
        const name = this.$t({ id: '税务登记申请导出文件' });
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

  /**
   * 变更历史
   */
  cangeRecord = () => {
    console.log(this.state.selectedRowKeys[0]);
    if (this.state.selectedRowKeys && this.state.selectedRowKeys[0]) {
      this.setState({
        cangeRecordShow: true,
        record: {
          id: this.state.selectedRowKeys[0],
        },
        title: '查看变更记录',
      });
    } else {
      message.error('请先勾选一行！');
    }
  };

  // 跳转到新建页面
  newForm = e => {
    if (e.key == 1) {
      this.setState({
        newShow: true,
        record: {
          transactionType: 'TAX_REGISTER_NEW',
          transactionTypeName: '税务登记新增',
          transactionStatus: 'GENERATE',
          listedCompany: true,
          overseasRegResEnt: false,
        },
        title: '新建税务登记',
      });
    } else if (e.key == 2) {
      this.setState({
        newShow: true,
        record: {
          transactionType: 'TAX_REGISTER_EDIT',
          transactionTypeName: '税务登记修改',
          transactionStatus: 'GENERATE',
          overseasRegResEnt: false,
          listedCompany: true,
        },
        title: '修改税务登记',
      });
    } else if (e.key == 3) {
      this.setState({
        newShow: true,
        record: {
          transactionType: 'TAX_REGISTER_CANCEL',
          transactionTypeName: '税务登记注销',
          transactionStatus: 'GENERATE',
          overseasRegResEnt: false,
          listedCompany: true,
        },
        title: '注销税务登记',
      });
    }
  };

  selectCellValue = selectedRowKeys => {
    this.setState(
      {
        selectedRowKeys,
      },
      () => {}
    );
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
      record,
      newShow,
    } = this.state;

    const menusUi = (
      <Menu onClick={this.newForm}>
        <Menu.Item key={1}>新建税务登记申请单</Menu.Item>
        <Menu.Item key={2}>修改税务登记申请单</Menu.Item>
        <Menu.Item key={3}>注销税务登记申请单</Menu.Item>
      </Menu>
    );

    // const rowSelection = {
    //   type: 'radio',
    //   selectedRowKeys,
    //   onChange: this.selectCellValue,
    // }

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
            {/* <Button type="primary" onClick={this.newItemTypeShowSlide}>
              {this.$t('common.create')}
            </Button> */}
            <Col id="application-form-drop" style={{ position: 'relative' }} span={4}>
              <Dropdown
                getPopupContainer={() => document.getElementById('application-form-drop')}
                trigger={['click']}
                overlay={menusUi}
              >
                <Button type="primary">
                  税务登记申请单<Icon type="down" />
                </Button>
              </Dropdown>
            </Col>
            {/* <Button onClick={this.handleImportShow}>{this.$t({ id: 'importer.import' })}</Button>{' '} */}
            <Button onClick={this.onExportClick}>{this.$t({ id: 'importer.importOut' })}</Button>{' '}
          </div>
        </div>
        <div className="Table_div" style={{ backgroundColor: 111 }}>
          <Table
            columns={columns}
            dataSource={data}
            pagination={pagination}
            loading={loading}
            // rowSelection={rowSelection}
            bordered
            // eslint-disable-next-line no-shadow
            // onRow={record => ({
            //   onClick: () => this.putItemTypeShowSlide(record),
            // })}
            size="middle"
          />
        </div>

        <SlideFrame
          // eslint-disable-next-line react/destructuring-assignment
          title={JSON.stringify(this.state.updateParams) === '{}' ? '新建税务登记' : '编辑税务登记'}
          show={showSlideFrameNew}
          afterClose={this.handleCloseNewSlide}
          onClose={this.handleClose}
        >
          <SeeDetails
            params={{ updateParams, timestamp }}
            visible={newShow}
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
          fileName="税务登记申请"
          onCancel={this.onExportCancel}
          excelItem="PREPAYMENT_FINANCIAL_QUERY"
        />
        <SeeDetails params={record} visible={newShow} onClose={this.close} />
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
)(RegisterApply);
