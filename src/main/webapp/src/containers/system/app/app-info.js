/**
 * Created by fudebao on 2017/12/05.
 */
import React, { Component } from 'react';

import { Button, Form, Badge, Input, message, Select } from 'antd';
import service from './app.service';

const FormItem = Form.Item;

class NewApp extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      services: [],
    };
  }

  componentDidMount() {
    service
      .getServiceList()
      .then(res => {
        this.setState({ services: res });
      })
      .catch(err => {
        message.error(err.message);
      });
  }

  // 新建或编辑
  handleSave = e => {
    const { params, onClose, form } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (err) return;

      this.setState({ loading: true });
      const method = !params.id ? service.new : service.update;

      method({ ...params, ...values })
        .then(() => {
          message.success(this.$t('common.operate.success'));
          this.setState({ loading: false });
          onClose(true);
        })
        .catch(error => {
          message.error(error.message);
          this.setState({ loading: false });
        });
    });
  };

  onCancel = () => {
    const { onClose, form } = this.props;
    form.resetFields();
    onClose();
  };

  // 监听表单值
  handleFormChange = () => {
    const { loading } = this.state;
    if (loading) {
      this.setState({
        loading: false,
      });
    }
  };

  render() {
    const { loading, services } = this.state;
    const { form, params } = this.props;
    const { getFieldDecorator } = form;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };

    return (
      <div className="new-payment-method">
        <Form onSubmit={this.handleSave} onChange={this.handleFormChange}>
          <FormItem {...formItemLayout} label={this.$t('app.info.code')}>
            {getFieldDecorator('appCode', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.appCode || 'base',
            })(
              <Select>
                {services.map(item => <Select.Option key={item}>{item}</Select.Option>)}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('app.info.name')}>
            {getFieldDecorator('appName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.appName || '',
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('common.column.status')}>
            {getFieldDecorator('status', {
              valuePropName: 'success',
              initialValue: params.id ? params.status === 'UP' : true,
            })(
              <Badge
                status={params.status === 'UP' ? 'success' : 'error'}
                text={
                  params.status === 'UP' ? this.$t('app.status.up') : this.$t('app.status.down')
                }
              />
            )}
          </FormItem>

          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={loading}>
              {this.$t('common.save')}
            </Button>
            <Button onClick={this.onCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

export default Form.create()(NewApp);
