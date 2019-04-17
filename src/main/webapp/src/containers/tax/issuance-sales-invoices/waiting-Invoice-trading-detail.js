/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Col, Row, Button } from 'antd';

const FormItem = Form.Item;

class WaitingInvoiceTradingDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      record: {},
      data: {},
    };
  }
  componentWillMount() {
    this.setState({
      data: this.props.params,
    });
    console.log(this.props.params);
  }
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
                  initialValue: data.clientNumber || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="客户名称">
                {getFieldDecorator('clientName', {
                  initialValue: params.clientName || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人">
                {getFieldDecorator('taxpayer', {
                  initialValue: params.taxpayer || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
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
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="发票类型">
                {getFieldDecorator('invoiceType', {
                  initialValue: params.invoiceType || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="商品编码">
                {getFieldDecorator('goodsId', {
                  initialValue: params.goodsId || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="商品名称">
                {getFieldDecorator('goodsName', {
                  initialValue: params.goodsName || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="交易流水号">
                {getFieldDecorator('tarnNUm', {
                  initialValue: params.tarnNUm || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="交易名称">
                {getFieldDecorator('tarnName', {
                  initialValue: params.tarnName || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="机构">
                {getFieldDecorator('org', {
                  initialValue: params.org || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
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
                {getFieldDecorator('addrPhone', {
                  initialValue: params.addrPhone || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="开户行及账号">
                {getFieldDecorator('clientBankAcc', {
                  initialValue: params.clientBankAcc || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="原币交易金额">
                {getFieldDecorator('tarnAmount', {
                  initialValue: params.tarnAmount || '',
                  rules: [{ required: true }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="本币交易金额">
                {getFieldDecorator('funTarnAmount', {
                  initialValue: params.funTarnAmount || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="税目">
                {/* {getFieldDecorator('tarnAmount', {
                  initialValue: params.tarnAmount || '',
                  rules: [{ required: true }],
                })(<Input disabled />)} */}
                {<Input />}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="税率%">
                {getFieldDecorator('vatRate', {
                  initialValue: params.vatRate || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="优惠税率%">
                {/* {getFieldDecorator('tarnAmount', {
                  initialValue: params.tarnAmount || '',
                  rules: [{ required: true }],
                })(<Input disabled />)} */}
                {<Input disabled />}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="可开票金额">
                {getFieldDecorator('mayInvoiceAmount', {
                  initialValue: params.mayInvoiceAmount || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="扣除税">
                {/* {getFieldDecorator('tarnAmount', {
                  initialValue: params.tarnAmount || '',
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
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="税额">
                {getFieldDecorator('taxes', {
                  initialValue: params.taxes || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
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
