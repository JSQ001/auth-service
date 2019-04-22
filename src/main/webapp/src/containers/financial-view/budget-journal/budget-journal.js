import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import SearchArea from 'widget/search-area';
import { Input, Button, message, Popover, Row, Col, Badge, Modal } from 'antd';
import CustomTable from 'components/Widget/custom-table';
import ExcelExporter from 'widget/excel-exporter';
import ListSelector from 'widget/list-selector';
import moment from 'moment';
import budgetJournalService from './budget-journal.service';
import FileSaver from 'file-saver';
import BudgetJournalDetailCommon from 'containers/financial-view/budget-journal/budget-journal-detail-readonly';
const Search = Input.Search;

class BudgetJournalView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      /**
       * 查询条件
       */
      searchForm: [
        {
          type: 'list',
          id: 'companyId',
          label: this.$t({ id: 'budget.journal.view.company' }),
          colSpan: 6,
          listType: 'all_company_by_tenantId',
          valueKey: 'id',
          labelKey: 'name',
          listExtraParams: {},
          single: true,
        },
        {
          type: 'select',
          id: 'journalTypeId',
          label: this.$t({ id: 'budgetJournal.journalTypeId' }),
          colSpan: 6,
          options: [],
          method: 'get',
          getUrl: `${config.budgetUrl}/api/budget/journals/finance/type/query/all`,
          labelKey: 'journalTypeName',
          valueKey: 'id',
        },
        {
          type: 'select',
          id: 'structureId',
          label: this.$t({ id: 'budgetJournal.structureId' }),
          colSpan: 6,
          options: [],
          method: 'get',
          getUrl: `${config.budgetUrl}/api/budget/structures/queryAll/by/tenantId`,
          labelKey: 'structureName',
          valueKey: 'id',
        },
        {
          type: 'value_list',
          label: this.$t({ id: 'budgetJournal.status' }),
          colSpan: 6,
          id: 'status',
          options: [
            { value: 1001, label: this.$t('budgetjournal.editor') } /*编辑中*/,
            { value: 1002, label: this.$t('budgetjournal.approval') } /*审批中*/,
            { value: 1003, label: this.$t('budgetjournal.returncommit') } /*撤回*/,
            { value: 1004, label: this.$t('budgetjournal.approval.and.approval') } /*审批通过*/,
            {
              value: 1005,
              label: this.$t('budgetjournal.rejection.of.examination.and.approval'),
            } /*审批驳回*/,
          ],
          valueListCode: 2028,
        },
        {
          type: 'select',
          id: 'scenarioId',
          label: this.$t({ id: 'budget.journal.view.scenarioId' }),
          colSpan: 6,
          options: [],
          method: 'get',
          getUrl: `${config.budgetUrl}/api/budget/scenarios/queryAll/by/tenantId`,
          labelKey: 'scenarioName',
          valueKey: 'id',
        },
        {
          type: 'select',
          id: 'versionId',
          label: this.$t({ id: 'budget.journal.view.versionId' }),
          colSpan: 6,
          options: [],
          method: 'get',
          getUrl: `${config.budgetUrl}/api/budget/versions/queryAll/by/tenantId`,
          labelKey: 'versionName',
          valueKey: 'id',
        },
        {
          type: 'list',
          id: 'applicatOid',
          label: this.$t({ id: 'budgetJournal.employeeId' }),
          colSpan: 6,
          listType: 'bgtUser',
          labelKey: 'fullName',
          valueKey: 'userOid',
        },
        {
          type: 'list',
          id: 'unitId',
          label: this.$t({ id: 'budget.journal.view.unit' }),
          colSpan: 6,
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentId',
          single: true,
        },
        {
          type: 'items',
          id: 'createdDate',
          colSpan: 6,
          items: [
            {
              type: 'date',
              id: 'createdDateFrom',
              label: this.$t({ id: 'budget.journal.view.createdDateFrom' }),
            },
            {
              type: 'date',
              id: 'createdDateTo',
              label: this.$t({ id: 'budget.journal.view.createdDateTo' }),
            },
          ],
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            {
              type: 'input',
              id: 'amountFrom',
              label: this.$t({ id: 'budget.journal.view.amountFrom' }),
            },
            {
              type: 'input',
              id: 'amountTo',
              label: this.$t({ id: 'budget.journal.view.amountTo' }),
            },
          ],
        },
      ],
      columns: [
        {
          title: this.$t('budget.balance.doc.no') /*单据编号*/,
          dataIndex: 'journalCode',
          width: 210,
          render: (journalCode, record) => {
            return (
              <Popover content={journalCode}>
                <a onClick={() => this.handleLink(record)}>{journalCode}</a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('budget.balance.doc.type') /*单据类型*/,
          dataIndex: 'journalTypeName',
          width: 210,
          render: journalTypeName => {
            return <Popover content={journalTypeName}>{journalTypeName}</Popover>;
          },
        },
        {
          title: this.$t('budget.periodstrategy') /*编制期段*/,
          dataIndex: 'periodStrategyName',
          width: 110,
          align: 'center',
          render: periodStrategyName => {
            return <Popover content={periodStrategyName}>{periodStrategyName}</Popover>;
          },
        },
        {
          title: this.$t('budget.balance.budget.structure') /*预算表*/,
          dataIndex: 'structureName',
          width: 110,
          align: 'center',
          render: structureName => {
            return <Popover content={structureName}>{structureName}</Popover>;
          },
        },
        {
          title: this.$t('budget.balance.year') /*年度*/,
          dataIndex: 'periodYear',
          width: 100,
          align: 'center',
          render: periodYear => {
            return <Popover content={periodYear}>{periodYear}</Popover>;
          },
        },
        {
          title: this.$t('budget.balance.budget.scenarios') /*预算场景*/,
          dataIndex: 'scenarioName',
          width: 140,
          render: scenarioName => {
            return <Popover content={scenarioName}>{scenarioName}</Popover>;
          },
        },
        {
          title: this.$t('budget.balance.budget.version') /*预算版本*/,
          dataIndex: 'versionName',
          width: 134,
          render: versionName => {
            return <Popover content={versionName}>{versionName}</Popover>;
          },
        },
        {
          title: this.$t('budget.balance.company') /*公司*/,
          dataIndex: 'companyName',
          width: 200,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: this.$t('budget.balance.department') /*部门*/,
          dataIndex: 'unitName',
          width: 143,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: this.$t('budget.balance.requisitioned.by') /*申请人*/,
          dataIndex: 'applicatName',
          width: 100,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: this.$t('budget.creation.date') /*创建日期*/,
          dataIndex: 'createdDate',
          width: 126,
          align: 'center',
          render: createdDate => {
            return <span>{moment(createdDate).format('YYYY-MM-DD')}</span>;
          },
        },
        {
          title: this.$t('budget.functional.currency.amount') /*本位币金额*/,
          dataIndex: 'amount',
          width: 153,
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: this.$t('budget.state') /*状态*/,
          dataIndex: 'status',
          width: 112,
          render: status => {
            return (
              <Badge
                status={this.$statusList[status].state}
                text={this.$statusList[status].label}
              />
            );
          },
        },
      ],
      data: [],
      /**
       * 导出
       */
      excelVisible: false,
      btLoading: false,
      exportColumns: [
        { title: this.$t('budget.balance.doc.no'), dataIndex: 'journalCode' } /*单据编号*/,
        { title: this.$t('budget.balance.doc.type'), dataIndex: 'journalTypeName' } /*单据类型*/,
        {
          title: this.$t('budget.budget.business.types'),
          dataIndex: 'businessTypeName',
        } /*预算业务类型*/,
        { title: this.$t('budget.periodstrategy'), dataIndex: 'periodStrategyName' } /*编制期段*/,
        {
          title: this.$t('budget.balance.budget.structure'),
          dataIndex: 'structureName',
        } /*预算表*/,
        { title: this.$t('budget.balance.year'), dataIndex: 'periodYear' } /*年度*/,
        {
          title: this.$t('budget.balance.budget.scenarios'),
          dataIndex: 'scenarioName',
        } /*预算场景*/,
        { title: this.$t('budget.balance.budget.version'), dataIndex: 'versionName' } /*预算版本*/,
        { title: this.$t('budget.balance.company'), dataIndex: 'companyName' } /*公司*/,
        { title: this.$t('budget.balance.department'), dataIndex: 'unitName' } /*部门*/,
        { title: this.$t('budget.balance.requisitioned.by'), dataIndex: 'applicatName' } /*申请人*/,
        { title: this.$t('budget.create.a.period'), dataIndex: 'createdDateStr' } /*创建时期*/,
        { title: this.$t('budgetjournal.total.amount'), dataIndex: 'amount' } /*总金额*/,
        { title: this.$t('budget.state'), dataIndex: 'statusName' } /*状态*/,
      ],
      lsVisible: false,
      selectorItem: {},
      extraParams: {},
      showBudgetJournal: false,
      budgetJournalHeadId: '',
    };
  }

  handleLink = record => {
    this.setState({
      showBudgetJournal: true,
      budgetJournalHeadId: record.id,
    });
  };

  onBudgetJournalSearch = params => {
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: { ...this.state.searchParam, journalCode: params },
      },
      () => {
        this.table.search(this.state.searchParam);
      }
    );
  };

  //详情页
  rowClick = record => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: ``,
      })
    );
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
    params.createdDateFrom = params.createdDateFrom
      ? moment(params.createdDateFrom).format('YYYY-MM-DD')
      : undefined;
    params.createdDateTo = params.createdDateTo
      ? moment(params.createdDateTo).format('YYYY-MM-DD')
      : undefined;
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: params,
      },
      () => {
        this.table.search(this.state.searchParam);
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
        this.table.search(this.state.searchParam);
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
   */
  export = result => {
    let hide = message.loading(this.$t('budget.files.generated')); // 正在生成文件，请等待......

    const exportParams = this.state.searchParam;
    budgetJournalService
      .export(result, exportParams)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('budget.operation.is.successful')); /*操作成功*/
          let fileName = res.headers['content-disposition'].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          this.setState({
            btLoading: false,
          });
          hide();
        }
      })
      .catch(e => {
        message.error(this.$t('budget.download.failed')); // 下载失败，请重试！
        this.setState({
          btLoading: false,
        });
        hide();
      });
  };

  render() {
    const { searchForm, params } = this.state;
    //返回列表
    const { columns, pagination, loading, data } = this.state;
    //弹窗
    const { lsVisible, extraParams, selectorItem, showBudgetJournal } = this.state;
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
                  {this.$t('budget.export.budget.journal')}
                  {/*导出预算日记账*/}
                </Button>
              </Col>
              <Col span={6}>
                <Search
                  placeholder={this.$t('budget.enter.budget.journal.number')}
                  onSearch={this.onBudgetJournalSearch}
                  enterButton
                />
              </Col>
            </Row>
          </div>
        </div>
        <CustomTable
          ref={ref => (this.table = ref)}
          scroll={{ x: 1850 }}
          columns={columns}
          tableKey="id"
          onClick={this.rowClick}
          url={`${config.budgetUrl}/api/budget/journals/finance/query`}
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
          fileName={this.$t('budgetjournal.journal')} /*预算日记账*/
          onCancel={this.onExportCancel}
          excelItem={'BUDGET_JOURNAL'}
        />
        <Modal
          title={this.$t('budget.budget.journal.details')} /*预算日记账详情*/
          visible={showBudgetJournal}
          destroyOnClose
          onCancel={() => {
            this.setState({ showBudgetJournal: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <BudgetJournalDetailCommon id={this.state.budgetJournalHeadId} isApprovePage={true} />
        </Modal>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    organization: state.user.organization || {},
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BudgetJournalView);
