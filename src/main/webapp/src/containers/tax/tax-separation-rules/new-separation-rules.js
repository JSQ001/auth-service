/**
 * Created by 22161 on 2019/3/8.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import { Button, Form, Input, message, Select, Row, Col, Switch, Icon } from 'antd';
const FormItem = Form.Item;
import config from 'config';
const Option = Select.Option;
import Service from './service';
import Lov from 'widget/Template/lov';

class NewSeparationRules extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      data: {},
      sourceSystemMethodOptions: [],
      TaxRuleCodeMethodOptions: [],
      SourceDataStatusMethodOptions: [],
      formItems: [],
    };
  }

  componentDidMount() {
    this.getTaxAccountingMethod();
    this.getTaxRuleCode();
    this.getSourceDataStatus();
    this.getDataById();
  }

  /**
   * 获取来源系统下拉列表
   */
  getTaxAccountingMethod = () => {
    // eslint-disable-next-line prefer-const
    let sourceSystemMethodOptions = [];
    this.getSystemValueList('TAX_SOURCE_SYSTEM').then(res => {
      res.data.values.map(data => {
        sourceSystemMethodOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        sourceSystemMethodOptions,
      });
    });
  };
  /**
   * 获取计税规则下拉列表
   */
  getTaxRuleCode = () => {
    // eslint-disable-next-line prefer-const
    let TaxRuleCodeMethodOptions = [];
    this.getSystemValueList('TAX_RULE_CODE').then(res => {
      res.data.values.map(data => {
        TaxRuleCodeMethodOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        TaxRuleCodeMethodOptions,
      });
    });
  };
  /**
   * 获取来源数据价税状态下拉列表
   */
  getSourceDataStatus = () => {
    // eslint-disable-next-line prefer-const
    let SourceDataStatusMethodOptions = [];
    this.getSystemValueList('TAX_SOURCE_DATA_STATUS').then(res => {
      res.data.values.map(data => {
        SourceDataStatusMethodOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        SourceDataStatusMethodOptions,
      });
    });
  };

  //  获取数据  。 。。
  getList(data) {
    Service.getColumns()
      .then(response => {
        if (data != undefined && data.taxVatSeparateRuleAddList.length) {
          data.taxVatSeparateRuleAddList.map((item, index) => {
            response.data.map((items, index1) => {
              if (item.dimensionId == items.dimensionId) {
                items.dimensionValueName = item.dimensionValueName;
                items.dimensionValueId = item.dimensionValueId;
              }
            });
          });
        }
        this.setState({
          formItems: response.data,
        });
      })
      .catch(() => {});
  }

  //  通过ID获取对应行数据  。 。。
  getDataById = () => {
    if (this.props.params.id != undefined) {
      Service.pageById(this.props.params.id).then(res => {
        this.setState({
          data: res.data,
        });
        this.getList(res.data);
      });
    } else {
      this.getList();
    }
  };

  // 动态创建表单
  createDom = () => {
    const { getFieldDecorator } = this.props.form;
    const { formItems } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 10 },
        sm: { span: 10 },
      },
      wrapperCol: {
        xs: { span: 14 },
        sm: { span: 14 },
      },
    };
    return formItems.map(item => {
      return (
        <Col span={12} key={item.id}>
          <FormItem {...formItemLayout} label={this.$t(item.dimensionName)}>
            {getFieldDecorator(item.id, {
              initialValue: item.dimensionValueId && {
                id: item.dimensionValueId,
                dimensionItemName: item.dimensionValueName,
                dimensionId: item.dimensionId,
              },
              rules: [{ required: false }], //
            })(
              <Lov
                labelKey="dimensionItemName"
                valueKey="id"
                code="dimension"
                single
                extraParams={{ dimensionId: item.dimensionId, enabled: true }}
              />
            )}
          </FormItem>
        </Col>
      );
    });
  };

  // 新建
  handleCreate = () => {
    let { formItems } = this.state;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        let params = {
          ...values,
        };
        if (params.taxRateId != undefined) {
          params.taxRateId = params.taxRateId.id;
        }
        if (params.taxAccountId != undefined) {
          params.taxAccountId = params.taxAccountId.id;
        }
        if (params.separateAccountId != undefined) {
          params.separateAccountId = params.separateAccountId.id;
        }
        let taxV = [];
        if (formItems != '') {
          formItems.map(res => {
            if (params[res.id] != '') {
              taxV.push({
                dimensionId: params[res.id].dimensionId,
                dimensionValueId: params[res.id].id,
                id: null,
                separateRuleId: params.id,
              });
            }
          });
        }
        params.taxVatSeparateRuleAddList = taxV;
        Service.insertInvoicingSite(params)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            this.props.form.resetFields();
            this.props.close(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  //   修改
  handleUpdate = () => {
    const { formItems, data } = this.state;
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        values.id = this.props.params.id;
        let params = { ...values };
        console.log(params);
        if (params.taxRateId != '') {
          params.taxRateId = params.taxRateId.id;
        }
        if (params.taxAccountId != '') {
          params.taxAccountId = params.taxAccountId.id;
        }
        if (params.separateAccountId != '') {
          params.separateAccountId = params.separateAccountId.id;
        }
        console.log(params);
        let taxV = [];
        if (formItems != '') {
          formItems.map((res, index) => {
            if (params[res.id] != undefined) {
              taxV.push({
                dimensionId: params[res.id].dimensionId,
                dimensionValueId: params[res.id].id,
                id: this.props.params.taxVatSeparateRuleAddList[index]
                  ? this.props.params.taxVatSeparateRuleAddList[index].id
                  : null,
                separateRuleId: params.id,
              });
            }
          });
        }
        console.log(taxV);
        params.taxVatSeparateRuleAddList = taxV;
        Service.updateInvoicingSite(params)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            this.props.form.resetFields();
            this.props.close(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(this.$t({ id: 'common.save.filed' }) + `,${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  //新建或者修改
  handleSave = e => {
    e.preventDefault();
    this.setState({
      loading: true,
    });
    typeof this.props.params.id === 'undefined' ? this.handleCreate() : this.handleUpdate();
  };

  onCancel = () => {
    this.props.close(false);
  };
  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      data,
      sourceSystemMethodOptions,
      TaxRuleCodeMethodOptions,
      SourceDataStatusMethodOptions,
    } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 10 },
        sm: { span: 10 },
      },
      wrapperCol: {
        xs: { span: 14 },
        sm: { span: 14 },
      },
    };
    const { params } = this.props;
    return (
      <div className="new-invoicing-site">
        <Form onSubmit={this.handleSave}>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'来源系统'}>
                {getFieldDecorator('sourceSystem', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.sourceSystem,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    {sourceSystemMethodOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'来源数据价税状态'}>
                {getFieldDecorator('sourceDataStatus', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.sourceDataStatus,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    {SourceDataStatusMethodOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'价税分离科目'}>
                {getFieldDecorator('separateAccountId', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.id && {
                    id: data.separateAccountId,
                    accountName: data.separateAccountName,
                  },
                })(
                  <Lov
                    labelKey="accountName"
                    valueKey="id"
                    code="subject"
                    single
                    extraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'税目'}>
                {getFieldDecorator('taxRateId', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.taxRateId && {
                    id: data.taxRateId,
                    remarks: data.taxRateName,
                  },
                })(
                  <Lov
                    labelKey="remarks"
                    valueKey="id"
                    code="tax-rate-definition"
                    single
                    extraParams={{ taxCategoryCode: 'VAT', enabled: 'Y' }}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'增值税科目方向'}>
                {getFieldDecorator('drOrCr', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.drOrCr,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    <Option value="DR">DR</Option>
                    <Option value="CR">CR</Option>
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'增值税科目'}>
                {getFieldDecorator('taxAccountId', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.id && {
                    id: data.taxAccountId,
                    accountName: data.separateAccountName,
                  },
                })(
                  <Lov
                    labelKey="accountName"
                    valueKey="id"
                    code="subject"
                    single
                    extraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'计税规则'}>
                {getFieldDecorator('taxRuleCode', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.taxRuleCode,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    {TaxRuleCodeMethodOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>{this.createDom()}</Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'是否申报'}>
                {getFieldDecorator('declareFlag', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.id ? data.declareFlag : true,
                  valuePropName: 'checked',
                })(
                  <Switch
                    checkedChildren="是"
                    unCheckedChildren="否"
                    onChange={checked => {}}
                    disabled={false}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'是否开票'}>
                {getFieldDecorator('invoiceFlag', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.id ? data.invoiceFlag : true,
                  valuePropName: 'checked',
                })(
                  <Switch
                    checkedChildren="是"
                    unCheckedChildren="否"
                    onChange={checked => {}}
                    disabled={false}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'是否生成凭证'}>
                {getFieldDecorator('accountingFlag', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.id ? data.accountingFlag : true,
                  valuePropName: 'checked',
                })(
                  <Switch
                    checkedChildren="是"
                    unCheckedChildren="否"
                    onChange={checked => {}}
                    disabled={false}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'是否启用'}>
                {getFieldDecorator('enabled', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.id ? data.enabled : true,
                  valuePropName: 'checked',
                })(
                  <Switch
                    checkedChildren="是"
                    unCheckedChildren="否"
                    onChange={checked => {
                      console.log(checked);
                    }}
                    disabled={false}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <div className="slide-footer">
            <Button type="primary" htmlType="submit" loading={this.state.loading}>
              {this.$t('common.save')}
            </Button>
            <Button onClick={this.onCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}
const WrappedSeparationRules = Form.create()(NewSeparationRules);
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
)(WrappedSeparationRules);
