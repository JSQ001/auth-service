/**
 * Created by 5716 on 2019/3/7.
 */
import React from 'react';
import { connect } from 'dva';

import { Button, Form, Switch, Input, message, Icon, Select } from 'antd';
import commodityDefineService from './commodity-define.service';

const FormItem = Form.Item;
const { Option } = Select;

class NewCommodityDefine extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      // paymentMethodCategoryOptions: [],
      searchFrom: [
        { id: 'enabled' },
        { id: 'commodityCode' },
        { id: 'commodityName' },
        { id: 'commodityAbb' },
      ],
    };
  }

  /* componentWillMount() {
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
  } */

  handleCreate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true,
        });
        commodityDefineService
          .addCommodity(values)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            this.props.form.resetFields();
            this.props.onClose(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(`${this.$t({ id: 'common.save.filed' })},${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  handleUpdate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({
          loading: true,
        });
        values.id = this.props.params.updateParams.id;
        commodityDefineService
          .upDateCommodity(values)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            this.props.form.resetFields();
            this.props.onClose(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(`${this.$t({ id: 'common.save.filed' })},${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  // 新建或者修改
  handleSave = e => {
    e.preventDefault();
    typeof this.props.params.updateParams.id === 'undefined'
      ? this.handleCreate()
      : this.handleUpdate();
  };

  onCancel = () => {
    this.props.onClose(false);
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
    return (
      <div className="new-commodity-define">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label="是否启用">
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue:
                JSON.stringify(this.props.params.updateParams) === '{}'
                  ? true
                  : this.props.params.updateParams.enabled,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
                disabled={false}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label="商品编码">
            {getFieldDecorator('commodityCode', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.commodityCode || '',
            })(<Input placeholder={this.$t('common.please.enter')} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="货物或劳务名称">
            {getFieldDecorator('commodityName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.commodityName || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="商品和服务分类简称">
            {getFieldDecorator('commodityAbb', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.commodityAbb || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="增值税特殊管理">
            {getFieldDecorator('vatManagement', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.vatManagement || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="优惠政策标识">
            {getFieldDecorator('preferentialPolicy', {
              valuePropName: 'checked',
              initialValue:
                JSON.stringify(this.props.params.updateParams) === '{}'
                  ? false
                  : this.props.params.updateParams.preferentialPolicy,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
                disabled={false}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="备注">
            {getFieldDecorator('remarks', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.remarks || '',
            })(<Input.TextArea autosize={{ minRows: 3 }} />)}
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

const WrappedCommodityDefine = Form.create()(NewCommodityDefine);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedCommodityDefine);
