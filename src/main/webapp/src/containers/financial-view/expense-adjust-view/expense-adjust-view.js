// create by 11475
// 费用调整单财务查询
import React from 'react';
import { connect } from 'dva';
import { Form, Button, message, Badge, Popover, Row, Col, Input } from 'antd';
import config from 'config';
import ListSelector from 'widget/list-selector';
import { routerRedux } from 'dva/router';
import CustomTable from 'widget/custom-table';
import moment from 'moment';
import SearchArea from 'widget/search-area';
import ExcelExporter from 'components/Widget/excel-exporter.js';
import FileSaver from 'file-saver';
import expenseAdjustViewService from './expense-adjust-view.service';

const { Search } = Input;

class ExpenseAdjustView extends React.Component {
  constructor(props) {
    super(props);
    const statusList = [
      { value: 1001, label: this.$t('common.editing') },
      { value: 1002, label: this.$t('common.approving') },
      { value: 1003, label: this.$t('common.withdraw') },
      { value: 1004, label: this.$t('common.approve.pass') },
      { value: 1005, label: this.$t('common.approve.rejected') },
    ];
    const status = {
      1001: { label: this.$t('common.editing'), state: 'default' },
      1004: { label: this.$t('common.approve.pass'), state: 'success' },
      1002: { label: this.$t('common.approving'), state: 'processing' },
      1005: { label: this.$t('common.approve.rejected'), state: 'error' },
      1003: { label: this.$t('common.withdraw'), state: 'warning' },
    };
    this.state = {
      loading: false,
      visible: false,
      // setOfBooksId: null,
      expenseType: [],
      searchForm: [
        // 单据公司
        {
          type: 'select',
          label: '单据公司',
          id: 'companyId',
          getUrl: `${config.mdataUrl}/api/company/by/condition`,
          getParams: { setOfBooksId: props.company.setOfBooksId },
          method: 'get',
          options: [],
          colSpan: '6',
          valueKey: 'id',
          labelKey: 'name',
        },
        // 单据类型
        {
          type: 'select',
          options: [],
          id: 'expAdjustTypeId',
          label: this.$t('epx.adjust.receipt.type'),
          labelKey: 'expAdjustTypeName',
          colSpan: 6,
          valueKey: 'id',
          getUrl: `${config.expenseUrl}/api/expense/adjust/types/document/query`,
          method: 'get',
          getParams: { setOfBooksId: props.company.setOfBooksId },
        },
        // 申请人
        {
          type: 'list',
          listType: 'user',
          options: [],
          id: 'applyId',
          label: '申请人',
          labelKey: 'fullName',
          valueKey: 'id',
          single: true,
          colSpan: 6,
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
        },
        // 状态
        {
          type: 'select',
          id: 'status',
          label: this.$t('common.column.status'),
          options: statusList,
          colSpan: 6,
        },
        // 单据部门
        {
          type: 'list',
          listType: 'department',
          id: 'unitId',
          label: '单据部门',
          options: [],
          labelKey: 'name',
          valueKey: 'departmentId',
          single: true,
          colSpan: 6,
          listExtraParams: { tenantId: props.user.tenantId },
        },
        // 申请日期
        {
          type: 'items',
          id: 'applyDateRange',
          items: [
            {
              type: 'date',
              id: 'applyDateFrom',
              label: '申请日期从',
            },
            {
              type: 'date',
              id: 'applyDateTo',
              label: '申请日期至',
            },
          ],
          colSpan: 6,
        },
        // 币种
        {
          type: 'select',
          key: 'currency',
          id: 'currency',
          label: '币种',
          getUrl: `${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`,
          options: [],
          method: 'get',
          labelKey: 'currency',
          valueKey: 'currency',
          colSpan: 6,
        },
        // 金额
        {
          type: 'items',
          id: 'amount',
          items: [
            {
              type: 'input',
              id: 'amountFrom',
              label: '金额从',
            },
            {
              type: 'input',
              id: 'amountTo',
              label: '金额至',
            },
          ],
          colSpan: 6,
        },
        // 调整类型
        {
          type: 'select',
          id: 'adjustTypeCategory',
          label: this.$t('exp.adjust.type'),
          colSpan: 6,
          options: [
            {
              label: this.$t('exp.adjust.exp.detail'),
              value: '1001',
            },
            {
              label: this.$t('exp.adjust.exp.add'),
              value: '1002',
            },
          ],
        },
        // 备注
        {
          type: 'input',
          id: 'description',
          label: this.$t('common.remark'),
          colSpan: 6,
        },
      ],
      columns: [
        {
          // 单据编号
          title: this.$t('common.document.code'),
          dataIndex: 'documentNumber',
          width: 150,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || '-'}</Popover>
            </span>
          ),
        },
        {
          // 单据公司
          title: this.$t('common.document.company'),
          dataIndex: 'companyName',
          width: 150,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || '-'}</Popover>
            </span>
          ),
        },
        {
          // 单据部门
          title: this.$t('commom.document.unit'),
          dataIndex: 'unitName',
          width: 150,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || '-'}</Popover>
            </span>
          ),
        },
        {
          // 单据类型
          title: this.$t('exp.receipt.type'),
          dataIndex: 'typeName',
          width: 150,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || '-'}</Popover>
            </span>
          ),
        },
        {
          // 调整类型
          title: this.$t('exp.adjust.type'),
          dataIndex: 'adjustTypeCategory',
          align: 'center',
          width: 100,
          render: desc => (
            <span>
              <Popover
                content={
                  desc === '1001' ? this.$t('exp.adjust.exp.detail') : this.$t('exp.adjust.exp.add')
                }
              >
                {desc === '1001' ? this.$t('exp.adjust.exp.detail') : this.$t('exp.adjust.exp.add')}
              </Popover>
            </span>
          ),
        },
        {
          // 申请人
          title: this.$t('exp.adjust.applier'),
          dataIndex: 'employeeName',
          width: 90,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || '-'}</Popover>
            </span>
          ),
        },
        {
          // 申请日期
          title: this.$t('exp.adjust.apply.date'),
          dataIndex: 'adjustDate',
          width: 100,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={moment(desc).format('YYYY-MM-DD')}>
                {desc ? moment(desc).format('YYYY-MM-DD') : '-'}
              </Popover>
            </span>
          ),
        },
        {
          // 币种
          title: this.$t('common.currency'),
          dataIndex: 'currencyCode',
          align: 'center',
          width: 80,
          render: desc => (
            <span>
              <Popover content={desc}>{desc || '-'}</Popover>
            </span>
          ),
        },
        {
          // 金额
          title: this.$t('common.amount'),
          dataIndex: 'totalAmount',
          width: 110,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={this.filterMoney(desc, 2)}>{this.filterMoney(desc, 2)}</Popover>
            </span>
          ),
        },
        // {
        //     //本币金额
        //     title: this.$t('request.base.amount'),
        //     dataIndex: 'functionalAmount',
        //     width: 110,
        //     align: 'center',
        //     render: desc => (
        //         <span>
        //             <Popover content={this.filterMoney(desc, 2)}>{this.filterMoney(desc, 2)}</Popover>
        //         </span>
        //     ),
        // },
        {
          // 备注
          title: this.$t('common.comment'),
          dataIndex: 'description',
          align: 'left',
          width: 150,
          render: desc => (
            <span>
              <Popover content={desc}>{desc || '-'}</Popover>
            </span>
          ),
        },
        {
          // 状态
          title: this.$t('common.column.status'),
          dataIndex: 'status',
          align: 'center',
          width: 100,
          render: value => {
            return <Badge status={status[value].state} text={status[value].label} />;
          },
        },
      ],
      data: [],
      // 导出
      excelVisible: false,
      btLoading: false,
      exportColumns: [
        { title: '单据编号', dataIndex: 'documentNumber' },
        { title: '单据公司', dataIndex: 'documentCompany' },
        { title: '单据部门', dataIndex: 'documentUnit' },
        { title: '单据类型', dataIndex: 'typeName' },
        { title: '调整类型', dataIndex: 'adjustTypeCategory' },
        { title: '申请人', dataIndex: 'employeeName' },
        { title: '币种', dataIndex: 'currencyCode' },
        { title: '金额', dataIndex: 'totalAmount' },
        { title: '备注', dataIndex: 'description' },
        { title: '状态', dataIndex: 'status' },
      ],
      // lsVisible: false,
      // selectorItem: {},
      // extraParams: {},
      pagination: {
        current: 0,
        page: 0,
        total: 0,
        pageSize: 10,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      searchParams: {},
    };
    // this.searchNumber = debounce(this.searchNumber, 500);
  }

  //调整单的超链接,
  handleLink(record) {
    this.props.dispatch(
      routerRedux.replace({
        //pathname: `/pre-payment/my-pre-payment/pre-payment-detail/${record.id}/preFinalQuery`,
        pathname: `/expense-application/expense-application/expense-application-detail/${
          record.id
        }`,
      })
    );
  }

  searchNumber = e => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: { ...searchParams, documentNumber: e },
      },
      () => {
        this.customTable.search({ ...searchParams, documentNumber: e });
      }
    );
  };

  /**
   * 搜索
   */
  searchFunction = params => {
    if (params.companyId && params.companyId[0]) {
      params.companyId = params.companyId[0];
    }
    if (params.unitId && params.unitId[0]) {
      params.unitId = params.unitId[0];
    }
    if (params.applyDateFrom) {
      params.applyDateFrom = moment(params.applyDateFrom).format('YYYY-MM-DD');
    }
    if (params.applyDateTo) {
      params.applyDateTo = moment(params.applyDateTo).format('YYYY-MM-DD');
    }
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: params,
      },
      () => {
        const { searchParam } = this.state;
        this.customTable.search(searchParam);
      }
    );
  };

  /**
   * 清空
   */
  clearFunction = () => {
    this.setState(
      {
        loading: true,
        page: 0,
        searchParam: {},
      },
      () => {
        const { searchParam } = this.state;
        this.customTable.search(searchParam);
      }
    );
  };

  rowClick = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: '/financial-view/expense-adjust-view/expense-adjust-view-detail/:id/:expenseAdjustTypeId/:type'
          .replace(':expenseAdjustTypeId', record.expAdjustTypeId)
          .replace(':id', record.id)
          .replace(':type', record.adjustTypeCategory),
      })
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
    const hide = message.loading('正在生成文件，请等待......');

    const { exportParams } = this.state;
    expenseAdjustViewService
      .export(result, exportParams)
      .then(res => {
        console.log(res);
        if (res.status === 200) {
          message.success('操作成功');
          const fileName = res.headers['content-disposition'].split('filename=')[1];
          const f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          this.setState({
            btLoading: false,
          });
          hide();
        }
      })
      .catch(e => {
        message.error(`下载失败，请重试!'${e}`);
        this.setState({
          btLoading: false,
        });
        hide();
      });
  };

  render() {
    const { visible, loading, searchForm, columns, data, pagination, expenseType } = this.state;
    // 弹窗
    // const { lsVisible, extraParams, selectorItem } = this.state;
    // 导出
    const { exportColumns, excelVisible, btLoading } = this.state;
    const { user, company } = this.props;
    return (
      // 搜索框
      <div className="pre-payment-container">
        <SearchArea
          searchForm={searchForm}
          maxLength={4}
          submitHandle={this.searchFunction}
          clearHandle={this.clearFunction}
        />
        <div className="divider" />
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={18}>
                <Button loading={btLoading} type="primary" onClick={this.onExportClick}>
                  导出费用调整单
                </Button>
              </Col>
              <Col span={6}>
                <Search
                  placeholder={this.$t('exp.input.number.tips')}
                  onSearch={this.searchNumber}
                  className="search-number"
                  enterButton
                />
              </Col>
            </Row>
          </div>
        </div>
        <CustomTable
          ref={ref => (this.customTable = ref)}
          scroll={{ x: 1850 }}
          columns={columns}
          tableKey="id"
          onClick={this.rowClick}
          url={`${config.expenseUrl}/api/expense/adjust/headers/query/dto`}
        />
        <ListSelector
          type="expense-adjust-type"
          visible={visible}
          single
          onOk={this.handleListOk}
          extraParams={{
            setOfBooksId: company.setOfBooksId,
            userId: user.id,
            enabled: true,
          }}
          onCancel={() => this.showListSelector(false)}
        />
        <ExcelExporter
          visible={excelVisible}
          onOk={this.export}
          columns={exportColumns}
          canCheckVersion={false}
          fileName="费用调整单"
          onCancel={this.onExportCancel}
          excelItem="BUDGET_JOURNAL"
        />
      </div>
    );
  }
}
const wrappedExpenseAdjustView = Form.create()(ExpenseAdjustView);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
    languages: state.languages,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedExpenseAdjustView);
