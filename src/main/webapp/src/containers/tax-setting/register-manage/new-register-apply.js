/* eslint-disable */
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
import service from './tax-register-apply.service';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
import SeeCangeRecord from './tax-change-record';
import SlideFrame from 'widget/slide-frame';
const FormItem = Form.Item;

// eslint-disable-next-line prefer-destructuring
const Option = Select.Option;

class NewRegisterApply extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      taxTransactionTypeOptions: [],
      taxTransactionStatusOptions: [],
      taxNumberTypeOptions: [],
      taxQualificationOptions: [],
      taxTypeOfBusinessOptions: [],
      whetherOrNotOptions: [],
      taxCompanyTypeOptions: [],
      taxAccountingSystemOptions: [],
      taxAccountingMethodOptions: [],
      cangeRecordShow: false,
      record: {},
      selectedRowKeys: '',
      data: {},
    };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({ visible: nextProps.visible });
  }

  componentWillMount() {
    this.getTaxTransactionType();
    this.getTaxNumberType();
    this.getPaymentMethodCategory();
    this.getTaxQualification();
    this.getTaxTypeOfBusiness();
    this.getWhetherOrNot();
    this.getTaxCompanyType();
    this.getTaxAccountingSystem();
    this.getTaxAccountingMethod();
    this.setState({
      data: this.props.params,
    });
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

  /**
   * 事务状态下拉列表
   */
  getTaxNumberType() {
    // eslint-disable-next-line prefer-const
    let taxTransactionStatusOptions = [];
    this.getSystemValueList('TAX_TRANSACTION_STATUS').then(res => {
      res.data.values.map(data => {
        taxTransactionStatusOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxTransactionStatusOptions,
      });
    });
  }

  /**
   * 税号类型下拉列表
   */
  getPaymentMethodCategory() {
    // eslint-disable-next-line prefer-const
    let taxNumberTypeOptions = [];
    this.getSystemValueList('TAX_PAYER_NUMBER_TYPE').then(res => {
      res.data.values.map(data => {
        taxNumberTypeOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxNumberTypeOptions,
      });
    });
  }

  /**
   * 纳税资质下拉列表
   */
  getTaxQualification() {
    // eslint-disable-next-line prefer-const
    let taxQualificationOptions = [];
    this.getSystemValueList('TAX_QUAIFICATION').then(res => {
      res.data.values.map(data => {
        taxQualificationOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxQualificationOptions,
      });
    });
  }

  /**
   * 登记注册类型下拉列表
   */
  getTaxTypeOfBusiness() {
    // eslint-disable-next-line prefer-const
    let taxTypeOfBusinessOptions = [];
    this.getSystemValueList('TAX_TYPE_Of_BUSINESS').then(res => {
      res.data.values.map(data => {
        taxTypeOfBusinessOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxTypeOfBusinessOptions,
      });
    });
  }

  /**
   * 是否下拉列表
   */
  getWhetherOrNot() {
    // eslint-disable-next-line prefer-const
    let whetherOrNotOptions = [];
    this.getSystemValueList('WHETHER_OR_NOT').then(res => {
      res.data.values.map(data => {
        whetherOrNotOptions.push({
          label: data.messageKey,
          value: data.value,
        });
      });
      this.setState({
        whetherOrNotOptions,
      });
    });
  }

  /**
   * 单位性质
   */
  getTaxCompanyType() {
    // eslint-disable-next-line prefer-const
    let taxCompanyTypeOptions = [];
    this.getSystemValueList('TAX_COMPANY_TYPE').then(res => {
      res.data.values.map(data => {
        taxCompanyTypeOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxCompanyTypeOptions,
      });
    });
  }

  /**
   * 适用会计制度
   */
  getTaxAccountingSystem() {
    // eslint-disable-next-line prefer-const
    let taxAccountingSystemOptions = [];
    this.getSystemValueList('TAX_ACCOUNTING_SYSTEM').then(res => {
      res.data.values.map(data => {
        taxAccountingSystemOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxAccountingSystemOptions,
      });
    });
  }
  /**
   * 变更历史
   */
  cangeRecord = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        // eslint-disable-next-line no-param-reassign
        values = { ...this.props.params, ...values };
        this.setState({
          cangeRecordShow: true,
          record: {
            id: values.id,
          },
          title: '查看变更记录',
        });
      }
    });
  };

  cangeClose = params => {
    this.setState(
      {
        cangeRecordShow: false,
      }
      // () => {
      //   // eslint-disable-next-line no-unused-expressions
      //   params && this.getList();
      // }
    );
  };
  /**
   * 核算方式
   */
  getTaxAccountingMethod() {
    // eslint-disable-next-line prefer-const
    let taxAccountingMethodOptions = [];
    this.getSystemValueList('ACCOUNTING_METHOD').then(res => {
      res.data.values.map(data => {
        taxAccountingMethodOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxAccountingMethodOptions,
      });
    });
  }

  handleSave = e => {
    e.preventDefault();
    // eslint-disable-next-line react/destructuring-assignment
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        // eslint-disable-next-line no-param-reassign
        values = { ...this.props.params, ...values };
        values.id = this.state.data.id;
        // 所属机构
        if (values.affiliation && values.affiliation[0]) {
          // eslint-disable-next-line no-param-reassign
          values.affiliation = values.affiliation[0].id;
        } else {
          // eslint-disable-next-line no-param-reassign
          values.affiliation = null;
        }
        // 主管税务机关
        if (values.natTaxAuth && values.natTaxAuth[0]) {
          // eslint-disable-next-line no-param-reassign
          values.natTaxAuth = values.natTaxAuth[0].id;
        } else {
          // eslint-disable-next-line no-param-reassign
          values.natTaxAuth = null;
        }
        // 上级纳税主体
        if (values.parentTaxpayerId && values.parentTaxpayerId[0]) {
          // eslint-disable-next-line no-param-reassign
          values.parentTaxpayerId = values.parentTaxpayerId[0].id;
        } else {
          // eslint-disable-next-line no-param-reassign
          values.parentTaxpayerId = null;
        }
        if (values.transactionType !== 'TAX_REGISTER_NEW') {
          // 纳税人识别号
          if (values.taxpayerNumber && values.taxpayerNumber[0]) {
            // eslint-disable-next-line no-param-reassign
            values.taxpayerNumber = values.taxpayerNumber[0].taxpayerNumber;
          } else {
            // eslint-disable-next-line no-param-reassign
            values.taxpayerNumber = null;
          }
        }
        if (!values.id) {
          // const { id } = values.id
          // service.getApplicationDetail(id).then(res => {
          // if (res.data = '') {
          service
            .addTax(values)
            // eslint-disable-next-line no-unused-vars
            .then(res => {
              if (res && res.data) {
                values.id = res.data.id;
                message.success('保存成功！');
                service.getApplicationDetail(res.data.id).then(res => {
                  this.setState({
                    data: res.data,
                    id: res.data.id,
                  });
                  console.log(this.state.data);
                });
              }
            })

            // eslint-disable-next-line no-shadow
            .catch(err => {
              message.error(err.response.data.message);
            });
          // }
          // })
        } else {
          // eslint-disable-next-line no-unused-vars
          service.updateTax(values).then(res => {
            message.success('修改成功！');
            // this.setState({ visible: false });
            this.props.form.resetFields();
            // eslint-disable-next-line no-unused-expressions
            // this.props.onClose && this.props.onClose(true);
          });
        }
      }
    });
  };

  // eslint-disable-next-line no-unused-vars
  handleChange = val => {
    if (this.state.loading) {
      this.setState({
        loading: false,
      });
    }
  };

  nameChange = value => {
    if (value.length !== 0 && value[0].id !== undefined) {
      let data = this.props.form.getFieldsValue();
      data = Object.keys(data).reduce((temp, key) => {
        if (key === 'listedCompany') {
          temp[key] = String(value[0][key]);
        } else if (key === 'overseasRegResEnt') {
          temp[key] = String(value[0][key]);
        } else {
          temp[key] = value[0][key];
        }
        return temp;
      }, {});
      if (value[0].affiliation) {
        data.affiliation = [{ id: value[0].affiliation, name: value[0].affiliationName }];
      }
      if (value[0].parentTaxpayerId) {
        data.parentTaxpayerId = [
          { taxpayerNumber: value[0].parentTaxpayerId, taxpayerName: value[0].parentTaxpayerName },
        ];
      }
      if (value[0].natTaxAuth) {
        data.natTaxAuth = [{ id: value[0].natTaxAuth, taxDepartment: value[0].natTaxAuthName }];
      }
      if (value[0].taxpayerNumber) {
        data.taxpayerNumber = [{ taxpayerNumber: value[0].taxpayerNumber }];
      }
      data.transactionStatus = 'GENERATE';
      if (this.props.params.transactionType === 'TAX_REGISTER_EDIT') {
        data.transactionType = 'TAX_REGISTER_EDIT';
        data.transactionTypeName = '税务登记修改';
      } else if (this.props.params.transactionType === 'TAX_REGISTER_CANCEL') {
        data.transactionType = 'TAX_REGISTER_CANCEL';
        data.transactionTypeName = '税务登记注销';
      }
      this.props.form.setFieldsValue(data);
    } else if (value.length !== 0 && value[0].id === undefined) {
    } else {
      this.props.form.setFieldsValue({
        accountingMethod: null,
        accountingSystem: null,
        affiliation: null,
        bankAccountNumber: null,
        businessScope: null,
        clientTaxBank: null,
        companyType: null,
        listedCompany: null,
        mainIndustry: null,
        natTaxAuth: null,
        operatingPeriod: null,
        operationAddress: null,
        overseasRegResEnt: null,
        parentTaxpayerId: null,
        postalCode: null,
        registeredAddress: null,
        registeredCapital: null,
        taxQualification: null,
        taxpayerAddress: null,
        taxpayerName: null,
        taxpayerNumber: null,
        taxpayerNumberType: null,
        taxpayerTel: null,
        typeOfBusiness: null,
        legalRepresentative: null,
      });
    }
  };

  handleCancel = () => {
    // eslint-disable-next-line no-unused-expressions
    this.props.onClose && this.props.onClose(true);
    this.setState({
      data: {},
    });
  };

  handleSubmit = e => {
    e.preventDefault();
    // eslint-disable-next-line react/destructuring-assignment
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        values = { ...this.props.params, ...values };
        // 所属机构
        if (values.affiliation && values.affiliation[0]) {
          values.affiliation = values.affiliation[0].id;
        } else {
          values.affiliation = null;
        }
        // 主管税务机关
        if (values.natTaxAuth && values.natTaxAuth[0]) {
          values.natTaxAuth = values.natTaxAuth[0].id;
        } else {
          values.natTaxAuth = null;
        }
        // 上级纳税主体
        if (values.parentTaxpayerId && values.parentTaxpayerId[0]) {
          values.parentTaxpayerId = values.parentTaxpayerId[0].id;
        } else {
          values.parentTaxpayerId = null;
        }
        if (values.transactionType !== 'TAX_REGISTER_NEW') {
          // 纳税人识别号
          if (values.taxpayerNumber && values.taxpayerNumber[0]) {
            values.taxpayerNumber = values.taxpayerNumber[0].taxpayerNumber;
          } else {
            values.taxpayerNumber = null;
          }
        }

        values.id = this.state.id;
        console.log(values.id);
        if (!values.id) {
          service
            .addTax(values)
            .then(res => {
              if (res && res.data) {
                // eslint-disable-next-line no-unused-vars
                service
                  .submitTax(values)
                  .then(resParam => {
                    message.success('提交成功！');
                    this.setState({ visible: false });
                    this.props.form.resetFields();
                    // eslint-disable-next-line no-unused-expressions
                    this.props.onClose && this.props.onClose(true);
                  })
                  .catch(e => {
                    if (e.response) {
                      message.error(
                        this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`
                      );
                    }
                    this.setState({ loading: false });
                  });
              }
            })
            .catch(e => {
              if (e.response) {
                message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
              }
              this.setState({ loading: false });
            });
          // message.error('请先保存！');
        } else {
          service
            .updateTax(values)
            .then(() => {
              // eslint-disable-next-line no-unused-vars
              service
                .submitTax(values)
                .then(res => {
                  message.success('提交成功！');
                  this.setState({ visible: false });
                  this.props.form.resetFields();
                  // eslint-disable-next-line no-unused-expressions
                  this.props.onClose && this.props.onClose(true);
                })
                .catch(e => {
                  if (e.response) {
                    message.error(
                      this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`
                    );
                  }
                  this.setState({ loading: false });
                });
            })
            .catch(e => {
              if (e.response) {
                message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
              }
              this.setState({ loading: false });
            });
        }
      }
    });
  };

  fileMethod = () => {
    // eslint-disable-next-line no-unused-expressions
    this.props.onClose && this.props.onClose(true);
  };

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
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人地址">
                {getFieldDecorator('taxpayerAddress', {
                  initialValue: data.taxpayerAddress || '',
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
            <Col span={12}>
              <FormItem {...formItemLayout} label="银行开户行">
                {getFieldDecorator('clientTaxBank', {
                  initialValue: data.clientTaxBank || '',
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
              <FormItem {...formItemLayout} label="银行账号">
                {getFieldDecorator('bankAccountNumber', {
                  initialValue: data.bankAccountNumber || '',
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
            <Col span={12}>
              <FormItem {...formItemLayout} label="纳税人电话">
                {getFieldDecorator('taxpayerTel', {
                  initialValue: data.taxpayerTel || '',
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
              <FormItem {...formItemLayout} label="主管税务机关">
                {getFieldDecorator('natTaxAuth', {
                  initialValue: data.natTaxAuth && [
                    { id: data.natTaxAuth, taxpayerName: data.natTaxAuthName },
                  ]
                    ? [
                        {
                          id: data.natTaxAuth,
                          taxDepartment: data.natTaxAuthName,
                        },
                      ]
                    : [],
                  rules: [],
                })(
                  <Chooser
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    single={true}
                    labelKey="taxDepartment"
                    valueKey="id"
                    onChange={this.handleChange}
                    type="tax_authority"
                    listExtraParams={{
                      taxDepartmentCode: params.natTaxAuth,
                      taxDepartment: params.natTaxAuthName,
                    }}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="上级纳税主体">
                {getFieldDecorator('parentTaxpayerId', {
                  initialValue: data.parentTaxpayerId
                    ? [
                        {
                          id: data.parentTaxpayerId,
                          taxpayerName: data.parentTaxpayerName,
                        },
                      ]
                    : [],
                  rules: [],
                })(
                  <Chooser
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    single={true}
                    labelKey="taxpayerName"
                    valueKey="id"
                    onChange={this.handleChange}
                    type="parent_tax_payer"
                    listExtraParams={{}}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="单位性质">
                {getFieldDecorator('companyType', {
                  initialValue: data.companyType || '',
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
                    {this.state.taxCompanyTypeOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="适用会计制度">
                {getFieldDecorator('accountingSystem', {
                  initialValue: data.accountingSystem || '',
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
                    {this.state.taxAccountingSystemOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="核算方式">
                {getFieldDecorator('accountingMethod', {
                  initialValue: data.accountingMethod || '',
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
                    {this.state.taxAccountingMethodOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="所属机构">
                {getFieldDecorator('affiliation', {
                  initialValue: data.affiliation
                    ? [
                        {
                          id: data.affiliation,
                          name: data.affiliationName,
                        },
                      ]
                    : [],
                  rules: [{ required: true }],
                })(
                  <Chooser
                    disabled={
                      params.transactionStatus === 'APPROVAL'
                        ? true
                        : params.transactionType === 'TAX_REGISTER_CANCEL'
                          ? true
                          : params.transactionStatus === 'APPROVAL_PASS'
                            ? true
                            : false
                    }
                    single={true}
                    labelKey="name"
                    valueKey="id"
                    onChange={this.handleChange}
                    type="tax_company"
                    listExtraParams={{}}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Alert style={{ marginBottom: 10, marginTop: 10 }} message="工商信息" type="info" />
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label="经营范围">
                {getFieldDecorator('businessScope', {
                  initialValue: data.businessScope || '',
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
            <Col span={12}>
              <FormItem {...formItemLayout} label="注册资金">
                {getFieldDecorator('registeredCapital', {
                  initialValue: data.registeredCapital || '',
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
              <FormItem {...formItemLayout} label="生产经营地址">
                {getFieldDecorator('operationAddress', {
                  initialValue: data.operationAddress || '',
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
            <Col span={12}>
              <FormItem {...formItemLayout} label="邮政编码">
                {getFieldDecorator('postalCode', {
                  initialValue: data.postalCode || '',
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
              <FormItem {...formItemLayout} label="营业期限">
                {getFieldDecorator('operatingPeriod', {
                  initialValue: data.operatingPeriod || '',
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
            <Col span={12}>
              <FormItem {...formItemLayout} label="法人代表">
                {getFieldDecorator('legalRepresentative', {
                  initialValue: data.legalRepresentative || '',
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
              <FormItem {...formItemLayout} label="注册地址">
                {getFieldDecorator('registeredAddress', {
                  initialValue: data.registeredAddress || '',
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
          <div
            style={{
              marginTop: 20,
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
            }}
          >
            <ApproveHistory type="902001" oid={params.documentOid} />
          </div>
          {/* style={{ borderTop: '1px solid #e8e8e8' }} */}
          <Row gutter={24}>
            <Col span={24} style={{ marginTop: '24px' }} align="middle">
              {params.transactionStatus === 'APPROVAL' ? (
                ''
              ) : params.transactionStatus === 'APPROVAL_PASS' ? (
                ''
              ) : params.transactionType === 'TAX_REGISTER_CANCEL' ? (
                ''
              ) : (
                <Button type="primary" onClick={this.handleSave}>
                  保存
                </Button>
              )}
              {params.transactionStatus === 'APPROVAL' ? (
                ''
              ) : params.transactionStatus === 'APPROVAL_PASS' ? (
                ''
              ) : (
                <Button type="primary" style={{ marginLeft: 10 }} onClick={this.handleSubmit}>
                  提交
                </Button>
              )}
              <Button style={{ marginLeft: 10 }} onClick={this.handleCancel}>
                取消
              </Button>
              <Button style={{ marginLeft: 10 }} onClick={this.fileMethod}>
                附件
              </Button>{' '}
              <Button onClick={this.cangeRecord}>查看变更记录</Button>{' '}
              <SlideFrame
                title="查看变更记录"
                show={cangeRecordShow}
                onClose={this.cangeClose}
                width="65vw"
              >
                <SeeCangeRecord params={record} onClose={this.close} />
              </SlideFrame>
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
