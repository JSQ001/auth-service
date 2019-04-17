/**
 * Created by 22161 on 2019/3/8.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';

import { Button, Form, Switch, Input, message, Icon, Select, Tooltip, InputNumber } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import taxRateService from './tax-rate.service';

class NewTaxRate extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      //paymentMethodCategoryOptions: [],
      searchFrom: [
        { id: 'enabled' },
        { id: 'taxRate' },
        { id: 'taxRatio' },
        { id: 'preferentialTaxRate' },
        { id: 'taxCategoryName' },
        { id: 'remarks' },
      ],
    };
  }

  /*componentWillMount() {
    this.getPaymentMethodCategory();
  }

  getPaymentMethodCategory() {
    let paymentMethodCategoryOptions = [];
    this.getSystemValueList(2105).then(res => {
      res.data.values.map(data => {
        paymentMethodCategoryOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        paymentMethodCategoryOptions,
      });
    });
  }*/
  // componentDidMount() {
  //   this.getPaymentMethodCategory();
  //   taxRateService
  //   .getTaxCategory(taxCategoryId).then(res=>{
  //     this.setState({
  //       taxRate
  //     })
  //   })
  // }

  handleCreate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      console.log(err);
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        values.taxCategoryId = values.taxCategory.key;
        delete values.taxCategory;
        taxRateService
          .insertTaxRate(values)
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
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        values.id = this.props.params.id;
        taxRateService
          .updateTaxRate(values)
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

  //校验金额
  checkPrice = (rule, value, callback) => {
    if (value >= 0) {
      callback();
      return;
    }
    callback('税率不能小于等于0！');
  };

  render() {
    console.log(this.props.params);
    const { getFieldDecorator } = this.props.form;
    const { enabled, isPut } = this.state;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { params } = this.props;
    return (
      <div className="new-tax-rate">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label={'状态'}>
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue:
                //JSON.stringify(this.props.params) === '{}' ? true : this.props.params.enabled,
                this.props.params.enabled == undefined ? true : this.props.params.enabled,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
                disabled={false}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'税率描述'}>
            {getFieldDecorator('taxRate', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.taxRate || '',
            })(<Input placeholder={this.$t('common.please.enter')} disabled={false} />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            style={{ width: '100%' }}
            label={
              <span>
                计税税率&nbsp;
                {/* <Tooltip title="例:0.06">
                  <Icon type="question-circle-o" />
                </Tooltip> */}
              </span>
            }
          >
            {getFieldDecorator('taxRatio', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                  validator: this.checkPrice,
                },
              ],
              initialValue: this.props.params.taxRatio || '',
            })(
              <InputNumber
                style={{ width: '100%' }}
                placeholder={this.$t({ id: 'common.please.enter' })}
                formatter={value => `${value}%`}
                parser={value => value.replace('%', '')}
                disabled={false}
              />
            )}
          </FormItem>

          <FormItem
            {...formItemLayout}
            label={
              <span>
                优惠税率&nbsp;
                {/* <Tooltip title="例:0.06">
                  <Icon type="question-circle-o" />
                </Tooltip> */}
              </span>
            }
          >
            {getFieldDecorator('preferentialTaxRate', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                  validator: this.checkPrice,
                },
              ],
              initialValue: this.props.params.preferentialTaxRate || '',
            })(
              <InputNumber
                style={{ width: '100%' }}
                placeholder={this.$t({ id: 'common.please.enter' })}
                formatter={value => `${value}%`}
                parser={value => value.replace('%', '')}
                disabled={false}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'税种名称'}>
            {getFieldDecorator('taxCategory', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: { key: params.taxCategoryId, label: params.taxCategoryName },
            })(
              <Select labelInValue disabled placeholder={this.$t({ id: 'common.please.enter' })} />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'备注'}>
            {getFieldDecorator('remarks', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.remarks || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
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

const WrappedTaxRate = Form.create()(NewTaxRate);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedTaxRate);
