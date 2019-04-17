/**
 * Created by 22161 on 2019/3/8.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';

import { Button, Form, Switch, Input, message, Icon, Select } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;

import Chooser from 'widget/chooser';
import distributionAccountingService from './distribution-accounting-service';
class NewInvoicingSite extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      isDisabled: true,
      searchFrom: [
        { id: 'companyId' },
        { id: 'companyName' },
        { id: 'departmentId' },
        { id: 'departmentName' },
      ],
    };
  }

  handleCreate = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (err) {
        this.setState({
          loading: false,
        });
        return;
      }
      console.log(this.props.id);
      if (!err) {
        console.log(values);
        let params = {
          ...values,
          departmentId: values.departmentId[0].id,
          companyCode: values.companyId[0].companyCode,
          companyId: values.companyId[0].id,
          responsibilityCenterId: values.departmentId[0].id,
          departmentName:
            values.departmentId && values.departmentId.length > 0
              ? values.departmentId[0].responsibilityCenterName
              : undefined,
          taxpayerId: this.props.id,
        };
        console.log(params);
        distributionAccountingService
          .insertTaxTaxpayerOrg(params)
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
        distributionAccountingService
          .updateInvoicingSite(values)
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

  switchChange = value => {
    this.setState({ enabled: value });
  };

  handleCompanyChange = values => {
    console.log(values);
    console.log(this.props);
    if (values.length < 1) {
      this.props.form.setFieldsValue({ companyName: '' });
      this.setState({
        isDisabled: true,
      });
    } else {
      this.props.form.setFieldsValue({ companyName: values[0].name });
      this.setState({
        isDisabled: false,
      });
    }
  };

  handleDepartmentChange = values => {
    console.log(values);
    if (values.length < 1) {
      this.props.form.setFieldsValue({ departmentName: '' });
    } else this.props.form.setFieldsValue({ departmentName: values[0].responsibilityCenterName });
  };

  render() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { enabled, isPut, isDisabled } = this.state;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { params } = this.props;
    return (
      <div className="new-invoicing-site">
        <Form onSubmit={this.handleSave}>
          {/* <FormItem {...formItemLayout} label={'是否启用'}>
            {getFieldDecorator('enabled', {
              valuePropName: 'checked',
              initialValue:
                // JSON.stringify(this.props.params) === '{}' ? true : this.props.params.enabled,
                params && params.id ? params.enabled : true,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
                disabled={false}
              />
            )}
          </FormItem> */}

          <FormItem {...formItemLayout} label={'机构代码'}>
            {getFieldDecorator('companyId', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
            })(
              <Chooser
                labelKey="companyCode"
                valueKey="id"
                type="company_detail"
                single={true}
                onChange={this.handleCompanyChange}
                listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={'机构名称'}>
            {getFieldDecorator('companyName', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.companyName || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'责任中心代码'}>
            {getFieldDecorator('departmentId', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
            })(
              <Chooser
                labelKey="responsibilityCenterCode"
                valueKey="id"
                type="responsibility_default_detail"
                single={true}
                disabled={isDisabled}
                listExtraParams={{
                  companyId:
                    getFieldValue('companyId') && getFieldValue('companyId')[0]
                      ? getFieldValue('companyId')[0].id
                      : '',
                }}
                onChange={this.handleDepartmentChange}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={'责任中心名称'}>
            {getFieldDecorator('departmentName', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.companyName || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled />)}
          </FormItem>

          {/* <FormItem {...formItemLayout} label={'所属纳税主体'}>
          {getFieldDecorator('taxpayerName', {
            rules: [
              {
                required: false,
                message: this.$t('common.please.enter'),
              },
            ],
            initialValue: this.props.params.preferentialTaxRate || '',
          })(
            <Chooser
              labelKey='name'
              valueKey='id'
              type="tax_payer"
              single={true}
            />
          )}
        </FormItem> */}

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

const WrappedInvoicingSite = Form.create()(NewInvoicingSite);
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
)(WrappedInvoicingSite);