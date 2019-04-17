import React, { Component } from 'react';
import {
  Input,
  Tabs,
  Table,
  Button,
  Row,
  Col,
  Popover,
  message,
  Modal,
  Form,
  Select,
  DatePicker,
} from 'antd';
import SearchArea from 'widget/search-area';
import service from './service';
import config from 'config';
const Search = Input.Search;
import ListSelector from 'widget/list-selector';
const TabPane = Tabs.TabPane;
import { connect } from 'dva';
import './index';
import moment from 'moment';
import RctDOM from 'react-dom';
import httpFetch from 'share/httpFetch';
import { get } from 'http';
class ReportPaperSign extends Component {
  constructor(props) {
    super(props);
    //props.getRef && props.getRef(this);
    this.state = {
      // 书写相对应的搜索框
      //未签收页签
      searchForm: [
        {
          type: 'input',
          id: 'documentNumber',
          label: '报销单单号',
          colSpan: '6',
        },
        {
          type: 'select',
          id: 'typeId',
          label: '单据类型',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
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
          type: 'list',
          id: 'applyId',
          label: '单据提交人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
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
      ],
      columns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: '120',
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          width: '120',
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
          title: '单据金额',
          dataIndex: 'amount',
          width: '100',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: '单据提交人',
          dataIndex: 'employeeName',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
      ],
      // table 选项中后的事件
      rowSelection: {
        onChange: (selectedRowKeys, selectedRows) => {
          //console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
          this.setState({
            selectList: selectedRows,
          });
        },
        getCheckboxProps: record => ({
          disabled: record.name === 'Disabled User', // Column configuration not to be checked
          name: record.name,
        }),
      },
      //已签收页签
      searchFormy: [
        {
          type: 'input',
          id: 'documentNumber',
          label: '报销单单号',
          colSpan: '6',
        },
        {
          type: 'select',
          id: 'typeId',
          label: '单据类型',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
        },
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
          type: 'list',
          id: 'applyId',
          label: '单据提交人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'select',
          id: 'iconFlag',
          label: '是否扫描至影像系统',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
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
          type: 'list',
          id: 'applyId',
          label: '送单人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },

        {
          type: 'date',
          id: 'signDate',
          label: '签收时间',
          colSpan: '6',
        },
      ],
      columnsy: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: '120',
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          width: '120',
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
          title: '单据金额',
          dataIndex: 'amount',
          width: '100',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: '单据提交人',
          dataIndex: 'employeeName',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '送单人',
          dataIndex: 'employeeNamef',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '签收时间',
          dataIndex: 'signDate',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '是否扫描至影像系统',
          dataIndex: 'iconFlag',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
      ],
      // table 选项中后的事件
      rowSelectiony: {
        onChange: (selectedRowKeys, selectedRows) => {
          //console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
          this.setState({
            selectListy: selectedRows,
          });
        },
        getCheckboxProps: record => ({
          disabled: record.name === 'Disabled User', // Column configuration not to be checked
          name: record.name,
        }),
      },
      //已退回页签
      searchFormb: [
        {
          type: 'input',
          id: 'documentNumber',
          label: '报销单单号',
          colSpan: '6',
        },
        {
          type: 'select',
          id: 'typeId',
          label: '单据类型',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
        },
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
          type: 'list',
          id: 'applyId',
          label: '单据提交人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'select',
          id: 'iconFlag',
          label: '是否扫描至影像系统',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
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
          type: 'list',
          id: 'applyId',
          label: '取单人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },

        {
          type: 'date',
          id: 'backDate',
          label: '退回时间',
          colSpan: '6',
        },
      ],
      columnsb: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: '120',
        },
        {
          title: '单据公司',
          dataIndex: 'companyName',
          width: '120',
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
          title: '单据金额',
          dataIndex: 'amount',
          width: '100',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: '单据提交人',
          dataIndex: 'employeeName',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '取单人',
          dataIndex: 'employeeNamef',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '退回时间',
          dataIndex: 'backDate',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '是否扫描至影像系统',
          dataIndex: 'iconFlag',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '备注',
          dataIndex: 'remark',
          width: '80',
          render: description => {
            return <Popover content={description}>{description}</Popover>;
          },
        },
      ],
      // table 选项中后的事件
      rowSelectionb: {
        onChange: (selectedRowKeys, selectedRows) => {
          //console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
          this.setState({
            selectListb: selectedRows,
          });
        },
        getCheckboxProps: record => ({
          disabled: record.name === 'Disabled User', // Column configuration not to be checked
          name: record.name,
        }),
      },
      //纸质单据查询页签
      searchFormc: [
        {
          type: 'input',
          id: 'documentNumber',
          label: '报销单单号',
          colSpan: '6',
        },
        {
          type: 'select',
          id: 'typeId',
          label: '单据类型',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
        },
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
          id: 'iconFlag',
          label: '是否扫描至影像系统',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
        },
        {
          type: 'list',
          id: 'applyId',
          label: '单据提交人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'select',
          id: 'signFlag',
          label: '单据签收状态',
          colSpan: 6,
          //getUrl: `${config.expenseUrl}/api/expense/application/type/query/all?setOfBooksId=${this.props.company.setOfBooksId}&enabled=true`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'typeName',
        },
        {
          type: 'list',
          id: 'applyId',
          label: '单据处理人',
          colSpan: '6',
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
      ],
      columnsc: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: '120',
          render: (desc, record) => (
            <Popover content={desc}>
              <a onClick={() => this.handleLinkReport(record)}>{desc || '-'}</a>
            </Popover>
          ),
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
          title: '单据提交人',
          dataIndex: 'employeeName',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '单据签收状态',
          dataIndex: 'signName',
          width: '120',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '处理人',
          dataIndex: 'assigenName',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '是否扫描至影像系统',
          dataIndex: 'iconFlag',
          width: '80',
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
      ],
      data: [],
      datay: [],
      datab: [],
      datac: [],
      loading: false,
      pagination: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      paginationy: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      paginationb: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      paginationc: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      selectList: [],
      selectListy: [],
      selectListb: [],
      page: 0,
      pageSize: 10,
      pagey: 0,
      pageSizey: 10,
      pageb: 0,
      pageSizeb: 10,
      pagec: 0,
      pageSizec: 10,
      //用来存储单据号，方便批量扫描
      searchList: [],
      searchListy: [],
      searchListb: [],
      searchListc: [],
      optionList: [],
      open: false,
      lsVisible: false,
      lsVisiblet: false,
      lsVisibley: false,
      lsVisiblety: false,
      lsVisibleb: false,
      value: '',
      valuey: '',
      valueb: '',
      valuec: '',
      flag: '',
      searchParams: {},
      searchParamsy: {},
      searchParamsb: {},
      searchParamsc: {},
      searchflag: false,
      searchflagy: false,
      searchflagb: false,
    };
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
  componentDidShow() {
    console.log(333);
  }
  //切换标签页时的回调
  handleTabChange = value => {};
  //未签收页的查询。
  searh = params => {
    //let params = this.state.searchFrom;
    let data = [{ documentNumber: 'sssss' }, { documentNumber: 'wwww' }];
    this.setState({
      data: data,
      searchParams: params,
      searchflag: true,
    });
    //搜索
    /*service.getList(params).then(ref =>{
            if(ref.status===200){
                this.setState({
                    data:ref.data,
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
                })
            }                 console.log(this.state.selectList);
        })*/
  };

  clear = () => {
    this.setState({
      searchParams: {},
    });
  };
  //切换分页
  onChangeCheckedPage = page => {
    if (page - 1 !== this.state.page) {
      this.setState(
        {
          loading: true,
          page: page - 1,
        },
        () => {
          this.searh();
        }
      );
    }
  };
  //切换分页显示条数
  onShowSizeChanger = (current, pageSize) => {
    this.setState(
      {
        loading: true,
        page: current - 1,
        pageSize,
      },
      () => {
        this.searh();
      }
    );
  };
  //已签收页的查询
  searhy = params => {
    //let params = this.state.searchFromy;
    let data = [{ documentNumber: 'sssss' }, { documentNumber: 'wwww' }];
    this.setState({
      datay: data,
      searchParamsy: params,
      searchflagy: true,
    });
    //搜索
    /*service.getList(params).then(ref =>{
            if(ref.status===200){
                this.setState({
                    data:ref.data,
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
                })
            }                 console.log(this.state.selectList);
        })*/
  };

  cleary = () => {
    this.setState({
      searchParamsy: {},
    });
  };
  //切换分页
  onChangeCheckedPagey = page => {
    if (page - 1 !== this.state.pagey) {
      this.setState(
        {
          loading: true,
          pagey: page - 1,
        },
        () => {
          this.searhy();
        }
      );
    }
  };
  //切换分页显示条数
  onShowSizeChangery = (current, pageSizey) => {
    this.setState(
      {
        loading: true,
        pagey: current - 1,
        pageSizey,
      },
      () => {
        this.searhy();
      }
    );
  };
  //已退回页面查询
  searhb = params => {
    //let params = this.state.searchFromb;
    let data = [{ documentNumber: 'sssss' }, { documentNumber: 'wwww' }];
    this.setState({
      datab: data,
      searchParamsb: params,
      searchflagb: true,
    });
    //搜索
    /*service.getList(params).then(ref =>{
            if(ref.status===200){
                this.setState({
                    data:ref.data,
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
                })
            }                 console.log(this.state.selectList);
        })*/
  };

  clearb = () => {
    this.setState({
      searchParamsb: {},
    });
  };
  //切换分页
  onChangeCheckedPageb = page => {
    if (page - 1 !== this.state.pageb) {
      this.setState(
        {
          loading: true,
          pageb: page - 1,
        },
        () => {
          this.searhb();
        }
      );
    }
  };
  //切换分页显示条数
  onShowSizeChangerb = (current, pageSizeb) => {
    this.setState(
      {
        loading: true,
        pageb: current - 1,
        pageSizeb,
      },
      () => {
        this.searhb();
      }
    );
  };
  //纸质查询页面
  searhc = params => {
    //let params = this.state.searchFromc;
    let data = [{ documentNumber: 'sssss' }, { documentNumber: 'wwww' }];
    this.setState({
      datac: data,
      searchParamsc: params,
    });
    //搜索
    /*service.getList(params).then(ref =>{
            if(ref.status===200){
                this.setState({
                    data:ref.data,
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
                })
            }                 console.log(this.state.selectList);
        })*/
  };

  clearc = () => {
    this.setState({
      searchParamsc: {},
    });
  };
  //切换分页
  onChangeCheckedPagec = page => {
    if (page - 1 !== this.state.pagec) {
      this.setState(
        {
          loading: true,
          pagec: page - 1,
        },
        () => {
          this.searhc();
        }
      );
    }
  };
  //切换分页显示条数
  onShowSizeChangerc = (current, pageSizec) => {
    this.setState(
      {
        loading: true,
        pageb: current - 1,
        pageSizec,
      },
      () => {
        this.searhc();
      }
    );
  };
  //根据报销单号进行查询 ,与查询条件区分开来,单独查询。
  //未签收页面
  onDocumentSearch = value => {
    //单据扫描之后对应的
    let params = this.state.searchList;
    if (value) {
      params.push(value);
      this.setState({
        searchList: params,
        value: '',
        searchflag: false,
      });
      //console.log(params);
    }

    // 进行搜索

    //console.log(this.state.selectList)
  };
  //输入框内容改变事件
  serhChange = value => {
    this.setState({
      value: value.target.value,
    });
  };
  //根据报销单号进行查询 ,与查询条件区分开来,单独查询。
  //已签收页面
  onDocumentSearchy = value => {
    //单据扫描之后对应的
    let params = this.state.searchListy;
    if (value) {
      params.push(value);
      this.setState({
        searchListy: params,
        valuey: '',
        searchflagy: false,
      });
      //console.log(params);
    }

    // 进行搜索

    //console.log(this.state.selectList)
  };
  //输入框内容改变事件
  serhChangey = value => {
    this.setState({
      valuey: value.target.value,
    });
  };
  //根据报销单号进行查询 ,与查询条件区分开来,单独查询。
  //已退回页面
  onDocumentSearchb = value => {
    //单据扫描之后对应的
    let params = this.state.searchListb;
    if (value) {
      params.push(value);
      this.setState({
        searchListb: params,
        valueb: '',
        searchflagb: false,
      });
      //console.log(params);
    }

    // 进行搜索

    //console.log(this.state.selectList)
  };
  //输入框内容改变事件
  serhChangeb = value => {
    this.setState({
      valueb: value.target.value,
    });
  };
  //纸质查询
  onDocumentSearchc = value => {
    //单据扫描之后对应的
    let params = this.state.searchListc;
    if (value) {
      params.push(value);
      this.setState({
        searchListc: params,
        valuec: '',
      });
      //console.log(params);
    }

    // 进行搜索

    //console.log(this.state.selectList)
  };
  //输入框内容改变事件
  serhChangec = value => {
    this.setState({
      valuec: value.target.value,
    });
  };

  //未签收页面的签收
  sign_in = () => {
    let { selectList, selectorItem, extraParams } = this.state;
    if (selectList.length > 0) {
      this.setState({
        lsVisible: true,
      });
    } else {
      message.error('请至少选择一行');
    }
  };
  //已退回页面的签收
  sign_inb = () => {
    let { selectListb, selectorItem, extraParams } = this.state;
    if (selectListb.length > 0) {
      this.setState({
        lsVisibleb: true,
      });
    } else {
      message.error('请至少选择一行');
    }
  };
  //未签收页面的退回
  send_back = () => {
    let selectList = this.state.selectList;
    if (selectList.length > 0) {
      this.setState({
        lsVisiblet: true,
      });
    } else {
      message.error('请至少选择一行');
    }
  };
  //已签收页面的退回
  sign_iny = () => {
    let selectListy = this.state.selectListy;
    if (selectListy.length > 0) {
      this.setState({
        lsVisibley: true,
      });
    } else {
      message.error('请至少选择一行');
    }
  };
  //影像
  send_backy = () => {
    let selectListy = this.state.selectListy;
    if (selectListy.length > 0) {
      this.setState({
        lsVisiblety: true,
      });
    } else {
      message.error('请至少选择一行');
    }
  };
  //未签收页面
  handleChange = value => {
    //选中之后应该将下拉列表设置为不选定
    this.setState({
      optionList: [],
    });
  };
  //查询员工
  handleFocus = () => {
    //获得焦点的事件
  };
  handleBlur = () => {
    //失去焦点的事件
  };
  handleSearch = value => {
    console.log(value);
    //在文本框改动时的时候应该去查询数据。
    let url = `${config.mdataUrl}/api/select/user/by/name/or/code`;
    let params = {
      setOfBooksId: this.props.company.setOfBooksId,
      keyword: value,
      roleType: 'TENANT',
    };
    httpFetch.get(url, params).then(res => {
      let options = [];
      let data = res.data;
      //console.log(data);
      this.setState({
        optionList: data,
      });
    });
  };

  onChange = () => {};
  //未签收页签的签收确定
  handleSave = e => {
    //提交后可以拿values 就是数据。
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;
      // 获取到勾选的单据编号。
      let { selectList, searchForm, searchList } = this.state;
      //console.log(values);
      //拼装参数
      let params = {
        applyId: values.applyId,
        documentDate: moment(values.documentDate).format('YYYY-MM-DD'),
        documentNumbers: selectList,
      };
      //传递参数之后需要关闭弹框同时要刷新页面。
      console.log(params);
      //刷新逻辑，判断是头上搜索的还是行上搜索的

      this.onClose();
    });
    //let{optionList} = this.state;
  };
  //未签收页签的退回确认
  handleSavet = e => {
    //提交后可以拿values 就是数据。
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;
      console.log(values);
      let { selectList, searchForm, searchList } = this.state;
      //然后 传递数据到后端 保存。
      let params = {
        applyId: values.applyId,
        documentDate: moment(values.documentDate).format('YYYY-MM-DD'),
        documentNumbers: selectList,
      };
      this.onClose();
    });
    //let{optionList} = this.state;
  };

  //已退回页签的签收确定
  handleSaveb = e => {
    //提交后可以拿values 就是数据。
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;
      // 获取到勾选的单据编号。
      let { selectListb, searchFormb, searchListb } = this.state;
      //console.log(values);
      //拼装参数
      let params = {
        applyId: values.applyIdb,
        documentDate: moment(values.documentDateb).format('YYYY-MM-DD'),
        documentNumbers: selectListb,
      };
      //传递参数之后需要关闭弹框同时要刷新页面。
      console.log(params);
      //刷新逻辑，判断是头上搜索的还是行上搜索的
      this.onCloseb();
    });
    //let{optionList} = this.state;
  };
  //已签收页签的退回确认
  handleSavety = e => {
    //提交后可以拿values 就是数据。
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;
      console.log(values);
      let { selectListy, searchFormy, searchListy } = this.state;
      //然后 传递数据到后端 保存。
      let params = {
        applyId: values.applyIdy,
        documentDate: moment(values.documentDate).format('YYYY-MM-DD'),
        documentNumbers: selectListy,
      };
      this.onClosey();
    });
    //let{optionList} = this.state;
  };
  //影像
  handleSavey = e => {
    //提交后可以拿values 就是数据。
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;
      // 获取到勾选的单据编号。
      //let {selectListb,searchFormb,searchListb}=this.state;
      //console.log(values);
      //拼装参数
      /*let params = {
                applyId:values.applyIdb,
                documentDate :moment(values.documentDateb).format('YYYY-MM-DD'),
                documentNumbers:selectListb,
            }
            //传递参数之后需要关闭弹框同时要刷新页面。
            console.log(params);*/
      //刷新逻辑，判断是头上搜索的还是行上搜索的
      this.onClosey();
    });
    //let{optionList} = this.state;
  };

  onClose = () => {
    let { searchflag } = this.state;
    this.setState({
      lsVisible: false,
      lsVisiblet: false,
    });
    //刷新逻辑，判断是头上搜索的还是行上搜索的
    if (searchflag) {
      //头上搜索 因此还需要将继续搜索一次
      () => {
        this.searh();
      };
    } else {
      //行上搜索
      () => {
        this.onDocumentSearch();
      };
    }
  };
  onClosey = () => {
    let { searchflagy } = this.state;
    this.setState({
      lsVisibley: false,
      lsVisiblety: false,
    });
    //刷新逻辑，判断是头上搜索的还是行上搜索的
    if (searchflagy) {
      //头上搜索 因此还需要将继续搜索一次
      () => {
        this.searhy();
      };
    } else {
      //行上搜索
      () => {
        this.onDocumentSearchy();
      };
    }
  };
  onCloseb = () => {
    let { searchflagb } = this.state;
    this.setState({
      lsVisibleb: false,
    });
    //刷新逻辑，判断是头上搜索的还是行上搜索的
    if (searchflag) {
      //头上搜索 因此还需要将继续搜索一次
      () => {
        this.searhb();
      };
    } else {
      //行上搜索
      () => {
        this.onDocumentSearchb();
      };
    }
  };

  render() {
    let { searchForm, columns, data, loading, pagination, rowSelection } = this.state;
    let {
      selectorItem,
      lsVisible,
      optionList,
      lsVisiblet,
      value,
      lsVisibleb,
      lsVisiblety,
      lsVisibley,
    } = this.state;
    let { searchFormy, columnsy, rowSelectiony, paginationy, datay, valuey } = this.state;
    let { searchFormb, columnsb, rowSelectionb, paginationb, datab, valueb } = this.state;
    let { searchFormc, columnsc, paginationc, datac } = this.state;
    const { getFieldDecorator } = this.props.form;
    return (
      <div className="my-workbench">
        <Tabs defaultActiveKey="1" onChange={this.handleTabChange}>
          <TabPane tab="未签收" key="1">
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
                    <Col span={2}>
                      <Button type="primary" onClick={this.sign_in}>
                        签收
                      </Button>
                    </Col>
                    <Col span={16}>
                      <Button type="primary" onClick={this.send_back}>
                        退回
                      </Button>
                    </Col>
                    <Col span={6}>
                      <Search
                        placeholder="批量扫描报销单"
                        value={value}
                        onChange={this.serhChange}
                        //获取焦点
                        autoFocus
                        onSearch={this.onDocumentSearch}
                        enterButton
                      />
                    </Col>
                  </Row>
                </div>
              </div>
              <Table
                croll={{ x: 1850 }}
                rowKey={record => record['id']}
                columns={columns}
                rowSelection={rowSelection}
                selections
                type="radio"
                size="middle"
                bordered
                loading={loading}
                pagination={pagination}
                dataSource={data}
              />
            </div>
            <Modal
              title="纸质单据签收信息维护"
              visible={lsVisible} //报账单弹框
              centered={true}
              width={500}
              closable={false}
              onCancel={this.onClose}
              footer={[]}
              //</TabPane>onOk = {this.handleSave}
            >
              <Form onSubmit={this.handleSave}>
                <Form.Item label="送单人">
                  {getFieldDecorator('applyId', {
                    rules: [
                      {
                        required: false,
                      },
                    ],
                  })(
                    <Select
                      showSearch
                      style={{ width: 200 }}
                      placeholder=""
                      optionFilterProp="children"
                      onChange={this.handleChange}
                      onFocus={this.handleFocus}
                      onBlur={this.handleBlur}
                      onSearch={this.handleSearch}
                      required={true}
                    >
                      {this.state.optionList.map(o => {
                        return (
                          <Select.Option key={o.id}>
                            {o.employeeId}-{o.fullName}
                          </Select.Option>
                        );
                      })}
                    </Select>
                  )}
                </Form.Item>
                <Form.Item label="单据签收日期">
                  {getFieldDecorator('documentDate', {
                    rules: [
                      {
                        required: true,
                        message: '日期必输！',
                      },
                    ],
                  })(<DatePicker onChange={this.onChange} />)}
                </Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  style={{ margin: '0 20px' }}
                >
                  确定
                </Button>
                <Button loading={loading} style={{ margin: '0 20px' }} onClick={this.onClose}>
                  返回
                </Button>
              </Form>
            </Modal>
            <Modal
              title="纸质单据退回信息维护"
              visible={lsVisiblet} //报账单弹框
              centered={true}
              width={500}
              closable={false}
              onCancel={this.onClose}
              footer={[]}
              //</TabPane>onOk = {this.handleSave}
            >
              <Form onSubmit={this.handleSavet}>
                <Form.Item label="取单人">
                  {getFieldDecorator('applyId', {
                    rules: [
                      {
                        required: false,
                      },
                    ],
                  })(
                    <Select
                      showSearch
                      style={{ width: 200 }}
                      placeholder=""
                      optionFilterProp="children"
                      onChange={this.handleChange}
                      onFocus={this.handleFocus}
                      onBlur={this.handleBlur}
                      onSearch={this.handleSearch}
                      required={true}
                    >
                      {this.state.optionList.map(o => {
                        return (
                          <Select.Option key={o.id}>
                            {o.employeeId}-{o.fullName}
                          </Select.Option>
                        );
                      })}
                    </Select>
                  )}
                </Form.Item>
                <Form.Item label="单据退回日期">
                  {getFieldDecorator('documentDate', {
                    rules: [
                      {
                        required: true,
                        message: '日期必输！',
                      },
                    ],
                  })(<DatePicker onChange={this.onChange} />)}
                </Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  style={{ margin: '0 20px' }}
                >
                  确定
                </Button>
                <Button loading={loading} style={{ margin: '0 20px' }} onClick={this.onClose}>
                  返回
                </Button>
              </Form>
            </Modal>
          </TabPane>
          <TabPane tab="已签收" key="2">
            <div>
              <SearchArea
                searchForm={searchFormy}
                submitHandle={this.searhy}
                clearHandle={this.cleary}
                maxLength={4}
              />
              <div className="divider" />
              <div className="table-header">
                <div className="table-header-buttons">
                  <Row>
                    <Col span={2}>
                      <Button type="primary" onClick={this.sign_iny}>
                        退回
                      </Button>
                    </Col>
                    <Col span={16}>
                      <Button type="primary" onClick={this.send_backy}>
                        扫描至影像系统
                      </Button>
                    </Col>
                    <Col span={6}>
                      <Search
                        placeholder="批量扫描报销单"
                        value={valuey}
                        onChange={this.serhChangey}
                        onSearch={this.onDocumentSearchy}
                        //获取焦点
                        autoFocus
                        enterButton
                      />
                    </Col>
                  </Row>
                </div>
              </div>
              <Table
                croll={{ x: 1850 }}
                rowKey={record => record['id']}
                columns={columnsy}
                rowSelection={rowSelectiony}
                selections
                type="radio"
                size="middle"
                bordered
                loading={loading}
                pagination={paginationy}
                dataSource={datay}
              />
            </div>
            <Modal
              title="纸质单据退回信息维护"
              visible={lsVisibley} //报账单弹框
              centered={true}
              width={500}
              closable={false}
              onCancel={this.onClosey}
              footer={[]}
              //</TabPane>onOk = {this.handleSave}
            >
              <Form onSubmit={this.handleSavety}>
                <Form.Item label="取单人">
                  {getFieldDecorator('applyIdty', {
                    rules: [
                      {
                        required: false,
                      },
                    ],
                  })(
                    <Select
                      showSearch
                      style={{ width: 200 }}
                      placeholder=""
                      optionFilterProp="children"
                      onChange={this.handleChange}
                      onFocus={this.handleFocus}
                      onBlur={this.handleBlur}
                      onSearch={this.handleSearch}
                      required={true}
                    >
                      {this.state.optionList.map(o => {
                        return (
                          <Select.Option key={o.id}>
                            {o.employeeId}-{o.fullName}
                          </Select.Option>
                        );
                      })}
                    </Select>
                  )}
                </Form.Item>
                <Form.Item label="单据退回日期">
                  {getFieldDecorator('documentDatety', {
                    rules: [
                      {
                        required: true,
                        message: '日期必输！',
                      },
                    ],
                  })(<DatePicker onChange={this.onChange} />)}
                </Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  style={{ margin: '0 20px' }}
                >
                  确定
                </Button>
                <Button loading={loading} style={{ margin: '0 20px' }} onClick={this.onClosey}>
                  返回
                </Button>
              </Form>
            </Modal>
            <Modal
              title="影像"
              visible={lsVisiblety} //报账单弹框
              centered={true}
              width={500}
              closable={false}
              onCancel={this.onClosey}
              footer={[]}
              //</TabPane>onOk = {this.handleSave}
            >
              <Form onSubmit={this.handleSavey}>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  style={{ margin: '0 20px' }}
                >
                  确定
                </Button>
                <Button loading={loading} style={{ margin: '0 20px' }} onClick={this.onClosey}>
                  返回
                </Button>
              </Form>
            </Modal>
          </TabPane>
          <TabPane tab="已退回" key="3">
            <div>
              <SearchArea
                searchForm={searchFormb}
                submitHandle={this.searhb}
                clearHandle={this.clearb}
                maxLength={4}
              />
              <div className="divider" />
              <div className="table-header">
                <div className="table-header-buttons">
                  <Row>
                    <Col span={18}>
                      <Button type="primary" onClick={this.sign_inb}>
                        签收
                      </Button>
                    </Col>
                    <Col span={6}>
                      <Search
                        placeholder="批量扫描报销单"
                        value={valueb}
                        onChange={this.serhChangeb}
                        onSearch={this.onDocumentSearchb}
                        //获取焦点
                        autoFocus
                        enterButton
                      />
                    </Col>
                  </Row>
                </div>
              </div>
              <Table
                croll={{ x: 1850 }}
                rowKey={record => record['id']}
                columns={columnsb}
                rowSelection={rowSelectionb}
                selections
                type="radio"
                size="middle"
                bordered
                loading={loading}
                pagination={paginationb}
                dataSource={datab}
              />
            </div>
            <Modal
              title="纸质单据签收信息维护"
              visible={lsVisibleb} //报账单弹框
              centered={true}
              width={500}
              closable={false}
              onCancel={this.onCloseb}
              footer={[]}
              //</TabPane>onOk = {this.handleSave}
            >
              <Form onSubmit={this.handleSaveb}>
                <Form.Item label="送单人">
                  {getFieldDecorator('applyIdb', {
                    rules: [
                      {
                        required: false,
                      },
                    ],
                  })(
                    <Select
                      showSearch
                      style={{ width: 200 }}
                      placeholder=""
                      optionFilterProp="children"
                      onChange={this.handleChange}
                      onFocus={this.handleFocus}
                      onBlur={this.handleBlur}
                      onSearch={this.handleSearch}
                      required={true}
                    >
                      {this.state.optionList.map(o => {
                        return (
                          <Select.Option key={o.id}>
                            {o.employeeId}-{o.fullName}
                          </Select.Option>
                        );
                      })}
                    </Select>
                  )}
                </Form.Item>
                <Form.Item label="单据签收日期">
                  {getFieldDecorator('documentDateb', {
                    rules: [
                      {
                        required: true,
                        message: '日期必输！',
                      },
                    ],
                  })(<DatePicker onChange={this.onChange} />)}
                </Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  style={{ margin: '0 20px' }}
                >
                  确定
                </Button>
                <Button loading={loading} style={{ margin: '0 20px' }} onClick={this.onCloseb}>
                  返回
                </Button>
              </Form>
            </Modal>
          </TabPane>
          <TabPane tab="纸质单据查询" key="4">
            <div>
              <SearchArea
                searchForm={searchFormc}
                submitHandle={this.searhc}
                clearHandle={this.clearc}
                maxLength={4}
              />
              <div className="divider" />
              <div className="table-header">
                <div className="table-header-buttons">
                  <Row>
                    <Col span={18} />
                    <Col span={6}>
                      <Search
                        placeholder="批量扫描报销单"
                        onSearch={this.onDocumentSearchc}
                        //获取焦点
                        autoFocus
                        enterButton
                      />
                    </Col>
                  </Row>
                </div>
              </div>
              <Table
                croll={{ x: 1850 }}
                rowKey={record => record['id']}
                columns={columnsc}
                selections
                type="radio"
                size="middle"
                bordered
                loading={loading}
                pagination={paginationc}
                dataSource={datac}
              />
            </div>
          </TabPane>
        </Tabs>
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

// 获取公共组件。
export default connect(mapStateToProps)(Form.create()(ReportPaperSign));
