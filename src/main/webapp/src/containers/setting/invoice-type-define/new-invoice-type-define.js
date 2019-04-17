import React, { Component } from 'react';
import { Form, Input, Button, message, Switch, Select, Icon, Tooltip } from 'antd';
import LanguageInput from 'components/Widget/Template/language-input/language-input';
import service from './invoice-type-define-service.js';
import baseService from 'share/base.service';

const FormItem = Form.Item;
const Option = Select.Option;

class NewInvoiceForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      saveLoading: false,
      newParams: { ...this.props.params },
      setOfBooks: [],
      valueCode: {},
      taxRateArr: [],
    };
  }
  componentDidMount = () => {
    this.getUseSetOfBooksValue();
    this.getRate();
  };
  //获取账套信息-启用
  getUseSetOfBooksValue = () => {
    baseService
      .getSetOfBooksByTenant()
      .then(res => {
        let list = [];
        res.data.map(item => {
          list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
        });
        this.setState({ setOfBooks: list });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };
  //获取税率值列表
  getRate = () => {
    const { valueCode } = this.state;
    valueCode.type = '1009';
    baseService
      .getSystemValueList(valueCode)
      .then(res => {
        let list = [];
        res.data.map(item => {
          list.push({ value: item.id, label: item.name });
        });
        this.setState({ taxRateArr: list });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //保存
  handleSubmit = e => {
    e.preventDefault();
    const { newParams, saveLoading } = this.state;
    this.props.form.validateFields((err, value) => {
      if (err) return;

      this.setState({ saveLoading: true });
      let handleMethod = null;
      let messageValue = '';
      let params = { ...value };
      //如果输入了中文 " ，",则替换成英文 ","
      params['invoiceCodeLength'] =
        params['invoiceCodeLength'] &&
        params['invoiceCodeLength'].replace(/({this.$t("expense.")})/gi, ','); /*，*/
      params['invoiceNumberLength'] =
        params['invoiceNumberLength'] &&
        params['invoiceNumberLength'].replace(/({this.$t("expense.")})/gi, ','); /*，*/
      if (!newParams.id) {
        //新增
        messageValue = this.$t('expense.new.success'); /*新增成功*/
        handleMethod = service.addInvoiceValues;
        params['i18n'] = newParams.i18n;
        params['creationMethod'] = 'CUSTOM';
      } else {
        //编辑
        messageValue = this.$t('expense.edit.success'); /*编辑成功*/
        params['id'] = newParams.id;
        handleMethod = service.editInvoiceValues;
      }

      handleMethod &&
        handleMethod(params)
          .then(res => {
            message.success(messageValue);
            this.setState({ saveLoading: false }, () => {
              this.props.close(true);
            });
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ saveLoading: false });
          });
    });
  };

  //取消
  handleCancel = () => {
    this.props.close(false);
  };

  //多语言
  i18nNameChange = (name, i18nName) => {
    const params = this.state.newParams;
    params.name = name;
    if (!params.i18n) {
      params.i18n = {};
    }
    params.i18n.name = i18nName;
  };

  render() {
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    const { params } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { saveLoading, newParams, setOfBooks, taxRateArr } = this.state;

    return (
      <div>
        <Form>
          <FormItem {...formItemLayout} label={this.$t('expense.invoice.type.code')} hasFeedback>
            {/*发票类型代码*/}
            {getFieldDecorator('invoiceTypeCode', {
              rules: [
                {
                  required: true,
                  message: this.$t('expense.please.enter.the') /*请输入*/,
                },
              ],
              initialValue: newParams.invoiceTypeCode || '',
            })(
              <Input
                placeholder={this.$t('expense.please.enter.the')}
                disabled={newParams.id ? true : false}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('expense.invoice.type.name')}>
            {/*发票类型名称*/}
            {getFieldDecorator('invoiceTypeName', {
              rules: [
                {
                  required: true,
                  message: this.$t('expense.please.enter.the') /*请输入*/,
                },
              ],
              initialValue: newParams.invoiceTypeName || '',
            })(
              <LanguageInput
                key={1}
                name={newParams.invoiceTypeName}
                i18nName={
                  newParams.i18n && newParams.i18n.invoiceTypeName
                    ? newParams.i18n.invoiceTypeName
                    : null
                }
                placeholder={this.$t('common.please.enter') /* 请输入 */}
                isEdit={newParams.id}
                nameChange={this.i18nNameChange}
                disabled={params.id && params.creationMethod.toUpperCase() == 'SYS' ? true : false}
              />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={this.$t('expense.whether.the.deduction')}
            hasFeedback
          >
            {/*是否抵扣*/}
            {getFieldDecorator('deductionFlag', {
              rules: [
                {
                  required: true,
                  message: this.$t('expense.please.select.a') /*请选择*/,
                },
              ],
              initialValue:
                (newParams.deductionFlag && newParams.deductionFlag.toUpperCase()) || 'Y',
            })(
              <Select disabled={newParams.id ? true : false}>
                <Option key={'Y'}>{this.$t('expense.yes')}</Option>
                {/*是*/}
                <Option key={'N'}>{this.$t('expense.no')}</Option>
                {/*否*/}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('expense.policy.setofbooksname')}>
            {/*账套*/}
            <div id="setOfBooksPos">
              {getFieldDecorator('setOfBooksId', {
                initialValue: newParams.setOfBooksId || '',
              })(
                <Select
                  allowClear
                  disabled={
                    newParams.id && newParams.creationMethod.toUpperCase() == 'SYS' ? true : false
                  }
                  placeholder={this.$t('expense.please.select.a')} /*请选择*/
                  getPopupContainer={() => document.getElementById('setOfBooksPos')}
                >
                  {setOfBooks.map(option => {
                    return (
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    );
                  })}
                </Select>
              )}
            </div>
          </FormItem>
          <FormItem
            {...formItemLayout}
            hasFeedback
            label={
              <span>
                {this.$t('expense.invoice.code.digit')}
                {/*发票代码位数*/}
                <Tooltip
                  placement="topRight"
                  title={this.$t('expense.desc.code4')} // 位数用来填写发票时校验发票有效位数，允许多种位数请用逗号隔开填写，如“12,10”
                >
                  <Icon type="info-circle" />
                </Tooltip>
              </span>
            }
          >
            {getFieldDecorator('invoiceCodeLength', {
              initialValue: newParams.invoiceCodeLength || '',
              rules: [
                {
                  // pattern: /^\d+([,，]\d+)*$/ig,
                  pattern: /^([1-9]+0*)([,，]([1-9]+0*))*$/gi, //形如 '1,1,3，10'
                  // pattern: /^([1-9]+0*|0+[1-9]+0*)([,，]([1-9]+0*|0+[1-9]+0*))*$/ig, //形如'01，10'
                  message: this.$t('expense.desc.code5'), // 请使用逗号分隔，且位数为正整数
                },
              ],
              validateTrigger: 'onBlur',
            })(
              <Input
                placeholder={this.$t('expense.please.enter.the')}
                disabled={
                  newParams.id && newParams.creationMethod.toUpperCase() == 'SYS' ? true : false
                }
              />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={this.$t('expense.invoice.number.digits')}
            hasFeedback
          >
            {/*发票号码位数*/}
            {getFieldDecorator('invoiceNumberLength', {
              initialValue: newParams.invoiceNumberLength || '',
              rules: [
                {
                  // pattern: /^\d+([,，]\d+)*$/ig,
                  pattern: /^([1-9]+0*)([,，]([1-9]+0*))*$/gi,
                  // pattern: /^([1-9]+0*|0+[1-9]+0*)([,，]([1-9]+0*|0+[1-9]+0*))*$/ig,
                  message: this.$t('expense.desc.code5'), // 请使用逗号分隔，且位数为正整数
                },
              ],
              validateTrigger: 'onBlur',
            })(
              <Input
                placeholder={this.$t('expense.please.enter.the')}
                disabled={
                  newParams.id && newParams.creationMethod.toUpperCase() == 'SYS' ? true : false
                }
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('expense.the.default.rate')}>
            {/*默认税率*/}
            <div id="taxRatePos">
              {getFieldDecorator('defaultTaxRate', {
                initialValue: newParams.defaultTaxRate || '',
              })(
                <Select
                  allowClear
                  placeholder={this.$t('expense.please.select.a')} /*请选择*/
                  getPopupContainer={() => document.getElementById('taxRatePos')}
                >
                  {taxRateArr.map(option => {
                    return (
                      <Option key={option.value} value={option.label}>
                        {option.label}
                      </Option>
                    );
                  })}
                </Select>
              )}
            </div>
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('expense.the.interface.map.values')}>
            {/*接口映射值*/}
            {getFieldDecorator('interfaceMapping', {
              initialValue: newParams.interfaceMapping || '',
            })(<Input placeholder={this.$t('expense.please.enter.the')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('expense.policy.enabled')}>
            {/*状态*/}
            {getFieldDecorator('enabled', {
              initialValue: newParams.id ? newParams.enabled : true,
              valuePropName: 'checked',
            })(<Switch />)}
            <span style={{ paddingLeft: '20px' }}>
              {this.props.form.getFieldValue('enabled')
                ? this.$t('expense.enable')
                : this.$t('expense.disable')}
            </span>
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={saveLoading}
              onClick={this.handleSubmit}
            >
              {this.$t('common.save')}
            </Button>
            <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

export default Form.create()(NewInvoiceForm);
