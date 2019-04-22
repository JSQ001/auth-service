/**
 * Created by 22161 on 2019/3/8.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import { Button, Form, Input, message, Select, Row, Col, DatePicker } from 'antd';
const FormItem = Form.Item;
import config from 'config';
const Option = Select.Option;
import Service from './transactio-details-data-supplement.service';
import Chooser from 'widget/chooser';
import Lov from 'widget/Template/lov';

class NewTransactioData extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      data: {},
      sourceSystemMethodOptions: [],
      formItems: [],
      getCurrency: [],
      dataCreationType: [],
    };
  }

  componentDidMount() {
    this.getTaxAccountingMethod();
    this.getDataCreationType();
    this.getList();
    this.getCurrency();
    this.setState({
      data: this.props.params,
    });
  }
  getList() {
    Service.getColumns()
      .then(response => {
        this.setState({
          formItems: response.data,
        });
      })
      .catch(() => {});
  }
  createDom = () => {
    const { getFieldDecorator } = this.props.form;
    const { formItems, data } = this.state;
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
              initialValue: item.dimensionId && {
                id: item.dimensionId,
                dimensionItemName: item.defaultItemValueName,
              },
              rules: [{ required: false }],
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
  /**
   * 获取来源系统下拉列表
   */
  getTaxAccountingMethod() {
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
  }
  /**
   * 获取数据创建方式下拉列表
   */
  getDataCreationType() {
    // eslint-disable-next-line prefer-const
    let dataCreationType = [];
    this.getSystemValueList('TAX_DATA_CREATION_TYPE').then(res => {
      res.data.values.map(data => {
        dataCreationType.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        dataCreationType,
      });
    });
  }
  getCurrency() {
    // eslint-disable-next-line prefer-const
    let getCurrency = [];
    let id = this.props.company.setOfBooksId;
    Service.getSystemValueList1(id).then(res => {
      res.data.map(data => {
        getCurrency.push({
          label: `${data.currency}-${data.currencyName}`,
          value: data.currency,
          key: data.currency,
        });
      });
      this.setState({
        getCurrency,
      });
    });
  }

  //   修改
  handleUpdate = () => {
    const { formItems } = this.state;
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
        if (params.clientAcc && params.clientAcc.length) {
          params.clientAcc = params.clientAcc[0].clientNumber;
        }
        let taxV = [];
        if (formItems != '') {
          formItems.map(res => {
            if (params[res.id] != '') {
              taxV.push({
                dimensionId: params[res.id].dimensionId,
                dimensionValueId: params[res.id].id,
              });
            }
          });
        }
        params.taxVatSeparateRuleAddList = taxV;
        console.log(params);
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
      this.setState({ loading: false });
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
    const { data, sourceSystemMethodOptions, getCurrency, dataCreationType } = this.state;
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
              <FormItem {...formItemLayout} label={'批次号'}>
                {getFieldDecorator('batchId', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.batchId,
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>{this.createDom()}</Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'交易流水号'}>
                {getFieldDecorator('transNum', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.transNum,
                })(<Input disabled />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'交易机构'}>
                {getFieldDecorator('org', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.org,
                })(<Input />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'客户名称'}>
                {getFieldDecorator('clientAcc', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.id && [{ id: data.id, clientName: data.taxpayerName }],
                })(
                  <Chooser
                    labelKey="clientName"
                    valueKey="id"
                    type="customer_information_query"
                    single={true}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'原币种金额'}>
                {getFieldDecorator('amount', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.amount,
                })(<Input />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'数据创建方式'}>
                {getFieldDecorator('dataType', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.dataType,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    {dataCreationType.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'价税分离状态'}>
                {getFieldDecorator('processFlag', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.processFlag,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    <Option value="Y">已完成</Option>
                    <Option value="N">未完成</Option>
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'交易日期'}>
                {getFieldDecorator('transDate', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.tranDate,
                })(<DatePicker />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'交易币种'}>
                {getFieldDecorator('currencyCode', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: data.currencyCode,
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    {getCurrency.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
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
const WrappedSeparationRules = Form.create()(NewTransactioData);
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
