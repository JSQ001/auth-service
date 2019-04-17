/* eslint-disable react/destructuring-assignment */
/* eslint-disable react/sort-comp */
import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, Alert, Col, Row, Button } from 'antd';

const FormItem = Form.Item;

class NewRegisterApply extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
    };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({ visible: nextProps.visible });
  }

  componentWillMount() {}

  handleCancel = () => {
    // eslint-disable-next-line no-unused-expressions
    this.props.onClose && this.props.onClose(true);
  };

  fileMethod = () => {
    // eslint-disable-next-line no-unused-expressions
    this.props.onClose && this.props.onClose(true);
  };

  render() {
    const { params } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { visible } = this.state;
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
        title="税务登记"
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
          <Alert style={{ marginBottom: 10, marginTop: 10 }} message="单据信息" type="info" />
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="事务类型">
                {getFieldDecorator('transactionTypeName', {
                  initialValue: params.transactionTypeName || '',
                  rules: [{ required: false, message: '不可编辑' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="申请人">
                {getFieldDecorator('applicantName', {
                  initialValue: params.applicantName || '',
                  rules: [{ required: false, message: '不可编辑' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Alert style={{ marginBottom: 10, marginTop: 10 }} message="基本信息" type="info" />
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人名称">
                {getFieldDecorator('taxpayerName', {
                  initialValue: params.taxpayerName || '',
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
              <FormItem {...formItemLayout} label="税号类型">
                {getFieldDecorator('taxpayerNumberType', {
                  initialValue: params.taxpayerNumberTypeName || '',
                  rules: [{ required: true, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="登记注册类型">
                {getFieldDecorator('typeOfBusiness', {
                  initialValue: params.typeOfBusinessName || '',
                  rules: [{ required: true, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税资质">
                {getFieldDecorator('taxQualification', {
                  initialValue: params.taxQualificationName || '',
                  rules: [{ required: true, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="国际行业（主行业）">
                {getFieldDecorator('mainIndustry', {
                  initialValue: params.mainIndustry || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="是否为上市公司">
                {getFieldDecorator('listedCompany', {
                  initialValue: params.listedCompanyName || '',
                  rules: [{ required: false, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="是否为境外注册居民企业">
                {getFieldDecorator('overseasRegResEnt', {
                  initialValue: params.overseasRegResEntName || '',
                  rules: [{ required: false, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人地址">
                {getFieldDecorator('taxpayerAddress', {
                  initialValue: params.taxpayerAddress || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="银行开户行">
                {getFieldDecorator('clientTaxBank', {
                  initialValue: params.clientTaxBank || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="银行账号">
                {getFieldDecorator('bankAccountNumber', {
                  initialValue: params.bankAccountNumber || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人电话">
                {getFieldDecorator('taxpayerTel', {
                  initialValue: params.taxpayerTel || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="主管税务机关">
                {getFieldDecorator('natTaxAuth', {
                  initialValue: params.natTaxAuthName || '',
                  rules: [],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="上级纳税主体">
                {getFieldDecorator('parentTaxpayerId', {
                  initialValue: params.parentTaxpayerName,
                  rules: [],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="单位性质">
                {getFieldDecorator('companyType', {
                  initialValue: params.companyTypeName || '',
                  rules: [{ required: false, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="适用会计制度">
                {getFieldDecorator('accountingSystem', {
                  initialValue: params.accountingSystemName || '',
                  rules: [{ required: false, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="核算方式">
                {getFieldDecorator('accountingMethod', {
                  initialValue: params.accountingMethodName || '',
                  rules: [{ required: true, message: '请选择' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="所属机构">
                {getFieldDecorator('affiliation', {
                  initialValue: params.affiliationName,
                  rules: [{ required: true }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Alert style={{ marginBottom: 10, marginTop: 10 }} message="工商信息" type="info" />
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="经营范围">
                {getFieldDecorator('businessScope', {
                  initialValue: params.businessScope || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="注册资金">
                {getFieldDecorator('registeredCapital', {
                  initialValue: params.registeredCapital || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="生产经营地址">
                {getFieldDecorator('operationAddress', {
                  initialValue: params.operationAddress || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="邮政编码">
                {getFieldDecorator('postalCode', {
                  initialValue: params.postalCode || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="营业期限">
                {getFieldDecorator('operatingPeriod', {
                  initialValue: params.operatingPeriod || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="法人代表">
                {getFieldDecorator('legalRepresentative', {
                  initialValue: params.legalRepresentative || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="注册地址">
                {getFieldDecorator('registeredAddress', {
                  initialValue: params.registeredAddress || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24} style={{ borderTop: '1px solid #e8e8e8' }}>
            <Col span={24} style={{ marginTop: '24px' }} align="middle">
              <Button style={{ marginLeft: 10 }} onClick={this.handleCancel}>
                取消
              </Button>
              <Button style={{ marginLeft: 10 }} onClick={this.fileMethod}>
                附件
              </Button>{' '}
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

export default connect(mapStateToProps)(Form.create()(NewRegisterApply));
