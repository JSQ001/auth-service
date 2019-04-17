import React, { Component } from 'react';
import { connect } from 'dva';
import config from 'config';
import { Button, message, Badge, Row, Col, Input, Popover } from 'antd';
import SearchArea from 'widget/search-area';
import moment from 'moment';
import { routerRedux } from 'dva/router';
import FileSaver from 'file-saver';
import ExcelExporter from 'widget/excel-exporter';
import CustomTable from 'widget/custom-table';
import accountingQueryService from './gl-work-order.service';

const { Search } = Input;

class AccountingGLWorkOrder extends Component {
  /**
   * 构造函数
   */
  constructor(props) {
    super(props);

    this.state = {
      loading: false,
      exportLoading: false,
      data: [],
      btLoading: false,
      // 查询条件
      searchForm: [
        {
          type: 'list',
          isRequired: false,
          selectorItem: {
            title: this.$t('accounting.company.documents') /*单据公司*/,
            url: `${config.mdataUrl}/api/company/dto/by/tenant`,
            searchForm: [
              {
                type: 'input',
                id: 'companyCode',
                label: this.$t('accounting.company.code'),
              } /*公司代码*/,
              {
                type: 'input',
                id: 'name',
                label: this.$t('accounting.the.name.of.the.company'),
              } /*公司名称*/,
            ],
            columns: [
              { title: this.$t('accounting.company.code'), dataIndex: 'companyCode' } /*公司代码*/,
              {
                title: this.$t('accounting.the.name.of.the.company'),
                dataIndex: 'name',
              } /*公司名称*/,
            ],
            key: 'id',
          },
          id: 'companyId',
          listExtraParams: { tenantId: props.company.tenantId, setOfBooksId: '' },
          label: this.$t('accounting.company.documents') /*单据公司*/,
          labelKey: 'name',
          valueKey: 'id',
          colSpan: '6',
          single: false,
        },
        {
          type: 'select',
          label: this.$t('accounting.type.of.document') /*单据类型*/,
          id: 'workOrderTypeId',
          colSpan: '6',
          getUrl: `${
            config.accountingUrl
          }/api/general/ledger/work/order/types/query/by/setOfBooksId?setOfBooksId=${
            props.company.setOfBooksId
          }`,
          options: [],
          method: 'get',
          valueKey: 'id',
          labelKey: 'workOrderTypeName',
          event: 'workOrderTypeId',
        },
        {
          type: 'list',
          id: 'employeeId',
          label: this.$t('acp.employeeName'), // 申请人
          colSpan: '6',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'select',
          label: this.$t('accounting.state') /*状态*/,
          id: 'status',
          colSpan: '6',
          options: [
            { value: 1001, label: this.$t('accounting.editing') } /*编辑中*/,
            {
              value: 1002,
              label: this.$t('accounting.in.the.examination.and.approval'),
            } /*审批中*/,
            { value: 1003, label: this.$t('accounting.to.withdraw') } /*撤回*/,
            {
              value: 1004,
              label: this.$t('accounting.the.examination.and.approval.by'),
            } /*审批通过*/,
            { value: 1005, label: this.$t('accounting.approval.to.dismiss') } /*审批驳回*/,
          ],
          valueKey: 'value',
          labelKey: 'label',
          event: 'status',
        },
        {
          type: 'items',
          id: 'requisitionDate',
          colSpan: '6',
          items: [
            {
              type: 'date',
              id: 'requisitionDateFrom',
              event: 'requisitionDateFrom',
              label: this.$t('accounting.application.date.from') /*申请日期从*/,
            },
            {
              type: 'date',
              id: 'requisitionDateTo',
              event: 'requisitionDateTo',
              label: this.$t('accounting.application.date.to') /*申请日期至*/,
            },
          ],
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: '6',
          items: [
            {
              type: 'input',
              id: 'amountFrom',
              label: this.$t('accounting.the.amount.from') /*金额从*/,
            },
            {
              type: 'input',
              id: 'amountTo',
              label: this.$t('accounting.the.amount.to') /*金额至*/,
            },
          ],
        },
        {
          type: 'select',
          label: this.$t('accounting.view.currencycode') /*币种*/,
          id: 'currency',
          colSpan: '6',
          options: [],
          method: 'get',
          getUrl: `${config.mdataUrl}/api/currency/rate/list`,
          listKey: 'records',
          getParams: {
            enable: true,
            setOfBooksId: props.company.setOfBooksId,
            tenantId: props.company.tenantId,
          },
          valueKey: 'currencyCode',
          labelKey: 'currencyCodeAndName',
          event: 'currency',
        },
        {
          type: 'input',
          id: 'remark',
          label: this.$t('accounting.note') /*备注*/,
          colSpan: 6,
        },
      ],
      // 表格
      columns: [
        {
          title: this.$t('accounting.receipt.number'),
          dataIndex: 'workOrderNumber',
          width: 200,
          tooltips: true,
        } /*单据编号*/,
        {
          title: this.$t('accounting.type.of.document'),
          dataIndex: 'typeName',
          width: 120,
          tooltips: true,
        } /*单据类型*/,
        {
          title: this.$t('accounting.company.documents'),
          dataIndex: 'companyName',
          width: 150,
          tooltips: true,
        } /*单据公司*/,
        {
          title: this.$t('accounting.the.applicant'),
          dataIndex: 'employeeName',
          width: 100,
        } /*申请人*/,
        {
          title: this.$t('accounting.application.date') /*申请日期*/,
          dataIndex: 'requisitionDate',
          width: 110,
          render: requisitionDate => {
            return <span>{moment(requisitionDate).format('YYYY-MM-DD')}</span>;
          },
        },
        {
          title: this.$t('accounting.view.currencycode'),
          dataIndex: 'currencyName',
          width: 90,
        } /*币种*/,
        {
          title: this.$t('accounting.the.amount.of') /*金额*/,
          dataIndex: 'amount',
          render: amount => {
            return <span>{this.filterMoney(amount, 2)}</span>;
          },
        },
        {
          title: this.$t('accounting.state') /*状态*/,
          dataIndex: 'status',
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
        {
          title: this.$t('accounting.note') /*备注*/,
          dataIndex: 'remark',
          render: remark => {
            return <Popover content={remark}>{remark}</Popover>;
          },
        },
      ],
      pagination: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      page: 0,
      pageSize: 10,
      searchParams: {},
      /**
       * 导出
       */
      excelVisible: false,
      exportColumns: [
        { title: this.$t('accounting.receipt.number'), dataIndex: 'workOrderNumber' } /*单据编号*/,
        { title: this.$t('accounting.type.of.document'), dataIndex: 'typeName' } /*单据类型*/,
        { title: this.$t('accounting.company.documents'), dataIndex: 'companyName' } /*单据公司*/,
        { title: this.$t('accounting.the.applicant'), dataIndex: 'employeeName' } /*申请人*/,
        {
          title: this.$t('accounting.application.date'),
          dataIndex: 'requisitionDate',
        } /*申请日期*/,
        { title: this.$t('accounting.view.currencycode'), dataIndex: 'currencyName' } /*币种*/,
        { title: this.$t('accounting.the.amount.of'), dataIndex: 'amount' } /*金额*/,
        { title: this.$t('accounting.state'), dataIndex: 'status' } /*状态*/,
        { title: this.$t('accounting.note'), dataIndex: 'remark' } /*备注*/,
      ],
      lsVisible: false,
      selectorItem: {},
      extraParams: {},
    };
  }

  handleEvent = (key, value) => {
    const { searchParams } = this.state;
    switch (key) {
      case 'APPLIER': {
        if (value && value[0]) {
          searchParams.employeeId = value[0].id;
        } else {
          searchParams.employeeId = '';
        }
        break;
      }
      case 'requisitionDateFrom': {
        if (value) {
          searchParams.requisitionDateFrom = moment(value).format('YYYY-MM-DD');
        } else {
          searchParams.requisitionDateFrom = '';
        }
        break;
      }
      case 'requisitionDateTo': {
        if (value) {
          searchParams.requisitionDateTo = moment(value).format('YYYY-MM-DD');
        } else {
          searchParams.requisitionDateTo = '';
        }
        break;
      }
      default:
        if (value) {
          searchParams[key] = value;
        }
    }
  };

  // 单号搜索
  onDocumentSearch = value => {
    if (value) {
      const { searchParams } = this.state;
      searchParams.workOrderNumber = value;
      this.setState({ searchParams }, () => {
        const { searchParams } = this.state;
        this.table.search(searchParams);
      });
    } else this.table.search();
  };

  search = values => {
    this.setState(
      {
        searchParams: {
          ...values,
          requisitionDateFrom:
            values.requisitionDateFrom && moment(values.requisitionDateFrom).format('YYYY-MM-DD'),
          requisitionDateTo:
            values.requisitionDateTo && moment(values.requisitionDateTo).format('YYYY-MM-DD'),
        },
      },
      () => {
        const { searchParams } = this.state;
        this.table.search(searchParams);
      }
    );
  };

  // 清除
  clear = () => {
    this.setState({ searchParams: {} }, () => {
      const { searchParams } = this.state;
      this.table.search(searchParams);
    });
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

  // 导出数据
  export = result => {
    const hide = message.loading(
      this.$t('accounting.generating.files.waiting')
    ); /*正在生成文件，请等待*/
    const { exportParams } = this.state;
    accountingQueryService
      .export(result, exportParams)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('accounting.operation.succeeded')); /*操作成功*/
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
        message.error(this.$t('accounting.download.failed.try.again')); // 下载失败，请重试!
        this.setState({
          btLoading: false,
        });
        hide();
      });
  };

  /**
   * 表格的行点击事件
   */
  onTableRowClick = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/financial-view/gl-work-order/gl-work-order-detail/${record.id}/${
          record.documentOid
        }`,
      })
    );
  };

  /**
   * 渲染函数
   */
  render() {
    const {
      loading,
      exportLoading,
      data,
      searchForm,
      pagination,
      columns,
      showSlideFrame,
      nowItem,
      slideFrameTitle,
    } = this.state;
    const { exportColumns, excelVisible, btLoading } = this.state;
    return (
      <div className="reimburse-container">
        <SearchArea
          searchForm={searchForm}
          maxLength={4}
          eventHandle={this.handleEvent}
          submitHandle={this.search}
          clearHandle={this.clear}
          wrappedComponentRef={inst => (this.formRef = inst)}
        />

        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={18}>
                <Button loading={btLoading} type="primary" onClick={this.onExportClick}>
                  {this.$t('accounting.export.accounting.work.order')}
                  {/*导出核算工单*/}
                </Button>
              </Col>
              <Col span={6}>
                <Search
                  placeholder={this.$t(
                    'account.input.accounting.work.order.number'
                  )} /*请输入核算工单单号*/
                  onSearch={this.onDocumentSearch}
                  enterButton
                />
              </Col>
            </Row>
          </div>
        </div>
        <CustomTable
          onClick={this.onTableRowClick}
          columns={columns}
          url={`${config.accountingUrl}/api/general/ledger/work/order/head/finance/query`}
          ref={ref => (this.table = ref)}
          onRowClick={this.onTableRowClick}
        />
        <ExcelExporter
          visible={excelVisible}
          onOk={this.export}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={this.$t('accounting.accounting.work.order.query')} /*核算工单财查询*/
          onCancel={this.onExportCancel}
          excelItem="ACCOUTN_FINANCIAL_QUERY"
        />
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
)(AccountingGLWorkOrder);
