import React from 'react';
import { connect } from 'dva';

import { Button, Form, Switch, Row, Col, Input, message, Icon, Select } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
import Lov from 'widget/Template/lov';
import invoiceRuleService from './vat-invoice-rule.service';

class NewInvoiceRule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      formItems: [],
      searchForm: [
        { id: 'enableFlag' },
        { id: 'invoiceTitle' },
        { id: 'invoiceType' },
        { id: 'unit' },
        { id: 'commodityId' },
      ],
    };
  }

  componentDidMount() {
    this.getList();
  }

  //编辑
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
        const params = { ...values };

        invoiceRuleService
          .updateInvoiceRule(params)
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
    // this.handleUpdate();
  };
  //取消编辑
  onCancel = () => {
    this.props.close(false);
  };
  switchChange = value => {
    this.setState({ enabled: value });
  };

  getList() {
    let ac = [];
    invoiceRuleService
      .getColumns()
      .then(response => {
        this.setState({
          formItems: response.data,
        });
      })
      .catch(() => {});
  }

  //动态创建表单
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
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
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

  render() {
    const { getFieldDecorator } = this.props.form;
    const { enabled, isPut } = this.state;
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
      <div className="new-invoice-rule">
        <Form onSubmit={this.handleSave}>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'状态'}>
                {getFieldDecorator('enableFlag', {
                  valuePropName: 'checked',
                  initialValue:
                    // JSON.stringify(this.props.params) === '{}' ? true : this.props.params.enabled,
                    params && params.id ? params.enableFlag : true,
                })(
                  <Switch
                    checkedChildren={<Icon type="check" />}
                    unCheckedChildren={<Icon type="cross" />}
                    onChange={this.switchChange}
                    disabled={false}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'开票名称'}>
                {getFieldDecorator('invoiceTitle', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: params.id ? params.invoiceTitle : '',
                })(<Input placeholder={this.$t('common.please.enter')} disabled={false} />)}
              </FormItem>
            </Col>
          </Row>

          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'发票类型'}>
                {getFieldDecorator('invoiceType', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: params.id ? params.invoiceType : '',
                })(
                  <Select placeholder="请选择" style={{ width: '100%' }}>
                    <Option value="普通发票">普通发票</Option>
                    <Option value="专用发票">专用发票</Option>
                    <Option value="电子普票">电子普票</Option>
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'单位'}>
                {getFieldDecorator('unit', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: params.id ? params.unit : '',
                })(<Input placeholder={this.$t('common.please.enter')} disabled={false} />)}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24}>
            <Col span={12}>
              <FormItem {...formItemLayout} label={'商品编码'}>
                {getFieldDecorator('commodityId', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: params.id ? params.commodityId : '',
                })(<Input placeholder={this.$t('common.please.enter')} disabled={false} />)}
              </FormItem>
            </Col>
            <Row gutter={24}>{this.createDom()}</Row>
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
const WrappedInvoiceRule = Form.create()(NewInvoiceRule);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedInvoiceRule);
