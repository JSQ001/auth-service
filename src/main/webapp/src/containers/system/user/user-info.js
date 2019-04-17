/**
 * Created by fudebao on 2017/12/05.
 */
import React from 'react';
import { connect } from 'react-redux';

import { Button, Form, Switch, Input, message, Icon } from 'antd';
import userService from './user.service';

const FormItem = Form.Item;

class NewUser extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  // 新建或编辑
  handleSave = e => {
    const { params, onClose, form } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        if (!params.id) {
          const toValue = {
            ...params,
            ...values,
          };
          userService
            .newUser(toValue)
            .then(() => {
              this.setState({ loading: false });
              form.resetFields();
              onClose(true);
              message.success(this.$t('common.operate.success'));
            })
            .catch(() => {
              this.setState({ loading: false });
              message.error(`${this.$t('common.save.filed')}${e.response.data.message}`);
            });
        } else {
          const toValue = {
            ...params,
            ...values,
          };
          userService
            .updateUser(toValue)
            .then(() => {
              this.setState({ loading: false });
              form.resetFields();
              onClose(true);
              message.success(this.$t('common.operate.success'));
            })
            .catch(() => {
              this.setState({ loading: false });
              message.error(`${this.$t('common.save.filed')}${e.response.data.message}`);
            });
        }
      }
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
    const { loading } = this.state;
    const { form, params } = this.props;
    const { getFieldDecorator } = form;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };

    return (
      <div className="new-payment-method">
        <Form onSubmit={this.handleSave} onChange={this.handleFormChange}>
          <FormItem {...formItemLayout} label={this.$t('user.info.code')}>
            {getFieldDecorator('login', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.login || '',
            })(
              <Input
                disabled={typeof params.id !== 'undefined'}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('user.info.name')}>
            {getFieldDecorator('userName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.userName || '',
            })(
              <Input
                disabled={typeof params.id !== 'undefined'}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={this.$t('user.info.remark')}>
            {getFieldDecorator('remark', {
              initialValue: params.remark || '',
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('user.info.email')}>
            {getFieldDecorator('email', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.email || '',
            })(
              <Input
                disabled={typeof params.id !== 'undefined'}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('user.info.mobile')}>
            {getFieldDecorator('mobile', {
              rules: [
                {
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: params.mobile || '',
            })(
              <Input
                disabled={typeof params.id !== 'undefined'}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label={this.$t('common.column.status')}>
            {getFieldDecorator('activated', {
              valuePropName: 'checked',
              initialValue: params.id ? params.activated : true,
            })(
              <Switch
                checked={params.id ? form.getFieldValue('activated') : true}
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
              />
            )}&nbsp;&nbsp;&nbsp;&nbsp;{form.getFieldValue('activated')
              ? this.$t('common.status.enable')
              : this.$t('common.status.disable')}
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

const WrappedNewUser = Form.create()(NewUser);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewUser);
