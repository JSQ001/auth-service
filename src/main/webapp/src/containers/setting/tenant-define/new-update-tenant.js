/**
 * Created by 14306 on 2019/01/29.
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Button, Alert, message } from 'antd';
import tenantService from 'containers/setting/tenant-define/tenant-define.service.js';
const FormItem = Form.Item;

class newUpdateTenant extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      versionCodeError: false,
      statusError: false,
      validateStatus: '',
      help: '',
      loading: false,
    };
  }

  componentWillMount() {
    if (this.props.params.record.id) {
      this.setState({
        paramCode: {
          parameterValueType: this.props.params.record.parameterValueType,
        },
      });
      this.handleParamValue(true, this.props.params.record.parameterCode);
    }
  }

  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ loading: true });
        tenantService
          .addTenant(values)
          .then(res => {
            this.props.onClose(true);
            message.success(this.$t('common.save.success', { name: '' }));
          })
          .catch(e => {
            this.setState({ loading: false });
            message.error(`${this.$t('common.save.filed')},${e.response.data.message}`);
          });
      }
    });
  };

  //先输入手机号，设置登录账号值
  handleMobile = e => {
    let phone = e.target.value;
    let login = this.props.form.getFieldValue('login');
    if (!login && phone) {
      this.props.form.setFieldsValue({ login: phone });
    }
  };

  //修改密码，重置确认密码
  handlePwd = () => {
    this.props.form.resetFields(['passwordConfirm']);
  };

  //确认密码
  handleConfirmPwd = e => {
    let confirmPwd = e.target.value;
    let pwd = this.props.form.getFieldValue('password');
    let flag = pwd === confirmPwd;
    this.setState({
      validateStatus: flag ? '' : 'error',
      help: flag ? '' : this.$t('reset-password.password.not.equal'),
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { validateStatus, help } = this.state;

    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10, offset: 0 },
    };

    return (
      <div className="new-tenant-definition">
        <Form onSubmit={this.handleSave}>
          <div
            style={{
              marginBottom: 20,
              fontSize: 14,
              color: 'rgba(0, 0, 0, 0.65)',
              fontWeight: 'bold',
            }}
          >
            {this.$t('tenant.info')}
          </div>
          <FormItem {...formItemLayout} label={this.$t('setting.tenant.code')}>
            {getFieldDecorator('tenantCode', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
                {
                  validator: (item, value, callback) => {
                    if (value) {
                      let str = /^[0-9a-zA-z-_]*$/;
                      if (!str.test(value) || value.length > 35) {
                        callback(this.$t({ id: 'setting.companyGroupCode.tips' }));
                      }
                    }
                    callback();
                  },
                },
              ],
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('setting.tenant.name')}>
            {getFieldDecorator('tenantName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <div style={{ borderBottom: '1px solid #e9e9e9' }} />
          <div
            style={{
              marginTop: 15,
              marginBottom: 10,
              fontSize: 14,
              color: 'rgba(0, 0, 0, 0.65)',
              fontWeight: 'bold',
            }}
          >
            {this.$t('tenant.info.admin')}
          </div>
          <Alert
            message={this.$t('common.help')}
            description={this.$t('tenant.help.info')}
            type="info"
            showIcon
            style={{ marginBottom: 15 }}
          />
          <FormItem {...formItemLayout} label={this.$t({ id: 'common.user.id' })}>
            {getFieldDecorator('employeeId', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
                {
                  validator: (item, value, callback) => {
                    if (value) {
                      let str = /^[0-9a-zA-z-_]*$/;
                      if (!str.test(value) || value.length > 35) {
                        callback(this.$t({ id: 'setting.companyGroupCode.tips' }));
                      }
                    }
                    callback();
                  },
                },
              ],
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('chooser.data.fullName')}>
            {getFieldDecorator('fullName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('login.little.email')}>
            {getFieldDecorator('email', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
                {
                  type: 'email',
                  message: this.$t('pdc.basic.info.email.error'),
                },
              ],
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('chooser.data.mobile')}>
            {getFieldDecorator('mobile', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
                {
                  validator: (item, value, callback) => {
                    if (value) {
                      let str = /^1[3|4|5|7|8]\d{9}$/;
                      if (!str.test(value) || value.length > 35) {
                        callback(this.$t('login.phone.err'));
                      }
                    }
                    callback();
                  },
                },
              ],
            })(<Input onBlur={this.handleMobile} placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('login.little.account')}>
            {getFieldDecorator('login', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
            })(<Input placeholder={this.$t('common.please.enter')} />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('login.password')}>
            {getFieldDecorator('password', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
            })(
              <Input.Password
                onChange={this.handlePwd}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            validateStatus={validateStatus}
            help={help}
            label={this.$t('login.password.confirm')}
          >
            {getFieldDecorator('passwordConfirm', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
            })(
              <Input.Password
                onBlur={this.handleConfirmPwd}
                placeholder={this.$t('common.please.enter')}
              />
            )}
          </FormItem>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={this.state.loading}>
              {this.$t({ id: 'common.save' })}
            </Button>
            <Button onClick={this.props.onClose}>{this.$t({ id: 'common.cancel' })}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

const WrappedNewUpdateTenant = Form.create({ name: 'newUpdateTenant' })(newUpdateTenant);

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewUpdateTenant);
