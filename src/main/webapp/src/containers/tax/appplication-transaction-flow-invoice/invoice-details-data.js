/* eslint-disable import/order */
/* eslint-disable no-unused-vars */
/* eslint-disable no-empty */
/* eslint-disable no-param-reassign */
/* eslint-disable no-unneeded-ternary */
/* eslint-disable react/sort-comp */
/* eslint-disable react/jsx-first-prop-new-line */
/* eslint-disable react/jsx-boolean-value */
/* eslint-disable array-callback-return */
/* eslint-disable react/no-unused-state */
/* eslint-disable react/jsx-closing-tag-location */
/* eslint-disable react/destructuring-assignment */
import React from 'react';
import { connect } from 'dva';
import { Modal, Form, Input, message, Alert, Select, Col, Row, Button } from 'antd';
import Chooser from 'components/Widget/chooser';
// import service from './tax-register-apply.service';
// import ApproveHistory from 'widget/Template/approve-history-work-flow';
// import SeeCangeRecord from './tax-change-record';
// import SlideFrame from 'widget/slide-frame';
const FormItem = Form.Item;

// eslint-disable-next-line prefer-destructuring
const Option = Select.Option;

class NewRegisterApply extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentWillMount() {
    this.getTaxAccountingMethod();
    this.setState({
      data: this.props.params,
    });
  }

  render() {
    const { params, visible } = this.props;
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { cangeRecordShow, record, data } = this.state;
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
      <Modal title="税务登记" visible={visible} footer={null} width="90%" zIndex="70%">
        <Form>
          <Alert style={{ marginBottom: 10, marginTop: 10 }} message="单据信息" type="info" />
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="申请编号">
                {getFieldDecorator('roleCode', {
                  initialValue: params.applyCode || '',
                  rules: [{ required: false, message: '不可编辑' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="事务类型">
                {getFieldDecorator('transactionType', {
                  initialValue: params.transactionType || 'TAX_REGISTER_NEW',
                  rules: [{ required: false, message: '不可编辑' }],
                })(
                  <Select disabled placeholder={this.$t('common.please.select')}>
                    {this.state.taxTransactionTypeOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="事务状态">
                {getFieldDecorator('transactionStatus', {
                  initialValue: params.transactionStatus || 'GENERATE',
                  rules: [{ required: false, message: '不可编辑' }],
                })(
                  <Select disabled placeholder={this.$t('common.please.select')}>
                    {this.state.taxTransactionStatusOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="申请人">
                {getFieldDecorator('applicantName', {
                  initialValue: this.props.user.userName,
                  rules: [{ required: false, message: '不可编辑' }],
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Alert style={{ marginBottom: 10, marginTop: 10 }} message="基本信息" type="info" />
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人识别号">
                {(params.transactionType !== 'TAX_REGISTER_NEW'
                  ? getFieldDecorator('taxpayerNumber', {
                      initialValue: data.taxpayerNumber
                        ? [
                            {
                              taxpayerNumber: data.taxpayerNumber,
                            },
                          ]
                        : [],
                      rules: [{ required: true }],
                    })
                  : getFieldDecorator('taxpayerNumber', {
                      initialValue: params.taxpayerNumber || '',
                      rules: [{ required: true }],
                    }))(
                  params.transactionType === 'TAX_REGISTER_NEW' ? (
                    <Input
                      disabled={
                        params.transactionStatus === 'APPROVAL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                      }
                      placeholder={this.$t('common.please.enter')}
                    />
                  ) : params.transactionType === 'TAX_REGISTER_EDIT' ? (
                    // eslint-disable-next-line react/jsx-wrap-multilines
                    // params.sign === 1 ? (<Input disabled={params.transactionStatus === 'APPROVAL'? true : params.transactionStatus === 'APPROVAL_PASS'? true : false} placeholder={this.$t('common.please.enter')} />) : (
                    <Chooser
                      single={true}
                      labelKey="taxpayerNumber"
                      valueKey="taxpayerNumber"
                      onChange={this.nameChange}
                      type="taxpayer_name"
                      listExtraParams={{}}
                    />
                  ) : // )
                  params.transactionType === 'TAX_REGISTER_CANCEL' ? (
                    // params.sign === 1 ? (<Input disabled={params.transactionStatus === 'APPROVAL'? true : params.transactionStatus === 'APPROVAL_PASS'? true : false} placeholder={this.$t('common.please.enter')} />) : (
                    <Chooser
                      single={true}
                      labelKey="taxpayerNumber"
                      valueKey="taxpayerNumber"
                      onChange={this.nameChange}
                      type="taxpayer_name"
                      listExtraParams={{}}
                    />
                  ) : (
                    // )
                    <Input
                      disabled={
                        params.transactionStatus === 'APPROVAL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : params.transactionStatus === 'APPROVAL_PASS'
                              ? true
                              : false
                      }
                      placeholder={this.$t('common.please.enter')}
                    />
                  )
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人名称">
                {getFieldDecorator('taxpayerName', {
                  initialValue: data.taxpayerName || '',
                  rules: [{ required: true, message: this.$t('common.please.enter') }],
                })(
                  <Input
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    placeholder={this.$t('common.please.enter')}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="税号类型">
                {getFieldDecorator('taxpayerNumberType', {
                  initialValue: data.taxpayerNumberType || '',
                  rules: [{ required: true, message: '请选择' }],
                })(
                  <Select
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.taxNumberTypeOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="登记注册类型">
                {getFieldDecorator('typeOfBusiness', {
                  initialValue: data.typeOfBusiness || '',
                  rules: [{ required: true, message: '请选择' }],
                })(
                  <Select
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.taxTypeOfBusinessOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税资质">
                {getFieldDecorator('taxQualification', {
                  initialValue: data.taxQualification || '',
                  rules: [{ required: true, message: '请选择' }],
                })(
                  <Select
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.taxQualificationOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="国际行业（主行业）">
                {getFieldDecorator('mainIndustry', {
                  initialValue: data.mainIndustry || '',
                  rules: [{ required: false, message: this.$t('common.please.enter') }],
                })(
                  <Input
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    placeholder={this.$t('common.please.enter')}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="是否为上市公司">
                {getFieldDecorator('listedCompany', {
                  initialValue: data.listedCompany || 'true',
                  rules: [{ required: false, message: '请选择' }],
                })(
                  <Select
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.whetherOrNotOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="是否为境外注册居民企业">
                {getFieldDecorator('overseasRegResEnt', {
                  initialValue: data.listedCompany || 'false',
                  rules: [{ required: false, message: '请选择' }],
                })(
                  <Select
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.whetherOrNotOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
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
