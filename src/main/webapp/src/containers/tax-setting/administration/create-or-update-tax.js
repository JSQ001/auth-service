/* eslint-disable react/destructuring-assignment */
/**
 * Created by 5716 on 2019/3/7.
 */
import React from 'react';
import { connect } from 'dva';

import { Button, Form, Switch, Input, message, Icon } from 'antd';
import organManagementService from './tax-administration.service';

const FormItem = Form.Item;

class NewOrganManagement extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // eslint-disable-next-line react/no-unused-state
      params: {},
      enableFlag: true,
      // eslint-disable-next-line react/no-unused-state
      isPut: false,
      loading: false,
      // paymentMethodCategoryOptions: [],
      // eslint-disable-next-line react/no-unused-state
      searchFrom: [{ id: 'enableFlag' }, { id: 'taxDepartmentCode' }, { id: 'taxDepartment' }],
    };
  }

  /* componentWillMount() {
    this.getPaymentMethodCategory();
  }

  getPaymentMethodCategory() {
    let paymentMethodCategoryOptions = [];
    this.getSystemValueList(2105).then(res => {
      res.data.values.map(data => {
        paymentMethodCategoryOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        paymentMethodCategoryOptions,
      });
    });
  } */

  handleCreate = () => {
    // eslint-disable-next-line react/destructuring-assignment
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        organManagementService
          .addOrganManagement(values)
          .then(() => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            // eslint-disable-next-line react/destructuring-assignment
            this.props.form.resetFields();
            // eslint-disable-next-line react/destructuring-assignment
            this.props.onClose(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(`${this.$t({ id: 'common.save.filed' })},${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  handleUpdate = () => {
    // eslint-disable-next-line react/destructuring-assignment
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        // eslint-disable-next-line no-param-reassign
        values.id = this.props.params.updateParams.id;
        organManagementService
          .upDateOrganManagement(values)
          .then(() => {
            message.success(
              this.$t({ id: 'common.operate.success' }, { name: values.description })
            );
            this.setState({
              loading: false,
            });
            this.props.form.resetFields();
            this.props.onClose(true);
          })
          .catch(e => {
            if (e.response) {
              message.error(`${this.$t({ id: 'common.save.filed' })},${e.response.data.message}`);
            }
            this.setState({ loading: false });
          });
      }
    });
  };

  // 新建或者修改
  handleSave = e => {
    e.preventDefault();
    // this.setState({
    //   loading: true,
    // });
    // eslint-disable-next-line no-unused-expressions
    typeof this.props.params.updateParams.id === 'undefined'
      ? this.handleCreate()
      : this.handleUpdate();
  };

  onCancel = () => {
    this.props.onClose(false);
  };

  switchChange = () => {
    this.setState(prevState => ({
      enableFlag: !prevState.enableFlag,
    }));
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {
      labelCol: { span: 6, offset: 1 },
      wrapperCol: { span: 14, offset: 1 },
    };
    return (
      <div className="create-or-update-tax">
        <Form onSubmit={this.handleSave}>
          <FormItem {...formItemLayout} label="是否启用">
            {getFieldDecorator('enableFlag', {
              valuePropName: 'checked',
              initialValue:
                JSON.stringify(this.props.params.updateParams) === '{}'
                  ? true
                  : this.props.params.updateParams.enableFlag,
            })(
              <Switch
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                onChange={this.switchChange}
                disabled={false}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label="税务机关代码">
            {getFieldDecorator('taxDepartmentCode', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.taxDepartmentCode || '',
            })(<Input placeholder={this.$t('common.please.enter')} disabled />)}
          </FormItem>
          <FormItem {...formItemLayout} label="税务机关名称">
            {getFieldDecorator('taxDepartment', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.taxDepartment || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="税务机关地址">
            {getFieldDecorator('taxDepartmentAddress', {
              rules: [
                {
                  required: true,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.taxDepartmentAddress || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
          </FormItem>
          <FormItem {...formItemLayout} label="联系方式">
            {getFieldDecorator('telephone', {
              rules: [
                {
                  required: false,
                  message: this.$t('common.please.enter'),
                },
              ],
              initialValue: this.props.params.updateParams.telephone || '',
            })(<Input placeholder={this.$t({ id: 'common.please.enter' })} disabled={false} />)}
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

const WrappedOrganManagement = Form.create()(NewOrganManagement);
function mapStateToProps() {
  return {};
}
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedOrganManagement);
