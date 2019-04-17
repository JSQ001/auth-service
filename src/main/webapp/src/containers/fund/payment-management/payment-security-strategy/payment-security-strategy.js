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

      console.log('data');
      console.log(data);
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
    // this.getPaymentSecurity()
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
              message.success('保存成功');
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
                  <FormItem label="消息" {...formItemLayout}>
                    {getFieldDecorator('news', {
                      rules: [{ required: true }],
                      initialValue: responseData.news ? responseData.news : '',
                    })(<Input placeholder="请维护" disabled={isReadOnly} />)}
                  </FormItem>
                </Col>
                <Col span={6} offset={2}>
                  <FormItem label="期限" {...formItemLayout}>
                    {getFieldDecorator('term', {
                      rules: [{ required: true, message: '请输入数字' }],
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
                    <span style={{ paddingLeft: '20px' }}>天</span>
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
                  <FormItem label="来源单据编号" {...formItemLayout}>
                    {getFieldDecorator('tradeCodeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.tradeCodeSign ? responseData.tradeCodeSign : true,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" disabled />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label="收款账号" {...formItemLayout}>
                    {getFieldDecorator('gatherAccountSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherAccountSign
                        ? responseData.tradeCodeSign
                        : true,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" disabled />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label="金额" {...formItemLayout}>
                    {getFieldDecorator('amountSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.amountSign ? responseData.amountSign : true,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" disabled />)}
                  </FormItem>
                </Col>
              </Row>
              <Row gutter={100}>
                <Col span={8}>
                  <FormItem label="银行大类名称" {...formItemLayout}>
                    {getFieldDecorator('gatherBankSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherBankSign
                        ? responseData.gatherBankSign
                        : false,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label="开户省" {...formItemLayout}>
                    {getFieldDecorator('gatherProvinceCodeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherProvinceCodeSign
                        ? responseData.gatherProvinceCodeSign
                        : false,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label="开户市" {...formItemLayout}>
                    {getFieldDecorator('gatherCityCodeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.gatherCityCodeSign
                        ? responseData.gatherCityCodeSign
                        : false,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" />)}
                  </FormItem>
                </Col>
              </Row>
              <Row gutter={100}>
                <Col span={8}>
                  <FormItem label="业务类型" {...formItemLayout}>
                    {getFieldDecorator('businessTypeSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.businessTypeSign
                        ? responseData.businessTypeSign
                        : false,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label="公私标志" {...formItemLayout}>
                    {getFieldDecorator('propFlagSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.propFlagSign ? responseData.propFlagSign : false,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem label="付款方式" {...formItemLayout}>
                    {getFieldDecorator('paymentMethodSign', {
                      valuePropName: 'checked',
                      initialValue: responseData.paymentMethodSign
                        ? responseData.paymentMethodSign
                        : false,
                    })(<Switch checkedChildren="启用" unCheckedChildren="未启用" />)}
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
                  保存
                </Button>
                <Button style={{ margin: '0 20px' }} onClick={this.buttonClear}>
                  取消
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
