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
import Chooser from 'widget/chooser';
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
      id: '',
      sourceSystemMethodOptions: [],
      formItems: [],
    };
  }
  componentWillMount() {}
  componentDidMount() {
    this.getTaxAccountingMethod();
    this.setState({
      data: this.props.params,
    });
    this.getList();
    console.log(this.props.params);
  }

  /**
   * 获取来源系统下拉列表
   */
  getTaxAccountingMethod() {
    // eslint-disable-next-line prefer-const
    let sourceSystemMethodOptions = [];
    this.getSystemValueList('TAX_SOURCE_SYSTEM').then(res => {
      console.log(res.data);
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
  }
  // switchChange = value => {
  //   this.setState({ enabled: value });
  // };

  getList() {
    Service.getColumns()
      .then(response => {
        this.setState({ formItems: response.data });
      })
      .catch(() => {});
  }
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
              initialValue: `${item.dimensionCode}-${item.dimensionName}`,
              rules: [{ required: false }],
            })(<Select />)}
          </FormItem>
        </Col>
      );
    });
  };
  // 新建
  handleCreate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      if (!err) {
        let params = { ...values };

        params = {
          ...values,
        };
        console.log(params);
        if (params.taxRateId !== '') {
          params.taxRateId = params.taxRateId.id;
        }
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
        params = {
          ...values,
        };
        if (params.taxRateId !== '') {
          params.taxRateId = params.taxRateId.id;
        }
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

  // switchChange = value => {
  //   this.setState({ enabled: value });
  // };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { data, sourceSystemMethodOptions } = this.state;
    // const formItemLayout = {
    //   labelCol: { span: 6, offset: 1 },
    //   wrapperCol: { span: 14, offset: 1 },
    // };
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
                  initialValue: data.sourceDataStatusName,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    <Option value="1">已拆分</Option>
                    <Option value="2">未拆分</Option>
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'价税分离科目'}>
                {getFieldDecorator('separateAccountName', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.separateAccountName,
                })(
                  <Chooser
                    labelKey="subject"
                    valueKey="separateAccountId"
                    type="subject"
                    listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                    single
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
                  initialValue: data.taxRateId && [
                    { id: data.taxRateId, taxCategoryName: data.taxRateName },
                  ],
                })(
                  <Lov labelKey="taxCategoryName" valueKey="id" code="tax-rate-definition" single />
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
                  initialValue: data.taxAccountName,
                })(
                  <Chooser
                    labelKey="taxAccountId"
                    valueKey="id"
                    type="subject"
                    listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                    single
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
                  initialValue: data.taxRuleCodeName,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    <Option value="1">一般计税</Option>
                    <Option value="2">简易计税</Option>
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
                    onChange={checked => {
                      console.log(checked);
                    }}
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
                    onChange={checked => {
                      console.log(checked);
                    }}
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
                    onChange={checked => {
                      console.log(checked);
                    }}
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
