import React, { Component } from 'react';
import { connect } from 'dva';
import config from 'config';
import SearchArea from 'widget/search-area';
//import baseService from 'share/base.service';
import { Input, Button, Badge, Divider, message, Popover, Row, Col, Modal } from 'antd';
import Table from 'widget/table';
const Search = Input.Search;
import prePaymentService from './application-view.service';
import moment from 'moment';
import ExcelExporter from 'widget/excel-exporter';
import ListSelector from 'widget/list-selector';
import FileSaver from 'file-saver';
import PayDetail from 'containers/pay/pay-workbench/payment-detail'; //支付详情
import { routerRedux } from 'dva/router';
//import costCenter from 'src/containers/setting/cost-center/cost-center';

class PerPaymentView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      /**
       * 查询条件
       */
      status: {
        1001: { label: '编辑中', state: 'default' },
        1004: { label: '审批通过', state: 'success' },
        1002: { label: '审批中', state: 'processing' },
        1005: { label: '审批驳回', state: 'error' },
        1003: { label: '撤回', state: 'warning' },
        0: { label: '未知', state: 'warning' },
        2004: { label: '支付成功', state: 'success' },
        2003: { label: '支付中', state: 'processing' },
        2002: { label: '审核通过', state: 'success' },
        2001: { label: '审核驳回', state: 'error' },
      },
      searchForm: [
        {
          type: 'list',
          id: 'companyId',
          label: '单据公司',
          colSpan: '6',
          listType: 'available_company',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          valueKey: 'id',
          labelKey: 'name',
          single: true,
        },
        {
          type: 'select',
          id: 'typeId',
          label: '单据类型',
          colSpan: 6,
          getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${
            this.props.company.setOfBooksId
          }&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
        },
        {
          type: 'list',
          id: 'applyId',
          label: '申请人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'select',
          id: 'status',
          label: '状态',
          colSpan: '6',
          options: [
            { value: 1001, label: '编辑中' },
            { value: 1002, label: '审批中' },
            { value: 1003, label: '撤回' },
            { value: 1004, label: '审批通过' },
            { value: 1005, label: '审批驳回' },
          ],
          valueKey: 'value',
          labelkey: 'label',
        },
        {
          type: 'list',
          id: 'unitId',
          label: '单据部门',
          colSpan: '6',
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentId',
          single: true,
        },
        {
          type: 'items',
          id: 'applyDate',
          colSpan: 6,
          items: [
            { type: 'date', id: 'applyDateFrom', label: '申请日期从' },
            { type: 'date', id: 'applyDateTo', label: '申请日期至' },
          ],
        },
        {
          type: 'select',
          label: '币种',
          id: 'currencyCode',
          colSpan: '6',
          options: [],
          method: 'get',
          getUrl: `${config.mdataUrl}/api/currency/rate/list`,
          listKey: 'records',
          getParams: {
            enable: true,
            setOfBooksId: this.props.company.setOfBooksId,
            tenantId: this.props.company.tenantId,
          },
          valueKey: 'currencyCode',
          labelKey: 'currencyCodeAndName',
          event: 'currencyCode',
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
          type: 'items',
          id: 'associatedAmount',
          colSpan: 10,
          items: [
            { type: 'input', id: 'associatedAmountFrom', label: '报账已关联金额从' },
            { type: 'input', id: 'associatedAmountTo', label: '报账已关联金额至' },
          ],
        },
        {
          type: 'items',
          id: 'relevanceAmount',
          colSpan: 10,
          items: [
            { type: 'input', id: 'relevanceAmountFrom', label: '报账可关联金额从' },
            { type: 'input', id: 'relevanceAmountTo', label: '报账可关联金额至' },
          ],
        },
        {
          type: 'select',
          id: 'closed_flag',
          label: '关闭状态',
          options: [
            { value: 1001, label: '未关闭' },
            { value: 1002, label: '部分关闭' },
            { value: 1003, label: '已关闭' },
          ],
          labelKey: 'value',
          valueKey: 'label',
        },
        {
          type: 'input',
          id: 'remark',
          label: '备注',
          colSpan: 6,
        },
      ],
      /**
       * 表格
       */
      columns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: '120',
          render: (documentNumber, record) => {
            return (
              <Popover content={documentNumber}>
                <a onClick={() => this.handleLink(record)}>{documentNumber}</a>
              </Popover>
            );
          },
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          width: '120',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '单据部门',
          dataIndex: 'departmentName',
          width: '100',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '单据类型',
          dataIndex: 'typeName',
          width: '120',
          render: typeName => {
            return <Popover content={typeName}>{typeName}</Popover>;
          },
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '申请日期',
          dataIndex: 'requisitionDate',
          width: '120',
          render: requisitionDate => {
            return <span>{moment(requisitionDate).format('YYYY-MM-DD')}</span>;
          },
        },
        {
          title: '币种',
          dataIndex: 'currencyCode',
          width: '100',
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: '100',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: '报销已关联金额',
          dataIndex: 'associatedAmount',
          width: '100',
          render: associatedAmount => {
            return <span>{this.filterMoney(associatedAmount, 2)}</span>;
          },
        },
        {
          title: '报销可关联金额',
          dataIndex: 'relevanceAmount',
          width: '100',
          render: relevanceAmount => {
            return <span>{this.filterMoney(relevanceAmount, 2)}</span>;
          },
        },
        {
          title: '备注',
          dataIndex: 'remarks',
          render: remarks => {
            return <Popover content={remarks}>{remarks}</Popover>;
          },
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: '100',
          render: value => (
            <Badge status={this.state.status[value].state} text={this.state.status[value].label} />
          ),
        },
        {
          title: '关闭状态',
          dataIndex: 'closedFlag',
          width: '100',
        },
        {
          title: '查看信息',
          dataIndex: 'view',
          width: '100',
          render: (view, record, index) => {
            return (
              <div>
                <a onClick={e => this.onReqClick(e, record, index)}>报账单</a>
                <Divider type="vertical" />
                <a
                  onClick={e => {
                    this.onPayClick(e, record, index);
                  }}
                >
                  预付款单
                </a>
              </div>
            );
          },
        },
      ],
      loading: true,
      pagination: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      paginationr: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      data: [],
      datar: [],
      expandedRowRenderList: [],
      searchParam: {},
      showChild: false,
      detailId: undefined,
      page: 0,
      pageSize: 10,
      pager: 0,
      pageSizer: 10,
      keys: [],
      /**
       * 弹窗-预付款单
       */
      lsVisible: false,
      extraParams: {},
      selectorItem: {},
      /**
       * 导出
       */
      excelVisible: false,
      btLoading: false,
      exportColumns: [
        { title: '单据编号', dataIndex: 'documentNumber' },
        { title: '单据公司', dataIndex: 'companyName' },
        { title: '单据部门', dataIndex: 'departmentName' },
        { title: '单据类型', dataIndex: 'typeName' },
        { title: '申请人', dataIndex: 'employeeName' },
        { title: '创建人', dataIndex: 'createByName' },
        { title: '申请日期', dataIndex: 'requisitionDate' },
        { title: '金额', dataIndex: 'amount' },
        { title: '报销已关联金额', dataIndex: 'associatedAmount' },
        { title: '报销可关联金额', dataIndex: 'relevanceAmount' },
        { title: '状态', dataIndex: 'statusName' },
        { title: '备注', dataIndex: 'description' },
        { title: '关闭状态', dataIndex: 'closed_flag' },
      ],
      payRequisitionDetail: '/pre-payment/my-pre-payment/pre-payment-detail/:id/:flag', //预付款详情,

      reportSearchForm: [
        {
          type: 'input',
          id: 'documentNumber',
          label: '费用申请单单号',
          disabled: true,
          defaultValue: '',
          colSpan: '6',
        },
        {
          type: 'input',
          id: 'reportNumber',
          label: '报账单单号',
          colSpan: '6',
        },
        {
          type: 'list',
          id: 'companyId',
          label: '单据公司',
          colSpan: '6',
          listType: 'available_company',
          valueKey: 'id',
          labelKey: 'name',
          single: true,
        },
        {
          type: 'list',
          id: 'unitId',
          label: '单据部门',
          colSpan: '6',
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentId',
          single: true,
        },
      ],
      reportColumns: [
        {
          title: '报账单单号',
          dataIndex: 'reportNumber',
          render: (reportNumber, record) => {
            return (
              <Popover content={reportNumber}>
                <a onClick={() => this.handleLinkReport(record)}>{reportNumber}</a>
              </Popover>
            );
          },
        },
        { title: '公司', dataIndex: 'companyName' },
        { title: '部门', dataIndex: 'unitName' },
        { title: '维度1', dataIndex: 'dimension1Name' },
        { title: '维度2', dataIndex: 'dimension2Name' },
        { title: '币种', dataIndex: 'currency' },
        {
          title: '关联金额',
          dataIndex: 'releaseAmount',
          render: releaseAmount => {
            return <span>{this.filterMoney(releaseAmount, 2)}</span>;
          },
        },
        { title: '状态', dataIndex: 'statusName' },
        { title: '反冲状态', dataIndex: 'statusName' },
        { title: '备注', dataIndex: 'description' },
      ],
      reportVisible: false,
      reportSearchParam: {},
    };
  }
  //申请单的超链接,
  handleLink(record) {
    // this.context.router.push(this.state.payRequisitionDetail.url.replace(':id', record.id).replace(':flag', 'preFinalQuery'))
    //  this.props.dispatch(
    //     routerRedux.push({
    //       pathname: this.state.payRequisitionDetail.url,
    //    })
    //   );

    this.props.dispatch(
      routerRedux.replace({
        //pathname: `/pre-payment/my-pre-payment/pre-payment-detail/${record.id}/preFinalQuery`,
        /*pathname: `/expense-application/expense-application/expense-application-detail/${
          record.id
        }`,*/
        pathname: `/financial-view/expense-application/application-view-detail/${record.id}`,
      })
    );
  }
  //预付款单的超链接
  handleLinkp(record) {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/pre-payment/my-pre-payment/pre-payment-detail/${record.id}/preFinalQuery`,
      })
    );
  }
  // 报账单的超链接
  handleLinkReport(record) {
    this.onCloser();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/my-reimburse/my-reimburse/reimburse-detail/${record.id}`,
      })
    );
  }

  // 报账单反冲的超链接
  handleLinkf(record) {
    this.onCloser();
    this.props.dispatch(
      routerRedux.replace({
        //pathname: `/my-reimburse/my-reimburse/reimburse-detail/${record.id}`,
      })
    );
  }
  /**
   * 生命周期函数，constructor之后render之前
   */
  componentWillMount = () => {
    this.getList();
  };
  /**
   * 点击预付款单查看
   */
  onPayClick = (e, record, index) => {
    e.preventDefault();
    let { extraParams, selectorItem } = this.state;
    const advancePaymentAmount = this.filterMoney(record.advancePaymentAmount, 2, true);
    const operationTypeList = {
      reserved: '反冲',
      refund: '退票',
      payment: '付款',
      return: '退款',
    };
    selectorItem = {
      title: '被关联的预付款单',
      url: `${config.prePaymentUrl}/api/cash/prepayment/requisitionLine/get/line/by/query`,
      searchForm: [
        {
          type: 'input',
          id: 'documentNumber',
          label: '申请单单号',
          disabled: true,
          defaultValue: record.documentNumber,
          colSpan: '6',
        },
        {
          type: 'input',
          id: 'requisitionNumber',
          label: '预付款单单号',
          colSpan: '6',
        },
        {
          type: 'select',
          id: 'typeId',
          label: '单据类型',
          colSpan: 6,
          getUrl: `${config.prePaymentUrl}/api/cash/pay/requisition/types//queryAll?setOfBookId=${
            this.props.company.setOfBooksId
          }`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
        },
        {
          type: 'input',
          id: 'reptypeId',
          label: '预付款类型',
          /*getUrl: `${config.prePaymentUrl}/api/cash/pay/requisition/types//queryAll?setOfBookId=${
            props.company.setOfBooksId
          }`,*/
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
          colSpan: '6',
        },
      ],
      columns: [
        {
          title: '预付款单单号',
          dataIndex: 'requisitionNumber',
          render: (desc, record) => (
            <Popover content={desc}>
              <a onClick={() => this.handleLinkp(record.id)}>{desc || '-'}</a>
            </Popover>
          ),
        },
        {
          title: '单据类型',
          dataIndex: 'typeName',
        },
        {
          title: '预付款类型',
          dataIndex: 'repTypeName',
        },
        {
          title: '申请人',
          dataIndex: 'employeeName',
        },
        {
          title: '申请日期',
          dataIndex: 'requisitionDate',
          render: scheduleDate => {
            return <span>{moment(requisitionDate).format('YYYY-MM-DD')}</span>;
          },
        },
        { title: '币种', dataIndex: 'currency' },
        {
          title: '关联金额',
          dataIndex: 'amount',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },

        {
          title: '状态',
          dataIndex: 'status',
          width: '100',
          render: status => {
            return (
              <Badge
                status={this.state.status[value].state}
                text={this.state.status[value].label}
              />
            );
          },
        },
        { title: '备注', dataIndex: 'remarks' },
      ],
      key: 'id',
    };
    extraParams = {
      headerId: record.id,
      documentCategory: 'PREPAYMENT_REQUISITION',
    };
    this.setState({
      lsVisible: true,
      extraParams,
      selectorItem,
    });
  };

  //查看支付流水详情
  viewPayDetail = id => {
    this.setState({
      showChild: true,
      detailId: id,

      detailFlag: 'PAYDETAIL',
    });
  };

  //弹出框关闭
  onClose = () => {
    this.setState({
      showChild: false,
    });
  };
  onCloser = () => {
    this.setState({
      reportVisible: false,
    });
  };

  /**
   * 点击报账单
   */
  onReqClick = (e, record, index) => {
    let rform = this.state.reportSearchForm;

    //defauktValue 此时是放入主界面的申请单单号， 及需要将当前行的 documentNumber 作为查询条件。
    rform[0].defaultValue = record.documentNumber;
    //rform[0].defaultValue = '1';
    this.setState({
      reportVisible: true,
      reportSearchForm: rform,
    });
  };
  /**
   * 获取费用申请单财务查询
   */
  getList = () => {
    let { page, pageSize, searchParam } = this.state;
    let params = searchParam;

    params.page = page;
    params.size = pageSize;
    if (searchParam.applyDateFrom) {
      params.applyDateFrom = moment(searchParam.applyDateFrom).format('YYYY-MM-DD');
    }
    if (searchParam.applyDateTo) {
      params.applyDateTo = moment(searchParam.applyDateTo).format('YYYY-MM-DD');
    }
    params.tenantId = this.props.company.tenantId;
    prePaymentService
      .getList(params)
      .then(res => {
        if (res.status === 200) {
          //console.log(res.data);
          this.setState({
            data: res.data,
            //测试
            /*data:[{documentNumber:'ceshi111',
                   companyName:'测试公司',
                   unitName:'测试部门',
                   typeName:'测试类型',
                   employeeName:'测试人',
                   requisitionDate:'2019-03-11',
                   currencyCode:'测试币种',
                   amount:'111',
                   associatedAmount:'',
                   relevanceAmount:'',
                   description:'这是测试而已',
                   status:'',
                   closed_flag:'',
                  }],*/
            loading: false,
            pagination: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: page + 1,
              onChange: this.onChangeCheckedPage,
              onShowSizeChange: this.onShowSizeChange,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: total => `共搜到 ${total} 条数据`,
            },
          });
        }
      })
      .catch(e => {
        message.error('加载数据失败');
      });
  };

  getReportList = () => {
    let { pager, pageSizer, reportSearchParam } = this.state;
    let params = {
      page: pager,
      size: pageSizer,
      companyId: reportSearchParam.companyId ? reportSearchParam.companyId : '',
      documentNumber: reportSearchParam.documentNumber ? reportSearchParam.documentNumber : '',
      reportNumber: reportSearchParam.reportNumber ? reportSearchParam.reportNumber : '',
      unitId: reportSearchParam.unitId ? reportSearchParam.unitId : '',
    };
    //采用的是费用申请单财务查询的， 后续报销单出来之后进行修改
    prePaymentService
      .getReportDist(params)
      .then(res => {
        if (res.status === 200) {
          this.setState({
            //测试
            /*datar:[{reportNumber:'bx1111',
          companyName:'测试公司'
        },{reportNumber:'bx1111',
        companyName:'测试公司12'
      }],*/
            datar: res.data,
            loading: false,
            paginationr: {
              total: Number(res.headers['x-total-count'])
                ? Number(res.headers['x-total-count'])
                : 0,
              current: pager + 1,
              onChange: this.onChangeCheckedPager,
              onShowSizeChange: this.onShowSizeChanger,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: total => `共搜到 ${total} 条数据`,
            },
          });
        }
      })
      .catch(e => {
        message.error('加载数据失败');
      });
  };
  /**
   * 切换每页显示的条数
   */
  onShowSizeChange = (current, pageSize) => {
    this.setState(
      {
        loading: true,
        page: current - 1,
        pageSize,
      },
      () => {
        this.getList();
      }
    );
  };

  onShowSizeChanger = (current, pageSize) => {
    this.setState(
      {
        loading: true,
        page: current - 1,
        pageSize,
      },
      () => {
        this.getReportList();
      }
    );
  };
  /**
   * 切换分页
   */
  onChangeCheckedPage = page => {
    if (page - 1 !== this.state.page) {
      this.setState(
        {
          loading: true,
          page: page - 1,
        },
        () => {
          this.getList();
        }
      );
    }
  };
  onChangeCheckedPager = page => {
    if (page - 1 !== this.state.pager) {
      this.setState(
        {
          loading: true,
          page: page - 1,
        },
        () => {
          this.getReportList();
        }
      );
    }
  };
  /**
   * 搜索
   */
  searh = params => {
    if (params.companyId && params.companyId[0]) {
      params.companyId = params.companyId[0];
    }
    if (params.unitId && params.unitId[0]) {
      params.unitId = params.unitId[0];
    }
    if (params.applyId && params.applyId[0]) {
      params.applyId = params.applyId[0];
    }
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: params,
      },
      () => {
        this.getList();
      }
    );
  };
  searhr = params => {
    if (params.companyId && params.companyId[0]) {
      params.companyId = params.companyId[0];
    }
    if (params.unitId && params.unitId[0]) {
      params.unitId = params.unitId[0];
    }
    this.setState(
      {
        loading: true,
        pager: 0,
        reportSearchParam: params,
      },
      () => {
        this.getReportList();
      }
    );
  };
  /**
   * 清空
   */
  clear = () => {
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: {},
      },
      () => {
        this.getList();
      }
    );
  };
  clearr = () => {
    this.setState(
      {
        loading: true,
        pager: 0,
        reportSearchParam: {
          reportNumbe: '',
          unitId: '',
          companyId: '',
        },
      },
      // 清空后查询 ，  后续根据 业务需求 来决定是否需要
      () => {
        this.getReportList();
      }
    );
  };
  /**
   * 点击导出按钮
   */
  onExportClick = () => {
    this.setState({
      btLoading: true,
      excelVisible: true,
    });
  };
  /**
   * 导出取消
   */
  onExportCancel = () => {
    this.setState({
      btLoading: false,
      excelVisible: false,
    });
  };
  /**
   * 确定导出
   * 开始导出参数
   */
  export = result => {
    let hide = message.loading('正在生成文件，请等待......');

    let setOfBooksId = this.props.company.setOfBooksId;
    const exportParams = this.state.searchParam;
    exportParams.tenantId = this.props.company.tenantId;
    prePaymentService
      .export(result, exportParams)
      .then(res => {
        console.log(res);
        if (res.status === 200) {
          message.success('操作成功');
          0; //content-disposition 在response.headers 里面没有。
          let fileName = '费用申请单.xlsx'; //res.headers['content- '].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          this.setState({
            btLoading: false,
          });
          hide();
        }
      })
      .catch(e => {
        alert(e);
        console.log('下载失败' + this.e);
        message.error('下载失败，请重试!');
        this.setState({
          btLoading: false,
        });
        hide();
      });
  };
  /**
   * 根据费用申请单单号搜索
   */
  onDocumentSearch = value => {
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: { ...this.state.searchParam, documentNumber: value },
      },
      () => {
        this.getList();
      }
    );
  };

  wrapClose = content => {
    let id = this.state.detailId;
    const newProps = {
      params: { id: id, refund: true, flag: this.state.showChild },
    };
    return React.createElement(content, Object.assign({}, newProps.params, newProps));
  };
  //嵌套子表格
  expandedRowRender = record => {
    const columnse = [
      {
        title: '费用反冲单号',
        dataIndex: 'number',
        render: (documentNumber, record) => {
          return (
            <Popover content={documentNumber}>
              <a onClick={() => this.handleLinkf(record)}>{documentNumber}</a>
            </Popover>
          );
        },
      },
      { title: '申请人', dataIndex: 'name' },
      {
        title: '反冲日期',
        dataIndex: 'Date',
        render: (Date, record, index) => {
          return <span>{moment(Date).format('YYYY-MM-DD')}</span>;
        },
      },
      { title: '币种', dataIndex: 'currency' },
      {
        title: '反冲金额',
        dataIndex: 'amount',
        render: amount => {
          return <span>{this.filterMoney(amount, 2)}</span>;
        },
      },
      { title: '状态', key: 'state', dataIndex: 'statusName' },
      { title: '备注', dataIndex: 'description' },
    ];

    return (
      <Table columns={columnse} dataSource={this.state.expandedRowRenderList} pagination={false} />
    );
  };
  // 展示多行时，点击的操作。
  expand = record => {
    let params = { reportNumbe: record.reportNumbe };
    prePaymentService.getList(params).then(res => {
      this.setState({
        //这里是获取数据源。
        expandedRowRenderList: res.data,
      });
    });
  };
  //点击下一行时， 上一行关闭。只展示一行数据。
  onExpandedRowsChange = expandedRow => {
    var keys = [];
    if (expandedRow.length != 0) {
      keys.push(expandedRow[expandedRow.length - 1]);
    }
    this.setState({
      keys: keys,
    });
  };

  /**
   * 渲染函数
   */
  render() {
    //查询条件
    const { searchForm, showChild, reportSearchForm } = this.state;
    //返回列表
    const { reportColumns, columns, pagination, loading, data, datar } = this.state;
    //弹窗
    const { lsVisible, extraParams, selectorItem, reportVisible, paginationr } = this.state;
    //导出
    const { exportColumns, excelVisible, btLoading } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.searh}
          clearHandle={this.clear}
          maxLength={4}
        />
        <div className="divider" />
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={18}>
                <Button loading={btLoading} type="primary" onClick={this.onExportClick}>
                  导出费用申请单
                </Button>
              </Col>
              <Col span={6}>
                <Search
                  placeholder="请输入费用申请单单号"
                  onSearch={this.onDocumentSearch}
                  enterButton
                />
              </Col>
            </Row>
          </div>
        </div>
        <Table
          scroll={{ x: 1850 }}
          rowKey={record => record['id']}
          columns={columns}
          size="middle"
          bordered
          loading={loading}
          pagination={pagination}
          dataSource={data}
        />
        {/* 弹出框 */}
        <ListSelector
          selectorItem={selectorItem}
          extraParams={extraParams}
          visible={lsVisible}
          onOk={() => this.setState({ lsVisible: false })}
          onCancel={() => this.setState({ lsVisible: false })}
          hideRowSelect={true}
          hideFooter={true}
          modalWidth={'70%'}
        />
        {/* 导出 */}
        <ExcelExporter
          visible={excelVisible}
          onOk={this.export}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={'费用申请单'}
          onCancel={this.onExportCancel}
          excelItem={'APPLICATION_FINANCIAL_QUERY'}
        />
        <Modal
          visible={showChild} //支付明细弹窗
          footer={[
            <Button key="back" size="large" onClick={this.onClose}>
              {this.$t({ id: 'pay.backlash.goback' })}
            </Button>,
          ]}
          width={1200}
          closable={false}
          onCancel={this.onClose}
        >
          <div>{this.wrapClose(PayDetail)}</div>
        </Modal>
        <Modal
          visible={reportVisible} //报账单弹框
          width={1200}
          closable={false}
          onCancel={this.onCloser}
          onOk={this.onCloser}
        >
          <div>
            <SearchArea
              searchForm={reportSearchForm}
              submitHandle={this.searhr}
              clearHandle={this.clearr}
              maxLength={4}
            />
            <div className="divider" />
            <Table
              scroll={{ x: 850 }}
              rowKey={record => record['id']}
              columns={reportColumns}
              size="middle"
              bordered
              loading={loading}
              pagination={paginationr}
              dataSource={datar}
              expandedRowRender={this.expandedRowRender}
              onExpand={this.expand}
              onExpandedRowsChange={this.onExpandedRowsChange}
              expandedRowKeys={this.state.keys}
            />
          </div>
        </Modal>
      </div>
    );
  }
}

/**
 * redux
 */

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(PerPaymentView);
