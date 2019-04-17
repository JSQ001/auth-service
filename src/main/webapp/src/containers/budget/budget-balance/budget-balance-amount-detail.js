import React from 'react';
import { connect } from 'dva';

import { Button, message, Popover } from 'antd';
import Table from 'widget/table';

import httpFetch from 'share/httpFetch';
import config from 'config';
import FileSaver from 'file-saver';
import { routerRedux } from 'dva/router';
import 'styles/budget-setting/budget-organization/new-budget-organization.scss';
import budgetBalanceService from 'containers/budget/budget-balance/budget-balance.service';

class BudgetBalanceAmountDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      exporting: false,
      page: 0,
      size: 10,
      pagination: {
        total: 0,
      },
      dimensionColumns: [],
      data: [],
      titleMap: {
        J: `${this.$t('budget.balance.budget.amt')}${this.$t('budget.balance.detail')}`,
        R: `${this.$t('budget.balance.budget.rsv')}${this.$t('budget.balance.detail')}`,
        U: `${this.$t('budget.balance.budget.usd')}${this.$t('budget.balance.detail')}`,
      },
      columns: {
        J: [
          {
            title: this.$t('budget.balance.period'),
            align: 'center',
            dataIndex: 'periodName',
            render: periodName => <Popover content={periodName}>{periodName}</Popover>,
          },
          { title: this.$t('budget.balance.season'), align: 'center', dataIndex: 'periodQuarter' },
          { title: this.$t('budget.balance.year'), align: 'center', dataIndex: 'periodYear' },
          {
            title: this.$t('budget.balance.company'),
            align: 'center',
            dataIndex: 'companyName',
            render: companyName => <Popover content={companyName}>{companyName}</Popover>,
          },
          {
            title: this.$t('budget.balance.department'),
            align: 'center',
            dataIndex: 'unitName',
            render: unitName => <Popover content={unitName}>{unitName}</Popover>,
          },
          {
            title: this.$t('budget.balance.budget.applicant'),
            align: 'center',
            dataIndex: 'applicantName',
          },
          {
            title: this.$t('budget.balance.budget.journal.type'),
            align: 'center',
            dataIndex: 'documentType',
          },
          {
            title: this.$t('budget.balance.budget.journal.code'),
            align: 'center',
            dataIndex: 'documentNumber',
            render: documentNumber => (
              <Popover content={documentNumber}>
                <a onClick={() => this.goBudgetJournal(documentNumber)}>{documentNumber}</a>
              </Popover>
            ),
          },
          {
            title: this.$t('budget.balance.budget.edit.date'),
            align: 'center',
            dataIndex: 'requisitionDate',
            render: requisitionDate =>
              requisitionDate ? new Date(requisitionDate).format('yyyy-MM-dd') : '-',
          },
          {
            title: this.$t('budget.balance.item'),
            align: 'center',
            dataIndex: 'itemName',
            render: itemName => <Popover content={itemName}>{itemName}</Popover>,
          },
          { title: this.$t('common.currency'), align: 'center', dataIndex: 'currency' },
          {
            title: this.$t('common.currency.rate'),
            align: 'center',
            dataIndex: 'rate',
            render: this.filterMoney,
          },
          {
            title: this.$t('common.base.currency.amount'),
            align: 'center',
            dataIndex: 'functionAmount',
            render: functionAmount => this.filterMoney(functionAmount, 4),
          },
          { title: this.$t('common.number'), align: 'center', dataIndex: 'quantity' },
          {
            title: this.$t('budget.balance.abstract'),
            align: 'center',
            dataIndex: 'description',
            render: description => <Popover content={description}>{description}</Popover>,
          },
        ],
        R: [
          {
            title: this.$t('budget.balance.company'),
            align: 'center',
            dataIndex: 'companyName',
            render: companyName => <Popover content={companyName}>{companyName}</Popover>,
          },
          {
            title: this.$t('budget.balance.department'),
            align: 'center',
            dataIndex: 'unitName',
            render: unitName => <Popover content={unitName}>{unitName}</Popover>,
          },
          {
            title: this.$t('budget.balance.requisitioned.by'),
            align: 'center',
            dataIndex: 'applicantName',
          },
          { title: this.$t('budget.balance.doc.type'), align: 'center', dataIndex: 'documentType' },
          {
            title: this.$t('budget.balance.doc.no'),
            align: 'center',
            dataIndex: 'documentNumber',
            render: (documentNumber, record) => (
              <Popover content={documentNumber}>
                <a onClick={() => this.goExpenseApplication(record)}>{documentNumber}</a>
              </Popover>
            ),
          },
          {
            title: this.$t('budget.balance.requisitioned.date'),
            align: 'center',
            dataIndex: 'requisitionDate',
            render: requisitionDate =>
              requisitionDate ? new Date(requisitionDate).format('yyyy-MM-dd') : '-',
          },
          {
            title: this.$t('budget.balance.doc.line.no'),
            align: 'center',
            dataIndex: 'documentLineNum',
          },
          {
            title: this.$t('budget.balance.requisitioned.item'),
            align: 'center',
            dataIndex: 'itemName',
            render: itemName => <Popover content={itemName}>{itemName}</Popover>,
          },
          { title: this.$t('common.currency'), align: 'center', dataIndex: 'currency' },
          {
            title: this.$t('budget.balance.requisitioned.amount'),
            align: 'center',
            dataIndex: 'amount',
          },
          { title: this.$t('common.tax'), align: 'center', dataIndex: 'taxAmount' },
          {
            title: this.$t('budget.balance.tax.free.amount'),
            align: 'center',
            dataIndex: 'saleAmount',
          },
          { title: this.$t('common.column.status'), align: 'center', dataIndex: 'documentStatus' },
          {
            title: this.$t('budget.balance.abstract'),
            align: 'center',
            dataIndex: 'description',
            render: description => <Popover content={description}>{description}</Popover>,
          },
          {
            title: this.$t('budget.balance.reversed.status'),
            align: 'center',
            dataIndex: 'reversedStatus',
          },
          { title: this.$t('budget.balance.period'), align: 'center', dataIndex: 'periodName' },
          {
            title: this.$t('budget.balance.audit.status'),
            align: 'center',
            dataIndex: 'auditStatus',
          },
        ],
        U: [
          {
            title: this.$t('budget.balance.company'),
            align: 'center',
            dataIndex: 'companyName',
            render: companyName => <Popover content={companyName}>{companyName}</Popover>,
          },
          {
            title: this.$t('common.department'),
            align: 'center',
            dataIndex: 'unitName',
            render: unitName => <Popover content={unitName}>{unitName}</Popover>,
          },
          {
            title: this.$t('budget.balance.reimbursed.by'),
            align: 'center',
            dataIndex: 'applicantName',
          },
          { title: this.$t('budget.balance.doc.type'), align: 'center', dataIndex: 'documentType' },
          {
            title: this.$t('budget.balance.doc.no'),
            align: 'center',
            dataIndex: 'documentNumber',
            render: (documentNumber, record) => (
              <Popover content={documentNumber}>
                <a onClick={() => this.skipToDocumentDetail(record)}>{documentNumber}</a>
              </Popover>
            ),
          },
          {
            title: this.$t('budget.balance.reimbursed.date'),
            align: 'center',
            dataIndex: 'requisitionDate',
            render: requisitionDate =>
              requisitionDate ? new Date(requisitionDate).format('yyyy-MM-dd') : '-',
          },
          {
            title: this.$t('budget.balance.reimbursed.item'),
            align: 'center',
            dataIndex: 'itemName',
            render: itemName => <Popover content={itemName}>{itemName}</Popover>,
          },
          { title: this.$t('common.currency'), align: 'center', dataIndex: 'currency' },
          {
            title: this.$t('budget.balance.reimbursed.amount'),
            align: 'center',
            dataIndex: 'amount',
          },
          { title: this.$t('common.column.status'), align: 'center', dataIndex: 'documentStatus' },
          {
            title: this.$t('budget.balance.abstract'),
            align: 'center',
            dataIndex: 'description',
            render: description => <Popover content={description}>{description}</Popover>,
          },
          { title: this.$t('budget.balance.period'), align: 'center', dataIndex: 'periodName' },
          {
            title: this.$t('budget.balance.audit.status'),
            align: 'center',
            dataIndex: 'auditStatus',
          },
        ],
      },
    };
  }

  //跳转去单据详情页
  skipToDocumentDetail = record => {
    if (record.businessType === 'EXP_REPORT') {
      budgetBalanceService.searchExportByBusinessCode(record.documentNumber).then(res => {
        if (res.data.length === 0) {
          message.error(this.$t('budget.balance.cannot.find.expense.report'));
        } else {
          window.open(
            menuRoute
              .getRouteItem('expense-report-detail-view')
              .url.replace(':expenseReportOid', res.data[0].entityOid)
          );
        }
      });
    }
  };

  exportDetail = () => {
    const { columns, dimensionColumns, titleMap } = this.state;
    const type = this.props.params.type;
    let columnFiledMap = {};
    columns[type].map((column, index) => {
      columnFiledMap['' + index] = column.title;
    });
    dimensionColumns.map(column => {
      columnFiledMap[20 + Number(column.index) + ''] = column.title;
    });
    let queryDetailDto = this.props.params.data;
    queryDetailDto.reserveFlag = this.props.params.type;
    queryDetailDto.organizationId = this.props.params.orgId;
    queryDetailDto.year = queryDetailDto.periodYear;
    let params = {
      excelVersion: '2003',
      columnFiledMap,
      queryDetailDto,
    };
    let hide = message.loading(this.$t('importer.spanned.file') /*正在生成文件..*/);
    this.setState({ exporting: true });
    budgetBalanceService
      .exportDetail(params)
      .then(res => {
        this.setState({ exporting: false });
        let b = new Blob([res.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        });
        FileSaver.saveAs(b, `${titleMap[type]}.xls`);
        hide();
      })
      .catch(() => {
        this.setState({ exporting: false });
        message.error(this.$t('importer.download.error.info') /*下载失败，请重试*/);
        hide();
      });
  };
  componentDidMount() {
    console.log(this.props);
    this.getList(this.props);
  }

  goExpenseApplication = record => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: '/expense-application/expense-application/expense-application-detail/:applicationId'.replace(
          ':applicationId',
          record.documentId
        ),
      })
    );
  };

  goBudgetJournal = code => {
    httpFetch
      .get(`${config.budgetUrl}/api/budget/journals/query/${code}`)
      .then(res => {
        this.props.dispatch(
          routerRedux.replace({
            pathname: '/budget/budget-journal/budget-journal-detail/:journalCode'.replace(
              ':journalCode',
              res.data.dto.id
            ),
          })
        );
      })
      .catch(e => {
        message.error('error');
      });
  };

  onChangePager = page => {
    if (page - 1 !== this.state.page)
      this.setState(
        {
          page: page - 1,
        },
        () => {
          this.getList(this.props);
        }
      );
  };

  getList = nextProps => {
    this.setState({ loading: true });
    let { page, size } = this.state;
    let params = nextProps.params.data;
    params.reserveFlag = nextProps.params.type;
    params.organizationId = nextProps.params.orgId;
    params.year = params.periodYear;
    httpFetch
      .post(
        `${config.budgetUrl}/api/budget/balance/query/results/detail?page=${page}&size=${size}`,
        params
      )
      .then(res => {
        let data = res.data.map((item, index) => {
          item.key = index;
          if (item.documentStatus) {
            switch (item.documentStatus) {
              case '1001':
                item.documentStatus = '编辑中';
                break;
              case '1002':
                item.documentStatus = '审批中';
                break;
              case '1003':
                item.documentStatus = '撤回';
                break;
              case '1004':
                item.documentStatus = '审批通过';
                break;
              case '1005':
                item.documentStatus = '审批驳回';
                break;
              case '1006':
                item.documentStatus = '审核通过';
                break;
              case '1007':
                item.documentStatus = '审核驳回';
                break;
            }
          }
          if (item.reversedStatus) {
            switch (item.reversedStatus) {
              case '1001':
                item.reversedStatus = '未关闭';
                break;
              case '1002':
                item.reversedStatus = '部分关闭';
                break;
              case '1003':
                item.reversedStatus = '已关闭';
                break;
            }
          }
          return item;
        });
        this.setState({
          loading: false,
          data,
          dimensionColumns: nextProps.params.dimensionColumns,
          pagination: {
            total: Number(res.headers['x-total-count']) ? Number(res.headers['x-total-count']) : 0,
            onChange: this.onChangePager,
            current: this.state.page + 1,
          },
        });
      })
      .catch(e => {
        this.setState({ loading: false });
        message.error('error');
      });
  };

  render() {
    const type = this.props.params.type;
    const {
      data,
      loading,
      pagination,
      columns,
      titleMap,
      dimensionColumns,
      exporting,
    } = this.state;

    let tableColumns = [].concat(columns[type] ? columns[type] : []).concat(dimensionColumns);
    return (
      <div>
        <h3 className="header-title">{titleMap[type]}</h3>
        <div className="table-header">
          <div className="table-header-title">
            {this.$t('common.total', { total: pagination.total ? pagination.total : '0' })}
          </div>{' '}
          {/* 共total条数据 */}
        </div>
        <Table
          columns={tableColumns}
          dataSource={data}
          bordered
          pagination={pagination}
          loading={loading}
          size="middle"
          rowKey="key"
          scroll={{ x: `${tableColumns.length * 20}%` }}
        />
        <div className="slide-footer">
          <Button
            onClick={() => {
              this.getList(this.props);
            }}
          >
            {this.$t('budget.balance.search.again')}
          </Button>
          <Button onClick={this.exportDetail} loading={exporting}>
            {this.$t('budget.balance.export.CVS')}
          </Button>
        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    organization: state.budget.organization,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(BudgetBalanceAmountDetail);
