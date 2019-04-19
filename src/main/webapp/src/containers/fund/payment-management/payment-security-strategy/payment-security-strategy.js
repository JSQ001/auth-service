import React from 'react';
import { connect } from 'dva';
import { Form, Row, Col, Input, Switch, Card, Button, InputNumber, message } from 'antd';
import paySecurityService from './payment-security-strategy.service';

const FormItem = Form.Item;

class PaymentSecurityStrategy extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // loading状态
      responseData: [], // 查询到的所有信息
    };
  }

  componentWillMount() {
    this.getPaymentSecurity();
    // this.getlistvalues();
  }

  /**
   * 获取列表数据
   */
  getPaymentSecurity = () => {
    this.setState({ loading: true });
    paySecurityService.getPaymentSecurityQuery().then(response => {
      const { data } = response;
      this.setState({
        responseData: data,
        loading: false,
      });
    });
  };

  /**
   * 搜索条件重置
   */
  buttonClear = e => {
    const { form } = this.props;
    e.preventDefault();
    form.resetFields();
  };

  /**
   * 保存
   */
  saveInformation = () => {
    const {
      form: { validateFields },
    } = this.props;
    validateFields((err, values) => {
      if (!err) {
        paySecurityService
          .savePaymentSecurity(values)
          .then(response => {
            if (response.data) {
              message.success(this.$t('fund.save.successful')); /* 保存成功 */
              this.getPaymentSecurity();
            }
          })
          .catch(error => {
            message.error(error.response.data.message);
          });
      }
    });
  };

  render() {
    const {
      form: { getFieldDecorator },
      isReadOnly,
    } = this.props;
    const formItemLayout = {
      labelCol: {
        span: 30,
      },
      wrapperCol: {
        span: 30,
      },
    };
    const { loading, responseData } = this.state;
    return (
      <div>
        <div className="table-header">
          <Card
            style={{
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
              marginRight: 15,
              marginLeft: 15,
              marginTop: 15,
            }}
            loading={loading}
          >
            <section>
              <Row>
                <Col span={6} offset={2}>
                  <FormItem label={this.$t('fund.the.message')} {...formItemLayout}>
                    {/* 消息 */}
                    {getFieldDecorator('news', {
                      rules: [{ required: true }],
                      initialValue: responseData.news ? responseData.news : '',
                    })(
                      <Input placeholder={this.$t('fund.please.maintain')} disabled={isReadOnly} />
                    )}
                    {/* 请维护 */}
                  </FormItem>
                </Col>
                <Col span={6} offset={2}>
                  <FormItem label="期限(天)" {...formItemLayout}>
                    {getFieldDecorator('term', {
                      rules: [
                        { required: true, message: this.$t('fund.please.enter.the.numbers') },
                      ] /* 请输入数字 */,
                      initialValue: responseData.term ? Number(responseData.term) : '',
                    })(
                      <InputNumber
                        min={1}
                        max={30}
                        formatter={value => `${value}`}
                        parser={value => value.replace('^[0-9]*[1-9][0-9]*$')}
                        precision="0"
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
            </section>
          </Card>
          <Card
            style={{
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
              marginRight: 15,
              marginLeft: 15,
              marginTop: 15,
            }}
          >
            <section style={{ padding: '50px 150px' }}>
              <Row gutter={100}>
                <Col span={8}>
                  <FormItem label={this.$t('fund.source.document.number')} {...formItemLayout}>
                    {/* 来源单据编号 */}
                    {getFieldDecorator('tradeCodeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.tradeCodeSign ? responseData.tradeCodeSign : true,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                        disabled
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label={this.$t('fund.receiving.account')} {...formItemLayout}>
                    {/* 收款账号 */}
                    {getFieldDecorator('gatherAccountSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherAccountSign
                        ? responseData.tradeCodeSign
                        : true,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                        disabled
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label={this.$t('fund.amount')} {...formItemLayout}>
                    {/* 金额 */}
                    {getFieldDecorator('amountSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.amountSign ? responseData.amountSign : true,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                        disabled
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
              </Row>
              <Row gutter={100}>
                <Col span={8}>
                  <FormItem label={this.$t('fund.name.of.bank.types')} {...formItemLayout}>
                    {/* 银行大类名称 */}
                    {getFieldDecorator('gatherBankSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherBankSign
                        ? responseData.gatherBankSign
                        : false,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                      />
                    )}
                    {/* 启用 未启用  */}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label={this.$t('fund.to.open.an.account.province')} {...formItemLayout}>
                    {/* 开户省 */}
                    {getFieldDecorator('gatherProvinceCodeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherProvinceCodeSign
                        ? responseData.gatherProvinceCodeSign
                        : false,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label={this.$t('fund.to.open.an.account')} {...formItemLayout}>
                    {/* 开户市 */}
                    {getFieldDecorator('gatherCityCodeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherCityCodeSign
                        ? responseData.gatherCityCodeSign
                        : false,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
              </Row>
              <Row gutter={100}>
                <Col span={8}>
                  <FormItem label={this.$t('fund.business.types')} {...formItemLayout}>
                    {/* 业务类型 */}
                    {getFieldDecorator('businessTypeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.businessTypeSign
                        ? responseData.businessTypeSign
                        : false,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label={this.$t('fund.public.private.signs')} {...formItemLayout}>
                    {/* 公私标志 */}
                    {getFieldDecorator('propFlagSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.propFlagSign ? responseData.propFlagSign : false,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label={this.$t('fund.payment.method')} {...formItemLayout}>
                    {/* 付款方式 */}
                    {getFieldDecorator('paymentMethodSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.paymentMethodSign
                        ? responseData.paymentMethodSign
                        : false,
                    })(
                      <Switch
                        checkedChildren={this.$t('fund.enable')}
                        unCheckedChildren={this.$t('fund.enable.is.not.enabled')}
                      />
                    )}
                    {/* 启用 未启用 */}
                  </FormItem>
                </Col>
              </Row>
            </section>
            <div style={{ textAlign: 'center' }}>
              <div style={{ float: 'right' }}>
                <Button
                  type="primary"
                  htmlType="submit"
                  style={{ margin: '0 20px' }}
                  onClick={this.saveInformation}
                >
                  {this.$t('fund.save')}
                  {/* 保存 */}
                </Button>
                <Button style={{ margin: '0 20px' }} onClick={this.buttonClear}>
                  {this.$t('fund.cancel')}
                  {/* 取消 */}
                </Button>
              </div>
            </div>
          </Card>
        </div>
      </div>
    );
  }
}

function show(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(show)(Form.create()(PaymentSecurityStrategy));
