/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Select, Col, Row, Button, message } from 'antd';
import Service from './waiting-Invoice-trading-flow.service';
import Lov from 'widget/Template/lov';
const Option = Select.Option;

const FormItem = Form.Item;

class WaitingInvoiceTradingDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      record: {},
      taxInvoiceType: [],
      data: {},
    };
  }
  componentWillMount() {
    this.getInvoiceType();
    this.setState({
      data: this.props.params,
    });
  }
  //获取发票类型
  getInvoiceType() {
    // eslint-disable-next-line prefer-const
    let taxInvoiceType = [];
    this.getSystemValueList('TAX_VAT_INVOICE_TYPE').then(res => {
      res.data.values.map(data => {
        taxInvoiceType.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxInvoiceType,
      });
    });
  }
  handleCancel = () => {
    this.props.onClose && this.props.onClose(true);
    this.setState({
      data: {},
    });
  };
  //保存
  handleSave = (e, values) => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        values.id = this.props.params.id;
        let params = { ...values };
        if (params.clientNumber != '') {
          params.clientNumber = params.clientNumber.id;
          // params.clientNumber = params.clientNumber.taxpayerNumber;
          // params.clientName = params.clientName.clientName;
        }
        if (params.goodsId != '') {
          params.goodsId = params.goodsId.id;
        }
        if (params.taxRateId != '') {
          params.taxRateId = params.taxRateId.id;
        }
        Service.saveWaitInvoice(params).then(res => {
          message.success('保存成功！');
          this.setState({
            loading: false,
          });
          this.props.form.resetFields();
          this.handleCancel();
        });
      }
    });
  };

  render() {
    const { params, visible } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { data } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 17 },
      },
    };
    return (
      <Modal
        title="待开票信息调整"
        visible={visible}
        onOk={this.handleSave}
        okText="保存"
        onCancel={this.handleCancel}
        footer={null}
        width="90%"
        zIndex="70%"
        destroyOnClose
      >
        <Form>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="客户编号">
                {getFieldDecorator('clientNumber', {
                  message: this.$t('common.please.enter'),
                  rules: [{ required: true }],
                  initialValue: data.id && {
                    id: data.clientNumber,
                    clientNumber: data.clientNumber,
                  },
                })(
                  <Lov
                    labelKey="clientNumber"
                    valueKey="id"
                    code="customer_information_query"
                    single
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="客户名称">
                {getFieldDecorator('clientName', {
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                  initialValue: data.id && { id: data.clientName, clientName: data.clientName },
                })(
                  <Lov
                    disabled
                    labelKey="clientName"
                    valueKey="id"
                    code="customer_information_query"
                    single
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人资质">
                {getFieldDecorator('taxQualification', {
                  initialValue: params.taxQualification || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
                {/* {(<Input disabled />)} */}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人识别号">
                {getFieldDecorator('taxpayerNumber', {
                  initialValue: params.taxpayerNumber || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="开票名称">
                {getFieldDecorator('invoiceTitle', {
                  initialValue: params.invoiceTitle || '',
                  rules: [{ required: true }],
                })(<Input />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="发票类型">
                {getFieldDecorator('invoiceType', {
                  initialValue: params.invoiceType || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(
                  <Select placeholder={this.$t('common.please.select')}>
                    {this.state.taxInvoiceType.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="商品编码">
                {getFieldDecorator('goodsId', {
                  rules: [{ required: true }],
                  initialValue: data.id && { id: data.goodsId, commodityCode: data.goodsId },
                })(<Lov labelKey="commodityCode" valueKey="id" code="commodity" single />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="商品名称">
                {/* {getFieldDecorator('goodsName', {
                  initialValue: params.goodsName || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)} */}
                {<Input disabled />}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="交易流水号">
                {getFieldDecorator('tranNum', {
                  initialValue: params.tranNum || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="交易名称">
                {/* {getFieldDecorator('tarnName', {
                  initialValue: params.tarnName || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)} */}
                {<Input disabled />}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="机构">
                {getFieldDecorator('orgId', {
                  initialValue: params.orgId || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
                {/* {(<Input disabled />)} */}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="来源系统">
                {getFieldDecorator('sourceSystem', {
                  initialValue: params.sourceSystem || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="地址、电话">
                {/* {getFieldDecorator('addrPhone', {
                  initialValue: params.addrPhone || '',
                  rules: [{ required: true }],
                })(<Input disabled />)} */}
                {<Input disabled />}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="开户行及账号">
                {/* {getFieldDecorator('clientBankAcc', {
                  initialValue: params.clientBankAcc || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)} */}
                {<Input disabled />}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="原币交易金额">
                {getFieldDecorator('tranAmount', {
                  initialValue: params.tranAmount || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
                {/* {<Input disabled/>} */}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="本币交易金额">
                {getFieldDecorator('funTranAmount', {
                  initialValue: params.funTranAmount || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
                {/* {<Input disabled/>} */}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="税目">
                {getFieldDecorator('taxRateId', {
                  rules: [{ required: true }],
                  initialValue: data.taxRateId && {
                    id: data.taxRateId,
                    taxRateId: data.taxRateId,
                  },
                })(<Lov labelKey="taxRateId" valueKey="id" code="tax-rate-definition" single />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="税率%">
                {getFieldDecorator('vatRate', {
                  initialValue: params.vatRate || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
                {/* {<Input disabled/>} */}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="优惠税率%">
                {getFieldDecorator('preferentialTaxRate', {
                  initialValue: params.preferentialTaxRate || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
                {/* {<Input disabled />} */}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="可开票金额">
                {getFieldDecorator('mayInvoiceAmount', {
                  initialValue: params.mayInvoiceAmount || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
                {/* {<Input disabled/>} */}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="扣除额">
                {/* {getFieldDecorator('deductAmount', {
                  initialValue: params.deductAmount || '',
                  rules: [{ required: true }],
                })(<Input disabled />)} */}
                {<Input disabled />}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="本次开票金额">
                {getFieldDecorator('invoiceAmount', {
                  initialValue: params.invoiceAmount || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="不含税金额">
                {getFieldDecorator('sales', {
                  initialValue: params.sales || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
                {/* {<Input disabled/>} */}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="税额">
                {getFieldDecorator('outTaxes', {
                  initialValue: params.outTaxes || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
                {/* {<Input disabled/>} */}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={24} style={{ marginTop: '24px' }} align="middle">
              <Button type="primary" onClick={this.handleSave}>
                保存
              </Button>
              <Button style={{ marginLeft: 10 }} onClick={this.handleCancel}>
                返回
              </Button>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(mapStateToProps)(Form.create()(WaitingInvoiceTradingDetail));
