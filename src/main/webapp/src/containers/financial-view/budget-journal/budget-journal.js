import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import SearchArea from 'widget/search-area';
import { Input, Button, message, Popover, Row, Col, Badge, Modal } from 'antd';
import Table from 'widget/table';
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
            { value: 1001, label: '编辑中' },
            { value: 1002, label: '审批中' },
            { value: 1003, label: '撤回' },
            { value: 1004, label: '审批通过' },
            { value: 1005, label: '审批驳回' },
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
        // {
        //   type: 'input',
        //   id: 'description',
        //   label: this.$t({ id: 'budget.journal.view.description' }),
        //   colSpan: 6,
        // },
      ],
      columns: [
        {
          title: '单据编号',
          dataIndex: 'journalCode',
          align: 'center',
          width: 180,
          render: (journalCode, record) => {
            return (
              <Popover content={journalCode}>
                <a onClick={() => this.handleLink(record)}>{journalCode}</a>
              </Popover>
            );
          },
        },
        {
          title: '单据类型',
          dataIndex: 'journalTypeName',
          align: 'center',
          width: 160,
          render: journalTypeName => {
            return <Popover content={journalTypeName}>{journalTypeName}</Popover>;
          },
        },
        {
          title: '编制期段',
          dataIndex: 'periodStrategyName',
          align: 'center',
          width: 120,
          render: periodStrategyName => {
            return <Popover content={periodStrategyName}>{periodStrategyName}</Popover>;
          },
        },
        {
          title: '预算表',
          dataIndex: 'structureName',
          align: 'center',
          width: 120,
          render: structureName => {
            return <Popover content={structureName}>{structureName}</Popover>;
          },
        },
        {
          title: '年度',
          dataIndex: 'periodYear',
          align: 'center',
          width: 120,
          render: periodYear => {
            return <Popover content={periodYear}>{periodYear}</Popover>;
          },
        },
        {
          title: '预算场景',
          dataIndex: 'scenarioName',
          align: 'center',
          width: 160,
          render: scenarioName => {
            return <Popover content={scenarioName}>{scenarioName}</Popover>;
          },
        },
        {
          title: '预算版本',
          dataIndex: 'versionName',
          align: 'center',
          width: 160,
          render: versionName => {
            return <Popover content={versionName}>{versionName}</Popover>;
          },
        },
        {
          title: '公司',
          dataIndex: 'companyName',
          align: 'center',
          width: 160,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '部门',
          dataIndex: 'unitName',
          align: 'center',
          width: 100,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '申请人',
          dataIndex: 'applicatName',
          align: 'center',
          width: 80,
          render: desc => <Popover content={desc}>{desc || '-'}</Popover>,
        },
        {
          title: '创建日期',
          dataIndex: 'createdDate',
          align: 'center',
          width: 120,
          render: createdDate => {
            return <span>{moment(createdDate).format('YYYY-MM-DD')}</span>;
          },
        },
        {
          title: '本位币金额',
          dataIndex: 'amount',
          align: 'center',
          width: 100,
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: '状态',
          dataIndex: 'status',
          align: 'center',
          width: 100,
          render: status => {
            return (
              <Badge
                status={this.$statusList[status].state}
                text={this.$statusList[status].label}
              />
            );
          },
        },
        // {
        //   title: '备注',
        //   dataIndex: 'description',
        //   align: 'center',
        //   width: 120,
        //   render: description => {
        //     return <Popover content={description}>{description}</Popover>;
        //   },
        // },
      ],
      data: [],
      /**
       * 导出
       */
      excelVisible: false,
      btLoading: false,
      exportColumns: [
        { title: '单据编号', dataIndex: 'journalCode' },
        { title: '单据类型', dataIndex: 'journalTypeName' },
        { title: '预算业务类型', dataIndex: 'businessTypeName' },
        { title: '编制期段', dataIndex: 'periodStrategyName' },
        { title: '预算表', dataIndex: 'structureName' },
        { title: '年度', dataIndex: 'periodYear' },
        { title: '预算场景', dataIndex: 'scenarioName' },
        { title: '预算版本', dataIndex: 'versionName' },
        { title: '公司', dataIndex: 'companyName' },
        { title: '部门', dataIndex: 'unitName' },
        { title: '申请人', dataIndex: 'applicatName' },
        { title: '创建时期', dataIndex: 'createdDateStr' },
        { title: '总金额', dataIndex: 'amount' },
        { title: '状态', dataIndex: 'statusName' },
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
    let hide = message.loading('正在生成文件，请等待......');

    const exportParams = this.state.searchParam;
    budgetJournalService
      .export(result, exportParams)
      .then(res => {
        if (res.status === 200) {
          message.success('操作成功');
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
        message.error('下载失败，请重试!');
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
                  导出预算日记账
                </Button>
              </Col>
              <Col span={6}>
                <Search
                  placeholder="请输入预算日记账编号"
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
          fileName={'预算日记账'}
          onCancel={this.onExportCancel}
          excelItem={'BUDGET_JOURNAL'}
        />
        <Modal
          title="预算日记账详情"
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
