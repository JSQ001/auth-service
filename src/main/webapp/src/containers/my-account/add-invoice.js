import React from 'react';
import { connect } from 'dva';
import PropTypes from 'prop-types';
import {
  Spin,
  Input,
  DatePicker,
  Row,
  Col,
  Icon,
  Popover,
  Modal,
  Form,
  Select,
  InputNumber,
  Button,
  message,
} from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import moment from 'moment';
import AddLine from 'containers/my-account/add-line-table';
import 'styles/my-account/create-invoice.scss';
import expenseService from 'containers/my-account/expense.service';
import invoiceImg from 'images/expense/invoice-info.png';
import invoiceImgEn from 'images/expense/invoice-info-en.png';
import { rejectPiwik } from 'share/piwik';
import httpFetch from 'share/httpFetch';
import config from 'config';
class AddInvoice extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      invoiceTypes: {
        fetched: false,
        data: [],
      },
    };
  }

  handleFocusInvoiceType = () => {
    let { invoiceTypes } = this.state;
    let params = {
      tenantId: this.props.company.tenantId,
      setOfBooksId: this.props.company.setOfBooksId,
    };
    if (!invoiceTypes.fetched) {
      httpFetch.get(`${config.expenseUrl}/api/invoice/type/query/for/invoice`, params).then(res => {
        this.setState({
          invoiceTypes: {
            fetched: true,
            data: res.data,
          },
        });
      });
    }
  };

  handleSelectInvoiceType = value => {
    console.log(value);
  };

  componentDidMount() {
    // let code = "01,04,3100171320,11111111,,20180531,111111";
    // this.testInvoice(code);
  }

  render() {
    const { invoiceTypes, checking, invoice, canSubmit } = this.state;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { onCreate, fromExpense, onBack, visible, onCancel } = this.props;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 17, offset: 0 },
    };
    const layout = {
      labelCol: { span: 4 },
      wrapperCol: { span: 20, offset: 0 },
    };

    const style = {
      marginLeft: -38,
    };

    const inputStyle = {
      width: '97%',
    };

    let invoiceTypeNo = getFieldValue('invoiceTypeNo');
    return (
      <div className="create-invoice">
        <Modal visible={visible} width={800} onCancel={onCancel} title="手工录入发票" footer={null}>
          <Form className="create-invoice" onSubmit={this.handleSubmit}>
            <Row gutter={24}>
              <Col span={18}>
                <FormItem
                  labelCol={{ span: 4 }}
                  wrapperCol={{ span: 11 }}
                  label={this.$t('expense.invoice.type') /*发票类型*/}
                  onSubmit={this.handleSubmit}
                >
                  {getFieldDecorator('invoiceTypeNo', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.select'),
                      },
                    ],
                  })(
                    <Select
                      dropdownMatchSelectWidth={false}
                      onFocus={this.handleFocusInvoiceType}
                      onChange={this.handleSelectInvoiceType}
                      placeholder={this.$t('common.please.select') /* 请选择 */}
                      notFoundContent={invoiceTypes.fetched ? null : <Spin />}
                    >
                      {invoiceTypes.data.map(item => {
                        return <Option key={item.invoiceTypeCode}>{item.invoiceTypeName}</Option>;
                      })}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={4}>
                <Popover
                  trigger="click"
                  placement="bottomRight"
                  content={
                    <img
                      style={{ width: '600px' }}
                      src={this.props.language.local === 'zh_cn' ? invoiceImg : invoiceImgEn}
                    />
                  }
                >
                  <span style={{ color: '#5A9FE6' }}>发票填写说明</span>
                </Popover>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={12}>
                <FormItem {...formItemLayout} label={this.$t('expense.invoice.date') /*开票日期*/}>
                  {getFieldDecorator('invoiceDate', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.select'),
                      },
                    ],
                  })(
                    <DatePicker
                      style={{ width: '100%' }}
                      disabledDate={current =>
                        current && current.isAfter(moment().subtract(0, 'days'))
                      }
                      placeholder={this.$t('common.please.select')}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout} label={this.$t('expense.invoice.code') /*发票代码*/}>
                  {getFieldDecorator('invoiceCode', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.enter'),
                      },
                      {
                        message: this.$t('common.must.characters.length', { length: '10/12' }),
                        validator: (rule, value, callback) => {
                          if (!value || (value.length !== 10 && value.length !== 12))
                            callback(true);
                          else callback();
                        },
                      },
                    ],
                  })(
                    <Input
                      maxLength={12}
                      // style={{width: '75%'}}
                      placeholder={this.$t('expense.invoice.code.help') /* 请输入10或12位数字*/}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={12}>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.invoice.number') /*发票号码*/}
                >
                  {getFieldDecorator('invoiceNumber', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.enter'),
                      },
                      {
                        len: 8,
                        message: this.$t('common.must.characters.length', { length: 8 }),
                      },
                    ],
                  })(
                    <Input
                      maxLength={8}
                      placeholder={this.$t('expense.invoice.number.help') /* 请输入8位数字*/}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout} label={this.$t('expense.invoice.device.number')}>
                  {getFieldDecorator('invoiceCode', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.enter'),
                      },
                      {
                        message: this.$t('common.must.characters.length', { length: '10/12' }),
                        validator: (rule, value, callback) => {
                          if (!value || (value.length !== 10 && value.length !== 12))
                            callback(true);
                          else callback();
                        },
                      },
                    ],
                  })(<Input maxLength={12} placeholder={this.$t('common.please.enter')} />)}
                </FormItem>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={12}>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.invoice.check.code') /*校验码*/}
                >
                  {getFieldDecorator('invoiceNumber', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.enter'),
                      },
                      {
                        len: 8,
                        message: this.$t('common.must.characters.length', { length: 8 }),
                      },
                    ],
                  })(
                    <Input
                      maxLength={8}
                      placeholder={this.$t('expense.invoice.number.help') /* 请输入8位数字*/}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout} label={this.$t('common.currency')}>
                  {getFieldDecorator('invoiceCode', {
                    initialValue: this.props.company.baseCurrency,
                  })(<Select disabled placeholder={this.$t('common.please.select')} />)}
                </FormItem>
              </Col>
            </Row>
            <Row gutter={24}>
              <Col span={12}>
                <FormItem
                  {...formItemLayout}
                  label={this.$t('expense.invoice.tax.price') /*价税合计*/}
                >
                  {getFieldDecorator('invoiceNumber', {
                    rules: [
                      {
                        required: true,
                        message: this.$t('common.please.enter'),
                      },
                      {
                        len: 8,
                        message: this.$t('common.must.characters.length', { length: 8 }),
                      },
                    ],
                  })(
                    <Input
                      maxLength={8}
                      placeholder={this.$t('expense.invoice.number.help') /* 请输入8位数字*/}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={12}>
                <FormItem {...formItemLayout} label={this.$t('common.tax')}>
                  {getFieldDecorator('invoiceCode', {})(
                    <Input disabled maxLength={12} placeholder={this.$t('expense.invoice.tax')} />
                  )}
                </FormItem>
              </Col>
            </Row>
            <FormItem
              style={{ marginLeft: -37 }}
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 8 }}
              label={this.$t('expense.invoice.amount.without.tax')}
            >
              {getFieldDecorator('taxTotal', {})(
                <Input
                  maxLength={12}
                  style={{ width: '97%' }}
                  disabled
                  placeholder={this.$t('common.please.enter')}
                />
              )}
            </FormItem>
            <FormItem style={style} {...layout} label={this.$t('common.comment')}>
              {getFieldDecorator('remark', {})(
                <Input
                  maxLength={12}
                  style={inputStyle}
                  placeholder={this.$t('common.please.enter')}
                />
              )}
            </FormItem>
            <FormItem style={style} {...layout} label={this.$t('expense.invoice.buyer')}>
              {getFieldDecorator('invoiceBuyer', {})(
                <Input
                  style={inputStyle}
                  maxLength={12}
                  placeholder={this.$t('common.please.enter')}
                />
              )}
            </FormItem>
            <FormItem
              style={style}
              {...layout}
              label={this.$t('expense.invoice.tax.payer.identity.number')}
            >
              {getFieldDecorator('identityNumber', {})(
                <Input
                  style={inputStyle}
                  maxLength={12}
                  placeholder={this.$t('common.please.enter')}
                />
              )}
            </FormItem>
            <FormItem style={style} {...layout} label={this.$t('expense.invoice.address.phone')}>
              {getFieldDecorator('addressPhone', {})(
                <Input
                  style={inputStyle}
                  maxLength={12}
                  placeholder={this.$t('common.please.enter')}
                />
              )}
            </FormItem>
            <FormItem
              style={style}
              {...layout}
              label={this.$t('expense.invoice.opening.bank.account')}
            >
              {getFieldDecorator('bankAccount', {})(
                <Input
                  style={inputStyle}
                  maxLength={12}
                  placeholder={this.$t('common.please.enter')}
                />
              )}
            </FormItem>
            <FormItem style={style} {...layout} label={this.$t('expense.invoice.seller')}>
              {getFieldDecorator('seller', {})(
                <Input
                  style={inputStyle}
                  maxLength={12}
                  placeholder={this.$t('common.please.enter')}
                />
              )}
            </FormItem>
          </Form>
          <div style={{ marginBottom: '10%' }}>
            <AddLine />
          </div>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={this.state.loading}>
              {this.$t({ id: 'common.save' })}
            </Button>
            <Button onClick={this.onCancel}>{this.$t({ id: 'common.cancel' })}</Button>
          </div>
        </Modal>
      </div>
    );
  }
}

AddInvoice.propTypes = {
  onCreate: PropTypes.func,
  fromExpense: PropTypes.bool,
  onBack: PropTypes.func,
  createType: PropTypes.number,
  digitalInvoice: PropTypes.any,
};

function mapStateToProps(state) {
  return {
    company: state.user.company,
    language: state.languages,
  };
}

const WrappedAddInvoice = Form.create()(AddInvoice);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedAddInvoice);
