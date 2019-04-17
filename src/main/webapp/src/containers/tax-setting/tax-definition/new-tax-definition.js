/**
 * Created by 22161 on 2019/3/7.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';

import { Button, Form, Switch, Input, message, Icon, Select } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import taxDefinitionService from './tax-definition.service';

class NewTaxDefinition extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      //paymentMethodCategoryOptions: [],
      searchFrom: [{ id: 'enabled' }, { id: 'taxCategoryCode' }, { id: 'taxCategoryName' }],
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

  handleCreate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        taxDefinitionService
          .insertTaxCategory(values)
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
        taxDefinitionService
          .updateTaxCategory(values)
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
    const { params, enabled, isPut } = this.state;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    console.log(this.props.params);
    return (
      <div className="new-tax-definition">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label={'状态'}>
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue:
                JSON.stringify(this.props.params) === '{}' ? true : this.props.params.enabled,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
                disabled={false}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'税种代码'}>
            {getFieldDecorator('taxCategoryCode', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.taxCategoryCode || '',
            })(<Input placeholder={this.$t('common.please.enter')} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'税种名称'}>
            {getFieldDecorator('taxCategoryName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.taxCategoryName || '',
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

const WrappedTaxDefinition = Form.create()(NewTaxDefinition);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedTaxDefinition);
