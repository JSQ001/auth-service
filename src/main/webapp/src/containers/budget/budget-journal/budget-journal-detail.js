import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Affix, Popover, Button, message, Popconfirm, notification, Icon, Spin } from 'antd';
import Table from 'widget/table';
import 'styles/budget/budget-journal/budget-journal-detail.scss';
import config from 'config';
import budgetService from 'containers/budget-setting/budget-organization/budget-structure/budget-structure.service';
import BasicInfo from 'components/Widget/basic-info';
import SlideFrame from 'components/Widget/slide-frame';
import NewBudgetJournalDetail from 'containers/budget/budget-journal/new-budget-journal-detail';
import ImporterNew from 'widget/Template/importer-new';
import ApproveHistory from 'widget/Template/approve-history-work-flow';

import budgetJournalService from 'containers/budget/budget-journal/budget-journal.service';

class BudgetJournalDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isNew: true,
      params: {},
      loading: true,
      columnsSetFlag: true,
      commitLoading: false,
      data: [],
      listData: [],
      HistoryData: [],
      headerAndListData: {},
      headerData: {},
      showSlideFrameNew: false,
      updateState: false,
      buttonLoading: false,
      fileList: [],
      selectorItem: {},
      selectedRowKeys: [],
      rowSelection: {
        type: 'checkbox',
        selectedRowKeys: [],
        onChange: this.onSelectChange,
      },
      commitFlag: false,
      infoDate: {},
      templateUrl: '',
      uploadUrl: '',
      errorUrl: '',
      pageSize: 10,
      page: 0,
      total: 0,
      pagination: {
        current: 0,
        page: 0,
        total: 0,
        pageSize: 10,
      },
      handleData: [
        {
          type: 'list',
          id: 'company',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          columnLabel: 'companyName',
          columnValue: 'companyId',
        }, //公司
        {
          type: 'list',
          id: 'unit',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          columnLabel: 'departmentName',
          columnValue: 'unitId',
        }, //部门
        {
          type: 'list',
          id: 'employee',
          options: [],
          labelKey: 'fullName',
          valueKey: 'id',
          columnLabel: 'employeeName',
          columnValue: 'employeeId',
        }, //人员
        {
          type: 'list',
          id: 'responsibilityCenter',
          options: [],
          labelKey: 'responsibilityCenterCodeName',
          valueKey: 'id',
          columnLabel: 'responsibilityCenterCodeName',
          columnValue: 'id',
        }, //责任中心
        {
          type: 'list',
          id: 'item',
          options: [],
          labelKey: 'itemName',
          valueKey: 'id',
          columnLabel: 'itemName',
          columnValue: 'itemId',
        }, //预算项目
        {
          type: 'select',
          id: 'periodName',
          options: [],
          labelKey: 'periodName',
          valueKey: 'periodName',
          columnLabel: 'periodName',
          columnValue: 'periodName',
        }, //期间
        {
          type: 'value_list',
          id: 'periodQuarter',
          options: [],
          labelKey: 'periodQuarter',
          columnLabel: 'periodQuarter',
          columnValue: 'periodQuarterName',
          value: 'periodQuarter',
        }, //季度
        {
          type: 'select',
          id: 'periodYear',
          options: [],
          labelKey: 'periodYear',
          valueKey: 'periodYear',
          columnLabel: 'periodYear',
          columnValue: 'periodYear',
        }, //年度
        {
          type: 'select',
          id: 'currency',
          method: 'get',
          options: [],
          labelKey: 'currencyName',
          valueKey: 'currency',
          columnLabel: 'currency',
          columnValue: 'currency',
        }, //币种
        { type: 'input', id: 'rate', valueKey: 'rate' }, //汇率
        { type: 'inputNumber', id: 'amount', valueKey: 'amount' }, //金额
        { type: 'inputNumber', id: 'functionalAmount', valueKey: 'functionalAmount' }, //本位金额
        { type: 'inputNumber', id: 'quantity', valueKey: 'quantity' }, //数量
        { type: 'input', id: 'remark', valueKey: 'remark' }, //备注
      ],
      infoList: [
        /*状态*/
        { type: 'badge', label: this.$t({ id: 'budgetJournal.status' }), id: 'status' },
        /*预算日记账编号*/
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.journalCode' }),
          id: 'journalCode',
          disabled: true,
        },
        /*总金额*/
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.total.amount' }),
          id: 'totalAmount',
          disabled: true,
        },
        /*申请人*/
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.employeeId' }),
          id: 'employeeName',
          disabled: true,
        },
        /*公司*/
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.companyId' }),
          id: 'companyName',
          disabled: true,
        },
        /*部门*/
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.unitId' }),
          id: 'unitName',
          disabled: true,
        },
        /*创建时间*/
        {
          type: 'date',
          label: this.$t({ id: 'budgetJournal.createdDate' }),
          id: 'createdDate',
          disabled: true,
        },
        /*预算日记账类型*/
        {
          type: 'list',
          id: 'journalType',
          listType: 'budget_journal_type',
          labelKey: 'journalTypeName',
          valueKey: 'journalTypeId',
          label: this.$t({ id: 'budgetJournal.journalTypeId' }),
          listExtraParams: { organizationId: this.props.organization.id },
          disabled: true,
        },
        /*预算表*/
        {
          type: 'select',
          id: 'budgetStructure',
          label: this.$t({ id: 'budgetJournal.structureId' }),
          options: [],
          method: 'get',
          disabled: true,
          getUrl: `${config.budgetUrl}/api/budget/structures/queryAll`,
          getParams: { organizationId: this.props.organization.id },
          labelKey: 'structureName',
          valueKey: 'id',
        },
        /*预算版本*/
        {
          type: 'list',
          id: 'versionName',
          listType: 'budget_versions',
          labelKey: 'versionName',
          valueKey: 'id',
          isRequired: true,
          single: true,
          label: this.$t({ id: 'budgetJournal.versionId' }),
          listExtraParams: { organizationId: this.props.organization.id, enabled: true },
        },
        /*预算场景*/
        {
          type: 'list',
          id: 'scenarioName',
          listType: 'budget_scenarios',
          labelKey: 'scenarioName',
          valueKey: 'id',
          single: true,
          isRequired: true,
          label: this.$t({ id: 'budgetJournal.scenarios' }) /*预算场景*/,
          listExtraParams: { organizationId: this.props.organization.id, enabled: true },
        },
        /*编辑期段*/
        {
          type: 'value_list',
          id: 'periodStrategy',
          label: this.$t({ id: 'budgetJournal.periodStrategy' }),
          options: [],
          valueListCode: 2002,
          disabled: true,
        },
        /*附件*/
        {
          type: 'file',
          label: this.$t({ id: 'budgetJournal.attachment' }),
          id: 'file',
          disabled: true,
        },
      ],
      dimensionList: [],

      columns: [
        {
          /*公司*/
          title: this.$t({ id: 'budgetJournal.companyId' }),
          key: 'companyName',
          dataIndex: 'companyName',
          width: 100,
          render: companyName => <Popover content={companyName}>{companyName}</Popover>,
        },
        {
          /*部门*/
          title: this.$t({ id: 'budgetJournal.unitId' }),
          key: 'departmentName',
          dataIndex: 'departmentName',
          width: 100,
          render: departmentName => <Popover content={departmentName}>{departmentName}</Popover>,
        },
        {
          /*责任中心 */
          title: this.$t('budget.balance.responsibility.center') /*责任中心*/,
          key: 'responsibilityCenterCodeName',
          dataIndex: 'responsibilityCenterCodeName',
          width: 150,
          render: responsibilityCenterCodeName => (
            <Popover content={responsibilityCenterCodeName}>{responsibilityCenterCodeName}</Popover>
          ),
        },
        {
          /*员工*/
          title: this.$t({ id: 'budgetJournal.employee' }),
          key: 'employeeName',
          dataIndex: 'employeeName',
          width: 80,
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          /*预算项目*/
          title: this.$t({ id: 'budgetJournal.item' }),
          key: 'itemName',
          dataIndex: 'itemName',
          width: 150,
          render: itemName => <Popover content={itemName}>{itemName}</Popover>,
        },
        {
          /*期间*/
          title: this.$t({ id: 'budgetJournal.periodName' }),
          key: 'periodName',
          dataIndex: 'periodName',
          width: 80,
        },
        {
          /*季度*/
          title: this.$t({ id: 'budgetJournal.periodQuarter' }),
          key: 'periodQuarterName',
          dataIndex: 'periodQuarterName',
          width: 80,
        },
        {
          /*年度*/
          title: this.$t({ id: 'budgetJournal.periodYear' }),
          key: 'periodYear',
          dataIndex: 'periodYear',
          width: 80,
          align: 'center',
        },
        {
          /*币种*/
          title: this.$t({ id: 'budgetJournal.currency' }),
          key: 'currency',
          dataIndex: 'currency',
          width: 80,
        },
        {
          /*汇率*/
          title: this.$t({ id: 'budgetJournal.rate' }),
          key: 'rate',
          dataIndex: 'rate',
          width: 80,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
          //render: rate => <Popover content={rate}>{rate}</Popover>,
        },
        {
          /*金额*/
          title: this.$t({ id: 'budgetJournal.amount' }),
          key: 'amount',
          dataIndex: 'amount',
          width: 150,
          render: recode => (
            <Popover content={this.filterMoney(recode)}>{this.filterMoney(recode)}</Popover>
          ),
        },
        {
          /*本币今额*/
          title: this.$t({ id: 'budgetJournal.functionalAmount' }),
          key: 'functionalAmount',
          dataIndex: 'functionalAmount',
          width: 150,
          render: recode => (
            <Popover content={this.filterMoney(recode)}>{this.filterMoney(recode)}</Popover>
          ),
        },
        {
          /*数字*/
          title: this.$t({ id: 'budgetJournal.quantity' }),
          key: 'quantity',
          dataIndex: 'quantity',
          width: 80,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        },
        {
          /*备注*/
          title: this.$t({ id: 'budgetJournal.remark' }),
          key: 'remark',
          dataIndex: 'remark',
          width: 200,
          render: remark => <Popover content={remark}>{remark}</Popover>,
        },
      ],

      showImportFrame: false,
      // budgetJournalPage: menuRoute.getRouteItem('budget-journal', 'key'),    //预算日记账
      flag: '',
    };
  }

  formatMoney = (number, decimals = 2, isString = false) => {
    number = (number + '').replace(/[^0-9+-Ee.]/g, '');
    var n = !isFinite(+number) ? 0 : +number,
      prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
      sep = typeof thousands_sep === 'undefined' ? ',' : thousands_sep,
      dec = typeof dec_point === 'undefined' ? '.' : dec_point,
      s = '',
      toFixedFix = function(n, prec) {
        var k = Math.pow(10, prec);
        return '' + Math.ceil(n * k) / k;
      };

    s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
    var re = /(-?\d+)(\d{3})/;
    while (re.test(s[0])) {
      s[0] = s[0].replace(re, '$1' + sep + '$2');
    }

    if ((s[1] || '').length < prec) {
      s[1] = s[1] || '';
      s[1] += new Array(prec - s[1].length + 1).join('0');
    }

    if (isString === true) {
      return s.join(dec);
    } else {
      return <span className="money-cell">{s.join(dec)}</span>;
    }
  };

  componentWillMount() {
    this.getBudgetStructureInfo();
    this.getBudgetJournalHead();
  }

  componentDidMount() {
    this.getBudgetJournalLine();
  }

  // 重渲染columns
  renderColumns = res => {
    let { columns } = this.state;
    let responsibilityVisible = String(res.responsibilityVisible).toLowerCase();
    let index = columns.findIndex(item => {
      return (index = item.key === 'responsibilityCenterCodeName');
    });
    if (responsibilityVisible === 'hidden') {
      columns.splice(index, 1);
    }
    return columns;
  };

  //选项改变时的回调，重置selection
  onSelectChange = (selectedRowKeys, selectedRows) => {
    let { rowSelection } = this.state;
    rowSelection.selectedRowKeys = selectedRowKeys;
    this.setState({
      rowSelection,
      selectedRowKeys,
      selectedData: selectedRowKeys,
    });
  };

  //删除预算日记账行
  handleDeleteLine = () => {
    let data = this.state.selectedRowKeys;
    let selectedRowKeys = [];
    data.map(item => {
      if (item) {
        let id = { id: item };
        selectedRowKeys.addIfNotExist(id);
      }
    });
    budgetJournalService
      .deleteBudgetJournalLine(selectedRowKeys)
      .then(req => {
        this.getBudgetJournalLine();
        this.getToleAmount();
        message.success(`${this.$t({ id: 'common.operate.success' })}`);
        this.setState({
          selectedRowKeys: [],
        });
      })
      .catch(e => {
        message.error(`${this.$t({ id: 'common.operate.filed' })}`);
      });
  };

  //根据attachmentOid，查询附件
  getFile = value => {
    budgetJournalService
      .getFileByAttachmentOid(value)
      .then(resp => {
        let fileList = this.state.fileList;
        fileList.addIfNotExist(resp.data);
        this.setState({
          fileList: fileList,
        });
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'budgetJournal.getAttachmentFail' })},${e.response.data.message}`
        );
      });
  };

  //根据预算表id，获得维度
  getDimensionByStructureId = value => {
    let params = {};
    params.enabled = true;
    params.structureId = value;
    budgetJournalService
      .getDimensionByStructureId(params)
      .then(resp => {
        this.setState(
          {
            dimensionList: resp.data,
          },
          () => {
            //根据预算表，的维度.获取获取Columuns和获取维度的handleData数据
            this.getColumnsAndDimensionhandleData();
          }
        );
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'budgetJournal.getDimensionFail' })},${e.response.data.message}`
        );
      });
  };

  //根据预算表的维度.获取维度Columuns和获取维度的handleData数据
  getColumnsAndDimensionhandleData() {
    let columns = this.state.columns;
    let handleData = this.state.handleData;
    const dimensionList = this.state.dimensionList;
    for (let i = 0; i < dimensionList.length; i++) {
      const item = dimensionList[i];
      const priority = item.sequenceNumber;
      columns.push({
        title: `${item.dimensionName}`,
        key: `dimension${priority}ValueName`,
        id: `dimension${priority}ValueName`,
        dataIndex: `dimension${priority}ValueName`,
        width: 150,
        tooltips: true,
        render: recode => <Popover content={recode}>{recode}</Popover>,
      });
      handleData.push({
        type: 'select',
        id: `dimension${priority}`,
        options: [],
        labelKey: 'id',
        valueKey: 'name',
        columnLabel: `dimension${priority}ValueName`,
        columnValue: `dimension${priority}ValueId`,
      });
    }
    this.setState({
      columns,
      columnsSetFlag: false,
    });
  }
  /**
   * 获取预算表信息
   */
  getBudgetStructureInfo() {
    const journalCode = this.props.match.params.journalCode;
    budgetJournalService
      .getBudgetStructureByJournalId(journalCode)
      .then(res => {
        console.log(res);
        if (res.data) {
          const columns = this.renderColumns(res.data);
          this.setState({
            columns,
            flag: res.data,
          });
        }
      })
      .catch(err => {
        console.log(err);
        this.setState({ spinning: false });
      });
  }

  //获取日记账总金额
  getToleAmount() {
    let infoDate = this.state.infoDate;

    budgetJournalService
      .getTotalCurrencyAmount(this.props.match.params.journalCode)
      .then(response => {
        infoDate.totalAmount = response.data;
        this.setState(infoDate);
      });
  }

  //获取日记账头
  getBudgetJournalHead() {
    this.setState({
      //loading: true,
      fileList: [],
    });
    const journalCode = this.props.match.params.journalCode;
    budgetJournalService
      .getBudgetJournalHeaderDetil(journalCode)
      .then(response => {
        let headerData = response.data;

        if (this.state.columnsSetFlag) {
          this.getDimensionByStructureId(headerData.structureId);
        }
        if (headerData.attachmentOid != null) {
          headerData.attachmentOid.map(item => {
            this.getFile(item);
          });
        }
        const journalType = [];
        const journalType1 = {
          journalTypeName: headerData.journalTypeName,
          journalTypeId: headerData.journalTypeId,
        };
        journalType.push(journalType1);

        //预算版本
        const versionName = [];
        const versionName1 = {
          versionName: headerData.versionName,
          id: headerData.versionId,
        };
        versionName.push(versionName1);

        //预算场景
        const scenarioName = [];
        const scenarioName1 = {
          scenarioName: headerData.scenario,
          id: headerData.scenarioId,
        };
        scenarioName.push(scenarioName1);

        //预算表
        const budgetStructure = {
          label: headerData.structureName,
          value: headerData.structureId,
        };

        //编制期段
        const period = headerData.periodStrategy;
        const periodStrategy = {
          label:
            period == 'YEAR'
              ? this.$t({ id: 'budgetJournal.year' })
              : period == 'QUARTER'
                ? this.$t({ id: 'budgetJournal.quarter' })
                : this.$t({ id: 'budgetJournal.month' }),
          value: period,
        };

        //状态
        let statusData = {};
        if (headerData.status === 1001) {
          statusData = { status: 'processing', value: this.$t('budgetjournal.editor') }; /*编辑中*/
        } else if (headerData.status === 1003) {
          statusData = { status: 'default', value: this.$t('budgetjournal.returncommit') }; /*撤回*/
        } else if (headerData.status === 1005) {
          statusData = {
            status: 'error',
            value: this.$t('budgetjournal.rejection.of.examination.and.approval'),
          }; /*审批驳回*/
        } else {
          statusData = { status: 'default', value: headerData.statusName };
        }

        //获取总金额
        const infoData = {
          ...headerData,
          status: statusData,
          journalType: journalType,
          versionName: versionName,
          scenarioName: scenarioName,
          budgetStructure: budgetStructure,
          file: this.state.fileList,
          periodStrategy: periodStrategy,
        };
        const templateUrl = `${
          config.budgetUrl
        }/api/budget/journals/export/template?budgetJournalHeadId=${headerData.id}`;
        const uploadUrl = `${config.budgetUrl}/api/budget/journals/import?budgetJournalHeadId=${
          headerData.id
        }`;
        const errorUrl = `${
          config.budgetUrl
        }/api/budget/batch/transaction/logs/failed/export/budgetJournal/${headerData.id}`;
        let headerAndListData = {
          dto: response.data,
          list: [],
        };
        this.setState(
          {
            templateUrl,
            uploadUrl,
            errorUrl,
            headerAndListData: headerAndListData,
            headerData: headerData,
            infoDate: infoData,
            // columns,
          },
          () => {
            this.getToleAmount();
          }
        );
      })
      .catch(e => {
        console.log(e);
        if (e.response) message.error(e.response.data.message);
      });
  }

  //获取日记账行
  getBudgetJournalLine() {
    let params = {};
    params.page = this.state.page;
    params.size = this.state.pageSize;
    this.setState({
      loading: true,
    });
    const journalCode = this.props.match.params.journalCode;
    budgetJournalService
      .getBudgetJournalLineDetil(journalCode, params)
      .then(response => {
        let listData = response.data;
        this.setState({
          loading: false,
          data: listData,
          commitFlag: listData.length > 0,
          pagination: {
            total: Number(response.headers['x-total-count']),
            onChange: this.onChangePager,
            current: this.state.page + 1,
          },
        });
      })
      .catch(e => {
        if (e.response) {
          message.error(e.response.data.message);
          this.setState({
            loading: false,
          });
        }
      });
  }

  //分页点击
  onChangePager = page => {
    if (page - 1 !== this.state.page)
      this.setState(
        {
          page: page - 1,
          loading: true,
        },
        () => {
          this.getBudgetJournalLine();
        }
      );
  };

  //保存编辑
  updateHandleInfo = value => {
    const headerAndListData = this.state.headerAndListData;

    if (!value.versionName.length) {
      message.error(this.$t('budget.budget.version.not.empty')); // 预算版本不能为空！
      return;
    }

    if (!value.scenarioName.length) {
      message.error(this.$t('budget.scenarios.not.empty')); // 预算场景不能为空！
      return;
    }
    headerAndListData.dto.versionId = value.versionName[0];
    headerAndListData.dto.scenarioId = value.scenarioName[0];

    this.setState(
      {
        headerAndListData: headerAndListData,
        updateState: true,
      },
      () => {
        this.handleSaveJournal();
      }
    );
  };

  handleSaveJournal() {
    let headerAndListData = this.state.headerAndListData;
    budgetJournalService
      .addBudgetJournalHeaderLine(headerAndListData)
      .then(req => {
        message.success(`${this.$t({ id: 'common.operate.success' })}`);
        this.getBudgetJournalHead();
        this.getToleAmount();
      })
      .catch(e => {
        if (e.response) message.error(e.response.data.message);
      });
  }

  showSlideFrameNew = value => {
    this.setState({
      showSlideFrameNew: value,
    });
  };

  showSlideFrameNewData = () => {
    const { headerData } = this.state;
    let time = new Date().valueOf();
    let params = {
      isNew: true,
      periodStrategy: this.state.headerAndListData.dto.periodStrategy,
      structureId: this.state.headerAndListData.dto.structureId,
      journalTypeId: this.state.headerAndListData.dto.journalTypeId,
      time: time,
      companyId: headerData.companyId,
      unitId: headerData.unitId,
      employeeId: headerData.employeeId,
    };
    this.setState(
      {
        params: params,
      },
      () => {
        this.showSlide(true);
      }
    );
  };

  showSlide = value => {
    this.setState({
      showSlideFrameNew: value,
    });
  };

  //获得表单数据,保存或者修改
  handleAfterCloseNewSlide = value => {
    this.setState({
      showSlideFrameNew: false,
    });
    if (value) {
      let data = value;
      data.journalHeaderId = this.state.headerAndListData.dto.id;
      data.functionalAmount = data.functionalAmount
        ? data.functionalAmount.toString().replace(/\,/g, '')
        : undefined;
      budgetJournalService
        .addBudgetJournalLine(data)
        .then(req => {
          message.success(`${this.$t({ id: 'common.operate.success' })}`);
          this.getBudgetJournalLine();
          this.getToleAmount();
        })
        .catch(e => {
          message.error(e.response.data.message);
        });
    }
  };

  //删除该预算日记账
  handleDeleteJournal = () => {
    const id = this.state.headerAndListData.dto.id;
    budgetJournalService
      .deleteBudgetJournal(id)
      .then(req => {
        message.success(`${this.$t({ id: 'common.operate.success' })}`);
        //删除完该预算日记账，跳转
        // let path = this.state.budgetJournalPage.url;
        // this.context.router.push(path);
        this.props.dispatch(
          routerRedux.push({
            pathname: `/budget/budget-journal/budget-journal`,
          })
        );
      })
      .catch(e => {
        message.error(`${this.$t({ id: 'common.operate.filed' })}`);
      });
  };

  //提交单据
  handlePut = () => {
    let header = this.state.headerAndListData.dto;
    if (header.formOid) {
      message.warning(this.$t('budget.submit.the.documents.to.the.workflow')); /*提交单据至工作流*/
      //工作流
      this.setState({
        commitLoading: true,
      });
      let header = this.state.headerAndListData.dto;
      /*let data = {
        applicantOID: header.applicatOiD,
        userOID: this.props.user.userOID,
        formOID: header.formOid,
        entityOID: header.documentOid,
        entityType: header.documentType,
        amount: 0,
        countersignApproverOids: null,
      };
  */
      let workFlowDocumentRef = {
        applicantOid: header.applicatOid,
        userOid: this.props.user.userOid,
        formOid: header.formOid,
        documentOid: header.documentOid,
        documentCategory: header.documentType,
        countersignApproverOIDs: null,
        documentNumber: header.journalCode,
        remark: header.description,
        companyId: header.companyId,
        unitOid: header.unitOid,
        amount: header.amount,
        currencyCode: header.currency,
        documentTypeId: header.journalTypeId,
        applicantDate: header.createdDate,
        documentId: header.id,
        versionNumber: header.versionNumber,
      };
      if (this.state.commitFlag) {
        budgetJournalService
          .commitBudgetJournalWorkflow(workFlowDocumentRef)
          .then(req => {
            message.success(this.$t('budget.submitted.successfully')); /*提交成功*/
            this.setState({
              listData: [],
              commitLoading: false,
            });
            // let path = this.state.budgetJournalPage.url;
            // this.context.router.push(path);
            this.props.dispatch(
              routerRedux.push({
                pathname: `/budget/budget-journal/budget-journal`,
              })
            );
          })
          .catch(e => {
            message.error(e.response.data.message);
            this.setState({
              commitLoading: false,
            });
          });
      } else {
        notification.open({
          message: this.$t('budget.line.information.not.empty') /*行信息不能为空！*/,
          description: this.$t('budgetjournal.andorlead') /*请添加或导入预算日记账行信息*/,
          icon: <Icon type="frown-circle" style={{ color: '#e93652' }} />,
        });
      }
    } else {
      message.warning(this.$t('budget.submit.documents.review')); /*提交单据至复核*/
      //非工作流
      if (this.state.commitFlag) {
        let header = this.state.headerAndListData.dto;
        budgetJournalService
          .commitBudgetJournal(header.id)
          .then(res => {
            message.success(`${this.$t({ id: 'common.operate.success' })}`);
            this.setState({
              listData: [],
            });
            // let path = this.state.budgetJournalPage.url;
            // this.context.router.push(path);
            this.props.dispatch(
              routerRedux.push({
                pathname: `/budget/budget-journal/budget-journal`,
              })
            );
          })
          .catch(e => {
            message.error(e.response.data.message);
          });
      } else {
        notification.open({
          message: this.$t({ id: 'budgetJournal.notEmpty' }),
          description: this.$t({ id: 'budgetJournal.andOrLead' }),
          icon: <Icon type="frown-circle" style={{ color: '#e93652' }} />,
        });
      }
    }
  };

  //编辑行前,数据处理，传入数据
  headleUpData(values) {
    let valuesData = {};
    const handData = this.state.handleData;
    handData.map(item => {
      if (item.type === 'select' || item.type === 'value_list') {
        valuesData[item.id] = values[item.columnLabel];
      } else if (item.type === 'list') {
        let result = [];
        let itemData = {};
        if (values[item.columnValue]) {
          itemData[item.labelKey] = values[item.columnLabel];
          itemData[item.valueKey] = values[item.columnValue];
          itemData['key'] = values[item.columnValue];
          result.push(itemData);
        }
        valuesData[item.id] = result;
      } else if (item.type === 'input' || item.type === 'inputNumber') {
        valuesData[item.id] = values[item.valueKey];
      }
    });
    return valuesData;
  }

  //编辑行
  handlePutData = value => {
    const { headerData } = this.state;
    let time = new Date().valueOf();
    let valuePutData = this.headleUpData(value);
    this.setState(
      {
        params: {
          ...valuePutData,
          id: value.id,
          structureId: this.state.headerAndListData.dto.structureId,
          journalTypeId: this.state.headerAndListData.dto.journalTypeId,
          periodStrategy: this.state.headerAndListData.dto.periodStrategy,
          versionNumber: value.id,
          isNew: false,
          oldData: value,
          time: time,
          responsibilityCenterId: {
            id: value.responsibilityCenterId,
            key: value.responsibilityCenterId,
            label: value.responsibilityCenterCodeName,
          },
          companyId: headerData.companyId,
          unitId: headerData.unitId,
          employeeId: headerData.employeeId,
        },
      },
      () => {
        this.showSlide(true);
      }
    );
  };

  //返回预算日记账查询
  handleReturn = () => {
    if (this.props.match.params.flag === 'budgetJournalView') {
      this.props.dispatch(
        routerRedux.push({
          pathname: `/financial-view/budget-journal/budget-journal`,
        })
      );
    } else {
      this.props.dispatch(
        routerRedux.push({
          pathname: `/budget/budget-journal/budget-journal`,
        })
      );
    }
  };

  //撤回
  handleRevocation = () => {
    let header = this.state.headerData;
    if (header.formOid) {
      //工作流
      this.setState({ spinLoading: true });
      const dataValue = {
        entities: [
          {
            entityOid: this.state.headerAndListData.dto.documentOid,
            entityType: this.state.headerAndListData.dto.documentType,
          },
        ],
      };
      budgetJournalService
        .revocationJournalWorkflow(dataValue)
        .then(res => {
          message.success(this.$t('common.operate.success'));
          this.props.dispatch(
            routerRedux.push({
              pathname: `/budget/budget-journal/budget-journal`,
            })
          );
        })
        .catch(e => {
          message.error(`${this.$t('common.operate.filed')},${e.response.data.message}`);
        });
    } else {
      //非工作流
      const headerIds = [];
      headerIds.push(this.state.headerData.id);
      budgetJournalService
        .revocationJournal(this.props.user.id, headerIds)
        .then(res => {
          message.success(this.$t('common.operate.success'));
          this.props.dispatch(
            routerRedux.push({
              pathname: `/budget/budget-journal/budget-journal`,
            })
          );
        })
        .catch(e => {
          message.error(`${this.$t('common.operate.filed')},${e.response.data.message}`);
        });
    }
  };

  onLoadOk = transactionID => {
    budgetJournalService.confirmation(transactionID).then(res => {
      this.showImport(false);
      this.getBudgetJournalLine();
    });
  };

  showImport = flag => {
    this.setState({ showImportFrame: flag });
  };

  render() {
    const {
      historyData,
      loading,
      data,
      templateUrl,
      errorUrl,
      uploadUrl,
      columns,
      pagination,
      formData,
      infoDate,
      infoList,
      updateState,
      showSlideFrameNew,
      rowSelection,
      showImportFrame,
      headerAndListData,
      commitLoading,
      headerData,
      flag,
    } = this.state;
    let visible = this.props.match.params.flag
      ? { budgetJournalView: false }[this.props.match.params.flag]
      : { '1001': true, '1003': true, 1005: true }[headerData.status];
    return (
      <div className="budget-journal-detail" style={{ paddingBottom: 20 }}>
        <Spin spinning={commitLoading}>
          <div className="budget-journal-cent">
            <BasicInfo
              isHideEditBtn={!visible}
              infoList={infoList}
              infoData={infoDate}
              updateHandle={this.updateHandleInfo}
              updateState={updateState}
            />

            <div className="table-header">
              <div className="table-header-title">
                {this.$t({ id: 'common.total' }, { total: `${this.state.pagination.total}` })}/{this.$t(
                  { id: 'common.total' },
                  { total: `${this.state.total}` }
                )}
              </div>
              {visible ? (
                <div className="table-header-buttons">
                  <Button type="primary" onClick={this.showSlideFrameNewData}>
                    {this.$t({ id: 'common.add' })}
                  </Button>
                  <Button type="primary" onClick={() => this.showImport(true)}>
                    {this.$t({ id: 'importer.import' } /*导入*/)}
                  </Button>
                  <ImporterNew
                    visible={showImportFrame}
                    title={this.$t({ id: 'budgetJournal.leading' })}
                    templateUrl={templateUrl}
                    uploadUrl={`${
                      config.budgetUrl
                    }/api/budget/journals/import?budgetJournalHeadId=${
                      this.props.match.params.journalCode
                    }`}
                    errorUrl={`${config.budgetUrl}/api/budget/journals/import/error/export`}
                    errorDataQueryUrl={`${
                      config.budgetUrl
                    }/api/budget/journals/import/query/result`}
                    deleteDataUrl={`${config.budgetUrl}/api/budget/journals/import/delete`}
                    fileName={this.$t({ id: 'budgetJournal.budgetJournalLeading' })}
                    onOk={this.onLoadOk}
                    afterClose={() => this.showImport(false)}
                  />
                  <Popconfirm
                    placement="topLeft"
                    title={this.$t({ id: 'common.delete' })}
                    onConfirm={this.handleDeleteLine}
                    okText={this.$t({ id: 'common.ok' })}
                    cancelText={this.$t({ id: 'common.cancel' })}
                  >
                    <Button className="delete" disabled={this.state.selectedRowKeys.length === 0}>
                      {this.$t({ id: 'common.delete' })}
                    </Button>
                  </Popconfirm>
                </div>
              ) : (
                ''
              )}
            </div>
            <Table
              columns={columns}
              dataSource={data}
              rowKey={record => record.id}
              bordered
              size="middle"
              scroll={{ x: '150%' }}
              onRow={record => ({
                onClick: () => this.handlePutData(record),
              })}
              pagination={this.state.pagination}
              rowSelection={rowSelection}
              loading={loading}
            />
          </div>
          <div className="collapse">
            <ApproveHistory type="801002" oid={headerData.documentOid} formOid={true} />
          </div>
          <SlideFrame
            title={this.$t({ id: 'budgetJournal.journal' })}
            show={showSlideFrameNew}
            onClose={() => this.showSlideFrameNew(false)}
          >
            <NewBudgetJournalDetail
              onClose={this.handleAfterCloseNewSlide}
              params={this.state.params}
              flag={flag}
            />
          </SlideFrame>
          <div className="divider" />
          <Affix
            offsetBottom={0}
            style={{
              position: 'fixed',
              bottom: 0,
              marginLeft: '-35px',
              width: '100%',
              height: '50px',
              boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
              background: '#fff',
              lineHeight: '50px',
              zIndex: 1,
            }}
          >
            {visible ? (
              <div>
                <Popconfirm
                  style={{ width: 200 }}
                  placement="topLeft"
                  title={this.$t({ id: 'budgetJournal.commit' })}
                  onConfirm={this.handlePut}
                  okText={this.$t({ id: 'common.ok' })}
                  cancelText={this.$t({ id: 'common.cancel' })}
                >
                  <Button type="primary" style={{ marginLeft: '20px', marginRight: '8px' }}>
                    {this.$t({ id: 'budgetJournal.commit' })}
                  </Button>
                </Popconfirm>
                <Popconfirm
                  placement="topLeft"
                  title={this.$t({ id: 'budgetJournal.delete.journal' })}
                  onConfirm={this.handleDeleteJournal}
                  okText={this.$t({ id: 'common.ok' })}
                  cancelText={this.$t({ id: 'common.cancel' })}
                >
                  <Button className="delete" style={{ marginRight: '8px' }}>
                    {this.$t({ id: 'budgetJournal.delete.journal' })}
                  </Button>
                </Popconfirm>
                <Button onClick={this.handleReturn}>
                  {this.$t({ id: 'budgetJournal.return' })}
                </Button>
              </div>
            ) : (
              <div>
                {!this.props.match.params.flag &&
                  headerData.status === 1002 && (
                    <Button
                      type="primary"
                      style={{ marginLeft: 20 }}
                      onClick={this.handleRevocation}
                    >
                      {this.$t({ id: 'budgetJournal.returnCommit' })}
                    </Button>
                  )}
                <Button style={{ marginLeft: 20 }} onClick={this.handleReturn}>
                  {this.$t({ id: 'budgetJournal.return' })}
                </Button>
              </div>
            )}
          </Affix>
        </Spin>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    organization: state.user.organization,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BudgetJournalDetail);
