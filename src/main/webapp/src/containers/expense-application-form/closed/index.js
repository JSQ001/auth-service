import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Modal, Row, Col, Input, message, Form } from 'antd';
import config from 'config';
import moment from 'moment';
const FormItem = Form.Item;
const { TextArea } = Input;
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import ExcelExporter from 'widget/excel-exporter';
import service from 'containers/expense-application-form/service';
import FileSaver from 'file-saver';
const Search = Input.Search;
import { messages } from 'utils/utils';

class ExpenseApplicationClosed extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
      },
      status: {
        NOT_CLOSED: this.$t('expense.not.closed') /*未关闭*/,
        CLOSED: this.$t('expense.closed') /*已关闭*/,
        PARTIAL_CLOSED: this.$t('expense.part.of.the.closing') /*部分关闭*/,
      },
      searchForm: [
        {
          type: 'list',
          colSpan: 6,
          id: 'companyId',
          label: this.$t('expense.company.documents') /*单据公司*/,
          listType: 'company',
          valueKey: 'id',
          labelKey: 'name',
          options: [],
          listExtraParams: { setOfBooksId: this.props.company.setOfBooksId },
          single: false,
        },
        {
          type: 'select',
          id: 'typeId',
          label: this.$t('expense.type.of.document') /*单据类型*/,
          options: [],
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            {
              type: 'date',
              id: 'dateFrom',
              label: this.$t('expense.application.date.from'),
            } /*申请日期从*/,
            {
              type: 'date',
              id: 'dateTo',
              label: this.$t('expense.application.date.to'),
            } /*申请日期至*/,
          ],
          colSpan: 7,
        },
        {
          type: 'list',
          listType: 'select_authorization_user',
          options: [],
          id: 'employeeId',
          label: this.$t('expense.reverse.apply.name') /*申请人*/,
          labelKey: 'userName',
          valueKey: 'userId',
          single: true,
          colSpan: 5,
          defaultValue: [{ userName: this.props.user.userName, userId: this.props.user.id }],
        },
        {
          type: 'select',
          key: 'closedFlag',
          id: 'closedFlag',
          label: this.$t('expense.policy.enabled') /*状态*/,
          options: [
            { label: this.$t('expense.not.closed'), value: 'NOT_CLOSED' } /*未关闭*/,
            { label: this.$t('expense.part.of.the.closing'), value: 'PARTIAL_CLOSED' } /*部分关闭*/,
          ],
          labelKey: 'label',
          valueKey: 'value',
          colSpan: 6,
        },
        {
          type: 'select',
          key: 'currency',
          id: 'currencyCode',
          label: this.$t('expense.policy.currencyName') /*币种*/,
          getUrl: `${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`,
          getParams: { setOfBooksId: this.props.company.setOfBooksId },
          options: [],
          method: 'get',
          labelKey: '${currency}-${currencyName}',
          valueKey: 'currency',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 7,
          items: [
            {
              type: 'input',
              id: 'amountFrom',
              label: this.$t('expense.the.amount.from'),
            } /*金额从*/,
            { type: 'input', id: 'amountTo', label: this.$t('expense.the.amount.to') } /*金额至*/,
          ],
        },
        {
          type: 'input',
          id: 'remarks',
          colSpan: 5,
          label: this.$t('expense.reverse.remark') /*备注*/,
        },
      ],
      columns: [
        {
          title: this.$t('expense.odd.numbers') /*单号*/,
          dataIndex: 'documentNumber',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('expense.company.documents') /*单据公司*/,
          dataIndex: 'companyName',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('expense.type.of.document') /*单据类型*/,
          dataIndex: 'typeName',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('expense.reverse.apply.name') /*申请人*/,
          dataIndex: 'employeeName',
          width: 80,
        },
        {
          title: this.$t('expense.application.date') /*申请日期*/,
          dataIndex: 'requisitionDate',
          width: 120,
          render: value => moment(value).format('YYYY-MM-DD'),
        },
        {
          title: this.$t('expense.policy.currencyName') /*币种*/,
          dataIndex: 'currencyCode',
          width: 90,
        },
        {
          title: this.$t('expense.the.amount.of') /*金额*/,
          dataIndex: 'amount',
          width: 150,
          render: value => this.filterMoney(value),
        },
        {
          title: this.$t('expense.local.currency.amount') /*本币金额*/,
          dataIndex: 'functionalAmount',
          width: 150,
          render: value => this.filterMoney(value),
        },
        {
          title: this.$t('expense.can.close.the.amount') /*可关闭金额*/,
          dataIndex: 'canCloseAmount',
          width: 150,
          render: value => this.filterMoney(value),
        },
        {
          title: this.$t('expense.reverse.remark') /*备注*/,
          dataIndex: 'remarks',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t('expense.policy.enabled') /*状态*/,
          dataIndex: 'closedFlag',
          width: 110,
          render: value => this.state.status[value],
        },
      ],
      searchParams: { employeeId: this.props.user.id },
      selectedRowKeys: [],
      exportVisible: false,
      modalVisible: false,
      btLoading: false,
    };
  }

  componentWillMount() {
    let searchForm = this.state.searchForm;
    service
      .getCreatedApplicationTypeList({
        setOfBooksId: this.props.company.setOfBooksId,
      })
      .then(res => {
        searchForm[1].options = res.data.map(o => ({ value: o.id, label: o.typeName }));
        this.setState({ searchForm });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  //获取列表
  getList = () => {
    let { searchParams } = this.state;

    searchParams.dateFrom &&
      (searchParams.dateFrom = moment(searchParams.dateFrom).format('YYYY-MM-DD'));
    searchParams.dateTo && (searchParams.dateTo = moment(searchParams.dateTo).format('YYYY-MM-DD'));

    this.table.search(searchParams);
  };

  //搜索
  search = values => {
    this.setState({ searchParams: { ...this.state.searchParams, ...values } }, () => {
      this.getList();
    });
  };

  //单号搜索
  searchNumber = value => {
    this.setState({ searchParams: { ...this.state.searchParams, documentNumber: value } }, () => {
      this.getList();
    });
  };

  //清除
  clear = () => {
    this.setState(
      { searchParams: { documentNumber: this.state.searchParams.documentNumber } },
      () => {
        this.getList();
      }
    );
  };

  //跳转到详情
  handleRowClick = recode => {
    this.props.dispatch(
      routerRedux.push({
        pathname:
          '/expense-application/expense-application-closed/expense-application-detail/' + recode.id,
      })
    );
  };

  // 选择
  onRowSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys });
  };

  // 确认关闭按钮
  handleClose = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ btLoading: true });
        let params = {
          headerIds: this.state.selectedRowKeys,
          messages: values.messages,
        };
        service
          .closedFunction(params)
          .then(res => {
            if (res.status === 200) {
              message.success(messages('common.operate.success') /**操作成功 */);
              this.setState({
                btLoading: false,
                selectedRowKeys: [],
                modalVisible: false,
              });
              this.getList();
            }
          })
          .catch(e => {
            this.setState({ btLoading: false });
            if (e.response) {
              message.error(
                messages('common.operate.filed' /*保存失败*/) + '!' + e.response.data.message
              );
            } else {
              message.error(messages('common.operate.filed'));
            }
          });
      }
    });
  };
  // 导出
  confirmExport = params => {
    let hide = message.loading(this.$t('expense.being.generated.file.please.wait1')); // 正在生成文件，请等待......
    let { searchParams } = this.state;
    searchParams.dateFrom &&
      (searchParams.dateFrom = moment(searchParams.dateFrom).format('YYYY-MM-DD'));
    searchParams.dateTo && (searchParams.dateTo = moment(searchParams.dateTo).format('YYYY-MM-DD'));
    this.setState({ btLoading: true });
    service
      .export(params, searchParams)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('expense.operation.is.successful')); /*操作成功*/
          let fileName = res.headers['content-disposition'].split('filename=')[1];
          let f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          this.setState({ btLoading: false });
          hide();
        }
      })
      .catch(() => {
        this.setState({ btLoading: false });
        message.error(this.$t('expense.download.failed')); // 下载失败，请重试!
        hide();
      });
  };

  render() {
    const { searchForm, columns, selectedRowKeys, btLoading } = this.state;
    const rowSelection = {
      onChange: this.onRowSelectChange,
      selectedRowKeys: selectedRowKeys,
    };
    let disabled = !selectedRowKeys.length;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { getFieldDecorator } = this.props.form;
    return (
      <div className="table-header">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          maxLength={4}
          clearHandle={this.clear}
        />
        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col span={18}>
            <div className="table-header-buttons">
              <Button
                loading={btLoading}
                type="primary"
                onClick={() => this.setState({ modalVisible: true })}
                disabled={disabled}
              >
                {this.$t('expense.shut.down')}
                {/*关闭*/}
              </Button>
              <Button
                loading={btLoading}
                onClick={() => this.setState({ exportVisible: true, btLoading: true })}
              >
                {this.$t('expense.export')}
                {/*导出*/}
              </Button>
            </div>
          </Col>
          <Col span={6}>
            <Search
              placeholder={this.$t(
                'expense.please.enter.the.application.number.alone'
              )} /*请输入申请单单号*/
              style={{ width: '100%' }}
              onSearch={this.searchNumber}
              enterButton
            />
          </Col>
        </Row>
        <CustomTable
          onClick={this.handleRowClick}
          rowSelection={rowSelection}
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.expenseUrl}/api/expense/application/header/query/closed/condition`}
          params={{ employeeId: this.props.user.id }}
          onRowClick={this.handleRowClick}
          scroll={{ x: 1300 }}
        />
        {/** 关闭按钮的弹出框 */}
        <Modal
          title={this.$t('expense.close.the.application.form')} /*关闭申请单*/
          visible={this.state.modalVisible}
          onOk={this.handleClose}
          confirmLoading={btLoading}
          destroyOnClose={true}
          onCancel={() => this.setState({ modalVisible: false })}
          okText={this.$t('expense.confirm')} /*确认*/
          cancelText={this.$t('expense.cancel')} /*取消*/
          cancelButtonProps={{ loading: btLoading }}
        >
          <Form>
            <FormItem {...formItemLayout} label={this.$t('expense.close.the.reason')}>
              {/*关闭原因*/}
              {getFieldDecorator('messages', {
                rules: [
                  {
                    required: true,
                    message: this.$t(
                      'expense.please.enter.the.shut.down.reason'
                    ) /*请输入关闭原因*/,
                  },
                ],
              })(
                <TextArea
                  autosize={{ minRows: 2 }}
                  style={{ minWidth: '100%' }}
                  placeholder={this.$t({ id: 'common.please.enter' } /*请输入*/)}
                />
              )}
            </FormItem>
          </Form>
        </Modal>
        {/* 导出 */}
        <ExcelExporter
          visible={this.state.exportVisible}
          onOk={this.confirmExport}
          columns={columns}
          canCheckVersion={false}
          fileName={this.$t('expense.application.form')} /*申请单*/
          excelItem={'PREPAYMENT_FINANCIAL_QUERY'}
          onCancel={() => this.setState({ exportVisible: false, btLoading: false })}
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
const wrappedExpenseApplicationClosed = Form.create()(ExpenseApplicationClosed);
export default connect(mapStateToProps)(wrappedExpenseApplicationClosed);
