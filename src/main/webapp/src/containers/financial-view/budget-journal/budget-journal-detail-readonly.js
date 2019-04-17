import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Affix, Popover, Button, message, Popconfirm, notification, Icon, Spin, Form } from 'antd';
import Table from 'widget/table';
import 'styles/budget/budget-journal/budget-journal-detail.scss';
import config from 'config';
import PropTypes from 'prop-types';
import BasicInfo from 'components/Widget/basic-info';
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
        }, // 公司
        {
          type: 'list',
          id: 'unit',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          columnLabel: 'departmentName',
          columnValue: 'unitId',
        }, // 部门
        {
          type: 'list',
          id: 'employee',
          options: [],
          labelKey: 'fullName',
          valueKey: 'id',
          columnLabel: 'employeeName',
          columnValue: 'employeeId',
        }, // 人员
        {
          type: 'list',
          id: 'item',
          options: [],
          labelKey: 'itemName',
          valueKey: 'id',
          columnLabel: 'itemName',
          columnValue: 'itemId',
        }, // 预算项目
        {
          type: 'select',
          id: 'periodName',
          options: [],
          labelKey: 'periodName',
          valueKey: 'periodName',
          columnLabel: 'periodName',
          columnValue: 'periodName',
        }, // 期间
        {
          type: 'value_list',
          id: 'periodQuarter',
          options: [],
          labelKey: 'periodQuarter',
          columnLabel: 'periodQuarter',
          columnValue: 'periodQuarterName',
          value: 'periodQuarter',
        }, // 季度
        {
          type: 'select',
          id: 'periodYear',
          options: [],
          labelKey: 'periodYear',
          valueKey: 'periodYear',
          columnLabel: 'periodYear',
          columnValue: 'periodYear',
        }, // 年度
        {
          type: 'select',
          id: 'currency',
          method: 'get',
          options: [],
          labelKey: 'currencyName',
          valueKey: 'currency',
          columnLabel: 'currency',
          columnValue: 'currency',
        }, // 币种
        { type: 'input', id: 'rate', valueKey: 'rate' }, // 汇率
        { type: 'inputNumber', id: 'amount', valueKey: 'amount' }, // 金额
        { type: 'inputNumber', id: 'functionalAmount', valueKey: 'functionalAmount' }, // 本位金额
        { type: 'inputNumber', id: 'quantity', valueKey: 'quantity' }, // 数量
        { type: 'input', id: 'remark', valueKey: 'remark' }, // 备注
      ],
      infoList: [
        /* 状态 */
        { type: 'badge', label: this.$t({ id: 'budgetJournal.status' }), id: 'status' },
        /* 预算日记账编号 */
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.journalCode' }),
          id: 'journalCode',
          disabled: true,
        },
        /* 总金额 */
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.total.amount' }),
          id: 'totalAmount',
          disabled: true,
        },
        /* 申请人 */
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.employeeId' }),
          id: 'employeeName',
          disabled: true,
        },
        /* 公司 */
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.companyId' }),
          id: 'companyName',
          disabled: true,
        },
        /* 部门 */
        {
          type: 'input',
          label: this.$t({ id: 'budgetJournal.unitId' }),
          id: 'unitName',
          disabled: true,
        },
        /* 创建时间 */
        {
          type: 'date',
          label: this.$t({ id: 'budgetJournal.createdDate' }),
          id: 'createdDate',
          disabled: true,
        },
        /* 预算日记账类型 */
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
        /* 预算表 */
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
        /* 预算版本 */
        {
          type: 'list',
          id: 'versionName',
          listType: 'budget_versions',
          labelKey: 'versionName',
          valueKey: 'id',
          single: true,
          label: this.$t({ id: 'budgetJournal.versionId' }),
          listExtraParams: { organizationId: this.props.organization.id, enabled: true },
        },
        /* 预算场景 */
        {
          type: 'list',
          id: 'scenarioName',
          listType: 'budget_scenarios',
          labelKey: 'scenarioName',
          valueKey: 'id',
          single: true,
          label: this.$t({ id: 'budgetJournal.scenarios' }) /* 预算场景 */,
          listExtraParams: { organizationId: this.props.organization.id, enabled: true },
        },
        /* 编辑期段 */
        {
          type: 'value_list',
          id: 'periodStrategy',
          label: this.$t({ id: 'budgetJournal.periodStrategy' }),
          options: [],
          valueListCode: 2002,
          disabled: true,
        },
        /* 附件 */
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
          /* 公司 */
          title: this.$t({ id: 'budgetJournal.companyId' }),
          key: 'companyName',
          dataIndex: 'companyName',
          align: 'center',
          width: '5%',
          render: companyName => <Popover content={companyName}>{companyName}</Popover>,
        },
        {
          /* 部门 */
          title: this.$t({ id: 'budgetJournal.unitId' }),
          align: 'center',
          key: 'departmentName',
          dataIndex: 'departmentName',
          width: '5%',
          render: departmentName => <Popover content={departmentName}>{departmentName}</Popover>,
        },
        {
          /* 员工 */
          title: this.$t({ id: 'budgetJournal.employee' }),
          align: 'center',
          key: 'employeeName',
          dataIndex: 'employeeName',
          width: '5%',
          render: recode => <Popover content={recode}>{recode}</Popover>,
        },
        {
          /* 预算项目 */
          title: this.$t({ id: 'budgetJournal.item' }),
          key: 'itemName',
          dataIndex: 'itemName',
          align: 'center',
          width: '10%',
          render: itemName => <Popover content={itemName}>{itemName}</Popover>,
        },
        {
          /* 期间 */
          title: this.$t({ id: 'budgetJournal.periodName' }),
          key: 'periodName',
          align: 'center',
          dataIndex: 'periodName',
        },
        {
          /* 季度 */
          title: this.$t({ id: 'budgetJournal.periodQuarter' }),
          key: 'periodQuarterName',
          align: 'center',
          dataIndex: 'periodQuarterName',
        },
        {
          /* 年度 */
          title: this.$t({ id: 'budgetJournal.periodYear' }),
          key: 'periodYear',
          align: 'center',
          dataIndex: 'periodYear',
        },
        {
          /* 币种 */
          title: this.$t({ id: 'budgetJournal.currency' }),
          key: 'currency',
          align: 'center',
          dataIndex: 'currency',
        },
        {
          /* 汇率 */
          title: this.$t({ id: 'budgetJournal.rate' }),
          key: 'rate',
          dataIndex: 'rate',
          align: 'center',
          render: rate => <Popover content={rate}>{rate}</Popover>,
        },
        {
          /* 金额 */
          title: this.$t({ id: 'budgetJournal.amount' }),
          key: 'amount',
          align: 'center',
          dataIndex: 'amount',
          width: 180,
          render: recode => (
            <Popover content={this.filterMoney(recode)}>{this.filterMoney(recode)}</Popover>
          ),
        },
        {
          /* 本币今额 */
          title: this.$t({ id: 'budgetJournal.functionalAmount' }),
          key: 'functionalAmount',
          align: 'center',
          dataIndex: 'functionalAmount',
          width: 180,
          render: recode => (
            <Popover content={this.filterMoney(recode)}>{this.filterMoney(recode)}</Popover>
          ),
        },
        {
          /* 数字 */
          title: this.$t({ id: 'budgetJournal.quantity' }),
          key: 'quantity',
          align: 'center',
          dataIndex: 'quantity',
        },
        {
          /* 备注 */
          title: this.$t({ id: 'budgetJournal.remark' }),
          key: 'remark',
          dataIndex: 'remark',
          align: 'center',
          render: remark => <Popover content={remark}>{remark}</Popover>,
        },
      ],

      showImportFrame: false,
      // budgetJournalPage: menuRoute.getRouteItem('budget-journal', 'key'),    //预算日记账
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
      s[0] = s[0].replace(re, `$1${sep}$2`);
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
    this.getBudgetJournalHead();
    this.getBudgetJournalLine();
  }

  // 选项改变时的回调，重置selection
  onSelectChange = (selectedRowKeys, selectedRows) => {
    const { rowSelection } = this.state;
    rowSelection.selectedRowKeys = selectedRowKeys;
    this.setState({
      rowSelection,
      selectedRowKeys,
      selectedData: selectedRowKeys,
    });
  };

  // 根据attachmentOid，查询附件
  getFile = value => {
    budgetJournalService
      .getFileByAttachmentOid(value)
      .then(resp => {
        const fileList = this.state.fileList;
        fileList.addIfNotExist(resp.data);
        this.setState({
          fileList,
        });
      })
      .catch(e => {
        message.error(
          `${this.$t({ id: 'budgetJournal.getAttachmentFail' })},${e.response.data.message}`
        );
      });
  };

  // 根据预算表id，获得维度
  getDimensionByStructureId = value => {
    const params = {};
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
            // 根据预算表，的维度.获取获取Columuns和获取维度的handleData数据
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

  // 根据预算表的维度.获取维度Columuns和获取维度的handleData数据
  getColumnsAndDimensionhandleData() {
    const columns = this.state.columns;
    const handleData = this.state.handleData;
    const dimensionList = this.state.dimensionList;
    for (let i = 0; i < dimensionList.length; i++) {
      const item = dimensionList[i];
      const priority = item.sequenceNumber;
      columns.push({
        title: `${item.dimensionName}`,
        key: `dimension${priority}ValueName`,
        id: `dimension${priority}ValueName`,
        dataIndex: `dimension${priority}ValueName`,
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

  // 获取日记账总金额
  getToleAmount() {
    const infoDate = this.state.infoDate;

    budgetJournalService.getTotalCurrencyAmount(this.props.id).then(response => {
      infoDate.totalAmount = response.data;
      this.setState(infoDate);
    });
  }

  // 获取日记账头
  getBudgetJournalHead() {
    this.setState({
      // loading: true,
      fileList: [],
    });
    budgetJournalService
      .getBudgetJournalHeaderDetil(this.props.id)
      .then(response => {
        const headerData = response.data;

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

        // 预算版本
        const versionName = [];
        const versionName1 = {
          versionName: headerData.versionName,
          id: headerData.versionId,
        };
        versionName.push(versionName1);

        // 预算场景
        const scenarioName = [];
        const scenarioName1 = {
          scenarioName: headerData.scenario,
          id: headerData.scenarioId,
        };
        scenarioName.push(scenarioName1);

        // 预算表
        const budgetStructure = {
          label: headerData.structureName,
          value: headerData.structureId,
        };

        // 编制期段
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

        // 状态
        let statusData = {};
        if (headerData.status === 1001) {
          statusData = { status: 'processing', value: '编辑中' };
        } else if (headerData.status === 1003) {
          statusData = { status: 'default', value: '撤回' };
        } else if (headerData.status === 1005) {
          statusData = { status: 'error', value: '审批驳回' };
        } else {
          statusData = { status: 'default', value: headerData.statusName };
        }

        // 获取总金额
        const infoData = {
          ...headerData,
          status: statusData,
          journalType,
          versionName,
          scenarioName,
          budgetStructure,
          file: this.state.fileList,
          periodStrategy,
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
            headerAndListData,
            headerData,
            infoDate: infoData,
          },
          () => {
            this.getToleAmount();
          }
        );
      })
      .catch(e => {
        if (e.response) message.error(e.response.data.message);
      });
  }

  // 获取日记账行
  getBudgetJournalLine() {
    const params = {};
    params.page = this.state.page;
    params.size = this.state.pageSize;
    this.setState({
      loading: true,
    });
    budgetJournalService
      .getBudgetJournalLineDetil(this.props.id, params)
      .then(response => {
        const listData = response.data;
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

  // 分页点击
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

  // 返回预算日记账查询
  handleReturn = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/financial-view/budget-journal/budget-journal`,
      })
    );
  };

  render() {
    const {
      loading,
      data,
      columns,
      infoDate,
      infoList,
      updateState,
      rowSelection,
      commitLoading,
      headerData,
    } = this.state;

    return (
      <div className="budget-journal-detail">
        <Spin spinning={commitLoading}>
          <div className="budget-journal-cent">
            <BasicInfo
              isHideEditBtn={true}
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
            </div>
            <Table
              columns={columns}
              dataSource={data}
              rowKey={record => record.id}
              bordered
              size="middle"
              scroll={{ x: '200%' }}
              pagination={this.state.pagination}
              rowSelection={rowSelection}
              loading={loading}
            />
          </div>
          <div className="collapse">
            <ApproveHistory type="801002" oid={headerData.documentOid} formOid />
          </div>

          <div className="divider" />
          {/* <Affix
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
            <Button style={{ marginLeft: 20 }} onClick={this.handleReturn}>
              {this.$t({ id: 'budgetJournal.return' })}
            </Button>
          </Affix> */}
        </Spin>
      </div>
    );
  }
}

BudgetJournalDetail.propTypes = {
  id: PropTypes.any.isRequired, //显示数据
  isApprovePage: PropTypes.bool, //是否在审批页面
  getBudgetStatus: PropTypes.func, //确认信息状态
};

BudgetJournalDetail.defaultProps = {
  isApprovePage: false,
  getBudgetStatus: () => {},
};
function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    organization: state.user.organization,
  };
}
const BudgetJournalDetailCommon = Form.create()(BudgetJournalDetail);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BudgetJournalDetailCommon);
