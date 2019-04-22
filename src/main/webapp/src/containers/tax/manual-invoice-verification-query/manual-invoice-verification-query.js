/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import { Badge, Button, Divider, Popconfirm, Popover, message, Tabs } from 'antd';
import SearchArea from 'widget/search-area';
import Table from 'widget/table';
import expensePolicyService from 'containers/setting/expense-policy/expense-policy.service';
import moment from 'moment';
import FileSaver from 'file-saver';
const TabPane = Tabs.TabPane;
import CustomTable from 'components/Widget/custom-table';
import ExcelExporter from 'widget/excel-exporter';
import SlideFrame from 'widget/slide-frame';
// import NewInvoicingSite from './new-invoicing-site';
import manualInvoiceVerificationQueryService from './manual-invoice-verification-query.service';
// import SeeDetails from './new-register-apply';

class ManualInvoiceVerificationQuery extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      record: {},
      visible: false,
      loading: false,
      newShow: false,
      taxwriteOff: [],
      //   tabValue: 'apply',
      data: [],
      searchParams: {
        typeFlag: props.match.params.typeFlag === ':typeFlag' ? '0' : props.match.params.typeFlag,
        setOfBooksId:
          props.match.params.sob === ':sob' ? props.company.setOfBooksId : props.match.params.sob,
      },
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
      },
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
          type: 'value_list',
          valueListCode: 'TAX_WRITE_OFF_STATUS',
          id: 'writeOffStatus',
          placeholder: '请选择',
          label: '核销状态',
          colSpan: 6,
          options: [],
        },
        // {
        //   type: 'select',
        //   id: 'applyStatus',
        //   label: '审批状态',
        //   colSpan: 6,
        //   options: [
        //     { label: '新建', value: '新建' },
        //     { label: '审批中', value: '审批中' },
        //     { label: '已拒绝', value: '已拒绝' },
        //     { label: '已审批', value: '已审批' },
        //   ],
        // },
        {
          type: 'lov',
          id: 'clientName',
          code: 'customer_information_query',
          label: '客户名称',
          valueKey: 'id',
          labelKey: 'clientName',
          single: true,
          colSpan: 6,
        },
      ],
      columns: [
        {
          //发票明细
          title: '发票明细',
          dataIndex: 'invoiceDetails',
          key: 'invoiceDetails',
          align: 'center',
          width: 200,
        },
        {
          //发票代码
          title: '发票代码',
          dataIndex: 'invoiceNumber',
          key: 'invoiceNumber',
          align: 'center',
          width: 200,
        },
        {
          //发票号码
          title: '发票号码',
          dataIndex: 'invoiceCode',
          key: 'invoiceCode',
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
          title: '开票状态',
          dataIndex: 'invoiceStatus',
          key: 'invoiceStatus',
          align: 'center',
        },
        {
          title: '开票日期',
          dataIndex: 'invoiceDate',
          key: 'invoiceDate',
          align: 'center',
        },
        {
          //纳税主体
          title: '纳税主体',
          dataIndex: 'taxpayerName',
          key: 'taxpayerName',
          align: 'center',
          width: 200,
        },
        {
          //客户名称
          title: '客户名称',
          dataIndex: 'clientName',
          key: 'clientName',
          align: 'center',
          width: 200,
        },
        {
          //价税合计金额
          title: '价税合计金额',
          dataIndex: 'totalPriceAndTax',
          key: 'totalPriceAndTax',
          align: 'center',
          width: 200,
        },
        {
          //不含税金额
          title: '不含税金额',
          dataIndex: 'totalSales',
          key: 'totalSales',
          align: 'center',
          width: 200,
        },

        {
          //税额
          title: '税额',
          dataIndex: 'totalTaxes',
          key: 'totalTaxes',
          align: 'center',
          width: 200,
        },
        {
          //申请人
          title: '申请人',
          dataIndex: 'applicantName',
          key: 'applicantName',
          align: 'center',
          width: 200,
        },
        {
          //事务状态
          title: '事务状态',
          dataIndex: 'applyStatus',
          key: 'applyStatus',
          align: 'center',
          width: 200,
        },
        // {
        //   //创建日期
        //   title: '创建日期',
        //   dataIndex: 'creationDate',
        //   key: 'creationDate',
        //   align: 'center',
        //   width: 200,
        // },
        // {
        //   //审批日期
        //   title: '审批日期',
        //   dataIndex: 'applyDate',
        //   key: 'applyDate',
        //   align: 'center',
        //   width: 200,
        // },
        {
          //发票状态
          title: '发票状态',
          dataIndex: 'invoiceStatus',
          key: 'invoiceStatus',
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
      model: {},
      exportColumns: [
        { title: '开票点编码', dataIndex: 'invoicingSiteCode' },
        { title: '开票点名称', dataIndex: 'invoicingSiteName' },
        { title: '所属纳税主体名称', dataIndex: 'taxpayerName' },
        { title: '开票点地址', dataIndex: 'invoicingSiteAdd' },
        { title: '税控钥匙密码', dataIndex: 'passwod' },
        { title: '开票终端标识', dataIndex: 'invoicingTerminal' },
        { title: '安全码', dataIndex: 'securityCode' },
        { title: '上边距', dataIndex: 'printTop' },
        { title: '左边距', dataIndex: 'printLeft' },
        { title: '备注', dataIndex: 'remarks' },
        { title: '状态', dataIndex: 'enabled' },
      ],
    };
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

  // 跳转开票权限分配页面
  toInvoicingDimension = (id, record) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/basic-tax-information-management/vat-invoicing-site/invoicing-dimension/${id}/${
          record.taxpayerId
        }`,
      })
    );
  };
  handleDelete = record => {
    expensePolicyService
      .deletePolicy(record.id)
      .then(res => {
        message.success(this.$t('common.delete.success'));
        this.customTable.search(this.state.searchParams);
      })
      .catch(e => {
        message.error(this.$t('common.delete.failed'));
      });
  };

  componentDidMount() {
    const { searchForm } = this.state;
    let params = { roleType: 'TENANT', enabled: true };
    let setOfBooksId =
      this.props.match.params.sob === ':sob'
        ? this.props.company.setOfBooksId
        : this.props.match.params.sob;
    expensePolicyService.getTenantAllSob(params).then(res => {
      searchForm[0].options = res.data.map(item => {
        if (item.id === setOfBooksId) {
          searchForm[0].defaultValue = {
            key: item.id,
            label: item.setOfBooksCode + '-' + item.setOfBooksName,
          };
        }
        return {
          key: item.id,
          value: item.id,
          label: item.setOfBooksCode + '-' + item.setOfBooksName,
        };
      });
      this.setState({ searchForm });
    });
  }

  // 根据申请编码跳转税务登记申请明细界面
  // eslint-disable-next-line react/sort-comp
  openDetails = record => {
    // eslint-disable-next-line react/no-unused-state
    record.sign = 1;
    this.setState({
      record: record,
      newShow: true,
      // showSlideFrameNew: true
    });
  };

  //员工信息，工号，电话等关键字是即时搜索
  // eventSearchAreaHandle = (e, item) => {
  //   let searchParams = this.state.searchParams;
  //   let pagination = this.state.pagination;
  //   pagination.page = 0;
  //   switch (e) {
  //     case 'SOB': {
  //       searchParams.setOfBooksId = item;
  //       break;
  //     }
  //     case 'expenseTypeId': {
  //       searchParams.expenseTypeId = item;
  //       break;
  //     }
  //     case 'dutyType': {
  //       searchParams.dutyType = item;
  //       break;
  //     }
  //     case 'companyLevelId': {
  //       searchParams.companyLevelId = item;
  //       break;
  //     }
  //   }
  //   this.setState(
  //     {
  //       pagination,
  //       searchParams,
  //     },
  //     () => {
  //       this.customTable.search(this.state.searchParams);
  //     }
  //   );
  // };
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
  clear = () => {
    // this.setState({
    //     searchParams: {
    //         setOfBook: '',
    //     },
    // });
    this.customTable.search();
  };

  // 新建
  create = () => {
    this.setState({
      visibel: true,
    });
  };
  //编辑
  edit = record => {
    console.log(record);
    this.setState({ model: JSON.parse(JSON.stringify(record)), visibel: true });
  };
  // // 新建、编辑费用政策
  // handleCreate = (e, record) => {
  //     !!record && e.preventDefault();
  //     !!record && e.stopPropagation();
  //     let id = record ? record.id : 'new';
  //     let url = record ? `edit-expense-policy/${record.id}` : e.id ? `expense-policy-detail/${e.id}` : 'new-expense-policy/new';
  //     this.props.dispatch(
  //         routerRedux.push({
  //             pathname: `/admin-setting/expense-policy/${url}/${
  //                 this.state.searchParams.setOfBooksId
  //                 }/${this.state.searchParams.typeFlag}`,
  //         })
  //     );
  // };

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
  //关闭侧拉框回调
  // close = flag => {
  //   this.setState({ visibel: false, model: {} }, () => {
  //     if (flag) {
  //       this.customTable.search();
  //     }
  //   });
  // };

  //关闭弹窗
  close = flag => {
    this.setState({ newShow: false, record: {} }, () => {
      // eslint-disable-next-line no-unused-expressions
      // flag && this.getList();
    });
  };

  newItemTypeShowSlide = () => {
    this.setState({ newShow: true });
  };

  a = () => {
    this.setState({
      newShow: true,
      record: {
        transactionType: 'MANUAL_INVOICE_APPLY',
        transactionTypeName: '手工开票申请',
        transactionStatus: 'GENERATE',
        listedCompany: true,
        overseasRegResEnt: false,
      },
      title: '手工开票申请',
    });
  };

  render() {
    const {
      columns,
      searchParams,
      searchForm,
      exportColumns,
      excelVisible,
      tabValue,
      model,
      visibel,
      newShow,
    } = this.state;
    return (
      <div className="train">
        {/* <Tabs defaultActiveKey={tabValue} onChange={this.handleTab}>
          <TabPane tab="费用申请政策" key="apply" />
          <TabPane tab="费用报销政策" key="export" />
        </Tabs> */}
        <SearchArea
          // eventHandle={this.eventSearchAreaHandle}
          searchForm={searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
          maxLength={4}
        />

        <div style={{ margin: '20px 0' }}>
          {/*新建*/}
          {/* <Button
            style={{ margin: '20px 20px 20px 0' }}
            className="create-btn"
            type="primary"
            onClick={this.a}
          >
            新建
          </Button> */}
          <Button style={{ marginRight: '20px' }} onClick={this.handleExport}>
            导出
          </Button>
          <CustomTable
            columns={columns}
            url={`${config.taxUrl}/api/tax/vat/manual/header/pageByCondition`}
            ref={ref => (this.customTable = ref)}
            scroll={{ x: 1500 }}
          />
        </div>
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'开票点信息'}
          onCancel={this.onExportCancel}
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
        />
        {newShow && <SeeDetails visible={newShow} onClose={this.close} />}
        {/* {newShow && <SeeDetails params={record} visible={newShow} onClose={this.close} />} */}
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

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(ManualInvoiceVerificationQuery);
