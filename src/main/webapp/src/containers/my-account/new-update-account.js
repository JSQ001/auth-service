import React from 'react';
import { connect } from 'dva';
import { DatePicker, Form, Button, Input, Select, message, Row, Col } from 'antd';
const FormItem = Form.Item;
import moment from 'moment';
import CustomAmount from 'widget/custom-amount';
import FileUpload from 'widget/file-upload';
import config from 'config';
import AddInvoice from 'components/Widget/Template/invoice/create-invoice-modal';
import InvoiceDetail from 'components/Widget/Template/invoice/invoice-detail';
import SelectInvoice from 'components/Widget/Template/invoice/select-invoice';
import SelectApplicationType from 'widget/select-application-type';
import SelectType from 'components/Widget/Template/invoice/select-type';
import service from './service';
import Upload from 'widget/upload-button';

class NewUpdateAccount extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currency: [],
      showInvoice: false,
      showAddInvoice: false,
      importInvoice: false,
      showInvoiceDetail: false,
      invoiceId: '',
      invoiceType: '',
      invoiceDetail: [],
      selectedInvoice: [],
      attachments: [],
      expenseType: {},
      attachmentOid: [],
      fileList: [],
    };
  }

  componentDidMount() {
    let { record } = this.props.params;
    if (record.id) {
      let fileList = [];
      let invoiceDetail = [];
      if (record.attachments) {
        fileList = record.attachments;
      }
      if (record.invoiceHead) {
        record.invoiceHead.map(item => {
          item.invoiceLineList.map(i =>
            invoiceDetail.push({ ...i, fromBook: item.fromBook, invoice: item })
          );
        });
      }
      this.setState({ fileList, invoiceDetail });
    }
  }

  //选择费用类型，所选类型是否有动态字段
  handleSelectExpenseType = expenseType => {
    this.setState({ expenseType });
  };

  getCurrency = () => {
    !this.state.currency.length &&
      this.service.getCurrencyMdataList().then(res => {
        this.setState({
          currency: res.data.records,
        });
      });
  };

  handleInvoice = value => {
    this.setState({
      invoiceType: value,
      showAddInvoice: true,
    });
  };

  handleCloseInvoice = () => {
    this.setState({ showAddInvoice: false });
  };

  handleImportInvoice = () => {
    this.setState({
      importInvoice: true,
    });
  };

  handleDeleteLink = data => {
    const { record } = this.props.params;
    console.log(record);
    let params = {
      expenseBookId: record.id,
      invoiceHeadId: data.invoice.id,
      invoiceLineId: data.id,
    };
    if (record.id && this.state.invoiceDetail.length) {
      service.deleteLinkInvoice(params).then(res => {
        message.success(this.$t('common.delete.success'));
      });
    }
    this.setState({
      refresh: true,
      invoiceDetail: this.state.invoiceDetail.filter(
        item => data.id.toString() !== item.id.toString()
      ),
    });
  };

  //数组去重
  myConcat = (origin, key = 'id') => {
    let temp = {};
    let target = [];
    origin.map(item => (temp[item[key]] = item));
    for (let item in temp) {
      target.push(temp[item]);
    }
    return target;
  };

  handleImportOk = result => {
    let { invoiceDetail } = this.state;
    this.setState({
      invoiceDetail: this.myConcat(invoiceDetail.concat(result)),
      selectedInvoice: result,
      importInvoice: false,
      showInvoice: result.length ? true : false,
    });
  };

  handleSuccess = value => {
    let invoiceDetail = [];
    value.map(i => {
      let Head = i.invoiceHead;
      let Line = i.invoiceLineList;
      let invoices = {
        ...Head,
        invoiceLineList: Line,
      };
      i.invoiceLineList.map(item => {
        invoiceDetail.push({
          ...item,
          invoice: invoices,
          fromBook: true,
        });
      });
    });
    invoiceDetail = this.state.invoiceDetail.concat(invoiceDetail);
    this.setState({
      invoiceDetail,
      showAddInvoice: false,
    });
  };

  onCancel = () => {
    this.props.onClose(this.state.refresh);
  };

  disabledDate(current) {
    // Can not select days before today and today
    return (
      current &&
      current <
        moment()
          .endOf('day')
          .subtract(1, 'days')
    );
  }

  // 提交
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFields((err, value) => {
      if (err) return;
      this.setState({ loading: true });
      const { invoiceDetail, attachmentOid, expenseType } = this.state;
      this.setState({ loading: false });
      let type = 'post';
      let messageValue = '新增成功';

      value.attachmentOid = attachmentOid.toString();
      value.expenseTypeId = value.expenseType.id;
      value.invoiceMethod = invoiceDetail.createdMethod;
      value.invoiceHead = invoiceDetail.map(item => item.invoice);
      value.tenantId = this.props.company.tenantId;
      value.setOfBooksId = this.props.company.setOfBooksId;
      value.exchangeRate = 1;
      value.functionalAmount = 1;

      value.fields =
        expenseType.fields &&
        expenseType.fields.map(item => {
          return { ...item, value: value[item.id] };
        });

      delete value.expenseType;
      if (!expenseType.entryMode) {
        value.price = value.amount;
        value.quantity = 1;
      }

      if (this.props.params.record.id) {
        messageValue = '编辑成功';
        type = 'put';
        value.versionNumber = this.props.params.record.versionNumber;
        value.id = this.props.params.record.id;
        value.tenantId = this.props.params.record.id;
      }
      service
        .addAccount(type, value)
        .then(res => {
          message.success(messageValue);
          this.setState({ loading: false }, () => {
            this.props.onClose(true);
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    });
  };

  //上传附件
  handleUpload = Oids => {
    this.setState({ attachmentOid: Oids });
  };

  renderItem(item) {
    switch (item.fieldDataType) {
      case 'TEXT': {
        return <Input placeholder={this.$t('common.please.enter')} />;
      }
      // case ""
    }
  }

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const {
      currency,
      showInvoice,
      attachmentOid,
      fileList,
      invoiceDetail,
      showAddInvoice,
      importInvoice,
      expenseType,
      selectedInvoice,
      invoiceId,
      invoiceType,
    } = this.state;
    const { record } = this.props.params;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10, offset: 0 },
    };
    let dynamic = record.id ? record.fields : expenseType.fields;

    return (
      <div className="new-update-account">
        <Form onSubmit={this.handleSave}>
          {/* 费用类型 */}
          <FormItem {...formItemLayout} label={this.$t('common.expense.type') /*费用类型*/}>
            {getFieldDecorator('expenseType', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.select' }),
                },
              ],
              initialValue: { name: record.expenseTypeName, id: record.expenseTypeId },
            })(
              <SelectApplicationType
                title="费用类型"
                onChange={this.handleSelectExpenseType}
                url={`${config.expenseUrl}/api/expense/types/${
                  this.props.company.setOfBooksId
                }/query`}
                params={{
                  //setOfBooksId: this.props.company.setOfBooksId
                  typeFlag: 1,
                  roleType: 'TENANT',
                }}
                filter={item => item.enabled}
              />
            )}
          </FormItem>
          {/* 发生日期 */}
          <FormItem {...formItemLayout} label={this.$t('common.happened.date') /*发生日期*/}>
            {getFieldDecorator('expenseDate', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.select'),
                },
              ],
              initialValue: record.id ? moment(new Date(record.createdDate)) : null,
            })(
              <DatePicker
                format="YYYY-MM-DD"
                allowClear={false}
                disabledDate={this.disabledDate}
                getCalendarContainer={this.getPopupContainer}
                style={{ width: '100%' }}
              />
            )}
          </FormItem>
          {/*动态属性*/
          dynamic &&
            dynamic.map(item => {
              return (
                <FormItem {...formItemLayout} key={item.id} label={item.name}>
                  {getFieldDecorator(item.id, {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.select'),
                      },
                    ],
                    initialValue: record.id ? item.value : item.defaultValueMode,
                  })(this.renderItem(item))}
                </FormItem>
              );
            })}
          <FormItem {...formItemLayout} label={this.$t('common.currency') /*币种*/}>
            {getFieldDecorator('currencyCode', {
              rules: [{ required: true }],
              initialValue: record.id ? record.currencyCode : this.props.company.baseCurrency,
            })(
              <Select
                dropdownMatchSelectWidth={false}
                showSearch={true}
                onFocus={this.getCurrency}
                optionFilterProp="children"
                filterOption={(input, option) =>
                  option.props.children
                    .toString()
                    .toLowerCase()
                    .indexOf(input.toLowerCase()) >= 0
                }
                placeholder={this.$t('common.please.select') /* 请选择 */}
                //disabled={currencyEditable}
              >
                {currency.map(item => (
                  <Select.Option key={item.currencyCode}>
                    {item.currencyCode + '-' + item.currencyName}
                  </Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('expense.enter.invoice') /*录入发票*/}>
            {getFieldDecorator('invoice')(
              <span>
                <SelectType onOk={this.handleInvoice} />
                <Button onClick={this.handleImportInvoice} style={{ marginLeft: 20 }}>
                  从票夹导入
                </Button>
              </span>
            )}
          </FormItem>
          {invoiceDetail.length > 0 ? (
            <InvoiceDetail deleteLink={this.handleDeleteLink} invoiceDetail={invoiceDetail} />
          ) : null}
          <FormItem {...formItemLayout} label={this.$t('common.amount')}>
            {getFieldDecorator('amount', {
              initialValue: record.amount,
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
            })(
              <CustomAmount
                style={{ width: '100%' }}
                disabled={expenseType.entryMode}
                //onChange={this.handleChangeAmount}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>
          {expenseType.entryMode && (
            <div>
              <FormItem {...formItemLayout} label="单价">
                {getFieldDecorator('price', {
                  initialValue: record.price,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  <CustomAmount
                    style={{ width: '100%' }}
                    disabled={expenseType.entryMode}
                    //onChange={this.handleChangeAmount}
                    placeholder={this.$t('common.please.enter')}
                  />
                )}
              </FormItem>

              <FormItem {...formItemLayout} label={this.$t('common.number')}>
                {getFieldDecorator('quantity', {
                  initialValue: record.quantity,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  <CustomAmount
                    style={{ width: '100%' }}
                    disabled={expenseType.entryMode}
                    //onChange={this.handleChangeAmount}
                    placeholder={this.$t('common.please.enter')}
                  />
                )}
              </FormItem>
            </div>
          )}

          <FormItem {...formItemLayout} label={this.$t('common.comment')}>
            {getFieldDecorator('remarks', {
              initialValue: record.remarks,
            })(
              <Input.TextArea
                style={{ width: '100%' }}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={this.$t('common.attachments') /*附件*/}
            style={{ marginBottom: 12 }}
          >
            <Upload
              attachmentType="INVOICE_IMAGES"
              uploadUrl={`${config.baseUrl}api/upload/static/attachment`}
              fileNum={9}
              wrappedComponentRef={upload => (this.upload = upload)}
              uploadHandle={this.handleUpload}
              defaultFileList={fileList}
              defaultOids={attachmentOid}
            />
          </FormItem>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={this.state.loading}>
              {this.$t({ id: 'common.save' })}
            </Button>
            <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' })}</Button>
          </div>
        </Form>
        <AddInvoice
          visible={showAddInvoice}
          params={{
            record: {},
            currency: this.props.form.getFieldValue('currencyCode'),
            invoiceTypeId: invoiceType.id,
            saveUrl: 'api/invoice/head/check/invoice',
          }}
          saveSuccess={this.handleSuccess}
          onCancel={this.handleCloseInvoice}
        />
        <SelectInvoice
          visible={importInvoice}
          onOk={this.handleImportOk}
          onCancel={() => {
            this.setState({ importInvoice: false });
          }}
          selectedData={selectedInvoice}
          params={{ createdBy: this.props.user.id, roleType: 'TENANT' }}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    profile: state.user.proFile,
    user: state.user.currentUser,
    language: state.languages,
  };
}
const WrappedNewUpdateAccount = Form.create()(NewUpdateAccount);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewUpdateAccount);
