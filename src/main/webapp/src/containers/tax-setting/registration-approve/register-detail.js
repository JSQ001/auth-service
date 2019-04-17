/* eslint-disable array-callback-return */
/* eslint-disable react/destructuring-assignment */
/* eslint-disable react/no-unused-state */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Alert, Col, Row } from 'antd';
import ApproveHistory from 'widget/Template/approve-history-work-flow';

class RegisterApply extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      taxTransactionTypeOptions: [],
    };
  }

  componentDidMount() {}

  componentWillReceiveProps(nextProps) {
    this.setState({ visible: nextProps.visible });
  }

  /**
   * 事务类型下拉列表
   */
  getTaxTransactionType() {
    // eslint-disable-next-line prefer-const
    let taxTransactionTypeOptions = [];
    this.getSystemValueList('TAX_REGISTER_TRANSACTION_TYPE').then(res => {
      res.data.values.map(data => {
        taxTransactionTypeOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxTransactionTypeOptions,
      });
    });
  }

  render() {
    const { headerData } = this.props;
    const { getFieldDecorator } = this.props.form;

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
      <Form>
        <Alert style={{ marginBottom: 10, marginTop: 10 }} message="单据信息" type="info" />
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="申请编号">
              {getFieldDecorator('roleCode', {
                initialValue: headerData.applyCode || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="事务类型">
              {getFieldDecorator('transactionTypeName', {
                initialValue: headerData.transactionTypeName || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="事务状态">
              {getFieldDecorator('transactionStatusName', {
                initialValue: headerData.transactionStatusName || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="申请人">
              {getFieldDecorator('applicantName', {
                initialValue: this.props.user.userName,
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Alert style={{ marginBottom: 10, marginTop: 10 }} message="基本信息" type="info" />
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="纳税人名称">
              {getFieldDecorator('taxpayerName', {
                initialValue: headerData.taxpayerName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="纳税人识别号">
              {getFieldDecorator('taxpayerNumber', {
                initialValue: headerData.taxpayerNumber || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="税号类型">
              {getFieldDecorator('taxpayerNumberTypeName', {
                initialValue: headerData.taxpayerNumberTypeName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="登记注册类型">
              {getFieldDecorator('typeOfBusinessName', {
                initialValue: headerData.typeOfBusinessName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="纳税资质">
              {getFieldDecorator('taxQualificationName', {
                initialValue: headerData.taxQualificationName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="国际行业（主行业）">
              {getFieldDecorator('mainIndustry', {
                initialValue: headerData.mainIndustry || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="是否为上市公司">
              {getFieldDecorator('listedCompanyName', {
                initialValue: headerData.listedCompanyName || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="是否为境外注册居民企业">
              {getFieldDecorator('overseasRegResEntName', {
                initialValue: headerData.overseasRegResEntName || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="纳税人地址">
              {getFieldDecorator('taxpayerAddress', {
                initialValue: headerData.taxpayerAddress || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="银行开户行">
              {getFieldDecorator('clientTaxBank', {
                initialValue: headerData.clientTaxBank || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="银行账号">
              {getFieldDecorator('bankAccountNumber', {
                initialValue: headerData.bankAccountNumber || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="纳税人电话">
              {getFieldDecorator('taxpayerTel', {
                initialValue: headerData.taxpayerTel || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="主管税务机关">
              {getFieldDecorator('natTaxAuthName', {
                initialValue: headerData.natTaxAuthName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="上级纳税主体">
              {getFieldDecorator('parentTaxpayerName', {
                initialValue: headerData.parentTaxpayerName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="单位性质">
              {getFieldDecorator('companyTypeName', {
                initialValue: headerData.companyTypeName || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="适用会计制度">
              {getFieldDecorator('accountingSystemName', {
                initialValue: headerData.accountingSystemName || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="核算方式">
              {getFieldDecorator('accountingMethodName', {
                initialValue: headerData.accountingMethodName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="所属机构">
              {getFieldDecorator('affiliationName', {
                initialValue: headerData.affiliationName || '',
                rules: [{ required: true, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Alert style={{ marginBottom: 10, marginTop: 10 }} message="工商信息" type="info" />
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="经营范围">
              {getFieldDecorator('businessScope', {
                initialValue: headerData.businessScope || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="注册资金">
              {getFieldDecorator('registeredCapital', {
                initialValue: headerData.registeredCapital || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="生产经营地址">
              {getFieldDecorator('operationAddress', {
                initialValue: headerData.operationAddress || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="邮政编码">
              {getFieldDecorator('postalCode', {
                initialValue: headerData.postalCode || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="营业期限">
              {getFieldDecorator('operatingPeriod', {
                initialValue: headerData.operatingPeriod || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="法人代表">
              {getFieldDecorator('legalRepresentative', {
                initialValue: headerData.legalRepresentative || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item {...formItemLayout} label="注册地址">
              {getFieldDecorator('registeredAddress', {
                initialValue: headerData.registeredAddress || '',
                rules: [{ required: false, message: '不可编辑' }],
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <div
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
        >
          <ApproveHistory type="902001" oid={headerData.documentOid} />
        </div>
      </Form>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(mapStateToProps)(Form.create()(RegisterApply));
