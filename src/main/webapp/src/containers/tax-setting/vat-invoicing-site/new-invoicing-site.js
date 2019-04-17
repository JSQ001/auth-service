/**
 * Created by 22161 on 2019/3/8.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';

import { Button, Form, Switch, Input, message, Icon, Select } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import invoicingSiteService from './invoicing-site.service';
import Chooser from 'widget/chooser';

class NewInvoicingSite extends React.Component {
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
        { id: 'invoicingSiteCode' },
        { id: 'invoicingSiteName' },
        { id: 'taxpayerName' },
        { id: 'invoicingSiteAdd' },
        { id: 'passwod' },

        { id: 'invoicingTerminal' },
        { id: 'securityCode' },
        { id: 'printTop' },
        { id: 'printLeft' },
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
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        // values.invoicingSiteId = values.invoicingSite.key;
        // delete values.invoicingSite;
        const params = { ...values };
        params.taxpayerId = values.taxpayerId[0].id;
        params.taxpayerName = values.taxpayerId[0].taxpayerName;

        invoicingSiteService
          .insertInvoicingSite(params)
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
        const params = { ...values };
        params.taxpayerId = values.taxpayerId[0].id;
        params.taxpayerName = values.taxpayerId[0].taxpayerName;

        invoicingSiteService
          .updateInvoicingSite(params)
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
    const { enabled, isPut } = this.state;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { params } = this.props;
    return (
      <div className="new-invoicing-site">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label={'状态'}>
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue:
                // JSON.stringify(this.props.params) === '{}' ? true : this.props.params.enabled,
                params && params.id ? params.enabled : true,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
                disabled={false}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={'开票点编码'}>
            {getFieldDecorator('invoicingSiteCode', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.id ? params.invoicingSiteCode : '',
            })(<Input placeholder={this.$t('common.please.enter')} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'开票点名称'}>
            {getFieldDecorator('invoicingSiteName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.invoicingSiteName || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>

          <FormItem {...formItemLayout} label={'所属纳税主体'}>
            {getFieldDecorator('taxpayerId', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.taxpayerId && [
                { id: params.taxpayerId, taxpayerName: params.taxpayerName },
              ],
            })(<Chooser labelKey="taxpayerName" valueKey="id" type="taxpayer_name" single />)}
          </FormItem>

          <FormItem {...formItemLayout} label={'开票点地址'}>
            {getFieldDecorator('invoicingSiteAdd', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.invoicingSiteAdd || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>

          <FormItem {...formItemLayout} label={'税控钥匙密码'}>
            {getFieldDecorator('passwod', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.remarks || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>

          <FormItem {...formItemLayout} label={'开票终端标识'}>
            {getFieldDecorator('invoicingTerminal', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.invoicingTerminal || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'安全码'}>
            {getFieldDecorator('securityCode', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.remarks || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'上边距'}>
            {getFieldDecorator('printTop', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.remarks || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'左边距'}>
            {getFieldDecorator('printLeft', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.remarks || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
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

const WrappedInvoicingSite = Form.create()(NewInvoicingSite);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedInvoicingSite);
