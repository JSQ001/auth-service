/**
 * Created by 22161 on 2019/3/8.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import { Button, Form, InputNumber, Input, message, Select } from 'antd';
const FormItem = Form.Item;
import config from 'config';
const Option = Select.Option;
import Service from './service';

class NewExtendDimConfiguration extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      data: {},
      id: '',
      taxClientTypeOptions: [],
      defaultItemValueName: [],
      sourceSystemMethodOptions: [],
    };
  }
  componentDidMount() {
    this.setState({
      data: this.props.params,
    });
    console.log(this.props.params);
    this.getDimensionName();
    this.getTaxAccountingMethod();
    this.getDefaultItemName();
  }
  // 获取维度下拉列表
  getDimensionName() {
    // eslint-disable-next-line prefer-const
    let taxQuaifationOptions = [];
    Service.getSystemValueList().then(res => {
      let taxClientTypeOptions = [];
      res.data.map(data => {
        taxClientTypeOptions.push({
          label: data.dimensionName,
          value: data.dimensionName,
          key: data.dimensionName,
          id: data.id,
        });
      });
      this.setState({
        taxClientTypeOptions,
      });
    });
  }

  // 获取维值下拉列表
  getTaxQuaifation() {
    // eslint-disable-next-line prefer-const
    let taxQuaifationOptions = [];
    Service.getSystemValueList().then(res => {
      let taxClientTypeOptions = [];
      res.data.map(data => {
        taxClientTypeOptions.push({
          label: data.dimensionName,
          value: data.dimensionName,
          key: data.dimensionName,
          id: data.dimensionId,
        });
      });
      this.setState({
        taxClientTypeOptions,
      });
    });
  }

  /**
   * 获取匹配字段下拉列表
   */
  getTaxAccountingMethod = () => {
    // eslint-disable-next-line prefer-const
    let sourceSystemMethodOptions = [];
    this.getSystemValueList('TAX_MATCH_FIELD').then(res => {
      console.log(res.data);
      res.data.values.map(data => {
        sourceSystemMethodOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        sourceSystemMethodOptions,
      });
    });
  };

  getDefaultItemName = () => {
    const id = this.props.params.dimensionId;
    console.log(id);
    if (id != undefined) {
      Service.getSystemValueList1(id).then(res => {
        let defaultItemValueName = [];
        res.data.map(data => {
          defaultItemValueName.push({
            label: data.dimensionItemName,
            value: data.dimensionItemName,
            key: data.dimensionItemName,
            id: data.id,
          });
        });
        this.setState({
          defaultItemValueName,
        });
      });
    }
  };
  handleCreate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        const params = { ...values };
        console.log(params);
        Service.insertInvoicingSite(params)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            this.props.form.resetFields();
            this.props.close(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  handleUpdate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      console.log(values);
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        values.id = this.props.params.id;
        const params = {
          ...values,
        };
        Service.updateInvoicingSite(params)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            this.props.form.resetFields();
            this.props.close(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  //新建或者修改
  handleSave = e => {
    e.preventDefault();
    this.setState({
      loading: true,
    });
    typeof this.props.params.id === 'undefined' ? this.handleCreate() : this.handleUpdate();
  };

  onCancel = () => {
    this.props.close(false);
  };

  switchChange = value => {
    this.setState({ enabled: value });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      data,
      taxClientTypeOptions,
      defaultItemValueName,
      sourceSystemMethodOptions,
    } = this.state;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { params } = this.props;
    return (
      <div className="new-invoicing-site">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label={'规则代码'}>
            {getFieldDecorator('ruleCode', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.ruleCode,
            })(
              <Select
                placeholder="请选择"
                style={{ width: '100%' }}
                onChange={value => {
                  if (value === 'VAT_INVOICE_RULE') {
                    value = '开票规则';
                  }
                  if (value === 'VAT_SEPARATE_RULE') {
                    value = '价税分离规则';
                  }
                  this.props.form.setFieldsValue({ ruleName: value });
                }}
              >
                <Option value="VAT_INVOICE_RULE">VAT_INVOICE_RULE</Option>
                <Option value="VAT_SEPARATE_RULE">VAT_SEPARATE_RULE</Option>
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={'规则名称'}>
            {getFieldDecorator('ruleName', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.ruleName,
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={true} />)}
          </FormItem>

          <FormItem {...formItemLayout} label={'维度'}>
            {getFieldDecorator('dimensionId', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.dimensionId,
            })(
              <Select
                placeholder="请选择"
                style={{ width: '100%' }}
                onChange={id => {
                  console.log(id);
                  Service.getSystemValueList1(id).then(res => {
                    let defaultItemValueName = [];
                    res.data.map(data => {
                      defaultItemValueName.push({
                        label: data.dimensionItemName,
                        value: data.dimensionItemName,
                        key: data.dimensionItemName,
                        id: data.id,
                      });
                    });
                    this.setState({
                      defaultItemValueName,
                    });
                  });
                }}
              >
                {taxClientTypeOptions.map(option => {
                  return (
                    <Option key={option.id} value={option.id}>
                      {option.label}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'默认值'}>
            {getFieldDecorator('defaultItemId', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.defaultItemId,
            })(
              <Select placeholder="请选择">
                {defaultItemValueName.map(option => {
                  return (
                    <Option key={option.id} value={option.id}>
                      {option.label}
                    </Option>
                  );
                })}
              </Select>
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'是否必输'}>
            {getFieldDecorator('essentialFlag', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.essentialFlag,
            })(
              <Select placeholder="请选择" style={{ width: '100%' }}>
                <Option value="Y">是</Option>
                <Option value="N">否</Option>
              </Select>
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'优先级'}>
            {getFieldDecorator('priority', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.priority,
            })(
              <InputNumber
                placeholder={this.$t({ id: 'common.please.enter' })}
                disabled={false}
                style={{ width: '100%' }}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'匹配表'}>
            {getFieldDecorator('tabName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.tabName,
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'匹配字段'}>
            {getFieldDecorator('colName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: data.colName,
            })(
              <Select placeholder="请选择" style={{ width: '100%' }}>
                {sourceSystemMethodOptions.map(option => {
                  return <Option key={option.value}>{option.label}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={this.state.loading}>
              {this.$t('common.save')}
            </Button>
            <Button onClick={this.onCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}
const WrappedSeparationRules = Form.create()(NewExtendDimConfiguration);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedSeparationRules);
