/**
 * Created by 22161 on 2019/3/8.
 */
/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import config from 'config';

import { Button, Form, Switch, Input, message, Icon, Select } from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;

import Chooser from 'widget/chooser';
import ClientOtherService from './client-other.service';

class NewClientOther extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      enabled: true,
      isPut: false,
      loading: false,
      clientDimensionItem: {
        title: '请选择维度',
        url: `${config.mdataUrl}/api/dimension/page/by/cond`,
        searchForm: [
          { type: 'input', id: 'dimensionCode', label: '维度代码' },
          { type: 'input', id: 'dimensionName', label: '维度名称' },
        ],
        columns: [
          { title: '维度代码', dataIndex: 'dimensionCode', width: 150 },
          { title: '维度名称', dataIndex: 'dimensionName', width: 150 },
        ],
        key: 'id',
      },
      clientDimensionValueItem: {
        title: '请选择维值',
        url: `${config.mdataUrl}/api/dimension/item/page/by/dimensionId?roleType=TENANT`,
        searchForm: [
          { type: 'input', id: 'dimensionItemCode', label: '维值代码' },
          { type: 'input', id: 'dimensionItemName', label: '维值名称' },
        ],
        columns: [
          { title: '维值代码', dataIndex: 'dimensionItemCode', width: 150 },
          { title: '维值名称', dataIndex: 'dimensionItemName', width: 150 },
        ],
        key: 'id',
      },
      dimensionId: '',
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
      if (!err) {
        let params = {
          ...values,
          dimensionValueId: values.dimensionItemCodeList[0].id,
          dimensionId: values.dimensionCodeList[0].id,
          applicationId: this.props.params.applicationId,
        };
        ClientOtherService.insertClientOther(params)
          .then(response => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: params.description })
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
        ClientOtherService.updateClientOther(values)
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

  handleDimensionChange = values => {
    this.setState({
      dimensionId: values.length === 0 ? null : values[0].id,
    });
    this.props.form.setFieldsValue({
      dimensionName: values.length === 0 ? null : values[0].dimensionName,
    });
  };

  handleDimensionItemChange = values => {
    this.props.form.setFieldsValue({
      dimensionItemName: values.length === 0 ? null : values[0].dimensionItemName,
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      enabled,
      isPut,
      clientDimensionItem,
      clientDimensionValueItem,
      dimensionId,
    } = this.state;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    const { params } = this.props;
    return (
      <div className="new-invoicing-site">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label={'维度代码'}>
            {getFieldDecorator('dimensionCodeList', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              // initialValue: this.props.params.preferentialTaxRate || '',
            })(
              <Chooser
                labelKey="dimensionCode"
                valueKey="id"
                selectorItem={clientDimensionItem}
                single={true}
                onChange={this.handleDimensionChange}
                listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId, enabled: true }}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={'维度名称'}>
            {getFieldDecorator('dimensionName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled />)}
          </FormItem>
          <FormItem {...formItemLayout} label={'维值代码'}>
            {getFieldDecorator('dimensionItemCodeList', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              // initialValue: this.props.params.preferentialTaxRate || '',
            })(
              <Chooser
                labelKey="dimensionItemCode"
                valueKey="id"
                selectorItem={clientDimensionValueItem}
                single={true}
                onChange={this.handleDimensionItemChange}
                listExtraParams={{ dimensionId: this.state.dimensionId, enabled: true }}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={'维值名称'}>
            {getFieldDecorator('dimensionItemName', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled />)}
          </FormItem>

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

const WrappedClientOther = Form.create()(NewClientOther);
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
)(WrappedClientOther);
