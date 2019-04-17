import React, { Component } from 'react';
import { Button, Dropdown, Menu, Icon, Popconfirm, message, Badge, Modal } from 'antd';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import moment from 'moment';
import ExcelExporter from 'widget/excel-exporter';
import ListSelector from 'widget/list-selector';
import 'styles/my-wallet/new-invoice.scss';
import config from 'config';
import { connect } from 'dva';
import FileSaver from 'file-saver';
import Invoice from './invoice';
import NewInvoice from './new-invoice';

import service from './my-wallet-service';

class MyWalletTab extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          colSpan: 6,
          id: 'invoiceTypeId',
          label: this.$t('expense.wallet.invoiceType') /* 发票类型 */,
          options: [],
          method: 'get',
          getUrl: `${config.expenseUrl}/api/invoice/type/query/for/invoice`,
          getParams: { tenantId: props.company.tenantId, setOfBooksId: props.company.setOfBooksId },
          labelKey: 'invoiceTypeName',
          valueKey: 'id',
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'invoiceCode',
          label: this.$t('expense.wallet.invoiceCode') /* 发票代码 */,
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'invoiceNo',
          label: this.$t('expense.wallet.invoiceNo') /* 发票号码 */,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'abc',
          items: [
            {
              type: 'date',
              id: 'invoiceDateFrom',
              label: this.$t('expense.wallet.invoiceDateFrom'),
            },
            { type: 'date', id: 'invoiceDateTo', label: this.$t('expense.wallet.invoiceDateTo') },
          ],
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'aaa',
          items: [
            {
              type: 'inputNumber',
              id: 'invoiceAmountFrom',
              label: this.$t('expense.wallet.invoiceAmountFrom'),
            },
            {
              type: 'inputNumber',
              id: 'invoiceAmountTo',
              label: this.$t('expense.wallet.invoiceAmountTo'),
            },
          ],
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'createdMethod',
          label: this.$t('expense.wallet.createMethod'),
          options: [],
          method: 'get',
          getUrl: `${config.baseUrl}/api/custom/enumerations/template/by/type`,
          getParams: { type: 'CREATED_METHOD' },
          labelKey: 'name',
          valueKey: 'value',
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'checkResult',
          label: this.$t('expense.wallet.checkResult'),
          options: [
            { label: this.$t('expense.wallet.checked'), value: true },
            { label: this.$t('expense.wallet.noChecked'), value: false },
          ],
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'reportProgress',
          label: this.$t('expense.wallet.reportProgress'),
          options: [],
          method: 'get',
          getUrl: `${config.baseUrl}/api/custom/enumerations/template/by/type`,
          getParams: { type: 'REPORT_PROGRESS' },
          labelKey: 'name',
          valueKey: 'value',
        },
      ],
      columns: [
        {
          title: this.$t('expense.wallet.invoiceType'),
          dataIndex: 'invoiceTypeName',
          width: 200,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.invoiceCode') /* 发票代码 */,
          dataIndex: 'invoiceCode',
          width: 200,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.invoiceNo') /* 发票号码 */,
          dataIndex: 'invoiceNo',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.invoiceDate') /*开票日期*/,
          dataIndex: 'invoiceDate',
          width: 150,
          align: 'center',
          render: value => value && moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t('expense.wallet.invoiceAmount') /*金额合计*/,
          dataIndex: 'invoiceAmount',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.createMethod') /*录入方式*/,
          dataIndex: 'createdMethodName',
          width: 90,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.checkResult') /*验真状态*/,
          dataIndex: 'checkResult',
          width: 80,
          tooltips: true,
          render: value => {
            return value ? (
              <Badge status="success" text={this.$t('expense.wallet.checked')} />
            ) : (
              <Badge status="error" text={this.$t('expense.wallet.noChecked')} />
            );
          },
        },
        {
          title: this.$t('expense.wallet.reportProgress') /*报账进度*/,
          dataIndex: 'reportProgressName',
          width: 80,
          tooltips: true,
        },
        {
          title: this.$t('expense.wallet.checkDetail'),
          dataIndex: 'tableName9',
          width: 80,
          align: 'center',
          render: (value, record) => (
            <a onClick={e => this.checkDetails(record, e)}>
              {this.$t('expense.wallet.checkDetail')}
            </a>
          ),
        },
        {
          title: this.$t('expense.wallet.quoteBill'),
          dataIndex: 'tableName10',
          align: 'center',
          width: 120,
          render: (value, record) => (
            <a onClick={e => this.billingClick(record, e)}>{this.$t('expense.wallet.quoteBill')}</a>
          ),
        },
      ],
      selectedRowKeys: [],
      menuList: [],
      currentType: {},
      visible: false,
      exportVisible: false,
      exportColumns: [
        { title: this.$t('expense.wallet.invoiceType'), dataIndex: 'invoiceTypeName' },
        { title: this.$t('expense.wallet.invoiceCode') /* 发票代码 */, dataIndex: 'invoiceCode' },
        { title: this.$t('expense.wallet.invoiceNo') /* 发票号码 */, dataIndex: 'invoiceNo' },
        { title: this.$t('expense.wallet.invoiceDate'), dataIndex: 'stringInvoiceDate' },
        { title: this.$t('expense.wallet.invoiceAmount'), dataIndex: 'invoiceAmount' },
        { title: this.$t('expense.wallet.createMethod'), dataIndex: 'createdMethodName' },
        { title: this.$t('expense.wallet.checkResult'), dataIndex: 'stringCheckResult' },
        { title: this.$t('expense.wallet.reportProgress'), dataIndex: 'reportProgressName' },
      ],
      showInvoiceDetail: false,
      showListSelector: false,
      invoiceId: '',
    };
  }

  componentDidMount() {
    this.getMenuList();
  }

  // 搜索
  search = params => {
    const data = {
      ...params,
      invoiceDateFrom: params.invoiceDateFrom && params.invoiceDateFrom.format('YYYY-MM-DD'),
      invoiceDateTo: params.invoiceDateTo && params.invoiceDateTo.format('YYYY-MM-DD'),
    };
    this.table.search(data);
  };

  // 点击删除发票按钮
  deleteInvoice = () => {
    const { selectedRowKeys } = this.state;
    service
      .deleteInvoice(selectedRowKeys)
      .then(() => {
        message.success(this.$t('common.delete.success'));
        this.table.search();
      })
      .catch(err => {
        const data = err.response.data;
        Modal.error({
          content: (
            <div>
              <div className="modal-tips">
                {data.map(item => (
                  <React.Fragment key={item.number}>
                    <div>
                      {this.$t('expense.wallet.invoiceCode') /* 发票代码 */}：{item.code}
                    </div>
                    <p>
                      {this.$t('expense.wallet.invoiceNo') /* 发票号码 */}：{item.number}
                    </p>
                  </React.Fragment>
                ))}
              </div>
              <div className="modal-tips-mess">
                {this.$t('expense.wallet.invoice.delete.warning')}
                {/* 	以上发票已被关联使用，不可删除！ */}
              </div>
            </div>
          ),
        });
      });
  };

  // 发票验真
  invoiceValidate = () => {
    const { selectedRowKeys } = this.state;
    service
      .validateInvoice(selectedRowKeys)
      .then(res => {
        message.success(this.$t('expense.wallet.check.success'));
        this.table.search();
      })
      .catch(err => {
        const data = err.response.data;
        Modal.error({
          content: (
            <div>
              <div>{this.$t('expense.wallet.check.total', { no: 2 })}</div>
              {/* 本次查验发票共{no}张 */}
              <p>{this.$t('expense.wallet.check.fail.success.total', { success: 4, fail: 2 })}</p>
              {/* 	验真通过发票：{success}张，验真失败发票：{fail}张 */}
              <div className="validate-row">{this.$t('expense.wallet.invoice.fail.info')}：</div>
              {/* 失败发票及原因如下 */}
              <div className="modal-tips-validate">
                {data.map(item => (
                  <React.Fragment key={item.number}>
                    <div className="validate-row">
                      {this.$t('expense.wallet.invoiceCode') /* 发票代码 */}：{item.code}，{this.$t(
                        'expense.wallet.invoiceNo'
                      ) /* 发票号码 */}：{item.number}，{this.$t(
                        'expense.wallet.invoice.fail.why'
                      ) /* 失败原因 */}：{item.message}
                    </div>
                  </React.Fragment>
                ))}
              </div>
            </div>
          ),
          width: 700,
        });
      });
  };

  // 导出
  confirmExport = params => {
    const hide = message.loading(this.$t('importer.spanned.file'));
    service
      .exportInvoiceInfo(params, this.props.user.id)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('finance.view.search.exportSuccess'));
          const fileName = res.headers['content-disposition'].split('filename=')[1];
          const f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          hide();
        }
      })
      .catch(() => {
        message.error(this.$t('importer.download.error.info'));
        hide();
      });
  };

  // 选择
  onRowSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys });
  };

  // 点击添加发票下拉框
  handleMenuClick = e => {
    this.setState({ visible: true, currentType: { id: e.key, name: e.item.props.children } });
  };

  // 改变侧拉框的 标题
  changeTitle = value => {
    this.setState({ currentType: value });
  };

  // 获取添加发票按钮下拉框
  getMenuList = () => {
    service
      .getInvoiceType()
      .then(res => this.setState({ menuList: res.data }))
      .catch(err => message.error(err.response.data.message));
  };

  // 查看详情
  checkDetails = (record, e) => {
    e.preventDefault();
    this.setState({ showInvoiceDetail: true, invoiceId: record.id });
  };

  // 关联报账单
  billingClick = (record, e) => {
    e.preventDefault();
    this.setState({
      showListSelector: true,
      invoiceId: record.id,
    });
  };

  // 跳转报账单详情
  ToBillDetail = id => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/reimburse-detail/${id}`,
      })
    );
  };

  // 侧滑框关闭
  close = flag => {
    this.setState({ visible: false });
    flag && this.table.search();
  };

  render() {
    const {
      searchForm,
      selectedRowKeys,
      columns,
      menuList,
      visible,
      currentType,
      exportVisible,
      exportColumns,
      showListSelector,
      showInvoiceDetail,
      invoiceId,
    } = this.state;
    const rowSelection = {
      onChange: this.onRowSelectChange,
    };
    const selectorItem = {
      title: this.$t('expense.wallet.quote.bills'),
      url: `${config.expenseUrl}/api/invoice/head/query/invoice/line/expense/by/headId`,
      searchForm: [
        {
          type: 'input',
          id: 'requisitionNumber',
          label: this.$t('expense.wallet.expenseNo') /* 报账单单号 */,
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'reportTypeId',
          label: this.$t('expense.wallet.quoteBillType') /* 报账单类型 */,
          colSpan: 6,
          options: [],
          method: 'get',
          getUrl: `${config.expenseUrl}/api/expense/report/type/query`,
          getParams: { setOfBooksId: this.props.company.setOfBooksId, page: 0, size: 1000 },
          labelKey: 'reportTypeName',
          valueKey: 'id',
        },
      ],
      columns: [
        {
          title: this.$t('expense.wallet.expenseNo') /* 报账单单号 */,
          dataIndex: 'requisitionNumber',
          align: 'center',
          render: (value, record) => (
            <a onClick={() => this.ToBillDetail(record.reportHeadId)}>{value}</a>
          ),
        },
        {
          title: this.$t('expense.wallet.quoteBillType') /* 报账单类型 */,
          dataIndex: 'reportTypeName',
          align: 'center',
        },
        { title: this.$t('detail.apply.id'), dataIndex: 'applicantName', align: 'center' },
        {
          title: this.$t('exp.adjust.apply.date'),
          dataIndex: 'requisitionDate',
          render: value => value && moment(value).format('YYYY-MM-DD'),
          align: 'center',
        },
        {
          title: this.$t('expense.wallet.invoiceLineNo'),
          dataIndex: 'invoiceLineSequence',
          align: 'center',
        },
        {
          title: this.$t('expense.wallet.expenseType'),
          dataIndex: 'expenseTypeName',
          align: 'center',
        },
        { title: this.$t('expense.wallet.lineRemark'), dataIndex: 'remarks', align: 'center' },
      ],
      key: 'id',
    };
    const dropMenu = (
      <Menu onClick={this.handleMenuClick}>
        {menuList.map(item => {
          return <Menu.Item key={item.id}>{item.invoiceTypeName}</Menu.Item>;
        })}
      </Menu>
    );
    const isDisabled = !selectedRowKeys.length;

    return (
      <div style={{ marginTop: '15px' }}>
        <SearchArea searchForm={searchForm} submitHandle={this.search} maxLength={4} />

        <div className="add-invoice-btn">
          <Dropdown
            overlay={dropMenu}
            trigger={['click']}
            getPopupContainer={() => document.querySelector('.ant-layout-content > .ant-tabs')}
          >
            <Button type="primary">
              {this.$t('expense.wallet.manual.add')}
              <Icon type="down" />
            </Button>
          </Dropdown>
          {isDisabled ? (
            <Button disabled style={{ margin: '0 10px' }}>
              {this.$t('expense.wallet.delete.invoice')}
            </Button>
          ) : (
            <Popconfirm title={this.$t('common.confirm.delete')} onConfirm={this.deleteInvoice}>
              <Button style={{ margin: '0 10px' }}>
                {this.$t('expense.wallet.delete.invoice')}
              </Button>
            </Popconfirm>
          )}
          <Button
            disabled={isDisabled}
            style={{ marginRight: '10px' }}
            onClick={this.invoiceValidate}
          >
            {this.$t('expense.wallet.invoice.check')}
          </Button>
          <Button onClick={() => this.setState({ exportVisible: true })}>
            {this.$t('common.export') /* 导出 */}
          </Button>
        </div>

        <CustomTable
          columns={columns}
          rowSelection={rowSelection}
          ref={ref => (this.table = ref)}
          url={`${config.expenseUrl}/api/invoice/head/query/by/cond?createdBy=${
            this.props.user.id
          }`}
        />

        {/* 新增侧滑框 */}
        <SlideFrame
          title={currentType.name}
          show={visible}
          onClose={() => this.setState({ visible: false })}
          width="65vw"
        >
          <NewInvoice
            invoiceTypeId={currentType.id}
            close={this.close}
            menuList={menuList}
            changeTitle={this.changeTitle}
          />
        </SlideFrame>

        {/* 导出 */}
        <ExcelExporter
          visible={exportVisible}
          onOk={this.confirmExport}
          columns={exportColumns}
          canCheckVersion={false}
          fileName={this.$t('expense.wallet.myWallet')} /* 我的票夹 */
          onCancel={() => this.setState({ exportVisible: false })}
          excelItem="PREPAYMENT_FINANCIAL_QUERY"
        />

        {/* 发票详情弹出框 */}
        <Invoice
          cancel={() => {
            this.setState({ showInvoiceDetail: false });
          }}
          id={invoiceId}
          visible={showInvoiceDetail}
          validate
        />

        {/* 关联报账单 */}
        <ListSelector
          visible={showListSelector}
          selectorItem={selectorItem}
          extraParams={{ headId: invoiceId }}
          onCancel={() => this.setState({ showListSelector: false })}
          hideRowSelect
          hideFooter
        />
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

export default connect(mapStateToProps)(MyWalletTab);
